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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.event.OpenFileEvent;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.framework.vfs.NodeTypeUtil;
import org.exoplatform.ide.client.model.template.FileTemplate;
import org.exoplatform.ide.client.model.template.FileTemplateList;
import org.exoplatform.ide.client.model.template.FolderTemplate;
import org.exoplatform.ide.client.model.template.ProjectTemplate;
import org.exoplatform.ide.client.model.template.ProjectTemplateList;
import org.exoplatform.ide.client.model.template.Template;
import org.exoplatform.ide.client.model.template.TemplateService;
import org.exoplatform.ide.client.model.util.IDEMimeTypes;
import org.exoplatform.ide.client.model.util.ImageUtil;
import org.exoplatform.ide.client.project.CreateProjectTemplateForm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Presenter for form "Create file from template".
 * Used to add file template to project template in {@link CreateProjectTemplateForm}
 * 
 * TODO: Remove this presenter and refactore {@link org.exoplatform.ide.client.navigation.template.CreateFileFromTemplatePresenter}
 * to reuse code.
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class CreateFileFromTemplatePresenter
{
   private static final String UNTITLED_FILE = IDE.NAVIGATION_CONSTANT.createFileUntitledFileName();
   
   private static final String ENTER_FILE_NAME_FIRST = IDE.TEMPLATE_CONSTANT.createFileFromTemplateEnterFileNameFirst();

   private String previousExtension;
   
   private String baseHref;
   
   private List<ProjectTemplate> projectTemplateList;
   
   private List<ProjectTemplate> usedProjectTemplates;
   
   private Map<String, File> openedFiles = new HashMap<String, File>();
   
   protected HandlerManager eventBus;

   protected CreateFromTemplateDisplay<FileTemplate> display;

   /**
    * The list of templates, that selected.
    */
   protected List<FileTemplate> selectedTemplates;

   /**
    * The list of templates to display.
    * This list must be initialized by subclasses,
    * because it depends on type of template (file of project).
    */
   protected List<FileTemplate> templateList;
   
   public CreateFileFromTemplatePresenter(HandlerManager eventBus, List<Item> selectedItems, List<Template> templateList,
      Map<String, File> openedFiles)
   {
      this.eventBus = eventBus;
      
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
   
   public void destroy()
   {
   }
   
   public void bindDisplay(CreateFromTemplateDisplay<FileTemplate> d)
   {
      display = d;
      
      /*
       * If name field is empty - disable create button
       */
      display.getNameField().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         public void onValueChange(ValueChangeEvent<String> event)
         {
            String value = event.getValue();
            
            if (value == null || value.length() == 0)
            {
               display.disableCreateButton();
            }
            else
            {
               display.enableCreateButton();
            }
         }
      });

      /*
       * Add click handler for create button
       */
      display.getCreateButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            submitTemplate();
         }
      });

      /*
       * If double click on template - than new template will be created.
       */
      display.getTemplateListGrid().addDoubleClickHandler(new DoubleClickHandler()
      {
         public void onDoubleClick(DoubleClickEvent event)
         {
            submitTemplate();
         }
      });

      /*
       * Close action on cancel button
       */
      display.getCancelButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            display.closeForm();
         }
      });

      /*
       * If template selected - than copy template name to name field and enable create button
       */
      display.getTemplateListGrid().addSelectionHandler(new SelectionHandler<FileTemplate>()
      {
         public void onSelection(SelectionEvent<FileTemplate> event)
         {
            selectedTemplates = display.getTemplatesSelected();
            templatesSelected();
         }
      });

      /*
       * Delete action on delete button
       */
      display.getDeleteButton().addClickHandler(new ClickHandler()
      {

         public void onClick(ClickEvent event)
         {
            deleteTemplate();
         }
      });

      /*
       * Initialize template list grid with template list
       */
      display.getTemplateListGrid().setValue(templateList);
      /*
       * Disable buttons and name field, because no template is selected
       */
      display.disableCreateButton();
      display.disableDeleteButton();
      display.disableNameField();
   }
   
   /**
    * Calls, when template selected in list grid.
    */
   protected void templatesSelected()
   {
      if (selectedTemplates.size() == 0)
      {
         display.disableCreateButton();
         display.disableDeleteButton();
         display.disableNameField();
         return;
      }
      
      if (selectedTemplates.size() > 1)
      {
         display.disableNameField();
         display.disableCreateButton();
         //check is one of selected templates is default
         for (Template template : selectedTemplates)
         {
            if (template.isDefault())
            {
               display.disableDeleteButton();
               return;
            }
         }
         
         display.enableDeleteButton();
         return;
      }
      
      display.enableNameField();
      display.enableCreateButton();
      if (selectedTemplates.get(0).isDefault())
      {
         display.disableDeleteButton();
      }
      else
      {
         display.enableDeleteButton();
      }
      
      setNewInstanceName();
   }
   
   /**
    * Executes, when delete button pressed.
    * Show ask dialog.
    */
   protected void deleteTemplate()
   {
      if (selectedTemplates.size() == 0)
      {
         return;
      }
      
      String message = "";
      if (selectedTemplates.size() == 1)
      {
         final String templateName = selectedTemplates.get(0).getName();
         message =
            org.exoplatform.ide.client.IDE.IDE_LOCALIZATION_MESSAGES
               .createFromTemplateAskDeleteOneTemplate(templateName);
      }
      else if (selectedTemplates.size() > 1)
      {
         message =
            org.exoplatform.ide.client.IDE.TEMPLATE_CONSTANT.createFromTemplateAskDeleteSeveralTemplates();
      }
      
      Dialogs.getInstance().ask(org.exoplatform.ide.client.IDE.TEMPLATE_CONSTANT.askDeleteTemplateDialogTitle(),
         message, new BooleanValueReceivedHandler()
         {
            public void booleanValueReceived(Boolean value)
            {
               if (value == null)
               {
                  return;
               }
               if (value)
               {
                  deleteNextTemplate();
               }
            }
         });
   }
   
   /**
    * Refresh List of the templates, after deleting
    */
   private void refreshTemplateList()
   {
      final List<Template> templates = new ArrayList<Template>();
      TemplateService.getInstance().getProjectTemplateList(new AsyncRequestCallback<ProjectTemplateList>(eventBus)
      {
         @Override
         protected void onSuccess(ProjectTemplateList result)
         {
            templates.addAll(result.getProjectTemplates());
            TemplateService.getInstance().getFileTemplateList(new AsyncRequestCallback<FileTemplateList>()
            {

               @Override
               protected void onSuccess(FileTemplateList result)
               {
                  templates.addAll(result.getFileTemplates());
                  updateTemplateList(templates);
                  
                  display.getTemplateListGrid().setValue(templateList);
                  if (templateList.size() > 0)
                  {
                     display.selectLastTemplate();
                  }
               }
            });
         }
      });
   }
   
   /**
    * Delete next template from selected list.
    */
   protected void deleteNextTemplate()
   {
      if (selectedTemplates.size() == 0)
      {
         refreshTemplateList();
         return;
      }
      deleteOneTemplate(selectedTemplates.get(0));
   }
   
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

   public void submitTemplate()
   {
      String fileName = display.getNameField().getValue();

      if ("".equals(fileName.trim()))
      {
         Dialogs.getInstance().showError(ENTER_FILE_NAME_FIRST);
         return;
      }
      
      if (baseHref == null)
      {
         baseHref = "";
      }

      FileTemplate selectedTemplate = (FileTemplate)selectedTemplates.get(0);
      
      String contentType = selectedTemplate.getMimeType();
      
      fileName = checkFileName(baseHref, fileName);

      final File newFile = new File(baseHref + fileName);
      newFile.setContentType(contentType);
      newFile.setJcrContentNodeType(NodeTypeUtil.getContentNodeType(contentType));
      newFile.setIcon(ImageUtil.getIcon(contentType));
      newFile.setNewFile(true);
      newFile.setContentChanged(true);
      newFile.setContent(selectedTemplate.getContent());
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
         TemplateService.getInstance().deleteFileTemplate(fileTemplate.getName(),
            new AsyncRequestCallback<String>(eventBus)
            {

               @Override
               protected void onSuccess(String result)
               {
                  selectedTemplates.remove(fileTemplate);
                  deleteNextTemplate();
               }
            });
         return;
      }
      
      String projectsNames = "";
      for (ProjectTemplate template : usedProjectTemplates)
      {
         projectsNames += template.getName() + ", ";
      }
      
      projectsNames = projectsNames.substring(0, projectsNames.length() - 2);
      
      final String message =
         org.exoplatform.ide.client.IDE.IDE_LOCALIZATION_MESSAGES.askDeleteTemplateUsedInOtherProjects(
            fileTemplate.getName(), projectsNames);
      
      Dialogs.getInstance().ask(org.exoplatform.ide.client.IDE.TEMPLATE_CONSTANT.askDeleteTemplateDialogTitle(),
         message, new BooleanValueReceivedHandler()
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
                  TemplateService.getInstance().deleteFileTemplate(fileTemplate.getName(),
                     new AsyncRequestCallback<String>(eventBus)
                     {

                        @Override
                        protected void onSuccess(String result)
                        {
                           selectedTemplates.remove(fileTemplate);
                           deleteNextTemplate();
                        }
                     });
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
