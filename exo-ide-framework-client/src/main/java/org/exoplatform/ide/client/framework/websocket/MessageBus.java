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

import com.google.gwt.http.client.RequestBuilder;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.ide.client.framework.websocket.events.WSMessageReceivedEvent;
import org.exoplatform.ide.client.framework.websocket.events.WSMessageReceivedHandler;
import org.exoplatform.ide.client.framework.websocket.exceptions.WebSocketException;
import org.exoplatform.ide.client.framework.websocket.messages.Pair;
import org.exoplatform.ide.client.framework.websocket.messages.RESTfulRequestBuilder;
import org.exoplatform.ide.client.framework.websocket.messages.RESTfulRequestCallback;
import org.exoplatform.ide.client.framework.websocket.messages.RESTfulRequestMessage;
import org.exoplatform.ide.client.framework.websocket.messages.RESTfulResponseMessage;
import org.exoplatform.ide.client.framework.websocket.messages.WSEventHandler;

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
   private Map<String, Set<WSEventHandler<?>>> channelToSubscribersMap = new HashMap<String, Set<WSEventHandler<?>>>();

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

      RESTfulResponseMessage message =
         AutoBeanCodex.decode(WebSocket.AUTO_BEAN_FACTORY, RESTfulResponseMessage.class, eventMessage).as();
      if (message == null)
      {
         return;
      }

      String messageType = null;
      for (Pair header : message.getHeaders())
      {
         if ("x-everrest-websocket-message-type".equals(header.getName()))
         {
            if ("subscribed-message".equals(header.getValue()))
            {
               messageType = header.getValue();
            }
         }
      }

      if ("subscribed-message".equals(messageType))
      {
         for (Pair header : message.getHeaders())
         {
            if ("x-everrest-websocket-channel".equals(header.getName()))
            {
               if (channelToSubscribersMap.containsKey(header.getValue()))
               {
                  // TODO find way to avoid copying of set
                  // Copy a Set to avoid 'CuncurrentModificationException' when 'unsubscribe()' method will invoked while iterating
                  Set<WSEventHandler<?>> subscribersSet =
                     new HashSet<WSEventHandler<?>>(channelToSubscribersMap.get(header.getValue()));
                  for (WSEventHandler<?> handler : subscribersSet)
                  {
                     handler.onResponseReceived(message);
                  }
               }
            }
         }
      }

      // ignore the confirmation message
      for (Pair header : message.getHeaders())
         if (HTTPHeader.LOCATION.equals(header.getName()) && header.getValue().contains("async/"))
            return;

      RESTfulRequestCallback<?> callback = callbackMap.remove(message.getUuid());
      if (callback != null)
         callback.onResponseReceived(message);
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
    * @param handler the {@link WSEventHandler} to fire
    *                   when receiving an event on the subscribed channel
    */
   void subscribe(Channels channel, WSEventHandler<?> handler)
   {
      if (handler == null)
      {
         throw new NullPointerException("Handler may not be null");
      }

      Set<WSEventHandler<?>> subscribersSet = channelToSubscribersMap.get(channel.toString());
      if (subscribersSet != null)
      {
         subscribersSet.add(handler);
         return;
      }

      subscribersSet = new HashSet<WSEventHandler<?>>();
      subscribersSet.add(handler);
      channelToSubscribersMap.put(channel.toString(), subscribersSet);
      RESTfulRequestBuilder.build(RequestBuilder.GET, null)
         .header("x-everrest-websocket-message-type", "subscribe-channel")
         .data("{\"channel\":\"" + channel.toString() + "\"}").send(null);
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
    * @param handler the {@link WSEventHandler} for which to remove the subscription
    */
   void unsubscribe(Channels channel, WSEventHandler<?> handler)
   {
      if (handler == null)
      {
         throw new NullPointerException("Handler may not be null");
      }

      Set<WSEventHandler<?>> subscribersSet = channelToSubscribersMap.get(channel.toString());
      if (subscribersSet == null)
      {
         return;
      }

      if (subscribersSet.remove(handler) && subscribersSet.isEmpty())
      {
         channelToSubscribersMap.remove(channel.toString());
         RESTfulRequestBuilder.build(RequestBuilder.GET, null)
            .header("x-everrest-websocket-message-type", "unsubscribe-channel")
            .data("{\"channel\":\"" + channel.toString() + "\"}").send(null);
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
      // TODO
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
