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

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.commons.dialogs.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.ide.client.framework.configuration.event.ConfigurationReceivedSuccessfullyEvent;
import org.exoplatform.ide.client.framework.configuration.event.ConfigurationReceivedSuccessfullyHandler;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.FileContentSaveCallback;
import org.exoplatform.ide.client.framework.vfs.Folder;
import org.exoplatform.ide.client.framework.vfs.FolderCreateCallback;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.framework.vfs.NodeTypeUtil;
import org.exoplatform.ide.client.framework.vfs.VirtualFileSystem;
import org.exoplatform.ide.client.model.template.FileTemplate;
import org.exoplatform.ide.client.model.template.FolderTemplate;
import org.exoplatform.ide.client.model.template.ProjectTemplate;
import org.exoplatform.ide.client.model.template.Template;
import org.exoplatform.ide.client.model.template.TemplateDeletedCallback;
import org.exoplatform.ide.client.model.template.TemplateList;
import org.exoplatform.ide.client.model.template.TemplateService;
import org.exoplatform.ide.client.model.util.ImageUtil;
import org.exoplatform.ide.client.project.event.CreateProjectFromTemplateEvent;
import org.exoplatform.ide.client.project.event.CreateProjectFromTemplateHandler;
import org.exoplatform.ide.extension.groovy.client.classpath.EnumSourceType;
import org.exoplatform.ide.extension.groovy.client.classpath.GroovyClassPathEntry;
import org.exoplatform.ide.extension.groovy.client.classpath.GroovyClassPathUtil;
import org.exoplatform.ide.extension.groovy.client.event.ConfigureBuildPathEvent;

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
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class CreateProjectFromTemplatePresenter implements CreateProjectFromTemplateHandler,
   ConfigurationReceivedSuccessfullyHandler, ItemsSelectedHandler, ViewClosedHandler
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

   private String baseHref;

   protected Display display;

   protected HandlerManager eventBus;

   private List<File> fileList = new ArrayList<File>();

   private List<FileTemplate> fileTemplates = new ArrayList<FileTemplate>();

   private List<Folder> folderList = new ArrayList<Folder>();

   private int itemsCreated = 0;

   private Folder projectFolder;

   /**
    * The list of templates to display.
    * This list must be initialized by subclasses,
    * because it depends on type of template (file of project).
    */
   protected List<ProjectTemplate> projectTemplates = new ArrayList<ProjectTemplate>();

   private String restServiceContext;

   private List<Item> selectedItems = new ArrayList<Item>();

   /**
    * The list of templates, that selected in list of templates.
    */
   protected List<ProjectTemplate> selectedTemplates;

   public CreateProjectFromTemplatePresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;

      eventBus.addHandler(ConfigurationReceivedSuccessfullyEvent.TYPE, this);
      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
      eventBus.addHandler(CreateProjectFromTemplateEvent.TYPE, this);
      eventBus.addHandler(ViewClosedEvent.TYPE, this);

      //refreshTemplateList();
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

   private void build(List<Template> templates, String href)
   {
      if (templates == null || templates.size() == 0)
      {
         return;
      }

      for (Template template : templates)
      {
         if (template instanceof FolderTemplate)
         {
            FolderTemplate projectTemplate = (FolderTemplate)template;

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

   private File createFileFromTemplate(FileTemplate fileTemplate, String href)
   {
      for (FileTemplate fTemplate : fileTemplates)
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

   private void createFolder(Folder folder)
   {
      VirtualFileSystem.getInstance().createFolder(folder, new FolderCreateCallback()
      {
         @Override
         protected void onSuccess(Folder result)
         {
            onFolderCreated(result);
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

      String message = "Do you want to delete template";
      if (selectedTemplates.size() == 1)
      {
         message += " <b>" + selectedTemplates.get(0).getName() + "</b>?";
      }
      else if (selectedTemplates.size() > 1)
      {
         message += "s?";
      }

      Dialogs.getInstance().ask("IDE", message, new BooleanValueReceivedHandler()
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
   protected void deleteTemplate(ProjectTemplate template)
   {
      TemplateService.getInstance().deleteTemplate(template, new TemplateDeletedCallback()
      {
         @Override
         protected void onSuccess(Template result)
         {
            selectedTemplates.remove(result);
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

      FileTemplate classPathTemplate = new FileTemplate(MimeType.APPLICATION_JSON, ".groovyclasspath", "", "", null);
      selectedTemplate.getChildren().add(classPathTemplate);

      build(selectedTemplate.getChildren(), baseHref + projectName + "/");
      projectFolder = new Folder(baseHref + projectName + "/");
      fileList.add(getClasspathFile(baseHref + projectName + "/"));

      createFolder(projectFolder);
   }

   /**
    * Do actions when project is created.
    */
   private void finishProjectCreation()
   {
      IDE.getInstance().closeView(Display.ID);

      eventBus.fireEvent(new RefreshBrowserEvent(new Folder(baseHref), projectFolder));
      eventBus.fireEvent(new ConfigureBuildPathEvent(projectFolder.getHref()));
   }

   /**
    * Get classpath file.
    * 
    * @param href href
    * @return {@link File} classpath file
    */
   private File getClasspathFile(String href)
   {
      href = (href.endsWith("/")) ? href : href + "/";
      String contentType = MimeType.APPLICATION_JSON;
      File newFile = new File(href + ".groovyclasspath");
      newFile.setContentType(contentType);
      newFile.setJcrContentNodeType(NodeTypeUtil.getContentNodeType(contentType));
      newFile.setIcon(ImageUtil.getIcon(contentType));
      newFile.setNewFile(true);

      String path = GroovyClassPathUtil.formPathFromHref(href, restServiceContext);
      GroovyClassPathEntry projectClassPathEntry = GroovyClassPathEntry.build(EnumSourceType.DIR.getValue(), path);
      List<GroovyClassPathEntry> groovyClassPathEntries = new ArrayList<GroovyClassPathEntry>();
      groovyClassPathEntries.add(projectClassPathEntry);

      String content = GroovyClassPathUtil.getClassPathJSON(groovyClassPathEntries);
      newFile.setContent(content);
      return newFile;
   }

   /**
    * @see org.exoplatform.ide.client.framework.configuration.event.ConfigurationReceivedSuccessfullyHandler#onConfigurationReceivedSuccessfully(org.exoplatform.ide.client.framework.configuration.event.ConfigurationReceivedSuccessfullyEvent)
    */
   @Override
   public void onConfigurationReceivedSuccessfully(ConfigurationReceivedSuccessfullyEvent event)
   {
      restServiceContext = event.getConfiguration().getContext();
   }

   @Override
   public void onCreateProjectFromTemplate(CreateProjectFromTemplateEvent event)
   {
      if (display == null)
      {
         if (selectedItems.size() > 0)
         {
            Item item = selectedItems.get(0);

            baseHref = item.getHref();
            if (item instanceof File)
            {
               baseHref = baseHref.substring(0, baseHref.lastIndexOf("/") + 1);
            }
         }

         display = GWT.create(Display.class);
         IDE.getInstance().openView(display.asView());
         bindDisplay();
      }
   }

   private void onFolderCreated(Folder folder)
   {
      if (itemsCreated < folderList.size())
      {
         createFolder(folderList.get(itemsCreated));
         itemsCreated++;
         return;
      }

      if (fileList.size() == 0)
      {
         finishProjectCreation();
         return;
      }

      saveFileContent(fileList.get(0));
      itemsCreated = 1;
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
      TemplateService.getInstance().getTemplates(new AsyncRequestCallback<TemplateList>()
      {
         @Override
         protected void onFailure(Throwable exception)
         {
            exception.printStackTrace();
            eventBus.fireEvent(new ExceptionThrownEvent(exception));
         }

         @Override
         protected void onSuccess(TemplateList result)
         {
            splitFileAndProjectTemplates(result.getTemplates());

            display.getTemplateListGrid().setValue(projectTemplates);
            if (projectTemplates != null && projectTemplates.size() > 0)
            {
               display.selectLastTemplate();
            }
         }
      });
   }

   private void saveFileContent(File file)
   {
      VirtualFileSystem.getInstance().saveContent(file, null, new FileContentSaveCallback()
      {
         @Override
         protected void onSuccess(FileData result)
         {
            if (itemsCreated < fileList.size())
            {
               saveFileContent(fileList.get(itemsCreated));
               itemsCreated++;
               return;
            }

            finishProjectCreation();
         }
      });
   }

   /**
    * Split list of templates on list of project templates and file templates.
    * 
    * @param templates
    */
   private void splitFileAndProjectTemplates(List<Template> templateList)
   {
      this.projectTemplates.clear();
      this.fileTemplates.clear();

      for (Template template : templateList)
      {
         if (template instanceof ProjectTemplate)
         {
            this.projectTemplates.add((ProjectTemplate)template);
         }
         else if (template instanceof FileTemplate)
         {
            this.fileTemplates.add((FileTemplate)template);
         }
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
            if (template.getNodeName() == null)
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
      if (selectedTemplates.get(0).getNodeName() == null)
      {
         display.setDeleteButtonEnabled(false);
      }
      else
      {
         display.setDeleteButtonEnabled(true);
      }

      display.getNameField().setValue(selectedTemplates.get(0).getName());
   }

}
