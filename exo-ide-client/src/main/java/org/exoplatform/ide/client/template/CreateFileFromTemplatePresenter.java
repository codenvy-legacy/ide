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

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.commons.dialogs.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.ide.client.framework.event.OpenFileEvent;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.Folder;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.framework.vfs.NodeTypeUtil;
import org.exoplatform.ide.client.model.template.FileTemplate;
import org.exoplatform.ide.client.model.template.FolderTemplate;
import org.exoplatform.ide.client.model.template.ProjectTemplate;
import org.exoplatform.ide.client.model.template.Template;
import org.exoplatform.ide.client.model.template.TemplateService;
import org.exoplatform.ide.client.model.util.IDEMimeTypes;
import org.exoplatform.ide.client.model.util.ImageUtil;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class CreateFileFromTemplatePresenter extends AbstractCreateFromTemplatePresenter<FileTemplate>
{
   private static final String UNTITLED_FILE = "Untitled file";

   private String previousExtension;
   
   private String baseHref;
   
   private List<ProjectTemplate> projectTemplateList;
   
   private List<ProjectTemplate> usedProjectTemplates;
   
   public CreateFileFromTemplatePresenter(HandlerManager eventBus, List<Item> selectedItems, List<Template> templateList)
   {
      super(eventBus, selectedItems);
      
      this.templateList = new ArrayList<FileTemplate>();
      
      projectTemplateList = new ArrayList<ProjectTemplate>();
      
      if (selectedItems != null && selectedItems.size() != 0)
      {
         Item item = selectedItems.get(0);

         baseHref = item.getHref();
         if (item instanceof File)
         {
            baseHref = baseHref.substring(0, baseHref.lastIndexOf("/") + 1);
         }
      }
      
      updateTemplateList(templateList);
   }
   
   /**
    * @see org.exoplatform.ide.client.template.AbstractCreateFromTemplatePresenter#updateTemplateList(java.util.List)
    */
   @Override
   void updateTemplateList(List<Template> templates)
   {
      templateList.clear();
      projectTemplateList.clear();
      
      for (Template template : templates)
      {
         if (template instanceof FileTemplate)
         {
            templateList.add((FileTemplate)template);
         }
         else if (template instanceof ProjectTemplate)
         {
            projectTemplateList.add((ProjectTemplate)template);
         }
      }
   }

   /**
    * @see org.exoplatform.ide.client.template.AbstractCreateFromTemplatePresenter#setNewInstanceName()
    */
   @Override
   void setNewInstanceName()
   {
      FileTemplate selectedTemplate = (FileTemplate)selectedTemplates.get(0);
      String extension = IDEMimeTypes.getExtensionsMap().get(selectedTemplate.getMimeType());
      if (previousExtension != null)
      {
         String name = display.getNameField().getValue();
         if (name == null || name.length() == 0)
         {
            name = UNTITLED_FILE;
         }
         if (name.endsWith("." + previousExtension))
         {
            name = name.substring(0, name.length() - previousExtension.length() - 1);
         }
         name += "." + extension;
         display.getNameField().setValue(name);
      }
      else
      {
         String value = display.getNameField().getValue();
         if (value == null || value.length() == 0)
         {
            value = UNTITLED_FILE;
         }
         display.getNameField().setValue(value + "." + extension);
      }
      previousExtension = extension;
   }

   /**
    * @see org.exoplatform.ide.client.template.AbstractCreateFromTemplatePresenter#submitTemplate()
    */
   @Override
   void submitTemplate()
   {
      String fileName = display.getNameField().getValue();

      if ("".equals(fileName.trim()))
      {
         Dialogs.getInstance().showError("You must enter file name the first!");
         return;
      }
      
      if (baseHref == null)
      {
         baseHref = "";
      }

      FileTemplate selectedTemplate = (FileTemplate)selectedTemplates.get(0);
      
      String contentType = selectedTemplate.getMimeType();

      File newFile = new File(baseHref + fileName);
      newFile.setContentType(contentType);
      newFile.setJcrContentNodeType(NodeTypeUtil.getContentNodeType(contentType));
      newFile.setIcon(ImageUtil.getIcon(contentType));
      newFile.setNewFile(true);
      newFile.setContent(selectedTemplate.getContent());
      newFile.setContentChanged(true);

      eventBus.fireEvent(new OpenFileEvent(newFile));

      display.closeForm();
   }
   
   /**
    * @see org.exoplatform.ide.client.template.AbstractCreateFromTemplatePresenter#deleteOneTemplate(org.exoplatform.ide.client.model.template.Template)
    */
   @Override
   protected void deleteOneTemplate(final FileTemplate fileTemplate)
   {
      usedProjectTemplates = new ArrayList<ProjectTemplate>();
      
      for (ProjectTemplate projectTemplate : projectTemplateList)
      {
         if (isPresentInProjectTemplate(projectTemplate, fileTemplate))
         {
            usedProjectTemplates.add(projectTemplate);
         }
      }
      
      if (usedProjectTemplates.size() == 0)
      {
         TemplateService.getInstance().deleteTemplate(fileTemplate);
         return;
      }
      
      String msg = "File template <b>" + fileTemplate.getName() + "</b> is used in <b>";
      
      for (ProjectTemplate template : usedProjectTemplates)
      {
         msg += template.getName() + ", ";
      }
      
      msg = msg.substring(0, msg.length() - 2);
      msg += "</b> project template(s). Are your sure you want to delete this template?";
      
      Dialogs.getInstance().ask("IDE", msg, new BooleanValueReceivedHandler()
      {
         public void booleanValueReceived(Boolean value)
         {
            if (value == null)
            {
               selectedTemplates.remove(fileTemplate);
               deleteNextTemplate();
               return;
            }
            if (value)
            {
               TemplateService.getInstance().deleteTemplate(fileTemplate);
            }
            else
            {
               selectedTemplates.remove(fileTemplate);
               deleteNextTemplate();
            }
         }
      });
   }
   
   private boolean isPresentInProjectTemplate(FolderTemplate projectTemplate, FileTemplate fileTemplate)
   {
      if (projectTemplate.getChildren() == null)
      {
         return false;
      }
      for (Template template : projectTemplate.getChildren())
      {
         if (template instanceof FileTemplate && template.getName().equals(fileTemplate.getName()))
         {
            return true;
         }
         else if (template instanceof FolderTemplate)
         {
            return isPresentInProjectTemplate((FolderTemplate)template, fileTemplate);
         }
      }
      return false;
   }

}
