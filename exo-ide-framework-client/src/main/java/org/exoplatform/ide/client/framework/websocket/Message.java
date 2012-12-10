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
package org.exoplatform.ide.client.framework.websocket;

/**
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: Message.java Dec 4, 2012 3:07:48 PM azatsarynnyy $
 *
 */
public interface Message
{
   /**
    * Get message UUID. If specified for request message then response message gets the same UUID.
    *
    * @return message unique identifier
    */
   String getUuid();

   /**
    * Set message UUID. If specified for request message then response message gets the same UUID.
    *
    * @param uuid
    *    message unique identifier
    */
   void setUuid(String uuid);

   /**
    * Get message body.
    *
    * @return message body
    */
   String getBody();

   /**
    * Set message body.
    *
    * @param body
    *    message body
    */
   void setBody(String body);
}
