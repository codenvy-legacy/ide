/*
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
 */
package org.exoplatform.ide.client.statusbar;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.ide.client.framework.application.event.EntryPointChangedEvent;
import org.exoplatform.ide.client.framework.application.event.EntryPointChangedHandler;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.Item;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;

public class StatusBarPresenter implements ItemsSelectedHandler, EntryPointChangedHandler
{

   interface Display
   {

      HasValue<String> getPathInfoField();

   }

   private Display display;

   private Handlers handlers;

   private String entryPoint;

   public StatusBarPresenter(HandlerManager eventBus)
   {
      handlers = new Handlers(eventBus);

      handlers.addHandler(ItemsSelectedEvent.TYPE, this);
      handlers.addHandler(EntryPointChangedEvent.TYPE, this);
   }

   public void destroy()
   {
      handlers.removeHandlers();
   }

   void bindDisplay(final Display d)
   {
      display = d;
   }

   public void onItemsSelected(ItemsSelectedEvent event)
   {
      String statusMessage = null;

      if (entryPoint == null)
      {
         statusMessage = "No entry point selected!";
      }
      else if (event.getSelectedItems().size() == 1)
      {
         Item item = event.getSelectedItems().get(0);
         statusMessage = item.getHref();
         if (item instanceof File)
         {
            statusMessage = statusMessage.substring(0, statusMessage.lastIndexOf("/"));
         }

         String prefix = entryPoint;
         if (prefix.endsWith("/"))
         {
            prefix = prefix.substring(0, prefix.length() - 1);
         }

         prefix = prefix.substring(0, prefix.lastIndexOf("/"));
         statusMessage = statusMessage.substring(prefix.length());
         if (statusMessage.endsWith("/"))
         {
            statusMessage = statusMessage.substring(0, statusMessage.length() - 1);
         }
      }
      else if (event.getSelectedItems().size() == 0)
      {
         statusMessage = "No items selected!";
      }
      else
      {
         statusMessage = "Selected: <b>" + event.getSelectedItems().size() + "</b> items";
      }

      display.getPathInfoField().setValue(statusMessage);
   }

   public void onEntryPointChanged(EntryPointChangedEvent event)
   {
      entryPoint = event.getEntryPoint();
   }

}
