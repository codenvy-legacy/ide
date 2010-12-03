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

package org.exoplatform.ide.client.application;

import java.util.List;

import org.exoplatform.gwtframework.ui.client.component.command.Control;
import org.exoplatform.gwtframework.ui.client.component.command.SimpleControl;
import org.exoplatform.gwtframework.ui.client.component.command.SimpleControlStateListener;
import org.exoplatform.gwtframework.ui.client.menu.ImageHelper;
import org.exoplatform.gwtframework.ui.client.menu.api.Menu;
import org.exoplatform.gwtframework.ui.client.menu.api.MenuItem;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Command;

/**
 * 
 * This class build and refresh top menu.
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class IDEMenu
{

   private static IDEMenu instance;

   public static IDEMenu getInstance()
   {
      return instance;
   }

   private Menu menu;

   private HandlerManager eventBus;

   public IDEMenu(HandlerManager eventBus)
   {
      instance = this;
      this.eventBus = eventBus;
   }

   public void setMenu(Menu menu)
   {
      this.menu = menu;
   }

   public void refreshMenu(List<Control> commands)
   {
      for (Control command : commands)
      {
         try
         {
            if (command instanceof SimpleControl)
            {
               getOrCeateMenuItem(command.getId(), (MenuItem)null, (SimpleControl)command);
            }
         }
         catch (Exception exc)
         {
            exc.printStackTrace();
         }
      }
   }

   private void getOrCeateMenuItem(String path, MenuItem parent, SimpleControl command)
   {
      if (path.indexOf("/") > 0)
      {
         String parentItemName = path.split("/")[0];
         MenuItem item = getItemByTitle(parent, parentItemName);
         if (item == null)
         {
            item = menu.addItem(parentItemName);
         }

         String rest = path.substring(path.indexOf("/") + 1);
         getOrCeateMenuItem(rest, item, command);
      }
      else
      {
         if (command.hasDelimiterBefore())
         {
            parent.addItem(null);
         }

         MenuItem item = parent.addItem(command.getTitle());
         new MenuItemCommand(item, command);
      }

   }

   private MenuItem getItemByTitle(MenuItem menuItem, String title)
   {
      if (menuItem == null)
      {
         for (MenuItem item : menu.getItems())
         {
            if (title.equals(item.getTitle()))
            {
               return item;
            }
         }
      }
      else
      {
         for (MenuItem item : menuItem.getItems())
         {
            if (title.equals(item.getTitle()))
            {
               return item;
            }
         }
      }

      return null;
   }

   private class MenuItemCommand implements Command, SimpleControlStateListener
   {

      private MenuItem menuItem;

      private SimpleControl command;

      public MenuItemCommand(MenuItem menuItem, SimpleControl command)
      {
         this.menuItem = menuItem;
         this.command = command;

         updateItemIcon();
         menuItem.setCommand(this);
         menuItem.setEnabled(command.isEnabled());
         menuItem.setVisible(command.isVisible());
         menuItem.setSelected(command.isSelected());
         menuItem.setHotKey(command.getHotKey());
         command.getStateListeners().add(this);
      }

      private void updateItemIcon()
      {
         String icon = "";
         if (command.isEnabled())
         {
            if (command.getNormalImage() != null)
            {
               icon = ImageHelper.getImageHTML(command.getNormalImage());
            } else if (command.getIcon() != null) {
               icon = ImageHelper.getImageHTML(command.getIcon());
            }
         }
         else
         {
            if (command.getDisabledImage() != null)
            {
               icon = ImageHelper.getImageHTML(command.getDisabledImage());
            } else if (command.getIcon() != null) {
               String iconNormal = command.getIcon();
               String iconDisabled = iconNormal.substring(0, iconNormal.lastIndexOf("."));
               iconDisabled += "_Disabled";
               iconDisabled += iconNormal.substring(iconNormal.lastIndexOf("."));
               icon = ImageHelper.getImageHTML(iconDisabled);
            }
         }

         menuItem.setIcon(icon);
      }

      public void updateControlEnabling(boolean enabled)
      {
         menuItem.setEnabled(enabled);
         updateItemIcon();
      }

      public void updateControlVisibility(boolean visible)
      {
         menuItem.setVisible(visible);
      }

      public void updateControlPrompt(String prompt)
      {
      }

      public void updateControlIcon(String icon)
      {
         updateItemIcon();
      }

      public void execute()
      {
         if (command.getEvent() != null)
         {
            eventBus.fireEvent(command.getEvent());
         }
      }

      public void updateControlTitle(String title)
      {
         menuItem.setTitle(title);
      }

      public void updateControlSelectionState(boolean selected)
      {
         menuItem.setSelected(selected);
      }

      public void updateControlHotKey(String hotKey)
      {
         menuItem.setHotKey(hotKey);
      }

   }

}
