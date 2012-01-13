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
package org.exoplatform.ide.client.project.template;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.ui.client.api.TreeGridItem;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.gwtframework.ui.client.dialog.StringValueReceivedHandler;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.model.template.FileTemplate;
import org.exoplatform.ide.client.model.template.FileTemplateList;
import org.exoplatform.ide.client.model.template.FolderTemplate;
import org.exoplatform.ide.client.model.template.ProjectTemplate;
import org.exoplatform.ide.client.model.template.ProjectTemplateList;
import org.exoplatform.ide.client.model.template.Template;
import org.exoplatform.ide.client.model.template.TemplateService;
import org.exoplatform.ide.client.navigation.event.CreateFileFromTemplateEvent;
import org.exoplatform.ide.client.navigation.template.CreateFileFromTemplateCallback;
import org.exoplatform.ide.client.template.MigrateTemplatesEvent;
import org.exoplatform.ide.client.template.TemplatesMigratedCallback;
import org.exoplatform.ide.client.template.TemplatesMigratedEvent;
import org.exoplatform.ide.client.template.TemplatesMigratedHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasKeyPressHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HasValue;

/**
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 * 
 */
public class CreateProjectTemplatePresenter implements CreateProjectTemplateHandler, TemplatesMigratedHandler,
   ViewClosedHandler
{

   public interface Display extends IsView
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

      String getTemplateLocationInProject(Template template);

   }

   private Display display;

   private List<Template> templateList = new ArrayList<Template>();

   private Template templateToCreate;

   private Template selectedTemplate;

   private ProjectTemplate projectTemplate;

   private static final String ENTER_FILE_NAME_FIRST = IDE.TEMPLATE_CONSTANT.createProjectTemplateEnterNameFirst();

   private boolean isTemplatesMigrated = false;

   public CreateProjectTemplatePresenter()
   {
      // IDE.getInstance().addControl(new CreateProjectTemplateControl());

      IDE.addHandler(CreateProjectTemplateEvent.TYPE, this);
      IDE.addHandler(TemplatesMigratedEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
   }

   public void bindDisplay(Display d)
   {
      display = d;

      display.getNameField().addValueChangeHandler(valueChangeHandler);

      display.getCancelButton().addClickHandler(closeFormHandler);

      display.getCreateButton().addClickHandler(createTemplateHandler);

      display.getAddFolderButton().addClickHandler(addFolderHandler);

      display.getAddFileButton().addClickHandler(addFileHandler);

      display.getDeleteButton().addClickHandler(deleteTemplateHandler);

      display.getTemplateTreeGrid().addSelectionHandler(templateSelectedHandler);

      projectTemplate = new ProjectTemplate("/");

      display.getTemplateTreeGrid().setValue(projectTemplate);
      display.disableCreateButton();
   }

   private ValueChangeHandler<String> valueChangeHandler = new ValueChangeHandler<String>()
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
   };

   private ClickHandler closeFormHandler = new ClickHandler()
   {
      public void onClick(ClickEvent event)
      {
         closeView();
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
         deleteTemplate(((FolderTemplate)value).getChildren());
         display.getTemplateTreeGrid().setValue(value);
         display.selectTemplate(value);
      }
   };

   private SelectionHandler<Template> templateSelectedHandler = new SelectionHandler<Template>()
   {
      public void onSelection(SelectionEvent<Template> event)
      {
         // in no selection - disable all buttons
         if (display.getTreeGridSelection() == null || display.getTreeGridSelection().size() != 1)
         {
            display.disableAddFolderButton();
            display.disableAddFileButton();
            display.disableDeleteButton();
            return;
         }
         // if selected one item
         selectedTemplate = display.getTreeGridSelection().get(0);
         // if root selected
         if (selectedTemplate == display.getTemplateTreeGrid().getValue())
         {
            // can't delete root folder
            display.disableDeleteButton();
         }
         else if (selectedTemplate instanceof FileTemplate
            && MimeType.APPLICATION_JSON.equals(((FileTemplate)selectedTemplate).getMimeType()))
         {
            // can not delete classpath file
            display.disableDeleteButton();
         }
         else
         {
            display.enableDeleteButton();
         }
         if (selectedTemplate instanceof FolderTemplate)
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
      CreateFileFromTemplateCallback callback = new CreateFileFromTemplateCallback()
      {

         @Override
         public void onSubmit(FileTemplate fileTemplate)
         {
            if ("".equals(fileTemplate.getFileName().trim()))
            {
               Dialogs.getInstance().showError(ENTER_FILE_NAME_FIRST);
               return;
            }
            addFileToProjectTemplate(fileTemplate);
         }
      };
      IDE.fireEvent(new CreateFileFromTemplateEvent(callback, IDE.TEMPLATE_CONSTANT.addFileButton(),
         IDE.IDE_LOCALIZATION_CONSTANT.addButton()));
   }

   private void callAddFolderForm()
   {
      Dialogs.getInstance().askForValue(IDE.TEMPLATE_CONSTANT.createProjectTemplateAddFolderTitle(),
         IDE.TEMPLATE_CONSTANT.createProjectTemplateAddFolderText(),
         IDE.TEMPLATE_CONSTANT.createProjectTemplateAddFolderDefault(), new StringValueReceivedHandler()
         {
            @Override
            public void stringValueReceived(String value)
            {
               if (value == null)
                  return;
               validateAndAddFolder(value);
            }
         });
   }

   private void validateAndAddFolder(String folderName)
   {
      FolderTemplate selectedFolder = (FolderTemplate)selectedTemplate;

      // validate
      if (folderName == null || folderName.length() == 0)
      {
         Dialogs.getInstance().showError(IDE.TEMPLATE_CONSTANT.createProjectTemplateValueCantBeEmpty());
         return;
      }

      if (selectedFolder.getChildren() != null)
      {
         for (Template template : selectedFolder.getChildren())
         {
            if (template instanceof FolderTemplate && folderName.equals(template.getName()))
            {
               Dialogs.getInstance().showError(IDE.TEMPLATE_CONSTANT.createProjectTemplateFolderAlreadyExists());
               return;
            }
         }
      }

      Template newFolder = new FolderTemplate(folderName);
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
         if (template instanceof FolderTemplate)
         {
            deleteTemplate(((FolderTemplate)template).getChildren());
         }
      }

   }

   private void createTemplate()
   {
      String templateName = display.getNameField().getValue().trim();

      String description = "";
      if (display.getDescriptionField().getValue() != null)
      {
         description = display.getDescriptionField().getValue();
      }

      templateToCreate = display.getTemplateTreeGrid().getValue();
      templateToCreate.setName(templateName);
      templateToCreate.setDescription(description);
      projectTemplate.setName(templateName);
      projectTemplate.setDescription(description);
      projectTemplate.setDefault(false);

      for (Template template : templateList)
      {
         if (template instanceof FolderTemplate && templateToCreate.getName().equals(template.getName()))
         {
            Dialogs.getInstance().showError(IDE.TEMPLATE_CONSTANT.createProjectTemplateProjectAlreadyExists());
            return;
         }
      }
      TemplateService.getInstance().addProjectTemplate(projectTemplate,
         new AsyncRequestCallback<String>(IDE.eventBus())
         {

            @Override
            protected void onSuccess(String result)
            {
               closeView();
               Dialogs.getInstance().showInfo(IDE.TEMPLATE_CONSTANT.createProjectTemplateCreated());
            }
         });
   }

   public void destroy()
   {
   }

   private void addFileToProjectTemplate(FileTemplate fileTemplate)
   {
      FolderTemplate selectedFolder = (FolderTemplate)selectedTemplate;

      if (selectedFolder.getChildren() != null)
      {
         for (Template template : selectedFolder.getChildren())
         {
            if (template instanceof FileTemplate
               && fileTemplate.getFileName().equals(((FileTemplate)template).getFileName()))
            {
               Dialogs.getInstance().showError(IDE.TEMPLATE_CONSTANT.createProjectTemplateFileAlreadyExists());
               return;
            }
         }
      }
      selectedFolder.getChildren().add(fileTemplate);
      display.updateTree();
      display.selectTemplate(fileTemplate);
   }

   private void closeView()
   {
      IDE.getInstance().closeView(display.asView().getId());
   }

   private void openView()
   {
      if (display == null)
      {
         Display d = GWT.create(Display.class);
         IDE.getInstance().openView(d.asView());
         bindDisplay(d);
      }
      else
      {
         IDE.fireEvent(new ExceptionThrownEvent("Display CreateProjectTemplate must be null"));
      }
   }

   /**
    * @see org.exoplatform.ide.client.project.event.CreateProjectTemplateHandler#onCreateProjectTemplate(org.exoplatform.ide.client.project.event.CreateProjectTemplateEvent)
    */
   @Override
   public void onCreateProjectTemplate(CreateProjectTemplateEvent event)
   {
      if (isTemplatesMigrated)
      {
         createProjectTemplate();
      }
      else
      {
         IDE.fireEvent(new MigrateTemplatesEvent(new TemplatesMigratedCallback()
         {
            @Override
            public void onTemplatesMigrated()
            {
               createProjectTemplate();
            }
         }));
      }
   }

   private void createProjectTemplate()
   {
      templateList = new ArrayList<Template>();
      TemplateService.getInstance().getProjectTemplateList(
         new AsyncRequestCallback<ProjectTemplateList>(IDE.eventBus())
         {
            @Override
            protected void onSuccess(ProjectTemplateList result)
            {
               templateList.addAll(result.getProjectTemplates());
               TemplateService.getInstance().getFileTemplateList(new AsyncRequestCallback<FileTemplateList>()
               {

                  @Override
                  protected void onSuccess(FileTemplateList result)
                  {
                     templateList.addAll(result.getFileTemplates());
                     // new CreateProjectTemplateView(eventBus, templates);
                     openView();
                  }
               });
            }
         });
   }

   /**
    * @see org.exoplatform.ide.client.template.TemplatesMigratedHandler#onTemplatesMigrated(org.exoplatform.ide.client.template.TemplatesMigratedEvent)
    */
   @Override
   public void onTemplatesMigrated(TemplatesMigratedEvent event)
   {
      isTemplatesMigrated = true;
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent)
    */
   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
      }
   }

}
