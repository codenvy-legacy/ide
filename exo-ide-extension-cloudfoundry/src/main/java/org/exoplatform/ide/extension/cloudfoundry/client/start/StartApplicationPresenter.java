/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.extension.cloudfoundry.client.start;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension;
import org.exoplatform.ide.extension.cloudfoundry.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudfoundryApplication;
import org.exoplatform.ide.extension.cloudfoundry.shared.Framework;

import java.util.List;

/**
 * Presenter for start and stop application commands.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: StartApplicationPresenter.java Jul 12, 2011 3:58:22 PM vereshchaka $
 *
 */
public class StartApplicationPresenter implements ItemsSelectedHandler, StartApplicationHandler, StopApplicationHandler,
RestartApplicationHandler
{
   /**
    * Events handler.
    */
   private HandlerManager eventBus;
   
   /**
    * Selected items in navigation tree.
    */
   private List<Item> selectedItems;
   
   public StartApplicationPresenter(HandlerManager eventbus)
   {
      this.eventBus = eventbus;
      
      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
      eventBus.addHandler(StartApplicationEvent.TYPE, this);
      eventBus.addHandler(StopApplicationEvent.TYPE, this);
      eventBus.addHandler(RestartApplicationEvent.TYPE, this);
   }
   
   public void bindDisplay(List<Framework> frameworks)
   {
   }
   
   /**
    * @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent)
    */
   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      selectedItems = event.getSelectedItems();
   }

   
   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.start.StopApplicationHandler#onStopApplication(org.exoplatform.ide.extension.cloudfoundry.client.start.StopApplicationEvent)
    */
   @Override
   public void onStopApplication(StopApplicationEvent event)
   {
      checkIsStopped();
   }
   
   /**
    * If user is not logged in to CloudFoundry, this handler will be called, after user logged in.
    */
   private LoggedInHandler startLoggedInHandler = new LoggedInHandler()
   {
      @Override
      public void onLoggedIn()
      {
         startApplication();
      }
   };
   
   /**
    * If user is not logged in to CloudFoundry, this handler will be called, after user logged in.
    */
   private LoggedInHandler stopLoggedInHandler = new LoggedInHandler()
   {
      @Override
      public void onLoggedIn()
      {
         stopApplication();
      }
   };
   
   /**
    * If user is not logged in to CloudFoundry, this handler will be called, after user logged in.
    */
   private LoggedInHandler checkIsStartedLoggedInHandler = new LoggedInHandler()
   {
      @Override
      public void onLoggedIn()
      {
         checkIsStarted();
      }
   };
   
   /**
    * If user is not logged in to CloudFoundry, this handler will be called, after user logged in.
    */
   private LoggedInHandler checkIsStoppedLoggedInHandler = new LoggedInHandler()
   {
      @Override
      public void onLoggedIn()
      {
         checkIsStopped();
      }
   };
   
   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.start.StartApplicationHandler#onStartApplication(org.exoplatform.ide.extension.cloudfoundry.client.start.StartApplicationEvent)
    */
   @Override
   public void onStartApplication(StartApplicationEvent event)
   {
      checkIsStarted();
   }
   
   private void checkIsStarted()
   {
      String workDir = getWorkDir();

      CloudFoundryClientService.getInstance().getApplicationInfo(workDir, null,
         new CloudFoundryAsyncRequestCallback<CloudfoundryApplication>(eventBus, checkIsStartedLoggedInHandler, null)
         {

            @Override
            protected void onSuccess(CloudfoundryApplication result)
            {
               if ("STARTED".equals(result.getState()))
               {
                  String msg = CloudFoundryExtension.LOCALIZATION_CONSTANT.applicationAlreadyStarted(result.getName());
                  eventBus.fireEvent(new OutputEvent(msg));
               }
               else
               {
                  startApplication();
               }
            }
         });
   }
   
   private void checkIsStopped()
   {
      String workDir = getWorkDir();

      CloudFoundryClientService.getInstance().getApplicationInfo(workDir, null,
         new CloudFoundryAsyncRequestCallback<CloudfoundryApplication>(eventBus, checkIsStoppedLoggedInHandler, null)
         {

            @Override
            protected void onSuccess(CloudfoundryApplication result)
            {
               if ("STOPPED".equals(result.getState()))
               {
                  String msg = CloudFoundryExtension.LOCALIZATION_CONSTANT.applicationAlreadyStopped(result.getName());
                  eventBus.fireEvent(new OutputEvent(msg));
               }
               else
               {
                  stopApplication();
               }
            }
         });
   }
   
   private void startApplication()
   {
      String workDir = getWorkDir();
      
      CloudFoundryClientService.getInstance().startApplication(workDir, null,
         new CloudFoundryAsyncRequestCallback<CloudfoundryApplication>(eventBus, startLoggedInHandler, null)
         {
            @Override
            protected void onSuccess(CloudfoundryApplication result)
            {
               final String appUris = getAppUrisAsString(result);
               String msg = "";
               if (appUris.isEmpty())
               {
                  msg = CloudFoundryExtension.LOCALIZATION_CONSTANT.applicationStarted(result.getName());
               }
               else
               {
                  msg = CloudFoundryExtension.LOCALIZATION_CONSTANT.applicationStartedOnUrls(result.getName(), appUris);
               }
               eventBus.fireEvent(new OutputEvent(msg));
            }
         });      
   }
   
   private String getAppUrisAsString(CloudfoundryApplication application)
   {
      String appUris = "";
      for (String uri : application.getUris())
      {
         if (!uri.startsWith("http"))
         {
            uri = "http://" + uri;
         }
         appUris += ", " + "<a href=\"" + uri + "\">" + uri + "</a>";
      }
      if (!appUris.isEmpty())
      {
         //crop unnecessary symbols
         appUris = appUris.substring(2);
      }
      return appUris;
   }
   
   private void stopApplication()
   {
      final String workDir = getWorkDir();
      
      CloudFoundryClientService.getInstance().stopApplication(workDir, null,
         new CloudFoundryAsyncRequestCallback<String>(eventBus, stopLoggedInHandler, null)
         {
            @Override
            protected void onSuccess(String result)
            {
               CloudFoundryClientService.getInstance().getApplicationInfo(workDir, null, new CloudFoundryAsyncRequestCallback<CloudfoundryApplication>(eventBus, null, null)
               {
                  @Override
                  protected void onSuccess(CloudfoundryApplication result)
                  {
                     final String msg = CloudFoundryExtension.LOCALIZATION_CONSTANT.applicationStopped(result.getName());
                     eventBus.fireEvent(new OutputEvent(msg));
                  }
               });
            }
         });
   }
   
   private String getWorkDir()
   {
      if (selectedItems.size() == 0)
         return null;
      
      String workDir = selectedItems.get(0).getHref();
      if (selectedItems.get(0) instanceof File)
      {
         workDir = workDir.substring(0, workDir.lastIndexOf("/") + 1);
      }
      return workDir;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.start.RestartApplicationHandler#onRestartApplication(org.exoplatform.ide.extension.cloudfoundry.client.start.RestartApplicationEvent)
    */
   @Override
   public void onRestartApplication(RestartApplicationEvent event)
   {
      restartApplication();
   }
   
   private LoggedInHandler restartLoggedInHandler = new LoggedInHandler()
   {
      @Override
      public void onLoggedIn()
      {
         restartApplication();
      }
   };
   
   private void restartApplication()
   {
      String workDir = getWorkDir();
      
      CloudFoundryClientService.getInstance().restartApplication(workDir, null,
         new CloudFoundryAsyncRequestCallback<CloudfoundryApplication>(eventBus, restartLoggedInHandler, null)
         {
            @Override
            protected void onSuccess(CloudfoundryApplication result)
            {
               final String appUris = getAppUrisAsString(result);
               String msg = "";
               if (appUris.isEmpty())
               {
                  msg = CloudFoundryExtension.LOCALIZATION_CONSTANT.applicationRestarted(result.getName());
               }
               else
               {
                  msg = CloudFoundryExtension.LOCALIZATION_CONSTANT.applicationRestartedUris(result.getName(), appUris);
               }
               eventBus.fireEvent(new OutputEvent(msg));
            }
         });
   }

}
