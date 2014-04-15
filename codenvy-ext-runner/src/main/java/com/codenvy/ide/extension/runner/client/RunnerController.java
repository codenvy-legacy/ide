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
package com.codenvy.ide.extension.runner.client;

import com.codenvy.api.core.rest.shared.dto.Link;
import com.codenvy.api.core.rest.shared.dto.ServiceError;
import com.codenvy.api.runner.ApplicationStatus;
import com.codenvy.api.runner.dto.ApplicationProcessDescriptor;
import com.codenvy.api.runner.dto.RunOptions;
import com.codenvy.api.runner.gwt.client.RunnerServiceClient;
import com.codenvy.api.runner.internal.dto.DebugMode;
import com.codenvy.ide.api.event.ProjectActionEvent;
import com.codenvy.ide.api.event.ProjectActionHandler;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.api.ui.workspace.WorkspaceAgent;
import com.codenvy.ide.commons.exception.ServerException;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.StringUnmarshaller;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.websocket.MessageBus;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.codenvy.ide.websocket.rest.StringUnmarshallerWS;
import com.codenvy.ide.websocket.rest.SubscriptionHandler;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import java.util.List;

import static com.codenvy.ide.api.notification.Notification.Status.FINISHED;
import static com.codenvy.ide.api.notification.Notification.Status.PROGRESS;
import static com.codenvy.ide.api.notification.Notification.Type.ERROR;

/**
 * This class controls launching application.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class RunnerController implements Notification.OpenNotificationHandler {
    private final DtoUnmarshallerFactory       dtoUnmarshallerFactory;
    private final DtoFactory                   dtoFactory;
    private       MessageBus                   messageBus;
    private       WorkspaceAgent               workspaceAgent;
    private       ResourceProvider             resourceProvider;
    private       ConsolePart                  console;
    private       RunnerServiceClient          service;
    private       UpdateServiceClient          updateService;
    private       RunnerLocalizationConstant   constant;
    private       NotificationManager          notificationManager;
    private       Notification                 notification;
    private       Project                      currentProject;
    /** Launched app. */
    private       ApplicationProcessDescriptor applicationProcessDescriptor;
    /** Handler for processing Maven build status which is received over WebSocket connection. */
    private       SubscriptionHandler<String>  runStatusHandler;
    /** Is launching of any application in progress? */
    private       boolean                      isLaunchingInProgress;
    private       ProjectRunCallback           runCallback;

    /**
     * Create controller.
     *
     * @param resourceProvider
     *         {@link com.codenvy.ide.api.resources.ResourceProvider}
     * @param eventBus
     *         {@link com.google.web.bindery.event.shared.EventBus}
     * @param workspaceAgent
     *         {@link com.codenvy.ide.api.ui.workspace.WorkspaceAgent}
     * @param console
     *         {@link com.codenvy.ide.api.parts.ConsolePart}
     * @param service
     *         {@link com.codenvy.api.runner.gwt.client.RunnerServiceClient}
     * @param updateService
     *         {@link com.codenvy.ide.extension.runner.client.UpdateServiceClient}
     * @param constant
     *         {@link com.codenvy.ide.extension.runner.client.RunnerLocalizationConstant}
     * @param notificationManager
     *         {@link com.codenvy.ide.api.notification.NotificationManager}
     */
    @Inject
    public RunnerController(ResourceProvider resourceProvider,
                            EventBus eventBus,
                            WorkspaceAgent workspaceAgent,
                            final ConsolePart console,
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
                applicationProcessDescriptor = null;
            }

            @Override
            public void onProjectClosed(ProjectActionEvent event) {
                isLaunchingInProgress = false;
                if (isAnyAppLaunched()) {
                    stopActiveProject();
                    console.clear();
                }
                currentProject = null;
                applicationProcessDescriptor = null;
            }

            @Override
            public void onProjectDescriptionChanged(ProjectActionEvent event) {
            }
        });
    }

    /**
     * Check whether any application is launched.
     *
     * @return <code>true</code> if any application is launched, and <code>false</code> otherwise
     */
    public boolean isAnyAppLaunched() {
        return isLaunchingInProgress;
    }

    /**
     * Run current project.
     *
     * @param debug
     *         if <code>true</code> - run in debug mode
     */
    public void runActiveProject(boolean debug) {
        runActiveProject(debug, null);
    }

    /**
     * Run current project.
     *
     * @param debug
     *         if <code>true</code> - run in debug mode
     * @param callback
     *         callback that will be notified when project run
     */
    public void runActiveProject(boolean debug, final ProjectRunCallback callback) {
        currentProject = resourceProvider.getActiveProject();
        if (currentProject == null) {
            Window.alert("Project is not opened.");
            return;
        }
        if (isLaunchingInProgress) {
            Window.alert("Launching of another project is in progress now.");
            return;
        }

        runCallback = callback;
        isLaunchingInProgress = true;
        notification = new Notification(constant.applicationStarting(currentProject.getName()), PROGRESS, RunnerController.this);
        notificationManager.showNotification(notification);

        RunOptions runOptions = dtoFactory.createDto(RunOptions.class);
        if (debug) {
            runOptions.setDebugMode(dtoFactory.createDto(DebugMode.class).withMode("default"));
        }
        runActiveProject(runOptions);
    }

    private void runActiveProject(RunOptions runOptions) {
        service.run(currentProject.getPath(), runOptions,
                    new AsyncRequestCallback<ApplicationProcessDescriptor>(
                            dtoUnmarshallerFactory.newUnmarshaller(ApplicationProcessDescriptor.class)) {
                        @Override
                        protected void onSuccess(ApplicationProcessDescriptor result) {
                            applicationProcessDescriptor = result;
                            startCheckingStatus(applicationProcessDescriptor);
                        }

                        @Override
                        protected void onFailure(Throwable exception) {
                            isLaunchingInProgress = false;
                            applicationProcessDescriptor = null;
                            onFail(constant.startApplicationFailed(currentProject.getName()), exception);
                        }
                    }
                   );
    }

    /** Get logs of the currently launched application. */
    public void getLogs() {
        final Link viewLogsLink = getAppLink(applicationProcessDescriptor, "view logs");
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

    /** Stop the currently launched application. */
    public void stopActiveProject() {
        final Link stopLink = getAppLink(applicationProcessDescriptor, "stop");
        if (stopLink == null) {
            onFail(constant.stopApplicationFailed(currentProject.getName()), null);
        }

        service.stop(stopLink, new AsyncRequestCallback<String>(new StringUnmarshaller()) {
            @Override
            protected void onSuccess(String result) {
                console.print(constant.applicationStopped(currentProject.getName()));
                getLogs();
                applicationProcessDescriptor = null;
            }

            @Override
            protected void onFailure(Throwable exception) {
                applicationProcessDescriptor = null;
                onFail(constant.stopApplicationFailed(currentProject.getName()), exception);
            }
        });
    }

    private void afterApplicationLaunched(ApplicationProcessDescriptor appDescriptor) {
        this.applicationProcessDescriptor = appDescriptor;
        if (runCallback != null) {
            runCallback.onRun(appDescriptor);
        }

        final Link appLink = getAppLink(appDescriptor, com.codenvy.api.runner.internal.Constants.LINK_REL_WEB_URL);
        if (appLink != null) {
            String url = appLink.getHref();

            final Link codeServerLink = getAppLink(appDescriptor, "code server");
            if (codeServerLink != null) {
                StringBuilder urlBuilder = new StringBuilder();
                urlBuilder.append(url);
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

            console.print(constant.applicationStartedOnUrl(currentProject.getName(),
                                                           "<a href=\"" + url + "\" target=\"_blank\">" + url + "</a>"));
        }
        notification.setStatus(FINISHED);
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


    /**
     * Starts checking job status by subscribing on receiving
     * messages over WebSocket or scheduling task to check status.
     *
     * @param buildTaskDescriptor
     *         the build job to check status
     */
    private void startCheckingStatus(final ApplicationProcessDescriptor buildTaskDescriptor) {
        runStatusHandler = new SubscriptionHandler<String>(new StringUnmarshallerWS()) {
            @Override
            protected void onMessageReceived(String result) {
                ApplicationProcessDescriptor newAppDescriptor = dtoFactory.createDtoFromJson(result, ApplicationProcessDescriptor.class);
                ApplicationStatus status = newAppDescriptor.getStatus();
                if (status == ApplicationStatus.RUNNING) {
                    afterApplicationLaunched(newAppDescriptor);
                } else if (status == ApplicationStatus.STOPPED) {
                    isLaunchingInProgress = false;
                    getLogs();
                    console.print(constant.applicationStopped(currentProject.getName()));
                    try {
                        messageBus.unsubscribe("runner:status:" + newAppDescriptor.getProcessId(), this);
                    } catch (WebSocketException e) {
                        Log.error(RunnerController.class, e);
                    }
                    applicationProcessDescriptor = null;
                } else if (status == ApplicationStatus.CANCELLED) {
                    onFail(constant.startApplicationFailed(currentProject.getName()), null);
                    try {
                        messageBus.unsubscribe("runner:status:" + newAppDescriptor.getProcessId(), this);
                    } catch (WebSocketException e) {
                        Log.error(RunnerController.class, e);
                    }
                    isLaunchingInProgress = false;
                    applicationProcessDescriptor = null;
                }
            }

            @Override
            protected void onErrorReceived(Throwable exception) {
                isLaunchingInProgress = false;
                applicationProcessDescriptor = null;

                if (exception instanceof ServerException &&
                    ((ServerException)exception).getHTTPStatus() == 500) {
                    ServiceError e = dtoFactory.createDtoFromJson(exception.getMessage(), ServiceError.class);
                    onFail(constant.startApplicationFailed(currentProject.getName()) + ": " + e.getMessage(), null);
                } else {
                    onFail(constant.startApplicationFailed(currentProject.getName()), exception);
                }

                try {
                    messageBus.unsubscribe("runner:status:" + buildTaskDescriptor.getProcessId(), this);
                } catch (WebSocketException e) {
                    Log.error(RunnerController.class, e);
                }
            }
        };

        try {
            messageBus.subscribe("runner:status:" + buildTaskDescriptor.getProcessId(), runStatusHandler);
        } catch (WebSocketException e) {
            Log.error(RunnerController.class, e);
        }
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

    /** Updates launched Codenvy Extension. */
    public void updateExtension() {
        final Notification notification =
                new Notification(constant.applicationUpdating(currentProject.getName()), PROGRESS, RunnerController.this);
        notificationManager.showNotification(notification);
        try {
            updateService.update(applicationProcessDescriptor, new RequestCallback<Void>() {
                @Override
                protected void onSuccess(Void result) {
                    notification.setStatus(FINISHED);
                    notification.setMessage(constant.applicationUpdated(currentProject.getName()));
                }

                @Override
                protected void onFailure(Throwable exception) {
                    notification.setStatus(FINISHED);
                    notification.setType(ERROR);
                    notification.setMessage(constant.updateApplicationFailed(currentProject.getName()));

                    if (exception != null && exception.getMessage() != null) {
                        console.print(exception.getMessage());
                    }
                }
            });
        } catch (WebSocketException e) {
            notification.setStatus(FINISHED);
            notification.setType(ERROR);
            notification.setMessage(constant.updateApplicationFailed(currentProject.getName()));
        }
    }
}
