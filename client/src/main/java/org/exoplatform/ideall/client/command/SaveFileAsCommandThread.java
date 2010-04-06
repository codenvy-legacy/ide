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
import org.exoplatform.ideall.client.browser.event.RefreshBrowserEvent;
import org.exoplatform.ideall.client.component.AskForValueDialog;
import org.exoplatform.ideall.client.component.ValueCallback;
import org.exoplatform.ideall.client.event.file.FileSavedEvent;
import org.exoplatform.ideall.client.event.file.SaveFileAsEvent;
import org.exoplatform.ideall.client.event.file.SaveFileAsHandler;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.model.vfs.api.File;
import org.exoplatform.ideall.client.model.vfs.api.Folder;
import org.exoplatform.ideall.client.model.vfs.api.Item;
import org.exoplatform.ideall.client.model.vfs.api.VirtualFileSystem;
import org.exoplatform.ideall.client.model.vfs.api.event.FileContentSavedEvent;
import org.exoplatform.ideall.client.model.vfs.api.event.FileContentSavedHandler;
import org.exoplatform.ideall.client.model.vfs.api.event.ItemPropertiesReceivedEvent;
import org.exoplatform.ideall.client.model.vfs.api.event.ItemPropertiesReceivedHandler;
import org.exoplatform.ideall.client.model.vfs.api.event.ItemPropertiesSavedEvent;
import org.exoplatform.ideall.client.model.vfs.api.event.ItemPropertiesSavedHandler;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class SaveFileAsCommandThread implements FileContentSavedHandler, ItemPropertiesSavedHandler,
   ExceptionThrownHandler, SaveFileAsHandler, ItemPropertiesReceivedHandler
{

   private ApplicationContext context;

   private Handlers handlers;

   private HandlerManager eventBus;

   private String sourceHref;

   public SaveFileAsCommandThread(HandlerManager eventBus, ApplicationContext context)
   {
      this.context = context;
      this.eventBus = eventBus;
      handlers = new Handlers(eventBus);
      eventBus.addHandler(SaveFileAsEvent.TYPE, this);
   }

   public void onSaveFileAs(SaveFileAsEvent event)
   {
      handlers.addHandler(FileContentSavedEvent.TYPE, this);
      handlers.addHandler(ItemPropertiesSavedEvent.TYPE, this);
      handlers.addHandler(ExceptionThrownEvent.TYPE, this);
      handlers.addHandler(ItemPropertiesReceivedEvent.TYPE, this);

      File file = event.getFile() != null ? event.getFile() : context.getActiveFile();
      onSaveAsFile(file);
   }

   private void onSaveAsFile(final File file)
   {
      String newFileName = file.isNewFile() ? file.getName() : "Copy Of " + file.getName();
      sourceHref = file.getHref();
      new AskForValueDialog("Save file as...", "Enter new file name:", newFileName, 400, new ValueCallback()
      {
         public void execute(String value)
         {
            if (value == null)
            {
               handlers.removeHandlers();
               return;
            }

            String pathToSave =
               getFilePath(context.getSelectedItems(context.getSelectedNavigationPanel()).get(0)) + value;
            System.out.println(pathToSave);
            File newFile = new File(pathToSave);
            newFile.setContent(file.getContent());
            newFile.setContentType(file.getContentType());
            newFile.setJcrContentNodeType(file.getJcrContentNodeType());
            newFile.setNewFile(true);
            if (file.isNewFile())
            {
            }
            else
            {
               newFile.getProperties().addAll(file.getProperties());
               newFile.setPropertiesChanged(true);
            }
            newFile.setIcon(file.getIcon());
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
      System.out.println("file content saved: " + event.getFile().getHref());
      if (event.isNewFile())
      {
         VirtualFileSystem.getInstance().getProperties(event.getFile());
      }
      else
      {
         VirtualFileSystem.getInstance().saveProperties(event.getFile());
      }
   }

   public void onItemPropertiesSaved(ItemPropertiesSavedEvent event)
   {
      handlers.removeHandlers();
      eventBus.fireEvent(new FileSavedEvent((File)event.getItem(), sourceHref));
      refreshBrouser(event.getItem().getHref());
   }

   public void onError(ExceptionThrownEvent event)
   {
      handlers.removeHandlers();
   }

   public void onItemPropertiesReceived(ItemPropertiesReceivedEvent event)
   {
      handlers.removeHandlers();
      eventBus.fireEvent(new FileSavedEvent((File)event.getItem(), sourceHref));
      refreshBrouser(event.getItem().getHref());
   }

   private void refreshBrouser(String hrefFolder)
   {
      hrefFolder = hrefFolder.substring(0, hrefFolder.lastIndexOf("/")) + "/";
      Folder folder = new Folder(hrefFolder);
      eventBus.fireEvent(new RefreshBrowserEvent(folder));
   }
}
