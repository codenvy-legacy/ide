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

import java.util.List;

import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension;
import org.exoplatform.ide.extension.cloudfoundry.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.cloudfoundry.client.project.ApplicationInfoChangedEvent;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudfoundryApplication;
import org.exoplatform.ide.extension.cloudfoundry.shared.Framework;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.vfs.client.model.ItemContext;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * Presenter for start and stop application commands.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: StartApplicationPresenter.java Jul 12, 2011 3:58:22 PM vereshchaka $
 *
 */
public class StartApplicationPresenter extends GitPresenter implements StartApplicationHandler, StopApplicationHandler,
   RestartApplicationHandler
{

   public StartApplicationPresenter()
   {
      IDE.addHandler(StartApplicationEvent.TYPE, this);
      IDE.addHandler(StopApplicationEvent.TYPE, this);
      IDE.addHandler(RestartApplicationEvent.TYPE, this);
   }

   public void bindDisplay(List<Framework> frameworks)
   {
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.start.StopApplicationHandler#onStopApplication(org.exoplatform.ide.extension.cloudfoundry.client.start.StopApplicationEvent)
    */
   @Override
   public void onStopApplication(StopApplicationEvent event)
   {
      if (event.getApplicationName() == null)
         checkIsStopped();
      else
         stopApplication(event.getApplicationName());
   }

   /**
    * If user is not logged in to CloudFoundry, this handler will be called, after user logged in.
    */
   private LoggedInHandler startLoggedInHandler = new LoggedInHandler()
   {
      @Override
      public void onLoggedIn()
      {
         startApplication(null);
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
         stopApplication(null);
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
      if (event.getApplicationName() == null && makeSelectionCheck())
         checkIsStarted();
      else
         startApplication(event.getApplicationName());
   }

   private void checkIsStarted()
   {
      ProjectModel project = ((ItemContext)selectedItems.get(0)).getProject();

      CloudFoundryClientService.getInstance().getApplicationInfo(vfs.getId(), project.getId(), null, null,
         new CloudFoundryAsyncRequestCallback<CloudfoundryApplication>(IDE.eventBus(), checkIsStartedLoggedInHandler, null)
         {
            @Override
            protected void onSuccess(CloudfoundryApplication result)
            {
               if ("STARTED".equals(result.getState()))
               {
                  String msg = CloudFoundryExtension.LOCALIZATION_CONSTANT.applicationAlreadyStarted(result.getName());
                  IDE.fireEvent(new OutputEvent(msg));
               }
               else
               {
                  startApplication(null);
               }
            }
         });
   }

   private void checkIsStopped()
   {
      ProjectModel project = ((ItemContext)selectedItems.get(0)).getProject();

      CloudFoundryClientService.getInstance().getApplicationInfo(vfs.getId(), project.getId(), null, null,
         new CloudFoundryAsyncRequestCallback<CloudfoundryApplication>(IDE.eventBus(), checkIsStoppedLoggedInHandler, null)
         {

            @Override
            protected void onSuccess(CloudfoundryApplication result)
            {
               if ("STOPPED".equals(result.getState()))
               {
                  String msg = CloudFoundryExtension.LOCALIZATION_CONSTANT.applicationAlreadyStopped(result.getName());
                  IDE.fireEvent(new OutputEvent(msg));
               }
               else
               {
                  stopApplication(null);
               }
            }
         });
   }

   private void startApplication(String name)
   {
      final String projectId = (((ItemContext)selectedItems.get(0)).getProject() != null) ?  ((ItemContext)selectedItems.get(0)).getProject().getId() : null;
      
      CloudFoundryClientService.getInstance().startApplication(vfs.getId(), projectId, name, null,
         new CloudFoundryAsyncRequestCallback<CloudfoundryApplication>(IDE.eventBus(), startLoggedInHandler, null)
         {
            @Override
            protected void onSuccess(CloudfoundryApplication result)
            {
               if (!"STARTED".equals(result.getState()))
               {
                  String msg = CloudFoundryExtension.LOCALIZATION_CONSTANT.applicationWasNotStarted(result.getName());
                  IDE.fireEvent(new OutputEvent(msg));
                  return;
               }
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
               IDE.fireEvent(new OutputEvent(msg));
               IDE.fireEvent(new ApplicationInfoChangedEvent(vfs.getId(), projectId));
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
         appUris += ", " + "<a href=\"" + uri + "\" target=\"_blank\">" + uri + "</a>";
      }
      if (!appUris.isEmpty())
      {
         //crop unnecessary symbols
         appUris = appUris.substring(2);
      }
      return appUris;
   }

   private void stopApplication(final String name)
   {
      final String projectId =
         (((ItemContext)selectedItems.get(0)).getProject() != null) ? ((ItemContext)selectedItems.get(0)).getProject()
            .getId() : null;
      
      CloudFoundryClientService.getInstance().stopApplication(vfs.getId(), projectId, name, null,
         new CloudFoundryAsyncRequestCallback<String>(IDE.eventBus(), stopLoggedInHandler, null)
         {
            @Override
            protected void onSuccess(String result)
            {
               CloudFoundryClientService.getInstance().getApplicationInfo(vfs.getId(), projectId, name, null,
                  new CloudFoundryAsyncRequestCallback<CloudfoundryApplication>(IDE.eventBus(), null, null)
                  {
                     @Override
                     protected void onSuccess(CloudfoundryApplication result)
                     {
                        final String msg =
                           CloudFoundryExtension.LOCALIZATION_CONSTANT.applicationStopped(result.getName());
                        IDE.fireEvent(new OutputEvent(msg));
                        IDE.fireEvent(new ApplicationInfoChangedEvent(vfs.getId(), projectId));
                     }
                  });
            }
         });
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.start.RestartApplicationHandler#onRestartApplication(org.exoplatform.ide.extension.cloudfoundry.client.start.RestartApplicationEvent)
    */
   @Override
   public void onRestartApplication(RestartApplicationEvent event)
   {

      restartApplication(event.getApplicationName());

   }

   private LoggedInHandler restartLoggedInHandler = new LoggedInHandler()
   {
      @Override
      public void onLoggedIn()
      {
         restartApplication(null);
      }
   };

   private void restartApplication(String name)
   {
      final String projectId = (((ItemContext)selectedItems.get(0)).getProject() != null) ?  ((ItemContext)selectedItems.get(0)).getProject().getId() : null;

      CloudFoundryClientService.getInstance().restartApplication(vfs.getId(), projectId, name, null,
         new CloudFoundryAsyncRequestCallback<CloudfoundryApplication>(IDE.eventBus(), restartLoggedInHandler, null)
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
               IDE.fireEvent(new OutputEvent(msg));
               IDE.fireEvent(new ApplicationInfoChangedEvent(vfs.getId(), projectId));
            }
         });
   }

}
