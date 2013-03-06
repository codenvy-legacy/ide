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
package com.codenvy.ide.extension.cloudfoundry.client.services;

import com.codenvy.ide.api.ui.console.Console;

import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.AutoBeanUnmarshaller;

import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryClientService;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryExtension;
import com.codenvy.ide.extension.cloudfoundry.shared.CloudFoundryServices;

import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.event.shared.EventBus;

import com.codenvy.ide.extension.cloudfoundry.client.login.LoggedInHandler;
import com.codenvy.ide.extension.cloudfoundry.client.services.CloudFoundryServicesUnmarshaller;
import com.codenvy.ide.extension.cloudfoundry.client.services.CreateServiceEvent;
import com.codenvy.ide.extension.cloudfoundry.client.services.CreateServiceHandler;
import com.codenvy.ide.extension.cloudfoundry.client.services.CreateServiceView;
import com.codenvy.ide.extension.cloudfoundry.client.services.ProvisionedServiceCreatedHandler;
import com.codenvy.ide.extension.cloudfoundry.shared.ProvisionedService;
import com.codenvy.ide.extension.cloudfoundry.shared.SystemService;

import java.util.LinkedHashMap;

/**
 *
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class CreateServicePresenter implements CreateServiceView.ActionDelegate, CreateServiceHandler
{
   private CreateServiceView view;

   private EventBus eventBus;

   private Console console;

   /**
    * Handler for successful service creation.
    */
   private ProvisionedServiceCreatedHandler serviceCreatedHandler;

   private LoggedInHandler createServiceLoggedInHandler = new LoggedInHandler()
   {

      @Override
      public void onLoggedIn()
      {
         doCreate();
      }
   };

   @Inject
   protected CreateServicePresenter(CreateServiceView view, EventBus eventBus, Console console)
   {
      this.view = view;
      this.view.setDelegate(this);
      this.eventBus = eventBus;
      this.console = console;

      this.eventBus.addHandler(CreateServiceEvent.TYPE, this);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onCreateClicked()
   {
      doCreate();
   }

   /**
    * Create new provisioned service.
    */
   private void doCreate()
   {
      String name = view.getName();
      String type = view.getSystemServices();
      try
      {
         AutoBean<ProvisionedService> provisionedService = CloudFoundryExtension.AUTO_BEAN_FACTORY.provisionedService();
         AutoBeanUnmarshaller<ProvisionedService> unmarshaller =
            new AutoBeanUnmarshaller<ProvisionedService>(provisionedService);

         CloudFoundryClientService.getInstance().createService(
            null,
            type,
            name,
            null,
            null,
            null,
            new CloudFoundryAsyncRequestCallback<ProvisionedService>(unmarshaller, createServiceLoggedInHandler, null,
               eventBus)
            {
               @Override
               protected void onSuccess(ProvisionedService result)
               {
                  serviceCreatedHandler.onProvisionedServiceCreated(result);
                  view.close();
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
   public void onCreateService(CreateServiceEvent event)
   {
      this.serviceCreatedHandler = event.getProvisionedServiceCreatedHandler();
      getServices();

      showDialog();
   }

   public void showDialog()
   {
      // TODO
      view.setName("");

      view.showDialog();
   }

   /**
    * Get the list of CloudFoundry services (provisioned and system).
    */
   private void getServices()
   {
      try
      {
         CloudFoundryClientService.getInstance().services(null,
            new AsyncRequestCallback<CloudFoundryServices>(new CloudFoundryServicesUnmarshaller())
            {
               @Override
               protected void onSuccess(CloudFoundryServices result)
               {
                  LinkedHashMap<String, String> values = new LinkedHashMap<String, String>();
                  for (SystemService service : result.getSystem())
                  {
                     values.put(service.getVendor(), service.getDescription());
                  }
                  view.setServices(values);
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  // TODO
                  //                  Dialogs.getInstance().showError(CloudFoundryExtension.LOCALIZATION_CONSTANT.retrieveServicesFailed());
                  Window.alert(CloudFoundryExtension.LOCALIZATION_CONSTANT.retrieveServicesFailed());
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
}