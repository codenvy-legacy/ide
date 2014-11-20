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
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.api.project.shared.dto.RunnerConfiguration;
import com.codenvy.api.project.shared.dto.RunnersDescriptor;
import com.codenvy.api.runner.dto.ApplicationProcessDescriptor;
import com.codenvy.api.runner.dto.ResourcesDescriptor;
import com.codenvy.api.runner.dto.RunOptions;
import com.codenvy.api.runner.dto.RunnerMetric;
import com.codenvy.api.runner.gwt.client.RunnerServiceClient;
import com.codenvy.api.runner.gwt.client.utils.RunnerUtils;
import com.codenvy.api.runner.internal.Constants;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.app.CurrentProject;
import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.event.ProjectActionEvent;
import com.codenvy.ide.api.event.ProjectActionHandler;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.parts.WorkspaceAgent;
import com.codenvy.ide.api.preferences.PreferencesManager;
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
import com.codenvy.ide.ui.dialogs.CancelCallback;
import com.codenvy.ide.ui.dialogs.ConfirmCallback;
import com.codenvy.ide.ui.dialogs.DialogFactory;
import com.codenvy.ide.ui.dialogs.confirm.ConfirmDialog;
import com.codenvy.ide.ui.dialogs.message.MessageDialog;
import com.codenvy.ide.util.StringUtils;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.websocket.MessageBus;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.StringUnmarshallerWS;
import com.codenvy.ide.websocket.rest.SubscriptionHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;

import javax.annotation.Nullable;
import java.util.Date;

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
public class RunController implements Notification.OpenNotificationHandler, ProjectActionHandler {

    /** WebSocket channel to get application's status. */
    public static final String STATUS_CHANNEL          = "runner:status:";
    public static final String PROCESS_STARTED_CHANNEL = "runner:process_started:";
    /** WebSocket channel to get runner output. */
    public static final String OUTPUT_CHANNEL          = "runner:output:";
    /** WebSocket channel to check application's health. */
    public static final String APP_HEALTH_CHANNEL      = "runner:app_health:";
    private final DtoUnmarshallerFactory dtoUnmarshallerFactory;
    private final DtoFactory             dtoFactory;
    private final AppContext             appContext;
    private final DialogFactory          dialogFactory;
    private final String                 workspaceId;
    /** Whether any app is running now? */
    protected boolean isAnyAppRunning  = false;
    protected boolean isAnyAppLaunched = false;
    protected LogMessagesHandler                                runnerOutputHandler;
    protected SubscriptionHandler<ApplicationProcessDescriptor> runnerStatusHandler;
    protected SubscriptionHandler<String>                       runnerHealthHandler;
    protected SubscriptionHandler<ApplicationProcessDescriptor> processStartedHandler;
    private   EditorAgent                                       editorAgent;
    private   MessageBus                                        messageBus;
    private   WorkspaceAgent                                    workspaceAgent;
    private   RunnerConsolePresenter                            console;
    private   RunnerServiceClient                               service;
    private   RunnerLocalizationConstant                        constant;
    private   NotificationManager                               notificationManager;
    private   PreferencesManager                                preferencesManager;
    private   Notification                                      mainNotification;
    private   ProjectRunCallback                                runCallback;
    private   boolean                                           isLastAppHealthOk;
    // The server makes the limited quantity of tries checking application's health,
    // so we're waiting for some time (about 30 sec.) and assume that app health is OK.
    private   Timer                                             setAppHealthOkTimer;
    // show time in "Total Time" indicator start immediately after launch running process
    private long clientStartTime = 0;
    // calculate on client-side
    private RunnerMetric                 totalActiveTimeMetric;
    private String                       theme;
    private int                          runnerMemory;
    private RunnerMetric                 stopTimeMetric;
    private ApplicationProcessDescriptor currentAppProcess;

    @Inject
    public RunController(EventBus eventBus,
                         WorkspaceAgent workspaceAgent,
                         final RunnerConsolePresenter console,
                         final RunnerServiceClient service,
                         final RunnerLocalizationConstant constant,
                         final NotificationManager notificationManager,
                         PreferencesManager preferencesManager,
                         DtoFactory dtoFactory,
                         EditorAgent editorAgent,
                         final DtoUnmarshallerFactory dtoUnmarshallerFactory,
                         MessageBus messageBus,
                         ThemeAgent themeAgent,
                         final AppContext appContext,
                         DialogFactory dialogFactory,
                         @Named("workspaceId") String workspaceId) {
        this.workspaceAgent = workspaceAgent;
        this.console = console;
        this.service = service;
        this.constant = constant;
        this.notificationManager = notificationManager;
        this.preferencesManager = preferencesManager;
        this.dtoFactory = dtoFactory;
        this.editorAgent = editorAgent;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.messageBus = messageBus;
        this.appContext = appContext;
        this.dialogFactory = dialogFactory;
        this.workspaceId = workspaceId;
        theme = themeAgent.getCurrentThemeId();

        eventBus.addHandler(ProjectActionEvent.TYPE, this);
    }

    @Override
    public void onProjectOpened(final ProjectActionEvent event) {
        startCheckingNewProcesses();

        Unmarshallable<Array<ApplicationProcessDescriptor>> unmarshaller =
                dtoUnmarshallerFactory.newArrayUnmarshaller(ApplicationProcessDescriptor.class);
        service.getRunningProcesses(
                event.getProject().getPath(),
                new AsyncRequestCallback<Array<ApplicationProcessDescriptor>>(unmarshaller) {
                    @Override
                    protected void onSuccess(Array<ApplicationProcessDescriptor> result) {
                        for (ApplicationProcessDescriptor processDescriptor : result.asIterable()) {
                            if (processDescriptor.getStatus() == NEW || processDescriptor.getStatus() == RUNNING) {
                                isLastAppHealthOk = true; //set true here because we don't get information
                                isAnyAppRunning = true;   // about app health in case we open already run app
                                isAnyAppLaunched = true;
                                console.setCurrentRunnerStatus(RunnerStatus.RUNNING);
                                onAppLaunched(processDescriptor);
                                getLogs(false);
                                console.onAppStarted(appContext.getCurrentProject().getProcessDescriptor());
                                Notification notification =
                                        new Notification(constant.projectRunningNow(event.getProject().getName()), INFO, true);
                                notificationManager.showNotification(notification);
                                break;
                            }
                        }
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        Log.error(RunController.class, exception);
                    }
                });
    }

    @Override
    public void onProjectClosed(ProjectActionEvent event) {
        console.clear();
        console.setCurrentRunnerStatus(RunnerStatus.IDLE);

        if (isAnyAppLaunched) {
            isAnyAppLaunched = false;
            isAnyAppRunning = false;
            isLastAppHealthOk = false;
            stopCheckingAppStatus(currentAppProcess);
            stopCheckingAppHealth(currentAppProcess);
            stopCheckingAppOutput(currentAppProcess);
            stopCheckingNewProcesses();
            console.onAppStopped();
        }
    }

    @Override
    public void onOpenClicked() {
        workspaceAgent.setActivePart(console);
    }

    /**
     * Determines whether any application is running.
     *
     * @return <code>true</code> if any application has been launched, and <code>false</code> otherwise
     */
    public boolean isAnyAppLaunched() {
        return isAnyAppLaunched;
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
     * @param callback
     *         callback that will be notified when project will be run
     * @param isUserAction
     *         points whether the build is started directly by user interaction
     */
    public void runActiveProject(final RunOptions runOptions, final ProjectRunCallback callback, final boolean isUserAction) {
        if (isAnyAppLaunched) {
            mainNotification = new Notification(constant.anotherProjectRunningNow(), ERROR);
            notificationManager.showNotification(mainNotification);
            return;
        }
        if (appContext.getCurrentProject() == null) {
            return;
        }

        runCallback = callback;

        // Save the files before running if necessary
        Array<EditorPartPresenter> dirtyEditors = editorAgent.getDirtyEditors();
        if (dirtyEditors.isEmpty()) {
            isAnyAppLaunched = true;
            checkRamAndRunProject(runOptions, isUserAction);
        } else {
            dialogFactory.createConfirmDialog(constant.titlePromptSaveFiles(), constant.messagePromptSaveFiles(), new ConfirmCallback() {
                @Override
                public void accepted() {
                    editorAgent.saveAll(new AsyncCallback() {
                        @Override
                        public void onFailure(Throwable caught) {
                            mainNotification = new Notification(constant.messageFailedSaveFiles(), ERROR);
                            notificationManager.showNotification(mainNotification);
                        }

                        @Override
                        public void onSuccess(Object result) {
                            isAnyAppLaunched = true;
                            checkRamAndRunProject(runOptions, isUserAction);
                        }
                    });

                }
            }, new CancelCallback() {
                @Override
                public void cancelled() {
                    checkRamAndRunProject(runOptions, isUserAction);
                }
            }).show();
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
    private void checkRamAndRunProject(final RunOptions runOptions, final boolean isUserAction) {
        if (appContext.getCurrentProject() == null)
            return;
        service.getResources(
                new AsyncRequestCallback<ResourcesDescriptor>(dtoUnmarshallerFactory.newUnmarshaller(ResourcesDescriptor.class)) {
                    @Override
                    protected void onSuccess(ResourcesDescriptor resourcesDescriptor) {
                        runnerMemory = 0;
                        int overrideMemory = 0;
                        int requiredMemory = 0;
                        int totalMemory = Integer.valueOf(resourcesDescriptor.getTotalMemory());
                        int usedMemory = Integer.valueOf(resourcesDescriptor.getUsedMemory());
                        int availableMemory = totalMemory - usedMemory;

                        ProjectDescriptor projectDescriptor = appContext.getCurrentProject().getProjectDescription();
                        RunnersDescriptor runners = projectDescriptor.getRunners();
                        RunnerConfiguration runnerConfiguration = null;
                        if (runOptions != null) {
                            if (runners != null) {
                                runnerConfiguration = runners.getConfigs().get(runOptions.getEnvironmentId());
                                if (runnerConfiguration == null) {
                                    runnerConfiguration = runners.getConfigs().get(runners.getDefault());
                                }
                            }
                            overrideMemory = runOptions.getMemorySize();
                        } else {
                            if (runners != null) {
                                runnerConfiguration = runners.getConfigs().get(runners.getDefault());
                            }
                            if (preferencesManager != null &&
                                preferencesManager.getValue(RunnerExtension.PREFS_RUNNER_RAM_SIZE_DEFAULT) != null) {
                                try {
                                    overrideMemory =
                                            Integer.parseInt(preferencesManager.getValue(RunnerExtension.PREFS_RUNNER_RAM_SIZE_DEFAULT));
                                } catch (NumberFormatException e) {
                                    //do nothing
                                }
                            }
                        }
                        if (runnerConfiguration != null) {
                            requiredMemory = runnerConfiguration.getRam();
                        }

                        if (!isSufficientMemory(totalMemory, usedMemory, requiredMemory)) {
                            isAnyAppLaunched = false;
                            return;
                        }

                        if (overrideMemory > 0) {
                            if (!isOverrideMemoryCorrect(totalMemory, usedMemory, overrideMemory)) {
                                isAnyAppLaunched = false;
                                return;
                            }
                            if (overrideMemory < requiredMemory) {
                        /* Offer the user to run an application with requiredMemory
                        * If the user selects OK, then runnerMemory = requiredMemory
                        * Else we should terminate the Runner process.
                        */
                                final int finalRequiredMemory = requiredMemory;
                                final ConfirmDialog confirmDialog = dialogFactory.createConfirmDialog(
                                        constant.titlesWarning(),
                                        constant.messagesOverrideMemory(), new ConfirmCallback() {
                                            @Override
                                            public void accepted() {
                                                runnerMemory = finalRequiredMemory;
                                                runProject(runOptions, isUserAction);
                                            }
                                        }, null);

                                final MessageDialog messageDialog = dialogFactory.createMessageDialog(
                                        constant.titlesWarning(),
                                        constant.messagesOverrideLessRequiredMemory(overrideMemory, requiredMemory), new ConfirmCallback() {
                                            @Override
                                            public void accepted() {
                                                confirmDialog.show();

                                            }
                                        });

                                messageDialog.show();
                                return;
                            }
                            runnerMemory = overrideMemory;
                            runProject(runOptions, isUserAction);
                            return;
                        }

                        if (requiredMemory > 0 && requiredMemory <= totalMemory && requiredMemory <= availableMemory) {
                            runnerMemory = requiredMemory;
                            runProject(runOptions, isUserAction);
                            return;
                        }

                        if (requiredMemory > 0) {
                            //check for requiredMemory < totalMemory && requiredMemory < availableMemory was in isSufficientRequiredMemory()
                            runnerMemory = requiredMemory;
                            runProject(runOptions, isUserAction);
                            return;
                        }
                /* Do not provide any value runnerMemorySize if:
                * - overrideMemory <= 0 &&
                * - requiredMemory <=0
                * or the resulting value > workspaceMemory or the resulting value > availableMemory
                */
                        runProject(runOptions, isUserAction);
                    }

                    @Override
                    protected void onFailure(Throwable throwable) {
                        onFail(constant.getResourcesFailed(), throwable);
                    }
                });
    }

    /**
     * Run project.
     *
     * @param runOptions
     *         options to configure run process
     * @param isUserAction
     *         points whether the build is started directly by user interaction
     */
    private void runProject(RunOptions runOptions, final boolean isUserAction) {
        final CurrentProject currentProject = appContext.getCurrentProject();
        if (currentProject == null) {
            return;
        }
        mainNotification = new Notification(constant.launchingRunner(currentProject.getProjectDescription().getName()),
                                            PROGRESS,
                                            true,
                                            RunController.this);
        notificationManager.showNotification(mainNotification);
        console.setCurrentRunnerStatus(RunnerStatus.IN_PROGRESS);
        console.print("[INFO] " + mainNotification.getMessage());

        if (isUserAction) {
            console.setActive();
        }
        if (runOptions == null) {
            runOptions = dtoFactory.createDto(RunOptions.class);
            runOptions.setSkipBuild(Boolean.parseBoolean(currentProject.getAttributeValue("runner:skipBuild")));
        }
        if (runnerMemory > 0) {
            runOptions.setMemorySize(runnerMemory);
        }

        if (runOptions.getEnvironmentId() == null && currentProject.getRunnerEnvId() != null) {
            runOptions.setEnvironmentId(currentProject.getRunnerEnvId());
        }
        runOptions.getShellOptions().put("WebShellTheme", theme);

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

    private boolean isSufficientMemory(int totalMemory, int usedMemory, final int requiredMemory) {
        int availableMemory = totalMemory - usedMemory;
        if (totalMemory < requiredMemory) {
            showWarning(constant.messagesTotalLessRequiredMemory(totalMemory, requiredMemory));
            return false;
        }
        if (availableMemory < requiredMemory) {
            showWarning(constant.messagesAvailableLessRequiredMemory(totalMemory, usedMemory, requiredMemory));
            return false;
        }
        return true;
    }

    private boolean isDefaultMemoryCorrect(int totalMemory, int usedMemory, final int defaultMemory) {
        int availableMemory = totalMemory - usedMemory;
        if (totalMemory < defaultMemory) {
            showWarning(constant.messagesTotalLessDefaultMemory(defaultMemory, totalMemory));
            return false;
        }
        if (availableMemory < defaultMemory) {
            showWarning(constant.messagesAvailableLessDefaultMemory(defaultMemory, totalMemory, usedMemory));
            return false;
        }
        return true;
    }

    private boolean isOverrideMemoryCorrect(int totalMemory, int usedMemory, final int overrideMemory) {
        int availableMemory = totalMemory - usedMemory;
        if (totalMemory < overrideMemory) {
            showWarning(constant.messagesTotalLessOverrideMemory(overrideMemory, totalMemory));
            return false;
        }
        if (availableMemory < overrideMemory) {
            showWarning(constant.messagesAvailableLessOverrideMemory(overrideMemory, totalMemory, usedMemory));
            return false;
        }
        return true;
    }

    private void startCheckingAppStatus(final ApplicationProcessDescriptor applicationProcessDescriptor) {
        clientStartTime = System.currentTimeMillis();
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

        runnerStatusHandler = new SubscriptionHandler<ApplicationProcessDescriptor>(
                dtoUnmarshallerFactory.newWSUnmarshaller(ApplicationProcessDescriptor.class)) {
            @Override
            protected void onMessageReceived(ApplicationProcessDescriptor result) {
                onApplicationStatusUpdated(result);
            }

            @Override
            protected void onErrorReceived(Throwable exception) {
                isAnyAppRunning = false;
                if (appContext.getCurrentProject() == null) {
                    Log.error(RunController.class, exception);
                    return;
                }

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
                    Log.error(RunController.class, e);
                }
                appContext.getCurrentProject().setProcessDescriptor(null);
                appContext.getCurrentProject().setIsRunningEnabled(true);
            }
        };

        try {
            messageBus.subscribe(STATUS_CHANNEL + applicationProcessDescriptor.getProcessId(), runnerStatusHandler);
        } catch (WebSocketException e) {
            Log.error(RunController.class, e);
        }
    }

    private void stopCheckingAppStatus(ApplicationProcessDescriptor applicationProcessDescriptor) {
        try {
            messageBus.unsubscribe(STATUS_CHANNEL + applicationProcessDescriptor.getProcessId(), runnerStatusHandler);
        } catch (WebSocketException e) {
            Log.error(RunController.class, e);
        }
    }

    private void startCheckingAppOutput(ApplicationProcessDescriptor applicationProcessDescriptor) {
        runnerOutputHandler = new LogMessagesHandler(applicationProcessDescriptor, console, messageBus);
        try {
            messageBus.subscribe(OUTPUT_CHANNEL + applicationProcessDescriptor.getProcessId(), runnerOutputHandler);
        } catch (WebSocketException e) {
            Log.error(RunController.class, e);
        }
    }

    private void stopCheckingAppOutput(ApplicationProcessDescriptor applicationProcessDescriptor) {
        runnerOutputHandler.stop();
        try {
            messageBus.unsubscribe(OUTPUT_CHANNEL + applicationProcessDescriptor.getProcessId(), runnerOutputHandler);
        } catch (WebSocketException e) {
            Log.error(RunController.class, e);
        }
    }

    private void startCheckingNewProcesses() {
        final ProjectDescriptor project = appContext.getCurrentProject().getProjectDescription();
        com.codenvy.ide.websocket.rest.Unmarshallable<ApplicationProcessDescriptor> unmarshaller =
                dtoUnmarshallerFactory.newWSUnmarshaller(ApplicationProcessDescriptor.class);
        processStartedHandler = new SubscriptionHandler<ApplicationProcessDescriptor>(unmarshaller) {
            @Override
            protected void onMessageReceived(ApplicationProcessDescriptor processDescriptor) {
                if (!isAnyAppLaunched() && (processDescriptor.getStatus() == NEW || processDescriptor.getStatus() == RUNNING)) {
                    isLastAppHealthOk = true; // set true here because we don't get information
                    isAnyAppRunning = true;   // about app health in case we open already run app
                    isAnyAppLaunched = true;
                    console.setCurrentRunnerStatus(RunnerStatus.RUNNING);
                    onAppLaunched(processDescriptor);
                    getLogs(false);
                    console.onAppStarted(appContext.getCurrentProject().getProcessDescriptor());
                    Notification notification = new Notification(constant.projectRunningNow(project.getName()), INFO, true);
                    notificationManager.showNotification(notification);
                }
            }

            @Override
            protected void onErrorReceived(Throwable exception) {
                Log.error(RunController.class, exception);
            }
        };
        String channel = PROCESS_STARTED_CHANNEL + workspaceId + ':' + project.getPath();
        try {
            messageBus.subscribe(channel, processStartedHandler);
        } catch (WebSocketException e) {
            Log.error(RunController.class, e);
        }
    }

    private void stopCheckingNewProcesses() {
        String channel = PROCESS_STARTED_CHANNEL + workspaceId + ':' + appContext.getCurrentProject().getProjectDescription().getPath();
        try {
            messageBus.unsubscribe(channel, processStartedHandler);
        } catch (WebSocketException e) {
            Log.error(RunController.class, e);
        }
    }

    private void startCheckingAppHealth(final ApplicationProcessDescriptor applicationProcessDescriptor) {
        if (RunnerUtils.getLink(applicationProcessDescriptor, Constants.LINK_REL_WEB_URL) == null) {
            return;
        }

        setAppHealthOkTimer = new Timer() {
            @Override
            public void run() {
                isLastAppHealthOk = true;

                String projectName = appContext.getCurrentProject().getProjectDescription().getName();

                String notificationMessage = constant.applicationMaybeStarted(projectName);
                Notification.Type notificationType = WARNING;
                Notification.Status notificationStatus = FINISHED;
                if (mainNotification == null) {
                    mainNotification = new Notification(notificationMessage, notificationType, notificationStatus);
                    notificationManager.showNotification(mainNotification);
                } else {
                    mainNotification.update(notificationMessage, notificationType, notificationStatus, null, true);
                }

                console.setCurrentRunnerStatus(RunnerStatus.RUNNING);
                console.print("[WARNING] " + notificationMessage);

                console.onAppStarted(applicationProcessDescriptor);
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
                        setAppHealthOkTimer.cancel();

                        isLastAppHealthOk = true;

                        String projectName = appContext.getCurrentProject().getProjectDescription().getName();

                        String notificationMessage = constant.applicationStarted(projectName);
                        Notification.Type notificationType = INFO;
                        Notification.Status notificationStatus = FINISHED;
                        if (mainNotification == null) {
                            mainNotification = new Notification(notificationMessage, notificationType, notificationStatus);
                            notificationManager.showNotification(mainNotification);
                        } else {
                            mainNotification.update(notificationMessage, notificationType, notificationStatus, null, true);
                        }

                        console.setCurrentRunnerStatus(RunnerStatus.RUNNING);
                        console.print("[INFO] " + notificationMessage);

                        console.onAppStarted(applicationProcessDescriptor);
                        stopCheckingAppHealth(applicationProcessDescriptor);
                    }
                }
            }

            @Override
            protected void onErrorReceived(Throwable exception) {
                Log.error(RunController.class, exception);
            }
        };

        try {
            messageBus.subscribe(APP_HEALTH_CHANNEL + applicationProcessDescriptor.getProcessId(), runnerHealthHandler);
        } catch (WebSocketException e) {
            Log.error(RunController.class, e);
        }
    }

    private void stopCheckingAppHealth(ApplicationProcessDescriptor applicationProcessDescriptor) {
        if (setAppHealthOkTimer != null) {
            setAppHealthOkTimer.cancel();
        }
        try {
            if (applicationProcessDescriptor != null) {
                String channel = APP_HEALTH_CHANNEL + applicationProcessDescriptor.getProcessId();
                if (messageBus.isHandlerSubscribed(runnerHealthHandler, channel)) {
                    messageBus.unsubscribe(channel, runnerHealthHandler);
                }
            }
        } catch (WebSocketException e) {
            Log.error(RunController.class, e);
        }
    }

    private void onAppLaunched(ApplicationProcessDescriptor applicationProcessDescriptor) {
        stopTimeMetric = null;
        if (appContext.getCurrentProject() == null) {
            return; //MUST never happen
        }
        currentAppProcess = applicationProcessDescriptor;
        String projectName = appContext.getCurrentProject().getProjectDescription().getName();

        String notificationMessage = constant.environmentCooking(projectName);
        Notification.Type notificationType = INFO;
        Notification.Status notificationStatus = PROGRESS;
        if (mainNotification == null) {
            mainNotification = new Notification(notificationMessage, notificationType, notificationStatus);
            notificationManager.showNotification(mainNotification);
        } else {
            mainNotification.update(notificationMessage, notificationType, notificationStatus, null, true);
        }


        console.print("[INFO] " + notificationMessage);
        console.onShellStarted(applicationProcessDescriptor);

        appContext.getCurrentProject().setProcessDescriptor(applicationProcessDescriptor);
        appContext.getCurrentProject().setIsRunningEnabled(false);
        startCheckingAppStatus(applicationProcessDescriptor);
        startCheckingAppOutput(applicationProcessDescriptor);
    }

    /** Process changing application status. */
    private void onApplicationStatusUpdated(ApplicationProcessDescriptor descriptor) {
        String projectName = appContext.getCurrentProject().getProjectDescription().getName();
        appContext.getCurrentProject().setProcessDescriptor(descriptor);

        String notificationMessage;
        Notification.Type notificationType;
        Notification.Status notificationStatus;
        switch (descriptor.getStatus()) {
            case RUNNING:
                console.setCurrentRunnerStatus(RunnerStatus.RUNNING);
                isAnyAppRunning = true;

                startCheckingAppHealth(descriptor);

                notificationMessage = constant.applicationStarting(projectName);
                notificationType = INFO;
                notificationStatus = FINISHED;
                if (mainNotification == null) {
                    mainNotification = new Notification(notificationMessage, notificationType, notificationStatus);
                    notificationManager.showNotification(mainNotification);
                } else {
                    mainNotification.update(notificationMessage, notificationType, notificationStatus, null, true);
                }

                console.setCurrentRunnerStatus(RunnerStatus.RUNNING);
                console.print("[INFO] " + notificationMessage);

                if (runCallback != null) {
                    runCallback.onRun(descriptor, appContext.getCurrentProject().getProjectDescription());
                }
                console.onShellStarted(descriptor);
                break;
            case STOPPED:
                stopTimeMetric = getRunnerMetric(RunnerMetric.STOP_TIME);
                isAnyAppLaunched = false;
                isAnyAppRunning = false;
                isLastAppHealthOk = false;
                currentAppProcess = null;
                appContext.getCurrentProject().setIsRunningEnabled(true);
                appContext.getCurrentProject().setProcessDescriptor(null);
                stopCheckingAppStatus(descriptor);
                stopCheckingAppHealth(descriptor);
                stopCheckingAppOutput(descriptor);

                notificationMessage = constant.applicationStopped(projectName);
                notificationStatus = FINISHED;

                // this mean that application has failed to start
                if (descriptor.getStartTime() == -1) {
                    notificationType = ERROR;
                    getLogs(false);
                } else {
                    notificationType = INFO;
                }
                if (mainNotification == null) {
                    mainNotification = new Notification(notificationMessage, notificationType, notificationStatus);
                    notificationManager.showNotification(mainNotification);
                } else {
                    mainNotification.update(notificationMessage, notificationType, notificationStatus, null, true);
                }

                if (descriptor.getStartTime() == -1) {
                    console.setCurrentRunnerStatus(RunnerStatus.FAILED);
                    console.print("[ERROR] " + notificationMessage);
                } else {
                    console.setCurrentRunnerStatus(RunnerStatus.DONE);
                    console.print("[INFO] " + notificationMessage);
                }

                console.onAppStopped();
                break;
            case FAILED:
                isAnyAppLaunched = false;
                isAnyAppRunning = false;
                appContext.getCurrentProject().setIsRunningEnabled(true);
                stopCheckingAppStatus(descriptor);
                stopCheckingAppHealth(descriptor);
                stopCheckingAppOutput(descriptor);
                isLastAppHealthOk = false;
                getLogs(false);

                notificationMessage = constant.applicationFailed(projectName);
                notificationStatus = FINISHED;
                notificationType = ERROR;
                if (mainNotification == null) {
                    mainNotification = new Notification(notificationMessage, notificationType, notificationStatus);
                    notificationManager.showNotification(mainNotification);
                } else {
                    mainNotification.update(notificationMessage, notificationType, notificationStatus, null, true);
                }

                console.setCurrentRunnerStatus(RunnerStatus.FAILED);
                console.print("[ERROR] " + notificationMessage);

                console.onAppStopped();
                break;
            case CANCELLED:
                stopTimeMetric = getRunnerMetric(RunnerMetric.STOP_TIME);
                isAnyAppLaunched = false;
                isAnyAppRunning = false;
                isLastAppHealthOk = false;
                currentAppProcess = null;
                appContext.getCurrentProject().setIsRunningEnabled(true);
                appContext.getCurrentProject().setProcessDescriptor(null);
                stopCheckingAppStatus(descriptor);
                stopCheckingAppHealth(descriptor);
                stopCheckingAppOutput(descriptor);

                notificationMessage = constant.applicationCanceled(projectName);
                notificationStatus = FINISHED;
                notificationType = ERROR;
                if (mainNotification == null) {
                    mainNotification = new Notification(notificationMessage, notificationType, notificationStatus);
                    notificationManager.showNotification(mainNotification);
                } else {
                    mainNotification.update(notificationMessage, notificationType, notificationStatus, null, true);
                }

                console.setCurrentRunnerStatus(RunnerStatus.FAILED);
                console.print("[ERROR] " + notificationMessage);

                console.onAppStopped();
                break;
            case NEW:
                console.setCurrentRunnerStatus(RunnerStatus.IN_PROGRESS);
                break;

        }
    }

    /** Get logs of the currently launched application. */
    public void getLogs(boolean isUserAction) {
        if (appContext.getCurrentProject() != null) {
            final Link viewLogsLink =
                    RunnerUtils.getLink(appContext.getCurrentProject().getProcessDescriptor(), Constants.LINK_REL_VIEW_LOG);
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
    }

    private void onFail(String message, Throwable exception) {
        isAnyAppLaunched = false;

        stopCheckingAppHealth(appContext.getCurrentProject().getProcessDescriptor());

        if (mainNotification != null) {
            mainNotification.setStatus(FINISHED);
            mainNotification.setType(ERROR);
            mainNotification.setMessage(message);
        }
        if (exception != null && exception.getMessage() != null) {
            message += ": " + exception.getMessage();
        }

        console.onAppStopped();
        console.setCurrentRunnerStatus(RunnerStatus.FAILED);
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
                isAnyAppLaunched = false;
                isAnyAppRunning = false;
                currentAppProcess = null;
                appContext.getCurrentProject().setIsRunningEnabled(true);
                appContext.getCurrentProject().setProcessDescriptor(null);
                onFail(constant.stopApplicationFailed(appContext.getCurrentProject().getProjectDescription().getName()), exception);
            }
        });
    }

    /** Returns <code>true</code> - if link to get runner recipe file is exist and <code>false</code> - otherwise. */
    public boolean isRecipeLinkExists() {
        if (isAnyAppLaunched() && appContext.getCurrentProject() != null && appContext.getCurrentProject().getProcessDescriptor() != null) {
            Link recipeLink = RunnerUtils.getLink(appContext.getCurrentProject().getProcessDescriptor(), Constants.LINK_REL_RUNNER_RECIPE);
            return recipeLink != null;
        }
        return false;
    }

    private void showWarning(String warning) {
        dialogFactory.createMessageDialog(constant.titlesWarning(), warning, null).show();
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
        if (stopTimeMetric != null && stopTimeMetric.getValue() != null) {
            double stopTimeMs = NumberFormat.getDecimalFormat().parse(stopTimeMetric.getValue());
            Date startDate = new Date((long)stopTimeMs);
            String stopDateFormatted = DateTimeFormat.getFormat("dd/MM/yyyy HH:mm:ss").format(startDate);
            return dtoFactory.createDto(RunnerMetric.class).withDescription(stopTimeMetric.getDescription())
                             .withValue(stopDateFormatted);
        }
        return null;
    }

    /** Returns uptime {@link RunnerMetric}. */
    @Nullable
    public RunnerMetric getTotalTime() {
        if (clientStartTime == 0) {
            return null;
        }
        if (!isAnyAppLaunched) {
            return totalActiveTimeMetric;
        }
        String humanReadable = StringUtils.timeSecToHumanReadable((System.currentTimeMillis() - clientStartTime) / 1000);
        totalActiveTimeMetric.setValue(humanReadable);
        return totalActiveTimeMetric;
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
}
