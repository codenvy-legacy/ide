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

package org.exoplatform.ide.client.menu;

import java.util.List;

import org.exoplatform.gwtframework.ui.client.command.Control;
import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.gwtframework.ui.client.command.control.MenuItemControl;
import org.exoplatform.gwtframework.ui.client.menu.MenuBar;
import org.exoplatform.gwtframework.ui.client.menu.MenuItem;

import com.google.gwt.event.shared.HandlerManager;

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

   private MenuBar menu;

   private HandlerManager eventBus;

   public IDEMenu(HandlerManager eventBus)
   {
      instance = this;
      this.eventBus = eventBus;
   }

   public void setMenu(MenuBar menu)
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
         new MenuItemControl(eventBus, item, command);
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

}
