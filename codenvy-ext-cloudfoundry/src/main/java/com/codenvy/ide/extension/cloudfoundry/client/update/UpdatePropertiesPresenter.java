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
package com.codenvy.ide.extension.cloudfoundry.client.update;

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.console.Console;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryAutoBeanFactory;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryClientService;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryLocalizationConstant;
import com.codenvy.ide.extension.cloudfoundry.client.login.LoggedInHandler;
import com.codenvy.ide.extension.cloudfoundry.shared.CloudFoundryApplication;
import com.codenvy.ide.rest.AutoBeanUnmarshaller;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.event.shared.EventBus;

/**
 * 
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
public class UpdatePropertiesPresenter
{
   private int memory;

   private String instances;

   private EventBus eventBus;

   private ResourceProvider resourceProvider;

   private Console console;

   private CloudFoundryLocalizationConstant constant;

   private CloudFoundryAutoBeanFactory autoBeanFactory;

   private AsyncCallback<String> updatePropertiesCallback;

   @Inject
   protected UpdatePropertiesPresenter(EventBus eventBus, ResourceProvider resourceProvider, Console console,
      CloudFoundryLocalizationConstant constant, CloudFoundryAutoBeanFactory autoBeanFactory)
   {
      this.eventBus = eventBus;
      this.resourceProvider = resourceProvider;
      this.console = console;
      this.constant = constant;
      this.autoBeanFactory = autoBeanFactory;
   }

   public void showUpdateMemoryDialog(AsyncCallback<String> callback)
   {
      this.updatePropertiesCallback = callback;
      getOldMemoryValue();
   }

   /**
    * If user is not logged in to CloudFoundry, this handler will be called, after user logged in.
    */
   private LoggedInHandler getOldMemoryValueLoggedInHandler = new LoggedInHandler()
   {
      @Override
      public void onLoggedIn()
      {
         getOldMemoryValue();
      }
   };

   private void getOldMemoryValue()
   {
      String projectId = resourceProvider.getActiveProject().getId();
      try
      {
         AutoBean<CloudFoundryApplication> cloudFoundryApplication = autoBeanFactory.cloudFoundryApplication();
         AutoBeanUnmarshaller<CloudFoundryApplication> unmarshaller =
            new AutoBeanUnmarshaller<CloudFoundryApplication>(cloudFoundryApplication);

         CloudFoundryClientService.getInstance().getApplicationInfo(
            resourceProvider.getVfsId(),
            projectId,
            null,
            null,
            new CloudFoundryAsyncRequestCallback<CloudFoundryApplication>(unmarshaller,
               getOldMemoryValueLoggedInHandler, null, eventBus, console, constant)
            {
               @Override
               protected void onSuccess(CloudFoundryApplication result)
               {
                  askForNewMemoryValue(result.getResources().getMemory());
               }
            });
      }
      catch (RequestException e)
      {
         eventBus.fireEvent(new ExceptionThrownEvent(e));
         console.print(e.getMessage());
      }
   }

   private void askForNewMemoryValue(int oldMemoryValue)
   {
      String value = Window.prompt(constant.updateMemoryInvalidNumberMessage(), String.valueOf(oldMemoryValue));
      if (value != null)
      {
         try
         {
            // check, is instances contains only numbers
            memory = Integer.parseInt(value);
            updateMemory(memory);
         }
         catch (NumberFormatException e)
         {
            String msg = constant.updateMemoryInvalidNumberMessage();
            eventBus.fireEvent(new ExceptionThrownEvent(msg));
            Window.alert(msg);
         }
      }
   }

   /**
    * If user is not logged in to CloudFoundry, this handler will be called, after user logged in.
    */
   private LoggedInHandler updateMemoryLoggedInHandler = new LoggedInHandler()
   {
      @Override
      public void onLoggedIn()
      {
         updateMemory(memory);
      }
   };

   private void updateMemory(final int memory)
   {
      final String projectId = resourceProvider.getActiveProject().getId();

      try
      {
         CloudFoundryClientService.getInstance().updateMemory(resourceProvider.getVfsId(), projectId, null, null,
            memory,
            new CloudFoundryAsyncRequestCallback<String>(null, updateMemoryLoggedInHandler, null, eventBus, console,
               constant)
            {
               @Override
               protected void onSuccess(String result)
               {
                  String msg = constant.updateMemorySuccess(String.valueOf(memory));
                  console.print(msg);
                  updatePropertiesCallback.onSuccess(projectId);
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
    *
    */
   public void showUpdateInstancesDialog(AsyncCallback<String> callback)
   {
      this.updatePropertiesCallback = callback;
      getOldInstancesValue();
   }

   /**
    * If user is not logged in to CloudFoundry, this handler will be called, after user logged in.
    */
   private LoggedInHandler getOldInstancesValueLoggedInHandler = new LoggedInHandler()
   {
      @Override
      public void onLoggedIn()
      {
         getOldInstancesValue();
      }
   };

   private void getOldInstancesValue()
   {
      String projectId = resourceProvider.getActiveProject().getId();

      try
      {
         AutoBean<CloudFoundryApplication> cloudFoundryApplication = autoBeanFactory.cloudFoundryApplication();
         AutoBeanUnmarshaller<CloudFoundryApplication> unmarshaller =
            new AutoBeanUnmarshaller<CloudFoundryApplication>(cloudFoundryApplication);

         CloudFoundryClientService.getInstance().getApplicationInfo(
            resourceProvider.getVfsId(),
            projectId,
            null,
            null,
            new CloudFoundryAsyncRequestCallback<CloudFoundryApplication>(unmarshaller,
               getOldInstancesValueLoggedInHandler, null, eventBus, console, constant)
            {
               @Override
               protected void onSuccess(CloudFoundryApplication result)
               {
                  askForInstancesNumber(result.getInstances());
               }
            });
      }
      catch (RequestException e)
      {
         eventBus.fireEvent(new ExceptionThrownEvent(e));
         console.print(e.getMessage());
      }
   }

   private void askForInstancesNumber(int oldInstancesValue)
   {
      String value = Window.prompt(constant.updateInstancesDialogMessage(), String.valueOf(oldInstancesValue));
      if (value != null)
      {
         instances = value;
         try
         {
            // check, is instances contains only numbers
            Integer.parseInt(instances);
            updateInstances(instances);
         }
         catch (NumberFormatException e)
         {
            String msg = constant.updateInstancesInvalidValueMessage();
            eventBus.fireEvent(new ExceptionThrownEvent(msg));
            Window.alert(msg);
         }
      }
   }

   private LoggedInHandler updateInstancesLoggedInHandler = new LoggedInHandler()
   {
      @Override
      public void onLoggedIn()
      {
         updateInstances(instances);
      }
   };

   /**
    * @param instancesExpression how should we change number of instances. Expected are:
    *           <ul>
    *           <li>&lt;num&gt; - set number of instances to &lt;num&gt;</li>
    *           <li>&lt;+num&gt; - increase by &lt;num&gt; of instances</li>
    *           <li>&lt;-num&gt; - decrease by &lt;num&gt; of instances</li>
    *           </ul>
    */
   private void updateInstances(final String instancesExpression)
   {
      final String projectId = resourceProvider.getActiveProject().getId();

      String encodedExp = URL.encodePathSegment(instancesExpression);

      try
      {
         CloudFoundryClientService.getInstance().updateInstances(resourceProvider.getVfsId(), projectId, null, null,
            encodedExp,
            new CloudFoundryAsyncRequestCallback<String>(null, updateInstancesLoggedInHandler, null, eventBus, console,
               constant)
            {
               @Override
               protected void onSuccess(String result)
               {
                  try
                  {
                     AutoBean<CloudFoundryApplication> cloudFoundryApplication =
                        autoBeanFactory.cloudFoundryApplication();
                     AutoBeanUnmarshaller<CloudFoundryApplication> unmarshaller =
                        new AutoBeanUnmarshaller<CloudFoundryApplication>(cloudFoundryApplication);

                     CloudFoundryClientService.getInstance().getApplicationInfo(
                        resourceProvider.getVfsId(),
                        projectId,
                        null,
                        null,
                        new CloudFoundryAsyncRequestCallback<CloudFoundryApplication>(unmarshaller, null, null,
                           eventBus, console, constant)
                        {
                           @Override
                           protected void onSuccess(CloudFoundryApplication result)
                           {
                              String msg = constant.updateInstancesSuccess(String.valueOf(result.getInstances()));
                              console.print(msg);
                              updatePropertiesCallback.onSuccess(projectId);
                           }
                        });
                  }
                  catch (RequestException e)
                  {
                     eventBus.fireEvent(new ExceptionThrownEvent(e));
                     console.print(e.getMessage());
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
}