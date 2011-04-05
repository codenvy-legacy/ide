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
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.edit.event.LockFileEvent;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.framework.vfs.ItemProperty;
import org.exoplatform.ide.client.framework.vfs.event.ItemLockResultReceivedEvent;
import org.exoplatform.ide.client.framework.vfs.event.ItemLockResultReceivedHandler;
import org.exoplatform.ide.client.framework.vfs.event.ItemUnlockedEvent;
import org.exoplatform.ide.client.framework.vfs.event.ItemUnlockedHandler;

import com.google.gwt.event.shared.HandlerManager;

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
   ApplicationSettingsReceivedHandler, ItemUnlockedHandler, ItemLockResultReceivedHandler
{

   //Edit/Show \\ Hide Line Numbers
   public static final String ID = "Edit/Lock \\ Unlock File";

   public static final String TITLE_LOCK = "Lock File";
   
   public static final String TITLE_UNLOCK = "Unlock File";

   public static final String PROMPT_LOCK = "Lock File";

   public static final String PROMPT_UNLOCK = "Unlock File";

   private boolean fileLocked = false;
   
   private Map<String, String> lockTokens;
   
   private File activeFile;

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
    * Initialize handlers for LockUnlockFileControl.
    * 
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize(com.google.gwt.event.shared.HandlerManager)
    */
   public void initialize(HandlerManager eventBus)
   {
      eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      eventBus.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
      eventBus.addHandler(ItemLockResultReceivedEvent.TYPE, this);
      eventBus.addHandler(ItemUnlockedEvent.TYPE, this);
   }
   
   /**
    * Handle this event to update status of button
    * according to status of active file: locked or unlocked.
    * 
    * @see org.exoplatform.ide.client.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.editor.event.EditorActiveFileChangedEvent)
    */
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      File file = event.getFile();
      activeFile = file;
      
      if (file == null || event.getEditor() == null)
      {
         setVisible(false);
         return;
      }
      
      setVisible(true);
      
      if (file.isNewFile())
      {
         fileLocked = false;
         update();
         setEnabled(false);
         return;
      }
      
      if (activeFile.getProperty(ItemProperty.LOCKDISCOVERY) != null)
      {
         if(!lockTokens.containsKey(activeFile.getHref()))
         {
            fileLocked = false;
            update();
            setEnabled(false);
            return;
         }
      }
      
      setEnabled(true);
      
      String lockToken = lockTokens.get(file.getHref());
      
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
         setPrompt(PROMPT_UNLOCK);
         setEvent(new LockFileEvent(false));
      }
      else
      {
         setTitle(TITLE_LOCK);
         setPrompt(PROMPT_LOCK);
         setEvent(new LockFileEvent(true));
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
    * Handle this event, to select button, if active file was locked.
    * 
    * @see org.exoplatform.ide.client.framework.vfs.event.ItemLockResultReceivedHandler#onItemLockResultReceived(org.exoplatform.ide.client.framework.vfs.event.ItemLockResultReceivedEvent)
    */
   public void onItemLockResultReceived(ItemLockResultReceivedEvent event)
   {
      if (isItemActiveFile(event.getItem()))
      {
         if (event.getException() == null)
         {
            fileLocked = true;
         }
         else
         {
            fileLocked = false;
         }
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
      
      if (activeFile.getHref().equals(item.getHref()))
      {
         return true;
      }
      
      return false;
   }
}
