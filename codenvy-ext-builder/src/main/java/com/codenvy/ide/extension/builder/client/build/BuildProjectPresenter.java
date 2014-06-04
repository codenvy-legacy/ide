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
package com.codenvy.ide.extension.builder.client.build;

import com.codenvy.api.builder.BuildStatus;
import com.codenvy.api.builder.dto.BuildOptions;
import com.codenvy.api.builder.dto.BuildTaskDescriptor;
import com.codenvy.api.builder.dto.BuilderMetric;
import com.codenvy.api.builder.gwt.client.BuilderServiceClient;
import com.codenvy.api.builder.internal.Constants;
import com.codenvy.api.core.rest.shared.dto.Link;
import com.codenvy.ide.api.event.ProjectActionEvent;
import com.codenvy.ide.api.event.ProjectActionHandler;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.api.ui.workspace.WorkspaceAgent;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.extension.builder.client.BuilderExtension;
import com.codenvy.ide.extension.builder.client.BuilderLocalizationConstant;
import com.codenvy.ide.extension.builder.client.console.BuilderConsolePresenter;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.websocket.MessageBus;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.SubscriptionHandler;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import javax.annotation.Nullable;
import java.util.List;

import static com.codenvy.ide.api.notification.Notification.Status.FINISHED;
import static com.codenvy.ide.api.notification.Notification.Status.PROGRESS;
import static com.codenvy.ide.api.notification.Notification.Type.ERROR;
import static com.codenvy.ide.api.notification.Notification.Type.INFO;
import static com.codenvy.ide.api.notification.Notification.Type.WARNING;

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
    protected       SubscriptionHandler<LogMessage>          buildOutputHandler;
    /** Whether any build is performed now? */
    protected boolean isBuildInProgress = false;
    protected Project             activeProject;
    protected Notification        notification;
    /** Descriptor of the last build task. */
    private   BuildTaskDescriptor lastBuildTaskDescriptor;
    private   BuilderMetric       lastWaitingTimeLimit;

    @Inject
    protected BuildProjectPresenter(EventBus eventBus,
                                    WorkspaceAgent workspaceAgent,
                                    ResourceProvider resourceProvider,
                                    final BuilderConsolePresenter console,
                                    BuilderServiceClient service,
                                    BuilderLocalizationConstant constant,
                                    NotificationManager notificationManager,
                                    DtoFactory dtoFactory,
                                    DtoUnmarshallerFactory dtoUnmarshallerFactory,
                                    MessageBus messageBus) {
        this.workspaceAgent = workspaceAgent;
        this.resourceProvider = resourceProvider;
        this.console = console;
        this.service = service;
        this.constant = constant;
        this.notificationManager = notificationManager;
        this.dtoFactory = dtoFactory;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.messageBus = messageBus;

        eventBus.addHandler(ProjectActionEvent.TYPE, new ProjectActionHandler() {
            @Override
            public void onProjectOpened(ProjectActionEvent event) {
            }

            @Override
            public void onProjectClosed(ProjectActionEvent event) {
                console.clear();
                activeProject = null;
                lastBuildTaskDescriptor = null;
            }

            @Override
            public void onProjectDescriptionChanged(ProjectActionEvent event) {
            }
        });
    }

    @Override
    public void onOpenClicked() {
        workspaceAgent.setActivePart(console);
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
            final String message = constant.buildInProgress(activeProject.getName());
            Notification notification = new Notification(message, ERROR);
            notificationManager.showNotification(notification);
            return;
        }

        lastBuildTaskDescriptor = null;
        activeProject = resourceProvider.getActiveProject();

        notification = new Notification(constant.buildStarted(activeProject.getName()), PROGRESS, BuildProjectPresenter.this);
        notificationManager.showNotification(notification);

        service.build(activeProject.getPath(),
                      buildOptions,
                      new AsyncRequestCallback<BuildTaskDescriptor>(dtoUnmarshallerFactory.newUnmarshaller(BuildTaskDescriptor.class)) {
                          @Override
                          protected void onSuccess(BuildTaskDescriptor result) {
                              if (result.getStatus() == BuildStatus.SUCCESSFUL) {
                                  // if project wasn't changed from the last build,
                                  // we get result immediately without re-build
                                  onBuildStatusUpdated(result);
                              } else {
                                  lastBuildTaskDescriptor = result;
                                  isBuildInProgress = true;
                                  startCheckingStatus(result);
                                  startCheckingOutput(result);
                              }
                          }

                          @Override
                          protected void onFailure(Throwable exception) {
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
                        lastBuildTaskDescriptor = result;
                        onBuildStatusUpdated(result);
                    }

                    @Override
                    protected void onErrorReceived(Throwable exception) {
                        isBuildInProgress = false;
                        try {
                            messageBus.unsubscribe(BuilderExtension.BUILD_STATUS_CHANNEL + buildTaskDescriptor.getTaskId(), this);
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

    private void stopCheckingStatus() {
        try {
            messageBus.unsubscribe(BuilderExtension.BUILD_STATUS_CHANNEL + lastBuildTaskDescriptor.getTaskId(), buildStatusHandler);
        } catch (WebSocketException e) {
            Log.error(BuildProjectPresenter.class, e);
        }
    }

    private void startCheckingOutput(BuildTaskDescriptor buildTaskDescriptor) {
        buildOutputHandler = new LogMessagesHandler(buildTaskDescriptor, console, messageBus);
        try {
            messageBus.subscribe(BuilderExtension.BUILD_OUTPUT_CHANNEL + buildTaskDescriptor.getTaskId(), buildOutputHandler);
        } catch (WebSocketException e) {
            Log.error(BuildProjectPresenter.class, e);
        }
    }

    /** Process changing build status. */
    private void onBuildStatusUpdated(BuildTaskDescriptor descriptor) {
        switch (descriptor.getStatus()) {
            case SUCCESSFUL:
                isBuildInProgress = false;
                stopCheckingStatus();

                notification.setStatus(FINISHED);
                notification.setType(INFO);
                notification.setMessage(constant.buildFinished(activeProject.getName()));

                workspaceAgent.setActivePart(console);
                break;
            case FAILED:
                isBuildInProgress = false;
                stopCheckingStatus();

                notification.setStatus(FINISHED);
                notification.setType(ERROR);
                notification.setMessage(constant.buildFailed());

                workspaceAgent.setActivePart(console);
                break;
            case CANCELLED:
                isBuildInProgress = false;
                stopCheckingStatus();

                notification.setStatus(FINISHED);
                notification.setType(WARNING);
                notification.setMessage(constant.buildCanceled());

                workspaceAgent.setActivePart(console);
                break;
        }
    }

    /** Returns link to download result of last build task. */
    @Nullable
    public String getLastBuildResultURL() {
        if (lastBuildTaskDescriptor != null) {
            Link downloadResultLink = getLink(lastBuildTaskDescriptor, Constants.LINK_REL_DOWNLOAD_RESULT);
            if (downloadResultLink != null) {
                return downloadResultLink.getHref();
            }
        }
        return null;
    }

    /** Returns startTime {@link BuilderMetric}. */
    @Nullable
    public BuilderMetric getLastBuildStartTime() {
        return getBuilderMetric("startTime");
    }

    /** Returns waitingTimeLimit {@link BuilderMetric}. */
    @Nullable
    public BuilderMetric getLastBuildTimeoutThreshold() {
        if (lastBuildTaskDescriptor == null) {
            return null;
        }
        BuilderMetric waitingTimeLimit = getBuilderMetric("waitingTimeLimit");
        if (waitingTimeLimit != null) {
            lastWaitingTimeLimit = waitingTimeLimit;
        }
        return lastWaitingTimeLimit;
    }

    /** Returns endTime {@link BuilderMetric}. */
    @Nullable
    public BuilderMetric getLastBuildEndTime() {
        return getBuilderMetric("endTime");
    }

    /** Returns runningTime {@link BuilderMetric}. */
    @Nullable
    public BuilderMetric getLastBuildRunningTime() {
        return getBuilderMetric("runningTime");
    }

    @Nullable
    private BuilderMetric getBuilderMetric(String metricName) {
        if (lastBuildTaskDescriptor != null) {
            for (BuilderMetric buildStat : lastBuildTaskDescriptor.getBuildStats()) {
                if (metricName.equals(buildStat.getName())) {
                    return buildStat;
                }
            }
        }
        return null;
    }

    /** Returns last build task's status. */
    @Nullable
    public String getLastBuildStatus() {
        if (lastBuildTaskDescriptor != null) {
            return lastBuildTaskDescriptor.getStatus().toString();
        }
        return null;
    }

    @Nullable
    private static Link getLink(BuildTaskDescriptor descriptor, String rel) {
        List<Link> links = descriptor.getLinks();
        for (Link link : links) {
            if (link.getRel().equalsIgnoreCase(rel))
                return link;
        }
        return null;
    }

}