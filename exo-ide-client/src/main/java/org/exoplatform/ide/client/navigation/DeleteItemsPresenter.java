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
package org.exoplatform.ide.client.navigation;

import org.exoplatform.gwtframework.commons.dialogs.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.editor.event.EditorCloseFileEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.navigation.event.SelectItemEvent;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.Folder;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.framework.vfs.VirtualFileSystem;
import org.exoplatform.ide.client.framework.vfs.event.ItemDeletedEvent;
import org.exoplatform.ide.client.framework.vfs.event.ItemUnlockedEvent;
import org.exoplatform.ide.client.navigation.event.DeleteItemEvent;
import org.exoplatform.ide.client.navigation.event.DeleteItemHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class DeleteItemsPresenter implements ApplicationSettingsReceivedHandler, ItemsSelectedHandler,
   EditorFileOpenedHandler, EditorFileClosedHandler, DeleteItemHandler, ViewClosedHandler
{

   public interface Display extends IsView
   {

      String ID = "ideDeleteItemsView";

      HasValue<String> getPromptField();

      HasClickHandlers getDeleteButton();

      HasClickHandlers getCancelButton();

   }
   
   private static final String UNLOCK_FAILURE_MSG = org.exoplatform.ide.client.IDE.ERRORS_CONSTANT.deleteFileUnlockFailure();
   
   private static final String DELETE_FILE_FAILURE_MESSAGE = org.exoplatform.ide.client.IDE.ERRORS_CONSTANT.deleteFileFailure();
   
   private static final String DELETE_FILE_DIALOG_TITLE = org.exoplatform.ide.client.IDE.NAVIGATION_CONSTANT.deleteFileDialogTitle();

   private HandlerManager eventBus;

   private Display display;

   private List<Item> selectedItems = new ArrayList<Item>();

   private Item lastDeletedItem;

   private Map<String, File> openedFiles = new HashMap<String, File>();

   private Map<String, String> lockTokens = new HashMap<String, String>();

   public DeleteItemsPresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;

      eventBus.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
      eventBus.addHandler(EditorFileOpenedEvent.TYPE, this);
      eventBus.addHandler(EditorFileClosedEvent.TYPE, this);
      eventBus.addHandler(DeleteItemEvent.TYPE, this);
      eventBus.addHandler(ViewClosedEvent.TYPE, this);
   }

   public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event)
   {
      ApplicationSettings applicationSettings = event.getApplicationSettings();
      if (applicationSettings.getValueAsMap("lock-tokens") == null)
      {
         applicationSettings.setValue("lock-tokens", new LinkedHashMap<String, String>(), Store.COOKIES);
      }
      lockTokens = applicationSettings.getValueAsMap("lock-tokens");
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

   public void onDeleteItem(DeleteItemEvent event)
   {
      if (display == null)
      {
         Display d = GWT.create(Display.class);
         IDE.getInstance().openView((View)d);
         bindDisplay(d);
      }
   }

   public void bindDisplay(Display d)
   {
      display = d;

      String prompt =
         selectedItems.size() == 1 ? "<br>Do you want to delete  <b>" + selectedItems.get(0).getName() + "</b> ?"
            : "<br>Do you want to delete <b>" + selectedItems.size() + "</b> items?";
      display.getPromptField().setValue(prompt);

      display.getCancelButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(Display.ID);
         }
      });

      display.getDeleteButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            deleteNextItem();
         }
      });
   }

   private void deleteNextItem()
   {
      if (selectedItems.size() == 0)
      {
         IDE.getInstance().closeView(Display.ID);
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
               eventBus.fireEvent(new ExceptionThrownEvent(exception, UNLOCK_FAILURE_MSG));
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
            selectedItems.remove(0);
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
            eventBus.fireEvent(new ExceptionThrownEvent(exception, DELETE_FILE_FAILURE_MESSAGE));
            IDE.getInstance().closeView(Display.ID);
         }
      });
   }

   private void showDialog(final Item item, String msg)
   {
      Dialogs.getInstance().ask(DELETE_FILE_DIALOG_TITLE, msg, new BooleanValueReceivedHandler()
      {
         public void booleanValueReceived(Boolean value)
         {
            if (value)
            {
               deleteItem(item);
            }
            else
            {
               IDE.getInstance().closeView(Display.ID);
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

   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
      }
   }

}
