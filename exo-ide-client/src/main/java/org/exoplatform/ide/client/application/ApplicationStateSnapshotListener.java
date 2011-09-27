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

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
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
import org.exoplatform.ide.client.model.settings.SettingsService;
import org.exoplatform.ide.vfs.client.event.ItemDeletedEvent;
import org.exoplatform.ide.vfs.client.event.ItemDeletedHandler;
import org.exoplatform.ide.vfs.client.event.ItemLockedEvent;
import org.exoplatform.ide.vfs.client.event.ItemLockedHandler;
import org.exoplatform.ide.vfs.client.event.ItemMovedEvent;
import org.exoplatform.ide.vfs.client.event.ItemMovedHandler;
import org.exoplatform.ide.vfs.client.event.ItemUnlockedEvent;
import org.exoplatform.ide.vfs.client.event.ItemUnlockedHandler;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.shared.File;
import org.exoplatform.ide.vfs.shared.Link;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public class ApplicationStateSnapshotListener implements EditorFileOpenedHandler, EditorFileClosedHandler,
   EditorActiveFileChangedHandler, ApplicationSettingsReceivedHandler, VfsChangedHandler,
   EditorReplaceFileHandler, ItemLockedHandler, ItemUnlockedHandler, ItemDeletedHandler, ItemMovedHandler
{

   private HandlerManager eventBus;

   private Map<String, FileModel> openedFiles = new LinkedHashMap<String, FileModel>();

   private ApplicationSettings applicationSettings;

   private Map<String, String> lockTokens;

   public ApplicationStateSnapshotListener(HandlerManager eventbus)
   {
      this.eventBus = eventbus;

      eventBus.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);

      eventBus.addHandler(EditorFileOpenedEvent.TYPE, this);
      eventBus.addHandler(EditorFileClosedEvent.TYPE, this);
      eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      eventBus.addHandler(VfsChangedEvent.TYPE, this);
      eventBus.addHandler(EditorReplaceFileEvent.TYPE, this);
      eventBus.addHandler(ItemLockedEvent.TYPE, this);
      eventBus.addHandler(ItemUnlockedEvent.TYPE, this);
      eventBus.addHandler(ItemDeletedEvent.TYPE, this);
      eventBus.addHandler(ItemMovedEvent.TYPE, this);
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

         FileModel file = openedFiles.get(fileName);
         if (!file.isPersisted())
         {
            continue;
         }

         files.add(file.getLinkByRelation(Link.REL_SELF).getHref());
      }

      applicationSettings.setValue("opened-files", files, Store.COOKIES);
      SettingsService.getInstance().saveSettingsToCookies(applicationSettings);
   }

   private void storeActiveFile(FileModel file)
   {
      String activeFile = "";
      if (null != file)
      {
         activeFile = file.getPath();
      }

      applicationSettings.setValue("active-file", activeFile, Store.COOKIES);
      SettingsService.getInstance().saveSettingsToCookies(applicationSettings);
   }

   /**
    * @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework.application.event.VfsChangedEvent)
    */
   public void onVfsChanged(VfsChangedEvent event)
   {
      String workspace = (event.getVfsInfo() != null) ? event.getVfsInfo().getId() : null;
      applicationSettings.setValue("entry-point", workspace, Store.COOKIES);
      Map<String, Link> map = event.getVfsInfo().getRoot().getLinks();
      Link root = event.getVfsInfo().getRoot().getLinks().get(Link.REL_SELF);
      String s = root.getHref();
      SettingsService.getInstance().saveSettingsToCookies(applicationSettings);
   }

   /**
    * @see org.exoplatform.ide.client.editor.event.EditorReplaceFileHandler#onEditorReplaceFile(org.exoplatform.ide.client.editor.event.EditorReplaceFileEvent)
    */
   public void onEditorReplaceFile(EditorReplaceFileEvent event)
   {
      storeOpenedFiles();
   }

   /**
    * Store Lock Tokens 
    */
   private void storeLockTokens()
   {
      applicationSettings.setValue("lock-tokens", lockTokens, Store.COOKIES);
      SettingsService.getInstance().saveSettingsToCookies(applicationSettings);
   }

   /**
    * @see org.exoplatform.ide.vfs.client.event.ItemMovedHandler#onItemMoved(org.exoplatform.ide.vfs.client.event.ItemMovedEvent)
    */
   @Override
   public void onItemMoved(ItemMovedEvent event)
   {
      //TODO
//      if (lockTokens.containsKey(event.getSourceHref()))
//      {
//         String lock = lockTokens.get(event.getSourceHref());
//         lockTokens.remove(event.getSourceHref());
//         lockTokens.put(event.getItem().getHref(), lock);
//         storeLockTokens();
//      }
//      else if (event.getItem() instanceof Folder)
//      {
//         String sourceHref = event.getSourceHref();
//         List<String> keys = new ArrayList<String>();
//         for (String k : lockTokens.keySet())
//         {
//            keys.add(k);
//         }
//
//         for (String key : keys)
//         {
//            if (key.startsWith(sourceHref))
//            {
//               String lock = lockTokens.get(key);
//               String name = key.substring(sourceHref.length());
//               String path = event.getItem().getHref();
//               if (!path.endsWith("/"))
//               {
//                  path += "/";
//               }
//               lockTokens.remove(key);
//               lockTokens.put(path + name, lock);
//               storeLockTokens();
//            }
//         }
//      }
   }

   /**
    * @see org.exoplatform.ide.vfs.client.event.ItemDeletedHandler#onItemDeleted(org.exoplatform.ide.vfs.client.event.ItemDeletedEvent)
    */
   @Override
   public void onItemDeleted(ItemDeletedEvent event)
   {
      if (event.getItem() instanceof File)
      {
         if (lockTokens.containsKey(event.getItem().getId()))
         {
            lockTokens.remove(event.getItem().getId());
            storeLockTokens();
         }
      }
   }

   /**
    * @see org.exoplatform.ide.vfs.client.event.ItemUnlockedHandler#onItemUnlocked(org.exoplatform.ide.vfs.client.event.ItemUnlockedEvent)
    */
   @Override
   public void onItemUnlocked(ItemUnlockedEvent event)
   {
      lockTokens.remove(event.getItem().getId());
      storeLockTokens();

   }

   /**
    * @see org.exoplatform.ide.vfs.client.event.ItemLockedHandler#onItemLocked(org.exoplatform.ide.vfs.client.event.ItemLockedEvent)
    */
   @Override
   public void onItemLocked(ItemLockedEvent event)
   {
      lockTokens.put(event.getItem().getId(), event.getLockToken().getLockToken());
      storeLockTokens();
   }

   //   /**
   //    * @see org.exoplatform.ide.client.framework.vfs.event.ItemDeletedHandler#onItemDeleted(org.exoplatform.ide.client.framework.vfs.event.ItemDeletedEvent)
   //    */
   //   public void onItemDeleted(ItemDeletedEvent event)
   //   {

   //   }

   //   /**
   //    * @see org.exoplatform.ide.client.framework.vfs.event.MoveCompleteHandler#onMoveComplete(org.exoplatform.ide.client.framework.vfs.event.MoveCompleteEvent)
   //    */
   //   public void onMoveComplete(MoveCompleteEvent event)
   //   {
   //      if (lockTokens.containsKey(event.getSourceHref()))
   //      {
   //         String lock = lockTokens.get(event.getSourceHref());
   //         lockTokens.remove(event.getSourceHref());
   //         lockTokens.put(event.getItem().getHref(), lock);
   //         storeLockTokens();
   //      }
   //      else if (event.getItem() instanceof Folder)
   //      {
   //         String sourceHref = event.getSourceHref();
   //         List<String> keys = new ArrayList<String>();
   //         for (String k : lockTokens.keySet())
   //         {
   //            keys.add(k);
   //         }
   //
   //         for (String key : keys)
   //         {
   //            if (key.startsWith(sourceHref))
   //            {
   //               String lock = lockTokens.get(key);
   //               String name = key.substring(sourceHref.length());
   //               String path = event.getItem().getHref();
   //               if (!path.endsWith("/"))
   //               {
   //                  path += "/";
   //               }
   //               lockTokens.remove(key);
   //               lockTokens.put(path + name, lock);
   //               storeLockTokens();
   //            }
   //         }
   //      }
   //   }

}
