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
package org.exoplatform.ideall.client.application.component;

import org.exoplatform.ideall.client.application.command.AbstractCommand;
import org.exoplatform.ideall.client.model.ApplicationContext;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public abstract class AbstractComponentInitializer
{

   private ApplicationContext context;

   protected void addCommand(AbstractCommand command)
   {
      context.getCommands().add(command);
   }

   protected void addCommand(AbstractCommand command, boolean showOnToolbar, boolean rightDocking)
   {
      context.getCommands().add(command);

      if (showOnToolbar)
      {
         addToToolBar(command.getId(), rightDocking);
      }
   }

   protected void addToToolBar(String id, boolean rightDocking)
   {
      if (rightDocking)
      {
         context.getToolBarItems().add(id);
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

         context.getToolBarItems().add(position, id);
      }
   }

   protected void addToolbarDelimiter(boolean toolbarRightDocking)
   {
      addToToolBar("---", toolbarRightDocking);
   }

   public void initializeComponent(HandlerManager eventBus, ApplicationContext context)
   {
      this.context = context;
      onItitialize();
   }

   protected abstract void onItitialize();

}
