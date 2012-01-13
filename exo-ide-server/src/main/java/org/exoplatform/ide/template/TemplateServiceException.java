/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.template;

/**
 * Checked exception that gives possibility to set response status that may be passed to client if this type of exception occurs.
 * 
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Jan 13, 2011 10:36:39 AM evgen $
 * 
 */
@SuppressWarnings("serial")
public class TemplateServiceException extends Exception
{

   private int status = 500;

   /**
    * @param message the detail message about exception
    * @param cause the cause
    */
   public TemplateServiceException(String message, Throwable cause)
   {
      super(message, cause);
   }

   /**
    * @param message the detail message about exception
    */
   public TemplateServiceException(String message)
   {
      super(message);
   }

   /**
    * @param status the HTTP status that should be used in response if any {@link javax.ws.rs.ext.ExceptionMapper} available for
    *           this exception
    * @param message the detail message about exception
    * @param cause the cause
    */
   public TemplateServiceException(int status, String message, Throwable cause)
   {
      super(message, cause);
      this.status = status;
   }

   /**
    * @param status the HTTP status that should be used in response if any {@link javax.ws.rs.ext.ExceptionMapper} available for
    *           this exception
    * @param message the detail message about exception
    */
   public TemplateServiceException(int status, String message)
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
