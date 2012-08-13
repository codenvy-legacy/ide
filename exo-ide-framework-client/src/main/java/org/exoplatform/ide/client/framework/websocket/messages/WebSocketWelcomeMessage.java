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
package org.exoplatform.ide.client.framework.websocket.messages;

/**
 * The WELCOME message is send from the server to a client,
 * when the connection has successfully been established.
 * The WELCOME message is the only message that contains
 * the session identifier for the client.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: WebSocketWelcomeMessage.java Jul 13, 2012 5:14:28 PM azatsarynnyy $
 *
 */
public interface WebSocketWelcomeMessage extends WebSocketMessage
{
   /**
    * REturns a WebSocket session identifier.
    * 
    * @return WebSocket session identifier
    */
   String getSessionId();

   /**
    * Sets a WebSocket session identifier.
    * 
    * @param sessionId WebSocket session identifier
    */
   void setSessionId(String sessionId);
}
