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
package org.exoplatform.ide.client.module.navigation.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.dialogs.callback.BooleanValueReceivedCallback;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.ide.client.editor.event.EditorUpdateFileStateEvent;
import org.exoplatform.ide.client.event.edit.PasteItemsCompleteEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.model.ApplicationContext;
import org.exoplatform.ide.client.model.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.model.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.model.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.module.navigation.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.module.navigation.event.edit.PasteItemsEvent;
import org.exoplatform.ide.client.module.navigation.event.edit.PasteItemsHandler;
import org.exoplatform.ide.client.module.navigation.event.selection.ItemsSelectedEvent;
import org.exoplatform.ide.client.module.navigation.event.selection.ItemsSelectedHandler;
import org.exoplatform.ide.client.module.vfs.api.File;
import org.exoplatform.ide.client.module.vfs.api.Folder;
import org.exoplatform.ide.client.module.vfs.api.Item;
import org.exoplatform.ide.client.module.vfs.api.VirtualFileSystem;
import org.exoplatform.ide.client.module.vfs.api.event.CopyCompleteEvent;
import org.exoplatform.ide.client.module.vfs.api.event.CopyCompleteHandler;
import org.exoplatform.ide.client.module.vfs.api.event.FileContentSavedEvent;
import org.exoplatform.ide.client.module.vfs.api.event.FileContentSavedHandler;
import org.exoplatform.ide.client.module.vfs.api.event.ItemDeletedEvent;
import org.exoplatform.ide.client.module.vfs.api.event.ItemDeletedHandler;
import org.exoplatform.ide.client.module.vfs.api.event.MoveCompleteEvent;
import org.exoplatform.ide.client.module.vfs.api.event.MoveCompleteHandler;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class PasteItemsCommandThread implements PasteItemsHandler, CopyCompleteHandler, MoveCompleteHandler,
   ExceptionThrownHandler, FileContentSavedHandler, ItemDeletedHandler, ItemsSelectedHandler, EditorFileOpenedHandler,
   EditorFileClosedHandler, ApplicationSettingsReceivedHandler
{
   private HandlerManager eventBus;

   private ApplicationContext context;

   private Handlers handlers;

   private String folderFromPaste;

   private String folderToPaste;

   private int numItemToCut;

   private List<Item> selectedItems = new ArrayList<Item>();

   private Map<String, File> openedFiles = new HashMap<String, File>();

   private Map<String, String> lockTokens;

   public PasteItemsCommandThread(HandlerManager eventBus, ApplicationContext context)
   {
      this.eventBus = eventBus;
      this.context = context;
      handlers = new Handlers(eventBus);

      eventBus.addHandler(PasteItemsEvent.TYPE, this);
      eventBus.addHandler(ItemDeletedEvent.TYPE, this);
      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
      eventBus.addHandler(EditorFileOpenedEvent.TYPE, this);
      eventBus.addHandler(EditorFileClosedEvent.TYPE, this);
      eventBus.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
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
      if (selectedItems.get(0) instanceof File)
      {
         String path = ((File)selectedItems.get(0)).getHref();
         return path.substring(0, path.lastIndexOf("/") + 1);
      }

      return selectedItems.get(0).getHref();
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
         cutCompleted();
         return;
      }

      Item item = context.getItemsToCut().get(0);

      if (item instanceof File)
      {
         File file = (File)item;
         if (openedFiles.get(file.getHref()) != null)
         {
            final File openedFile = openedFiles.get(file.getHref());
            if (openedFile.isContentChanged())
            {
               Dialogs.getInstance().ask("Cut", "Save <b>" + openedFile.getName() + "</b> file?",
                  new BooleanValueReceivedCallback()
                  {

                     public void execute(Boolean value)
                     {
                        if (value != null && value == true)
                        {
                           VirtualFileSystem.getInstance()
                              .saveContent(openedFile, lockTokens.get(openedFile.getHref()));
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

      VirtualFileSystem.getInstance().move(item, destination,lockTokens.get(item.getHref()));
   }

   private void cutCompleted()
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
      if (!href.endsWith("/"))
      {
         href += "/";
      }
      List<String> keys = new ArrayList<String>();
      for (String key : openedFiles.keySet())
      {
         keys.add(key);
      }

      for (String key : keys)
      {
         if (key.startsWith(sourceHref))
         {
            File file = openedFiles.get(key);
            String fileHref = file.getHref().replace(sourceHref, href);
            file.setHref(fileHref);

            openedFiles.remove(key);
            openedFiles.put(fileHref, file);
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

         if (openedFiles.containsKey(event.getSourceHref()))
         {
            File openedFle = openedFiles.get(event.getSourceHref());
            openedFle.setHref(file.getHref());
            openedFiles.remove(event.getSourceHref());
            openedFiles.put(openedFle.getHref(), openedFle);

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
      Item del = event.getItem();
      for (Item i : context.getItemsToCopy())
      {
         if (i.getHref().equals(del.getHref()))
         {
            context.getItemsToCopy().remove(i);
            break;
         }
      }

      for (Item i : context.getItemsToCut())
      {
         if (i.getHref().equals(del.getHref()))
         {
            context.getItemsToCut().remove(i);
            break;
         }
      }
   }

   public void onItemsSelected(ItemsSelectedEvent event)
   {
      selectedItems = event.getSelectedItems();
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
