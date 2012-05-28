/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.client.framework.contextmenu;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;

import org.exoplatform.gwtframework.ui.client.command.Control;
import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.gwtframework.ui.client.command.ui.MenuItemControl;
import org.exoplatform.gwtframework.ui.client.menu.CloseMenuHandler;
import org.exoplatform.gwtframework.ui.client.menu.ItemSelectedHandler;
import org.exoplatform.gwtframework.ui.client.menu.MenuItem;
import org.exoplatform.gwtframework.ui.client.menu.MenuLockLayer;
import org.exoplatform.gwtframework.ui.client.menu.PopupMenu;
import org.exoplatform.gwtframework.ui.client.menu.PopupMenuItem;
import org.exoplatform.ide.client.framework.module.IDE;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom context menu, which consists of {@link Control} list.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Apr 23, 2012 11:45:52 AM anya $
 * 
 */
public class ContextMenu implements ItemSelectedHandler, CloseMenuHandler
{
   private final String ID = "eXoIDEContextMenu";

   /**
    * Instance of context menu.
    */
   private static ContextMenu contextMenu;

   /**
    * Lock layer, when context menu is shown.
    */
   private MenuLockLayer lockLayer;

   /**
    * Context menu.
    */
   private PopupMenu popupMenu;

   private CloseMenuHandler closeHandler;

   private ContextMenu()
   {
   }

   /**
    * @return {@link ContextMenu}
    */
   public static ContextMenu get()
   {
      if (contextMenu == null)
      {
         contextMenu = new ContextMenu();
      }
      return contextMenu;
   }

   /**
    * Show context menu.
    * 
    * @param commands list of commands to show in context menu
    * @param x context menu left coordinate
    * @param y context menu top coordinate
    */
   @SuppressWarnings("rawtypes")
   public void show(List<Control> commands, int x, int y, CloseMenuHandler closeHandler)
   {
      this.closeHandler = closeHandler;
      lockLayer = new MenuLockLayer(this);

      List<MenuItem> contextMenuItems = new ArrayList<MenuItem>();
      for (Control control : commands)
      {
         if (control instanceof SimpleControl && ((SimpleControl)control).isShowInContextMenu())
         {
            PopupMenuItem popupMenuItem = new PopupMenuItem(control.getIcon(), ((SimpleControl)control).getTitle());
            if (control.hasDelimiterBefore())
            {
               contextMenuItems.add(new PopupMenuItem(null));
            }
            contextMenuItems.add(popupMenuItem);
            new MenuItemControl(IDE.eventBus(), popupMenuItem, (SimpleControl)control);
         }
      }

      popupMenu = new PopupMenu(contextMenuItems, lockLayer, this);
      popupMenu.getElement().setId(ID);
      popupMenu.addDomHandler(new ContextMenuHandler()
      {
         
         @Override
         public void onContextMenu(ContextMenuEvent event)
         {
            event.stopPropagation();
            event.preventDefault();
         }
      }, ContextMenuEvent.getType());
      lockLayer.add(popupMenu);
      
      popupMenu.getElement().getStyle().setTop(y, Unit.PX);
      popupMenu.getElement().getStyle().setLeft(x, Unit.PX);
   }

   /**
    * @see org.exoplatform.gwtframework.ui.client.menu.ItemSelectedHandler#onMenuItemSelected(org.exoplatform.gwtframework.ui.client.menu.MenuItem)
    */
   @Override
   public void onMenuItemSelected(MenuItem menuItem)
   {
      closePopupMenu();
   }

   /**
    * @see org.exoplatform.gwtframework.ui.client.menu.CloseMenuHandler#onCloseMenu()
    */
   @Override
   public void onCloseMenu()
   {
      closePopupMenu();
   }

   /**
    * Close popup menu.
    */
   protected void closePopupMenu()
   {
      if (popupMenu != null)
      {
         popupMenu.removeFromParent();
         popupMenu = null;
      }

      if (lockLayer != null)
      {
         lockLayer.removeFromParent();
         lockLayer = null;
      }
      if (closeHandler != null)
      {
         closeHandler.onCloseMenu();
         closeHandler = null;
      }
   }
}
