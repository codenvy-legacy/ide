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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.editor.EditorFactory;
import org.exoplatform.ide.client.framework.editor.EditorNotFoundException;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorOpenFileEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.model.template.FileTemplates;
import org.exoplatform.ide.client.model.util.IDEMimeTypes;
import org.exoplatform.ide.client.navigation.event.CreateNewFileEvent;
import org.exoplatform.ide.client.navigation.event.CreateNewFileHandler;
import org.exoplatform.ide.editor.api.EditorProducer;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ItemContext;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.Project;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class CreateFileCommandHandler implements CreateNewFileHandler, ItemsSelectedHandler, EditorFileOpenedHandler,
   EditorFileClosedHandler, ApplicationSettingsReceivedHandler
{

   private List<Item> selectedItems = new ArrayList<Item>();

   private Map<String, FileModel> openedFiles = new HashMap<String, FileModel>();

   private ApplicationSettings applicationSettings;

   private static final String UNTITLED_FILE_NAME = IDE.NAVIGATION_CONSTANT.createFileUntitledFileName();

   public CreateFileCommandHandler()
   {
      IDE.addHandler(EditorFileOpenedEvent.TYPE, this);
      IDE.addHandler(EditorFileClosedEvent.TYPE, this);
      IDE.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
      IDE.addHandler(ItemsSelectedEvent.TYPE, this);

      IDE.addHandler(CreateNewFileEvent.TYPE, this);
   }

   public void onItemsSelected(ItemsSelectedEvent event)
   {
      selectedItems = event.getSelectedItems();
   }

   public void onCreateNewFile(CreateNewFileEvent event)
   {
      String extension = IDEMimeTypes.getExtensionsMap().get(event.getMimeType());

      String content = FileTemplates.getTemplateFor(event.getMimeType());

      String fileName = UNTITLED_FILE_NAME + "." + extension;
      int index = 1;
      for (FileModel m : openedFiles.values())
      {
         if (m.getName().equals(fileName))
         {
            fileName = UNTITLED_FILE_NAME + " " + index + "." + extension;
            index++;
         }
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
      
      Map<String, String> defaultEditors = applicationSettings.getValueAsMap("default-editors");
      if (defaultEditors == null)
      {
         defaultEditors = new LinkedHashMap<String, String>();
      }

      try
      {
         String defaultEditorDescription = defaultEditors.get(event.getMimeType());
         //         Editor editor = EditorUtil.getEditor(event.getMimeType(), defaultEditorDescription);
         EditorProducer producer = EditorFactory.getEditorProducer(event.getMimeType(), defaultEditorDescription);
         IDE.fireEvent(new EditorOpenFileEvent(newFile, producer));
      }
      catch (EditorNotFoundException e)
      {
         Dialogs.getInstance().showError(
            IDE.IDE_LOCALIZATION_MESSAGES.createFileCantFindEditorForType(event.getMimeType()));
      }
   }

   public void onEditorFileOpened(EditorFileOpenedEvent event)
   {
      openedFiles = event.getOpenedFiles();
   }

   public void onEditorFileClosed(EditorFileClosedEvent event)
   {
      openedFiles = event.getOpenedFiles();
   }

   public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event)
   {
      applicationSettings = event.getApplicationSettings();
   }

}
