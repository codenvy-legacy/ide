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
package com.codenvy.ide.extension.cloudfoundry.client.start;

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
import com.codenvy.ide.extension.cloudfoundry.shared.Framework;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AutoBeanUnmarshaller;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.event.shared.EventBus;

import java.util.List;

/**
 * Presenter for start and stop application commands.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: StartApplicationPresenter.java Jul 12, 2011 3:58:22 PM vereshchaka $
 */
@Singleton
public class StartApplicationPresenter
{
   private EventBus eventBus;

   private ResourceProvider resourceProvider;

   private Console console;

   private CloudFoundryLocalizationConstant constant;

   private CloudFoundryAutoBeanFactory autoBeanFactory;

   private AsyncCallback<String> appInfoChangedCallback;

   private LoginPresenter loginPresenter;

   /**
    * Create presenter.
    * 
    * @param eventBus
    * @param resourceProvider
    * @param console
    * @param constant
    * @param autoBeanFactory
    * @param loginPresenter
    */
   @Inject
   protected StartApplicationPresenter(EventBus eventBus, ResourceProvider resourceProvider, Console console,
      CloudFoundryLocalizationConstant constant, CloudFoundryAutoBeanFactory autoBeanFactory,
      LoginPresenter loginPresenter)
   {
      this.eventBus = eventBus;
      this.resourceProvider = resourceProvider;
      this.console = console;
      this.constant = constant;
      this.autoBeanFactory = autoBeanFactory;
      this.loginPresenter = loginPresenter;
   }

   public void bindDisplay(List<Framework> frameworks)
   {
   }

   /**
    * If user is not logged in to CloudFoundry, this handler will be called, after user logged in.
    */
   private LoggedInHandler startLoggedInHandler = new LoggedInHandler()
   {
      @Override
      public void onLoggedIn()
      {
         startApplication(null, appInfoChangedCallback);
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
         stopApplication(null, appInfoChangedCallback);
      }
   };

   /**
    * If user is not logged in to CloudFoundry, this handler will be called, after user logged in.
    */
   private LoggedInHandler restartLoggedInHandler = new LoggedInHandler()
   {
      @Override
      public void onLoggedIn()
      {
         restartApplication(null, appInfoChangedCallback);
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
    * Starts CloudFounry application.
    *  
    * @param appName
    * @param callback
    */
   public void startApp(String appName, AsyncCallback<String> callback)
   {
      this.appInfoChangedCallback = callback;
      if (appName == null)
      {
         checkIsStarted();
      }
      else
      {
         startApplication(appName, callback);
      }
   }

   /**
    * Gets information about active project and check its state.
    */
   private void checkIsStarted()
   {
      Project project = resourceProvider.getActiveProject();

      try
      {
         AutoBean<CloudFoundryApplication> CloudFoundryApplication = autoBeanFactory.cloudFoundryApplication();
         AutoBeanUnmarshaller<CloudFoundryApplication> unmarshaller =
            new AutoBeanUnmarshaller<CloudFoundryApplication>(CloudFoundryApplication);
         CloudFoundryClientService.getInstance().getApplicationInfo(
            resourceProvider.getVfsId(),
            project.getId(),
            null,
            null,
            new CloudFoundryAsyncRequestCallback<CloudFoundryApplication>(unmarshaller, checkIsStartedLoggedInHandler,
               null, eventBus, console, constant, loginPresenter)
            {
               @Override
               protected void onSuccess(CloudFoundryApplication result)
               {
                  if ("STARTED".equals(result.getState()) && result.getInstances() == result.getRunningInstances())
                  {
                     String msg = constant.applicationAlreadyStarted(result.getName());
                     console.print(msg);
                  }
                  else
                  {
                     startApplication(null, appInfoChangedCallback);
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
    * Starts application.
    * 
    * @param name
    * @param callback
    */
   private void startApplication(String name, final AsyncCallback<String> callback)
   {
      final String projectId =
         resourceProvider.getActiveProject() != null ? resourceProvider.getActiveProject().getId() : null;

      try
      {
         AutoBean<CloudFoundryApplication> cloudFoundryApplication = autoBeanFactory.cloudFoundryApplication();
         AutoBeanUnmarshaller<CloudFoundryApplication> unmarshaller =
            new AutoBeanUnmarshaller<CloudFoundryApplication>(cloudFoundryApplication);

         CloudFoundryClientService.getInstance().startApplication(
            resourceProvider.getVfsId(),
            projectId,
            name,
            null,
            new CloudFoundryAsyncRequestCallback<CloudFoundryApplication>(unmarshaller, startLoggedInHandler, null,
               eventBus, console, constant, loginPresenter)
            {
               @Override
               protected void onSuccess(CloudFoundryApplication result)
               {
                  if ("STARTED".equals(result.getState()) && result.getInstances() == result.getRunningInstances())
                  {
                     String msg = constant.applicationCreatedSuccessfully(result.getName());
                     if (result.getUris().isEmpty())
                     {
                        msg += "<br>" + constant.applicationStartedWithNoUrls();
                     }
                     else
                     {
                        msg += "<br>" + constant.applicationStartedOnUrls(result.getName(), getAppUrisAsString(result));
                     }

                     console.print(msg);
                     callback.onSuccess(projectId);
                  }
                  else
                  {
                     String msg = constant.applicationWasNotStarted(result.getName());
                     console.print(msg);
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
    * Creates application's url in HTML format. 
    * 
    * @param application
    * @return
    */
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

   /**
    * Stops CloudFounry application.
    * 
    * @param appName
    * @param callback
    */
   public void stopApp(String appName, AsyncCallback<String> callback)
   {
      if (appName == null)
      {
         checkIsStopped();
      }
      else
      {
         stopApplication(appName, callback);
      }
   }

   /**
    * Gets information about active project and check its state.
    */
   private void checkIsStopped()
   {
      Project project = resourceProvider.getActiveProject();

      try
      {
         AutoBean<CloudFoundryApplication> CloudFoundryApplication = autoBeanFactory.cloudFoundryApplication();
         AutoBeanUnmarshaller<CloudFoundryApplication> unmarshaller =
            new AutoBeanUnmarshaller<CloudFoundryApplication>(CloudFoundryApplication);

         CloudFoundryClientService.getInstance().getApplicationInfo(
            resourceProvider.getVfsId(),
            project.getId(),
            null,
            null,
            new CloudFoundryAsyncRequestCallback<CloudFoundryApplication>(unmarshaller, checkIsStoppedLoggedInHandler,
               null, eventBus, console, constant, loginPresenter)
            {
               @Override
               protected void onSuccess(CloudFoundryApplication result)
               {
                  if ("STOPPED".equals(result.getState()))
                  {
                     String msg = constant.applicationAlreadyStopped(result.getName());
                     console.print(msg);
                  }
                  else
                  {
                     stopApplication(null, appInfoChangedCallback);
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
    * Stops application.
    * 
    * @param name
    * @param callback
    */
   private void stopApplication(final String name, final AsyncCallback<String> callback)
   {
      final String projectId =
         resourceProvider.getActiveProject() != null ? resourceProvider.getActiveProject().getId() : null;

      try
      {
         CloudFoundryClientService.getInstance().stopApplication(resourceProvider.getVfsId(), projectId, name, null,
            new CloudFoundryAsyncRequestCallback<String>(null, stopLoggedInHandler, null, eventBus, console, constant,
               loginPresenter)
            {
               @Override
               protected void onSuccess(String result)
               {
                  try
                  {
                     AutoBean<CloudFoundryApplication> CloudFoundryApplication =
                        autoBeanFactory.cloudFoundryApplication();
                     AutoBeanUnmarshaller<CloudFoundryApplication> unmarshaller =
                        new AutoBeanUnmarshaller<CloudFoundryApplication>(CloudFoundryApplication);

                     CloudFoundryClientService.getInstance().getApplicationInfo(
                        resourceProvider.getVfsId(),
                        projectId,
                        name,
                        null,
                        new CloudFoundryAsyncRequestCallback<CloudFoundryApplication>(unmarshaller, null, null,
                           eventBus, console, constant, loginPresenter)
                        {
                           @Override
                           protected void onSuccess(CloudFoundryApplication result)
                           {
                              final String msg = constant.applicationStopped(result.getName());
                              console.print(msg);
                              callback.onSuccess(projectId);
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

   /**
    * Restarts CloudFoundry application.
    * 
    * @param appName
    * @param callback
    */
   public void restartApp(String appName, AsyncCallback<String> callback)
   {
      this.appInfoChangedCallback = callback;
      restartApplication(appName, callback);
   }

   /**
    * Restart application.
    * 
    * @param name
    * @param callback
    */
   private void restartApplication(String name, final AsyncCallback<String> callback)
   {
      final String projectId =
         resourceProvider.getActiveProject() != null ? resourceProvider.getActiveProject().getId() : null;

      try
      {
         AutoBean<CloudFoundryApplication> cloudFoundryApplication = autoBeanFactory.cloudFoundryApplication();
         AutoBeanUnmarshaller<CloudFoundryApplication> unmarshaller =
            new AutoBeanUnmarshaller<CloudFoundryApplication>(cloudFoundryApplication);

         CloudFoundryClientService.getInstance().restartApplication(
            resourceProvider.getVfsId(),
            projectId,
            name,
            null,
            new CloudFoundryAsyncRequestCallback<CloudFoundryApplication>(unmarshaller, restartLoggedInHandler, null,
               eventBus, console, constant, loginPresenter)
            {
               @Override
               protected void onSuccess(CloudFoundryApplication result)
               {
                  if (result.getInstances() == result.getRunningInstances())
                  {
                     final String appUris = getAppUrisAsString(result);
                     String msg = "";
                     if (appUris.isEmpty())
                     {
                        msg = constant.applicationRestarted(result.getName());
                     }
                     else
                     {
                        msg = constant.applicationRestartedUris(result.getName(), appUris);
                     }

                     console.print(msg);
                     callback.onSuccess(projectId);
                  }
                  else
                  {
                     String msg = constant.applicationWasNotStarted(result.getName());
                     console.print(msg);
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