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
package org.exoplatform.ideall.client.menu;

import java.util.LinkedHashMap;

import org.exoplatform.ideall.client.Handlers;
import org.exoplatform.ideall.client.application.command.AbstractCommand;
import org.exoplatform.ideall.client.menu.bean.MenuBarItem;
import org.exoplatform.ideall.client.menu.bean.PopupMenuItem;
import org.exoplatform.ideall.client.menu.event.GWTMenuItemSelectedEvent;
import org.exoplatform.ideall.client.menu.event.GWTMenuItemSelectedHandler;
import org.exoplatform.ideall.client.menu.event.UpdateMainMenuEvent;
import org.exoplatform.ideall.client.menu.event.UpdateMainMenuHandler;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class GWTMenuBarPresenter implements UpdateMainMenuHandler, GWTMenuItemSelectedHandler
{

   public interface Display
   {

      void buildMenu(LinkedHashMap<String, MenuBarItem> menuBarItems);

      void closeMenu();

   }

   private HandlerManager eventBus;

   private Display display;

   private Handlers handlers;

   public GWTMenuBarPresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      handlers = new Handlers(eventBus);
   }

   public void bindDisplay(Display d)
   {
      display = d;
      handlers.addHandler(UpdateMainMenuEvent.TYPE, this);
      handlers.addHandler(GWTMenuItemSelectedEvent.TYPE, this);
   }

   public void onUpdateMainMenu(UpdateMainMenuEvent event)
   {
      LinkedHashMap<String, MenuBarItem> menuBarItems = new LinkedHashMap<String, MenuBarItem>();

      for (AbstractCommand command : event.getCommands())
      {
         try
         {
            registerItem(menuBarItems, command.getId(), command);
         }
         catch (Exception exc)
         {
            exc.printStackTrace();
         }
      }

      display.buildMenu(menuBarItems);
   }

   private void registerItem(LinkedHashMap<String, MenuBarItem> menuBarItems, String workingId, AbstractCommand command)
   {
      if (workingId.indexOf("/") < 0)
      {
         // this is menu bar item
         MenuBarItem item = new MenuBarItem(workingId, command);
         menuBarItems.put(workingId, item);
      }
      else
      {
         /*
          * INIT MENU BAR
          */
         String menuBarTitle = workingId.substring(0, workingId.indexOf("/"));
         MenuBarItem menuBarItem = menuBarItems.get(menuBarTitle);
         if (menuBarItem == null)
         {
            menuBarItem = new MenuBarItem(menuBarTitle, null);
            menuBarItems.put(menuBarTitle, menuBarItem);
         }

         workingId = workingId.substring(workingId.indexOf("/") + 1);

         /*
          * INIT POPUP MENU ITEM
          */

         if (workingId.indexOf("/") >= 0)
         {
            String popup1Name = workingId.substring(0, workingId.indexOf("/"));
            workingId = workingId.substring(workingId.indexOf("/") + 1);

            PopupMenuItem popup1 = menuBarItem.getPopupItems().get(popup1Name);
            if (popup1 == null)
            {
               popup1 = new PopupMenuItem(popup1Name);
               menuBarItem.getPopupItems().put(popup1Name, popup1);
            }

            PopupMenuItem popup2 = new PopupMenuItem(workingId, command);
            popup1.getPopupItems().put(workingId, popup2);
         }
         else
         {
            PopupMenuItem popupItem = new PopupMenuItem(workingId, command);
            menuBarItem.getPopupItems().put(workingId, popupItem);
         }

      }
   }

   public void onGWTMenuItemSelected(GWTMenuItemSelectedEvent event)
   {
      display.closeMenu();

      if (event.getCommand() != null && event.getCommand().getEvent() != null)
      {
         eventBus.fireEvent(event.getCommand().getEvent());
      }
   }

}
