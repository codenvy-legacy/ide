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

import com.google.collide.json.shared.JsonStringSet;
import com.google.collide.shared.util.JsonCollections;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;

import org.eclipse.jdt.client.create.CreateJavaClassPresenter;
import org.eclipse.jdt.client.event.CleanProjectEvent;
import org.eclipse.jdt.client.event.CleanProjectHandler;
import org.eclipse.jdt.client.event.PackageCreatedEvent;
import org.eclipse.jdt.client.event.PackageCreatedHandler;
import org.eclipse.jdt.client.event.ReparseOpenedFilesEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.RequestStatusHandler;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.event.FileSavedEvent;
import org.exoplatform.ide.client.framework.event.FileSavedHandler;
import org.exoplatform.ide.client.framework.job.Job;
import org.exoplatform.ide.client.framework.job.Job.JobStatus;
import org.exoplatform.ide.client.framework.job.JobChangeEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.project.ActiveProjectChangedEvent;
import org.exoplatform.ide.client.framework.project.ActiveProjectChangedHandler;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.project.ProjectType;
import org.exoplatform.ide.client.framework.util.StringUnmarshaller;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.client.framework.websocket.MessageBus.ReadyState;
import org.exoplatform.ide.client.framework.websocket.rest.RESTfulRequest;
import org.exoplatform.ide.client.framework.websocket.rest.RequestCallback;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  10:51:51 AM Mar 5, 2012 evgen $
 *
 */
public class JavaClasspathResolver implements CleanProjectHandler, ProjectOpenedHandler, VfsChangedHandler,
   ActiveProjectChangedHandler, FileSavedHandler, ProjectClosedHandler, PackageCreatedHandler
{

   private String vfsId;

   private ProjectModel project;

   private HandlerRegistration saveFileHandler;

   private final SupportedProjectResolver projectResolver;

   /**
    * 
    */
   public JavaClasspathResolver(SupportedProjectResolver projectResolver)
   {
      this.projectResolver = projectResolver;
      IDE.addHandler(CleanProjectEvent.TYPE, this);
      IDE.addHandler(ProjectOpenedEvent.TYPE, this);
      IDE.addHandler(ProjectClosedEvent.TYPE, this);
      IDE.addHandler(VfsChangedEvent.TYPE, this);
      IDE.addHandler(ActiveProjectChangedEvent.TYPE, this);
      IDE.addHandler(PackageCreatedEvent.TYPE, this);
   }

   /**
   * @see org.exoplatform.ide.client.framework.event.FileSavedHandler#onFileSaved(org.exoplatform.ide.client.framework.event.FileSavedEvent)
   */
   @Override
   public void onFileSaved(FileSavedEvent event)
   {
      if ("pom.xml".equals(event.getFile().getName()))
      {
         if (event.getFile().getProject() != null)
         {
            resolveDependencies(event.getFile().getProject());
         }
         else
         {
            resolveDependencies(this.project);
         }
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.project.ProjectClosedHandler#onProjectClosed(org.exoplatform.ide.client.framework.project.ProjectClosedEvent)
    */
   @Override
   public void onProjectClosed(ProjectClosedEvent event)
   {
      if (saveFileHandler != null)
      {
         saveFileHandler.removeHandler();
         saveFileHandler = null;
      }
      this.project = null;
   }

   /**
    * @see org.eclipse.jdt.client.event.CleanProjectHandler#onCleanProject(org.eclipse.jdt.client.event.CleanProjectEvent)
    */
   @Override
   public void onCleanProject(CleanProjectEvent event)
   {
      resolveDependencies(this.project);
   }
   
   
   @Override
   public void onVfsChanged(VfsChangedEvent event)
   {
      vfsId = event.getVfsInfo().getId();
   }

   @Override
   public void onProjectOpened(ProjectOpenedEvent event)
   {
      saveFileHandler = IDE.addHandler(FileSavedEvent.TYPE, this);
      this.project = event.getProject();
      ArrayList<ProjectModel> mvnModules = new ArrayList<ProjectModel>();
      if (project.getProjectType().equals(ProjectType.MultiModule.value()))
      {
         List<ProjectModel> children = project.getModules();
         for (ProjectModel item : children)
         {
            if (projectResolver.isProjectSupported(item.getProjectType()))
            {
               mvnModules.add(item);
            }
         }
      }
      else if (projectResolver.isProjectSupported(project.getProjectType()))
      {
         mvnModules.add(project);
      }
      resolveDependencies(mvnModules.toArray(new ProjectModel[mvnModules.size()]));

   }
   
   /**
    * @see org.eclipse.jdt.client.event.PackageCreatedHandler#onPackageCreated(org.eclipse.jdt.client.event.PackageCreatedEvent)
    */
   @Override
   public void onPackageCreated(PackageCreatedEvent event)
   {
      FolderModel parentFolder = event.getParentFolder();
      ProjectModel project = parentFolder.getProject();
      String sourcePath =
         project.hasProperty("sourceFolder") ? (String)project.getPropertyValue("sourceFolder")
            : CreateJavaClassPresenter.DEFAULT_SOURCE_FOLDER;
      String path = project.getPath() + "/" + sourcePath;
      String pack = "";
      if (!path.equals(parentFolder.getPath()))
         pack = parentFolder.getPath().substring(path.length() + 1);
      pack = pack.replaceAll("\\\\", ".");
      String newPackage = event.getPack();
      if (newPackage.contains("."))
      {
         String[] packageFragments = newPackage.split("\\.");
         StringBuilder builder = new StringBuilder(pack);
         for (String fragment : packageFragments)
         {
            if (builder.length() != 0)
               builder.append('.');
            builder.append(fragment);
            TypeInfoStorage.get().getPackages(project.getId()).add(builder.toString());
         }
      }
      else
         TypeInfoStorage.get().getPackages(project.getId()).add(pack + '.' + newPackage);

   }

   @Override
   public void onActiveProjectChanged(ActiveProjectChangedEvent event)
   {
      project = event.getProject();
   }
   
   private void resolveDependencies(ProjectModel... projects)
   {
//      if (IDE.messageBus().getReadyState() == ReadyState.OPEN)
//      {
//         resolveDependenciesWS(projects);
//      }
//      else
//      {
         resolveDependenciesRest(projects);
//      }
   }



   private void resolveDependenciesRest(ProjectModel... projects)
   {
      for (ProjectModel project : projects)
      {
         final UpdateDependencyStatusHandler updateDependencyStatusHandler =
            new UpdateDependencyStatusHandler(project.getName());
         final String projectId = project.getId();
         updateDependencyStatusHandler.requestInProgress(projectId);
         String url =
            Utils.getRestContext() + "/ide/code-assistant/java/update-dependencies?projectid=" + projectId + "&vfsid="
               + vfsId;
         StringUnmarshaller unmarshaller = new StringUnmarshaller(new StringBuilder());

         try
         {
            AsyncRequest.build(RequestBuilder.GET, url, true).send(
               new AsyncRequestCallback<StringBuilder>((Unmarshallable<StringBuilder>)unmarshaller)
               {

                  @Override
                  protected void onSuccess(StringBuilder result)
                  {
                     dependenciesResolvedSuccessed(updateDependencyStatusHandler, projectId, result);
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


   private void resolveDependenciesWS(ProjectModel... projects)
   {
      for (ProjectModel project : projects)
      {
         final UpdateDependencyStatusHandler updateDependencyStatusHandler =
            new UpdateDependencyStatusHandler(project.getName());
         final String projectId = project.getId();
         updateDependencyStatusHandler.requestInProgress(projectId);
         String url = "/ide/code-assistant/java/update-dependencies?projectid=" + projectId + "&vfsid=" + vfsId;
         StringUnmarshaller unmarshaller = new StringUnmarshaller(new StringBuilder());

         RESTfulRequest.build(RequestBuilder.GET, url).send(
            new RequestCallback<StringBuilder>(
               (org.exoplatform.ide.client.framework.websocket.rest.Unmarshallable<StringBuilder>)unmarshaller)
            {

               @Override
               protected void onSuccess(StringBuilder result)
               {
                  dependenciesResolvedSuccessed(updateDependencyStatusHandler, projectId, result);
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
   }

   /**
    * 
    */
   private void doClean()
   {
      TypeInfoStorage.get().clear();
      NameEnvironment.clearFQNBlackList();
   }

  
   /**
    * @param updateDependencyStatusHandler
    * @param projectId
    * @param result
    */
   private void dependenciesResolvedSuccessed(final UpdateDependencyStatusHandler updateDependencyStatusHandler,
      final String projectId, StringBuilder result)
   {
      updateDependencyStatusHandler.requestFinished(projectId);
      if (result != null && result.length() > 0)
      {
         JSONArray arr = JSONParser.parseLenient(result.toString()).isArray();
         JsonStringSet stringSet = JsonCollections.createStringSet();
         for (int i = 0; i < arr.size(); i++)
         {
            stringSet.add(arr.get(i).isString().stringValue());
         }
         doClean();
         TypeInfoStorage.get().setPackages(projectId, stringSet);
         IDE.fireEvent(new ReparseOpenedFilesEvent());
      }
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
