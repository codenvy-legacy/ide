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
import com.codenvy.ide.client.util.logging.Log;
import com.codenvy.ide.collaboration.dto.ChatMessage;
import com.codenvy.ide.collaboration.dto.ChatParticipantAdd;
import com.codenvy.ide.collaboration.dto.ChatParticipantRemove;
import com.codenvy.ide.collaboration.dto.RoutingTypes;
import com.codenvy.ide.collaboration.dto.UserDetails;
import com.codenvy.ide.collaboration.dto.client.DtoClientImpls.ChatMessageImpl;
import com.codenvy.ide.collaboration.dto.client.DtoClientImpls.UserDetailsImpl;
import com.codenvy.ide.notification.Notification;
import com.codenvy.ide.notification.Notification.NotificationType;
import com.codenvy.ide.notification.NotificationManager;
import com.google.collide.client.CollabEditor;
import com.google.collide.client.CollabEditorExtension;
import com.google.collide.client.code.ParticipantModel;
import com.google.collide.client.code.ParticipantModel.Listener;
import com.google.collide.client.collaboration.DocumentCollaborationController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import elemental.events.Event;
import elemental.events.EventListener;
import elemental.events.KeyboardEvent.KeyCode;

import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.websocket.MessageFilter;
import org.exoplatform.ide.client.framework.websocket.MessageFilter.MessageRecipient;
import org.exoplatform.ide.client.framework.websocket.WebSocketException;
import org.exoplatform.ide.json.client.Jso;
import org.exoplatform.ide.json.client.JsoArray;
import org.exoplatform.ide.json.client.JsoStringMap;
import org.exoplatform.ide.json.shared.JsonArray;
import org.exoplatform.ide.json.shared.JsonCollections;
import org.exoplatform.ide.json.shared.JsonStringMap;
import org.exoplatform.ide.json.shared.JsonStringMap.IterationCallback;
import org.exoplatform.ide.shared.util.StringUtils;

import java.util.Date;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class ProjectChatPresenter implements ViewClosedHandler, ShowHideChatHandler, EditorActiveFileChangedHandler
{

   public interface Display extends IsView
   {
      String ID = "codenvyIdeChat";

      String getChatMessage();

      void clearMessage();

      void addMessage(UserDetails userDetails, String message, long time);

      void addListener(EventListener eventListener);

      void messageNotDelivered(String messageId);

      void messageDelivered(String messageId);

      void removeParticipant(String userId);

      void addParticipant(Participant participant);

      void removeEditParticipant(String userId);

      void addEditParticipant(String userId);

      void clearEditParticipants();
   }

   private class MessagesTimer extends Timer
   {

      private String messageId;

      public boolean executed = false;

      private MessagesTimer(String messageId)
      {
         this.messageId = messageId;
      }

      @Override
      public void run()
      {
         executed = true;
         if (display != null)
         {
            display.messageNotDelivered(messageId);
         }
      }
   }

   public static final int MESSAGE_DELIVER_TIMEOUT = 10000;


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

   private Listener listener = new Listener()
   {
      @Override
      public void participantAdded(com.google.collide.client.code.Participant participant)
      {
         display.addEditParticipant(participant.getUserId());
      }

      @Override
      public void participantRemoved(com.google.collide.client.code.Participant participant)
      {
         display.removeEditParticipant(participant.getUserId());
      }
   };

   private ChatApi chatApi;

   private IDE ide;

   private ShowChatControl control;

   private CollabEditorExtension collabExtension;

   private Display display;

   private JsonStringMap<UserDetails> users = JsonCollections.createMap();

   private JsonStringMap<MessagesTimer> deliverTimers = JsonCollections.createMap();

   private String userId;

   private String projectId;

   private boolean viewClosed = true;

   private ParticipantModel participantModel;

   public ProjectChatPresenter(ChatApi chatApi, MessageFilter messageFilter, IDE ide, ShowChatControl chatControl,
      final String userId, CollabEditorExtension collabExtension)
   {
      this.chatApi = chatApi;
      this.ide = ide;
      this.userId = userId;
      control = chatControl;
      this.collabExtension = collabExtension;
      ide.eventBus().addHandler(ViewClosedEvent.TYPE, this);
      ide.eventBus().addHandler(ShowHideChatEvent.TYPE, this);
      IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
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
               if (userId.equals(message.userId()))
               {
                  return;
               }
               removeParticipant(message.userId());
            }
         });
   }

   private void removeParticipant(String userId)
   {
      users.remove(userId);
      display.removeParticipant(userId);
   }

   private void addParticipant(UserDetails user)
   {
      if (user.getUserId().equals(userId))
      {
         ((UserDetailsImpl)user).setIsCurrentUser(true);
      }
      users.put(user.getUserId(), user);
      if (display != null && !user.isCurrentUser())
      {
         Participant p = getParticipant(user);
         display.addParticipant(p);
      }
   }

   private Participant getParticipant(UserDetails user)
   {
      com.google.collide.client.code.Participant participant = collabExtension.getUsersModel().getParticipant(
         user.getUserId());
      Participant p = ((Jso)user).cast();
      p.setColor(participant.getColor());
      return p;
   }


   private void messageReceived(ChatMessage message)
   {
      if (message.getUserId().equals(userId))
      {
         MessagesTimer timer = deliverTimers.remove(message.getDateTime());
         if (timer.executed)
         {
            display.messageDelivered(message.getDateTime());
         }
         else
         {
            timer.cancel();
         }
         return;
      }
      display.addMessage(users.get(message.getUserId()), message.getMessage(), Long.valueOf(message.getDateTime()));
      if (viewClosed || !display.asView().isViewVisible())
      {
         ChatNotificationWidget widget = new ChatNotificationWidget(users.get(message.getUserId()),
            message.getMessage());
         Notification chatNotification = new Notification(widget, NotificationType.MESSAGE, 10000);
         NotificationManager.get().addNotification(chatNotification);
      }
   }

   private void sendMessage()
   {

      String message = display.getChatMessage();
      if (message.isEmpty())
      {
         return;
      }
      if (StringUtils.isNullOrWhitespace(message))
      {
         return;
      }
      ChatMessageImpl chatMessage = ChatMessageImpl.make();
      chatMessage.setUserId(userId);
      chatMessage.setProjectId(projectId);
      Date d = new Date();
      chatMessage.setDateTime(String.valueOf(d.getTime()));
      MessagesTimer messagesTimer = new MessagesTimer(chatMessage.getDateTime());
      messagesTimer.schedule(MESSAGE_DELIVER_TIMEOUT);
      deliverTimers.put(chatMessage.getDateTime(), messagesTimer);
      SafeHtmlBuilder b = new SafeHtmlBuilder();
      b.appendEscapedLines(message);
      chatMessage.setMessage(b.toSafeHtml().asString());
      display.clearMessage();
      display.addMessage(users.get(userId), chatMessage.getMessage(), d.getTime());
      try
      {
         chatApi.SEND_MESSAGE.send(chatMessage);
      }
      catch (WebSocketException e)
      {
         Log.debug(ProjectChatPresenter.class, e);
      }

   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView().getId().equals(Display.ID))
      {
         control.chatOpened(false);
         viewClosed = true;
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
         if (display != null)
         {
            ide.openView(display.asView());
            viewClosed = false;
         }
      }
      else
      {
         ide.closeView(Display.ID);
      }
   }

   private void openChat()
   {
      ide.openView(display.asView());
      viewClosed = false;
      control.chatOpened(true);
   }

   void setChatParticipants(JsonArray<UserDetails> chatParticipants)
   {
      users = JsonCollections.createMap();
      for (UserDetails ud : chatParticipants.asIterable())
      {
         if (ud.getUserId().equals(userId))
         {
            ((UserDetailsImpl)ud).setIsCurrentUser(true);
         }
         users.put(ud.getUserId(), ud);
      }
   }

   void setProjectId(String projectId)
   {
      this.projectId = projectId;
      display = GWT.create(Display.class);
      display.addListener(enterListener);
      users.iterate(new IterationCallback<UserDetails>()
      {
         @Override
         public void onIteration(String key, UserDetails value)
         {
            Participant p = getParticipant(value);
            display.addParticipant(p);
         }
      });
      if (users.size() > 1)
      {
         openChat();
      }
   }

   void projectClosed()
   {
      ide.closeView(Display.ID);
      display = null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      if (event.getEditor() != null && event.getEditor() instanceof CollabEditor)
      {
         CollabEditor editor = (CollabEditor)event.getEditor();
         DocumentCollaborationController controller = collabExtension.getCollaborationManager().getDocumentCollaborationController(
            editor.getEditor().getDocument().getId());

         if (participantModel != null)
         {
            participantModel.removeListener(listener);
         }
         participantModel = controller.getParticipantModel();
         participantModel.addListener(listener);
         JsoStringMap<com.google.collide.client.code.Participant> participants = participantModel.getParticipants();
         participants.iterate(new IterationCallback<com.google.collide.client.code.Participant>()
         {
            @Override
            public void onIteration(String key, com.google.collide.client.code.Participant value)
            {
              display.addEditParticipant(value.getUserId());
            }
         });
      }
      else
      {
         display.clearEditParticipants();
      }
   }
}
