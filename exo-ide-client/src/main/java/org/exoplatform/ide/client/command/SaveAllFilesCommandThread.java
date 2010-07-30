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
package org.exoplatform.ideall.client.command;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.ideall.client.event.file.FileSavedEvent;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.module.navigation.event.SaveAllFilesEvent;
import org.exoplatform.ideall.client.module.navigation.event.SaveAllFilesHandler;
import org.exoplatform.ideall.client.module.vfs.api.File;
import org.exoplatform.ideall.client.module.vfs.api.VirtualFileSystem;
import org.exoplatform.ideall.client.module.vfs.api.event.FileContentSavedEvent;
import org.exoplatform.ideall.client.module.vfs.api.event.FileContentSavedHandler;
import org.exoplatform.ideall.client.module.vfs.api.event.ItemPropertiesSavedEvent;
import org.exoplatform.ideall.client.module.vfs.api.event.ItemPropertiesSavedHandler;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class SaveAllFilesCommandThread implements FileContentSavedHandler, ItemPropertiesSavedHandler,
   ExceptionThrownHandler, SaveAllFilesHandler
{

   private ApplicationContext context;

   private Handlers handlers;
   
   private HandlerManager eventBus;

   public SaveAllFilesCommandThread(HandlerManager eventBus, ApplicationContext context)
   {
      this.context = context;
      this.eventBus = eventBus;
      handlers = new Handlers(eventBus);
      eventBus.addHandler(SaveAllFilesEvent.TYPE, this);
   }

   public void onSaveAllFiles(SaveAllFilesEvent event)
   {
      handlers.addHandler(FileContentSavedEvent.TYPE, this);
      handlers.addHandler(ItemPropertiesSavedEvent.TYPE, this);
      handlers.addHandler(ExceptionThrownEvent.TYPE, this);

      saveNextUnsavedFile();
   }

   protected void saveNextUnsavedFile()
   {
      for (File file : context.getOpenedFiles().values())
      {
         if (!file.isNewFile() && file.isContentChanged())
         {
            VirtualFileSystem.getInstance().saveContent(file);
            return;
         }
      }

      handlers.removeHandlers();
   }

   public void onFileContentSaved(FileContentSavedEvent event)
   {
      if (event.getFile().isPropertiesChanged())
      {
         VirtualFileSystem.getInstance().saveProperties(event.getFile());
      }
      else
      {
         eventBus.fireEvent(new FileSavedEvent(event.getFile(), null));
         saveNextUnsavedFile();
      }
   }

   public void onItemPropertiesSaved(ItemPropertiesSavedEvent event)
   {
      eventBus.fireEvent(new FileSavedEvent((File)event.getItem(), null));
      saveNextUnsavedFile();
   }

   public void onError(ExceptionThrownEvent event)
   {
      handlers.removeHandlers();
   }

}
