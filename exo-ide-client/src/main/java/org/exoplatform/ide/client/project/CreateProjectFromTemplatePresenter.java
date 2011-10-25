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
package org.exoplatform.ide.client.project;

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
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.IDELoader;
import org.exoplatform.ide.client.framework.event.ProjectCreatedEvent;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.model.template.FileTemplate;
import org.exoplatform.ide.client.model.template.FileTemplateList;
import org.exoplatform.ide.client.model.template.ProjectTemplate;
import org.exoplatform.ide.client.model.template.ProjectTemplateList;
import org.exoplatform.ide.client.model.template.Template;
import org.exoplatform.ide.client.model.template.TemplateService;
import org.exoplatform.ide.client.project.event.CreateProjectFromTemplateEvent;
import org.exoplatform.ide.client.project.event.CreateProjectFromTemplateHandler;
import org.exoplatform.ide.client.template.MigrateTemplatesEvent;
import org.exoplatform.ide.client.template.TemplatesMigratedCallback;
import org.exoplatform.ide.client.template.TemplatesMigratedEvent;
import org.exoplatform.ide.client.template.TemplatesMigratedHandler;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.FileUnmarshaller;
import org.exoplatform.ide.vfs.client.marshal.FolderUnmarshaller;
import org.exoplatform.ide.vfs.client.marshal.ProjectUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class CreateProjectFromTemplatePresenter implements CreateProjectFromTemplateHandler, ItemsSelectedHandler,
   ViewClosedHandler, TemplatesMigratedHandler
{

   /**
    * Display interface, that templates view have to implement. 
    */
   public interface Display extends IsView
   {

      String ID = "ideCreateProjectFromTemplateView";

      /**
       * Get cancel button for registration click handlers.
       * @return
       */
      HasClickHandlers getCancelButton();

      /**
       * Get create button for registration click handlers.
       * @return
       */
      HasClickHandlers getCreateButton();

      /**
       * Get delete button for registration click handlers.
       * @return
       */
      HasClickHandlers getDeleteButton();

      /**
       * Get the value of name field.
       * @return
       */
      HasValue<String> getNameField();

      /**
       * Get the list of selected templates in list grid.
       * @return
       */
      List<ProjectTemplate> getSelectedTemplates();

      /**
       * Get the template list grid for registration handlers.
       * @return
       */
      ListGridItem<ProjectTemplate> getTemplateListGrid();

      /**
       * Select the last template in list grid.
       */
      void selectLastTemplate();

      /*
       * Enables or disables Create button.
       */
      void setCreateButtonEnabled(boolean enabled);

      /**
       * Enables or disables Delete button.
       * 
       * @param enabled
       */
      void setDeleteButtonEnabled(boolean enabled);

      /**
       * Enables or disables Project Name field.
       * 
       * @param enabled
       */
      void setNameFieldEnabled(boolean enabled);

   }

   private FolderModel baseFolder;

   protected Display display;

   protected HandlerManager eventBus;

   private List<FileModel> fileList = new ArrayList<FileModel>();

   private List<FileTemplate> fileTemplates = new ArrayList<FileTemplate>();

   private List<FolderModel> folderList = new ArrayList<FolderModel>();

   private int itemsCreated = 0;

   private ProjectModel projectFolder;

   /**
    * The list of templates to display.
    * This list must be initialized by subclasses,
    * because it depends on type of template (file of project).
    */
   protected List<ProjectTemplate> projectTemplates = new ArrayList<ProjectTemplate>();

   private List<Item> selectedItems = new ArrayList<Item>();

   /**
    * The list of templates, that selected in list of templates.
    */
   protected List<ProjectTemplate> selectedTemplates;

   private boolean isTemplatesMigrated = false;

   public CreateProjectFromTemplatePresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;

      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
      eventBus.addHandler(CreateProjectFromTemplateEvent.TYPE, this);
      eventBus.addHandler(ViewClosedEvent.TYPE, this);
      eventBus.addHandler(TemplatesMigratedEvent.TYPE, this);
   }

   /**
    * 
    */
   public void bindDisplay()
   {
      folderList.clear();
      fileList.clear();

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
               display.setCreateButtonEnabled(false);
            }
            else
            {
               display.setCreateButtonEnabled(true);
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
            doCreateProjectFromTemplate();
         }
      });

      /*
       * If double click on template - than new template will be created.
       */
      display.getTemplateListGrid().addDoubleClickHandler(new DoubleClickHandler()
      {
         public void onDoubleClick(DoubleClickEvent event)
         {
            doCreateProjectFromTemplate();
         }
      });

      /*
       * Close action on cancel button
       */
      display.getCancelButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(Display.ID);
         }
      });

      /*
       * If template selected - than copy template name to name field and enable create button
       */
      display.getTemplateListGrid().addSelectionHandler(new SelectionHandler<ProjectTemplate>()
      {
         public void onSelection(SelectionEvent<ProjectTemplate> event)
         {
            selectedTemplates = display.getSelectedTemplates();
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
       * Disable buttons and name field, because no template is selected
       */
      display.setCreateButtonEnabled(false);
      display.setDeleteButtonEnabled(false);
      display.setNameFieldEnabled(false);

      /*
       * Refresh template list grid
       */
      refreshTemplateList();
   }

   /**
    * @param templates - list of templates (folder and file), from which folders and files will be created
    * @param parent - parent folder
    */
   private void build(List<Template> templates, ProjectModel parent)
   {
      if (templates == null || templates.size() == 0)
      {
         return;
      }

//      for (Template template : templates)
//      {
//         if (template instanceof FolderTemplate)
//         {
//            FolderTemplate projectTemplate = (FolderTemplate)template;
//
//            FolderModel folder = new FolderModel(projectTemplate.getName(), parent);
//            folderList.add(folder);
//            build(projectTemplate.getChildren(), folder);
//         }
//         else if (template instanceof FileTemplate)
//         {
//            FileTemplate fileTemplate = (FileTemplate)template;
//            FileModel file = createFileFromTemplate(fileTemplate, parent);
//            if (file != null)
//            {
//               fileList.add(file);
//            }
//         }
//      }
   }

   private FileModel createFileFromTemplate(FileTemplate fileTemplate, FolderModel parent)
   {
      for (FileTemplate fTemplate : fileTemplates)
      {
         if (fTemplate.getName().equals(fileTemplate.getName()))
         {
            String contentType = fTemplate.getMimeType();

            FileModel newFile = new FileModel(fileTemplate.getFileName(), contentType, fTemplate.getContent(), parent);
            return newFile;
         }
      }

      return null;
   }

   private void createFolder(final FolderModel folder)
   {
      try
      {
         VirtualFileSystem.getInstance().createFolder(folder.getParent(),
            new AsyncRequestCallback<FolderModel>(new FolderUnmarshaller(folder))
            {

               @Override
               protected void onSuccess(FolderModel result)
               {
                  onFolderCreated(folder);
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  eventBus.fireEvent(new ExceptionThrownEvent(exception,
                     "Service is not deployed.<br>Resource already exist.<br>Parent folder not found."));
               }
            });
      }
      catch (RequestException e)
      {
         e.printStackTrace();
         eventBus.fireEvent(new ExceptionThrownEvent(e,
            "Service is not deployed.<br>Resource already exist.<br>Parent folder not found."));
      }
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

      deleteTemplate(selectedTemplates.get(0));
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
         message = org.exoplatform.ide.client.IDE.TEMPLATE_CONSTANT.createFromTemplateAskDeleteSeveralTemplates();
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
    * Call template service to delete template.
    * If success, call method, that will delete next template from selected list.
    * @param template
    */
   protected void deleteTemplate(final ProjectTemplate template)
   {
      TemplateService.getInstance().deleteProjectTemplate(template.getName(),
         new org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback<String>(eventBus)
         {
            @Override
            protected void onSuccess(String result)
            {
               selectedTemplates.remove(template);
               deleteNextTemplate();
            }
         });
   }

   /**
    * Call, when create button pressed (or when double clicked on template).
    * Create new instance of selected template.
    */
   public void doCreateProjectFromTemplate()
   {
      String projectName = display.getNameField().getValue();

      ProjectTemplate selectedTemplate = selectedTemplates.get(0);

      //      FileTemplate classPathTemplate = new FileTemplate(MimeType.APPLICATION_JSON, ".groovyclasspath", "", "", null);
      //      selectedTemplate.getChildren().add(classPathTemplate);

      if (selectedTemplate.isDefault())
      {
         final IDELoader loader = new IDELoader();
         try
         {
            loader.show();
            TemplateService.getInstance().createProjectFromTemplate(VirtualFileSystem.getInstance().getInfo().getId(), baseFolder.getId(), projectName, selectedTemplate, new org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback<ProjectModel>(new ProjectUnmarshaller(new ProjectModel()))
            {
               
               @Override
               protected void onSuccess(ProjectModel result)
               {
                  loader.hide();
                  projectFolder = result;
                  finishProjectCreation();
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  loader.hide();
                  eventBus.fireEvent(new ExceptionThrownEvent(exception));
               }
            });
         }
         catch (RequestException e)
         {
            e.printStackTrace();
            loader.hide();
            eventBus.fireEvent(new ExceptionThrownEvent(e));
         }
      }
      else
      {
         //TODO create project from user template
//         folderList.clear();
//         projectFolder = new ProjectModel(projectName, baseFolder, selectedTemplate.getType(), null);
//         build(selectedTemplate.getChildren(), projectFolder);
//         //      fileList.add(createClasspathFile(baseHref + URL.encodePathSegment(projectName) + "/"));
//         createFolder((FolderModel)projectFolder);
      }
   }

   /**
    * Do actions when project is created.
    */
   private void finishProjectCreation()
   {
      IDE.getInstance().closeView(Display.ID);

      eventBus.fireEvent(new RefreshBrowserEvent(baseFolder, projectFolder));
      eventBus.fireEvent(new ProjectCreatedEvent(projectFolder));
   }

   @Override
   public void onCreateProjectFromTemplate(CreateProjectFromTemplateEvent event)
   {
      if (isTemplatesMigrated)
      {
         createProjectFromTemplate();
      }
      else
      {
         eventBus.fireEvent(new MigrateTemplatesEvent(new TemplatesMigratedCallback()
         {
            @Override
            public void onTemplatesMigrated()
            {
               createProjectFromTemplate();
            }
         }));
      }
   }

   private void createProjectFromTemplate()
   {
      if (display == null)
      {
         if (selectedItems.size() > 0)
         {
            Item item = selectedItems.get(0);

            if(item instanceof ProjectModel)
            {
               //TODO log error
               return;
            }
            if (item instanceof FileModel)
            {
               baseFolder = ((FileModel)item).getParent();
            }
            else 
            {
               baseFolder = (FolderModel)item;
            }
         }

         display = GWT.create(Display.class);
         IDE.getInstance().openView(display.asView());
         bindDisplay();
      }
   }

   private void onFolderCreated(FolderModel folder)
   {
      if (itemsCreated < folderList.size())
      {
         createFolder(folderList.get(itemsCreated));
         itemsCreated++;
         return;
      }
      itemsCreated = 0;

      if (fileList.size() == 0)
      {
         finishProjectCreation();
         return;
      }

      saveFileContent(fileList.get(0));
   }

   /**
    * @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent)
    */
   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      selectedItems = event.getSelectedItems();
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
    * Refresh List of the templates, after deleting
    */
   private void refreshTemplateList()
   {
      TemplateService.getInstance().getProjectTemplateList(
         new org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback<ProjectTemplateList>(eventBus)
         {
            @Override
            protected void onSuccess(ProjectTemplateList result)
            {
               projectTemplates = result.getProjectTemplates();
               display.getTemplateListGrid().setValue(projectTemplates);
               if (projectTemplates != null && projectTemplates.size() > 0)
               {
                  display.selectLastTemplate();
               }
               //get all file templates to create from them files
               TemplateService.getInstance().getFileTemplateList(
                  new org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback<FileTemplateList>(eventBus)
                  {

                     @Override
                     protected void onSuccess(FileTemplateList result)
                     {
                        fileTemplates = result.getFileTemplates();

                     }
                  });

            }
         });
   }

   private void saveFileContent(FileModel file)
   {
      try
      {
         VirtualFileSystem.getInstance().createFile(projectFolder,
            new AsyncRequestCallback<FileModel>(new FileUnmarshaller(file))
            {

               @Override
               protected void onSuccess(FileModel result)
               {
                  if (itemsCreated < fileList.size())
                  {
                     saveFileContent(fileList.get(itemsCreated));
                     itemsCreated++;
                     return;
                  }
                  itemsCreated = 0;

                  finishProjectCreation();
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  exception.printStackTrace();
               }
            });
      }
      catch (RequestException e)
      {
         e.printStackTrace();
      }
   }

   /**
    * Calls, when template selected in list grid.
    */
   protected void templatesSelected()
   {
      if (selectedTemplates.size() == 0)
      {
         display.setCreateButtonEnabled(false);
         display.setDeleteButtonEnabled(false);
         display.setNameFieldEnabled(false);
         return;
      }

      if (selectedTemplates.size() > 1)
      {
         display.setNameFieldEnabled(false);
         display.setCreateButtonEnabled(false);

         //check is one of selected templates is default
         for (Template template : selectedTemplates)
         {
            if (template.isDefault())
            {
               display.setDeleteButtonEnabled(false);
               return;
            }
         }

         display.setDeleteButtonEnabled(true);
         return;
      }

      display.setNameFieldEnabled(true);
      display.setCreateButtonEnabled(true);
      if (selectedTemplates.get(0).isDefault())
      {
         display.setDeleteButtonEnabled(false);
      }
      else
      {
         display.setDeleteButtonEnabled(true);
      }

      display.getNameField().setValue(selectedTemplates.get(0).getName());
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
