/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.menu;

import com.google.inject.Inject;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.ide.json.JsonCollections;
import org.exoplatform.ide.json.JsonStringMap;
import org.exoplatform.ide.menu.MainMenuPresenter.Display;

/**
 * Implements {@link MainMenuPresenter.Display} using standard GWT Menu Widgets  
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
public class MainMenuView implements Display
{
   /** Parent menu bar */
   private final MenuBar parentMenuBar;

   /** Map storing Path and corresponding menu item  */
   private final JsonStringMap<MenuItem> menuItems;

   /**
    * Create new {@link MainMenuView} 
    */
   @Inject
   public MainMenuView()
   {
      this.parentMenuBar = new MenuBar();
      this.parentMenuBar.setAnimationEnabled(true);
      this.parentMenuBar.setAutoOpen(true);

      this.menuItems = JsonCollections.createStringMap();
   }

   /**
   * {@inheritDoc}
   */
   @Override
   public Widget asWidget()
   {
      return parentMenuBar;
   }

   /**
   * {@inheritDoc}
   */
   @Override
   public void setVisible(String path, boolean visible)
   {
      if (menuItems.containsKey(path))
      {
         menuItems.get(path).setVisible(visible);
      }
   }

   /**
   * {@inheritDoc}
   */
   @Override
   public void setEnabled(String path, boolean enabled)
   {
      if (menuItems.containsKey(path))
      {
         menuItems.get(path).setEnabled(enabled);
      }
   }

   /**
   * {@inheritDoc}
   */
   @Override
   public void addMenuItem(String path, Image icon, Command command, boolean visible, boolean enabled)
   {
      MenuPath menuPath = new MenuPath(path);
      // Recursively get destination menu bar
      MenuBar dstMenuBar = getOrCreateParentMenuBar(menuPath, menuPath.getSize() - 1);
      // create new item
      MenuItem newItem = dstMenuBar.addItem(menuPath.getPathElementAt(menuPath.getSize() - 1), command);
      newItem.setVisible(visible);
      newItem.setEnabled(enabled);
      // store item in the map
      menuItems.put(path, newItem);
   }

   /**
    * Recursively find corresponding menu bar or create new if nothing found
    * 
    * @param menuPath
    * @param depth
    * @return
    */
   private MenuBar getOrCreateParentMenuBar(MenuPath menuPath, int depth)
   {
      if (depth == 0)
      {
         return parentMenuBar;
      }
      else
      {
         // try to get parent
         MenuItem menuItem = menuItems.get(menuPath.getParentPath(depth));
         if (menuItem != null)
         {
            MenuBar subMenu = menuItem.getSubMenu();
            if (subMenu == null)
            {
               subMenu = createSubMenuBar();
               menuItem.setSubMenu(subMenu);
            }
            return subMenu;
         }
         else
         {
            MenuBar dstMenuBar = getOrCreateParentMenuBar(menuPath, depth - 1);
            MenuItem newItem = dstMenuBar.addItem(menuPath.getPathElementAt(depth - 1), createSubMenuBar());
            menuItems.put(menuPath.getParentPath(depth), newItem);
            return newItem.getSubMenu();
         }
      }
   }

   /**
    * @return new instance of {@link MenuBar}
    */
   private MenuBar createSubMenuBar()
   {
      MenuBar menuBar = new MenuBar(true);
      menuBar.setAnimationEnabled(true);
      menuBar.setAutoOpen(true);
      return menuBar;
   }

}
