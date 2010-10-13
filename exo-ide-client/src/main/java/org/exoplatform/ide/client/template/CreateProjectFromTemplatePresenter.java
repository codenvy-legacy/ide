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
package org.exoplatform.ide.client.template;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.ide.client.model.template.FileTemplate;
import org.exoplatform.ide.client.model.template.ProjectTemplate;
import org.exoplatform.ide.client.model.template.Template;
import org.exoplatform.ide.client.model.util.ImageUtil;
import org.exoplatform.ide.client.module.navigation.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.module.vfs.api.File;
import org.exoplatform.ide.client.module.vfs.api.Folder;
import org.exoplatform.ide.client.module.vfs.api.Item;
import org.exoplatform.ide.client.module.vfs.api.VirtualFileSystem;
import org.exoplatform.ide.client.module.vfs.api.event.FileContentSavedEvent;
import org.exoplatform.ide.client.module.vfs.api.event.FileContentSavedHandler;
import org.exoplatform.ide.client.module.vfs.api.event.FolderCreatedEvent;
import org.exoplatform.ide.client.module.vfs.api.event.FolderCreatedHandler;
import org.exoplatform.ide.client.module.vfs.webdav.NodeTypeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class CreateProjectFromTemplatePresenter extends AbstractCreateFromTemplatePresenter<ProjectTemplate> 
implements FolderCreatedHandler, FileContentSavedHandler
{
   private List<FileTemplate> fileTemplateList = new ArrayList<FileTemplate>();
   
   private List<Folder> folderList = new ArrayList<Folder>();
   
   private List<File> fileList = new ArrayList<File>();
   
   private int itemsCreated = 0;
   
   private String baseHref;
   
   private Folder projectFolder;
   
   public CreateProjectFromTemplatePresenter(HandlerManager eventBus, List<Item> selectedItems, List<Template> templateList)
   {
      super(eventBus, selectedItems);
      
      this.templateList = new ArrayList<ProjectTemplate>();
      
      if (selectedItems != null && selectedItems.size() != 0)
      {
         Item item = selectedItems.get(0);

         baseHref = item.getHref();
         if (item instanceof File)
         {
            baseHref = baseHref.substring(0, baseHref.lastIndexOf("/") + 1);
         }
      }
      
      initFileAndProjectTemplates(templateList);
   }
   
   /**
    * @see org.exoplatform.ide.client.template.AbstractCreateFromTemplatePresenter#bindDisplay(org.exoplatform.ide.client.template.CreateFromTemplateDisplay)
    */
   @Override
   public void bindDisplay(CreateFromTemplateDisplay<ProjectTemplate>d)
   {
      super.bindDisplay(d);
      display.getNameField().setValue("Untitled project");
   }

   /**
    * @see org.exoplatform.ide.client.template.AbstractCreateFromTemplatePresenter#updateTemplateList(java.util.List)
    */
   @Override
   void updateTemplateList(List<Template> templates)
   {
      templateList.clear();
      fileTemplateList.clear();
      
      initFileAndProjectTemplates(templates);
      
      display.getTemplateListGrid().setValue(templateList);
      if (templateList != null && templateList.size() > 0)
      {
         display.selectLastTemplate();
      }
   }
   
   /**
    * Split list of templates on two lists: 
    * list of project templates and file templates.
    * 
    * @param templates
    */
   private void initFileAndProjectTemplates(List<Template> templates)
   {
      for (Template template : templates)
      {
         if (template instanceof ProjectTemplate)
         {
            templateList.add((ProjectTemplate)template);
         }
         else if (template instanceof FileTemplate)
         {
            fileTemplateList.add((FileTemplate)template);
         }
      }
   }
   
   private File createFileFromTemplate(FileTemplate fileTemplate, String href)
   {
      for (FileTemplate fTemplate : fileTemplateList)
      {
         if (fTemplate.getName().equals(fileTemplate.getName()))
         {
            String contentType = fTemplate.getMimeType();

            File newFile = new File(href + fileTemplate.getFileName());
            newFile.setContentType(contentType);
            newFile.setJcrContentNodeType(NodeTypeUtil.getContentNodeType(contentType));
            newFile.setIcon(ImageUtil.getIcon(contentType));
            newFile.setNewFile(true);
            newFile.setContent(fTemplate.getContent());
            
            return newFile;
         }
      }
      
      return null;
   }

   /**
    * @see org.exoplatform.ide.client.template.AbstractCreateFromTemplatePresenter#setNewInstanceName()
    */
   @Override
   void setNewInstanceName()
   {
      display.getNameField().setValue(selectedTemplate.getName());
   }

   /**
    * @see org.exoplatform.ide.client.template.AbstractCreateFromTemplatePresenter#submitTemplate()
    */
   @Override
   void submitTemplate()
   {
      String projectName = display.getNameField().getValue();

      if ("".equals(projectName.trim()))
      {
         Dialogs.getInstance().showError("You must enter project name the first!");
         return;
      }
      
      if (baseHref == null)
      {
         Dialogs.getInstance().showError("Select root folder for project!");
         return;
      }
      
      build(selectedTemplate.getChildren(), baseHref + projectName + "/");
      projectFolder = new Folder(baseHref + projectName + "/");
      
      handlers.addHandler(FolderCreatedEvent.TYPE, this);
      
      VirtualFileSystem.getInstance().createFolder(projectFolder);
   }
   
   private void build(List<Template>templates, String href)
   {
      if (templates == null || templates.size() == 0)
      {
         return;
      }
      
      for (Template template : templates)
      {
         if (template instanceof ProjectTemplate)
         {
            ProjectTemplate projectTemplate = (ProjectTemplate)template;
            
            folderList.add(new Folder(href + projectTemplate.getName() + "/"));
            build(projectTemplate.getChildren(), href + projectTemplate.getName() + "/");
         }
         else if (template instanceof FileTemplate)
         {
            FileTemplate fileTemplate = (FileTemplate)template;
            File file = createFileFromTemplate(fileTemplate, href);
            if (file != null)
            {
               fileList.add(file);
            }
         }
      }
   }
   
   /**
    * @see org.exoplatform.ide.client.module.vfs.api.event.FolderCreatedHandler#onFolderCreated(org.exoplatform.ide.client.module.vfs.api.event.FolderCreatedEvent)
    */
   public void onFolderCreated(FolderCreatedEvent event)
   {
      if (itemsCreated < folderList.size())
      {
         VirtualFileSystem.getInstance().createFolder(folderList.get(itemsCreated));
         itemsCreated++;
         return;
      }
      handlers.removeHandler(FolderCreatedEvent.TYPE);
      if (fileList.size() == 0)
      {
         eventBus.fireEvent(new RefreshBrowserEvent(new Folder(baseHref), projectFolder));
         display.closeForm();
         return;
      }
      
      handlers.addHandler(FileContentSavedEvent.TYPE, this);
      VirtualFileSystem.getInstance().saveContent(fileList.get(0));
      itemsCreated = 1;
   }

   /**
    * @see org.exoplatform.ide.client.module.vfs.api.event.FileContentSavedHandler#onFileContentSaved(org.exoplatform.ide.client.module.vfs.api.event.FileContentSavedEvent)
    */
   public void onFileContentSaved(FileContentSavedEvent event)
   {
      if (itemsCreated < fileList.size())
      {
         VirtualFileSystem.getInstance().saveContent(fileList.get(itemsCreated));
         itemsCreated++;
         return;
      }
      handlers.removeHandler(FileContentSavedEvent.TYPE);
      eventBus.fireEvent(new RefreshBrowserEvent(new Folder(baseHref), projectFolder));
      display.closeForm();
   }


}
