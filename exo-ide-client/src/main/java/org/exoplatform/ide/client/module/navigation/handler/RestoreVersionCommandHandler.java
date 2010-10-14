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
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.ide.client.framework.event.OpenFileEvent;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.module.navigation.event.versioning.RestoreVersionEvent;
import org.exoplatform.ide.client.module.navigation.event.versioning.RestoreVersionHandler;
import org.exoplatform.ide.client.framework.module.vfs.api.File;
import org.exoplatform.ide.client.framework.module.vfs.api.Version;
import org.exoplatform.ide.client.framework.module.vfs.api.VirtualFileSystem;
import org.exoplatform.ide.client.framework.module.vfs.api.event.FileContentSavedEvent;
import org.exoplatform.ide.client.framework.module.vfs.api.event.FileContentSavedHandler;
import org.exoplatform.ide.client.framework.module.vfs.api.event.ItemPropertiesReceivedEvent;
import org.exoplatform.ide.client.framework.module.vfs.api.event.ItemPropertiesReceivedHandler;
import org.exoplatform.ide.client.versioning.event.ShowVersionEvent;
import org.exoplatform.ide.client.versioning.event.ShowVersionHandler;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Sep 30, 2010 $
 *
 */
public class RestoreVersionCommandHandler implements ShowVersionHandler, RestoreVersionHandler,
   ItemPropertiesReceivedHandler, ApplicationSettingsReceivedHandler, ExceptionThrownHandler, FileContentSavedHandler
{
   private HandlerManager eventBus;

   private Handlers handlers;

   private Version activeVersion;

   private Map<String, String> lockTokens;
   
   private Version restoreVersion;

   public RestoreVersionCommandHandler(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      handlers = new Handlers(eventBus);
      handlers.addHandler(ShowVersionEvent.TYPE, this);
      handlers.addHandler(RestoreVersionEvent.TYPE, this);
      handlers.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.module.navigation.event.versioning.RestoreVersionHandler#onRestoreToVersion(org.exoplatform.ide.client.module.navigation.event.versioning.RestoreVersionEvent)
    */
   public void onRestoreToVersion(RestoreVersionEvent event)
   {
      if (activeVersion == null)
         return;
      restoreVersion = activeVersion;
      File file = new File(restoreVersion.getItemHref());
      handlers.addHandler(ItemPropertiesReceivedEvent.TYPE, this);
      VirtualFileSystem.getInstance().getProperties(file);
   }

   /**
    * @see org.exoplatform.ide.client.framework.module.vfs.api.event.ItemPropertiesReceivedHandler#onItemPropertiesReceived(org.exoplatform.ide.client.framework.module.vfs.api.event.ItemPropertiesReceivedEvent)
    */
   public void onItemPropertiesReceived(ItemPropertiesReceivedEvent event)
   {
      handlers.removeHandler(ItemPropertiesReceivedEvent.TYPE);
      if (event.getItem() != null && event.getItem() instanceof File && restoreVersion != null)
      {
         File file = (File)event.getItem();
         file.setContent(restoreVersion.getContent());
         handlers.addHandler(FileContentSavedEvent.TYPE, this);
         VirtualFileSystem.getInstance().saveContent(file, lockTokens.get(file.getHref()));
      }
   }

   public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event)
   {
      if (event.getApplicationSettings().getValueAsMap("lock-tokens") == null)
      {
         event.getApplicationSettings().setValue("lock-tokens", new LinkedHashMap<String, String>(), Store.COOKIES);
      }

      lockTokens = event.getApplicationSettings().getValueAsMap("lock-tokens");
   }

   /**
    * @see org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler#onError(org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent)
    */
   public void onError(ExceptionThrownEvent event)
   {
      handlers.removeHandler(ExceptionThrownEvent.TYPE);
      handlers.removeHandler(ItemPropertiesReceivedEvent.TYPE);
      handlers.removeHandler(FileContentSavedEvent.TYPE);
   }

   /**
    * @see org.exoplatform.ide.client.framework.module.vfs.api.event.FileContentSavedHandler#onFileContentSaved(org.exoplatform.ide.client.framework.module.vfs.api.event.FileContentSavedEvent)
    */
   public void onFileContentSaved(FileContentSavedEvent event)
   {
      handlers.removeHandler(FileContentSavedEvent.TYPE);
      eventBus.fireEvent(new OpenFileEvent(event.getFile()));
   }

   /**
    * @see org.exoplatform.ide.client.versioning.event.ShowVersionHandler#onShowVersion(org.exoplatform.ide.client.versioning.event.ShowVersionEvent)
    */
   public void onShowVersion(ShowVersionEvent event)
   {
      activeVersion = event.getVersion();
   }
}
