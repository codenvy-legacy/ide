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
package org.exoplatform.ide.client.navigation.handler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.dialogs.ValueCallback;
import org.exoplatform.ide.client.dialogs.ValueDiscardCallback;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.event.FileSavedEvent;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.event.SaveFileAsEvent;
import org.exoplatform.ide.client.framework.event.SaveFileAsHandler;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.FileUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ItemContext;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.http.client.RequestException;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class SaveFileAsCommandHandler implements SaveFileAsHandler, ItemsSelectedHandler,
   EditorActiveFileChangedHandler, ApplicationSettingsReceivedHandler
{

   private String sourceId;

   private List<Item> selectedItems = new ArrayList<Item>();

   private FileModel activeFile;

   private Map<String, String> lockTokens;

   private static final String PREFIX = IDE.NAVIGATION_CONSTANT.saveFileAsNewFileNamePrefix();

   private static final String SAVE_AS_DIALOG_TITLE = IDE.NAVIGATION_CONSTANT.saveFileAsDialogTitle();

   private static final String SAVE_AS_DIALOG_ENTER_NEW_NAME = IDE.NAVIGATION_CONSTANT.saveFileAsDialogEnterNewName();

   private static final String SAVE_AS_DIALOG_DO_YOU_WANT_TO_SAVE = IDE.NAVIGATION_CONSTANT
      .saveFileAsDialogDoYouWantToSave();

   /**
    * Event to be fired after pressing No button in ask dialog.
    */
   private GwtEvent<?> eventFiredOnNoButtonPressed;

   /**
    * Event to be fired after pressing Cancel button in ask dialog.
    */
   private GwtEvent<?> eventFiredOnCancelButtonPressed;

   /**
    * File to be saved.
    */
   private FileModel fileToSave;

   public SaveFileAsCommandHandler()
   {
      IDE.addHandler(SaveFileAsEvent.TYPE, this);
      IDE.addHandler(ItemsSelectedEvent.TYPE, this);
      IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      IDE.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
   }

   /**
    * Add handlers
    * Open Save As Dialog
    * 
    * @see org.exoplatform.ide.client.module.navigation.event.SaveFileAsHandler#onSaveFileAs(org.exoplatform.ide.client.module.navigation.event.SaveFileAsEvent)
    */
   public void onSaveFileAs(SaveFileAsEvent event)
   {
      if (selectedItems == null || selectedItems.size() == 0)
      {
         Dialogs.getInstance().showInfo(IDE.ERRORS_CONSTANT.saveFileAsTargetNotSelected());
         return;
      }

      FileModel file = event.getFile() != null ? event.getFile() : activeFile;

      eventFiredOnCancelButtonPressed = event.getEventFiredOnCancel();

      eventFiredOnNoButtonPressed = event.getEventFiredOnNo();

      fileToSave = file;
      
      sourceId = file.getId();

      askForNewFileName(event.getDialogType());
   }

   /**
    * Open Save As Dialog 
    * 
    * @param file
    */
   private void askForNewFileName(SaveFileAsEvent.SaveDialogType type)
   {
      final String newFileName = !fileToSave.isPersisted() ? fileToSave.getName() : PREFIX + " " + fileToSave.getName();
      //      sourceHref = fileToSave.getId();

      if (type.equals(SaveFileAsEvent.SaveDialogType.YES_CANCEL))
      {
         org.exoplatform.ide.client.dialogs.AskForValueDialog.getInstance().ask(SAVE_AS_DIALOG_TITLE,
            SAVE_AS_DIALOG_ENTER_NEW_NAME, newFileName, 400, fileNameEnteredCallback);
      }
      else
      {
         org.exoplatform.ide.client.dialogs.AskForValueDialog.getInstance().ask(SAVE_AS_DIALOG_TITLE,
            SAVE_AS_DIALOG_DO_YOU_WANT_TO_SAVE, newFileName, 400, fileNameEnteredCallback, noButtonSelectedCallback);
      }
   }

   private ValueCallback fileNameEnteredCallback = new ValueCallback()
   {
      @Override
      public void execute(String value)
      {
         if (value == null)
         {
            if (eventFiredOnCancelButtonPressed != null)
            {
               IDE.fireEvent(eventFiredOnCancelButtonPressed);
            }

            return;
         }

         saveFileAs(fileToSave, value);
      }
   };

   ValueDiscardCallback noButtonSelectedCallback = new ValueDiscardCallback()
   {
      @Override
      public void discard()
      {
         if (eventFiredOnNoButtonPressed != null)
         {
            IDE.fireEvent(eventFiredOnNoButtonPressed);
         }
      }
   };

   private void saveFileAs(FileModel file, String name)
   {
      FolderModel folderToSave = getFilePath(selectedItems.get(0));
      FileModel newFile = new FileModel(name, file.getMimeType(), file.getContent(), folderToSave);

      if (file.isPersisted())
      {
         newFile.getProperties().addAll(file.getProperties());
      }

      try
      {
         VirtualFileSystem.getInstance().createFile(folderToSave,
            new AsyncRequestCallback<FileModel>(new FileUnmarshaller(newFile))
            {
               @Override
               protected void onSuccess(FileModel result)
               {
                  IDE.fireEvent(new FileSavedEvent(result, sourceId));
                  refreshBrowser(result.getParent());
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  IDE.fireEvent(new ExceptionThrownEvent(exception));
               }
            });
      }
      catch (RequestException e)
      {
         e.printStackTrace();
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
      //      saveContent(newFile, null, new FileContentSaveCallback()
      //      {
      //         @Override
      //         protected void onSuccess(FileData result)
      //         {
      //            File file = result.getFile();
      //            
      //            if (file.isPropertiesChanged())
      //            {
      //               saveFileProperties(file, lockTokens.get(file.getHref()));
      //            }
      //            else
      //            {
      //               getProperties(file);
      //            }            
      //         }
      //      });

   }

   //   private void saveFileProperties(FileModel file, String lockToken)
   //   {
   //      VirtualFileSystem.getInstance().saveProperties(file, lockToken, new ItemPropertiesCallback()
   //      {
   //         @Override
   //         protected void onSuccess(Item result)
   //         {
   //            getProperties(result);
   //         }
   //      });
   //   }

   private FolderModel getFilePath(Item item)
   {
      if (item instanceof FileModel)
      {
         return ((FileModel)item).getParent();
      }
      else if (item instanceof FolderModel)
      {
         return (FolderModel)item;
      }
      else if (item instanceof ProjectModel)
      {
         return new FolderModel((Folder)item);
      }
      return new FolderModel(item.getName(), ((ItemContext)item).getParent(), item.getLinks());
   }

   //   private void getProperties(Item item)
   //   {
   //      VirtualFileSystem.getInstance().getProperties(item, new ItemPropertiesCallback()
   //      {
   //         @Override
   //         protected void onSuccess(Item result)
   //         {
   //            File savedFile = (File)result;
   //            savedFile.setNewFile(false);
   //            savedFile.setContentChanged(false);
   //           
   //         }
   //      });
   //   }

   private void refreshBrowser(FolderModel folder)
   {
      IDE.fireEvent(new RefreshBrowserEvent(folder));
   }

   public void onItemsSelected(ItemsSelectedEvent event)
   {
      selectedItems = event.getSelectedItems();
   }

   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      activeFile = event.getFile();
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

      lockTokens = event.getApplicationSettings().getValueAsMap("lock-tokens");
   }

}
