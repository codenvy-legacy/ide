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
package org.exoplatform.ide.git.client.init;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.RequestStatusHandler;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.websocket.MessageBus.Channels;
import org.exoplatform.ide.client.framework.websocket.WebSocket;
import org.exoplatform.ide.client.framework.websocket.WebSocketEventHandler;
import org.exoplatform.ide.client.framework.websocket.WebSocketException;
import org.exoplatform.ide.client.framework.websocket.messages.WebSocketEventMessage;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.vfs.client.model.ItemContext;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * Presenter for Init Repository view.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Mar 24, 2011 9:07:58 AM anya $
 * 
 */
public class InitRepositoryPresenter extends GitPresenter implements InitRepositoryHandler
{
   public interface Display extends IsView
   {
      /**
       * Get's bare field.
       * 
       * @return {@link HasValue}
       */
      HasValue<Boolean> getBareValue();

      /**
       * Get's working directory field.
       * 
       * @return {@link HasValue}
       */
      HasValue<String> getWorkDirValue();

      /**
       * Gets initialize repository button.
       * 
       * @return {@link HasClickHandlers}
       */
      HasClickHandlers getInitButton();

      /**
       * Gets cancel button.
       * 
       * @return {@link HasClickHandlers}
       */
      HasClickHandlers getCancelButton();
   }

   private Display display;

   private RequestStatusHandler statusHandler;

   /**
    * @param eventBus
    */
   public InitRepositoryPresenter()
   {
      IDE.addHandler(InitRepositoryEvent.TYPE, this);
   }

   public void bindDisplay(Display d)
   {
      this.display = d;

      display.getInitButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            initRepository();
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
    * @see org.exoplatform.ide.git.client.init.InitRepositoryHandler#onInitRepository(org.exoplatform.ide.git.client.init.InitRepositoryEvent)
    */
   @Override
   public void onInitRepository(InitRepositoryEvent event)
   {
      if (makeSelectionCheck())
      {
         Display d = GWT.create(Display.class);
         IDE.getInstance().openView((View)d);
         bindDisplay(d);
         display.getWorkDirValue().setValue(((ItemContext)selectedItems.get(0)).getProject().getPath(), true);
      }
   }

   /**
    * Get the values of the necessary parameters for initialization of the repository.
    */
   public void initRepository()
   {
      final String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
      String projectName = ((ItemContext)selectedItems.get(0)).getProject().getName();
      boolean bare = display.getBareValue().getValue();
      try
      {
         boolean useWebSocketForCallback = false;
         final WebSocket ws = null;//WebSocket.getInstance(); TODO: temporary disable web-sockets
         if (ws != null && ws.getReadyState() == WebSocket.ReadyState.OPEN)
         {
            useWebSocketForCallback = true;
            statusHandler = new InitRequestStatusHandler(projectName);
            statusHandler.requestInProgress(projectId);
            ws.messageBus().subscribe(Channels.GIT_REPO_INITIALIZED, repoInitializedHandler);
         }
         final boolean useWebSocket = useWebSocketForCallback;

         GitClientService.getInstance().init(vfs.getId(), projectId, projectName, bare, useWebSocket,
            new AsyncRequestCallback<String>()
            {

               @Override
               protected void onSuccess(String result)
               {
                  if (!useWebSocket)
                  {
                     IDE.fireEvent(new OutputEvent(GitExtension.MESSAGES.initSuccess(), Type.INFO));
                     IDE.fireEvent(new RefreshBrowserEvent(((ItemContext)selectedItems.get(0)).getProject()));
                  }
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  handleError(exception);
                  if (useWebSocket)
                  {
                     ws.messageBus().unsubscribe(Channels.GIT_REPO_INITIALIZED, repoInitializedHandler);
                     ProjectModel project = ((ItemContext)selectedItems.get(0)).getProject();
                     statusHandler.requestError(project.getId(), exception);
                  }
               }
            });
      }
      catch (RequestException e)
      {
         handleError(e);
      }
      catch (WebSocketException e)
      {
         handleError(e);
      }
      IDE.getInstance().closeView(display.asView().getId());
   }

   private void handleError(Throwable e)
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

         ProjectModel project = ((ItemContext)selectedItems.get(0)).getProject();
         statusHandler.requestFinished(project.getId());
         IDE.fireEvent(new OutputEvent(GitExtension.MESSAGES.initSuccess(), Type.INFO));
         IDE.fireEvent(new RefreshBrowserEvent(project));
      }

      @Override
      public void onError(Exception exception)
      {
         WebSocket.getInstance().messageBus().unsubscribe(Channels.GIT_REPO_INITIALIZED, this);

         ProjectModel project = ((ItemContext)selectedItems.get(0)).getProject();
         statusHandler.requestError(project.getId(), exception);
         handleError(exception);
      }
   };
}
