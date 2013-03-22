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

import com.codenvy.ide.api.ui.console.ConsolePart;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryAutoBeanFactory;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryClientService;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryLocalizationConstant;
import com.codenvy.ide.extension.cloudfoundry.client.login.LoggedInHandler;
import com.codenvy.ide.extension.cloudfoundry.client.login.LoginPresenter;
import com.codenvy.ide.extension.cloudfoundry.client.marshaller.CloudFoundryServicesUnmarshaller;
import com.codenvy.ide.extension.cloudfoundry.shared.CloudFoundryApplication;
import com.codenvy.ide.extension.cloudfoundry.shared.CloudFoundryServices;
import com.codenvy.ide.extension.cloudfoundry.shared.ProvisionedService;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.AutoBeanUnmarshaller;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.event.shared.EventBus;

import java.util.Arrays;

/**
 * Presenter for managing CloudFondry services.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jul 13, 2012 10:53:33 AM anya $
 */
@Singleton
public class ManageServicesPresenter implements ManageServicesView.ActionDelegate
{
   private ManageServicesView view;

   /**
    * Application, for which need to bind service.
    */
   private CloudFoundryApplication application;

   /**
    * Selected provisioned service.
    */
   private ProvisionedService selectedService;

   /**
    * Selected provisioned service.
    */
   private String selectedBoundedService;

   private CreateServicePresenter createServicePresenter;

   /**
    * If user is not logged in to CloudFoundry, this handler will be called, after user logged in.
    */
   private LoggedInHandler deleteServiceLoggedInHandler = new LoggedInHandler()
   {
      @Override
      public void onLoggedIn()
      {
         deleteService(selectedService);
      }
   };

   /**
    * If user is not logged in to CloudFoundry, this handler will be called, after user logged in.
    */
   private LoggedInHandler bindServiceLoggedInHandler = new LoggedInHandler()
   {
      @Override
      public void onLoggedIn()
      {
         bindService(selectedService);
      }
   };

   /**
    * If user is not logged in to CloudFoundry, this handler will be called, after user logged in.
    */
   private LoggedInHandler unBindServiceLoggedInHandler = new LoggedInHandler()
   {
      @Override
      public void onLoggedIn()
      {
         unbindService(selectedBoundedService);
      }
   };

   /**
    * If user is not logged in to CloudFoundry, this handler will be called, after user logged in.
    */
   private LoggedInHandler getApplicationInfoLoggedInHandler = new LoggedInHandler()
   {
      @Override
      public void onLoggedIn()
      {
         getApplicationInfo();
      }
   };

   private EventBus eventBus;

   private ConsolePart console;

   private CloudFoundryLocalizationConstant constant;

   private CloudFoundryAutoBeanFactory autoBeanFactory;

   private LoginPresenter loginPresenter;

   /**
    * Create presenter.
    * 
    * @param view
    * @param eventBus
    * @param console
    * @param createServicePresenter
    * @param constant
    * @param autoBeanFactory
    * @param loginPresenter
    */
   @Inject
   protected ManageServicesPresenter(ManageServicesView view, EventBus eventBus, ConsolePart console,
      CreateServicePresenter createServicePresenter, CloudFoundryLocalizationConstant constant,
      CloudFoundryAutoBeanFactory autoBeanFactory, LoginPresenter loginPresenter)
   {
      this.view = view;
      this.view.setDelegate(this);
      this.eventBus = eventBus;
      this.console = console;
      this.createServicePresenter = createServicePresenter;
      this.constant = constant;
      this.autoBeanFactory = autoBeanFactory;
      this.loginPresenter = loginPresenter;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onAddClicked()
   {
      createServicePresenter.showDialog(new AsyncCallback<ProvisionedService>()
      {
         @Override
         public void onSuccess(ProvisionedService result)
         {
            getServices();
         }
         
         @Override
         public void onFailure(Throwable caught)
         {
            // do nothing   
         }
      });
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onDeleteClicked()
   {
      askBeforeDelete(selectedService);
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
   public void onUnbindServiceClicked(String service)
   {
      unbindService(service);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onBindServiceClicked(ProvisionedService service)
   {
      bindService(service);
   }

   /**
    * Delete provisioned service.
    * 
    * @param service service to delete
    */
   private void deleteService(final ProvisionedService service)
   {
      try
      {
         CloudFoundryClientService.getInstance().deleteService(null, service.getName(),
            new CloudFoundryAsyncRequestCallback<Object>(null, deleteServiceLoggedInHandler, null, eventBus, console,
               constant, loginPresenter)
            {
               @Override
               protected void onSuccess(Object result)
               {
                  getServices();
                  if (application.getServices().contains(service.getName()))
                  {
                     getApplicationInfo();
                  }
               }
            });
      }
      catch (RequestException e)
      {
         eventBus.fireEvent(new ExceptionThrownEvent(e));
         console.print(e.getMessage());
      }
   }

   /**
    * Bind service to application.
    * 
    * @param service service to bind
    */
   private void bindService(final ProvisionedService service)
   {
      try
      {
         CloudFoundryClientService.getInstance().bindService(null, service.getName(), application.getName(), null,
            null,
            new CloudFoundryAsyncRequestCallback<Object>(null, bindServiceLoggedInHandler, null, eventBus, console,
               constant, loginPresenter)
            {
               @Override
               protected void onSuccess(Object result)
               {
                  getApplicationInfo();
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  super.onFailure(exception);
                  getApplicationInfo();
               }
            });
      }
      catch (RequestException e)
      {
         eventBus.fireEvent(new ExceptionThrownEvent(e));
         console.print(e.getMessage());
      }
   }

   /**
    * Unbind service from application.
    * 
    * @param service
    */
   private void unbindService(String service)
   {
      try
      {
         CloudFoundryClientService.getInstance().unbindService(null, service, application.getName(), null, null,
            new CloudFoundryAsyncRequestCallback<Object>(null, unBindServiceLoggedInHandler, null, eventBus, console,
               constant, loginPresenter)
            {
               @Override
               protected void onSuccess(Object result)
               {
                  getApplicationInfo();
               }
            });
      }
      catch (RequestException e)
      {
         eventBus.fireEvent(new ExceptionThrownEvent(e));
         console.print(e.getMessage());
      }
   }

   /**
    * Ask user before deleting service.
    * 
    * @param service
    */
   private void askBeforeDelete(final ProvisionedService service)
   {
      if (Window.confirm(constant.deleteServiceQuestion(service.getName())))
      {
         deleteService(service);
      }
   }

   /**
    * Gets the list of services and put them to field.
    */
   private void getApplicationInfo()
   {
      AutoBean<CloudFoundryApplication> cloudFoundryApplication = autoBeanFactory.cloudFoundryApplication();
      AutoBeanUnmarshaller<CloudFoundryApplication> unmarshaller =
         new AutoBeanUnmarshaller<CloudFoundryApplication>(cloudFoundryApplication);
      try
      {
         CloudFoundryClientService.getInstance().getApplicationInfo(
            null,
            null,
            application.getName(),
            null,
            new CloudFoundryAsyncRequestCallback<CloudFoundryApplication>(unmarshaller,
               getApplicationInfoLoggedInHandler, null, eventBus, console, constant, loginPresenter)
            {
               @Override
               protected void onSuccess(CloudFoundryApplication result)
               {
                  application = result;
                  getServices();
                  view.setBoundedServices(result.getServices());
               }
            });
      }
      catch (RequestException e)
      {
         eventBus.fireEvent(new ExceptionThrownEvent(e));
         console.print(e.getMessage());
      }
   }

   /**
    * Get the list of CloudFoundry services (system and provisioned).
    */
   private void getServices()
   {
      try
      {
         CloudFoundryClientService.getInstance().services(null,
            new AsyncRequestCallback<CloudFoundryServices>(new CloudFoundryServicesUnmarshaller(autoBeanFactory))
            {
               @Override
               protected void onSuccess(CloudFoundryServices result)
               {
                  view.setProvisionedServices(Arrays.asList(result.getProvisioned()));
                  view.setEnableDeleteButton(false);
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  Window.alert(constant.retrieveServicesFailed());
               }
            });
      }
      catch (RequestException e)
      {
         eventBus.fireEvent(new ExceptionThrownEvent(e));
         console.print(e.getMessage());
      }
   }

   /**
    * Shows dialog.
    * 
    * @param application application where will manage services
    */
   public void showDialog(CloudFoundryApplication application)
   {
      this.application = application;

      view.setEnableDeleteButton(false);
      getApplicationInfo();

      view.showDialog();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onSelectedService(ProvisionedService service)
   {
      selectedService = service;

      updateControls();
   }

   /**
    * Updates graphic components on the view.
    */
   private void updateControls()
   {
      view.setEnableDeleteButton(selectedService != null);
   }
}