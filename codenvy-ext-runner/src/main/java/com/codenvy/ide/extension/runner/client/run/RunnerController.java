/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.extension.runner.client.run;

import com.codenvy.api.core.rest.shared.dto.Link;
import com.codenvy.api.core.rest.shared.dto.ServiceError;
import com.codenvy.api.runner.dto.ApplicationProcessDescriptor;
import com.codenvy.api.runner.dto.DebugMode;
import com.codenvy.api.runner.dto.RunOptions;
import com.codenvy.api.runner.dto.RunnerEnvironment;
import com.codenvy.api.runner.dto.RunnerMetric;
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
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import javax.annotation.Nullable;
import java.util.List;

import static com.codenvy.ide.api.notification.Notification.Status.FINISHED;
import static com.codenvy.ide.api.notification.Notification.Status.PROGRESS;
import static com.codenvy.ide.api.notification.Notification.Type.ERROR;
import static com.codenvy.ide.api.notification.Notification.Type.INFO;
import static com.codenvy.ide.api.notification.Notification.Type.WARNING;

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
    protected final ResourceProvider           resourceProvider;
    private final   DtoUnmarshallerFactory     dtoUnmarshallerFactory;
    private final   DtoFactory                 dtoFactory;
    private         MessageBus                 messageBus;
    private         WorkspaceAgent             workspaceAgent;
    private         RunnerConsolePresenter     console;
    private         RunnerServiceClient        service;
    private         UpdateServiceClient        updateService;
    private         RunnerLocalizationConstant constant;
    private         NotificationManager        notificationManager;
    private         Notification               notification;
    /** Whether any app is running now? */
    protected boolean isAnyAppRunning = false;
    protected Project                                           activeProject;
    /** Descriptor of the last launched application. */
    private   ApplicationProcessDescriptor                      lastApplicationDescriptor;
    private   RunnerMetric                                      lastAppWaitingTimeLimit;
    private   ProjectRunCallback                                runCallback;
    protected SubscriptionHandler<LogMessage>                   runnerOutputHandler;
    protected SubscriptionHandler<ApplicationProcessDescriptor> runnerStatusHandler;
    private   long                                              startTime;

    @Inject
    public RunnerController(EventBus eventBus,
                            WorkspaceAgent workspaceAgent,
                            ResourceProvider resourceProvider,
                            final RunnerConsolePresenter console,
                            RunnerServiceClient service,
                            UpdateServiceClient updateService,
                            RunnerLocalizationConstant constant,
                            NotificationManager notificationManager,
                            DtoFactory dtoFactory,
                            DtoUnmarshallerFactory dtoUnmarshallerFactory,
                            MessageBus messageBus) {
        this.workspaceAgent = workspaceAgent;
        this.resourceProvider = resourceProvider;
        this.console = console;
        this.service = service;
        this.updateService = updateService;
        this.constant = constant;
        this.notificationManager = notificationManager;
        this.dtoFactory = dtoFactory;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.messageBus = messageBus;

        eventBus.addHandler(ProjectActionEvent.TYPE, new ProjectActionHandler() {
            @Override
            public void onProjectOpened(ProjectActionEvent event) {
            }

            @Override
            public void onProjectClosed(ProjectActionEvent event) {
                if (isAnyAppRunning()) {
                    stopActiveProject();
                }
                console.clear();
                activeProject = null;
                lastApplicationDescriptor = null;
            }

            @Override
            public void onProjectDescriptionChanged(ProjectActionEvent event) {
            }
        });
    }

    @Override
    public void onOpenClicked() {
        workspaceAgent.setActivePart(console);
    }

    /**
     * Determines whether any application is running.
     *
     * @return <code>true</code> if any application is running, and <code>false</code> otherwise
     */
    public boolean isAnyAppRunning() {
        return isAnyAppRunning;
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
        if (isAnyAppRunning) {
            final String message = "Launching of another project is in progress now.";
            Notification notification = new Notification(message, ERROR);
            notificationManager.showNotification(notification);
            return;
        }

        lastApplicationDescriptor = null;
        activeProject = resourceProvider.getActiveProject();

        notification = new Notification(constant.applicationStarting(activeProject.getName()), PROGRESS, RunnerController.this);
        notificationManager.showNotification(notification);
        runCallback = callback;

        RunOptions runOptions = dtoFactory.createDto(RunOptions.class);
        if (debug) {
            runOptions.setDebugMode(dtoFactory.createDto(DebugMode.class).withMode("default"));
        }
        if (environment != null) {
            runOptions.setEnvironmentId(environment.getId());
        }

        service.run(activeProject.getPath(), runOptions,
                    new AsyncRequestCallback<ApplicationProcessDescriptor>(
                            dtoUnmarshallerFactory.newUnmarshaller(ApplicationProcessDescriptor.class)) {
                        @Override
                        protected void onSuccess(ApplicationProcessDescriptor result) {
                            lastApplicationDescriptor = result;
                            isAnyAppRunning = true;
                            startCheckingStatus(lastApplicationDescriptor);
                            startCheckingOutput(lastApplicationDescriptor);
                        }

                        @Override
                        protected void onFailure(Throwable exception) {
                            onFail(constant.startApplicationFailed(activeProject.getName()), exception);
                        }
                    }
                   );
    }

    private void startCheckingStatus(final ApplicationProcessDescriptor buildTaskDescriptor) {
        runnerStatusHandler = new SubscriptionHandler<ApplicationProcessDescriptor>(
                dtoUnmarshallerFactory.newWSUnmarshaller(ApplicationProcessDescriptor.class)) {
            @Override
            protected void onMessageReceived(ApplicationProcessDescriptor result) {
                lastApplicationDescriptor = result;
                onApplicationStatusUpdated(result);
            }

            @Override
            protected void onErrorReceived(Throwable exception) {
                isAnyAppRunning = false;
                lastApplicationDescriptor = null;

                if (exception instanceof ServerException &&
                    ((ServerException)exception).getHTTPStatus() == 500) {
                    ServiceError e = dtoFactory.createDtoFromJson(exception.getMessage(), ServiceError.class);
                    onFail(constant.startApplicationFailed(activeProject.getName()) + ": " + e.getMessage(), null);
                } else {
                    onFail(constant.startApplicationFailed(activeProject.getName()), exception);
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

    private void stopCheckingStatus() {
        try {
            messageBus.unsubscribe(RUNNER_STATUS_CHANNEL + lastApplicationDescriptor.getProcessId(), runnerStatusHandler);
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

    /** Process changing application status. */
    private void onApplicationStatusUpdated(ApplicationProcessDescriptor descriptor) {
        switch (descriptor.getStatus()) {
            case RUNNING:
                startTime = System.currentTimeMillis();

                notification.setStatus(FINISHED);
                notification.setType(INFO);
                notification.setMessage(constant.applicationStarted(activeProject.getName()));

                workspaceAgent.setActivePart(console);

                if (runCallback != null) {
                    runCallback.onRun(descriptor, activeProject);
                }
                break;
            case STOPPED:
                isAnyAppRunning = false;
                stopCheckingStatus();

                // this mean that application has failed to start
                if (descriptor.getStartTime() == -1) {
                    notification.setType(ERROR);
                    getLogs();
                } else {
                    notification.setType(INFO);
                }

                notification.setStatus(FINISHED);
                notification.setMessage(constant.applicationStopped(activeProject.getName()));

                workspaceAgent.setActivePart(console);
                break;
            case FAILED:
                isAnyAppRunning = false;
                stopCheckingStatus();
                getLogs();

                notification.setStatus(FINISHED);
                notification.setType(ERROR);
                notification.setMessage(constant.applicationFailed(activeProject.getName()));

                workspaceAgent.setActivePart(console);
                break;
            case CANCELLED:
                isAnyAppRunning = false;
                stopCheckingStatus();

                notification.setStatus(FINISHED);
                notification.setType(WARNING);
                notification.setMessage(constant.applicationCanceled(activeProject.getName()));

                workspaceAgent.setActivePart(console);
                break;
        }
    }

    /** Get logs of the currently launched application. */
    public void getLogs() {
        final Link viewLogsLink = getLink(lastApplicationDescriptor, Constants.LINK_REL_VIEW_LOG);
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

    /** Stop the currently running application. */
    public void stopActiveProject() {
        final Link stopLink = getLink(lastApplicationDescriptor, Constants.LINK_REL_STOP);
        if (stopLink == null) {
            onFail(constant.stopApplicationFailed(activeProject.getName()), null);
            return;
        }

        service.stop(stopLink, new AsyncRequestCallback<ApplicationProcessDescriptor>(
                dtoUnmarshallerFactory.newUnmarshaller(ApplicationProcessDescriptor.class)) {
            @Override
            protected void onSuccess(ApplicationProcessDescriptor result) {
                // stopping will be processed in onApplicationStatusUpdated() method
            }

            @Override
            protected void onFailure(Throwable exception) {
                isAnyAppRunning = false;
                lastApplicationDescriptor = null;
                onFail(constant.stopApplicationFailed(activeProject.getName()), exception);
            }
        });
    }

    /** Updates launched Codenvy Extension. */
    public void updateExtension() {
        final Notification notification =
                new Notification(constant.applicationUpdating(activeProject.getName()), PROGRESS, RunnerController.this);
        notificationManager.showNotification(notification);
        try {
            updateService.update(lastApplicationDescriptor, new RequestCallback<Void>() {
                @Override
                protected void onSuccess(Void result) {
                    notification.setStatus(FINISHED);
                    notification.setMessage(constant.applicationUpdated(activeProject.getName()));
                }

                @Override
                protected void onFailure(Throwable exception) {
                    notification.setStatus(FINISHED);
                    notification.setType(ERROR);
                    notification.setMessage(constant.updateApplicationFailed(activeProject.getName()));

                    if (exception != null && exception.getMessage() != null) {
                        console.print(exception.getMessage());
                    }
                }
            });
        } catch (WebSocketException e) {
            notification.setStatus(FINISHED);
            notification.setType(ERROR);
            notification.setMessage(constant.updateApplicationFailed(activeProject.getName()));
        }
    }

    /** Returns URL of the application which is currently running. */
    @Nullable
    public String getCurrentAppURL() {
        // don't show app URL in console when app is stopped. After some time this URL may be used by other app.
        if (lastApplicationDescriptor != null && getCurrentAppStopTime() == null) {
            return getAppLink(lastApplicationDescriptor);
        }
        return null;
    }

    /** Returns URL of the application which is currently running. */
    @Nullable
    public String getCurrentAppShellURL() {
        // don't show shell URL in console when app is stopped. After some time this URL may be used by other app.
        if (lastApplicationDescriptor != null && getCurrentAppStopTime() == null) {
            Link link = getLink(lastApplicationDescriptor, "shell url");
            if (link != null) {
                return link.getHref();
            }
        }
        return null;
    }

    /** Returns startTime {@link RunnerMetric}. */
    @Nullable
    public RunnerMetric getCurrentAppStartTime() {
        return getRunnerMetric("startTime");
    }

    /** Returns waitingTimeLimit {@link RunnerMetric}. */
    @Nullable
    public RunnerMetric getCurrentAppTimeoutThreshold() {
        if (lastApplicationDescriptor == null) {
            return null;
        }
        RunnerMetric waitingTime = getRunnerMetric("waitingTimeLimit");
        if (waitingTime != null) {
            lastAppWaitingTimeLimit = waitingTime;
        }
        return lastAppWaitingTimeLimit;
    }

    /** Returns stopTime {@link RunnerMetric}. */
    @Nullable
    public RunnerMetric getCurrentAppStopTime() {
        return getRunnerMetric("stopTime");
    }

    /** Returns uptime {@link RunnerMetric}. */
    @Nullable
    public RunnerMetric getTotalTime() {
        // if app already stopped, get uptime from server
        if (getCurrentAppStopTime() != null) {
            return getRunnerMetric("uptime");
        }

        // if app is running now, count uptime on the client-side
        if (getCurrentAppStartTime() != null) {
            final long totalTimeMillis = System.currentTimeMillis() - startTime;
            int ss = (int)(totalTimeMillis / 1000);
            int mm = 0;
            if (ss >= 60) {
                mm = ss / 60;
                ss = ss % 60;
            }
            int hh = 0;
            if (mm >= 60) {
                hh = mm / 60;
                mm = mm % 60;
            }
            int d = 0;
            if (hh >= 24) {
                d = hh / 24;
                hh = hh % 24;
            }
            final String value =
                    String.valueOf("" + d + "d:" + getDoubleDigit(hh) + "h:" + getDoubleDigit(mm) + "m:" + getDoubleDigit(ss) + "s");
            return dtoFactory.createDto(RunnerMetric.class).withDescription("Application's uptime").withValue(value);
        }

        return null;
    }

    @Nullable
    private RunnerMetric getRunnerMetric(String metricName) {
        if (lastApplicationDescriptor != null) {
            for (RunnerMetric runnerStat : lastApplicationDescriptor.getRunStats()) {
                if (metricName.equals(runnerStat.getName())) {
                    return runnerStat;
                }
            }
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

    @Nullable
    private static Link getLink(ApplicationProcessDescriptor appDescriptor, String rel) {
        List<Link> links = appDescriptor.getLinks();
        for (Link link : links) {
            if (link.getRel().equalsIgnoreCase(rel))
                return link;
        }
        return null;
    }

    /** Get a double digit int from a single, e.g. 1 = "01", 2 = "02". */
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
