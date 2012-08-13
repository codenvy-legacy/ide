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
 * Class represents the WebSocket message.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: WebSocketCallMessage.java Jul 31, 2012 5:14:28 PM azatsarynnyy $
 *
 */
public class WebSocketMessage
{
   /**
    * Enum defines the standard WebSocket message types for interaction between client and server.
    */
   public enum Type {
      WELCOME, //
      PUBLISH, //
      EVENT, //
      CALL, //
      CALL_RESULT, //
      SUBSCRIBE, //
      UNSUBSCRIBE;
   }

   /**
    * TYpe of the WebSocket message.
    */
   protected String type;

   /**
    * Returns a type of the WebScoket message.
    * 
    * @return message type
    */
   public String getType()
   {
      return type;
   }

   /**
    * Sets a type of the WebScoket message.
    * 
    * @param type message type
    */
   public void setType(String type)
   {
      this.type = type;
   }
}
