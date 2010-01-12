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
package org.exoplatform.ideall.client.toolbar;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.ideall.client.Handlers;
import org.exoplatform.ideall.client.application.command.AbstractCommand;
import org.exoplatform.ideall.client.application.command.DummyCommand;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.toolbar.event.UpdateToolbarEvent;
import org.exoplatform.ideall.client.toolbar.event.UpdateToolbarHandler;

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

      void updateToolBar(List<AbstractCommand> leftDockedItems, List<AbstractCommand> rightDockedItems);

   }

   private HandlerManager eventBus;

   private ApplicationContext context;

   private Handlers handlers;

   private Display display;

   public GWTToolbarPresenter(HandlerManager eventBus, ApplicationContext context)
   {
      this.eventBus = eventBus;
      this.context = context;
      handlers = new Handlers(eventBus);
   }

   public void bindDisplay(Display d)
   {
      display = d;
      handlers.addHandler(UpdateToolbarEvent.TYPE, this);
   }

   private AbstractCommand getCommandById(String id)
   {
      for (AbstractCommand command : context.getCommands())
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
      ArrayList<AbstractCommand> leftDockedItems = new ArrayList<AbstractCommand>();
      ArrayList<AbstractCommand> rightDockedItems = new ArrayList<AbstractCommand>();

      boolean rightDocking = false;
      for (String id : context.getToolBarItems())
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
                  rightDockedItems.add(0, new DummyCommand(id));
               }
               else
               {
                  leftDockedItems.add(new DummyCommand(id));
               }
            }
            else
            {
               AbstractCommand command = getCommandById(id);
               if (rightDocking)
               {
                  rightDockedItems.add(0, command);
               }
               else
               {
                  leftDockedItems.add(command);
               }
            }
         }
      }

      display.updateToolBar(leftDockedItems, rightDockedItems);
   }

}
