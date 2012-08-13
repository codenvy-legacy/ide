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
 * @version $Id: WebSocketEventMessage.java Jul 31, 2012 5:14:28 PM azatsarynnyy $
 *
 */
public class WebSocketEventMessage extends WebSocketMessage
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
    * An exception describing failure. This will be <code>null</code> if the operation succeeded.
    */
   private String exception;

   public WebSocketEventMessage(String channel, String payload, String exception)
   {
      this.type = Type.EVENT.name();
      this.channel = channel;
      this.payload = payload;
      this.exception = exception;
   }

   /**
    * Sets a channel for publishing message.
    * 
    * @param channel channel
    */
   public void setChannel(String channel)
   {
      this.channel = channel;
   }

   /**
    * Returns a channel for publishing message
    * 
    * @return channel
    */
   public String getChannel()
   {
      return channel;
   }

   /**
    * Sets the payload.
    * 
    * @param payload text data
    */
   public void setPayload(String payload)
   {
      this.payload = payload;
   }

   /**
    * Returns the payload.
    * 
    * @return payload.
    */
   public String getPayload()
   {
      return payload;
   }

   /**
    * Returns an exception describing failure.
    * 
    * @return an exception describing failure. This will be <code>null</code> if the operation succeeded.
    */
   public String getException()
   {
      return exception;
   }

   /**
    * Sets an exception describing failure.
    * 
    * @param exception an exception
    */
   public void setException(String exception)
   {
      this.exception = exception;
   }

   /**
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      return "{\"type\":\"" + type + "\", " +
               "\"channel\":\"" + channel + "\", " +
               "\"payload\":" + payload  + ", " +
               "\"exception\":" + exception +
               "}";
   }
}
