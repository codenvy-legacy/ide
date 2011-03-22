/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.git.shared;

/**
 * Request to remove files.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: RmRequest.java 22817 2011-03-22 09:17:52Z andrew00x $
 */
public class RmRequest extends GitRequest
{
   /**
    * List of files to remove.
    */
   private String[] files;

   /**
    * @param files files to remove
    */
   public RmRequest(String[] files)
   {
      this.files = files;
   }

   /**
    * "Empty" remove request. Corresponding setters used to setup required
    * parameters.
    */
   public RmRequest()
   {
   }

   /**
    * @return files to remove
    */
   public String[] getFiles()
   {
      return files;
   }

   /**
    * @param files files to remove
    */
   public void setFiles(String[] files)
   {
      this.files = files;
   }
}
