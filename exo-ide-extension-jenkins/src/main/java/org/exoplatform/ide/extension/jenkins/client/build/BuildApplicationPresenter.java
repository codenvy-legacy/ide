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

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Image;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
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
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ChildrenUnmarshaller;
import org.exoplatform.ide.vfs.client.model.ItemContext;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class BuildApplicationPresenter extends GitPresenter implements BuildApplicationHandler,
   UserInfoReceivedHandler, ViewClosedHandler
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

   private Status prevStatus = null;

   private boolean buildInProgress = false;

   /**
    * Project for build on Jenkins.
    */
   private ProjectModel project;

   /**
    *
    */
   public BuildApplicationPresenter()
   {
      IDE.addHandler(BuildApplicationEvent.TYPE, this);
      IDE.addHandler(UserInfoReceivedEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
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
         message += "Building of project <b>" + project.getPath() + "</b> is performed.";

         Dialogs.getInstance().showError(message);
         return;
      }

      project = event.getProject();
      if (project == null && makeSelectionCheck())
      {
         project = ((ItemContext)selectedItems.get(0)).getProject();
      }
      checkIsGitRepository(project);

   }

   private void checkIsGitRepository(final ProjectModel project)
   {
      try
      {
         VirtualFileSystem.getInstance().getChildren(
            project,
            new org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback<List<Item>>(
               new ChildrenUnmarshaller(new ArrayList<Item>()))
            {

               @Override
               protected void onSuccess(List<Item> result)
               {
                  for (Item item : result)
                  {
                     if (".git".equals(item.getName()))
                     {
                        beforeBuild();
                        return;
                     }
                  }
                  initRepository(project.getId());
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  initRepository(project.getId());
               }
            });
      }
      catch (RequestException e)
      {
         e.printStackTrace();
      }
   }

   /**
    * Perform check, that job already exists.
    * If it doesn't exist, then create job.
    */
   private void beforeBuild()
   {
      jobName = (String)project.getPropertyValue("jenkins-job");
      if (jobName != null && !jobName.isEmpty())
      {
         build(jobName);
      }
      else
      {
         createJob();
      }
   }

   /**
    * Create new Jenkins job.
    * @param repository repository URL (public location of local repository)
    */
   private void createJob()
   {
      //dummy check that user name is e-mail.
      //Jenkins create git tag on build. Marks user as author of tag.
      String mail = userInfo.getName().contains("@") ? userInfo.getName() : userInfo.getName() + "@exoplatform.local";
      String uName = userInfo.getName().split("@")[0];//Jenkins don't alow in job name '@' character
      JenkinsService.get().createJenkinsJob(uName + "-" + getProjectName() + "-" + Random.nextInt(Integer.MAX_VALUE), uName, mail, vfs.getId(), project.getId(), new AsyncRequestCallback<Job>()
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
      String projectName = project.getPath();
      if (projectName.endsWith("/"))
      {
         projectName = projectName.substring(0, projectName.length() - 1);
      }
      projectName = projectName.substring(projectName.lastIndexOf("/") + 1, projectName.length() - 1);
      return projectName;
   }

   /**
    * Start building application
    * @param jobName name of Jenkins job
    */
   private void build(String jobName)
   {
      String projectId = "";
      JenkinsService.get().buildJob(vfs.getId(), projectId, jobName, new AsyncRequestCallback<String>()
      {
         @Override
         protected void onSuccess(String result)
         {
            buildInProgress = true;

            showBuildMessage("Building project <b>" + project.getPath() + "</b>");

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

      String message =
         "Building project <b>" + project.getPath() + "</b> has been finished.\r\nResult: "
            + status.getLastBuildResult() == null ? "Unknown" : status.getLastBuildResult();

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
         JenkinsService.get().jobStatus(vfs.getId(), project.getId(), jobName, new AsyncRequestCallback<JobStatus>()
         {
            @Override
            protected void onSuccess(JobStatus status)
            {
               updateJobStatus(status);

               if (status.getStatus() == Status.END)
               {
                  IDE.fireEvent(new ApplicationBuiltEvent(status));

                  JenkinsService.get().getJenkinsOutput(vfs.getId(), project.getId(), jobName,
                     new AsyncRequestCallback<String>()
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
    * Initialize Git repository.
    * 
    * @param path working directory of the repository
    */
   private void initRepository(final String projectId)
   {
      try
      {
         GitClientService.getInstance().init(vfs.getId(), projectId, false,
            new org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback<String>()
            {
               @Override
               protected void onSuccess(String result)
               {
                  showBuildMessage(GitExtension.MESSAGES.initSuccess());
                  IDE.fireEvent(new RefreshBrowserEvent());
                  createJob();
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  exception.printStackTrace();
                  String errorMessage =
                     (exception.getMessage() != null && exception.getMessage().length() > 0) ? exception.getMessage()
                        : GitExtension.MESSAGES.initFailed();
                  IDE.fireEvent(new OutputEvent(errorMessage, Type.ERROR));
               }
            });
      }
      catch (RequestException e)
      {
         e.printStackTrace();
         String errorMessage =
            (e.getMessage() != null && e.getMessage().length() > 0) ? e.getMessage() : GitExtension.MESSAGES
               .initFailed();
         IDE.fireEvent(new OutputEvent(errorMessage, Type.ERROR));
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
}
