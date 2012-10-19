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
 * Interface represents an exception describing failure.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: WebSocketEventMessageException.java Jul 13, 2012 5:14:28 PM azatsarynnyy $
 *
 */
public interface WebSocketEventMessageException extends WebSocketMessage
{
   /**
    * Returns an exception name.
    * 
    * @return name of the exception
    */
   String getName();

   /**
    * Sets an exception name.
    * 
    * @param name name of the exception
    */
   void setName(String name);

   /**
    * Returns a detail message that represents an exception.
    * 
    * @return error message
    */
   String getMessage();

   /**
    * Sets a detail message that represents an exception.
    * 
    * @param message error message
    */
   void setMessage(String message);
}
