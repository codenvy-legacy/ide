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
 * Interface represents the WebSocket message for a Remote Procedure Call.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: WebSocketCallMessage.java Jul 31, 2012 5:14:28 PM azatsarynnyy $
 *
 */
public interface WebSocketCallMessage extends WebSocketMessage
{
   /**
    * Returns the call identifier which allow the client to assign
    * a certain result to a corresponding previous request.
    * 
    * @return call identifier
    */
   String getCallId();

   /**
    * Sets the call identifier which allow the client to assign
    * a certain result to a corresponding previous request.
    * 
    * @param callId call identifier
    */
   void setCallId(String callId);

   String getProcId();

   void setProcId(String procId);

   String getPayload();

   void setPayload(String payload);
}
