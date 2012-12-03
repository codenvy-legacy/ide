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
import org.exoplatform.ide.client.framework.websocket.messages.SubscriptionHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * {@link MessageBus} provides two asynchronous messaging patterns: RPC and
 * list-based PubSub. Class maintains lists of channels and subscribers and
 * notifying each one individually a message received. Also used to publishing
 * messages to channel and to calling remote methods.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: MessageBus.java Jul 30, 2012 9:24:42 AM azatsarynnyy $
 *
 */
public class MessageBus implements WSMessageReceivedHandler
{
   /** Enumeration describes the WebSocket event types. */
   public enum Channels {
      /** Channel for the messages containing debugger events. */
      DEBUGGER_EVENTS("debugger:events:"),

      /** Channel for the messages containing the application names which may be stopped soon. */
      DEBUGGER_EXPIRE_SOON_APP("debugger:expireSoonApp:"),

      /** Channel for the messages containing message which informs about debugger is disconnected. */
      DEBUGGER_DISCONNECTED("debugger:disconnected:"),

      /** Channel for the messages containing status of the Maven build job. */
      MAVEN_BUILD_STATUS("maven:buildStatus:"),

      /** Channel for the messages containing status of the Jenkins job. */
      JENKINS_JOB_STATUS("jenkins:jobStatus:");

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
   protected Map<String, Set<SubscriptionHandler<?>>> channelToSubscribersMap =
      new HashMap<String, Set<SubscriptionHandler<?>>>();

   /**
    * Map of the call identifier to the {@link RESTfulRequestCallback}.
    */
   private Map<String, RESTfulRequestCallback<?>> callbackMap = new HashMap<String, RESTfulRequestCallback<?>>();

   /**
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

      // temporary ignore the confirmation message
      for (Pair header : message.getHeaders())
      {
         if (HTTPHeader.LOCATION.equals(header.getName()) && header.getValue().contains("async/"))
         {
            return;
         }
      }

      String messageType = getMessageType(message);
      if ("subscribed-message".equals(messageType))
      {
         processSubscribedMessage(message);
         return;
      }

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
    * @param channelID channel identifier
    * @param handler the {@link SubscriptionHandler} to fire
    *                   when receiving an event on the subscribed channel
    */
   protected void subscribe(String channelID, SubscriptionHandler<?> handler)
   {
      if (handler == null)
      {
         throw new NullPointerException("Handler may not be null");
      }

      Set<SubscriptionHandler<?>> subscribersSet = channelToSubscribersMap.get(channelID);
      if (subscribersSet != null)
      {
         subscribersSet.add(handler);
         return;
      }

      subscribersSet = new HashSet<SubscriptionHandler<?>>();
      subscribersSet.add(handler);
      channelToSubscribersMap.put(channelID, subscribersSet);
      RESTfulRequestBuilder.build(RequestBuilder.GET, null)
         .header("x-everrest-websocket-message-type", "subscribe-channel").data("{\"channel\":\"" + channelID + "\"}")
         .send(null);
   }

   /**
    * Unregisters existing subscriber to receive messages on a particular channel.
    * If it's the last unsubscribe to a channel, a message is sent to the server to
    * unsubscribe the client for that channel.
    * 
    * <p><strong>Note:</strong> the method runs asynchronously and does not provide
    * feedback whether a unsubscription was successful or not.
    * 
    * @param channelID channel identifier
    * @param handler the {@link SubscriptionHandler} for which to remove the subscription
    */
   protected void unsubscribe(String channelID, SubscriptionHandler<?> handler)
   {
      if (handler == null)
      {
         throw new NullPointerException("Handler may not be null");
      }

      Set<SubscriptionHandler<?>> subscribersSet = channelToSubscribersMap.get(channelID);
      if (subscribersSet == null)
      {
         return;
      }

      if (subscribersSet.remove(handler) && subscribersSet.isEmpty())
      {
         channelToSubscribersMap.remove(channelID);
         RESTfulRequestBuilder.build(RequestBuilder.GET, null)
            .header("x-everrest-websocket-message-type", "unsubscribe-channel")
            .data("{\"channel\":\"" + channelID + "\"}").send(null);
      }
   }

   /**
    * Publishes a message in a particular channel.
    * 
    * @param channelID channel identifier
    * @param data the text data to be published to the channel
    * @throws WebSocketException throws if an error has occurred while publishing data
    */
   public void publish(String channelID, String data) throws WebSocketException
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

   /**
    * Returns type of the provided message(e.g., subscribed-message, pong, ...).
    * 
    * @param message {@link RESTfulResponseMessage}
    * @return type of the message
    */
   private String getMessageType(RESTfulResponseMessage message)
   {
      for (Pair header : message.getHeaders())
      {
         if ("x-everrest-websocket-message-type".equals(header.getName()))
         {
            return header.getValue();
         }
      }
      return null;
   }

   /**
    * Process the message that received by subscription.
    * 
    * @param message {@link RESTfulResponseMessage}
    */
   private void processSubscribedMessage(RESTfulResponseMessage message)
   {
      for (Pair header : message.getHeaders())
      {
         if ("x-everrest-websocket-channel".equals(header.getName())
            && channelToSubscribersMap.containsKey(header.getValue()))
         {
            // TODO find way to avoid copying of set
            // Copy a Set to avoid 'CuncurrentModificationException' when 'unsubscribe()' method will invoked while iterating
            Set<SubscriptionHandler<?>> subscribersSet =
               new HashSet<SubscriptionHandler<?>>(channelToSubscribersMap.get(header.getValue()));
            for (SubscriptionHandler<?> handler : subscribersSet)
            {
               handler.onResponseReceived(message);
            }
         }
      }
   }

}
