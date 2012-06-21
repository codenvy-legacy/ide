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
package org.exoplatform.ide.extension.googleappengine.client.deploy;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.paas.Paas;
import org.exoplatform.ide.client.framework.paas.PaasCallback;
import org.exoplatform.ide.client.framework.paas.PaasComponent;
import org.exoplatform.ide.client.framework.util.ProjectResolver;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineAsyncRequestCallback;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineClientService;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineExtension;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEnginePresenter;
import org.exoplatform.ide.extension.googleappengine.client.create.CreateApplicationEvent;
import org.exoplatform.ide.extension.googleappengine.client.login.LoginEvent;
import org.exoplatform.ide.extension.googleappengine.shared.ApplicationInfo;
import org.exoplatform.ide.extension.googleappengine.shared.User;
import org.exoplatform.ide.extension.maven.client.event.BuildProjectEvent;
import org.exoplatform.ide.extension.maven.client.event.ProjectBuiltEvent;
import org.exoplatform.ide.extension.maven.client.event.ProjectBuiltHandler;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

import java.util.Arrays;

/**
 * Presenter for deploying application to Google App Engine, can be as a part of deployment step in wizard.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 16, 2012 5:51:08 PM anya $
 * 
 */
public class DeployApplicationPresenter extends GoogleAppEnginePresenter implements PaasComponent,
   DeployApplicationHandler, ProjectBuiltHandler
{
   interface Display
   {
      HasValue<String> getApplicationIdField();

      HasValue<Boolean> getUseExisting();

      void enableApplicationIdField(boolean enable);

      Composite getView();
   }

   private PaasCallback paasCallback;

   private Display display;

   /**
    * Google App Engine application's id.
    */
   private String applicationId;

   /**
    * Flag points, whether to use existed GAE application or create new one.
    */
   private boolean useExisted;

   /**
    * Application's war URL (for Java only).
    */
   private String applicationUrl;

   private ProjectModel builtProject;

   public DeployApplicationPresenter()
   {
      IDE.getInstance().addPaas(
         new Paas("Google App Engine", this, Arrays.asList(ProjectResolver.APP_ENGINE_JAVA,
            ProjectResolver.APP_ENGINE_PYTHON))
         {
            @Override
            public boolean isFirstInDeployments()
            {
               return true;
            }
         });

      IDE.getInstance().addControl(new DeployApplicationControl());

      IDE.addHandler(DeployApplicationEvent.TYPE, this);
   }

   /**
    * Bind display with presenter.
    */
   public void bindDisplay()
   {
      display.getUseExisting().addValueChangeHandler(new ValueChangeHandler<Boolean>()
      {

         @Override
         public void onValueChange(ValueChangeEvent<Boolean> event)
         {
            useExisted = event.getValue();
            boolean enable = event.getValue();
            display.enableApplicationIdField(enable);
         }
      });

      display.getApplicationIdField().addValueChangeHandler(new ValueChangeHandler<String>()
      {

         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            applicationId = event.getValue();
         }
      });
   }

   /**
    * @see org.exoplatform.ide.client.framework.paas.PaasComponent#getView(java.lang.String,
    *      org.exoplatform.ide.client.framework.paas.PaasCallback)
    */
   @Override
   public void getView(String projectName, PaasCallback paasCallback)
   {
      this.paasCallback = paasCallback;

      if (display == null)
      {
         display = GWT.create(Display.class);
      }
      bindDisplay();
      display.getUseExisting().setValue(false);
      display.enableApplicationIdField(false);
      display.getApplicationIdField().setValue("");

      this.paasCallback.onViewReceived(display.getView());
   }

   /**
    * @see org.exoplatform.ide.client.framework.paas.PaasComponent#validate()
    */
   @Override
   public void validate()
   {
      Scheduler.get().scheduleDeferred(new ScheduledCommand()
      {
         @Override
         public void execute()
         {
            applicationId = display.getApplicationIdField().getValue();
            // Check user is logged to Google App Engine.
            isUserLogged(true);
         }
      });
   }

   /**
    * @see org.exoplatform.ide.client.framework.paas.PaasComponent#deploy(org.exoplatform.ide.vfs.client.model.ProjectModel)
    */
   @Override
   public void deploy(final ProjectModel project)
   {
      Scheduler.get().scheduleDeferred(new ScheduledCommand()
      {
         @Override
         public void execute()
         {
            if (useExisted)
            {
               setApplicationId(applicationId, project);
            }
            else
            {
               IDE.fireEvent(new CreateApplicationEvent());
            }
         }
      });
   }

   /**
    * @see org.exoplatform.ide.client.framework.paas.PaasComponent#createProject(org.exoplatform.ide.vfs.client.model.ProjectModel)
    */
   @Override
   public void createProject(ProjectModel project)
   {
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.deploy.DeployApplicationHandler#onDeployApplication(org.exoplatform.ide.extension.googleappengine.client.deploy.DeployApplicationEvent)
    */
   @Override
   public void onDeployApplication(DeployApplicationEvent event)
   {
      isUserLogged(false);
   }

   /**
    * Before deploying check application type. If it is Java - build it before deploy.
    */
   private void beforeDeploy(ProjectModel project)
   {
      if (isAppEngineProject())
      {
         applicationUrl = null;
         if (ProjectResolver.APP_ENGINE_JAVA.equals(project.getProjectType()))
         {
            buildProject(project);
         }
         else
         {
            deployApplication(project);
         }
      }
      else
      {
         Dialogs.getInstance().showError(GoogleAppEngineExtension.GAE_LOCALIZATION.notAppEngineProjectError());
      }
   }

   /**
    * Perform deploying application to Google App Engine.
    */
   public void deployApplication(final ProjectModel project)
   {
      try
      {
         AutoBean<ApplicationInfo> applicationInfo = GoogleAppEngineExtension.AUTO_BEAN_FACTORY.applicationInfo();
         AutoBeanUnmarshaller<ApplicationInfo> unmarshaller =
            new AutoBeanUnmarshaller<ApplicationInfo>(applicationInfo);

         IDE.fireEvent(new OutputEvent(GoogleAppEngineExtension.GAE_LOCALIZATION.deployApplicationMessage(project
            .getName()), Type.INFO));
         GoogleAppEngineClientService.getInstance().update(currentVfs.getId(), project, applicationUrl,
            new GoogleAppEngineAsyncRequestCallback<ApplicationInfo>(unmarshaller)
            {

               @Override
               protected void onSuccess(ApplicationInfo result)
               {
                  StringBuilder link = new StringBuilder("<a href='");
                  link.append(result.getWebURL()).append("' target='_blank'>").append(result.getWebURL())
                     .append("</a>");
                  IDE.fireEvent(new OutputEvent(GoogleAppEngineExtension.GAE_LOCALIZATION.deployApplicationSuccess(
                     project.getName(), link.toString()), Type.INFO));
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   /**
    * Build Java project before deploy.
    */
   private void buildProject(ProjectModel project)
   {
      this.applicationUrl = null;
      this.builtProject = project;
      IDE.addHandler(ProjectBuiltEvent.TYPE, this);
      IDE.fireEvent(new BuildProjectEvent(project));
   }

   /**
    * @see org.exoplatform.ide.extension.maven.client.event.ProjectBuiltHandler#onProjectBuilt(org.exoplatform.ide.extension.maven.client.event.ProjectBuiltEvent)
    */
   @Override
   public void onProjectBuilt(ProjectBuiltEvent event)
   {
      IDE.removeHandler(ProjectBuiltEvent.TYPE, this);
      if (event.getBuildStatus().getDownloadUrl() != null)
      {
         applicationUrl = event.getBuildStatus().getDownloadUrl();
         deployApplication(builtProject);
      }
   }

   /**
    * Sets the application's id to configuration file (appengine-web.xml or app.yaml).
    * 
    * @param appId application's id
    */
   private void setApplicationId(String appId, final ProjectModel project)
   {
      try
      {
         GoogleAppEngineClientService.getInstance().setApplicationId(currentVfs.getId(), project.getId(), appId,
            new GoogleAppEngineAsyncRequestCallback<Object>()
            {

               @Override
               protected void onSuccess(Object result)
               {
                  beforeDeploy(project);
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   /**
    * Checks if user is logged to Google App Engine.
    * 
    * @param wizardStep
    */
   private void isUserLogged(final boolean wizardStep)
   {
      AutoBean<User> user = GoogleAppEngineExtension.AUTO_BEAN_FACTORY.user();
      AutoBeanUnmarshaller<User> unmarshaller = new AutoBeanUnmarshaller<User>(user);
      try
      {
         GoogleAppEngineClientService.getInstance().getLoggedUser(
            new GoogleAppEngineAsyncRequestCallback<User>(unmarshaller)
            {

               @Override
               protected void onSuccess(User result)
               {
                  if (!result.isAuthenticated())
                  {
                     IDE.fireEvent(new LoginEvent());
                     if (wizardStep)
                     {
                        paasCallback.onValidate(false);
                     }
                     return;
                  }
                  if (wizardStep)
                  {
                     if (display.getUseExisting().getValue() && (applicationId == null || applicationId.isEmpty()))
                     {
                        Dialogs.getInstance().showError(
                           GoogleAppEngineExtension.GAE_LOCALIZATION.deployApplicationEmptyIdMessage());
                        paasCallback.onValidate(false);
                     }
                     else
                     {
                        paasCallback.onValidate(true);
                     }
                  }
                  else
                  {
                     beforeDeploy(currentProject);
                  }
               }

               /**
                * @see org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineAsyncRequestCallback#onFailure(java.lang.Throwable)
                */
               @Override
               protected void onFailure(Throwable exception)
               {
                  super.onFailure(exception);
                  if (wizardStep)
                  {
                     paasCallback.onValidate(false);
                  }
                  // TODO check response
               }
            });
      }
      catch (RequestException e)
      {
         if (wizardStep)
         {
            paasCallback.onValidate(false);
         }
      }
   }
}
