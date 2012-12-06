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

import org.exoplatform.ide.client.framework.websocket.events.MessageReceivedEvent;
import org.exoplatform.ide.client.framework.websocket.events.MessageReceivedHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Default implementation of {@link MessageBus}.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: DefaultMessageBus.java Dec 4, 2012 2:50:32 PM azatsarynnyy $
 *
 */
public abstract class DefaultMessageBus implements MessageBus, MessageReceivedHandler
{
   /**
    * Internal {@link WebSocket} instance.
    */
   private WebSocket ws;

   /**
    * WebSocket server URL.
    */
   private String url;

   /**
    * Map of the call identifier to the {@link ReplyHandler}.
    */
   protected Map<String, ReplyHandler> callbackMap;

   /**
    * Map of the channel to the subscribers.
    */
   protected Map<String, Set<MessageHandler>> channelToSubscribersMap;

   /**
    * Creates new {@link DefaultMessageBus} instance.
    * 
    * @param url WebSocket server URL
    */
   public DefaultMessageBus(String url)
   {
      this.url = url;
      initialize();
   }

   public void initialize()
   {
      ws = WebSocket.create(url);
      ws.setOnMessageHandler(this);
      callbackMap = new HashMap<String, ReplyHandler>();
      channelToSubscribersMap = new HashMap<String, Set<MessageHandler>>();
   }

   /**
    * @see org.exoplatform.ide.client.framework.websocket.MessageBus#close()
    */
   @Override
   public void close()
   {
      ws.close();
   }

   /**
    * @see org.exoplatform.ide.client.framework.websocket.MessageBus#getReadyState()
    */
   @Override
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
    * @throws WebSocketException 
    * @see org.exoplatform.ide.client.framework.websocket.MessageBus#send(java.lang.String, java.lang.String)
    */
   @Override
   public void send(String channel, String message) throws WebSocketException
   {
      send(channel, message, null);
   }

   /**
    * @throws WebSocketException 
    * @see org.exoplatform.ide.client.framework.websocket.MessageBus#send(java.lang.String, java.lang.String,
    *       org.exoplatform.ide.client.framework.websocket.MessageBus.ReplyHandler)
    */
   @Override
   public void send(String channel, String message, ReplyHandler callback) throws WebSocketException
   {
      if (callback != null)
         callbackMap.put(channel, callback);

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
    * @see org.exoplatform.ide.client.framework.websocket.MessageBus#setOnOpenHandler(org.exoplatform.ide.client.framework.websocket.MessageBus.ConnectionOpenedHandler)
    */
   @Override
   public void setOnOpenHandler(ConnectionOpenedHandler handler)
   {
      ws.setOnOpenHandler(handler);
   }

   /**
    * @see org.exoplatform.ide.client.framework.websocket.MessageBus#setOnCloseHandler(org.exoplatform.ide.client.framework.websocket.MessageBus.ConnectionClosedHandler)
    */
   @Override
   public void setOnCloseHandler(ConnectionClosedHandler handler)
   {
      ws.setOnCloseHandler(handler);
   }

   /**
    * @see org.exoplatform.ide.client.framework.websocket.MessageBus#setOnErrorHandler(org.exoplatform.ide.client.framework.websocket.MessageBus.ConnectionErrorHandler)
    */
   @Override
   public void setOnErrorHandler(ConnectionErrorHandler handler)
   {
      ws.setOnErrorHandler(handler);
   }

   /**
    * @see org.exoplatform.ide.client.framework.websocket.MessageBus#subscribe(java.lang.String,
    *       org.exoplatform.ide.client.framework.websocket.MessageBus.MessageHandler)
    */
   @Override
   public void subscribe(String channel, MessageHandler handler)
   {
      if (handler == null)
      {
         throw new NullPointerException("Handler may not be null");
      }

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

   protected abstract void sendSubscribeMessage(String channel);

   /**
    * @see org.exoplatform.ide.client.framework.websocket.MessageBus#unsubscribe(java.lang.String,
    *       org.exoplatform.ide.client.framework.websocket.MessageBus.MessageHandler)
    */
   @Override
   public void unsubscribe(String channel, MessageHandler handler)
   {
      if (handler == null)
      {
         throw new NullPointerException("Handler may not be null");
      }

      Set<MessageHandler> subscribersSet = channelToSubscribersMap.get(channel);
      if (subscribersSet == null)
      {
         return;
      }

      if (subscribersSet.remove(handler) && subscribersSet.isEmpty())
      {
         channelToSubscribersMap.remove(channel);
         sendUnsubscribeMessage(channel);
      }
   }

   protected abstract void sendUnsubscribeMessage(String channel);

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
