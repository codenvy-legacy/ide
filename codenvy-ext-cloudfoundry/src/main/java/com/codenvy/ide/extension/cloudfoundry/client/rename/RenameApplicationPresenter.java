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
package com.codenvy.ide.extension.cloudfoundry.client.rename;

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.console.Console;

import com.codenvy.ide.rest.AutoBeanUnmarshaller;

import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryClientService;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryExtension;

import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.event.shared.EventBus;

import com.codenvy.ide.extension.cloudfoundry.client.login.LoggedInHandler;
import com.codenvy.ide.extension.cloudfoundry.client.rename.RenameApplicationEvent;
import com.codenvy.ide.extension.cloudfoundry.client.rename.RenameApplicationHandler;
import com.codenvy.ide.extension.cloudfoundry.client.rename.RenameApplicationView;
import com.codenvy.ide.extension.cloudfoundry.shared.CloudFoundryApplication;

/**
 *
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class RenameApplicationPresenter implements RenameApplicationView.ActionDelegate, RenameApplicationHandler
{
   private RenameApplicationView view;

   private EventBus eventBus;

   private ResourceProvider resourceProvider;

   private Console console;

   /**
    * The name of application.
    */
   private String applicationName;

   @Inject
   protected RenameApplicationPresenter(RenameApplicationView view, EventBus eventBus,
      ResourceProvider resourceProvider, Console console)
   {
      this.view = view;
      this.view.setDelegate(this);
      this.resourceProvider = resourceProvider;
      this.console = console;
      this.eventBus = eventBus;

      this.eventBus.addHandler(RenameApplicationEvent.TYPE, this);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onNameChanged()
   {
      String newName = view.getName();
      boolean enable = !applicationName.equals(newName) && newName != null && !newName.isEmpty();
      view.enableRenameButton(enable);

      // TODO press Enter
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onRenameClicked()
   {
      renameApplication();
   }

   private LoggedInHandler renameAppLoggedInHandler = new LoggedInHandler()
   {
      @Override
      public void onLoggedIn()
      {
         renameApplication();
      }
   };

   private void renameApplication()
   {
      final String newName = view.getName();
      // TODO
      //      String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
      String projectId = resourceProvider.getActiveProject().getId();

      try
      {
         CloudFoundryClientService.getInstance().renameApplication(resourceProvider.getVfsId(), projectId,
            applicationName, null, newName,
            new CloudFoundryAsyncRequestCallback<String>(null, renameAppLoggedInHandler, null, eventBus)
            {
               @Override
               protected void onSuccess(String result)
               {
                  view.close();

                  // TODO
                  //                  IDE.fireEvent(new OutputEvent(CloudFoundryExtension.LOCALIZATION_CONSTANT.renameApplicationSuccess(
                  //                     applicationName, newName)));
                  console.print(CloudFoundryExtension.LOCALIZATION_CONSTANT.renameApplicationSuccess(applicationName,
                     newName));
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
   public void onRenameApplication(RenameApplicationEvent event)
   {
      // TODO GIT
      //      if (makeSelectionCheck())
      //      {
      //         getApplicationInfo();
      //      }
      getApplicationInfo();
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
      // TODO
      //      String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
      String projectId = resourceProvider.getActiveProject().getId();

      try
      {
         AutoBean<CloudFoundryApplication> cloudFoundryApplication =
            CloudFoundryExtension.AUTO_BEAN_FACTORY.cloudFoundryApplication();

         AutoBeanUnmarshaller<CloudFoundryApplication> unmarshaller =
            new AutoBeanUnmarshaller<CloudFoundryApplication>(cloudFoundryApplication);

         CloudFoundryClientService.getInstance().getApplicationInfo(resourceProvider.getVfsId(), projectId, null, null,
            new CloudFoundryAsyncRequestCallback<CloudFoundryApplication>(unmarshaller, appInfoLoggedInHandler, null,
               eventBus)
            {
               @Override
               protected void onSuccess(CloudFoundryApplication result)
               {
                  applicationName = result.getName();
                  showDialog();
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

   public void showDialog()
   {
      view.setName(applicationName);
      view.selectValueInRenameField();
      view.enableRenameButton(false);

      view.showDialog();
   }
}