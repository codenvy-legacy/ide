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
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasKeyPressHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.TreeGridItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextAreaItem;
import org.exoplatform.gwtframework.ui.client.component.TextField;
import org.exoplatform.gwtframework.ui.client.component.TitleOrientation;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.model.template.Template;
import org.exoplatform.ide.client.template.ui.TemplateTree;

import java.util.List;

/**
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class CreateProjectTemplateView extends ViewImpl implements CreateProjectTemplatePresenter.Display
{
   
   public static final int WIDTH = 530;

   public static final int HEIGHT = 350;

   private static final String ID = "ideCreateProjectTemplateForm";

   private static final String ID_CREATE_BUTTON = "ideCreateProjectTemplateFormCreateButton";

   private static final String ID_CANCEL_BUTTON = "ideCreateProjectTemplateFormCancelButton";

   private static final String ID_ADD_FOLDER_BUTTON = "ideCreateProjectTemplateFormAddFolderButton";

   private static final String ID_ADD_FILE_BUTTON = "ideCreateProjectTemplateFormAddFileButton";

   private static final String ID_DELETE_BUTTON = "ideCreateProjectTemplateFormDeleteButton";

   private static final String TEMPLATE_NAME_FIELD = "ideCreateProjectTemplateFormNameField";

   private static final String DESCRIPTION_FIELD = "ideCreateProjectTemplateFormDescriptionField";
   
   private static final String TITLE = IDE.TEMPLATE_CONSTANT.createProjectTemplateTitle();
   
   @UiField
   ImageButton createButton;

   @UiField
   ImageButton cancelButton;

   @UiField
   ImageButton addFolderButton;

   @UiField
   ImageButton addFileButton;

   @UiField
   ImageButton deleteButton;

   @UiField
   TemplateTree templateTreeGrid;

   @UiField
   TextField nameField;

   @UiField
   TextAreaItem descriptionField;
   
   @UiField
   ScrollPanel scrollPanel;
   
   interface CreateProjectTemplateViewUiBinder extends UiBinder<Widget, CreateProjectTemplateView>
   {
   }
   
   private static CreateProjectTemplateViewUiBinder uiBinder = GWT.create(CreateProjectTemplateViewUiBinder.class);

   public CreateProjectTemplateView()
   {
      super(ID, "popup", TITLE, new Image(IDEImageBundle.INSTANCE.ok()), WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));
      
      createButton.setButtonId(ID_CREATE_BUTTON);
      cancelButton.setButtonId(ID_CANCEL_BUTTON);
      addFolderButton.setButtonId(ID_ADD_FOLDER_BUTTON);
      addFileButton.setButtonId(ID_ADD_FILE_BUTTON);
      deleteButton.setButtonId(ID_DELETE_BUTTON);
      nameField.setName(TEMPLATE_NAME_FIELD);
      nameField.setTitleOrientation(TitleOrientation.LEFT);
      descriptionField.setName(DESCRIPTION_FIELD);
      descriptionField.setTitleOrientation(TitleOrientation.LEFT);
      
      DOM.setStyleAttribute(scrollPanel.getElement(), "zIndex", "0");
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateProjectTemplatePresenter.Display#getNameField()
    */
   public HasValue<String> getNameField()
   {
      return nameField;
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateProjectTemplatePresenter.Display#getDescriptionField()
    */
   public HasValue<String> getDescriptionField()
   {
      return descriptionField;
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateProjectTemplatePresenter.Display#getCreateButton()
    */
   public HasClickHandlers getCreateButton()
   {
      return createButton;
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateProjectTemplatePresenter.Display#getCancelButton()
    */
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateProjectTemplatePresenter.Display#enableCreateButton()
    */
   public void enableCreateButton()
   {
      createButton.setEnabled(true);
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateProjectTemplatePresenter.Display#disableCreateButton()
    */
   public void disableCreateButton()
   {
      createButton.setEnabled(false);
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateProjectTemplatePresenter.Display#getTemplateTreeGrid()
    */
   public TreeGridItem<Template> getTemplateTreeGrid()
   {
      return templateTreeGrid;
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateProjectTemplatePresenter.Display#getAddFolderButton()
    */
   public HasClickHandlers getAddFolderButton()
   {
      return addFolderButton;
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateProjectTemplatePresenter.Display#getAddFileButton()
    */
   public HasClickHandlers getAddFileButton()
   {
      return addFileButton;
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateProjectTemplatePresenter.Display#getDeleteButton()
    */
   public HasClickHandlers getDeleteButton()
   {
      return deleteButton;
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateProjectTemplatePresenter.Display#getTreeGridSelection()
    */
   public List<Template> getTreeGridSelection()
   {
      return templateTreeGrid.getSelectedItems();
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateProjectTemplatePresenter.Display#enableAddFolderButton()
    */
   public void enableAddFolderButton()
   {
      addFolderButton.setEnabled(true);
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateProjectTemplatePresenter.Display#disableAddFolderButton()
    */
   public void disableAddFolderButton()
   {
      addFolderButton.setEnabled(false);
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateProjectTemplatePresenter.Display#enableAddFileButton()
    */
   public void enableAddFileButton()
   {
      addFileButton.setEnabled(true);
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateProjectTemplatePresenter.Display#disableAddFileButton()
    */
   public void disableAddFileButton()
   {
      addFileButton.setEnabled(false);
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateProjectTemplatePresenter.Display#enableDeleteButton()
    */
   public void enableDeleteButton()
   {
      deleteButton.setEnabled(true);
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateProjectTemplatePresenter.Display#disableDeleteButton()
    */
   public void disableDeleteButton()
   {
      deleteButton.setEnabled(false);
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateProjectTemplatePresenter.Display#selectTemplate(org.exoplatform.ide.client.model.template.Template)
    */
   public void selectTemplate(Template template)
   {
      templateTreeGrid.selectTemplate(template);
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateProjectTemplatePresenter.Display#getNameFieldKeyPressed()
    */
   public HasKeyPressHandlers getNameFieldKeyPressed()
   {
      return (HasKeyPressHandlers)nameField;
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateProjectTemplatePresenter.Display#updateTree()
    */
   public void updateTree()
   {
      templateTreeGrid.updateTree();
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateProjectTemplatePresenter.Display#setRootNodeName(java.lang.String)
    */
   public void setRootNodeName(String name)
   {
      templateTreeGrid.setRootNodeName(name);
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateProjectTemplatePresenter.Display#getTemplateLocationInProject(org.exoplatform.ide.client.model.template.Template)
    */
   public String getTemplateLocationInProject(Template template)
   {
      return templateTreeGrid.getTemplateLocation(template);
   }

}
