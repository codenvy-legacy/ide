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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Component used for sending messages to client via WebSocket connections.
 * Stores user WebSocket-connections.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: IDEWebSocketDispatcher.java Jun 20, 2012 5:10:29 PM azatsarynnyy $
 *
 */
public class IDEWebSocketDispatcher
{
   /**
    * Stores user connections.
    */
   private Map<String, List<MessageInbound>> connections = new HashMap<String, List<MessageInbound>>();

   /**
    * Add user connection.
    * 
    * @param userName connected user name
    * @param inbound inbound connection
    */
   public void addConnection(String userName, MessageInbound inbound)
   {
      List<MessageInbound> userConnectionsList = connections.get(userName);
      if (userConnectionsList != null)
      {
         userConnectionsList.add(inbound);
      }
      else
      {
         userConnectionsList = new ArrayList<MessageInbound>();
         userConnectionsList.add(inbound);
         connections.put(userName, userConnectionsList);
      }
   }

   /**
    * Remove user's connection.
    * 
    * @param userName disconnected user name
    * @param inbound inbound connection
    */
   public void removeConnection(String userName, MessageInbound inbound)
   {
      List<MessageInbound> userConnectionsList = connections.get(userName);
      if (userConnectionsList != null)
      {
         userConnectionsList.remove(inbound);
      }
      if (userConnectionsList.isEmpty())
      {
         connections.remove(userName);
      }
   }

   /**
    * Sends the text message to the client.
    * <p>NOTE: if user has more than one active connections then message will be sent to all connections.
    * 
    * @param userId identifier of the connected user
    * @param message text message for sending
    * @param eventType event type
    * @throws IOException if an error occurs writing to the client
    */
   public void sendMessageToClient(String userId, String message, String eventType) throws IOException
   {
      String wsMessage = "{\"event\":\"" + eventType + "\"," + "\"data\":" + message + "}";

      List<MessageInbound> userConnectionsList = connections.get(userId);
      if (userConnectionsList == null)
      {
         // TODO
         return;
      }

      for (MessageInbound messageInbound : userConnectionsList)
      {
         WsOutbound wsOut = messageInbound.getWsOutbound();
         wsOut.writeTextMessage(CharBuffer.wrap(wsMessage));
         wsOut.flush();
      }
   }
}
