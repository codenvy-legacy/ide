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

import java.util.List;

import org.exoplatform.gwtframework.ui.client.command.Control;
import org.exoplatform.gwtframework.ui.client.command.StatusTextControl;
import org.exoplatform.gwtframework.ui.client.command.control.TextButtonControl;
import org.exoplatform.gwtframework.ui.client.event.UpdateStatusBarEvent;
import org.exoplatform.gwtframework.ui.client.event.UpdateStatusBarHandler;
import org.exoplatform.gwtframework.ui.client.toolbar.Toolbar;

import com.google.gwt.event.shared.HandlerManager;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class StatusBar implements UpdateStatusBarHandler
{

   private HandlerManager eventBus;

   private Toolbar statusbar;

   public StatusBar(HandlerManager eventBus, Toolbar statusbar)
   {
      this.eventBus = eventBus;
      this.statusbar = statusbar;

      eventBus.addHandler(UpdateStatusBarEvent.TYPE, this);
   }

   public void onRefreshStatusBar(UpdateStatusBarEvent event)
   {
      boolean rightDocking = false;

      for (String id : event.getStatusBarItems())
      {
         if ("".equals(id))
         {
            rightDocking = true;
            continue;
         }

         Control command = getCommand(id, event.getCommands());
         if (command == null)
         {
            continue;
         }

         /*
          * accept only StatusText and Loader controls
          */

         if (command instanceof StatusTextControl)
         {
            StatusTextControl statusTextControl = (StatusTextControl)command;
            TextButtonControl statusText = new TextButtonControl(eventBus, statusTextControl, statusbar);
            statusbar.addItem(statusText, rightDocking);
         }
      }
   }

   private Control getCommand(String commandId, List<Control> commands)
   {
      for (Control command : commands)
      {
         if (commandId.equals(command.getId()))
         {
            return command;
         }
      }

      return null;
   }

}
