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
package org.exoplatform.ide.client.module.vfs.api;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Sep 24, 2010 $
 *
 */
public class Version extends File
{

   /**
    * Version creation date 
    */
   private String creationDate;

   /**
    * Content length
    */
   private int contentLength;

   /**
    *  Version name
    */
   private String displayName;
   
   /**
    * Version's owner href.
    */
   private String itemHref;

   /**
    * @param path
    */
   public Version(String href)
   {
      super(href);
   }

   /**
    * @return the creationDate
    */
   public String getCreationDate()
   {
      return creationDate;
   }

   /**
    * @param creationDate the creationDate to set
    */
   public void setCreationDate(String creationDate)
   {
      this.creationDate = creationDate;
   }

   /**
    * @return the contentLength
    */
   public int getContentLength()
   {
      return contentLength;
   }

   /**
    * @param contentLength the contentLength to set
    */
   public void setContentLength(int contentLength)
   {
      this.contentLength = contentLength;
   }

   /**
    * @return the displayName
    */
   public String getDisplayName()
   {
      return displayName;
   }

   /**
    * @param displayName the displayName to set
    */
   public void setDisplayName(String displayName)
   {
      this.displayName = displayName;
   }

   public String getItemHref()
   {
      return itemHref;
   }

   public void setItemHref(String itemHref)
   {
      this.itemHref = itemHref;
   }
   
   /**
    * @see org.exoplatform.ide.client.module.vfs.api.Item#getName()
    */
   @Override
   public String getName()
   {
      String name = itemHref;
      if (name.endsWith("/"))
      {
         name = name.substring(0, name.length() - 1);
      }
      name = name.substring(name.lastIndexOf("/") + 1);
      return name;
   }
}
