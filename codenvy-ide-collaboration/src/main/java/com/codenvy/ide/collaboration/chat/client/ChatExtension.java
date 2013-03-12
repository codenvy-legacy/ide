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
package com.codenvy.ide.collaboration.chat.client;

import com.codenvy.ide.collaboration.dto.GetChatParticipantsResponse;
import com.codenvy.ide.collaboration.dto.client.DtoClientImpls.GetChatParticipantsImpl;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;

import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.userinfo.UserInfo;
import org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedEvent;
import org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedHandler;
import org.exoplatform.ide.client.framework.websocket.FrontendApi.ApiCallback;
import org.exoplatform.ide.client.framework.websocket.MessageBus.ReadyState;
import org.exoplatform.ide.client.framework.websocket.MessageFilter;
import org.exoplatform.ide.client.framework.websocket.events.MainSocketOpenedEvent;
import org.exoplatform.ide.client.framework.websocket.events.MainSocketOpenedHandler;
import org.exoplatform.ide.client.framework.websocket.events.MessageHandler;
import org.exoplatform.ide.dtogen.client.RoutableDtoClientImpl;
import org.exoplatform.ide.dtogen.shared.ServerError.FailureReason;
import org.exoplatform.ide.dtogen.shared.ServerToClientDto;
import org.exoplatform.ide.json.client.Jso;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 *
 */
public class ChatExtension extends Extension implements MainSocketOpenedHandler, ProjectOpenedHandler, ProjectClosedHandler, UserInfoReceivedHandler
{

   public static final ChatResources resources = GWT.create(ChatResources.class);

   private ShowChatControl chatControl;

   private MessageFilter messageFilter = new MessageFilter();

   private MessageHandler handler =
  new MessageHandler()
{
   @Override
   public void onMessage(String message)
   {
      ServerToClientDto dto = (ServerToClientDto)Jso.deserialize(message).<RoutableDtoClientImpl>cast();
      messageFilter.dispatchMessage(dto);
   }
};

   private ChatApi chatApi;

   private ProjectChatPresenter chatPresenter;

   private HandlerRegistration handlerRegistration;

   private UserInfo userInfo;

   private ProjectModel currentProject;

   private boolean subscribeOnReady = false;

   /**
    * {@inheritDoc}
    */
   @Override
   public void initialize()
   {
      resources.chatCss().ensureInjected();
      chatControl = new ShowChatControl(resources);
      IDE.getInstance().addControl(chatControl);
      IDE.addHandler(ProjectOpenedEvent.TYPE, this);
      IDE.addHandler(ProjectClosedEvent.TYPE, this);
      IDE.addHandler(UserInfoReceivedEvent.TYPE, this);
   }

   @Override
   public void onMainSocketOpened(MainSocketOpenedEvent event)
   {
      if(subscribeOnReady && currentProject != null)
      {
         subscribeToChanel();
      }
      createPresenter();
      handlerRegistration.removeHandler();
   }

   private void createPresenter()
   {
      chatApi = new ChatApi(IDE.messageBus());
      chatPresenter = new ProjectChatPresenter(chatApi, messageFilter, IDE.getInstance(), chatControl, userInfo.getName());
   }

   @Override
   public void onProjectClosed(ProjectClosedEvent event)
   {
      IDE.messageBus().unsubscribe("project_chat." + event.getProject().getId(), handler);
      currentProject = null;
   }

   @Override
   public void onProjectOpened(ProjectOpenedEvent event)
   {
      currentProject = event.getProject();
      if (IDE.messageBus().getReadyState() != ReadyState.OPEN)
      {
         subscribeOnReady = true;
      }
      else
      {
        subscribeToChanel();
      }
   }

   private void subscribeToChanel()
   {
      String projectId = currentProject.getId();
      IDE.messageBus().subscribe("project_chat." + projectId, handler);
      GetChatParticipantsImpl request = GetChatParticipantsImpl.make();
      request.setProjectId(projectId);
      chatPresenter.setProjectId(projectId);
      chatApi.GET_CHAT_PARTISIPANTS.send(request,new ApiCallback<GetChatParticipantsResponse>()
      {
         @Override
         public void onFail(FailureReason reason)
         {

         }

         @Override
         public void onMessageReceived(GetChatParticipantsResponse message)
         {
            chatPresenter.setChatParticipants(message.getParticipants());
         }
      });
   }

   @Override
   public void onUserInfoReceived(UserInfoReceivedEvent event)
   {
      userInfo = event.getUserInfo();
      if (IDE.messageBus().getReadyState() != ReadyState.OPEN)
      {
         handlerRegistration = IDE.eventBus().addHandler(MainSocketOpenedEvent.TYPE, this);
         return;
      }

      createPresenter();
   }
}
