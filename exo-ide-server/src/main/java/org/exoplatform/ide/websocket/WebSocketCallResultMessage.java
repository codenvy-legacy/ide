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
 * Class represents the WebSocket message which is returned
 * for a Remote Procedure Call and contains the result of call.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: WebSocketCallResultMessage.java Jul 31, 2012 5:14:28 PM azatsarynnyy $
 *
 */
public class WebSocketCallResultMessage extends WebSocketMessage
{
   /**
    * The server always returns this call identifier so that
    * the client can easily associate a result with the
    * corresponding previous request.
    */
   private String callId;

   /**
    * Result of a request.
    */
   private String payload;

   public WebSocketCallResultMessage(String callId, String payload)
   {
      this.type = Type.CALL_RESULT.toString();
      this.callId = callId;
      this.payload = payload;
   }

   /**
    * Sets the call identifier which allow the client to assign
    * a certain result to a corresponding previous request.
    * 
    * @param callId call identifier
    */
   public void setCallId(String callId)
   {
      this.callId = callId;
   }

   /**
    * Returns the call identifier which allow the client to assign
    * a certain result to a corresponding previous request.
    * 
    * @return call identifier
    */
   public String getCallId()
   {
      return callId;
   }

   /**
    * Sets a result of a request.
    * 
    * @param payload result of a request
    */
   public void setPayload(String payload)
   {
      this.payload = payload;
   }

   /**
    * Returns a result of a request.
    * 
    * @return result of a request
    */
   public String getPayload()
   {
      return payload;
   }

   /**
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      return "{\"type\":\"" + type + "\", " +
               "\"callId\":\"" + callId + "\", " +
               "\"payload\":" + payload +
               "}";
   }
}
