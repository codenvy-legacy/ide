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

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.maven.client.BuilderAsyncRequestCallback;
import org.exoplatform.ide.extension.maven.client.BuilderClientService;
import org.exoplatform.ide.extension.maven.client.BuilderExtension;
import org.exoplatform.ide.extension.maven.client.control.BuildProjectControl;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.vfs.client.model.ItemContext;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.List;

/**
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: BuildProjectPresenter.java Feb 17, 2012 5:39:10 PM azatsarynnyy $
 *
 */
public class BuildProjectPresenter implements BuildProjectHandler, ItemsSelectedHandler, ViewClosedHandler,
   VfsChangedHandler
{

   private static final String UNABLE_TO_GET_GIT_URL = BuilderExtension.LOCALIZATION_CONSTANT.unableToGetGitUrl();

   private static final String NEED_INITIALIZE_GIT = BuilderExtension.LOCALIZATION_CONSTANT.needInitializeGit();

   private String gitUrl = null;

   private String buildID = null;

   /**
    * Delay in millisecond between build status request
    */
   private static final int delay = 3000;

   private String prevStatus = "";

   private boolean buildInProgress = false;

   public interface Display extends IsView
   {
      void output(String text);

      void startAnimation();

      void stopAnimation();
   }

   private Display display;

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
         String message = BuilderExtension.LOCALIZATION_CONSTANT.buildInProgress(project.getPath().substring(1));
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

   //      try
   //      {
   //         VirtualFileSystem.getInstance().getChildren(project,
   //            new AsyncRequestCallback<List<Item>>(new ChildrenUnmarshaller(new ArrayList<Item>()))
   //            {
   //
   //               @Override
   //               protected void onSuccess(List<Item> result)
   //               {
   //                  for (Item item : result)
   //                  {
   //                     if (".git".equals(item.getName()))
   //                     {
   //                        return;
   //                     }
   //                  }
   //                  initRepository(project);
   //               }
   //
   //               @Override
   //               protected void onFailure(Throwable exception)
   //               {
   //                  initRepository(project);
   //               }
   //            });
   //      }
   //      catch (RequestException e)
   //      {
   //      }
   //   }

   /**
    * Initialize Git repository.
    * 
    * @param project
    */
   private void initRepository(final ProjectModel project)
   {
      try
      {
         GitClientService.getInstance().init(vfs.getId(), project.getId(), project.getName(), false,
            new AsyncRequestCallback<String>()
            {
               @Override
               protected void onSuccess(String result)
               {
                  showBuildMessage(GitExtension.MESSAGES.initSuccess());
                  IDE.fireEvent(new RefreshBrowserEvent());
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  String errorMessage =
                     (exception.getMessage() != null && exception.getMessage().length() > 0) ? exception.getMessage()
                        : GitExtension.MESSAGES.initFailed();
                  IDE.fireEvent(new OutputEvent(errorMessage, Type.ERROR));
               }
            });
      }
      catch (RequestException e)
      {
         String errorMessage =
            (e.getMessage() != null && e.getMessage().length() > 0) ? e.getMessage() : GitExtension.MESSAGES
               .initFailed();
         IDE.fireEvent(new OutputEvent(errorMessage, Type.ERROR));
      }
   }

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

   private void doBuild()
   {
      try
      {
         BuilderClientService.getInstance().build(gitUrl,
            new BuilderAsyncRequestCallback<StringBuilder>(new StringUnmarshaller(new StringBuilder()))
            {
               @Override
               protected void onSuccess(StringBuilder result)
               {
                  buildID = result.substring(result.lastIndexOf("/") + 1);

                  buildInProgress = true;

                  showBuildMessage("Building project <b>" + project.getPath().substring(1) + "</b>");

                  display.startAnimation();

                  prevStatus = null;
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

   private Timer refreshBuildStatusTimer = new Timer()
   {
      @Override
      public void run()
      {
         try
         {
            BuilderClientService.getInstance().status(buildID,
               new AsyncRequestCallback<StringBuilder>(new StringUnmarshaller(new StringBuilder()))
               {
                  @Override
                  protected void onSuccess(StringBuilder response)
                  {
                     String status = "";
                     if (response.indexOf("SUCCESSFUL") != -1)
                     {
                        status = "SUCCESSFUL";
                     }
                     else if (response.indexOf("IN_PROGRESS") != -1)
                     {
                        status = "IN_PROGRESS";
                     }
                     else if (response.indexOf("FAILED") != -1)
                     {
                        status = "FAILED";
                     }

                     updateBuildStatus(status);

                     if (status.equals("IN_PROGRESS"))
                     {
                        schedule(delay);
                     }
                     else if (status.equals("FAILED"))
                     {
                        showBuildMessage("BUILD FAILED!");
                        showLog();
                     }
                     else
                     {
                        String downloadUrl =
                           response.substring(response.indexOf("downloadUrl\":\"") + "downloadUrl\":\"".length(),
                              response.indexOf("\"}"));

                        StringBuilder message =
                           new StringBuilder("You can download result of build by <a href=\"").append(downloadUrl)
                              .append("\">this link</a>");

                        showBuildMessage(message.toString());
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
            IDE.fireEvent(new ExceptionThrownEvent(e));
         }
      }
   };

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
    * @param status
    */
   private void updateBuildStatus(String status)
   {
      if (status.equals("IN_PROGRESS") && !prevStatus.equals("IN_PROGRESS"))
      {
         setBuildStatusInProgress(status);
         return;
      }

      if ((status.equals("SUCCESSFUL") && !prevStatus.equals("SUCCESSFUL"))
         || (status.equals("FAILED") && !prevStatus.equals("FAILED")))
      {
         setBuildStatusFinished(status);
         return;
      }
   }

   /**
    * Sets Building status: IN_PROGRESS
    * 
    * @param status
    */
   private void setBuildStatusInProgress(String status)
   {
      prevStatus = "IN_PROGRESS";
      //showBuildMessage("Status: " + status);
   }

   /**
    * Sets Building status: SUCCESSFUL
    * 
    * @param status
    */
   private void setBuildStatusFinished(String status)
   {
      buildInProgress = false;

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

      //prevStatus = "SUCCESSFUL"; // prevStatus = Status.FAILED;

      String message =
         "Building project <b>" + project.getPath().substring(1) + "</b> has been finished.\r\nResult: "
            + ((status.length() == 0) ? "Unknown" : status);

      showBuildMessage(message);
      display.stopAnimation();
   }

   /**
    * @param message
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
         display = null;
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
