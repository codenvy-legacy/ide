/*
 * Copyright (C) 2011 eXo Platform SAS.
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
import com.google.gwt.event.dom.client.HasKeyPressHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.template.ProjectTemplate;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;

import java.util.List;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: CreateProjectView.java Dec 1, 2011 12:51:33 PM vereshchaka $
 * 
 */
public class CreateProjectView extends ViewImpl implements CreateProjectPresenter.Display
{
   private static final String ID = "CreateNewProjectView";

   private static final String TITLE = IDE.TEMPLATE_CONSTANT.createProjectFromTemplateTitle();

   private static final int HEIGHT = 345;

   private static final int WIDTH = 550;

   interface CreateProjectViewUiBinder extends UiBinder<Widget, CreateProjectView>
   {
   }

   /**
    * UIBinder instance
    */
   private static CreateProjectViewUiBinder uiBinder = GWT.create(CreateProjectViewUiBinder.class);

   @UiField
   ProjectTemplateListGrid templateListGrid;

   @UiField
   ImageButton cancelButton;

   @UiField
   ImageButton nextButton;

   @UiField
   ImageButton finishButton;

   @UiField
   TextInput projectNameField;

   public CreateProjectView()
   {
      super(ID, ViewType.POPUP, TITLE, null, WIDTH, HEIGHT, false);
      add(uiBinder.createAndBindUi(this));
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
    * @see org.exoplatform.ide.client.project.create.CreateProjectPresenter.Display#getNameField()
    */
   @Override
   public HasValue<String> getNameField()
   {
      return projectNameField;
   }

   /**
    * @see org.exoplatform.ide.client.project.create.CreateProjectPresenter.Display#getSelectedTemplates()
    */
   @Override
   public List<ProjectTemplate> getSelectedTemplates()
   {
      return templateListGrid.getSelectedItems();
   }

   /**
    * @see org.exoplatform.ide.client.project.create.CreateProjectPresenter.Display#getTemplateListGrid()
    */
   @Override
   public ListGridItem<ProjectTemplate> getTemplateListGrid()
   {
      return templateListGrid;
   }

   /**
    * @see org.exoplatform.ide.client.project.create.CreateProjectPresenter.Display#setNextButtonEnabled(boolean)
    */
   @Override
   public void setNextButtonEnabled(boolean enabled)
   {
      nextButton.setEnabled(enabled);
   }

   /**
    * @see org.exoplatform.ide.client.project.create.CreateProjectPresenter.Display#focusInNameField()
    */
   @Override
   public void focusInNameField()
   {
      projectNameField.focus();
   }

   /**
    * @see org.exoplatform.ide.client.project.create.CreateProjectPresenter.Display#selectAllTextInNameField()
    */
   @Override
   public void selectAllTextInNameField()
   {
      projectNameField.selectAll();
   }

   @Override
   public HasKeyPressHandlers nameTextField()
   {
      return projectNameField;
   }

   @Override
   public HasClickHandlers getFinishButton()
   {
      return finishButton;
   }

   @Override
   public void setNextButtonVisible(boolean visible)
   {
      nextButton.setVisible(visible);
   }

   @Override
   public void setFinishButtonVisible(boolean visible)
   {
      finishButton.setVisible(visible);
   }

   @Override
   public void setFinishButtonEnabled(boolean enabled)
   {
      finishButton.setEnabled(enabled);
   }

   @Override
   public void setProjectNameFieldEnabled(boolean enabled)
   {
      projectNameField.setEnabled(enabled);
   }

}
