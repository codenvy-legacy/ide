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
   private String channel;

   private String payload;

   private String exception;

   public WebSocketEventMessage(String channel, String payload, String exception)
   {
      this.type = Type.EVENT.name();
      this.channel = channel;
      this.payload = payload;
      this.exception = exception;
   }

   @Override
   public String toString()
   {
      return "{\"type\":\"" + type + "\", " +
               "\"channel\":\"" + channel + "\", " +
               "\"payload\":" + payload  + ", " +
               "\"exception\":" + exception +
               "}";
   }

   public void setChannel(String channel)
   {
      this.channel = channel;
   }

   public String getChannel()
   {
      return channel;
   }

   public void setPayload(String payload)
   {
      this.payload = payload;
   }

   public String getPayload()
   {
      return payload;
   }

   /**
    * @return an exception
    */
   public String getException()
   {
      return exception;
   }

   /**
    * @param exception an exception
    */
   public void setException(String exception)
   {
      this.exception = exception;
   }
}
