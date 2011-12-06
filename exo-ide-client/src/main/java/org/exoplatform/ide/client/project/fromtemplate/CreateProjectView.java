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
package org.exoplatform.ide.client.project.fromtemplate;

import com.google.gwt.uibinder.client.UiField;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.client.model.template.ProjectTemplate;

import java.util.List;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: CreateProjectView.java Dec 1, 2011 12:51:33 PM vereshchaka $
 *
 */
public class CreateProjectView extends ViewImpl  implements CreateProjectFromTemplatePresenter.Display
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
   ImageButton nextButton;
   
   @UiField
   ImageButton cancelButton;
   
   @UiField
   TextInput projectNameField;

   public CreateProjectView()
   {
      super(ID, ViewType.POPUP, TITLE, null, WIDTH, HEIGHT, false);
      add(uiBinder.createAndBindUi(this));
   }

   /**
    * @see org.exoplatform.ide.client.project.fromtemplate.CreateProjectFromTemplatePresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.client.project.fromtemplate.CreateProjectFromTemplatePresenter.Display#getCreateButton()
    */
   @Override
   public HasClickHandlers getCreateButton()
   {
      return nextButton;
   }

   /**
    * @see org.exoplatform.ide.client.project.fromtemplate.CreateProjectFromTemplatePresenter.Display#getDeleteButton()
    */
   @Override
   public HasClickHandlers getDeleteButton()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.exoplatform.ide.client.project.fromtemplate.CreateProjectFromTemplatePresenter.Display#getNameField()
    */
   @Override
   public HasValue<String> getNameField()
   {
      return projectNameField;
   }

   /**
    * @see org.exoplatform.ide.client.project.fromtemplate.CreateProjectFromTemplatePresenter.Display#getSelectedTemplates()
    */
   @Override
   public List<ProjectTemplate> getSelectedTemplates()
   {
      return templateListGrid.getSelectedItems();
   }

   /**
    * @see org.exoplatform.ide.client.project.fromtemplate.CreateProjectFromTemplatePresenter.Display#getTemplateListGrid()
    */
   @Override
   public ListGridItem<ProjectTemplate> getTemplateListGrid()
   {
      return templateListGrid;
   }

   /**
    * @see org.exoplatform.ide.client.project.fromtemplate.CreateProjectFromTemplatePresenter.Display#selectLastTemplate()
    */
   @Override
   public void selectLastTemplate()
   {
      templateListGrid.selectLastItem();
   }

   /**
    * @see org.exoplatform.ide.client.project.fromtemplate.CreateProjectFromTemplatePresenter.Display#setCreateButtonEnabled(boolean)
    */
   @Override
   public void setCreateButtonEnabled(boolean enabled)
   {
      nextButton.setEnabled(enabled);
   }

   /**
    * @see org.exoplatform.ide.client.project.fromtemplate.CreateProjectFromTemplatePresenter.Display#setDeleteButtonEnabled(boolean)
    */
   @Override
   public void setDeleteButtonEnabled(boolean enabled)
   {
      // TODO Auto-generated method stub
      
   }

   /**
    * @see org.exoplatform.ide.client.project.fromtemplate.CreateProjectFromTemplatePresenter.Display#setNameFieldEnabled(boolean)
    */
   @Override
   public void setNameFieldEnabled(boolean enabled)
   {
      projectNameField.setEnabled(enabled);
   }
}
