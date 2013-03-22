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

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.console.ConsolePart;
import com.codenvy.ide.commons.exception.ServerException;
import com.codenvy.ide.extension.maven.client.BuilderClientService;
import com.codenvy.ide.extension.maven.client.BuilderExtension;
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
import com.codenvy.ide.websocket.rest.AutoBeanUnmarshallerWS;
import com.codenvy.ide.websocket.rest.SubscriptionHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
public class BuildProjectPresenter implements BuildProjectHandler, BuildProjectView.ActionDelegate
// TODO IDEX-57 
// Need to research: do these classes need?
// , ItemsSelectedHandler, ViewClosedHandler, VfsChangedHandler, ItemDeletedHandler
{
   private static final String BUILD_SUCCESS = BuilderExtension.LOCALIZATION_CONSTANT.buildSuccess();

   private static final String BUILD_FAILED = BuilderExtension.LOCALIZATION_CONSTANT.buildFailed();

   private final static String LAST_SUCCESS_BUILD = "lastSuccessBuild";

   private final static String ARTIFACT_DOWNLOAD_URL = "artifactDownloadUrl";

   private BuildProjectView view;

   /**
    * Identifier of project we want to send for build.
    */
   private String projectId = null;

   /**
    * The builds identifier.
    */
   private String buildID = null;

   /**
    * Delay in millisecond between requests for build job status.
    */
   private static final int delay = 3000;

   /**
    * Status of previously build.
    */
   private Status previousStatus = null;

   /**
    * Build of another project is performed.
    */
   private boolean isBuildInProgress = false;

   /**
    * View closed flag.
    */
   private boolean isViewClosed = true;

   private boolean publishAfterBuild = false;

   /**
    * Project for build.
    */
   private Project project;

   protected RequestStatusHandler statusHandler;

   private String buildStatusChannel;

   private EventBus eventBus;

   private ResourceProvider resourceProvider;

   private ConsolePart console;

   @Inject
   protected BuildProjectPresenter(BuildProjectView view, EventBus eventBus, ResourceProvider resourceProvider,
      ConsolePart console)
   {
      // TODO IDEX-57
      // do these classes need?

      //      IDE.getInstance().addControl(new BuildProjectControl());
      //      IDE.getInstance().addControl(new BuildAndPublishProjectControl());
      //
      //      IDE.addHandler(ViewClosedEvent.TYPE, this);
      //      IDE.addHandler(ItemsSelectedEvent.TYPE, this);
      //      IDE.addHandler(VfsChangedEvent.TYPE, this);
      //      IDE.addHandler(ItemDeletedEvent.TYPE, this);
      this.view = view;
      this.eventBus = eventBus;
      this.resourceProvider = resourceProvider;
      this.console = console;

      this.eventBus.addHandler(BuildProjectEvent.TYPE, this);
   }

   /**
    * @see com.codenvy.ide.extension.maven.client.event.BuildProjectHandler#onBuildProject(com.codenvy.ide.extension.maven.client.event.BuildProjectEvent)
    */
   @Override
   public void onBuildProject(BuildProjectEvent event)
   {
      if (isBuildInProgress)
      {
         String message = BuilderExtension.LOCALIZATION_CONSTANT.buildInProgress(project.getPath().substring(1));
         // TODO IDEX-57
         // We don't have analog Dialogs class in IDE3
         //         Dialogs.getInstance().showError(message);
         Window.alert(message);
         return;
      }

      project = event.getProject();
      if (project == null && makeSelectionCheck())
      {
         // TODO IDEX-57
         // Check: is it good solution? may be need to get selected item from Selection service  
         //         project = ((ItemContext)selectedItems.get(0)).getProject();
         project = resourceProvider.getActiveProject();
      }

      statusHandler = new BuildRequestStatusHandler(project.getPath().substring(1), eventBus);

      publishAfterBuild = event.isPublish();

      buildApplicationIfNeed(event.isForce());
   }

   /**
    * Start the build of project.
    */
   private void doBuild()
   {
      projectId = project.getId();
      statusHandler.requestInProgress(projectId);

      try
      {
         BuilderClientService.getInstance().build(projectId, resourceProvider.getVfsId(), project.getName(),
            (String)project.getPropertyValue(ProjectDescription.PROPERTY_PRIMARY_NATURE),
            new AsyncRequestCallback<StringBuilder>(new StringUnmarshaller(new StringBuilder()))
            {
               @Override
               protected void onSuccess(StringBuilder result)
               {
                  buildID = result.substring(result.lastIndexOf("/") + 1);
                  setBuildInProgress(true);
                  showBuildMessage("Building project <b>" + project.getPath().substring(1) + "</b>");
                  view.startAnimation();
                  previousStatus = null;
                  startCheckingStatus(buildID);
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  statusHandler.requestError(projectId, exception);
                  setBuildInProgress(false);
                  view.stopAnimation();
                  if (exception instanceof ServerException && exception.getMessage() != null)
                  {
                     // TODO IDEX-57
                     // Research: is it good solution?
                     //                     IDE.fireEvent(new OutputEvent(exception.getMessage(), Type.ERROR));
                     console.print(exception.getMessage());
                  }
                  else
                  {
                     // TODO IDEX-57
                     // Research: is it good solution?
                     //                     IDE.fireEvent(new ExceptionThrownEvent(exception));
                     console.print(exception.getMessage());
                  }
               }
            });
      }
      catch (RequestException e)
      {
         setBuildInProgress(false);
         view.stopAnimation();
         // TODO IDEX-57
         // Research: is it good solution?
         //         IDE.fireEvent(new OutputEvent(e.getMessage(), Type.ERROR));
         console.print(e.getMessage());
      }
   }

   /**
    * Starts checking job status by subscribing on receiving
    * messages over WebSocket or scheduling task to check status.
    * 
    * @param buildId id of the build job to check status
    */
   private void startCheckingStatus(String buildId)
   {
      // TODO IDEX-57
      // Problem with using Websocket
      //      try
      //      {
      buildStatusChannel = BuilderExtension.BUILD_STATUS_CHANNEL + buildId;
      //         IDE.messageBus().subscribe(buildStatusChannel, buildStatusHandler);
      //      }
      //      catch (WebSocketException e)
      //      {
      refreshBuildStatusTimer.schedule(delay);
      //      }
   }

   /**
    * Start the build of project and publish it to public repository.
    */
   private void doBuildAndPublish()
   {
      projectId = project.getId();
      statusHandler.requestInProgress(projectId);

      try
      {
         BuilderClientService.getInstance().buildAndPublish(projectId, resourceProvider.getVfsId(), project.getName(),
            (String)project.getPropertyValue(ProjectDescription.PROPERTY_PRIMARY_NATURE),
            new AsyncRequestCallback<StringBuilder>(new StringUnmarshaller(new StringBuilder()))
            {
               @Override
               protected void onSuccess(StringBuilder result)
               {
                  buildID = result.substring(result.lastIndexOf("/") + 1);
                  setBuildInProgress(true);
                  showBuildMessage("Building project <b>" + project.getPath().substring(1) + "</b>");
                  //                  display.startAnimation();
                  previousStatus = null;
                  refreshBuildStatusTimer.schedule(delay);
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  statusHandler.requestError(projectId, exception);
                  setBuildInProgress(false);
                  view.stopAnimation();
                  if (exception instanceof ServerException && exception.getMessage() != null)
                  {
                     // TODO IDEX-57
                     // Research: is it good solution?
                     //                     IDE.fireEvent(new OutputEvent(exception.getMessage(), Type.INFO));
                     console.print(exception.getMessage());
                  }
                  else
                  {
                     // TODO IDEX-57
                     // Research: is it good solution?
                     //                     IDE.fireEvent(new ExceptionThrownEvent(exception));
                     console.print(exception.getMessage());
                  }
               }
            });
      }
      catch (RequestException e)
      {
         setBuildInProgress(false);
         view.stopAnimation();
         // TODO IDEX-57
         // Research: is it good solution?
         //         IDE.fireEvent(new OutputEvent(e.getMessage(), Type.INFO));
         console.print(e.getMessage());
      }
   }

   private void buildApplicationIfNeed(boolean force)
   {
      //if isPublish true start build & publish process any way
      if (publishAfterBuild)
      {
         doBuildAndPublish();
         return;
      }
      if (force)
      {
         doBuild();
         return;
      }
      //Going to check is need built project.
      //Need compare to properties lastBuildTime and lastModificationTime  
      //After check is artifact available for downloading   
      project.refreshProperties(new AsyncCallback<Project>()
      {
         @Override
         public void onSuccess(Project result)
         {
            Property downloadUrlProp = result.getProperty(ARTIFACT_DOWNLOAD_URL);
            if (downloadUrlProp != null && !downloadUrlProp.getValue().isEmpty())
            {
               if (isProjectChangedAfterLastBuild(result))
               {
                  checkDownloadUrl(downloadUrlProp.getValue().get(0));
               }
               else
               {
                  doBuild();
               }
            }
            else
            {
               doBuild();
            }
         }

         @Override
         public void onFailure(Throwable exception)
         {
            doBuild();
            Log.error(BuildProjectPresenter.class, exception);
         }
      });
   }

   private boolean isProjectChangedAfterLastBuild(Project item)
   {
      long buildTime = 0;
      long lastUpdateTime = 0;
      Property buildTimeProperty = item.getProperty(LAST_SUCCESS_BUILD);
      if (buildTimeProperty != null && !buildTimeProperty.getValue().isEmpty())
      {
         buildTime = Long.parseLong(buildTimeProperty.getValue().get(0));
      }
      Property lastUpdateTimeProp = item.getProperty("vfs:lastUpdateTime");
      if (lastUpdateTimeProp == null)
      {
         return false;
      }
      lastUpdateTime = Long.parseLong(lastUpdateTimeProp.getValue().get(0));
      return buildTime > lastUpdateTime;
   }

   private void checkDownloadUrl(final String url)
   {
      try
      {
         BuilderClientService.getInstance().checkArtifactUrl(url, new AsyncRequestCallback<Object>()
         {
            @Override
            protected void onSuccess(Object result)
            {
               BuildStatus buildStatus = BuilderExtension.AUTO_BEAN_FACTORY.buildStatus().as();
               buildStatus.setStatus(BuildStatus.Status.SUCCESSFUL);
               buildStatus.setDownloadUrl(url);
               eventBus.fireEvent(new ProjectBuiltEvent(buildStatus));
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               doBuild();
            }
         });
      }
      catch (RequestException e)
      {
         doBuild();
         e.printStackTrace();
      }
   }

   private void setBuildInProgress(boolean buildInProgress)
   {
      isBuildInProgress = buildInProgress;
      view.setClearOutputButtonEnabled(!buildInProgress);
   }

   /**
    * A timer for periodically sending request of build status.
    */
   private Timer refreshBuildStatusTimer = new Timer()
   {
      @Override
      public void run()
      {
         try
         {
            AutoBean<BuildStatus> buildStatus = BuilderExtension.AUTO_BEAN_FACTORY.create(BuildStatus.class);
            AutoBeanUnmarshaller<BuildStatus> unmarshaller = new AutoBeanUnmarshaller<BuildStatus>(buildStatus);
            BuilderClientService.getInstance().status(buildID, new AsyncRequestCallback<BuildStatus>(unmarshaller)
            {
               @Override
               protected void onSuccess(BuildStatus response)
               {
                  updateBuildStatus(response);

                  Status status = response.getStatus();
                  if (status == Status.IN_PROGRESS)
                  {
                     schedule(delay);
                  }
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  setBuildInProgress(false);
                  view.stopAnimation();
                  // TODO IDEX-57
                  // Research: is it good solution?
                  //                  IDE.fireEvent(new ExceptionThrownEvent(exception));
                  console.print(exception.getMessage());
               }
            });
         }
         catch (RequestException e)
         {
            setBuildInProgress(false);
            view.stopAnimation();
            // TODO IDEX-57
            // Research: is it good solution?
            //            IDE.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
         }
      }
   };

   /**
    * Check for status and display necessary messages.
    *
    * @param buildStatus status of build
    */
   private void updateBuildStatus(BuildStatus buildStatus)
   {
      Status status = buildStatus.getStatus();

      if (status == Status.IN_PROGRESS && previousStatus != Status.IN_PROGRESS)
      {
         previousStatus = Status.IN_PROGRESS;
         return;
      }

      if ((status == Status.SUCCESSFUL && previousStatus != Status.SUCCESSFUL)
         || (status == Status.FAILED && previousStatus != Status.FAILED))
      {
         afterBuildFinished(buildStatus);
         return;
      }
   }

   /**
    * Perform actions after build is finished.
    *
    * @param buildStatus status of build job
    */
   private void afterBuildFinished(BuildStatus buildStatus)
   {
      // TODO IDEX-57
      // Problem with using Websocket
      //      try
      //      {
      //         IDE.messageBus().unsubscribe(buildStatusChannel, buildStatusHandler);
      //      }
      //      catch (Exception e)
      //      {
      //         // nothing to do
      //      }

      setBuildInProgress(false);
      previousStatus = buildStatus.getStatus();

      StringBuilder message =
         new StringBuilder("Finished building project <b>").append(project.getPath().substring(1))
            .append("</b>.\r\nResult: ").append(buildStatus.getStatus());

      if (buildStatus.getStatus() == Status.SUCCESSFUL)
      {
         // TODO IDEX-57
         // Research: is it good solution?
         //         IDE.fireEvent(new OutputEvent(BUILD_SUCCESS, Type.INFO));
         console.print(BUILD_SUCCESS);

         statusHandler.requestFinished(projectId);
         if (projectId != null)
         {
            writeBuildInfo(buildStatus);
            checkIfProjectIsUnderWatching();
         }
         if (publishAfterBuild)
         {
            getPublishArtifactResult();
         }
      }
      else if (buildStatus.getStatus() == Status.FAILED)
      {
         // TODO IDEX-57
         // Research: is it good solution?
         //         IDE.fireEvent(new OutputEvent(BUILD_FAILED, Type.ERROR));
         console.print(BUILD_FAILED);

         String errorMessage = buildStatus.getError();
         String exceptionMessage = "Building of project failed";
         if (errorMessage != null && !errorMessage.equals("null"))
         {
            message.append("\r\n" + errorMessage);
            exceptionMessage += ": " + errorMessage;
         }

         statusHandler.requestError(projectId, new Exception("Building of project failed", new Throwable(
            exceptionMessage)));
      }
      showBuildMessage(message.toString());
      view.stopAnimation();
      // TODO IDEX-57
      // Research: is it good solution?
      //      IDE.fireEvent(new ProjectBuiltEvent(buildStatus));
      eventBus.fireEvent(new ProjectBuiltEvent(buildStatus));
   }

   /**
    * Getting information about publish artifact process for its only suggest dependency
    *
    */
   private void getPublishArtifactResult()
   {
      try
      {
         StringBuilder builder = new StringBuilder();
         BuilderClientService.getInstance().result(buildID,
            new AsyncRequestCallback<StringBuilder>(new StringUnmarshaller(builder))
            {
               @Override
               protected void onSuccess(StringBuilder result)
               {
                  JSONObject json = JSONParser.parseStrict((result.toString())).isObject();
                  if (json.containsKey("artifactDownloadUrl"))
                  {
                     String artifactUrl = json.get("artifactDownloadUrl").isString().stringValue();
                     // TODO IDEX-57
                     // Research: is it good solution?
                     //                     IDE.fireEvent(new OutputEvent("You can download your artifact :<a href=" + artifactUrl
                     //                        + " target=\"_blank\">" + artifactUrl + "</a>", Type.INFO));
                     console.print("You can download your artifact :<a href=" + artifactUrl + " target=\"_blank\">"
                        + artifactUrl + "</a>");
                  }
                  if (json.containsKey("suggestDependency"))
                  {
                     String dep = json.get("suggestDependency").isString().stringValue();
                     //format XML
                     String res = formatDepXml(dep);
                     // TODO IDEX-57
                     // Research: is it good solution?
                     //                     IDE.fireEvent(new OutputEvent("Dependency for your pom:<br><span style=\"color:black;\">" + res
                     //                        + "</span>", Type.INFO));
                     console.print("Dependency for your pom:<br><span style=\"color:black;\">" + res + "</span>");
                  }
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  // nothing to do
               }
            });
      }
      catch (RequestException e)
      {
         e.printStackTrace();
      }
   }

   private void writeBuildInfo(BuildStatus buildStatus)
   {
      project.getProperties().add(new Property(LAST_SUCCESS_BUILD, buildStatus.getTime()));
      project.getProperties().add(new Property(ARTIFACT_DOWNLOAD_URL, buildStatus.getDownloadUrl()));

      project.flushProjectProperties(new AsyncCallback<Project>()
      {
         @Override
         public void onSuccess(Project result)
         {
            //Nothing todo
         }

         @Override
         public void onFailure(Throwable caught)
         {
            //Ignore this exception
         }
      });
   }

   private void checkIfProjectIsUnderWatching()
   {
      project.refreshProperties(new AsyncCallback<Project>()
      {
         @Override
         public void onSuccess(Project result)
         {
            JsonArray<Property> properties = result.getProperties();
            for (int i = 0; i < properties.size(); i++)
            {
               Property property = properties.get(i);
               if ("vfs:lastUpdateTime".equals(property.getName()))
               {
                  return;
               }
            }
            startWatchingProjectChanges();
         }

         @Override
         public void onFailure(Throwable caught)
         {
         }
      });
   }

   private void startWatchingProjectChanges()
   {
      // TODO IDEX-57
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
    * @param message message for output
    */
   private void showBuildMessage(String message)
   {
      // TODO IDEX-57
      // Review and change to new architecture
      //      if (display != null)
      //      {
      //         if (isViewClosed)
      //         {
      //            IDE.getInstance().openView(display.asView());
      //            isViewClosed = false;
      //         }
      //         else
      //         {
      //            display.asView().activate();
      //         }
      //      }
      //      else
      //      {
      //         display = GWT.create(Display.class);
      //         IDE.getInstance().openView(display.asView());
      //         bindDisplay();
      //         isViewClosed = false;
      //      }
      //

      // TODO IDEX-57
      // View does nothing
      view.showMessageInOutput(message);
      // This was added because need to see some message
      console.print(message);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onClearOutputClicked()
   {
      view.clearOutput();
   }

   private boolean makeSelectionCheck()
   {
      // TODO IDEX-57
      // Check: does this snippent need? 
      //      if (selectedItems == null || selectedItems.size() <= 0)
      //      {
      //         Dialogs.getInstance().showInfo(BuilderExtension.LOCALIZATION_CONSTANT.selectedItemsFail());
      //         return false;
      //      }

      //      if (!(selectedItems.get(0) instanceof ItemContext) || ((ItemContext)selectedItems.get(0)).getProject() == null)
      //      {
      //         Dialogs.getInstance().showInfo("Project is not selected.");
      //         return false;
      //      }

      if (resourceProvider.getActiveProject() == null)
      {
         Window.alert("Project is not selected.");
         return false;
      }

      return true;
   }

   /**
    * @param dep
    * @return
    */
   private String formatDepXml(String dep)
   {

      String formatStr = SafeHtmlUtils.htmlEscape(dep)//
         .replaceFirst("&gt;&lt;", "&gt;<br>&nbsp;&nbsp;&lt;")//
         .replaceFirst("&gt;&lt;", "&gt;<br>&nbsp;&nbsp;&lt;")//
         .replaceFirst("&gt;&lt;", "&gt;<br>&nbsp;&nbsp;&lt;");
      if (formatStr.contains("&lt;type&gt;"))
      {
         formatStr = formatStr.replaceFirst("&gt;&lt;", "&gt;<br>&nbsp;&nbsp;&lt;");
      }
      return formatStr.replaceFirst("&gt;&lt;", "&gt;<br>&lt;");
   }

   /**
    * Handler for processing Maven build status which is received over WebSocket connection.
    */
   private SubscriptionHandler<BuildStatus> buildStatusHandler = new SubscriptionHandler<BuildStatus>(
      new AutoBeanUnmarshallerWS<BuildStatus>(BuilderExtension.AUTO_BEAN_FACTORY.create(BuildStatus.class)))
   {
      @Override
      protected void onSuccess(BuildStatus buildStatus)
      {
         updateBuildStatus(buildStatus);
      }

      @Override
      protected void onFailure(Throwable exception)
      {
         // TODO IDEX-57
         // Problem with using Websocket
         //         try
         //         {
         //            IDE.messageBus().unsubscribe(buildStatusChannel, this);
         //         }
         //         catch (WebSocketException e)
         //         {
         //            // nothing to do
         //         }

         setBuildInProgress(false);
         view.stopAnimation();
         // TODO IDEX-57
         // Research: is it good solution?
         //         IDE.fireEvent(new ExceptionThrownEvent(exception));
         console.print(exception.getMessage());
      }
   };

   /**
    * Deserializer for responses body.
    */
   private class StringUnmarshaller implements Unmarshallable<StringBuilder>
   {
      protected StringBuilder builder;

      public StringUnmarshaller(StringBuilder builder)
      {
         this.builder = builder;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public void unmarshal(Response response)
      {
         builder.append(response.getText());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public StringBuilder getPayload()
      {
         return builder;
      }
   }
}