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
package org.eclipse.jdt.client;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;

import org.eclipse.jdt.client.event.CleanProjectEvent;
import org.eclipse.jdt.client.event.CleanProjectHandler;
import org.eclipse.jdt.client.event.ReparseOpenedFilesEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.RequestStatusHandler;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.job.Job;
import org.exoplatform.ide.client.framework.job.JobChangeEvent;
import org.exoplatform.ide.client.framework.job.Job.JobStatus;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.project.ActiveProjectChangedEvent;
import org.exoplatform.ide.client.framework.project.ActiveProjectChangedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  10:51:51 AM Mar 5, 2012 evgen $
 *
 */
public class CleanProjectCommandHandler implements CleanProjectHandler, ProjectOpenedHandler, VfsChangedHandler,
   ActiveProjectChangedHandler
{

   private String vfsId;

   private ProjectModel project;

   /**
    * 
    */
   public CleanProjectCommandHandler()
   {
      IDE.addHandler(CleanProjectEvent.TYPE, this);
      IDE.addHandler(ProjectOpenedEvent.TYPE, this);
      IDE.addHandler(VfsChangedEvent.TYPE, this);
      IDE.addHandler(ActiveProjectChangedEvent.TYPE, this);
   }

   /**
    * @see org.eclipse.jdt.client.event.CleanProjectHandler#onCleanProject(org.eclipse.jdt.client.event.CleanProjectEvent)
    */
   @Override
   public void onCleanProject(CleanProjectEvent event)
   {
      if (vfsId != null && project != null)
      {
         final UpdateDependencyStatusHandler updateDependencyStatusHandler =
            new UpdateDependencyStatusHandler(project.getName());
         final String projectId = project.getId();
         updateDependencyStatusHandler.requestInProgress(projectId);
         String url =
            Utils.getRestContext() + "/ide/code-assistant/java/update-dependencies?projectid=" + projectId + "&vfsid="
               + vfsId;
         try
         {
            AsyncRequest.build(RequestBuilder.GET, url, true).send(new AsyncRequestCallback<String>()
            {

               @Override
               protected void onSuccess(String result)
               {
                  updateDependencyStatusHandler.requestFinished(projectId);
                  doClean();
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  updateDependencyStatusHandler.requestError(projectId, exception);
                  IDE.fireEvent(new OutputEvent("<pre>" + exception.getMessage() + "</pre>", Type.ERROR));
                  exception.printStackTrace();
               }
            });
         }
         catch (RequestException e)
         {
            e.printStackTrace();
         }
      }
   }

   /**
    * 
    */
   private void doClean()
   {
      TypeInfoStorage.get().clear();

      IDE.fireEvent(new ReparseOpenedFilesEvent());
      NameEnvironment.clearFQNBlackList();
   }

   @Override
   public void onVfsChanged(VfsChangedEvent event)
   {
      vfsId = event.getVfsInfo().getId();
   }

   @Override
   public void onProjectOpened(ProjectOpenedEvent event)
   {
      project = event.getProject();
   }
   
   @Override
   public void onActiveProjectChanged(ActiveProjectChangedEvent event)
   {
      project = event.getProject();
   }

   private class UpdateDependencyStatusHandler implements RequestStatusHandler
   {

      private String projectName;

      /**
       * @param projectName project's name
       */
      public UpdateDependencyStatusHandler(String projectName)
      {
         super();
         this.projectName = projectName;
      }

      /**
       * @see org.exoplatform.gwtframework.commons.rest.RequestStatusHandler#requestInProgress(java.lang.String)
       */
      @Override
      public void requestInProgress(String id)
      {
         Job job = new Job(id, JobStatus.STARTED);
         job.setStartMessage(JdtExtension.LOCALIZATION_CONSTANT.updateDependencyStarted(projectName));
         IDE.fireEvent(new JobChangeEvent(job));
      }

      /**
       * @see org.exoplatform.gwtframework.commons.rest.RequestStatusHandler#requestFinished(java.lang.String)
       */
      @Override
      public void requestFinished(String id)
      {
         Job job = new Job(id, JobStatus.FINISHED);
         job.setFinishMessage(JdtExtension.LOCALIZATION_CONSTANT.updateDependencyFinished(projectName));
         IDE.fireEvent(new JobChangeEvent(job));
      }

      /**
       * @see org.exoplatform.gwtframework.commons.rest.RequestStatusHandler#requestError(java.lang.String, java.lang.Throwable)
       */
      @Override
      public void requestError(String id, Throwable exception)
      {
         Job job = new Job(id, JobStatus.ERROR);
         job.setError(exception);
         IDE.fireEvent(new JobChangeEvent(job));
      }
   }
}
