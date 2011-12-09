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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.control.Docking;
import org.exoplatform.ide.client.framework.editor.event.EditorCloseFileEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.navigation.event.SelectItemEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.event.ItemDeletedEvent;
import org.exoplatform.ide.vfs.client.event.ItemUnlockedEvent;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ItemContext;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class DeleteItemsPresenter implements ApplicationSettingsReceivedHandler, ItemsSelectedHandler,
   EditorFileOpenedHandler, EditorFileClosedHandler, DeleteItemHandler, ViewClosedHandler, ProjectOpenedHandler,
   ProjectClosedHandler
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

   //private List<Item> selectedItems = new ArrayList<Item>();

   private List<Item> items = new ArrayList<Item>();

   private List<Item> itemsToDelete = new ArrayList<Item>();

   private Item lastDeletedItem;

   private Map<String, FileModel> openedFiles = new HashMap<String, FileModel>();

   private Map<String, String> lockTokens = new HashMap<String, String>();

   private ProjectModel currentProject;

   /**
    * Creates new instance of this presenter.
    */
   public DeleteItemsPresenter()
   {
      IDE.getInstance().addControl(new DeleteItemControl(), Docking.TOOLBAR);

      IDE.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
      IDE.addHandler(ItemsSelectedEvent.TYPE, this);
      IDE.addHandler(EditorFileOpenedEvent.TYPE, this);
      IDE.addHandler(EditorFileClosedEvent.TYPE, this);
      IDE.addHandler(DeleteItemEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
      IDE.addHandler(ProjectOpenedEvent.TYPE, this);
      IDE.addHandler(ProjectClosedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler#onApplicationSettingsReceived(org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent)
    */
   public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event)
   {
      ApplicationSettings applicationSettings = event.getApplicationSettings();
      if (applicationSettings.getValueAsMap("lock-tokens") == null)
      {
         applicationSettings.setValue("lock-tokens", new LinkedHashMap<String, String>(), Store.COOKIES);
      }
      lockTokens = applicationSettings.getValueAsMap("lock-tokens");
   }

   /**
    * @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent)
    */
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      items = event.getSelectedItems();
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler#onEditorFileOpened(org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent)
    */
   public void onEditorFileOpened(EditorFileOpenedEvent event)
   {
      openedFiles = event.getOpenedFiles();
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler#onEditorFileClosed(org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent)
    */
   public void onEditorFileClosed(EditorFileClosedEvent event)
   {
      openedFiles = event.getOpenedFiles();
   }

   /**
    * @see org.exoplatform.ide.client.operation.deleteitem.DeleteItemHandler#onDeleteItem(org.exoplatform.ide.client.operation.deleteitem.DeleteItemEvent)
    */
   public void onDeleteItem(DeleteItemEvent event)
   {
      if (display != null)
      {
         return;
      }

      if (items.size() == 1 && items.get(0) instanceof ProjectModel && currentProject != null
         && currentProject.getId().equals(items.get(0).getId()))
      {
         Dialogs.getInstance().showInfo("Project must be closed before deleting.");
         return;
      }

      display = GWT.create(Display.class);
      IDE.getInstance().openView(display.asView());
      bindDisplay();
   }

   /**
    * Fills values on display and 
    */
   public void bindDisplay()
   {
      String message = "";

      if (items.size() == 1)
      {
         Item item = items.get(0);
         if (item instanceof ProjectModel)
         {
            message = IDE.IDE_LOCALIZATION_MESSAGES.deleteItemsAskDeleteProject(item.getName());
         }
         else
         {
            message = IDE.IDE_LOCALIZATION_MESSAGES.deleteItemsAskDeleteOneItem(item.getName());
         }
      }
      else
      {
         message = IDE.IDE_LOCALIZATION_MESSAGES.deleteItemsAskDeleteSeveralItems(items.size());
      }

      display.getPromptField().setValue(message);

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
            prepareToDelete();
         }
      });
   }

   private void prepareToDelete()
   {
      itemsToDelete = new ArrayList<Item>();
      itemsToDelete.addAll(items);
      deleteNextItem();
   }

   private void deleteNextItem()
   {
      if (itemsToDelete.size() == 0)
      {
         if (display != null)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }

         deleteItemsComplete();
         return;
      }

      final Item item = itemsToDelete.get(0);
      if (item instanceof FileModel)
      {
         if (openedFiles.get(item.getId()) != null)
         {
            FileModel file = openedFiles.get(item.getId());
            //TODO
            if (file.isContentChanged())
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
                     IDE.fireEvent(new ItemUnlockedEvent(item));
                     deleteItem(item);
                  }

                  @Override
                  protected void onFailure(Throwable exception)
                  {
                     IDE.fireEvent(new ExceptionThrownEvent(exception, UNLOCK_FAILURE_MSG));
                  }
               });
         }
         catch (RequestException e)
         {
            e.printStackTrace();
            IDE.fireEvent(new ExceptionThrownEvent(e, UNLOCK_FAILURE_MSG));
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
               itemsToDelete.remove(0);
               IDE.fireEvent(new ItemDeletedEvent(item));

               if (item instanceof FileModel)
               {
                  if (openedFiles.get(item.getId()) != null)
                  {
                     IDE.fireEvent(new EditorCloseFileEvent((FileModel)item, true));
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
                        IDE.fireEvent(new EditorCloseFileEvent(file, true));
                     }
                  }
               }
               lastDeletedItem = item;
               deleteNextItem();
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               IDE.fireEvent(new ExceptionThrownEvent(exception, DELETE_FILE_FAILURE_MESSAGE));
               IDE.getInstance().closeView(display.asView().getId());
            }
         });
      }
      catch (RequestException e)
      {
         e.printStackTrace();
         IDE.fireEvent(new ExceptionThrownEvent(e, DELETE_FILE_FAILURE_MESSAGE));
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

      FolderModel folder = null;
      if (lastDeletedItem instanceof ItemContext)
      {
         folder = ((ItemContext)lastDeletedItem).getParent();
      }

      if (folder != null)
      {
         IDE.fireEvent(new RefreshBrowserEvent(folder));
         IDE.fireEvent(new SelectItemEvent(folder.getId()));
      }
   }

   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
      }
   }

   @Override
   public void onProjectClosed(ProjectClosedEvent event)
   {
      currentProject = null;
   }

   @Override
   public void onProjectOpened(ProjectOpenedEvent event)
   {
      currentProject = event.getProject();
   }

}
