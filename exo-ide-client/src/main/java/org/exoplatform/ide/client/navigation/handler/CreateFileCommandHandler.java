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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorOpenFileEvent;
import org.exoplatform.ide.client.framework.module.FileType;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.model.template.FileTemplates;
import org.exoplatform.ide.client.navigation.event.CreateNewFileEvent;
import org.exoplatform.ide.client.navigation.event.CreateNewFileHandler;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ItemContext;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.Project;

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

   private static final String UNTITLED_FILE_NAME = IDE.NAVIGATION_CONSTANT.createFileUntitledFileName();

   public CreateFileCommandHandler()
   {
      IDE.addHandler(EditorFileOpenedEvent.TYPE, this);
      IDE.addHandler(EditorFileClosedEvent.TYPE, this);
      IDE.addHandler(ItemsSelectedEvent.TYPE, this);

      IDE.addHandler(CreateNewFileEvent.TYPE, this);
   }

   public void onItemsSelected(ItemsSelectedEvent event)
   {
      selectedItems = event.getSelectedItems();
   }

   public void onCreateNewFile(CreateNewFileEvent event)
   {
      FileType fileType = IDE.getInstance().getFileTypeRegistry().getFileType(event.getMimeType());
      if (fileType == null)
      {
         return;
      }
      
      String extension = fileType.getExtension();

      String content = FileTemplates.getTemplateFor(event.getMimeType());

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

      FileModel newFile = new FileModel(fileName, event.getMimeType(), content, parent);
      newFile.setContentChanged(true);
      newFile.setId(fileName);
      newFile.setProject(project);

      IDE.fireEvent(new EditorOpenFileEvent(newFile));
   }

   public void onEditorFileOpened(EditorFileOpenedEvent event)
   {
      openedFiles = event.getOpenedFiles();
   }

   public void onEditorFileClosed(EditorFileClosedEvent event)
   {
      openedFiles = event.getOpenedFiles();
   }

}
