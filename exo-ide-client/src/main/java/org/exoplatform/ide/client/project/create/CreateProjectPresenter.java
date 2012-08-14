/*
 * Copyright (C) 2012 eXo Platform SAS.
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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

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
import org.exoplatform.ide.client.framework.paas.recent.DeployResultHandler;
import org.exoplatform.ide.client.framework.paas.recent.PaaS;
import org.exoplatform.ide.client.framework.project.Language;
import org.exoplatform.ide.client.framework.project.ProjectCreatedEvent;
import org.exoplatform.ide.client.framework.project.ProjectType;
import org.exoplatform.ide.client.framework.template.ProjectTemplate;
import org.exoplatform.ide.client.framework.template.TemplateService;
import org.exoplatform.ide.client.framework.template.marshal.ProjectTemplateListUnmarshaller;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.util.ProjectResolver;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ChildrenUnmarshaller;
import org.exoplatform.ide.vfs.client.marshal.ProjectUnmarshaller;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jul 24, 2012 3:38:19 PM anya $
 * 
 */
public class CreateProjectPresenter implements CreateProjectHandler, VfsChangedHandler, ViewClosedHandler,
   DeployResultHandler
{
   interface Display extends IsView
   {
      void setProjectTypes(List<Object> values);

      SingleSelectionModel<Object> getSingleSelectionModel();

      HasClickHandlers getCancelButton();

      HasClickHandlers getBackButton();

      HasClickHandlers getNextButton();

      HasClickHandlers getFinishButton();

      HasValue<String> getNameField();

      HasValue<String> getErrorLabel();

      ListGridItem<PaaS> getTargetGrid();

      ListGridItem<ProjectTemplate> getTemplatesGrid();

      void selectTarget(PaaS target);

      void selectTemplate(ProjectTemplate projectTemplate);

      void enableNextButton(boolean enabled);

      void enableFinishButton(boolean enabled);

      void showCreateProjectStep();

      void showDeployProjectStep();

      void showChooseTemlateStep();

      void setDeployView(Composite deployView);
   }

   private Display display;

   private boolean isDeployStep = false;

   private boolean isChooseTemplateStep = false;

   private VirtualFileSystemInfo vfsInfo;

   private ProjectType selectedProjectType;

   private ProjectTemplate selectedTemplate;

   private List<ProjectTemplate> allProjectTemplates;

   private List<ProjectTemplate> availableProjectTemplates = new ArrayList<ProjectTemplate>();

   private PaaS currentPaaS;

   private PaaS selectedTarget;

   public CreateProjectPresenter()
   {
      IDE.getInstance().addControl(new CreateProjectControl());

      IDE.addHandler(CreateProjectEvent.TYPE, this);
      IDE.addHandler(VfsChangedEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
   }

   public void bindDisplay()
   {
      display.getSingleSelectionModel().addSelectionChangeHandler(new SelectionChangeEvent.Handler()
      {

         @Override
         public void onSelectionChange(SelectionChangeEvent event)
         {
            if (display.getSingleSelectionModel().getSelectedObject() != null)
            {
               if (display.getSingleSelectionModel().getSelectedObject() instanceof ProjectType)
               {
                  selectedProjectType = (ProjectType)display.getSingleSelectionModel().getSelectedObject();
                  List<PaaS> values =
                     getAvailableTargets((ProjectType)display.getSingleSelectionModel().getSelectedObject());
                  display.getTargetGrid().setValue(values);
                  display.selectTarget(values.get(0));
               }
               else if (display.getSingleSelectionModel().getSelectedObject() instanceof LanguageItem)
               {
                  selectedProjectType = null;
                  display.getTargetGrid().setValue(new ArrayList<PaaS>());
               }
            }
            else
            {
               List<PaaS> values = new ArrayList<PaaS>();
               values.add(new NoneTarget());
               display.getTargetGrid().setValue(values);
               display.selectTarget(values.get(0));
            }
            updateButtonState();
         }

      });

      display.getNameField().addValueChangeHandler(new ValueChangeHandler<String>()
      {

         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            updateButtonState();
         }
      });

      display.getCancelButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
      });

      display.getBackButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            goBack();
         }
      });

      display.getNextButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            if (!isChooseTemplateStep)
            {
               validateProjectName(display.getNameField().getValue());
            }
            else
            {
               goNext();
            }
         }
      });

      display.getTargetGrid().addSelectionHandler(new SelectionHandler<PaaS>()
      {

         @Override
         public void onSelection(SelectionEvent<PaaS> event)
         {
            selectedTarget = event.getSelectedItem();
            availableProjectTemplates = getProjectTemplates(selectedProjectType, selectedTarget);
            updateButtonState();
         }
      });

      display.getTemplatesGrid().addSelectionHandler(new SelectionHandler<ProjectTemplate>()
      {

         @Override
         public void onSelection(SelectionEvent<ProjectTemplate> event)
         {
            selectedTemplate = event.getSelectedItem();
            updateButtonState();
         }
      });

      display.getFinishButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            if (isDeployStep)
            {
               doDeploy((availableProjectTemplates.size() == 1) ? availableProjectTemplates.get(0) : selectedTemplate);
            }
            else if (isChooseTemplateStep)
            {
               createProject(selectedTemplate);
            }
            else if (availableProjectTemplates.size() == 1)
            {
               createProject(availableProjectTemplates.get(0));
            }
            else
            {
               createProject(null);
            }
         }
      });
   }

   /**
    * Update the enabled/disabled state of the buttons.
    */
   private void updateButtonState()
   {
      boolean enabled =
         display.getNameField().getValue() != null && !display.getNameField().getValue().isEmpty()
            && selectedProjectType != null;
      boolean noneDeploy = (selectedTarget == null || selectedTarget instanceof NoneTarget);

      if (isChooseTemplateStep)
      {
         display.enableFinishButton(enabled && noneDeploy && selectedTemplate != null);
         display.enableNextButton(enabled && !noneDeploy && selectedTemplate != null);
      }
      else if (isDeployStep)
      {
         display.enableFinishButton(true);
      }
      else
      {
         boolean hasTemplatesToChoose = availableProjectTemplates != null && availableProjectTemplates.size() > 1;
         display.enableFinishButton(enabled && noneDeploy && !hasTemplatesToChoose);
         display.enableNextButton(enabled && (!noneDeploy || hasTemplatesToChoose));
      }
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

   /**
    * @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework.application.event.VfsChangedEvent)
    */
   @Override
   public void onVfsChanged(VfsChangedEvent event)
   {
      this.vfsInfo = event.getVfsInfo();
   }

   /**
    * @see org.exoplatform.ide.client.framework.event.CreateProjectHandler#onCreateProject(org.exoplatform.ide.client.framework.event.CreateProjectEvent)
    */
   @Override
   public void onCreateProject(CreateProjectEvent event)
   {
      if (display == null)
      {
         display = GWT.create(Display.class);
         IDE.getInstance().openView(display.asView());
         bindDisplay();
      }
      display.showCreateProjectStep();
      isDeployStep = false;
      getProjectTemplates();
   }

   /**
    * Get the list of available project templates.
    */
   private void getProjectTemplates()
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
                  allProjectTemplates = result;
                  List<Object> tree = formProjectTree(result);
                  display.setProjectTypes(tree);
                  if (!tree.isEmpty())
                  {
                     display.getSingleSelectionModel().setSelected(tree.get(0), true);
                  }

                  if (display.getNameField().getValue() == null || display.getNameField().getValue().isEmpty())
                  {
                     display.getNameField().setValue("untitled");
                  }
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
    * Go to previous step.
    */
   private void goBack()
   {
      if (isDeployStep)
      {
         isDeployStep = false;
         if (availableProjectTemplates != null && availableProjectTemplates.size() > 1 && !selectedTarget.isProvidesTemplate())
         {
            goToTemplatesStep();
         }
         else
         {
            goToProjectStep();
         }
      }
      else if (isChooseTemplateStep)
      {
         isChooseTemplateStep = false;
         goToProjectStep();
      }
      updateButtonState();
   }

   /**
    * Go to next step.
    */
   private void goNext()
   {
      if (isChooseTemplateStep)
      {
         isChooseTemplateStep = false;
         goToDeployStep();
      }
      else
      {
         if (availableProjectTemplates != null && availableProjectTemplates.size() > 1 && !selectedTarget.isProvidesTemplate())
         {
            goToTemplatesStep();
         }
         else
         {
            goToDeployStep();
         }
      }
      updateButtonState();
   }

   /**
    * Move to deploy project step.
    */
   private void goToDeployStep()
   {
      isDeployStep = true;

      String projectName = display.getNameField().getValue();
      for (PaaS paas : IDE.getInstance().getPaaSes())
      {
         if (paas.getId().equals(selectedTarget.getId()))
         {
            currentPaaS = paas;
            if (paas.getPaaSActions() != null)
            {
               display.showDeployProjectStep();
               isDeployStep = true;
               display.setDeployView(paas.getPaaSActions().getDeployView(projectName, selectedProjectType));
               display.enableFinishButton(true);
            }
            else
            {
               Dialogs.getInstance().showError(
                  org.exoplatform.ide.client.IDE.TEMPLATE_CONSTANT.noRegistedDeployAction(paas.getTitle()));
            }
            return;
         }
      }
   }

   /**
    * Move to choosing project template step.
    */
   private void goToTemplatesStep()
   {
      isChooseTemplateStep = true;
      display.getTemplatesGrid().setValue(availableProjectTemplates);
      display.showChooseTemlateStep();
   }

   /**
    * Move to project's data step.
    */
   private void goToProjectStep()
   {
      display.showCreateProjectStep();
   }

   /**
    * Get the list of targets, where project with pointed project type can be deployed.
    * 
    * @param projectType
    * @return {@link List} of {@link PaaS}
    */
   private List<PaaS> getAvailableTargets(ProjectType projectType)
   {
      List<PaaS> values = new ArrayList<PaaS>();
      values.add(new NoneTarget());
      for (PaaS paas : IDE.getInstance().getPaaSes())
      {
         if (paas.getSupportedProjectTypes().contains(projectType))
         {
            values.add(paas);
         }
      };
      return values;
   }

   private void createProject(ProjectTemplate projectTemplate)
   {
      if (projectTemplate == null)
      {
         Dialogs.getInstance().showError(org.exoplatform.ide.client.IDE.TEMPLATE_CONSTANT.noProjectTempate());
         return;
      }

      if (vfsInfo == null || vfsInfo.getRoot() == null)
      {
         Dialogs.getInstance().showError(
            org.exoplatform.ide.client.IDE.ERRORS_CONSTANT.createProjectErrorVFSInfoNotSets());
         return;
      }

      try
      {
         String parentId = vfsInfo.getRoot().getId();
         String projectName = display.getNameField().getValue();
         IDELoader.getInstance().setMessage(org.exoplatform.ide.client.IDE.TEMPLATE_CONSTANT.creatingProject());
         IDELoader.getInstance().show();
         TemplateService.getInstance().createProjectFromTemplate(vfsInfo.getId(), parentId, projectName,
            projectTemplate.getName(),
            new AsyncRequestCallback<ProjectModel>(new ProjectUnmarshaller(new ProjectModel()))
            {
               @Override
               protected void onSuccess(final ProjectModel result)
               {
                  IDELoader.getInstance().hide();
                  IDE.getInstance().closeView(display.asView().getId());
                  IDE.fireEvent(new ProjectCreatedEvent(result));
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  IDELoader.getInstance().hide();
                  IDE.fireEvent(new ExceptionThrownEvent(exception));
               }
            });
      }
      catch (RequestException e)
      {
         IDELoader.getInstance().hide();
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.paas.recent.DeployResultHandler#onDeployFinished(boolean)
    */
   @Override
   public void onDeployFinished(boolean success)
   {
      if (success && display != null)
      {
         IDE.getInstance().closeView(display.asView().getId());
      }
   }

   private void doDeploy(ProjectTemplate projectTemplate)
   {
      if (currentPaaS != null)
      {
         if (projectTemplate != null || currentPaaS.isProvidesTemplate())
         {
            currentPaaS.getPaaSActions().deploy(projectTemplate, this);
         }
         else
         {
            Dialogs.getInstance().showError(
               org.exoplatform.ide.client.IDE.TEMPLATE_CONSTANT.noProjectTemplateForTarget(currentPaaS.getTitle()));
         }
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.paas.recent.DeployResultHandler#onProjectCreated(org.exoplatform.ide.vfs.client.model.ProjectModel)
    */
   @Override
   public void onProjectCreated(ProjectModel project)
   {
      IDE.fireEvent(new ProjectCreatedEvent(project));
      if (display != null)
      {
         IDE.getInstance().closeView(display.asView().getId());
      }
   }

   /**
    * Prepare project tree to be displayed, where project types are grouped by language.
    * 
    * @param projectTemplates available project templates
    * @return {@link List}
    */
   private List<Object> formProjectTree(List<ProjectTemplate> projectTemplates)
   {
      List<Object> tree = new ArrayList<Object>();

      // Display project types, that are not included in any language group:
      for (ProjectTemplate projectTemplate : projectTemplates)
      {
         boolean found = false;
         for (Language lang : Language.values())
         {
            if (ProjectResolver.getProjectTypesByLanguage(lang).contains(
               ProjectType.fromValue(projectTemplate.getType())))
            {
               found = true;
               break;
            }
         }
         if (!found)
         {
            tree.add(ProjectType.fromValue(projectTemplate.getType()));
         }
      }

      for (Language lang : Language.values())
      {
         List<ProjectType> types = ProjectResolver.getProjectTypesByLanguage(lang);
         List<ProjectType> projectTypes = new ArrayList<ProjectType>();
         for (ProjectTemplate projectTemplate : projectTemplates)
         {
            ProjectType projectType = ProjectType.fromValue(projectTemplate.getType());
            if (types.contains(projectType) && !projectTypes.contains(projectType))
            {
               projectTypes.add(projectType);
            }
         }
         if (!projectTypes.isEmpty())
         {
            tree.add(new LanguageItem(lang, projectTypes));
         }
      }
      return tree;
   }

   /**
    * Get the list of project templates, that are suitable to pointed project type and deploy target.
    * 
    * @param projectType project's type
    * @param target deploy target
    * @return {@link List} list of {@link ProjectTemplate}
    */
   private List<ProjectTemplate> getProjectTemplates(ProjectType projectType, PaaS target)
   {
      List<ProjectTemplate> templates = new ArrayList<ProjectTemplate>();

      // Get templates by project's type:
      if (target instanceof NoneTarget)
      {
         for (ProjectTemplate projectTemplate : allProjectTemplates)
         {
            if (projectTemplate.getType().equals(projectType.value()))
            {
               templates.add(projectTemplate);
            }
         }
         return templates;
      }

      // Get templates by project type and it's deploy target:
      for (ProjectTemplate projectTemplate : allProjectTemplates)
      {
         if (projectTemplate.getType().equals(projectType.value())
            && (projectTemplate.getTargets() == null || projectTemplate.getTargets().contains(target.getId())))
         {
            templates.add(projectTemplate);
         }
      }
      return templates;
   }

   private class NoneTarget extends PaaS
   {
      public NoneTarget()
      {
         super("none", "None", null, null);
      }
   }

   /**
    * Validates project name for existence.
    * 
    * @param projectName project's name
    */
   private void validateProjectName(final String projectName)
   {
      try
      {
         VirtualFileSystem.getInstance().getChildren(VirtualFileSystem.getInstance().getInfo().getRoot(),
            ItemType.PROJECT, new AsyncRequestCallback<List<Item>>(new ChildrenUnmarshaller(new ArrayList<Item>()))
            {
               @Override
               protected void onSuccess(List<Item> result)
               {
                  for (Item item : result)
                  {
                     if (projectName.equals(item.getName()))
                     {
                        display.getErrorLabel().setValue(
                           org.exoplatform.ide.client.IDE.TEMPLATE_CONSTANT
                              .createProjectFromTemplateProjectExists(projectName));
                        return;
                     }
                  }
                  display.getErrorLabel().setValue("");
                  goNext();
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  IDE.fireEvent(new ExceptionThrownEvent(exception, "Searching of projects failed."));
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e, "Searching of projects failed."));
      }

   }
}
