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

import org.exoplatform.ideall.client.solution.command.Command;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ToolbarItem
{

   public static enum Type {

      COMMAND, DELIMITER, SPACER

   }

   private Type type;

   private String id;

   private Command command;

   public ToolbarItem(Type type)
   {
      this.type = type;
   }

   public ToolbarItem(Type type, String id, Command command)
   {
      this.type = type;
      this.id = id;
      this.command = command;
   }

   public Type getType()
   {
      return type;
   }

   public String getId()
   {
      return id;
   }

   public Command getCommand()
   {
      return command;
   }

}
