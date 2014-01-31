/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
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
package com.codenvy.ide.ext.java.jdi.client.debug;

import com.codenvy.api.runner.dto.ApplicationProcessDescriptor;
import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.event.ProjectActionEvent;
import com.codenvy.ide.api.event.ProjectActionHandler;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.parts.base.BasePresenter;
import com.codenvy.ide.api.ui.workspace.PartPresenter;
import com.codenvy.ide.api.ui.workspace.PartStackType;
import com.codenvy.ide.api.ui.workspace.WorkspaceAgent;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.commons.exception.ServerException;
import com.codenvy.ide.debug.Breakpoint;
import com.codenvy.ide.debug.BreakpointGutterManager;
import com.codenvy.ide.debug.Debugger;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.ext.java.jdi.client.JavaRuntimeExtension;
import com.codenvy.ide.ext.java.jdi.client.JavaRuntimeLocalizationConstant;
import com.codenvy.ide.ext.java.jdi.client.JavaRuntimeResources;
import com.codenvy.ide.ext.java.jdi.client.debug.changevalue.ChangeValuePresenter;
import com.codenvy.ide.ext.java.jdi.client.debug.expression.EvaluateExpressionPresenter;
import com.codenvy.ide.ext.java.jdi.client.fqn.FqnResolver;
import com.codenvy.ide.ext.java.jdi.client.fqn.FqnResolverFactory;
import com.codenvy.ide.ext.java.jdi.client.marshaller.DebuggerEventListUnmarshaller;
import com.codenvy.ide.ext.java.jdi.client.marshaller.DebuggerEventListUnmarshallerWS;
import com.codenvy.ide.ext.java.jdi.shared.BreakPoint;
import com.codenvy.ide.ext.java.jdi.shared.BreakPointEvent;
import com.codenvy.ide.ext.java.jdi.shared.DebuggerEvent;
import com.codenvy.ide.ext.java.jdi.shared.DebuggerEventList;
import com.codenvy.ide.ext.java.jdi.shared.DebuggerInfo;
import com.codenvy.ide.ext.java.jdi.shared.Location;
import com.codenvy.ide.ext.java.jdi.shared.StackFrameDump;
import com.codenvy.ide.ext.java.jdi.shared.StepEvent;
import com.codenvy.ide.ext.java.jdi.shared.Value;
import com.codenvy.ide.ext.java.jdi.shared.Variable;
import com.codenvy.ide.extension.runner.client.RunnerController;
import com.codenvy.ide.resources.model.File;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.HTTPStatus;
import com.codenvy.ide.rest.StringUnmarshaller;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.websocket.MessageBus;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.SubscriptionHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;
import static com.codenvy.ide.ext.java.client.projectmodel.JavaProjectDescription.ATTRIBUTE_SOURCE_FOLDERS;
import static com.codenvy.ide.ext.java.jdi.shared.DebuggerEvent.BREAKPOINT;
import static com.codenvy.ide.ext.java.jdi.shared.DebuggerEvent.STEP;

/**
 * The presenter provides debug java application.
 *
 * @author Vitaly Parfonov
 * @author Artem Zatsarynnyy
 */
@Singleton
public class DebuggerPresenter extends BasePresenter implements DebuggerView.ActionDelegate, Debugger {
    private static final String TITLE                  = "Debug";
    /** Period for checking debugger events. */
    private static final int    CHECK_EVENTS_PERIOD_MS = 2000;
    /** Channel identifier to receive events from debugger over WebSocket. */
    private String                                 debuggerEventsChannel;
    /** Channel identifier to receive event when debugger will disconnected. */
    private String                                 debuggerDisconnectedChannel;
    private DebuggerView                           view;
    private DtoFactory                             dtoFactory;
    private RunnerController                       runnerController;
    private DebuggerClientService                  service;
    private JavaRuntimeResources                   resources;
    private ConsolePart                            console;
    private JavaRuntimeLocalizationConstant        constant;
    private DebuggerInfo                           debuggerInfo;
    private MessageBus                             messageBus;
    private BreakpointGutterManager                gutterManager;
    private WorkspaceAgent                         workspaceAgent;
    private FqnResolverFactory                     resolverFactory;
    private EditorAgent                            editorAgent;
    private Variable                               selectedVariable;
    private EvaluateExpressionPresenter            evaluateExpressionPresenter;
    private ChangeValuePresenter                   changeValuePresenter;
    private NotificationManager                    notificationManager;
    private StringMap<File>                        filesToBreakpoints;
    /** A timer for checking debugger events. */
    private Timer                                  checkEventsTimer;
    /** Handler for processing events which is received from debugger over WebSocket connection. */
    private SubscriptionHandler<DebuggerEventList> debuggerEventsHandler;
    private SubscriptionHandler<Void>              debuggerDisconnectedHandler;
    private List<Variable>                         variables;
    private ApplicationProcessDescriptor           appDescriptor;

    /** Create presenter. */
    @Inject
    public DebuggerPresenter(DebuggerView view,
                             JavaRuntimeResources resources,
                             final DebuggerClientService service,
                             final EventBus eventBus,
                             final ConsolePart console,
                             final MessageBus messageBus,
                             final JavaRuntimeLocalizationConstant constant,
                             WorkspaceAgent workspaceAgent,
                             BreakpointGutterManager gutterManager,
                             FqnResolverFactory resolverFactory,
                             EditorAgent editorAgent,
                             final EvaluateExpressionPresenter evaluateExpressionPresenter,
                             ChangeValuePresenter changeValuePresenter,
                             final NotificationManager notificationManager,
                             final DtoFactory dtoFactory,
                             final RunnerController runnerController) {
        this.view = view;
        this.dtoFactory = dtoFactory;
        this.runnerController = runnerController;
        this.view.setDelegate(this);
        this.view.setTitle(TITLE);
        this.resources = resources;
        this.service = service;
        this.console = console;
        this.messageBus = messageBus;
        this.constant = constant;
        this.workspaceAgent = workspaceAgent;
        this.gutterManager = gutterManager;
        this.resolverFactory = resolverFactory;
        this.filesToBreakpoints = Collections.createStringMap();
        this.variables = new ArrayList<Variable>();
        this.editorAgent = editorAgent;
        this.evaluateExpressionPresenter = evaluateExpressionPresenter;
        this.changeValuePresenter = changeValuePresenter;
        this.notificationManager = notificationManager;

        this.checkEventsTimer = new Timer() {
            @Override
            public void run() {
                try {
                    service.checkEvents(debuggerInfo.getId(),
                                        new AsyncRequestCallback<DebuggerEventList>(new DebuggerEventListUnmarshaller(dtoFactory)) {
                                            @Override
                                            protected void onSuccess(DebuggerEventList result) {
                                                onEventListReceived(result);
                                            }

                                            @Override
                                            protected void onFailure(Throwable exception) {
                                                cancel();
                                                closeView();

                                                if (exception instanceof ServerException) {
                                                    ServerException serverException = (ServerException)exception;
                                                    if (HTTPStatus.INTERNAL_ERROR == serverException.getHTTPStatus() &&
                                                        serverException.getMessage() != null
                                                        && serverException.getMessage().contains("not found")) {
                                                        runnerController.stopActiveProject();
                                                        onDebuggerDisconnected();
                                                        return;
                                                    }
                                                }
                                                Notification notification = new Notification(exception.getMessage(), ERROR);
                                                notificationManager.showNotification(notification);
                                            }
                                        });
                } catch (RequestException e) {
                    console.print(e.getMessage());
                }
            }
        };

        this.debuggerEventsHandler = new SubscriptionHandler<DebuggerEventList>(new DebuggerEventListUnmarshallerWS(dtoFactory)) {
            @Override
            public void onMessageReceived(DebuggerEventList result) {
                onEventListReceived(result);
            }

            @Override
            public void onErrorReceived(Throwable exception) {
                try {
                    messageBus.unsubscribe(debuggerEventsChannel, this);
                } catch (WebSocketException e) {
                    Log.error(DebuggerPresenter.class, e);
                }
                closeView();

                if (exception instanceof com.codenvy.ide.websocket.rest.exceptions.ServerException) {
                    com.codenvy.ide.websocket.rest.exceptions.ServerException serverException =
                            (com.codenvy.ide.websocket.rest.exceptions.ServerException)exception;
                    if (HTTPStatus.INTERNAL_ERROR == serverException.getHTTPStatus() && serverException.getMessage() != null
                        && serverException.getMessage().contains("not found")) {
                        runnerController.stopActiveProject();
                        onDebuggerDisconnected();
                        return;
                    }
                }
                Notification notification = new Notification(exception.getMessage(), ERROR);
                notificationManager.showNotification(notification);
            }
        };

        this.debuggerDisconnectedHandler = new SubscriptionHandler<Void>() {
            @Override
            protected void onMessageReceived(Void result) {
                try {
                    messageBus.unsubscribe(debuggerDisconnectedChannel, this);
                } catch (WebSocketException e) {
                    Log.error(DebuggerPresenter.class, e);
                }

                evaluateExpressionPresenter.closeDialog();
                closeView();
                onDebuggerDisconnected();
            }

            @Override
            protected void onErrorReceived(Throwable exception) {
                try {
                    messageBus.unsubscribe(debuggerDisconnectedChannel, this);
                } catch (WebSocketException e) {
                    Log.error(DebuggerPresenter.class, e);
                }
            }
        };

        eventBus.addHandler(ProjectActionEvent.TYPE, new ProjectActionHandler() {
            @Override
            public void onProjectOpened(ProjectActionEvent event) {
                // do nothing
            }

            @Override
            public void onProjectClosed(ProjectActionEvent event) {
                // application will be stopped after closing a project
                if (debuggerInfo != null) {
                    changeButtonsEnableState(false);
                    onDebuggerDisconnected();
                    closeView();
                }
            }

            @Override
            public void onProjectDescriptionChanged(ProjectActionEvent event) {
                // do nothing
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public String getTitle() {
        return TITLE;
    }

    /** {@inheritDoc} */
    @Override
    public ImageResource getTitleImage() {
        return resources.debugApp();
    }

    /** {@inheritDoc} */
    @Override
    public String getTitleToolTip() {
        return "Debug";
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        view.setBreakpoints(gutterManager.getBreakPoints());
        view.setVariables(variables);

        container.setWidget(view);
    }

    private void onEventListReceived(@NotNull DebuggerEventList eventList) {
        String filePath = null;

        int currentLineNumber = 0;
        if (eventList.getEvents().size() > 0) {
            Location location;
            File activeFile = null;

            EditorPartPresenter activeEditor = editorAgent.getActiveEditor();
            if (activeEditor != null) {
                activeFile = activeEditor.getEditorInput().getFile();
            }

            List<DebuggerEvent> events = eventList.getEvents();
            for (DebuggerEvent event : events) {
                if (event.getType() == STEP) {
                    StepEvent stepEvent = (StepEvent)event;
                    location = stepEvent.getLocation();
                    filePath = resolveFilePathByLocation(location);
                    if (activeFile == null || !filePath.equalsIgnoreCase(activeFile.getPath())) {
                        openFile(location);
                    }
                    currentLineNumber = location.getLineNumber();
                } else if (event.getType() == BREAKPOINT) {
                    BreakPointEvent breakPointEvent = (BreakPointEvent)event;
                    location = breakPointEvent.getBreakPoint().getLocation();
                    filePath = resolveFilePathByLocation(location);
                    if (activeFile == null || !filePath.equalsIgnoreCase(activeFile.getPath())) {
                        activeFile = openFile(location);
                    }
                    currentLineNumber = location.getLineNumber();
                }
                getStackFrameDump();
                changeButtonsEnableState(true);
            }

            if (activeFile != null && filePath != null && filePath.equalsIgnoreCase(activeFile.getPath())) {
                gutterManager.markCurrentBreakPoint(currentLineNumber - 1);
            }
        }
    }

    /**
     * Create file path from location.
     *
     * @param location
     *         location of class
     * @return file path
     */
    @NotNull
    private String resolveFilePathByLocation(@NotNull Location location) {
        File file = getFileWithBreakPoints(location.getClassName());
        Project fileProject = file.getProject();
        final String sourcePath = fileProject.hasProperty(ATTRIBUTE_SOURCE_FOLDERS) ?
                                  (String)fileProject.getPropertyValue(ATTRIBUTE_SOURCE_FOLDERS) : "src/main/java";
        return fileProject.getPath() + "/" + sourcePath + "/" + location.getClassName().replace(".", "/") + ".java";
    }

    @Nullable
    private File openFile(@NotNull Location location) {
        File file = getFileWithBreakPoints(location.getClassName());
        if (file != null) {
            editorAgent.openEditor(file);
        }
        return file;
    }

    /**
     * Return file with current fqn.
     *
     * @param fqn
     *         file fqn
     * @return {@link File}
     */
    @Nullable
    private File getFileWithBreakPoints(@NotNull String fqn) {
        return filesToBreakpoints.get(fqn);
    }

    private void getStackFrameDump() {
        try {
            service.getStackFrameDump(debuggerInfo.getId(), new AsyncRequestCallback<String>(new StringUnmarshaller()) {
                @Override
                protected void onSuccess(String result) {
                    StackFrameDump dump = dtoFactory.createDtoFromJson(result, StackFrameDump.class);
                    List<Variable> variables = new ArrayList<Variable>();
                    variables.addAll(dump.getFields());
                    variables.addAll(dump.getLocalVariables());

                    DebuggerPresenter.this.variables = variables;
                    view.setVariables(variables);
                }

                @Override
                protected void onFailure(Throwable exception) {
                    Notification notification = new Notification(exception.getMessage(), ERROR);
                    notificationManager.showNotification(notification);
                }
            });
        } catch (RequestException e) {
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
        }
    }

    /** Change enable state of all buttons (except Disconnect button) on Debugger panel. */
    private void changeButtonsEnableState(boolean isEnable) {
        view.setEnableResumeButton(isEnable);
        view.setEnableStepIntoButton(isEnable);
        view.setEnableStepOverButton(isEnable);
        view.setEnableStepReturnButton(isEnable);
        view.setEnableEvaluateExpressionButtonEnable(isEnable);
    }

    /** {@inheritDoc} */
    @Override
    public void onResumeButtonClicked() {
        changeButtonsEnableState(false);
        try {
            service.resume(debuggerInfo.getId(), new AsyncRequestCallback<Void>() {
                @Override
                protected void onSuccess(Void result) {
                    resetStates();
                }

                @Override
                protected void onFailure(Throwable exception) {
                    Notification notification = new Notification(exception.getMessage(), ERROR);
                    notificationManager.showNotification(notification);
                }

            });
        } catch (RequestException e) {
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onRemoveAllBreakpointsButtonClicked() {
        try {
            service.deleteAllBreakpoints(debuggerInfo.getId(), new AsyncRequestCallback<String>() {
                @Override
                protected void onSuccess(String result) {
                    gutterManager.removeAllBreakPoints();
                    view.setBreakpoints(Collections.<Breakpoint>createArray());
                }

                @Override
                protected void onFailure(Throwable exception) {
                    Notification notification = new Notification(exception.getMessage(), ERROR);
                    notificationManager.showNotification(notification);
                }

            });
        } catch (RequestException e) {
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onDisconnectButtonClicked() {
        disconnectDebugger();
    }

    /** {@inheritDoc} */
    @Override
    public void onStepIntoButtonClicked() {
        changeButtonsEnableState(false);
        try {
            service.stepInto(debuggerInfo.getId(), new AsyncRequestCallback<Void>() {
                @Override
                protected void onSuccess(Void result) {
                    resetStates();
                }

                @Override
                protected void onFailure(Throwable exception) {
                    Notification notification = new Notification(exception.getMessage(), ERROR);
                    notificationManager.showNotification(notification);
                }

            });
        } catch (RequestException e) {
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onStepOverButtonClicked() {
        changeButtonsEnableState(false);
        try {
            service.stepOver(debuggerInfo.getId(), new AsyncRequestCallback<Void>() {
                @Override
                protected void onSuccess(Void result) {
                    resetStates();
                }

                @Override
                protected void onFailure(Throwable exception) {
                    Notification notification = new Notification(exception.getMessage(), ERROR);
                    notificationManager.showNotification(notification);
                }

            });
        } catch (RequestException e) {
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onStepReturnButtonClicked() {
        changeButtonsEnableState(false);
        try {
            service.stepReturn(debuggerInfo.getId(), new AsyncRequestCallback<Void>() {
                @Override
                protected void onSuccess(Void result) {
                    resetStates();
                }

                @Override
                protected void onFailure(Throwable exception) {
                    Notification notification = new Notification(exception.getMessage(), ERROR);
                    notificationManager.showNotification(notification);
                }

            });
        } catch (RequestException e) {
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onChangeValueButtonClicked() {
        if (selectedVariable == null) {
            return;
        }

        changeValuePresenter.showDialog(debuggerInfo, selectedVariable, new AsyncCallback<String>() {
            @Override
            public void onSuccess(String s) {
                view.setVariables(variables);
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.error(DebuggerPresenter.class, throwable);
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onEvaluateExpressionButtonClicked() {
        evaluateExpressionPresenter.showDialog(debuggerInfo);
    }

    /** {@inheritDoc} */
    @Override
    public void onExpandVariablesTree() {
        List<Variable> rootVariables = selectedVariable.getVariables();
        if (rootVariables.size() == 0) {
            try {
                service.getValue(debuggerInfo.getId(), selectedVariable, new AsyncRequestCallback<String>(new StringUnmarshaller()) {
                    @Override
                    protected void onSuccess(String result) {
                        Value value = dtoFactory.createDtoFromJson(result, Value.class);
                        List<Variable> variables = value.getVariables();
                        view.setVariablesIntoSelectedVariable(variables);
                        view.updateSelectedVariable();
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        Notification notification = new Notification(exception.getMessage(), ERROR);
                        notificationManager.showNotification(notification);
                    }
                });
            } catch (RequestException e) {
                Notification notification = new Notification(e.getMessage(), ERROR);
                notificationManager.showNotification(notification);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onSelectedVariableElement(@NotNull Variable variable) {
        this.selectedVariable = variable;
        updateChangeValueButtonEnableState();
    }

    /** Update enable state for 'Change value' button. */
    private void updateChangeValueButtonEnableState() {
        view.setEnableChangeValueButtonEnable(selectedVariable != null);
    }

    private void resetStates() {
        variables.clear();
        view.setVariables(variables);
        selectedVariable = null;
        updateChangeValueButtonEnableState();
        gutterManager.unmarkCurrentBreakPoint();
    }

    private void showDialog(@NotNull DebuggerInfo debuggerInfo) {
        view.setVMName(debuggerInfo.getVmName() + " " + debuggerInfo.getVmVersion());
        selectedVariable = null;
        updateChangeValueButtonEnableState();
        changeButtonsEnableState(false);

        workspaceAgent.openPart(this, PartStackType.INFORMATION);
        PartPresenter activePart = partStack.getActivePart();
        if (activePart != null && !activePart.equals(this)) {
            partStack.setActivePart(this);
        }
    }

    private void closeView() {
        variables.clear();
        workspaceAgent.hidePart(this);
        workspaceAgent.removePart(this);
    }

    /** Connect to the debugger. */
    public void attachDebugger(@NotNull final ApplicationProcessDescriptor appDescriptor) {
        this.appDescriptor = appDescriptor;
        try {
            service.connect(appDescriptor.getDebugHost(), appDescriptor.getDebugPort(),
                            new AsyncRequestCallback<String>(new StringUnmarshaller()) {
                                @Override
                                public void onSuccess(String result) {
                                    debuggerInfo = dtoFactory.createDtoFromJson(result, DebuggerInfo.class);
                                    console.print(
                                            constant.debuggerConnected(appDescriptor.getDebugHost() + ':' + appDescriptor.getDebugPort()));
                                    showDialog(debuggerInfo);
                                    startCheckingEvents();
                                }

                                @Override
                                protected void onFailure(Throwable exception) {
                                    Notification notification = new Notification(exception.getMessage(), ERROR);
                                    notificationManager.showNotification(notification);
                                }
                            });
        } catch (RequestException e) {
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
        }
    }

    private void disconnectDebugger() {
        if (debuggerInfo != null) {
            stopCheckingDebugEvents();
            try {
                service.disconnect(debuggerInfo.getId(), new AsyncRequestCallback<Void>() {
                    @Override
                    protected void onSuccess(Void result) {
                        changeButtonsEnableState(false);
                        runnerController.stopActiveProject();
                        onDebuggerDisconnected();
                        closeView();
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        Notification notification = new Notification(exception.getMessage(), ERROR);
                        notificationManager.showNotification(notification);
                    }
                });
            } catch (RequestException e) {
                Notification notification = new Notification(e.getMessage(), ERROR);
                notificationManager.showNotification(notification);
            }
        } else {
            changeButtonsEnableState(false);
            gutterManager.unmarkCurrentBreakPoint();
        }
    }

    private void startCheckingEvents() {
        debuggerEventsChannel = JavaRuntimeExtension.EVENTS_CHANNEL + debuggerInfo.getId();
        try {
            messageBus.subscribe(debuggerEventsChannel, debuggerEventsHandler);
        } catch (WebSocketException e) {
            // In case of WebSocket subscription is failed try to poll debugger events over HTTP,
            checkEventsTimer.scheduleRepeating(CHECK_EVENTS_PERIOD_MS);
        }

        try {
            debuggerDisconnectedChannel = JavaRuntimeExtension.DISCONNECT_CHANNEL + debuggerInfo.getId();
            messageBus.subscribe(debuggerDisconnectedChannel, debuggerDisconnectedHandler);
        } catch (WebSocketException e) {
            Log.error(DebuggerPresenter.class, e);
        }
    }

    private void stopCheckingDebugEvents() {
        checkEventsTimer.cancel();
        try {
            if (messageBus.isHandlerSubscribed(debuggerEventsHandler, debuggerEventsChannel)) {
                messageBus.unsubscribe(debuggerEventsChannel, debuggerEventsHandler);
            }
            if (messageBus.isHandlerSubscribed(debuggerDisconnectedHandler, debuggerDisconnectedChannel)) {
                messageBus.unsubscribe(debuggerDisconnectedChannel, debuggerDisconnectedHandler);
            }
        } catch (WebSocketException e) {
            Log.error(DebuggerPresenter.class, e);
        }
    }

    /** Perform some action after disconnecting a debugger. */
    private void onDebuggerDisconnected() {
        debuggerInfo = null;
        gutterManager.unmarkCurrentBreakPoint();
        gutterManager.removeAllBreakPoints();
        console.print(constant.debuggerDisconnected(appDescriptor.getDebugHost() + ':' + appDescriptor.getDebugPort()));
        appDescriptor = null;
    }

    private void updateBreakPoints() {
        view.setBreakpoints(gutterManager.getBreakPoints());
    }

    /** {@inheritDoc} */
    @Override
    public void addBreakpoint(@NotNull final File file, final int lineNumber, final AsyncCallback<Breakpoint> callback)
            throws RequestException {
        if (debuggerInfo != null) {
            Location location = dtoFactory.createDto(Location.class);
            location.setLineNumber(lineNumber + 1);
            final FqnResolver resolver = resolverFactory.getResolver(file.getMimeType());
            if (resolver != null) {
                location.setClassName(resolver.resolveFqn(file));
            } else {
                Log.warn(DebuggerPresenter.class, "FqnResolver is not found");
            }

            BreakPoint breakPoint = dtoFactory.createDto(BreakPoint.class);
            breakPoint.setLocation(location);
            breakPoint.setEnabled(true);

            try {
                service.addBreakpoint(debuggerInfo.getId(), breakPoint, new AsyncRequestCallback<Void>() {
                    @Override
                    protected void onSuccess(Void result) {
                        if (resolver != null) {
                            final String fqn = resolver.resolveFqn(file);
                            filesToBreakpoints.put(fqn, file);
                            Breakpoint breakpoint = new Breakpoint(Breakpoint.Type.BREAKPOINT, lineNumber, fqn);
                            callback.onSuccess(breakpoint);
                        }
                        updateBreakPoints();
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        callback.onFailure(exception);
                    }
                });
            } catch (RequestException e) {
                Notification notification = new Notification(e.getMessage(), ERROR);
                notificationManager.showNotification(notification);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void deleteBreakpoint(@NotNull File file, int lineNumber, final AsyncCallback<Void> callback) throws RequestException {
        if (debuggerInfo != null) {
            Location location = dtoFactory.createDto(Location.class);
            location.setLineNumber(lineNumber);
            FqnResolver resolver = resolverFactory.getResolver(file.getMimeType());
            if (resolver != null) {
                location.setClassName(resolver.resolveFqn(file));
            } else {
                Log.warn(DebuggerPresenter.class, "FqnResolver is not found");
            }

            BreakPoint point = dtoFactory.createDto(BreakPoint.class);
            point.setLocation(location);
            point.setEnabled(true);

            try {
                service.deleteBreakpoint(debuggerInfo.getId(), point, new AsyncRequestCallback<Void>() {
                    @Override
                    protected void onSuccess(Void result) {
                        callback.onSuccess(null);
                        updateBreakPoints();
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        callback.onFailure(exception);
                    }
                });
            } catch (RequestException e) {
                Notification notification = new Notification(e.getMessage(), ERROR);
                notificationManager.showNotification(notification);
            }
        }
    }
}