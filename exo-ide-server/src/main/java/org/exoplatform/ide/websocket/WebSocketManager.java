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

import java.io.IOException;
import java.nio.CharBuffer;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
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
   public enum EventType {
      /**
       * Event type for message with session identifier for client.
       */
      WELCOME("welcome"),

      /**
       * Event type for subscribing to receive messages on a particular message topic.
       */
      SUBSCRIBE("subscribe"),

      /**
       * Event type for unsubscribing to receive messages on a particular message topic.
       */
      UNSUBSCRIBE("unsubscribe"),

      /**
       * Event type for publishing message with a particular message topic.
       */
      PUBLISH("publish"),

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

      private EventType(String value)
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
    * Stores connection identifiers mapped to the connections themselves.
    * Used for sending message to the client by identifier.
    */
   private ConcurrentMap<String, CopyOnWriteArraySet<MessageInbound>> connections =
      new ConcurrentHashMap<String, CopyOnWriteArraySet<MessageInbound>>();

   /**
    * Stores event types mapped to the subscribers.
    * Used for publish messages to the clients which subscribed
    * to receive messages on a particular message topic.
    */
   private ConcurrentMap<String, CopyOnWriteArraySet<String>> events =
      new ConcurrentHashMap<String, CopyOnWriteArraySet<String>>();

   /**
    * Register user connection in active connection list.
    * 
    * @param sessionId identifier of the WebSocket session
    * @param inbound inbound connection
    */
   public void registerConnection(String sessionId, MessageInbound inbound)
   {
      if (sessionId == null)
      {
         throw new NullPointerException("Session identifier must not be null");
      }
      if (inbound == null)
      {
         throw new NullPointerException("Inbound must not be null");
      }

      CopyOnWriteArraySet<MessageInbound> connectionsSet = connections.get(sessionId);
      if (connectionsSet != null)
      {
         connectionsSet.add(inbound);
      }
      else
      {
         connectionsSet = new CopyOnWriteArraySet<MessageInbound>();
         connectionsSet.add(inbound);
         connections.put(sessionId, connectionsSet);
      }
   }

   /**
    * Remove user connection from registered connection list.
    * 
    * @param sessionId identifier of the WebSocket session
    * @param inbound inbound connection
    */
   public void unregisterConnection(String sessionId, MessageInbound inbound)
   {
      CopyOnWriteArraySet<MessageInbound> connectionsSet = connections.get(sessionId);
      if (connectionsSet != null)
      {
         connectionsSet.remove(inbound);
         if (connectionsSet.isEmpty())
         {
            connections.remove(sessionId);
         }
      }
   }

   /**
    * Subscribes client to receive messages on a particular message topic.
    * 
    * @param sessionId client's session identifier
    * @param topicId topic identifier
    */
   public void subscribe(String sessionId, String topicId)
   {
      if (sessionId == null)
      {
         throw new NullPointerException("Session identifier must not be null");
      }
      if (topicId == null)
      {
         throw new NullPointerException("Topic identifier must not be null");
      }

      CopyOnWriteArraySet<String> subscribersSet = events.get(sessionId);
      if (subscribersSet != null)
      {
         subscribersSet.add(sessionId);
      }
      else
      {
         subscribersSet = new CopyOnWriteArraySet<String>();
         subscribersSet.add(sessionId);
         events.put(topicId, subscribersSet);
      }
   }

   /**
    * Unsubscribes client to receive messages on a particular message topic.
    * 
    * @param sessionId client's session identifier
    * @param topicId topic identifier. If <code>null</code> - client will be unsubscribed to all subscriptions.
    */
   public void unsubscribe(String sessionId, String topicId)
   {
      if (topicId == null)
      {
         // unsubscribe client to all subscriptions
         ConcurrentHashMap<String, CopyOnWriteArraySet<String>> eventsCopy =
            new ConcurrentHashMap<String, CopyOnWriteArraySet<String>>(events);
         for (Entry<String, CopyOnWriteArraySet<String>> entry : eventsCopy.entrySet())
         {
            CopyOnWriteArraySet<String> connectionsSet = entry.getValue();
            connectionsSet.remove(sessionId);
            if (connectionsSet.isEmpty())
            {
               events.remove(entry.getKey());
            }
         }
         return;
      }

      CopyOnWriteArraySet<String> subscribersSet = events.get(topicId);
      if (subscribersSet != null)
      {
         subscribersSet.remove(sessionId);
         if (subscribersSet.isEmpty())
         {
            events.remove(topicId);
         }
      }
   }

   /**
    * Publishes message with a particular topic.
    * 
    * @param topicId topic identifier
    * @param message the message
    * @param e an exception
    * @throws IOException
    */
   public void publish(String topicId, String message, Exception e) throws IOException
   {
      if (events.containsKey(topicId))
      {
         CopyOnWriteArraySet<String> subscribersSet = events.get(topicId);
         for (String subscriber : subscribersSet)
         {
            send(subscriber, topicId, message, e);
         }
      }
   }

   /**
    * Sends the message to client.
    * <p><strong>Note:</strong> if user has more than one active
    * connections with the same session identifier then message
    * will be sent to all connections.
    * 
    * @param sessionId identifier of the WebSocket session
    * @param eventType event type
    * @param data the data to be sent to the client
    * @param e an exception to be sent to the client
    * @throws IOException if an error occurs writing to the client
    */
   public void send(String sessionId, EventType eventType, String data, Exception e) throws IOException
   {
      send(sessionId, eventType.toString(), data, e);
   }

   /**
    * Sends the message to client.
    * <p><strong>Note:</strong> if user has more than one active
    * connections with the same session identifier then message
    * will be sent to all connections.
    * 
    * @param sessionId identifier of the WebSocket session
    * @param eventType event type
    * @param data the data to be sent to the client
    * @param e an exception to be sent to the client
    * @throws IOException if an error occurs writing to the client
    */
   public void send(String sessionId, String eventType, String data, Exception e) throws IOException
   {
      String exception = null;
      if (e != null)
      {
         String errorMessage = e.getMessage().replaceAll("\n", " ");
         exception = "{\"type\":\"" + e.getClass().getSimpleName() + "\",\"message\":\"" + errorMessage + "\"}";
      }

      if (data == null || data.trim().isEmpty())
      {
         data = "{}";
      }

      String wsMessage =
         "{\"event\":\"" + eventType + "\", \"data\":" + data + ", " + "\"exception\":" + exception + "}";

      CopyOnWriteArraySet<MessageInbound> connectionsSet = connections.get(sessionId);
      if (connectionsSet == null)
      {
         throw new IllegalArgumentException("Client's session with ID " + sessionId + " not found.");
      }

      for (MessageInbound messageInbound : connectionsSet)
      {
         WsOutbound wsOut = messageInbound.getWsOutbound();
         wsOut.writeTextMessage(CharBuffer.wrap(wsMessage));
         wsOut.flush();
      }
   }
}
