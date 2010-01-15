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
package org.exoplatform.ideall.client.solution.menu;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.exoplatform.ideall.client.solution.command.Command;
import org.exoplatform.ideall.client.solution.menu.bean.CommandItem;
import org.exoplatform.ideall.client.solution.menu.bean.DelimiterItem;
import org.exoplatform.ideall.client.solution.menu.bean.MenuItem;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class PopupItemsFilter
{

   protected static class MenuItemWrapper
   {

      private String title;

      private MenuItem menuItem;

      public MenuItemWrapper(String title, MenuItem menuItem)
      {
         this.title = title;
         this.menuItem = menuItem;
      }

      public String getTitle()
      {
         return title;
      }

      public MenuItem getMenuItem()
      {
         return menuItem;
      }

      public boolean isDelimiter()
      {
         return menuItem instanceof DelimiterItem;
      }

   }

   /**
    * Return filtered list of popup items.
    * Remove delimiters from start of list, remove delimiters from end of list,
    * remove dublicated delimiters.
    * 
    * @param items
    * @return
    */
   public static LinkedHashMap<String, MenuItem> getFilteredItems(LinkedHashMap<String, MenuItem> items)
   {
      ArrayList<MenuItemWrapper> menuItems = new ArrayList<MenuItemWrapper>();

      Iterator<String> iterator = items.keySet().iterator();
      while (iterator.hasNext())
      {
         String title = iterator.next();
         MenuItem menuItem = items.get(title);

         if (menuItem instanceof CommandItem)
         {
            Command command = ((CommandItem)menuItem).getCommand();
            if (command.isVisible())
            {
               MenuItemWrapper item = new MenuItemWrapper(title, menuItem);
               menuItems.add(item);
            }
         }
         else
         {
            MenuItemWrapper item = new MenuItemWrapper(title, menuItem);
            menuItems.add(item);
         }
      }

      removeDelimitersFromStart(menuItems);
      removeDelimitersFromEnd(menuItems);
      removeDublicatedDelimiters(menuItems);

      LinkedHashMap<String, MenuItem> filteredItems = new LinkedHashMap<String, MenuItem>();
      for (MenuItemWrapper item : menuItems)
      {
         filteredItems.put(item.getTitle(), item.getMenuItem());
      }
      return filteredItems;
   }

   /**
    * Remove delimiters from start of list
    * 
    * @param menuItems
    */
   private static void removeDelimitersFromStart(ArrayList<MenuItemWrapper> menuItems)
   {
      while (menuItems.size() > 0)
      {
         if (menuItems.get(0).isDelimiter())
         {
            menuItems.remove(0);
         }
         else
         {
            break;
         }
      }
   }

   /**
    * Remove delimiters from end of list
    * 
    * @param menuItems
    */
   private static void removeDelimitersFromEnd(ArrayList<MenuItemWrapper> menuItems)
   {
      while (menuItems.size() > 0)
      {
         if (menuItems.get(menuItems.size() - 1).isDelimiter())
         {
            menuItems.remove(menuItems.size() - 1);
         }
         else
         {
            break;
         }
      }
   }
   
   /**
    * Remove dublicated delimiters
    * 
    * @param menuItems
    */
   private static void removeDublicatedDelimiters(ArrayList<MenuItemWrapper> menuItems) {
      while (true) {
         boolean isDelimiterBefore = false;
         int i = - 1;
         for (MenuItemWrapper item : menuItems) {
            i++;
            if (item.isDelimiter()) {
               if (isDelimiterBefore) {
                  break;
               } else {
                  isDelimiterBefore = true;
               }
            } else {
               isDelimiterBefore = false;
            }
         }
         
         if (isDelimiterBefore && i >= 0) {
            isDelimiterBefore = false;
            menuItems.remove(i);
         } else {
            break;
         }
      }
   }

}
