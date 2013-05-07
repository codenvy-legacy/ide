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
package com.codenvy.ide.extension.maven.client.build;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.workspace.AbstractPartPresenter;
import com.codenvy.ide.api.ui.workspace.PartStackType;
import com.codenvy.ide.api.ui.workspace.WorkspaceAgent;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.commons.exception.ServerException;
import com.codenvy.ide.extension.maven.client.*;
import com.codenvy.ide.extension.maven.client.event.BuildProjectEvent;
import com.codenvy.ide.extension.maven.client.event.BuildProjectHandler;
import com.codenvy.ide.extension.maven.client.event.ProjectBuiltEvent;
import com.codenvy.ide.extension.maven.shared.BuildStatus;
import com.codenvy.ide.extension.maven.shared.BuildStatus.Status;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.ProjectDescription;
import com.codenvy.ide.resources.model.Property;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.AutoBeanUnmarshaller;
import com.codenvy.ide.rest.RequestStatusHandler;
import com.codenvy.ide.rest.Unmarshallable;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.websocket.MessageBus;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.AutoBeanUnmarshallerWS;
import com.codenvy.ide.websocket.rest.SubscriptionHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Presenter for created builder view. The view must be pointed in Views.gwt.xml.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: BuildProjectPresenter.java Feb 17, 2012 5:39:10 PM azatsarynnyy $
 */
@Singleton
public class BuildProjectPresenter extends AbstractPartPresenter implements BuildProjectHandler, BuildProjectView.ActionDelegate {
    private final static String LAST_SUCCESS_BUILD = "lastSuccessBuild";

    private final static String ARTIFACT_DOWNLOAD_URL = "artifactDownloadUrl";

    private BuildProjectView view;

    /** Identifier of project we want to send for build. */
    private String projectId = null;

    /** The builds identifier. */
    private String buildID = null;

    /** Delay in millisecond between requests for build job status. */
    private static final int delay = 3000;

    /** Status of previously build. */
    private Status previousStatus = null;

    /** Build of another project is performed. */
    private boolean isBuildInProgress = false;

    /** View closed flag. */
    private boolean isViewClosed = true;

    private boolean publishAfterBuild = false;

    /** Project for build. */
    private Project project;

    protected RequestStatusHandler statusHandler;

    private String buildStatusChannel;

    private EventBus eventBus;

    private ResourceProvider resourceProvider;

    private ConsolePart console;

    private BuilderClientService service;

    private BuilderLocalizationConstant constant;

    private BuilderAutoBeanFactory autoBeanFactory;

    private BuilderResources resources;

    private WorkspaceAgent workspaceAgent;

    private MessageBus messageBus;

    /** Handler for processing Maven build status which is received over WebSocket connection. */
    private final SubscriptionHandler<BuildStatus> buildStatusHandler;

    /**
     * Create presenter.
     *
     * @param view
     * @param eventBus
     * @param resourceProvider
     * @param console
     * @param service
     * @param constant
     * @param autoBeanFactory
     * @param resources
     * @param workspaceAgent
     * @param messageBus
     */
    @Inject
    protected BuildProjectPresenter(BuildProjectView view, EventBus eventBus, ResourceProvider resourceProvider, ConsolePart console,
                                    BuilderClientService service, BuilderLocalizationConstant constant,
                                    BuilderAutoBeanFactory autoBeanFactory, BuilderResources resources, WorkspaceAgent workspaceAgent,
                                    MessageBus messageBus) {
        this.view = view;
        this.workspaceAgent = workspaceAgent;
        this.view.setDelegate(this);
        this.eventBus = eventBus;
        this.resourceProvider = resourceProvider;
        this.console = console;
        this.service = service;
        this.constant = constant;
        this.autoBeanFactory = autoBeanFactory;
        this.resources = resources;
        this.messageBus = messageBus;

        buildStatusHandler = new SubscriptionHandler<BuildStatus>(
                new AutoBeanUnmarshallerWS<BuildStatus>(this.autoBeanFactory.create(BuildStatus.class))) {
            @Override
            protected void onSuccess(BuildStatus buildStatus) {
                updateBuildStatus(buildStatus);
            }

            @Override
            protected void onFailure(Throwable exception) {
                try {
                    BuildProjectPresenter.this.messageBus.unsubscribe(buildStatusChannel, this);
                } catch (WebSocketException e) {
                    // nothing to do
                }

                setBuildInProgress(false);
                BuildProjectPresenter.this.view.stopAnimation();
                BuildProjectPresenter.this.eventBus.fireEvent(new ExceptionThrownEvent(exception));
                BuildProjectPresenter.this.console.print(exception.getMessage());
            }
        };

        // TODO need to remove following code
        this.eventBus.addHandler(BuildProjectEvent.TYPE, this);
    }

    /** {@inheritDoc} */
    @Override
    public void onBuildProject(BuildProjectEvent event) {
        if (isBuildInProgress) {
            String message = constant.buildInProgress(project.getPath().substring(1));
            Window.alert(message);
            return;
        }

        project = event.getProject();
        if (project == null && makeSelectionCheck()) {
            project = resourceProvider.getActiveProject();
        }

        statusHandler = new BuildRequestStatusHandler(project.getPath().substring(1), eventBus, constant);

        publishAfterBuild = event.isPublish();

        buildApplicationIfNeed(event.isForce());
    }

    /** Start the build of project. */
    private void doBuild() {
        projectId = project.getId();
        statusHandler.requestInProgress(projectId);

        try {
            StringUnmarshaller unmarshaller = new StringUnmarshaller(new StringBuilder());
            service.build(projectId, resourceProvider.getVfsId(), project.getName(),
                          (String)project.getPropertyValue(ProjectDescription.PROPERTY_PRIMARY_NATURE),
                          new AsyncRequestCallback<StringBuilder>(unmarshaller) {
                              @Override
                              protected void onSuccess(StringBuilder result) {
                                  buildID = result.substring(result.lastIndexOf("/") + 1);
                                  setBuildInProgress(true);
                                  showBuildMessage("Building project <b>" + project.getPath().substring(1) + "</b>");
                                  view.startAnimation();
                                  previousStatus = null;
                                  startCheckingStatus(buildID);
                              }

                              @Override
                              protected void onFailure(Throwable exception) {
                                  statusHandler.requestError(projectId, exception);
                                  setBuildInProgress(false);
                                  view.stopAnimation();
                                  if (exception instanceof ServerException && exception.getMessage() != null) {
                                      console.print(exception.getMessage());
                                  } else {
                                      eventBus.fireEvent(new ExceptionThrownEvent(exception));
                                      console.print(exception.getMessage());
                                  }
                              }
                          });
        } catch (RequestException e) {
            setBuildInProgress(false);
            view.stopAnimation();
            console.print(e.getMessage());
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

    /** Start the build of project and publish it to public repository. */
    private void doBuildAndPublish() {
        projectId = project.getId();
        statusHandler.requestInProgress(projectId);

        try {
            StringUnmarshaller unmarshaller = new StringUnmarshaller(new StringBuilder());
            service.buildAndPublish(projectId, resourceProvider.getVfsId(), project.getName(),
                                    (String)project.getPropertyValue(ProjectDescription.PROPERTY_PRIMARY_NATURE),
                                    new AsyncRequestCallback<StringBuilder>(unmarshaller) {
                                        @Override
                                        protected void onSuccess(StringBuilder result) {
                                            buildID = result.substring(result.lastIndexOf("/") + 1);
                                            setBuildInProgress(true);
                                            showBuildMessage(
                                                    "Building project <b>" + project.getPath().substring(1) +
                                                    "</b>");
                                            view.startAnimation();
                                            previousStatus = null;
                                            refreshBuildStatusTimer.schedule(delay);
                                        }

                                        @Override
                                        protected void onFailure(Throwable exception) {
                                            statusHandler.requestError(projectId, exception);
                                            setBuildInProgress(false);
                                            view.stopAnimation();
                                            if (exception instanceof ServerException &&
                                                exception.getMessage() != null) {
                                                console.print(exception.getMessage());
                                            } else {
                                                eventBus.fireEvent(new ExceptionThrownEvent(exception));
                                                console.print(exception.getMessage());
                                            }
                                        }
                                    });
        } catch (RequestException e) {
            setBuildInProgress(false);
            view.stopAnimation();
            console.print(e.getMessage());
        }
    }

    /**
     * Builds application if it needs.
     *
     * @param force
     *         <code>true</code> to force build, <code>false</code> to not force build
     */
    private void buildApplicationIfNeed(boolean force) {
        //if isPublish true start build & publish process any way
        if (publishAfterBuild) {
            doBuildAndPublish();
            return;
        }
        if (force) {
            doBuild();
            return;
        }
        //Going to check is need built project.
        //Need compare to properties lastBuildTime and lastModificationTime
        //After check is artifact available for downloading
        project.refreshProperties(new AsyncCallback<Project>() {
            @Override
            public void onSuccess(Project result) {
                Property downloadUrlProp = result.getProperty(ARTIFACT_DOWNLOAD_URL);
                if (downloadUrlProp != null && !downloadUrlProp.getValue().isEmpty()) {
                    if (isProjectChangedAfterLastBuild(result)) {
                        checkDownloadUrl(downloadUrlProp.getValue().get(0));
                    } else {
                        doBuild();
                    }
                } else {
                    doBuild();
                }
            }

            @Override
            public void onFailure(Throwable exception) {
                doBuild();
                Log.error(BuildProjectPresenter.class, exception);
            }
        });
    }

    /**
     * Checks if the project is changed after last build.
     *
     * @param item
     *         current project
     * @return <code>true</code> if the project is changed, and <code>true</code> otherwise.
     */
    private boolean isProjectChangedAfterLastBuild(Project item) {
        long buildTime = 0;
        long lastUpdateTime = 0;
        Property buildTimeProperty = item.getProperty(LAST_SUCCESS_BUILD);
        if (buildTimeProperty != null && !buildTimeProperty.getValue().isEmpty()) {
            buildTime = Long.parseLong(buildTimeProperty.getValue().get(0));
        }
        Property lastUpdateTimeProp = item.getProperty("vfs:lastUpdateTime");
        if (lastUpdateTimeProp == null) {
            return false;
        }
        lastUpdateTime = Long.parseLong(lastUpdateTimeProp.getValue().get(0));
        return buildTime > lastUpdateTime;
    }

    /**
     * Checks download url.
     *
     * @param url
     *         download url
     */
    private void checkDownloadUrl(final String url) {
        try {
            service.checkArtifactUrl(url, new AsyncRequestCallback<Object>() {
                @Override
                protected void onSuccess(Object result) {
                    BuildStatus buildStatus = autoBeanFactory.buildStatus().as();
                    buildStatus.setStatus(BuildStatus.Status.SUCCESSFUL);
                    buildStatus.setDownloadUrl(url);
                    eventBus.fireEvent(new ProjectBuiltEvent(buildStatus));
                }

                @Override
                protected void onFailure(Throwable exception) {
                    doBuild();
                }
            });
        } catch (RequestException e) {
            doBuild();
            e.printStackTrace();
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

    /** A timer for periodically sending request of build status. */
    private Timer refreshBuildStatusTimer = new Timer() {
        @Override
        public void run() {
            try {
                AutoBean<BuildStatus> buildStatus = autoBeanFactory.create(BuildStatus.class);
                AutoBeanUnmarshaller<BuildStatus> unmarshaller = new AutoBeanUnmarshaller<BuildStatus>(buildStatus);
                service.status(buildID, new AsyncRequestCallback<BuildStatus>(unmarshaller) {
                    @Override
                    protected void onSuccess(BuildStatus response) {
                        updateBuildStatus(response);

                        Status status = response.getStatus();
                        if (status == Status.IN_PROGRESS) {
                            schedule(delay);
                        }
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        setBuildInProgress(false);
                        view.stopAnimation();
                        eventBus.fireEvent(new ExceptionThrownEvent(exception));
                        console.print(exception.getMessage());
                    }
                });
            } catch (RequestException e) {
                setBuildInProgress(false);
                view.stopAnimation();
                eventBus.fireEvent(new ExceptionThrownEvent(e));
                console.print(e.getMessage());
            }
        }
    };

    /**
     * Check for status and display necessary messages.
     *
     * @param buildStatus
     *         status of build
     */
    private void updateBuildStatus(BuildStatus buildStatus) {
        Status status = buildStatus.getStatus();

        if (status == Status.IN_PROGRESS && previousStatus != Status.IN_PROGRESS) {
            previousStatus = Status.IN_PROGRESS;
            return;
        }

        if ((status == Status.SUCCESSFUL && previousStatus != Status.SUCCESSFUL)
            || (status == Status.FAILED && previousStatus != Status.FAILED)) {
            afterBuildFinished(buildStatus);
            return;
        }
    }

    /**
     * Perform actions after build is finished.
     *
     * @param buildStatus
     *         status of build job
     */
    private void afterBuildFinished(BuildStatus buildStatus) {
        try {
            messageBus.unsubscribe(buildStatusChannel, buildStatusHandler);
        } catch (Exception e) {
            // nothing to do
        }

        setBuildInProgress(false);
        previousStatus = buildStatus.getStatus();

        StringBuilder message =
                new StringBuilder("Finished building project <b>").append(project.getPath().substring(1))
                                                                  .append("</b>.\r\nResult: ").append(buildStatus.getStatus());

        if (buildStatus.getStatus() == Status.SUCCESSFUL) {
            console.print(constant.buildSuccess());

            statusHandler.requestFinished(projectId);
            if (projectId != null) {
                writeBuildInfo(buildStatus);
                checkIfProjectIsUnderWatching();
            }
            if (publishAfterBuild) {
                getPublishArtifactResult();
            }
        } else if (buildStatus.getStatus() == Status.FAILED) {
            console.print(constant.buildFailed());

            String errorMessage = buildStatus.getError();
            String exceptionMessage = "Building of project failed";
            if (errorMessage != null && !errorMessage.equals("null")) {
                message.append("\r\n" + errorMessage);
                exceptionMessage += ": " + errorMessage;
            }

            statusHandler.requestError(projectId, new Exception("Building of project failed", new Throwable(
                    exceptionMessage)));
        }
        showBuildMessage(message.toString());
        view.stopAnimation();
        eventBus.fireEvent(new ProjectBuiltEvent(buildStatus));
    }

    /** Getting information about publish artifact process for its only suggest dependency */
    private void getPublishArtifactResult() {
        try {
            StringUnmarshaller unmarshaller = new StringUnmarshaller(new StringBuilder());
            service.result(buildID, new AsyncRequestCallback<StringBuilder>(unmarshaller) {
                @Override
                protected void onSuccess(StringBuilder result) {
                    JSONObject json = JSONParser.parseStrict((result.toString())).isObject();
                    if (json.containsKey("artifactDownloadUrl")) {
                        String artifactUrl = json.get("artifactDownloadUrl").isString().stringValue();
                        console.print(
                                "You can download your artifact :<a href=" + artifactUrl + " target=\"_blank\">" + artifactUrl + "</a>");
                    }
                    if (json.containsKey("suggestDependency")) {
                        String dep = json.get("suggestDependency").isString().stringValue();
                        //format XML
                        String res = formatDepXml(dep);
                        console.print("Dependency for your pom:<br><span style=\"color:black;\">" + res + "</span>");
                    }
                }

                @Override
                protected void onFailure(Throwable exception) {
                    // nothing to do
                }
            });
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes build info.
     *
     * @param buildStatus
     *         build status
     */
    private void writeBuildInfo(BuildStatus buildStatus) {
        project.getProperties().add(new Property(LAST_SUCCESS_BUILD, buildStatus.getTime()));
        project.getProperties().add(new Property(ARTIFACT_DOWNLOAD_URL, buildStatus.getDownloadUrl()));

        project.flushProjectProperties(new AsyncCallback<Project>() {
            @Override
            public void onSuccess(Project result) {
                //Nothing to do
            }

            @Override
            public void onFailure(Throwable caught) {
                //Ignore this exception
            }
        });
    }

    /** Checks if project is uder watching. */
    private void checkIfProjectIsUnderWatching() {
        project.refreshProperties(new AsyncCallback<Project>() {
            @Override
            public void onSuccess(Project result) {
                JsonArray<Property> properties = result.getProperties();
                for (int i = 0; i < properties.size(); i++) {
                    Property property = properties.get(i);
                    if ("vfs:lastUpdateTime".equals(property.getName())) {
                        return;
                    }
                }
                startWatchingProjectChanges();
            }

            @Override
            public void onFailure(Throwable caught) {
            }
        });
    }

    /**
     *
     */
    private void startWatchingProjectChanges() {
        // TODO IDEX-62
        // We don't have vfs module. Need to create or use some analog method
        //      try
        //      {
        //         VirtualFileSystem.getInstance().startWatchUpdates(project.getId(), new AsyncRequestCallback<Object>()
        //         {
        //
        //            @Override
        //            protected void onSuccess(Object result)
        //            {
        //            }
        //
        //            @Override
        //            protected void onFailure(Throwable exception)
        //            {
        //
        //            }
        //         });
        //      }
        //      catch (RequestException e)
        //      {
        //         e.printStackTrace();
        //      }
    }

    /**
     * Output the message and activate view if necessary.
     *
     * @param message
     *         message for output
     */
    private void showBuildMessage(String message) {

        if (isViewClosed) {
            workspaceAgent.openPart(this, PartStackType.INFORMATION);
            isViewClosed = false;
        }

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
     * Checks if some project is opened/selected.
     *
     * @return <code>true</code> if some project is opened, and <code>true</code> otherwise.
     */
    private boolean makeSelectionCheck() {
        if (resourceProvider.getActiveProject() == null) {
            Window.alert("Project is not selected.");
            return false;
        }

        return true;
    }

    /**
     * Formats dependency xml.
     *
     * @param dep
     * @return formated xml
     */
    private String formatDepXml(String dep) {

        String formatStr = SafeHtmlUtils.htmlEscape(dep)//
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
        return "Output";
    }

    /** {@inheritDoc} */
    @Override
    public ImageResource getTitleImage() {
        return resources.build();
    }

    /** {@inheritDoc} */
    @Override
    public String getTitleToolTip() {
        return "Displays maven output";
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        view.setClearOutputButtonEnabled(false);
        container.setWidget(view);
    }

    /** Deserializer for responses body. */
    private class StringUnmarshaller implements Unmarshallable<StringBuilder> {
        protected StringBuilder builder;

        public StringUnmarshaller(StringBuilder builder) {
            this.builder = builder;
        }

        /** {@inheritDoc} */
        @Override
        public void unmarshal(Response response) {
            builder.append(response.getText());
        }

        /** {@inheritDoc} */
        @Override
        public StringBuilder getPayload() {
            return builder;
        }
    }
}