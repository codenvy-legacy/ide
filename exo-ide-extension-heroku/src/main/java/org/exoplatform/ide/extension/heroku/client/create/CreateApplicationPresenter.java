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
package org.exoplatform.ide.extension.heroku.client.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.websocket.MessageBus.Channels;
import org.exoplatform.ide.client.framework.websocket.WebSocket;
import org.exoplatform.ide.client.framework.websocket.WebSocketEventHandler;
import org.exoplatform.ide.client.framework.websocket.WebSocketException;
import org.exoplatform.ide.client.framework.websocket.messages.WebSocketEventMessage;
import org.exoplatform.ide.extension.heroku.client.HerokuAsyncRequestCallback;
import org.exoplatform.ide.extension.heroku.client.HerokuClientService;
import org.exoplatform.ide.extension.heroku.client.HerokuExtension;
import org.exoplatform.ide.extension.heroku.client.login.LoggedInEvent;
import org.exoplatform.ide.extension.heroku.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.heroku.client.login.LoginEvent;
import org.exoplatform.ide.extension.heroku.client.marshaller.Property;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.vfs.client.model.ItemContext;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Presenter for created application view. The view must be pointed in Views.gwt.xml.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: May 26, 2011 2:37:21 PM anya $
 * 
 */
public class CreateApplicationPresenter extends GitPresenter implements ViewClosedHandler, CreateApplicationHandler,
   LoggedInHandler
{
   interface Display extends IsView
   {
      /**
       * Get create button's click handler.
       * 
       * @return {@link HasClickHandlers} click handler
       */
      HasClickHandlers getCreateButton();

      /**
       * Get cancel button's click handler.
       * 
       * @return {@link HasClickHandlers} click handler
       */
      HasClickHandlers getCancelButton();

      /**
       * Get application name field.
       * 
       * @return {@link HasValue}
       */
      HasValue<String> getApplicationNameField();

      /**
       * Get remote repository name field.
       * 
       * @return {@link HasValue}
       */
      HasValue<String> getRemoteNameField();

      /**
       * Get Git work directory location field.
       * 
       * @return {@link HasValue}
       */
      HasValue<String> getWorkDirLocationField();

      /**
       * Change the enable state of the create button.
       * 
       * @param enable
       */
      void enableCreateButton(boolean enable);

      /**
       * Give focus to application name field.
       */
      void focusInApplicationNameField();
   }

   private Display display;

   private ProjectModel project;

   private String applicationName;

   private String remoteName;

   public CreateApplicationPresenter()
   {
      IDE.addHandler(ViewClosedEvent.TYPE, this);
      IDE.addHandler(CreateApplicationEvent.TYPE, this);
   }

   /**
    * Bind display with presenter.
    */
   public void bindDisplay()
   {
      display.getCreateButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            applicationName = display.getApplicationNameField().getValue();
            remoteName = display.getRemoteNameField().getValue();
            project = ((ItemContext)selectedItems.get(0)).getProject();
            IDE.getInstance().closeView(display.asView().getId());
            doCreateApplication();
         }
      });

      display.getCancelButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
      });
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.create.CreateApplicationHandler#onCreateApplication(org.exoplatform.ide.extension.heroku.client.create.CreateApplicationEvent)
    */
   @Override
   public void onCreateApplication(CreateApplicationEvent event)
   {
      if (makeSelectionCheck())
      {
         String workdir = ((ItemContext)selectedItems.get(0)).getProject().getPath();
         if (display == null)
         {
            display = GWT.create(Display.class);
            bindDisplay();
            IDE.getInstance().openView(display.asView());
            applicationName = null;
            remoteName = null;
            project = null;
            display.focusInApplicationNameField();
            display.getWorkDirLocationField().setValue(workdir);
         }
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent)
    */
   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
      }
   }

   /**
    * Perform creation of application on Heroku.
    */
   protected void doCreateApplication()
   {
      try
      {
         boolean useWebSocketForCallback = false;
         final WebSocket ws = WebSocket.getInstance();
         if (ws != null && ws.getReadyState() == WebSocket.ReadyState.OPEN)
         {
            useWebSocketForCallback = true;
            ws.messageBus().subscribe(Channels.HEROKU_APP_CREATED, appCreatedHandler);
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
                  }
               }

               /**
                * @see org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback#onFailure(java.lang.Throwable)
                */
               @Override
               protected void onFailure(Throwable exception)
               {
                  super.onFailure(exception);
                  if (useWebSocket)
                  {
                     ws.messageBus().unsubscribe(Channels.HEROKU_APP_CREATED, appCreatedHandler);
                  }
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
      catch (WebSocketException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
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
    * @see org.exoplatform.ide.extension.heroku.client.login.LoggedInHandler#onLoggedIn(org.exoplatform.ide.extension.heroku.client.login.LoggedInEvent)
    */
   @Override
   public void onLoggedIn(LoggedInEvent event)
   {
      IDE.removeHandler(LoggedInEvent.TYPE, this);
      if (!event.isFailed())
      {
         doCreateApplication();
      }
   }

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
               IDE.addHandler(LoggedInEvent.TYPE, CreateApplicationPresenter.this);
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
