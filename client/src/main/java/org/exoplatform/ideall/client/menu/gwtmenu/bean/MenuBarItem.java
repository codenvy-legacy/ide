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

import org.exoplatform.ideall.client.application.command.AbstractCommand;
import org.exoplatform.ideall.client.application.command.CommandStateListener;
import org.exoplatform.ideall.client.menu.gwtmenu.GWTMenuBar;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class MenuBarItem extends MenuItem implements CommandStateListener
{

   public MenuBarItem(String title, AbstractCommand command)
   {
      super(title, command);
   }

   private Element element;

   public void initElement(Element element)
   {
      this.element = element;
      if (command != null) {
         command.getStateListeners().add(this);         
      }

      boolean enabled = command == null ? true : command.isEnabled();

      if (enabled)
      {
         element.setClassName(GWTMenuBar.Style.ITEM);
      }
      else
      {
         element.setClassName(GWTMenuBar.Style.ITEM_DISABLED);
      }

      DOM.setElementAttribute(element, GWTMenuBar.TITLE_PROPERTY, title);
      DOM.setElementAttribute(element, GWTMenuBar.ENABLED_PROPERTY, "" + enabled);
   }

   public void updateCommandEnabling(boolean enabled)
   {
      element.setClassName(enabled ? GWTMenuBar.Style.ITEM : GWTMenuBar.Style.ITEM_DISABLED);
      DOM.setElementAttribute(element, GWTMenuBar.ENABLED_PROPERTY, "" + enabled);
   }

   public void updateCommandVisibility(boolean visible)
   {
   }

}
