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
package com.codenvy.ide.menu;

import com.codenvy.ide.Resources;
import com.codenvy.ide.menu.Item.ConteinerType;

import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.json.JsonStringMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;


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
   private final JsonStringMap<Item> menuItems;

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
      parentMenuBar.addStyleName(resources.menuCSS().menuHorizontal());
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
         menuItems.get(path).setSelected(selected);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void addMenuItem(String path, ExtendedCommand command, boolean visible, boolean enabled)
   {
      addMainMenuItem(path, command, visible, enabled);
   }

   /**
    * Create Main Menu item.
    * 
    * @param path
    * @param command
    * @param visible
    * @param enabled
    * @return
    */
   private Item addMainMenuItem(String path, ExtendedCommand command, boolean visible, boolean enabled)
   {
      MenuPath menuPath = new MenuPath(path);
      // Recursively get destination menu bar
      MenuBar dstMenuBar = getOrCreateParentMenuBar(menuPath, menuPath.getSize() - 1);
      // create new item
      Item newItem = new Item(menuPath, null, command.getToolTip(), command, ConteinerType.MAIN_MENU, resources);
      dstMenuBar.addItem(newItem);

      newItem.setVisible(visible);
      newItem.setEnabled(enabled);
      // store item in the map
      menuItems.put(path, newItem);

      return newItem;
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
            MenuPath path = new MenuPath(menuPath.getParentPath(depth));
            Item newItem = new Item(path, null, null, createSubMenuBar(), ConteinerType.MAIN_MENU, resources);
            dstMenuBar.addItem(newItem);
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
      menuBar.addStyleName(resources.menuCSS().menuVertical());
      return menuBar;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void addMenuItem(String path, ExtendedCommand command, boolean visible, boolean enabled, boolean selected)
   {
      Item newItem = addMainMenuItem(path, command, visible, enabled);
      newItem.setSelected(selected);
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