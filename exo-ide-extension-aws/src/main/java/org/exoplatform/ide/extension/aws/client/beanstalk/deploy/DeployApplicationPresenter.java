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
package org.exoplatform.ide.extension.aws.client.beanstalk.deploy;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.gwtframework.commons.rest.RequestStatusHandler;
import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.gwtframework.ui.client.component.GWTLoader;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.paas.DeployResultHandler;
import org.exoplatform.ide.client.framework.paas.HasPaaSActions;
import org.exoplatform.ide.client.framework.project.ProjectType;
import org.exoplatform.ide.client.framework.template.ProjectTemplate;
import org.exoplatform.ide.client.framework.template.TemplateService;
import org.exoplatform.ide.extension.aws.client.AWSExtension;
import org.exoplatform.ide.extension.aws.client.AWSLocalizationConstant;
import org.exoplatform.ide.extension.aws.client.AwsAsyncRequestCallback;
import org.exoplatform.ide.extension.aws.client.beanstalk.BeanstalkClientService;
import org.exoplatform.ide.extension.aws.client.beanstalk.SolutionStackListUnmarshaller;
import org.exoplatform.ide.extension.aws.client.beanstalk.create.EnvironmentRequestStatusHandler;
import org.exoplatform.ide.extension.aws.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.aws.shared.beanstalk.ApplicationInfo;
import org.exoplatform.ide.extension.aws.shared.beanstalk.CreateApplicationRequest;
import org.exoplatform.ide.extension.aws.shared.beanstalk.CreateEnvironmentRequest;
import org.exoplatform.ide.extension.aws.shared.beanstalk.EnvironmentHealth;
import org.exoplatform.ide.extension.aws.shared.beanstalk.EnvironmentInfo;
import org.exoplatform.ide.extension.aws.shared.beanstalk.EnvironmentStatus;
import org.exoplatform.ide.extension.aws.shared.beanstalk.Event;
import org.exoplatform.ide.extension.aws.shared.beanstalk.EventsList;
import org.exoplatform.ide.extension.aws.shared.beanstalk.ListEventsRequest;
import org.exoplatform.ide.extension.aws.shared.beanstalk.SolutionStack;
import org.exoplatform.ide.extension.maven.client.event.BuildProjectEvent;
import org.exoplatform.ide.extension.maven.client.event.ProjectBuiltEvent;
import org.exoplatform.ide.extension.maven.client.event.ProjectBuiltHandler;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ChildrenUnmarshaller;
import org.exoplatform.ide.vfs.client.marshal.ProjectUnmarshaller;
import org.exoplatform.ide.vfs.client.model.ItemWrapper;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.StringProperty;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:azatsarynnyy@exoplatform.com">Artem Zatsarynnyy</a>
 * @version $Id: DeployApplicationPresenter.java Sep 25, 2012 11:55:34 AM azatsarynnyy $
 *
 */
public class DeployApplicationPresenter implements HasPaaSActions, VfsChangedHandler, ProjectBuiltHandler
{
   interface Display
   {
      TextFieldItem getNameField();

      TextFieldItem getEnvNameField();

      HasValue<String> getSolutionStackField();

      void setSolutionStackValues(String[] values);

      Composite getView();
   }

   private static final AWSLocalizationConstant LOCALIZATION_CONSTANT = AWSExtension.LOCALIZATION_CONSTANT;

   /**
    * Label of AWS Beanstalk application initial version.
    */
   private static final String INITIAL_VERSION_LABEL = "initial version";

   private VirtualFileSystemInfo vfsInfo;

   private ProjectModel project;

   private Display display;

   /**
    * Public url to war file of application.
    */
   private String warUrl;

   private DeployResultHandler deployResultHandler;

   /**
    * Info about environment for launching application.
    * <code>null</code> if environment is not launched.
    */
   private EnvironmentInfo environment;

   /**
    * Info about created AWS Beanstalk application.
    * <code>null</code> if application is not created.
    */
   private ApplicationInfo applicationInfo;

   private String projectName;

   /**
    * Time of last received event.
    */
   protected long lastReceivedEventTime;

   /**
    * Delay in millisecond between environment status checking.
    */
   private static final int delay = 2000;

   protected RequestStatusHandler environmentStatusHandler;

   public DeployApplicationPresenter()
   {
      IDE.addHandler(VfsChangedEvent.TYPE, this);
   }

   /**
    * Bind display with presenter.
    */
   private void bindDisplay()
   {
   }

   /**
    * @see org.exoplatform.ide.client.framework.paas.HasPaaSActions#getDeployView(java.lang.String,
    *       org.exoplatform.ide.client.framework.project.ProjectType)
    */
   @Override
   public Composite getDeployView(String projectName, ProjectType projectType)
   {
      this.projectName = projectName;
      if (display == null)
      {
         display = GWT.create(Display.class);
      }
      bindDisplay();
      getSolutionStacks();
      return display.getView();
   }

   /**
    * @see org.exoplatform.ide.client.framework.paas.HasPaaSActions#deploy(org.exoplatform.ide.client.framework.template.ProjectTemplate,
    *       org.exoplatform.ide.client.framework.paas.DeployResultHandler)
    */
   @Override
   public void deploy(ProjectTemplate projectTemplate, DeployResultHandler deployResultHandler)
   {
      this.deployResultHandler = deployResultHandler;
      environmentStatusHandler = new EnvironmentRequestStatusHandler(display.getEnvNameField().getValue());
      createProject(projectTemplate);
   }

   /**
    * Creates a new project from template.
    * 
    * @param projectTemplate template of the project
    */
   private void createProject(ProjectTemplate projectTemplate)
   {
      final Loader loader = new GWTLoader();
      loader.setMessage(LOCALIZATION_CONSTANT.creatingProject());
      loader.show();
      try
      {
         TemplateService.getInstance().createProjectFromTemplate(vfsInfo.getId(), vfsInfo.getRoot().getId(),
            projectName, projectTemplate.getName(),
            new AsyncRequestCallback<ProjectModel>(new ProjectUnmarshaller(new ProjectModel()))
            {
               @Override
               protected void onSuccess(ProjectModel result)
               {
                  loader.hide();
                  project = result;
                  deployResultHandler.onProjectCreated(project);
                  beforeDeploy();
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  loader.hide();
                  IDE.fireEvent(new ExceptionThrownEvent(exception));
               }
            });
      }
      catch (RequestException e)
      {
         loader.hide();
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   /**
    * Builds project if it's a Maven project and creates application.
    */
   private void beforeDeploy()
   {
      try
      {
         VirtualFileSystem.getInstance().getChildren(project,
            new AsyncRequestCallback<List<Item>>(new ChildrenUnmarshaller(new ArrayList<Item>()))
            {
               @Override
               protected void onSuccess(List<Item> result)
               {
                  project.getChildren().setItems(result);
                  for (Item i : result)
                  {
                     if (i.getItemType() == ItemType.FILE && "pom.xml".equals(i.getName()))
                     {
                        buildApplication();
                        return;
                     }
                  }
                  createApplication();
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  IDE.fireEvent(new ExceptionThrownEvent(exception, "Can't receive children of project "
                     + project.getName()));
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   private void createApplication()
   {
      final String applicationName = display.getNameField().getValue();
      CreateApplicationRequest createApplicationRequest =
         AWSExtension.AUTO_BEAN_FACTORY.createApplicationRequest().as();
      createApplicationRequest.setApplicationName(applicationName);
      //createApplicationRequest.setDescription(display.getDescriptionField().getValue());
      createApplicationRequest.setDescription("");
      //createApplicationRequest.setS3Bucket(display.getS3BucketField().getValue());
      createApplicationRequest.setS3Bucket("");
      //createApplicationRequest.setS3Key(display.getS3KeyField().getValue());
      createApplicationRequest.setS3Key("");
      createApplicationRequest.setWar(warUrl);

      AutoBean<ApplicationInfo> autoBean = AWSExtension.AUTO_BEAN_FACTORY.applicationInfo();

      try
      {
         BeanstalkClientService.getInstance().createApplication(
            vfsInfo.getId(),
            project.getId(),
            createApplicationRequest,
            new AwsAsyncRequestCallback<ApplicationInfo>(new AutoBeanUnmarshaller<ApplicationInfo>(autoBean),
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
                  applicationInfo = result;
                  IDE.fireEvent(new OutputEvent(AWSExtension.LOCALIZATION_CONSTANT.createApplicationSuccess(result
                     .getName()), Type.INFO));
                  createEnvironment(result.getName());
                  IDE.fireEvent(new RefreshBrowserEvent(project));
               }

               @Override
               protected void processFail(Throwable exception)
               {
                  deployResultHandler.onDeployFinished(false);

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

   private void createEnvironment(final String applicationName)
   {
      environmentStatusHandler.requestInProgress(project.getId());

      final String environmentName = display.getEnvNameField().getValue();
      CreateEnvironmentRequest createEnvironmentRequest =
         AWSExtension.AUTO_BEAN_FACTORY.createEnvironmentRequest().as();
      createEnvironmentRequest.setApplicationName(applicationName);
      //createEnvironmentRequest.setDescription(display.getEnvDescriptionField().getValue());
      createEnvironmentRequest.setDescription("");
      createEnvironmentRequest.setEnvironmentName(environmentName);
      createEnvironmentRequest.setSolutionStackName(display.getSolutionStackField().getValue());

      AutoBean<EnvironmentInfo> autoBean = AWSExtension.AUTO_BEAN_FACTORY.environmentInfo();
      try
      {
         BeanstalkClientService.getInstance().createEnvironment(
            vfsInfo.getId(),
            project.getId(),
            createEnvironmentRequest,
            new AwsAsyncRequestCallback<EnvironmentInfo>(new AutoBeanUnmarshaller<EnvironmentInfo>(autoBean),
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
                  deployResultHandler.onDeployFinished(false);
                  environmentStatusHandler.requestError(project.getId(), exception);

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
                  deployResultHandler.onDeployFinished(true);
                  IDE.fireEvent(new OutputEvent(AWSExtension.LOCALIZATION_CONSTANT
                     .createEnvironmentLaunching(environmentName), Type.INFO));
                  writeEnvironmentId();
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
                     environmentStatusHandler.requestError(project.getId(), exception);
                  }
               });
         }
         catch (RequestException e)
         {
            IDE.fireEvent(new ExceptionThrownEvent(e));
         }

         ListEventsRequest listEventsRequest = AWSExtension.AUTO_BEAN_FACTORY.listEventsRequest().as();
         listEventsRequest.setApplicationName(applicationInfo.getName());
         listEventsRequest.setVersionLabel(INITIAL_VERSION_LABEL);
         listEventsRequest.setEnvironmentId(environment.getId());
         listEventsRequest.setStartTime(lastReceivedEventTime);
         AutoBean<EventsList> eventsListAutoBean = AWSExtension.AUTO_BEAN_FACTORY.eventList();
         AutoBeanUnmarshaller<EventsList> eventsListUnmarshaller =
            new AutoBeanUnmarshaller<EventsList>(eventsListAutoBean);
         try
         {
            BeanstalkClientService.getInstance().getApplicationEvents(vfsInfo.getId(), project.getId(),
               listEventsRequest, new AsyncRequestCallback<EventsList>(eventsListUnmarshaller)
               {
                  @Override
                  protected void onSuccess(EventsList result)
                  {
                     StringBuffer message = new StringBuffer();
                     // shows events in chronological order
                     List<Event> eventsList = result.getEvents();
                     if (eventsList.size() > 0)
                     {
                        for (int i = eventsList.size() - 1; i >= 0; i--)
                        {
                           Event event = eventsList.get(i);
                           message.append(event.getMessage()).append("</br>");
                        }
                        IDE.fireEvent(new OutputEvent(message.toString()));
                        lastReceivedEventTime = eventsList.get(0).getEventDate() + 1;
                     }
                  }

                  @Override
                  protected void onFailure(Throwable exception)
                  {
                     // nothing to do
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
      StringBuffer message = new StringBuffer();
      if (environment.getStatus() == EnvironmentStatus.Ready)
      {
         environmentStatusHandler.requestFinished(project.getId());

         message.append(AWSExtension.LOCALIZATION_CONSTANT.createApplicationStartedOnUrl(
            environment.getApplicationName(), getAppUrl(environment)));

         if (environment.getHealth() != EnvironmentHealth.Green)
         {
            message.append(", but health status of the application's environment is " + environment.getHealth().name());
         }
         IDE.fireEvent(new OutputEvent(message.toString(), Type.INFO));
      }
      else if (environment.getStatus() == EnvironmentStatus.Terminated)
      {
         environmentStatusHandler.requestError(project.getId(), null);

         message
            .append(AWSExtension.LOCALIZATION_CONSTANT.createApplicationTerminated());
         IDE.fireEvent(new OutputEvent(message.toString(), Type.ERROR));
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

   private void buildApplication()
   {
      IDE.addHandler(ProjectBuiltEvent.TYPE, this);
      IDE.fireEvent(new BuildProjectEvent(project));
   }

   /**
    * @see org.exoplatform.ide.client.framework.paas.HasPaaSActions#deploy(org.exoplatform.ide.vfs.client.model.ProjectModel, 
    *       org.exoplatform.ide.client.framework.paas.DeployResultHandler)
    */
   @Override
   public void deploy(ProjectModel project, DeployResultHandler deployResultHandler)
   {
      // TODO Auto-generated method stub

   }

   /**
    * Get the list of solution stack and put them to the appropriate field.
    */
   private void getSolutionStacks()
   {
      try
      {
         BeanstalkClientService.getInstance().getAvailableSolutionStacks(
            new AwsAsyncRequestCallback<List<SolutionStack>>(new SolutionStackListUnmarshaller(), new LoggedInHandler()
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

   /**
    * @see org.exoplatform.ide.client.framework.paas.HasPaaSActions#validate()
    */
   @Override
   public boolean validate()
   {
      return display.getNameField().getValue() != null && !display.getNameField().getValue().isEmpty()
         && display.getEnvNameField().getValue() != null && !display.getEnvNameField().getValue().isEmpty()
         && display.getSolutionStackField().getValue() != null && !display.getSolutionStackField().getValue().isEmpty();
   }

   /**
    * @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework.application.event.VfsChangedEvent)
    */
   @Override
   public void onVfsChanged(VfsChangedEvent event)
   {
      vfsInfo = event.getVfsInfo();
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

   /**
    * Writes application's AWS environment identifier to the project properties.
    */
   private void writeEnvironmentId()
   {
      project.getProperties().add(new StringProperty("awsEnvironmentId", environment.getId()));
      try
      {
         VirtualFileSystem.getInstance().updateItem(project, null, new AsyncRequestCallback<ItemWrapper>()
         {

            @Override
            protected void onSuccess(ItemWrapper result)
            {
               // nothing to do
            }

            @Override
            protected void onFailure(Throwable ignore)
            {
               // ignore this exception
            }
         });
      }
      catch (RequestException e)
      {
         e.printStackTrace();
      }
   }

}
