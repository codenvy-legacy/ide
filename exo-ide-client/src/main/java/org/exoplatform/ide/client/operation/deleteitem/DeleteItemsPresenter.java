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
package org.exoplatform.ide.client.operation.deleteitem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.control.event.RegisterControlEvent.DockTarget;
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
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.event.ItemDeletedEvent;
import org.exoplatform.ide.vfs.client.event.ItemUnlockedEvent;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.shared.Item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestException;
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

      HasValue<String> getPromptField();

      HasClickHandlers getDeleteButton();

      HasClickHandlers getCancelButton();

   }

   private static final String UNLOCK_FAILURE_MSG = org.exoplatform.ide.client.IDE.ERRORS_CONSTANT
      .deleteFileUnlockFailure();

   private static final String DELETE_FILE_FAILURE_MESSAGE = org.exoplatform.ide.client.IDE.ERRORS_CONSTANT
      .deleteFileFailure();

   private static final String DELETE_FILE_DIALOG_TITLE = org.exoplatform.ide.client.IDE.NAVIGATION_CONSTANT
      .deleteFileDialogTitle();

   private Display display;

   private List<Item> selectedItems = new ArrayList<Item>();

   private Item lastDeletedItem;

   private Map<String, FileModel> openedFiles = new HashMap<String, FileModel>();

   private Map<String, String> lockTokens = new HashMap<String, String>();

   public DeleteItemsPresenter()
   {
      IDE.getInstance().addControl(new DeleteItemCommand(), DockTarget.TOOLBAR, false);
      
      IDE.EVENT_BUS.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
      IDE.EVENT_BUS.addHandler(ItemsSelectedEvent.TYPE, this);
      IDE.EVENT_BUS.addHandler(EditorFileOpenedEvent.TYPE, this);
      IDE.EVENT_BUS.addHandler(EditorFileClosedEvent.TYPE, this);
      IDE.EVENT_BUS.addHandler(DeleteItemEvent.TYPE, this);
      IDE.EVENT_BUS.addHandler(ViewClosedEvent.TYPE, this);
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
         selectedItems.size() == 1 ? org.exoplatform.ide.client.IDE.IDE_LOCALIZATION_MESSAGES
            .deleteItemsAskDeleteOneItem(selectedItems.get(0).getName())
            : org.exoplatform.ide.client.IDE.IDE_LOCALIZATION_MESSAGES.deleteItemsAskDeleteSeveralItems(selectedItems
               .size());
      display.getPromptField().setValue(prompt);

      display.getCancelButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(display.asView().getId());
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
         IDE.getInstance().closeView(display.asView().getId());
         deleteItemsComplete();
         return;
      }

      final Item item = selectedItems.get(0);
      if (item instanceof FileModel)
      {
         if (openedFiles.get(item.getId()) != null)
         {
            FileModel file = openedFiles.get(item.getId());
            //TODO
            if (file.isContentChanged()/* || file.isPropertiesChanged()*/)
            {
               String msg =
                  org.exoplatform.ide.client.IDE.IDE_LOCALIZATION_MESSAGES.deleteItemsAskDeleteModifiedFile(item
                     .getName());
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

         String path = item.getPath();
         //HashMap<String, File> openedFiles = context.getOpenedFiles();

         HashMap<String, FileModel> copy = new HashMap<String, FileModel>();
         for (String key : openedFiles.keySet())
         {
            FileModel file = openedFiles.get(key);
            copy.put(key, file);
         }

         int files = 0;
         for (FileModel file : copy.values())
         {
            if (file.getPath().startsWith(path) && file.isPersisted() && file.isContentChanged())
            {
               files++;
            }
         }

         if (files > 0)
         {
            final String msg =
               org.exoplatform.ide.client.IDE.IDE_LOCALIZATION_MESSAGES.deleteItemsAskDeleteFolderWithModifiedFiles(
                  item.getName(), copy.size());
            showDialog(item, msg);
            return;
         }

      }
      if (lockTokens.containsKey(item.getId()))
      {
         try
         {
            VirtualFileSystem.getInstance().unlock((FileModel)item, lockTokens.get(item.getId()),
               new AsyncRequestCallback<Object>()
               {

                  @Override
                  protected void onSuccess(Object result)
                  {
                     IDE.EVENT_BUS.fireEvent(new ItemUnlockedEvent(item));
                     deleteItem(item);
                  }

                  @Override
                  protected void onFailure(Throwable exception)
                  {
                     IDE.EVENT_BUS.fireEvent(new ExceptionThrownEvent(exception, UNLOCK_FAILURE_MSG));
                  }
               });
         }
         catch (RequestException e)
         {
            e.printStackTrace();
            IDE.EVENT_BUS.fireEvent(new ExceptionThrownEvent(e, UNLOCK_FAILURE_MSG));
         }
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
      try
      {
         VirtualFileSystem.getInstance().delete(item, new AsyncRequestCallback<String>()
         {

            @Override
            protected void onSuccess(String result)
            {
               selectedItems.remove(0);
               IDE.EVENT_BUS.fireEvent(new ItemDeletedEvent(item));

               if (item instanceof FileModel)
               {
                  if (openedFiles.get(item.getId()) != null)
                  {
                     IDE.EVENT_BUS.fireEvent(new EditorCloseFileEvent((FileModel)item, true));
                  }
               }
               else
               {
                  //find out opened files are been in the removed folder
                  final String path = item.getPath();

                  HashMap<String, FileModel> copy = new HashMap<String, FileModel>();
                  for (String key : openedFiles.keySet())
                  {
                     FileModel file = openedFiles.get(key);
                     copy.put(key, file);
                  }

                  for (FileModel file : copy.values())
                  {
                     if (file.getPath().startsWith(path) && file.isPersisted())
                     {
                        lockTokens.remove(file.getId());
                        IDE.EVENT_BUS.fireEvent(new EditorCloseFileEvent(file, true));
                     }
                  }
               }
               lastDeletedItem = item;
               deleteNextItem();
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               IDE.EVENT_BUS.fireEvent(new ExceptionThrownEvent(exception, DELETE_FILE_FAILURE_MESSAGE));
               IDE.getInstance().closeView(display.asView().getId());
            }
         });
      }
      catch (RequestException e)
      {
         e.printStackTrace();
         IDE.EVENT_BUS.fireEvent(new ExceptionThrownEvent(e, DELETE_FILE_FAILURE_MESSAGE));
      }
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
               IDE.getInstance().closeView(display.asView().getId());
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

      FolderModel folder;
      if (lastDeletedItem instanceof FileModel)
      {
         folder = ((FileModel)lastDeletedItem).getParent();
      }
      else
         folder = ((FolderModel)lastDeletedItem).getParent();
      IDE.EVENT_BUS.fireEvent(new RefreshBrowserEvent(folder));
      IDE.EVENT_BUS.fireEvent(new SelectItemEvent(folder.getId()));
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
