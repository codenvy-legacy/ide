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

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.gwtframework.ui.client.command.SimpleControlStateListener;
import org.exoplatform.gwtframework.ui.client.menu.MenuItem;
import org.exoplatform.gwtframework.ui.client.util.ImageHelper;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Command;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class MenuItemCommand implements Command, SimpleControlStateListener
{
   
   private HandlerManager eventBus;

   private MenuItem menuItem;

   private SimpleControl command;

   public MenuItemCommand(HandlerManager eventBus, MenuItem menuItem, SimpleControl command)
   {
      this.eventBus = eventBus;
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

