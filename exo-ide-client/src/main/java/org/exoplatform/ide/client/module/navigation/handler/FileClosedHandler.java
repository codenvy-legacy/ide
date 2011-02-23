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

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.framework.vfs.VirtualFileSystem;
import org.exoplatform.ide.client.framework.vfs.event.ItemUnlockedEvent;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author <a href="tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Sep 13, 2010 $
 *
 */
public class FileClosedHandler implements EditorFileClosedHandler, ApplicationSettingsReceivedHandler
{

   private HandlerManager eventBus;

   private Map<String, String> lockTokens;

   public FileClosedHandler(HandlerManager eventBus)
   {
      this.eventBus = eventBus;

      eventBus.addHandler(EditorFileClosedEvent.TYPE, this);
      eventBus.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler#onEditorFileClosed(org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent)
    */
   public void onEditorFileClosed(EditorFileClosedEvent event)
   {
      String lockToken = lockTokens.get(event.getFile().getHref());
      if(event.getFile().isNewFile())
      {
         return;
      }
      
      if (lockToken != null)
      {
         VirtualFileSystem.getInstance().unlock(event.getFile(), lockToken, new AsyncRequestCallback<Item>()
         {
            
            @Override
            protected void onSuccess(Item result)
            {
               eventBus.fireEvent(new ItemUnlockedEvent(result));               
            }
            
            @Override
            protected void onFailure(Throwable exception)
            {
               eventBus.fireEvent(new ExceptionThrownEvent("Service is not deployed."));               
            }
         });
      }
   }

   /**
    * @see org.exoplatform.ide.client.model.settings.event.ApplicationSettingsReceivedHandler#onApplicationSettingsReceived(org.exoplatform.ide.client.model.settings.event.ApplicationSettingsReceivedEvent)
    */
   public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event)
   {
      if (event.getApplicationSettings().getValueAsMap("lock-tokens") == null)
      {
         event.getApplicationSettings().setValue("lock-tokens", new LinkedHashMap<String, String>(), Store.COOKIES);
      }

      lockTokens = event.getApplicationSettings().getValueAsMap("lock-tokens");
   }

}
