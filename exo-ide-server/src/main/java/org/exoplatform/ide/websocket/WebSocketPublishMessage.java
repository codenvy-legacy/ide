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
 * Class represents the WebSocket message.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: WebSocketCallMessage.java Jul 31, 2012 5:14:28 PM azatsarynnyy $
 *
 */
public class WebSocketPublishMessage
{
   /**
    * A channel for publishing message.
    */
   private String channel;

   /**
    * Payload.
    */
   private String payload;

   /**
    * A text represents an error.
    */
   private String error;

   /**
    * Creates a new {@link WebSocketPublishMessage} from JSON message.
    * 
    * @param jsonMessage JSON message
    */
   public WebSocketPublishMessage(String jsonMessage)
   {
      try
      {
         JsonValue jsonValue = JsonHelper.parseJson(jsonMessage);
         if (jsonValue == null || !jsonValue.isObject())
         {
            return;
         }
         this.channel = jsonValue.getElement("channel").getStringValue();
         this.payload = jsonValue.getElement("payload").getStringValue();
      }
      catch (ParsingResponseException e)
      {
         e.printStackTrace();
      }
   }

   /**
    * Returns a channel for publishing message.
    * 
    * @return channel identifier
    */
   public String getChannel()
   {
      return channel;
   }

   /**
    * Sets a channel for publishing message.
    * 
    * @param channel channel identifier
    */
   public void setChannel(String channel)
   {
      this.channel = channel;
   }

   /**
    * Returns a data which will be sent in a message.
    * 
    * @return payload
    */
   public String getPayload()
   {
      return payload;
   }

   /**
    * Sets a data which will be sent in a message.
    * 
    * @param payload payload
    */
   public void setPayload(String payload)
   {
      this.payload = payload;
   }

   /**
    * Returns an error.
    * 
    * @return an error
    */
   public String getError()
   {
      return error;
   }

   /**
    * Sets an text represent error.
    * 
    * @param error an error
    */
   public void setError(String error)
   {
      this.error = error;
   }
}
