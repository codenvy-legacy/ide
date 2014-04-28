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
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.IntegerMap;
import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.extension.builder.client.BuilderExtension;
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
    protected       SubscriptionHandler<Line>                buildOutputHandler;
    /** Build of another project is performed. */
    protected boolean isBuildInProgress = false;
    /** Project for build. */
    protected Project      projectToBuild;
    protected Notification notification;
    private IntegerMap<Line> messagesBuffer = Collections.createIntegerMap();
    private int lastMessageNum;

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

        console.clearDownloadLink();
        console.clear();
        projectToBuild = resourceProvider.getActiveProject();
        lastMessageNum = 0;

        notification = new Notification(constant.buildStarted(projectToBuild.getName()), PROGRESS, BuildProjectPresenter.this);
        notificationManager.showNotification(notification);

        service.build(projectToBuild.getPath(),
                      buildOptions,
                      new AsyncRequestCallback<BuildTaskDescriptor>(dtoUnmarshallerFactory.newUnmarshaller(BuildTaskDescriptor.class)) {
                          @Override
                          protected void onSuccess(BuildTaskDescriptor result) {
                              if (result.getStatus() == BuildStatus.SUCCESSFUL) {
                                  notification.setStatus(FINISHED);
                                  notification.setMessage(constant.buildFinished(projectToBuild.getName()));
                              } else {
                                  isBuildInProgress = true;
                                  startCheckingStatus(result);
                                  startCheckingOutput(result);
                              }
                          }

                          @Override
                          protected void onFailure(Throwable exception) {
                              isBuildInProgress = false;
                              notification.setStatus(FINISHED);
                              notification.setType(ERROR);
                              notification.setMessage(constant.buildFailed());
                              console.print(exception.getMessage());
                          }
                      }
                     );
    }

    private void startCheckingStatus(final BuildTaskDescriptor buildTaskDescriptor) {
        buildStatusHandler =
                new SubscriptionHandler<BuildTaskDescriptor>(dtoUnmarshallerFactory.newWSUnmarshaller(BuildTaskDescriptor.class)) {
                    @Override
                    protected void onMessageReceived(BuildTaskDescriptor result) {
                        switch (result.getStatus()) {
                            case SUCCESSFUL:
                            case CANCELLED:
                            case FAILED:
                                afterBuildFinished(result);
                        }
                    }

                    @Override
                    protected void onErrorReceived(Throwable exception) {
                        isBuildInProgress = false;
                        try {
                            messageBus.unsubscribe(BuilderExtension.BUILD_STATUS_CHANNEL + buildTaskDescriptor.getTaskId(), this);
                            messageBus.unsubscribe(BuilderExtension.BUILD_OUTPUT_CHANNEL + buildTaskDescriptor.getTaskId(),
                                                   buildOutputHandler);
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
            messageBus.subscribe(BuilderExtension.BUILD_STATUS_CHANNEL + buildTaskDescriptor.getTaskId(), buildStatusHandler);
        } catch (WebSocketException e) {
            Log.error(BuildProjectPresenter.class, e);
        }
    }

    private void startCheckingOutput(final BuildTaskDescriptor buildTaskDescriptor) {
        buildOutputHandler = new SubscriptionHandler<Line>(new LineUnmarshaller()) {
            @Override
            protected void onMessageReceived(Line result) {
//                if (lastMessageNum == 0 || lastMessageNum == result.num - 1) {
//                    lastMessageNum = result.num;
                console.print(result.text);
//                } else if (lastMessageNum == result.num - 1) {
//                    messagesBuffer.put(result.num, result);
//                }
//
//                Line nextLine = messagesBuffer.get(result.num + 1);
//                while (nextLine != null) {
//                    lastMessageNum = result.num;
//                    console.print(nextLine.text);
//                    messagesBuffer.erase(nextLine.num);
//                    nextLine = messagesBuffer.get(nextLine.num + 1);
//                }
            }

            @Override
            protected void onErrorReceived(Throwable throwable) {
                try {
                    messageBus.unsubscribe(BuilderExtension.BUILD_OUTPUT_CHANNEL + buildTaskDescriptor.getTaskId(), this);
                    Log.error(BuildProjectPresenter.class, throwable);
                } catch (WebSocketException e) {
                    Log.error(BuildProjectPresenter.class, e);
                }
            }
        };

        try {
            messageBus.subscribe(BuilderExtension.BUILD_OUTPUT_CHANNEL + buildTaskDescriptor.getTaskId(), buildOutputHandler);
        } catch (WebSocketException e) {
            Log.error(BuildProjectPresenter.class, e);
        }
    }

    private void afterBuildFinished(BuildTaskDescriptor descriptor) {
        isBuildInProgress = false;
        try {
            messageBus.unsubscribe(BuilderExtension.BUILD_STATUS_CHANNEL + descriptor.getTaskId(), buildStatusHandler);
            messageBus.unsubscribe(BuilderExtension.BUILD_OUTPUT_CHANNEL + descriptor.getTaskId(), buildOutputHandler);
        } catch (Exception e) {
            Log.error(BuildProjectPresenter.class, e);
        }

        notification.setStatus(FINISHED);

        switch (descriptor.getStatus()) {
            case SUCCESSFUL:
                Link downloadResultLink = getAppLink(descriptor, Constants.LINK_REL_DOWNLOAD_RESULT);
                console.setDownloadLink(downloadResultLink.getHref());

                notification.setType(INFO);
                notification.setMessage(constant.buildFinished(projectToBuild.getName()));
                break;
            case FAILED:
                notification.setType(ERROR);
                notification.setMessage(constant.buildFailed());
                break;
            case CANCELLED:
                notification.setType(ERROR);
                notification.setMessage(constant.buildCanceled());
                break;
        }
        workspaceAgent.setActivePart(console);
    }

    private void getBuildLogs(BuildTaskDescriptor descriptor) {
        Link statusLink = getAppLink(descriptor, Constants.LINK_REL_VIEW_LOG);
        service.log(statusLink, new AsyncRequestCallback<String>(new StringUnmarshaller()) {
            @Override
            protected void onSuccess(String result) {
                console.print(result);
            }

            @Override
            protected void onFailure(Throwable exception) {
                String msg = constant.failGetBuildResult();
                console.print(msg);
                notificationManager.showNotification(new Notification(msg, ERROR));
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onOpenClicked() {
        workspaceAgent.setActivePart(console);
    }

    private Link getAppLink(BuildTaskDescriptor descriptor, String rel) {
        List<Link> links = descriptor.getLinks();
        for (Link link : links) {
            if (link.getRel().equalsIgnoreCase(rel))
                return link;
        }
        return null;
    }

    private class LineUnmarshaller implements Unmarshallable<Line> {
        private Line line;

        @Override
        public void unmarshal(Message response) throws UnmarshallerException {
            JSONObject jsonObject = JSONParser.parseStrict(response.getBody()).isObject();
            if (jsonObject == null) {
                return;
            }
            if (jsonObject.containsKey("line")) {
                final int lineNumber = 0/*(int)jsonObject.get("lineNumber").isNumber().doubleValue()*/;
                final String text = jsonObject.get("line").isString().stringValue();
                line = new Line(lineNumber, text);
            }
        }

        @Override
        public Line getPayload() {
            return line;
        }
    }

    private class Line {
        int    num;
        String text;

        Line(int lineNumber, String text) {
            this.num = lineNumber;
            this.text = text;
        }
    }

}