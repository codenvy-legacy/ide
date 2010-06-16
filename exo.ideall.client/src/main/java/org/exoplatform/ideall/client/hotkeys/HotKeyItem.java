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
package org.exoplatform.ideall.client.hotkeys;

import com.google.gwt.resources.client.ImageResource;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: 
 *
 */
public class HotKeyItem
{

   private String controlId;

   private String hotKey;

   private String icon;

   private ImageResource image;

   private String group;

   public HotKeyItem(String controlId, String hotKeys, String icon, String group)
   {
      this.controlId = controlId;
      this.hotKey = hotKeys;
      this.icon = icon;
      this.group = group;
   }

   public HotKeyItem(String controlId, String hotKeys, ImageResource image, String group)
   {
      this.controlId = controlId;
      this.hotKey = hotKeys;
      this.image = image;
      this.group = group;
   }

   public String getGroup()
   {
      return group;
   }

   /**
    * @return the control id
    */
   public String getControlId()
   {
      return controlId;
   }

   /**
    * @param controlId the control id to set
    */
   public void setControlId(String controlId)
   {
      this.controlId = controlId;
   }

   public String getHotKey()
   {
      return hotKey;
   }

   public void setHotKey(String hotKey)
   {
      this.hotKey = hotKey;
   }

   public String getIcon()
   {
      return icon;
   }

   public ImageResource getImage()
   {
      return image;
   }

}
