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

package org.exoplatform.gwtframework.ui.client.command.ui;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.gwtframework.ui.client.command.SimpleControlStateListener;
import org.exoplatform.gwtframework.ui.client.component.IconButton;
import org.exoplatform.gwtframework.ui.client.component.Toolbar;
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

public class IconButtonControl extends IconButton implements SimpleControlStateListener
{

   private final static String DISABLED_SUFFIX = "_Disabled";

   private HandlerManager eventBus;

   private SimpleControl control;

   private Toolbar toolbar;

   public IconButtonControl(HandlerManager eventBus, SimpleControl control, Toolbar toolbar)
   {
      super((String)null, (String)null);
      setCommand(iconButtonCommand);

      this.eventBus = eventBus;
      this.control = control;
      this.toolbar = toolbar;

      setIcon(getControlIcon());
      setDisabledIcon(getControlDisabledIcon());
      setEnabled(control.isEnabled());
      setTitle(control.getPrompt());
   }

   @Override
   protected void onAttach()
   {
      super.onAttach();
      getParent().setVisible(control.isVisible());
      control.getStateListeners().add(this);
   }

   @Override
   protected void onDetach()
   {
      super.onDetach();
      control.getStateListeners().remove(this);
   }

   protected String getControlIcon()
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

      return icon;
   }

   protected String getControlDisabledIcon()
   {
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

      return disabledIcon;
   }

   private Command iconButtonCommand = new Command()
   {
      public void execute()
      {
         if (control.getEvent() != null)
         {
            eventBus.fireEvent(control.getEvent());
         }
      }
   };

   public void updateControlEnabling(boolean enabled)
   {
      setEnabled(enabled);
   }

   public void updateControlVisibility(boolean visible)
   {
      getParent().setVisible(visible);
      toolbar.hideDuplicatedDelimiters();
   }

   public void updateControlPrompt(String prompt)
   {
      setTitle(prompt);
   }

   public void updateControlIcon(String icon)
   {
      String iconHtml = "";
      if (control.isEnabled())
      {
         if (control.getNormalImage() != null)
         {
            iconHtml = ImageHelper.getImageHTML(control.getNormalImage());
         } else if (control.getIcon() != null) {
            iconHtml = ImageHelper.getImageHTML(control.getIcon());
         }
      }
      else
      {
         if (control.getDisabledImage() != null)
         {
            iconHtml = ImageHelper.getImageHTML(control.getDisabledImage());
         } else if (control.getIcon() != null) {
            String iconNormal = control.getIcon();
            String iconDisabled = iconNormal.substring(0, iconNormal.lastIndexOf("."));
            iconDisabled += "_Disabled";
            iconDisabled += iconNormal.substring(iconNormal.lastIndexOf("."));
            iconHtml = ImageHelper.getImageHTML(iconDisabled);
         }
      }
      setIcon(iconHtml);
   }

   public void updateControlTitle(String title)
   {
   }

   public void updateControlSelectionState(boolean selected)
   {
      setSelected(selected);
   }

   public void updateControlHotKey(String hotKey)
   {
   }

}
