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
package org.exoplatform.ideall.client.solution.menu.bean;

import java.util.Iterator;
import java.util.LinkedHashMap;

import org.exoplatform.ideall.client.solution.command.Command;
import org.exoplatform.ideall.client.solution.command.CommandStateListener;
import org.exoplatform.ideall.client.solution.menu.GWTMenuBar;
import org.exoplatform.ideall.client.solution.menu.style.MenuBarStyle;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class MenuBarItem implements MenuItem, CommandStateListener, MenuItemStateListener
{

   private String title;

   private Command command;

   protected LinkedHashMap<String, MenuItem> children = new LinkedHashMap<String, MenuItem>();

   private Element element;

   public MenuBarItem(String title, Command command)
   {
      this.title = title;
      this.command = command;
   }

   public LinkedHashMap<String, MenuItem> getChildren()
   {
      return children;
   }

   public void initElement(Element element)
   {
      this.element = element;
      if (command != null)
      {
         command.getStateListeners().add(this);
      }

      boolean enabled = command == null ? true : command.isEnabled();

      if (enabled)
      {
         element.setClassName(MenuBarStyle.ITEM);
      }
      else
      {
         element.setClassName(MenuBarStyle.ITEM_DISABLED);
      }

      DOM.setElementAttribute(element, GWTMenuBar.TITLE_PROPERTY, title);
      DOM.setElementAttribute(element, GWTMenuBar.ENABLED_PROPERTY, "" + enabled);
   }

   public void updateCommandEnabling(boolean enabled)
   {
      element.setClassName(enabled ? MenuBarStyle.ITEM : MenuBarStyle.ITEM_DISABLED);
      DOM.setElementAttribute(element, GWTMenuBar.ENABLED_PROPERTY, "" + enabled);
   }

   public void updateCommandVisibility(boolean visible)
   {
   }

   public void updateCommandSelectedState(boolean selected)
   {
   }

   public void updateMenuItemState()
   {
      boolean enabled = getPopupItemsState(children);
      updateCommandEnabling(enabled);
   }

   /**
    * Calculating the count of visible children in menu bar item
    * 
    * @param menuItems
    * @return
    */
   private boolean getPopupItemsState(LinkedHashMap<String, MenuItem> menuItems)
   {
      int allItems = 0;
      int visibleItems = 0;
      Iterator<String> iterator = menuItems.keySet().iterator();
      while (iterator.hasNext())
      {
         String title = iterator.next();
         MenuItem menuItem = menuItems.get(title);
         if (menuItem instanceof CommandItem)
         {
            allItems++;
            CommandItem commandItem = (CommandItem)menuItem;
            if (commandItem.getCommand().isVisible())
            {
               visibleItems++;
            }
         }
      }

      return visibleItems > 0;
   }

}
