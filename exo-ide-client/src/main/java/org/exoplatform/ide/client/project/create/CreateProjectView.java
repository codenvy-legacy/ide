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
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SingleSelectionModel;

import org.exoplatform.gwtframework.ui.client.CellTreeResource;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.Label;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.paas.PaaS;
import org.exoplatform.ide.client.framework.template.ProjectTemplate;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;

import java.util.List;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jul 24, 2012 4:41:51 PM anya $
 * 
 */
public class CreateProjectView extends ViewImpl implements CreateProjectPresenter.Display
{
   private static final String ID = "eXoCreateNewProjectView";

   private static final String CREATE_TITLE = IDE.TEMPLATE_CONSTANT.createProjectFromTemplateTitle();

   private static final int HEIGHT = 350;

   private static final int WIDTH = 660;

   private final String PROJECT_TREE_ID = "eXoCreateProjectViewProjectTree";

   private final String BACK_BUTTON_ID = "eXoCreateNewProjectViewBackButton";

   private final String NEXT_BUTTON_ID = "eXoCreateNewProjectViewNextButton";

   private final String FINISH_BUTTON_ID = "eXoCreateNewProjectViewFinishButton";

   private final String CANCEL_BUTTON_ID = "eXoCreateNewProjectViewCancelButton";

   private final String NAME_FIELD_ID = "eXoCreateNewProjectViewNameField";

   private static CreateProjectViewUiBinder uiBinder = GWT.create(CreateProjectViewUiBinder.class);

   interface CreateProjectViewUiBinder extends UiBinder<Widget, CreateProjectView>
   {
   }

   @UiField
   ImageButton cancelButton;

   @UiField
   ImageButton nextButton;

   @UiField
   ImageButton backButton;

   @UiField
   ImageButton finishButton;

   @UiField
   TextInput projectNameField;

   @UiField
   Label errorLabel;

   @UiField
   ProjectTemplateGrid templatesGrid;

   @UiField
   TargetGrid targetGrid;

   @UiField
   DockLayoutPanel createProjectStep;

   @UiField
   FlowPanel deployProjectStep;

   @UiField
   DockLayoutPanel chooseTemplateStep;

   @UiField
   ScrollPanel treePanel;

   private CellTree.Resources res = GWT.create(CellTreeResource.class);

   private SingleSelectionModel<Object> selectionModel;

   private ProjectTypeTreeViewModel projectTypeTreeViewModel;

   private CellTree projectTypeTree;

   public CreateProjectView()
   {
      super(ID, ViewType.MODAL, CREATE_TITLE, null, WIDTH, HEIGHT, false);
      add(uiBinder.createAndBindUi(this));

      selectionModel = new SingleSelectionModel<Object>();
      projectTypeTreeViewModel = new ProjectTypeTreeViewModel(selectionModel);
      projectTypeTree = new CellTree(projectTypeTreeViewModel, null, res);

      // Keyboard is disabled because of the selection problem (when selecting programmatically), if
      // KeyboardSelectionPolicy.BOUND_TO_SELECTION is set
      // and because of the focus border, when use KeyboardSelectionPolicy.ENABLED.
      projectTypeTree.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);

      projectTypeTree.getElement().setId(PROJECT_TREE_ID);
      treePanel.add(projectTypeTree);

      backButton.setButtonId(BACK_BUTTON_ID);
      nextButton.setButtonId(NEXT_BUTTON_ID);
      finishButton.setButtonId(FINISH_BUTTON_ID);
      cancelButton.setButtonId(CANCEL_BUTTON_ID);

      projectNameField.setName(NAME_FIELD_ID);

      deployProjectStep.setVisible(false);
   }

   /**
    * @see org.exoplatform.ide.client.project.create.CreateProjectPresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.client.project.create.CreateProjectPresenter.Display#getNextButton()
    */
   @Override
   public HasClickHandlers getNextButton()
   {
      return nextButton;
   }

   /**
    * @see org.exoplatform.ide.client.project.create.CreateProjectPresenter.Display#getFinishButton()
    */
   @Override
   public HasClickHandlers getFinishButton()
   {
      return finishButton;
   }

   /**
    * @see org.exoplatform.ide.client.project.create.CreateProjectPresenter.Display#getNameField()
    */
   @Override
   public HasValue<String> getNameField()
   {
      return projectNameField;
   }

   /**
    * @see org.exoplatform.ide.client.project.create.CreateProjectPresenter.Display#enableNextButton(boolean)
    */
   @Override
   public void enableNextButton(boolean enabled)
   {
      nextButton.setEnabled(enabled);
   }

   /**
    * @see org.exoplatform.ide.client.project.create.CreateProjectPresenter.Display#enableFinishButton(boolean)
    */
   @Override
   public void enableFinishButton(boolean enabled)
   {
      finishButton.setEnabled(enabled);
   }

   /**
    * @see org.exoplatform.ide.client.project.create.CreateProjectPresenter.Display#showCreateProjectStep()
    */
   @Override
   public void showCreateProjectStep()
   {
      createProjectStep.setVisible(true);
      chooseTemplateStep.setVisible(false);
      deployProjectStep.setVisible(false);

      backButton.setVisible(false);
      finishButton.setVisible(true);
      nextButton.setVisible(true);
   }

   /**
    * @see org.exoplatform.ide.client.project.create.CreateProjectPresenter.Display#showDeployProjectStep()
    */
   @Override
   public void showDeployProjectStep()
   {
      createProjectStep.setVisible(false);
      chooseTemplateStep.setVisible(false);
      deployProjectStep.setVisible(true);

      backButton.setVisible(true);
      finishButton.setVisible(true);
      nextButton.setVisible(false);
   }

   /**
    * @see org.exoplatform.ide.client.project.create.CreateProjectPresenter.Display#showChooseTemlateStep()
    */
   @Override
   public void showChooseTemlateStep()
   {
      chooseTemplateStep.setVisible(true);
      createProjectStep.setVisible(false);
      deployProjectStep.setVisible(false);

      backButton.setVisible(true);
      finishButton.setVisible(true);
      nextButton.setVisible(true);
   }

   /**
    * @see org.exoplatform.ide.client.project.create.CreateProjectPresenter.Display#getBackButton()
    */
   @Override
   public HasClickHandlers getBackButton()
   {
      return backButton;
   }

   /**
    * @see org.exoplatform.ide.client.project.create.CreateProjectPresenter.Display#setDeployView(com.google.gwt.user.client.ui.Composite)
    */
   @Override
   public void setDeployView(Composite deployView)
   {
      deployProjectStep.clear();
      deployProjectStep.add(deployView);
   }

   /**
    * @see org.exoplatform.ide.client.project.create.CreateProjectPresenter.Display#getTargetGrid()
    */
   @Override
   public ListGridItem<PaaS> getTargetGrid()
   {
      return targetGrid;
   }

   /**
    * @see org.exoplatform.ide.client.project.create.CreateProjectPresenter.Display#setProjectTypes(java.util.List)
    */
   @Override
   public void setProjectTypes(List<Object> values)
   {
      projectTypeTreeViewModel.getDataProvider().getList().clear();
      projectTypeTreeViewModel.getDataProvider().setList(values);
   }

   /**
    * @see org.exoplatform.ide.client.project.create.CreateProjectPresenter.Display#getSingleSelectionModel()
    */
   @Override
   public SingleSelectionModel<Object> getSingleSelectionModel()
   {
      return selectionModel;
   }

   /**
    * @see org.exoplatform.ide.client.project.create.CreateProjectPresenter.Display#selectTarget(org.exoplatform.ide.client.framework.paas.recent.PaaS)
    */
   @Override
   public void selectTarget(PaaS target)
   {
      targetGrid.selectItem(target);
   }

   /**
    * @see org.exoplatform.ide.client.project.create.CreateProjectPresenter.Display#getTemplatesGrid()
    */
   @Override
   public ListGridItem<ProjectTemplate> getTemplatesGrid()
   {
      return templatesGrid;
   }

   /**
    * @see org.exoplatform.ide.client.project.create.CreateProjectPresenter.Display#selectTemplate(org.exoplatform.ide.client.framework.template.ProjectTemplate)
    */
   @Override
   public void selectTemplate(ProjectTemplate projectTemplate)
   {
      templatesGrid.selectItem(projectTemplate);
   }

   /**
    * @see org.exoplatform.ide.client.project.create.CreateProjectPresenter.Display#getErrorLabel()
    */
   @Override
   public HasValue<String> getErrorLabel()
   {
      return errorLabel;
   }
}
