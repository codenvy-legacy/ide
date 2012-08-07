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
import org.exoplatform.ide.helper.JsonHelper;
import org.exoplatform.ide.helper.ParsingResponseException;
import org.exoplatform.ide.websocket.WebSocketMessage.Type;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.io.IOException;
import java.nio.CharBuffer;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Class used for managing WebSocket connections and sending messages to clients.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: WebSocketManager.java Jun 20, 2012 5:10:29 PM azatsarynnyy $
 *
 */
public class WebSocketManager
{
   /**
    * Enumeration describing the WebSocket message event types.
    */
   public enum Channels {
      /**
       * Event type for message that contains status of the Maven build job.
       */
      MAVEN_BUILD_STATUS("mavenBuildStatus"),

      /**
       * Event type for message that contains status of the Jenkins build job.
       */
      JENKINS_BUILD_STATUS("jenkinsBuildStatus"),

      /**
       * Event type for message that contains debugger events.
       */
      DEBUGGER_EVENTS("debuggerEvents"),

      /**
       * Indicates that the git-repository has been initialized.
       */
      GIT_REPO_INITIALIZED("gitRepoInitialized"),

      /**
       * Indicates that the git-repository has been cloned.
       */
      GIT_REPO_CLONED("gitRepoCloned"),

      /**
       * Indicates that Heroku application has been created.
       */
      HEROKU_APP_CREATED("herokuAppCreated");

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
    * Exo logger.
    */
   private static final Log LOG = ExoLogger.getLogger(WebSocketManager.class);

   /**
    * Map of the session identifier to the connection.
    */
   // TODO
   // If new session id will be generated for every new connection then
   // use  Map<String, MessageInbound> instead of Map<String, CopyOnWriteArraySet<MessageInbound>>
   private Map<String, CopyOnWriteArraySet<MessageInbound>> sessionToConnection =
      new ConcurrentHashMap<String, CopyOnWriteArraySet<MessageInbound>>();

   /**
    * Map of the channel to the subscribers.
    * Used for publish messages to clients which subscribed
    * to receive messages on a particular channel.
    */
   private Map<String, CopyOnWriteArraySet<String>> channelToSubscribers =
      new ConcurrentHashMap<String, CopyOnWriteArraySet<String>>();

   /**
    * Register user connection in active connection list.
    * 
    * @param sessionId identifier of the WebSocket session
    * @param connection WebSocket connection
    */
   public void registerConnection(String sessionId, MessageInbound connection)
   {
      if (sessionId == null)
      {
         throw new NullPointerException("Session identifier must not be null");
      }
      if (connection == null)
      {
         throw new NullPointerException("Connection must not be null");
      }

      CopyOnWriteArraySet<MessageInbound> connectionsSet = sessionToConnection.get(sessionId);
      if (connectionsSet != null)
      {
         connectionsSet.add(connection);
      }
      else
      {
         connectionsSet = new CopyOnWriteArraySet<MessageInbound>();
         connectionsSet.add(connection);
         sessionToConnection.put(sessionId, connectionsSet);
      }

      try
      {
         send(sessionId, new WebSocketWelcomeMessage(sessionId));
      }
      catch (IOException e)
      {
         LOG.warn("An error occurs sending data to client over WebSocket", e);
      }
   }

   /**
    * Remove WebSocket connection from connections registry and unsubscribe
    * the client with the given session identifier from the all channels.
    * 
    * @param sessionId identifier of the WebSocket session
    * @param inbound WebSocket connection
    */
   public void unregisterConnection(String sessionId, MessageInbound inbound)
   {
      unsubscribe(sessionId, null);
      CopyOnWriteArraySet<MessageInbound> connectionsSet = sessionToConnection.get(sessionId);
      if (connectionsSet != null)
      {
         if (connectionsSet.remove(inbound) && connectionsSet.isEmpty())
         {
            sessionToConnection.remove(sessionId);
         }
      }
   }

   /**
    * Parse and process incoming message.
    * 
    * @param message incoming message
    */
   public void onMessage(String sessionId, String message)
   {
      String type = null;
      try
      {
         JsonValue jsonValue = JsonHelper.parseJson(message.toString());
         if (jsonValue != null && jsonValue.isObject())
         {
            type = jsonValue.getElement("type").getStringValue();
         }

         if (Type.SUBSCRIBE.name().equals(type))
         {
            WebSocketSubscribeMessage webSocketMessage = new WebSocketSubscribeMessage(message);
            subscribe(sessionId, webSocketMessage.getChannel());
         }
         else if (Type.UNSUBSCRIBE.name().equals(type))
         {
            WebSocketSubscribeMessage webSocketMessage = new WebSocketSubscribeMessage(message);
            unsubscribe(sessionId, webSocketMessage.getChannel());
         }
         else if (Type.PUBLISH.name().equals(type))
         {
            WebSocketPublishMessage webSocketMessage = new WebSocketPublishMessage(message);
            publish(webSocketMessage.getChannel(), webSocketMessage.getPayload(), null, sessionId);
         }
         else if (Type.CALL.name().equals(type))
         {
            WebSocketCallMessage webSocketMessage = new WebSocketCallMessage(message);
            call(sessionId, webSocketMessage.getCallId(), webSocketMessage.getPayload());
         }
      }
      catch (ParsingResponseException e)
      {
         LOG.warn("An error occurs parsing the WebSocket message", e);
      }
      catch (IOException e)
      {
         LOG.warn("An error occurs sending data to client over WebSocket", e);
      }
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

      CopyOnWriteArraySet<String> subscribersSet = channelToSubscribers.get(sessionId);
      if (subscribersSet != null)
      {
         subscribersSet.add(sessionId);
      }
      else
      {
         subscribersSet = new CopyOnWriteArraySet<String>();
         subscribersSet.add(sessionId);
         channelToSubscribers.put(channel, subscribersSet);
      }
   }

   /**
    * Unsubscribes the client to receive messages on a particular channel or the all channels.
    * 
    * @param sessionId client's session identifier
    * @param channel channel name. If <code>null</code> - client will be unsubscribed to all subscriptions.
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
    * Publishes message in a particular channel.
    * 
    * @param channel channel name
    * @param message the message
    * @param an exception to be sent to the client. May be </code>null<code>
    * @param excludeSessionId
    * @throws IOException 
    */
   public void publish(String channel, String message, Exception e, String excludeSessionId) throws IOException
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

   private void call(String sessionId, String callId, String data) throws IOException
   {
      // TODO
      // processCall
      // send result to caller callback
      //send(sessionId, new WebSocketCallResultMessage(callId, "\"" + result + "\""));
   }

   /**
    * Sends the message to client.
    * <p><strong>Note:</strong> if user has more than one active
    * connections with the same session identifier then message
    * will be sent to all connections.
    * 
    * @param sessionId identifier of the WebSocket connection
    * @param message the {@link WebSocketMessage} to be sent to the client
    * @throws IOException if an error occurs writing to the client
    */
   private void send(String sessionId, WebSocketMessage message) throws IOException
   {
      send(sessionId, message.toString());
   }

   /**
    * Sends the message to client.
    * <p><strong>Note:</strong> if user has more than one active
    * connections with the same session identifier then message
    * will be sent to all connections.
    * 
    * @param sessionId identifier of the WebSocket session
    * @param message the message to be sent to the client
    * @throws IOException if an error occurs writing to the client
    */
   private void send(String sessionId, String message) throws IOException
   {
      CopyOnWriteArraySet<MessageInbound> connectionsSet = sessionToConnection.get(sessionId);
      if (connectionsSet == null)
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
