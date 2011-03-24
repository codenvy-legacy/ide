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

import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.event.FileSavedEvent;
import org.exoplatform.ide.client.framework.event.SaveFileAsEvent;
import org.exoplatform.ide.client.framework.event.SaveFileEvent;
import org.exoplatform.ide.client.framework.event.SaveFileHandler;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.FileContentSaveCallback;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.framework.vfs.ItemPropertiesCallback;
import org.exoplatform.ide.client.framework.vfs.VirtualFileSystem;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class SaveFileCommandHandler implements SaveFileHandler, 
EditorActiveFileChangedHandler, ApplicationSettingsReceivedHandler
{

   private HandlerManager eventBus;

   private File activeFile;

   private Map<String, String> lockTokens;

   public SaveFileCommandHandler(HandlerManager eventBus)
   {
      this.eventBus = eventBus;

      eventBus.addHandler(SaveFileEvent.TYPE, this);
      eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      eventBus.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
   }

   public void onSaveFile(SaveFileEvent event)
   {
      File file = event.getFile() != null ? event.getFile() : activeFile;

      if (file.isNewFile())
      {
         eventBus.fireEvent(new SaveFileAsEvent(file, SaveFileAsEvent.SaveDialogType.YES_CANCEL, null, null));
         return;
      }

      String lockToken = lockTokens.get(file.getHref());

      if (file.isContentChanged())
      {
         VirtualFileSystem.getInstance().saveContent(file, lockToken, new FileContentSaveCallback()
         {
            @Override
            protected void onSuccess(FileData result)
            {
               getProperties(result.getFile());
            }
         });
         return;
      }
      else
      {
         if (file.isPropertiesChanged())
         {
            VirtualFileSystem.getInstance().saveProperties(file, lockToken, new ItemPropertiesCallback()
            {
               @Override
               protected void onSuccess(Item result)
               {
                  eventBus.fireEvent(new FileSavedEvent((File)result, null));
               }
            });
            return;
         }
      }
      eventBus.fireEvent(new FileSavedEvent(file, null));
   }

   private void getProperties(File file)
   {
      VirtualFileSystem.getInstance().getProperties(file, new ItemPropertiesCallback()
      {
         @Override
         protected void onSuccess(Item result)
         {
            eventBus.fireEvent(new FileSavedEvent((File)result, null));
         }
      });
   }

   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      activeFile = event.getFile();
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
      
      lockTokens = event.getApplicationSettings().getValueAsMap("lock-tokens");
   }

}
