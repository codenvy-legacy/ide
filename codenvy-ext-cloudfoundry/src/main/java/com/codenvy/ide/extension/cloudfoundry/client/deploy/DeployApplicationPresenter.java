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
package com.codenvy.ide.extension.cloudfoundry.client.deploy;

import com.codenvy.ide.api.parts.ConsolePart;

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.paas.DeployResultHandler;
import com.codenvy.ide.api.ui.paas.HasPaaSActions;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.core.event.RefreshBrowserEvent;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryAutoBeanFactory;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryClientService;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryExtension;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryLocalizationConstant;
import com.codenvy.ide.extension.cloudfoundry.client.login.LoggedInHandler;
import com.codenvy.ide.extension.cloudfoundry.client.login.LoginPresenter;
import com.codenvy.ide.extension.cloudfoundry.client.marshaller.TargetsUnmarshaller;
import com.codenvy.ide.extension.cloudfoundry.shared.CloudFoundryApplication;
import com.codenvy.ide.extension.maven.client.event.BuildProjectEvent;
import com.codenvy.ide.extension.maven.client.event.ProjectBuiltEvent;
import com.codenvy.ide.extension.maven.client.event.ProjectBuiltHandler;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Resource;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.AutoBeanUnmarshaller;
import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Presenter for deploying application on CloudFoundry.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: DeployApplicationPresenter.java Dec 2, 2011 10:17:23 AM vereshchaka $
 */
@Singleton
public class DeployApplicationPresenter implements DeployApplicationView.ActionDelegate, HasPaaSActions,
   ProjectBuiltHandler
{
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

   private Project project;

   private DeployResultHandler deployResultHandler;

   private ResourceProvider resourcesProvider;

   private ConsolePart console;

   private CloudFoundryLocalizationConstant constant;

   private CloudFoundryAutoBeanFactory autoBeanFactory;

   private HandlerRegistration projectBuildHandler;

   private LoginPresenter loginPresenter;

   /**
    * Create presenter.
    * 
    * @param view
    * @param eventBus
    * @param resourcesProvider
    * @param console
    * @param constant
    * @param autoBeanFactory
    * @param loginPresenter
    */
   @Inject
   protected DeployApplicationPresenter(DeployApplicationView view, EventBus eventBus,
      ResourceProvider resourcesProvider, ConsolePart console, CloudFoundryLocalizationConstant constant,
      CloudFoundryAutoBeanFactory autoBeanFactory, LoginPresenter loginPresenter)
   {
      this.view = view;
      this.view.setDelegate(this);
      this.eventBus = eventBus;
      this.resourcesProvider = resourcesProvider;
      this.console = console;
      this.constant = constant;
      this.autoBeanFactory = autoBeanFactory;
      this.loginPresenter = loginPresenter;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onNameChanged()
   {
      name = view.getName();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onUrlChanged()
   {
      url = view.getUrl();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onServerChanged()
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

      // TODO Need to create some special service after this class
      // This class still doesn't have analog.
      //      JobManager.get().showJobSeparated();

      // TODO This code uses WebSocket but we have not ported it yet.
      //      AutoBean<CloudFoundryApplication> cloudFoundryApplication = autoBeanFactory.cloudFoundryApplication();
      //      AutoBeanUnmarshallerWS<CloudFoundryApplication> unmarshaller =
      //         new AutoBeanUnmarshallerWS<CloudFoundryApplication>(cloudFoundryApplication);
      //
      //      try
      //      {
      //         // Application will be started after creation (IDE-1618)
      //         boolean noStart = false;
      //         CloudFoundryClientService.getInstance().createWS(
      //            server,
      //            name,
      //            null,
      //            url,
      //            0,
      //            0,
      //            noStart,
      //            resourcesProvider.getVfsId(),
      //            resourcesProvider.getActiveProject().getId(),
      //            warUrl,
      //            new CloudFoundryRESTfulRequestCallback<CloudFoundryApplication>(unmarshaller, loggedInHandler, null,
      //               server, eventBus)
      //            {
      //               @Override
      //               protected void onSuccess(CloudFoundryApplication result)
      //               {
      //                  onAppCreatedSuccess(result);
      //               }
      //
      //               @Override
      //               protected void onFailure(Throwable exception)
      //               {
      //                  // TODO
      //                  //                  deployResultHandler.onDeployFinished(false);
      //                  console.print(constant.applicationCreationFailed());
      //                  super.onFailure(exception);
      //               }
      //            });
      //      }
      //      catch (WebSocketException e)
      //      {
      //         createApplicationREST(loggedInHandler);
      //      }
      createApplicationREST(loggedInHandler);
   }

   /**
    * Create application on CloudFoundry by sending request over HTTP.
    * 
    * @param loggedInHandler handler that should be called after success login
    */
   private void createApplicationREST(LoggedInHandler loggedInHandler)
   {
      AutoBean<CloudFoundryApplication> cloudFoundryApplication = autoBeanFactory.cloudFoundryApplication();
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
            resourcesProvider.getActiveProject().getId(),
            warUrl,
            new CloudFoundryAsyncRequestCallback<CloudFoundryApplication>(unmarshaller, loggedInHandler, null, server,
               eventBus, console, constant, loginPresenter)
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
                  console.print(constant.applicationCreationFailed());
                  super.onFailure(exception);
               }
            });
      }
      catch (RequestException e)
      {
         // TODO
         //         deployResultHandler.onDeployFinished(false);
         eventBus.fireEvent(new ExceptionThrownEvent(e));
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
      String msg = constant.applicationCreatedSuccessfully(app.getName());
      if ("STARTED".equals(app.getState()))
      {
         if (app.getUris().isEmpty())
         {
            msg += "<br>" + constant.applicationStartedWithNoUrls();
         }
         else
         {
            msg += "<br>" + constant.applicationStartedOnUrls(app.getName(), getAppUrlsAsString(app));
         }
      }
      // TODO
      //      deployResultHandler.onDeployFinished(true);
      console.print(msg);
      eventBus.fireEvent(new RefreshBrowserEvent(project));
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

   // TODO This method will use when shows view
   /**
    * Get the list of server and put them to select field.
    */
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
                     if (result.isEmpty())
                     {
                        JsonArray<String> servers = JsonCollections.createArray();
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
    * Validate action before build project.
    */
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
         CloudFoundryClientService.getInstance().validateAction(
            "create",
            server,
            name,
            null,
            url,
            resourcesProvider.getVfsId(),
            null,
            0,
            0,
            true,
            new CloudFoundryAsyncRequestCallback<String>(null, validateHandler, null, server, eventBus, console,
               constant, loginPresenter)
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
         eventBus.fireEvent(new ExceptionThrownEvent(e));
         console.print(e.getMessage());
      }
   }

   /**
    * Check current project is maven project. 
    */
   private void beforeDeploy()
   {
      JsonArray<Resource> children = project.getChildren();

      for (int i = 0; i < children.size(); i++)
      {
         Resource child = children.get(i);
         if (child.isFile() && "pom.xml".equals(child.getName()))
         {
            buildApplication();
            return;
         }
      }

      createApplication();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void deploy(Project project, DeployResultHandler deployResultHandler)
   {
      this.project = project;
      this.deployResultHandler = deployResultHandler;
      buildApplication();
   }

   /**
    * Builds application.
    */
   private void buildApplication()
   {
      // TODO IDEX-57
      // Replace EventBus Events with direct method calls and DI
      projectBuildHandler = eventBus.addHandler(ProjectBuiltEvent.TYPE, this);
      eventBus.fireEvent(new BuildProjectEvent(project));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean validate()
   {
      return view.getName() != null && !view.getName().isEmpty() && view.getUrl() != null && !view.getUrl().isEmpty();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onProjectBuilt(ProjectBuiltEvent event)
   {
      projectBuildHandler.removeHandler();
      if (event.getBuildStatus().getDownloadUrl() != null)
      {
         warUrl = event.getBuildStatus().getDownloadUrl();
         createApplication();
      }
   }
}