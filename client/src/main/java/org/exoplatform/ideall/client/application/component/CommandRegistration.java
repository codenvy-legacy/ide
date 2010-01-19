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

import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.solution.command.Command;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class CommandRegistration
{

   private Command command;

   private ApplicationContext context;

   public CommandRegistration(Command command, ApplicationContext context)
   {
      this.command = command;
      this.context = context;
   }

   public CommandRegistration enable()
   {
      command.setEnabled(true);
      return this;
   }

   public CommandRegistration disable()
   {
      command.setEnabled(false);
      return this;
   }

   public CommandRegistration show()
   {
      command.setVisible(true);
      return this;
   }

   public CommandRegistration hide()
   {
      command.setVisible(false);
      return this;
   }

   public CommandRegistration select()
   {
      command.setSelected(true);
      return this;
   }

   public CommandRegistration deselect()
   {
      command.setSelected(false);
      return this;
   }

   public CommandRegistration setDelimiterBefore()
   {
      command.setDelimiterBefore(true);
      return this;
   }

   public CommandRegistration dockOnToolbar()
   {
      dockOnToolbar(false);
      return this;
   }

   public void dockOnToolbar(boolean rightDocking)
   {
      if (rightDocking)
      {
         context.getToolBarItems().add(command.getId());
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

         context.getToolBarItems().add(position, command.getId());
      }

   }

}
