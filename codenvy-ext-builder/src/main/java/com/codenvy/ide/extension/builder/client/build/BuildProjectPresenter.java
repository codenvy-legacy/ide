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

import com.codenvy.api.builder.BuildStatus;
import com.codenvy.api.builder.dto.BuildTaskDescriptor;
import com.codenvy.api.core.rest.shared.dto.Link;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.workspace.WorkspaceAgent;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.commons.exception.ServerException;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.extension.builder.client.BuilderClientService;
import com.codenvy.ide.extension.builder.client.BuilderLocalizationConstant;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.RequestStatusHandler;
import com.codenvy.ide.rest.StringUnmarshaller;
import com.codenvy.ide.websocket.MessageBus;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Timer;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import java.util.List;

import static com.codenvy.ide.api.notification.Notification.Status.FINISHED;
import static com.codenvy.ide.api.notification.Notification.Status.PROGRESS;
import static com.codenvy.ide.api.notification.Notification.Type.ERROR;
import static com.codenvy.ide.api.notification.Notification.Type.INFO;

/**
 * Presenter for build project with builder.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: BuildProjectPresenter.java Feb 17, 2012 5:39:10 PM azatsarynnyy $
 */

//TODO: need rework for using websocket wait for server side

@Singleton
public class BuildProjectPresenter implements Notification.OpenNotificationHandler {
    private final static String TITLE = "Output";
    /** Delay in millisecond between requests for build job status. */
    private static final int    delay = 3000;
    /** Handler for processing Maven build status which is received over WebSocket connection. */
//    private final SubscriptionHandler<String> buildStatusHandler;
    private final DtoFactory dtoFactory;

    /** Identifier of project we want to send for build. */
    /** Build of another project is performed. */
    private boolean isBuildInProgress = false;
    /** Project for build. */
    private       Project                     projectToBuild;
    private       RequestStatusHandler        statusHandler;
    //    private       String                      buildStatusChannel;
    private final EventBus                    eventBus;
    private       ResourceProvider            resourceProvider;
    private       ConsolePart                 console;
    private       BuilderClientService        service;
    private       BuilderLocalizationConstant constant;
    private       WorkspaceAgent              workspaceAgent;
    //    private       MessageBus                  messageBus;
    private       NotificationManager         notificationManager;
    private       Notification                notification;

    /**
     * Create presenter.
     *
     * @param eventBus
     * @param resourceProvider
     * @param console
     * @param service
     * @param constant
     * @param workspaceAgent
     * @param messageBus
     * @param notificationManager
     */
    @Inject
    protected BuildProjectPresenter(EventBus eventBus,
                                    ResourceProvider resourceProvider,
                                    ConsolePart console,
                                    BuilderClientService service,
                                    BuilderLocalizationConstant constant,
                                    WorkspaceAgent workspaceAgent,
                                    MessageBus messageBus,
                                    NotificationManager notificationManager,
                                    DtoFactory dtoFactory) {
        this.eventBus = eventBus;
        this.workspaceAgent = workspaceAgent;
        this.resourceProvider = resourceProvider;
        this.console = console;
        this.service = service;
        this.constant = constant;
//        this.messageBus = messageBus;
        this.notificationManager = notificationManager;
        this.dtoFactory = dtoFactory;
    }

    /**
     * Performs building of project.
     *
     * @param project
     *         project to build. If <code>null</code> - active project will build.
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
        statusHandler = new BuildRequestStatusHandler(projectToBuild.getName(), eventBus, constant);
        doBuild();
    }

    /** Start the build of project. */
    private void doBuild() {
        statusHandler.requestInProgress(projectToBuild.getName());
        try {
            service.build(projectToBuild.getPath(),
                          new AsyncRequestCallback<String>(new StringUnmarshaller()) {
                              @Override
                              protected void onSuccess(String result) {
                                  BuildTaskDescriptor btd = dtoFactory.createDtoFromJson(result, BuildTaskDescriptor.class);
                                  startCheckingStatus(btd);
                                  setBuildInProgress(true);
                                  String message = constant.buildStarted(projectToBuild.getName());
                                  notification = new Notification(message, PROGRESS, BuildProjectPresenter.this);
                                  notificationManager.showNotification(notification);
                              }

                              @Override
                              protected void onFailure(Throwable exception) {
                                  statusHandler.requestError(projectToBuild.getName(), exception);
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
     * @param buildTaskDescriptor
     *         the build job to check status
     */
    private void startCheckingStatus(BuildTaskDescriptor buildTaskDescriptor) {
        RefreshBuildStatusTimer statusTimer = new RefreshBuildStatusTimer(buildTaskDescriptor);
        statusTimer.run();
//        try {
//            buildStatusChannel = BuilderExtension.BUILD_STATUS_CHANNEL + buildTaskDescriptor.getTaskId();
//            messageBus.subscribe(buildStatusChannel, buildStatusHandler);
//        } catch (WebSocketException e) {
//        }
    }


    /**
     * Sets build in progress.
     *
     * @param buildInProgress
     */
    private void setBuildInProgress(boolean buildInProgress) {
        isBuildInProgress = buildInProgress;
    }

    /**
     * Check for status and display necessary messages.
     *
     * @param descriptor
     *         status of build
     */
    private void updateBuildStatus(BuildTaskDescriptor descriptor) {
        BuildStatus status = descriptor.getStatus();
        if (status == BuildStatus.IN_PROGRESS || status == BuildStatus.IN_QUEUE) {
            return;
        }
        if (status == BuildStatus.CANCELLED || status == BuildStatus.FAILED || status == BuildStatus.SUCCESSFUL) {
            afterBuildFinished(descriptor);
            return;
        }
    }


    /**
     * Perform actions after build is finished.
     *
     * @param descriptor
     *         status of build job
     */
    private void afterBuildFinished(BuildTaskDescriptor descriptor) {
//        try {
//            messageBus.unsubscribe(buildStatusChannel, buildStatusHandler);
//        } catch (Exception e) {
//            // nothing to do
//        }
        setBuildInProgress(false);
        String message = constant.buildFinished(projectToBuild.getName());
        notification.setStatus(FINISHED);
        notification.setMessage(message.toString());

        if (descriptor.getStatus() == BuildStatus.SUCCESSFUL) {
            Notification notification = new Notification(constant.buildSuccess(), INFO, this);
            notificationManager.showNotification(notification);
            statusHandler.requestFinished(projectToBuild.getName());
            console.print(message.toString());
        } else if (descriptor.getStatus() == BuildStatus.FAILED) {
            Notification notification = new Notification(constant.buildFailed(), ERROR, this);
            notificationManager.showNotification(notification);
            String errorMessage = constant.buildFailed();
            statusHandler.requestError(projectToBuild.getName(), new Exception(errorMessage));
            console.print(errorMessage);
        }
        getBuildLogs(descriptor);
        if (descriptor.getStatus() == BuildStatus.SUCCESSFUL) {
            List<Link> links = descriptor.getLinks();
            for (int i = 0; i < links.size(); i++) {
                Link link = links.get(i);
                if (link.getRel().equalsIgnoreCase("download result"))
                    console.print(constant.downloadArtifact(link.getHref()));
            }
        }
    }

    private void getBuildLogs(BuildTaskDescriptor descriptor) {
        Link statusLink = null;
        List<Link> links = descriptor.getLinks();
        for (int i = 0; i < links.size(); i++) {
            Link link = links.get(i);
            if (link.getRel().equalsIgnoreCase("view build log"))
                statusLink = link;
        }
        try {
            service.log(statusLink, new AsyncRequestCallback<String>(new StringUnmarshaller()) {
                @Override
                protected void onSuccess(String result) {
                    console.printf(result);
                }

                @Override
                protected void onFailure(Throwable exception) {
                    String msg = constant.failGetBuildResult();
                    console.print(msg);
                    Notification notification = new Notification(msg, ERROR);
                    notificationManager.showNotification(notification);
                }
            });
        } catch (RequestException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }



    /** {@inheritDoc} */
    @Override
    public void onOpenClicked() {
        workspaceAgent.setActivePart(console);
    }


    //Temporary solutions wait for server side for reworking to websocket
    private class RefreshBuildStatusTimer extends Timer {
        private BuildTaskDescriptor buildTaskDescriptor;

        public RefreshBuildStatusTimer(BuildTaskDescriptor buildTaskDescriptor) {
            this.buildTaskDescriptor = buildTaskDescriptor;
        }


        @Override
        public void run() {
            Link statusLink = null;
            if (buildTaskDescriptor.getStatus().equals(BuildStatus.IN_QUEUE) ||
                buildTaskDescriptor.getStatus().equals(BuildStatus.IN_PROGRESS)) {
                List<Link> links = buildTaskDescriptor.getLinks();
                for (int i = 0; i < links.size(); i++) {
                    Link link = links.get(i);
                    if (link.getRel().equalsIgnoreCase("get status"))
                        statusLink = link;
                }
            }

            try {
                service.status(statusLink, new AsyncRequestCallback<String>(new StringUnmarshaller()) {
                    @Override
                    protected void onSuccess(String response) {
                        BuildTaskDescriptor btd = dtoFactory.createDtoFromJson(response, BuildTaskDescriptor.class);
                        updateBuildStatus(btd);

                        BuildStatus status = btd.getStatus();
                        if (status == BuildStatus.IN_PROGRESS || status == BuildStatus.IN_QUEUE) {
                            schedule(delay);
                        }
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

    }
}