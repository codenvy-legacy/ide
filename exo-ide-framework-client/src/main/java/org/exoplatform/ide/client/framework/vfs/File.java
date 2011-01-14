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
package org.exoplatform.ide.client.framework.vfs;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class File extends Item
{

   private boolean newFile = false;

   private String content;

   private boolean contentChanged = false;

   private String contentType;

   private String jcrContentNodeType;

   public File(String path)
   {
      super(path);
   }

   /**
    * @return the newFile
    */
   public boolean isNewFile()
   {
      return newFile;
   }

   /**
    * @param newFile the newFile to set
    */
   public void setNewFile(boolean newFile)
   {
      this.newFile = newFile;
   }

   /**
    * @return the content
    */
   public String getContent()
   {
      return content;
   }

   /**
    * @param content the content to set
    */
   public void setContent(String content)
   {
      this.content = content;
   }

   /**
    * @return the contentChanged
    */
   public boolean isContentChanged()
   {
      return contentChanged;
   }

   /**
    * @param contentChanged the contentChanged to set
    */
   public void setContentChanged(boolean contentChanged)
   {
      this.contentChanged = contentChanged;
   }

   /**
    * @return the contentType
    */
   public String getContentType()
   {
      return contentType;
   }

   /**
    * @param contentType the contentType to set
    */
   public void setContentType(String contentType)
   {
      this.contentType = contentType;
   }

   /**
    * @return the jcrContentNodeType
    */
   public String getJcrContentNodeType()
   {
      return jcrContentNodeType;
   }

   /**
    * @param jcrContentNodeType the jcrContentNodeType to set
    */
   public void setJcrContentNodeType(String jcrContentNodeType)
   {
      this.jcrContentNodeType = jcrContentNodeType;
   }

}
