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
package org.exoplatform.ide.client.versioning.handler;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.commons.dialogs.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.event.OpenFileEvent;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.FileContentSaveCallback;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.framework.vfs.ItemPropertiesCallback;
import org.exoplatform.ide.client.framework.vfs.Version;
import org.exoplatform.ide.client.framework.vfs.VirtualFileSystem;
import org.exoplatform.ide.client.versioning.event.RestoreToVersionEvent;
import org.exoplatform.ide.client.versioning.event.RestoreToVersionHandler;
import org.exoplatform.ide.client.versioning.event.ShowVersionContentEvent;
import org.exoplatform.ide.client.versioning.event.ShowVersionContentHandler;
import org.exoplatform.ide.client.versioning.event.VersionRestoredEvent;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Sep 30, 2010 $
 *
 */
public class RestoreToVersionCommandHandler implements ShowVersionContentHandler, RestoreToVersionHandler,
   ApplicationSettingsReceivedHandler
{
   
   private static final String ASK_DIALOG_TITLE = IDE.VERSIONS_CONSTANT.restoreToVersionDialogTitle();
   
   private HandlerManager eventBus;

   private Version activeVersion;

   private Map<String, String> lockTokens;

   public RestoreToVersionCommandHandler(HandlerManager eventBus)
   {
      this.eventBus = eventBus;

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
    * @see org.exoplatform.ide.client.versioning.event.RestoreToVersionHandler#onRestoreToVersion(org.exoplatform.ide.client.versioning.event.RestoreToVersionEvent)
    */
   public void onRestoreToVersion(RestoreToVersionEvent event)
   {
      if (activeVersion == null)
      {
         return;
      }

      Dialogs.getInstance().ask(ASK_DIALOG_TITLE,
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
      VirtualFileSystem.getInstance().getProperties(file, null, new ItemPropertiesCallback()
      {

         @Override
         protected void onSuccess(Item result)
         {
            if (result != null && result instanceof File && activeVersion != null)
            {
               File file = (File)result;
               file.setContent(activeVersion.getContent());
               saveFileContent(file);
            }
         }
      });
   }

   private void saveFileContent(final File file)
   {
      VirtualFileSystem.getInstance().saveContent(file, lockTokens.get(file.getHref()), new FileContentSaveCallback()
      {
         @Override
         protected void onSuccess(FileData result)
         {
            eventBus.fireEvent(new VersionRestoredEvent(activeVersion, result.getFile()));
            eventBus.fireEvent(new OpenFileEvent(result.getFile()));
         }
      });
   }

}
