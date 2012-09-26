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
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HasValue;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.gwtframework.commons.rest.RequestStatusHandler;
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
import org.exoplatform.ide.extension.aws.client.beanstalk.create.EnvironmentRequestStatusHandler;
import org.exoplatform.ide.extension.aws.client.beanstalk.environment.CreateEnvironmentEvent;
import org.exoplatform.ide.extension.aws.client.beanstalk.environment.EnvironmentCreatedHandler;
import org.exoplatform.ide.extension.aws.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.aws.shared.beanstalk.ApplicationInfo;
import org.exoplatform.ide.extension.aws.shared.beanstalk.ApplicationVersionInfo;
import org.exoplatform.ide.extension.aws.shared.beanstalk.EnvironmentInfo;
import org.exoplatform.ide.extension.aws.shared.beanstalk.EnvironmentStatus;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
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

   protected RequestStatusHandler environmentStatusHandler;

   // TODO
   private EnvironmentInfo newEnvironmentInfo;

   /**
    * Delay in millisecond between environment status checking.
    */
   private static final int delay = 2000;

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
         newEnvironmentInfo = environmentInfo;
         IDE.fireEvent(new OutputEvent(AWSExtension.LOCALIZATION_CONSTANT.createEnvironmentLaunching(newEnvironmentInfo
            .getName()), Type.INFO));
         environmentStatusHandler = new EnvironmentRequestStatusHandler(newEnvironmentInfo.getName());
         environmentStatusHandler.requestInProgress(openedProject.getId());
         checkEnvironmentStatusTimer.schedule(delay);
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

      display.getVersionActions().addLaunchHandler(new SelectionHandler<ApplicationVersionInfo>()
      {

         @Override
         public void onSelection(SelectionEvent<ApplicationVersionInfo> event)
         {
            //            IDE.fireEvent(new LaunchEnvironmentEvent(currentVfs.getId(), openedProject.getId(), applicationInfo
            //               .getName(), environmentLaunchedHandler));
            IDE.fireEvent(new CreateEnvironmentEvent(currentVfs.getId(), openedProject.getId(), applicationInfo
               .getName(), event.getSelectedItem().getVersionLabel(), environmentCreatedHandler));
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
               .getName(), null, environmentCreatedHandler));
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
                  openView();
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
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

   /**
    * A timer for periodically sending request of environment status.
    */
   private Timer checkEnvironmentStatusTimer = new Timer()
   {
      @Override
      public void run()
      {
         AutoBean<EnvironmentInfo> autoBean = AWSExtension.AUTO_BEAN_FACTORY.environmentInfo();
         AutoBeanUnmarshaller<EnvironmentInfo> unmarshaller = new AutoBeanUnmarshaller<EnvironmentInfo>(autoBean);
         try
         {
            BeanstalkClientService.getInstance().getEnvironmentInfo(newEnvironmentInfo.getId(),
               new AsyncRequestCallback<EnvironmentInfo>(unmarshaller)
               {
                  @Override
                  protected void onSuccess(EnvironmentInfo result)
                  {
                     updateEnvironmentStatus(result);
                     if (result.getStatus() == EnvironmentStatus.Launching)
                     {
                        schedule(delay);
                     }
                  }

                  @Override
                  protected void onFailure(Throwable exception)
                  {
                     String message =
                        AWSExtension.LOCALIZATION_CONSTANT.createEnvironmentFailed(newEnvironmentInfo.getName());
                     if (exception instanceof ServerException && ((ServerException)exception).getMessage() != null)
                     {
                        message += "<br>" + ((ServerException)exception).getMessage();
                     }
                     IDE.fireEvent(new OutputEvent(message, Type.ERROR));
                     environmentStatusHandler.requestError(openedProject.getId(), exception);
                  }
               });
         }
         catch (RequestException e)
         {
            IDE.fireEvent(new ExceptionThrownEvent(e));
         }
      }
   };

   private void updateEnvironmentStatus(EnvironmentInfo environment)
   {
      if (environment.getStatus() == EnvironmentStatus.Ready)
      {
         IDE.fireEvent(new OutputEvent(AWSExtension.LOCALIZATION_CONSTANT.createApplicationStartedOnUrl(
            environment.getApplicationName(), getAppUrl(environment)), Type.INFO));

         environmentStatusHandler.requestFinished(openedProject.getId());
      }
   }

   private String getAppUrl(EnvironmentInfo environment)
   {
      String appUrl = environment.getEndpointUrl();
      if (!appUrl.startsWith("http"))
      {
         appUrl = "http://" + appUrl;
      }
      appUrl = "<a href=\"" + appUrl + "\" target=\"_blank\">" + appUrl + "</a>";
      return appUrl;
   }
}
