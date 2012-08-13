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

/**
 * The WELCOME message is send from the server to a client,
 * when the connection has successfully been established.
 * The WELCOME message is the only message that contains
 * the session identifier for the client.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: WebSocketCallMessage.java Jul 31, 2012 5:14:28 PM azatsarynnyy $
 *
 */
public class WebSocketWelcomeMessage extends WebSocketMessage
{
   /**
    * WebSocket session identifier.
    */
   private String sessionId;

   /**
    * Constructs a new message with a given session identifier.
    * 
    * @param sessionId
    */
   public WebSocketWelcomeMessage(String sessionId)
   {
      this.type = Type.WELCOME.toString();
      this.sessionId = sessionId;
   }

   /**
    * Sets a WebSocket session identifier.
    * 
    * @param sessionId WebSocket session identifier
    */
   public void setSessionId(String sessionId)
   {
      this.sessionId = sessionId;
   }

   /**
    * Returns WebSocket session identifier.
    * 
    * @return WebSocket session identifier
    */
   public String getSessionId()
   {
      return sessionId;
   }

   /**
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      return "{\"type\":\"" + type + "\", " + "\"sessionId\":\"" + sessionId + "\"}";
   }
}
