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
package org.exoplatform.ide.extension.heroku.client.deploy;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.RequestStatusHandler;
import org.exoplatform.gwtframework.ui.client.component.GWTLoader;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.job.JobManager;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.paas.DeployResultHandler;
import org.exoplatform.ide.client.framework.paas.HasPaaSActions;
import org.exoplatform.ide.client.framework.project.ProjectType;
import org.exoplatform.ide.client.framework.template.ProjectTemplate;
import org.exoplatform.ide.client.framework.template.TemplateService;
import org.exoplatform.ide.client.framework.websocket.MessageBus.Channels;
import org.exoplatform.ide.client.framework.websocket.WebSocket;
import org.exoplatform.ide.client.framework.websocket.WebSocketEventHandler;
import org.exoplatform.ide.client.framework.websocket.WebSocketException;
import org.exoplatform.ide.client.framework.websocket.messages.WebSocketEventMessage;
import org.exoplatform.ide.extension.heroku.client.HerokuAsyncRequestCallback;
import org.exoplatform.ide.extension.heroku.client.HerokuClientService;
import org.exoplatform.ide.extension.heroku.client.HerokuExtension;
import org.exoplatform.ide.extension.heroku.client.HerokuLocalizationConstant;
import org.exoplatform.ide.extension.heroku.client.create.CreateRequestHandler;
import org.exoplatform.ide.extension.heroku.client.login.LoggedInEvent;
import org.exoplatform.ide.extension.heroku.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.heroku.client.login.LoginEvent;
import org.exoplatform.ide.extension.heroku.client.marshaller.Property;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.client.init.InitRequestStatusHandler;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ChildrenUnmarshaller;
import org.exoplatform.ide.vfs.client.marshal.ProjectUnmarshaller;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jul 26, 2012 5:41:46 PM anya $
 * 
 */
public class DeployApplicationPresenter implements HasPaaSActions, VfsChangedHandler, LoggedInHandler
{
   interface Display
   {
      HasValue<String> getApplicationNameField();

      HasValue<String> getRemoteNameField();

      Composite getView();
   }

   private static final HerokuLocalizationConstant lb = HerokuExtension.LOCALIZATION_CONSTANT;

   private VirtualFileSystemInfo vfs;

   private Display display;

   private ProjectModel project;

   private DeployResultHandler deployResultHandler;

   private String projectName;

   private RequestStatusHandler gitInitStatusHandler;

   private CreateRequestHandler appCreateRequestHandler;

   public DeployApplicationPresenter()
   {
      IDE.addHandler(VfsChangedEvent.TYPE, this);
   }

   public void bindDisplay()
   {
   }

   /**
    * Form the message about application creation to display in output.
    * 
    * @param properties application's properties
    * @return {@link String}
    */
   public String formApplicationCreatedMessage(List<Property> properties)
   {
      if (properties == null)
      {
         return HerokuExtension.LOCALIZATION_CONSTANT.createApplicationSuccess("");
      }
      StringBuilder message = new StringBuilder("<br> [");
      for (Property property : properties)
      {
         if ("webUrl".equals(property.getName()))
         {
            message.append("<b>").append(property.getName()).append("</b>").append(" : ").append("<a href='")
               .append(property.getValue()).append("' target='_blank'>").append(property.getValue()).append("</a>")
               .append("<br>");
         }
         else
         {
            message.append("<b>").append(property.getName()).append("</b>").append(" : ").append(property.getValue())
               .append("<br>");
         }
      }
      message.append("] ");
      return HerokuExtension.LOCALIZATION_CONSTANT.createApplicationSuccess(message.toString());
   }

   /**
    * Create new Heroku application.
    */
   private void createApplication()
   {
      String applicationName =
         (display.getApplicationNameField().getValue() == null || display.getApplicationNameField().getValue()
            .isEmpty()) ? null : display.getApplicationNameField().getValue();
      String remoteName =
         (display.getRemoteNameField().getValue() == null || display.getRemoteNameField().getValue().isEmpty()) ? null
            : display.getRemoteNameField().getValue();
      JobManager.get().showJobSeparated();
      try
      {
         boolean useWebSocketForCallback = false;
         final WebSocket ws = null;//WebSocket.getInstance(); TODO: temporary disable web-sockets
         if (ws != null && ws.getReadyState() == WebSocket.ReadyState.OPEN)
         {
            useWebSocketForCallback = true;
            appCreateRequestHandler = new CreateRequestHandler();
            appCreateRequestHandler.requestInProgress(project.getId());
            ws.messageBus().subscribe(Channels.HEROKU_APP_CREATED, appCreatedHandler);
         }
         final boolean useWebSocket = useWebSocketForCallback;

         HerokuClientService.getInstance().createApplication(applicationName, vfs.getId(), project.getId(), remoteName,
            useWebSocket, new HerokuAsyncRequestCallback(this)
            {

               @Override
               protected void onSuccess(List<Property> properties)
               {
                  IDE.fireEvent(new OutputEvent(formApplicationCreatedMessage(properties), Type.INFO));
                  IDE.fireEvent(new RefreshBrowserEvent(project));
                  deployResultHandler.onDeployFinished(true);
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  super.onFailure(exception);
                  deployResultHandler.onDeployFinished(false);
               }
            });
      }
      catch (RequestException e)
      {
         deployResultHandler.onDeployFinished(false);
      }
      catch (WebSocketException e)
      {
         deployResultHandler.onDeployFinished(false);
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework.application.event.VfsChangedEvent)
    */
   @Override
   public void onVfsChanged(VfsChangedEvent event)
   {
      this.vfs = event.getVfsInfo();
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.login.LoggedInHandler#onLoggedIn(org.exoplatform.ide.extension.heroku.client.login.LoggedInEvent)
    */
   @Override
   public void onLoggedIn(LoggedInEvent event)
   {
      IDE.removeHandler(LoggedInEvent.TYPE, this);
      createApplication();
   }

   /**
    * Initialize Git repository.
    * 
    * @param path working directory of the repository
    */
   private void initRepository(final ProjectModel project)
   {
      JobManager.get().showJobSeparated();
      try
      {
         boolean useWebSocketForCallback = false;
         final WebSocket ws = null;//WebSocket.getInstance(); TODO: temporary disable web-sockets
         if (ws != null && ws.getReadyState() == WebSocket.ReadyState.OPEN)
         {
            useWebSocketForCallback = true;
            gitInitStatusHandler = new InitRequestStatusHandler(project.getName());
            gitInitStatusHandler.requestInProgress(project.getId());
            ws.messageBus().subscribe(Channels.GIT_REPO_INITIALIZED, repoInitializedHandler);
         }
         final boolean useWebSocket = useWebSocketForCallback;

         GitClientService.getInstance().init(vfs.getId(), project.getId(), project.getName(), false, useWebSocket,
            new AsyncRequestCallback<String>()
            {
               @Override
               protected void onSuccess(String result)
               {
                  if (!useWebSocket)
                  {
                     createApplication();
                  }
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  handleGitError(exception);
                  if (useWebSocket)
                  {
                     ws.messageBus().unsubscribe(Channels.GIT_REPO_INITIALIZED, repoInitializedHandler);
                     gitInitStatusHandler.requestError(project.getId(), exception);
                  }
               }
            });
      }
      catch (RequestException e)
      {
         handleGitError(e);
      }
      catch (WebSocketException e)
      {
         e.printStackTrace();
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.paas.recent.HasPaaSActions#getDeployView(java.lang.String,
    *      org.exoplatform.ide.client.framework.project.ProjectType)
    */
   @Override
   public Composite getDeployView(String projectName, ProjectType projectType)
   {
      if (display == null)
      {
         display = GWT.create(Display.class);
         bindDisplay();
      }
      this.projectName = projectName;
      display.getApplicationNameField().setValue("");
      display.getRemoteNameField().setValue("");
      return display.getView();
   }

   /**
    * @see org.exoplatform.ide.client.framework.paas.recent.HasPaaSActions#deploy(org.exoplatform.ide.client.framework.template.ProjectTemplate,
    *      org.exoplatform.ide.client.framework.paas.recent.DeployResultHandler)
    */
   @Override
   public void deploy(ProjectTemplate projectTemplate, DeployResultHandler deployResultHandler)
   {
      this.deployResultHandler = deployResultHandler;
      createProject(projectTemplate);
   }

   /**
    * Create new project from pointed template.
    * 
    * @param projectTemplate
    */
   private void createProject(ProjectTemplate projectTemplate)
   {
      final Loader loader = new GWTLoader();
      loader.setMessage(lb.creatingProject());
      loader.show();
      try
      {
         TemplateService.getInstance().createProjectFromTemplate(vfs.getId(), vfs.getRoot().getId(), projectName,
            projectTemplate.getName(),
            new AsyncRequestCallback<ProjectModel>(new ProjectUnmarshaller(new ProjectModel()))
            {

               @Override
               protected void onSuccess(ProjectModel result)
               {
                  loader.hide();
                  project = result;
                  deployResultHandler.onProjectCreated(project);
                  initRepository(project);
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  loader.hide();
                  IDE.fireEvent(new ExceptionThrownEvent(exception));
               }
            });
      }
      catch (RequestException e)
      {
         loader.hide();
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.paas.recent.HasPaaSActions#deploy(org.exoplatform.ide.vfs.client.model.ProjectModel,
    *      org.exoplatform.ide.client.framework.paas.recent.DeployResultHandler)
    */
   @Override
   public void deploy(ProjectModel project, DeployResultHandler deployResultHandler)
   {
      this.project = project;
      this.deployResultHandler = deployResultHandler;
      checkIsGitRepository(project);
   }

   private void checkIsGitRepository(final ProjectModel project)
   {
      try
      {
         VirtualFileSystem.getInstance().getChildren(project,
            new AsyncRequestCallback<List<Item>>(new ChildrenUnmarshaller(new ArrayList<Item>()))
            {

               @Override
               protected void onSuccess(List<Item> result)
               {
                  for (Item item : result)
                  {
                     if (".git".equals(item.getName()))
                     {
                        createApplication();
                        return;
                     }
                  }
                  initRepository(project);
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  initRepository(project);
               }
            });
      }
      catch (RequestException e)
      {
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.paas.HasPaaSActions#validate()
    */
   @Override
   public boolean validate()
   {
      return true;
   }

   private void handleGitError(Throwable e)
   {
      String errorMessage =
         (e.getMessage() != null && e.getMessage().length() > 0) ? e.getMessage() : GitExtension.MESSAGES.initFailed();
      IDE.fireEvent(new OutputEvent(errorMessage, Type.ERROR));
   }

   /**
    * Performs actions after the Git-repository was initialized.
    */
   private WebSocketEventHandler repoInitializedHandler = new WebSocketEventHandler()
   {
      @Override
      public void onMessage(WebSocketEventMessage event)
      {
         WebSocket.getInstance().messageBus().unsubscribe(Channels.GIT_REPO_INITIALIZED, this);

         gitInitStatusHandler.requestFinished(project.getId());
         createApplication();
      }

      @Override
      public void onError(Exception exception)
      {
         WebSocket.getInstance().messageBus().unsubscribe(Channels.GIT_REPO_INITIALIZED, this);

         gitInitStatusHandler.requestError(project.getId(), exception);
         handleGitError(exception);
      }
   };

   /**
    * Performs actions after the application was created.
    */
   private WebSocketEventHandler appCreatedHandler = new WebSocketEventHandler()
   {
      @Override
      public void onMessage(WebSocketEventMessage event)
      {
         WebSocket.getInstance().messageBus().unsubscribe(Channels.HEROKU_APP_CREATED, this);

         List<Property> properties = parseApplicationProperties(event.getPayload().getPayload());
         if (properties != null)
         {
            IDE.fireEvent(new OutputEvent(formApplicationCreatedMessage(properties), Type.INFO));
            IDE.fireEvent(new RefreshBrowserEvent(project));
            deployResultHandler.onDeployFinished(true);
         }
      }

      @Override
      public void onError(Exception exception)
      {
         WebSocket.getInstance().messageBus().unsubscribe(Channels.HEROKU_APP_CREATED, this);

         if (exception.getMessage() != null && !exception.getMessage().isEmpty())
         {
            if (exception.getMessage().contains("Authentication required"))
            {
               IDE.addHandler(LoggedInEvent.TYPE, DeployApplicationPresenter.this);
               IDE.fireEvent(new LoginEvent());
               return;
            }

            IDE.fireEvent(new OutputEvent(exception.getMessage(), Type.ERROR));
         }
      }
   };

   /**
    * Deserializes data in JSON format to List that contain application properties.
    * 
    * @param jsonData data in JSON format
    */
   private List<Property> parseApplicationProperties(String jsonData)
   {
      if (jsonData == null || jsonData.isEmpty())
      {
         return null;
      }

      JSONValue json = JSONParser.parseStrict(jsonData);
      if (json == null)
      {
         return null;
      }
      JSONObject jsonObject = json.isObject();
      if (jsonObject == null)
      {
         return null;
      }

      List<Property> properties = new ArrayList<Property>();
      for (String key : jsonObject.keySet())
      {
         if (jsonObject.get(key).isString() != null)
         {
            String value = jsonObject.get(key).isString().stringValue();
            properties.add(new Property(key, value));
         }
      }
      return properties;
   }
}
