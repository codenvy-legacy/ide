/*
 * Copyright (C) 2011 eXo Platform SAS.
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
 */
package org.exoplatform.ide.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class CommandQueue
{

   private static CommandQueue instance;

   public static CommandQueue getInstance()
   {
      if (instance == null)
      {
         instance = new CommandQueue();
      }
      return instance;
   }

   private List<Command> commands = new ArrayList<Command>();

   private List<GwtEvent.Type<?>> eventTypes = new ArrayList<GwtEvent.Type<?>>();

   public void addCommand(Command command, GwtEvent.Type<?> eventType)
   {
      commands.add(command);
      eventTypes.add(eventType);
   }

   public void clear()
   {
      commands.clear();
   }

   public void eventFired(GwtEvent.Type<?> type)
   {
      if (commands.size() == 0)
      {
         return;
      }

      if (eventTypes.get(0) != type)
      {
         return;
      }

      commands.remove(0);
      eventTypes.remove(0);
      run();
   }

   public void run()
   {
      if (commands.size() == 0)
      {
         return;
      }

      new Timer()
      {
         @Override
         public void run()
         {
            commands.get(0).execute();
         }
      }.schedule(100);
   }

}
