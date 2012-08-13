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

import org.everrest.core.impl.provider.json.JsonValue;
import org.exoplatform.ide.helper.JsonHelper;
import org.exoplatform.ide.helper.ParsingResponseException;

/**
 * Class represents the WebSocket message for a Remote Procedure Call.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: WebSocketCallMessage.java Jul 31, 2012 5:14:28 PM azatsarynnyy $
 *
 */
public class WebSocketCallMessage
{
   /**
    * To allow the client to assign a certain result to a previous
    * request the client assigns a call identifier to each request.
    */
   private String callId;

   /**
    * Remote procedure identifier.
    */
   private String procId;

   /**
    * Payload.
    */
   private String payload;

   /**
    * Creates a new {@link WebSocketCallMessage} from JSON message.
    * 
    * @param jsonMessage JSON message
    */
   public WebSocketCallMessage(String jsonMessage)
   {
      try
      {
         JsonValue jsonValue = JsonHelper.parseJson(jsonMessage);
         if (jsonValue == null || !jsonValue.isObject())
         {
            return;
         }
         this.callId = jsonValue.getElement("callId").getStringValue();
         this.procId = jsonValue.getElement("procId").getStringValue();
         this.payload = jsonValue.getElement("payload").getStringValue();
      }
      catch (ParsingResponseException e)
      {
         e.printStackTrace();
      }
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
    * Returns the remote procedure identifier.
    * 
    * @return remote procedure identifier
    */
   public String getProcId()
   {
      return procId;
   }

   /**
    * Sets the remote procedure identifier
    * 
    * @param procId remote procedure identifier
    */
   public void setProcId(String procId)
   {
      this.procId = procId;
   }

   /**
    * Returns payload.
    * 
    * @return payload
    */
   public String getPayload()
   {
      return payload;
   }

   /**
    * Sets payload.
    * 
    * @param payload payload
    */
   public void setPayload(String payload)
   {
      this.payload = payload;
   }
}
