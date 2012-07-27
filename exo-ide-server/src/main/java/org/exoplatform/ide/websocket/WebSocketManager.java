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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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
      GIT_REPO_CLONED("gitRepoCloned");

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
    * Stores user connections.
    */
   private ConcurrentMap<String, List<MessageInbound>> connections = new ConcurrentHashMap<String, List<MessageInbound>>();

   /**
    * Register user connection in active connection list.
    * 
    * @param sessionId identifier of the WebSocket session
    * @param inbound inbound connection
    */
   public void registerConnection(String sessionId, MessageInbound inbound)
   {
      List<MessageInbound> userConnectionsList = connections.get(sessionId);
      if (userConnectionsList != null)
      {
         userConnectionsList.add(inbound);
      }
      else
      {
         userConnectionsList = new ArrayList<MessageInbound>();
         userConnectionsList.add(inbound);
         connections.put(sessionId, userConnectionsList);
      }
   }

   /**
    * Remove user connection from registered connection list.
    * 
    * @param sessionId identifier of the WebSocket session
    * @param inbound inbound connection
    */
   public void deregisterConnection(String sessionId, MessageInbound inbound)
   {
      List<MessageInbound> userConnectionsList = connections.get(sessionId);
      if (userConnectionsList == null)
      {
         return;
      }

      userConnectionsList.remove(inbound);
      if (userConnectionsList.isEmpty())
      {
         connections.remove(sessionId);
      }
   }

   /**
    * Sends the message to client.
    * <p><strong>Note:</strong> if user has more than one active
    * connections with the same session identifier then message
    * will be sent to all connections.
    * 
    * @param sessionId identifier of the WebSocket session
    * @param eventType {@link EventType}
    * @param data the data to be sent to the client
    * @param e an exception to be sent to the client
    * @throws IOException if an error occurs writing to the client
    */
   public void send(String sessionId, EventType eventType, String data, Exception e) throws IOException
   {
      String exception = null;
      if (e != null)
      {
         exception = "{\"type\":\"" + e.getClass().getSimpleName() + "\",\"message\":\"" + e.getMessage() + "\"}";
      }

      if (data == null || data.trim().isEmpty())
      {
         data = "{}";
      }

      String wsMessage =
         "{\"event\":\"" + eventType + "\", \"data\":" + data + ", " + "\"exception\":" + exception + "}";

      List<MessageInbound> userConnectionsList = connections.get(sessionId);
      if (userConnectionsList == null)
      {
         throw new IllegalArgumentException("WebSocket session with ID: " + sessionId + " not found.");
      }

      for (MessageInbound messageInbound : userConnectionsList)
      {
         WsOutbound wsOut = messageInbound.getWsOutbound();
         wsOut.writeTextMessage(CharBuffer.wrap(wsMessage));
         wsOut.flush();
      }
   }
}
