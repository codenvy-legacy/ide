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
package org.exoplatform.ide.client.module.navigation.handler;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.ide.client.module.navigation.event.selection.ItemsSelectedEvent;
import org.exoplatform.ide.client.module.navigation.event.selection.ItemsSelectedHandler;
import org.exoplatform.ide.client.module.navigation.event.versioning.ViewItemVersionsEvent;
import org.exoplatform.ide.client.module.navigation.event.versioning.ViewItemVersionsHandler;
import org.exoplatform.ide.client.module.vfs.api.File;
import org.exoplatform.ide.client.module.vfs.api.Item;
import org.exoplatform.ide.client.module.vfs.api.VirtualFileSystem;
import org.exoplatform.ide.client.module.vfs.api.event.ItemDeletedEvent;
import org.exoplatform.ide.client.module.vfs.api.event.ItemDeletedHandler;
import org.exoplatform.ide.client.module.vfs.api.event.ItemVersionsReceivedEvent;
import org.exoplatform.ide.client.module.vfs.api.event.ItemVersionsReceivedHandler;
import org.exoplatform.ide.client.versioning.ViewVersionsForm;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Sep 27, 2010 $
 *
 */
public class ViewItemVersionsControlHandler implements ViewItemVersionsHandler, ItemsSelectedHandler,
   ItemDeletedHandler, ExceptionThrownHandler, ItemVersionsReceivedHandler
{
   private HandlerManager eventBus;

   private Handlers handlers;

   private Item selectedItem;

   public ViewItemVersionsControlHandler(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      handlers = new Handlers(eventBus);
      handlers.addHandler(ViewItemVersionsEvent.TYPE, this);
      handlers.addHandler(ItemsSelectedEvent.TYPE, this);
      handlers.addHandler(ItemDeletedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.module.navigation.event.versioning.ViewItemVersionsHandler#onViewItemVersions(org.exoplatform.ide.client.module.navigation.event.versioning.ViewItemVersionsEvent)
    */
   public void onViewItemVersions(ViewItemVersionsEvent event)
   {
      if (selectedItem != null && selectedItem instanceof File)
      {
         handlers.addHandler(ExceptionThrownEvent.TYPE, this);
         handlers.addHandler(ItemVersionsReceivedEvent.TYPE, this);
         VirtualFileSystem.getInstance().getVersions(selectedItem);
      }
      else
      {
         Dialogs.getInstance().showInfo("Please, select file in navigation tree.");
      }
   }

   /**
    * @see org.exoplatform.ide.client.module.navigation.event.selection.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client.module.navigation.event.selection.ItemsSelectedEvent)
    */
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      if (event.getSelectedItems().size() == 1)
      {
         selectedItem = event.getSelectedItems().get(0);
      }
   }

   /**
    * @see org.exoplatform.ide.client.module.vfs.api.event.ItemDeletedHandler#onItemDeleted(org.exoplatform.ide.client.module.vfs.api.event.ItemDeletedEvent)
    */
   public void onItemDeleted(ItemDeletedEvent event)
   {
      selectedItem = null;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler#onError(org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent)
    */
   public void onError(ExceptionThrownEvent event)
   {
      handlers.removeHandler(ExceptionThrownEvent.TYPE);
      handlers.removeHandler(ItemVersionsReceivedEvent.TYPE);
   }

   /**
    * @see org.exoplatform.ide.client.module.vfs.api.event.ItemVersionsReceivedHandler#onItemVersionsReceived(org.exoplatform.ide.client.module.vfs.api.event.ItemVersionsReceivedEvent)
    */
   public void onItemVersionsReceived(ItemVersionsReceivedEvent event)
   {
      handlers.removeHandler(ExceptionThrownEvent.TYPE);
      handlers.removeHandler(ItemVersionsReceivedEvent.TYPE);
      if (event.getVersions() != null && event.getVersions().size() > 0)
      {
         new ViewVersionsForm(eventBus, event.getItem(), event.getVersions());
      }
      else
      {
         Dialogs.getInstance().showInfo("Item \"" + event.getItem().getName() + "\" has no versions.");
      }

   }

}
