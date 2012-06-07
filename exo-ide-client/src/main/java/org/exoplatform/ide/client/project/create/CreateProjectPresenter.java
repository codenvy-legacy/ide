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
package org.exoplatform.ide.client.project.create;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.IDELoader;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.event.CreateProjectEvent;
import org.exoplatform.ide.client.framework.event.CreateProjectHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.paas.Paas;
import org.exoplatform.ide.client.framework.project.ProjectCreatedEvent;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.hotkeys.HotKeyHelper.KeyCode;
import org.exoplatform.ide.client.model.template.ProjectTemplate;
import org.exoplatform.ide.client.model.template.TemplateService;
import org.exoplatform.ide.client.model.template.marshal.ProjectTemplateListUnmarshaller;
import org.exoplatform.ide.client.project.deploy.DeployProjectToPaasEvent;
import org.exoplatform.ide.client.template.MigrateTemplatesEvent;
import org.exoplatform.ide.client.template.TemplatesMigratedCallback;
import org.exoplatform.ide.client.template.TemplatesMigratedEvent;
import org.exoplatform.ide.client.template.TemplatesMigratedHandler;
import org.exoplatform.ide.vfs.client.marshal.ProjectUnmarshaller;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasKeyPressHandlers;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class CreateProjectPresenter implements CreateProjectHandler, ViewClosedHandler, TemplatesMigratedHandler,
   VfsChangedHandler
{

   /**
    * Display interface, that templates view have to implement.
    */
   public interface Display extends IsView
   {

      /**
       * Get cancel button for registration click handlers.
       * 
       * @return Cancel button
       */
      HasClickHandlers getCancelButton();

      /**
       * Get next button for registration click handlers.
       * 
       * @return Next button
       */
      HasClickHandlers getNextButton();

      /**
       * Get Finish button for registration click handlers.
       * 
       * @return Finish button
       */
      HasClickHandlers getFinishButton();

      /**
       * Returns project name field.
       * 
       * @return
       */
      HasValue<String> getNameField();

      /**
       * Returns project name field.
       * 
       * @return
       */
      HasKeyPressHandlers nameTextField();

      /**
       * Get the list of selected templates in list grid.
       * 
       * @return
       */
      List<ProjectTemplate> getSelectedTemplates();

      /**
       * Get the template list grid for registration handlers.
       * 
       * @return
       */
      ListGridItem<ProjectTemplate> getTemplateListGrid();

      /**
       * Enables or disables Next button.
       * 
       * @param enabled
       */
      void setNextButtonEnabled(boolean enabled);

      /**
       * Shows or hides Next button.
       * 
       * @param visible
       */
      void setNextButtonVisible(boolean visible);

      /**
       * Enables or disables Finish button.
       * 
       * @param enabled
       */
      void setFinishButtonEnabled(boolean enabled);

      /**
       * Shows or hides Finish button.
       * 
       * @param visible
       */
      void setFinishButtonVisible(boolean visible);

      /**
       * Give focus to name field.
       */
      void focusInNameField();

      /**
       * Selects all of the text in name field.
       */
      void selectAllTextInNameField();

      /**
       * Enables or disabled project name field.
       * 
       * @param enabled
       */
      void setProjectNameFieldEnabled(boolean enabled);
   }

   protected Display display;

   private VirtualFileSystemInfo vfsInfo;

   /**
    * The list of templates to display. This list must be initialized by subclasses, because it depends on type of template (file
    * of project).
    */
   protected List<ProjectTemplate> projectTemplates;

   /**
    * The list of templates, that selected in list of templates.
    */
   protected List<ProjectTemplate> selectedTemplates;

   private boolean isTemplatesMigrated = false;

   /**
    * Comparator for sorting project templates.
    */
   private static final Comparator<ProjectTemplate> PROJECT_TEMPLATE_COMPARATOR = new ProjectTemplateComparator();

   public CreateProjectPresenter()
   {
      IDE.getInstance().addControl(new CreateProjectControl());

      IDE.addHandler(CreateProjectEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
      IDE.addHandler(TemplatesMigratedEvent.TYPE, this);
      IDE.addHandler(VfsChangedEvent.TYPE, this);
   }

   /**
    * 
    */
   public void bindDisplay()
   {
      /*
       * If name field is empty - disable create button
       */
      display.getNameField().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         public void onValueChange(ValueChangeEvent<String> event)
         {
            String value = event.getValue().trim();

            if (value == null || value.isEmpty())
            {
               display.setNextButtonEnabled(false);
               display.setFinishButtonEnabled(false);
            }
            else
            {
               display.setNextButtonEnabled(true);
               display.setFinishButtonEnabled(true);
            }
         }
      });

      display.nameTextField().addKeyPressHandler(new KeyPressHandler()
      {
         @Override
         public void onKeyPress(KeyPressEvent event)
         {
            if (KeyCode.ENTER == event.getNativeEvent().getKeyCode())
            {
               if (hasPaasForDeployment())
               {
                  goToNextStep();
               }
               else
               {
                  createProject();
               }
            }
         }
      });

      /*
       * Add click handler for create button
       */
      display.getNextButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            goToNextStep();
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

      display.getFinishButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            createProject();
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

            display.setFinishButtonVisible(!hasPaasForDeployment());
            display.setNextButtonVisible(hasPaasForDeployment());
            display.focusInNameField();
            display.selectAllTextInNameField();
         }
      });

      /*
       * Disable buttons and name field, because no template is selected
       */
      display.setNextButtonEnabled(false);
      display.setFinishButtonEnabled(false);
   }

   private void goToNextStep()
   {
      String name = display.getNameField().getValue();
      if (name == null || name.isEmpty())
      {
         name = display.getSelectedTemplates().get(0).getName();
      }

      String type = display.getSelectedTemplates().get(0).getType();
      IDE.eventBus().fireEvent(
         new DeployProjectToPaasEvent(name, type, display.getSelectedTemplates().get(0).getName()));
      IDE.getInstance().closeView(display.asView().getId());
   }

   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
      }
   }

   private boolean hasPaasForDeployment()
   {
      if (selectedTemplates == null || selectedTemplates.size() != 1)
      {
         return false;
      }

      ProjectTemplate template = selectedTemplates.get(0);
      String projectType = template.getType();

      for (Paas paas : IDE.getInstance().getPaases())
      {
         if (paas.getSupportedProjectTypes().contains(projectType))
         {
            return true;
         }
      }

      return false;
   }

   /**
    * @see org.exoplatform.ide.client.template.TemplatesMigratedHandler#onTemplatesMigrated(org.exoplatform.ide.client.template.TemplatesMigratedEvent)
    */
   @Override
   public void onTemplatesMigrated(TemplatesMigratedEvent event)
   {
      isTemplatesMigrated = true;
   }

   @Override
   public void onVfsChanged(VfsChangedEvent event)
   {
      vfsInfo = event.getVfsInfo();
   }

   /**
    * @see org.exoplatform.ide.client.framework.event.CreateProjectHandler#onCreateProject(org.exoplatform.ide.client.framework.event.CreateProjectEvent)
    */
   @Override
   public void onCreateProject(final CreateProjectEvent createProjectEvent)
   {
      if (vfsInfo == null)
      {
         Dialogs.getInstance().showError("Vfs info is not received. Can't create project.");
         return;
      }

      if (display != null)
      {
         return;
      }

      if (isTemplatesMigrated)
      {
         showCreateProjectForm(createProjectEvent.getProjectName(), createProjectEvent.getProjectType());
         return;
      }

      IDE.fireEvent(new MigrateTemplatesEvent(new TemplatesMigratedCallback()
      {
         @Override
         public void onTemplatesMigrated()
         {
            showCreateProjectForm(createProjectEvent.getProjectName(), createProjectEvent.getProjectType());
         }
      }));
   }

   private void showCreateProjectForm(String projectName, String projectType)
   {
      display = GWT.create(Display.class);
      bindDisplay();
      IDE.getInstance().openView(display.asView());
      display.getNameField().setValue(projectName != null ? projectName : "untitled");

      display.setNextButtonVisible(false);
      display.setFinishButtonVisible(true);
      display.setNextButtonEnabled(false);
      display.setFinishButtonEnabled(false);
      display.setProjectNameFieldEnabled(false);

      /*
       * Refresh template list grid
       */
      if (projectTemplates == null)
      {
         refreshTemplateList(projectType);
      }
      else
      {
         fillTemplatesListGrid(projectType);
      }
   }

   /**
    * Refresh List of the templates
    */
   private void refreshTemplateList(final String projectType)
   {
      try
      {
         TemplateService.getInstance().getProjectTemplateList(
            new AsyncRequestCallback<List<ProjectTemplate>>(new ProjectTemplateListUnmarshaller(
               new ArrayList<ProjectTemplate>()))
            {
               @Override
               protected void onSuccess(List<ProjectTemplate> result)
               {
                  projectTemplates = result;
                  Collections.sort(projectTemplates, PROJECT_TEMPLATE_COMPARATOR);
                  fillTemplatesListGrid(projectType);
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  IDE.fireEvent(new ExceptionThrownEvent(exception));
               }

            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   /**
    * Fill templates list grid.
    * 
    * @param projectType
    */
   private void fillTemplatesListGrid(final String projectType)
   {
      display.getTemplateListGrid().setValue(projectTemplates);
      if (projectTemplates != null && projectTemplates.size() > 0)
      {
         if (projectType != null)
         {
            for (ProjectTemplate template : projectTemplates)
            {
               if (template.getType().equals(projectType))
               {
                  display.getTemplateListGrid().selectItem(template);
                  updateButtonsState();
                  return;
               }
            }
         }

         ProjectTemplate templateToSelect = projectTemplates.get(0);
         display.getTemplateListGrid().selectItem(templateToSelect);

         updateButtonsState();
      }
   }

   /**
    * Updates visibility of Next and Finish buttons.
    */
   private void updateButtonsState()
   {
      display.setFinishButtonVisible(!hasPaasForDeployment());
      display.setNextButtonVisible(hasPaasForDeployment());
      display.setFinishButtonEnabled(true);
      display.setNextButtonEnabled(true);
      display.setProjectNameFieldEnabled(true);
   }

   /**
    * Creates project without deployment.
    */
   private void createProject()
   {
      final IDELoader loader = new IDELoader();
      try
      {
         String parentId = vfsInfo.getRoot().getId();
         String projectName = display.getNameField().getValue();
         String projectType = display.getSelectedTemplates().get(0).getType();
         String templateName = display.getSelectedTemplates().get(0).getName();

         loader.show();

         TemplateService.getInstance().createProjectFromTemplate(vfsInfo.getId(), parentId, projectName, templateName,
            new AsyncRequestCallback<ProjectModel>(new ProjectUnmarshaller(new ProjectModel()))
            {
               @Override
               protected void onSuccess(final ProjectModel result)
               {
                  loader.hide();
                  IDE.getInstance().closeView(display.asView().getId());
                  IDE.fireEvent(new ProjectCreatedEvent(result));
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  loader.hide();
                  IDE.fireEvent(new ExceptionThrownEvent(exception));
               }
            });
      }
      catch (RequestException e)
      {
         loader.hide();
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

}
