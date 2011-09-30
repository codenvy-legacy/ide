/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.extension.jenkins.client.build;

import com.google.gwt.http.client.RequestException;

import com.google.gwt.user.client.Random;

import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.userinfo.UserInfo;
import org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedEvent;
import org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedHandler;
import org.exoplatform.ide.extension.jenkins.client.JenkinsExtension;
import org.exoplatform.ide.extension.jenkins.client.JenkinsService;
import org.exoplatform.ide.extension.jenkins.client.JobResult;
import org.exoplatform.ide.extension.jenkins.client.event.ApplicationBuiltEvent;
import org.exoplatform.ide.extension.jenkins.client.event.BuildApplicationEvent;
import org.exoplatform.ide.extension.jenkins.client.event.BuildApplicationHandler;
import org.exoplatform.ide.extension.jenkins.shared.Job;
import org.exoplatform.ide.extension.jenkins.shared.JobStatus;
import org.exoplatform.ide.extension.jenkins.shared.JobStatus.Status;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitClientUtil;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.git.client.marshaller.WorkDirResponse;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.shared.Item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Image;

/**
 * 
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class BuildApplicationPresenter extends GitPresenter implements BuildApplicationHandler,
   UserInfoReceivedHandler, ViewClosedHandler, VfsChangedHandler
{

   public interface Display extends IsView
   {

      void output(String text);

      void clearOutput();

      void startAnimation();

      void stopAnimation();

      void setBlinkIcon(Image icon, boolean blinking);

   }

   private Display display;

   private boolean closed = true;

   private String jobName;

   private UserInfo userInfo;

   /**
    * Delay in millisecond between job status request  
    */
   private static final int delay = 10000;

   private String restContext;

   private Status prevStatus = null;

   private String entryPoint;

   private boolean buildInProgress = false;

   /**
    * The id of folder, which contains project to build.
    * If id is null, than get the id of selected folder.
    */
   private String folderId;

   /**
    * @param eventBus
    */
   public BuildApplicationPresenter(String restContext)
   {
      super(IDE.EVENT_BUS);
      this.restContext = restContext;
      IDE.EVENT_BUS.addHandler(BuildApplicationEvent.TYPE, this);
      IDE.EVENT_BUS.addHandler(UserInfoReceivedEvent.TYPE, this);
      IDE.EVENT_BUS.addHandler(ViewClosedEvent.TYPE, this);
      IDE.EVENT_BUS.addHandler(VfsChangedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.extension.jenkins.client.event.BuildApplicationHandler#onBuildApplication(org.exoplatform.ide.extension.jenkins.client.event.BuildApplicationEvent)
    */
   @Override
   public void onBuildApplication(BuildApplicationEvent event)
   {
      if (buildInProgress)
      {
         String message = "You can not start the build of two projects at the same time.<br>";
         message += "Building of project <b>" + getProjectDir() + "</b> is performed.";

         Dialogs.getInstance().showError(message);
         return;
      }

      //the id of folder to build can be received from event.
      //In other case, it will be got from selected items.
      folderId = event.getFolderId();

      getWorkDir();
   }

   /**
    * @see org.exoplatform.ide.git.client.GitPresenter#onWorkDirReceived()
    */
   @Override
   public void onWorkDirReceived()
   {
      beforeBuild();
   }

   /**
    * Perform check, that job already exists.
    * If it doesn't exist, then create job.
    */
   private void beforeBuild()
   {
      String url = "";
      if (folderId != null)
      {
         url = folderId;
      }
      else
      {
         Item item = selectedItems.get(0);
         if (item instanceof FolderModel)
            url = item.getId();
         else
            url = item.getParentId();
      }
      JenkinsService.get().getFileContent(url, ".jenkins-job", new AsyncRequestCallback<String>()
      {
         @Override
         protected void onSuccess(String result)
         {
            jobName = result;
            build(result);
         }

         /**
           * @see org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback#onFailure(java.lang.Throwable)
           */
         @Override
         protected void onFailure(Throwable exception)
         {
            if (exception instanceof ServerException)
            {
               ServerException ex = (ServerException)exception;
               if (ex.getHTTPStatus() == HTTPStatus.NOT_FOUND)
               {
                  createJob(GitClientUtil.getPublicGitRepoUrl(workDir, restContext));
                  return;
               }
            }
            super.onFailure(exception);
         }
      });
   }

   /**
    * Create new Jenkins job.
    * @param repository repository URL (public location of local repository)
    */
   private void createJob(String repository)
   {
      //for test
      //repository = "git://github.com/EvgenVidolob/TestJavaProject.git";

      //dummy check that user name is e-mail.
      //Jenkins create git tag on build. Marks user as author of tag.
      String mail = userInfo.getName().contains("@") ? userInfo.getName() : userInfo.getName() + "@exoplatform.local";

      String uName = userInfo.getName().split("@")[0];//Jenkins don't alow in job name '@' character
      JenkinsService.get().createJenkinsJob(uName + "-" + getProjectName() + "-" + Random.nextInt(Integer.MAX_VALUE),
         repository, uName, mail, workDir, new AsyncRequestCallback<Job>()
         {
            @Override
            protected void onSuccess(Job result)
            {
               build(result.getName());
               jobName = result.getName();
            }
         });
   }

   /**
   * Get project name (last URL segment of workDir value)
   * @return project name
   */
   private String getProjectName()
   {
      String projectName = workDir;
      if (projectName.endsWith("/"))
      {
         projectName = projectName.substring(0, projectName.length() - 1);
      }
      projectName = projectName.substring(projectName.lastIndexOf("/") + 1, projectName.length() - 1);
      return projectName;
   }

   /**
    * Get project's directory ( from root of workspace ).
    * 
    * @return
    */
   private String getProjectDir()
   {
      String wd = workDir;
      if (wd.endsWith("/"))
      {
         wd = wd.substring(0, wd.length() - 1);
      }
      wd = wd.substring(entryPoint.length() - 1);
      //      wd = wd.substring(0, wd.lastIndexOf("/"));
      //
      //      String ep = entryPoint;
      //      if (ep.endsWith("/"))
      //      {
      //         ep = ep.substring(0, ep.length() - 1);
      //      }
      //      ep = ep.substring(0, ep.lastIndexOf("/"));

      return wd;//.substring(ep.length());
   }

   /**
    * Start building application
    * @param jobName name of Jenkins job
    */
   private void build(String jobName)
   {
      JenkinsService.get().buildJob(jobName, new AsyncRequestCallback<String>()
      {
         @Override
         protected void onSuccess(String result)
         {
            buildInProgress = true;

            String projectDir = getProjectDir();
            showBuildMessage("Building project <b>" + projectDir + "</b>");

            display.startAnimation();
            display.setBlinkIcon(new Image(JenkinsExtension.RESOURCES.grey()), true);

            prevStatus = null;
            refreshJobStatusTimer.schedule(delay);
         }
      });
   }

   /**
    * Sets Building status: Queue
    * 
    * @param status
    */
   private void setBuildStatusQueue(JobStatus status)
   {
      prevStatus = Status.QUEUE;
      showBuildMessage("Status: " + status.getStatus());
      display.setBlinkIcon(new Image(JenkinsExtension.RESOURCES.grey()), true);
   }

   /**
    * Sets Building status: Building
    * 
    * @param status
    */
   private void setBuildStatusBuilding(JobStatus status)
   {
      prevStatus = Status.BUILD;
      showBuildMessage("Status: " + status.getStatus());
      display.setBlinkIcon(new Image(JenkinsExtension.RESOURCES.blue()), true);
   }

   /**
    * Sets Building status: Finished
    * 
    * @param status
    */
   private void setBuildStatusFinished(JobStatus status)
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

      prevStatus = Status.END;

      String projectDir = getProjectDir();

      String message =
         "Building project <b>" + projectDir + "</b> has been finished.\r\nResult: " + status.getLastBuildResult() == null
            ? "Unknown" : status.getLastBuildResult();

      showBuildMessage(message);
      display.stopAnimation();

      if (status.getLastBuildResult() == null)
      {
         display.setBlinkIcon(new Image(JenkinsExtension.RESOURCES.red()), false);
         return;
      }

      switch (JobResult.valueOf(status.getLastBuildResult()))
      {
         case SUCCESS :
            display.setBlinkIcon(new Image(JenkinsExtension.RESOURCES.blue()), false);
            break;

         case FAILURE :
            display.setBlinkIcon(new Image(JenkinsExtension.RESOURCES.red()), false);
            break;

         default :
            display.setBlinkIcon(new Image(JenkinsExtension.RESOURCES.yellow()), false);
            break;
      }
   }

   /**
    * Check for status and display necessary messages.
    * 
    * @param status
    */
   private void updateJobStatus(JobStatus status)
   {
      if (status.getStatus() == Status.QUEUE && prevStatus != Status.QUEUE)
      {
         setBuildStatusQueue(status);
         return;
      }

      if (status.getStatus() == Status.BUILD && prevStatus != Status.BUILD)
      {
         setBuildStatusBuilding(status);
         return;
      }

      if (status.getStatus() == Status.END && prevStatus != Status.END)
      {
         setBuildStatusFinished(status);
         return;
      }
   }

   private Timer refreshJobStatusTimer = new Timer()
   {
      @Override
      public void run()
      {
         JenkinsService.get().jobStatus(jobName, new AsyncRequestCallback<JobStatus>()
         {
            @Override
            protected void onSuccess(JobStatus status)
            {
               updateJobStatus(status);

               if (status.getStatus() == Status.END)
               {
                  IDE.EVENT_BUS.fireEvent(new ApplicationBuiltEvent(status));

                  JenkinsService.get().getJenkinsOutput(jobName, new AsyncRequestCallback<String>()
                  {
                     @Override
                     protected void onSuccess(String result)
                     {
                        showBuildMessage(result);
                     }
                  });
               }
               else
               {
                  schedule(delay);
               }
            }

            protected void onFailure(Throwable exception)
            {
               buildInProgress = false;
               super.onFailure(exception);
            };

         });
      }
   };

   /**
    * @see org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedHandler#onUserInfoReceived(org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedEvent)
    */
   @Override
   public void onUserInfoReceived(UserInfoReceivedEvent event)
   {
      userInfo = event.getUserInfo();
   }

   /**
    * @see org.exoplatform.ide.git.client.GitPresenter#getWorkDir()
    */
   @Override
   public void getWorkDir()
   {
      //the id of folder, that is the start point to get the workDir.
      final String startDirId;
      if (folderId != null)
      {
         startDirId = folderId;
      }
      else if (selectedItems != null && selectedItems.size() > 0)
      {
         startDirId = selectedItems.get(0).getId();
      }
      else
      {
         Dialogs.getInstance().showInfo(GitExtension.MESSAGES.selectedItemsFail());
         return;
      }

      //First get the working directory of the repository if exists:
      GitClientService.getInstance().getWorkDir(startDirId, new AsyncRequestCallback<WorkDirResponse>()
      {
         @Override
         protected void onSuccess(WorkDirResponse result)
         {
            workDir = result.getWorkDir();
            workDir = (workDir.endsWith("/.git")) ? workDir.substring(0, workDir.lastIndexOf("/.git")) : workDir;
            onWorkDirReceived();
         }

         @Override
         protected void onFailure(Throwable exception)
         {
            initRepository(startDirId);
         }
      });
   }

   /**
    * Initialize Git repository.
    * 
    * @param path working directory of the repository
    */
   private void initRepository(final String path)
   {
      try
      {
         GitClientService.getInstance().init(path, false,
            new org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback<String>()
            {
               @Override
               protected void onSuccess(String result)
               {
                  workDir = path;
                  //eventBus.fireEvent(new OutputEvent(GitExtension.MESSAGES.initSuccess(), Type.INFO));
                  showBuildMessage(GitExtension.MESSAGES.initSuccess());
                  eventBus.fireEvent(new RefreshBrowserEvent());
                  onWorkDirReceived();
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  String errorMessage =
                     (exception.getMessage() != null && exception.getMessage().length() > 0) ? exception.getMessage()
                        : GitExtension.MESSAGES.initFailed();
                  eventBus.fireEvent(new OutputEvent(errorMessage, Type.ERROR));
               }
            });
      }
      catch (RequestException e)
      {
         e.printStackTrace();
         String errorMessage =
            (e.getMessage() != null && e.getMessage().length() > 0) ? e.getMessage() : GitExtension.MESSAGES
               .initFailed();
         eventBus.fireEvent(new OutputEvent(errorMessage, Type.ERROR));
      }
   }

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

   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         closed = true;
      }
   }

   @Override
   public void onVfsChanged(VfsChangedEvent event)
   {
      //TODO not url
      entryPoint = event.getVfsInfo().getId();
   }

}
