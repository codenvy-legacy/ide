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
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Timer;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.maven.client.BuilderClientService;
import org.exoplatform.ide.extension.maven.client.BuilderExtension;
import org.exoplatform.ide.extension.maven.client.control.BuildProjectControl;
import org.exoplatform.ide.extension.maven.shared.BuildStatus;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.vfs.client.model.ItemContext;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.List;

/**
 * Presenter for created builder view. The view must be pointed in Views.gwt.xml.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: BuildProjectPresenter.java Feb 17, 2012 5:39:10 PM azatsarynnyy $
 *
 */
public class BuildProjectPresenter implements BuildProjectHandler, ItemsSelectedHandler, ViewClosedHandler,
   VfsChangedHandler
{
   public interface Display extends IsView
   {
      void output(String text);

      void startAnimation();

      void stopAnimation();
   }

   private Display display;

   private static final String UNABLE_TO_GET_GIT_URL = BuilderExtension.LOCALIZATION_CONSTANT.unableToGetGitUrl();

   private static final String NEED_INITIALIZE_GIT = BuilderExtension.LOCALIZATION_CONSTANT.needInitializeGit();

   private static final BuildStatus.Status BUILD_STATUS_SUCCESSFUL = BuildStatus.Status.SUCCESSFUL;

   private static final BuildStatus.Status BUILD_STATUS_FAILED = BuildStatus.Status.FAILED;

   private static final BuildStatus.Status BUILD_STATUS_IN_PROGRESS = BuildStatus.Status.IN_PROGRESS;

   /**
    * Git repository url of projects.
    */
   private String gitUrl = null;

   /**
    * The build's identifier.
    */
   private String buildID = null;

   /**
    * Delay in millisecond between build status request.
    */
   private static final int delay = 3000;

   /**
    * Status of previously build.
    */
   private BuildStatus.Status previousStatus = null;

   /**
    * Build of another project is performed.
    */
   private boolean buildInProgress = false;

   /**
    * View closed flag.
    */
   private boolean closed = true;

   /**
    * Selected items in browser tree.
    */
   protected List<Item> selectedItems;

   /**
    * Current virtual file system.
    */
   protected VirtualFileSystemInfo vfs;

   /**
    * Project for build.
    */
   private ProjectModel project;

   public BuildProjectPresenter()
   {
      IDE.getInstance().addControl(new BuildProjectControl());

      IDE.addHandler(BuildProjectEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
      IDE.addHandler(ItemsSelectedEvent.TYPE, this);
      IDE.addHandler(VfsChangedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.framework.project.BuildProjectHandler#onBuildProject(org.exoplatform.ide.client.framework.project.BuildProjectEvent)
    */
   @Override
   public void onBuildProject(BuildProjectEvent event)
   {
      if (buildInProgress)
      {
         String message = BuilderExtension.LOCALIZATION_CONSTANT.buildInProgress(project.getPath());
         Dialogs.getInstance().showError(message);
         return;
      }

      project = event.getProject();
      if (project == null && makeSelectionCheck())
      {
         project = ((ItemContext)selectedItems.get(0)).getProject();
      }

      if (isGitRepository(project))
      {
         build();
      }
      else
      {
         Dialogs.getInstance().showError(NEED_INITIALIZE_GIT);
      }
   }

   /**
    * Сhecks whether the repository is initialized.
    * 
    * @param project
    */
   private boolean isGitRepository(final ProjectModel project)
   {
      for (Item item : project.getChildren().getItems())
      {
         if (".git".equals(item.getName()))
         {
            return true;
         }
      }
      return false;
   }

   /**
    * Get the Git repository url and start the build of project.
    */
   private void build()
   {
      String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
      try
      {
         GitClientService.getInstance().getGitReadOnlyUrl(vfs.getId(), projectId,
            new AsyncRequestCallback<StringBuilder>(new StringUnmarshaller(new StringBuilder()))
            {
               @Override
               protected void onSuccess(StringBuilder result)
               {
                  gitUrl = result.toString();
                  doBuild();
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  String errorMessage =
                     (exception.getMessage() != null && exception.getMessage().length() > 0) ? exception.getMessage()
                        : UNABLE_TO_GET_GIT_URL;
                  IDE.fireEvent(new OutputEvent(errorMessage, Type.ERROR));
               }
            });
      }
      catch (RequestException e)
      {
         String errorMessage =
            (e.getMessage() != null && e.getMessage().length() > 0) ? e.getMessage() : UNABLE_TO_GET_GIT_URL;
         IDE.fireEvent(new OutputEvent(errorMessage, Type.ERROR));
      }
   }

   /**
    * Start the build of project.
    */
   private void doBuild()
   {
      try
      {
         BuilderClientService.getInstance().build(gitUrl,
            new AsyncRequestCallback<StringBuilder>(new StringUnmarshaller(new StringBuilder()))
            {
               @Override
               protected void onSuccess(StringBuilder result)
               {
                  buildID = result.substring(result.lastIndexOf("/") + 1);

                  buildInProgress = true;

                  showBuildMessage("Building project <b>" + project.getPath() + "</b>");

                  display.startAnimation();

                  previousStatus = null;
                  refreshBuildStatusTimer.schedule(delay);
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  IDE.fireEvent(new OutputEvent(exception.getMessage(), Type.INFO));
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new OutputEvent(e.getMessage(), Type.INFO));
      }
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

                  BuildStatus.Status status = response.getStatus();
                  if (status == BUILD_STATUS_IN_PROGRESS)
                  {
                     schedule(delay);
                  }
                  else if (status == BUILD_STATUS_FAILED)
                  {
                     showLog();
                  }
               }

               protected void onFailure(Throwable exception)
               {
                  buildInProgress = false;
                  IDE.fireEvent(new ExceptionThrownEvent(exception));
               };

            });
         }
         catch (RequestException e)
         {
            buildInProgress = false;
            IDE.fireEvent(new ExceptionThrownEvent(e));
         }
      }
   };

   /**
    * Output a log of build.
    */
   private void showLog()
   {
      try
      {
         BuilderClientService.getInstance().log(buildID,
            new AsyncRequestCallback<StringBuilder>(new StringUnmarshaller(new StringBuilder()))
            {
               @Override
               protected void onSuccess(StringBuilder result)
               {
                  showBuildMessage(result.toString());
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  IDE.fireEvent(new OutputEvent(exception.getMessage(), Type.INFO));
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new OutputEvent(e.getMessage(), Type.INFO));
      }
   }

   /**
    * Check for status and display necessary messages.
    * 
    * @param buildStatus status of build
    */
   private void updateBuildStatus(BuildStatus buildStatus)
   {
      BuildStatus.Status status = buildStatus.getStatus();

      if (status == BUILD_STATUS_IN_PROGRESS && previousStatus != BUILD_STATUS_IN_PROGRESS)
      {
         previousStatus = BUILD_STATUS_IN_PROGRESS;
         return;
      }

      if ((status == BUILD_STATUS_SUCCESSFUL && previousStatus != BUILD_STATUS_SUCCESSFUL)
         || (status == BUILD_STATUS_FAILED && previousStatus != BUILD_STATUS_FAILED))
      {
         afterBuildFinished(buildStatus);
         return;
      }
   }

   /**
    * Perform actions after build is finished.
    * 
    * @param buildStatus status of build
    */
   private void afterBuildFinished(BuildStatus buildStatus)
   {
      buildInProgress = false;
      previousStatus = buildStatus.getStatus();

      StringBuilder message =
         new StringBuilder("Building project <b>").append(project.getPath())
            .append("</b> has been finished.\r\nResult: ").append(buildStatus.getStatus());

      if (buildStatus.getStatus() == BUILD_STATUS_SUCCESSFUL)
      {
         message.append("\r\nYou can download result of build by <a href=\"").append(buildStatus.getDownloadUrl())
            .append("\">this link</a>");
      }
      else if (buildStatus.getStatus() == BUILD_STATUS_FAILED)
      {
         message.append(buildStatus.getError());
      }

      showBuildMessage(message.toString());
      display.stopAnimation();
   }

   /**
    * Output the message and activate view if necessary.
    * 
    * @param message message for output
    */
   private void showBuildMessage(String message)
   {
      if (display != null)
      {
         if (closed)
         {
            IDE.getInstance().openView(display.asView());
            closed = false;
         }
         else
         {
            display.asView().activate();
         }
      }
      else
      {
         display = GWT.create(Display.class);
         IDE.getInstance().openView(display.asView());
         closed = false;
      }

      display.output(message);
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent)
    */
   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         closed = true;
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent)
    */
   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      this.selectedItems = event.getSelectedItems();
   }

   /**
    * @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework.application.event.VfsChangedEvent)
    */
   @Override
   public void onVfsChanged(VfsChangedEvent event)
   {
      this.vfs = event.getVfsInfo();
   }

   protected boolean makeSelectionCheck()
   {
      if (selectedItems == null || selectedItems.size() <= 0)
      {
         Dialogs.getInstance().showInfo(GitExtension.MESSAGES.selectedItemsFail());
         return false;
      }

      if (!(selectedItems.get(0) instanceof ItemContext) || ((ItemContext)selectedItems.get(0)).getProject() == null)
      {
         Dialogs.getInstance().showInfo("Project is not selected.");
         return false;
      }

      if (selectedItems.get(0).getPath().isEmpty() || selectedItems.get(0).getPath().equals("/"))
      {
         Dialogs.getInstance().showInfo(GitExtension.MESSAGES.selectedWorkace());
         return false;
      }

      return true;
   }

   /**
    * Deserializer for response's body.
    */
   private class StringUnmarshaller implements Unmarshallable<StringBuilder>
   {

      protected StringBuilder builder;

      /**
       * @param callback
       */
      public StringUnmarshaller(StringBuilder builder)
      {
         this.builder = builder;
      }

      /**
       * @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response)
       */
      @Override
      public void unmarshal(Response response)
      {
         builder.append(response.getText());
      }

      @Override
      public StringBuilder getPayload()
      {
         return builder;
      }
   }
}
