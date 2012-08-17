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
package org.exoplatform.ide.extension.openshift.client.deploy;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.gwtframework.ui.client.component.GWTLoader;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.job.JobManager;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.paas.DeployResultHandler;
import org.exoplatform.ide.client.framework.paas.HasPaaSActions;
import org.exoplatform.ide.client.framework.project.ProjectCreatedEvent;
import org.exoplatform.ide.client.framework.project.ProjectType;
import org.exoplatform.ide.client.framework.template.ProjectTemplate;
import org.exoplatform.ide.extension.openshift.client.OpenShiftAsyncRequestCallback;
import org.exoplatform.ide.extension.openshift.client.OpenShiftClientService;
import org.exoplatform.ide.extension.openshift.client.OpenShiftExceptionThrownEvent;
import org.exoplatform.ide.extension.openshift.client.OpenShiftExtension;
import org.exoplatform.ide.extension.openshift.client.OpenShiftLocalizationConstant;
import org.exoplatform.ide.extension.openshift.client.key.UpdatePublicKeyCallback;
import org.exoplatform.ide.extension.openshift.client.key.UpdatePublicKeyCommandHandler;
import org.exoplatform.ide.extension.openshift.client.login.LoggedInEvent;
import org.exoplatform.ide.extension.openshift.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.openshift.client.login.LoginEvent;
import org.exoplatform.ide.extension.openshift.client.marshaller.ApplicationTypesUnmarshaller;
import org.exoplatform.ide.extension.openshift.shared.AppInfo;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ProjectUnmarshaller;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: DeployApplicationPresenter.java Dec 5, 2011 1:58:22 PM vereshchaka $
 * 
 */
public class DeployApplicationPresenter implements HasPaaSActions, VfsChangedHandler
{

   public interface Display
   {
      HasValue<String> getApplicationNameField();

      HasValue<String> getTypeField();

      void setTypeValues(String[] types);

      Composite getView();
   }

   private static final OpenShiftLocalizationConstant lb = OpenShiftExtension.LOCALIZATION_CONSTANT;

   private VirtualFileSystemInfo vfs;

   private Display display;

   private ProjectModel project;

   private ProjectType projectType;

   private String projectName;

   private DeployResultHandler deployResultHandler;

   public DeployApplicationPresenter()
   {
      IDE.addHandler(VfsChangedEvent.TYPE, this);
   }

   public void bindDisplay()
   {

   }

   /**
    * Forms the message to be shown, when application is created.
    * 
    * @param appInfo application information
    * @return {@link String} message
    */
   protected String formApplicationCreatedMessage(AppInfo appInfo)
   {
      String applicationStr = "<br> [";
      applicationStr += "<b>Name</b>" + " : " + appInfo.getName() + "<br>";
      applicationStr += "<b>Git URL</b>" + " : " + appInfo.getGitUrl() + "<br>";
      applicationStr +=
         "<b>Public URL</b>" + " : <a href=\"" + appInfo.getPublicUrl() + "\" target=\"_blank\">"
            + appInfo.getPublicUrl() + "</a><br>";
      applicationStr += "<b>Type</b>" + " : " + appInfo.getType() + "<br>";
      applicationStr += "] ";

      return lb.createApplicationSuccess(applicationStr);
   }

   /**
    * @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework.application.event.VfsChangedEvent)
    */
   @Override
   public void onVfsChanged(VfsChangedEvent event)
   {
      this.vfs = event.getVfsInfo();
   }

   private void getApplicationTypes()
   {
      try
      {
         OpenShiftClientService.getInstance().getApplicationTypes(
            new OpenShiftAsyncRequestCallback<List<String>>(new ApplicationTypesUnmarshaller(new ArrayList<String>()),
               new LoggedInHandler()
               {
                  @Override
                  public void onLoggedIn(LoggedInEvent event)
                  {
                     getApplicationTypes();
                  }
               }, null)
            {
               @Override
               protected void onSuccess(List<String> result)
               {
                  display.setTypeValues(result.toArray(new String[result.size()]));
                  display.getTypeField().setValue(detectType(projectType.value(), result));
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.paas.recent.HasPaaSActions#deploy(org.exoplatform.ide.client.framework.template.ProjectTemplate,
    *      org.exoplatform.ide.client.framework.paas.recent.DeployResultHandler)
    */
   @Override
   public void deploy(ProjectTemplate projectTemplate, DeployResultHandler deployResultHandler)
   {
      String applicationName = display.getApplicationNameField().getValue();
      this.deployResultHandler = deployResultHandler;
      if (applicationName == null || applicationName.isEmpty())
      {
         Dialogs.getInstance().showError("Application name must not be empty");
      }
      else
      {
         createEmptyProject();
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.paas.recent.HasPaaSActions#getDeployView(java.lang.String,
    *      org.exoplatform.ide.client.framework.project.ProjectType)
    */
   @Override
   public Composite getDeployView(String projectName, ProjectType projectType)
   {
      this.projectName = projectName;
      this.projectType = projectType;
      if (display == null)
      {
         display = GWT.create(Display.class);
      }
      bindDisplay();
      display.getApplicationNameField().setValue(projectName);
      getApplicationTypes();
      return display.getView();
   }

   private String detectType(String projectType, List<String> types)
   {
      // Try to detect by starting symbols:
      for (String type : types)
      {
         if (type.toLowerCase().startsWith(projectType.toLowerCase()))
         {
            return type;
         }
      }

      // Try to detect by containing symbols:
      for (String type : types)
      {
         if (type.toLowerCase().contains(projectType.toLowerCase()))
         {
            return type;
         }
      }
      return types.get(0);
   }

   private void createApplication()
   {
      final String applicationName = display.getApplicationNameField().getValue();
      String applicationType = display.getTypeField().getValue();
      try
      {
         JobManager.get().showJobSeparated();
         AutoBean<AppInfo> appInfo = OpenShiftExtension.AUTO_BEAN_FACTORY.appInfo();
         AutoBeanUnmarshaller<AppInfo> unmarshaller = new AutoBeanUnmarshaller<AppInfo>(appInfo);
         OpenShiftClientService.getInstance().createApplication(applicationName, vfs.getId(), project.getId(),
            applicationType, new AsyncRequestCallback<AppInfo>(unmarshaller)
            {

               @Override
               protected void onSuccess(AppInfo result)
               {
                  IDE.fireEvent(new OutputEvent(formApplicationCreatedMessage(result), Type.INFO));

                  Scheduler.get().scheduleDeferred(new ScheduledCommand()
                  {

                     @Override
                     public void execute()
                     {
                        updateSSHPublicKey();
                     }
                  });
               }

               /**
                * @see org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback#onFailure(java.lang.Throwable)
                */
               @Override
               protected void onFailure(Throwable exception)
               {
                  if (exception instanceof ServerException)
                  {
                     ServerException serverException = (ServerException)exception;
                     if (HTTPStatus.OK == serverException.getHTTPStatus()
                        && "Authentication-required".equals(serverException.getHeader(HTTPHeader.JAXRS_BODY_PROVIDED)))
                     {
                        IDE.fireEvent(new LoginEvent());
                        return;
                     }
                  }

                  IDE.fireEvent(new OpenShiftExceptionThrownEvent(exception, lb.createApplicationFail(applicationName)));
                  deployResultHandler.onDeployFinished(false);
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new OpenShiftExceptionThrownEvent(e, lb.createApplicationFail(applicationName)));
         deployResultHandler.onDeployFinished(false);
      }
   }

   private void updateSSHPublicKey()
   {
      UpdatePublicKeyCommandHandler.getInstance().updatePublicKey(new UpdatePublicKeyCallback()
      {
         @Override
         public void onPublicKeyUpdated(boolean success)
         {
            if (!success)
            {
               return;
            }

            Scheduler.get().scheduleDeferred(new ScheduledCommand()
            {

               @Override
               public void execute()
               {
                  pullAppSources();
               }
            });
         }
      });
   }

   private void pullAppSources()
   {
      new PullApplicationSourcesHandler().pullApplicationSources(vfs, project, new PullCompleteCallback()
      {
         @Override
         public void onPullComplete(boolean success)
         {
            if (!success)
            {
               Dialogs.getInstance().showError(OpenShiftExtension.LOCALIZATION_CONSTANT.pullSourceFailed());
            }
            else
            {
               IDE.fireEvent(new ProjectCreatedEvent(project));
               deployResultHandler.onDeployFinished(true);
            }
         }
      });

   }

   private void createEmptyProject()
   {
      final Loader loader = new GWTLoader();
      loader.setMessage(lb.creatingProject());
      try
      {
         loader.show();
         final ProjectModel newProject = new ProjectModel();
         newProject.setName(projectName);
         newProject.setProjectType(projectType.value());

         VirtualFileSystem.getInstance().createProject(vfs.getRoot(),
            new AsyncRequestCallback<ProjectModel>(new ProjectUnmarshaller(newProject))
            {

               @Override
               protected void onSuccess(ProjectModel result)
               {
                  loader.hide();
                  project = result;
                  deployResultHandler.onProjectCreated(project);
                  createApplication();
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  loader.hide();
                  deployResultHandler.onDeployFinished(false);
                  IDE.fireEvent(new ExceptionThrownEvent(exception));
               }
            });
      }
      catch (Exception e)
      {
         loader.hide();
         deployResultHandler.onDeployFinished(false);
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.paas.recent.HasPaaSActions#deploy(org.exoplatform.ide.vfs.client.model.ProjectModel,
    *      org.exoplatform.ide.client.framework.paas.recent.DeployResultHandler)
    */
   @Override
   public void deploy(ProjectModel project, DeployResultHandler deployResultHandler)
   {
      this.deployResultHandler = deployResultHandler;
      this.project = project;
      createApplication();
   }
}
