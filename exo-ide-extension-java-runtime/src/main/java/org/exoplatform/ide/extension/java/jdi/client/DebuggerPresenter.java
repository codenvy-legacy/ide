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
package org.exoplatform.ide.extension.java.jdi.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.event.CursorPosition;
import org.exoplatform.ide.client.framework.event.OpenFileEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.project.ProjectProperties;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.client.framework.websocket.WebSocketException;
import org.exoplatform.ide.client.framework.websocket.rest.AutoBeanUnmarshallerWS;
import org.exoplatform.ide.client.framework.websocket.rest.RequestCallback;
import org.exoplatform.ide.client.framework.websocket.rest.SubscriptionHandler;
import org.exoplatform.ide.extension.java.jdi.client.events.AppStartedEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.AppStoppedEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.AppStoppedHandler;
import org.exoplatform.ide.extension.java.jdi.client.events.BreakPointsUpdatedEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.BreakPointsUpdatedHandler;
import org.exoplatform.ide.extension.java.jdi.client.events.ChangeValueEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.DebugAppEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.DebugAppHandler;
import org.exoplatform.ide.extension.java.jdi.client.events.DebuggerActivityEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.DebuggerConnectedEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.DebuggerConnectedHandler;
import org.exoplatform.ide.extension.java.jdi.client.events.DebuggerDisconnectedEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.DebuggerDisconnectedHandler;
import org.exoplatform.ide.extension.java.jdi.client.events.EvaluateExpressionEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.JRebelUserInfoEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.RunAppEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.RunAppHandler;
import org.exoplatform.ide.extension.java.jdi.client.events.StopAppEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.StopAppHandler;
import org.exoplatform.ide.extension.java.jdi.client.events.UpdateAppEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.UpdateAppHandler;
import org.exoplatform.ide.extension.java.jdi.client.events.UpdateVariableValueInTreeEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.UpdateVariableValueInTreeHandler;
import org.exoplatform.ide.extension.java.jdi.client.ui.DebuggerView;
import org.exoplatform.ide.extension.java.jdi.client.ui.RunDebuggerView;
import org.exoplatform.ide.extension.java.jdi.shared.ApplicationInstance;
import org.exoplatform.ide.extension.java.jdi.shared.BreakPoint;
import org.exoplatform.ide.extension.java.jdi.shared.BreakPointEvent;
import org.exoplatform.ide.extension.java.jdi.shared.DebuggerEvent;
import org.exoplatform.ide.extension.java.jdi.shared.DebuggerEventList;
import org.exoplatform.ide.extension.java.jdi.shared.DebuggerInfo;
import org.exoplatform.ide.extension.java.jdi.shared.Location;
import org.exoplatform.ide.extension.java.jdi.shared.StackFrameDump;
import org.exoplatform.ide.extension.java.jdi.shared.StepEvent;
import org.exoplatform.ide.extension.java.jdi.shared.Variable;
import org.exoplatform.ide.extension.maven.client.event.BuildProjectEvent;
import org.exoplatform.ide.extension.maven.client.event.ProjectBuiltEvent;
import org.exoplatform.ide.extension.maven.client.event.ProjectBuiltHandler;
import org.exoplatform.ide.extension.maven.shared.BuildStatus;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ItemUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.ItemWrapper;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Property;
import org.exoplatform.ide.vfs.shared.PropertyImpl;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.web.bindery.autobean.shared.AutoBean;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class DebuggerPresenter implements DebuggerConnectedHandler, DebuggerDisconnectedHandler,
                              ViewClosedHandler, BreakPointsUpdatedHandler, RunAppHandler, DebugAppHandler,
                              ProjectBuiltHandler, StopAppHandler, UpdateAppHandler, AppStoppedHandler, ProjectClosedHandler,
                              ProjectOpenedHandler, EditorActiveFileChangedHandler, UpdateVariableValueInTreeHandler {

    private Display                 display;

    private DebuggerInfo            debuggerInfo;

    private CurrentEditorBreakPoint currentBreakPoint;

    private ApplicationInstance     runningApp;

    private BreakpointsManager      breakpointsManager;

    private boolean                 startDebugger;

    private boolean                 updateApp;

    private FileModel               activeFile;

    private ProjectModel            project;

    /** Default time (in milliseconds) to prolong application expiration time. */
    private static long             DEFAULT_PROLONG_TIME     = 10 * 60 * 1000; // 10 minutes

    /** Name of 'JRebel' project property. */
    private static final String     JREBEL                   = "jrebel";

    /** Max number of JRebel updating. */
    private final byte              MAX_NUMBER_JREBEL_UPDATE = 5;
    /** Value of 'JRebel update' project property if user data  forward to ZeroTurnaround. */
    private final byte              JREBEL_UPDATED           = 0;

    /** Channel identifier to receive events from debugger over WebSocket. */
    private String                  debuggerEventsChannel;

    /** Channel identifier to receive event when debugger will disconnected. */
    private String                  debuggerDisconnectedChannel;

    /** Channel identifier to receive events when application expiration time will left. */
    private String                  expireSoonAppChannel;

    /** Channel identifier to receive events when application stop. */
    private String                  applicationStoppedChannel;


    /** Used to check if events from debugger receiving over WebSocket or over HTTP. */
    private boolean                 isCheckEventsTimerRunned;

    public interface Display extends IsView {

        HasClickHandlers getResumeButton();

        HasClickHandlers getRemoveAllBreakpointsButton();

        HasClickHandlers getDisconnectButton();

        HasClickHandlers getStepIntoButton();

        HasClickHandlers getStepOverButton();

        HasClickHandlers getStepReturnButton();

        HasClickHandlers getChangeValueButton();

        HasClickHandlers getEvaluateExpressionButton();

        Variable getSelectedVariable();

        List<Variable> getVariables();

        void setBreakPoints(List<BreakPoint> breakPoints);

        void setVariables(List<Variable> variables);

        void setEnableResumeButton(boolean isEnable);

        void setRemoveAllBreakpointsButton(boolean isEnable);

        void setDisconnectButton(boolean isEnable);

        void setStepIntoButton(boolean isEnable);

        void setStepOverButton(boolean isEnable);

        void setStepReturnButton(boolean isEnable);

        void setChangeValueButtonEnable(boolean isEnable);

        void setEvaluateExpressionButtonEnable(boolean isEnable);

        void showExpirationDialog(BooleanValueReceivedHandler handler);

        void closeExpirationDialog();
    }

    public DebuggerPresenter(BreakpointsManager breakpointsManager) {
        this.breakpointsManager = breakpointsManager;
    }

    void bindDisplay(Display d) {
        this.display = d;

        display.getResumeButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                doResume();
            }

        });

        display.getStepIntoButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                doStepInto();
            }
        });

        display.getStepOverButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                doStepOver();
            }
        });

        display.getStepReturnButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                doStepReturn();
            }
        });

        display.getDisconnectButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                doDisconnectDebugger();
                // doStopApp();
            }
        });

        display.getRemoveAllBreakpointsButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                doRemoveAllBreakPoints();
            }
        });

        display.getChangeValueButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.fireEvent(new ChangeValueEvent(debuggerInfo, display.getSelectedVariable()));
            }
        });

        display.getEvaluateExpressionButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.fireEvent(new EvaluateExpressionEvent(debuggerInfo));
            }
        });

        disableButtons();
    }

    private void doResume() {
        disableButtons();
        try {
            DebuggerClientService.getInstance().resume(debuggerInfo.getId(), new AsyncRequestCallback<String>() {

                @Override
                protected void onSuccess(String result) {
                    resetStates();
                }

                @Override
                protected void onFailure(Throwable exception) {
                    IDE.fireEvent(new ExceptionThrownEvent(exception));
                }

            });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    private void doStepInto() {
        disableButtons();
        try {
            DebuggerClientService.getInstance().stepInto(debuggerInfo.getId(), new AsyncRequestCallback<String>() {

                @Override
                protected void onSuccess(String result) {
                    resetStates();
                }

                @Override
                protected void onFailure(Throwable exception) {
                    IDE.fireEvent(new ExceptionThrownEvent(exception));
                }

            });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    private void doStepOver() {
        disableButtons();
        try {
            DebuggerClientService.getInstance().stepOver(debuggerInfo.getId(), new AsyncRequestCallback<String>() {

                @Override
                protected void onSuccess(String result) {
                    resetStates();
                }

                @Override
                protected void onFailure(Throwable exception) {
                    IDE.fireEvent(new ExceptionThrownEvent(exception));
                }

            });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    private void doStepReturn() {
        disableButtons();
        try {
            DebuggerClientService.getInstance().stepReturn(debuggerInfo.getId(), new AsyncRequestCallback<String>() {

                @Override
                protected void onSuccess(String result) {
                    resetStates();
                }

                @Override
                protected void onFailure(Throwable exception) {
                    IDE.fireEvent(new ExceptionThrownEvent(exception));
                }

            });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    private void doDisconnectDebugger() {
        if (debuggerInfo != null) {
            stopCheckingEvents();
            try {
                DebuggerClientService.getInstance().disconnect(debuggerInfo.getId(), new AsyncRequestCallback<String>() {

                    @Override
                    protected void onSuccess(String result) {
                        disableButtons();
                        debuggerInfo = null;
                        breakpointsManager.unmarkCurrentBreakPoint(currentBreakPoint);
                        currentBreakPoint = null;
                        IDE.eventBus().fireEvent(new DebuggerDisconnectedEvent());
                        doStopApp();
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        IDE.fireEvent(new ExceptionThrownEvent(exception));
                    }
                });

            } catch (RequestException e) {
                IDE.fireEvent(new ExceptionThrownEvent(e));
            }
        } else {
            doStopApp();
        }
    }

    private void doGetDump() {
        AutoBean<StackFrameDump> autoBean = DebuggerExtension.AUTO_BEAN_FACTORY.create(StackFrameDump.class);
        AutoBeanUnmarshaller<StackFrameDump> unmarshaller = new AutoBeanUnmarshaller<StackFrameDump>(autoBean);
        try {
            DebuggerClientService.getInstance().dump(debuggerInfo.getId(),
                                                     new AsyncRequestCallback<StackFrameDump>(unmarshaller) {

                                                         @Override
                                                         protected void onSuccess(StackFrameDump result) {
                                                             List<Variable> variables = new ArrayList<Variable>(result.getFields());
                                                             if (result.getLocalVariables() != null) {
                                                                 variables.addAll(result.getLocalVariables());
                                                             }
                                                             display.setVariables(variables);
                                                         }

                                                         @Override
                                                         protected void onFailure(Throwable exception) {
                                                             IDE.eventBus().fireEvent(new ExceptionThrownEvent(exception));
                                                         }
                                                     });
        } catch (RequestException e) {
            IDE.eventBus().fireEvent(new ExceptionThrownEvent(e));
        }
    }

    @Override
    public void onDebuggerConnected(DebuggerConnectedEvent event) {
        debuggerInfo = event.getDebuggerInfo();
        if (display == null) {
            display = new DebuggerView(debuggerInfo);
            bindDisplay(display);
            IDE.getInstance().openView(display.asView());
            startCheckingEvents();
        }
    }

    private void disableButtons() {
        if (display != null) {
            IDE.fireEvent(new DebuggerActivityEvent(true));
            display.setEnableResumeButton(false);
            display.setStepIntoButton(false);
            display.setStepOverButton(false);
            display.setStepReturnButton(false);
            display.setEvaluateExpressionButtonEnable(false);
        }
    }

    private void enableButtons() {
        if (display != null) {
            IDE.fireEvent(new DebuggerActivityEvent(false));
            display.setEnableResumeButton(true);
            display.setStepIntoButton(true);
            display.setStepOverButton(true);
            display.setStepReturnButton(true);
            display.setEvaluateExpressionButtonEnable(true);
        }
    }

    /** A timer for checking events */
    private Timer checkEventsTimer = new Timer() {
                                       @Override
                                       public void run() {
                                           AutoBean<DebuggerEventList> debuggerEventList =
                                                                                           DebuggerExtension.AUTO_BEAN_FACTORY.create(DebuggerEventList.class);
                                           DebuggerEventListUnmarshaller unmarshaller =
                                                                                        new DebuggerEventListUnmarshaller(
                                                                                                                          debuggerEventList.as());
                                           try {
                                               DebuggerClientService.getInstance()
                                                                    .checkEvents(debuggerInfo.getId(),
                                                                                 new AsyncRequestCallback<DebuggerEventList>(unmarshaller) {
                                                                                     @Override
                                                                                     protected void onSuccess(DebuggerEventList result) {
                                                                                         onEventListReceived(result);
                                                                                     }

                                                                                     @Override
                                                                                     protected void onFailure(Throwable exception) {
                                                                                         cancel();
                                                                                         if (display != null)
                                                                                             IDE.getInstance().closeView(display.asView()
                                                                                                                                .getId());
                                                                                         if (runningApp != null) {
                                                                                             if (exception instanceof ServerException) {
                                                                                                 ServerException serverException =
                                                                                                                                   (ServerException)exception;
                                                                                                 if (HTTPStatus.INTERNAL_ERROR ==
                                                                                                     serverException.getHTTPStatus()
                                                                                                     && serverException.getMessage() != null
                                                                                                     && serverException.getMessage()
                                                                                                                       .contains("not found")) {
                                                                                                     IDE.fireEvent(new OutputEvent(
                                                                                                                                   DebuggerExtension.LOCALIZATION_CONSTANT
                                                                                                                                                                          .debuggerDisconnected(),
                                                                                                                                   Type.WARNING));
                                                                                                     IDE.fireEvent(new AppStoppedEvent(
                                                                                                                                       runningApp.getName(),
                                                                                                                                       false));
                                                                                                     return;
                                                                                                 }
                                                                                             }
                                                                                             IDE.fireEvent(new ExceptionThrownEvent(
                                                                                                                                    exception));
                                                                                         }
                                                                                     }
                                                                                 });
                                           } catch (RequestException e) {
                                               IDE.fireEvent(new ExceptionThrownEvent(e));
                                           }
                                       }
                                   };

    /**
     * Start checking events from debugger. Subscribes on WebSocket channel or starts timer for checking events over HTTP.
     */
    private void startCheckingEvents() {
        debuggerEventsChannel = DebuggerExtension.EVENTS_CHANNEL + debuggerInfo.getId();
        debuggerDisconnectedChannel = DebuggerExtension.DISCONNECT_CHANNEL + debuggerInfo.getId();
        try {
            IDE.messageBus().subscribe(debuggerEventsChannel, debuggerEventsHandler);
            IDE.messageBus().subscribe(debuggerDisconnectedChannel, debuggerDisconnectedHandler);
        } catch (WebSocketException e) {
            checkEventsTimer.scheduleRepeating(3000);
            isCheckEventsTimerRunned = true;
        }
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
                if (IDE.messageBus().isHandlerSubscribed(debuggerEventsHandler, debuggerEventsChannel))
                    IDE.messageBus().unsubscribe(debuggerEventsChannel, debuggerEventsHandler);

                if (IDE.messageBus().isHandlerSubscribed(expireSoonAppsHandler, expireSoonAppChannel))
                    IDE.messageBus().unsubscribe(expireSoonAppChannel, expireSoonAppsHandler);
            } catch (WebSocketException e) {
                // nothing to do
            }
        }
    }

    /**
     * Performs actions when event list was received.
     * 
     * @param eventList debugger event list
     */
    private void onEventListReceived(DebuggerEventList eventList) {
        String filePath = null;
        System.out.println("DebuggerPresenter.onEventListReceived()" + eventList.getEvents().size());
        if (eventList != null && eventList.getEvents().size() > 0) {
            Location location;
            for (DebuggerEvent event : eventList.getEvents()) {
                if (event instanceof StepEvent) {
                    StepEvent stepEvent = (StepEvent)event;
                    location = stepEvent.getLocation();
                    filePath = resolveFilePath(location);
                    if (!filePath.equalsIgnoreCase(activeFile.getPath())) {
                        openFile(location);
                    }
                    currentBreakPoint = new CurrentEditorBreakPoint(location.getLineNumber(), "BreakPoint", filePath);
                } else if (event instanceof BreakPointEvent) {
                    BreakPointEvent breakPointEvent = (BreakPointEvent)event;
                    location = breakPointEvent.getBreakPoint().getLocation();
                    filePath = resolveFilePath(location);
                    if (!filePath.equalsIgnoreCase(activeFile.getPath())) {
                        openFile(location);
                    }
                    currentBreakPoint = new CurrentEditorBreakPoint(location.getLineNumber(), "BreakPoint", filePath);
                }
                doGetDump();
                enableButtons();
            }
            if (filePath != null && filePath.equalsIgnoreCase(activeFile.getPath())) {
                breakpointsManager.markCurrentBreakPoint(currentBreakPoint);
            }
        }
    }

    private void openFile(final Location location) {
        FileModel fileModel = breakpointsManager.getFileWithBreakPoints().get(location.getClassName());
        if (fileModel == null) {
            String path = resolveFilePath(location);
            try {
                FileModel openFileModel = new FileModel();
                openFileModel.setProject(project);
                VirtualFileSystem.getInstance()
                                 .getItemByPath(path,
                                                new AsyncRequestCallback<ItemWrapper>(
                                                                                      new ItemUnmarshaller(new ItemWrapper(openFileModel))) {

                                                    @Override
                                                    protected void onSuccess(ItemWrapper result) {
                                                        IDE.eventBus()
                                                           .fireEvent(
                                                                      new OpenFileEvent((FileModel)result.getItem(),
                                                                                        new CursorPosition(
                                                                                                           location.getLineNumber())));
                                                    }

                                                    @Override
                                                    protected void onFailure(Throwable exception) {
                                                        Dialogs.getInstance().showInfo("Source not found",
                                                                                       "Can't load source of the " +
                                                                                           location.getClassName() + " class.");
                                                    }
                                                });
            } catch (RequestException e) {
                IDE.fireEvent(new ExceptionThrownEvent(e));
            }

        } else {
            IDE.eventBus().fireEvent(new OpenFileEvent(fileModel, new CursorPosition(location.getLineNumber())));
        }
    }

    private String resolveFilePath(final Location location) {
        String sourcePath =
                            project.hasProperty("sourceFolder") ? (String)project.getPropertyValue("sourceFolder") : "src/main/java";
        String path = project.getPath() + "/" + sourcePath + "/" + location.getClassName().replace(".", "/") + ".java";
        return path;
    }

    public void reconnectDebugger(ApplicationInstance debugApplicationInstance) {
        ReLaunchDebuggerPresenter runDebuggerPresenter = new ReLaunchDebuggerPresenter(debugApplicationInstance);
        RunDebuggerView view = new RunDebuggerView();
        runDebuggerPresenter.bindDisplay(view);
        IDE.getInstance().openView(view.asView());
    }

    /**
     * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     *      .event.ViewClosedEvent)
     */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

    /**
     * @see org.exoplatform.ide.extension.java.jdi.client.events.DebuggerDisconnectedHandler#onDebuggerDisconnected(org.exoplatform.ide
     *      .extension.java.jdi.client.events.DebuggerDisconnectedEvent)
     */
    @Override
    public void onDebuggerDisconnected(DebuggerDisconnectedEvent event) {
        if (display != null) {
            display.closeExpirationDialog();
            IDE.getInstance().closeView(display.asView().getId());
        }
    }

    /**
     * @see org.exoplatform.ide.extension.java.jdi.client.events.BreakPointsUpdatedHandler#onBreakPointsUpdated(org.exoplatform.ide
     *      .extension.java.jdi.client.events.BreakPointsUpdatedEvent)
     */
    @Override
    public void onBreakPointsUpdated(BreakPointsUpdatedEvent event) {
        if (event.getBreakPoints() != null) {
            List<BreakPoint> breakPoints = new ArrayList<BreakPoint>();
            Collection<Set<EditorBreakPoint>> values = event.getBreakPoints().values();
            for (Set<EditorBreakPoint> ebps : values) {
                for (EditorBreakPoint editorBreakPoint : ebps) {
                    breakPoints.add(editorBreakPoint.getBreakPoint());
                }
            }
            display.setBreakPoints(breakPoints);
        }
    }

    /**
     * @see org.exoplatform.ide.extension.java.jdi.client.events.RunAppHandler#onRunApp(org.exoplatform.ide.extension.java.jdi.client
     *      .events.RunAppEvent)
     */
    @Override
    public void onRunApp(RunAppEvent event) {
        IDE.addHandler(ProjectBuiltEvent.TYPE, this);
        startDebugger = false;
        IDE.fireEvent(new BuildProjectEvent());
    }

    /**
     * @see org.exoplatform.ide.extension.java.jdi.client.events.DebugAppHandler#onDebugApp(org.exoplatform.ide.extension.java.jdi
     *      .client.events.DebugAppEvent)
     */
    @Override
    public void onDebugApp(DebugAppEvent event) {
        IDE.addHandler(ProjectBuiltEvent.TYPE, this);
        startDebugger = true;
        IDE.fireEvent(new BuildProjectEvent());
    }

    /**
     * @see org.exoplatform.ide.extension.java.jdi.client.events.UpdateAppHandler#onUpdateApp(org.exoplatform.ide.extension.java.jdi
     *      .client.events.UpdateAppEvent)
     */
    @Override
    public void onUpdateApp(UpdateAppEvent event) {
        IDE.addHandler(ProjectBuiltEvent.TYPE, this);
        updateApp = true;
        if (startDebugger) {
            doRemoveAllBreakPoints();
        }
        IDE.fireEvent(new BuildProjectEvent());
    }

    /**
     * @see org.exoplatform.ide.extension.maven.client.event.ProjectBuiltHandler#onProjectBuilt(org.exoplatform.ide.extension.maven
     *      .client.event.ProjectBuiltEvent)
     */
    @Override
    public void onProjectBuilt(ProjectBuiltEvent event) {
        if (IDE.eventBus().isEventHandled(ProjectBuiltEvent.TYPE)) {
            IDE.eventBus().removeHandler(ProjectBuiltEvent.TYPE, this);
        }
        BuildStatus buildStatus = event.getBuildStatus();
        if (buildStatus.getStatus().equals(BuildStatus.Status.SUCCESSFUL)) {
            IDE.eventBus().fireEvent(
                                     new OutputEvent(DebuggerExtension.LOCALIZATION_CONSTANT.applicationStarting(), Type.INFO));
            if (updateApp) {
                updateApp = false;
                if (writeJRebelCountProperty(project)) {
                    updateApplication(buildStatus.getDownloadUrl());
                }
            } else {
                startApplication(buildStatus.getDownloadUrl());
            }
        }
    }

    /**
     * Writes 'jrebelCount' property to the project properties.
     * 
     * @param project {@link ProjectModel}
     */
    private boolean writeJRebelCountProperty(ProjectModel project) {
        if (project.getPropertyValue(ProjectProperties.JREBEL_COUNT.value()) == null) {
            project.getProperties().add(new PropertyImpl(ProjectProperties.JREBEL_COUNT.value(), Integer.toString(1)));
        }
        else {
            int countJRebel = Integer.parseInt(project.getPropertyValue(ProjectProperties.JREBEL_COUNT.value()));
            if (countJRebel == JREBEL_UPDATED) {
                return true;
            } else if (countJRebel < MAX_NUMBER_JREBEL_UPDATE) {
                countJRebel++;
            }
            else {
                IDE.fireEvent(new JRebelUserInfoEvent());
                return false;
            }
            for (Property prop : project.getProperties()) {
                if (prop.getName().equals(ProjectProperties.JREBEL_COUNT.value())) {
                    List<String> value = new ArrayList<String>();
                    value.add(Integer.toString(countJRebel));
                    prop.setValue(value);
                    break;
                }
            }
        }
        try {
            VirtualFileSystem.getInstance().updateItem(project, null, new AsyncRequestCallback<ItemWrapper>() {

                @Override
                protected void onSuccess(ItemWrapper result) {
                    // nothing to do
                }

                @Override
                protected void onFailure(Throwable ignore) {
                    // ignore this exception
                }
            });
        } catch (RequestException e) {
            // ignore this exception
        }
        return true;
    }

    private void startApplication(String url) {
        if (startDebugger) {
            debugApplication(url);
        } else {
            runApplication(url);
        }
    }

    /**
     * Run application in debug mode by sending request over WebSocket or HTTP.
     * 
     * @param warUrl location of .war file
     */
    private void debugApplication(String warUrl) {
        AutoBean<ApplicationInstance> debugApplicationInstance =
                                                                 DebuggerExtension.AUTO_BEAN_FACTORY.debugApplicationInstance();
        AutoBeanUnmarshallerWS<ApplicationInstance> unmarshaller =
                                                                   new AutoBeanUnmarshallerWS<ApplicationInstance>(debugApplicationInstance);

        try {
            ApplicationRunnerClientService.getInstance().debugApplicationWS(project.getName(), warUrl, isUseJRebel(),
                                                                            new RequestCallback<ApplicationInstance>(unmarshaller) {
                                                                                @Override
                                                                                protected void onSuccess(ApplicationInstance result) {
                                                                                    String fixedStopURL =  Utils.getRestContext() + result.getStopURL();
                                                                                    result.setStopURL(fixedStopURL);
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
     * Run application in debug mode by sending request over HTTP.
     * 
     * @param warUrl location of .war file
     */
    private void debugApplicationREST(String warUrl) {
        AutoBean<ApplicationInstance> debugApplicationInstance =
                                                                 DebuggerExtension.AUTO_BEAN_FACTORY.debugApplicationInstance();
        AutoBeanUnmarshaller<ApplicationInstance> unmarshaller =
                                                                 new AutoBeanUnmarshaller<ApplicationInstance>(debugApplicationInstance);

        try {
            ApplicationRunnerClientService.getInstance().debugApplication(project.getName(), warUrl, isUseJRebel(),
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
            onApplicationStartFailure(null);
        }
    }

    /**
     * Run application by sending request over WebSocket or HTTP.
     * 
     * @param warUrl location of .war file
     */
    private void runApplication(String warUrl) {
        AutoBean<ApplicationInstance> applicationInstance = DebuggerExtension.AUTO_BEAN_FACTORY.applicationInstance();
        AutoBeanUnmarshallerWS<ApplicationInstance> unmarshaller =
                                                                   new AutoBeanUnmarshallerWS<ApplicationInstance>(applicationInstance);

        try {
            ApplicationRunnerClientService.getInstance().runApplicationWS(project.getName(), warUrl, isUseJRebel(),
                                                                          new RequestCallback<ApplicationInstance>(unmarshaller) {
                                                                              @Override
                                                                              protected void onSuccess(ApplicationInstance result) {
                                                                                  String fixedStopURL =  Utils.getRestContext() + result.getStopURL();
                                                                                  result.setStopURL(fixedStopURL);
                                                                                  onApplicationStarted(result);
                                                                              }

                                                                              @Override
                                                                              protected void onFailure(Throwable exception) {
                                                                                  onApplicationStartFailure(exception);
                                                                              }
                                                                          });
        } catch (WebSocketException e) {
            runApplicationREST(warUrl);
        }
    }

    /**
     * Run application by sending request over HTTP.
     * 
     * @param warUrl location of .war file
     */
    private void runApplicationREST(String warUrl) {
        AutoBean<ApplicationInstance> applicationInstance = DebuggerExtension.AUTO_BEAN_FACTORY.applicationInstance();
        AutoBeanUnmarshaller<ApplicationInstance> unmarshaller =
                                                                 new AutoBeanUnmarshaller<ApplicationInstance>(applicationInstance);

        try {
            ApplicationRunnerClientService.getInstance().runApplication(project.getName(), warUrl, isUseJRebel(),
                                                                        new AsyncRequestCallback<ApplicationInstance>(unmarshaller) {
                                                                            @Override
                                                                            protected void onSuccess(ApplicationInstance result) {
                                                                                onApplicationStarted(result);
                                                                            }

                                                                            @Override
                                                                            protected void onFailure(Throwable exception) {
                                                                                onApplicationStartFailure(exception);
                                                                            }
                                                                        });
        } catch (RequestException e) {
            onApplicationStartFailure(null);
        }
    }

    protected void connectDebugger(final ApplicationInstance debugApplicationInstance) {
        AutoBean<DebuggerInfo> debuggerInfo = DebuggerExtension.AUTO_BEAN_FACTORY.create(DebuggerInfo.class);
        AutoBeanUnmarshaller<DebuggerInfo> unmarshaller = new AutoBeanUnmarshaller<DebuggerInfo>(debuggerInfo);
        try {
            DebuggerClientService.getInstance().connect(debugApplicationInstance.getDebugHost(),
                                                        debugApplicationInstance.getDebugPort(),
                                                        new AsyncRequestCallback<DebuggerInfo>(unmarshaller) {
                                                            @Override
                                                            public void onSuccess(DebuggerInfo result) {
                                                                IDE.eventBus().fireEvent(new DebuggerConnectedEvent(result));
                                                            }

                                                            @Override
                                                            protected void onFailure(Throwable exception) {
                                                                reconnectDebugger(debugApplicationInstance);
                                                            }
                                                        });
        } catch (RequestException e) {
            IDE.eventBus().fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /**
     * Update deployed application using JRebel.
     * 
     * @param warUrl URL to download project WAR
     */
    private void updateApplication(String warUrl) {
        if (runningApp != null) {
            try {
                ApplicationRunnerClientService.getInstance().updateApplication(runningApp.getName(), warUrl,
                                                                               new AsyncRequestCallback<Object>() {

                                                                                   @Override
                                                                                   protected void onSuccess(Object result) {
                                                                                       String message =
                                                                                                        DebuggerExtension.LOCALIZATION_CONSTANT
                                                                                                                                               .applicationUpdated(runningApp.getName(),
                                                                                                                                                                   getAppUrlsAsString(
                                                                                                                                                                   runningApp));
                                                                                       IDE.fireEvent(new OutputEvent(
                                                                                                                     message,
                                                                                                                     OutputMessage.Type.JRebel));
                                                                                   }

                                                                                   @Override
                                                                                   protected void onFailure(Throwable exception) {
                                                                                       String message =
                                                                                                        (exception.getMessage() != null)
                                                                                                            ? exception.getMessage()
                                                                                                            : DebuggerExtension.LOCALIZATION_CONSTANT.updateApplicationFailed(
                                                                                                                                                     runningApp.getName());
                                                                                       IDE.fireEvent(new OutputEvent(
                                                                                                                     message,
                                                                                                                     OutputMessage.Type.ERROR));
                                                                                   }
                                                                               });
            } catch (RequestException e) {
                IDE.fireEvent(new ExceptionThrownEvent(e));
            }
        }
    }

    private void onApplicationStarted(ApplicationInstance app) {
        String msg = DebuggerExtension.LOCALIZATION_CONSTANT.applicationStarted(app.getName());
        msg +=
               "<br>"
                   + DebuggerExtension.LOCALIZATION_CONSTANT.applicationStartedOnUrls(app.getName(), getAppUrlsAsString(app));
        IDE.fireEvent(new OutputEvent(msg, OutputMessage.Type.INFO));
        IDE.fireEvent(new AppStartedEvent(app));
        runningApp = app;
        try {
            applicationStoppedChannel = DebuggerExtension.APPLICATION_STOP_CHANNEL + runningApp.getName();
            IDE.messageBus().subscribe(applicationStoppedChannel, applicationStoppedHandler);
        } catch (WebSocketException e) {
            // nothing to do
        }
    }

    private void onApplicationStartFailure(Throwable exception) {
        String msg = DebuggerExtension.LOCALIZATION_CONSTANT.startApplicationFailed();
        if (exception != null && exception.getMessage() != null) {
            msg += " : " + exception.getMessage();
        }
        IDE.fireEvent(new OutputEvent(msg, OutputMessage.Type.ERROR));
    }

    private void onDebugStarted(ApplicationInstance app) {
        String msg = DebuggerExtension.LOCALIZATION_CONSTANT.applicationStarted(app.getName());
        msg +=
               "<br>"
                   + DebuggerExtension.LOCALIZATION_CONSTANT.applicationStartedOnUrls(app.getName(), getAppUrlsAsString(app));
        IDE.fireEvent(new OutputEvent(msg, OutputMessage.Type.INFO));
        connectDebugger(app);
        IDE.fireEvent(new AppStartedEvent(app));
        runningApp = app;

        try {
            expireSoonAppChannel = DebuggerExtension.EXPIRE_SOON_APP_CHANNEL + runningApp.getName();
            IDE.messageBus().subscribe(expireSoonAppChannel, expireSoonAppsHandler);
        } catch (WebSocketException e) {
            // nothing to do
        }
    }

    private String getAppUrlsAsString(ApplicationInstance application) {
        String appUris = "";
        UrlBuilder builder = new UrlBuilder();
        String uri = builder.setProtocol("http").setHost(application.getHost()).buildString();
        appUris += ", " + "<a href=\"" + uri + "\" target=\"_blank\">" + uri + "</a>";
        return appUris;
    }

    @Override
    public void onStopApp(StopAppEvent event) {
        doDisconnectDebugger();
        try {
            IDE.messageBus().unsubscribe(expireSoonAppChannel, applicationStoppedHandler);
        } catch (WebSocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // doStopApp();
    }

    private void doStopApp() {
        if (runningApp != null) {
            try {
                DebuggerClientService.getInstance().stopApplication(runningApp, new AsyncRequestCallback<String>() {

                    @Override
                    protected void onSuccess(String result) {
                        if (runningApp != null)
                            IDE.fireEvent(new AppStoppedEvent(runningApp.getName(), true));
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        String message =
                                         (exception.getMessage() != null) ? exception.getMessage()
                                             : DebuggerExtension.LOCALIZATION_CONSTANT.stopApplicationFailed();
                        IDE.fireEvent(new OutputEvent(message, OutputMessage.Type.WARNING));

                        if (exception instanceof ServerException) {
                            ServerException serverException = (ServerException)exception;
                            if (HTTPStatus.INTERNAL_ERROR == serverException.getHTTPStatus()
                                && serverException.getMessage() != null && serverException.getMessage().contains("not found")
                                && runningApp != null) {
                                IDE.fireEvent(new AppStoppedEvent(runningApp.getName(), false));
                            }
                        }
                    }
                });
            } catch (RequestException e) {
                IDE.fireEvent(new ExceptionThrownEvent(e));
            }
        }
    }

    @Override
    public void onAppStopped(AppStoppedEvent appStopedEvent) {
        String msg = DebuggerExtension.LOCALIZATION_CONSTANT.applicationStoped(appStopedEvent.getAppName());
        IDE.fireEvent(new OutputEvent(msg, OutputMessage.Type.INFO));
        runningApp = null;
    }

    @Override
    public void onProjectClosed(ProjectClosedEvent event) {
        doStopApp();
        project = null;
    }

    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        doStopApp();
        project = event.getProject();
    }

    @Override
    public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event) {
        activeFile = event.getFile();
        if (activeFile == null) {
            return;
        }

        String path = event.getFile().getPath();
        if (currentBreakPoint != null && currentBreakPoint.getFilePath().equals(path)) {
            breakpointsManager.markCurrentBreakPoint(currentBreakPoint);
        }
    }

    private void resetStates() {
        display.setVariables(Collections.<Variable> emptyList());
        breakpointsManager.unmarkCurrentBreakPoint(currentBreakPoint);
        currentBreakPoint = null;
    }


    private void doRemoveAllBreakPoints() {
        try {
            DebuggerClientService.getInstance().deleteAllBreakPoint(debuggerInfo.getId(),
                                                                    new AsyncRequestCallback<String>() {

                                                                        @Override
                                                                        protected void onSuccess(String result) {
                                                                            IDE.fireEvent(new BreakPointsUpdatedEvent(
                                                                                                                      Collections.<String, Set<EditorBreakPoint>> emptyMap()));
                                                                        }

                                                                        @Override
                                                                        protected void onFailure(Throwable exception) {
                                                                            IDE.fireEvent(new ExceptionThrownEvent(exception));
                                                                        }

                                                                    });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** @see org.exoplatform.ide.extension.java.jdi.client.events.UpdateVariableValueInTreeHandler#onUpdateVariableValueInTree(org.exoplatform.ide.extension.java.jdi.client.events.UpdateVariableValueInTreeEvent) */
    @Override
    public void onUpdateVariableValueInTree(UpdateVariableValueInTreeEvent event) {
        Variable variable = event.getVariable();
        String value = event.getValue();

        List<Variable> list = display.getVariables();
        variable.setValue(value);
        int index = list.lastIndexOf(variable);
        list.set(index, variable);
        display.setVariables(list);
    }

    /** Prolong expiration time of the application which is currently runned. */
    private void prolongExpirationTime() {
        try {
            ApplicationRunnerClientService.getInstance().prolongExpirationTime(runningApp.getName(), DEFAULT_PROLONG_TIME,
                                                                               new RequestCallback<Object>() {

                                                                                   @Override
                                                                                   protected void onSuccess(Object result) {
                                                                                       try {
                                                                                           IDE.messageBus()
                                                                                              .subscribe(expireSoonAppChannel,
                                                                                                         expireSoonAppsHandler);
                                                                                       } catch (WebSocketException e) {
                                                                                           // nothing to do
                                                                                       }
                                                                                   }

                                                                                   @Override
                                                                                   protected void onFailure(Throwable exception) {
                                                                                       if (exception.getMessage() == null) {
                                                                                           Dialogs.getInstance()
                                                                                                  .showError(
                                                                                                             DebuggerExtension.LOCALIZATION_CONSTANT
                                                                                                                                                    .prolongExpirationTimeFailed());
                                                                                       } else {
                                                                                           Dialogs.getInstance()
                                                                                                  .showError(exception.getMessage());
                                                                                       }
                                                                                   }
                                                                               });
        } catch (WebSocketException e) {
            Dialogs.getInstance().showError(DebuggerExtension.LOCALIZATION_CONSTANT.prolongExpirationTimeFailed());
        }
    }

    /**
     * Whether to use JRebel feature for the current project.
     * 
     * @return <code>true</code> if need to use JRebel
     */
    private boolean isUseJRebel() {
        Property property = project.getProperty(JREBEL);
        if (property != null) {
            List<String> value = property.getValue();
            if (value != null && !value.isEmpty()) {
                if (value.get(0) != null) {
                    return Boolean.parseBoolean(value.get(0));
                }
            }
        }
        return false;
    }

    /** Handler for processing received application name which will be stopped soon. */
    private SubscriptionHandler<Object>            expireSoonAppsHandler       = new SubscriptionHandler<Object>() {

                                                                                   @Override
                                                                                   public void onSuccess(Object result) {
                                                                                       // unsubscribe to receiving events to avoid receiving
                                                                                       // messages while user not press any button in
                                                                                       // appeared dialog
                                                                                       try {
                                                                                           IDE.messageBus()
                                                                                              .unsubscribe(expireSoonAppChannel, this);
                                                                                       } catch (WebSocketException e) {
                                                                                           // nothing to do
                                                                                       }

                                                                                       display.showExpirationDialog(new BooleanValueReceivedHandler() {
                                                                                           @Override
                                                                                           public void booleanValueReceived(Boolean value) {
                                                                                               if (value) {
                                                                                                   prolongExpirationTime();
                                                                                               } else {
                                                                                                   doDisconnectDebugger();
                                                                                               }
                                                                                           }
                                                                                       });
                                                                                       return;
                                                                                   }

                                                                                   @Override
                                                                                   public void onFailure(Throwable exception) {
                                                                                       try {
                                                                                           IDE.messageBus()
                                                                                              .unsubscribe(expireSoonAppChannel, this);
                                                                                       } catch (WebSocketException e) {
                                                                                           // nothing to do
                                                                                       }
                                                                                   }
                                                                               };

    /** Handler for processing debugger disconnected event. */
    private SubscriptionHandler<Object>            debuggerDisconnectedHandler = new SubscriptionHandler<Object>() {
                                                                                   @Override
                                                                                   protected void onSuccess(Object result) {
                                                                                       try {
                                                                                           IDE.messageBus()
                                                                                              .unsubscribe(debuggerDisconnectedChannel,
                                                                                                           this);
                                                                                       } catch (WebSocketException e) {
                                                                                           // nothing to do
                                                                                       }
                                                                                       if (display != null) {
                                                                                           display.closeExpirationDialog();
                                                                                           IDE.getInstance().closeView(display.asView()
                                                                                                                              .getId());
                                                                                       }
                                                                                       if (runningApp != null) {
                                                                                           IDE.fireEvent(new OutputEvent(
                                                                                                                         DebuggerExtension.LOCALIZATION_CONSTANT.debuggerDisconnected(),
                                                                                                                         Type.INFO));
                                                                                           IDE.fireEvent(new DebuggerDisconnectedEvent());
                                                                                           IDE.fireEvent(new AppStoppedEvent(
                                                                                                                             runningApp.getName(),
                                                                                                                             false));
                                                                                       }
                                                                                   }

                                                                                   @Override
                                                                                   protected void onFailure(Throwable exception) {
                                                                                       try {
                                                                                           IDE.messageBus()
                                                                                              .unsubscribe(debuggerDisconnectedChannel,
                                                                                                           this);
                                                                                       } catch (WebSocketException e) {
                                                                                           // nothing to do
                                                                                       }
                                                                                   }
                                                                               };


    /** Handler for processing debugger disconnected event. */
    private SubscriptionHandler<Object>            applicationStoppedHandler   = new SubscriptionHandler<Object>() {
                                                                                   @Override
                                                                                   protected void onSuccess(Object result) {
                                                                                       try {
                                                                                           IDE.messageBus()
                                                                                              .unsubscribe(applicationStoppedChannel, this);
                                                                                       } catch (WebSocketException e) {
                                                                                           // nothing to do
                                                                                       }

                                                                                       if (display != null)
                                                                                           IDE.getInstance().closeView(display.asView()
                                                                                                                              .getId());
                                                                                       if (runningApp != null) {
                                                                                           // IDE.fireEvent(new
                                                                                           // OutputEvent(DebuggerExtension.LOCALIZATION_CONSTANT.debuggerDisconnected(),
                                                                                           // Type.INFO));
                                                                                           IDE.fireEvent(new AppStoppedEvent(
                                                                                                                             runningApp.getName(),
                                                                                                                             false));
                                                                                       }
                                                                                   }

                                                                                   @Override
                                                                                   protected void onFailure(Throwable exception) {
                                                                                       try {
                                                                                           IDE.messageBus()
                                                                                              .unsubscribe(applicationStoppedChannel, this);
                                                                                       } catch (WebSocketException e) {
                                                                                           // nothing to do
                                                                                       }
                                                                                   }
                                                                               };


    /** Handler for processing events which is received from debugger over WebSocket connection. */
    private SubscriptionHandler<DebuggerEventList> debuggerEventsHandler       =
                                                                                 new SubscriptionHandler<DebuggerEventList>(
                                                                                                                            new DebuggerEventListUnmarshallerWS(
                                                                                                                                                                DebuggerExtension.AUTO_BEAN_FACTORY.create(DebuggerEventList.class)
                                                                                                                                                                                                   .as())) {
                                                                                     @Override
                                                                                     public void onSuccess(DebuggerEventList result) {
                                                                                         onEventListReceived(result);
                                                                                     }

                                                                                     @Override
                                                                                     public void onFailure(Throwable exception) {
                                                                                         try {
                                                                                             IDE.messageBus()
                                                                                                .unsubscribe(debuggerEventsChannel, this);
                                                                                             IDE.messageBus()
                                                                                                .unsubscribe(expireSoonAppChannel,
                                                                                                             expireSoonAppsHandler);
                                                                                         } catch (WebSocketException e) {
                                                                                             // nothing to do
                                                                                         }

                                                                                         IDE.getInstance().closeView(display.asView()
                                                                                                                            .getId());
                                                                                         if (runningApp != null) {
                                                                                             if (exception instanceof org.exoplatform.ide.client.framework.websocket.rest.exceptions.ServerException) {
                                                                                                 org.exoplatform.ide.client.framework.websocket.rest.exceptions.ServerException serverException =
                                                                                                                                                                                                  (org.exoplatform.ide.client.framework.websocket.rest.exceptions.ServerException)exception;
                                                                                                 if (HTTPStatus.INTERNAL_ERROR == serverException.getHTTPStatus()
                                                                                                     && serverException.getMessage() != null
                                                                                                     && serverException.getMessage()
                                                                                                                       .contains("not found")) {
                                                                                                     IDE.fireEvent(new OutputEvent(
                                                                                                                                   DebuggerExtension.LOCALIZATION_CONSTANT.debuggerDisconnected(),
                                                                                                                                   Type.WARNING));
                                                                                                     IDE.fireEvent(new AppStoppedEvent(
                                                                                                                                       runningApp.getName(),
                                                                                                                                       false));
                                                                                                     return;
                                                                                                 }
                                                                                             }
                                                                                             IDE.fireEvent(new ExceptionThrownEvent(
                                                                                                                                    exception));
                                                                                         }
                                                                                     }
                                                                                 };

}
