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
package com.codenvy.ide.websocket;

/**
 * Thrown when there was a problem with WebSocket connection.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: WebSocketException.java Jul 12, 2012 2:44:19 PM azatsarynnyy $
 */
@SuppressWarnings("serial")
public class WebSocketException extends Exception
{

   public WebSocketException()
   {
      super();
   }

   public WebSocketException(String message)
   {
      super(message);
   }

   public WebSocketException(Throwable cause)
   {
      super(cause);
   }

   public WebSocketException(String message, Throwable cause)
   {
      super(message, cause);
   }
}