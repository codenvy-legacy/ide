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
package org.exoplatform.ide.client.hotkeys;

import org.exoplatform.gwtframework.ui.client.command.Control;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: 
 *
 */
public class HotKeyItem
{

   private String hotKey;

   private Control command;
   
   //--- fields for group item
   private boolean isGroup = false;
   
   private String title;
   
   private String group;
   
   public HotKeyItem(Control command, String hotkey, String group)
   {
      this.command = command;
      this.hotKey = hotkey;
      this.group = group;
   }

   public HotKeyItem(String title, String hotkey, boolean isGroup, String group)
   {
      this.title = title;
      this.hotKey = hotkey;
      this.isGroup = isGroup;
      this.group = group;
   }
   
   /**
    * @return the group
    */
   public String getGroup()
   {
      return group;
   }
   
   /**
    * @return the isGroup
    */
   public boolean isGroup()
   {
      return isGroup;
   }
   
   /**
    * @return the title
    */
   public String getTitle()
   {
      return title;
   }

   public String getHotKey()
   {
      return hotKey;
   }

   public void setHotKey(String hotKey)
   {
      this.hotKey = hotKey;
   }

   /**
    * @return the command
    */
   public Control getCommand()
   {
      return command;
   }

}
