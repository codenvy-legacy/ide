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
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasKeyPressHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.ui.client.api.TreeGridItem;
import org.exoplatform.ide.client.model.template.FileTemplate;
import org.exoplatform.ide.client.model.template.ProjectTemplate;
import org.exoplatform.ide.client.model.template.Template;
import org.exoplatform.ide.client.model.template.TemplateServiceImpl;
import org.exoplatform.ide.client.model.template.event.TemplateCreatedEvent;
import org.exoplatform.ide.client.model.template.event.TemplateCreatedHandler;
import org.exoplatform.ide.client.module.navigation.action.AbstractCreateFolderForm;
import org.exoplatform.ide.client.module.navigation.action.CreateFolderDisplay;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class CreateProjectTemplatePresenter implements TemplateCreatedHandler
{
   
   public interface Display
   {
      TreeGridItem<Template> getTemplateTreeGrid();

      HasValue<String> getNameField();
      
      HasKeyPressHandlers getNameFieldKeyPressed();
      
      HasValue<String> getDescriptionField();

      HasClickHandlers getCreateButton();

      HasClickHandlers getCancelButton();
      
      HasClickHandlers getAddFolderButton();

      HasClickHandlers getAddFileButton();
      
      HasClickHandlers getDeleteButton();
      
      List<Template> getTreeGridSelection();

      void closeForm();

      void enableCreateButton();

      void disableCreateButton();
      
      void enableAddFolderButton();

      void disableAddFolderButton();      
      
      void enableAddFileButton();

      void disableAddFileButton();
      
      void enableDeleteButton();

      void disableDeleteButton();
      
      void selectTemplate(Template template);
      
      void updateTree();
      
      void setRootNodeName(String name);

   }
   
   private static final String DEFAULT_PROJECT_NAME = "New Project";
   
   private HandlerManager eventBus;
   
   private Handlers handlers;
   
   private Display display;
   
   private List<Template> templateList = new ArrayList<Template>();
   
   private Template templateToCreate;
   
   private Template selectedTemplate;
   
   public CreateProjectTemplatePresenter(HandlerManager eventBus, List<Template> templateList)
   {
      this.eventBus = eventBus;
      this.templateList = templateList;
      
      handlers = new Handlers(eventBus);
   }
   
   public void bindDisplay(Display d)
   {
      display = d;
      
      handlers.addHandler(TemplateCreatedEvent.TYPE, this);
      
      display.getNameFieldKeyPressed().addKeyPressHandler(nameFieldKeyPressedHandler);
      
      display.getCancelButton().addClickHandler(closeFormHandler);

      display.getCreateButton().addClickHandler(createTemplateHandler);
      
      display.getAddFolderButton().addClickHandler(addFolderHandler);
      
      display.getAddFileButton().addClickHandler(addFileHandler);
      
      display.getDeleteButton().addClickHandler(deleteTemplateHandler);
      
      display.getTemplateTreeGrid().addSelectionHandler(templateSelectedHandler);

      ProjectTemplate projectTemplate = new ProjectTemplate(DEFAULT_PROJECT_NAME);
      display.getTemplateTreeGrid().setValue(projectTemplate);
   }
   
   private KeyPressHandler nameFieldKeyPressedHandler = new KeyPressHandler()
   {
      
      public void onKeyPress(KeyPressEvent event)
      {
         if (event.getCharCode() == KeyCodes.KEY_ENTER)
         {
            final String nameValue = display.getNameField().getValue();
            final String projectName = nameValue == null || nameValue.length() < 1 ? DEFAULT_PROJECT_NAME : nameValue;
            display.getTemplateTreeGrid().getValue().setName(projectName);
            display.setRootNodeName(projectName);
         }
      }
   };
   
   private ClickHandler closeFormHandler  = new ClickHandler()
   {
      public void onClick(ClickEvent event)
      {
         display.closeForm();
      }
   };
   
   private ClickHandler createTemplateHandler = new ClickHandler()
   {
      public void onClick(ClickEvent event)
      {
         createTemplate();
      }
   };
   
   private ClickHandler addFolderHandler = new ClickHandler()
   {
      public void onClick(ClickEvent event)
      {
         callAddFolderForm();
      }
   };
   
   private ClickHandler addFileHandler = new ClickHandler()
   {
      public void onClick(ClickEvent event)
      {
         addFileToTemplate();
      }
   };
   
   private ClickHandler deleteTemplateHandler = new ClickHandler()
   {
      public void onClick(ClickEvent event)
      {
         Template value = display.getTemplateTreeGrid().getValue();
         deleteTemplate(((ProjectTemplate)value).getChildren());
         display.getTemplateTreeGrid().setValue(value);
         display.selectTemplate(value);
      }
   };
   
   private SelectionHandler<Template> templateSelectedHandler = new SelectionHandler<Template>()
   {
      public void onSelection(SelectionEvent<Template> event)
      {
         //in no selection - disable all buttons
         if (display.getTreeGridSelection() == null || display.getTreeGridSelection().size() != 1)
         {
            display.disableAddFolderButton();
            display.disableAddFileButton();
            display.disableDeleteButton();
            return;
         }
         //if selected one item
         selectedTemplate = display.getTreeGridSelection().get(0);
         //if root selected
         if (selectedTemplate == display.getTemplateTreeGrid().getValue())
         {
            //can't delete root folder
            display.disableDeleteButton();
         }
         else
         {
            display.enableDeleteButton();
         }
         if (selectedTemplate instanceof ProjectTemplate)
         {
            display.enableAddFolderButton();
            display.enableAddFileButton();
         }
         else
         {
            display.disableAddFolderButton();
            display.disableAddFileButton();
         }
         
      }
   };
   
   private void addFileToTemplate()
   {
      AbstractCreateFromTemplatePresenter<FileTemplate> addFilePresenter = new CreateFileFromTemplatePresenter(eventBus, null, templateList)
      {
         @Override
         void submitTemplate()
         {
            final String fileName = display.getNameField().getValue().trim();
            if ("".equals(fileName))
            {
               Dialogs.getInstance().showError("You must enter file name the first!");
               return;
            }

            FileTemplate fileTemplate = new FileTemplate(selectedTemplate.getName(), fileName, selectedTemplate.getMimeType());
            addFileToProjectTemplate(fileTemplate);
            display.closeForm();

         }
      };
      
      CreateFromTemplateDisplay<FileTemplate> createFileDisplay =
         new CreateFileFromTemplateForm(eventBus, templateList, addFilePresenter)
      {
         @Override
         String getCreateButtonTitle()
         {
            return "Add";
         }
         
         @Override
         String getFormTitle()
         {
            return "Add file";
         }
      };
      addFilePresenter.bindDisplay(createFileDisplay);
   }
   
   private void callAddFolderForm()
   {
      final CreateFolderDisplay createFolderDisplay = new AbstractCreateFolderForm(eventBus, "Add folder", "Add")
      {
      };
      
      createFolderDisplay.getCreateButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            createFolderDisplay.closeForm();
            
            final String folderName = createFolderDisplay.getFolderNameField().getValue();
            validateAndAddFolder(folderName);
           
         }
      });
      
      createFolderDisplay.getFolderNameFiledKeyPressed().addKeyPressHandler(new KeyPressHandler()
      {
         public void onKeyPress(KeyPressEvent event)
         {
            if (event.getCharCode() == KeyCodes.KEY_ENTER)
            {
               createFolderDisplay.closeForm();
               
               final String folderName = createFolderDisplay.getFolderNameField().getValue();
               validateAndAddFolder(folderName);
            }
         }
      });
      
      createFolderDisplay.getCancelButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            createFolderDisplay.closeForm();
         }
      });
   }
   
   private void validateAndAddFolder(String folderName)
   {
      ProjectTemplate selectedFolder = (ProjectTemplate)selectedTemplate;
      
      //validate
      if (folderName == null || folderName.length() == 0)
      {
         Dialogs.getInstance().showError("Value can't be empty");
         return;
      }
      
      if (selectedFolder.getChildren() != null)
      {
         for (Template template : selectedFolder.getChildren())
         {
            if (template instanceof ProjectTemplate && folderName.equals(template.getName()))
            {
               Dialogs.getInstance().showError("Folder with such name already exists");
               return;
            }
         }
      }
      
      Template newFolder = new ProjectTemplate(folderName);
      if (selectedFolder.getChildren() == null)
      {
         selectedFolder.setChildren(new ArrayList<Template>());
      }
      selectedFolder.getChildren().add(newFolder);
      display.updateTree();
      display.selectTemplate(newFolder);
   }
   
   private void deleteTemplate(List<Template> templates)
   {
      if (templates == null)
      {
         return;
      }
      
      for (Template template : templates)
      {
         if (selectedTemplate == template)
         {
            templates.remove(template);
            return;
         }
         if (template instanceof ProjectTemplate)
         {
            deleteTemplate(((ProjectTemplate)template).getChildren());
         }
      }
      
   }
   
   private void createTemplate()
   {
      String description = "";
      if (display.getDescriptionField().getValue() != null)
      {
         description = display.getDescriptionField().getValue();
      }
      
      templateToCreate = display.getTemplateTreeGrid().getValue();
      templateToCreate.setDescription(description);
      
      for (Template template : templateList)
      {
         if (template instanceof ProjectTemplate && templateToCreate.getName().equals(template.getName()))
         {
            Dialogs.getInstance().showError("Project template with such name already exists!");
            return;
         }
      }
      TemplateServiceImpl.getInstance().createTemplate(templateToCreate);
   }
   
   public void destroy()
   {
      handlers.removeHandlers();
   }

   /**
    * @see org.exoplatform.ide.client.model.template.event.TemplateCreatedHandler#onTemplateCreated(org.exoplatform.ide.client.model.template.event.TemplateCreatedEvent)
    */
   public void onTemplateCreated(TemplateCreatedEvent event)
   {
      display.closeForm();
      Dialogs.getInstance().showInfo("Template created successfully!");
   }
   
   private void addFileToProjectTemplate(FileTemplate fileTemplate)
   {
      ProjectTemplate selectedFolder = (ProjectTemplate)selectedTemplate;
      
      if (selectedFolder.getChildren() != null)
      {
         for (Template template : selectedFolder.getChildren())
         {
            if (template instanceof FileTemplate && fileTemplate.getFileName().equals(((FileTemplate)template).getFileName()))
            {
               Dialogs.getInstance().showError("File with such name already exists");
               return;
            }
         }
      }
      
      if (selectedFolder.getChildren() == null)
      {
         selectedFolder.setChildren(new ArrayList<Template>());
      }
      selectedFolder.getChildren().add(fileTemplate);
      display.updateTree();
      display.selectTemplate(fileTemplate);
   }

}
