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
package org.exoplatform.ideall.client.toolbar.customize;

import org.exoplatform.gwtframework.ui.client.component.command.Command;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class CommandItemEx
{

   private String title;

   private Command command;

   private boolean group = false;

   public CommandItemEx(String title, Command command)
   {
      this.title = title;
      this.command = command;
   }

   public CommandItemEx(String title, boolean group)
   {
      this.title = title;
      this.group = group;
   }

   public String getTitle()
   {
      return title;
   }

   public Command getCommand()
   {
      return command;
   }

   public boolean isGroup()
   {
      return group;
   }

}
