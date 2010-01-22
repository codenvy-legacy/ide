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
package org.exoplatform.ideall.client.solution.toolbar;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.ideall.client.Handlers;
import org.exoplatform.ideall.client.solution.command.Command;
import org.exoplatform.ideall.client.solution.toolbar.bean.ToolbarItem;
import org.exoplatform.ideall.client.solution.toolbar.event.UpdateToolbarEvent;
import org.exoplatform.ideall.client.solution.toolbar.event.UpdateToolbarHandler;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class GWTToolbarPresenter implements UpdateToolbarHandler
{

   public interface Display
   {

      void updateToolBar(List<ToolbarItem> leftDockedItems, List<ToolbarItem> rightDockedItems);

   }

   private HandlerManager eventBus;

   private Handlers handlers;

   private Display display;

   public GWTToolbarPresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      handlers = new Handlers(eventBus);
   }

   public void bindDisplay(Display d)
   {
      display = d;
      handlers.addHandler(UpdateToolbarEvent.TYPE, this);
   }

   private Command getCommandById(String id, ArrayList<Command> commands)
   {
      for (Command command : commands)
      {
         if (id.equals(command.getId()))
         {
            return command;
         }
      }

      return null;
   }

   public void onUpdateToolbar(UpdateToolbarEvent event)
   {
      ArrayList<ToolbarItem> leftDockedItems = new ArrayList<ToolbarItem>();
      ArrayList<ToolbarItem> rightDockedItems = new ArrayList<ToolbarItem>();

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
                  ToolbarItem delimiter = new ToolbarItem();
                  rightDockedItems.add(0, delimiter);
               }
               else
               {
                  ToolbarItem delimiter = new ToolbarItem();
                  leftDockedItems.add(delimiter);
               }
            }
            else
            {
               Command command = getCommandById(id, event.getCommands());
               
               if (rightDocking)
               {
                  ToolbarItem commandItem = new ToolbarItem(command);
                  rightDockedItems.add(0, commandItem);
               }
               else
               {
                  ToolbarItem commandItem = new ToolbarItem(command);
                  leftDockedItems.add(commandItem);
               }
            }

         }
      }

      display.updateToolBar(leftDockedItems, rightDockedItems);
   }

}
