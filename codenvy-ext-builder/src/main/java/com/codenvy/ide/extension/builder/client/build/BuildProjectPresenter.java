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
import com.codenvy.api.builder.dto.BuildOptions;
import com.codenvy.api.builder.dto.BuildTaskDescriptor;
import com.codenvy.api.builder.gwt.client.BuilderServiceClient;
import com.codenvy.api.builder.internal.Constants;
import com.codenvy.api.core.rest.shared.dto.Link;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.api.ui.workspace.WorkspaceAgent;
import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.extension.builder.client.BuilderLocalizationConstant;
import com.codenvy.ide.extension.builder.client.console.BuilderConsolePresenter;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.StringUnmarshaller;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.websocket.Message;
import com.codenvy.ide.websocket.MessageBus;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.SubscriptionHandler;
import com.codenvy.ide.websocket.rest.Unmarshallable;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.List;

import static com.codenvy.ide.api.notification.Notification.Status.FINISHED;
import static com.codenvy.ide.api.notification.Notification.Status.PROGRESS;
import static com.codenvy.ide.api.notification.Notification.Type.ERROR;
import static com.codenvy.ide.api.notification.Notification.Type.INFO;

/**
 * Controls building application.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class BuildProjectPresenter implements Notification.OpenNotificationHandler {

    private static final String BUILDER_STATUS_CHANNEL = "builder:status:";
    private static final String BUILDER_OUTPUT_CHANNEL = "builder:output:";
    protected final ResourceProvider                         resourceProvider;
    protected final BuilderConsolePresenter                  console;
    protected final BuilderServiceClient                     service;
    protected final BuilderLocalizationConstant              constant;
    protected final WorkspaceAgent                           workspaceAgent;
    protected final MessageBus                               messageBus;
    protected final NotificationManager                      notificationManager;
    protected final DtoFactory                               dtoFactory;
    protected final DtoUnmarshallerFactory                   dtoUnmarshallerFactory;
    /** Handler for processing Maven build status which is received over WebSocket connection. */
    protected       SubscriptionHandler<BuildTaskDescriptor> buildStatusHandler;
    protected       SubscriptionHandler<String>              buildOutputHandler;
    /** Build of another project is performed. */
    protected boolean isBuildInProgress = false;
    /** Project for build. */
    protected Project      projectToBuild;
    protected Notification notification;

    /** Create presenter. */
    @Inject
    protected BuildProjectPresenter(ResourceProvider resourceProvider,
                                    BuilderConsolePresenter console,
                                    BuilderServiceClient service,
                                    BuilderLocalizationConstant constant,
                                    WorkspaceAgent workspaceAgent,
                                    MessageBus messageBus,
                                    NotificationManager notificationManager,
                                    DtoFactory dtoFactory,
                                    DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        this.workspaceAgent = workspaceAgent;
        this.resourceProvider = resourceProvider;
        this.console = console;
        this.service = service;
        this.constant = constant;
        this.messageBus = messageBus;
        this.notificationManager = notificationManager;
        this.dtoFactory = dtoFactory;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
    }

    /** Build active project. */
    public void buildActiveProject() {
        buildActiveProject(null);
    }

    /**
     * Build active project, specifying options to configure build process.
     *
     * @param buildOptions
     *         options to configure build process
     */
    public void buildActiveProject(BuildOptions buildOptions) {
        if (isBuildInProgress) {
            final String message = constant.buildInProgress(projectToBuild.getName());
            Notification notification = new Notification(message, ERROR);
            notificationManager.showNotification(notification);
            return;
        }

        projectToBuild = resourceProvider.getActiveProject();

        service.build(projectToBuild.getPath(),
                      buildOptions,
                      new AsyncRequestCallback<BuildTaskDescriptor>(dtoUnmarshallerFactory.newUnmarshaller(BuildTaskDescriptor.class)) {
                          @Override
                          protected void onSuccess(BuildTaskDescriptor result) {
                              if (result.getStatus() == BuildStatus.SUCCESSFUL) {
                                  final String message = constant.buildFinished(projectToBuild.getName());
                                  notification = new Notification(message, FINISHED, BuildProjectPresenter.this);
                                  notificationManager.showNotification(notification);
                              } else {
                                  setBuildInProgress(true);
                                  final String message = constant.buildStarted(projectToBuild.getName());
                                  notification = new Notification(message, PROGRESS, BuildProjectPresenter.this);
                                  notificationManager.showNotification(notification);
                                  startCheckingStatus(result);
                                  startCheckingOutput(result);
                              }
                          }

                          @Override
                          protected void onFailure(Throwable exception) {
                              setBuildInProgress(false);
                              notification.setStatus(FINISHED);
                              notification.setType(ERROR);
                              notification.setMessage(exception.getMessage());
                          }
                      }
                     );
    }

    private void startCheckingStatus(final BuildTaskDescriptor buildTaskDescriptor) {
        buildStatusHandler =
                new SubscriptionHandler<BuildTaskDescriptor>(dtoUnmarshallerFactory.newWSUnmarshaller(BuildTaskDescriptor.class)) {
                    @Override
                    protected void onMessageReceived(BuildTaskDescriptor result) {
                        updateBuildStatus(result);
                    }

                    @Override
                    protected void onErrorReceived(Throwable exception) {
                        setBuildInProgress(false);
                        try {
                            messageBus.unsubscribe(BUILDER_STATUS_CHANNEL + buildTaskDescriptor.getTaskId(), this);
                            Log.error(BuildProjectPresenter.class, exception);
                        } catch (WebSocketException e) {
                            Log.error(BuildProjectPresenter.class, e);
                        }
                        notification.setType(ERROR);
                        notification.setStatus(FINISHED);
                        notification.setMessage(exception.getMessage());
                    }
                };

        try {
            messageBus.subscribe(BUILDER_STATUS_CHANNEL + buildTaskDescriptor.getTaskId(), buildStatusHandler);
        } catch (WebSocketException e) {
            Log.error(BuildProjectPresenter.class, e);
        }
    }

    private void updateBuildStatus(BuildTaskDescriptor descriptor) {
        switch (descriptor.getStatus()) {
            case SUCCESSFUL:
            case CANCELLED:
            case FAILED:
                afterBuildFinished(descriptor);
                break;
        }
    }

    private void startCheckingOutput(final BuildTaskDescriptor buildTaskDescriptor) {
        buildOutputHandler = new SubscriptionHandler<String>(new LineUnmarshaller()) {
            @Override
            protected void onMessageReceived(String result) {
                console.print(result);
            }

            @Override
            protected void onErrorReceived(Throwable throwable) {
                try {
                    messageBus.unsubscribe(BUILDER_OUTPUT_CHANNEL + buildTaskDescriptor.getTaskId(), this);
                    Log.error(BuildProjectPresenter.class, throwable);
                } catch (WebSocketException e) {
                    Log.error(BuildProjectPresenter.class, e);
                }
            }
        };

        try {
            messageBus.subscribe(BUILDER_OUTPUT_CHANNEL + buildTaskDescriptor.getTaskId(), buildOutputHandler);
        } catch (WebSocketException e) {
            Log.error(BuildProjectPresenter.class, e);
        }
    }

    private void setBuildInProgress(boolean buildInProgress) {
        isBuildInProgress = buildInProgress;
    }

    /** Perform actions after build is finished. */
    private void afterBuildFinished(BuildTaskDescriptor descriptor) {
        try {
            messageBus.unsubscribe(BUILDER_STATUS_CHANNEL + descriptor.getTaskId(), buildStatusHandler);
        } catch (Exception e) {
            Log.error(BuildProjectPresenter.class, e);
        }

        setBuildInProgress(false);
        final String message = constant.buildFinished(projectToBuild.getName());
        notification.setStatus(FINISHED);
        notification.setMessage(message);

        if (descriptor.getStatus() == BuildStatus.SUCCESSFUL) {
            notificationManager.showNotification(new Notification(constant.buildSuccess(), INFO, this));
            console.print(message);
        } else if (descriptor.getStatus() == BuildStatus.FAILED) {
            notificationManager.showNotification(new Notification(constant.buildFailed(), ERROR, this));
            console.print(constant.buildFailed());
        }

        if (descriptor.getStatus() == BuildStatus.SUCCESSFUL) {
            List<Link> links = descriptor.getLinks();
            for (Link link : links) {
                if (link.getRel().equalsIgnoreCase(Constants.LINK_REL_DOWNLOAD_RESULT)) {
                    console.print(constant.downloadArtifact(link.getHref()));
                }
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

        service.log(statusLink, new AsyncRequestCallback<String>(new StringUnmarshaller()) {
            @Override
            protected void onSuccess(String result) {
                console.print(result);
            }

            @Override
            protected void onFailure(Throwable exception) {
                String msg = constant.failGetBuildResult();
                console.print(msg);
                Notification notification = new Notification(msg, ERROR);
                notificationManager.showNotification(notification);
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onOpenClicked() {
        workspaceAgent.setActivePart(console);
    }

    private class LineUnmarshaller implements Unmarshallable<String> {
        private String line;

        @Override
        public void unmarshal(Message response) throws UnmarshallerException {
            JSONObject jsonObject = JSONParser.parseStrict(response.getBody()).isObject();
            if (jsonObject == null) {
                return;
            }
            if (jsonObject.containsKey("line")) {
                line = jsonObject.get("line").isString().stringValue();
            }
        }

        @Override
        public String getPayload() {
            return line;
        }
    }

}