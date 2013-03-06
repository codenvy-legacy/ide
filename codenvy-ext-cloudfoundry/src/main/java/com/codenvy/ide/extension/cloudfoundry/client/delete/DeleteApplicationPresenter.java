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
package com.codenvy.ide.extension.cloudfoundry.client.delete;

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.console.Console;
import com.codenvy.ide.resources.model.Project;

import com.codenvy.ide.rest.AutoBeanUnmarshaller;

import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryClientService;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryExtension;

import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.event.shared.EventBus;

import com.codenvy.ide.extension.cloudfoundry.client.delete.ApplicationDeletedEvent;
import com.codenvy.ide.extension.cloudfoundry.client.delete.DeleteApplicationEvent;
import com.codenvy.ide.extension.cloudfoundry.client.delete.DeleteApplicationHandler;
import com.codenvy.ide.extension.cloudfoundry.client.delete.DeleteApplicationView;
import com.codenvy.ide.extension.cloudfoundry.client.login.LoggedInHandler;
import com.codenvy.ide.extension.cloudfoundry.shared.CloudFoundryApplication;

/**
 *
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class DeleteApplicationPresenter implements DeleteApplicationView.ActionDelegate, DeleteApplicationHandler
{
   private DeleteApplicationView view;

   /**
    * The name of application.
    */
   private String appName;

   /**
    * Name of the server.
    */
   private String serverName;

   private ResourceProvider resourceProvider;

   private EventBus eventBus;

   private Console console;

   @Inject
   protected DeleteApplicationPresenter(DeleteApplicationView view, ResourceProvider resourceProvider,
      EventBus eventBus, Console console)
   {
      this.view = view;
      this.resourceProvider = resourceProvider;
      this.eventBus = eventBus;
      this.console = console;

      this.eventBus.addHandler(DeleteApplicationEvent.TYPE, this);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onCancelClicked()
   {
      view.close();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onDeleteClicked()
   {
      deleteApplication();

      view.close();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onDeleteApplication(DeleteApplicationEvent event)
   {
      serverName = event.getServer();
      //      if (event.getApplicationName() == null && makeSelectionCheck())
      if (event.getApplicationName() == null)
      {
         getApplicationInfo();
      }
      else
      {
         appName = event.getApplicationName();
         showDialog(appName);
      }
   }

   private LoggedInHandler appInfoLoggedInHandler = new LoggedInHandler()
   {
      @Override
      public void onLoggedIn()
      {
         getApplicationInfo();
      }
   };

   private void getApplicationInfo()
   {
      //      String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
      String projectId = resourceProvider.getActiveProject().getId();

      try
      {
         AutoBean<CloudFoundryApplication> cloudFoundryApplication =
            CloudFoundryExtension.AUTO_BEAN_FACTORY.cloudFoundryApplication();

         AutoBeanUnmarshaller<CloudFoundryApplication> unmarshaller =
            new AutoBeanUnmarshaller<CloudFoundryApplication>(cloudFoundryApplication);

         CloudFoundryClientService.getInstance().getApplicationInfo(
            resourceProvider.getVfsId(),
            projectId,
            null,
            null,
            new CloudFoundryAsyncRequestCallback<CloudFoundryApplication>(unmarshaller, appInfoLoggedInHandler, null,
               eventBus)
            {
               @Override
               protected void onSuccess(CloudFoundryApplication result)
               {
                  appName = result.getName();
                  showDialog(appName);
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

   private LoggedInHandler deleteAppLoggedInHandler = new LoggedInHandler()
   {
      @Override
      public void onLoggedIn()
      {
         deleteApplication();
      }
   };

   private void deleteApplication()
   {
      boolean isDeleteServices = view.isDeleteServices();
      String projectId = null;

      // TODO
      //      if (selectedItems.size() > 0 && selectedItems.get(0) instanceof ItemContext)
      //      {
      //         ProjectModel project = ((ItemContext)selectedItems.get(0)).getProject();
      //         if (project != null && project.getPropertyValue("cloudfoundry-application") != null
      //            && appName.equals(project.getPropertyValue("cloudfoundry-application")))
      //         {
      //            projectId = project.getId();
      //         }
      //      }

      Project project = resourceProvider.getActiveProject();
      if (project != null && project.getPropertyValue("cloudfoundry-application") != null
         && appName.equals(project.getPropertyValue("cloudfoundry-application")))
      {
         projectId = project.getId();
      }

      try
      {
         CloudFoundryClientService.getInstance().deleteApplication(resourceProvider.getVfsId(), projectId, appName,
            serverName, isDeleteServices,
            new CloudFoundryAsyncRequestCallback<String>(null, deleteAppLoggedInHandler, null, eventBus)
            {
               @Override
               protected void onSuccess(String result)
               {
                  view.close();
                  // TODO
                  //                  IDE.fireEvent(new OutputEvent(CloudFoundryExtension.LOCALIZATION_CONSTANT
                  //                     .applicationDeletedMsg(appName), Type.INFO));
                  console.print(CloudFoundryExtension.LOCALIZATION_CONSTANT.applicationDeletedMsg(appName));
                  // TODO
                  //                  IDE.fireEvent(new ApplicationDeletedEvent(appName));
                  eventBus.fireEvent(new ApplicationDeletedEvent(appName));
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

   public void showDialog(String appName)
   {
      view.setAskMessage(CloudFoundryExtension.LOCALIZATION_CONSTANT.deleteApplicationQuestion(appName));
      view.setAskDeleteServices(CloudFoundryExtension.LOCALIZATION_CONSTANT.deleteApplicationAskDeleteServices());

      view.showDialog();
   }
}