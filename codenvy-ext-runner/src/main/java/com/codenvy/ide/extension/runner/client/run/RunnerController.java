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
import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.api.runner.ApplicationStatus;
import com.codenvy.api.runner.dto.ApplicationProcessDescriptor;
import com.codenvy.api.runner.dto.DebugMode;
import com.codenvy.api.runner.dto.ResourcesDescriptor;
import com.codenvy.api.runner.dto.RunOptions;
import com.codenvy.api.runner.dto.RunnerEnvironment;
import com.codenvy.api.runner.dto.RunnerMetric;
import com.codenvy.api.runner.gwt.client.RunnerServiceClient;
import com.codenvy.api.runner.gwt.client.utils.RunnerUtils;
import com.codenvy.api.runner.internal.Constants;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.app.CurrentProject;
import com.codenvy.ide.api.editor.CodenvyTextEditor;
import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.event.ProjectActionEvent;
import com.codenvy.ide.api.event.ProjectActionHandler;
import com.codenvy.ide.api.event.WindowActionEvent;
import com.codenvy.ide.api.event.WindowActionHandler;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.parts.WorkspaceAgent;
import com.codenvy.ide.api.projecttree.AbstractTreeNode;
import com.codenvy.ide.api.projecttree.generic.FileNode;
import com.codenvy.ide.api.theme.ThemeAgent;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.commons.exception.ServerException;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.extension.runner.client.ProjectRunCallback;
import com.codenvy.ide.extension.runner.client.RunnerExtension;
import com.codenvy.ide.extension.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.extension.runner.client.console.RunnerConsolePresenter;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.StringUnmarshaller;
import com.codenvy.ide.rest.Unmarshallable;
import com.codenvy.ide.ui.dialogs.ask.Ask;
import com.codenvy.ide.ui.dialogs.ask.AskHandler;
import com.codenvy.ide.util.StringUtils;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.websocket.MessageBus;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.StringUnmarshallerWS;
import com.codenvy.ide.websocket.rest.SubscriptionHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.codenvy.api.runner.ApplicationStatus.NEW;
import static com.codenvy.api.runner.ApplicationStatus.RUNNING;
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
    public static final String STATUS_CHANNEL     = "runner:status:";
    /** WebSocket channel to get runner output. */
    public static final String OUTPUT_CHANNEL     = "runner:output:";
    /** WebSocket channel to check application's health. */
    public static final String APP_HEALTH_CHANNEL = "runner:app_health:";
    private final DtoUnmarshallerFactory dtoUnmarshallerFactory;
    private final DtoFactory             dtoFactory;
    private final AppContext             appContext;
    /** Whether any app is running now? */
    protected boolean isAnyAppRunning = false;
    protected LogMessagesHandler                                runnerOutputHandler;
    protected SubscriptionHandler<ApplicationProcessDescriptor> runnerStatusHandler;
    protected SubscriptionHandler<String>                       runnerHealthHandler;
    private   EditorAgent                                       editorAgent;
    private   MessageBus                                        messageBus;
    private   ProjectServiceClient                              projectServiceClient;
    private   EventBus                                          eventBus;
    private   WorkspaceAgent                                    workspaceAgent;
    private   RunnerConsolePresenter                            console;
    private   RunnerServiceClient                               service;
    private   RunnerLocalizationConstant                        constant;
    private   NotificationManager                               notificationManager;
    private   Notification                                      notification;
    private   ProjectRunCallback                                runCallback;
    private   boolean                                           isLastAppHealthOk;
    // The server makes the limited quantity of tries checking application's health,
    // so we're waiting for some time (about 30 sec.) and assume that app health is OK.
    private   Timer                                             setAppHealthOkTimer;
    //show time in "Total Time" indicator start immediately  after launch running process
    private   Timer                                             totalActiveTimeTimer;
    private   RunnerMetric                                      totalActiveTimeMetric; //calculate on client-side
    private   String                                            theme;

    @Inject
    public RunnerController(EventBus eventBus,
                            final WorkspaceAgent workspaceAgent,
                            final RunnerConsolePresenter console,
                            final RunnerServiceClient service,
                            final RunnerLocalizationConstant constant,
                            final NotificationManager notificationManager,
                            DtoFactory dtoFactory,
                            EditorAgent editorAgent,
                            final DtoUnmarshallerFactory dtoUnmarshallerFactory,
                            MessageBus messageBus,
                            ThemeAgent themeAgent,
                            final AppContext appContext,
                            ProjectServiceClient projectServiceClient) {
        this.eventBus = eventBus;
        this.workspaceAgent = workspaceAgent;
        this.console = console;
        this.service = service;
        this.constant = constant;
        this.notificationManager = notificationManager;
        this.dtoFactory = dtoFactory;
        this.editorAgent = editorAgent;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.messageBus = messageBus;
        this.appContext = appContext;
        this.projectServiceClient = projectServiceClient;
        theme = themeAgent.getCurrentThemeId();

        eventBus.addHandler(ProjectActionEvent.TYPE, new ProjectActionHandler() {
            @Override
            public void onProjectOpened(final ProjectActionEvent event) {
                Unmarshallable<Array<ApplicationProcessDescriptor>> unmarshaller =
                        dtoUnmarshallerFactory.newArrayUnmarshaller(ApplicationProcessDescriptor.class);
                service.getRunningProcesses(event.getProject().getPath(),
                                            new AsyncRequestCallback<Array<ApplicationProcessDescriptor>>(unmarshaller) {
                                                @Override
                                                protected void onSuccess(Array<ApplicationProcessDescriptor> result) {
                                                    for (ApplicationProcessDescriptor processDescriptor : result.asIterable()) {
                                                        if (processDescriptor.getStatus() == NEW ||
                                                            processDescriptor.getStatus() == RUNNING) {
                                                            onAppLaunched(processDescriptor);
                                                            getLogs(false);
                                                            console.onAppStarted(appContext.getCurrentProject().getProcessDescriptor());
                                                            notificationManager.showNotification(new Notification(
                                                                    constant.projectRunningNow(event.getProject().getName()), INFO));

                                                            break;
                                                        }
                                                    }
                                                }

                                                @Override
                                                protected void onFailure(Throwable ignore) {
                                                }
                                            }
                                           );
            }

            @Override
            public void onProjectClosed(ProjectActionEvent event) {
                console.clear();
            }
        });

        eventBus.addHandler(WindowActionEvent.TYPE, new WindowActionHandler() {
            @Override
            public void onWindowClosing(WindowActionEvent event) {
                if (isAnyAppRunning() && !getRunnerMetric(RunnerMetric.TERMINATION_TIME).getValue().equals(RunnerMetric.ALWAYS_ON)) {
                    event.setMessage(constant.appWillBeStopped(appContext.getCurrentProject().getProjectDescription().getName()));
                }
            }

            @Override
            public void onWindowClosed(WindowActionEvent event) {
                if (!getRunnerMetric(RunnerMetric.TERMINATION_TIME).getValue().equals(RunnerMetric.ALWAYS_ON)) {
                    stopActiveProject(false);
                }
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

    /**
     * Check whether the files is saved before running and run active project.
     *
     * @param runOptions
     *         options to configure run process
     * @param isUserAction
     *         points whether the build is started directly by user interaction
     */
    public void runActiveProject(final RunOptions runOptions, final boolean isUserAction) {
        // Save the files before running if necessary
        Array<EditorPartPresenter> dirtyEditors = editorAgent.getDirtyEditors();
        if (dirtyEditors.isEmpty()) {
            runProject(runOptions, isUserAction);
        } else {
            Ask askWindow = new Ask(constant.titlePromptSaveFiles(), constant.messagePromptSaveFiles(), new AskHandler() {
                @Override
                public void onOk() {
                    editorAgent.saveAll(new AsyncCallback() {
                        @Override
                        public void onFailure(Throwable caught) {
                            Log.error(getClass(), caught.getMessage());

                            Notification notification = new Notification(constant.messageFailedSaveFiles(), ERROR);
                            notificationManager.showNotification(notification);
                        }

                        @Override
                        public void onSuccess(Object result) {
                            runProject(runOptions, isUserAction);
                        }
                    });
                }

                @Override
                public void onCancel() {
                    runProject(runOptions, isUserAction);
                }
            });
            askWindow.show();
        }
    }

    /**
     * Check the RAM and run active project.
     *
     * @param runOptions
     *         options to configure run process
     * @param isUserAction
     *         points whether the build is started directly by user interaction
     */
    private void runProject(final RunOptions runOptions, final boolean isUserAction) {
        service.getResources(new AsyncRequestCallback<ResourcesDescriptor>(
                dtoUnmarshallerFactory.newUnmarshaller(ResourcesDescriptor.class)) {
            @Override
            protected void onSuccess(ResourcesDescriptor resourcesDescriptor) {
                //TODO Add RAM Check for each Runner Request

                if (runOptions != null) {
                    runActiveProject(runOptions, null, isUserAction);
                } else {
                    runActiveProject(null, false, null, isUserAction);
                }
            }

            @Override
            protected void onFailure(Throwable throwable) {
                onFail(constant.getResourcesFailed(), throwable);
            }
        });
    }

    /**
     * Run active project.
     *
     * @param debug
     *         if <code>true</code> - run in debug mode
     * @param callback
     *         callback that will be notified when project will be run
     * @param isUserAction
     *         points whether the build is started directly by user interaction
     */
    public void runActiveProject(boolean debug, final ProjectRunCallback callback, boolean isUserAction) {
        runActiveProject(null, debug, callback, isUserAction);
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
     * @param isUserAction
     *         points whether the build is started directly by user interaction
     */
    public void runActiveProject(RunnerEnvironment environment, boolean debug, final ProjectRunCallback callback, boolean isUserAction) {
        final CurrentProject currentProject = appContext.getCurrentProject();
        if (currentProject == null)
            return;
        if (isAnyAppRunning) {
            Notification notification = new Notification(constant.anotherProjectRunningNow(), ERROR);
            notificationManager.showNotification(notification);
            return;
        }

        runCallback = callback;

        notification = new Notification(constant.applicationStarting(currentProject.getProjectDescription().getName()), PROGRESS,
                                        RunnerController.this);
        notificationManager.showNotification(notification);
        console.print("[INFO] " + notification.getMessage());

        RunOptions runOptions = dtoFactory.createDto(RunOptions.class);
        if (debug) {
            runOptions.setDebugMode(dtoFactory.createDto(DebugMode.class).withMode("default"));
        }
        if (environment != null) {
            runOptions.setEnvironmentId(environment.getId());
        } else if (currentProject.getRunnerEnvId() != null) {
            runOptions.setEnvironmentId(currentProject.getRunnerEnvId());
        }

        runOptions.getShellOptions().put("WebShellTheme", theme);
        runOptions.setSkipBuild(Boolean.parseBoolean(currentProject.getAttributeValue("runner:skipBuild")));


        setDefaultRam2runOptions(runOptions);


        if (isUserAction) {
            console.setActive();
        }

        service.run(currentProject.getProjectDescription().getPath(), runOptions,
                    new AsyncRequestCallback<ApplicationProcessDescriptor>(
                            dtoUnmarshallerFactory.newUnmarshaller(ApplicationProcessDescriptor.class)) {
                        @Override
                        protected void onSuccess(ApplicationProcessDescriptor result) {
                            onAppLaunched(result);
                        }

                        @Override
                        protected void onFailure(Throwable exception) {
                            onFail(constant.startApplicationFailed(currentProject.getProjectDescription().getName()), exception);
                        }
                    }
                   );
    }

    /**
     * Run active project.
     *
     * @param runOptions
     *         options to configure run process
     * @param callback
     *         callback that will be notified when project will be run
     */
    public void runActiveProject(RunOptions runOptions, final ProjectRunCallback callback, boolean isUserAction) {
        final CurrentProject currentProject = appContext.getCurrentProject();
        if (currentProject == null)
            return;
        if (isAnyAppRunning) {
            Notification notification = new Notification(constant.anotherProjectRunningNow(), ERROR);
            notificationManager.showNotification(notification);
            return;
        }

        if (currentProject.getProcessDescriptor() != null &&
            (currentProject.getProcessDescriptor().getStatus().equals(ApplicationStatus.NEW)
             || currentProject.getProcessDescriptor().getStatus().equals(ApplicationStatus.RUNNING))) {
            Notification notification = new Notification(constant.projectRunningNow(
                    currentProject.getProjectDescription().getName()), ERROR);
            notificationManager.showNotification(notification);
            return;
        }

        notification = new Notification(constant.applicationStarting(currentProject.getProjectDescription().getName()), PROGRESS,
                                        RunnerController.this);
        notificationManager.showNotification(notification);
        console.print("[INFO] " + notification.getMessage());

        if (isUserAction) {
            console.setActive();
        }

        runOptions.getShellOptions().put("WebShellTheme", theme);
        runOptions.setSkipBuild(Boolean.parseBoolean(currentProject.getAttributeValue("runner:skipBuild")));

        service.run(currentProject.getProjectDescription().getPath(), runOptions,
                    new AsyncRequestCallback<ApplicationProcessDescriptor>(
                            dtoUnmarshallerFactory.newUnmarshaller(ApplicationProcessDescriptor.class)) {
                        @Override
                        protected void onSuccess(ApplicationProcessDescriptor result) {
                            isAnyAppRunning = true;
                            if (notification == null)
                                notification =
                                        new Notification(constant.applicationStarted(currentProject.getProjectDescription().getName()),
                                                         INFO);
                            currentProject.setProcessDescriptor(result);
                            currentProject.setIsRunningEnabled(false);
                            notification.setStatus(FINISHED);
                            notification.setMessage(constant.applicationStarted(currentProject.getProjectDescription().getName()));
                            startCheckingAppStatus(result);
                            startCheckingAppOutput(result);
                        }

                        @Override
                        protected void onFailure(Throwable exception) {
                            onFail(constant.startApplicationFailed(currentProject.getProjectDescription().getName()), exception);
                        }
                    }
                   );
    }

    private void setDefaultRam2runOptions(RunOptions runOptions) {
        Map<String, String> preferences = appContext.getCurrentUser().getProfile().getPreferences();
        if (preferences != null && preferences.containsKey(RunnerExtension.PREFS_RUNNER_RAM_SIZE_DEFAULT)) {
            try {
                Log.info(RunnerController.class, preferences.get(RunnerExtension.PREFS_RUNNER_RAM_SIZE_DEFAULT));
                int ram = Integer.parseInt(preferences.get(RunnerExtension.PREFS_RUNNER_RAM_SIZE_DEFAULT));
                runOptions.setMemorySize(ram);
            } catch (NumberFormatException e) {
                Log.error(RunnerController.class, e);
            }
        }
    }

    private void startCheckingAppStatus(final ApplicationProcessDescriptor applicationProcessDescriptor) {
        totalActiveTimeMetric = dtoFactory.createDto(RunnerMetric.class).withDescription("Total active time")
                                          .withName("total_time");

        //checking if it's ALWAYS ON app and we reopen running project
        // in this case we initiate timer with UP_TIME metric
        RunnerMetric runnerMetric = getRunnerMetric(RunnerMetric.UP_TIME);
        long initTime = 0;
        if (runnerMetric != null) {
            String value = runnerMetric.getValue();
            double v = NumberFormat.getDecimalFormat().parse(value);
            initTime = (long)v / 1000;
        }
        totalActiveTimeTimer = new TotalTimeTimer(initTime);
        totalActiveTimeTimer.scheduleRepeating(1000);

        runnerStatusHandler = new SubscriptionHandler<ApplicationProcessDescriptor>(
                dtoUnmarshallerFactory.newWSUnmarshaller(ApplicationProcessDescriptor.class)) {
            @Override
            protected void onMessageReceived(ApplicationProcessDescriptor result) {
                onApplicationStatusUpdated(result);
            }

            @Override
            protected void onErrorReceived(Throwable exception) {
                isAnyAppRunning = false;

                if (exception instanceof ServerException &&
                    ((ServerException)exception).getHTTPStatus() == 500) {
                    ServiceError e = dtoFactory.createDtoFromJson(exception.getMessage(), ServiceError.class);
                    onFail(constant.startApplicationFailed(appContext.getCurrentProject().getProjectDescription().getName()) + ": " +
                           e.getMessage(), null);
                } else {
                    onFail(constant.startApplicationFailed(appContext.getCurrentProject().getProjectDescription().getName()), exception);
                }

                try {
                    messageBus.unsubscribe(STATUS_CHANNEL + applicationProcessDescriptor.getProcessId(), this);
                } catch (WebSocketException e) {
                    Log.error(RunnerController.class, e);
                }
                appContext.getCurrentProject().setProcessDescriptor(null);
                appContext.getCurrentProject().setIsRunningEnabled(true);
            }
        };

        try {
            messageBus.subscribe(STATUS_CHANNEL + applicationProcessDescriptor.getProcessId(), runnerStatusHandler);
        } catch (WebSocketException e) {
            Log.error(RunnerController.class, e);
        }
    }

    private void stopCheckingAppStatus(ApplicationProcessDescriptor applicationProcessDescriptor) {
        try {
            messageBus.unsubscribe(STATUS_CHANNEL + applicationProcessDescriptor.getProcessId(), runnerStatusHandler);
        } catch (WebSocketException e) {
            Log.error(RunnerController.class, e);
        }
    }

    private void startCheckingAppOutput(ApplicationProcessDescriptor applicationProcessDescriptor) {
        runnerOutputHandler = new LogMessagesHandler(applicationProcessDescriptor, console, messageBus);
        try {
            messageBus.subscribe(OUTPUT_CHANNEL + applicationProcessDescriptor.getProcessId(), runnerOutputHandler);
        } catch (WebSocketException e) {
            Log.error(RunnerController.class, e);
        }
    }

    private void stopCheckingAppOutput(ApplicationProcessDescriptor applicationProcessDescriptor) {
        runnerOutputHandler.stop();
        try {
            messageBus.unsubscribe(OUTPUT_CHANNEL + applicationProcessDescriptor.getProcessId(), runnerOutputHandler);
        } catch (WebSocketException e) {
            Log.error(RunnerController.class, e);
        }
    }

    private void startCheckingAppHealth(final ApplicationProcessDescriptor applicationProcessDescriptor) {
        isLastAppHealthOk = false;

        setAppHealthOkTimer = new Timer() {
            @Override
            public void run() {
                isLastAppHealthOk = true;
            }
        };
        setAppHealthOkTimer.schedule(30 * 1000);

        runnerHealthHandler = new SubscriptionHandler<String>(new StringUnmarshallerWS()) {
            @Override
            protected void onMessageReceived(String result) {
                JSONObject jsonObject = JSONParser.parseStrict(result).isObject();
                if (jsonObject != null && jsonObject.containsKey("url") && jsonObject.containsKey("status")) {
                    final String urlStatus = jsonObject.get("status").isString().stringValue();
                    if (urlStatus.equals("OK")) {
                        isLastAppHealthOk = true;
                        console.onAppStarted(applicationProcessDescriptor);
                        stopCheckingAppHealth(applicationProcessDescriptor);
                    }
                }
            }

            @Override
            protected void onErrorReceived(Throwable exception) {
                Log.error(RunnerController.class, exception);
            }
        };

        try {
            messageBus.subscribe(APP_HEALTH_CHANNEL + applicationProcessDescriptor.getProcessId(), runnerHealthHandler);
        } catch (WebSocketException e) {
            Log.error(RunnerController.class, e);
        }
    }

    private void stopCheckingAppHealth(ApplicationProcessDescriptor applicationProcessDescriptor) {
        setAppHealthOkTimer.cancel();
        try {
            messageBus.unsubscribe(APP_HEALTH_CHANNEL + applicationProcessDescriptor.getProcessId(), runnerHealthHandler);
        } catch (WebSocketException e) {
            Log.error(RunnerController.class, e);
        }
    }

    private void onAppLaunched(ApplicationProcessDescriptor applicationProcessDescriptor) {
        isAnyAppRunning = true;
        appContext.getCurrentProject().setProcessDescriptor(applicationProcessDescriptor);
        appContext.getCurrentProject().setIsRunningEnabled(false);
        startCheckingAppStatus(applicationProcessDescriptor);
        startCheckingAppOutput(applicationProcessDescriptor);
    }

    /** Process changing application status. */
    private void onApplicationStatusUpdated(ApplicationProcessDescriptor descriptor) {
        appContext.getCurrentProject().setProcessDescriptor(descriptor);
        String projectName = appContext.getCurrentProject().getProjectDescription().getName();
        switch (descriptor.getStatus()) {
            case RUNNING:
                startCheckingAppHealth(descriptor);
                if (notification == null)
                    notification = new Notification(constant.applicationStarted(projectName), INFO);
                notification.setStatus(FINISHED);
                notification.setMessage(constant.applicationStarted(projectName));
                console.print("[INFO] " + notification.getMessage());
                if (runCallback != null) {
                    runCallback.onRun(descriptor, appContext.getCurrentProject().getProjectDescription());
                }
                startCheckingAppHealth(descriptor);
                console.onShellStarted(descriptor);
                break;
            case STOPPED:
                totalActiveTimeTimer.cancel();
                isAnyAppRunning = false;
                isLastAppHealthOk = false;
                appContext.getCurrentProject().setIsRunningEnabled(true);
                stopCheckingAppStatus(descriptor);
                stopCheckingAppOutput(descriptor);

                if (notification == null)
                    notification = new Notification(constant.applicationStopped(descriptor.getProject()), INFO);

                // this mean that application has failed to start
                if (descriptor.getStartTime() == -1) {
                    notification.setType(ERROR);
                    getLogs(false);
                }
                notification.setStatus(FINISHED);
                notification.setMessage(constant.applicationStopped(projectName));

                console.print("[INFO] " + notification.getMessage());

                console.onAppStopped();
                break;
            case FAILED:
                totalActiveTimeTimer.cancel();
                isAnyAppRunning = false;
                appContext.getCurrentProject().setIsRunningEnabled(true);
                stopCheckingAppStatus(descriptor);
                stopCheckingAppOutput(descriptor);
                isLastAppHealthOk = false;
                getLogs(false);

                if (notification == null)
                    notification = new Notification(constant.applicationFailed(projectName), ERROR);
                notification.setStatus(FINISHED);
                notification.setMessage(constant.applicationFailed(projectName));
                console.print("[INFO] " + notification.getMessage());

                console.onAppStopped();
                break;
            case CANCELLED:
                totalActiveTimeTimer.cancel();
                isAnyAppRunning = false;
                isLastAppHealthOk = false;
                appContext.getCurrentProject().setIsRunningEnabled(true);
                stopCheckingAppStatus(descriptor);
                stopCheckingAppOutput(descriptor);

                if (notification == null)
                    notification = new Notification(constant.applicationCanceled(projectName), WARNING);
                notification.setStatus(FINISHED);
                notification.setMessage(constant.applicationCanceled(projectName));
                console.print("[INFO] " + notification.getMessage());

                console.onAppStopped();
                break;
        }
    }

    /** Get logs of the currently launched application. */
    public void getLogs(boolean isUserAction) {
        final Link viewLogsLink = RunnerUtils.getLink(appContext.getCurrentProject().getProcessDescriptor(), Constants.LINK_REL_VIEW_LOG);
        if (viewLogsLink == null) {
            onFail(constant.getApplicationLogsFailed(), null);
        }

        if (isUserAction) {
            console.setActive();
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
    public void stopActiveProject(boolean isUserAction) {
        final Link stopLink = RunnerUtils.getLink(appContext.getCurrentProject().getProcessDescriptor(), Constants.LINK_REL_STOP);
        if (stopLink == null) {
            onFail(constant.stopApplicationFailed(appContext.getCurrentProject().getProjectDescription().getName()), null);
            return;
        }

        if (isUserAction) {
            console.setActive();
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
                appContext.getCurrentProject().setIsRunningEnabled(true);
                appContext.getCurrentProject().setProcessDescriptor(null);
                onFail(constant.stopApplicationFailed(appContext.getCurrentProject().getProjectDescription().getName()), exception);
            }
        });
    }

    /** Returns <code>true</code> - if link to get runner recipe file is exist and <code>false</code> - otherwise. */
    public boolean isRecipeLinkExists() {
        if (isAnyAppRunning() && appContext.getCurrentProject() != null && appContext.getCurrentProject().getProcessDescriptor() != null) {
            Link recipeLink = RunnerUtils.getLink(appContext.getCurrentProject().getProcessDescriptor(), Constants.LINK_REL_RUNNER_RECIPE);
            return recipeLink != null;
        }
        return false;
    }

    /** Opens runner recipe file in editor. */
    public void showRecipe() {
        if (appContext.getCurrentProject() != null && appContext.getCurrentProject().getProcessDescriptor() != null) {
            final Link recipeLink = RunnerUtils.getLink(appContext.getCurrentProject().getProcessDescriptor(), "runner recipe");
            if (recipeLink != null) {
                List<Link> links = new ArrayList<>(1);
                links.add(dtoFactory.createDto(Link.class).withHref(recipeLink.getHref())
                                    .withRel("get content"));
                ItemReference recipeFile = dtoFactory.createDto(ItemReference.class)
                                                     .withName("Runner Recipe")
                                                     .withPath("runner_recipe")
                                                     .withMediaType("text/plain")
                                                     .withLinks(links);
                FileNode recipeFileNode = new RecipeFile(null, recipeFile, eventBus, projectServiceClient);
                editorAgent.openEditor(recipeFileNode);
                EditorPartPresenter editor = editorAgent.getOpenedEditors().get(recipeFile.getPath());
                if (editor instanceof CodenvyTextEditor) {
                    ((CodenvyTextEditor)editor).getView().setReadOnly(true);
                }
            }
        }
    }

    /** Returns URL of the application which is currently running. */
    @Nullable
    public String getCurrentAppURL() {
        ApplicationProcessDescriptor processDescriptor = appContext.getCurrentProject().getProcessDescriptor();
        // Don't show app URL in console when app is stopped. After some time this URL may be used by other app.
        if (processDescriptor != null && processDescriptor.getStatus().equals(RUNNING) && isLastAppHealthOk) {
            return getAppLink();
        }
        return null;
    }

    /** Returns startTime {@link RunnerMetric}. */
    @Nullable
    public RunnerMetric getCurrentAppStartTime() {
        ApplicationProcessDescriptor processDescriptor = appContext.getCurrentProject().getProcessDescriptor();
        if (processDescriptor != null && processDescriptor.getCreationTime() >= 0) {
            Date startDate = new Date(processDescriptor.getCreationTime());
            String startDateFormatted = DateTimeFormat.getFormat("dd/MM/yyyy HH:mm:ss").format(startDate);
            return dtoFactory.createDto(RunnerMetric.class).withDescription("Process started at")
                             .withValue(startDateFormatted);
        }
        return null;
    }

    /** Returns waitingTimeLimit {@link RunnerMetric}. */
    @Nullable
    public RunnerMetric getCurrentAppTerminationTime() {
        ApplicationProcessDescriptor processDescriptor = appContext.getCurrentProject().getProcessDescriptor();
        if (processDescriptor == null) {
            return null;
        }
        RunnerMetric terminationMetric = getRunnerMetric(RunnerMetric.TERMINATION_TIME);
        if (terminationMetric != null) {
            if (RunnerMetric.ALWAYS_ON.equals(terminationMetric.getValue()))
                return dtoFactory.createDto(RunnerMetric.class).withDescription(terminationMetric.getDescription())
                                 .withValue(terminationMetric.getValue());
            // if app is running now, count uptime on the client-side
            if (terminationMetric.getValue() != null) {
                double terminationTime = NumberFormat.getDecimalFormat().parse(terminationMetric.getValue());
                final double terminationTimeout = terminationTime - System.currentTimeMillis();
                if (terminationTimeout <= 0) {
                    return null;
                }
                final String value = StringUtils.timeMlsToHumanReadable((long)terminationTimeout);
                return dtoFactory.createDto(RunnerMetric.class).withDescription(terminationMetric.getDescription())
                                 .withValue(value);
            }
        }
        RunnerMetric lifeTimeMetric = getRunnerMetric(RunnerMetric.LIFETIME);
        if (lifeTimeMetric != null && processDescriptor.getStatus().equals(NEW)) {
            if (RunnerMetric.ALWAYS_ON.equals(lifeTimeMetric.getValue()))
                return dtoFactory.createDto(RunnerMetric.class).withDescription(lifeTimeMetric.getDescription())
                                 .withValue(lifeTimeMetric.getValue());
            if (lifeTimeMetric.getValue() != null) {
                double lifeTime = NumberFormat.getDecimalFormat().parse(lifeTimeMetric.getValue());
                final String value = StringUtils.timeMlsToHumanReadable((long)lifeTime);
                return dtoFactory.createDto(RunnerMetric.class).withDescription(lifeTimeMetric.getDescription())
                                 .withValue(value + " " + constant.startsAfterLaunch());
            }
        }
        return null;
    }


    /** Returns stopTime {@link RunnerMetric}. */
    @Nullable
    public RunnerMetric getCurrentAppStopTime() {
        RunnerMetric runnerMetric = getRunnerMetric(RunnerMetric.STOP_TIME);
        if (runnerMetric != null && runnerMetric.getValue() != null) {
            double stopTimeMs = NumberFormat.getDecimalFormat().parse(runnerMetric.getValue());
            Date startDate = new Date((long)stopTimeMs);
            String stopDateFormatted = DateTimeFormat.getFormat("dd/MM/yyyy HH:mm:ss").format(startDate);
            return dtoFactory.createDto(RunnerMetric.class).withDescription(runnerMetric.getDescription())
                             .withValue(stopDateFormatted);
        }
        return null;
    }

    /** Returns uptime {@link RunnerMetric}. */
    @Nullable
    public RunnerMetric getTotalTime() {
        if (totalActiveTimeMetric != null && totalActiveTimeMetric.getValue() != null) {
            return totalActiveTimeMetric;
        }
        return null;
    }

    @Nullable
    private RunnerMetric getRunnerMetric(String metricName) {
        if (appContext.getCurrentProject() != null) {
            ApplicationProcessDescriptor processDescriptor = appContext.getCurrentProject().getProcessDescriptor();
            if (processDescriptor != null) {
                for (RunnerMetric runnerStat : processDescriptor.getRunStats()) {
                    if (metricName.equals(runnerStat.getName())) {
                        return runnerStat;
                    }
                }
            }
        }
        return null;
    }

    private String getAppLink() {
        String url = null;
        final Link appLink = RunnerUtils.getLink(appContext.getCurrentProject().getProcessDescriptor(),
                                                 com.codenvy.api.runner.internal.Constants.LINK_REL_WEB_URL);
        if (appLink != null) {
            url = appLink.getHref();

            final Link codeServerLink = RunnerUtils.getLink(appContext.getCurrentProject().getProcessDescriptor(), "code server");
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

    private static class RecipeFile extends FileNode {
        public RecipeFile(AbstractTreeNode parent, ItemReference data, EventBus eventBus, ProjectServiceClient projectServiceClient) {
            super(parent, data, eventBus, projectServiceClient);
        }

        @Override
        public void getContent(final AsyncCallback<String> callback) {
            for (Link link : data.getLinks()) {
                if ("get content".equals(link.getRel())) {
                    try {
                        new RequestBuilder(RequestBuilder.GET, link.getHref()).sendRequest("", new RequestCallback() {
                            @Override
                            public void onResponseReceived(Request request, Response response) {
                                callback.onSuccess(response.getText());
                            }

                            @Override
                            public void onError(Request request, Throwable exception) {
                                Log.error(RecipeFile.class, exception);
                            }
                        });
                    } catch (RequestException e) {
                        Log.error(RecipeFile.class, e);
                    }
                    break;
                }
            }
        }
    }

    private class TotalTimeTimer extends Timer {
        long timeSeconds;

        private TotalTimeTimer(long initTime) {
            timeSeconds = initTime;
        }

        @Override
        public void run() {
            timeSeconds++;
            String humanReadable = StringUtils.timeSecToHumanReadable(timeSeconds);
            totalActiveTimeMetric.setValue(humanReadable);
        }
    }

}
