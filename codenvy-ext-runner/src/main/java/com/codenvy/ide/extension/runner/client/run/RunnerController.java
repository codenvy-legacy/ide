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
import com.codenvy.ide.api.editor.CodenvyTextEditor;
import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.event.ProjectActionEvent;
import com.codenvy.ide.api.event.ProjectActionHandler;
import com.codenvy.ide.api.event.WindowActionEvent;
import com.codenvy.ide.api.event.WindowActionHandler;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.resources.model.File;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.api.ui.theme.ThemeAgent;
import com.codenvy.ide.api.ui.workspace.WorkspaceAgent;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.commons.exception.ServerException;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.extension.runner.client.ProjectRunCallback;
import com.codenvy.ide.extension.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.extension.runner.client.RunnerUtils;
import com.codenvy.ide.extension.runner.client.console.RunnerConsolePresenter;
import com.codenvy.ide.extension.runner.client.update.UpdateServiceClient;
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
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.codenvy.ide.websocket.rest.StringUnmarshallerWS;
import com.codenvy.ide.websocket.rest.SubscriptionHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
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

//TODO rre-rework this class
// try remove fields like lastApplicationDescriptor;
public class RunnerController implements Notification.OpenNotificationHandler {

    /** WebSocket channel to get application's status. */
    public static final String STATUS_CHANNEL     = "runner:status:";
    /** WebSocket channel to get runner output. */
    public static final String OUTPUT_CHANNEL     = "runner:output:";
    /** WebSocket channel to check application's health. */
    public static final String APP_HEALTH_CHANNEL = "runner:app_health:";
    protected final ResourceProvider           resourceProvider;
    private final   DtoUnmarshallerFactory     dtoUnmarshallerFactory;
    private final   DtoFactory                 dtoFactory;
    private         EditorAgent                editorAgent;
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
    private   ProjectRunCallback                                runCallback;
    protected LogMessagesHandler                                runnerOutputHandler;
    protected SubscriptionHandler<ApplicationProcessDescriptor> runnerStatusHandler;
    protected SubscriptionHandler<String>                       runnerHealthHandler;
    private   boolean                                           isLastAppHealthOk;
    // The server makes the limited quantity of tries checking application's health,
    // so we're waiting for some time (about 30 sec.) and assume that app health is OK.
    private   Timer                                             setAppHealthOkTimer;
    //show time in "Total Time" indicator start immediately  after launch running process
    private   Timer                                             totalActiveTimeTimer;
    private   RunnerMetric                                      totalActiveTimeMetric; //calculate on client-side


    private String theme;

    @Inject
    public RunnerController(EventBus eventBus,
                            final WorkspaceAgent workspaceAgent,
                            final ResourceProvider resourceProvider,
                            final RunnerConsolePresenter console,
                            final RunnerServiceClient service,
                            UpdateServiceClient updateService,
                            final RunnerLocalizationConstant constant,
                            final NotificationManager notificationManager,
                            DtoFactory dtoFactory,
                            EditorAgent editorAgent,
                            final DtoUnmarshallerFactory dtoUnmarshallerFactory,
                            MessageBus messageBus,
                            ThemeAgent themeAgent) {
        this.workspaceAgent = workspaceAgent;
        this.resourceProvider = resourceProvider;
        this.console = console;
        this.service = service;
        this.updateService = updateService;
        this.constant = constant;
        this.notificationManager = notificationManager;
        this.dtoFactory = dtoFactory;
        this.editorAgent = editorAgent;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.messageBus = messageBus;
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
                                                            console.onAppStarted(lastApplicationDescriptor);
                                                            notificationManager.showNotification(new Notification(
                                                                    "Application " + event.getProject().getName() + " is running now.",
                                                                    INFO));

                                                            break;
                                                        }
                                                    }
                                                }

                                                @Override
                                                protected void onFailure(Throwable ignore) {
                                                }
                                            });
            }

            @Override
            public void onProjectClosed(ProjectActionEvent event) {
                if (isAnyAppRunning() && !getRunnerMetric(RunnerMetric.TERMINATION_TIME).getValue().equals(RunnerMetric.ALWAYS_ON)) {
                    stopActiveProject(false);
                }
                console.clear();
                activeProject = null;
                lastApplicationDescriptor = null;
            }

            @Override
            public void onProjectDescriptionChanged(ProjectActionEvent event) {
            }
        });

        eventBus.addHandler(WindowActionEvent.TYPE, new WindowActionHandler() {
            @Override
            public void onWindowClosing(WindowActionEvent event) {
                if (isAnyAppRunning() && !getRunnerMetric(RunnerMetric.TERMINATION_TIME).getValue().equals(RunnerMetric.ALWAYS_ON)) {
                    event.setMessage(constant.appWillBeStopped(resourceProvider.getActiveProject().getName()));
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

    /** Run active project. */
    public void runActiveProject(final boolean isUserAction) {
        // Save the files before running if necessary
        Array<EditorPartPresenter> dirtyEditors = editorAgent.getDirtyEditors();
        if (dirtyEditors.isEmpty()) {
            runActiveProject(null, false, null, isUserAction);
        } else {
            Ask askWindow = new Ask(constant.titlePromptSaveFiles(), constant.messagePromptSaveFiles(), new AskHandler() {
                @Override
                public void onOk() {
                    editorAgent.saveAll(new AsyncCallback() {
                        @Override
                        public void onFailure(Throwable caught) {
                            Log.error(getClass(), caught.getMessage());
                        }

                        @Override
                        public void onSuccess(Object result) {
                            runActiveProject(null, false, null, isUserAction);
                        }
                    });
                }

                @Override
                public void onCancel() {
                    runActiveProject(null, false, null, isUserAction);
                }
            });
            askWindow.show();
        }
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
        if (isAnyAppRunning) {
            Notification notification = new Notification("Another project is running now.", ERROR);
            notificationManager.showNotification(notification);
            return;
        }

        runCallback = callback;
        lastApplicationDescriptor = null;
        activeProject = resourceProvider.getActiveProject();

        notification = new Notification(constant.applicationStarting(activeProject.getName()), PROGRESS, RunnerController.this);
        notificationManager.showNotification(notification);
        console.print("[INFO] " + notification.getMessage());

        RunOptions runOptions = dtoFactory.createDto(RunOptions.class);
        if (debug) {
            runOptions.setDebugMode(dtoFactory.createDto(DebugMode.class).withMode("default"));
        }
        if (environment != null) {
            runOptions.setEnvironmentId(environment.getId());
        } else  if(activeProject.getRunnerEnvId() != null) {
            runOptions.setEnvironmentId(activeProject.getRunnerEnvId());
        }


        runOptions.getShellOptions().put("WebShellTheme", theme);
        runOptions.setSkipBuild(Boolean.parseBoolean(activeProject.getAttributeValue("runner:skipBuild")));


        if (isUserAction) {
            console.setActive();
        }

        service.run(activeProject.getPath(), runOptions,
                    new AsyncRequestCallback<ApplicationProcessDescriptor>(
                            dtoUnmarshallerFactory.newUnmarshaller(ApplicationProcessDescriptor.class)) {
                        @Override
                        protected void onSuccess(ApplicationProcessDescriptor result) {
                            onAppLaunched(result);
                        }

                        @Override
                        protected void onFailure(Throwable exception) {
                            onFail(constant.startApplicationFailed(activeProject.getName()), exception);
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
        if (isAnyAppRunning) {
            Notification notification = new Notification("Another project is running now.", ERROR);
            notificationManager.showNotification(notification);
            return;
        }

        runCallback = callback;
        lastApplicationDescriptor = null;
        activeProject = resourceProvider.getActiveProject();

        notification = new Notification(constant.applicationStarting(activeProject.getName()), PROGRESS, RunnerController.this);
        notificationManager.showNotification(notification);
        console.print("[INFO] " + notification.getMessage());

        if (isUserAction) {
            console.setActive();
        }

        runOptions.getShellOptions().put("WebShellTheme", theme);
        runOptions.setSkipBuild(Boolean.parseBoolean(activeProject.getAttributeValue("runner:skipBuild")));

        service.run(activeProject.getPath(), runOptions,
                    new AsyncRequestCallback<ApplicationProcessDescriptor>(
                            dtoUnmarshallerFactory.newUnmarshaller(ApplicationProcessDescriptor.class)) {
                        @Override
                        protected void onSuccess(ApplicationProcessDescriptor result) {
                            lastApplicationDescriptor = result;
                            isAnyAppRunning = true;
                            if (notification == null)
                                notification = new Notification(constant.applicationStarted(activeProject.getName()), INFO);
                            notification.setStatus(FINISHED);
                            notification.setMessage(constant.applicationStarted(activeProject.getName()));
                            startCheckingAppStatus(lastApplicationDescriptor);
                            startCheckingAppOutput(lastApplicationDescriptor);
                        }

                        @Override
                        protected void onFailure(Throwable exception) {
                            onFail(constant.startApplicationFailed(activeProject.getName()), exception);
                        }
                    }
                   );
    }

    private void startCheckingAppStatus(final ApplicationProcessDescriptor applicationProcessDescriptor) {
        totalActiveTimeMetric = dtoFactory.createDto(RunnerMetric.class).withDescription("Total active time").withName("total_time");

        //checking if it's ALWAYS ON app and we reopen running project
        // in this case we initiate timer with UP_TIME metric
        RunnerMetric runnerMetric = getRunnerMetric(RunnerMetric.UP_TIME);
        long initTime = 0;
        if (runnerMetric != null) {
            String value = runnerMetric.getValue();
            double v = NumberFormat.getDecimalFormat().parse(value);
            initTime = (long)v/1000;
        }
        totalActiveTimeTimer = new TotalTimeTimer(initTime);
        totalActiveTimeTimer.scheduleRepeating(1000);

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
                    messageBus.unsubscribe(STATUS_CHANNEL + applicationProcessDescriptor.getProcessId(), this);
                } catch (WebSocketException e) {
                    Log.error(RunnerController.class, e);
                }
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
        lastApplicationDescriptor = applicationProcessDescriptor;
        isAnyAppRunning = true;

        startCheckingAppStatus(lastApplicationDescriptor);
        startCheckingAppOutput(lastApplicationDescriptor);
    }

    /** Process changing application status. */
    private void onApplicationStatusUpdated(ApplicationProcessDescriptor descriptor) {

        switch (descriptor.getStatus()) {
            case RUNNING:
                if (notification == null)
                    notification = new Notification(constant.applicationStarted(activeProject.getName()), INFO);
                notification.setStatus(FINISHED);
                notification.setMessage(constant.applicationStarted(activeProject.getName()));
                console.print("[INFO] " + notification.getMessage());
                if (runCallback != null) {
                    runCallback.onRun(descriptor, activeProject);
                }
                startCheckingAppHealth(descriptor);
                console.onShellStarted(descriptor);
                break;
            case STOPPED:
                totalActiveTimeTimer.cancel();
                isAnyAppRunning = false;
                isLastAppHealthOk = false;
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
                notification.setMessage(constant.applicationStopped(activeProject.getName()));

                console.print("[INFO] " + notification.getMessage());

                console.onAppStopped();
                lastApplicationDescriptor=null;
                break;
            case FAILED:
                totalActiveTimeTimer.cancel();
                isAnyAppRunning = false;
                stopCheckingAppStatus(descriptor);
                stopCheckingAppOutput(descriptor);
                isLastAppHealthOk = false;
                getLogs(false);

                if (notification == null)
                    notification = new Notification(constant.applicationFailed(activeProject.getName()), ERROR);
                notification.setStatus(FINISHED);
                notification.setMessage(constant.applicationFailed(activeProject.getName()));
                console.print("[INFO] " + notification.getMessage());

                console.onAppStopped();
                lastApplicationDescriptor=null;
                break;
            case CANCELLED:
                totalActiveTimeTimer.cancel();
                isAnyAppRunning = false;
                isLastAppHealthOk = false;
                stopCheckingAppStatus(descriptor);
                stopCheckingAppOutput(descriptor);

                if (notification == null)
                    notification = new Notification(constant.applicationCanceled(activeProject.getName()), WARNING);
                notification.setStatus(FINISHED);
                notification.setMessage(constant.applicationCanceled(activeProject.getName()));
                console.print("[INFO] " + notification.getMessage());

                console.onAppStopped();
                lastApplicationDescriptor=null;
                break;
        }
    }

    /** Get logs of the currently launched application. */
    public void getLogs(boolean isUserAction) {
        final Link viewLogsLink = RunnerUtils.getLink(lastApplicationDescriptor, Constants.LINK_REL_VIEW_LOG);
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
        final Link stopLink = RunnerUtils.getLink(lastApplicationDescriptor, Constants.LINK_REL_STOP);
        if (stopLink == null) {
            onFail(constant.stopApplicationFailed(activeProject.getName()), null);
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

    /** Returns <code>true</code> - if link to get runner recipe file is exist and <code>false</code> - otherwise. */
    public boolean isRecipeLinkExists() {
        if (isAnyAppRunning() && lastApplicationDescriptor != null) {
            Link recipeLink = RunnerUtils.getLink(lastApplicationDescriptor, Constants.LINK_REL_RUNNER_RECIPE);
            return recipeLink != null;
        }
        return false;
    }

    /** Opens runner recipe file in editor. */
    public void showRecipe() {
        if (lastApplicationDescriptor != null) {
            final Link recipeLink = RunnerUtils.getLink(lastApplicationDescriptor, Constants.LINK_REL_RUNNER_RECIPE);
            if (recipeLink != null) {
                RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, recipeLink.getHref());
                try {
                    builder.sendRequest("", new com.google.gwt.http.client.RequestCallback() {
                        public void onResponseReceived(Request request, Response response) {
                            File recipeFile = new RecipeFile(response.getText());
                            editorAgent.openEditor(recipeFile);
                            EditorPartPresenter editor = editorAgent.getOpenedEditors().get(recipeFile.getPath());
                            if (editor instanceof CodenvyTextEditor) {
                                ((CodenvyTextEditor)editor).getView().setReadOnly(true);
                            }
                        }

                        public void onError(Request request, Throwable exception) {
                            notificationManager.showNotification(new Notification(
                                    "Attempted to retrive runner recipe, and an error occured.  If this issue continues, " +
                                    "please contact support.",
                                    ERROR));
                            Log.error(RunnerController.class, exception);
                        }
                    });
                } catch (RequestException e) {
                    notificationManager.showNotification(new Notification(
                            "Attempted to retrive runner recipe, and an error occured.  If this issue continues, please contact support.",
                            ERROR));
                    Log.error(RunnerController.class, e);
                }
            }
        }
    }

    /** Returns URL of the application which is currently running. */
    @Nullable
    public String getCurrentAppURL() {
        // Don't show app URL in console when app is stopped. After some time this URL may be used by other app.
        if (lastApplicationDescriptor != null && lastApplicationDescriptor.getStatus().equals(RUNNING) && isLastAppHealthOk) {
            return getAppLink();
        }
        return null;
    }

    /** Returns startTime {@link RunnerMetric}. */
    @Nullable
    public RunnerMetric getCurrentAppStartTime() {
        if (lastApplicationDescriptor != null && lastApplicationDescriptor.getCreationTime() >= 0) {
            Date startDate = new Date(lastApplicationDescriptor.getCreationTime());
            String startDateFormatted = DateTimeFormat.getFormat("dd/MM/yyyy HH:mm:ss").format(startDate);
            return dtoFactory.createDto(RunnerMetric.class).withDescription("Process started at").withValue(startDateFormatted);
        }
        return null;
    }

    /** Returns waitingTimeLimit {@link RunnerMetric}. */
    @Nullable
    public RunnerMetric getCurrentAppTerminationTime() {
        if (lastApplicationDescriptor == null) {
            return null;
        }
        RunnerMetric terminationMetric = getRunnerMetric(RunnerMetric.TERMINATION_TIME);
        if (terminationMetric != null) {
            if (RunnerMetric.ALWAYS_ON.equals(getRunnerMetric(RunnerMetric.TERMINATION_TIME).getValue()))
                return dtoFactory.createDto(RunnerMetric.class).withDescription(terminationMetric.getDescription())
                                 .withValue(getRunnerMetric(RunnerMetric.TERMINATION_TIME).getValue());
            // if app is running now, count uptime on the client-side
            if (terminationMetric.getValue() != null) {
                double terminationTime = NumberFormat.getDecimalFormat().parse(terminationMetric.getValue());
                final double terminationTimeout = terminationTime - System.currentTimeMillis();
                final String value = StringUtils.timeMlsToHumanReadable((long)terminationTimeout);
                return dtoFactory.createDto(RunnerMetric.class).withDescription(terminationMetric.getDescription()).withValue(value);
            }
        }
        RunnerMetric lifeTimeMetric = getRunnerMetric(RunnerMetric.LIFETIME);
        if (lifeTimeMetric != null && lastApplicationDescriptor != null && lastApplicationDescriptor.getStatus().equals(NEW)) {
            if (RunnerMetric.ALWAYS_ON.equals(getRunnerMetric(RunnerMetric.LIFETIME).getValue()))
                return dtoFactory.createDto(RunnerMetric.class).withDescription(lifeTimeMetric.getDescription())
                                 .withValue(getRunnerMetric(RunnerMetric.LIFETIME).getValue());
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
            return dtoFactory.createDto(RunnerMetric.class).withDescription(runnerMetric.getDescription()).withValue(stopDateFormatted);
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
        if (lastApplicationDescriptor != null) {
            for (RunnerMetric runnerStat : lastApplicationDescriptor.getRunStats()) {
                if (metricName.equals(runnerStat.getName())) {
                    return runnerStat;
                }
            }
        }
        return null;
    }

    private String getAppLink() {
        String url = null;
        final Link appLink = RunnerUtils.getLink(lastApplicationDescriptor, com.codenvy.api.runner.internal.Constants.LINK_REL_WEB_URL);
        if (appLink != null) {
            url = appLink.getHref();

            final Link codeServerLink = RunnerUtils.getLink(lastApplicationDescriptor, "code server");
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


    private class RecipeFile extends File {
        public RecipeFile(String content) {
            super();
            name = "Runner Recipe";
            mimeType = "text/plain";
            setContent(content);
        }

        @Override
        public String getPath() {
            return "runner_recipe";
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
