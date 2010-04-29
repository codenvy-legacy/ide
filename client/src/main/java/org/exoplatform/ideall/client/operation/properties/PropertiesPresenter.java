/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ideall.client.operation.properties;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.model.vfs.api.File;
import org.exoplatform.ideall.client.model.vfs.api.Item;
import org.exoplatform.ideall.client.model.vfs.api.event.ItemPropertiesReceivedEvent;
import org.exoplatform.ideall.client.model.vfs.api.event.ItemPropertiesReceivedHandler;
import org.exoplatform.ideall.client.model.vfs.api.event.ItemPropertiesSavedEvent;
import org.exoplatform.ideall.client.model.vfs.api.event.ItemPropertiesSavedHandler;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class PropertiesPresenter implements ItemPropertiesSavedHandler, ItemPropertiesReceivedHandler
{

   public interface Display
   {

      void refreshProperties(File file);

   }

   private HandlerManager eventBus;

   private ApplicationContext context;

   private Display display;

   private Handlers handlers;

   public PropertiesPresenter(HandlerManager eventBus, ApplicationContext context)
   {
      this.eventBus = eventBus;
      this.context = context;
      handlers = new Handlers(eventBus);
   }

   public void bindDisplay(Display d)
   {
      display = d;
      handlers.addHandler(ItemPropertiesSavedEvent.TYPE, this);
      handlers.addHandler(ItemPropertiesReceivedEvent.TYPE, this);
   }

   public void destroy()
   {
      handlers.removeHandlers();
   }

   private void refreshProperties(Item item)
   {
      if (!(item instanceof File))
      {
         return;
      }

      if (context.getActiveFile() == null)
      {
         return;
      }

      File file = (File)item;

      if (context.getActiveFile().getHref().equals(file.getHref()))
      {
         display.refreshProperties(file);
      }
   }

   public void onItemPropertiesSaved(ItemPropertiesSavedEvent event)
   {
      refreshProperties(event.getItem());
   }

   public void onItemPropertiesReceived(ItemPropertiesReceivedEvent event)
   {
      refreshProperties(event.getItem());
   }

}
