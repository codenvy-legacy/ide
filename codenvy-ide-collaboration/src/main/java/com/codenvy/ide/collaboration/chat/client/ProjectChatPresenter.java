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

import com.codenvy.ide.client.util.SignalEvent;
import com.codenvy.ide.client.util.SignalEventUtils;
import com.codenvy.ide.collaboration.dto.ChatMessage;
import com.codenvy.ide.collaboration.dto.ChatParticipantAdd;
import com.codenvy.ide.collaboration.dto.ChatParticipantRemove;
import com.codenvy.ide.collaboration.dto.RoutingTypes;
import com.codenvy.ide.collaboration.dto.UserDetails;
import com.codenvy.ide.collaboration.dto.client.DtoClientImpls.ChatMessageImpl;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import elemental.events.Event;
import elemental.events.EventListener;
import elemental.events.KeyboardEvent.KeyCode;

import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.websocket.MessageFilter;
import org.exoplatform.ide.client.framework.websocket.MessageFilter.MessageRecipient;
import org.exoplatform.ide.dtogen.shared.ServerToClientDto;
import org.exoplatform.ide.json.shared.JsonArray;
import org.exoplatform.ide.json.shared.JsonCollections;
import org.exoplatform.ide.json.shared.JsonStringMap;
import org.exoplatform.ide.shared.util.StringUtils;

import java.util.Date;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class ProjectChatPresenter implements ViewClosedHandler, ShowHideChatHandler
{

   public interface Display extends IsView
   {
      String ID = "codenvyIdeChat";

      String getChatMessage();

      void clearMessage();

      void addMessage(UserDetails userDetails, String message, long time);

      void addListener(EventListener eventListener);

      void setParticipants(JsonStringMap<UserDetails> chatParticipants);
   }


   private EventListener enterListener = new EventListener()
   {
      @Override
      public void handleEvent(Event event)
      {
         SignalEvent signalEvent = SignalEventUtils.create(event);
         if (signalEvent != null && signalEvent.getKeyCode() == KeyCode.ENTER)
         {
            if (signalEvent.getAltKey() || signalEvent.getCommandKey() ||
               signalEvent.getCtrlKey() || signalEvent.getMetaKey() || signalEvent.getShiftKey())
            {
               return;
            }
            event.stopPropagation();
            event.preventDefault();
            sendMessage();
         }
      }
   };

   private ChatApi chatApi;

   private IDE ide;

   private ShowChatControl control;

   private Display display;

   private JsonStringMap<UserDetails> users = JsonCollections.createMap();

   private String userId;

   private String projectId;

   public ProjectChatPresenter(ChatApi chatApi, MessageFilter messageFilter, IDE ide, ShowChatControl chatControl,
      String userId)
   {
      this.chatApi = chatApi;
      this.ide = ide;
      this.userId = userId;
      control = chatControl;
      ide.eventBus().addHandler(ViewClosedEvent.TYPE, this);
      ide.eventBus().addHandler(ShowHideChatEvent.TYPE, this);
      messageFilter.registerMessageRecipient(RoutingTypes.CHAT_MESSAGE, new MessageRecipient<ChatMessage>()
      {
         @Override
         public void onMessageReceived(ChatMessage message)
         {
            messageReceived(message);
         }
      });

      messageFilter.registerMessageRecipient(RoutingTypes.CHAT_PARTISIPANT_ADD,
         new MessageRecipient<ChatParticipantAdd>()
         {
            @Override
            public void onMessageReceived(ChatParticipantAdd message)
            {
               addParticipant(message.user());
            }
         });

      messageFilter.registerMessageRecipient(RoutingTypes.CHAT_PARTISIPANT_REMOVE,
         new MessageRecipient<ChatParticipantRemove>()
         {
            @Override
            public void onMessageReceived(ChatParticipantRemove message)
            {
               removeParticipant(message.userId());
            }
         });
   }

   private void removeParticipant(String userId)
   {
      users.remove(userId);
      display.setParticipants(users);
   }

   private void addParticipant(UserDetails user)
   {
      users.put(user.getUserId(),user);
      if (display != null)
      {
         display.setParticipants(users);
      }
   }


   private void messageReceived(ChatMessage message)
   {
      display.addMessage(users.get(message.getUserId()), message.getMessage(), Long.valueOf(message.getDateTime()));
   }

   private void sendMessage()
   {

      String message = display.getChatMessage();
      if (message.isEmpty())
      {
         return;
      }
      if(StringUtils.isNullOrWhitespace(message))
      {
         return;
      }
      ChatMessageImpl chatMessage = ChatMessageImpl.make();
      chatMessage.setUserId(userId);
      chatMessage.setProjectId(projectId);
      Date d = new Date();
      chatMessage.setDateTime(String.valueOf(d.getTime()));
      SafeHtmlBuilder b = new SafeHtmlBuilder();
      b.appendEscapedLines(message);
      chatMessage.setMessage(b.toSafeHtml().asString());
      chatApi.SEND_MESSAGE.send(chatMessage);
      display.clearMessage();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView().getId().equals(Display.ID))
      {
         display = null;
         control.chatOpened(false);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onShowHideChat(ShowHideChatEvent event)
   {
      if (event.isShow())
      {
         display = GWT.create(Display.class);
         display.addListener(enterListener);
         display.setParticipants(users);
         ide.openView(display.asView());
         control.chatOpened(true);
      }
      else
      {
         ide.closeView(Display.ID);
      }
   }

   public void setChatParticipants(JsonArray<UserDetails> chatParticipants)
   {
      users = JsonCollections.createMap();
      for(UserDetails ud : chatParticipants.asIterable())
      {
         users.put(ud.getUserId(), ud);
      }
   }

   public void setProjectId(String projectId)
   {
      this.projectId = projectId;
   }
}
