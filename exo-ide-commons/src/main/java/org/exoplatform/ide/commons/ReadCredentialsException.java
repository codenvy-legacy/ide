/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package org.exoplatform.ide.commons;

/**
 * @author <a href="mailto:vparfonov@codenvy.com">Vitaly Parfonov</a>
 * @version $Id: ReadCredentialsException.java Mar 1, 2013 vetal $
 *
 */
@SuppressWarnings("serial")
public class ReadCredentialsException extends Exception
{

   /**
    * @param message the detail message
    * @param cause the cause
    */
   public ReadCredentialsException(String message, Throwable cause)
   {
      super(message, cause);
   }

   /**
    * @param message the detail message
    */
   public ReadCredentialsException(String message)
   {
      super(message);
   }

   /**
    * @param cause the cause
    */
   public ReadCredentialsException(Throwable cause)
   {
      super(cause);
   }

}
