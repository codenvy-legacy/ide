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
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import java.util.Date;
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
    /** Name of the builder used to build last task. It need only to display on console's statistics panel. */
    private   String              lastBuildBuilderName;

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

        if (buildOptions != null && buildOptions.getBuilderName() != null && !buildOptions.getBuilderName().isEmpty()) {
            lastBuildBuilderName = buildOptions.getBuilderName();
        } else {
            lastBuildBuilderName = activeProject.getAttributeValue(Constants.BUILDER_NAME);
        }

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
    public String getLastBuildResultURL() {
        if (lastBuildTaskDescriptor != null) {
            Link downloadResultLink = getLink(lastBuildTaskDescriptor, Constants.LINK_REL_DOWNLOAD_RESULT);
            if (downloadResultLink != null) {
                return downloadResultLink.getHref();
            }
        }
        return null;
    }

    /** Returns time when last build task started in format HH:mm:ss. */
    public String getLastBuildStartTime() {
        if (lastBuildTaskDescriptor != null && lastBuildTaskDescriptor.getStartTime() > 0) {
            final Date startDate = new Date(lastBuildTaskDescriptor.getStartTime());
            return DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.HOUR24_MINUTE_SECOND).format(startDate);
        }
        return null;
    }

    /** Returns time when last build task finished in format HH:mm:ss. */
    public String getLastBuildEndTime() {
        if (lastBuildTaskDescriptor != null && lastBuildTaskDescriptor.getEndTime() > 0) {
            final Date endDate = new Date(lastBuildTaskDescriptor.getEndTime());
            return DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.HOUR24_MINUTE_SECOND).format(endDate);
        }
        return null;
    }

    /** Returns total build time in format mm:ss.ms. */
    public String getLastBuildTotalTime() {
        if (lastBuildTaskDescriptor != null && lastBuildTaskDescriptor.getEndTime() > 0) {
            final long totalTimeMs = lastBuildTaskDescriptor.getEndTime() - lastBuildTaskDescriptor.getStartTime();
            int ms = (int)(totalTimeMs % 1000);
            int ss = (int)(totalTimeMs / 1000);
            int mm = 0;
            if (ss > 60) {
                mm = ss / 60;
                ss = ss % 60;
            }
            return String.valueOf("" + getDoubleDigit(mm) + ':' + getDoubleDigit(ss) + '.' + ms);
        }
        return null;
    }

    /** Returns name of the builder used to build last task. */
    public String getLastBuildConfiguration() {
        if (lastBuildTaskDescriptor != null) {
            return lastBuildBuilderName;
        }
        return null;
    }

    private static Link getLink(BuildTaskDescriptor descriptor, String rel) {
        List<Link> links = descriptor.getLinks();
        for (Link link : links) {
            if (link.getRel().equalsIgnoreCase(rel))
                return link;
        }
        return null;
    }

    /** Get a double digit int from a single, e.g.: 1 = "01", 2 = "02". */
    private static String getDoubleDigit(int i) {
        final String doubleDigitI;
        switch (i) {
            case 0:
                doubleDigitI = "00";
                break;
            case 1:
                doubleDigitI = "01";
                break;
            case 2:
                doubleDigitI = "02";
                break;
            case 3:
                doubleDigitI = "03";
                break;
            case 4:
                doubleDigitI = "04";
                break;
            case 5:
                doubleDigitI = "05";
                break;
            case 6:
                doubleDigitI = "06";
                break;
            case 7:
                doubleDigitI = "07";
                break;
            case 8:
                doubleDigitI = "08";
                break;
            case 9:
                doubleDigitI = "09";
                break;
            default:
                doubleDigitI = Integer.toString(i);
        }
        return doubleDigitI;
    }
}