/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.maven.client.build;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Timer;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.gwtframework.commons.rest.RequestStatusHandler;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.control.Docking;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.websocket.WebSocketException;
import org.exoplatform.ide.client.framework.websocket.rest.AutoBeanUnmarshallerWS;
import org.exoplatform.ide.client.framework.websocket.rest.SubscriptionHandler;
import org.exoplatform.ide.extension.maven.client.BuilderClientService;
import org.exoplatform.ide.extension.maven.client.BuilderExtension;
import org.exoplatform.ide.extension.maven.client.control.BuildAndPublishProjectControl;
import org.exoplatform.ide.extension.maven.client.control.BuildProjectControl;
import org.exoplatform.ide.extension.maven.client.control.BuildProjectStopControl;
import org.exoplatform.ide.extension.maven.client.event.BuildProjectHandler;
import org.exoplatform.ide.extension.maven.client.event.BuildProjectStopHandler;
import org.exoplatform.ide.extension.maven.client.event.BuildProjectEvent;
import org.exoplatform.ide.extension.maven.client.event.BuildProjectStopEvent;
import org.exoplatform.ide.extension.maven.client.event.ProjectBuiltEvent;
import org.exoplatform.ide.extension.maven.shared.BuildStatus;
import org.exoplatform.ide.extension.maven.shared.BuildStatus.Status;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.event.ItemDeletedEvent;
import org.exoplatform.ide.vfs.client.event.ItemDeletedHandler;
import org.exoplatform.ide.vfs.client.marshal.ItemUnmarshaller;
import org.exoplatform.ide.vfs.client.model.ItemContext;
import org.exoplatform.ide.vfs.client.model.ItemWrapper;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.Property;
import org.exoplatform.ide.vfs.shared.PropertyImpl;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.List;

/**
 * Presenter for created builder view. The view must be pointed in Views.gwt.xml.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: BuildProjectPresenter.java Feb 17, 2012 5:39:10 PM azatsarynnyy $
 */
public class BuildProjectPresenter implements BuildProjectHandler, ItemsSelectedHandler, ViewClosedHandler,
                                              VfsChangedHandler, ItemDeletedHandler, BuildProjectStopHandler {
    public interface Display extends IsView {
        HasClickHandlers getClearOutputButton();

        void showMessageInOutput(String text);

        void startAnimation();

        void stopAnimation();

        void clearOutput();

        void setClearOutputButtonEnabled(boolean isEnabled);
    }

    private Display display;

    private static final String BUILD_SUCCESS = BuilderExtension.LOCALIZATION_CONSTANT.buildSuccess();

    private static final String BUILD_FAILED = BuilderExtension.LOCALIZATION_CONSTANT.buildFailed();

    private final static String LAST_SUCCESS_BUILD = "lastSuccessBuild";

    private final static String ARTIFACT_DOWNLOAD_URL = "artifactDownloadUrl";

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

    /** Selected items in browser tree. */
    protected List<Item> selectedItems;

    /** Current virtual file system. */
    protected VirtualFileSystemInfo vfs;

    /** Project for build. */
    private ProjectModel project;

    protected RequestStatusHandler statusHandler;

    private String buildStatusChannel;

    public BuildProjectPresenter() {
        IDE.getInstance().addControl(new BuildProjectControl());
        IDE.getInstance().addControl(new BuildAndPublishProjectControl());
        IDE.getInstance().addControl(new BuildProjectStopControl(), Docking.STATUSBAR_RIGHT);

        IDE.addHandler(BuildProjectEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
        IDE.addHandler(ItemsSelectedEvent.TYPE, this);
        IDE.addHandler(VfsChangedEvent.TYPE, this);
        IDE.addHandler(ItemDeletedEvent.TYPE, this);
        IDE.addHandler(BuildProjectStopEvent.TYPE, this);
    }

    /** @see org.exoplatform.ide.extension.maven.client.event.BuildProjectHandler#onBuildProject(org.exoplatform.ide.extension.maven
     * .client.event.BuildProjectEvent) */
    @Override
    public void onBuildProject(BuildProjectEvent event) {
        if (isBuildInProgress) {
            String message = BuilderExtension.LOCALIZATION_CONSTANT.buildInProgress(project.getPath().substring(1));
            Dialogs.getInstance().showError(message);
            return;
        }

        if (display == null) {
            display = GWT.create(Display.class);
            bindDisplay();
        }

        project = event.getProject();
        if (project == null && makeSelectionCheck()) {
            Item item = selectedItems.get(0);
            if (item instanceof ProjectModel) {
                project = (ProjectModel)item;
            } else {
                project = ((ItemContext)item).getProject();
            }
        }

        statusHandler = new BuildRequestStatusHandler(project.getPath().substring(1));
        publishAfterBuild = event.isPublish();
        buildApplicationIfNeed(event.isForce());
    }

    /** Start the build of project. */
    private void doBuild() {
        projectId = project.getId();
        statusHandler.requestInProgress(projectId);

        try {
            BuilderClientService.getInstance().build(projectId, vfs.getId(), project.getName(), project.getProjectType(),
                                                     new AsyncRequestCallback<StringBuilder>(new StringUnmarshaller(new StringBuilder())) {
                                                         @Override
                                                         protected void onSuccess(StringBuilder result) {
                                                             buildID = result.substring(result.lastIndexOf("/") + 1);
                                                             setBuildInProgress(true);
                                                             showBuildMessage(
                                                                     "Building project <b>" + project.getPath().substring(1) + "</b>");
                                                             display.startAnimation();
                                                             previousStatus = null;
                                                             startCheckingStatus(buildID);
                                                         }

                                                         @Override
                                                         protected void onFailure(Throwable exception) {
                                                             statusHandler.requestError(projectId, exception);
                                                             setBuildInProgress(false);
                                                             display.stopAnimation();
                                                             if (exception instanceof ServerException && exception.getMessage() != null) {
                                                                 IDE.fireEvent(new OutputEvent(exception.getMessage(), Type.ERROR));
                                                             } else {
                                                                 IDE.fireEvent(new ExceptionThrownEvent(exception));
                                                             }
                                                         }
                                                     });
        } catch (RequestException e) {
            setBuildInProgress(false);
            display.stopAnimation();
            IDE.fireEvent(new OutputEvent(e.getMessage(), Type.ERROR));
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
            IDE.messageBus().subscribe(buildStatusChannel, buildStatusHandler);
        } catch (WebSocketException e) {
            refreshBuildStatusTimer.schedule(delay);
        }
    }

    /** Start the build of project and publish it to public repository. */
    private void doBuildAndPublish() {
        projectId = project.getId();
        statusHandler.requestInProgress(projectId);

        try {
            BuilderClientService.getInstance().buildAndPublish(projectId, vfs.getId(), project.getName(), project.getProjectType(),
                                                               new AsyncRequestCallback<StringBuilder>(
                                                                       new StringUnmarshaller(new StringBuilder())) {
                                                                   @Override
                                                                   protected void onSuccess(StringBuilder result) {
                                                                       buildID = result.substring(result.lastIndexOf("/") + 1);
                                                                       setBuildInProgress(true);
                                                                       showBuildMessage(
                                                                               "Building project <b>" + project.getPath().substring(1) +
                                                                               "</b>");
                                                                       display.startAnimation();
                                                                       previousStatus = null;
                                                                       refreshBuildStatusTimer.schedule(delay);
                                                                   }

                                                                   @Override
                                                                   protected void onFailure(Throwable exception) {
                                                                       statusHandler.requestError(projectId, exception);
                                                                       setBuildInProgress(false);
                                                                       display.stopAnimation();
                                                                       if (exception instanceof ServerException &&
                                                                           exception.getMessage() != null) {
                                                                           IDE.fireEvent(
                                                                                   new OutputEvent(exception.getMessage(), Type.INFO));
                                                                       } else {
                                                                           IDE.fireEvent(new ExceptionThrownEvent(exception));
                                                                       }
                                                                   }
                                                               });
        } catch (RequestException e) {
            setBuildInProgress(false);
            display.stopAnimation();
            IDE.fireEvent(new OutputEvent(e.getMessage(), Type.INFO));
        }
    }

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
        try {
            //Going to check is need built project.
            //Need compare to properties lastBuildTime and lastModificationTime
            //After check is artifact available for downloading
            VirtualFileSystem.getInstance().getItemById(project.getId(),
                                                        new AsyncRequestCallback<ItemWrapper>(
                                                                new ItemUnmarshaller(new ItemWrapper(project))) {

                                                            @Override
                                                            protected void onSuccess(ItemWrapper result) {
                                                                Property downloadUrlProp =
                                                                        result.getItem().getProperty(ARTIFACT_DOWNLOAD_URL);
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
                                                            protected void onFailure(Throwable exception) {
                                                                doBuild();
                                                                exception.printStackTrace();
                                                            }
                                                        });
        } catch (RequestException e) {
            doBuild();
            e.printStackTrace();
        }
    }

    private boolean isProjectChangedAfterLastBuild(ItemWrapper item) {
        long buildTime = 0;
        long lastUpdateTime;
        Property buildTimeProperty = item.getItem().getProperty(LAST_SUCCESS_BUILD);
        if (buildTimeProperty != null && !buildTimeProperty.getValue().isEmpty()) {
            buildTime = Long.parseLong(buildTimeProperty.getValue().get(0));
        }
        Property lastUpdateTimeProp = item.getItem().getProperty("vfs:lastUpdateTime");
        if (lastUpdateTimeProp == null) {
            return false;
        }
        lastUpdateTime = Long.parseLong(lastUpdateTimeProp.getValue().get(0));
        return buildTime > lastUpdateTime;
    }

    private void checkDownloadUrl(final String url) {
        try {
            BuilderClientService.getInstance().checkArtifactUrl(url, new AsyncRequestCallback<Object>() {

                @Override
                protected void onSuccess(Object result) {
                    BuildStatus buildStatus = BuilderExtension.AUTO_BEAN_FACTORY.buildStatus().as();
                    buildStatus.setStatus(BuildStatus.Status.SUCCESSFUL);
                    buildStatus.setDownloadUrl(url);
                    IDE.fireEvent(new ProjectBuiltEvent(buildStatus));
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

    private void setBuildInProgress(boolean buildInProgress) {
        isBuildInProgress = buildInProgress;
        display.setClearOutputButtonEnabled(!buildInProgress);
    }

    /** A timer for periodically sending request of build status. */
    private Timer refreshBuildStatusTimer = new Timer() {
        @Override
        public void run() {
            try {
                AutoBean<BuildStatus> buildStatus = BuilderExtension.AUTO_BEAN_FACTORY.create(BuildStatus.class);
                AutoBeanUnmarshaller<BuildStatus> unmarshaller = new AutoBeanUnmarshaller<BuildStatus>(buildStatus);
                BuilderClientService.getInstance().status(buildID, new AsyncRequestCallback<BuildStatus>(unmarshaller) {
                    @Override
                    protected void onSuccess(BuildStatus response) {
                        updateBuildStatus(response);

                        Status status = response.getStatus();
                        if (status == Status.IN_PROGRESS) {
                            schedule(delay);
                        }
                    }

                    protected void onFailure(Throwable exception) {
                        setBuildInProgress(false);
                        display.stopAnimation();
                        IDE.fireEvent(new ExceptionThrownEvent(exception));
                    }
                });
            } catch (RequestException e) {
                setBuildInProgress(false);
                display.stopAnimation();
                IDE.fireEvent(new ExceptionThrownEvent(e));
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
            || (status == Status.FAILED && previousStatus != Status.FAILED)
            || status == Status.CANCELLED) {
            afterBuildFinished(buildStatus);
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
            IDE.messageBus().unsubscribe(buildStatusChannel, buildStatusHandler);
        } catch (Exception e) {
            // nothing to do
        }

        setBuildInProgress(false);
        previousStatus = buildStatus.getStatus();

        StringBuilder message =
                new StringBuilder("Finished building project <b>").append(project.getPath().substring(1))
                                                                  .append("</b>.\r\nResult: ").append(buildStatus.getStatus());

        if (buildStatus.getStatus() == Status.SUCCESSFUL) {
            IDE.fireEvent(new OutputEvent(BUILD_SUCCESS, Type.INFO));

            statusHandler.requestFinished(projectId);
            if (projectId != null) {
                writeBuildInfo(buildStatus);
                startWatchingProjectChanges();
            }
            if (publishAfterBuild) {
                getPublishArtifactResult();
            }
        } else if (buildStatus.getStatus() == Status.FAILED) {
            IDE.fireEvent(new OutputEvent(BUILD_FAILED, Type.ERROR));

            String errorMessage = buildStatus.getError();
            String exceptionMessage = "Building of project failed";
            if (errorMessage != null && !errorMessage.equals("null")) {
                message.append("\r\n");
                message.append(errorMessage);
                exceptionMessage += ": " + errorMessage;
            }

            statusHandler.requestError(projectId, new Exception("Building of project failed", new Throwable(exceptionMessage)));
        } else if (buildStatus.getStatus() == Status.CANCELLED) {
            String errorMessage = buildStatus.getError();
            statusHandler.requestError(projectId, new Exception("Build cancelled", new Throwable(errorMessage)));
        }
        showBuildMessage(message.toString());
        display.stopAnimation();
        IDE.fireEvent(new ProjectBuiltEvent(buildStatus));
    }

    @Override
    public void onItemDeleted(ItemDeletedEvent event) {
        //Erase projectId variable if user deletes project while it builds.
        if (event.getItem().getId().equals(projectId)) {
            projectId = null;
        }
    }

    /** Getting information about publish artifact process for its only suggest dependency */
    private void getPublishArtifactResult() {
        try {
            StringBuilder builder = new StringBuilder();
            BuilderClientService.getInstance().result(buildID,
                                                      new AsyncRequestCallback<StringBuilder>(new StringUnmarshaller(builder)) {
                                                          @Override
                                                          protected void onSuccess(StringBuilder result) {
                                                              JSONObject json = JSONParser.parseStrict((result.toString())).isObject();
                                                              if (json.containsKey("artifactDownloadUrl")) {
                                                                  String artifactUrl =
                                                                          json.get("artifactDownloadUrl").isString().stringValue();
                                                                  IDE.fireEvent(new OutputEvent(
                                                                          "You can download your artifact :<a href=" + artifactUrl
                                                                          + " target=\"_blank\">" + artifactUrl + "</a>", Type.INFO));
                                                              }
                                                              if (json.containsKey("suggestDependency")) {
                                                                  String dep = json.get("suggestDependency").isString().stringValue();
                                                                  //format XML
                                                                  String res = formatDepXml(dep);
                                                                  IDE.fireEvent(new OutputEvent(
                                                                          "Dependency for your pom:<br><span style=\"color:black;\">" + res
                                                                          + "</span>", Type.INFO));
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

    private void writeBuildInfo(BuildStatus buildStatus) {
        project.getProperties().add(new PropertyImpl(LAST_SUCCESS_BUILD, buildStatus.getTime()));
        project.getProperties().add(new PropertyImpl(ARTIFACT_DOWNLOAD_URL, buildStatus.getDownloadUrl()));
        try {
            VirtualFileSystem.getInstance().updateItem(project, null, new AsyncRequestCallback<ItemWrapper>() {

                @Override
                protected void onSuccess(ItemWrapper result) {
                    //Nothing todo
                }

                @Override
                protected void onFailure(Throwable ignore) {
                    //Ignore this exception
                }
            });
        } catch (RequestException e) {
            e.printStackTrace();
        }

    }

    private void startWatchingProjectChanges() {
        try {
            VirtualFileSystem.getInstance().startWatchUpdates(project.getId(), new AsyncRequestCallback<Object>() {

                @Override
                protected void onSuccess(Object result) {
                }

                @Override
                protected void onFailure(Throwable exception) {

                }
            });
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }

    /**
     * Output the message and activate view if necessary.
     *
     * @param message
     *         message for output
     */
    private void showBuildMessage(String message) {
        if (display != null) {
            if (isViewClosed) {
                IDE.getInstance().openView(display.asView());
                isViewClosed = false;
            } else {
                display.asView().activate();
            }
        } else {
            display = GWT.create(Display.class);
            IDE.getInstance().openView(display.asView());
            bindDisplay();
            isViewClosed = false;
        }

        display.showMessageInOutput(message);
    }

    /** Bind display (view) with presenter. */
    public void bindDisplay() {
        display.getClearOutputButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                display.clearOutput();
            }
        });
    }

    /**
     * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     *      .event.ViewClosedEvent)
     */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            isViewClosed = true;
        }
    }

    /**
     * @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client
     *      .framework.navigation.event.ItemsSelectedEvent)
     */
    @Override
    public void onItemsSelected(ItemsSelectedEvent event) {
        this.selectedItems = event.getSelectedItems();
    }

    /**
     * @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework
     *      .application.event.VfsChangedEvent)
     */
    @Override
    public void onVfsChanged(VfsChangedEvent event) {
        this.vfs = event.getVfsInfo();
    }

    private boolean makeSelectionCheck() {
        if (selectedItems == null || selectedItems.size() <= 0) {
            Dialogs.getInstance().showInfo(BuilderExtension.LOCALIZATION_CONSTANT.selectedItemsFail());
            return false;
        }

        Item item = selectedItems.get(0);

        if (item instanceof ProjectModel) {
            return true;
        }

        if (((ItemContext)item).getProject() != null) {
            return true;
        }

        Dialogs.getInstance().showInfo("Project is not selected.");
        return false;
    }

    /**
     * @param dep
     * @return
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

    /** Handler for processing Maven build status which is received over WebSocket connection. */
    private SubscriptionHandler<BuildStatus> buildStatusHandler = new SubscriptionHandler<BuildStatus>(
            new AutoBeanUnmarshallerWS<BuildStatus>(BuilderExtension.AUTO_BEAN_FACTORY.create(BuildStatus.class))) {
        @Override
        protected void onSuccess(BuildStatus buildStatus) {
            updateBuildStatus(buildStatus);
        }

        @Override
        protected void onFailure(Throwable exception) {
            try {
                IDE.messageBus().unsubscribe(buildStatusChannel, this);
            } catch (WebSocketException e) {
                // nothing to do
            }

            setBuildInProgress(false);
            display.stopAnimation();
            IDE.fireEvent(new ExceptionThrownEvent(exception));
        }
    };

    /** Deserializer for responses body. */
    private class StringUnmarshaller implements Unmarshallable<StringBuilder> {

        protected StringBuilder builder;

        public StringUnmarshaller(StringBuilder builder) {
            this.builder = builder;
        }

        /** @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response) */
        @Override
        public void unmarshal(Response response) {
            builder.append(response.getText());
        }

        @Override
        public StringBuilder getPayload() {
            return builder;
        }
    }

    @Override
    public void onBuildProjectStopEvent(BuildProjectStopEvent event) {
        try {
            BuilderClientService.getInstance().cancel(buildID, project.getName(), project.getProjectType(), new AsyncRequestCallback<StringBuilder>() {
                @Override
                protected void onSuccess(StringBuilder result) {
                    //nothing to do
                }

                @Override
                protected void onFailure(Throwable exception) {
                    //nothing to do
                }
            });
        } catch (RequestException e) {
            setBuildInProgress(false);
            display.stopAnimation();
            statusHandler.requestError(projectId, new Throwable(e.getMessage()));
        }
    }
}
