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

import java.util.LinkedHashMap;
import java.util.Map;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.dialogs.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.ide.client.framework.event.OpenFileEvent;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.Version;
import org.exoplatform.ide.client.framework.vfs.VirtualFileSystem;
import org.exoplatform.ide.client.framework.vfs.event.FileContentSavedEvent;
import org.exoplatform.ide.client.framework.vfs.event.FileContentSavedHandler;
import org.exoplatform.ide.client.framework.vfs.event.ItemPropertiesReceivedEvent;
import org.exoplatform.ide.client.framework.vfs.event.ItemPropertiesReceivedHandler;
import org.exoplatform.ide.client.module.navigation.event.versioning.RestoreToVersionEvent;
import org.exoplatform.ide.client.module.navigation.event.versioning.RestoreToVersionHandler;
import org.exoplatform.ide.client.versioning.event.ShowVersionContentEvent;
import org.exoplatform.ide.client.versioning.event.ShowVersionContentHandler;

import com.google.gwt.event.shared.HandlerManager;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Sep 30, 2010 $
 *
 */
public class RestoreToVersionCommandHandler implements ShowVersionContentHandler, RestoreToVersionHandler,
   ItemPropertiesReceivedHandler, ApplicationSettingsReceivedHandler, ExceptionThrownHandler, FileContentSavedHandler
{
   private HandlerManager eventBus;

   private Handlers handlers;

   private Version activeVersion;

   private Map<String, String> lockTokens;

   public RestoreToVersionCommandHandler(HandlerManager eventBus)
   {
      this.eventBus = eventBus;

      handlers = new Handlers(eventBus);

      eventBus.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
      eventBus.addHandler(ShowVersionContentEvent.TYPE, this);
      eventBus.addHandler(RestoreToVersionEvent.TYPE, this);
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
    * @see org.exoplatform.ide.client.versioning.event.ShowVersionContentHandler#onShowVersionContent(org.exoplatform.ide.client.versioning.event.ShowVersionContentEvent)
    */
   public void onShowVersionContent(ShowVersionContentEvent event)
   {
      activeVersion = event.getVersion();
   }

   /**
    * @see org.exoplatform.ide.client.module.navigation.event.versioning.RestoreToVersionHandler#onRestoreToVersion(org.exoplatform.ide.client.module.navigation.event.versioning.RestoreToVersionEvent)
    */
   public void onRestoreToVersion(RestoreToVersionEvent event)
   {
      if (activeVersion == null)
      {
         return;
      }

      Dialogs.getInstance().ask("Restore version",
         "Do you want to restore file to version " + activeVersion.getDisplayName() + "?",
         new BooleanValueReceivedHandler()
         {
            public void booleanValueReceived(Boolean value)
            {
               if (value != null && value)
               {
                  restoreToVersion();
               }
            }
         });
   }
   
   private void restoreToVersion()
   {
      File file = new File(activeVersion.getItemHref());
      handlers.addHandler(ItemPropertiesReceivedEvent.TYPE, this);
      handlers.addHandler(ExceptionThrownEvent.TYPE, this);
      VirtualFileSystem.getInstance().getProperties(file, null);
   }

   /**
    * @see org.exoplatform.ide.client.framework.vfs.event.ItemPropertiesReceivedHandler#onItemPropertiesReceived(org.exoplatform.ide.client.framework.vfs.event.ItemPropertiesReceivedEvent)
    */
   public void onItemPropertiesReceived(ItemPropertiesReceivedEvent event)
   {
      handlers.removeHandler(ItemPropertiesReceivedEvent.TYPE);
      if (event.getItem() != null && event.getItem() instanceof File && activeVersion != null)
      {
         File file = (File)event.getItem();
         file.setContent(activeVersion.getContent());
         handlers.addHandler(FileContentSavedEvent.TYPE, this);
         VirtualFileSystem.getInstance().saveContent(file, lockTokens.get(file.getHref()));
      }
   }

   /**
    * @see org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler#onError(org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent)
    */
   public void onError(ExceptionThrownEvent event)
   {
      handlers.removeHandlers();
   }

   /**
    * @see org.exoplatform.ide.client.framework.vfs.event.FileContentSavedHandler#onFileContentSaved(org.exoplatform.ide.client.framework.vfs.event.FileContentSavedEvent)
    */
   public void onFileContentSaved(FileContentSavedEvent event)
   {
      handlers.removeHandlers();
      eventBus.fireEvent(new OpenFileEvent(event.getFile()));
   }

}
