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

import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.framework.event.FileSavedEvent;
import org.exoplatform.ide.client.framework.event.SaveAllFilesEvent;
import org.exoplatform.ide.client.framework.event.SaveAllFilesHandler;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.FileContentSaveCallback;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.framework.vfs.ItemPropertiesCallback;
import org.exoplatform.ide.client.framework.vfs.VirtualFileSystem;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class SaveAllFilesCommandHandler implements SaveAllFilesHandler, EditorFileOpenedHandler, 
EditorFileClosedHandler, ApplicationSettingsReceivedHandler
{

   private HandlerManager eventBus;

   private Map<String, File> openedFiles = new HashMap<String, File>();

   private Map<String, String> lockTokens;

   public SaveAllFilesCommandHandler(HandlerManager eventBus)
   {
      this.eventBus = eventBus;

      this.eventBus.addHandler(SaveAllFilesEvent.TYPE, this);
      this.eventBus.addHandler(EditorFileOpenedEvent.TYPE, this);
      this.eventBus.addHandler(EditorFileClosedEvent.TYPE, this);
      this.eventBus.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
   }

   public void onSaveAllFiles(SaveAllFilesEvent event)
   {
      saveNextUnsavedFile();
   }

   protected void saveNextUnsavedFile()
   {
      for (File file : openedFiles.values())
      {
         if (!file.isNewFile() && file.isContentChanged())
         {
            VirtualFileSystem.getInstance().saveContent(file, lockTokens.get(file.getHref()), new FileContentSaveCallback()
            {

               @Override
               protected void onSuccess(FileData result)
               {
                  if (result.getFile().isPropertiesChanged())
                  {
                     String lockToken = lockTokens.get(result.getFile().getHref());
                     saveFileProperties(result.getFile(), lockToken);
                  }
                  else
                  {
                     eventBus.fireEvent(new FileSavedEvent(result.getFile(), null));
                     saveNextUnsavedFile();
                  }
               }
            });
            return;
         }
      }

   }
   
   private void saveFileProperties(File file, String lockToken)
   {
      VirtualFileSystem.getInstance().saveProperties(file, lockToken, new ItemPropertiesCallback()
      {
         @Override
         protected void onSuccess(Item result)
         {
            eventBus.fireEvent(new FileSavedEvent((File)result, null));
            saveNextUnsavedFile();
         }
      });
   }

   public void onEditorFileOpened(EditorFileOpenedEvent event)
   {
      openedFiles = event.getOpenedFiles();
   }

   public void onEditorFileClosed(EditorFileClosedEvent event)
   {
      openedFiles = event.getOpenedFiles();
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

      lockTokens = (Map<String, String>)event.getApplicationSettings().getValueAsMap("lock-tokens");
   }

}
