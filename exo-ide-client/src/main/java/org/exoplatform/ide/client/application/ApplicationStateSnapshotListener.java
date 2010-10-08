/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
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
package org.exoplatform.ide.client.application;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.ide.client.editor.event.EditorReplaceFileEvent;
import org.exoplatform.ide.client.editor.event.EditorReplaceFileHandler;
import org.exoplatform.ide.client.framework.application.event.EntryPointChangedEvent;
import org.exoplatform.ide.client.framework.application.event.EntryPointChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.settings.event.SaveApplicationSettingsEvent;
import org.exoplatform.ide.client.framework.settings.event.SaveApplicationSettingsEvent.SaveType;
import org.exoplatform.ide.client.module.vfs.api.File;
import org.exoplatform.ide.client.module.vfs.api.Folder;
import org.exoplatform.ide.client.module.vfs.api.event.ItemDeletedEvent;
import org.exoplatform.ide.client.module.vfs.api.event.ItemDeletedHandler;
import org.exoplatform.ide.client.module.vfs.api.event.ItemLockResultReceivedEvent;
import org.exoplatform.ide.client.module.vfs.api.event.ItemLockResultReceivedHandler;
import org.exoplatform.ide.client.module.vfs.api.event.ItemUnlockedEvent;
import org.exoplatform.ide.client.module.vfs.api.event.ItemUnlockedHandler;
import org.exoplatform.ide.client.module.vfs.api.event.MoveCompleteEvent;
import org.exoplatform.ide.client.module.vfs.api.event.MoveCompleteHandler;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public class ApplicationStateSnapshotListener implements EditorFileOpenedHandler, EditorFileClosedHandler,
   EditorActiveFileChangedHandler, ApplicationSettingsReceivedHandler, EntryPointChangedHandler,
   EditorReplaceFileHandler, ItemLockResultReceivedHandler, ItemUnlockedHandler, ItemDeletedHandler,
   MoveCompleteHandler
{

   private HandlerManager eventBus;

   private Map<String, File> openedFiles = new LinkedHashMap<String, File>();

   private ApplicationSettings applicationSettings;

   private Handlers handlers;

   private Map<String, String> lockTokens;

   public ApplicationStateSnapshotListener(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      handlers = new Handlers(eventBus);

      handlers.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);

      handlers.addHandler(EditorFileOpenedEvent.TYPE, this);
      handlers.addHandler(EditorFileClosedEvent.TYPE, this);
      handlers.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      handlers.addHandler(EntryPointChangedEvent.TYPE, this);
      handlers.addHandler(EditorReplaceFileEvent.TYPE, this);
      handlers.addHandler(ItemLockResultReceivedEvent.TYPE, this);
      handlers.addHandler(ItemUnlockedEvent.TYPE, this);
      handlers.addHandler(ItemDeletedEvent.TYPE, this);
      handlers.addHandler(MoveCompleteEvent.TYPE, this);
   }

   public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event)
   {
      applicationSettings = event.getApplicationSettings();
      if (applicationSettings.getValueAsMap("lock-tokens") == null)
      {
         applicationSettings.setValue("lock-tokens", new LinkedHashMap<String, String>(), Store.COOKIES);
      }
      lockTokens = applicationSettings.getValueAsMap("lock-tokens");
   }

   public void onEditorFileOpened(EditorFileOpenedEvent event)
   {
      openedFiles = event.getOpenedFiles();
      storeOpenedFiles();
   }

   public void onEditorFileClosed(EditorFileClosedEvent event)
   {
      openedFiles = event.getOpenedFiles();
      storeOpenedFiles();
   }

   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      storeOpenedFiles();
      storeActiveFile(event.getFile());
   }

   private void storeOpenedFiles()
   {
      List<String> files = new ArrayList<String>();

      Iterator<String> openedFilesIter = openedFiles.keySet().iterator();
      while (openedFilesIter.hasNext())
      {
         String fileName = openedFilesIter.next();

         File file = openedFiles.get(fileName);
         if (file.isNewFile())
         {
            continue;
         }

         files.add(fileName);
      }

      applicationSettings.setValue("opened-files", files, Store.COOKIES);
      eventBus.fireEvent(new SaveApplicationSettingsEvent(applicationSettings, SaveType.COOKIES));
   }

   private void storeActiveFile(File file)
   {
      String activeFile = "";
      if (file != null)
      {
         activeFile = file.getHref();
      }

      applicationSettings.setValue("active-file", activeFile, Store.COOKIES);
      eventBus.fireEvent(new SaveApplicationSettingsEvent(applicationSettings, SaveType.COOKIES));
   }

   public void onEntryPointChanged(EntryPointChangedEvent event)
   {
      applicationSettings.setValue("entry-point", event.getEntryPoint(), Store.COOKIES);
      eventBus.fireEvent(new SaveApplicationSettingsEvent(applicationSettings, SaveType.COOKIES));
   }

   /**
    * @see org.exoplatform.ide.client.editor.event.EditorReplaceFileHandler#onEditorReplaceFile(org.exoplatform.ide.client.editor.event.EditorReplaceFileEvent)
    */
   public void onEditorReplaceFile(EditorReplaceFileEvent event)
   {
      storeOpenedFiles();
   }

   /**
    * @see org.exoplatform.ide.client.module.vfs.api.event.ItemUnlockedHandler#onItemUnlocked(org.exoplatform.ide.client.module.vfs.api.event.ItemUnlockedEvent)
    */
   public void onItemUnlocked(ItemUnlockedEvent event)
   {
      lockTokens.remove(event.getItem().getHref());
      storeLockTokens();
   }

   /**
    * @see org.exoplatform.ide.client.module.vfs.api.event.ItemLockResultReceivedHandler#onItemLockResultReceived(org.exoplatform.ide.client.module.vfs.api.event.ItemLockResultReceivedEvent)
    */
   public void onItemLockResultReceived(ItemLockResultReceivedEvent event)
   {
      if (event.getException() == null)
      {
         lockTokens.put(event.getItem().getHref(), event.getLockToken().getLockToken());
         storeLockTokens();
      }
   }

   /**
    * Store Lock Tokens 
    */
   private void storeLockTokens()
   {
      applicationSettings.setValue("lock-tokens", lockTokens, Store.COOKIES);
   }

   /**
    * @see org.exoplatform.ide.client.module.vfs.api.event.ItemDeletedHandler#onItemDeleted(org.exoplatform.ide.client.module.vfs.api.event.ItemDeletedEvent)
    */
   public void onItemDeleted(ItemDeletedEvent event)
   {
      if (event.getItem() instanceof File)
      {
         lockTokens.remove(event.getItem().getHref());
         storeLockTokens();
      }
   }

   /**
    * @see org.exoplatform.ide.client.module.vfs.api.event.MoveCompleteHandler#onMoveComplete(org.exoplatform.ide.client.module.vfs.api.event.MoveCompleteEvent)
    */
   public void onMoveComplete(MoveCompleteEvent event)
   {
      if (lockTokens.containsKey(event.getSourceHref()))
      {
         String lock = lockTokens.get(event.getSourceHref());
         lockTokens.remove(event.getSourceHref());
         lockTokens.put(event.getItem().getHref(), lock);
         storeLockTokens();
      }
      else if (event.getItem() instanceof Folder)
      {
         String sourceHref = event.getSourceHref();
         List<String> keys = new ArrayList<String>();
         for (String k : lockTokens.keySet())
         {
            keys.add(k);
         }

         for (String key : keys)
         {
            if (key.startsWith(sourceHref))
            {
               String lock = lockTokens.get(key);
               String name = key.substring(sourceHref.length());
               String path = event.getItem().getHref();
               if (!path.endsWith("/"))
               {
                  path += "/";
               }
               lockTokens.remove(key);
               lockTokens.put(path + name, lock);
               storeLockTokens();
            }
         }
      }
   }

}
