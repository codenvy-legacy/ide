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
package org.exoplatform.ide.project;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@SuppressWarnings("serial")
public class ProjectPrepareException extends Exception
{
   private int status = 500;

   /**
    * @param message the detail message about exception
    * @param cause the cause
    */
   public ProjectPrepareException(String message, Throwable cause)
   {
      super(message, cause);
   }

   /**
    * @param message the detail message about exception
    */
   public ProjectPrepareException(String message)
   {
      super(message);
   }

   /**
    * @param status the HTTP status that should be used in response if any {@link javax.ws.rs.ext.ExceptionMapper} available for
    *           this exception
    * @param message the detail message about exception
    * @param cause the cause
    */
   public ProjectPrepareException(int status, String message, Throwable cause)
   {
      super(message, cause);
      this.status = status;
   }

   /**
    * @param status the HTTP status that should be used in response if any {@link javax.ws.rs.ext.ExceptionMapper} available for
    *           this exception
    * @param message the detail message about exception
    */
   public ProjectPrepareException(int status, String message)
   {
      super(message);
      this.status = status;
   }

   /**
    * @return the HTTP status that should be used in response if any {@link javax.ws.rs.ext.ExceptionMapper} available for this
    *         exception
    */
   public int getStatus()
   {
      return status;
   }
}
