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
package org.exoplatform.ide.extension.java.client;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Timer;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.gwtframework.commons.rest.RequestStatusHandler;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.project.ProjectType;
import org.exoplatform.ide.client.framework.util.ProjectResolver;
import org.exoplatform.ide.extension.maven.client.BuilderClientService;
import org.exoplatform.ide.extension.maven.client.BuilderExtension;
import org.exoplatform.ide.extension.maven.client.build.BuildRequestStatusHandler;
import org.exoplatform.ide.extension.maven.shared.BuildStatus;
import org.exoplatform.ide.extension.maven.shared.BuildStatus.Status;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class JavaClasspathCreator implements ProjectOpenedHandler
{

   private static final String GENERATE_CLASSPATH_ERROR = "An error occured. Classpath can not be generated.";

   private static final Set<String> projectTypes = new HashSet<String>();

   private static final String BUILD_SUCCESS = BuilderExtension.LOCALIZATION_CONSTANT.buildSuccess();

   private static final String BUILD_FAILED = BuilderExtension.LOCALIZATION_CONSTANT.buildFailed();

   private RequestStatusHandler statusHandler;

   /**
    * Delay in millisecond between requests for build job status.
    */
   private static final int delay = 3000;

   /**
    * The build's identifier.
    */
   private String buildID = null;

   /**
    * Status of previously build.
    */
   private Status previousStatus = null;

   private final String restContext;

   private ProjectModel project;

   static
   {
      //TODO move this class to JDT extension and use SupportedProjects interface to remove this hardcode
      projectTypes.add(ProjectResolver.SERVLET_JSP);
      projectTypes.add(ProjectResolver.SPRING);
      projectTypes.add(ProjectResolver.APP_ENGINE_JAVA);
      projectTypes.add(ProjectType.JAVA.value());
      projectTypes.add(ProjectType.SPRING.value());
      projectTypes.add(ProjectType.JSP.value());
      projectTypes.add(ProjectType.AWS.value());
      projectTypes.add(ProjectType.JAR.value());
   }

   /**
    * 
    */
   public JavaClasspathCreator(String restContext)
   {
      this.restContext = restContext;
      IDE.addHandler(ProjectOpenedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.framework.project.ProjectOpenedHandler#onProjectOpened(org.exoplatform.ide.client.framework.project.ProjectOpenedEvent)
    */
   @Override
   public void onProjectOpened(ProjectOpenedEvent event)
   {
      project = event.getProject();
      if (project.getProjectType().equals(ProjectType.MultiModule.value()))
      {
         doBuildAndPublish();
      }
      else if (projectTypes.contains(project.getProjectType()) && !project.hasProperty("exoide:classpath"))
      {
         generateClassPath(project.getId());
      }
   }

   /**
    * Start the build of project and publish it to public repository.
    */
   private void doBuildAndPublish()
   {
      statusHandler = new BuildRequestStatusHandler(project.getPath().substring(1));
      statusHandler.requestInProgress(project.getId());
      try
      {
         BuilderClientService.getInstance().buildAndPublish(project.getId(), 
            VirtualFileSystem.getInstance().getInfo().getId(),
            new AsyncRequestCallback<StringBuilder>(new StringUnmarshaller(new StringBuilder()))
            {
               @Override
               protected void onSuccess(StringBuilder result)
               {
                  buildID = result.substring(result.lastIndexOf("/") + 1);
                  previousStatus = null;
                  refreshBuildStatusTimer.schedule(delay);
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  statusHandler.requestError(project.getId(), exception);
                  if (exception instanceof ServerException && exception.getMessage() != null)
                  {
                     IDE.fireEvent(new OutputEvent(exception.getMessage(), Type.INFO));
                  }
                  else
                  {
                     IDE.fireEvent(new ExceptionThrownEvent(exception));
                  }
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new OutputEvent(e.getMessage(), Type.INFO));
      }
   }

   /**
    * @param id
    */
   private void generateClassPath(String projectId)
   {
      String url =
         restContext + "/ide/java/classpath/generate" + "?projectid=" + projectId + "&vfsid="
            + VirtualFileSystem.getInstance().getInfo().getId();
      try
      {
         AsyncRequest.build(RequestBuilder.POST, url).send(new AsyncRequestCallback<String>()
         {
            @Override
            protected void onSuccess(String result)
            {
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               if (GWT.isScript())
               {
                  IDE.fireEvent(new ExceptionThrownEvent(exception, GENERATE_CLASSPATH_ERROR));
               }
               else
               {
                  exception.printStackTrace();
               }
            }
         });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
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

                  Status status = response.getStatus();
                  if (status == Status.IN_PROGRESS)
                  {
                     schedule(delay);
                  }
               }

               protected void onFailure(Throwable exception)
               {
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
      previousStatus = buildStatus.getStatus();

      StringBuilder message =
         new StringBuilder("Finished building project <b>").append(project.getId()).append("</b>.\r\nResult: ")
            .append(buildStatus.getStatus());

      if (buildStatus.getStatus() == Status.SUCCESSFUL)
      {
         IDE.fireEvent(new OutputEvent(BUILD_SUCCESS, Type.INFO));
         statusHandler.requestFinished(project.getId());
         message.append("\r\nYou can download the build result <a href=\"").append(buildStatus.getDownloadUrl())
            .append("\">here</a>");

         List<ProjectModel> children = project.getModules();
         for (ProjectModel item : children)
         {
            if (projectTypes.contains(project.getProjectType()) && !project.hasProperty("exoide:classpath"))
            {
               generateClassPath(item.getId());
            }
         }
      }
      else if (buildStatus.getStatus() == Status.FAILED)
      {
         IDE.fireEvent(new OutputEvent(BUILD_FAILED, Type.ERROR));

         String errorMessage = buildStatus.getError();
         String exceptionMessage = "Building of project failed";
         if (errorMessage != null && !errorMessage.equals("null"))
         {
            message.append("\r\n" + errorMessage);
            exceptionMessage += ": " + errorMessage;
         }

         statusHandler.requestError(project.getId(), new Exception(exceptionMessage));
      }

      if (buildStatus.getStatus() == Status.FAILED)
      {
         showLog();
      }

//      IDE.fireEvent(new ProjectBuiltEvent(buildStatus));
   }

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
    * Deserializer for response's body.
    */
   private class StringUnmarshaller implements Unmarshallable<StringBuilder>
   {

      protected StringBuilder builder;

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
