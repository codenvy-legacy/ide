/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package org.exoplatform.ide.extension.cloudfoundry.client.project;

import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.event.shared.EventBus;

import org.exoplatform.ide.api.resources.ResourceProvider;
import org.exoplatform.ide.api.ui.console.Console;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension;
import org.exoplatform.ide.extension.cloudfoundry.client.info.ApplicationInfoPresenter;
import org.exoplatform.ide.extension.cloudfoundry.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.cloudfoundry.client.marshaller.StringUnmarshaller;
import org.exoplatform.ide.extension.cloudfoundry.client.services.ManageServicesEvent;
import org.exoplatform.ide.extension.cloudfoundry.client.services.ManageServicesHandler;
import org.exoplatform.ide.extension.cloudfoundry.client.services.ManageServicesPresenter;
import org.exoplatform.ide.extension.cloudfoundry.client.start.RestartApplicationEvent;
import org.exoplatform.ide.extension.cloudfoundry.client.start.StartApplicationEvent;
import org.exoplatform.ide.extension.cloudfoundry.client.start.StopApplicationEvent;
import org.exoplatform.ide.extension.cloudfoundry.client.update.UpdateInstancesEvent;
import org.exoplatform.ide.extension.cloudfoundry.client.update.UpdateMemoryEvent;
import org.exoplatform.ide.extension.cloudfoundry.client.update.UpdatePropertiesPresenter;
import org.exoplatform.ide.extension.cloudfoundry.client.url.UnmapUrlPresenter;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudFoundryApplication;
import org.exoplatform.ide.resources.model.Project;
import org.exoplatform.ide.rest.AsyncRequestCallback;
import org.exoplatform.ide.rest.AutoBeanUnmarshaller;

/**
 * 
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
public class CloudFoundryProjectPresenter implements CloudFoundryProjectView.ActionDelegate,
   ApplicationInfoChangedHandler, ManageServicesHandler
// ProjectOpenedHandler, ProjectClosedHandler,
//   ManageCloudFoundryProjectHandler, ViewClosedHandler, ApplicationDeletedHandler, ApplicationInfoChangedHandler, ActiveProjectChangedHandler
{
   private CloudFoundryProjectView view;

   private ApplicationInfoPresenter applicationInfoPresenter;

   private UnmapUrlPresenter unmapUrlPresenter;

   private UpdatePropertiesPresenter updateProperyPresenter;

   private ManageServicesPresenter manageServicesPresenter;

   private EventBus eventBus;

   private ResourceProvider resourceProvider;

   private Console console;

   private CloudFoundryApplication application;

   @Inject
   protected CloudFoundryProjectPresenter(CloudFoundryProjectView view,
      ApplicationInfoPresenter applicationInfoPresenter, UnmapUrlPresenter unmapUrlPresenter,
      UpdatePropertiesPresenter updateProperyPresenter, ManageServicesPresenter manageServicesPresenter,
      EventBus eventBus, ResourceProvider resourceProvider, Console console)
   {
      this.view = view;
      this.view.setDelegate(this);
      this.applicationInfoPresenter = applicationInfoPresenter;
      this.unmapUrlPresenter = unmapUrlPresenter;
      this.updateProperyPresenter = updateProperyPresenter;
      this.eventBus = eventBus;
      this.resourceProvider = resourceProvider;
      this.console = console;
      this.manageServicesPresenter = manageServicesPresenter;

      this.eventBus.addHandler(ApplicationInfoChangedEvent.TYPE, this);
      this.eventBus.addHandler(ManageServicesEvent.TYPE, this);
   }

   /**
    * Show dialog.
    */
   public void showDialog()
   {
      getApplicationInfo(resourceProvider.getActiveProject());

      view.showDialog();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onCloseClicked()
   {
      view.close();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onUpdateClicked()
   {
      // TODO
      //      IDE.eventBus().fireEvent(new UpdateApplicationEvent());
      //      eventBus.fireEvent(new UpdateApplicationEvent());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onLogsClicked()
   {
      getLogs();
   }

   protected void getLogs()
   {
      try
      {
         CloudFoundryClientService.getInstance().getLogs(resourceProvider.getVfsId(),
            resourceProvider.getActiveProject().getId(),
            new AsyncRequestCallback<StringBuilder>(new StringUnmarshaller(new StringBuilder()))
            {
               @Override
               protected void onSuccess(StringBuilder result)
               {
                  // TODO
                  //                  IDE.fireEvent(new OutputEvent("<pre>" + result.toString() + "</pre>", Type.OUTPUT));
                  console.print("<pre>" + result.toString() + "</pre>");
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  // TODO
                  //                  IDE.fireEvent(new ExceptionThrownEvent(exception.getMessage()));
                  console.print(exception.getMessage());
               }
            });
      }
      catch (RequestException e)
      {
         // TODO
         //         IDE.fireEvent(new ExceptionThrownEvent(e.getMessage()));
         console.print(e.getMessage());

         e.printStackTrace();
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onServicesClicked()
   {
      // TODO
      //      IDE.fireEvent(new ManageServicesEvent(application));
      eventBus.fireEvent(new ManageServicesEvent(application));
   }

   /**
    * Get application properties.
    * 
    * @param project
    */
   protected void getApplicationInfo(final Project project)
   {
      try
      {
         AutoBean<CloudFoundryApplication> cloudFoundryApplication =
            CloudFoundryExtension.AUTO_BEAN_FACTORY.cloudFoundryApplication();

         AutoBeanUnmarshaller<CloudFoundryApplication> unmarshaller =
            new AutoBeanUnmarshaller<CloudFoundryApplication>(cloudFoundryApplication);

         CloudFoundryClientService.getInstance().getApplicationInfo(resourceProvider.getVfsId(), project.getId(), null,
            null, new CloudFoundryAsyncRequestCallback<CloudFoundryApplication>(unmarshaller, new LoggedInHandler()
            {
               @Override
               public void onLoggedIn()
               {
                  // TODO
                  getApplicationInfo(project);
               }
            }, null, eventBus)
            {
               @Override
               protected void onSuccess(CloudFoundryApplication result)
               {
                  // TODO
                  //                  if (display == null)
                  //                  {
                  //                     display = GWT.create(Display.class);
                  //                     bindDisplay();
                  //                     IDE.getInstance().openView(display.asView());
                  //                  }
                  application = result;
                  displayApplicationProperties(result);
               }
            });
      }
      catch (RequestException e)
      {
         // TODO
         //         IDE.fireEvent(new ExceptionThrownEvent(e));
         console.print(e.getMessage());
      }
   }

   protected void displayApplicationProperties(CloudFoundryApplication application)
   {
      view.setApplicationName(application.getName());
      view.setApplicationInstances(String.valueOf(application.getInstances()));
      view.setApplicationMemory(String.valueOf(application.getResources().getMemory()) + "MB");
      view.setApplicationModel(String.valueOf(application.getStaging().getModel()));
      view.setApplicationStack(String.valueOf(application.getStaging().getStack()));
      view.setApplicationStatus(String.valueOf(application.getState()));

      if (application.getUris() != null && application.getUris().size() > 0)
      {
         view.setApplicationUrl(application.getUris().get(0));
      }
      else
      {
         //Set empty field if we specialy unmap all urls and closed url controller window, if whe don't do this, in
         //info window will be appear old url, that is not good
         view.setApplicationUrl(null);
      }
      boolean isStarted = ("STARTED".equals(application.getState()));
      view.setStartButtonEnabled(!isStarted);
      view.setStopButtonEnabled(isStarted);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onDeleteClicked()
   {
      // TODO
      //      IDE.eventBus().fireEvent(new DeleteApplicationEvent());
      //      eventBus.fireEvent(new DeleteApplicationEvent());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onInfoClicked()
   {
      // TODO
      //      IDE.eventBus().fireEvent(new ApplicationInfoEvent());
      applicationInfoPresenter.showDialog();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onStartClicked()
   {
      // TODO
      //      IDE.eventBus().fireEvent(new StartApplicationEvent());
      eventBus.fireEvent(new StartApplicationEvent());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onStopClicked()
   {
      // TODO Auto-generated method stub
      //      IDE.eventBus().fireEvent(new StopApplicationEvent());
      eventBus.fireEvent(new StopApplicationEvent());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onRestartClicked()
   {
      // TODO Auto-generated method stub
      //      IDE.eventBus().fireEvent(new RestartApplicationEvent());
      eventBus.fireEvent(new RestartApplicationEvent());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onEditMemoryClicked()
   {
      // TODO Auto-generated method stub
      //      IDE.eventBus().fireEvent(new UpdateMemoryEvent());
      eventBus.fireEvent(new UpdateMemoryEvent());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onEditUrlClicked()
   {
      // TODO
      //      IDE.eventBus().fireEvent(new UnmapUrlEvent());
      unmapUrlPresenter.showDialog();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onEditInstancesClicked()
   {
      // TODO Auto-generated method stub
      //      IDE.eventBus().fireEvent(new UpdateInstancesEvent());
      eventBus.fireEvent(new UpdateInstancesEvent());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onApplicationInfoChanged(ApplicationInfoChangedEvent event)
   {
      Project openedProject = resourceProvider.getActiveProject();
      if (event.getProjectId() != null && resourceProvider.getVfsId().equals(event.getVfsId()) && openedProject != null
         && openedProject.getId().equals(event.getProjectId()))
      {
         getApplicationInfo(openedProject);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onManageServices(ManageServicesEvent event)
   {
      getApplicationInfo(resourceProvider.getActiveProject());
   }
}