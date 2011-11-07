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
package org.exoplatform.ide.client.edit.control;

import java.util.LinkedHashMap;
import java.util.Map;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.edit.event.LockFileEvent;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.vfs.client.event.ItemLockedEvent;
import org.exoplatform.ide.vfs.client.event.ItemLockedHandler;
import org.exoplatform.ide.vfs.client.event.ItemUnlockedEvent;
import org.exoplatform.ide.vfs.client.event.ItemUnlockedHandler;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.shared.File;
import org.exoplatform.ide.vfs.shared.Item;

/**
 * Control for manual lock or unlock file.
 * 
 * Changes selection status after pressure.
 * 
 * If active file changes, check is file is locked and 
 * also changes selection status.
 * 
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
@RolesAllowed({"administrators", "developers"})
public class LockUnlockFileControl extends SimpleControl implements IDEControl, EditorActiveFileChangedHandler,
   ApplicationSettingsReceivedHandler, ItemUnlockedHandler, ItemLockedHandler
{

   //Edit/Show \\ Hide Line Numbers
   public static final String ID = "Edit/Lock \\ Unlock File";

   public static final String TITLE_LOCK = IDE.IDE_LOCALIZATION_CONSTANT.lockFileLockControl();

   public static final String TITLE_UNLOCK = IDE.IDE_LOCALIZATION_CONSTANT.lockFileUnlockControl();

   private boolean fileLocked = false;

   private Map<String, String> lockTokens;

   private FileModel activeFile;

   /**
    * 
    */
   public LockUnlockFileControl()
   {
      super(ID);
      setTitle(TITLE_LOCK);
      setImages(IDEImageBundle.INSTANCE.lockUnlockFile(), IDEImageBundle.INSTANCE.lockUnlockFileDisabled());
      setEvent(new LockFileEvent(true));
      setEnabled(true);
      setDelimiterBefore(true);
      setCanBeSelected(true);
   }

   /**
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize()
    */
   @Override
   public void initialize()
   {
      IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      IDE.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
      IDE.addHandler(ItemLockedEvent.TYPE, this);
      IDE.addHandler(ItemUnlockedEvent.TYPE, this);
   }

   /**
    * Handle this event to update status of button
    * according to status of active file: locked or unlocked.
    * 
    * @see org.exoplatform.ide.client.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.editor.event.EditorActiveFileChangedEvent)
    */
   @Override
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      FileModel file = event.getFile();
      activeFile = file;

      if (file == null || event.getEditor() == null)
      {
         setVisible(false);
         return;
      }

      setVisible(true);

      if (!file.isPersisted())
      {
         fileLocked = false;
         update();
         setEnabled(false);
         return;
      }

      if (activeFile.isLocked())
      {
         if (!lockTokens.containsKey(activeFile.getId()))
         {
            fileLocked = false;
            update();
            setEnabled(false);
            return;
         }
      }

      setEnabled(true);

      String lockToken = lockTokens.get(file.getId());

      if (lockToken == null)
      {
         fileLocked = false;
      }
      else
      {
         fileLocked = true;
      }
      update();
   }

   /**
    * Update selection status, prompt and event of button
    * according to status of active file: locked or unlocked.
    */
   private void update()
   {
      setSelected(fileLocked);

      if (fileLocked)
      {
         setTitle(TITLE_UNLOCK);
         setPrompt(TITLE_UNLOCK);
         setEvent(new LockFileEvent(false));
      }
      else
      {
         setTitle(TITLE_LOCK);
         setPrompt(TITLE_LOCK);
         setEvent(new LockFileEvent(true));
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler#onApplicationSettingsReceived(org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent)
    */
   @Override
   public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event)
   {
      if (event.getApplicationSettings().getValueAsMap("lock-tokens") == null)
      {
         event.getApplicationSettings().setValue("lock-tokens", new LinkedHashMap<String, String>(), Store.COOKIES);
      }

      lockTokens = event.getApplicationSettings().getValueAsMap("lock-tokens");
   }

   /**
    * Handle ItemunlockEvent to deselect button,
    * if active file was unlocked.
    * 
    * @see org.exoplatform.ide.client.framework.vfs.event.ItemUnlockedHandler#onItemUnlocked(org.exoplatform.ide.client.framework.vfs.event.ItemUnlockedEvent)
    */
   public void onItemUnlocked(ItemUnlockedEvent event)
   {
      if (isItemActiveFile(event.getItem()))
      {
         fileLocked = false;
         update();
      }
   }

   /**
    * Checks, if item is instance of file.
    * If item is file, checks, is active file equals to item.
    * 
    * @param item - item to check is equals to active file
    * @return true - item is active file, false - item is not active file
    */
   private boolean isItemActiveFile(Item item)
   {
      if (item == null || activeFile == null || !(item instanceof File))
      {
         return false;
      }

      if (activeFile.getId().equals(item.getId()))
      {
         return true;
      }

      return false;
   }

   /**
    * Handle this event, to select button, if active file was locked.
    * @see org.exoplatform.ide.vfs.client.event.ItemLockedHandler#onItemLocked(org.exoplatform.ide.vfs.client.event.ItemLockedEvent)
    */
   @Override
   public void onItemLocked(ItemLockedEvent event)
   {
      if (isItemActiveFile(event.getItem()))
      {
         fileLocked = true;
         update();
      }
   }
}
