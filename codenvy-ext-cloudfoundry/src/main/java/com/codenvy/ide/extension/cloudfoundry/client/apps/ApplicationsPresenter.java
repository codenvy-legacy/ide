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
package com.codenvy.ide.extension.cloudfoundry.client.apps;

import com.codenvy.ide.api.ui.console.Console;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryAutoBeanFactory;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryClientService;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryExtension;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryLocalizationConstant;
import com.codenvy.ide.extension.cloudfoundry.client.delete.DeleteApplicationPresenter;
import com.codenvy.ide.extension.cloudfoundry.client.login.LoggedInHandler;
import com.codenvy.ide.extension.cloudfoundry.client.marshaller.ApplicationListUnmarshaller;
import com.codenvy.ide.extension.cloudfoundry.client.marshaller.TargetsUnmarshaller;
import com.codenvy.ide.extension.cloudfoundry.client.start.StartApplicationPresenter;
import com.codenvy.ide.extension.cloudfoundry.shared.CloudFoundryApplication;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * The applications presenter manager CloudFounry application.
 * The presenter can start, stop, update, delete application.
 * 
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: Aug 18, 2011 evgen $
 */
@Singleton
public class ApplicationsPresenter implements ApplicationsView.ActionDelegate
{
   private ApplicationsView view;

   private String currentServer;

   private List<String> servers = new ArrayList<String>();

   private EventBus eventBus;

   private Console console;

   private CloudFoundryLocalizationConstant constant;

   private CloudFoundryAutoBeanFactory autoBeanFactory;

   private StartApplicationPresenter startAppPresenter;

   private DeleteApplicationPresenter deleteAppPresenter;

   private AsyncCallback<String> appInfoChangedCallback = new AsyncCallback<String>()
   {
      @Override
      public void onSuccess(String result)
      {
         getApplicationList();
      }

      @Override
      public void onFailure(Throwable caught)
      {
         // do nothing
      }
   };

   /**
    * Create presenter.
    * 
    * @param view
    * @param eventBus
    * @param console
    * @param resourceProvider
    * @param startAppPresenter
    * @param deleteAppPresenter
    * @param constant
    * @param autoBeanFactory
    */
   @Inject
   protected ApplicationsPresenter(ApplicationsView view, EventBus eventBus, Console console,
      StartApplicationPresenter startAppPresenter, DeleteApplicationPresenter deleteAppPresenter,
      CloudFoundryLocalizationConstant constant, CloudFoundryAutoBeanFactory autoBeanFactory)
   {
      this.view = view;
      this.view.setDelegate(this);
      this.eventBus = eventBus;
      this.console = console;
      this.constant = constant;
      this.autoBeanFactory = autoBeanFactory;
      this.startAppPresenter = startAppPresenter;
      this.deleteAppPresenter = deleteAppPresenter;
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
   public void onShowClicked()
   {
      checkLogginedToServer();
   }

   /**
    * Gets list of available application for current user.
    */
   private void getApplicationList()
   {
      try
      {
         CloudFoundryClientService.getInstance().getApplicationList(
            currentServer,
            new CloudFoundryAsyncRequestCallback<List<CloudFoundryApplication>>(new ApplicationListUnmarshaller(
               new ArrayList<CloudFoundryApplication>(), autoBeanFactory), new LoggedInHandler()
            {
               @Override
               public void onLoggedIn()
               {
                  getApplicationList();
               }
            }, null, currentServer, eventBus, console, constant)
            {

               @Override
               protected void onSuccess(List<CloudFoundryApplication> result)
               {
                  view.setApplications(result);
                  view.setServer(currentServer);

                  // update the list of servers, if was enter value, that doesn't present in list
                  if (!servers.contains(currentServer))
                  {
                     getServers();
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
    * Gets servers.
    */
   private void getServers()
   {
      try
      {
         CloudFoundryClientService.getInstance().getTargets(
            new AsyncRequestCallback<List<String>>(new TargetsUnmarshaller(new ArrayList<String>()))
            {
               @Override
               protected void onSuccess(List<String> result)
               {
                  servers = result;
                  view.setServers(servers);
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  eventBus.fireEvent(new ExceptionThrownEvent(exception));
                  console.print(exception.getMessage());
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
    * Show dialog.
    */
   public void showDialog()
   {
      checkLogginedToServer();
   }

   /**
    * Gets target from CloudFoundry server. If this works well then we will know 
    * we have connect to CloudFoundry server.
    */
   private void checkLogginedToServer()
   {
      try
      {
         CloudFoundryClientService.getInstance().getTargets(
            new AsyncRequestCallback<List<String>>(new TargetsUnmarshaller(new ArrayList<String>()))
            {
               @Override
               protected void onSuccess(List<String> result)
               {
                  if (result.isEmpty())
                  {
                     servers = new ArrayList<String>();
                     servers.add(CloudFoundryExtension.DEFAULT_SERVER);
                  }
                  else
                  {
                     servers = result;
                  }
                  // open view
                  openView();
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  eventBus.fireEvent(new ExceptionThrownEvent(exception));
                  console.print(exception.getMessage());
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
    * Opens view.
    */
   private void openView()
   {
      view.setServers(servers);
      // fill the list of applications
      currentServer = servers.get(0);
      getApplicationList();

      if (!view.isDisplayed())
      {
         view.showDialog();
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onStartClicked(CloudFoundryApplication app)
   {
      startAppPresenter.startApp(app.getName(), appInfoChangedCallback);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onStopClicked(CloudFoundryApplication app)
   {
      startAppPresenter.stopApp(app.getName(), appInfoChangedCallback);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onRestartClicked(CloudFoundryApplication app)
   {
      startAppPresenter.restartApp(app.getName(), appInfoChangedCallback);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onDeleteClicked(CloudFoundryApplication app)
   {
      deleteAppPresenter.deleteApp(currentServer, app.getName(), appInfoChangedCallback);
   }
}