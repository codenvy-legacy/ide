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
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.dialogs.callback.BooleanValueReceivedCallback;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.ideall.client.browser.event.RefreshBrowserEvent;
import org.exoplatform.ideall.client.editor.event.EditorUpdateFileStateEvent;
import org.exoplatform.ideall.client.event.edit.PasteItemsCompleteEvent;
import org.exoplatform.ideall.client.event.edit.PasteItemsEvent;
import org.exoplatform.ideall.client.event.edit.PasteItemsHandler;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.vfs.api.File;
import org.exoplatform.ideall.vfs.api.Folder;
import org.exoplatform.ideall.vfs.api.Item;
import org.exoplatform.ideall.vfs.api.VirtualFileSystem;
import org.exoplatform.ideall.vfs.api.event.CopyCompleteEvent;
import org.exoplatform.ideall.vfs.api.event.CopyCompleteHandler;
import org.exoplatform.ideall.vfs.api.event.FileContentSavedEvent;
import org.exoplatform.ideall.vfs.api.event.FileContentSavedHandler;
import org.exoplatform.ideall.vfs.api.event.ItemDeletedEvent;
import org.exoplatform.ideall.vfs.api.event.ItemDeletedHandler;
import org.exoplatform.ideall.vfs.api.event.MoveCompleteEvent;
import org.exoplatform.ideall.vfs.api.event.MoveCompleteHandler;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class PasteItemsCommandThread implements PasteItemsHandler, CopyCompleteHandler, MoveCompleteHandler,
   ExceptionThrownHandler, FileContentSavedHandler, ItemDeletedHandler
{
   private HandlerManager eventBus;

   private ApplicationContext context;

   private Handlers handlers;

   private String folderFromPaste;

   private String folderToPaste;

   private int numItemToCut;

   public PasteItemsCommandThread(HandlerManager eventBus, ApplicationContext context)
   {
      this.eventBus = eventBus;
      this.context = context;
      handlers = new Handlers(eventBus);

      eventBus.addHandler(PasteItemsEvent.TYPE, this);
      eventBus.addHandler(ItemDeletedEvent.TYPE, this);
   }

   public void onPasteItems(PasteItemsEvent event)
   {
      if (context.getItemsToCopy().size() != 0)
      {
         handlers.addHandler(CopyCompleteEvent.TYPE, this);
         handlers.addHandler(ExceptionThrownEvent.TYPE, this);
         folderToPaste = getPathToPaste();
         folderFromPaste = getPahtFromPaste(context.getItemsToCopy().get(0));
         copyNextItem();
         numItemToCut = 0;
         return;
      }

      if (context.getItemsToCut().size() != 0)
      {
         handlers.addHandler(MoveCompleteEvent.TYPE, this);
         handlers.addHandler(ExceptionThrownEvent.TYPE, this);
         handlers.addHandler(FileContentSavedEvent.TYPE, this);
         folderToPaste = getPathToPaste();
         folderFromPaste = getPahtFromPaste(context.getItemsToCut().get(0));
         numItemToCut = context.getItemsToCut().size();
         cutNextItem();
      }
   }

   private String getPathToPaste()
   {
      String selectedNavigationPanel = context.getSelectedNavigationPanel();
      if (context.getSelectedItems(selectedNavigationPanel).get(0) instanceof File)
      {
         String path = ((File)context.getSelectedItems(selectedNavigationPanel).get(0)).getHref();
         return path.substring(0, path.lastIndexOf("/") + 1);
      }

      return context.getSelectedItems(selectedNavigationPanel).get(0).getHref();
   }

   private String getPahtFromPaste(Item item)
   {
      String path = item.getHref();
      if (path.endsWith("/"))
      {
         path = path.substring(0, path.length() - 1);
      }

      return path.substring(0, path.lastIndexOf("/") + 1);
   }

   /****************************************************************************************************
    * COPY
    ****************************************************************************************************/

   private void copyNextItem()
   {
      if (context.getItemsToCopy().size() == 0)
      {
         copyComlited();
         return;
      }

      Item item = context.getItemsToCopy().get(0);

      if (folderFromPaste.equals(folderToPaste) || folderToPaste.equals(item.getHref()))
      {
         String message = "Can't copy items in the same directory!";
         Dialogs.getInstance().showError(message);
         handlers.removeHandlers();
         return;
      }

      String destination = folderToPaste + item.getName();
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

   private void copyComlited()
   {
      handlers.removeHandlers();
      eventBus.fireEvent(new PasteItemsCompleteEvent());

      Folder folder = new Folder(folderToPaste);

      eventBus.fireEvent(new RefreshBrowserEvent(folder, folder));
   }

   /****************************************************************************************************
    * PASTE
    ****************************************************************************************************/

   private void cutNextItem()
   {
      if (context.getItemsToCut().size() == 0)
      {
         cutComplited();
         return;
      }

      Item item = context.getItemsToCut().get(0);

      if (item instanceof File)
      {
         File file = (File)item;
         if (context.getOpenedFiles().get(file.getHref()) != null)
         {
            final File openedFile = context.getOpenedFiles().get(file.getHref());
            if (openedFile.isContentChanged())
            {
               Dialogs.getInstance().ask("Cut", "Save <b>" + openedFile.getName() + "</b> file?",
                  new BooleanValueReceivedCallback()
                  {
                     public void execute(Boolean value)
                     {
                        if (value != null && value == true)
                        {
                           VirtualFileSystem.getInstance().saveContent(openedFile);
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

      if (folderFromPaste.equals(folderToPaste) || folderToPaste.equals(item.getHref()))
      {
         String message = "Can't move items in the same directory!";
         Dialogs.getInstance().showError(message);
         handlers.removeHandlers();
         return;
      }

      String destination = folderToPaste + item.getName();

      VirtualFileSystem.getInstance().move(item, destination);
   }

   private void cutComplited()
   {
      handlers.removeHandlers();
      eventBus.fireEvent(new PasteItemsCompleteEvent());

      List<Folder> folders = new ArrayList<Folder>();

      Folder folderFrom = new Folder(folderFromPaste);
      Folder folderTo = new Folder(folderToPaste);

      folders.add(folderFrom);
      folders.add(folderTo);
      eventBus.fireEvent(new RefreshBrowserEvent(folders, folderTo));
   }

   /****************************************************************************************************
    * FINALIZE
    ****************************************************************************************************/

   public void onError(ExceptionThrownEvent event)
   {
      handlers.removeHandlers();
      if (numItemToCut > 0)
      {
         List<Folder> folders = new ArrayList<Folder>();

         Folder folderFrom = new Folder(folderFromPaste);
         Folder folderTo = new Folder(folderToPaste);

         folders.add(folderFrom);
         folders.add(folderTo);
         eventBus.fireEvent(new RefreshBrowserEvent(folders, folderTo));
      }
   }

   private void updateOpenedFiles(String href, String sourceHref)
   {
      List<String> keys = new ArrayList<String>();
      for (String key : context.getOpenedFiles().keySet())
      {
         keys.add(key);
      }

      for (String key : keys)
      {
         if (key.startsWith(sourceHref))
         {
            File file = context.getOpenedFiles().get(key);
            String fileHref = file.getHref().replace(sourceHref, href);
            file.setHref(fileHref);

            context.getOpenedFiles().remove(key);
            context.getOpenedFiles().put(fileHref, file);
            eventBus.fireEvent(new EditorUpdateFileStateEvent(file));
         }
      }
   }

   public void onMoveComplete(MoveCompleteEvent event)
   {
      numItemToCut--;
      if (event.getItem() instanceof File)
      {
         File file = (File)event.getItem();

         if (context.getOpenedFiles().containsKey(event.getSourceHref()))
         {
            File openedFle = context.getOpenedFiles().get(event.getSourceHref());
            openedFle.setHref(file.getHref());
            context.getOpenedFiles().remove(event.getSourceHref());
            context.getOpenedFiles().put(openedFle.getHref(), openedFle);

            eventBus.fireEvent(new EditorUpdateFileStateEvent(file));
         }
      }
      else
      {
         updateOpenedFiles(event.getItem().getHref(), event.getSourceHref());
      }
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

   public void onItemDeleted(ItemDeletedEvent event)
   {
      context.getItemsToCopy().remove(event.getItem());
      context.getItemsToCut().remove(event.getItem());
   }

}
