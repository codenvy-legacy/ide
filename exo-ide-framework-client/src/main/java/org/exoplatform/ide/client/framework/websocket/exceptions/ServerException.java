/**
 * Copyright (C) 2009 eXo Platform SAS.
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
 *
 */

package org.exoplatform.ide.client.framework.websocket.exceptions;

import org.exoplatform.ide.client.framework.websocket.rest.RESTfulResponseMessage;

/**
 * Thrown when there was an any exception was received from the server.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: ServerException.java Nov 9, 2012 5:20:18 PM azatsarynnyy $
 *
 */
@SuppressWarnings("serial")
public class ServerException extends Exception
{
   private RESTfulResponseMessage response;

   public ServerException(RESTfulResponseMessage response)
   {
      this.response = response;
   }

   @Override
   public String getMessage()
   {
      if (response.getBody() == null || response.getBody().isEmpty())
         return null;
      return response.getBody();
   }
}
