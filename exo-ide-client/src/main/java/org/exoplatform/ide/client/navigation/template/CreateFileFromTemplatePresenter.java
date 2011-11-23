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
package org.exoplatform.ide.client.navigation.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.framework.event.OpenFileEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.model.template.FileTemplate;
import org.exoplatform.ide.client.model.template.FileTemplateList;
import org.exoplatform.ide.client.model.template.FolderTemplate;
import org.exoplatform.ide.client.model.template.ProjectTemplate;
import org.exoplatform.ide.client.model.template.Template;
import org.exoplatform.ide.client.model.template.TemplateService;
import org.exoplatform.ide.client.model.util.IDEMimeTypes;
import org.exoplatform.ide.client.navigation.event.CreateFileFromTemplateEvent;
import org.exoplatform.ide.client.navigation.event.CreateFileFromTemplateHandler;
import org.exoplatform.ide.client.template.MigrateTemplatesEvent;
import org.exoplatform.ide.client.template.TemplatesMigratedCallback;
import org.exoplatform.ide.client.template.TemplatesMigratedEvent;
import org.exoplatform.ide.client.template.TemplatesMigratedHandler;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HasValue;

/**
 * Presenter for form "Create file from template"
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class CreateFileFromTemplatePresenter implements CreateFileFromTemplateHandler, ViewClosedHandler,
   EditorFileOpenedHandler, EditorFileClosedHandler, ItemsSelectedHandler, TemplatesMigratedHandler
{

   public interface Display extends IsView
   {
      /**
       * Get the template list grid for registration handlers.
       * @return
       */
      ListGridItem<FileTemplate> getTemplateListGrid();

      /**
       * Get the list of selected templates in list grid.
       * @return
       */
      FileTemplate getSelectedTemplate();

      /**
       * Select template in the list of templates.
       * 
       * @param template template to be selected
       */
      void selectTemplate(FileTemplate template);

      /**
       * Get file name field.
       * @return
       */
      HasValue<String> getFileNameField();

      /**
       * Get create button for registration click handlers.
       * @return
       */
      HasClickHandlers getCreateButton();

      /**
       * Get cancel button for registration click handlers.
       * @return
       */
      HasClickHandlers getCancelButton();

      /**
       * Get delete button for registration click handlers.
       * @return
       */
      HasClickHandlers getDeleteButton();

      /**
       * Sets Create button enabled.
       * 
       * @param enabled
       */
      void setCreateButtonEnabled(boolean enabled);

      /**
       * Sets Delete button enabled.
       * 
       * @param enabled
       */
      void setDeleteButtonEnabled(boolean enabled);

      /**
       * Sets File Name field enabled.
       * 
       * @param enabled
       */
      void setFileNameFieldEnabled(boolean enabled);

      /**
       * Set the title of submit button.
       * @param title
       */
      void setSubmitButtonTitle(String title);

   }

   private static final String ENTER_NAME_FIRST_MSG = org.exoplatform.ide.client.IDE.ERRORS_CONSTANT
      .createFileFromTemplateEnterName();

   /**
    * Used to return, when submit button was pressed. Can be null,
    * then normal behavior will be provided.
    */
   private CreateFileFromTemplateCallback submitCallback;

   private Map<String, FileModel> openedFiles = new HashMap<String, FileModel>();

   private Display display;

   private static final String UNTITLED_FILE = org.exoplatform.ide.client.IDE.NAVIGATION_CONSTANT
      .createFileUntitledFileName();

   private String previousExtension;

   private FolderModel baseFolder;

   private List<ProjectTemplate> projectTemplates = new ArrayList<ProjectTemplate>();

   protected List<FileTemplate> fileTemplates = new ArrayList<FileTemplate>();

   /**
    * The list of templates, that selected.
    */
   protected FileTemplate selectedTemplate;

   private boolean isTemplatesMigrated = false;

   public CreateFileFromTemplatePresenter()
   {
      IDE.addHandler(CreateFileFromTemplateEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
      IDE.addHandler(EditorFileOpenedEvent.TYPE, this);
      IDE.addHandler(EditorFileClosedEvent.TYPE, this);
      IDE.addHandler(ItemsSelectedEvent.TYPE, this);
      IDE.addHandler(TemplatesMigratedEvent.TYPE, this);
   }

   @Override
   public void onEditorFileClosed(EditorFileClosedEvent event)
   {
      openedFiles = event.getOpenedFiles();
   }

   @Override
   public void onEditorFileOpened(EditorFileOpenedEvent event)
   {
      openedFiles = event.getOpenedFiles();
   }

   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      List<Item> selectedItems = event.getSelectedItems();
      if (selectedItems != null && selectedItems.isEmpty())
      {
         Item item = selectedItems.get(0);
         if (item instanceof FileModel)
         {
            baseFolder = ((FileModel)item).getParent();
         }
         else if (item instanceof FolderModel)
         {
            baseFolder = (FolderModel)item;
         }
         else if (item instanceof ProjectModel)
         {
            baseFolder = new FolderModel((ProjectModel)item);
         }
      }
   }

   @Override
   public void onCreateFileFromTemplate(final CreateFileFromTemplateEvent event)
   {
      submitCallback = event.getCallback();

      if (isTemplatesMigrated)
      {
         createFileFromTemplate(event.getFormTitle(), event.getSubmitButtonTitle());
      }
      else
      {
         IDE.fireEvent(new MigrateTemplatesEvent(new TemplatesMigratedCallback()
         {
            @Override
            public void onTemplatesMigrated()
            {
               createFileFromTemplate(event.getFormTitle(), event.getSubmitButtonTitle());
            }
         }));
      }
   }

   /**
    * @param formTitle the title of form. If <code>null</code> than default will be used.
    * @param submitButtonTitle the title of submit button. If <code>null</code> than default will be used.
    */
   private void createFileFromTemplate(final String formTitle, final String submitButtonTitle)
   {
      if (display != null)
      {
         IDE.fireEvent(new ExceptionThrownEvent("Display " + display.asView().getId() + " is not null"));
      }

      TemplateService.getInstance().getFileTemplateList(new AsyncRequestCallback<FileTemplateList>(IDE.eventBus())
      {
         @Override
         protected void onSuccess(FileTemplateList result)
         {
            fileTemplates = result.getFileTemplates();
            display = GWT.create(Display.class);
            IDE.getInstance().openView(display.asView());
            bindDisplay(formTitle, submitButtonTitle);
         }
      });

   }

   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
      }
   }

   protected void prepareTemplateLists(List<Template> templates)
   {
      fileTemplates.clear();
      projectTemplates.clear();

      for (Template template : templates)
      {
         if (template instanceof FileTemplate)
         {
            fileTemplates.add((FileTemplate)template);
         }
         else if (template instanceof ProjectTemplate)
         {
            projectTemplates.add((ProjectTemplate)template);
         }
      }
   }

   protected void updateFileNameExtension()
   {
      String extension = IDEMimeTypes.getExtensionsMap().get(selectedTemplate.getMimeType());

      if (previousExtension != null)
      {
         String name = display.getFileNameField().getValue();
         if (name == null || name.length() == 0)
         {
            name = UNTITLED_FILE;
         }
         if (name.endsWith("." + previousExtension))
         {
            name = name.substring(0, name.length() - previousExtension.length() - 1);
         }
         name += "." + extension;
         display.getFileNameField().setValue(name);
      }
      else
      {
         String value = display.getFileNameField().getValue();
         if (value == null || value.length() == 0)
         {
            value = UNTITLED_FILE;
         }
         display.getFileNameField().setValue(value + "." + extension);
      }

      previousExtension = extension;
   }

   /**
    * @see org.exoplatform.ide.client.template.AbstractCreateFromTemplatePresenter#submitTemplate()
    */
   public void doCreateFileFromTemplate()
   {
      String fileName = display.getFileNameField().getValue();

      if ("".equals(fileName.trim()))
      {
         Dialogs.getInstance().showError(ENTER_NAME_FIRST_MSG);
         return;
      }

      String contentType = selectedTemplate.getMimeType();

      fileName = getDefaultNewFileName(baseFolder, fileName);

      final FileModel newFile = new FileModel(fileName, contentType, selectedTemplate.getContent(), baseFolder);
      //      newFile.setContentType(contentType);
      //      newFile.setJcrContentNodeType(NodeTypeUtil.getContentNodeType(contentType));
      //      newFile.setIcon(ImageUtil.getIcon(contentType));
      //      newFile.setNewFile(true);
      //      newFile.setContentChanged(true);
      //      newFile.setContent(selectedTemplate.getContent());
      IDE.fireEvent(new OpenFileEvent(newFile));

      closeView();
   }

   private void closeView()
   {
      IDE.getInstance().closeView(display.asView().getId());
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
   private String getDefaultNewFileName(FolderModel folder, String proposedName)
   {
      String name = proposedName;
      if (openedFiles == null || openedFiles.isEmpty())
      {
         return name;
      }

      final String nameWithoutExt = name.substring(0, name.lastIndexOf("."));
      String extension = name.substring(name.lastIndexOf(".") + 1, name.length());
      int index = 1;
      for (FileModel file : openedFiles.values())
      {
         if (file.getParentId().equals(folder.getId()) && file.getName().equals(name))
         {
            name = nameWithoutExt + " " + index + "." + extension;
            index++;
         }
      }

      return name;
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

   private ValueChangeHandler<String> fileNameFieldChangeHandler = new ValueChangeHandler<String>()
   {
      @Override
      public void onValueChange(ValueChangeEvent<String> event)
      {
         /*
          * If name field is empty - disable create button
          */

         String value = event.getValue();

         if (value == null || value.length() == 0)
         {
            display.setCreateButtonEnabled(false);
         }
         else
         {
            display.setCreateButtonEnabled(true);
         }
      }
   };

   public void bindDisplay(String title, String submitButtonTitle)
   {
      display.getFileNameField().addValueChangeHandler(fileNameFieldChangeHandler);

      /*
       * Add click handler for create button
       */
      display.getCreateButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            if (submitCallback != null)
            {
               FileTemplate fileTemplate =
                  new FileTemplate(display.getSelectedTemplate().getName(), display.getFileNameField().getValue());
               closeView();
               submitCallback.onSubmit(fileTemplate);
               submitCallback = null;
            }
            else
            {
               doCreateFileFromTemplate();
            }
         }
      });

      /*
       * If double click on template - than new template will be created.
       */
      display.getTemplateListGrid().addDoubleClickHandler(new DoubleClickHandler()
      {
         public void onDoubleClick(DoubleClickEvent event)
         {
            if (submitCallback != null)
            {
               FileTemplate fileTemplate =
                  new FileTemplate(display.getSelectedTemplate().getName(), display.getFileNameField().getValue(),
                     display.getSelectedTemplate().getMimeType());
               closeView();
               submitCallback.onSubmit(fileTemplate);
               submitCallback = null;
            }
            else
            {
               doCreateFileFromTemplate();
            }
         }
      });

      /*
       * Close action on cancel button
       */
      display.getCancelButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
      });

      /*
       * If template selected - than copy template name to name field and enable create button
       */
      display.getTemplateListGrid().addSelectionHandler(new SelectionHandler<FileTemplate>()
      {
         public void onSelection(SelectionEvent<FileTemplate> event)
         {
            onTemplateSelected();
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
      display.getTemplateListGrid().setValue(fileTemplates);

      /*
       * Disable buttons and name field, because no template is selected
       */
      display.setCreateButtonEnabled(false);
      display.setDeleteButtonEnabled(false);
      display.setFileNameFieldEnabled(false);

      if (!fileTemplates.isEmpty())
      {
         display.selectTemplate(fileTemplates.get(0));
      }

      if (title != null)
      {
         display.asView().setTitle(title);
      }
      if (submitButtonTitle != null)
      {
         display.setSubmitButtonTitle(submitButtonTitle);
      }
   }

   /**
    * Refresh List of the templates, after deleting
    */
   private void refreshTemplateList()
   {
      TemplateService.getInstance().getFileTemplateList(new AsyncRequestCallback<FileTemplateList>(IDE.eventBus())
      {
         @Override
         protected void onSuccess(FileTemplateList result)
         {
            fileTemplates = result.getFileTemplates();
            selectedTemplate = null;
            display.getTemplateListGrid().setValue(fileTemplates);
            if (!fileTemplates.isEmpty())
            {
               display.selectTemplate(fileTemplates.get(0));
            }
         }

         @Override
         protected void onFailure(Throwable exception)
         {
            IDE.fireEvent(new ExceptionThrownEvent(exception));
         }
      });
   }

   /**
    * Executes, when delete button pressed.
    * Show ask dialog.
    */
   protected void deleteTemplate()
   {
      if (selectedTemplate == null)
      {
         return;
      }

      String message =
         org.exoplatform.ide.client.IDE.IDE_LOCALIZATION_MESSAGES.templateAskDeleteTemplate(selectedTemplate.getName());

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
                  doDeleteTemplate();
               }
            }
         });
   }

   /**
    * Delete selected template.
    */
   private void doDeleteTemplate()
   {
      List<ProjectTemplate> usedProjectTemplates = new ArrayList<ProjectTemplate>();
      for (ProjectTemplate projectTemplate : projectTemplates)
      {
         if (isPresentInProjectTemplate(projectTemplate, selectedTemplate))
         {
            usedProjectTemplates.add(projectTemplate);
         }
      }

      if (usedProjectTemplates.isEmpty())
      {
         TemplateService.getInstance().deleteFileTemplate(selectedTemplate.getName(),
            new AsyncRequestCallback<String>(IDE.eventBus())
            {

               @Override
               protected void onSuccess(String result)
               {
                  selectedTemplate = null;
                  refreshTemplateList();
               }
            });
         return;
      }

      StringBuffer projectsNames = new StringBuffer();
      for (ProjectTemplate template : usedProjectTemplates)
      {
         projectsNames.append(template.getName()).append(", ");
      }

      final String message =
         org.exoplatform.ide.client.IDE.IDE_LOCALIZATION_MESSAGES.askDeleteTemplateUsedInOtherProjects(
            selectedTemplate.getName(), projectsNames.substring(0, projectsNames.length() - 2));

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
                  TemplateService.getInstance().deleteFileTemplate(selectedTemplate.getName(),
                     new AsyncRequestCallback<String>(IDE.eventBus())
                     {

                        @Override
                        protected void onSuccess(String result)
                        {
                           selectedTemplate = null;
                           refreshTemplateList();
                        }
                     });
               }
            }
         });

   }

   /**
    * Calls, when template selected in list grid.
    */
   protected void onTemplateSelected()
   {
      selectedTemplate = display.getSelectedTemplate();

      if (selectedTemplate == null)
      {
         display.setCreateButtonEnabled(false);
         display.setDeleteButtonEnabled(false);
         display.setFileNameFieldEnabled(false);
         return;
      }

      display.setFileNameFieldEnabled(true);
      display.setCreateButtonEnabled(true);

      if (selectedTemplate.isDefault())
      {
         display.setDeleteButtonEnabled(false);
      }
      else
      {
         display.setDeleteButtonEnabled(true);
      }

      updateFileNameExtension();
   }

   /**
    * @see org.exoplatform.ide.client.template.TemplatesMigratedHandler#onTemplatesMigrated(org.exoplatform.ide.client.template.TemplatesMigratedEvent)
    */
   @Override
   public void onTemplatesMigrated(TemplatesMigratedEvent event)
   {
      isTemplatesMigrated = true;
   }

}
