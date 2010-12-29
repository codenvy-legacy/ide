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

import org.exoplatform.gwtframework.ui.client.button.IconButton;
import org.exoplatform.gwtframework.ui.client.command.ControlStateListener;
import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.gwtframework.ui.client.command.SimpleControlStateListener;
import org.exoplatform.gwtframework.ui.client.toolbar.Toolbar;
import org.exoplatform.gwtframework.ui.client.util.ImageHelper;

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

public class ToolbarButtonControl implements SimpleControlStateListener
{

   private HandlerManager eventBus;

   private final static String DISABLED_SUFFIX = "_Disabled";

   private SimpleControl control;

   private IconButton iconButton;

   private Widget wrapperWidget;

   private Toolbar toolbar;

   public ToolbarButtonControl(HandlerManager eventBus, SimpleControl control, Toolbar toolbar)
   {
      this.eventBus = eventBus;
      this.control = control;
      this.toolbar = toolbar;

      control.getStateListeners().add(this);

      refreshButton();
   }

   public IconButton getButton()
   {
      return iconButton;
   }

   public void setWrapper(Widget wrapperWidget)
   {
      this.wrapperWidget = wrapperWidget;
      wrapperWidget.setVisible(control.isVisible());
   }

   private void refreshButton()
   {
      String icon = "";
      if (control.getNormalImage() != null)
      {
         icon = ImageHelper.getImageHTML(control.getNormalImage());
      }
      else if (control.getIcon() != null)
      {
         icon = ImageHelper.getImageHTML(control.getIcon());
      }

      String disabledIcon = "";
      if (control.getDisabledImage() != null)
      {
         disabledIcon = ImageHelper.getImageHTML(control.getDisabledImage());
      }
      else if (control.getIcon() != null)
      {
         String disabledIconURL = control.getIcon();
         disabledIconURL = control.getIcon().substring(0, control.getIcon().lastIndexOf("."));
         disabledIconURL += DISABLED_SUFFIX;
         disabledIconURL += control.getIcon().substring(control.getIcon().lastIndexOf("."));
         disabledIcon = ImageHelper.getImageHTML(disabledIconURL);
      }

      if (iconButton == null)
      {
         iconButton = new IconButton(icon, disabledIcon, onClickCommand);
         iconButton.setEnabled(control.isEnabled());
      }
      else
      {
         iconButton.setIcon(icon);
         iconButton.setDisabledIcon(disabledIcon);
      }
      
      iconButton.setTitle(control.getPrompt());
   }

   public void updateControlEnabling(boolean enabled)
   {
      iconButton.setEnabled(enabled);
   }

   public void updateControlVisibility(boolean visible)
   {
      wrapperWidget.setVisible(visible);
      toolbar.hideDuplicatedDelimiters();
   }

   public void updateControlPrompt(String prompt)
   {
      iconButton.setTitle(prompt);
   }

   public void updateControlIcon(String icon)
   {
      refreshButton();
   }

   private Command onClickCommand = new Command()
   {
      public void execute()
      {
         if (control.isEnabled() && control.getEvent() != null)
         {
            eventBus.fireEvent(control.getEvent());
         }
      }
   };

   public void updateControlTitle(String title)
   {
   }

   public void updateControlSelectionState(boolean selected)
   {
      iconButton.setSelected(selected);
   }

   public void updateControlHotKey(String hotKey)
   {
   }

}
