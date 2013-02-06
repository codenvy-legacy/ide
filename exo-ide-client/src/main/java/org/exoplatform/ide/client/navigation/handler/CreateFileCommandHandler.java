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

import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.gwtframework.ui.client.dialog.StringValueReceivedHandler;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorOpenFileEvent;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.FileType;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.template.FileTemplates;
import org.exoplatform.ide.client.navigation.event.CreateNewFileEvent;
import org.exoplatform.ide.client.navigation.event.CreateNewFileHandler;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.FileUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ItemContext;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 */
public class CreateFileCommandHandler implements CreateNewFileHandler, ItemsSelectedHandler, EditorFileOpenedHandler,
   EditorFileClosedHandler
{

   private List<Item> selectedItems = new ArrayList<Item>();

   private Map<String, FileModel> openedFiles = new HashMap<String, FileModel>();

   private static final String DIALOG_TITLE = IDE.NAVIGATION_CONSTANT.createNewFileDialogTitle();

   private static final String NAME_FIELD_TITLE = IDE.NAVIGATION_CONSTANT.createNewFileDialogNameFieldTitle();

   private static final String UNTITLED_FILE_NAME = IDE.NAVIGATION_CONSTANT.createFileUntitledFileName();

   public CreateFileCommandHandler()
   {
      IDE.addHandler(EditorFileOpenedEvent.TYPE, this);
      IDE.addHandler(EditorFileClosedEvent.TYPE, this);
      IDE.addHandler(ItemsSelectedEvent.TYPE, this);

      IDE.addHandler(CreateNewFileEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent)
    */
   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      selectedItems = event.getSelectedItems();
   }

   /**
    * @see org.exoplatform.ide.client.navigation.event.CreateNewFileHandler#onCreateNewFile(org.exoplatform.ide.client.navigation.event.CreateNewFileEvent)
    */
   @Override
   public void onCreateNewFile(CreateNewFileEvent event)
   {
      FileType fileType = IDE.getInstance().getFileTypeRegistry().getFileType(event.getMimeType());
      if (fileType == null)
      {
         return;
      }

      String extension = fileType.getExtension();
      String fileName = UNTITLED_FILE_NAME + "." + extension;

      int index = 1;
      Set<String> openedFilesNames = new HashSet<String>();
      for (FileModel m : openedFiles.values())
      {
         openedFilesNames.add(m.getName());
      }
      while (openedFilesNames.contains(fileName))
      {
         fileName = UNTITLED_FILE_NAME + " " + index + "." + extension;
         index++;
      }

      askForFileName(fileName, event.getMimeType());
   }

   /**
    * Ask user for a new file name.
    * 
    * @param fileName default name
    * @param mimeType file MIME-type
    */
   private void askForFileName(final String fileName, final String mimeType)
   {
      Dialogs.getInstance().askForValue(DIALOG_TITLE, NAME_FIELD_TITLE, fileName, new StringValueReceivedHandler()
      {

         @Override
         public void stringValueReceived(String value)
         {
            if (value != null)
            {
               createAndOpenFile(value, mimeType);
            }
         }
      });
   }

   /**
    * Creates a new file with the given name, MIME-type and default content.
    * File's content will be opened in a new editor after creation.
    * 
    * @param fileName a new file name
    * @param mimeType a new file MIME-type
    */
   private void createAndOpenFile(String fileName, String mimeType)
   {
      FolderModel parent = new FolderModel();
      ProjectModel project = null;
      if (selectedItems != null && selectedItems.size() != 0)
      {
         Item item = selectedItems.get(0);

         if (item instanceof FileModel)
         {
            parent = ((FileModel)item).getParent();
         }
         else if (item instanceof FolderModel)
         {
            parent = (FolderModel)item;
         }
         else if (item instanceof ProjectModel)
         {
            parent = new FolderModel((Project)item);
         }

         if (item instanceof ItemContext)
         {
            project = ((ItemContext)item).getProject();
         }
      }

      String content = FileTemplates.getTemplateFor(mimeType);
      FileModel newFile = new FileModel(fileName, mimeType, content, parent);
      newFile.setId(fileName);
      newFile.setProject(project);

      try
      {
         VirtualFileSystem.getInstance().createFile(newFile.getParent(),
            new AsyncRequestCallback<FileModel>(new FileUnmarshaller(newFile))
            {
               @Override
               protected void onSuccess(FileModel result)
               {
                  IDE.fireEvent(new EditorOpenFileEvent(result));
                  IDE.fireEvent(new RefreshBrowserEvent(result.getParent(), result));
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
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler#onEditorFileOpened(org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent)
    */
   @Override
   public void onEditorFileOpened(EditorFileOpenedEvent event)
   {
      openedFiles = event.getOpenedFiles();
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler#onEditorFileClosed(org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent)
    */
   @Override
   public void onEditorFileClosed(EditorFileClosedEvent event)
   {
      openedFiles = event.getOpenedFiles();
   }

}
