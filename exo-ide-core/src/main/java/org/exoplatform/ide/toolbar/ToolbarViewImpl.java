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
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.MenuItemSeparator;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import org.exoplatform.ide.json.JsonArray;
import org.exoplatform.ide.json.JsonCollections;
import org.exoplatform.ide.json.JsonStringMap;
import org.exoplatform.ide.menu.ExtendedCommand;
import org.exoplatform.ide.menu.MenuPath;

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

   private final JsonStringMap<MenuItem> toolbarItems;

   private final JsonStringMap<MenuItem> dropDownItems;

   private final JsonStringMap<ToggleItem> toggleItems;

   private final JsonStringMap<JsonArray<MenuItem>> groupItems;

   private final JsonStringMap<MenuItemSeparator> groupsSeparator;

   private final ToolbarResources resources;

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
   public ToolbarViewImpl(ToolbarResources resources)
   {
      initWidget(uiBinder.createAndBindUi(this));

      this.addStyleName(resources.toolbarCSS().menuHorizontal());

      this.resources = resources;
      this.toolbarItems = JsonCollections.createStringMap();
      this.dropDownItems = JsonCollections.createStringMap();
      this.toggleItems = JsonCollections.createStringMap();
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
      if (toolbarItems.containsKey(path))
      {
         toolbarItems.get(path).setVisible(visible);
      }

      if (toggleItems.containsKey(path))
      {
         toggleItems.get(path).setVisible(visible);
      }

      if (dropDownItems.containsKey(path))
      {
         dropDownItems.get(path).setVisible(visible);
      }

      if (groupItems.containsKey(path))
      {
         groupsSeparator.get(path).setVisible(visible);

         JsonArray<MenuItem> items = groupItems.get(path);
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
      if (toolbarItems.containsKey(path))
      {
         setEnable(toolbarItems.get(path), enabled);
      }

      if (toggleItems.containsKey(path))
      {
         setEnable(toggleItems.get(path), enabled);
      }

      if (dropDownItems.containsKey(path))
      {
         setEnable(dropDownItems.get(path), enabled);
      }

      if (groupItems.containsKey(path))
      {
         JsonArray<MenuItem> items = groupItems.get(path);
         for (int i = 0; i < items.size(); i++)
         {
            setEnable(items.get(i), enabled);
         }
      }
   }

   /**
    * Sets enabled state for item.
    * 
    * @param item
    * @param enabled
    */
   private void setEnable(MenuItem item, boolean enabled)
   {
      item.setEnabled(enabled);
      
      if (enabled)
      {
         item.removeStyleName(resources.toolbarCSS().disable());
         item.addStyleName(resources.toolbarCSS().enable());
      }
      else
      {
         item.removeStyleName(resources.toolbarCSS().enable());
         item.addStyleName(resources.toolbarCSS().disable());
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setSelected(String path, boolean selected) throws IllegalStateException
   {
      if (toggleItems.containsKey(path))
      {
         toggleItems.get(path).setSelected(selected);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void addItem(String path, ExtendedCommand command, boolean visible, boolean enabled)
      throws IllegalStateException
   {
      MenuPath menuPath = new MenuPath(path);

      int depth = menuPath.getSize() - 1;
      MenuBar dstMenuBar = getOrCreateParentMenuBar(menuPath, depth, visible);
      String item = getItemContent(menuPath, command.getIcon());

      MenuItem menuItem;
      if (depth == 1)
      {
         // in order to create item into the group
         String parentName = menuPath.getParentPath(depth);
         JsonArray<MenuItem> items = groupItems.get(parentName);

         if (items.size() == 0)
         {
            // if it is the first item into the group
            menuItem = dstMenuBar.addItem(item, true, command);
            items.add(menuItem);
         }
         else
         {
            // when the group has items needs to insert item after last
            // because could be situation when other group was created after the current group 
            MenuItem previousItem = items.get(items.size() - 1);
            menuItem = new MenuItem(item, true, command);

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
         menuItem = dstMenuBar.addItem(item, true, command);
      }

      menuItem.setVisible(visible);
      setEnable(menuItem, enabled);
      menuItem.setTitle(command.getToolTip());
      menuItem.addStyleName(resources.toolbarCSS().toolbarItem());

      toolbarItems.put(path, menuItem);
   }

   /**
    * Find corresponding menu bar, create new group or throw exception if nothing found.
    * 
    * @param menuPath
    * @param depth
    * @param visible
    * @return
    */
   private MenuBar getOrCreateParentMenuBar(MenuPath menuPath, int depth, boolean visible) throws IllegalStateException
   {
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
            JsonArray<MenuItem> items = JsonCollections.createArray();

            groupsSeparator.put(groupName, newSeparator);
            groupItems.put(groupName, items);
         }

         return menu;
      }
      else
      {
         // in order to create item into the dropdown item/popup menu
         MenuItem menuItem = dropDownItems.get(menuPath.getParentPath(depth));
         if (menuItem == null)
         {
            throw new IllegalStateException("Parent item is not exist");
         }

         return menuItem.getSubMenu();
      }
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
         return icon.toString() + (depth != 1 ? " <span>" + title + "</span>" : "");
      }
      else
      {
         return "<span>" + title + "</span>";
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void addToggleItem(String path, ToggleCommand command, boolean visible, boolean enabled,
      boolean selected) throws IllegalStateException
   {
      MenuPath menuPath = new MenuPath(path);

      int depth = menuPath.getSize() - 1;
      MenuBar dstMenuBar = getOrCreateParentMenuBar(menuPath, depth, visible);
      String item = getItemContent(menuPath, command.getIcon());

      ToggleItem menuItem;
      if (depth == 1)
      {
         // in order to create item into the group
         String parentName = menuPath.getParentPath(depth);
         JsonArray<MenuItem> items = groupItems.get(parentName);

         if (items.size() == 0)
         {
            // if it is the first item into the group
            menuItem = new ToggleItem(item, true, command, selected, resources);
            menu.addItem(menuItem);
            items.add(menuItem);
         }
         else
         {
            // when the group has items needs to insert item after last
            // because could be situation when other group was created after the current group
            MenuItem previousItem = items.get(items.size() - 1);
            menuItem = new ToggleItem(item, true, command, selected, resources);

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
         menuItem = new ToggleItem(item, true, command, selected, resources);
         dstMenuBar.addItem(menuItem);
      }

      menuItem.setVisible(visible);
      setEnable(menuItem, enabled);
      menuItem.setTitle(command.getToolTip());

      toggleItems.put(path, menuItem);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void addDropDownItem(String path, Image icon, String tooltip, boolean visible, boolean enabled)
   {
      MenuPath menuPath = new MenuPath(path);

      int depth = menuPath.getSize() - 1;
      MenuBar dstMenuBar = getOrCreateParentMenuBar(menuPath, depth, visible);
      String item = getItemContent(menuPath, icon);

      MenuItem menuItem;
      if (depth == 1)
      {
         // in order to create item into the group
         String parentName = menuPath.getParentPath(depth);
         JsonArray<MenuItem> items = groupItems.get(parentName);

         if (items.size() == 0)
         {
            // if it is the first item into the group
            menuItem = dstMenuBar.addItem(item, true, createSubMenuBar());
            items.add(menuItem);
         }
         else
         {
            // when the group has items needs to insert item after last
            // because could be situation when other group was created after the current group
            MenuItem previousItem = items.get(items.size() - 1);
            menuItem = new MenuItem(item, true, createSubMenuBar());

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
         menuItem = dstMenuBar.addItem(item, true, createSubMenuBar());
      }

      menuItem.setVisible(visible);
      setEnable(menuItem, enabled);
      menuItem.setTitle(tooltip);
      menuItem.addStyleName(resources.toolbarCSS().toolbarItem());

      dropDownItems.put(path, menuItem);
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
      menuBar.addStyleName(resources.toolbarCSS().menuVertical());

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