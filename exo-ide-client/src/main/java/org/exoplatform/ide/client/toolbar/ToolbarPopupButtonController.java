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

package org.exoplatform.ide.client.toolbar;

import org.exoplatform.gwtframework.ui.client.button.PopupMenuButton;
import org.exoplatform.gwtframework.ui.client.command.ControlStateListener;
import org.exoplatform.gwtframework.ui.client.command.PopupMenuControl;
import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.gwtframework.ui.client.menu.MenuItem;
import org.exoplatform.gwtframework.ui.client.toolbar.Toolbar;
import org.exoplatform.gwtframework.ui.client.util.ImageHelper;
import org.exoplatform.ide.client.menu.MenuItemCommand;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ToolbarPopupButtonController implements ControlStateListener
{

   private HandlerManager eventBus;

   private final static String DISABLED_SUFFIX = "_Disabled";

   private PopupMenuControl popupMenuControl;

   private Toolbar toolbar;

   private PopupMenuButton button;

   private Widget wrapperWidget;

   public ToolbarPopupButtonController(HandlerManager eventBus, PopupMenuControl popupMenuControl, Toolbar toolbar)
   {
      this.eventBus = eventBus;
      this.popupMenuControl = popupMenuControl;
      this.toolbar = toolbar;
      popupMenuControl.getStateListeners().add(this);

      refreshButton();
      refreshItems();
   }

   private void refreshButton()
   {
      String icon = "";
      if (popupMenuControl.getNormalImage() != null)
      {
         icon = ImageHelper.getImageHTML(popupMenuControl.getNormalImage());
      }
      else if (popupMenuControl.getIcon() != null)
      {
         icon = ImageHelper.getImageHTML(popupMenuControl.getIcon());
      }

      String disabledIcon = "";
      if (popupMenuControl.getDisabledImage() != null)
      {
         disabledIcon = ImageHelper.getImageHTML(popupMenuControl.getDisabledImage());
      }
      else if (popupMenuControl.getIcon() != null)
      {
         String disabledIconURL = popupMenuControl.getIcon();
         disabledIconURL = popupMenuControl.getIcon().substring(0, popupMenuControl.getIcon().lastIndexOf("."));
         disabledIconURL += DISABLED_SUFFIX;
         disabledIconURL += popupMenuControl.getIcon().substring(popupMenuControl.getIcon().lastIndexOf("."));
         disabledIcon = ImageHelper.getImageHTML(disabledIconURL);
      }

      if (button == null)
      {
         button = new PopupMenuButton(icon, disabledIcon);
         button.setEnabled(popupMenuControl.isEnabled());
      }
      else
      {
         button.setIcon(icon);
         button.setDisabledIcon(disabledIcon);
      }

      button.setTitle(popupMenuControl.getPrompt());
   }

   private void refreshItems()
   {
      for (SimpleControl command : popupMenuControl.getCommands())
      {
         String icon;

         if (command.isEnabled())
         {
            if (command.getNormalImage() != null)
            {
               icon = ImageHelper.getImageHTML(command.getNormalImage());
            }
            else
            {
               icon = ImageHelper.getImageHTML(command.getIcon());
            }
         }
         else
         {
            if (command.getDisabledImage() != null)
            {
               icon = ImageHelper.getImageHTML(command.getDisabledImage());
            }
            else
            {
               icon = ImageHelper.getImageHTML(command.getIcon());
            }

         }

         if (command.hasDelimiterBefore())
         {
            button.addItem(null);
         }

         MenuItem menuItem = button.addItem(icon, command.getTitle());
         Command c = new MenuItemCommand(eventBus, menuItem, command);
      }

   }

   public PopupMenuButton getButton()
   {
      return button;
   }

   public void setWrapper(Widget wrapperWidget)
   {
      this.wrapperWidget = wrapperWidget;
      wrapperWidget.setVisible(popupMenuControl.isVisible());
   }

   public void updateControlEnabling(boolean enabled)
   {
      button.setEnabled(enabled);
   }

   public void updateControlVisibility(boolean visible)
   {
      wrapperWidget.setVisible(visible);
      toolbar.hideDuplicatedDelimiters();
   }

   public void updateControlPrompt(String prompt)
   {
      button.setTitle(popupMenuControl.getPrompt());
   }

   public void updateControlIcon(String icon)
   {
      System.out.println("ToolbarPopupButtonController.updateControlIcon()");
   }

}
