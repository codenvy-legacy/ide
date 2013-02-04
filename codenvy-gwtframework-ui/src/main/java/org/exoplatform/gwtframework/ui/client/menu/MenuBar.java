/**
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
 *
 */

package org.exoplatform.gwtframework.ui.client.menu;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * MenuBar is visual component, represents top menu.   
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class MenuBar extends Composite implements ItemSelectedHandler, CloseMenuHandler
{

   /**
    * This is visual component.
    * Uses for handling mouse events on MenuBar.
    */
   private class MenuBarTable extends FlexTable
   {

      public MenuBarTable()
      {
         sinkEvents(Event.ONMOUSEOVER | Event.ONMOUSEOUT | Event.ONMOUSEDOWN);
      }

      @Override
      public void onBrowserEvent(Event event)
      {
         Element td = getEventTargetCell(event);
         MenuBarItem item = menuBarItems.get(td);

         if (item == null)
         {
            return;
         }

         switch (DOM.eventGetType(event))
         {
            case Event.ONMOUSEOVER :
               if (selectedMenuBarItem != null && item != selectedMenuBarItem)
               {
                  if (item.onMouseDown())
                  {
                     openPopupMenu(item);
                  }
               }

               item.onMouseOver();
               break;

            case Event.ONMOUSEOUT :
               item.onMouseOut();
               break;

            case Event.ONMOUSEDOWN :
               if (item == selectedMenuBarItem)
               {
                  if (lockLayer != null)
                  {
                     lockLayer.close();
                  }
                  return;
               }

               if (item.onMouseDown())
               {
                  openPopupMenu(item);
               }
               break;

            default :
               break;
         }

      }
   }

   /**
    * Panel, which contains top menu.
    */
   private AbsolutePanel absolutePanel;

   /**
    * Lock layer for displaying popup menus.
    */
   private MenuLockLayer lockLayer;

   /**
    * List Menu Bar items.
    */
   private Map<Element, MenuBarItem> menuBarItems = new LinkedHashMap<Element, MenuBarItem>();

   /**
    * Store selected Menu Bar item.
    */
   private MenuBarItem selectedMenuBarItem;

   /**
    * Working table, cells of which are contains element of Menu.
    */
   private MenuBarTable table;

   /**
    * Create MenuBar.
    */
   public MenuBar()
   {
      absolutePanel = new AbsolutePanel();
      initWidget(absolutePanel);
      absolutePanel.setStyleName(MenuBarStyle.MENUBAR);

      table = new MenuBarTable();
      table.setStyleName(MenuBarStyle.MENUBAR_TABLE);
      table.setCellPadding(0);
      table.setCellSpacing(0);
      DOM.setElementAttribute(table.getElement(), "border", "0");
      absolutePanel.add(table);
   }

   /**
    * Create and add new item in menu.
    * 
    * @param title title of new item
    * @return new instance of MenuBarItem which extends MenuItem 
    */
   public MenuItem addItem(String title)
   {
      return addItem(null, title);
   }

   /**
    * Create and add new item in menu.
    * 
    * @param title title of new item
    * @param command command, which will be executed when menu item will be selected
    * @return new instance of MenuBarItem which extends MenuItem
    */
   public MenuItem addItem(String title, Command command)
   {
      return addItem(null, title, command);
   }

   /**
    * Create and add new item in menu.
    * 
    * @param icon item's icon which must be represented as HTML image. Image must be prepared like "<img ... />" tag
    * @param title title of new item
    * @return new instance of MenuBarItem which extends MenuItem
    */
   public MenuItem addItem(String icon, String title)
   {
      return addItem(icon, title, null);
   }

   /**
    * Create and add new item in menu.
    * 
    * @param icon item's icon which must be represented as HTML image. Image must be prepared like "<img ... />" tag
    * @param title title of new item
    * @param command command, which will be executed when menu item will be selected
    * @return new instance of MenuBarItem which extends MenuItem
    */
   public MenuItem addItem(String icon, String title, Command command)
   {
      table.setText(0, menuBarItems.size(), title);
      Element element = table.getCellFormatter().getElement(0, menuBarItems.size());
      MenuBarItem item = new MenuBarItem(icon, title, element, this);

      item.onMouseOut();
      menuBarItems.put(element, item);
      return item;
   }

   /**
    * Get list of items.
    * 
    * @return list of items
    */
   public List<MenuItem> getItems()
   {
      List<MenuItem> items = new ArrayList<MenuItem>();

      Iterator<MenuBarItem> itemIter = menuBarItems.values().iterator();
      while (itemIter.hasNext())
      {
         MenuBarItem item = itemIter.next();
         items.add(item);
      }

      return items;
   }

   /**
    * Handle closing of all popup windows.
    * 
    * @see org.exoplatform.gwtframework.ui.client.CloseMenuHandler.menu.nn.impl.CloseMenuCallback#onCloseMenu()
    */
   public void onCloseMenu()
   {
      selectedMenuBarItem.setNormalState();
      selectedMenuBarItem = null;
      lockLayer = null;
   }

   /**
    * Handle selection of Menu Item.
    * 
    * @see org.exoplatform.gwtframework.ui.client.ItemSelectedHandler.menu.nn.impl.ItemSelectedCallback#onMenuItemSelected(org.exoplatform.gwtframework.ui.client.extension.menu.nn.api.MenuItem)
    */
   public void onMenuItemSelected(MenuItem menuItem)
   {
      if (menuItem instanceof MenuBarItem)
      {
         MenuBarItem item = (MenuBarItem)menuItem;
         if (selectedMenuBarItem != null && selectedMenuBarItem != menuItem)
         {
            selectedMenuBarItem.setNormalState();
            selectedMenuBarItem.closePopupMenu();
         }

         selectedMenuBarItem = item;
      }
      else if (menuItem instanceof PopupMenuItem)
      {
         lockLayer.close();
         lockLayer = null;
      }
   }

   /**
    * Open Popup Menu.
    * 
    * @param item - popup menu item.
    */
   public void openPopupMenu(MenuBarItem item)
   {
      if (lockLayer == null)
      {
         int top = getAbsoluteTop() + getOffsetHeight();
         lockLayer = new MenuLockLayer(this, top);
      }

      item.openPopupMenu(lockLayer);
   }

   private String toString(MenuItem menuItem, int depth)
   {
      String prefix = "";
      for (int i = 0; i < depth; i++)
      {
         prefix += "        ";
      }

      String str = "";
      if (menuItem.getTitle() == null)
      {
         str += prefix + "-------------------------------\r\n";
      }
      else
      {
         str += prefix + "[ " + menuItem.getTitle() + " ]\r\n";
      }

      for (MenuItem childIten : menuItem.getItems())
      {
         str += toString(childIten, depth + 1);
      }

      return str;
   }

   @Override
   public String toString()
   {
      String str = "";

      for (MenuItem menuItem : menuBarItems.values())
      {
         str += toString(menuItem, 0);
      }

      return str;
   }

}
