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
package org.exoplatform.ide.extension.netvibes.client.model;

/**
 * The result of the deploy operation.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Dec 2, 2010 $
 * 
 */
public class DeployResult
{
   /**
    * The result is successful or not.
    */
   private boolean isSuccess;

   /**
    * Result message.
    */
   private String message;

   /**
    * @return the isSuccess
    */
   public boolean isSuccess()
   {
      return isSuccess;
   }

   /**
    * @param isSuccess the isSuccess to set
    */
   public void setSuccess(boolean isSuccess)
   {
      this.isSuccess = isSuccess;
   }

   /**
    * @return the message
    */
   public String getMessage()
   {
      return message;
   }

   /**
    * @param message the message to set
    */
   public void setMessage(String message)
   {
      this.message = message;
   }
}
