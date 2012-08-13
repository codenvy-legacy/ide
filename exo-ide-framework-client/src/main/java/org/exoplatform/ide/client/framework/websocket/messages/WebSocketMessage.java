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

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

/**
 * Interface represents the WebSocket message.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: WebSocketMessage.java Jul 13, 2012 5:14:28 PM azatsarynnyy $
 *
 */
public interface WebSocketMessage
{
   /**
    * Enum of the WebSocket message types.
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
    * Returns a message type.
    * 
    * @return message type
    */
   @PropertyName("type")
   Type getType();

   /**
    * Sets the message type.
    * 
    * @param type message type
    */
   void setType(Type type);
}
