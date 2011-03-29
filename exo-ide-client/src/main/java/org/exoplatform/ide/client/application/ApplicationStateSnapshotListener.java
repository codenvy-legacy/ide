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
package org.exoplatform.ide.client.application;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.exoplatform.ide.client.framework.application.event.EntryPointChangedEvent;
import org.exoplatform.ide.client.framework.application.event.EntryPointChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorReplaceFileEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorReplaceFileHandler;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.settings.event.SaveApplicationSettingsEvent;
import org.exoplatform.ide.client.framework.settings.event.SaveApplicationSettingsEvent.SaveType;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.Folder;
import org.exoplatform.ide.client.framework.vfs.event.ItemDeletedEvent;
import org.exoplatform.ide.client.framework.vfs.event.ItemDeletedHandler;
import org.exoplatform.ide.client.framework.vfs.event.ItemLockResultReceivedEvent;
import org.exoplatform.ide.client.framework.vfs.event.ItemLockResultReceivedHandler;
import org.exoplatform.ide.client.framework.vfs.event.ItemUnlockedEvent;
import org.exoplatform.ide.client.framework.vfs.event.ItemUnlockedHandler;
import org.exoplatform.ide.client.framework.vfs.event.MoveCompleteEvent;
import org.exoplatform.ide.client.framework.vfs.event.MoveCompleteHandler;

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

   private Map<String, String> lockTokens;

   public ApplicationStateSnapshotListener(HandlerManager eventbus)
   {
      this.eventBus = eventbus;

      eventBus.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);

      eventBus.addHandler(EditorFileOpenedEvent.TYPE, this);
      eventBus.addHandler(EditorFileClosedEvent.TYPE, this);
      eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      eventBus.addHandler(EntryPointChangedEvent.TYPE, this);
      eventBus.addHandler(EditorReplaceFileEvent.TYPE, this);
      eventBus.addHandler(ItemLockResultReceivedEvent.TYPE, this);
      eventBus.addHandler(ItemUnlockedEvent.TYPE, this);
      eventBus.addHandler(ItemDeletedEvent.TYPE, this);
      eventBus.addHandler(MoveCompleteEvent.TYPE, this);
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
    * @see org.exoplatform.ide.client.framework.vfs.event.ItemUnlockedHandler#onItemUnlocked(org.exoplatform.ide.client.framework.vfs.event.ItemUnlockedEvent)
    */
   public void onItemUnlocked(ItemUnlockedEvent event)
   {
      lockTokens.remove(event.getItem().getHref());
      storeLockTokens();
   }

   /**
    * @see org.exoplatform.ide.client.framework.vfs.event.ItemLockResultReceivedHandler#onItemLockResultReceived(org.exoplatform.ide.client.framework.vfs.event.ItemLockResultReceivedEvent)
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
      eventBus.fireEvent(new SaveApplicationSettingsEvent(applicationSettings, SaveType.COOKIES));
   }

   /**
    * @see org.exoplatform.ide.client.framework.vfs.event.ItemDeletedHandler#onItemDeleted(org.exoplatform.ide.client.framework.vfs.event.ItemDeletedEvent)
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
    * @see org.exoplatform.ide.client.framework.vfs.event.MoveCompleteHandler#onMoveComplete(org.exoplatform.ide.client.framework.vfs.event.MoveCompleteEvent)
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
