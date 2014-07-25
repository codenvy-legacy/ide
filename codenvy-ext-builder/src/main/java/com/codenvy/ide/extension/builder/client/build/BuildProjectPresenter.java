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
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.ide.api.AppContext;
import com.codenvy.ide.api.build.BuildContext;
import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.event.ProjectActionEvent;
import com.codenvy.ide.api.event.ProjectActionHandler;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.ui.workspace.WorkspaceAgent;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.extension.builder.client.BuilderExtension;
import com.codenvy.ide.extension.builder.client.BuilderLocalizationConstant;
import com.codenvy.ide.extension.builder.client.console.BuilderConsolePresenter;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.ui.dialogs.ask.Ask;
import com.codenvy.ide.ui.dialogs.ask.AskHandler;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.websocket.MessageBus;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.SubscriptionHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
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

    protected final AppContext                               appContext;
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
    protected ProjectDescriptor   activeProject;
    protected Notification        notification;
    private   BuildContext        buildContext;
    /** Descriptor of the last build task. */
    private   BuildTaskDescriptor lastBuildTaskDescriptor;
    private   BuilderMetric       lastWaitingTimeLimit;
    private   EditorAgent         editorAgent;

    @Inject
    protected BuildProjectPresenter(EventBus eventBus,
                                    WorkspaceAgent workspaceAgent,
                                    AppContext appContext,
                                    final BuilderConsolePresenter console,
                                    BuilderServiceClient service,
                                    BuilderLocalizationConstant constant,
                                    NotificationManager notificationManager,
                                    DtoFactory dtoFactory,
                                    EditorAgent editorAgent,
                                    DtoUnmarshallerFactory dtoUnmarshallerFactory,
                                    MessageBus messageBus,
                                    BuildContext buildContext) {
        this.workspaceAgent = workspaceAgent;
        this.appContext = appContext;
        this.console = console;
        this.service = service;
        this.constant = constant;
        this.notificationManager = notificationManager;
        this.dtoFactory = dtoFactory;
        this.editorAgent = editorAgent;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.messageBus = messageBus;
        this.buildContext = buildContext;

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
        });
    }

    @Override
    public void onOpenClicked() {
        workspaceAgent.setActivePart(console);
    }

    /** Build active project.
     * @param isUserAction points whether the build is started directly by user interaction
     */
    public void buildActiveProject(final boolean isUserAction) {
        //Save the files before building if necessary
        Array<EditorPartPresenter> dirtyEditors = editorAgent.getDirtyEditors();
        if (dirtyEditors.isEmpty()) {
            buildActiveProject(null, isUserAction);
        } else {
            Ask askWindow = new Ask(constant.titlePromptSaveFiles(), constant.messagePromptSaveFiles(), new AskHandler() {
                @Override
                public void onOk() {
                    editorAgent.saveAll(new AsyncCallback() {
                        @Override
                        public void onFailure(Throwable caught) {
                            Log.error(getClass(), caught.getMessage());
                        }

                        @Override
                        public void onSuccess(Object result) {
                            buildActiveProject(null, isUserAction);
                        }
                    });
                }

                @Override
                public void onCancel() {
                    buildActiveProject(null, isUserAction);
                }
            });
            askWindow.show();
        }
    }

    /**
     * Build active project, specifying options to configure build process.
     *
     * @param buildOptions
     *         options to configure build process
     * @param isUserAction points whether the build is started directly by user interaction        
     */
    public void buildActiveProject(BuildOptions buildOptions, boolean isUserAction) {
        if (isBuildInProgress) {
            final String message = constant.buildInProgress(activeProject.getName());
            Notification notification = new Notification(message, ERROR);
            notificationManager.showNotification(notification);
            return;
        }

        lastBuildTaskDescriptor = null;
        activeProject = appContext.getCurrentProject().getProjectDescription();

        notification = new Notification(constant.buildStarted(activeProject.getName()), PROGRESS, BuildProjectPresenter.this);
        notificationManager.showNotification(notification);
        buildContext.setBuilding(true);
        if (isUserAction){
            console.setActive();
        }
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
                              buildContext.setBuilding(false);
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
                        buildContext.setBuilding(false);
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
                buildContext.setBuilding(false);
                break;
            case FAILED:
                isBuildInProgress = false;
                stopCheckingStatus();

                notification.setStatus(FINISHED);
                notification.setType(ERROR);
                notification.setMessage(constant.buildFailed());
                buildContext.setBuilding(false);
                break;
            case CANCELLED:
                isBuildInProgress = false;
                stopCheckingStatus();

                notification.setStatus(FINISHED);
                notification.setType(WARNING);
                notification.setMessage(constant.buildCanceled());
                buildContext.setBuilding(false);
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
        BuilderMetric builderMetric = getBuilderMetric("runningTime");
        if (builderMetric != null && builderMetric.getValue() != null) {
            //The value is the number of seconds (example: 4.000s):
            int ss = (int)getNumber(builderMetric.getValue());
            int mm = 0;
            if (ss >= 60) {
                mm = ss / 60;
                ss = ss % 60;
            }
            int hh = 0;
            if (mm >= 60) {
                hh = mm / 60;
                mm = mm % 60;
            }
            StringBuilder value = new StringBuilder();
            value.append(hh > 9 ? hh : "0" + hh).append("h:");
            value.append(mm > 9 ? mm : "0" + mm).append("m:");
            value.append(ss > 9 ? ss : "0" + ss).append("s");
            return dtoFactory.createDto(BuilderMetric.class).withName(builderMetric.getName()).withDescription(builderMetric.getDescription()).withValue(value.toString());
        }
        return builderMetric;
    }
    
    /**
     * Parses given string to find the decimal number.
     * @param str input to parse 
     * @return decimal number
     */
    private native float getNumber(String str) /*-{
        var pattern = new RegExp("[0-9]+(\.[0-9]+)?");
        var result = pattern.exec(str);
        if (result != null && result.length > 0){
            return parseFloat(result[0]);
        }
        return 0;
    }-*/;

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