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
package org.exoplatform.ide.extension.cloudfoundry.client.apps;

import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import org.exoplatform.ide.commons.exception.ExceptionThrownEvent;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService;
import org.exoplatform.ide.extension.cloudfoundry.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.cloudfoundry.client.marshaller.ApplicationListUnmarshaller;
import org.exoplatform.ide.extension.cloudfoundry.client.marshaller.TargetsUnmarshaller;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudFoundryApplication;
import org.exoplatform.ide.json.JsonArray;
import org.exoplatform.ide.json.JsonCollections;
import org.exoplatform.ide.rest.AsyncRequestCallback;

/**
 * 
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
public class ApplicationsPresenter implements ApplicationsView.ActionDelegate
// implements ViewClosedHandler, ShowApplicationsHandler, ApplicationDeletedHandler, ApplicationInfoChangedHandler
{
   private ApplicationsView view;

   private String currentServer;

   private JsonArray<String> servers = JsonCollections.createArray();

   private EventBus eventBus;

   @Inject
   public ApplicationsPresenter(EventBus eventBus)
   {
      this(new ApplicationsViewImpl(), eventBus);
   }

   protected ApplicationsPresenter(ApplicationsView view, EventBus eventBus)
   {
      this.view = view;
      this.view.setDelegate(this);
      this.eventBus = eventBus;
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
      currentServer = view.getServer();
      getApplicationList();
   }

   private void getApplicationList()
   {
      try
      {
         CloudFoundryClientService.getInstance().getApplicationList(
            currentServer,
            new CloudFoundryAsyncRequestCallback<JsonArray<CloudFoundryApplication>>(new ApplicationListUnmarshaller(
               JsonCollections.<CloudFoundryApplication> createArray()), new LoggedInHandler()
            {
               @Override
               public void onLoggedIn()
               {
                  getApplicationList();
               }
            }, null, currentServer, eventBus)
            {

               @Override
               protected void onSuccess(JsonArray<CloudFoundryApplication> result)
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
      }
   }

   private void getServers()
   {
      try
      {
         CloudFoundryClientService.getInstance()
            .getTargets(
               new AsyncRequestCallback<JsonArray<String>>(new TargetsUnmarshaller(JsonCollections
                  .<String> createArray()))
               {
                  @Override
                  protected void onSuccess(JsonArray<String> result)
                  {
                     servers = result;
                     view.setServers(servers);
                  }

                  @Override
                  protected void onFailure(Throwable exception)
                  {
                     eventBus.fireEvent(new ExceptionThrownEvent(exception));
                  }
               });
      }
      catch (RequestException e)
      {
         eventBus.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   /**
    * Show dialog.
    */
   public void showDialog()
   {
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
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void doStopApplication(CloudFoundryApplication app)
   {
      // TODO Auto-generated method stub
      //      IDE.fireEvent(new StopApplicationEvent(event.getSelectedItem().getName()));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void doRestartApplication(CloudFoundryApplication app)
   {
      // TODO Auto-generated method stub
      //      IDE.fireEvent(new RestartApplicationEvent(event.getSelectedItem().getName()));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void doDeleteApplication(CloudFoundryApplication app)
   {
      // TODO Auto-generated method stub
      //      IDE.fireEvent(new DeleteApplicationEvent(event.getSelectedItem().getName(), currentServer));
   }
}