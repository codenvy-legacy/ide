/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.ext.java.jdi.client.debug;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.annotations.Nullable;
import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.workspace.PartPresenter;
import com.codenvy.ide.api.ui.workspace.PartStackType;
import com.codenvy.ide.api.ui.workspace.WorkspaceAgent;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.debug.Breakpoint;
import com.codenvy.ide.debug.BreakpointGutterManager;
import com.codenvy.ide.debug.Debugger;
import com.codenvy.ide.ext.java.jdi.client.JavaRuntimeExtension;
import com.codenvy.ide.ext.java.jdi.client.JavaRuntimeLocalizationConstant;
import com.codenvy.ide.ext.java.jdi.client.JavaRuntimeResources;
import com.codenvy.ide.ext.java.jdi.client.debug.changevalue.ChangeValuePresenter;
import com.codenvy.ide.ext.java.jdi.client.debug.expression.EvaluateExpressionPresenter;
import com.codenvy.ide.ext.java.jdi.client.debug.relaunch.ReLaunchDebuggerPresenter;
import com.codenvy.ide.ext.java.jdi.client.fqn.FqnResolver;
import com.codenvy.ide.ext.java.jdi.client.fqn.FqnResolverFactory;
import com.codenvy.ide.ext.java.jdi.client.marshaller.*;
import com.codenvy.ide.ext.java.jdi.client.run.ApplicationRunnerClientService;
import com.codenvy.ide.ext.java.jdi.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.java.jdi.shared.*;
import com.codenvy.ide.extension.maven.client.event.BuildProjectEvent;
import com.codenvy.ide.extension.maven.client.event.ProjectBuiltEvent;
import com.codenvy.ide.extension.maven.client.event.ProjectBuiltHandler;
import com.codenvy.ide.extension.maven.shared.BuildStatus;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.json.JsonStringMap;
import com.codenvy.ide.part.base.BasePresenter;
import com.codenvy.ide.resources.model.File;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Property;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.HTTPStatus;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.websocket.MessageBus;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.codenvy.ide.websocket.rest.SubscriptionHandler;
import com.codenvy.ide.websocket.rest.exceptions.ServerException;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

import static com.codenvy.ide.ext.java.client.projectmodel.JavaProjectDesctiprion.PROPERTY_SOURCE_FOLDERS;
import static com.codenvy.ide.ext.java.jdi.shared.DebuggerEvent.BREAKPOINT;
import static com.codenvy.ide.ext.java.jdi.shared.DebuggerEvent.STEP;

/**
 * The presenter provides debug java application.
 *
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 */
@Singleton
public class DebuggerPresenter extends BasePresenter implements DebuggerView.ActionDelegate, ProjectBuiltHandler, Debugger {
    private static final String TITLE                    = "Debug";
    /** Default time (in milliseconds) to prolong application expiration time. */
    private static final long   DEFAULT_PROLONG_TIME     = 10 * 60 * 1000; // 10 minutes
    /** Name of 'JRebel' project property. */
    private static final String JREBEL                   = "jrebel";
    /** Name of 'JRebel update' project property. */
    private static final String JREBEL_COUNT             = "jrebelCount";
    /** Max number of JRebel updating. */
    private static final byte   MAX_NUMBER_JREBEL_UPDATE = 10;
    /** Value of 'JRebel update' project property if user data  forward to ZeroTurnaround. */
    private static final byte   JREBEL_UPDATED           = 0;
    /** Channel identifier to receive events from debugger over WebSocket. */
    private String                                 debuggerEventsChannel;
    /** Channel identifier to receive event when debugger will disconnected. */
    private String                                 debuggerDisconnectedChannel;
    /** Channel identifier to receive events when application expiration time will left. */
    private String                                 expireSoonAppChannel;
    /** Used to check if events from debugger receiving over WebSocket or over HTTP. */
    private boolean                                isCheckEventsTimerRunned;
    private DebuggerView                           view;
    private DebuggerClientService                  service;
    private JavaRuntimeResources                   resources;
    private EventBus                               eventBus;
    private ConsolePart                            console;
    private JavaRuntimeLocalizationConstant        constant;
    private DebuggerInfo                           debuggerInfo;
    private Project                                project;
    private ApplicationInstance                    runningApp;
    private MessageBus                             messageBus;
    private BreakpointGutterManager                gutterManager;
    private WorkspaceAgent                         workspaceAgent;
    private ResourceProvider                       resourceProvider;
    private ApplicationRunnerClientService         applicationRunnerClientService;
    private FqnResolverFactory                     resolverFactory;
    private EditorAgent                            editorAgent;
    private Variable                               selectedVariable;
    private boolean                                updateApp;
    private String                                 restContext;
    private HandlerRegistration                    projectBuildHandler;
    private ReLaunchDebuggerPresenter              reLaunchDebuggerPresenter;
    private EvaluateExpressionPresenter            evaluateExpressionPresenter;
    private ChangeValuePresenter                   changeValuePresenter;
    private JsonStringMap<File>                    fileWithBreakPoints;
    /** Handler for processing events which is received from debugger over WebSocket connection. */
    private SubscriptionHandler<DebuggerEventList> debuggerEventsHandler;
    /** Handler for processing received application name which will be stopped soon. */
    private SubscriptionHandler<Object>            expireSoonAppsHandler;
    /** Handler for processing debugger disconnected event. */
    private SubscriptionHandler<Object>            debuggerDisconnectedHandler;
    private JsonArray<Variable>                    variables;
    /** A timer for checking events */
    private Timer checkEventsTimer = new Timer() {
        @Override
        public void run() {
            DtoClientImpls.DebuggerEventListImpl eventList = DtoClientImpls.DebuggerEventListImpl.make();
            DebuggerEventListUnmarshaller unmarshaller = new DebuggerEventListUnmarshaller(eventList);

            try {
                service.checkEvents(debuggerInfo.getId(), new AsyncRequestCallback<DebuggerEventList>(unmarshaller) {
                    @Override
                    protected void onSuccess(DebuggerEventList result) {
                        onEventListReceived(result);
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        cancel();
                        closeDialog();

                        if (runningApp != null) {
                            if (exception instanceof ServerException) {
                                ServerException serverException = (ServerException)exception;
                                if (HTTPStatus.INTERNAL_ERROR == serverException.getHTTPStatus() && serverException.getMessage() != null
                                    && serverException.getMessage().contains("not found")) {
                                    console.print(constant.debuggerDisconnected());
                                    appStopped(runningApp.getName());
                                    return;
                                }
                            }
                            eventBus.fireEvent(new ExceptionThrownEvent(exception));
                            console.print(exception.getMessage());
                        }
                    }
                });
            } catch (RequestException e) {
                eventBus.fireEvent(new ExceptionThrownEvent(e));
                console.print(e.getMessage());
            }
        }
    };

    /**
     * Create presenter.
     *
     * @param view
     * @param resources
     * @param service
     * @param eventBus
     * @param console
     * @param messageBus
     * @param constant
     * @param resourceProvider
     * @param workspaceAgent
     * @param applicationRunnerClientService
     * @param restContext
     * @param gutterManager
     * @param resolverFactory
     * @param editorAgent
     * @param reLaunchDebuggerPresenter
     * @param evaluateExpressionPresenter
     * @param changeValuePresenter
     */
    @Inject
    protected DebuggerPresenter(DebuggerView view, JavaRuntimeResources resources, DebuggerClientService service, EventBus eventBus,
                                ConsolePart console, MessageBus messageBus, JavaRuntimeLocalizationConstant constant,
                                ResourceProvider resourceProvider, WorkspaceAgent workspaceAgent,
                                ApplicationRunnerClientService applicationRunnerClientService, @Named("restContext") String restContext,
                                BreakpointGutterManager gutterManager, FqnResolverFactory resolverFactory, EditorAgent editorAgent,
                                ReLaunchDebuggerPresenter reLaunchDebuggerPresenter,
                                EvaluateExpressionPresenter evaluateExpressionPresenter, ChangeValuePresenter changeValuePresenter) {
        this.view = view;
        this.view.setDelegate(this);
        this.view.setTitle(TITLE);
        this.resources = resources;
        this.service = service;
        this.eventBus = eventBus;
        this.console = console;
        this.messageBus = messageBus;
        this.constant = constant;
        this.resourceProvider = resourceProvider;
        this.workspaceAgent = workspaceAgent;
        this.applicationRunnerClientService = applicationRunnerClientService;
        this.restContext = restContext;
        this.gutterManager = gutterManager;
        this.resolverFactory = resolverFactory;
        this.fileWithBreakPoints = JsonCollections.createStringMap();
        this.variables = JsonCollections.createArray();
        this.editorAgent = editorAgent;
        this.reLaunchDebuggerPresenter = reLaunchDebuggerPresenter;
        this.evaluateExpressionPresenter = evaluateExpressionPresenter;
        this.changeValuePresenter = changeValuePresenter;
        this.expireSoonAppsHandler = new SubscriptionHandler<Object>() {
            @Override
            public void onMessageReceived(Object result) {
                // unsubscribe to receiving events to avoid receiving
                // messages while user not press any button in appeared dialog
                try {
                    DebuggerPresenter.this.messageBus.unsubscribe(expireSoonAppChannel, this);
                } catch (WebSocketException e) {
                    // nothing to do
                }

                boolean isProlonged = Window.confirm(DebuggerPresenter.this.constant.prolongExpirationTimeQuestion());
                if (isProlonged) {
                    prolongExpirationTime();
                } else {
                    doDisconnectDebugger();
                }
            }

            @Override
            public void onErrorReceived(Throwable exception) {
                try {
                    DebuggerPresenter.this.messageBus.unsubscribe(expireSoonAppChannel, this);
                } catch (WebSocketException e) {
                    // nothing to do
                }
            }
        };

        DtoClientImpls.DebuggerEventListImpl debuggerEventList = DtoClientImpls.DebuggerEventListImpl.make();
        DebuggerEventListUnmarshallerWS unmarshallerWS = new DebuggerEventListUnmarshallerWS(debuggerEventList);
        this.debuggerEventsHandler = new SubscriptionHandler<DebuggerEventList>(unmarshallerWS) {
            @Override
            public void onMessageReceived(DebuggerEventList result) {
                onEventListReceived(result);
            }

            @Override
            public void onErrorReceived(Throwable exception) {
                try {
                    DebuggerPresenter.this.messageBus.unsubscribe(debuggerEventsChannel, this);
                    DebuggerPresenter.this.messageBus.unsubscribe(expireSoonAppChannel, expireSoonAppsHandler);
                } catch (WebSocketException e) {
                    // nothing to do
                }

                closeDialog();

                if (runningApp != null) {
                    if (exception instanceof ServerException) {
                        ServerException serverException = (ServerException)exception;
                        if (HTTPStatus.INTERNAL_ERROR == serverException.getHTTPStatus() && serverException.getMessage() != null
                            && serverException.getMessage().contains("not found")) {
                            DebuggerPresenter.this.console.print(DebuggerPresenter.this.constant.debuggerDisconnected());
                            debuggerDisconnected();
                            appStopped(runningApp.getName());

                            return;
                        }
                    }
                    DebuggerPresenter.this.eventBus.fireEvent(new ExceptionThrownEvent(exception));
                    DebuggerPresenter.this.console.print(exception.getMessage());
                }
            }
        };

        this.debuggerDisconnectedHandler = new SubscriptionHandler<Object>() {
            @Override
            protected void onMessageReceived(Object result) {
                try {
                    DebuggerPresenter.this.messageBus.unsubscribe(debuggerDisconnectedChannel, this);
                } catch (WebSocketException e) {
                    // nothing to do
                }

                DebuggerPresenter.this.evaluateExpressionPresenter.closeDialog();
                closeDialog();

                if (runningApp != null) {
                    DebuggerPresenter.this.console.print(DebuggerPresenter.this.constant.debuggerDisconnected());
                    debuggerDisconnected();
                }
            }

            @Override
            protected void onErrorReceived(Throwable exception) {
                try {
                    DebuggerPresenter.this.messageBus.unsubscribe(debuggerDisconnectedChannel, this);
                } catch (WebSocketException e) {
                    // nothing to do
                }
            }
        };
    }

    /**
     * Performs actions when event list was received.
     *
     * @param eventList
     *         debugger event list
     */
    private void onEventListReceived(@NotNull DebuggerEventList eventList) {
        String filePath = null;

        System.out.println("DebuggerPresenter.onEventListReceived()" + eventList.getEvents().size());
        int currentLineNumber = 0;
        if (eventList.getEvents().size() > 0) {
            Location location;
            File activeFile = null;

            EditorPartPresenter activeEditor = editorAgent.getActiveEditor();
            if (activeEditor != null) {
                activeFile = activeEditor.getEditorInput().getFile();
            }

            JsonArray<DebuggerEvent> events = eventList.getEvents();
            for (int i = 0; i < events.size(); i++) {
                DebuggerEvent event = events.get(i);
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
                doGetDump();
                enableButtons(true);
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
        String sourcePath =
                project.hasProperty(PROPERTY_SOURCE_FOLDERS) ? (String)project.getPropertyValue(PROPERTY_SOURCE_FOLDERS)
                                                             : "src/main/java";
        return project.getPath() + "/" + sourcePath + "/" + location.getClassName().replace(".", "/") + ".java";
    }

    @Nullable
    private File openFile(@NotNull Location location) {
        File file = getFileWithBreakPoints(location.getClassName());
        if (file != null) {
            editorAgent.openEditor(file);
        }
        return file;
    }

    /** Get dump. */
    private void doGetDump() {
        DtoClientImpls.StackFrameDumpImpl stackFrameDump = DtoClientImpls.StackFrameDumpImpl.make();
        StackFrameDumpUnmarshaller unmarshaller = new StackFrameDumpUnmarshaller(stackFrameDump);

        try {
            service.dump(debuggerInfo.getId(), new AsyncRequestCallback<StackFrameDump>(unmarshaller) {
                @Override
                protected void onSuccess(StackFrameDump result) {
                    JsonArray<Variable> variables = JsonCollections.createArray();
                    variables.addAll(result.getFields());
                    variables.addAll(result.getLocalVariables());

                    DebuggerPresenter.this.variables = variables;
                    view.setVariables(variables);
                }

                @Override
                protected void onFailure(Throwable exception) {
                    eventBus.fireEvent(new ExceptionThrownEvent(exception));
                    console.print(exception.getMessage());
                }
            });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }

    /**
     * Change the enable state of buttons.
     *
     * @param isEnable
     *         <code>true</code> to enable the button, <code>false</code> to disable it
     */
    private void enableButtons(boolean isEnable) {
        view.setEnableResumeButton(isEnable);
        view.setEnableStepIntoButton(isEnable);
        view.setEnableStepOverButton(isEnable);
        view.setEnableStepReturnButton(isEnable);
        view.setEnableEvaluateExpressionButtonEnable(isEnable);
    }

    /** Prolong expiration time of the application which is currently runned. */
    private void prolongExpirationTime() {
        try {
            applicationRunnerClientService.prolongExpirationTime(runningApp.getName(), DEFAULT_PROLONG_TIME, new RequestCallback<Object>() {
                @Override
                protected void onSuccess(Object result) {
                    try {
                        messageBus.subscribe(expireSoonAppChannel, expireSoonAppsHandler);
                    } catch (WebSocketException e) {
                        // nothing to do
                    }
                }

                @Override
                protected void onFailure(Throwable exception) {
                    if (exception.getMessage() == null) {
                        Window.alert(constant.prolongExpirationTimeFailed());
                    } else {
                        Window.alert(exception.getMessage());
                    }
                }
            });
        } catch (WebSocketException e) {
            Window.alert(constant.prolongExpirationTimeFailed());
        }
    }

    /**
     * Check whether application is run.
     *
     * @return <code>true</code> if the application is run, and <code>false</code> otherwise
     */
    public boolean isAppRunning() {
        return runningApp != null;
    }

    /** @return running application */
    public ApplicationInstance getRunningApp() {
        return runningApp;
    }

    /** {@inheritDoc} */
    @Override
    public void onResumeButtonClicked() {
        enableButtons(false);
        try {
            service.resume(debuggerInfo.getId(), new AsyncRequestCallback<String>() {
                @Override
                protected void onSuccess(String result) {
                    resetStates();
                }

                @Override
                protected void onFailure(Throwable exception) {
                    eventBus.fireEvent(new ExceptionThrownEvent(exception));
                    console.print(exception.getMessage());
                }

            });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
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
                    view.setBreakPoints(JsonCollections.<Breakpoint>createArray());
                }

                @Override
                protected void onFailure(Throwable exception) {
                    eventBus.fireEvent(new ExceptionThrownEvent(exception));
                    console.print(exception.getMessage());
                }

            });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
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
                        enableButtons(false);
                        debuggerInfo = null;
                        gutterManager.unmarkCurrentBreakPoint();
                        closeDialog();
                        doStopApp();
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        eventBus.fireEvent(new ExceptionThrownEvent(exception));
                        console.print(exception.getMessage());
                    }
                });

            } catch (RequestException e) {
                eventBus.fireEvent(new ExceptionThrownEvent(e));
                console.print(e.getMessage());
            }
        } else {
            enableButtons(false);
            gutterManager.unmarkCurrentBreakPoint();
            doStopApp();
        }
    }

    /** Stop application. */
    public void doStopApp() {
        if (runningApp != null) {
            try {
                service.stopApplication(runningApp, new AsyncRequestCallback<String>() {
                    @Override
                    protected void onSuccess(String result) {
                        if (runningApp != null) {
                            appStopped(runningApp.getName());
                        }
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        String message = exception.getMessage() != null ? exception.getMessage() : constant.stopApplicationFailed();
                        console.print(message);

                        if (exception instanceof ServerException) {
                            ServerException serverException = (ServerException)exception;
                            if (HTTPStatus.INTERNAL_ERROR == serverException.getHTTPStatus() && serverException.getMessage() != null &&
                                serverException.getMessage().contains("not found") && runningApp != null) {
                                appStopped(runningApp.getName());
                            }
                        }
                    }
                });
            } catch (RequestException e) {
                eventBus.fireEvent(new ExceptionThrownEvent(e));
                console.print(e.getMessage());
            }
        }
    }

    /**
     * Show message about stopped some application.
     *
     * @param appName
     *         application name
     */
    private void appStopped(@NotNull String appName) {
        String msg = constant.applicationStoped(appName);
        console.print(msg);
        runningApp = null;
    }

    /**
     * Stop checking events from debugger. If not subscribed on appropriate WebSocket channel then stops previously launched timer. If
     * subscribed then unsubscribe from channel.
     */
    private void stopCheckingEvents() {
        if (isCheckEventsTimerRunned) {
            checkEventsTimer.cancel();
            isCheckEventsTimerRunned = false;
        } else {
            try {
                if (messageBus.isHandlerSubscribed(debuggerEventsHandler, debuggerEventsChannel))
                    messageBus.unsubscribe(debuggerEventsChannel, debuggerEventsHandler);

                if (messageBus.isHandlerSubscribed(expireSoonAppsHandler, expireSoonAppChannel))
                    messageBus.unsubscribe(expireSoonAppChannel, expireSoonAppsHandler);
            } catch (WebSocketException e) {
                // nothing to do
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onStepIntoButtonClicked() {
        enableButtons(false);
        try {
            service.stepInto(debuggerInfo.getId(), new AsyncRequestCallback<String>() {
                @Override
                protected void onSuccess(String result) {
                    resetStates();
                }

                @Override
                protected void onFailure(Throwable exception) {
                    eventBus.fireEvent(new ExceptionThrownEvent(exception));
                    console.print(exception.getMessage());
                }

            });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onStepOverButtonClicked() {
        enableButtons(false);
        try {
            service.stepOver(debuggerInfo.getId(), new AsyncRequestCallback<String>() {
                @Override
                protected void onSuccess(String result) {
                    resetStates();
                }

                @Override
                protected void onFailure(Throwable exception) {
                    eventBus.fireEvent(new ExceptionThrownEvent(exception));
                    console.print(exception.getMessage());
                }

            });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onStepReturnButtonClicked() {
        enableButtons(false);
        try {
            service.stepReturn(debuggerInfo.getId(), new AsyncRequestCallback<String>() {
                @Override
                protected void onSuccess(String result) {
                    resetStates();
                }

                @Override
                protected void onFailure(Throwable exception) {
                    eventBus.fireEvent(new ExceptionThrownEvent(exception));
                    console.print(exception.getMessage());
                }

            });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
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
                ((DtoClientImpls.VariableImpl)selectedVariable).setValue(s);
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
        JsonArray<Variable> rootVariables = selectedVariable.getVariables();
        if (rootVariables == null) {
            DtoClientImpls.ValueImpl value = DtoClientImpls.ValueImpl.make();
            ValueUnmarshaller unmarshaller = new ValueUnmarshaller(value);

            try {
                service.getValue(debuggerInfo.getId(), selectedVariable, new AsyncRequestCallback<Value>(unmarshaller) {
                    @Override
                    protected void onSuccess(Value result) {
                        JsonArray<Variable> variables = result.getVariables();
                        view.setVariablesIntoSelectedVariable(variables);
                        view.updateSelectedVariable();
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        eventBus.fireEvent(new ExceptionThrownEvent(exception));
                        console.print(exception.getMessage());
                    }
                });
            } catch (RequestException e) {
                eventBus.fireEvent(new ExceptionThrownEvent(e));
                console.print(e.getMessage());
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
        view.setBreakPoints(gutterManager.getBreakPoints());
        view.setVariables(variables);

        container.setWidget(view);
    }

    /** Start checking events from debugger. Subscribes on WebSocket channel or starts timer for checking events over HTTP. */
    private void startCheckingEvents() {
        debuggerEventsChannel = JavaRuntimeExtension.EVENTS_CHANNEL + debuggerInfo.getId();
        debuggerDisconnectedChannel = JavaRuntimeExtension.DISCONNECT_CHANNEL + debuggerInfo.getId();
        try {
            messageBus.subscribe(debuggerEventsChannel, debuggerEventsHandler);
            messageBus.subscribe(debuggerDisconnectedChannel, debuggerDisconnectedHandler);
        } catch (WebSocketException e) {
            checkEventsTimer.scheduleRepeating(3000);
            isCheckEventsTimerRunned = true;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onProjectBuilt(ProjectBuiltEvent event) {
        projectBuildHandler.removeHandler();
        BuildStatus buildStatus = event.getBuildStatus();
        if (buildStatus.getStatus().equals(BuildStatus.Status.SUCCESSFUL)) {
            console.print(constant.applicationStarting());
            if (updateApp) {
                updateApp = false;
                if (writeJRebelCountProperty(project)) {
                    updateApplication(buildStatus.getDownloadUrl());
                }
            } else {
                debugApplication(buildStatus.getDownloadUrl());
            }
        }
    }

    /**
     * Writes 'jrebelCount' property to the project properties.
     *
     * @param project
     *         {@link Project}
     */
    private boolean writeJRebelCountProperty(@NotNull final Project project) {
        if (project.getPropertyValue(JREBEL_COUNT) == null) {
            project.getProperties().add(new Property(JREBEL_COUNT, Integer.toString(1)));
        } else {
            int countJRebel = Integer.parseInt((String)project.getPropertyValue(JREBEL_COUNT));
            if (countJRebel == JREBEL_UPDATED) {
                return true;
            } else if (countJRebel < MAX_NUMBER_JREBEL_UPDATE) {
                countJRebel++;
            } else {
                // TODO need to port JRebel support
                // IDE.fireEvent(new JRebelUserInfoEvent());
                return false;
            }
            JsonArray<Property> properties = project.getProperties();
            for (int i = 0; i < properties.size(); i++) {
                Property property = properties.get(i);
                if (property.getName().equals(JREBEL_COUNT)) {
                    JsonArray<String> value = JsonCollections.createArray(Integer.toString(countJRebel));
                    property.setValue(value);
                    break;
                }
            }
        }

        project.flushProjectProperties(new AsyncCallback<Project>() {
            @Override
            public void onSuccess(Project result) {
                // nothing to do
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error(DebuggerPresenter.class, "Can not refresh properties in project " + project.getName(), caught);
            }
        });
        return true;
    }

    /**
     * Update deployed application using JRebel.
     *
     * @param warUrl
     *         URL to download project WAR
     */
    private void updateApplication(@NotNull String warUrl) {
        if (runningApp != null) {
            try {
                applicationRunnerClientService.updateApplication(runningApp.getName(), warUrl, new AsyncRequestCallback<Object>() {
                    @Override
                    protected void onSuccess(Object result) {
                        String message = constant.applicationUpdated(runningApp.getName(), getAppUrlsAsString(runningApp));
                        console.print(message);
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        String message = exception.getMessage() != null ? exception.getMessage()
                                                                        : constant.updateApplicationFailed(runningApp.getName());
                        console.print(message);
                    }
                });
            } catch (RequestException e) {
                eventBus.fireEvent(new ExceptionThrownEvent(e));
                console.print(e.getMessage());
            }
        }
    }

    /**
     * Creates application's url in HTML format.
     *
     * @param application
     *         current application
     * @return url
     */
    private String getAppUrlsAsString(ApplicationInstance application) {
        String appUris = "";
        UrlBuilder builder = new UrlBuilder();
        String uri = builder.setProtocol("http").setHost(application.getHost()).buildString();
        appUris += ", " + "<a href=\"" + uri + "\" target=\"_blank\">" + uri + "</a>";
        return appUris;
    }

    /**
     * Run application in debug mode by sending request over WebSocket or HTTP.
     *
     * @param warUrl
     *         location of .war file
     */
    private void debugApplication(@NotNull String warUrl) {
        DtoClientImpls.ApplicationInstanceImpl applicationInstance = DtoClientImpls.ApplicationInstanceImpl.make();
        ApplicationInstanceUnmarshallerWS unmarshaller = new ApplicationInstanceUnmarshallerWS(applicationInstance);

        try {
            applicationRunnerClientService
                    .debugApplicationWS(project.getName(), warUrl, isUseJRebel(), new RequestCallback<ApplicationInstance>(unmarshaller) {
                        @Override
                        protected void onSuccess(ApplicationInstance result) {
                            // Need this temporary fix because with using
                            // websocket we get stopURL like:
                            // ide/java/runner/stop?name=app-zcuz5b5wawcn5u23
                            // but it must be like:
                            // http://127.0.0.1:8080/IDE/rest/private/ide/java/runner/stop?name=app-8gkiomg9q4qrhkxz
                            if (!result.getStopURL().matches("http[s]?://.+/ide/rest/.*/stop\\?name=.+")) {
                                String fixedStopURL = Window.Location.getProtocol() + "//" + Window.Location.getHost() + restContext + "/" +
                                                      result.getStopURL();
                                ((DtoClientImpls.ApplicationInstanceImpl)result).setStopURL(fixedStopURL);
                            }

                            onDebugStarted(result);
                        }

                        @Override
                        protected void onFailure(Throwable exception) {
                            onApplicationStartFailure(exception);
                        }
                    });
        } catch (WebSocketException e) {
            debugApplicationREST(warUrl);
        }
    }

    /**
     * Perform some action when debug is started.
     *
     * @param app
     *         current application
     */
    private void onDebugStarted(@NotNull ApplicationInstance app) {
        String msg = constant.applicationStarted(app.getName());
        msg += "<br>"
               + constant.applicationStartedOnUrls(app.getName(), getAppUrlsAsString(app));
        console.print(msg);
        connectDebugger(app);
        runningApp = app;

        try {
            expireSoonAppChannel = JavaRuntimeExtension.EXPIRE_SOON_APP_CHANNEL + runningApp.getName();
            messageBus.subscribe(expireSoonAppChannel, expireSoonAppsHandler);
        } catch (WebSocketException e) {
            // nothing to do
        }
    }

    /**
     * Connect to debugger.
     *
     * @param debugApplicationInstance
     *         current application
     */
    private void connectDebugger(@NotNull final ApplicationInstance debugApplicationInstance) {
        DtoClientImpls.DebuggerInfoImpl debuggerInfo = DtoClientImpls.DebuggerInfoImpl.make();
        DebuggerInfoUnmarshaller unmarshaller = new DebuggerInfoUnmarshaller(debuggerInfo);

        try {
            service.connect(debugApplicationInstance.getDebugHost(), debugApplicationInstance.getDebugPort(),
                            new AsyncRequestCallback<DebuggerInfo>(unmarshaller) {
                                @Override
                                public void onSuccess(DebuggerInfo result) {
                                    DebuggerPresenter.this.debuggerInfo = result;
                                    showDialog(result);
                                }

                                @Override
                                protected void onFailure(Throwable exception) {
                                    reconnectDebugger(debugApplicationInstance);
                                }
                            });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }

    /**
     * Reconnect debugger.
     *
     * @param debugApplicationInstance
     *         current application
     */
    private void reconnectDebugger(@NotNull ApplicationInstance debugApplicationInstance) {
        reLaunchDebuggerPresenter.showDialog(debugApplicationInstance, new AsyncCallback<DebuggerInfo>() {
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

    /** Disconnect debugger. */
    private void doDisconnectDebugger() {
        if (debuggerInfo != null) {
            stopCheckingEvents();
            try {
                service.disconnect(debuggerInfo.getId(), new AsyncRequestCallback<String>() {
                    @Override
                    protected void onSuccess(String result) {
                        enableButtons(false);
                        debuggerInfo = null;
                        gutterManager.unmarkCurrentBreakPoint();
                        closeDialog();
                        doStopApp();
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        console.print(exception.getMessage());
                    }
                });

            } catch (RequestException e) {
                console.print(e.getMessage());
            }
        } else {
            enableButtons(false);
            gutterManager.unmarkCurrentBreakPoint();
            doStopApp();
        }
    }

    /**
     * Run application in debug mode by sending request over HTTP.
     *
     * @param warUrl
     *         location of .war file
     */
    private void debugApplicationREST(@NotNull String warUrl) {
        DtoClientImpls.ApplicationInstanceImpl applicationInstance = DtoClientImpls.ApplicationInstanceImpl.make();
        ApplicationInstanceUnmarshaller unmarshaller = new ApplicationInstanceUnmarshaller(applicationInstance);

        try {
            applicationRunnerClientService.debugApplication(project.getName(), warUrl, isUseJRebel(),
                                                            new AsyncRequestCallback<ApplicationInstance>(unmarshaller) {
                                                                @Override
                                                                protected void onSuccess(ApplicationInstance result) {
                                                                    onDebugStarted(result);
                                                                }

                                                                @Override
                                                                protected void onFailure(Throwable exception) {
                                                                    onApplicationStartFailure(exception);
                                                                }
                                                            });
        } catch (RequestException e) {
            onApplicationStartFailure(e);
        }
    }

    /**
     * Show message about fail application start.
     *
     * @param exception
     *         problem which happened
     */
    private void onApplicationStartFailure(@NotNull Throwable exception) {
        String msg = constant.startApplicationFailed();
        if (exception.getMessage() != null) {
            msg += " : " + exception.getMessage();
        }
        console.print(msg);
    }

    /**
     * Whether to use JRebel feature for the current project.
     *
     * @return <code>true</code> if need to use JRebel
     */
    private boolean isUseJRebel() {
        Property property = project.getProperty(JREBEL);
        if (property != null) {
            JsonArray<String> value = property.getValue();
            if (value != null && !value.isEmpty()) {
                if (value.get(0) != null) {
                    return Boolean.parseBoolean(value.get(0));
                }
            }
        }
        return false;
    }

    /** Debugs application. */
    public void debugApplication() {
        project = resourceProvider.getActiveProject();
        // TODO IDEX-57
        // Replace EventBus Events with direct method calls and DI
        projectBuildHandler = eventBus.addHandler(ProjectBuiltEvent.TYPE, this);
        eventBus.fireEvent(new BuildProjectEvent(project));
    }

    /** Updates application. */
    public void updateApplication() {
        // TODO this method will be used for JRebel
        project = resourceProvider.getActiveProject();
        updateApp = true;
        onRemoveAllBreakpointsButtonClicked();

        // TODO IDEX-57
        // Replace EventBus Events with direct method calls and DI
        projectBuildHandler = eventBus.addHandler(ProjectBuiltEvent.TYPE, this);
        eventBus.fireEvent(new BuildProjectEvent(project));
    }

    /**
     * Show dialog.
     *
     * @param debuggerInfo
     *         information about current debugger
     */
    private void showDialog(@NotNull DebuggerInfo debuggerInfo) {
        String vmName = debuggerInfo.getVmName() + " " + debuggerInfo.getVmVersion();
        view.setVMName(vmName);
        selectedVariable = null;
        updateChangeValueButton();
        enableButtons(false);

        workspaceAgent.openPart(this, PartStackType.INFORMATION);
        PartPresenter activePart = partStack.getActivePart();
        if (activePart != null && !activePart.equals(this)) {
            partStack.setActivePart(this);
        }

        debuggerConnected(debuggerInfo);
        startCheckingEvents();
    }

    /** Close dialog. */
    private void closeDialog() {
        variables.clear();
        workspaceAgent.hidePart(this);
        workspaceAgent.removePart(this);
    }

    /** Update breakpoints. */
    public void updateBreakPoint() {
        JsonArray<Breakpoint> breakPoints = gutterManager.getBreakPoints();
        view.setBreakPoints(breakPoints);
    }

    /** Perform some action when debugger is connected. */
    public void debuggerConnected(@NotNull DebuggerInfo debuggerInfo) {
        this.debuggerInfo = debuggerInfo;
    }

    /** Perform some action when debugger is disconnected. */
    public void debuggerDisconnected() {
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
    public File getFileWithBreakPoints(@NotNull String fqn) {
        return fileWithBreakPoints.get(fqn);
    }

    /** {@inheritDoc} */
    @Override
    public void addBreakPoint(@NotNull final File file, final int lineNumber, final AsyncCallback<Breakpoint> callback)
            throws RequestException {
        if (debuggerInfo != null) {
            DtoClientImpls.LocationImpl location = DtoClientImpls.LocationImpl.make();
            location.setLineNumber(lineNumber + 1);
            final FqnResolver resolver = resolverFactory.getResolver(file.getMimeType());
            if (resolver != null) {
                location.setClassName(resolver.resolveFqn(file));
            } else {
                Log.warn(DebuggerPresenter.class, "FqnResolver is not found");
            }

            final DtoClientImpls.BreakPointImpl point = DtoClientImpls.BreakPointImpl.make();
            point.setLocation(location);
            point.setIsEnabled(true);

            try {
                service.addBreakPoint(debuggerInfo.getId(), point, new AsyncRequestCallback<BreakPoint>() {
                    @Override
                    protected void onSuccess(BreakPoint result) {
                        if (resolver != null) {
                            String fqn = resolver.resolveFqn(file);
                            fileWithBreakPoints.put(fqn, file);
                            Breakpoint breakpoint = new Breakpoint(Breakpoint.Type.BREAKPOINT, lineNumber, fqn);
                            callback.onSuccess(breakpoint);
                        }
                        updateBreakPoint();
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
            DtoClientImpls.LocationImpl location = DtoClientImpls.LocationImpl.make();
            location.setLineNumber(lineNumber);
            FqnResolver resolver = resolverFactory.getResolver(file.getMimeType());
            if (resolver != null) {
                location.setClassName(resolver.resolveFqn(file));
            } else {
                Log.warn(DebuggerPresenter.class, "FqnResolver is not found");
            }

            final DtoClientImpls.BreakPointImpl point = DtoClientImpls.BreakPointImpl.make();
            point.setLocation(location);
            point.setIsEnabled(true);

            try {
                service.deleteBreakPoint(debuggerInfo.getId(), point, new AsyncRequestCallback<BreakPoint>() {
                    @Override
                    protected void onSuccess(BreakPoint result) {
                        callback.onSuccess(null);
                        updateBreakPoint();
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