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
package org.exoplatform.ide.client.framework.websocket;

import com.google.gwt.core.client.JavaScriptException;

import org.exoplatform.ide.client.framework.websocket.events.ConnectionClosedHandler;
import org.exoplatform.ide.client.framework.websocket.events.ConnectionErrorHandler;
import org.exoplatform.ide.client.framework.websocket.events.ConnectionOpenedHandler;
import org.exoplatform.ide.client.framework.websocket.events.MessageHandler;
import org.exoplatform.ide.client.framework.websocket.events.MessageReceivedEvent;
import org.exoplatform.ide.client.framework.websocket.events.MessageReceivedHandler;
import org.exoplatform.ide.client.framework.websocket.events.ReplyHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Abstract WebSocket message bus, that provides two asynchronous
 * messaging patterns: RPC and list-based PubSub.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: MessageBus.java Dec 4, 2012 2:50:32 PM azatsarynnyy $
 *
 */
public abstract class MessageBus implements MessageReceivedHandler
{

   /**
    * This enumeration used to describe the ready state of the WebSocket connection.
    */
   public static enum ReadyState {

      /** The WebSocket object is created but connection has not yet been established. */
      CONNECTING(0),

      /** Connection is established and communication is possible. A WebSocket must
       * be in the open state in order to send and receive data over the network. */
      OPEN(1),

      /** Connection is going through the closing handshake. */
      CLOSING(2),

      /** The connection has been closed or could not be opened. */
      CLOSED(3);

      private final int value;

      private ReadyState(int value)
      {
         this.value = value;
      }

      @Override
      public String toString()
      {
         return String.valueOf(value);
      }
   }

   /**
    * Internal {@link WebSocket} instance.
    */
   private WebSocket ws;

   /**
    * WebSocket server URL.
    */
   private String url;

   /**
    * Map of the message identifier to the {@link ReplyHandler}.
    */
   private Map<String, ReplyHandler> callbackMap;

   /**
    * Map of the channel to the subscribers.
    */
   private Map<String, Set<MessageHandler>> channelToSubscribersMap;

   /**
    * Create new {@link MessageBus} instance.
    * 
    * @param url WebSocket server URL
    */
   public MessageBus(String url)
   {
      this.url = url;
      initialize();
   }

   /**
    * Initialize the message bus.
    */
   public void initialize()
   {
      ws = WebSocket.create(url);
      ws.setOnMessageHandler(this);
      callbackMap = new HashMap<String, ReplyHandler>();
      channelToSubscribersMap = new HashMap<String, Set<MessageHandler>>();
   }

   /**
    * Close the message bus.
    */
   public void close()
   {
      ws.close();
   }

   /**
    * Return the ready state of the WebSocket connection.
    * 
    * @return ready state of the WebSocket
    */
   public ReadyState getReadyState()
   {
      if (ws == null)
         throw new IllegalStateException("WebSocket is not initialized.");

      switch (ws.getReadyState())
      {
         case 0 :
            return ReadyState.CONNECTING;
         case 1 :
            return ReadyState.OPEN;
         case 2 :
            return ReadyState.CLOSING;
         case 3 :
            return ReadyState.CLOSED;
         default :
            return ReadyState.CLOSED;
      }
   }

   /**
    * Send {@link Message}.
    * 
    * @param message {@link Message} to send
    * @param callback callback for receiving reply to message. May be <code>null</code>.
    * @throws WebSocketException throws if an any error has occurred while sending data
    */
   public abstract void send(Message message, ReplyHandler callback) throws WebSocketException;

   /**
    * Send text message.
    * 
    * @param uuid a message identifier
    * @param message message to send
    * @param callback callback for receiving reply to message
    * @throws WebSocketException throws if an any error has occurred while sending data
    */
   protected void send(String uuid, String message, ReplyHandler callback) throws WebSocketException
   {
      if (callback != null)
         callbackMap.put(uuid, callback);

      send(message);
   }

   /**
    * Transmit text data over WebSocket.
    * 
    * @param message text message
    * @throws WebSocketException throws if an any error has occurred while sending data
    */
   protected void send(String message) throws WebSocketException
   {
      try
      {
         ws.send(message);
      }
      catch (JavaScriptException e)
      {
         throw new WebSocketException(e.getMessage());
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.websocket.events.MessageReceivedHandler#onMessageReceived(org.exoplatform.ide.client.framework.websocket.events.MessageReceivedEvent)
    */
   @Override
   public void onMessageReceived(MessageReceivedEvent event)
   {
      Message message = parseMessage(event.getMessage());
      String channel = getChannel(message);
      if (channel != null)
      {
         // this is a message received by subscription
         processSubscriptionMessage(message);
      }
      else
      {
         ReplyHandler callback = callbackMap.remove(message.getUuid());
         if (callback != null)
            callback.onReply(message);
      }
   }

   /**
    * Parse text message to {@link Message} object.
    * 
    * @param message text message
    * @return {@link Message}
    */
   protected abstract Message parseMessage(String message);

   /**
    * Process the {@link Message} that received by subscription.
    * 
    * @param message {@link Message}
    */
   private void processSubscriptionMessage(Message message)
   {
      String channel = getChannel(message);
      Set<MessageHandler> subscribersSet = channelToSubscribersMap.get(channel);
      if (subscribersSet != null)
      {
         // TODO
         // Find way to avoid copying of set.
         // Copy a Set to avoid 'CuncurrentModificationException' when 'unsubscribe()' method will invoked while iterating.
         Set<MessageHandler> subscribersSetCopy = new HashSet<MessageHandler>(subscribersSet);
         for (MessageHandler handler : subscribersSetCopy)
         {
            handler.onMessage(message);
         }
      }
   }

   /**
    * Get channel from which {@link Message} was received.
    * 
    * @param message {@link Message}
    * @return channel identifier or <code>null</code> if message is invalid.
    */
   protected abstract String getChannel(Message message);

   /**
    * Sets the {@link ConnectionOpenedHandler} to be notified when the {@link MessageBus} opened.
    * 
    * @param handler {@link ConnectionOpenedHandler}
    */
   public void setOnOpenHandler(ConnectionOpenedHandler handler)
   {
      ws.setOnOpenHandler(handler);
   }

   /**
    * Sets the {@link ConnectionClosedHandler} to be notified when the {@link MessageBus} closed.
    * 
    * @param handler {@link ConnectionClosedHandler}
    */
   public void setOnCloseHandler(ConnectionClosedHandler handler)
   {
      ws.setOnCloseHandler(handler);
   }

   /**
    * Sets the {@link ConnectionErrorHandler} to be notified when there is any error in communication over WebSocket.
    * 
    * @param handler {@link ConnectionErrorHandler}
    */
   public void setOnErrorHandler(ConnectionErrorHandler handler)
   {
      ws.setOnErrorHandler(handler);
   }

   /**
    * Subscribes a new {@link MessageHandler} which will listener for messages sent to the specified channel.
    * Upon the first subscribe to a channel, a message is sent to the server to
    * subscribe the client for that channel. Subsequent subscribes for a channel
    * already previously subscribed to do not trigger a send of another message
    * to the server because the client has already a subscription, and merely registers
    * (client side) the additional handler to be fired for events received on the respective channel.
    * 
    * @param channel channel identifier
    * @param handler {@link MessageHandler} to subscribe
    * @throws WebSocketException throws if an any error has occurred while subscribing
    */
   public void subscribe(String channel, MessageHandler handler) throws WebSocketException
   {
      Set<MessageHandler> subscribersSet = channelToSubscribersMap.get(channel);
      if (subscribersSet != null)
      {
         subscribersSet.add(handler);
         return;
      }
      subscribersSet = new HashSet<MessageHandler>();
      subscribersSet.add(handler);
      channelToSubscribersMap.put(channel, subscribersSet);
      sendSubscribeMessage(channel);
   }

   /**
    * Send message with subscription info.
    * 
    * @param channel channel identifier
    * @throws WebSocketException throws if an any error has occurred while sending data
    */
   protected abstract void sendSubscribeMessage(String channel) throws WebSocketException;

   /**
    * Unsubscribes a previously subscribed handler listening on the specified channel.
    * If it's the last unsubscribe to a channel, a message is sent to the server to
    * unsubscribe the client for that channel.
    * 
    * @param channel channel identifier
    * @param handler {@link MessageHandler} to unsubscribe
    * @throws WebSocketException throws if an any error has occurred while unsubscribing
    * @throws IllegalArgumentException throws if provided handler not subscribed to any channel
    */
   public void unsubscribe(String channel, MessageHandler handler) throws WebSocketException
   {
      Set<MessageHandler> subscribersSet = channelToSubscribersMap.get(channel);
      if (subscribersSet == null)
         throw new IllegalArgumentException("Handler not subscribed to any channel.");

      if (subscribersSet.remove(handler) && subscribersSet.isEmpty())
      {
         channelToSubscribersMap.remove(channel);
         sendUnsubscribeMessage(channel);
      }
   }

   /**
    * Send message with unsubscription info.
    * 
    * @param channel channel identifier
    * @throws WebSocketException throws if an any error has occurred while sending data
    */
   protected abstract void sendUnsubscribeMessage(String channel) throws WebSocketException;

   /**
    * Check if a provided handler is subscribed to a provided channel or not.
    * 
    * @param handler {@link MessageHandler} to check
    * @param channel channel to check
    * @return <code>true</code> if handler subscribed to channel and <code>false</code> if not
    */
   public boolean isHandlerSubscribed(MessageHandler handler, String channel)
   {
      Set<MessageHandler> set = channelToSubscribersMap.get(channel);
      if (set == null)
         return false;
      return set.contains(handler);
   }

   /**
    * Get the WebSocket server URL.
    * 
    * @return URL of the WebSocket server
    */
   public String getURL()
   {
      return url;
   }

}
