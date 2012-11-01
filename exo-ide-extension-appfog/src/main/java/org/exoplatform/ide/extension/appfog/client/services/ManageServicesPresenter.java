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
package org.exoplatform.ide.extension.appfog.client.services;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.http.client.RequestException;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.appfog.client.AppfogAsyncRequestCallback;
import org.exoplatform.ide.extension.appfog.client.AppfogClientService;
import org.exoplatform.ide.extension.appfog.client.AppfogExtension;
import org.exoplatform.ide.extension.appfog.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.appfog.shared.AppfogApplication;
import org.exoplatform.ide.extension.appfog.shared.AppfogProvisionedService;
import org.exoplatform.ide.extension.appfog.shared.AppfogServices;

import java.util.ArrayList;
import java.util.List;

/**
 * Presenter for managing Appfog services.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class ManageServicesPresenter implements ManageServicesHandler, ViewClosedHandler,
   ProvisionedServiceCreatedHandler
{
   interface Display extends IsView
   {
      HasClickHandlers getAddButton();

      HasClickHandlers getDeleteButton();

      HasClickHandlers getCancelButton();

      HasUnbindServiceHandler getUnbindServiceHandler();

      HasBindServiceHandler getBindServiceHandler();

      ListGridItem<AppfogProvisionedService> getProvisionedServicesGrid();

      ListGridItem<String> getBoundedServicesGrid();

      void enableDeleteButton(boolean enabled);

   }

   private Display display;

   /**
    * Application, for which need to bind service.
    */
   private AppfogApplication application;

   /**
    * Selected provisioned service.
    */
   private AppfogProvisionedService selectedService;

   /**
    * Selected provisioned service.
    */
   private String selectedBoundedService;

   private LoggedInHandler deleteServiceLoggedInHandler = new LoggedInHandler()
   {

      @Override
      public void onLoggedIn()
      {
         deleteService(selectedService);
      }
   };

   private LoggedInHandler bindServiceLoggedInHandler = new LoggedInHandler()
   {

      @Override
      public void onLoggedIn()
      {
         bindService(selectedService);
      }
   };

   private LoggedInHandler unBindServiceLoggedInHandler = new LoggedInHandler()
   {

      @Override
      public void onLoggedIn()
      {
         unbindService(selectedBoundedService);
      }
   };

   private LoggedInHandler getApplicationInfoLoggedInHandler = new LoggedInHandler()
   {

      @Override
      public void onLoggedIn()
      {
         getApplicationInfo();
      }
   };

   public ManageServicesPresenter()
   {
      IDE.addHandler(ManageServicesEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
   }

   public void bindDisplay()
   {
      display.getProvisionedServicesGrid().addSelectionHandler(new SelectionHandler<AppfogProvisionedService>()
      {
         @Override
         public void onSelection(SelectionEvent<AppfogProvisionedService> event)
         {
            selectedService = event.getSelectedItem();
            boolean enable = (selectedService != null);
            display.enableDeleteButton(enable);
         }
      });

      display.getBoundedServicesGrid().addSelectionHandler(new SelectionHandler<String>()
      {

         @Override
         public void onSelection(SelectionEvent<String> event)
         {
            selectedBoundedService = event.getSelectedItem();
         }
      });

      display.getCancelButton().addClickHandler(new ClickHandler()
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
            askBeforeDelete(selectedService);
         }
      });

      display.getAddButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            doAdd();
         }
      });

      display.getBindServiceHandler().addBindServiceHandler(new SelectionHandler<AppfogProvisionedService>()
      {

         @Override
         public void onSelection(SelectionEvent<AppfogProvisionedService> event)
         {
            bindService(event.getSelectedItem());
         }
      });

      display.getUnbindServiceHandler().addUnbindServiceHandler(new SelectionHandler<String>()
      {
         public void onSelection(SelectionEvent<String> event)
         {
            unbindService(event.getSelectedItem());
         }
      });
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.services.ManageServicesHandler#onManageServices(org.exoplatform.ide.extension.cloudfoundry.client.services.ManageServicesEvent)
    */
   @Override
   public void onManageServices(ManageServicesEvent event)
   {
      this.application = event.getApplication();
      if (display == null)
      {
         display = GWT.create(Display.class);
         IDE.getInstance().openView(display.asView());
         bindDisplay();
      }
      display.enableDeleteButton(false);
      getApplicationInfo();
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

   /**
    * Get the list of Appfog services (system and provisioned).
    */
   private void getServices()
   {
      try
      {
         AppfogClientService.getInstance().services(null,
            new AsyncRequestCallback<AppfogServices>(new AppfogServicesUnmarshaller())
            {

               @Override
               protected void onSuccess(AppfogServices result)
               {
                  List<AppfogProvisionedService> filteredServices = new ArrayList<AppfogProvisionedService>();

                  for (AppfogProvisionedService service : result.getAppfogProvisionedService())
                  {
                     if (service.getInfra().getName().equals(application.getInfra().getName()))
                     {
                        filteredServices.add(service);
                     }
                  }

                  display.getProvisionedServicesGrid().setValue(filteredServices);
                  display.enableDeleteButton(false);
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  Dialogs.getInstance().showError(AppfogExtension.LOCALIZATION_CONSTANT.retrieveServicesFailed());
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   /**
    * Perform adding new provisioned service.
    */
   private void doAdd()
   {
      IDE.fireEvent(new CreateServiceEvent(this));
   }

   /**
    * Delete provisioned service.
    *
    * @param service service to delete
    */
   private void deleteService(final AppfogProvisionedService service)
   {
      try
      {
         AppfogClientService.getInstance().deleteService(null, service.getName(),
            new AppfogAsyncRequestCallback<Object>(null, deleteServiceLoggedInHandler, null)
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
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   /**
    * Bind service to application.
    *
    * @param service service to bind
    */
   private void bindService(final AppfogProvisionedService service)
   {
      try
      {
         AppfogClientService.getInstance().bindService(null, service.getName(), application.getName(), null,
            null, new AppfogAsyncRequestCallback<Object>(null, bindServiceLoggedInHandler, null)
         {

            @Override
            protected void onSuccess(Object result)
            {
               getApplicationInfo();
            }

            /**
             * @see org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback#onFailure(java.lang.Throwable)
             */
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
         IDE.fireEvent(new ExceptionThrownEvent(e));
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
         AppfogClientService.getInstance().unbindService(null, service, application.getName(), null, null,
            new AppfogAsyncRequestCallback<Object>(null, unBindServiceLoggedInHandler, null)
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
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   /**
    * Ask user before deleting service.
    *
    * @param service
    */
   private void askBeforeDelete(final AppfogProvisionedService service)
   {
      Dialogs.getInstance().ask(AppfogExtension.LOCALIZATION_CONSTANT.deleteServiceTitle(),
         AppfogExtension.LOCALIZATION_CONSTANT.deleteServiceQuestion(service.getName()),
         new BooleanValueReceivedHandler()
         {

            @Override
            public void booleanValueReceived(Boolean value)
            {
               if (value != null && value)
               {
                  deleteService(service);
               }
            }
         });
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.services.ProvisionedServiceCreatedHandler#onProvisionedServiceCreated(org.exoplatform.ide.extension.cloudfoundry.shared.ProvisionedService)
    */
   @Override
   public void onProvisionedServiceCreated(AppfogProvisionedService service)
   {
      getServices();
   }

   private void getApplicationInfo()
   {
      AutoBean<AppfogApplication> appfogApplication =
         AppfogExtension.AUTO_BEAN_FACTORY.appfogApplication();

      AutoBeanUnmarshaller<AppfogApplication> unmarshaller =
         new AutoBeanUnmarshaller<AppfogApplication>(appfogApplication);
      try
      {
         AppfogClientService.getInstance().getApplicationInfo(
            null,
            null,
            application.getName(),
            null,
            new AppfogAsyncRequestCallback<AppfogApplication>(unmarshaller,
               getApplicationInfoLoggedInHandler, null)
            {

               @Override
               protected void onSuccess(AppfogApplication result)
               {
                  application = result;
                  getServices();
                  display.getBoundedServicesGrid().setValue(result.getServices());
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }
}
