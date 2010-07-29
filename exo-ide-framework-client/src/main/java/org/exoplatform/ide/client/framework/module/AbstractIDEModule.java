/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.client.framework.module;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.ui.client.component.command.Control;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public class AbstractIDEModule implements IDEModule
{

   protected HandlerManager eventBus;

   protected Handlers handlers;
  
   private List<Control> controls = new ArrayList<Control>();

   private List<String> toolbarItems = new ArrayList<String>();
   
   public AbstractIDEModule(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      handlers = new Handlers(eventBus);
      toolbarItems.add("");
   }

//   protected void addControl(Control control)
//   {
//      controls.add(control);
//   }
//
//   protected void addControl(Control control, boolean dockOnToolbar)
//   {
//      addControl(control, dockOnToolbar, false);
//   }
//
//   protected void addControl(Control control, boolean dockOnToolbar, boolean rightDocking)
//   {
//      controls.add(control);
//      if (!dockOnToolbar)
//      {
//         return;
//      }
//
//      if (rightDocking)
//      {
//         toolbarItems.add(control.getId());
//      }
//      else
//      {
//         int position = 0;
//         for (String curId : toolbarItems)
//         {
//            if ("".equals(curId))
//            {
//               break;
//            }
//            position++;
//         }
//
//         if (control.hasDelimiterBefore())
//         {
//            toolbarItems.add(position, "---");
//            position++;
//         }
//
//         toolbarItems.add(position, control.getId());
//      }
//   }
//
//   public List<Control> getControls()
//   {
//      return null;
//   }
//
//   public List<String> getStatusbarItems()
//   {
//      return null;
//   }
//
//   public List<String> getToolbarItems()
//   {
//      return null;
//   }

}
