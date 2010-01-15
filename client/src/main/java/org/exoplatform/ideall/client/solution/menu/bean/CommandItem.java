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
package org.exoplatform.ideall.client.solution.menu.bean;

import java.util.LinkedHashMap;

import org.exoplatform.ideall.client.solution.command.Command;
import org.exoplatform.ideall.client.solution.command.CommandStateListener;

import com.google.gwt.user.client.Element;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class CommandItem implements MenuItem, CommandStateListener, MenuItemStateListener
{

   private String title;

   private Command command;

   private Element element;

   private LinkedHashMap<String, MenuItem> children = new LinkedHashMap<String, MenuItem>();

   private MenuItemStateListener menuItemStateListener;

   public CommandItem(String title, Command command, MenuItemStateListener menuItemStateListener)
   {
      this.title = title;
      this.command = command;
      this.menuItemStateListener = menuItemStateListener;
      command.getStateListeners().add(this);
   }

   public String getTitle()
   {
      return title;
   }

   public LinkedHashMap<String, MenuItem> getChildren()
   {
      return children;
   }

   public void initElement(Element element)
   {
      this.element = element;
   }

   public Command getCommand()
   {
      return command;
   }

   public void updateCommandEnabling(boolean enabled)
   {
      menuItemStateListener.updateMenuItemState();
   }

   public void updateCommandSelectedState(boolean selected)
   {
   }

   public void updateCommandVisibility(boolean visible)
   {
      menuItemStateListener.updateMenuItemState();
   }

   public void updateMenuItemState()
   {
      menuItemStateListener.updateMenuItemState();
   }

}
