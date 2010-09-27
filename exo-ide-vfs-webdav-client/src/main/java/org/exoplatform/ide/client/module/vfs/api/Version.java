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
    * Version's creator
    */
   private String creator;
   
   /**
    * Version's label
    */
   private String versionLabel;
   
   /**
    * @param path
    */
   public Version(String href)
   {
      super(href);
   }

   /**
    * @return the creator
    */
   public String getCreator()
   {
      return creator;
   }

   /**
    * @param creator the creator to set
    */
   public void setCreator(String creator)
   {
      this.creator = creator;
   }

   /**
    * @return {@link String} version label
    */
   public String getVersionLabel()
   {
      return versionLabel;
   }

   /**
    * @param versionLabel version label
    */
   public void setVersionLabel(String versionLabel)
   {
      this.versionLabel = versionLabel;
   }
}
