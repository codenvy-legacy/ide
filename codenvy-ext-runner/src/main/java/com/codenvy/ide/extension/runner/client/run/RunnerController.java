/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
 * 
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.extension.runner.client.run;

import com.codenvy.api.core.rest.shared.dto.Link;
import com.codenvy.api.core.rest.shared.dto.ServiceError;
import com.codenvy.api.runner.dto.ApplicationProcessDescriptor;
import com.codenvy.api.runner.dto.DebugMode;
import com.codenvy.api.runner.dto.RunOptions;
import com.codenvy.api.runner.dto.RunnerEnvironment;
import com.codenvy.api.runner.gwt.client.RunnerServiceClient;
import com.codenvy.api.runner.internal.Constants;
import com.codenvy.ide.api.event.ProjectActionEvent;
import com.codenvy.ide.api.event.ProjectActionHandler;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.api.ui.workspace.WorkspaceAgent;
import com.codenvy.ide.commons.exception.ServerException;
import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.extension.runner.client.ProjectRunCallback;
import com.codenvy.ide.extension.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.extension.runner.client.console.RunnerConsolePresenter;
import com.codenvy.ide.extension.runner.client.update.UpdateServiceClient;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.StringUnmarshaller;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.websocket.Message;
import com.codenvy.ide.websocket.MessageBus;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.codenvy.ide.websocket.rest.SubscriptionHandler;
import com.codenvy.ide.websocket.rest.Unmarshallable;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import java.util.List;

import static com.codenvy.ide.api.notification.Notification.Status.FINISHED;
import static com.codenvy.ide.api.notification.Notification.Status.PROGRESS;
import static com.codenvy.ide.api.notification.Notification.Type.ERROR;

/**
 * Controls launching application.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class RunnerController implements Notification.OpenNotificationHandler {

    public static final String RUNNER_STATUS_CHANNEL = "runner:status:";
    public static final String RUNNER_OUTPUT_CHANNEL = "runner:output:";
    private final DtoUnmarshallerFactory                            dtoUnmarshallerFactory;
    private final DtoFactory                                        dtoFactory;
    private       MessageBus                                        messageBus;
    private       WorkspaceAgent                                    workspaceAgent;
    private       ResourceProvider                                  resourceProvider;
    private       RunnerConsolePresenter                            console;
    private       RunnerServiceClient                               service;
    private       UpdateServiceClient                               updateService;
    private       RunnerLocalizationConstant                        constant;
    private       NotificationManager                               notificationManager;
    private       Notification                                      notification;
    private       Project                                           project;
    /** Launched app. */
    private       ApplicationProcessDescriptor                      currentApplication;
    /** Determines whether any application is launched. */
    private       boolean                                           isLaunchingInProgress;
    private       ProjectRunCallback                                runCallback;
    protected     SubscriptionHandler<LogMessage>                   runnerOutputHandler;
    protected     SubscriptionHandler<ApplicationProcessDescriptor> runStatusHandler;

    /** Create controller. */
    @Inject
    public RunnerController(ResourceProvider resourceProvider,
                            EventBus eventBus,
                            WorkspaceAgent workspaceAgent,
                            final RunnerConsolePresenter console,
                            RunnerServiceClient service,
                            UpdateServiceClient updateService,
                            RunnerLocalizationConstant constant,
                            NotificationManager notificationManager,
                            DtoUnmarshallerFactory dtoUnmarshallerFactory,
                            DtoFactory dtoFactory,
                            MessageBus messageBus) {
        this.resourceProvider = resourceProvider;
        this.workspaceAgent = workspaceAgent;
        this.console = console;
        this.service = service;
        this.updateService = updateService;
        this.constant = constant;
        this.notificationManager = notificationManager;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.dtoFactory = dtoFactory;
        this.messageBus = messageBus;

        eventBus.addHandler(ProjectActionEvent.TYPE, new ProjectActionHandler() {
            @Override
            public void onProjectOpened(ProjectActionEvent event) {
                isLaunchingInProgress = false;
                currentApplication = null;
            }

            @Override
            public void onProjectClosed(ProjectActionEvent event) {
                isLaunchingInProgress = false;
                if (isAnyAppLaunched()) {
                    stopActiveProject();
                }
                console.clear();
                project = null;
                currentApplication = null;
            }

            @Override
            public void onProjectDescriptionChanged(ProjectActionEvent event) {
            }
        });
    }

    /**
     * Determines whether any application is launched.
     *
     * @return <code>true</code> if any application is launched, and <code>false</code> otherwise
     */
    public boolean isAnyAppLaunched() {
        return isLaunchingInProgress;
    }

    /** Run active project. */
    public void runActiveProject() {
        runActiveProject(null, false, null);
    }

    /**
     * Run active project, specifying environment.
     *
     * @param environment
     *         environment which will be used to run project
     */
    public void runActiveProject(RunnerEnvironment environment) {
        runActiveProject(environment, false, null);
    }

    /**
     * Run active project.
     *
     * @param debug
     *         if <code>true</code> - run in debug mode
     * @param callback
     *         callback that will be notified when project will be run
     */
    public void runActiveProject(boolean debug, final ProjectRunCallback callback) {
        runActiveProject(null, debug, callback);
    }

    /**
     * Run active project.
     *
     * @param environment
     *         environment which will be used to run project
     * @param debug
     *         if <code>true</code> - run in debug mode
     * @param callback
     *         callback that will be notified when project will be run
     */
    public void runActiveProject(RunnerEnvironment environment, boolean debug, final ProjectRunCallback callback) {
        project = resourceProvider.getActiveProject();
        if (project == null) {
            Window.alert("Project is not opened.");
            return;
        }
        if (isLaunchingInProgress) {
            Window.alert("Launching of another project is in progress now.");
            return;
        }

        console.clear();
        isLaunchingInProgress = true;
        notification = new Notification(constant.applicationStarting(project.getName()), PROGRESS, RunnerController.this);
        notificationManager.showNotification(notification);
        runCallback = callback;

        RunOptions runOptions = dtoFactory.createDto(RunOptions.class);
        if (debug) {
            runOptions.setDebugMode(dtoFactory.createDto(DebugMode.class).withMode("default"));
        }
        if (environment != null) {
            runOptions.setEnvironmentId(environment.getId());
        }

        service.run(project.getPath(), runOptions,
                    new AsyncRequestCallback<ApplicationProcessDescriptor>(
                            dtoUnmarshallerFactory.newUnmarshaller(ApplicationProcessDescriptor.class)) {
                        @Override
                        protected void onSuccess(ApplicationProcessDescriptor result) {
                            currentApplication = result;
                            startCheckingStatus(currentApplication);
                            startCheckingOutput(currentApplication);
                        }

                        @Override
                        protected void onFailure(Throwable exception) {
                            isLaunchingInProgress = false;
                            currentApplication = null;
                            onFail(constant.startApplicationFailed(project.getName()), exception);
                        }
                    }
                   );
    }

    private void startCheckingStatus(final ApplicationProcessDescriptor buildTaskDescriptor) {
        runStatusHandler = new SubscriptionHandler<ApplicationProcessDescriptor>(
                dtoUnmarshallerFactory.newWSUnmarshaller(ApplicationProcessDescriptor.class)) {
            @Override
            protected void onMessageReceived(ApplicationProcessDescriptor result) {
                currentApplication = result;

                switch (currentApplication.getStatus()) {
                    case RUNNING:
                        afterApplicationLaunched(currentApplication);
                        break;
                    case STOPPED:
                        isLaunchingInProgress = false;
                        currentApplication = null;
                        console.clearAppURL();
                        notification.setStatus(FINISHED);
                        notification.setMessage(constant.applicationStopped(project.getName()));
                        console.print(constant.applicationStopped(project.getName()));

                        try {
                            messageBus.unsubscribe(RUNNER_STATUS_CHANNEL + currentApplication.getProcessId(), this);
                            messageBus.unsubscribe(RUNNER_OUTPUT_CHANNEL + currentApplication.getProcessId(), runnerOutputHandler);
                        } catch (WebSocketException e) {
                            Log.error(RunnerController.class, e);
                        }
                        break;
                    case CANCELLED:
                        isLaunchingInProgress = false;
                        currentApplication = null;
                        console.clearAppURL();
                        notification.setStatus(FINISHED);
                        notification.setMessage(constant.applicationCanceled(project.getName()));

                        try {
                            messageBus.unsubscribe(RUNNER_STATUS_CHANNEL + currentApplication.getProcessId(), this);
                            messageBus.unsubscribe(RUNNER_OUTPUT_CHANNEL + currentApplication.getProcessId(), runnerOutputHandler);
                        } catch (WebSocketException e) {
                            Log.error(RunnerController.class, e);
                        }
                        break;
                }
            }

            @Override
            protected void onErrorReceived(Throwable exception) {
                isLaunchingInProgress = false;
                currentApplication = null;

                if (exception instanceof ServerException &&
                    ((ServerException)exception).getHTTPStatus() == 500) {
                    ServiceError e = dtoFactory.createDtoFromJson(exception.getMessage(), ServiceError.class);
                    onFail(constant.startApplicationFailed(project.getName()) + ": " + e.getMessage(), null);
                } else {
                    onFail(constant.startApplicationFailed(project.getName()), exception);
                }

                try {
                    messageBus.unsubscribe(RUNNER_STATUS_CHANNEL + buildTaskDescriptor.getProcessId(), this);
                } catch (WebSocketException e) {
                    Log.error(RunnerController.class, e);
                }
            }
        };

        try {
            messageBus.subscribe(RUNNER_STATUS_CHANNEL + buildTaskDescriptor.getProcessId(), runStatusHandler);
        } catch (WebSocketException e) {
            Log.error(RunnerController.class, e);
        }
    }

    private void startCheckingOutput(ApplicationProcessDescriptor applicationProcessDescriptor) {
        runnerOutputHandler = new LogMessagesHandler(applicationProcessDescriptor, console, messageBus);

        try {
            messageBus.subscribe(RUNNER_OUTPUT_CHANNEL + applicationProcessDescriptor.getProcessId(), runnerOutputHandler);
        } catch (WebSocketException e) {
            Log.error(RunnerController.class, e);
        }
    }

    /** Get logs of the currently launched application. */
    public void getLogs() {
        final Link viewLogsLink = getAppLink(currentApplication, Constants.LINK_REL_VIEW_LOG);
        if (viewLogsLink == null) {
            onFail(constant.getApplicationLogsFailed(), null);
        }

        service.getLogs(viewLogsLink, new AsyncRequestCallback<String>(new StringUnmarshaller()) {
            @Override
            protected void onSuccess(String result) {
                console.print(result);
            }

            @Override
            protected void onFailure(Throwable exception) {
                onFail(constant.getApplicationLogsFailed(), exception);
            }
        });
    }

    private void afterApplicationLaunched(ApplicationProcessDescriptor appDescriptor) {
        this.currentApplication = appDescriptor;
        if (runCallback != null) {
            runCallback.onRun(appDescriptor, project);
        }

        final Link appLink = getAppLink(appDescriptor, com.codenvy.api.runner.internal.Constants.LINK_REL_WEB_URL);
        if (appLink != null) {
            String url = appLink.getHref();

            final Link codeServerLink = getAppLink(appDescriptor, "code server");
            if (codeServerLink != null) {
                StringBuilder urlBuilder = new StringBuilder();
                urlBuilder.append(appLink.getHref());
                final String codeServerHref = codeServerLink.getHref();
                final int colon = codeServerHref.lastIndexOf(':');
                if (colon > 0) {
                    urlBuilder.append("?h=");
                    urlBuilder.append(codeServerHref.substring(0, colon));
                    urlBuilder.append("&p=");
                    urlBuilder.append(codeServerHref.substring(colon + 1));
                } else {
                    urlBuilder.append("?h=");
                    urlBuilder.append(codeServerHref);
                }
                url = urlBuilder.toString();
            }
            console.setAppURL(url);
        }
        notification.setStatus(FINISHED);
        notification.setMessage(constant.applicationStarted(project.getName()));
    }

    private void onFail(String message, Throwable exception) {
        if (notification != null) {
            notification.setStatus(FINISHED);
            notification.setType(ERROR);
            notification.setMessage(message);
        }

        if (exception != null && exception.getMessage() != null) {
            message += ": " + exception.getMessage();
        }
        console.print(message);
    }

    private Link getAppLink(ApplicationProcessDescriptor appDescriptor, String rel) {
        List<Link> links = appDescriptor.getLinks();
        for (Link link : links) {
            if (link.getRel().equalsIgnoreCase(rel))
                return link;
        }
        return null;
    }

    @Override
    public void onOpenClicked() {
        workspaceAgent.setActivePart(console);
    }

    /** Stop the currently launched application. */
    public void stopActiveProject() {
        final Link stopLink = getAppLink(currentApplication, Constants.LINK_REL_STOP);
        if (stopLink == null) {
            onFail(constant.stopApplicationFailed(project.getName()), null);
            return;
        }

        service.stop(stopLink, new AsyncRequestCallback<ApplicationProcessDescriptor>(
                dtoUnmarshallerFactory.newUnmarshaller(ApplicationProcessDescriptor.class)) {
            @Override
            protected void onSuccess(ApplicationProcessDescriptor result) {
                // stopping will be processed in startCheckingStatus() method
            }

            @Override
            protected void onFailure(Throwable exception) {
                currentApplication = null;
                onFail(constant.stopApplicationFailed(project.getName()), exception);
            }
        });
    }

    /** Updates launched Codenvy Extension. */
    public void updateExtension() {
        final Notification notification =
                new Notification(constant.applicationUpdating(project.getName()), PROGRESS, RunnerController.this);
        notificationManager.showNotification(notification);
        try {
            updateService.update(currentApplication, new RequestCallback<Void>() {
                @Override
                protected void onSuccess(Void result) {
                    notification.setStatus(FINISHED);
                    notification.setMessage(constant.applicationUpdated(project.getName()));
                }

                @Override
                protected void onFailure(Throwable exception) {
                    notification.setStatus(FINISHED);
                    notification.setType(ERROR);
                    notification.setMessage(constant.updateApplicationFailed(project.getName()));

                    if (exception != null && exception.getMessage() != null) {
                        console.print(exception.getMessage());
                    }
                }
            });
        } catch (WebSocketException e) {
            notification.setStatus(FINISHED);
            notification.setType(ERROR);
            notification.setMessage(constant.updateApplicationFailed(project.getName()));
        }
    }

}
