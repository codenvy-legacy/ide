/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.client.module.navigation.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.dialogs.callback.BooleanValueReceivedCallback;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorCloseFileEvent;
import org.exoplatform.ide.client.module.navigation.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.module.navigation.event.selection.SelectItemEvent;
import org.exoplatform.ide.client.framework.module.vfs.api.File;
import org.exoplatform.ide.client.framework.module.vfs.api.Folder;
import org.exoplatform.ide.client.framework.module.vfs.api.Item;
import org.exoplatform.ide.client.framework.module.vfs.api.VirtualFileSystem;
import org.exoplatform.ide.client.framework.module.vfs.api.event.ItemDeletedEvent;
import org.exoplatform.ide.client.framework.module.vfs.api.event.ItemDeletedHandler;
import org.exoplatform.ide.client.framework.module.vfs.api.event.ItemUnlockedEvent;
import org.exoplatform.ide.client.framework.module.vfs.api.event.ItemUnlockedHandler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class DeleteItemPresenter implements ItemDeletedHandler, ExceptionThrownHandler, ItemUnlockedHandler
{

   public interface Display
   {

      HasClickHandlers getDeleteButton();

      HasClickHandlers getCancelButton();

      void closeForm();

      void hideForm();

   }

   private Display display;

   private List<Item> selectedItems;

   private Handlers handlers;

   private HandlerManager eventBus;

   private Item lastDeletedItem;

   private Map<String, File> openedFiles;

   private Map<String, String> lockTokens;

   public DeleteItemPresenter(HandlerManager eventBus, List<Item> selectedItems, Map<String, File> openedFiles,
      Map<String, String> lockTokens)
   {
      this.eventBus = eventBus;
      this.selectedItems = selectedItems;
      this.openedFiles = openedFiles;
      this.lockTokens = lockTokens;

      handlers = new Handlers(eventBus);
   }

   public void bindDisplay(Display d)
   {
      display = d;

      display.getCancelButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            display.closeForm();
         }
      });

      display.getDeleteButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            display.hideForm();
            deleteNextItem();
         }
      });

      handlers.addHandler(ItemDeletedEvent.TYPE, this);
      handlers.addHandler(ExceptionThrownEvent.TYPE, this);

   }

   public void destroy()
   {
      handlers.removeHandlers();
   }

   private void deleteNextItem()
   {
      if (selectedItems.size() == 0)
      {
         display.closeForm();
         deleteItemsComplete();
         return;
      }

      Item item = selectedItems.get(0);

      if (item instanceof File)
      {
         if (openedFiles.get(item.getHref()) != null)
         {
            File file = openedFiles.get(item.getHref());
            if (file.isContentChanged() || file.isPropertiesChanged())
            {
               String msg = "Do you want to delete modified file <b>" + item.getName() + "</b>?";
               showDialog(file, msg);
               return;
            }
         }
      }
      else
      {
         /*
          * check for new and unsaved files here
          */

         String href = item.getHref();
         //HashMap<String, File> openedFiles = context.getOpenedFiles();

         HashMap<String, File> copy = new HashMap<String, File>();
         for (String key : openedFiles.keySet())
         {
            File file = openedFiles.get(key);
            copy.put(key, file);
         }

         int files = 0;
         for (File file : copy.values())
         {
            if (file.getHref().startsWith(href) && !file.isNewFile() && file.isContentChanged())
            {
               files++;
            }
         }

         if (files > 0)
         {
            String msg =
               "Folder <b>" + item.getName() + "</b> contains " + copy.size() + " modified file(s), delete them?";
            showDialog(item, msg);
            return;
         }

      }
      if (lockTokens.containsKey(item.getHref()))
      {
         handlers.addHandler(ItemUnlockedEvent.TYPE, this);
         VirtualFileSystem.getInstance().unlock(item, lockTokens.get(item.getHref()));
      }
      else
      {
         VirtualFileSystem.getInstance().deleteItem(item);
      }
   }

   private void showDialog(final Item item, String msg)
   {
      Dialogs.getInstance().ask("Delete file", msg, new BooleanValueReceivedCallback()
      {

         public void execute(Boolean value)
         {
            if (value)
            {
               VirtualFileSystem.getInstance().deleteItem(item);
            }
            else
            {
               display.closeForm();
               deleteItemsComplete();
            }
         }

      });
   }

   public void onItemDeleted(ItemDeletedEvent event)
   {
      Item item = event.getItem();

      selectedItems.remove(item);

      //items.remove(0);

      if (item instanceof File)
      {
         if (openedFiles.get(item.getHref()) != null)
         {
            eventBus.fireEvent(new EditorCloseFileEvent((File)item, true));
         }
      }
      else
      {
         //find out the files are been in the removed folder

         String href = event.getItem().getHref();

         HashMap<String, File> copy = new HashMap<String, File>();
         for (String key : openedFiles.keySet())
         {
            File file = openedFiles.get(key);
            copy.put(key, file);
         }

         for (File file : copy.values())
         {
            if (file.getHref().startsWith(href) && !file.isNewFile())
            {
               lockTokens.remove(file.getHref());
               eventBus.fireEvent(new EditorCloseFileEvent(file, true));
            }

            //            if (Utils.match(file.getHref(), "^" + href + ".*", ""))
            //            {
            //               eventBus.fireEvent(new EditorCloseFileEvent(file, true));
            //            }
         }
      }
      lastDeletedItem = item;
      deleteNextItem();
   }

   public void onError(ExceptionThrownEvent event)
   {
      handlers.removeHandlers();
      display.closeForm();
   }

   private void deleteItemsComplete()
   {
      if (lastDeletedItem == null)
      {
         return;
      }

      selectedItems.clear();
      handlers.removeHandlers();

      String selectedItemHref = lastDeletedItem.getHref();

      if (lastDeletedItem instanceof Folder)
      {
         selectedItemHref = selectedItemHref.substring(0, selectedItemHref.lastIndexOf("/"));
      }
      selectedItemHref = selectedItemHref.substring(0, selectedItemHref.lastIndexOf("/") + 1);

      Folder folder = new Folder(selectedItemHref);

      //      context.getSelectedItems(context.getSelectedNavigationPanel()).clear();
      //      context.getSelectedItems(context.getSelectedNavigationPanel()).add(folder);

      eventBus.fireEvent(new RefreshBrowserEvent(folder));
      eventBus.fireEvent(new SelectItemEvent(folder.getHref()));
   }

   /**
    * @see org.exoplatform.ide.client.framework.module.vfs.api.event.ItemUnlockedHandler#onItemUnlocked(org.exoplatform.ide.client.framework.module.vfs.api.event.ItemUnlockedEvent)
    */
   public void onItemUnlocked(ItemUnlockedEvent event)
   {
      handlers.removeHandler(ItemUnlockedEvent.TYPE);
      VirtualFileSystem.getInstance().deleteItem(event.getItem());
   }

}
