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

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import org.exoplatform.ide.client.framework.websocket.events.WebSocketMessageEvent;
import org.exoplatform.ide.client.framework.websocket.events.WebSocketMessageHandler;
import org.exoplatform.ide.client.framework.websocket.messages.WebSocketCallMessage;
import org.exoplatform.ide.client.framework.websocket.messages.WebSocketCallResultMessage;
import org.exoplatform.ide.client.framework.websocket.messages.WebSocketEventMessage;
import org.exoplatform.ide.client.framework.websocket.messages.WebSocketMessage;
import org.exoplatform.ide.client.framework.websocket.messages.WebSocketMessage.Type;
import org.exoplatform.ide.client.framework.websocket.messages.WebSocketPublishMessage;
import org.exoplatform.ide.client.framework.websocket.messages.WebSocketSubscribeMessage;
import org.exoplatform.ide.client.framework.websocket.messages.WebSocketWelcomeMessage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
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
public class MessageBus implements WebSocketMessageHandler
{
   /** Enumeration describing the WebSocket event types. */
   public enum Channels {
      /** Channel for the messages containing status of the Maven build job. */
      MAVEN_BUILD_STATUS("maven:buildStatus"),

      /** Channel for the messages containing status of the Jenkins build job. */
      JENKINS_BUILD_STATUS("jenkins:buildStatus"),

      /** Channel for the messages containing started application instance. */
      APP_STARTED("debugger:appStarted"),

      /** Channel for the messages containing started application instance for debugging. */
      DEBUGGER_STARTED("debugger:debugAppStarted"),

      /** Channel for the messages containing debugger event. */
      DEBUGGER_EVENT("debugger:event"),

      /** Channel for the messages containing the debugger event. */
      DEBUGGER_EXPIRE_SOON_APPS("debugger:expireSoonApps"),

      /** Channel for the messages indicating the Git repository has been initialized. */
      GIT_REPO_INITIALIZED("git:repoInitialized"),

      /** Channel for the messages indicating the Git repository has been cloned. */
      GIT_REPO_CLONED("git:repoCloned"),

      /** Channel for the messages indicating Heroku application has been created. */
      HEROKU_APP_CREATED("heroku:appCreated");

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
   private Map<String, Set<WebSocketEventHandler>> channelToSubscribersMap =
      new HashMap<String, Set<WebSocketEventHandler>>();

   /**
    * Map of the call identifier to the {@link WebSocketRPCResultCallback}.
    */
   private Map<String, WebSocketRPCResultCallback> callbackMap = new HashMap<String, WebSocketRPCResultCallback>();

   /**
    * Receive and process WebSocket messages.
    * 
    * @see org.exoplatform.ide.client.framework.websocket.events.WebSocketMessageHandler
    *       #onWebSocketMessage(org.exoplatform.ide.client.framework.websocket.events.WebSocketMessageEvent)
    */
   @Override
   public void onWebSocketMessage(WebSocketMessageEvent event)
   {
      String eventMessage = event.getMessage();
      if (eventMessage == null)
      {
         return;
      }

      JSONObject jsonObject = JSONParser.parseStrict(eventMessage).isObject();
      if (jsonObject == null)
      {
         return;
      }
      String messageType = null;
      if (!jsonObject.containsKey("type"))
      {
         return;
      }
      messageType = jsonObject.get("type").isString().stringValue();

      if (Type.WELCOME.name().equals(messageType))
      {
         WebSocketWelcomeMessage message =
            AutoBeanCodex.decode(WebSocket.AUTO_BEAN_FACTORY, WebSocketWelcomeMessage.class, eventMessage).as();
         WebSocket.getInstance().getSession().setId(message.getSessionId());
      }
      else if (Type.EVENT.name().equals(messageType))
      {
         WebSocketEventMessage message =
            AutoBeanCodex.decode(WebSocket.AUTO_BEAN_FACTORY, WebSocketEventMessage.class, eventMessage).as();
         if (channelToSubscribersMap.containsKey(message.getChannel()))
         {
            // copy the Set to avoid 'CuncurrentModificationException' when 'unsubscribe()' method will be invoke
            Set<WebSocketEventHandler> subscribersSet =
               new HashSet<WebSocketEventHandler>(channelToSubscribersMap.get(message.getChannel()));
            for (WebSocketEventHandler webSocketEventHandler : subscribersSet)
            {
               webSocketEventHandler.onWebSocketEvent(message);
            }
         }
      }
      else if (Type.CALL_RESULT.name().equals(messageType))
      {
         WebSocketCallResultMessage message =
            AutoBeanCodex.decode(WebSocket.AUTO_BEAN_FACTORY, WebSocketCallResultMessage.class, eventMessage).as();
         WebSocketRPCResultCallback callback = callbackMap.remove(message.getCallId());
         if (callback != null)
         {
            callback.onResult(message);
         }
      }
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
    * @param channel channel identifier
    * @param webSocketEventHandler the {@link WebSocketEventHandler} to fire
    *                   when receiving an event on the subscribed channel
    * @throws WebSocketException if an error has occurred while sending data
    */
   public void subscribe(Channels channel, WebSocketEventHandler webSocketEventHandler) throws WebSocketException
   {
      subscribe(channel.toString(), webSocketEventHandler);
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
    * @param channel channel identifier
    * @param webSocketEventHandler the {@link WebSocketEventHandler} to fire
    *                   when receiving an event on the subscribed channel
    * @throws WebSocketException if an error has occurred while sending data
    */
   public void subscribe(String channel, WebSocketEventHandler webSocketEventHandler) throws WebSocketException
   {
      if (webSocketEventHandler == null)
      {
         throw new NullPointerException("Subscriber must not be null");
      }

      Set<WebSocketEventHandler> subscribersSet = channelToSubscribersMap.get(channel);
      if (subscribersSet != null)
      {
         subscribersSet.add(webSocketEventHandler);
         return;
      }

      subscribersSet = new HashSet<WebSocketEventHandler>();
      subscribersSet.add(webSocketEventHandler);
      channelToSubscribersMap.put(channel, subscribersSet);

      WebSocketSubscribeMessage message = WebSocket.AUTO_BEAN_FACTORY.webSocketSubscribeMessage().as();
      message.setType(Type.SUBSCRIBE);
      message.setChannel(channel);

      AutoBean<WebSocketSubscribeMessage> webSocketSubscribeMessageBean =
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
    * @param channel channel identifier
    * @param webSocketEventHandler the {@link WebSocketEventHandler} for which to remove the subscription
    */
   public void unsubscribe(Channels channel, WebSocketEventHandler webSocketEventHandler)
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
    * @param webSocketEventHandler the {@link WebSocketEventHandler} for which to remove the subscription
    */
   public void unsubscribe(String channel, WebSocketEventHandler webSocketEventHandler)
   {
      Set<WebSocketEventHandler> subscribersSet = channelToSubscribersMap.get(channel);
      if (subscribersSet == null)
      {
         return;
      }

      if (subscribersSet.remove(webSocketEventHandler) && subscribersSet.isEmpty())
      {
         channelToSubscribersMap.remove(channel);
         WebSocketSubscribeMessage message = WebSocket.AUTO_BEAN_FACTORY.webSocketSubscribeMessage().as();
         message.setType(Type.UNSUBSCRIBE);
         message.setChannel(channel);

         AutoBean<WebSocketSubscribeMessage> webSocketUnsubscribeMessageBean =
            WebSocket.AUTO_BEAN_FACTORY.webSocketSubscribeMessage(message);
         try
         {
            WebSocket.getInstance().send(AutoBeanCodex.encode(webSocketUnsubscribeMessageBean).getPayload());
         }
         catch (WebSocketException e)
         {
            // do nothing
         }
      }
   }

   /**
    * Calls remote procedure.
    * 
    * @param procId remote procedure identifier
    * @param data data which will be sent
    * @param callback handler which will be called when a reply from the server is received
    * @throws WebSocketException throws if an error has occurred while sending data
    */
   public void call(String procId, String data, WebSocketRPCResultCallback callback) throws WebSocketException
   {
      WebSocketCallMessage message = WebSocket.AUTO_BEAN_FACTORY.webSocketCallMessage().as();
      message.setType(WebSocketMessage.Type.CALL);
      message.setCallId(generateCallId());
      message.setProcId(procId);
      message.setPayload(data);

      callbackMap.put(message.getCallId(), callback);

      AutoBean<WebSocketCallMessage> webSocketCallMessageBean =
         WebSocket.AUTO_BEAN_FACTORY.webSocketCallMessage(message);
      WebSocket.getInstance().send(AutoBeanCodex.encode(webSocketCallMessageBean).getPayload());
   }

   /**
    * Publishes a message in a particular channel.
    * 
    * @param channel channel identifier
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
      WebSocketPublishMessage message = WebSocket.AUTO_BEAN_FACTORY.webSocketPublishMessage().as();
      message.setType(WebSocketMessage.Type.PUBLISH);
      message.setChannel(channel);
      message.setPayload(data);

      AutoBean<WebSocketPublishMessage> webSocketPublishMessageBean =
         WebSocket.AUTO_BEAN_FACTORY.webSocketPublishMessage();
      WebSocket.getInstance().send(AutoBeanCodex.encode(webSocketPublishMessageBean).getPayload());
   }

   /**
    * Returns randomly generated identifier.
    * 
    * @return a randomly generated <tt>CallId</tt>.
    */
   private String generateCallId()
   {
      Random rand = new Random();
      String id = "";

      for (int i = 0; i < 12; i++)
      {
         int r = rand.nextInt(62);
         if (r > 35)
            id += (char)(r + 61);
         else if (r > 9)
            id += (char)(r + 55);
         else
            id += r;
      }

      return id;
   }
}
