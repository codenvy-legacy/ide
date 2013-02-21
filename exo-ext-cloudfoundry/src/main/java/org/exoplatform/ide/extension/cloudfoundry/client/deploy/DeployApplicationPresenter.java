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
package org.exoplatform.ide.extension.cloudfoundry.client.deploy;

import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.event.shared.EventBus;

import org.exoplatform.ide.api.resources.ResourceProvider;
import org.exoplatform.ide.api.ui.console.Console;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryLocalizationConstant;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryRESTfulRequestCallback;
import org.exoplatform.ide.extension.cloudfoundry.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.cloudfoundry.client.marshaller.TargetsUnmarshaller;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudFoundryApplication;
import org.exoplatform.ide.rest.AsyncRequestCallback;
import org.exoplatform.ide.rest.AutoBeanUnmarshaller;
import org.exoplatform.ide.websocket.WebSocketException;
import org.exoplatform.ide.websocket.rest.AutoBeanUnmarshallerWS;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class DeployApplicationPresenter implements DeployApplicationView.ActionDelegate
//implements ProjectBuiltHandler, HasPaaSActions, VfsChangedHandler
{
   private static final CloudFoundryLocalizationConstant lb = CloudFoundryExtension.LOCALIZATION_CONSTANT;

   private DeployApplicationView view;

   private EventBus eventBus;

   private String server;

   private String name;

   private String url;

   /**
    * Public url to war file of application.
    */
   private String warUrl;

   private String projectName;

   private ResourceProvider resourcesProvider;

   private Console console;

   // TODO
   //   private DeployResultHandler deployResultHandler;

   @Inject
   protected DeployApplicationPresenter(DeployApplicationView view, EventBus eventBus,
      ResourceProvider resourcesProvider, Console console)
   {
      this.view = view;
      this.view.setDelegate(this);
      this.eventBus = eventBus;
      this.resourcesProvider = resourcesProvider;
      this.console = console;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onNameFieldChanged()
   {
      // TODO this method definitely don't need. should delete it...
      name = view.getName();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onUrlFieldChanged()
   {
      // TODO this method definitely don't need. should delete it...
      url = view.getUrl();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onServerFieldChanged()
   {
      server = view.getServer();
      // if url set automatically, than try to create url using server and name
      String target = view.getServer();
      String sufix = target.substring(target.indexOf("."));
      String oldUrl = view.getUrl();
      String prefix = "<name>";
      if (!oldUrl.isEmpty() && oldUrl.contains("."))
      {
         prefix = oldUrl.substring(0, oldUrl.indexOf("."));
      }
      String url = prefix + sufix;
      view.setUrl(url);
   }

   /**
    * Create application on CloudFoundry by sending request over WebSocket or HTTP.
    */
   private void createApplication()
   {
      LoggedInHandler loggedInHandler = new LoggedInHandler()
      {
         @Override
         public void onLoggedIn()
         {
            createApplication();
         }
      };
      // TODO
      //      JobManager.get().showJobSeparated();
      AutoBean<CloudFoundryApplication> cloudFoundryApplication =
         CloudFoundryExtension.AUTO_BEAN_FACTORY.cloudFoundryApplication();
      AutoBeanUnmarshallerWS<CloudFoundryApplication> unmarshaller =
         new AutoBeanUnmarshallerWS<CloudFoundryApplication>(cloudFoundryApplication);

      try
      {
         // Application will be started after creation (IDE-1618)
         boolean noStart = false;
         CloudFoundryClientService.getInstance().createWS(
            server,
            name,
            null,
            url,
            0,
            0,
            noStart,
            resourcesProvider.getVfsId(),
            // TODO
            resourcesProvider.getActiveProject().getId(),
            warUrl,
            new CloudFoundryRESTfulRequestCallback<CloudFoundryApplication>(unmarshaller, loggedInHandler, null,
               server, eventBus)
            {
               @Override
               protected void onSuccess(CloudFoundryApplication result)
               {
                  onAppCreatedSuccess(result);
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  // TODO
                  //                  deployResultHandler.onDeployFinished(false);
                  // TODO
                  //                     IDE.fireEvent(new OutputEvent(lb.applicationCreationFailed(), OutputMessage.Type.INFO));
                  console.print(lb.applicationCreationFailed());
                  super.onFailure(exception);
               }
            });
      }
      catch (WebSocketException e)
      {
         createApplicationREST(loggedInHandler);
      }
   }

   /**
    * Create application on CloudFoundry by sending request over HTTP.
    * 
    * @param loggedInHandler handler that should be called after success login
    */
   private void createApplicationREST(LoggedInHandler loggedInHandler)
   {
      AutoBean<CloudFoundryApplication> cloudFoundryApplication =
         CloudFoundryExtension.AUTO_BEAN_FACTORY.cloudFoundryApplication();
      AutoBeanUnmarshaller<CloudFoundryApplication> unmarshaller =
         new AutoBeanUnmarshaller<CloudFoundryApplication>(cloudFoundryApplication);

      try
      {
         // Application will be started after creation (IDE-1618)
         boolean noStart = false;
         CloudFoundryClientService.getInstance().create(
            server,
            name,
            null,
            url,
            0,
            0,
            noStart,
            resourcesProvider.getVfsId(),
            // TODO
            //            project.getId(),
            resourcesProvider.getActiveProject().getId(),
            warUrl,
            new CloudFoundryAsyncRequestCallback<CloudFoundryApplication>(unmarshaller, loggedInHandler, null, server,
               eventBus)
            {
               @Override
               protected void onSuccess(CloudFoundryApplication result)
               {
                  onAppCreatedSuccess(result);
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  // TODO
                  //                  deployResultHandler.onDeployFinished(false);
                  // TODO
                  //                  IDE.fireEvent(new OutputEvent(lb.applicationCreationFailed(), OutputMessage.Type.INFO));
                  console.print(lb.applicationCreationFailed());
                  super.onFailure(exception);
               }
            });
      }
      catch (RequestException e)
      {
         // TODO
         //         deployResultHandler.onDeployFinished(false);
         // TODO
         //         IDE.fireEvent(new ExceptionThrownEvent(e));
         console.print(e.getMessage());
      }
   }

   /**
    * Performs action when application successfully created.
    * 
    * @param app @link CloudFoundryApplication} which is created
    */
   private void onAppCreatedSuccess(CloudFoundryApplication app)
   {
      warUrl = null;
      String msg = lb.applicationCreatedSuccessfully(app.getName());
      if ("STARTED".equals(app.getState()))
      {
         if (app.getUris().isEmpty())
         {
            msg += "<br>" + lb.applicationStartedWithNoUrls();
         }
         else
         {
            msg += "<br>" + lb.applicationStartedOnUrls(app.getName(), getAppUrlsAsString(app));
         }
      }
      // TODO
      //      deployResultHandler.onDeployFinished(true);
      // TODO
      //      IDE.fireEvent(new OutputEvent(msg, OutputMessage.Type.INFO));
      console.print(msg);
      // TODO
      //      IDE.fireEvent(new RefreshBrowserEvent(project));
      //      eventBus.fireEvent(new RefreshBrowserEvent(project));
   }

   /**
    * Returns application URLs as string.
    * 
    * @param application {@link CloudFoundryApplication Cloud Foundry application}
    * @return application URLs
    */
   private String getAppUrlsAsString(CloudFoundryApplication application)
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
    * Get the list of server and put them to select field.
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
                  if (result.isEmpty())
                  {
                     List<String> servers = new ArrayList<String>();
                     servers.add(CloudFoundryExtension.DEFAULT_SERVER);
                     view.setServerValues(servers);
                     view.setServer(CloudFoundryExtension.DEFAULT_SERVER);
                  }
                  else
                  {
                     view.setServerValues(result);
                     view.setServer(result.get(0));
                  }
                  view.setName(projectName);
                  // don't forget to init values, that are stored, when
                  // values in form fields are changed.
                  name = projectName;
                  server = view.getServer();
                  String urlSufix = server.substring(server.indexOf("."));
                  view.setUrl(name + urlSufix);
                  url = view.getUrl();
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
         //         IDE.fireEvent(new ExceptionThrownEvent(e));
         console.print(e.getMessage());
      }
   }

   public void performValidation()
   {
      LoggedInHandler validateHandler = new LoggedInHandler()
      {
         @Override
         public void onLoggedIn()
         {
            performValidation();
         }
      };

      try
      {
         CloudFoundryClientService.getInstance().validateAction("create", server, name, null, url,
            resourcesProvider.getVfsId(), null, 0, 0, true,
            new CloudFoundryAsyncRequestCallback<String>(null, validateHandler, null, server, eventBus)
            {
               @Override
               protected void onSuccess(String result)
               {
                  beforeDeploy();
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

   private void beforeDeploy()
   {
      // TODO Unmarshaller
      //      try
      //      {
      //         VirtualFileSystem.getInstance().getChildren(project,
      //            new AsyncRequestCallback<List<Item>>(new ChildrenUnmarshaller(new ArrayList<Item>()))
      //            {
      //
      //               @Override
      //               protected void onSuccess(List<Item> result)
      //               {
      //                  project.getChildren().setItems(result);
      //                  for (Item i : result)
      //                  {
      //                     if (i.getItemType() == ItemType.FILE && "pom.xml".equals(i.getName()))
      //                     {
      //                        buildApplication();
      //                        return;
      //                     }
      //                  }
      //                  createApplication();
      //               }
      //
      //               @Override
      //               protected void onFailure(Throwable exception)
      //               {
      //                  IDE.fireEvent(new ExceptionThrownEvent(exception, "Can't receive project children "
      //                     + project.getName()));
      //               }
      //            });
      //      }
      //      catch (RequestException e)
      //      {
      //         // TODO
      //         //         IDE.fireEvent(new ExceptionThrownEvent(e));
      //         console.print(e.getMessage());
      //      }
   }
}