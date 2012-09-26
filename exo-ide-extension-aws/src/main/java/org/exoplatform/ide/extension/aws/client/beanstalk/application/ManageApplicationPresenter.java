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
package org.exoplatform.ide.extension.aws.client.beanstalk.application;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.aws.client.AWSExtension;
import org.exoplatform.ide.extension.aws.client.AwsAsyncRequestCallback;
import org.exoplatform.ide.extension.aws.client.beanstalk.ApplicationVersionListUnmarshaller;
import org.exoplatform.ide.extension.aws.client.beanstalk.BeanstalkClientService;
import org.exoplatform.ide.extension.aws.client.beanstalk.application.update.ApplicationUpdatedHandler;
import org.exoplatform.ide.extension.aws.client.beanstalk.application.update.UpdateApplicationEvent;
import org.exoplatform.ide.extension.aws.client.beanstalk.application.versions.CreateVersionEvent;
import org.exoplatform.ide.extension.aws.client.beanstalk.application.versions.DeleteVersionEvent;
import org.exoplatform.ide.extension.aws.client.beanstalk.application.versions.HasVersionActions;
import org.exoplatform.ide.extension.aws.client.beanstalk.application.versions.VersionCreatedHandler;
import org.exoplatform.ide.extension.aws.client.beanstalk.application.versions.VersionDeletedHandler;
import org.exoplatform.ide.extension.aws.client.beanstalk.environment.CreateEnvironmentEvent;
import org.exoplatform.ide.extension.aws.client.beanstalk.environment.EnvironmentCreatedHandler;
import org.exoplatform.ide.extension.aws.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.aws.shared.beanstalk.ApplicationInfo;
import org.exoplatform.ide.extension.aws.shared.beanstalk.ApplicationVersionInfo;
import org.exoplatform.ide.extension.aws.shared.beanstalk.EnvironmentInfo;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ItemUnmarshaller;
import org.exoplatform.ide.vfs.client.model.ItemWrapper;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.StringProperty;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.Date;
import java.util.List;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Sep 19, 2012 11:34:35 AM anya $
 * 
 */
public class ManageApplicationPresenter implements ProjectOpenedHandler, ProjectClosedHandler,
   ManageApplicationHandler, VfsChangedHandler, ViewClosedHandler
{

   interface Display extends IsView
   {
      // GeneralInfo

      HasValue<String> getApplicationNameField();

      HasValue<String> getUrlField();

      HasValue<String> getDescriptionField();

      HasValue<String> getCreateDateField();

      HasValue<String> getUpdatedDateField();

      HasClickHandlers getDeleteButton();

      HasClickHandlers getUpdateDescriptionButton();

      HasClickHandlers getCloseButton();

      HasClickHandlers getCreateVersionButton();

      HasClickHandlers getLaunchEnvironmentButton();

      // Versions

      ListGridItem<ApplicationVersionInfo> getVersionsGrid();

      HasVersionActions getVersionActions();

      void selectVersionsTab();
   }

   private Display display;

   private ProjectModel openedProject;

   private VirtualFileSystemInfo currentVfs;

   private ApplicationInfo applicationInfo;

   private EnvironmentInfo environmentInfo;

   private VersionDeletedHandler versionDeletedHandler = new VersionDeletedHandler()
   {

      @Override
      public void onVersionDeleted(ApplicationVersionInfo version)
      {
         getVersions();
      }
   };

   private ApplicationUpdatedHandler applicationUpdatedHandler = new ApplicationUpdatedHandler()
   {

      @Override
      public void onApplicationUpdated(ApplicationInfo application)
      {
         applicationInfo = application;
         if (display != null)
         {
            display.getDescriptionField().setValue(application.getDescription());
         }
      }
   };

   private VersionCreatedHandler versionCreatedHandler = new VersionCreatedHandler()
   {

      @Override
      public void onVersionCreate(ApplicationVersionInfo version)
      {
         getVersions();
         display.selectVersionsTab();
      }
   };

   private EnvironmentCreatedHandler environmentCreatedHandler = new EnvironmentCreatedHandler()
   {

      @Override
      public void onEnvironmentCreated(EnvironmentInfo environmentInfo)
      {
         // TODO do actions when environment is created
      }
   };

   public ManageApplicationPresenter()
   {
      IDE.getInstance().addControl(new ManageApplicationControl());

      IDE.addHandler(ProjectOpenedEvent.TYPE, this);
      IDE.addHandler(ProjectClosedEvent.TYPE, this);
      IDE.addHandler(ManageApplicationEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
      IDE.addHandler(VfsChangedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.application.ManageApplicationHandler#onManageApplication(org.exoplatform.ide.extension.aws.client.beanstalk.application.ManageApplicationEvent)
    */
   @Override
   public void onManageApplication(ManageApplicationEvent event)
   {
      if (openedProject == null || !AWSExtension.isAWSApplication(openedProject))
      {
         Dialogs.getInstance().showInfo(AWSExtension.LOCALIZATION_CONSTANT.notAWSApplictaionMessage());
         return;
      }

      environmentInfo = null;
      getApplicationInfo();
   }

   public void bindDisplay()
   {
      display.getCloseButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
      });

      display.getDeleteButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            askForDelete();
         }
      });

      display.getUpdateDescriptionButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            IDE.fireEvent(new UpdateApplicationEvent(currentVfs.getId(), openedProject.getId(), applicationInfo,
               applicationUpdatedHandler));
         }
      });

      display.getVersionActions().addDeleteHandler(new SelectionHandler<ApplicationVersionInfo>()
      {

         @Override
         public void onSelection(SelectionEvent<ApplicationVersionInfo> event)
         {
            IDE.fireEvent(new DeleteVersionEvent(currentVfs.getId(), openedProject.getId(), event.getSelectedItem(),
               versionDeletedHandler));
         }
      });

      display.getCreateVersionButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            IDE.fireEvent(new CreateVersionEvent(currentVfs.getId(), openedProject, applicationInfo.getName(),
               versionCreatedHandler));
         }
      });

      display.getLaunchEnvironmentButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            IDE.fireEvent(new CreateEnvironmentEvent(currentVfs.getId(), openedProject.getId(), applicationInfo
               .getName(), environmentCreatedHandler));
         }
      });
   }

   /**
    * @see org.exoplatform.ide.client.framework.project.ProjectClosedHandler#onProjectClosed(org.exoplatform.ide.client.framework.project.ProjectClosedEvent)
    */
   @Override
   public void onProjectClosed(ProjectClosedEvent event)
   {
      this.openedProject = null;
   }

   /**
    * @see org.exoplatform.ide.client.framework.project.ProjectOpenedHandler#onProjectOpened(org.exoplatform.ide.client.framework.project.ProjectOpenedEvent)
    */
   @Override
   public void onProjectOpened(ProjectOpenedEvent event)
   {
      this.openedProject = event.getProject();
   }

   /**
    * @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework.application.event.VfsChangedEvent)
    */
   @Override
   public void onVfsChanged(VfsChangedEvent event)
   {
      this.currentVfs = event.getVfsInfo();
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

   private void askForDelete()
   {
      final String applicationName = applicationInfo.getName();
      Dialogs.getInstance().ask(AWSExtension.LOCALIZATION_CONSTANT.deleteApplicationTitle(),
         AWSExtension.LOCALIZATION_CONSTANT.deleteApplicationQuestion(applicationName),
         new BooleanValueReceivedHandler()
         {

            @Override
            public void booleanValueReceived(Boolean value)
            {
               if (value != null && value)
               {
                  deleteApplication(applicationName);
               }
            }
         });
   }

   private void deleteApplication(final String applicationName)
   {
      try
      {
         BeanstalkClientService.getInstance().deleteApplication(currentVfs.getId(), openedProject.getId(),
            new AwsAsyncRequestCallback<Object>(new LoggedInHandler()
            {

               @Override
               public void onLoggedIn()
               {
                  deleteApplication(applicationName);
               }
            })
            {
               @Override
               protected void processFail(Throwable exception)
               {
                  String message = AWSExtension.LOCALIZATION_CONSTANT.deleteApplicationFailed(applicationName);
                  if (exception instanceof ServerException && ((ServerException)exception).getMessage() != null)
                  {
                     message += "<br>" + ((ServerException)exception).getMessage();
                  }
                  IDE.fireEvent(new OutputEvent(message, Type.ERROR));
               }

               @Override
               protected void onSuccess(Object result)
               {
                  IDE.fireEvent(new OutputEvent(AWSExtension.LOCALIZATION_CONSTANT
                     .deleteApplicationSuccess(applicationName), Type.INFO));
                  if (display != null)
                  {
                     IDE.getInstance().closeView(display.asView().getId());
                  }
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   private void getApplicationInfo()
   {
      AutoBean<ApplicationInfo> autoBean = AWSExtension.AUTO_BEAN_FACTORY.applicationInfo();
      try
      {
         BeanstalkClientService.getInstance().getApplicationInfo(
            currentVfs.getId(),
            openedProject.getId(),
            new AwsAsyncRequestCallback<ApplicationInfo>(new AutoBeanUnmarshaller<ApplicationInfo>(autoBean),
               new LoggedInHandler()
               {

                  @Override
                  public void onLoggedIn()
                  {
                     getApplicationInfo();
                  }
               })
            {

               @Override
               protected void processFail(Throwable exception)
               {
                  IDE.fireEvent(new ExceptionThrownEvent(exception));
               }

               @Override
               protected void onSuccess(ApplicationInfo result)
               {
                  applicationInfo = result;
                  getEnvironmentId();
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   /**
    * Get the application's environment identifier from the project properties.
    */
   private void getEnvironmentId()
   {
      try
      {
         VirtualFileSystem.getInstance().getItemById(openedProject.getId(),
            new AsyncRequestCallback<ItemWrapper>(new ItemUnmarshaller(new ItemWrapper(openedProject)))
            {

               @Override
               protected void onSuccess(ItemWrapper result)
               {
                  StringProperty awsEnvironmentId = (StringProperty)result.getItem().getProperty("awsEnvironmentId");
                  if (awsEnvironmentId != null && !awsEnvironmentId.getValue().isEmpty())
                  {
                     getEnvironmentInfo(awsEnvironmentId.getValue().get(0));
                  }
                  else
                  {
                     openView();
                  }
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  openView();
                  exception.printStackTrace();
               }
            });
      }
      catch (RequestException e)
      {
         openView();
         e.printStackTrace();
      }
   }

   /**
    * Get the environment info by the provided identifier and open view.
    * 
    * @param environmentId environment identifier
    */
   private void getEnvironmentInfo(String environmentId)
   {
      AutoBean<EnvironmentInfo> autoBean = AWSExtension.AUTO_BEAN_FACTORY.environmentInfo();
      AutoBeanUnmarshaller<EnvironmentInfo> unmarshaller = new AutoBeanUnmarshaller<EnvironmentInfo>(autoBean);
      try
      {
         BeanstalkClientService.getInstance().getEnvironmentInfo(environmentId,
            new AsyncRequestCallback<EnvironmentInfo>(unmarshaller)
            {
               @Override
               protected void onSuccess(EnvironmentInfo result)
               {
                  environmentInfo = result;
                  openView();
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  openView();
                  exception.printStackTrace();
               }
            });
      }
      catch (RequestException e)
      {
         openView();
         e.printStackTrace();
      }
   }

   private void openView()
   {
      if (display == null)
      {
         display = GWT.create(Display.class);
         IDE.getInstance().openView(display.asView());
         bindDisplay();
      }
      display.getApplicationNameField().setValue(applicationInfo.getName());
      if (environmentInfo != null && environmentInfo.getEndpointUrl() != null)
      {
         String envUrl = environmentInfo.getEndpointUrl();
         if (!envUrl.startsWith("http"))
         {
            envUrl = "http://" + envUrl;
         }

         display.getUrlField().setValue("<a href =\"" + envUrl + "\" target=\"_blank\">" + envUrl + "</a>");
      }
      else
      {
         display.getUrlField().setValue("n/a");
      }
      display.getDescriptionField().setValue(
         applicationInfo.getDescription() != null ? applicationInfo.getDescription() : "");
      display.getCreateDateField().setValue(new Date(applicationInfo.getCreated()).toString());
      display.getUpdatedDateField().setValue(new Date(applicationInfo.getUpdated()).toString());

      getVersions();
   }

   private void getVersions()
   {
      try
      {
         BeanstalkClientService.getInstance().getVersions(
            currentVfs.getId(),
            openedProject.getId(),
            new AwsAsyncRequestCallback<List<ApplicationVersionInfo>>(new ApplicationVersionListUnmarshaller(),
               new LoggedInHandler()
               {

                  @Override
                  public void onLoggedIn()
                  {
                     getVersions();
                  }
               })
            {

               @Override
               protected void processFail(Throwable exception)
               {
               }

               @Override
               protected void onSuccess(List<ApplicationVersionInfo> result)
               {
                  display.getVersionsGrid().setValue(result);
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }
}
