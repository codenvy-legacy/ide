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
package com.codenvy.ide.extension.cloudfoundry.client.delete;

import com.codenvy.ide.api.parts.ConsolePart;

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryAutoBeanFactory;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryClientService;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryLocalizationConstant;
import com.codenvy.ide.extension.cloudfoundry.client.login.LoggedInHandler;
import com.codenvy.ide.extension.cloudfoundry.client.login.LoginPresenter;
import com.codenvy.ide.extension.cloudfoundry.shared.CloudFoundryApplication;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AutoBeanUnmarshaller;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Presenter for delete application operation.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: DeleteApplicationPresenter.java Jul 14, 2011 11:51:13 AM vereshchaka $
 */
@Singleton
public class DeleteApplicationPresenter implements DeleteApplicationView.ActionDelegate
{
   private DeleteApplicationView view;

   /**
    * The name of application.
    */
   private String appName;

   /**
    * Name of the server.
    */
   private String serverName;

   private ResourceProvider resourceProvider;

   private EventBus eventBus;

   private ConsolePart console;

   private CloudFoundryLocalizationConstant constant;

   private CloudFoundryAutoBeanFactory autoBeanFactory;

   private LoginPresenter loginPresenter;

   private AsyncCallback<String> appDeleteCallback;

   /**
    * Create presenter.
    * 
    * @param view
    * @param resourceProvider
    * @param eventBus
    * @param console
    * @param constant
    * @param autoBeanFactory
    * @param loginPresenter
    */
   @Inject
   protected DeleteApplicationPresenter(DeleteApplicationView view, ResourceProvider resourceProvider,
      EventBus eventBus, ConsolePart console, CloudFoundryLocalizationConstant constant,
      CloudFoundryAutoBeanFactory autoBeanFactory, LoginPresenter loginPresenter)
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
   public void onCancelClicked()
   {
      view.close();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onDeleteClicked()
   {
      deleteApplication(appDeleteCallback);

      view.close();
   }

   /**
    * Deletes CloudFoundry application.
    * 
    * @param serverName
    * @param appName
    * @param callback
    */
   public void deleteApp(String serverName, String appName, AsyncCallback<String> callback)
   {
      this.serverName = serverName;
      this.appDeleteCallback = callback;

      // If application name is absent then need to find it
      if (appName == null)
      {
         getApplicationInfo();
      }
      else
      {
         this.appName = appName;
         showDialog(appName);
      }
   }

   /**
    * If user is not logged in to CloudFoundry, this handler will be called, after user logged in.
    */
   private LoggedInHandler appInfoLoggedInHandler = new LoggedInHandler()
   {
      @Override
      public void onLoggedIn()
      {
         getApplicationInfo();
      }
   };

   /**
    * Get application's name and put it to the field.
    */
   private void getApplicationInfo()
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
            new CloudFoundryAsyncRequestCallback<CloudFoundryApplication>(unmarshaller, appInfoLoggedInHandler, null,
               eventBus, console, constant, loginPresenter)
            {
               @Override
               protected void onSuccess(CloudFoundryApplication result)
               {
                  appName = result.getName();
                  showDialog(appName);
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
   private LoggedInHandler deleteAppLoggedInHandler = new LoggedInHandler()
   {
      @Override
      public void onLoggedIn()
      {
         deleteApplication(appDeleteCallback);
      }
   };

   /**
    * Deletes application.
    * 
    * @param callback
    */
   private void deleteApplication(AsyncCallback<String> callback)
   {
      boolean isDeleteServices = view.isDeleteServices();
      String projectId = null;

      Project project = resourceProvider.getActiveProject();
      // Checking does current project work with deleting CloudFoundry application.
      // If project don't have the same CloudFoundry application name in properties
      // then this property won't be cleaned.
      if (project != null && project.getPropertyValue("cloudfoundry-application") != null
         && appName.equals(project.getPropertyValue("cloudfoundry-application")))
      {
         projectId = project.getId();
      }

      try
      {
         CloudFoundryClientService.getInstance().deleteApplication(resourceProvider.getVfsId(), projectId, appName,
            serverName, isDeleteServices,
            new CloudFoundryAsyncRequestCallback<String>(null, deleteAppLoggedInHandler, null, eventBus, console,
               constant, loginPresenter)
            {
               @Override
               protected void onSuccess(String result)
               {
                  view.close();
                  console.print(constant.applicationDeletedMsg(appName));
                  appDeleteCallback.onSuccess(appName);
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
    * @param appName application name which need to delete
    */
   public void showDialog(String appName)
   {
      view.setAskMessage(constant.deleteApplicationQuestion(appName));
      view.setAskDeleteServices(constant.deleteApplicationAskDeleteServices());
      view.setDeleteServices(false);

      view.showDialog();
   }
}