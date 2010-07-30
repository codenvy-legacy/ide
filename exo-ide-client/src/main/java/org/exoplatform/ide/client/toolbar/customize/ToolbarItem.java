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
package org.exoplatform.ide.client.toolbar.customize;

import org.exoplatform.gwtframework.ui.client.component.command.Control;

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

   private Control command;

   public ToolbarItem(Type type)
   {
      this.type = type;
   }

   public ToolbarItem(Type type, String id, Control command)
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

   public Control getCommand()
   {
      return command;
   }

}
