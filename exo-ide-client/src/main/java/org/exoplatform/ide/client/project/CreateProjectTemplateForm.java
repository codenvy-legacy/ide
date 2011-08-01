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

import java.util.List;

import org.exoplatform.gwtframework.ui.client.api.TreeGridItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextAreaItem;
import org.exoplatform.gwtframework.ui.client.component.TextField;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.framework.ui.IDEDialogWindow;
import org.exoplatform.ide.client.model.template.Template;
import org.exoplatform.ide.client.template.ui.TemplateTree;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasKeyPressHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class CreateProjectTemplateForm extends IDEDialogWindow implements CreateProjectTemplatePresenter.Display
{
   
   public static final int WIDTH = 530;

   public static final int HEIGHT = 350;

   public static final int TEXT_FIELD_WIDTH = 450;

   private static final String ID = "ideCreateProjectTemplateForm";

   private static final String ID_CREATE_BUTTON = "ideCreateProjectTemplateFormCreateButton";

   private static final String ID_CANCEL_BUTTON = "ideCreateProjectTemplateFormCancelButton";

   private static final String ID_ADD_FOLDER_BUTTON = "ideCreateProjectTemplateFormAddFolderButton";

   private static final String ID_ADD_FILE_BUTTON = "ideCreateProjectTemplateFormAddFileButton";

   private static final String ID_DELETE_BUTTON = "ideCreateProjectTemplateFormDeleteButton";

   private static final String ID_NAME_FIELDS_FORM = "ideCreateProjectTemplateFormNameFieldsForm";

   private static final String TEMPLATE_NAME_FIELD = "ideCreateProjectTemplateFormNameField";

   private static final String DESCRIPTION_FIELD = "ideCreateProjectTemplateFormDescriptionField";

   private static final String ADD_FOLDER_BUTTON = IDE.TEMPLATE_CONSTANT.createProjectTemplateAddFolderBtn();

   private static final String ADD_FILE_BUTTON = IDE.TEMPLATE_CONSTANT.createProjectTemplateAddFileBtn();

   private static final String DELETE_BUTTON = IDE.IDE_LOCALIZATION_CONSTANT.deleteButton();
   
   private static final String TITLE = IDE.TEMPLATE_CONSTANT.createProjectTemplateTitle();
   
   private static final String TEXT_FIELD_NAME = IDE.TEMPLATE_CONSTANT.createProjectTemplateTextFieldName();
   
   private static final String TEXT_AREA_DESCRIPTION = IDE.TEMPLATE_CONSTANT.createProjectTemplateTextAreaDescription();

   private static final String BUTTONS_WIDTH = "120px";

   private static final String BUTTONS_HEIGHT = "22px";

   private VerticalPanel windowLayout;

   private ImageButton createButton;

   private ImageButton cancelButton;

   private ImageButton addFolderButton;

   private ImageButton addFileButton;

   private ImageButton deleteButton;

   private TemplateTree templateTreeGrid;

   private TextField templateNameField;

   private TextAreaItem templateDescriptionField;

   private CreateProjectTemplatePresenter presenter;

   public CreateProjectTemplateForm(HandlerManager eventBus, List<Template> templateList)
   {
      super(WIDTH, HEIGHT, ID);

      setTitle(TITLE);
      //TODO
      //setCanDragResize(true);
      setCanMaximize(true);

      windowLayout = new VerticalPanel();
      windowLayout.setSpacing(15);
      setWidget(windowLayout);
      windowLayout.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

      createFieldsForm();

      createFileTemplateListLayout();

      createButtonsForm();

      show();

      presenter = new CreateProjectTemplatePresenter(eventBus, templateList);
      presenter.bindDisplay(this);
   }

   private void createFieldsForm()
   {
      VerticalPanel fieldsLayout = new VerticalPanel();
      fieldsLayout.setHeight("35px");
      fieldsLayout.setWidth("100%");
      fieldsLayout.getElement().setId(ID_NAME_FIELDS_FORM);
      fieldsLayout.setSpacing(1);

      templateNameField = new TextField("TemplateName", TEXT_FIELD_NAME);
      templateNameField.setName(TEMPLATE_NAME_FIELD);
      templateNameField.setWidth(TEXT_FIELD_WIDTH);
      templateNameField.setTitleWidth(63);

      templateDescriptionField = new TextAreaItem("Description");
      templateDescriptionField.setName(DESCRIPTION_FIELD);
      templateDescriptionField.setTitle(TEXT_AREA_DESCRIPTION);
      //  TODO templateDescriptionField.setTitleOrientation(TitleOrientation.LEFT);
      // templateDescriptionField.setColSpan(2);
      templateDescriptionField.setHeight(40);
      templateDescriptionField.setWidth(TEXT_FIELD_WIDTH);

      //  form.setColWidths("*", "" + TEXT_FIELD_WIDTH);
      fieldsLayout.add(templateNameField);
      fieldsLayout.add(templateDescriptionField);
      //      fieldsLayout.addMember(form);

      windowLayout.add(fieldsLayout);
   }

   private void createFileTemplateListLayout()
   {
      HorizontalPanel projectLayout = new HorizontalPanel();
      templateTreeGrid = new TemplateTree();
      ScrollPanel treeWrapper = new ScrollPanel(templateTreeGrid);
      treeWrapper.setSize("360px", "150px");
      DOM.setStyleAttribute(treeWrapper.getElement(), "zIndex", "0");
      DOM.setStyleAttribute(treeWrapper.getElement(), "border", "1px solid #A7ABB4");

      projectLayout.add(treeWrapper);

      projectLayout.setSpacing(10);

      projectLayout.add(getActionsButtons());

      windowLayout.add(projectLayout);
   }

   private VerticalPanel getActionsButtons()
   {
      VerticalPanel buttonsLayout = new VerticalPanel();
      buttonsLayout.setWidth(BUTTONS_WIDTH);
      buttonsLayout.setHeight("100%");

      buttonsLayout.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
      buttonsLayout.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);

      buttonsLayout.setSpacing(15);

      addFolderButton = new ImageButton(ADD_FOLDER_BUTTON);
      addFolderButton.setButtonId(ID_ADD_FOLDER_BUTTON);
      addFolderButton.setWidth(BUTTONS_WIDTH);
      addFolderButton.setHeight(BUTTONS_HEIGHT);
      addFolderButton.setImage(new Image(Images.Buttons.ADD));

      addFileButton = new ImageButton(ADD_FILE_BUTTON);
      addFileButton.setButtonId(ID_ADD_FILE_BUTTON);
      addFileButton.setWidth(BUTTONS_WIDTH);
      addFileButton.setHeight(BUTTONS_HEIGHT);
      addFileButton.setImage(new Image(Images.Buttons.ADD));

      deleteButton = new ImageButton(DELETE_BUTTON);
      deleteButton.setButtonId(ID_DELETE_BUTTON);
      deleteButton.setWidth(BUTTONS_WIDTH);
      deleteButton.setHeight(BUTTONS_HEIGHT);
      deleteButton.setImage(new Image(Images.Buttons.REMOVE));

      buttonsLayout.add(addFolderButton);
      buttonsLayout.add(addFileButton);
      buttonsLayout.add(deleteButton);

      return buttonsLayout;
   }

   private void createButtonsForm()
   {
      HorizontalPanel buttonsLayout = new HorizontalPanel();
      buttonsLayout.setHeight("22px");
      buttonsLayout.setSpacing(5);

      createButton = new ImageButton(IDE.IDE_LOCALIZATION_CONSTANT.createButton());
      createButton.setButtonId(ID_CREATE_BUTTON);
      createButton.setWidth("75px");
      createButton.setHeight("22px");
      createButton.setImage(new Image(Images.Buttons.YES));

      cancelButton = new ImageButton(IDE.IDE_LOCALIZATION_CONSTANT.cancelButton());
      cancelButton.setButtonId(ID_CANCEL_BUTTON);
      cancelButton.setWidth("75px");
      cancelButton.setHeight("22px");
      cancelButton.setImage(new Image(Images.Buttons.NO));

      buttonsLayout.add(createButton);
      buttonsLayout.add(cancelButton);

      windowLayout.add(buttonsLayout);

   }

   @Override
   public void destroy()
   {
      presenter.destroy();
      super.destroy();
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateProjectTemplatePresenter.Display#getNameField()
    */
   public HasValue<String> getNameField()
   {
      return templateNameField;
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateProjectTemplatePresenter.Display#getDescriptionField()
    */
   public HasValue<String> getDescriptionField()
   {
      return templateDescriptionField;
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
    * @see org.exoplatform.ide.client.template.CreateProjectTemplatePresenter.Display#closeForm()
    */
   public void closeForm()
   {
      destroy();
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
      addFileButton.setEnabled(true);
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
      return (HasKeyPressHandlers)templateNameField;
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
