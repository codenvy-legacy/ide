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

import org.exoplatform.gwtframework.commons.dialogs.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.ide.client.event.edit.PasteItemsCompleteEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorReplaceFileEvent;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.vfs.CopyCallback;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.FileContentSaveCallback;
import org.exoplatform.ide.client.framework.vfs.Folder;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.framework.vfs.VirtualFileSystem;
import org.exoplatform.ide.client.framework.vfs.callback.MoveItemCallback;
import org.exoplatform.ide.client.framework.vfs.event.ItemDeletedEvent;
import org.exoplatform.ide.client.framework.vfs.event.ItemDeletedHandler;
import org.exoplatform.ide.client.framework.vfs.event.MoveCompleteEvent;
import org.exoplatform.ide.client.model.ApplicationContext;
import org.exoplatform.ide.client.navigation.event.PasteItemsEvent;
import org.exoplatform.ide.client.navigation.event.PasteItemsHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class PasteItemsCommandHandler implements PasteItemsHandler, ItemDeletedHandler, 
ItemsSelectedHandler, EditorFileOpenedHandler, EditorFileClosedHandler, ApplicationSettingsReceivedHandler
{
   private HandlerManager eventBus;

   private ApplicationContext context;

   private String folderFromPaste;

   private String folderToPaste;

   private int numItemToCut;

   private List<Item> selectedItems = new ArrayList<Item>();

   private Map<String, File> openedFiles = new HashMap<String, File>();

   private Map<String, String> lockTokens;

   public PasteItemsCommandHandler(HandlerManager eventBus, ApplicationContext context)
   {
      this.eventBus = eventBus;
      this.context = context;

      eventBus.addHandler(PasteItemsEvent.TYPE, this);
      eventBus.addHandler(ItemDeletedEvent.TYPE, this);
      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
      eventBus.addHandler(EditorFileOpenedEvent.TYPE, this);
      eventBus.addHandler(EditorFileClosedEvent.TYPE, this);
      eventBus.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
   }

   /****************************************************************************************************
    * PASTE
    ****************************************************************************************************/
   public void onPasteItems(PasteItemsEvent event)
   {
      if (context.getItemsToCopy().size() != 0)
      {
         folderToPaste = getPathToPaste();
         folderFromPaste = getPahtFromPaste(context.getItemsToCopy().get(0));
         copyNextItem();
         numItemToCut = 0;
         return;
      }

      if (context.getItemsToCut().size() != 0)
      {
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
         return;
      }

      String destination = folderToPaste + item.getName();
      VirtualFileSystem.getInstance().copy(item, destination, new CopyCallback()
      {

         @Override
         protected void onSuccess(CopyItemData result)
         {
            if (context.getItemsToCopy().size() != 0)
            {
               context.getItemsToCopy().remove(result);
               copyNextItem();
            }
         }
      });
   }

   private void copyComlited()
   {
      eventBus.fireEvent(new PasteItemsCompleteEvent());

      Folder folder = new Folder(folderToPaste);

      eventBus.fireEvent(new RefreshBrowserEvent(folder, folder));
   }

   /****************************************************************************************************
    * CUT
    ****************************************************************************************************/

   private void cutNextItem()
   {
      if (context.getItemsToCut().size() == 0)
      {
         cutCompleted();
         return;
      }

      final Item item = context.getItemsToCut().get(0);

      if (item instanceof File)
      {
         File file = (File)item;
         if (openedFiles.get(file.getHref()) != null)
         {
            final File openedFile = openedFiles.get(file.getHref());
            if (openedFile.isContentChanged())
            {
               Dialogs.getInstance().ask("Cut", "Save <b>" + openedFile.getName() + "</b> file?",
                  new BooleanValueReceivedHandler()
                  {
                     public void booleanValueReceived(Boolean value)
                     {
                        if (value != null && value == true)
                        {
                           VirtualFileSystem.getInstance().saveContent(openedFile,
                              lockTokens.get(openedFile.getHref()), new FileContentSaveCallback()
                              {
                                 @Override
                                 protected void onSuccess(FileData result)
                                 {
                                    cutNextItem();
                                 }
                              });
                        }
                     }
                  });
               return;
            }

         }
      }

      if (folderFromPaste.equals(folderToPaste) || folderToPaste.equals(item.getHref()))
      {
         Dialogs.getInstance().showError("Can't move items in the same directory!");
         return;
      }

      String destination = folderToPaste + item.getName();

      
      VirtualFileSystem.getInstance().move(item, destination, lockTokens.get(item.getHref()), new MoveItemCallback()
      {
         @Override
         protected void onSuccess(MoveItemData result)
         {
            moveComplete(result.getItem(), result.getOldHref());
            eventBus.fireEvent(new MoveCompleteEvent(result.getItem(), result.getOldHref()));
         }

         @Override
         protected void onFailure(Throwable exception)
         {
            super.onFailure(exception);
            handleError();
         }
      });
   }

   private void cutCompleted()
   {
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

   private void handleError()
   {
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
            eventBus.fireEvent(new EditorReplaceFileEvent(new File(key),file));
         }
      }
   }

   public void moveComplete(Item item, String oldSourceHref)
   {
      numItemToCut--;
      if (item instanceof File)
      {
         File file = (File)item;

         if (openedFiles.containsKey(oldSourceHref))
         {
            File openedFle = openedFiles.get(oldSourceHref);
            openedFle.setHref(file.getHref());
            openedFiles.remove(oldSourceHref);
            openedFiles.put(openedFle.getHref(), openedFle);

            eventBus.fireEvent(new EditorReplaceFileEvent(new File(oldSourceHref), file));
         }
      }
      else
      {
         updateOpenedFiles(item.getHref(), oldSourceHref);
      }
      if (context.getItemsToCut().size() != 0)
      {
         context.getItemsToCut().remove(item);
         cutNextItem();
      }

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
   public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event)
   {
      if (event.getApplicationSettings().getValueAsMap("lock-tokens") == null)
      {
               event.getApplicationSettings().setValue("lock-tokens", new LinkedHashMap<String, String>(), Store.COOKIES);
      }
      
      lockTokens = (Map<String, String>)event.getApplicationSettings().getValueAsMap("lock-tokens");
   }   

}
