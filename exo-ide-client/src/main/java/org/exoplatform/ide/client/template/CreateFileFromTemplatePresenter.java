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
package org.exoplatform.ide.client.template;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.commons.dialogs.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.ide.client.framework.event.OpenFileEvent;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.framework.vfs.NodeTypeUtil;
import org.exoplatform.ide.client.model.template.FileTemplate;
import org.exoplatform.ide.client.model.template.FolderTemplate;
import org.exoplatform.ide.client.model.template.ProjectTemplate;
import org.exoplatform.ide.client.model.template.Template;
import org.exoplatform.ide.client.model.util.IDEMimeTypes;
import org.exoplatform.ide.client.model.util.ImageUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Presenter for form "Create file from template"
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
   
   private Map<String, File> openedFiles = new HashMap<String, File>();
   
   public CreateFileFromTemplatePresenter(HandlerManager eventBus, List<Item> selectedItems, List<Template> templateList,
      Map<String, File> openedFiles)
   {
      super(eventBus, selectedItems);
      
      this.templateList = new ArrayList<FileTemplate>();
      this.openedFiles = openedFiles;
      
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
   protected void updateTemplateList(List<Template> templates)
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
   protected void setNewInstanceName()
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
   public void submitTemplate()
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
      
      fileName = checkFileName(baseHref, fileName);

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
    * Check, is there is the same file name in opened file.
    * <p> 
    * If file name is unique, return the same name.
    * <p> 
    * If there is file with such name in opened files,
    * return new file name: old name and index at the end of name.
    * 
    * @param href - file href
    * @param proposedName - proposed name for new file
    * @return {@link String}
    */
   private String checkFileName(String href, String proposedName)
   {
      if (openedFiles == null || openedFiles.isEmpty())
      {
         return proposedName;
      }
      
      final String nameWithoutExt = proposedName.substring(0, proposedName.lastIndexOf("."));
      String extension = proposedName.substring(proposedName.lastIndexOf(".") + 1, proposedName.length());
      int index = 1;
      while (openedFiles.get(href + proposedName) != null)
      {
         proposedName = nameWithoutExt + " " + index + "." + extension;
         index++;
      }
      
      return proposedName;
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
         deleteOneTemplate(fileTemplate);
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
               deleteOneTemplate(fileTemplate);
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
