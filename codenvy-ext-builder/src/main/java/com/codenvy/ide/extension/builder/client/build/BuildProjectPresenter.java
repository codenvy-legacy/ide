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
package com.codenvy.ide.extension.builder.client.build;

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.parts.base.BasePresenter;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.workspace.PartPresenter;
import com.codenvy.ide.api.ui.workspace.PartStackType;
import com.codenvy.ide.api.ui.workspace.WorkspaceAgent;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.commons.exception.ServerException;
import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.extension.builder.client.BuilderClientService;
import com.codenvy.ide.extension.builder.client.BuilderExtension;
import com.codenvy.ide.extension.builder.client.BuilderLocalizationConstant;
import com.codenvy.ide.extension.builder.client.BuilderResources;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.RequestStatusHandler;
import com.codenvy.ide.rest.Unmarshallable;
import com.codenvy.ide.websocket.Message;
import com.codenvy.ide.websocket.MessageBus;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.SubscriptionHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import static com.codenvy.ide.api.notification.Notification.Status.FINISHED;
import static com.codenvy.ide.api.notification.Notification.Status.PROGRESS;
import static com.codenvy.ide.api.notification.Notification.Type.ERROR;

/**
 * Presenter for build project with builder.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: BuildProjectPresenter.java Feb 17, 2012 5:39:10 PM azatsarynnyy $
 */
@Singleton
public class BuildProjectPresenter extends BasePresenter implements BuildProjectView.ActionDelegate,
                                                                    Notification.OpenNotificationHandler {
    private final static String TITLE                 = "Output";
    /** Delay in millisecond between requests for build job status. */
    private static final int    delay                 = 3000;
    /** Handler for processing Maven build status which is received over WebSocket connection. */
    private final SubscriptionHandler<String> buildStatusHandler;
    private       BuildProjectView            view;
    /** Identifier of project we want to send for build. */
    private String              projectId         = null;
    /** The builds identifier. */
    private String              buildID           = null;
    /** Status of previously build. */
    /** Build of another project is performed. */
    private boolean             isBuildInProgress = false;
    /** View closed flag. */
    private boolean             isViewClosed      = true;
    /** Project for build. */
    private Project                     projectToBuild;
    private RequestStatusHandler        statusHandler;
    private String                      buildStatusChannel;
    private EventBus                    eventBus;
    private ResourceProvider            resourceProvider;
    private ConsolePart                 console;
    private BuilderClientService        service;
    private BuilderLocalizationConstant constant;
    private BuilderResources            resources;
    private WorkspaceAgent              workspaceAgent;
    private MessageBus                  messageBus;
    private NotificationManager         notificationManager;
    private Notification                notification;

    /** A timer for periodically sending request of build status. */
    private Timer refreshBuildStatusTimer = new Timer() {
        @Override
        public void run() {
            StringUnmarshaller unmarshaller = new StringUnmarshaller();

            try {
                service.status(buildID, new AsyncRequestCallback<String>(unmarshaller) {
                    @Override
                    protected void onSuccess(String response) {

                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        setBuildInProgress(false);
                        notification.setStatus(FINISHED);
                        notification.setMessage(exception.getMessage());
                        notification.setType(ERROR);

                        eventBus.fireEvent(new ExceptionThrownEvent(exception));
                    }
                });
            } catch (RequestException e) {
                setBuildInProgress(false);
                notification.setStatus(FINISHED);
                notification.setMessage(e.getMessage());
                notification.setType(ERROR);
                eventBus.fireEvent(new ExceptionThrownEvent(e));
            }
        }
    };

    /**
     * Create presenter.
     *
     * @param view
     * @param eventBus
     * @param resourceProvider
     * @param console
     * @param service
     * @param constant
     * @param resources
     * @param workspaceAgent
     * @param messageBus
     * @param notificationManager
     */
    @Inject
    protected BuildProjectPresenter(BuildProjectView view, EventBus eventBus, ResourceProvider resourceProvider,
                                    ConsolePart console, BuilderClientService service,
                                    BuilderLocalizationConstant constant, BuilderResources resources,
                                    WorkspaceAgent workspaceAgent, MessageBus messageBus,
                                    NotificationManager notificationManager) {
        this.view = view;
        this.view.setDelegate(this);
        this.view.setTitle(TITLE);
        this.workspaceAgent = workspaceAgent;
        this.eventBus = eventBus;
        this.resourceProvider = resourceProvider;
        this.console = console;
        this.service = service;
        this.constant = constant;
        this.resources = resources;
        this.messageBus = messageBus;
        this.notificationManager = notificationManager;


        StringUnmarshallerWs unmarshaller = new StringUnmarshallerWs();

        buildStatusHandler = new SubscriptionHandler<String>(unmarshaller) {
            @Override
            protected void onMessageReceived(String buildStatus) {
//                updateBuildStatus(buildStatus);
            }

            @Override
            protected void onErrorReceived(Throwable exception) {
                try {
                    BuildProjectPresenter.this.messageBus.unsubscribe(buildStatusChannel, this);
                } catch (WebSocketException e) {
                    // nothing to do
                }

                setBuildInProgress(false);
                notification.setType(ERROR);
                notification.setStatus(FINISHED);
                notification.setMessage(exception.getMessage());

                BuildProjectPresenter.this.eventBus.fireEvent(new ExceptionThrownEvent(exception));
            }
        };


    }


    /**
     * Performs building of project.
     *
     * @param project
     *         project to build. If <code>null</code> - active project will build.
     *
     */
    public void buildProject(Project project) {
        if (isBuildInProgress) {
            String message = constant.buildInProgress(projectToBuild.getPath().substring(1));
            Notification notification = new Notification(message, ERROR);
            notificationManager.showNotification(notification);
            return;
        }

        if (project == null) {
            projectToBuild = resourceProvider.getActiveProject();
        } else {
            projectToBuild = project;
        }

        statusHandler = new BuildRequestStatusHandler(projectToBuild.getPath().substring(1), eventBus, constant);
        doBuild();
    }

    /** Start the build of project. */
    private void doBuild() {
        projectId = projectToBuild.getId();
        statusHandler.requestInProgress(projectId);
        StringUnmarshaller unmarshaller = new StringUnmarshaller();

        try {
            service.build(projectToBuild.getName(),
                          new AsyncRequestCallback<String>(unmarshaller) {
                              @Override
                              protected void onSuccess(String result) {
                                  buildID = result.substring(result.lastIndexOf("/") + 1);
                                  setBuildInProgress(true);
                                  String message =
                                          "Building project <b>" + projectToBuild.getPath().substring(1) + "</b>";
                                  notification = new Notification(message, PROGRESS, BuildProjectPresenter.this);
                                  notificationManager.showNotification(notification);
                                  startCheckingStatus(buildID);
                              }

                              @Override
                              protected void onFailure(Throwable exception) {
                                  statusHandler.requestError(projectId, exception);
                                  setBuildInProgress(false);
                                  notification.setStatus(FINISHED);
                                  notification.setType(ERROR);
                                  notification.setMessage(exception.getMessage());

                                  if (!(exception instanceof ServerException)) {
                                      eventBus.fireEvent(new ExceptionThrownEvent(exception));
                                  }
                              }
                          });
        } catch (RequestException e) {
            setBuildInProgress(false);
            notification.setStatus(FINISHED);
            notification.setType(ERROR);
            notification.setMessage(e.getMessage());
        }
    }

    /**
     * Starts checking job status by subscribing on receiving
     * messages over WebSocket or scheduling task to check status.
     *
     * @param buildId
     *         id of the build job to check status
     */
    private void startCheckingStatus(String buildId) {
        try {
            buildStatusChannel = BuilderExtension.BUILD_STATUS_CHANNEL + buildId;
            messageBus.subscribe(buildStatusChannel, buildStatusHandler);
        } catch (WebSocketException e) {
            refreshBuildStatusTimer.schedule(delay);
        }
    }


    /**
     * Sets build in progress.
     *
     * @param buildInProgress
     */
    private void setBuildInProgress(boolean buildInProgress) {
        isBuildInProgress = buildInProgress;
        view.setClearOutputButtonEnabled(!buildInProgress);
    }


    /**
     * Output the message and activate view if necessary.
     *
     * @param message
     *         message for output
     */
    private void showBuildMessage(String message) {
        view.setClearOutputButtonEnabled(true);
        view.showMessageInOutput(message);
    }

    /** {@inheritDoc} */
    @Override
    public void onClearOutputClicked() {
        view.clearOutput();
        view.setClearOutputButtonEnabled(false);
    }

    /**
     * Formats dependency string in xml.
     *
     * @param dependency
     *         dependency string in xml format
     * @return formatted xml
     */
    private String formatDependencyXml(String dependency) {

        String formatStr = SafeHtmlUtils.htmlEscape(dependency)//
                .replaceFirst("&gt;&lt;", "&gt;<br>&nbsp;&nbsp;&lt;")//
                .replaceFirst("&gt;&lt;", "&gt;<br>&nbsp;&nbsp;&lt;")//
                .replaceFirst("&gt;&lt;", "&gt;<br>&nbsp;&nbsp;&lt;");
        if (formatStr.contains("&lt;type&gt;")) {
            formatStr = formatStr.replaceFirst("&gt;&lt;", "&gt;<br>&nbsp;&nbsp;&lt;");
        }
        return formatStr.replaceFirst("&gt;&lt;", "&gt;<br>&lt;");
    }

    /** {@inheritDoc} */
    @Override
    public String getTitle() {
        return TITLE;
    }

    /** {@inheritDoc} */
    @Override
    public ImageResource getTitleImage() {
        return resources.build();
    }

    /** {@inheritDoc} */
    @Override
    public String getTitleToolTip() {
        return "Displays builder output";
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        view.setClearOutputButtonEnabled(false);
        container.setWidget(view);
    }

    /** {@inheritDoc} */
    @Override
    public void onOpenClicked() {
        if (isViewClosed) {
            workspaceAgent.openPart(this, PartStackType.INFORMATION);
            isViewClosed = false;
        }

        PartPresenter activePart = partStack.getActivePart();
        if (activePart == null || !activePart.equals(this)) {
            partStack.setActivePart(this);
        }
    }

    /** Deserializer for responses body. */
    private class StringUnmarshaller implements Unmarshallable<String> {
        protected String builder;

        /** {@inheritDoc} */
        @Override
        public void unmarshal(Response response) {
            builder = response.getText();
        }

        /** {@inheritDoc} */
        @Override
        public String getPayload() {
            return builder;
        }
    }

    private class StringUnmarshallerWs implements com.codenvy.ide.websocket.rest.Unmarshallable<String> {
        private String payload;

        /** {@inheritDoc} */
        @Override
        public void unmarshal(Message response) throws UnmarshallerException {
            payload = response.getBody();
        }

        /** {@inheritDoc} */
        @Override
        public String getPayload() {
            return payload;
        }
    }
}