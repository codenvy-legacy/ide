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

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.ide.client.framework.websocket.WebSocketMessage.Type;
import org.exoplatform.ide.client.framework.websocket.events.WSMessageReceivedEvent;
import org.exoplatform.ide.client.framework.websocket.events.WSMessageReceivedHandler;
import org.exoplatform.ide.client.framework.websocket.exceptions.WebSocketException;
import org.exoplatform.ide.client.framework.websocket.pubsub.WSEventHandler;
import org.exoplatform.ide.client.framework.websocket.pubsub.WSPublishMessage;
import org.exoplatform.ide.client.framework.websocket.pubsub.WSSubscribeMessage;
import org.exoplatform.ide.client.framework.websocket.rest.Pair;
import org.exoplatform.ide.client.framework.websocket.rest.RESTfulRequestCallback;
import org.exoplatform.ide.client.framework.websocket.rest.RESTfulRequestMessage;
import org.exoplatform.ide.client.framework.websocket.rest.RESTfulResponseMessage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * {@link MessageBus} provides list-based PubSub asynchronous messaging pattern.
 * Class maintains lists of channels/subscribers and notifying each one
 * individually a message received. Also used to publishing messages to the channel.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: MessageBus.java Jul 30, 2012 9:24:42 AM azatsarynnyy $
 *
 */
public class MessageBus implements WSMessageReceivedHandler
{
   /** Enumeration describes the WebSocket event types. */
   public enum Channels {
      /** Channel for the messages containing the application names which may be stopped soon. */
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
    * Map of the channel to the subscribers.
    */
   private Map<String, Set<WSEventHandler>> channelToSubscribersMap = new HashMap<String, Set<WSEventHandler>>();

   /**
    * Map of the call identifier to the {@link RESTfulRequestCallback}.
    */
   private Map<String, RESTfulRequestCallback<?>> callbackMap = new HashMap<String, RESTfulRequestCallback<?>>();

   /**
    * Receive and process WebSocket messages.
    * 
    * @see org.exoplatform.ide.client.framework.websocket.events.WSMessageReceivedHandler
    *       #onWSMessageReceived(org.exoplatform.ide.client.framework.websocket.events.WSMessageReceivedEvent)
    */
   @Override
   public void onWSMessageReceived(WSMessageReceivedEvent event)
   {
      String eventMessage = event.getMessage();
      if (eventMessage == null || eventMessage.isEmpty())
      {
         return;
      }
      // Temporary commented, while PubSub not used.
//      JSONObject jsonObject = JSONParser.parseStrict(eventMessage).isObject();
//      if (jsonObject == null)
//      {
//         return;
//      }
//
//      String messageType = null;
//      if (!jsonObject.containsKey("type"))
//      {
//         return;
//      }
//      messageType = jsonObject.get("type").isString().stringValue();
//
//      if (Type.EVENT.name().equals(messageType))
//      {
//         WSEventMessage message =
//            AutoBeanCodex.decode(WebSocket.AUTO_BEAN_FACTORY, WSEventMessage.class, eventMessage).as();
//         if (channelToSubscribersMap.containsKey(message.getChannel()))
//         {
//            // copy the Set to avoid 'CuncurrentModificationException' when 'unsubscribe()' method will be invoke
//            Set<WSEventHandler> subscribersSet =
//               new HashSet<WSEventHandler>(channelToSubscribersMap.get(message.getChannel()));
//            for (WSEventHandler webSocketEventHandler : subscribersSet)
//            {
//               webSocketEventHandler.onWebSocketEvent(message);
//            }
//         }
//      }
//      else if (Type.CALL_RESULT.name().equals(messageType))
//      {
         RESTfulResponseMessage message =
            AutoBeanCodex.decode(WebSocket.AUTO_BEAN_FACTORY, RESTfulResponseMessage.class, eventMessage).as();

         if (message == null)
         {
            return;
         }

         // ignore the confirmation message
         for (Pair header : message.getHeaders())
         {
            if (HTTPHeader.LOCATION.equals(header.getName()) && header.getValue().contains("async/"))
            {
               return;
            }
         }

         RESTfulRequestCallback<?> callback = callbackMap.remove(message.getUuid());
         if (callback != null)
         {
            callback.onResponseReceived(message);
         }
//      }
   }

   /**
    * Registers a new subscriber which will receive messages on a particular channel.
    * Upon the first subscribe to a channel, a message is sent to the server to
    * subscribe the client for that channel. Subsequent subscribes for a channel
    * already previously subscribed to do not trigger a send of another message
    * to the server because the client has already a subscription, and merely registers
    * (client side) the additional handler to be fired for events received on the respective channel.
    * 
    * <p><strong>Note:</strong> the method runs asynchronously and does not provide
    * feedback whether a subscription was successful or not.
    * 
    * @param channel {@link Channels} identifier
    * @param webSocketEventHandler the {@link WSEventHandler} to fire
    *                   when receiving an event on the subscribed channel
    * @throws WebSocketException if an error has occurred while sending data
    */
   public void subscribe(Channels channel, WSEventHandler webSocketEventHandler) throws WebSocketException
   {
      if (webSocketEventHandler == null)
      {
         throw new NullPointerException("Subscriber must not be null");
      }

      Set<WSEventHandler> subscribersSet = channelToSubscribersMap.get(channel);
      if (subscribersSet != null)
      {
         subscribersSet.add(webSocketEventHandler);
         return;
      }

      subscribersSet = new HashSet<WSEventHandler>();
      subscribersSet.add(webSocketEventHandler);
      channelToSubscribersMap.put(channel.toString(), subscribersSet);

      WSSubscribeMessage message = WebSocket.AUTO_BEAN_FACTORY.webSocketSubscribeMessage().as();
      message.setType(Type.SUBSCRIBE);
      message.setChannel(channel.toString());

      AutoBean<WSSubscribeMessage> webSocketSubscribeMessageBean =
         WebSocket.AUTO_BEAN_FACTORY.webSocketSubscribeMessage(message);
      WebSocket.getInstance().send(AutoBeanCodex.encode(webSocketSubscribeMessageBean).getPayload());
   }

   /**
    * Unregisters existing subscriber to receive messages on a particular channel.
    * If it's the last unsubscribe to a channel, a message is sent to the server to
    * unsubscribe the client for that channel.
    * 
    * <p><strong>Note:</strong> the method runs asynchronously and does not provide
    * feedback whether a unsubscription was successful or not.
    * 
    * @param channel {@link Channels} identifier
    * @param webSocketEventHandler the {@link WSEventHandler} for which to remove the subscription
    */
   public void unsubscribe(Channels channel, WSEventHandler webSocketEventHandler)
   {
      unsubscribe(channel.toString(), webSocketEventHandler);
   }

   /**
    * Unregisters existing subscriber to receive messages on a particular channel.
    * If it's the last unsubscribe to a channel, a message is sent to the server to
    * unsubscribe the client for that channel.
    * 
    * <p><strong>Note:</strong> the method runs asynchronously and does not provide
    * feedback whether a unsubscription was successful or not.
    * 
    * @param channel channel identifier
    * @param webSocketEventHandler the {@link WSEventHandler} for which to remove the subscription
    */
   public void unsubscribe(String channel, WSEventHandler webSocketEventHandler)
   {
      Set<WSEventHandler> subscribersSet = channelToSubscribersMap.get(channel);
      if (subscribersSet == null)
      {
         return;
      }

      if (subscribersSet.remove(webSocketEventHandler) && subscribersSet.isEmpty())
      {
         channelToSubscribersMap.remove(channel);
         WSSubscribeMessage message = WebSocket.AUTO_BEAN_FACTORY.webSocketSubscribeMessage().as();
         message.setType(Type.UNSUBSCRIBE);
         message.setChannel(channel);

         AutoBean<WSSubscribeMessage> webSocketUnsubscribeMessageBean =
            WebSocket.AUTO_BEAN_FACTORY.webSocketSubscribeMessage(message);
         try
         {
            WebSocket.getInstance().send(AutoBeanCodex.encode(webSocketUnsubscribeMessageBean).getPayload());
         }
         catch (WebSocketException e)
         {
            webSocketEventHandler.onError(e);
         }
      }
   }

   /**
    * Publishes a message in a particular channel.
    * 
    * @param channel {@link Channels} identifier
    * @param data the text data to be published to the channel
    * @throws WebSocketException throws if an error has occurred while publishing data
    */
   public void publish(Channels channel, String data) throws WebSocketException
   {
      publish(channel.toString(), data);
   }

   /**
    * Publishes a message in a particular channel.
    * 
    * @param channel channel identifier
    * @param data the text data to be published to the channel
    * @throws WebSocketException throws if an error has occurred while publishing data
    */
   public void publish(String channel, String data) throws WebSocketException
   {
      WSPublishMessage message = WebSocket.AUTO_BEAN_FACTORY.webSocketPublishMessage().as();
      message.setType(WebSocketMessage.Type.PUBLISH);
      message.setChannel(channel);
      message.setPayload(data);

      AutoBean<WSPublishMessage> webSocketPublishMessageBean = WebSocket.AUTO_BEAN_FACTORY.webSocketPublishMessage();
      WebSocket.getInstance().send(AutoBeanCodex.encode(webSocketPublishMessageBean).getPayload());
   }

   /**
    * Sends serialized {@link RESTfulRequestMessage}.
    * 
    * @param message data which will be sent
    * @param callback handler which will be called when a reply from the server is received
    * @param uuid message UUID
    * @throws WebSocketException throws if an error has occurred while sending data
    */
   public void send(String message, RESTfulRequestCallback<?> callback, String uuid) throws WebSocketException
   {
      if (callback != null)
      {
         callbackMap.put(uuid, callback);
      }
      WebSocket.getInstance().send(message);
   }

}
