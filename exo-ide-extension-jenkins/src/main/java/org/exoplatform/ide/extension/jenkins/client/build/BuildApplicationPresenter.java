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

import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.userinfo.UserInfo;
import org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedEvent;
import org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedHandler;
import org.exoplatform.ide.client.framework.vfs.Folder;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.extension.jenkins.client.JenkinsExtension;
import org.exoplatform.ide.extension.jenkins.client.JenkinsService;
import org.exoplatform.ide.extension.jenkins.client.event.ApplicationBuiltEvent;
import org.exoplatform.ide.extension.jenkins.client.event.BuildApplicationEvent;
import org.exoplatform.ide.extension.jenkins.client.event.BuildApplicationHandler;
import org.exoplatform.ide.extension.jenkins.client.event.GetJenkinsOutputEvent;
import org.exoplatform.ide.extension.jenkins.client.event.GetJenkinsOutputHandler;
import org.exoplatform.ide.extension.jenkins.shared.Job;
import org.exoplatform.ide.extension.jenkins.shared.JobStatus;
import org.exoplatform.ide.extension.jenkins.shared.JobStatus.Status;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitClientUtil;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.git.client.marshaller.WorkDirResponse;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;

/**
 * 
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class BuildApplicationPresenter extends GitPresenter implements BuildApplicationHandler,
   UserInfoReceivedHandler, GetJenkinsOutputHandler
{

   public interface Display extends IsView
   {

      void output(String text);

      void clearOutput();

      void startAnimation();

      void stopAnimation();

   }

   private Display display;

   private String jobName;

   private UserInfo userInfo;

   //private BuildStatusControl control;

   /**
    * Delay in millisecond between job status request  
    */
   private static final int delay = 10000;

   private String restContext;

   /**
    * @param eventBus
    */
   public BuildApplicationPresenter(String restContext)
   {
      super(IDE.EVENT_BUS);
      this.restContext = restContext;
      IDE.EVENT_BUS.addHandler(BuildApplicationEvent.TYPE, this);
      IDE.EVENT_BUS.addHandler(UserInfoReceivedEvent.TYPE, this);
      IDE.EVENT_BUS.addHandler(GetJenkinsOutputEvent.TYPE, this);

      //      control = new BuildStatusControl();
      //      IDE.getInstance().addControl(control, DockTarget.STATUSBAR, true);
   }

   /**
    * @see org.exoplatform.ide.extension.jenkins.client.event.BuildApplicationHandler#onBuildApplication(org.exoplatform.ide.extension.jenkins.client.event.BuildApplicationEvent)
    */
   @Override
   public void onBuildApplication(BuildApplicationEvent event)
   {
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

      JenkinsService.get().createJenkinsJob("" + System.currentTimeMillis(), repository, userInfo.getName(), mail,
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
//            IDE.EVENT_BUS.fireEvent(new OutputEvent("Build started", Type.INFO));
//            control.setStartBuildingMessage(getProjectName());
            
            showBuildMessage("Build started");
            display.startAnimation();
            
//            Dialogs.getInstance().showInfo(JenkinsExtension.MESSAGES.buildStarted(getProjectName()));
            prevStatus = null;
            statusTimer.schedule(delay);
         }
      });
   }

//   /**
//    * Get project name (last URL segment of workDir value)
//    * @return project name
//    */
//   private String getProjectName()
//   {
//      String projectName = workDir;
//      if (projectName.endsWith("/"))
//      {
//         projectName = projectName.substring(0, projectName.length() - 1);
//      }
//      projectName = projectName.substring(projectName.lastIndexOf("/") + 1, projectName.length());
//      return projectName;
//   }
//   
   private Status prevStatus = null;

   private void updateJobStatus(JobStatus status) {
      if (status.getStatus() == Status.QUEUE && prevStatus != Status.QUEUE) {
         prevStatus = Status.QUEUE;
         showBuildMessage("Status: " + status.getStatus());
         return;
      }
      
      if (status.getStatus() == Status.BUILD && prevStatus != Status.BUILD) {
         prevStatus = Status.BUILD;
         showBuildMessage("Status: " + status.getStatus());
         return;
      }
      
      if (status.getStatus() == Status.END && prevStatus != Status.END) {
         prevStatus = Status.END;
         //showBuildMessage("Status: " + status.getStatus());
         
         String message = "Build finished\r\nResult:&nbsp;" + status.getLastBuildResult();
         showBuildMessage(message);
         
         display.stopAnimation();
         
         return;
      }
      
   }   
   
   private Timer statusTimer = new Timer()
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
               
               if (status.getStatus() == Status.END) {
                  IDE.EVENT_BUS.fireEvent(new ApplicationBuiltEvent(status));

                  JenkinsService.get().getJenkinsOutput(jobName, new AsyncRequestCallback<String>()
                     {
                        @Override
                        protected void onSuccess(String result)
                        {
                           //IDE.EVENT_BUS.fireEvent(new OutputEvent("<pre>" + result + "</pre>", Type.INFO));
                           showBuildMessage(result);
                        }
                     });                  
                  
               } else {
                  schedule(delay);
               }

               //control.updateStatus(result);
               /*
               if (status.getStatus() == Status.END)
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
               */
            }

//            @Override
//            protected void onFailure(Throwable exception)
//            {
//               cancel();
//               //control.setText("&nbsp;");
//               
//               super.onFailure(exception);
//            };
         });
      }
   };

//   /**
//    * Show Info dialog for project build result
//    * @param result
//    */
//   private void showBuildResultInfoDialog(JobResult result)
//   {
//      Dialogs.getInstance().ask(JenkinsExtension.MESSAGES.buildResultTitle(),
//         JenkinsExtension.MESSAGES.buildResultMessage(getProjectName(), result.toString()),
//         new BooleanValueReceivedHandler()
//         {
//
//            @Override
//            public void booleanValueReceived(Boolean value)
//            {
//               if (value != null && value)
//               {
//                  JenkinsService.get().getJenkinsOutput(jobName, new AsyncRequestCallback<String>()
//                  {
//
//                     @Override
//                     protected void onSuccess(String result)
//                     {
//                        IDE.EVENT_BUS.fireEvent(new OutputEvent("<pre>" + result + "</pre>", Type.INFO));
//                     }
//                  });
//               }
//            }
//         });
//   }

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
               Dialogs.getInstance().ask(JenkinsExtension.MESSAGES.noGitRepositoryTitle(),
                  JenkinsExtension.MESSAGES.noGitRepository(), new BooleanValueReceivedHandler()
                  {
                     @Override
                     public void booleanValueReceived(Boolean value)
                     {
                        if (value != null && value)
                        {
                           initRepository(selectedItems.get(0).getHref());
                        }
                     }
                  });
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
      GitClientService.getInstance().init(path, false, new AsyncRequestCallback<String>()
      {
         @Override
         protected void onSuccess(String result)
         {
            workDir = path;
            //eventBus.fireEvent(new OutputEvent(GitExtension.MESSAGES.initSuccess(), Type.INFO));
            showBuildMessage(GitExtension.MESSAGES.initSuccess());
            eventBus.fireEvent(new RefreshBrowserEvent());
            createJob(GitClientUtil.getPublicGitRepoUrl(workDir, restContext));
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

   private void showBuildMessage(String message)
   {
      if (display != null)
      {
         display.asView().activate();
      }
      else
      {
         display = GWT.create(Display.class);
         IDE.getInstance().openView(display.asView());
      }

      display.output(message);
   }

}
