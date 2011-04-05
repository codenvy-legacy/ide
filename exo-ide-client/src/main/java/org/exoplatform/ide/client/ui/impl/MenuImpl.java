/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.client.ui.impl;

import java.util.List;

import org.exoplatform.gwtframework.ui.client.command.Control;
import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.gwtframework.ui.client.command.ui.MenuItemControl;
import org.exoplatform.gwtframework.ui.client.menu.MenuBar;
import org.exoplatform.gwtframework.ui.client.menu.MenuItem;
import org.exoplatform.ide.client.ui.api.Menu;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class MenuImpl extends MenuBar implements Menu
{

   @Override
   public void refresh(List<Control> commands, HandlerManager eventBus)
   {
      for (Control command : commands)
      {
         if (!(command instanceof SimpleControl))
         {
            continue;
         }

         SimpleControl control = (SimpleControl)command;
         MenuItem createdItem = add(null, control, 0);
         new MenuItemControl(eventBus, createdItem, control);
      }
   }

   private MenuItem add(MenuItem parent, SimpleControl control, int depth)
   {
      String[] path = control.getId().split("/");

      MenuItem item = getItemByTitle(parent, path[depth]);
      if (item == null)
      {
         if (depth == 0)
         {
            item = addItem(path[0]);
         }
         else
         {
            if (depth == path.length - 1)
            {
               if (control.hasDelimiterBefore())
               {
                  parent.addItem(null);
               }
            }
            
            item = parent.addItem(path[depth]);
         }
      }

      if (depth < path.length - 1)
      {
         return add(item, control, depth + 1);
      }
      else
      {
         return item;
      }
   }

   private MenuItem getItemByTitle(MenuItem parent, String title)
   {
      if (parent == null)
      {
         for (MenuItem item : getItems())
         {
            if (title.equals(item.getTitle()))
            {
               return item;
            }
         }
      }
      else
      {
         for (MenuItem item : parent.getItems())
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
