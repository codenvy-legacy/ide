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

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.console.Console;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryClientService;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryExtension;
import com.codenvy.ide.extension.cloudfoundry.client.delete.ApplicationDeletedEvent;
import com.codenvy.ide.extension.cloudfoundry.client.delete.ApplicationDeletedHandler;
import com.codenvy.ide.extension.cloudfoundry.client.delete.DeleteApplicationEvent;
import com.codenvy.ide.extension.cloudfoundry.client.delete.DeleteApplicationPresenter;
import com.codenvy.ide.extension.cloudfoundry.client.login.LoggedInHandler;
import com.codenvy.ide.extension.cloudfoundry.client.marshaller.ApplicationListUnmarshaller;
import com.codenvy.ide.extension.cloudfoundry.client.marshaller.TargetsUnmarshaller;
import com.codenvy.ide.extension.cloudfoundry.client.project.ApplicationInfoChangedEvent;
import com.codenvy.ide.extension.cloudfoundry.client.project.ApplicationInfoChangedHandler;
import com.codenvy.ide.extension.cloudfoundry.client.start.RestartApplicationEvent;
import com.codenvy.ide.extension.cloudfoundry.client.start.StartApplicationEvent;
import com.codenvy.ide.extension.cloudfoundry.client.start.StartApplicationPresenter;
import com.codenvy.ide.extension.cloudfoundry.client.start.StopApplicationEvent;
import com.codenvy.ide.extension.cloudfoundry.shared.CloudFoundryApplication;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
public class ApplicationsPresenter implements ApplicationsView.ActionDelegate, ApplicationInfoChangedHandler,
   ApplicationDeletedHandler
// implements ViewClosedHandler, ShowApplicationsHandler, ApplicationInfoChangedHandler
{
   private ApplicationsView view;

   private String currentServer;

   private List<String> servers = new ArrayList<String>();

   private EventBus eventBus;

   private Console console;

   private ResourceProvider resourceProvider;

   @Inject
   protected ApplicationsPresenter(ApplicationsView view, EventBus eventBus, Console console,
      ResourceProvider resourceProvider, StartApplicationPresenter startAppPresenter,
      DeleteApplicationPresenter deleteAppPresenter)
   {
      this.view = view;
      this.view.setDelegate(this);
      this.eventBus = eventBus;
      this.console = console;
      this.resourceProvider = resourceProvider;

      this.eventBus.addHandler(ApplicationInfoChangedEvent.TYPE, this);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void doClose()
   {
      view.close();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void doShow()
   {
      checkLogginedToServer();
   }

   private void getApplicationList()
   {
      try
      {
         CloudFoundryClientService.getInstance().getApplicationList(
            currentServer,
            new CloudFoundryAsyncRequestCallback<List<CloudFoundryApplication>>(new ApplicationListUnmarshaller(
               new ArrayList<CloudFoundryApplication>()), new LoggedInHandler()
            {
               @Override
               public void onLoggedIn()
               {
                  getApplicationList();
               }
            }, null, currentServer, eventBus)
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
         // TODO
         //         eventBus.fireEvent(new ExceptionThrownEvent(e));
         console.print(e.getMessage());
      }
   }

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
                  // TODO
                  //                  eventBus.fireEvent(new ExceptionThrownEvent(exception));
                  console.print(exception.getMessage());
               }
            });
      }
      catch (RequestException e)
      {
         // TODO
         //         eventBus.fireEvent(new ExceptionThrownEvent(e));
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
                  // TODO
                  //                  IDE.fireEvent(new ExceptionThrownEvent(exception));
                  console.print(exception.getMessage());
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

   private void openView()
   {
      view.setServers(servers);
      // fill the list of applications
      currentServer = servers.get(0);
      getApplicationList();

      view.showDialog();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void doStartApplication(CloudFoundryApplication app)
   {
      // TODO Auto-generated method stub
      //      IDE.fireEvent(new StartApplicationEvent(event.getSelectedItem().getName()));
      eventBus.fireEvent(new StartApplicationEvent(app.getName()));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void doStopApplication(CloudFoundryApplication app)
   {
      // TODO Auto-generated method stub
      //      IDE.fireEvent(new StopApplicationEvent(event.getSelectedItem().getName()));
      eventBus.fireEvent(new StopApplicationEvent(app.getName()));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void doRestartApplication(CloudFoundryApplication app)
   {
      // TODO Auto-generated method stub
      //      IDE.fireEvent(new RestartApplicationEvent(event.getSelectedItem().getName()));
      eventBus.fireEvent(new RestartApplicationEvent(app.getName()));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void doDeleteApplication(CloudFoundryApplication app)
   {
      // TODO Auto-generated method stub
      //      IDE.fireEvent(new DeleteApplicationEvent(event.getSelectedItem().getName(), currentServer));
      eventBus.fireEvent(new DeleteApplicationEvent(app.getName(), currentServer));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onApplicationInfoChanged(ApplicationInfoChangedEvent event)
   {
      getApplicationList();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onApplicationDeleted(ApplicationDeletedEvent event)
   {
      getApplicationList();
   }
}