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
package org.exoplatform.ideall.client.toolbar.component;

import org.exoplatform.ideall.client.application.command.AbstractCommand;
import org.exoplatform.ideall.client.application.command.CommandStateListener;
import org.exoplatform.ideall.client.toolbar.GWTToolbar;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ToolbarButton extends Composite implements ToolbarItem, CommandStateListener
{

   public static interface Style
   {

      public final static String BUTTON_PANEL_LEFT = "exo-toolbar16ButtonPanel_Left";

      public final static String BUTTON_PANEL_RIGHT = "exo-toolbar16ButtonPanel_Right";

      public final static String BUTTON_PANEL_LEFT_HIDDEN = "exo-toolbar16ButtonPanel_LeftHidden";

      public final static String BUTTON_PANEL_RIGHT_HIDDEN = "exo-toolbar16ButtonPanel_RightHidden";

      public final static String BUTTON = "exo-toolbar16Button";

      public final static String BUTTON_OVER = "exo-toolbar16ButtonOver";

      public final static String BUTTON_DOWN = "exo-toolbar16ButtonDown";

      public final static String BUTTON_ICON = "exo-toolbar16Icon";

   }

   private final static String DISABLED_SUFFIX = "_Disabled";

   private HandlerManager eventBus;

   private AbstractCommand command;

   private SimplePanel simplePanel;

   private String iconNormal;

   private String iconDisabled;

   private ButtonPanel buttonPanel;

   private boolean rightDocking;

   private boolean pressedOnce = false;

   private GWTToolbar toolbar;

   public ToolbarButton(HandlerManager eventBus, AbstractCommand command, boolean rightDocking, GWTToolbar toolbar)
   {
      this.eventBus = eventBus;
      this.command = command;
      this.rightDocking = rightDocking;
      this.toolbar = toolbar;

      iconNormal = command.getIcon();
      iconDisabled = iconNormal.substring(0, iconNormal.lastIndexOf("."));
      iconDisabled += DISABLED_SUFFIX;
      iconDisabled += iconNormal.substring(iconNormal.lastIndexOf("."));

      simplePanel = new SimplePanel();
      initWidget(simplePanel);
      updateButtonVisibility();
      simplePanel.setTitle(command.getTitle());

      createButtonPanel();

      command.getStateListeners().add(this);
   }

   @Override
   protected void onDetach()
   {
      super.onDetach();
   }

   public AbstractCommand getCommand()
   {
      return command;
   }

   private String getImageHTML()
   {
      return "<img class=\"" + Style.BUTTON_ICON + "\" src=\"" + (command.isEnabled() ? iconNormal : iconDisabled)
         + "\" >";
   }

   private void createButtonPanel()
   {
      buttonPanel = new ButtonPanel();
      buttonPanel.setStyleName(Style.BUTTON);
      DOM.setInnerHTML(buttonPanel.getElement(), getImageHTML());
      simplePanel.add(buttonPanel);
   }

   protected void onMouseOver()
   {
      buttonPanel.setStyleName(Style.BUTTON_OVER);
      pressedOnce = false;
   }

   protected void onMouseOut()
   {
      buttonPanel.setStyleName(Style.BUTTON);
   }

   protected void onMouseDown()
   {
      buttonPanel.setStyleName(Style.BUTTON_DOWN);
      pressedOnce = true;
   }

   protected void onMouseUp()
   {
      buttonPanel.setStyleName(Style.BUTTON_OVER);

      if (pressedOnce)
      {
         pressedOnce = false;

         if (command.getEvent() != null)
         {
            eventBus.fireEvent(command.getEvent());
         }
      }
   }

   public void updateCommandEnabling(boolean enabled)
   {
      DOM.setInnerHTML(buttonPanel.getElement(), getImageHTML());
      buttonPanel.setStyleName(Style.BUTTON);
   }

   protected void updateButtonVisibility()
   {
      if (command.isVisible())
      {
         simplePanel.setStyleName(rightDocking ? Style.BUTTON_PANEL_RIGHT : Style.BUTTON_PANEL_LEFT);
      }
      else
      {
         simplePanel.setStyleName(rightDocking ? Style.BUTTON_PANEL_RIGHT_HIDDEN : Style.BUTTON_PANEL_LEFT_HIDDEN);
      }
   }

   public void updateCommandVisibility(boolean visible)
   {
      updateButtonVisibility();
      toolbar.checkDelimiters();
   };
   
   public boolean isVisible() {
      return command.isVisible();
   }

   private class ButtonPanel extends SimplePanel
   {

      public ButtonPanel()
      {
         sinkEvents(Event.ONMOUSEOVER | Event.ONMOUSEOUT | Event.ONMOUSEDOWN | Event.ONMOUSEUP);
      }

      @Override
      public void onBrowserEvent(Event event)
      {
         if (!command.isEnabled())
         {
            return;
         }

         switch (DOM.eventGetType(event))
         {
            case Event.ONMOUSEOVER :
               onMouseOver();
               break;

            case Event.ONMOUSEOUT :
               onMouseOut();
               break;

            case Event.ONMOUSEDOWN :
               onMouseDown();
               break;

            case Event.ONMOUSEUP :
               onMouseUp();
               break;
         }
      }

   }

}
