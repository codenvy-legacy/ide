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
package org.exoplatform.ideall.client.menu.gwtmenu.bean;

import java.util.LinkedHashMap;

import org.exoplatform.ideall.client.application.command.AbstractCommand;

import com.google.gwt.user.client.Element;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public abstract class MenuItem
{

   protected AbstractCommand command;

   protected String title;

   protected LinkedHashMap<String, PopupMenuItem> popupItems = new LinkedHashMap<String, PopupMenuItem>();

   public LinkedHashMap<String, PopupMenuItem> getPopupItems()
   {
      return popupItems;
   }

   public MenuItem(String title)
   {
      this.title = title;
   }

   public MenuItem(String title, AbstractCommand command)
   {
      this.title = title;
      this.command = command;
   }

   public AbstractCommand getCommand()
   {
      return command;
   }

   public String getTitle()
   {
      return title;
   }

   public abstract void initElement(Element element);

}
