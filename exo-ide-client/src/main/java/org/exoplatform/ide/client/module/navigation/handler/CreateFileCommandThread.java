/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.client.module.navigation.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.editor.api.Editor;
import org.exoplatform.gwtframework.editor.api.EditorNotFoundException;
import org.exoplatform.ide.client.editor.EditorUtil;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorOpenFileEvent;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.model.template.FileTemplate;
import org.exoplatform.ide.client.model.template.FileTemplates;
import org.exoplatform.ide.client.model.template.ProjectTemplate;
import org.exoplatform.ide.client.model.template.TemplateList;
import org.exoplatform.ide.client.model.template.TemplateService;
import org.exoplatform.ide.client.model.template.event.TemplateListReceivedEvent;
import org.exoplatform.ide.client.model.template.event.TemplateListReceivedHandler;
import org.exoplatform.ide.client.model.util.IDEMimeTypes;
import org.exoplatform.ide.client.model.util.ImageUtil;
import org.exoplatform.ide.client.module.navigation.event.newitem.CreateFileFromTemplateEvent;
import org.exoplatform.ide.client.module.navigation.event.newitem.CreateFileFromTemplateHandler;
import org.exoplatform.ide.client.module.navigation.event.newitem.CreateNewFileEvent;
import org.exoplatform.ide.client.module.navigation.event.newitem.CreateNewFileHandler;
import org.exoplatform.ide.client.module.navigation.event.newitem.CreateProjectFromTemplateEvent;
import org.exoplatform.ide.client.module.navigation.event.newitem.CreateProjectFromTemplateHandler;
import org.exoplatform.ide.client.module.navigation.event.selection.ItemsSelectedEvent;
import org.exoplatform.ide.client.module.navigation.event.selection.ItemsSelectedHandler;
import org.exoplatform.ide.client.module.vfs.api.File;
import org.exoplatform.ide.client.module.vfs.api.Item;
import org.exoplatform.ide.client.module.vfs.webdav.NodeTypeUtil;
import org.exoplatform.ide.client.template.CreateFileFromTemplateForm;
import org.exoplatform.ide.client.template.CreateFileFromTemplatePresenter;
import org.exoplatform.ide.client.template.CreateFromTemplateDisplay;
import org.exoplatform.ide.client.template.CreateProjectFromTemplateForm;
import org.exoplatform.ide.client.template.CreateProjectFromTemplatePresenter;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class CreateFileCommandThread implements CreateNewFileHandler, CreateFileFromTemplateHandler,
   TemplateListReceivedHandler, ItemsSelectedHandler, EditorFileOpenedHandler,
   EditorFileClosedHandler, ApplicationSettingsReceivedHandler, CreateProjectFromTemplateHandler
{
   private HandlerManager eventBus;

   private Handlers handlers;

   private List<Item> selectedItems = new ArrayList<Item>();

   private Map<String, File> openedFiles = new HashMap<String, File>();

   private ApplicationSettings applicationSettings;
   
   private boolean createFile;

   public CreateFileCommandThread(HandlerManager eventBus)
   {
      this.eventBus = eventBus;

      handlers = new Handlers(eventBus);

      eventBus.addHandler(EditorFileOpenedEvent.TYPE, this);
      eventBus.addHandler(EditorFileClosedEvent.TYPE, this);
      eventBus.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
      eventBus.addHandler(CreateNewFileEvent.TYPE, this);
      eventBus.addHandler(CreateFileFromTemplateEvent.TYPE, this);
      eventBus.addHandler(CreateProjectFromTemplateEvent.TYPE, this);
      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);      
   }

   public void onItemsSelected(ItemsSelectedEvent event)
   {
      selectedItems = event.getSelectedItems();
   }

   public void onCreateNewFile(CreateNewFileEvent event)
   {
      String extension = IDEMimeTypes.getExtensionsMap().get(event.getMimeType());
      
      String href;
      
      if (selectedItems != null && selectedItems.size() != 0)
      {
         Item item = selectedItems.get(0);

         href = item.getHref();
         if (item instanceof File)
         {
            href = href.substring(0, href.lastIndexOf("/") + 1);
         }
      }
      else
      {
         href = "";
      }

      String content = FileTemplates.getTemplateFor(event.getMimeType());

      String fileName = "Untitled file." + extension;
      int index = 1;
      while (openedFiles.get(href + fileName) != null)
      {
         fileName = "Untitled file " + index + "." + extension;
         index++;
      }

      File newFile = new File(href + fileName);
      newFile.setContentType(event.getMimeType());
      newFile.setJcrContentNodeType(NodeTypeUtil.getContentNodeType(event.getMimeType()));
      newFile.setIcon(ImageUtil.getIcon(event.getMimeType()));
      newFile.setNewFile(true);
      newFile.setContent(content);
      newFile.setContentChanged(true);

      Map<String, String> defaultEditors = applicationSettings.getValueAsMap("default-editors");
      if (defaultEditors == null)
      {
         defaultEditors = new LinkedHashMap<String, String>();
      }

      try
      {
         String defaultEditorDescription = defaultEditors.get(event.getMimeType());
         Editor editor = EditorUtil.getEditor(event.getMimeType(), defaultEditorDescription);
         eventBus.fireEvent(new EditorOpenFileEvent(newFile, editor));
      }
      catch (EditorNotFoundException e)
      {
         Dialogs.getInstance().showError("Can't find editor for type <b>" + event.getMimeType() + "</b>");
      }
   }

   public void onCreateFileFromTemplate(CreateFileFromTemplateEvent event)
   {
      handlers.addHandler(TemplateListReceivedEvent.TYPE, this);
      createFile = true;
      TemplateService.getInstance().getTemplates();
   }

   /**
    * @see org.exoplatform.ide.client.model.template.event.TemplateListReceivedHandler#onTemplateListReceived(org.exoplatform.ide.client.model.template.event.TemplateListReceivedEvent)
    */
   public void onTemplateListReceived(TemplateListReceivedEvent event)
   {
      handlers.removeHandler(TemplateListReceivedEvent.TYPE);
      TemplateList templateList = event.getTemplateList();
      if (createFile)
      {
         CreateFileFromTemplatePresenter createFilePresenter =
            new CreateFileFromTemplatePresenter(eventBus, selectedItems, templateList.getTemplates());
         CreateFromTemplateDisplay<FileTemplate> createFileDisplay =
            new CreateFileFromTemplateForm(eventBus, templateList.getTemplates(), createFilePresenter);
         createFilePresenter.bindDisplay(createFileDisplay);
      }
      else
      {
         CreateProjectFromTemplatePresenter createProjectPresenter =
            new CreateProjectFromTemplatePresenter(eventBus, selectedItems, templateList.getTemplates());
         CreateFromTemplateDisplay<ProjectTemplate> createProjectDisplay =
            new CreateProjectFromTemplateForm(eventBus, templateList.getTemplates(), createProjectPresenter);
         createProjectPresenter.bindDisplay(createProjectDisplay);
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

   /**
    * @see org.exoplatform.ide.client.module.navigation.event.newitem.CreateProjectFromTemplateHandler#onCreateProjectFromTemplate(org.exoplatform.ide.client.module.navigation.event.newitem.CreateProjectFromTemplateEvent)
    */
   public void onCreateProjectFromTemplate(CreateProjectFromTemplateEvent event)
   {
      try
      {
         handlers.removeHandler(TemplateListReceivedEvent.TYPE);
      }
      catch (Exception e)
      {
         // TODO: handle exception
      }
      handlers.addHandler(TemplateListReceivedEvent.TYPE, this);
      createFile = false;
      TemplateService.getInstance().getTemplates();
   }


}
