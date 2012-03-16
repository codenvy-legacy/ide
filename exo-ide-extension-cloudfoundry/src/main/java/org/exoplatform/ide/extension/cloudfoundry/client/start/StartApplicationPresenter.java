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

import com.google.gwt.http.client.RequestException;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension;
import org.exoplatform.ide.extension.cloudfoundry.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.cloudfoundry.client.project.ApplicationInfoChangedEvent;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudFoundryApplication;
import org.exoplatform.ide.extension.cloudfoundry.shared.Framework;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.vfs.client.model.ItemContext;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

import java.util.List;

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

      try
      {
         AutoBean<CloudFoundryApplication> CloudFoundryApplication =
            CloudFoundryExtension.AUTO_BEAN_FACTORY.create(CloudFoundryApplication.class);
         AutoBeanUnmarshaller<CloudFoundryApplication> unmarshaller =
            new AutoBeanUnmarshaller<CloudFoundryApplication>(CloudFoundryApplication);
         CloudFoundryClientService.getInstance().getApplicationInfo(
            vfs.getId(),
            project.getId(),
            null,
            null,
            new CloudFoundryAsyncRequestCallback<CloudFoundryApplication>(unmarshaller, checkIsStartedLoggedInHandler,
               null)
            {
               @Override
               protected void onSuccess(CloudFoundryApplication result)
               {
                  if ("STARTED".equals(result.getState()))
                  {
                     String msg =
                        CloudFoundryExtension.LOCALIZATION_CONSTANT.applicationAlreadyStarted(result.getName());
                     IDE.fireEvent(new OutputEvent(msg));
                  }
                  else
                  {
                     startApplication(null);
                  }
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   private void checkIsStopped()
   {
      ProjectModel project = ((ItemContext)selectedItems.get(0)).getProject();

      try
      {
         AutoBean<CloudFoundryApplication> CloudFoundryApplication =
            CloudFoundryExtension.AUTO_BEAN_FACTORY.create(CloudFoundryApplication.class);

         AutoBeanUnmarshaller<CloudFoundryApplication> unmarshaller =
            new AutoBeanUnmarshaller<CloudFoundryApplication>(CloudFoundryApplication);

         CloudFoundryClientService.getInstance().getApplicationInfo(
            vfs.getId(),
            project.getId(),
            null,
            null,
            new CloudFoundryAsyncRequestCallback<CloudFoundryApplication>(unmarshaller, checkIsStoppedLoggedInHandler,
               null)
            {

               @Override
               protected void onSuccess(CloudFoundryApplication result)
               {
                  if ("STOPPED".equals(result.getState()))
                  {
                     String msg =
                        CloudFoundryExtension.LOCALIZATION_CONSTANT.applicationAlreadyStopped(result.getName());
                     IDE.fireEvent(new OutputEvent(msg));
                  }
                  else
                  {
                     stopApplication(null);
                  }
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   private void startApplication(String name)
   {
      final String projectId =
         (((ItemContext)selectedItems.get(0)).getProject() != null) ? ((ItemContext)selectedItems.get(0)).getProject()
            .getId() : null;

      try
      {
         AutoBean<CloudFoundryApplication> cloudFoundryApplication =
            CloudFoundryExtension.AUTO_BEAN_FACTORY.create(CloudFoundryApplication.class);

         AutoBeanUnmarshaller<CloudFoundryApplication> unmarshaller =
            new AutoBeanUnmarshaller<CloudFoundryApplication>(cloudFoundryApplication);

         CloudFoundryClientService.getInstance().startApplication(vfs.getId(), projectId, name, null,
            new CloudFoundryAsyncRequestCallback<CloudFoundryApplication>(unmarshaller, startLoggedInHandler, null)
            {
               @Override
               protected void onSuccess(CloudFoundryApplication result)
               {
                  if (!"STARTED".equals(result.getState()))
                  {
                     String msg =
                        CloudFoundryExtension.LOCALIZATION_CONSTANT.applicationWasNotStarted(result.getName());
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
                     msg =
                        CloudFoundryExtension.LOCALIZATION_CONSTANT.applicationStartedOnUrls(result.getName(), appUris);
                  }
                  IDE.fireEvent(new OutputEvent(msg));
                  IDE.fireEvent(new ApplicationInfoChangedEvent(vfs.getId(), projectId));
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   private String getAppUrisAsString(CloudFoundryApplication application)
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
         // crop unnecessary symbols
         appUris = appUris.substring(2);
      }
      return appUris;
   }

   private void stopApplication(final String name)
   {
      final String projectId =
         (((ItemContext)selectedItems.get(0)).getProject() != null) ? ((ItemContext)selectedItems.get(0)).getProject()
            .getId() : null;

      try
      {
         CloudFoundryClientService.getInstance().stopApplication(vfs.getId(), projectId, name, null,
            new CloudFoundryAsyncRequestCallback<String>(null, stopLoggedInHandler, null)
            {
               @Override
               protected void onSuccess(String result)
               {
                  try
                  {
                     AutoBean<CloudFoundryApplication> CloudFoundryApplication =
                        CloudFoundryExtension.AUTO_BEAN_FACTORY.create(CloudFoundryApplication.class);

                     AutoBeanUnmarshaller<CloudFoundryApplication> unmarshaller =
                        new AutoBeanUnmarshaller<CloudFoundryApplication>(CloudFoundryApplication);

                     CloudFoundryClientService.getInstance().getApplicationInfo(vfs.getId(), projectId, name, null,
                        new CloudFoundryAsyncRequestCallback<CloudFoundryApplication>(unmarshaller, null, null)
                        {
                           @Override
                           protected void onSuccess(CloudFoundryApplication result)
                           {
                              final String msg =
                                 CloudFoundryExtension.LOCALIZATION_CONSTANT.applicationStopped(result.getName());
                              IDE.fireEvent(new OutputEvent(msg));
                              IDE.fireEvent(new ApplicationInfoChangedEvent(vfs.getId(), projectId));
                           }
                        });
                  }
                  catch (RequestException e)
                  {
                     IDE.fireEvent(new ExceptionThrownEvent(e));
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
      final String projectId =
         (((ItemContext)selectedItems.get(0)).getProject() != null) ? ((ItemContext)selectedItems.get(0)).getProject()
            .getId() : null;

      try
      {
         AutoBean<CloudFoundryApplication> cloudFoundryApplication =
            CloudFoundryExtension.AUTO_BEAN_FACTORY.create(CloudFoundryApplication.class);

         AutoBeanUnmarshaller<CloudFoundryApplication> unmarshaller =
            new AutoBeanUnmarshaller<CloudFoundryApplication>(cloudFoundryApplication);

         CloudFoundryClientService.getInstance().restartApplication(vfs.getId(), projectId, name, null,
            new CloudFoundryAsyncRequestCallback<CloudFoundryApplication>(unmarshaller, restartLoggedInHandler, null)
            {
               @Override
               protected void onSuccess(CloudFoundryApplication result)
               {
                  final String appUris = getAppUrisAsString(result);
                  String msg = "";
                  if (appUris.isEmpty())
                  {
                     msg = CloudFoundryExtension.LOCALIZATION_CONSTANT.applicationRestarted(result.getName());
                  }
                  else
                  {
                     msg =
                        CloudFoundryExtension.LOCALIZATION_CONSTANT.applicationRestartedUris(result.getName(), appUris);
                  }
                  IDE.fireEvent(new OutputEvent(msg));
                  IDE.fireEvent(new ApplicationInfoChangedEvent(vfs.getId(), projectId));
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

}
