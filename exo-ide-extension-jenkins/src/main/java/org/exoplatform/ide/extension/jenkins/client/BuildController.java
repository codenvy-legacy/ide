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
package org.exoplatform.ide.extension.jenkins.client;

import com.google.gwt.user.client.Timer;

import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.control.event.RegisterControlEvent.DockTarget;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.userinfo.UserInfo;
import org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedEvent;
import org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedHandler;
import org.exoplatform.ide.client.framework.vfs.Folder;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.extension.jenkins.client.control.BuildStatusControl;
import org.exoplatform.ide.extension.jenkins.client.event.ApplicationBuiltEvent;
import org.exoplatform.ide.extension.jenkins.client.event.BuildApplicationEvent;
import org.exoplatform.ide.extension.jenkins.client.event.BuildApplicationHandler;
import org.exoplatform.ide.extension.jenkins.client.event.GetJenkinsOutputEvent;
import org.exoplatform.ide.extension.jenkins.client.event.GetJenkinsOutputHandler;
import org.exoplatform.ide.extension.jenkins.client.event.GitRemoteRepositorySelectedEvent;
import org.exoplatform.ide.extension.jenkins.client.event.GitRemoteRepositorySelectedHandler;
import org.exoplatform.ide.extension.jenkins.shared.Job;
import org.exoplatform.ide.extension.jenkins.shared.JobStatus;
import org.exoplatform.ide.extension.jenkins.shared.JobStatus.Status;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.git.client.marshaller.WorkDirResponse;
import org.exoplatform.ide.git.shared.Remote;

import java.util.List;

/**
 * 
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class BuildController extends GitPresenter implements BuildApplicationHandler,
   GitRemoteRepositorySelectedHandler, UserInfoReceivedHandler, GetJenkinsOutputHandler
{

   private String jobName;

   private UserInfo userInfo;

   private BuildStatusControl control;

   /**
    * Delay in millisecond between job status request  
    */
   private static final int delay = 10000;

   /**
    * @param eventBus
    */
   public BuildController()
   {
      super(IDE.EVENT_BUS);
      IDE.EVENT_BUS.addHandler(BuildApplicationEvent.TYPE, this);
      IDE.EVENT_BUS.addHandler(GitRemoteRepositorySelectedEvent.TYPE, this);
      IDE.EVENT_BUS.addHandler(UserInfoReceivedEvent.TYPE, this);
      IDE.EVENT_BUS.addHandler(GetJenkinsOutputEvent.TYPE, this);
      control = new BuildStatusControl();
      IDE.getInstance().addControl(control, DockTarget.STATUSBAR, true);
   }

   /**
    * @see org.exoplatform.ide.extension.jenkins.client.event.BuildApplicationHandler#onBuildApplication(org.exoplatform.ide.extension.jenkins.client.event.BuildApplicationEvent)
    */
   @Override
   public void onBuildApplication(BuildApplicationEvent event)
   {
      if (selectedItems == null || selectedItems.size() <= 0)
      {
         Dialogs.getInstance().showInfo(GitExtension.MESSAGES.selectedItemsFail());
         return;
      }

      Item item = selectedItems.get(0);
      String url = "";
      if (item instanceof Folder)
         url = item.getHref();
      else
         url = item.getHref().substring(0, item.getHref().lastIndexOf("/"));
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
                  getWorkDir();
                  return;
               }
            }
            super.onFailure(exception);
         }
      });
   }

   /**
    * Process git repository
    * @param remotes
    */
   private void remoteRepositoriesReceived(List<Remote> remotes)
   {
      if (remotes.size() == 0)
      {
         Dialogs.getInstance().showError(JenkinsExtension.MESSAGES.noRemoteRepository());
         return;
      }
      if (remotes.size() > 1)
      {
         String[] values = new String[remotes.size()];
         for (int i = 0; i < remotes.size(); i++)
         {
            values[i] = remotes.get(i).getUrl();
         }
         new SelectGitRemoteRepositoryPresenter(values);
      }
      else
      {
         createJob(remotes.get(0).getUrl());
      }

   }

   /**
    * Create new Jenkins job with remote git repository
    * @param remoteRepo Remote Git repository URL
    */
   private void createJob(String remoteRepo)
   {
      //dummy check that user name is e-mail.
      //Jenkins create git tag on build. Marks user as author of tag.
      String mail = userInfo.getName().contains("@") ? userInfo.getName() : userInfo.getName() + "@exoplatform.local";

      JenkinsService.get().createJenkinsJob("" + System.currentTimeMillis(), remoteRepo, userInfo.getName(), mail,
         workDir, new AsyncRequestCallback<Job>()
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
            IDE.EVENT_BUS.fireEvent(new OutputEvent("Build started", Type.INFO));
            control.setStartBuildingMessage(getProjectName());
            Dialogs.getInstance().showInfo(JenkinsExtension.MESSAGES.buildStarted(getProjectName()));
            statusTimer.schedule(delay);
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
      projectName = projectName.substring(projectName.lastIndexOf("/") + 1, projectName.length());
      return projectName;
   }

   private Timer statusTimer = new Timer()
   {

      @Override
      public void run()
      {
         JenkinsService.get().jobStatus(jobName, new AsyncRequestCallback<JobStatus>()
         {

            @Override
            protected void onSuccess(JobStatus result)
            {
               control.updateStatus(result);
               if (result.getStatus() == Status.END)
               {
                  cancel();
                  IDE.EVENT_BUS.fireEvent(new OutputEvent("Build finished<br/>Result:&nbsp;"
                     + result.getLastBuildResult(), Type.INFO));
                  JobResult jobResult = JobResult.valueOf(result.getLastBuildResult());
                  if (jobResult != JobResult.SUCCESS)
                  {
                     showBuildResultInfoDialog(jobResult);
                  }
                  IDE.EVENT_BUS.fireEvent(new ApplicationBuiltEvent(result));
               }
               else
               {
                  schedule(delay);
               }
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               cancel();
               control.setText("&nbsp;");
               super.onFailure(exception);
            };
         });
      }
   };

   /**
    * Show Info dialog for project build result
    * @param result
    */
   private void showBuildResultInfoDialog(JobResult result)
   {
      Dialogs.getInstance().ask(JenkinsExtension.MESSAGES.buildResultTitle(),
         JenkinsExtension.MESSAGES.buildResultMessage(getProjectName(), result.toString()),
         new BooleanValueReceivedHandler()
         {

            @Override
            public void booleanValueReceived(Boolean value)
            {
               if (value != null && value)
               {
                  JenkinsService.get().getJenkinsOutput(jobName, new AsyncRequestCallback<String>()
                  {

                     @Override
                     protected void onSuccess(String result)
                     {
                        IDE.EVENT_BUS.fireEvent(new OutputEvent("<pre>" + result + "</pre>", Type.INFO));
                     }
                  });
               }
            }
         });
   }

   /**
    * @see org.exoplatform.ide.git.client.GitPresenter#onWorkDirReceived()
    */
   @Override
   public void onWorkDirReceived()
   {
      GitClientService.getInstance().remoteList(workDir, null, true, new AsyncRequestCallback<List<Remote>>()
      {

         @Override
         protected void onSuccess(List<Remote> result)
         {
            remoteRepositoriesReceived(result);
         }
      });
   }

   /**
    * @see org.exoplatform.ide.extension.jenkins.client.event.GitRemoteRepositorySelectedHandler#onGitRemoteRepositorySelected(org.exoplatform.ide.extension.jenkins.client.event.GitRemoteRepositorySelectedEvent)
    */
   @Override
   public void onGitRemoteRepositorySelected(GitRemoteRepositorySelectedEvent event)
   {
      createJob(event.getRemoteRepository());
   }

   /**
    * @see org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedHandler#onUserInfoReceived(org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedEvent)
    */
   @Override
   public void onUserInfoReceived(UserInfoReceivedEvent event)
   {
      userInfo = event.getUserInfo();
   }

   /**
    * @see org.exoplatform.ide.extension.jenkins.client.event.GetJenkinsOutputHandler#onGetJenkinsOutput(org.exoplatform.ide.extension.jenkins.client.event.GetJenkinsOutputEvent)
    */
   @Override
   public void onGetJenkinsOutput(GetJenkinsOutputEvent event)
   {
      if (event.getJobName() == null)
         return;

      JenkinsService.get().getJenkinsOutput(event.getJobName(), new AsyncRequestCallback<String>()
      {

         @Override
         protected void onSuccess(String result)
         {
            IDE.EVENT_BUS.fireEvent(new OutputEvent("<pre>" + result + "</pre>", Type.INFO));
         }
      });
   }

   /**
    * @see org.exoplatform.ide.git.client.GitPresenter#getWorkDir()
    */
   @Override
   public void getWorkDir()
   {
      if (selectedItems == null || selectedItems.size() <= 0)
      {
         Dialogs.getInstance().showInfo(GitExtension.MESSAGES.selectedItemsFail());
         return;
      }

      //First get the working directory of the repository if exists:
      GitClientService.getInstance().getWorkDir(selectedItems.get(0).getHref(),
         new AsyncRequestCallback<WorkDirResponse>()
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
               Dialogs.getInstance().showError(JenkinsExtension.MESSAGES.noGitReposytory());
            }
         });
   }

}
