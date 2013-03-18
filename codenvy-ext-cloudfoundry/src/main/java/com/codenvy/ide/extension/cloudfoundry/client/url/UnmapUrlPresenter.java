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
package com.codenvy.ide.extension.cloudfoundry.client.url;

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.console.Console;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryAutoBeanFactory;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryClientService;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryLocalizationConstant;
import com.codenvy.ide.extension.cloudfoundry.client.login.LoggedInHandler;
import com.codenvy.ide.extension.cloudfoundry.client.login.LoginPresenter;
import com.codenvy.ide.extension.cloudfoundry.shared.CloudFoundryApplication;
import com.codenvy.ide.rest.AutoBeanUnmarshaller;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.event.shared.EventBus;

import java.util.List;

/**
 * 
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
public class UnmapUrlPresenter implements UnmapUrlView.ActionDelegate
{
   private UnmapUrlView view;

   private List<String> registeredUrls;

   private String unregisterUrl;

   private String urlToMap;

   private boolean isBindingChanged = false;

   private ResourceProvider resourceProvider;

   private EventBus eventBus;

   private Console console;

   private CloudFoundryLocalizationConstant constant;

   private CloudFoundryAutoBeanFactory autoBeanFactory;

   private AsyncCallback<String> unmapUrlCallback;

   private LoginPresenter loginPresenter;

   @Inject
   protected UnmapUrlPresenter(UnmapUrlView view, ResourceProvider resourceProvider, EventBus eventBus,
      Console console, CloudFoundryLocalizationConstant constant, CloudFoundryAutoBeanFactory autoBeanFactory,
      LoginPresenter loginPresenter)
   {
      this.view = view;
      this.view.setDelegate(this);
      this.resourceProvider = resourceProvider;
      this.eventBus = eventBus;
      this.console = console;
      this.constant = constant;
      this.autoBeanFactory = autoBeanFactory;
      this.loginPresenter = loginPresenter;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onCloseClicked()
   {
      if (isBindingChanged)
      {
         String projectId = resourceProvider.getActiveProject().getId();
         unmapUrlCallback.onSuccess(projectId);
      }

      view.close();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onMapUrlClicked()
   {
      urlToMap = view.getMapUrl();
      for (String url : registeredUrls)
      {
         if (url.equals(urlToMap) || ("http://" + url).equals(urlToMap))
         {
            Window.alert(constant.mapUrlAlredyRegistered());
            return;
         }
      }
      if (urlToMap.startsWith("http://"))
      {
         urlToMap = urlToMap.substring(7);
      }
      mapUrl(urlToMap);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onUnMapUrlClicked(String url)
   {
      askForUnmapUrl(url);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onMapUrlChanged()
   {
      view.setEnableMapUrlButton(view.getMapUrl() != null && !view.getMapUrl().isEmpty());
   }

   /**
    * Show dialog
    */
   public void showDialog(AsyncCallback<String> callback)
   {
      this.unmapUrlCallback = callback;
      getAppRegisteredUrls();
   }

   private void getAppRegisteredUrls()
   {
      String projectId = resourceProvider.getActiveProject().getId();

      try
      {
         AutoBean<CloudFoundryApplication> cloudFoundryApplication = autoBeanFactory.cloudFoundryApplication();
         AutoBeanUnmarshaller<CloudFoundryApplication> unmarshaller =
            new AutoBeanUnmarshaller<CloudFoundryApplication>(cloudFoundryApplication);

         CloudFoundryClientService.getInstance().getApplicationInfo(resourceProvider.getVfsId(), projectId, null, null,
            new CloudFoundryAsyncRequestCallback<CloudFoundryApplication>(unmarshaller, null, null, eventBus, console,
               constant, loginPresenter)
            {
               @Override
               protected void onSuccess(CloudFoundryApplication result)
               {
                  isBindingChanged = false;

                  registeredUrls = result.getUris();

                  view.setEnableMapUrlButton(false);
                  view.setRegisteredUrls(registeredUrls);
                  view.setMapUrl("");

                  view.showDialog();
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
    * If user is not logged in to CloudFoundry, this handler will be called, after user logged in.
    */
   private LoggedInHandler mapUrlLoggedInHandler = new LoggedInHandler()
   {
      @Override
      public void onLoggedIn()
      {
         mapUrl(urlToMap);
      }
   };

   private void mapUrl(final String url)
   {
      String projectId = resourceProvider.getActiveProject().getId();

      try
      {
         CloudFoundryClientService.getInstance().mapUrl(resourceProvider.getVfsId(), projectId, null, null, url,
               new CloudFoundryAsyncRequestCallback<String>(null, mapUrlLoggedInHandler, null, eventBus, console,
               constant, loginPresenter)
            {
               @Override
               protected void onSuccess(String result)
               {
                  isBindingChanged = true;
                  String registeredUrl = url;
                  if (!url.startsWith("http"))
                  {
                     registeredUrl = "http://" + url;
                  }
                  registeredUrl = "<a href=\"" + registeredUrl + "\" target=\"_blank\">" + registeredUrl + "</a>";
                     String msg = constant.mapUrlRegisteredSuccess(registeredUrl);
                  console.print(msg);
                  registeredUrls.add(url);
                  view.setRegisteredUrls(registeredUrls);
                  view.setMapUrl("");
               }
            });
      }
      catch (RequestException e)
      {
         eventBus.fireEvent(new ExceptionThrownEvent(e));
         console.print(e.getMessage());
      }
   }

   private void askForUnmapUrl(final String url)
   {
      if (Window.confirm(constant.unmapUrlConfirmationDialogMessage()))
      {
         unregisterUrl = url;
         unregisterUrl(unregisterUrl);
      }
   }

   LoggedInHandler unregisterUrlLoggedInHandler = new LoggedInHandler()
   {
      @Override
      public void onLoggedIn()
      {
         unregisterUrl(unregisterUrl);
      }
   };

   private void unregisterUrl(final String url)
   {
      String projectId = resourceProvider.getActiveProject().getId();
      try
      {
         CloudFoundryClientService.getInstance().unmapUrl(resourceProvider.getVfsId(), projectId, null, null, url,
            new CloudFoundryAsyncRequestCallback<Object>(null, unregisterUrlLoggedInHandler, null, eventBus, console,
               constant, loginPresenter)
            {
               @Override
               protected void onSuccess(Object result)
               {
                  isBindingChanged = true;
                  registeredUrls.remove(url);
                  view.setRegisteredUrls(registeredUrls);
                  String unmappedUrl = url;
                  if (!unmappedUrl.startsWith("http"))
                  {
                     unmappedUrl = "http://" + unmappedUrl;
                  }
                  String msg = constant.unmapUrlSuccess(unmappedUrl);
                  console.print(msg);
               }
            });
      }
      catch (RequestException e)
      {
         eventBus.fireEvent(new ExceptionThrownEvent(e));
         console.print(e.getMessage());
      }
   }
}