/**
 * Copyright (C) 2009 eXo Platform SAS.
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
 *
 */
package org.exoplatform.gwtframework.ui.client.command;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class SimpleControl extends Control<SimpleControl>
{

   private String title;

   private boolean showInMenu;

   private boolean showInContextMenu;

   private boolean canBeSelected;

   private boolean selected;

   private String hotKey;
   
   /**
    * If true, then this command will call by hotkey, 
    * even if it will be disabled.
    */
   private boolean ignoreDisable;

   private GwtEvent<?> event;
   
   private String groupName;

   public SimpleControl(String id)
   {
      super(id);
   }

   /*
    * TITLE
    */

   public String getTitle()
   {
      return title;
   }

   public SimpleControl setTitle(String title)
   {
      if (this.title == title)
      {
         return this;
      }

      this.title = title;
      for (ControlStateListener listener : getStateListeners())
      {
         if (!(listener instanceof SimpleControlStateListener))
         {
            continue;
         }

         ((SimpleControlStateListener)listener).updateControlTitle(title);
      }

      return this;
   }

   /*
    * SHOW IN MENU
    */

   public boolean isShowInMenu()
   {
      return showInMenu;
   }

   public SimpleControl setShowInMenu(boolean showInMenu)
   {
      this.showInMenu = showInMenu;
      return this;
   }

   /*
    * SHOW IN CONTEXT MENU
    */
   public boolean isShowInContextMenu()
   {
      return showInContextMenu;
   }

   public SimpleControl setShowInContextMenu(boolean showInContextMenu)
   {
      this.showInContextMenu = showInContextMenu;
      return this;
   }

   /*
    * CALL BY HOTKEY AND IGNORE DISABLE
    */

   public boolean isIgnoreDisable()
   {
      return ignoreDisable;
   }

   public SimpleControl setIgnoreDisable(boolean ignoreDisable)
   {
      this.ignoreDisable = ignoreDisable;
      return this;
   }

   /*
    * EVENT
    */

   public GwtEvent<?> getEvent()
   {
      return event;
   }

   public SimpleControl setEvent(GwtEvent<?> event)
   {
      this.event = event;
      return this;
   }

   public String getHotKey()
   {
      return hotKey;
   }

   public SimpleControl setHotKey(String hotKey)
   {
      this.hotKey = hotKey;
      
      for (ControlStateListener listener : getStateListeners())
      {
         if (!(listener instanceof SimpleControlStateListener))
         {
            continue;
         }

         ((SimpleControlStateListener)listener).updateControlHotKey(hotKey);
      }      
      
      return this;
   }

   public boolean canBeSelected()
   {
      return canBeSelected;
   }

   public SimpleControl setCanBeSelected(boolean canBeSelected)
   {
      this.canBeSelected = canBeSelected;
      return this;
   }

   public boolean isSelected()
   {
      return selected;
   }

   public SimpleControl setSelected(boolean selected)
   {
      if (this.selected == selected)
      {
         return this;
      }

      this.selected = selected;

      for (ControlStateListener listener : getStateListeners())
      {
         if (!(listener instanceof SimpleControlStateListener))
         {
            continue;
         }

         ((SimpleControlStateListener)listener).updateControlSelectionState(selected);
      }

      return this;
   }

   /**
    * Returns group name
    * 
    * @return group name
    */
   public String getGroupName()
   {
      return groupName;
   }

   /**
    * Sets new group name
    * 
    * @param groupName new group name
    */
   public SimpleControl setGroupName(String groupName)
   {
      this.groupName = groupName;
      return this;
   }
   
}
