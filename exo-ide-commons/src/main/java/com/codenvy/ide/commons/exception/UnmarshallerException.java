/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package com.codenvy.ide.commons.exception;

/**
 * Created by The eXo Platform SAS. Notifies about unmarshalling error accured.
 * 
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
@SuppressWarnings("serial")
public class UnmarshallerException extends Exception
{

   /**
    * Creates an Instance of {@link UnauthorizedException} with message and root cause
    * 
    * @param message
    * @param cause
    */
   public UnmarshallerException(String message, Throwable cause)
   {
      super(message, cause);
   }

}
