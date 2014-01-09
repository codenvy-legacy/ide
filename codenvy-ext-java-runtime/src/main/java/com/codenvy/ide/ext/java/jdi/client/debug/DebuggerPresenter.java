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
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.debug.Breakpoint;
import com.codenvy.ide.debug.BreakpointGutterManager;
import com.codenvy.ide.debug.Debugger;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.ext.java.jdi.client.JavaRuntimeExtension;
import com.codenvy.ide.ext.java.jdi.client.JavaRuntimeLocalizationConstant;
import com.codenvy.ide.ext.java.jdi.client.JavaRuntimeResources;
import com.codenvy.ide.ext.java.jdi.client.debug.changevalue.ChangeValuePresenter;
import com.codenvy.ide.ext.java.jdi.client.debug.expression.EvaluateExpressionPresenter;
import com.codenvy.ide.ext.java.jdi.client.debug.relaunch.ReLaunchDebuggerPresenter;
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
import com.codenvy.ide.resources.model.File;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.HTTPStatus;
import com.codenvy.ide.rest.StringUnmarshaller;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.websocket.MessageBus;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.SubscriptionHandler;
import com.codenvy.ide.websocket.rest.exceptions.ServerException;
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
import static com.codenvy.ide.ext.java.client.projectmodel.JavaProjectDesctiprion.PROPERTY_SOURCE_FOLDERS;
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
    private static final String TITLE                = "Debug";
    /** Default time (in milliseconds) to prolong application expiration time. */
    private static final long   DEFAULT_PROLONG_TIME = 10 * 60 * 1000; // 10 minutes
    /** Name of 'JRebel' project property. */
    /** Channel identifier to receive events from debugger over WebSocket. */
    private String                                 debuggerEventsChannel;
    /** Channel identifier to receive event when debugger will disconnected. */
    private String                                 debuggerDisconnectedChannel;
    /** Used to check if events from debugger receiving over WebSocket or over HTTP. */
    private boolean                                isCheckEventsTimerRunned;
    private DebuggerView                           view;
    private DtoFactory                             dtoFactory;
    private DebuggerClientService                  service;
    private JavaRuntimeResources                   resources;
    private EventBus                               eventBus;
    private ConsolePart                            console;
    private JavaRuntimeLocalizationConstant        constant;
    private DebuggerInfo                           debuggerInfo;
    private MessageBus                             messageBus;
    private BreakpointGutterManager                gutterManager;
    private WorkspaceAgent                         workspaceAgent;
    private FqnResolverFactory                     resolverFactory;
    private EditorAgent                            editorAgent;
    private Variable                               selectedVariable;
    private ReLaunchDebuggerPresenter              reLaunchDebuggerPresenter;
    private EvaluateExpressionPresenter            evaluateExpressionPresenter;
    private ChangeValuePresenter                   changeValuePresenter;
    private NotificationManager                    notificationManager;
    private StringMap<File>                        fileWithBreakPoints;
    /** A timer for checking debugger events. */
    private Timer                                  checkEventsTimer;
    /** Handler for processing events which is received from debugger over WebSocket connection. */
    private SubscriptionHandler<DebuggerEventList> debuggerEventsHandler;
    /** Handler for processing debugger disconnected event. */
    private SubscriptionHandler<Object>            debuggerDisconnectedHandler;
    private List<Variable>                         variables;

    /** Create presenter. */
    @Inject
    protected DebuggerPresenter(DebuggerView view,
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
                                ReLaunchDebuggerPresenter reLaunchDebuggerPresenter,
                                final EvaluateExpressionPresenter evaluateExpressionPresenter,
                                ChangeValuePresenter changeValuePresenter,
                                final NotificationManager notificationManager,
                                final DtoFactory dtoFactory) {
        this.view = view;
        this.dtoFactory = dtoFactory;
        this.view.setDelegate(this);
        this.view.setTitle(TITLE);
        this.resources = resources;
        this.service = service;
        this.eventBus = eventBus;
        this.console = console;
        this.messageBus = messageBus;
        this.constant = constant;
        this.workspaceAgent = workspaceAgent;
        this.gutterManager = gutterManager;
        this.resolverFactory = resolverFactory;
        this.fileWithBreakPoints = Collections.createStringMap();
        this.variables = new ArrayList<Variable>();
        this.editorAgent = editorAgent;
        this.reLaunchDebuggerPresenter = reLaunchDebuggerPresenter;
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
                                                closeDialog();

                                                if (exception instanceof ServerException) {
                                                    ServerException serverException = (ServerException)exception;
                                                    if (HTTPStatus.INTERNAL_ERROR == serverException.getHTTPStatus() &&
                                                        serverException.getMessage() != null
                                                        && serverException.getMessage().contains("not found")) {
                                                        console.print(constant.debuggerDisconnected());
                                                        return;
                                                    }
                                                }
                                                eventBus.fireEvent(new ExceptionThrownEvent(exception));
                                                console.print(exception.getMessage());
                                            }
                                        });
                } catch (RequestException e) {
                    eventBus.fireEvent(new ExceptionThrownEvent(e));
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
                    // nothing to do
                }
                closeDialog();

                if (exception instanceof ServerException) {
                    ServerException serverException = (ServerException)exception;
                    if (HTTPStatus.INTERNAL_ERROR == serverException.getHTTPStatus() && serverException.getMessage() != null
                        && serverException.getMessage().contains("not found")) {
                        console.print(constant.debuggerDisconnected());
                        debuggerDisconnected();
                        return;
                    }
                }
                eventBus.fireEvent(new ExceptionThrownEvent(exception));
                Notification notification = new Notification(exception.getMessage(), ERROR);
                notificationManager.showNotification(notification);
            }
        };

        this.debuggerDisconnectedHandler = new SubscriptionHandler<Object>() {
            @Override
            protected void onMessageReceived(Object result) {
                try {
                    messageBus.unsubscribe(debuggerDisconnectedChannel, this);
                } catch (WebSocketException e) {
                    // nothing to do
                }

                evaluateExpressionPresenter.closeDialog();
                closeDialog();

                console.print(constant.debuggerDisconnected());
                debuggerDisconnected();
            }

            @Override
            protected void onErrorReceived(Throwable exception) {
                try {
                    messageBus.unsubscribe(debuggerDisconnectedChannel, this);
                } catch (WebSocketException e) {
                    // nothing to do
                }
            }
        };

        this.eventBus.addHandler(ProjectActionEvent.TYPE, new ProjectActionHandler() {
            @Override
            public void onProjectOpened(ProjectActionEvent event) {
                // do nothing
            }

            @Override
            public void onProjectClosed(ProjectActionEvent event) {
                disconnectDebugger();
            }

            @Override
            public void onProjectDescriptionChanged(ProjectActionEvent event) {
                // do nothing
            }
        });
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
                    filePath = resolveFilePath(location);
                    if (activeFile == null || !filePath.equalsIgnoreCase(activeFile.getPath())) {
                        openFile(location);
                    }
                    currentLineNumber = location.getLineNumber();
                } else if (event.getType() == BREAKPOINT) {
                    BreakPointEvent breakPointEvent = (BreakPointEvent)event;
                    location = breakPointEvent.getBreakPoint().getLocation();
                    filePath = resolveFilePath(location);
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
    private String resolveFilePath(@NotNull Location location) {
        File file = getFileWithBreakPoints(location.getClassName());
        Project fileProject = file.getProject();
        String sourcePath = fileProject.hasProperty(PROPERTY_SOURCE_FOLDERS) ? (String)fileProject.getPropertyValue(PROPERTY_SOURCE_FOLDERS)
                                                                             : "src/main/java";
        return fileProject.getPath() + "/" + sourcePath + "/" + location.getClassName().replace(".", "/") + ".java";

        // TODO: provide project
//        String sourcePath =
//                project.hasProperty(PROPERTY_SOURCE_FOLDERS) ? (String)project.getPropertyValue(PROPERTY_SOURCE_FOLDERS)
//                                                             : "src/main/java";
//        return project.getPath() + "/" + sourcePath + "/" + location.getClassName().replace(".", "/") + ".java";
    }

    @Nullable
    private File openFile(@NotNull Location location) {
        File file = getFileWithBreakPoints(location.getClassName());
        if (file != null) {
            editorAgent.openEditor(file);
        }
        return file;
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
                    eventBus.fireEvent(new ExceptionThrownEvent(exception));
                    Notification notification = new Notification(exception.getMessage(), ERROR);
                    notificationManager.showNotification(notification);
                }
            });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
        }
    }

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
            service.resume(debuggerInfo.getId(), new AsyncRequestCallback<String>() {
                @Override
                protected void onSuccess(String result) {
                    resetStates();
                }

                @Override
                protected void onFailure(Throwable exception) {
                    eventBus.fireEvent(new ExceptionThrownEvent(exception));
                    Notification notification = new Notification(exception.getMessage(), ERROR);
                    notificationManager.showNotification(notification);
                }

            });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onRemoveAllBreakpointsButtonClicked() {
        try {
            service.deleteAllBreakPoint(debuggerInfo.getId(), new AsyncRequestCallback<String>() {
                @Override
                protected void onSuccess(String result) {
                    gutterManager.removeAllBreakPoints();
                    view.setBreakpoints(Collections.<Breakpoint>createArray());
                }

                @Override
                protected void onFailure(Throwable exception) {
                    eventBus.fireEvent(new ExceptionThrownEvent(exception));
                    Notification notification = new Notification(exception.getMessage(), ERROR);
                    notificationManager.showNotification(notification);
                }

            });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onDisconnectButtonClicked() {
        if (debuggerInfo != null) {
            stopCheckingEvents();
            try {
                service.disconnect(debuggerInfo.getId(), new AsyncRequestCallback<String>() {
                    @Override
                    protected void onSuccess(String result) {
                        changeButtonsEnableState(false);
                        debuggerInfo = null;
                        gutterManager.unmarkCurrentBreakPoint();
                        closeDialog();
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        eventBus.fireEvent(new ExceptionThrownEvent(exception));
                        Notification notification = new Notification(exception.getMessage(), ERROR);
                        notificationManager.showNotification(notification);
                    }
                });

            } catch (RequestException e) {
                eventBus.fireEvent(new ExceptionThrownEvent(e));
                Notification notification = new Notification(e.getMessage(), ERROR);
                notificationManager.showNotification(notification);
            }
        } else {
            changeButtonsEnableState(false);
            gutterManager.unmarkCurrentBreakPoint();
        }
    }

    /**
     * Stop checking events from debugger. If not subscribed on appropriate WebSocket channel
     * then stops previously launched timer. If subscribed then unsubscribes from channel.
     */
    private void stopCheckingEvents() {
        if (isCheckEventsTimerRunned) {
            checkEventsTimer.cancel();
            isCheckEventsTimerRunned = false;
        } else {
            try {
                if (messageBus.isHandlerSubscribed(debuggerEventsHandler, debuggerEventsChannel))
                    messageBus.unsubscribe(debuggerEventsChannel, debuggerEventsHandler);
            } catch (WebSocketException e) {
                // nothing to do
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onStepIntoButtonClicked() {
        changeButtonsEnableState(false);
        try {
            service.stepInto(debuggerInfo.getId(), new AsyncRequestCallback<String>() {
                @Override
                protected void onSuccess(String result) {
                    resetStates();
                }

                @Override
                protected void onFailure(Throwable exception) {
                    eventBus.fireEvent(new ExceptionThrownEvent(exception));
                    Notification notification = new Notification(exception.getMessage(), ERROR);
                    notificationManager.showNotification(notification);
                }

            });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onStepOverButtonClicked() {
        changeButtonsEnableState(false);
        try {
            service.stepOver(debuggerInfo.getId(), new AsyncRequestCallback<String>() {
                @Override
                protected void onSuccess(String result) {
                    resetStates();
                }

                @Override
                protected void onFailure(Throwable exception) {
                    eventBus.fireEvent(new ExceptionThrownEvent(exception));
                    Notification notification = new Notification(exception.getMessage(), ERROR);
                    notificationManager.showNotification(notification);
                }

            });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onStepReturnButtonClicked() {
        changeButtonsEnableState(false);
        try {
            service.stepReturn(debuggerInfo.getId(), new AsyncRequestCallback<String>() {
                @Override
                protected void onSuccess(String result) {
                    resetStates();
                }

                @Override
                protected void onFailure(Throwable exception) {
                    eventBus.fireEvent(new ExceptionThrownEvent(exception));
                    Notification notification = new Notification(exception.getMessage(), ERROR);
                    notificationManager.showNotification(notification);
                }

            });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
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
                dtoFactory.createDtoFromJson(s, Variable.class);
//                ((DtoClientImpls.VariableImpl)selectedVariable).setValue(s);
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
    public void onExpandTreeClicked() {
        List<Variable> rootVariables = selectedVariable.getVariables();
        if (rootVariables == null) {
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
                        eventBus.fireEvent(new ExceptionThrownEvent(exception));
                        Notification notification = new Notification(exception.getMessage(), ERROR);
                        notificationManager.showNotification(notification);
                    }
                });
            } catch (RequestException e) {
                eventBus.fireEvent(new ExceptionThrownEvent(e));
                Notification notification = new Notification(e.getMessage(), ERROR);
                notificationManager.showNotification(notification);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onSelectedTreeElementClicked(@NotNull Variable selectedVariable) {
        this.selectedVariable = selectedVariable;
        updateChangeValueButton();
    }

    /** Update enable state for change value button. */
    private void updateChangeValueButton() {
        view.setEnableChangeValueButtonEnable(selectedVariable != null);
    }

    /** Reset states. */
    private void resetStates() {
        variables.clear();
        view.setVariables(variables);
        selectedVariable = null;
        updateChangeValueButton();
        gutterManager.unmarkCurrentBreakPoint();
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
        return "Displays debug dialog";
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        view.setBreakpoints(gutterManager.getBreakPoints());
        view.setVariables(variables);

        container.setWidget(view);
    }

    private void showDialog(@NotNull DebuggerInfo debuggerInfo) {
        final String vmName = debuggerInfo.getVmName() + " " + debuggerInfo.getVmVersion();
        view.setVMName(vmName);
        selectedVariable = null;
        updateChangeValueButton();
        changeButtonsEnableState(false);

        workspaceAgent.openPart(this, PartStackType.INFORMATION);
        PartPresenter activePart = partStack.getActivePart();
        if (activePart != null && !activePart.equals(this)) {
            partStack.setActivePart(this);
        }

        debuggerConnected(debuggerInfo);
        startCheckingEvents();
    }

    private void closeDialog() {
        variables.clear();
        workspaceAgent.hidePart(this);
        workspaceAgent.removePart(this);
    }

    /** Start checking events from debugger. Subscribes on WebSocket channel or starts timer for checking events over HTTP. */
    private void startCheckingEvents() {
        debuggerEventsChannel = JavaRuntimeExtension.EVENTS_CHANNEL + debuggerInfo.getId();
        try {
            messageBus.subscribe(debuggerEventsChannel, debuggerEventsHandler);
        } catch (WebSocketException e) {
            checkEventsTimer.scheduleRepeating(3000);
            isCheckEventsTimerRunned = true;
        }
        try {
            debuggerDisconnectedChannel = JavaRuntimeExtension.DISCONNECT_CHANNEL + debuggerInfo.getId();
            messageBus.subscribe(debuggerDisconnectedChannel, debuggerDisconnectedHandler);
        } catch (WebSocketException ignore) {
        }
    }

    /** Connect to debugger. */
    public void connectDebugger(@NotNull final ApplicationProcessDescriptor appDescriptor) {
        final String debugHost = appDescriptor.getDebugHost() == null ? "127.0.0.1" : appDescriptor.getDebugHost();
        try {
            service.connect(debugHost, appDescriptor.getDebugPort(),
                            new AsyncRequestCallback<String>(new StringUnmarshaller()) {
                                @Override
                                public void onSuccess(String result) {
                                    console.print(constant.debuggerConnected(debugHost + ':' + appDescriptor.getDebugPort()));
                                    debuggerInfo = dtoFactory.createDtoFromJson(result, DebuggerInfo.class);
                                    showDialog(debuggerInfo);
                                }

                                @Override
                                protected void onFailure(Throwable exception) {
                                    reconnectDebugger(appDescriptor);
                                }
                            });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
        }
    }

    private void reconnectDebugger(@NotNull ApplicationProcessDescriptor appDescriptor) {
        reLaunchDebuggerPresenter.showDialog(appDescriptor, new AsyncCallback<DebuggerInfo>() {
            @Override
            public void onSuccess(DebuggerInfo debuggerInfo) {
                DebuggerPresenter.this.debuggerInfo = debuggerInfo;
                showDialog(debuggerInfo);
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.error(DebuggerPresenter.class, throwable);
            }
        });
    }

    private void disconnectDebugger() {
        if (debuggerInfo != null) {
            stopCheckingEvents();
            try {
                service.disconnect(debuggerInfo.getId(), new AsyncRequestCallback<String>() {
                    @Override
                    protected void onSuccess(String result) {
                        changeButtonsEnableState(false);
                        debuggerInfo = null;
                        gutterManager.unmarkCurrentBreakPoint();
                        closeDialog();
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

    /** Perform some action when debugger is connected. */
    private void debuggerConnected(@NotNull DebuggerInfo debuggerInfo) {
        this.debuggerInfo = debuggerInfo;
    }

    /** Perform some action when debugger is disconnected. */
    private void debuggerDisconnected() {
        debuggerInfo = null;
        gutterManager.removeAllBreakPoints();
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
        return fileWithBreakPoints.get(fqn);
    }

    private void updateBreakPoints() {
        view.setBreakpoints(gutterManager.getBreakPoints());
    }

    /** {@inheritDoc} */
    @Override
    public void addBreakPoint(@NotNull final File file, final int lineNumber, final AsyncCallback<Breakpoint> callback)
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

            BreakPoint point = dtoFactory.createDto(BreakPoint.class);
            point.setLocation(location);
            point.setEnabled(true);

            try {
                service.addBreakPoint(debuggerInfo.getId(), point, new AsyncRequestCallback<String>() {
                    @Override
                    protected void onSuccess(String result) {
                        if (resolver != null) {
                            String fqn = resolver.resolveFqn(file);
                            fileWithBreakPoints.put(fqn, file);
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
                Log.error(DebuggerPresenter.class, e);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void deleteBreakPoint(@NotNull File file, int lineNumber, final AsyncCallback<Void> callback) throws RequestException {
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
                service.deleteBreakPoint(debuggerInfo.getId(), point, new AsyncRequestCallback<Void>() {
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
                Log.error(DebuggerPresenter.class, e);
            }
        }
    }
}