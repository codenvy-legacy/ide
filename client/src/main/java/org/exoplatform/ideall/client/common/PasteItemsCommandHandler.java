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

import java.util.List;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.gwtframework.ui.client.dialogs.Dialogs;
import org.exoplatform.ideall.client.browser.event.RefreshBrowserEvent;
import org.exoplatform.ideall.client.event.edit.PasteItemsCompleteEvent;
import org.exoplatform.ideall.client.event.edit.PasteItemsEvent;
import org.exoplatform.ideall.client.event.edit.PasteItemsHandler;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.model.vfs.api.File;
import org.exoplatform.ideall.client.model.vfs.api.Item;
import org.exoplatform.ideall.client.model.vfs.api.VirtualFileSystem;
import org.exoplatform.ideall.client.model.vfs.api.event.ItemCopyCompleteEvent;
import org.exoplatform.ideall.client.model.vfs.api.event.ItemCopyCompleteHandler;
import org.exoplatform.ideall.client.model.vfs.api.event.MoveCompleteEvent;
import org.exoplatform.ideall.client.model.vfs.api.event.MoveCompleteHandler;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class PasteItemsCommandHandler implements PasteItemsHandler, ItemCopyCompleteHandler, MoveCompleteHandler,
   ExceptionThrownHandler
{
   private HandlerManager eventBus;

   private ApplicationContext context;

   private Handlers handlers;

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
      
      System.out.println(pathToCopy);
      
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

   private boolean isFilesOpen(List<Item> items)
   {
      for (Item i : items)
      {
         if (i instanceof File)
         {
            if (context.getOpenedFiles().containsValue(i))
            {
               return true;
            }
         }
      }
      return false;
   }

   public void onPasteItems(PasteItemsEvent event)
   {
      if (context.getItemsToCopy().size() != 0)
      {
         handlers.addHandler(ItemCopyCompleteEvent.TYPE, this);
         handlers.addHandler(ExceptionThrownEvent.TYPE, this);
         copyNextItem();
      }

      if (context.getItemsToCut().size() != 0)
      {
         if (!isFilesOpen(context.getItemsToCut()))
         {
            handlers.addHandler(MoveCompleteEvent.TYPE, this);
            handlers.addHandler(ExceptionThrownEvent.TYPE, this);
            cutNextItem();
         }
         else
         {
            String message = "You should close open files, before cut";
            Dialogs.getInstance().showInfo(message);
            return;
         }
      }

   }

   public void onItemCopyComplete(ItemCopyCompleteEvent event)
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
}
