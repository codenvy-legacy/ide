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
package org.exoplatform.ide.client.permissions;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.framework.vfs.VirtualFileSystem;
import org.exoplatform.ide.client.framework.vfs.event.ItemACLReceivedEvent;
import org.exoplatform.ide.client.framework.vfs.event.ItemACLReceivedHandler;
import org.exoplatform.ide.client.module.navigation.event.selection.ItemsSelectedEvent;
import org.exoplatform.ide.client.module.navigation.event.selection.ItemsSelectedHandler;
import org.exoplatform.ide.client.permissions.event.ShowPermissionsEvent;
import org.exoplatform.ide.client.permissions.event.ShowPermissionsHandler;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Oct 19, 2010 $
 *
 */
public class ShowPermissionsCommandHandler implements ShowPermissionsHandler, ItemsSelectedHandler, ItemACLReceivedHandler, ExceptionThrownHandler
{

   private HandlerManager eventBus;

   private Handlers handlers;
   
   private Item selectedItem;
   
   /**
    * @param eventBus
    */
   public ShowPermissionsCommandHandler(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      handlers = new Handlers(eventBus);
      
      eventBus.addHandler(ShowPermissionsEvent.TYPE, this);
      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.permissions.event.ShowPermissionsHandler#onShowPermissions(org.exoplatform.ide.client.permissions.event.ShowPermissionsEvent)
    */
   public void onShowPermissions(ShowPermissionsEvent event)
   {
      if(selectedItem == null)
         return;
      
      handlers.addHandler(ItemACLReceivedEvent.TYPE, this);
      handlers.addHandler(ExceptionThrownEvent.TYPE, this);
      VirtualFileSystem.getInstance().getACL(selectedItem);
   }

   /**
    * @see org.exoplatform.ide.client.module.navigation.event.selection.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client.module.navigation.event.selection.ItemsSelectedEvent)
    */
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      if(event.getSelectedItems().size() == 1)
      {
         selectedItem = event.getSelectedItems().get(0);
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.vfs.event.ItemACLReceivedHandler#onItemACLReceived(org.exoplatform.ide.client.framework.vfs.event.ItemACLReceivedEvent)
    */
   public void onItemACLReceived(ItemACLReceivedEvent event)
   {
      handlers.removeHandlers();
      new PermissionsManagerForm(eventBus, event.getItem());
   }

   /**
    * @see org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler#onError(org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent)
    */
   public void onError(ExceptionThrownEvent event)
   {
      handlers.removeHandlers();
   }

}
