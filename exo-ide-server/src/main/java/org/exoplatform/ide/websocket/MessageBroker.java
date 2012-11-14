/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.websocket;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.WsOutbound;
import org.everrest.core.impl.provider.json.JsonValue;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.ide.commons.JsonHelper;
import org.exoplatform.ide.commons.ParsingResponseException;
import org.exoplatform.ide.websocket.WebSocketMessage.Type;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.io.IOException;
import java.nio.CharBuffer;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * {@link MessageBroker} provides list-based PubSub asynchronous messaging pattern.
 * The {@link MessageBroker} looks up clients registered under the session identifier
 * and then passes the message to them.
 * {@link MessageBroker} implements a queue of messages that were sent with errors.
 * This messages must be resent on the next client connection.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: MessageBroker.java Jun 20, 2012 5:10:29 PM azatsarynnyy $
 *
 */
public class MessageBroker
{
   /** Enumeration describing the WebSocket event types. */
   public enum Channels {
      /** Channel for the messages containing the debugger event. */
      DEBUGGER_EXPIRE_SOON_APPS("debugger:expireSoonApps");

      private final String eventTypeValue;

      private Channels(String value)
      {
         this.eventTypeValue = value;
      }

      @Override
      public String toString()
      {
         return eventTypeValue;
      }
   }

   /**
    * WebSocket session manager that used for managing of the client's sessions.
    */
   private static SessionManager sessionManager = (SessionManager)ExoContainerContext.getCurrentContainer()
      .getComponentInstanceOfType(SessionManager.class);

   /**
    * Exo logger.
    */
   private static final Log LOG = ExoLogger.getLogger(MessageBroker.class);

   /**
    * Map of the channel to the subscribers.
    * Used for publish messages to clients which subscribed
    * to receive messages on a particular channel.
    */
   private ConcurrentMap<String, CopyOnWriteArraySet<String>> channelToSubscribers =
      new ConcurrentHashMap<String, CopyOnWriteArraySet<String>>();

   /**
    * Map of the session identifier to the queued messages that were sent with errors.
    */
   private Map<String, CopyOnWriteArrayList<WebSocketMessage>> notSendedMessagesQueue =
      new ConcurrentHashMap<String, CopyOnWriteArrayList<WebSocketMessage>>();

   /**
    * Process incoming message.
    * 
    * @param message incoming message
    * @param sessionId WebSocket session identifier
    */
   public void handleMessage(String sessionId, String message)
   {
      if (message.equals("PING"))
      {
         return;
      }

      String messageType = null;
      JsonValue jsonValue = null;
      try
      {
         jsonValue = JsonHelper.parseJson(message.toString());
      }
      catch (ParsingResponseException e)
      {
         LOG.warn("An error occurs parsing the WebSocket message", e);
      }

      if (jsonValue != null && jsonValue.isObject())
      {
         messageType = jsonValue.getElement("type").getStringValue();
      }
      else
      {
         return;
      }

      if (Type.SUBSCRIBE.name().equals(messageType))
      {
         WebSocketSubscribeMessage webSocketMessage = new WebSocketSubscribeMessage(message);
         subscribe(sessionId, webSocketMessage.getChannel());
      }
      else if (Type.UNSUBSCRIBE.name().equals(messageType))
      {
         WebSocketSubscribeMessage webSocketMessage = new WebSocketSubscribeMessage(message);
         unsubscribe(sessionId, webSocketMessage.getChannel());
      }
      else if (Type.PUBLISH.name().equals(messageType))
      {
         WebSocketPublishMessage webSocketMessage = new WebSocketPublishMessage(message);
         publish(webSocketMessage.getChannel(), webSocketMessage.getPayload(), null, sessionId);
      }
   }

   /**
    * Resends all messages that were sent with any errors.
    * 
    * @param sessionId WebSocket session identifier
    */
   public void checkNotSendedMessages(String sessionId)
   {
      List<WebSocketMessage> messageList = notSendedMessagesQueue.get(sessionId);
      if (messageList != null)
      {
         for (WebSocketMessage message : messageList)
         {
            messageList.remove(message);
            if (messageList.isEmpty())
            {
               notSendedMessagesQueue.remove(sessionId);
            }
            send(sessionId, message);
         }
      }
   }

   /**
    * Removes all queued messages that were sent with errors.
    * 
    * @param sessionId WebSocket session identifier
    */
   void clearNotSendedMessageQueue(String sessionId)
   {
      notSendedMessagesQueue.remove(sessionId);
   }

   /**
    * Subscribes client to receive messages on a particular channel.
    * 
    * @param sessionId client's session identifier
    * @param channel channel name
    */
   private void subscribe(String sessionId, String channel)
   {
      if (sessionId == null)
      {
         throw new NullPointerException("Session identifier must not be null");
      }
      if (channel == null)
      {
         throw new NullPointerException("Channel name must not be null");
      }

      CopyOnWriteArraySet<String> subscribersSet = channelToSubscribers.get(channel);
      if (subscribersSet == null)
      {
         CopyOnWriteArraySet<String> newSubscribersSet = new CopyOnWriteArraySet<String>();
         subscribersSet = channelToSubscribers.putIfAbsent(channel, newSubscribersSet);
         if (subscribersSet == null)
         {
            subscribersSet = newSubscribersSet;
         }
      }
      subscribersSet.add(sessionId);
   }

   /**
    * Unsubscribes the client to receive the messages on a particular channel or on the all channels.
    * 
    * @param sessionId client's session identifier
    * @param channel channel name. If <code>null</code> then client will be unsubscribed to all subscriptions.
    */
   public void unsubscribe(String sessionId, String channel)
   {
      if (sessionId == null)
      {
         throw new NullPointerException("Session identifier must not be null");
      }

      if (channel != null)
      {
         doUnsubscribe(sessionId, channel, channelToSubscribers.get(channel));
      }
      else
      {
         for (Entry<String, CopyOnWriteArraySet<String>> entry : channelToSubscribers.entrySet())
         {
            doUnsubscribe(sessionId, entry.getKey(), entry.getValue());
         }
      }
   }

   private void doUnsubscribe(String sessionId, String channel, Set<String> subscribersSet)
   {
      if (channel == null || subscribersSet == null)
      {
         return;
      }

      if (subscribersSet.remove(sessionId) && subscribersSet.isEmpty())
      {
         channelToSubscribers.remove(channel);
      }
   }

   /**
    * Publishes a message in a particular channel.
    * 
    * @param channel channel identifier
    * @param message the text message to be published to the channel
    * @param an exception to be sent to the client. May be <code>null</code>.
    * @param excludeSessionId identifier of the WebSocket session,
    *          who does not will be sent a message
    */
   public void publish(Channels channel, String message, Exception e, String excludeSessionId)
   {
      publish(channel.toString(), message, e, excludeSessionId);
   }

   /**
    * Publishes a message in a particular channel.
    * 
    * @param channel channel identifier
    * @param message the text message to be published to the channel
    * @param an exception to be sent to the client. May be <code>null</code>.
    * @param excludeSessionId identifier of the WebSocket session,
    *          who does not will be sent a message
    */
   public void publish(String channel, String message, Exception e, String excludeSessionId)
   {
      if (channelToSubscribers.containsKey(channel))
      {
         CopyOnWriteArraySet<String> subscribersSet = channelToSubscribers.get(channel);
         for (String subscriber : subscribersSet)
         {
            if (excludeSessionId != null && excludeSessionId.equals(subscriber))
            {
               return;
            }

            String exception = null;
            if (e != null)
            {
               String errorMessage = e.getMessage().replaceAll("\n", " ");
               exception = "{\"name\":\"" + e.getClass().getSimpleName() + "\",\"message\":\"" + errorMessage + "\"}";
            }
            send(subscriber, new WebSocketEventMessage(channel, message, exception));
         }
      }
   }

   /**
    * Sends the message to the client.
    * <p><strong>Note:</strong> if user has more than one active
    * connections with the same session identifier then message
    * will be sent to all connections.
    * 
    * @param sessionId WebSocket session identifier
    * @param message the {@link WebSocketMessage} to be sent to the client
    */
   void send(String sessionId, WebSocketMessage message)
   {
      try
      {
         send(sessionId, message.toString());
      }
      catch (Exception e)
      {
         CopyOnWriteArrayList<WebSocketMessage> messageList = notSendedMessagesQueue.get(sessionId);
         if (messageList != null)
         {
            messageList.add(message);
         }
         else
         {
            messageList = new CopyOnWriteArrayList<WebSocketMessage>();
            messageList.add(message);
            notSendedMessagesQueue.put(sessionId, messageList);
         }
      }
   }

   /**
    * Sends the message to the client.
    * <p><strong>Note:</strong> if user has more than one active
    * connections with the same session identifier then message
    * will be sent to all connections.
    * 
    * @param sessionId WebSocket session identifier
    * @param message the text message to be sent to the client
    * @throws IOException if an error occurs writing to the client
    */
   private void send(String sessionId, String message) throws IOException
   {
      CopyOnWriteArraySet<MessageInbound> connectionsSet = sessionManager.getConnectionsOfSession(sessionId);
      if (connectionsSet == null)
      {
         throw new IllegalArgumentException("Unable to find session with ID " + sessionId + ".");
      }

      if (connectionsSet.isEmpty())
      {
         throw new IllegalArgumentException("Unable to find connection with session ID " + sessionId + ".");
      }

      for (MessageInbound messageInbound : connectionsSet)
      {
         WsOutbound wsOut = messageInbound.getWsOutbound();
         wsOut.writeTextMessage(CharBuffer.wrap(message));
         wsOut.flush();
      }
   }
}
