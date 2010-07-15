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
package org.exoplatform.ideall.client.framework.plugin;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.ui.client.component.command.Control;
import org.exoplatform.ideall.client.framework.model.AbstractApplicationContext;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public abstract class AbstractIDEModule implements IDEModule
{

   protected HandlerManager eventBus;

   protected AbstractApplicationContext context;

   protected Handlers handlers;

   public AbstractIDEModule(HandlerManager eventBus, AbstractApplicationContext context)
   {
      this.eventBus = eventBus;
      this.context = context;
      handlers = new Handlers(eventBus);
   }

   protected void addControl(Control control)
   {
      context.getCommands().add(control);
   }

   protected void addControl(Control control, boolean dockOnToolbar)
   {
      addControl(control, dockOnToolbar, false);
   }

   protected void addControl(Control control, boolean dockOnToolbar, boolean rightDocking)
   {
      context.getCommands().add(control);
      if (!dockOnToolbar)
      {
         return;
      }

      if (rightDocking)
      {
         context.getToolBarItems().add(control.getId());
      }
      else
      {
         int position = 0;
         for (String curId : context.getToolBarItems())
         {
            if ("".equals(curId))
            {
               break;
            }
            position++;
         }

         if (control.hasDelimiterBefore())
         {
            context.getToolBarItems().add(position, "---");
            position++;
         }

         context.getToolBarItems().add(position, control.getId());
      }
   }

}
