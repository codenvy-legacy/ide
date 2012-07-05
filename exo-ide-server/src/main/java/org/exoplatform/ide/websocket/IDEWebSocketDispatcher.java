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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class used for sending messages to client via WebSocket connections.
 * Stores WebSocket connections.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: IDEWebSocketDispatcher.java Jun 20, 2012 5:10:29 PM azatsarynnyy $
 *
 */
public class IDEWebSocketDispatcher
{
   public enum EventType
   {
      /**
       * Maven build job status.
       */
      BUILD_STATUS("buildStatus"),

      /**
       * Debugger events.
       */
      DEBUGGER_EVENTS("debuggerEvents");

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
   private Map<String, List<MessageInbound>> connections = new ConcurrentHashMap<String, List<MessageInbound>>();

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
   public void unregisterConnection(String sessionId, MessageInbound inbound)
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
    * Sends the text message to client.
    * <p><strong>Note:</strong> if user has more than one active connections then message will be sent to all connections.
    * 
    * @param sessionId identifier of the WebSocket session
    * @param message text message for sending
    * @param eventType event type
    * @throws IOException if an error occurs writing to the client
    */
   public void sendEventMessage(String sessionId, String message, EventType eventType) throws IOException
   {
      String wsMessage = "{\"event\":\"" + eventType + "\"," + "\"data\":" + message + "}";

      List<MessageInbound> userConnectionsList = connections.get(sessionId);
      if (userConnectionsList == null)
      {
         throw new IllegalArgumentException("WebSocket session " + sessionId + " not found.");
      }

      for (MessageInbound messageInbound : userConnectionsList)
      {
         WsOutbound wsOut = messageInbound.getWsOutbound();
         wsOut.writeTextMessage(CharBuffer.wrap(wsMessage));
         wsOut.flush();
      }
   }

}
