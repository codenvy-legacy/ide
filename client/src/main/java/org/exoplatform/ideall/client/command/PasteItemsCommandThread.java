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
package org.exoplatform.ideall.client.command;

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

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class PasteItemsCommandThread implements PasteItemsHandler, CopyCompleteHandler, MoveCompleteHandler,
   ExceptionThrownHandler, FileContentSavedHandler
{
   private HandlerManager eventBus;

   private ApplicationContext context;

   private Handlers handlers;

   public PasteItemsCommandThread(HandlerManager eventBus, ApplicationContext context)
   {
      this.eventBus = eventBus;
      this.context = context;
      handlers = new Handlers(eventBus);

      eventBus.addHandler(PasteItemsEvent.TYPE, this);
   }

   public void onPasteItems(PasteItemsEvent event)
   {
      if (context.getItemsToCopy().size() != 0)
      {
         handlers.addHandler(CopyCompleteEvent.TYPE, this);
         handlers.addHandler(ExceptionThrownEvent.TYPE, this);
         copyNextItem();
         return;
      }

      if (context.getItemsToCut().size() != 0)
      {
         handlers.addHandler(MoveCompleteEvent.TYPE, this);
         handlers.addHandler(ExceptionThrownEvent.TYPE, this);
         handlers.addHandler(FileContentSavedEvent.TYPE, this);

         cutNextItem();
      }
   }

   private String getPathToPaste(Item item)
   {
      String selectedNavigationPanel = context.getSelectedNavigationPanel();
      if (context.getSelectedItems(selectedNavigationPanel).get(0) instanceof File)
      {
         String path = ((File)context.getSelectedItems(selectedNavigationPanel).get(0)).getPath();
         return path.substring(0, path.lastIndexOf("/"));
      }

      return context.getSelectedItems(selectedNavigationPanel).get(0).getPath();
   }

   /****************************************************************************************************
    * COPY
    ****************************************************************************************************/

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

      String pathToCopy = getPathToPaste(context.getSelectedItems(context.getSelectedNavigationPanel()).get(0));

      if (pathFromCopy.equals(pathToCopy))
      {
         String message = "Can't copy files in the same directory!";
         Dialogs.getInstance().showError(message);
         return;
      }

      String destination = pathToCopy + "/" + item.getName();

      VirtualFileSystem.getInstance().copy(item, destination);
   }

   public void onCopyComplete(CopyCompleteEvent event)
   {
      if (context.getItemsToCopy().size() != 0)
      {
         context.getItemsToCopy().remove(event.getCopiedItem());
         copyNextItem();
      }
   }

   /****************************************************************************************************
    * PASTE
    ****************************************************************************************************/

   private void cutNextItem()
   {
      if (context.getItemsToCut().size() == 0)
      {
         operationCompleted();
         return;
      }

      Item item = context.getItemsToCut().get(0);

      if (item instanceof File)
      {
         File file = (File)item;
         if (context.getOpenedFiles().get(file.getPath()) != null)
         {
            final File openedFile = context.getOpenedFiles().get(file.getPath());
            if (openedFile.isContentChanged())
            {
               Dialogs.getInstance().ask("Cut", "Save <b>" + openedFile.getName() + "</b> file?",
                  new BooleanValueReceivedCallback()
                  {
                     public void execute(Boolean value)
                     {
                        if (value != null && value == true)
                        {
                           VirtualFileSystem.getInstance().saveFileContent(openedFile, openedFile.getPath());
                        }
                        else
                        {
                           handlers.removeHandlers();
                        }
                     }
                  });
               return;
            }

         }
      }

      String pathFromCut = item.getPath();
      pathFromCut = pathFromCut.substring(0, pathFromCut.lastIndexOf("/"));

      String pathToCut = getPathToPaste(context.getSelectedItems(context.getSelectedNavigationPanel()).get(0));
      if (pathFromCut.equals(pathToCut))
      {
         String message = "Can't move files in the same directory!";
         Dialogs.getInstance().showError(message);
         return;
      }

      String destination = pathToCut + "/" + item.getName();

      VirtualFileSystem.getInstance().move(item, destination);
   }

   /****************************************************************************************************
    * FINALIZE
    ****************************************************************************************************/

   public void onError(ExceptionThrownEvent event)
   {
      handlers.removeHandlers();
   }

   private void operationCompleted()
   {
      handlers.removeHandlers();
      eventBus.fireEvent(new PasteItemsCompleteEvent());
      eventBus.fireEvent(new RefreshBrowserEvent());
   }

   public void onMoveComplete(MoveCompleteEvent event)
   {
      if (context.getItemsToCut().size() != 0)
      {
         context.getItemsToCut().remove(event.getItem());
         cutNextItem();
      }

   }

   public void onFileContentSaved(FileContentSavedEvent event)
   {
      cutNextItem();
   }

}
