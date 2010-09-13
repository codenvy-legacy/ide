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

import java.util.Map;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.model.ApplicationContext;
import org.exoplatform.ide.client.module.vfs.api.LockToken;
import org.exoplatform.ide.client.module.vfs.api.VirtualFileSystem;
import org.exoplatform.ide.client.module.vfs.api.event.ItemUnlockedEvent;
import org.exoplatform.ide.client.module.vfs.api.event.ItemUnlockedHandler;

import com.google.gwt.event.shared.HandlerManager;

/**
 * @author <a href="tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Sep 13, 2010 $
 *
 */
public class FileClosedHandler implements EditorFileClosedHandler, ItemUnlockedHandler
{

   private HandlerManager eventBus;

   private Handlers handlers;
   
   private Map<String, LockToken> lockTokens; 

   public FileClosedHandler(HandlerManager eventBus, Map<String, LockToken> lockTokens)
   {
      this.eventBus = eventBus;
      this.lockTokens = lockTokens;

      handlers = new Handlers(eventBus);

      eventBus.addHandler(EditorFileClosedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler#onEditorFileClosed(org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent)
    */
   public void onEditorFileClosed(EditorFileClosedEvent event)
   {
      LockToken lockToken = lockTokens.get(event.getFile().getHref());

      if (lockToken != null)
      {
         handlers.addHandler(ItemUnlockedEvent.TYPE, this);
         VirtualFileSystem.getInstance().unlock(event.getFile(), lockToken);
      }
   }

   /**
    * @see org.exoplatform.ide.client.module.vfs.api.event.ItemUnlockedHandler#onItemUnlocked(org.exoplatform.ide.client.module.vfs.api.event.ItemUnlockedEvent)
    */
   public void onItemUnlocked(ItemUnlockedEvent event)
   {
      handlers.removeHandlers();
//      context.getLockTokens().remove(event.getItem().getHref());
   }

}
