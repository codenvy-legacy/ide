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

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.ui.client.command.Control;
import org.exoplatform.gwtframework.ui.client.command.PopupMenuControl;
import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.gwtframework.ui.client.command.control.IconButtonControl;
import org.exoplatform.gwtframework.ui.client.command.control.PopupMenuButtonControl;
import org.exoplatform.gwtframework.ui.client.event.UpdateToolbarEvent;
import org.exoplatform.gwtframework.ui.client.event.UpdateToolbarHandler;
import org.exoplatform.gwtframework.ui.client.toolbar.Toolbar;

import com.google.gwt.event.shared.HandlerManager;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class IDEToolbar implements UpdateToolbarHandler
{

   private Toolbar toolbar;

   private HandlerManager eventBus;

   public IDEToolbar(HandlerManager eventBus, Toolbar toolbar)
   {
      this.eventBus = eventBus;
      this.toolbar = toolbar;

      eventBus.addHandler(UpdateToolbarEvent.TYPE, this);
   }

   public void onUpdateToolbar(UpdateToolbarEvent event)
   {
      toolbar.clear();

      List<String> leftItems = new ArrayList<String>();
      List<String> rightItems = new ArrayList<String>();

      boolean rightDocking = false;
      for (String id : event.getToolBarItems())
      {
         if ("".equals(id))
         {
            rightDocking = true;
         }
         else
         {
            if (id.startsWith("---"))
            {
               if (rightDocking)
               {
                  rightItems.add(0, id);
               }
               else
               {
                  leftItems.add(id);
               }
            }
            else
            {
               Control control = getControl(event.getCommands(), id);
               if (control == null)
               {
                  continue;
               }

               if (rightDocking)
               {
                  rightItems.add(0, id);
               }
               else
               {
                  leftItems.add(id);
               }
            }

         }
      }

      addItems(leftItems, event.getCommands(), false);
      addItems(rightItems, event.getCommands(), true);
      toolbar.hideDuplicatedDelimiters();
   }

   private void addItems(List<String> items, List<Control> controls, boolean right)
   {
      for (String item : items)
      {
         if ("---".equals(item))
         {
            if (right)
            {
               toolbar.addDelimiter(true);
            }
            else
            {
               toolbar.addDelimiter();
            }

            continue;
         }

         Control control = getControl(controls, item);
         if (control != null)
         {
            if (control instanceof SimpleControl)
            {
               addIconButton((SimpleControl)control, right);
            }
            else if (control instanceof PopupMenuControl)
            {
               addPopupMenuButton((PopupMenuControl)control, right);
            }

         }

      }
   }

   private void addIconButton(SimpleControl simpleControl, boolean rightDocking)
   {
      IconButtonControl iconButtonControl = new IconButtonControl(eventBus, simpleControl, toolbar);
      toolbar.addItem(iconButtonControl, rightDocking);
   }

   private void addPopupMenuButton(PopupMenuControl popupMenuControl, boolean rightDocking)
   {
      PopupMenuButtonControl popupMenuButtonControl = new PopupMenuButtonControl(eventBus, popupMenuControl, toolbar);
      toolbar.addItem(popupMenuButtonControl, rightDocking);
   }

   private Control getControl(List<Control> controls, String controlId)
   {
      for (Control<?> c : controls)
      {
         if (c.getId().equals(controlId))
         {
            return c;
         }
      }

      return null;
   }

}
