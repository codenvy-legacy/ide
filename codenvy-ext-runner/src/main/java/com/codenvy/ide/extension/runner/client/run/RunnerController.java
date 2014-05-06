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
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.extension.runner.client.ProjectRunCallback;
import com.codenvy.ide.extension.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.extension.runner.client.console.RunnerConsolePresenter;
import com.codenvy.ide.extension.runner.client.update.UpdateServiceClient;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.StringUnmarshaller;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.websocket.MessageBus;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.codenvy.ide.websocket.rest.SubscriptionHandler;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import java.util.Date;
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

    /** WebSocket channel to get application's status. */
    public static final String RUNNER_STATUS_CHANNEL = "runner:status:";
    /** WebSocket channel to get runner output. */
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
    /** Descriptor of the application which is currently running. */
    private       ApplicationProcessDescriptor                      currentApplication;
    private       ProjectRunCallback                                runCallback;
    protected     SubscriptionHandler<LogMessage>                   runnerOutputHandler;
    protected     SubscriptionHandler<ApplicationProcessDescriptor> runnerStatusHandler;

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
                currentApplication = null;
            }

            @Override
            public void onProjectClosed(ProjectActionEvent event) {
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
     * Determines whether any application is running.
     *
     * @return <code>true</code> if any application is running, and <code>false</code> otherwise
     */
    public boolean isAnyAppLaunched() {
        return currentApplication != null;
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
        if (currentApplication != null) {
            Window.alert("Launching of another project is in progress now.");
            return;
        }

        currentApplication = null;
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
                            currentApplication = null;
                            onFail(constant.startApplicationFailed(project.getName()), exception);
                        }
                    }
                   );
    }

    private void startCheckingStatus(final ApplicationProcessDescriptor buildTaskDescriptor) {
        runnerStatusHandler = new SubscriptionHandler<ApplicationProcessDescriptor>(
                dtoUnmarshallerFactory.newWSUnmarshaller(ApplicationProcessDescriptor.class)) {
            @Override
            protected void onMessageReceived(ApplicationProcessDescriptor result) {
                currentApplication = result;

                switch (currentApplication.getStatus()) {
                    case RUNNING:
                        afterApplicationLaunched(currentApplication);
                        break;
                    case STOPPED:
//                        currentApplication = null;
                        notification.setStatus(FINISHED);
                        notification.setMessage(constant.applicationStopped(project.getName()));
                        console.print(constant.applicationStopped(project.getName()));

                        try {
                            messageBus.unsubscribe(RUNNER_STATUS_CHANNEL + currentApplication.getProcessId(), this);
                        } catch (WebSocketException e) {
                            Log.error(RunnerController.class, e);
                        }
                        break;
                    case CANCELLED:
                        currentApplication = null;
                        notification.setStatus(FINISHED);
                        notification.setMessage(constant.applicationCanceled(project.getName()));

                        try {
                            messageBus.unsubscribe(RUNNER_STATUS_CHANNEL + currentApplication.getProcessId(), this);
                        } catch (WebSocketException e) {
                            Log.error(RunnerController.class, e);
                        }
                        break;
                }
            }

            @Override
            protected void onErrorReceived(Throwable exception) {
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
            messageBus.subscribe(RUNNER_STATUS_CHANNEL + buildTaskDescriptor.getProcessId(), runnerStatusHandler);
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

    private void afterApplicationLaunched(ApplicationProcessDescriptor appDescriptor) {
        currentApplication = appDescriptor;
        if (runCallback != null) {
            runCallback.onRun(appDescriptor, project);
        }

        notification.setStatus(FINISHED);
        notification.setMessage(constant.applicationStarted(project.getName()));
    }

    /** Get logs of the currently launched application. */
    public void getLogs() {
        final Link viewLogsLink = getLink(currentApplication, Constants.LINK_REL_VIEW_LOG);
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

    @Override
    public void onOpenClicked() {
        workspaceAgent.setActivePart(console);
    }

    /** Stop the currently launched application. */
    public void stopActiveProject() {
        final Link stopLink = getLink(currentApplication, Constants.LINK_REL_STOP);
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

    /** Returns URL of the application which is currently running. */
    public String getCurrentAppURL() {
        if (currentApplication != null) {
            return getAppLink(currentApplication);
        }
        return null;
    }

    /** Returns time when last app started, in format HH:mm:ss. */
    public String getCurrentAppStartTime() {
        if (currentApplication != null) {
            final Date startDate = new Date(currentApplication.getStartTime());
            return DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.HOUR24_MINUTE_SECOND).format(startDate);
        }
        return null;
    }

    /** Returns time when last app stopped in format HH:mm:ss. */
    public String getCurrentAppStopTime() {
        if (currentApplication != null && currentApplication.getStopTime() > 0) {
            final Date stopDate = new Date(currentApplication.getStopTime());
            return DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.HOUR24_MINUTE_SECOND).format(stopDate);
        }
        return null;
    }

    /** Returns total time which application was launched, in format mm:ss.ms. */
    public String getTotalTime() {
        if (currentApplication != null && currentApplication.getStopTime() > 0) {
            final long totalTimeMs = currentApplication.getStopTime() - currentApplication.getStartTime();
            int ms = (int)(totalTimeMs % 1000);
            int ss = (int)(totalTimeMs / 1000);
            int mm = 0;
            if (ss > 60) {
                mm = ss / 60;
                ss = ss % 60;
            }
            return String.valueOf("" + getDoubleDigit(mm) + ':' + getDoubleDigit(ss) + '.' + ms);
        }
        return null;
    }

    private static String getAppLink(ApplicationProcessDescriptor appDescriptor) {
        String url = null;
        final Link appLink = getLink(appDescriptor, com.codenvy.api.runner.internal.Constants.LINK_REL_WEB_URL);
        if (appLink != null) {
            url = appLink.getHref();

            final Link codeServerLink = getLink(appDescriptor, "code server");
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
        }
        return url;
    }

    private static Link getLink(ApplicationProcessDescriptor appDescriptor, String rel) {
        List<Link> links = appDescriptor.getLinks();
        for (Link link : links) {
            if (link.getRel().equalsIgnoreCase(rel))
                return link;
        }
        return null;
    }

    /** Get a double digit int from a single, e.g.: 1 = "01", 2 = "02". */
    private static String getDoubleDigit(int i) {
        final String doubleDigitI;
        switch (i) {
            case 0:
                doubleDigitI = "00";
                break;
            case 1:
                doubleDigitI = "01";
                break;
            case 2:
                doubleDigitI = "02";
                break;
            case 3:
                doubleDigitI = "03";
                break;
            case 4:
                doubleDigitI = "04";
                break;
            case 5:
                doubleDigitI = "05";
                break;
            case 6:
                doubleDigitI = "06";
                break;
            case 7:
                doubleDigitI = "07";
                break;
            case 8:
                doubleDigitI = "08";
                break;
            case 9:
                doubleDigitI = "09";
                break;
            default:
                doubleDigitI = Integer.toString(i);
        }
        return doubleDigitI;
    }
}
