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
package org.exoplatform.ide.client.framework.vfs;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.vfs.FileContentSaveCallback.FileData;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: FolderCreateCallback.java Feb 9, 2011 11:25:55 AM vereshchaka $
 *
 */
public abstract class FileContentSaveCallback extends AsyncRequestCallback<FileData>
{
   public class FileData
   {
      private File file;
      
      private boolean isNew;
      
      public FileData(File file, boolean isNew)
      {
         this.file = file;
         this.isNew = isNew;
      }
      
      /**
       * @return the file
       */
      public File getFile()
      {
         return file;
      }
      
      /**
       * @param file the file to set
       */
      public void setFile(File file)
      {
         this.file = file;
      }
      
      /**
       * @return the isNew
       */
      public boolean isNew()
      {
         return isNew;
      }
      
      /**
       * @param isNew the isNew to set
       */
      public void setNew(boolean isNew)
      {
         this.isNew = isNew;
      }
      
   }
   
   /**
    * @see org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback#onFailure(java.lang.Throwable)
    */
   @Override
   protected void onFailure(Throwable exception)
   {
      fireEvent(new ExceptionThrownEvent(exception, "Service is not deployed.<br>Resource not found."));
   }

}
