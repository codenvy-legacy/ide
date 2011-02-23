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
package org.exoplatform.ide.client.module.navigation.action;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.dialogs.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.editor.event.EditorCloseFileEvent;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.navigation.event.SelectItemEvent;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.Folder;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.framework.vfs.VirtualFileSystem;
import org.exoplatform.ide.client.framework.vfs.event.ItemDeletedEvent;
import org.exoplatform.ide.client.framework.vfs.event.ItemUnlockedEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class DeleteItemPresenter
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
         VirtualFileSystem.getInstance().unlock(item, lockTokens.get(item.getHref()), new AsyncRequestCallback<Item>()
         {
            
            @Override
            protected void onSuccess(Item result)
            {
               eventBus.fireEvent(new ItemUnlockedEvent(result));
               deleteItem(result);
               
            }
            
            @Override
            protected void onFailure(Throwable exception)
            {
               eventBus.fireEvent(new ExceptionThrownEvent("Service is not deployed."));               
            }
         });
      }
      else
      {
         deleteItem(item);
      }
   }
   
   /**
    * Delete item.
    * 
    * @param item
    */
   private void deleteItem(final Item item)
   {
      VirtualFileSystem.getInstance().deleteItem(item, new AsyncRequestCallback<Item>()
      {
         
         @Override
         protected void onSuccess(Item result)
         {
            selectedItems.remove(item);
            eventBus.fireEvent(new ItemDeletedEvent(item));

            if (item instanceof File)
            {
               if (openedFiles.get(item.getHref()) != null)
               {
                  eventBus.fireEvent(new EditorCloseFileEvent((File)item, true));
               }
            }
            else
            {
               //find out opened files are been in the removed folder
               final String href = item.getHref();

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
               }
            }
            lastDeletedItem = item;
            deleteNextItem();
         }
         
         @Override
         protected void onFailure(Throwable exception)
         {
            eventBus.fireEvent(new ExceptionThrownEvent("Service is not deployed.<br>Resource not found."));
            display.closeForm();
         }
      });
   }

   private void showDialog(final Item item, String msg)
   {
      Dialogs.getInstance().ask("Delete file", msg, new BooleanValueReceivedHandler()
      {
         public void booleanValueReceived(Boolean value)
         {
            if (value)
            {
               deleteItem(item);
            }
            else
            {
               display.closeForm();
               deleteItemsComplete();
            }
         }

      });
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

      eventBus.fireEvent(new RefreshBrowserEvent(folder));
      eventBus.fireEvent(new SelectItemEvent(folder.getHref()));
   }
}
