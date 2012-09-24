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
package org.exoplatform.ide.extension.aws.client.beanstalk.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HasValue;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.gwtframework.commons.rest.RequestStatusHandler;
import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.job.JobManager;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.project.Language;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.project.ProjectType;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.util.ProjectResolver;
import org.exoplatform.ide.extension.aws.client.AWSExtension;
import org.exoplatform.ide.extension.aws.client.beanstalk.BeanstalkAsyncRequestCallback;
import org.exoplatform.ide.extension.aws.client.beanstalk.BeanstalkClientService;
import org.exoplatform.ide.extension.aws.client.beanstalk.SolutionStackListUnmarshaller;
import org.exoplatform.ide.extension.aws.client.beanstalk.login.LoggedInHandler;
import org.exoplatform.ide.extension.aws.shared.beanstalk.ApplicationInfo;
import org.exoplatform.ide.extension.aws.shared.beanstalk.CreateApplicationRequest;
import org.exoplatform.ide.extension.aws.shared.beanstalk.CreateEnvironmentRequest;
import org.exoplatform.ide.extension.aws.shared.beanstalk.EnvironmentInfo;
import org.exoplatform.ide.extension.aws.shared.beanstalk.EnvironmentStatus;
import org.exoplatform.ide.extension.aws.shared.beanstalk.SolutionStack;
import org.exoplatform.ide.extension.maven.client.event.BuildProjectEvent;
import org.exoplatform.ide.extension.maven.client.event.ProjectBuiltEvent;
import org.exoplatform.ide.extension.maven.client.event.ProjectBuiltHandler;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.List;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Sep 17, 2012 11:54:00 AM anya $
 * 
 */
public class CreateApplicationPresenter implements ProjectOpenedHandler, ProjectClosedHandler, VfsChangedHandler,
   CreateApplicationHandler, ViewClosedHandler, ProjectBuiltHandler
{
   interface Display extends IsView
   {
      // Create Application step

      TextFieldItem getNameField();

      TextFieldItem getDescriptionField();

      TextFieldItem getS3BucketField();

      TextFieldItem getS3KeyField();

      // Create Environment step
      TextFieldItem getEnvNameField();

      TextFieldItem getEnvDescriptionField();

      HasValue<String> getSolutionStackField();

      HasValue<Boolean> getLaunchEnvField();

      void setSolutionStackValues(String[] values);

      HasClickHandlers getNextButton();

      HasClickHandlers getBackButton();

      HasClickHandlers getFinishButton();

      HasClickHandlers getCancelButton();

      void enableNextButton(boolean enabled);

      void enableFinishButton(boolean enabled);

      void enableCreateEnvironmentStep(boolean enabled);

      void focusInApplicationNameField();

      void showCreateApplicationStep();

      void showCreateEnvironmentStep();
   }

   private Display display;

   private ProjectModel openedProject;

   private VirtualFileSystemInfo vfsInfo;

   private String warUrl = null;

   private boolean launchEnvironment;

   private EnvironmentInfo environment;

   /**
    * Delay in millisecond between environment status checking.
    */
   private static final int delay = 2000;

   protected RequestStatusHandler statusHandler;

   public CreateApplicationPresenter()
   {
      IDE.getInstance().addControl(new CreateApplicationControl());

      IDE.addHandler(ProjectClosedEvent.TYPE, this);
      IDE.addHandler(ProjectOpenedEvent.TYPE, this);
      IDE.addHandler(VfsChangedEvent.TYPE, this);
      IDE.addHandler(VfsChangedEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
      IDE.addHandler(CreateApplicationEvent.TYPE, this);
   }

   public void bindDisplay()
   {
      display.getCancelButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
      });

      display.getNextButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            display.showCreateEnvironmentStep();
         }
      });

      display.getBackButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            display.showCreateApplicationStep();
         }
      });

      display.getFinishButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            warUrl = null;
            launchEnvironment = display.getLaunchEnvField().getValue();
            beforeCreation();
         }
      });

      display.getNameField().addValueChangeHandler(new ValueChangeHandler<String>()
      {

         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            display.enableNextButton(event.getValue() != null);
         }
      });

      display.getEnvNameField().addValueChangeHandler(new ValueChangeHandler<String>()
      {

         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            display.enableFinishButton(!display.getLaunchEnvField().getValue()
               || (display.getLaunchEnvField().getValue() && event.getValue() != null && !event.getValue().isEmpty()));
         }
      });

      display.getLaunchEnvField().addValueChangeHandler(new ValueChangeHandler<Boolean>()
      {

         @Override
         public void onValueChange(ValueChangeEvent<Boolean> event)
         {
            display.enableCreateEnvironmentStep(event.getValue());
            display.enableFinishButton(!event.getValue()
               || (event.getValue() && display.getEnvNameField().getValue() != null && !display.getEnvNameField()
                  .getValue().isEmpty()));
         }
      });
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.create.CreateApplicationHandler#onCreateApplication(org.exoplatform.ide.extension.aws.client.beanstalk.create.CreateApplicationEvent)
    */
   @Override
   public void onCreateApplication(CreateApplicationEvent event)
   {
      if (display == null)
      {
         display = GWT.create(Display.class);
         IDE.getInstance().openView(display.asView());
         bindDisplay();
      }
      display.showCreateApplicationStep();
      display.focusInApplicationNameField();
      display.enableNextButton(false);
      display.enableFinishButton(false);
      display.getLaunchEnvField().setValue(true);
      getSolutionStacks();
      statusHandler = new EnvironmentRequestStatusHandler(display.getEnvNameField().getValue());
   }

   /**
    * @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework.application.event.VfsChangedEvent)
    */
   @Override
   public void onVfsChanged(VfsChangedEvent event)
   {
      this.vfsInfo = event.getVfsInfo();
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

   private void beforeCreation()
   {
      ProjectType projectType = ProjectType.fromValue(openedProject.getProjectType());
      if (ProjectResolver.getProjectTypesByLanguage(Language.JAVA).contains(projectType))
      {
         IDE.addHandler(ProjectBuiltEvent.TYPE, this);
         JobManager.get().showJobSeparated();
         IDE.fireEvent(new BuildProjectEvent(openedProject));
      }
      else
      {
         createApplication();
      }
   }

   public void createApplication()
   {
      final String applicationName = display.getNameField().getValue();
      CreateApplicationRequest createApplicationRequest =
         AWSExtension.AUTO_BEAN_FACTORY.createApplicationRequest().as();
      createApplicationRequest.setApplicationName(applicationName);
      createApplicationRequest.setDescription(display.getDescriptionField().getValue());
      createApplicationRequest.setS3Bucket(display.getS3BucketField().getValue());
      createApplicationRequest.setS3Key(display.getS3KeyField().getValue());
      createApplicationRequest.setWar(warUrl);

      AutoBean<ApplicationInfo> autoBean = AWSExtension.AUTO_BEAN_FACTORY.applicationInfo();

      try
      {
         BeanstalkClientService.getInstance().createApplication(
            vfsInfo.getId(),
            openedProject.getId(),
            createApplicationRequest,
            new BeanstalkAsyncRequestCallback<ApplicationInfo>(new AutoBeanUnmarshaller<ApplicationInfo>(autoBean),
               new LoggedInHandler()
               {
                  @Override
                  public void onLoggedIn()
                  {
                     createApplication();
                  }
               })
            {

               @Override
               protected void onSuccess(ApplicationInfo result)
               {
                  IDE.fireEvent(new OutputEvent(AWSExtension.LOCALIZATION_CONSTANT.createApplicationSuccess(result
                     .getName()), Type.INFO));
                  if (launchEnvironment)
                  {
                     createEnvironment(result.getName());
                  }
                  else
                  {
                     IDE.getInstance().closeView(display.asView().getId());
                  }
                  IDE.fireEvent(new RefreshBrowserEvent(openedProject));
               }

               @Override
               protected void processFail(Throwable exception)
               {
                  String message = AWSExtension.LOCALIZATION_CONSTANT.createApplicationFailed(applicationName);
                  if (exception instanceof ServerException && ((ServerException)exception).getMessage() != null)
                  {
                     message += "<br>" + ((ServerException)exception).getMessage();
                  }
                  IDE.fireEvent(new OutputEvent(message, Type.ERROR));
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   private void getSolutionStacks()
   {
      try
      {
         BeanstalkClientService.getInstance().getAvailableSolutionStacks(
            new BeanstalkAsyncRequestCallback<List<SolutionStack>>(new SolutionStackListUnmarshaller(),
               new LoggedInHandler()
               {
                  @Override
                  public void onLoggedIn()
                  {
                     getSolutionStacks();
                  }
               })
            {
               @Override
               protected void onSuccess(List<SolutionStack> result)
               {
                  String[] values = new String[result.size()];
                  int i = 0;
                  for (SolutionStack solutionStack : result)
                  {
                     values[i] = solutionStack.getName();
                     i++;
                  }
                  display.setSolutionStackValues(values);
               }

               @Override
               protected void processFail(Throwable exception)
               {
                  IDE.fireEvent(new ExceptionThrownEvent(exception));
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   public void createEnvironment(final String applicationName)
   {
      statusHandler.requestInProgress(openedProject.getId());

      final String environmentName = display.getEnvNameField().getValue();
      CreateEnvironmentRequest createEnvironmentRequest =
         AWSExtension.AUTO_BEAN_FACTORY.createEnvironmentRequest().as();
      createEnvironmentRequest.setApplicationName(applicationName);
      createEnvironmentRequest.setDescription(display.getEnvDescriptionField().getValue());
      createEnvironmentRequest.setEnvironmentName(environmentName);
      createEnvironmentRequest.setSolutionStackName(display.getSolutionStackField().getValue());

      AutoBean<EnvironmentInfo> autoBean = AWSExtension.AUTO_BEAN_FACTORY.environmentInfo();
      try
      {
         BeanstalkClientService.getInstance().createEnvironment(
            vfsInfo.getId(),
            openedProject.getId(),
            createEnvironmentRequest,
            new BeanstalkAsyncRequestCallback<EnvironmentInfo>(new AutoBeanUnmarshaller<EnvironmentInfo>(autoBean),
               new LoggedInHandler()
               {
                  @Override
                  public void onLoggedIn()
                  {
                     createEnvironment(applicationName);
                  }
               })
            {

               @Override
               protected void processFail(Throwable exception)
               {
                  statusHandler.requestError(openedProject.getId(), exception);

                  String message = AWSExtension.LOCALIZATION_CONSTANT.createEnvironmentFailed(environmentName);
                  if (exception instanceof ServerException && ((ServerException)exception).getMessage() != null)
                  {
                     message += "<br>" + ((ServerException)exception).getMessage();
                  }
                  IDE.fireEvent(new OutputEvent(message, Type.ERROR));
               }

               @Override
               protected void onSuccess(EnvironmentInfo result)
               {
                  environment = result;
                  if (display != null)
                  {
                     IDE.getInstance().closeView(display.asView().getId());
                  }
                  IDE.fireEvent(new OutputEvent(AWSExtension.LOCALIZATION_CONSTANT
                     .createEnvironmentLaunching(environmentName), Type.INFO));
                  checkEnvironmentStatusTimer.schedule(delay);
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
            BeanstalkClientService.getInstance().getEnvironmentInfo(environment.getId(),
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
                     String message = AWSExtension.LOCALIZATION_CONSTANT.createEnvironmentFailed(environment.getName());
                     if (exception instanceof ServerException && ((ServerException)exception).getMessage() != null)
                     {
                        message += "<br>" + ((ServerException)exception).getMessage();
                     }
                     IDE.fireEvent(new OutputEvent(message, Type.ERROR));
                     statusHandler.requestError(openedProject.getId(), exception);
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

         statusHandler.requestFinished(openedProject.getId());
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

   /**
    * @see org.exoplatform.ide.extension.maven.client.event.ProjectBuiltHandler#onProjectBuilt(org.exoplatform.ide.extension.maven.client.event.ProjectBuiltEvent)
    */
   @Override
   public void onProjectBuilt(ProjectBuiltEvent event)
   {
      IDE.removeHandler(event.getAssociatedType(), this);
      if (event.getBuildStatus().getDownloadUrl() != null)
      {
         warUrl = event.getBuildStatus().getDownloadUrl();
         createApplication();
      }
   }

}
