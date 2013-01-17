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

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import org.exoplatform.ide.Resources;
import org.exoplatform.ide.json.JsonCollections;
import org.exoplatform.ide.json.JsonStringMap;
import org.exoplatform.ide.toolbar.ToggleItem;

/**
 * Implements {@link MainMenuView} using standard GWT Menu Widgets
 * 
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public class MainMenuViewImpl extends Composite implements MainMenuView
{
   private static MainMenuUiBinder uiBinder = GWT.create(MainMenuUiBinder.class);

   /** Parent menu bar */
   @UiField
   MenuBar parentMenuBar;

   /** Map storing Path and corresponding menu item  */
   private final JsonStringMap<MenuItem> menuItems;

   private final Resources resources;

   interface MainMenuUiBinder extends UiBinder<Widget, MainMenuViewImpl>
   {
   }

   /**
    * Create new {@link MainMenuViewImpl}
    */
   @Inject
   public MainMenuViewImpl(Resources resources)
   {
      initWidget(uiBinder.createAndBindUi(this));
      parentMenuBar.addStyleName(resources.toolbarCSS().menuHorizontal());
      this.menuItems = JsonCollections.createStringMap();
      this.resources = resources;
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
   public void setSelected(String path, boolean selected)
   {
      if (menuItems.containsKey(path))
      {
         MenuItem item = menuItems.get(path);
         if (item instanceof ToggleItem)
         {
            ((ToggleItem)item).setSelected(selected);
         }
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void addMenuItem(String path, ExtendedCommand command, boolean visible, boolean enabled)
   {
      MenuPath menuPath = new MenuPath(path);
      // Recursively get destination menu bar
      MenuBar dstMenuBar = getOrCreateParentMenuBar(menuPath, menuPath.getSize() - 1);
      // create new item
      String content = getItemContent(menuPath, command.getIcon());
      MenuItem newItem = dstMenuBar.addItem(content, true, command);
      newItem.setVisible(visible);
      newItem.setEnabled(enabled);
      newItem.addStyleName(resources.toolbarCSS().toolbarItem());
      // store item in the map
      menuItems.put(path, newItem);
   }

   /**
    * Create item content.
    * 
    * @param menuPath
    * @param icon
    * @return
    */
   private String getItemContent(MenuPath menuPath, Image icon)
   {
      int depth = menuPath.getSize() - 1;
      String title = menuPath.getPathElementAt(depth);

      if (icon != null)
      {
         return (depth != 0 ? icon.toString() : "") + " <span>" + title + "</span>";
      }
      else
      {
         return "<span>" + title + "</span>";
      }
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
      menuBar.addStyleName(resources.toolbarCSS().menuVertical());
      return menuBar;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void addMenuItem(String path, ExtendedCommand command, boolean visible, boolean enabled, boolean selected)
   {
      MenuPath menuPath = new MenuPath(path);
      // Recursively get destination menu bar
      MenuBar dstMenuBar = getOrCreateParentMenuBar(menuPath, menuPath.getSize() - 1);
      // create new item
      String content = getItemContent(menuPath, command.getIcon());
      ToggleItem newItem = new ToggleItem(content, true, command, selected, resources);
      dstMenuBar.addItem(newItem);
      newItem.setVisible(visible);
      newItem.setEnabled(enabled);
      newItem.setSelected(selected);
      // store item in the map
      menuItems.put(path, newItem);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setDelegate(ActionDelegate delegate)
   {
      // ok
      // there are no events for now
   }

}