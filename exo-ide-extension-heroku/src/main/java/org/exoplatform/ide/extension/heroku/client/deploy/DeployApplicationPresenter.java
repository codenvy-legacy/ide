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
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.RequestStatusHandler;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.paas.Paas;
import org.exoplatform.ide.client.framework.paas.PaasCallback;
import org.exoplatform.ide.client.framework.paas.PaasComponent;
import org.exoplatform.ide.client.framework.util.ProjectResolver;
import org.exoplatform.ide.client.framework.websocket.WebSocket;
import org.exoplatform.ide.client.framework.websocket.WebSocketExceptionMessage;
import org.exoplatform.ide.client.framework.websocket.WebSocketMessage;
import org.exoplatform.ide.client.framework.websocket.event.Subscriber;
import org.exoplatform.ide.extension.heroku.client.HerokuAsyncRequestCallback;
import org.exoplatform.ide.extension.heroku.client.HerokuClientService;
import org.exoplatform.ide.extension.heroku.client.HerokuExtension;
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
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: DeployApplicationPresenter.java Dec 5, 2011 1:58:22 PM vereshchaka $
 * 
 */
public class DeployApplicationPresenter implements PaasComponent, VfsChangedHandler, LoggedInHandler, Subscriber
{
   interface Display
   {
      HasValue<String> getApplicationNameField();

      HasValue<String> getRemoteNameField();

      Composite getView();

   }

   private VirtualFileSystemInfo vfs;

   private Display display;

   private PaasCallback paasCallback;

   private ProjectModel project;

   private String applicationName;

   private String remoteName;

   private RequestStatusHandler gitInitStatusHandler;

   private CreateRequestHandler appCreateRequestHandler;

   public DeployApplicationPresenter()
   {
      IDE.addHandler(VfsChangedEvent.TYPE, this);

      IDE.getInstance().addPaas(new Paas("Heroku", this, Arrays.asList(ProjectResolver.RAILS)));
   }

   public void bindDisplay()
   {

      display.getApplicationNameField().addValueChangeHandler(new ValueChangeHandler<String>()
      {

         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            if (event.getValue().isEmpty())
            {
               applicationName = null;
            }
            else
            {
               applicationName = event.getValue();
            }
         }
      });

      display.getRemoteNameField().addValueChangeHandler(new ValueChangeHandler<String>()
      {

         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            if (event.getValue().isEmpty())
            {
               remoteName = null;
            }
            else
            {
               remoteName = event.getValue();
            }
         }
      });

   }

   // ----Implementation------------------------

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

   private void createApplication()
   {
      try
      {
         boolean useWebSocketForCallback = false;
         final WebSocket ws = WebSocket.getInstance();
         if (ws != null && ws.getReadyState() == WebSocket.ReadyState.OPEN)
         {
            useWebSocketForCallback = true;
            appCreateRequestHandler = new CreateRequestHandler();
            appCreateRequestHandler.requestInProgress(project.getId());
            ws.subscribe("herokuAppCreated", this);
         }
         final boolean useWebSocket = useWebSocketForCallback;

         HerokuClientService.getInstance().createApplication(applicationName, vfs.getId(), project.getId(), remoteName,
            useWebSocket, new HerokuAsyncRequestCallback(this)
            {

               @Override
               protected void onSuccess(List<Property> properties)
               {
                  if (!useWebSocket)
                  {
                     IDE.fireEvent(new OutputEvent(formApplicationCreatedMessage(properties), Type.INFO));
                     IDE.fireEvent(new RefreshBrowserEvent(project));
                     paasCallback.onDeploy(true);
                  }
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  super.onFailure(exception);
                  paasCallback.onDeploy(false);
                  if (useWebSocket)
                  {
                     ws.unsubscribe("herokuAppCreated", DeployApplicationPresenter.this);
                  }
               }
            });
      }
      catch (RequestException e)
      {
         paasCallback.onDeploy(false);
      }

   }

   /**
    * @see org.exoplatform.ide.client.framework.paas.PaasComponent#getView()
    */
   @Override
   public void getView(String projectName, PaasCallback paasCallback)
   {
      this.paasCallback = paasCallback;
      if (display == null)
      {
         display = GWT.create(Display.class);
      }
      bindDisplay();
      // clear values
      display.getApplicationNameField().setValue("");
      display.getRemoteNameField().setValue("");
      applicationName = null;
      remoteName = null;
      this.paasCallback.onViewReceived(display.getView());
   }

   /**
    * @see org.exoplatform.ide.client.framework.paas.PaasComponent#validate()
    */
   @Override
   public void validate()
   {
      paasCallback.onValidate(true);
   }

   /**
    * @see org.exoplatform.ide.client.framework.paas.PaasComponent#deploy()
    */
   @Override
   public void deploy(ProjectModel project)
   {
      this.project = project;

      checkIsGitRepository(project);
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
                        // beforeBuild();
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
    * Initialize Git repository.
    * 
    * @param path working directory of the repository
    */
   private void initRepository(final ProjectModel project)
   {
      try
      {
         boolean useWebSocketForCallback = false;
         final WebSocket ws = WebSocket.getInstance();
         if (ws != null && ws.getReadyState() == WebSocket.ReadyState.OPEN)
         {
            useWebSocketForCallback = true;
            gitInitStatusHandler = new InitRequestStatusHandler(project.getName());
            gitInitStatusHandler.requestInProgress(project.getId());
            ws.subscribe("gitRepoInitialized", DeployApplicationPresenter.this);
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
                  // showBuildMessage(GitExtension.MESSAGES.initSuccess());
                  // IDE.fireEvent(new RefreshBrowserEvent());
                  // createJob();
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  String errorMessage =
                     (exception.getMessage() != null && exception.getMessage().length() > 0) ? exception.getMessage()
                        : GitExtension.MESSAGES.initFailed();
                  IDE.fireEvent(new OutputEvent(errorMessage, Type.ERROR));
                  if (useWebSocket)
                  {
                     ws.unsubscribe("gitRepoInitialized", DeployApplicationPresenter.this);
                  }
               }
            });
      }
      catch (RequestException e)
      {
         String errorMessage =
            (e.getMessage() != null && e.getMessage().length() > 0) ? e.getMessage() : GitExtension.MESSAGES
               .initFailed();
         IDE.fireEvent(new OutputEvent(errorMessage, Type.ERROR));
      }
   }

   @Override
   public void createProject(ProjectModel project)
   {
   }

   /**
    * @see org.exoplatform.ide.client.framework.websocket.event.Subscriber#onMessage(org.exoplatform.ide.client.framework.websocket.WebSocketMessage)
    */
   @Override
   public void onMessage(WebSocketMessage webSocketMessage)
   {
      if (webSocketMessage.getEvent().equals("gitRepoInitialized:" + project.getId()))
      {
         onGitRepoInitialized(webSocketMessage);
      }
      else if (webSocketMessage.getEvent().equals("herokuAppCreated"))
      {
         onHerokuAppCreated(webSocketMessage);
      }
   }

   /**
    * Performs actions when Git-repository initialized.
    * 
    * @param webSocketMessage WebSocket message
    */
   private void onGitRepoInitialized(WebSocketMessage webSocketMessage)
   {
      WebSocket.getInstance().subscribe("gitRepoInitialized", this);

      if (!project.getId().equals(webSocketMessage.getData().asString()))
      {
         return;
      }

      WebSocketExceptionMessage webSocketException = webSocketMessage.getException();
      if (webSocketException == null)
      {
         gitInitStatusHandler.requestFinished(project.getId());
         createApplication();
         return;
      }

      String exceptionMessage = null;
      if (webSocketException.getMessage() != null && webSocketException.getMessage().length() > 0)
      {
         exceptionMessage = webSocketException.getMessage();
      }

      gitInitStatusHandler.requestError(project.getId(), new Exception(exceptionMessage));
      String errorMessage = (exceptionMessage != null) ? exceptionMessage : GitExtension.MESSAGES.initFailed();
      IDE.fireEvent(new OutputEvent(errorMessage, Type.ERROR));
   }

   /**
    * Performs actions when application created.
    * 
    * @param webSocketMessage WebSocket message
    */
   private void onHerokuAppCreated(WebSocketMessage webSocketMessage)
   {
      WebSocket.getInstance().subscribe("herokuAppCreated", this);

      WebSocketExceptionMessage webSocketException = webSocketMessage.getException();
      if (webSocketException == null)
      {
         List<Property> properties = parseApplicationProperties(webSocketMessage.getData().getPayload());
         if (properties != null)
         {
            IDE.fireEvent(new OutputEvent(formApplicationCreatedMessage(properties), Type.INFO));
            IDE.fireEvent(new RefreshBrowserEvent(project));
            paasCallback.onDeploy(true);
         }
         return;
      }

      if (webSocketException.getMessage() != null && webSocketException.getMessage().length() > 0)
      {
         if (webSocketException.getMessage().contains("Authentication required"))
         {
            IDE.addHandler(LoggedInEvent.TYPE, this);
            IDE.fireEvent(new LoginEvent());
            return;
         }

         IDE.fireEvent(new OutputEvent(webSocketException.getMessage(), Type.ERROR));
      }
   }

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
