/**
 * Copyright (C) 2009 eXo Platform SAS.
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
 *
 */
package org.exoplatform.ide.client.module.navigation.handler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.ide.client.component.AskForValueDialog;
import org.exoplatform.ide.client.component.ValueCallback;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.event.FileSavedEvent;
import org.exoplatform.ide.client.framework.event.SaveFileAsEvent;
import org.exoplatform.ide.client.framework.event.SaveFileAsHandler;
import org.exoplatform.ide.client.framework.userinfo.UserInfo;
import org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedEvent;
import org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedHandler;
import org.exoplatform.ide.client.model.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.model.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.model.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.module.navigation.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.module.navigation.event.selection.ItemsSelectedEvent;
import org.exoplatform.ide.client.module.navigation.event.selection.ItemsSelectedHandler;
import org.exoplatform.ide.client.module.vfs.api.File;
import org.exoplatform.ide.client.module.vfs.api.Folder;
import org.exoplatform.ide.client.module.vfs.api.Item;
import org.exoplatform.ide.client.module.vfs.api.VirtualFileSystem;
import org.exoplatform.ide.client.module.vfs.api.event.FileContentSavedEvent;
import org.exoplatform.ide.client.module.vfs.api.event.FileContentSavedHandler;
import org.exoplatform.ide.client.module.vfs.api.event.ItemLockedEvent;
import org.exoplatform.ide.client.module.vfs.api.event.ItemLockedHandler;
import org.exoplatform.ide.client.module.vfs.api.event.ItemPropertiesReceivedEvent;
import org.exoplatform.ide.client.module.vfs.api.event.ItemPropertiesReceivedHandler;
import org.exoplatform.ide.client.module.vfs.api.event.ItemPropertiesSavedEvent;
import org.exoplatform.ide.client.module.vfs.api.event.ItemPropertiesSavedHandler;
import org.exoplatform.ide.client.module.vfs.api.event.ItemUnlockedEvent;
import org.exoplatform.ide.client.module.vfs.api.event.ItemUnlockedHandler;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class SaveFileAsCommandThread implements FileContentSavedHandler, ItemPropertiesSavedHandler,
   ExceptionThrownHandler, SaveFileAsHandler, ItemPropertiesReceivedHandler, ItemsSelectedHandler,
   EditorActiveFileChangedHandler, ItemUnlockedHandler, UserInfoReceivedHandler, ItemLockedHandler,
   ApplicationSettingsReceivedHandler
{

   private Handlers handlers;

   private HandlerManager eventBus;

   private String sourceHref;

   private List<Item> selectedItems = new ArrayList<Item>();

   private File activeFile;

   private Map<String, String> lockTokens;

   private UserInfo userInfo;

   private File oldFile;

   private File newFile;

   public SaveFileAsCommandThread(HandlerManager eventBus)
   {
      this.eventBus = eventBus;

      handlers = new Handlers(eventBus);
      this.eventBus.addHandler(SaveFileAsEvent.TYPE, this);
      this.eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
      this.eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      this.eventBus.addHandler(UserInfoReceivedEvent.TYPE, this);
      this.eventBus.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
   }

   /**
    * Add handlers
    * Open Save As Dialog
    * 
    * @see org.exoplatform.ide.client.module.navigation.event.SaveFileAsHandler#onSaveFileAs(org.exoplatform.ide.client.module.navigation.event.SaveFileAsEvent)
    */
   public void onSaveFileAs(SaveFileAsEvent event)
   {
      if (selectedItems == null || selectedItems.size() == 0)
      {
         Dialogs.getInstance().showInfo(
            "Please, select target folder in the Workspace Panel before calling this command !");
         return;
      }

      event.isSaveOnly();

      handlers.addHandler(FileContentSavedEvent.TYPE, this);
      handlers.addHandler(ItemPropertiesSavedEvent.TYPE, this);
      handlers.addHandler(ExceptionThrownEvent.TYPE, this);
      handlers.addHandler(ItemPropertiesReceivedEvent.TYPE, this);
      handlers.addHandler(ItemUnlockedEvent.TYPE, this);
      handlers.addHandler(ItemLockedEvent.TYPE, this);

      File file = event.getFile() != null ? event.getFile() : activeFile;
      newFileName(file);
   }

   /**
    * Open Save As Dialog 
    * 
    * @param file
    */
   private void newFileName(final File file)
   {

      final String newFileName = file.isNewFile() ? file.getName() : "Copy Of " + file.getName();
      sourceHref = file.getHref();
      new AskForValueDialog("Save file as", "Enter new file name:", newFileName, 400, new ValueCallback()
      {
         public void execute(String value)
         {
            if (value == null)
            {
               handlers.removeHandlers();
               return;
            }
            String pathToSave = getFilePath(selectedItems.get(0)) + value;
            File newFile = new File(pathToSave);
            newFile.setContent(file.getContent());
            newFile.setContentType(file.getContentType());
            newFile.setJcrContentNodeType(file.getJcrContentNodeType());
            newFile.setNewFile(true);
            newFile.setContentChanged(true);

            if (!file.isNewFile())
            {
               newFile.getProperties().addAll(file.getProperties());
               newFile.setPropertiesChanged(true);
            }

            newFile.setIcon(file.getIcon());

            oldFile = file;
            SaveFileAsCommandThread.this.newFile = newFile;

            //            VirtualFileSystem.getInstance().lock(newFile, 600, userInfo.getName());
            VirtualFileSystem.getInstance().saveContent(newFile);
         }

      });
   }

   private String getFilePath(Item item)
   {
      String href = item.getHref();
      if (item instanceof File)
      {
         href = href.substring(0, href.lastIndexOf("/") + 1);
      }
      return href;
   }

   public void onFileContentSaved(FileContentSavedEvent event)
   {
      if (!oldFile.isNewFile())
      {
         VirtualFileSystem.getInstance().unlock(oldFile, lockTokens.get(oldFile.getHref()));
      }
      else
      {
         VirtualFileSystem.getInstance().lock(event.getFile(), 600, userInfo.getName());
      }
   }

   public void onItemPropertiesSaved(ItemPropertiesSavedEvent event)
   {
      VirtualFileSystem.getInstance().getProperties(event.getItem());
   }

   public void onError(ExceptionThrownEvent event)
   {
      handlers.removeHandlers();
   }

   public void onItemPropertiesReceived(ItemPropertiesReceivedEvent event)
   {
      handlers.removeHandlers();
      eventBus.fireEvent(new FileSavedEvent((File)event.getItem(), sourceHref));
      refreshBrowser(event.getItem().getHref());
   }

   private void refreshBrowser(String hrefFolder)
   {
      hrefFolder = hrefFolder.substring(0, hrefFolder.lastIndexOf("/")) + "/";
      Folder folder = new Folder(hrefFolder);
      eventBus.fireEvent(new RefreshBrowserEvent(folder));
   }

   public void onItemsSelected(ItemsSelectedEvent event)
   {
      selectedItems = event.getSelectedItems();
   }

   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      activeFile = event.getFile();
   }

   /**
    * @see org.exoplatform.ide.client.module.vfs.api.event.ItemUnlockedHandler#onItemUnlocked(org.exoplatform.ide.client.module.vfs.api.event.ItemUnlockedEvent)
    */
   public void onItemUnlocked(ItemUnlockedEvent event)
   {
      VirtualFileSystem.getInstance().lock(newFile, 600, userInfo.getName());
   }

   /**
    * @see org.exoplatform.ide.client.model.conversation.event.UserInfoReceivedHandler#onUserInfoReceived(org.exoplatform.ide.client.model.conversation.event.UserInfoReceivedEvent)
    */
   public void onUserInfoReceived(UserInfoReceivedEvent event)
   {
      userInfo = event.getUserInfo();
   }

   /**
    * @see org.exoplatform.ide.client.module.vfs.api.event.ItemLockedHandler#onItemLocked(org.exoplatform.ide.client.module.vfs.api.event.ItemLockedEvent)
    */
   public void onItemLocked(ItemLockedEvent event)
   {

      if (event.getItem().isPropertiesChanged())
      {
         VirtualFileSystem.getInstance().saveProperties(event.getItem(), lockTokens.get(event.getItem().getHref()));
      }
      else
      {
         VirtualFileSystem.getInstance().getProperties(event.getItem());
      }
   }

   /**
    * @see org.exoplatform.ide.client.model.settings.event.ApplicationSettingsReceivedHandler#onApplicationSettingsReceived(org.exoplatform.ide.client.model.settings.event.ApplicationSettingsReceivedEvent)
    */
   @SuppressWarnings("unchecked")
   public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event)
   {
      if (event.getApplicationSettings().getValue("lock-tokens") == null)
      {
         event.getApplicationSettings().setValue("lock-tokens", new LinkedHashMap<String, String>(), Store.COOKIES);
      }
      lockTokens = (Map<String, String>)event.getApplicationSettings().getValue("lock-tokens");
   }

}
