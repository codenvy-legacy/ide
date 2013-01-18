/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.toolbar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItemSeparator;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import org.exoplatform.ide.json.JsonArray;
import org.exoplatform.ide.json.JsonCollections;
import org.exoplatform.ide.json.JsonStringMap;
import org.exoplatform.ide.menu.ExtendedCommand;
import org.exoplatform.ide.menu.Item;
import org.exoplatform.ide.menu.Item.ConteinerType;
import org.exoplatform.ide.menu.MenuPath;
import org.exoplatform.ide.menu.MenuResources;

/**
 * The implementation of {@link ToolbarView}
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public class ToolbarViewImpl extends Composite implements ToolbarView
{
   private static ToolbarViewImplUiBinder uiBinder = GWT.create(ToolbarViewImplUiBinder.class);

   @UiField
   MenuBar menu;

   private final JsonStringMap<Item> menuItems;

   private final JsonStringMap<JsonArray<Item>> groupItems;

   private final JsonStringMap<MenuItemSeparator> groupsSeparator;

   private final MenuResources resources;

   private int countGroupsItems = 0;

   interface ToolbarViewImplUiBinder extends UiBinder<Widget, ToolbarViewImpl>
   {
   }

   /**
    * Create view with given instance of resources. 
    * 
    * @param resources
    */
   @Inject
   public ToolbarViewImpl(MenuResources resources)
   {
      initWidget(uiBinder.createAndBindUi(this));

      this.addStyleName(resources.menuCSS().menuHorizontal());

      this.resources = resources;
      this.menuItems = JsonCollections.createStringMap();
      this.groupItems = JsonCollections.createStringMap();
      this.groupsSeparator = JsonCollections.createStringMap();
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

   /**
    * {@inheritDoc}
    */
   @Override
   public void setVisible(String path, boolean visible)
   {
      // find item and change its state
      if (menuItems.containsKey(path))
      {
         menuItems.get(path).setVisible(visible);
      }

      if (groupItems.containsKey(path))
      {
         groupsSeparator.get(path).setVisible(visible);

         JsonArray<Item> items = groupItems.get(path);
         for (int i = 0; i < items.size(); i++)
         {
            items.get(i).setVisible(visible);
         }
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setEnabled(String path, boolean enabled)
   {
      // find item and change its state
      if (menuItems.containsKey(path))
      {
         menuItems.get(path).setEnabled(enabled);
      }

      if (groupItems.containsKey(path))
      {
         JsonArray<Item> items = groupItems.get(path);
         for (int i = 0; i < items.size(); i++)
         {
            items.get(i).setEnabled(enabled);
         }
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setSelected(String path, boolean selected) throws IllegalStateException
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
   public void addItem(String path, ExtendedCommand command, boolean visible, boolean enabled)
      throws IllegalStateException
   {
      addToolbarItem(path, command.getIcon(), command.getToolTip(), command, visible, enabled);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void addToggleItem(String path, ToggleCommand command, boolean visible, boolean enabled, boolean selected)
      throws IllegalStateException
   {
      Item menuItem = addToolbarItem(path, command.getIcon(), command.getToolTip(), command, visible, enabled);
      menuItem.setSelected(selected);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void addDropDownItem(String path, ImageResource icon, String tooltip, boolean visible, boolean enabled)
      throws IllegalStateException
   {
      addToolbarItem(path, icon, tooltip, null, visible, enabled);
   }

   /**
    * Create toolbar item.
    * 
    * @param path
    * @param icon
    * @param tooltip
    * @param command
    * @param visible
    * @param enabled
    * @return new item
    * @throws IllegalStateException
    */
   private Item addToolbarItem(String path, ImageResource icon, String tooltip, ExtendedCommand command,
      boolean visible, boolean enabled) throws IllegalStateException
   {
      MenuPath menuPath = new MenuPath(path);
      int depth = menuPath.getSize() - 1;

      Item menuItem;
      if (depth == 0)
      {
         // if path has only one name
         throw new IllegalStateException("Group or item with entered name is not exist");
      }
      else if (depth == 1)
      {
         // in order to create item into the group
         String groupName = menuPath.getParentPath(depth);
         MenuItemSeparator groupSeparator = groupsSeparator.get(groupName);
         if (groupSeparator == null)
         {
            // if group isn't exist then creates it 
            MenuItemSeparator newSeparator = menu.addSeparator();
            newSeparator.setVisible(visible);
            JsonArray<Item> items = JsonCollections.createArray();

            groupsSeparator.put(groupName, newSeparator);
            groupItems.put(groupName, items);

            // it is the first item into the group
            if (command != null)
            {
               menuItem = new Item(menuPath, null, tooltip, command, ConteinerType.TOOLBAR, resources);
            }
            else
            {
               menuItem = new Item(menuPath, icon, tooltip, createSubMenuBar(), ConteinerType.TOOLBAR, resources);
            }

            menu.addItem(menuItem);
            items.add(menuItem);
         }
         else
         {
            // when the group has items needs to insert item after last
            // because could be situation when other group was created after the current group 
            String parentName = menuPath.getParentPath(depth);
            JsonArray<Item> items = groupItems.get(parentName);

            Item previousItem = items.get(items.size() - 1);
            if (command != null)
            {
               menuItem = new Item(menuPath, null, tooltip, command, ConteinerType.TOOLBAR, resources);
            }
            else
            {
               menuItem = new Item(menuPath, icon, tooltip, createSubMenuBar(), ConteinerType.TOOLBAR, resources);
            }

            // insert after last item into current group
            int previousItemIndex = menu.getItemIndex(previousItem);
            if (previousItemIndex < countGroupsItems)
            {
               menu.insertItem(menuItem, previousItemIndex + 1);
            }
            else
            {
               menu.addItem(menuItem);
            }

            items.add(menuItem);
         }

         countGroupsItems++;
      }
      else
      {
         // in order to create item into the dropdown item/popup menu
         Item parentItem = menuItems.get(menuPath.getParentPath(depth));
         if (parentItem == null)
         {
            throw new IllegalStateException("Parent item is not exist");
         }

         if (command != null)
         {
            menuItem = new Item(menuPath, null, tooltip, command, ConteinerType.TOOLBAR, resources);
         }
         else
         {
            menuItem = new Item(menuPath, icon, tooltip, createSubMenuBar(), ConteinerType.TOOLBAR, resources);
         }

         parentItem.getSubMenu().addItem(menuItem);
      }

      menuItem.setVisible(visible);
      menuItem.setEnabled(enabled);

      menuItems.put(path, menuItem);

      return menuItem;
   }

   /**
    * Create SubMenu bar.
    * 
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
   public void copyMainMenuItem(String toolbarPath, String mainMenuPath)
   {
      // TODO Auto-generated method stub
   }
}