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

import org.exoplatform.gwtframework.commons.dialogs.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
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
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.framework.vfs.NodeTypeUtil;
import org.exoplatform.ide.client.model.template.FileTemplate;
import org.exoplatform.ide.client.model.template.FolderTemplate;
import org.exoplatform.ide.client.model.template.ProjectTemplate;
import org.exoplatform.ide.client.model.template.Template;
import org.exoplatform.ide.client.model.template.TemplateDeletedCallback;
import org.exoplatform.ide.client.model.template.TemplateList;
import org.exoplatform.ide.client.model.template.TemplateService;
import org.exoplatform.ide.client.model.util.IDEMimeTypes;
import org.exoplatform.ide.client.model.util.ImageUtil;
import org.exoplatform.ide.client.navigation.event.CreateFileFromTemplateEvent;
import org.exoplatform.ide.client.navigation.event.CreateFileFromTemplateHandler;

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
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;

/**
 * Presenter for form "Create file from template"
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class CreateFileFromTemplatePresenter implements CreateFileFromTemplateHandler, ViewClosedHandler,
   EditorFileOpenedHandler, EditorFileClosedHandler, ItemsSelectedHandler
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

   }
   
   private static final String ENTER_NAME_FIRST_MSG = org.exoplatform.ide.client.IDE.ERRORS_CONSTANT.createFileFromTemplateEnterName();

   private Map<String, File> openedFiles = new HashMap<String, File>();

   private HandlerManager eventBus;

   private Display display;

   private static final String UNTITLED_FILE = org.exoplatform.ide.client.IDE.NAVIGATION_CONSTANT.createFileUntitledFileName();

   private String previousExtension;

   private String baseHref;

   private List<ProjectTemplate> projectTemplates = new ArrayList<ProjectTemplate>();

   protected List<FileTemplate> fileTemplates = new ArrayList<FileTemplate>();

   /**
    * The list of templates, that selected.
    */
   protected FileTemplate selectedTemplate;

   public CreateFileFromTemplatePresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;

      eventBus.addHandler(CreateFileFromTemplateEvent.TYPE, this);
      eventBus.addHandler(ViewClosedEvent.TYPE, this);
      eventBus.addHandler(EditorFileOpenedEvent.TYPE, this);
      eventBus.addHandler(EditorFileClosedEvent.TYPE, this);
      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
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
      if (selectedItems != null && selectedItems.size() != 0)
      {
         Item item = selectedItems.get(0);
         baseHref = item.getHref();
         if (item instanceof File)
         {
            baseHref = baseHref.substring(0, baseHref.lastIndexOf("/") + 1);
         }
      }
   }

   @Override
   public void onCreateFileFromTemplate(CreateFileFromTemplateEvent event)
   {
      if (display != null)
      {
         return;
      }

      TemplateService.getInstance().getTemplates(new AsyncRequestCallback<TemplateList>()
      {
         @Override
         protected void onSuccess(TemplateList result)
         {
            prepareTemplateLists(result.getTemplates());

            display = GWT.create(Display.class);
            IDE.getInstance().openView(display.asView());
            bindDisplay();
         }

         @Override
         protected void onFailure(Throwable exception)
         {
            eventBus.fireEvent(new ExceptionThrownEvent(exception));
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

   /**
    * @see org.exoplatform.ide.client.template.AbstractCreateFromTemplatePresenter#updateTemplateList(java.util.List)
    */
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

      if (baseHref == null)
      {
         baseHref = "";
      }

      String contentType = selectedTemplate.getMimeType();

      fileName = getDefaultNewFileName(baseHref, fileName);

      final File newFile = new File(baseHref + fileName);
      newFile.setContentType(contentType);
      newFile.setJcrContentNodeType(NodeTypeUtil.getContentNodeType(contentType));
      newFile.setIcon(ImageUtil.getIcon(contentType));
      newFile.setNewFile(true);
      newFile.setContentChanged(true);
      newFile.setContent(selectedTemplate.getContent());
      eventBus.fireEvent(new OpenFileEvent(newFile));

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
   private String getDefaultNewFileName(String href, String proposedName)
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

   public void bindDisplay()
   {
      display.getFileNameField().addValueChangeHandler(fileNameFieldChangeHandler);

      /*
       * Add click handler for create button
       */
      display.getCreateButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            doCreateFileFromTemplate();
         }
      });

      /*
       * If double click on template - than new template will be created.
       */
      display.getTemplateListGrid().addDoubleClickHandler(new DoubleClickHandler()
      {
         public void onDoubleClick(DoubleClickEvent event)
         {
            doCreateFileFromTemplate();
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

      if (fileTemplates.size() > 0)
      {
         display.selectTemplate(fileTemplates.get(0));
      }
   }

   /**
    * Refresh List of the templates, after deleting
    */
   private void refreshTemplateList()
   {
      TemplateService.getInstance().getTemplates(new AsyncRequestCallback<TemplateList>()
      {

         @Override
         protected void onSuccess(TemplateList result)
         {
            selectedTemplate = null;

            prepareTemplateLists(result.getTemplates());

            display.getTemplateListGrid().setValue(fileTemplates);
            if (fileTemplates.size() > 0)
            {
               display.selectTemplate(fileTemplates.get(0));
            }
         }

         @Override
         protected void onFailure(Throwable exception)
         {
            eventBus.fireEvent(new ExceptionThrownEvent(exception));
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

      String message = org.exoplatform.ide.client.IDE.IDE_LOCALIZATION_MESSAGES.templateAskDeleteTemplate(selectedTemplate.getName());

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

      if (usedProjectTemplates.size() == 0)
      {
         TemplateService.getInstance().deleteTemplate(selectedTemplate, new TemplateDeletedCallback()
         {
            @Override
            protected void onSuccess(Template result)
            {
               selectedTemplate = null;
               refreshTemplateList();
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
            selectedTemplate.getName(), projectsNames);

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
                  TemplateService.getInstance().deleteTemplate(selectedTemplate, new TemplateDeletedCallback()
                  {
                     @Override
                     protected void onSuccess(Template result)
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

      if (selectedTemplate.getNodeName() == null)
      {
         display.setDeleteButtonEnabled(false);
      }
      else
      {
         display.setDeleteButtonEnabled(true);
      }

      updateFileNameExtension();
   }

}
