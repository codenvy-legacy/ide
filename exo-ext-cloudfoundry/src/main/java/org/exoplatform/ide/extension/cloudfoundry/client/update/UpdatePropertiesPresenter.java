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
package org.exoplatform.ide.extension.cloudfoundry.client.update;

import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.URL;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.event.shared.EventBus;

import org.exoplatform.ide.api.resources.ResourceProvider;
import org.exoplatform.ide.api.ui.console.Console;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension;
import org.exoplatform.ide.extension.cloudfoundry.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.cloudfoundry.client.project.ApplicationInfoChangedEvent;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudFoundryApplication;
import org.exoplatform.ide.rest.AutoBeanUnmarshaller;

/**
 * 
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
public class UpdatePropertiesPresenter implements UpdatePropertiesView.ActionDelegate, UpdateMemoryHandler,
   UpdateInstancesHandler
{
   private UpdatePropertiesView view;

   private int memory;

   private String instances;

   private EventBus eventBus;

   private ResourceProvider resourceProvider;

   private Console console;

   private boolean isMemoryEditing;

   @Inject
   protected UpdatePropertiesPresenter(UpdatePropertiesView view, EventBus eventBus, ResourceProvider resourceProvider,
      Console console)
   {
      this.view = view;
      this.view.setDelegate(this);
      this.eventBus = eventBus;
      this.resourceProvider = resourceProvider;
      this.console = console;

      this.eventBus.addHandler(UpdateMemoryEvent.TYPE, this);
      this.eventBus.addHandler(UpdateInstancesEvent.TYPE, this);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onOkClicked()
   {
      // TODO
      try
      {
         if (isMemoryEditing)
         {
            memory = Integer.parseInt(view.getProperty());
            updateMemory(memory);
         }
         else
         {
            instances = view.getProperty();
            Integer.parseInt(instances);
            updateInstances(instances);
         }
      }
      catch (NumberFormatException e)
      {
         String msg = CloudFoundryExtension.LOCALIZATION_CONSTANT.updateInstancesInvalidValueMessage();
         // TODO
         //                     IDE.fireEvent(new ExceptionThrownEvent(msg));
         console.print(msg);
      }

      view.close();
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
   public void onPropertyChanged()
   {
      view.setEnableOkButton(!view.getProperty().isEmpty());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onUpdateMemory(UpdateMemoryEvent event)
   {
      // TODO
      //      if (makeSelectionCheck())
      //      {
      //         getOldMemoryValue();
      //      }
      isMemoryEditing = true;
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
      // TODO
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
            new CloudFoundryAsyncRequestCallback<CloudFoundryApplication>(unmarshaller,
               getOldMemoryValueLoggedInHandler, null, eventBus)
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
         // TODO
         //         IDE.fireEvent(new ExceptionThrownEvent(e));
         console.print(e.getMessage());
      }
   }

   private void askForNewMemoryValue(int oldMemoryValue)
   {
      // TODO
      //      Dialogs.getInstance().askForValue(CloudFoundryExtension.LOCALIZATION_CONSTANT.updateMemoryDialogTitle(),
      //         CloudFoundryExtension.LOCALIZATION_CONSTANT.updateMemoryDialogMessage(), String.valueOf(oldMemoryValue),
      //         new StringValueReceivedHandler()
      //         {
      //            @Override
      //            public void stringValueReceived(String value)
      //            {
      //               if (value == null)
      //               {
      //                  return;
      //               }
      //               else
      //               {
      //                  try
      //                  {
      //                     memory = Integer.parseInt(value);
      //                     updateMemory(memory);
      //                  }
      //                  catch (NumberFormatException e)
      //                  {
      //                     String msg = CloudFoundryExtension.LOCALIZATION_CONSTANT.updateMemoryInvalidNumberMessage();
      //                     // TODO
      //                     //                     IDE.fireEvent(new ExceptionThrownEvent(msg));
      //                     console.print(msg);
      //                  }
      //               }
      //            }
      //         });

      view.setDialogTitle(CloudFoundryExtension.LOCALIZATION_CONSTANT.updateMemoryDialogTitle());
      view.setMessage(CloudFoundryExtension.LOCALIZATION_CONSTANT.updateMemoryDialogMessage());
      view.setProperty(String.valueOf(oldMemoryValue));

      view.showDialog();
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
      // TODO
      //      final String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
      final String projectId = resourceProvider.getActiveProject().getId();

      try
      {
         CloudFoundryClientService.getInstance().updateMemory(resourceProvider.getVfsId(), projectId, null, null,
            memory, new CloudFoundryAsyncRequestCallback<String>(null, updateMemoryLoggedInHandler, null, eventBus)
            {
               @Override
               protected void onSuccess(String result)
               {
                  String msg = CloudFoundryExtension.LOCALIZATION_CONSTANT.updateMemorySuccess(String.valueOf(memory));
                  // TODO
                  //                  IDE.fireEvent(new OutputEvent(msg));
                  console.print(msg);
                  // TODO
                  //                  IDE.fireEvent(new ApplicationInfoChangedEvent(vfs.getId(), projectId));
                  eventBus.fireEvent(new ApplicationInfoChangedEvent(resourceProvider.getVfsId(), projectId));
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
   public void onUpdateInstances(UpdateInstancesEvent event)
   {
      isMemoryEditing = false;
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
      // TODO
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
            new CloudFoundryAsyncRequestCallback<CloudFoundryApplication>(unmarshaller,
               getOldInstancesValueLoggedInHandler, null, eventBus)
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
         // TODO
         //         IDE.fireEvent(new ExceptionThrownEvent(e));
         console.print(e.getMessage());
      }
   }

   private void askForInstancesNumber(int oldInstancesValue)
   {
      // TODO
      //      Dialogs.getInstance().askForValue(CloudFoundryExtension.LOCALIZATION_CONSTANT.updateInstancesDialogTitle(),
      //         CloudFoundryExtension.LOCALIZATION_CONSTANT.updateInstancesDialogMessage(), String.valueOf(oldInstancesValue),
      //         new StringValueReceivedHandler()
      //         {
      //            @Override
      //            public void stringValueReceived(String value)
      //            {
      //               if (value == null)
      //               {
      //                  return;
      //               }
      //               else
      //               {
      //
      //                  instances = value;
      //                  try
      //                  {
      //                     // check, is instances contains only numbers
      //                     Integer.parseInt(instances);
      //                     updateInstances(instances);
      //                  }
      //                  catch (NumberFormatException e)
      //                  {
      //                     String msg = CloudFoundryExtension.LOCALIZATION_CONSTANT.updateInstancesInvalidValueMessage();
      //                     // TODO
      //                     //                     IDE.fireEvent(new ExceptionThrownEvent(msg));
      //                     console.print(msg);
      //                  }
      //               }
      //            }
      //         });

      view.setDialogTitle(CloudFoundryExtension.LOCALIZATION_CONSTANT.updateInstancesDialogTitle());
      view.setMessage(CloudFoundryExtension.LOCALIZATION_CONSTANT.updateInstancesDialogMessage());
      view.setProperty(String.valueOf(oldInstancesValue));

      view.showDialog();
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
      // TODO
      //      final String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
      final String projectId = resourceProvider.getActiveProject().getId();

      String encodedExp = URL.encodePathSegment(instancesExpression);

      try
      {
         CloudFoundryClientService.getInstance().updateInstances(resourceProvider.getVfsId(), projectId, null, null,
            encodedExp,
            new CloudFoundryAsyncRequestCallback<String>(null, updateInstancesLoggedInHandler, null, eventBus)
            {
               @Override
               protected void onSuccess(String result)
               {
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
                        new CloudFoundryAsyncRequestCallback<CloudFoundryApplication>(unmarshaller, null, null,
                           eventBus)
                        {
                           @Override
                           protected void onSuccess(CloudFoundryApplication result)
                           {
                              String msg =
                                 CloudFoundryExtension.LOCALIZATION_CONSTANT.updateInstancesSuccess(String
                                    .valueOf(result.getInstances()));
                              // TODO
                              //                              IDE.fireEvent(new OutputEvent(msg));
                              console.print(msg);
                              // TODO
                              //                              IDE.fireEvent(new ApplicationInfoChangedEvent(vfs.getId(), projectId));
                              eventBus.fireEvent(new ApplicationInfoChangedEvent(resourceProvider.getVfsId(), projectId));
                           }
                        });
                  }
                  catch (RequestException e)
                  {
                     // TODO
                     //                     IDE.fireEvent(new ExceptionThrownEvent(e));
                     console.print(e.getMessage());
                  }
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