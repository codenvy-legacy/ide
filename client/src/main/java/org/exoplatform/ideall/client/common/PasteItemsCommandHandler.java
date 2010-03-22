/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ideall.client.common;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.gwtframework.ui.client.dialogs.Dialogs;
import org.exoplatform.gwtframework.ui.client.dialogs.callback.BooleanValueReceivedCallback;
import org.exoplatform.ideall.client.browser.event.RefreshBrowserEvent;
import org.exoplatform.ideall.client.event.edit.PasteItemsCompleteEvent;
import org.exoplatform.ideall.client.event.edit.PasteItemsEvent;
import org.exoplatform.ideall.client.event.edit.PasteItemsHandler;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.model.vfs.api.File;
import org.exoplatform.ideall.client.model.vfs.api.Item;
import org.exoplatform.ideall.client.model.vfs.api.VirtualFileSystem;
import org.exoplatform.ideall.client.model.vfs.api.event.CopyCompleteEvent;
import org.exoplatform.ideall.client.model.vfs.api.event.CopyCompleteHandler;
import org.exoplatform.ideall.client.model.vfs.api.event.FileContentSavedEvent;
import org.exoplatform.ideall.client.model.vfs.api.event.FileContentSavedHandler;
import org.exoplatform.ideall.client.model.vfs.api.event.MoveCompleteEvent;
import org.exoplatform.ideall.client.model.vfs.api.event.MoveCompleteHandler;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Timer;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class PasteItemsCommandHandler implements PasteItemsHandler, CopyCompleteHandler, MoveCompleteHandler,
   ExceptionThrownHandler, FileContentSavedHandler
{
   private HandlerManager eventBus;

   private ApplicationContext context;

   private Handlers handlers;

   private List<File> fileToSaveContent = new ArrayList<File>();

   public PasteItemsCommandHandler(HandlerManager eventBus, ApplicationContext context)
   {
      this.eventBus = eventBus;
      this.context = context;
      handlers = new Handlers(eventBus);

      eventBus.addHandler(PasteItemsEvent.TYPE, this);

   }

   private String getPathToPaste(Item item)
   {
      if (context.getSelectedItems().get(0) instanceof File)
      {
         String path = ((File)context.getSelectedItems().get(0)).getPath();
         return path.substring(0, path.lastIndexOf("/"));
      }

      return context.getSelectedItems().get(0).getPath();

   }

   private void copyNextItem()
   {
      if (context.getItemsToCopy().size() == 0)
      {
         operationCompleted();
         return;
      }

      Item item = context.getItemsToCopy().get(0);

      String pathFromCopy = item.getPath();
      pathFromCopy = pathFromCopy.substring(0, pathFromCopy.lastIndexOf("/"));

      String pathToCopy = getPathToPaste(context.getSelectedItems().get(0));

      if (pathFromCopy.equals(pathToCopy))
      {
         String message = "Can't copy files in the same directory!";
         Dialogs.getInstance().showError(message);
         return;
      }

      String destination = pathToCopy + "/" + item.getName();

      VirtualFileSystem.getInstance().copy(item, destination);
   }

   private void cutNextItem()
   {
      if (context.getItemsToCut().size() == 0)
      {
         operationCompleted();
         return;
      }

      Item item = context.getItemsToCut().get(0);

      String pathFromCut = item.getPath();
      pathFromCut = pathFromCut.substring(0, pathFromCut.lastIndexOf("/"));

      String pathToCut = getPathToPaste(context.getSelectedItems().get(0));
      if (pathFromCut.equals(pathToCut))
      {
         String message = "Can't move files in the same directory!";
         Dialogs.getInstance().showError(message);
         return;
      }

      String destination = pathToCut + "/" + item.getName();

      VirtualFileSystem.getInstance().move(item, destination);
   }

   private void operationCompleted()
   {
      eventBus.fireEvent(new PasteItemsCompleteEvent());
      //eventBus.fireEvent(new SetFocusOnItemEvent(context.getSelectedItems().get(0).getPath()));
      eventBus.fireEvent(new RefreshBrowserEvent());
      handlers.removeHandlers();
   }

   private boolean isFilesChanged(List<Item> items)
   {
      boolean fileChanged = false;
      fileToSaveContent.clear();
      for (Item i : items)
      {
         if (i instanceof File)
         {
            File file = (File)i;
            if (context.getOpenedFiles().get(file.getPath()) != null)
            {
               if (context.getOpenedFiles().get(file.getPath()).isContentChanged())
               {
                  fileToSaveContent.add(context.getOpenedFiles().get(file.getPath()));
                  fileChanged = true;
               }
            }
         }
      }

      return fileChanged;
   }

   public void onPasteItems(PasteItemsEvent event)
   {
      if (context.getItemsToCopy().size() != 0)
      {
         handlers.addHandler(CopyCompleteEvent.TYPE, this);
         handlers.addHandler(ExceptionThrownEvent.TYPE, this);
         copyNextItem();
      }

      if (context.getItemsToCut().size() != 0)
      {
         handlers.addHandler(MoveCompleteEvent.TYPE, this);
         handlers.addHandler(ExceptionThrownEvent.TYPE, this);

         if (!isFilesChanged(context.getItemsToCut()))
         {
            cutNextItem();
         }
         else
         {
            Dialogs.getInstance().ask("Cut", "Save opened files?", new BooleanValueReceivedCallback()
            {
               public void execute(Boolean value)
               {
                  if (value != null && value == true)
                  {
                     handlers.addHandler(FileContentSavedEvent.TYPE, PasteItemsCommandHandler.this);
                     saveFileContent();
                  }
                  else
                  {
                     handlers.removeHandlers();
                  }
               }

            });
         }
      }

   }

   private void saveFileContent()
   {
      if (fileToSaveContent.size() != 0)
      {
         VirtualFileSystem.getInstance().saveFileContent(fileToSaveContent.get(0), fileToSaveContent.get(0).getPath());
         return;
      }

      cutNextItem();

   }

   public void onCopyComplete(CopyCompleteEvent event)
   {
      if (context.getItemsToCopy().size() != 0)
      {
         context.getItemsToCopy().remove(event.getCopiedItem());
         copyNextItem();
      }
   }

   public void onMoveComplete(MoveCompleteEvent event)
   {
      if (context.getItemsToCut().size() != 0)
      {
         context.getItemsToCut().remove(event.getItem());
         cutNextItem();
      }

   }

   public void onError(ExceptionThrownEvent event)
   {
      handlers.removeHandlers();
   }

   public void onFileContentSaved(FileContentSavedEvent event)
   {
      fileToSaveContent.remove(event.getFile());
      saveFileContent();
   }

}
