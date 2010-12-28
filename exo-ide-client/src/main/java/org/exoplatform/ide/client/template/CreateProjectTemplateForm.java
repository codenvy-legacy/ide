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
package org.exoplatform.ide.client.template;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasKeyPressHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.StatefulCanvas;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ToolbarItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;

import org.exoplatform.gwtframework.ui.client.api.TreeGridItem;
import org.exoplatform.gwtframework.ui.client.smartgwt.component.IButton;
import org.exoplatform.gwtframework.ui.client.smartgwt.component.TextAreaItem;
import org.exoplatform.gwtframework.ui.client.smartgwt.component.TextField;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.framework.ui.DialogWindow;
import org.exoplatform.ide.client.model.template.Template;

import java.util.List;

/**
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class CreateProjectTemplateForm extends DialogWindow implements CreateProjectTemplatePresenter.Display
{
   public static final int WIDTH = 550;

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
   
   private static final String ADD_FOLDER_BUTTON = "Add Folder";
   
   private static final String ADD_FILE_BUTTON = "Add File";
   
   private static final String DELETE_BUTTON = "Delete";
   
   private static final int BUTTONS_WIDTH = 120;
   
   private static final int BUTTONS_HEIGHT = 22;
   
   private VLayout windowLayout;

   private IButton createButton;

   private IButton cancelButton;
   
   private IButton addFolderButton;

   private IButton addFileButton;
   
   private IButton deleteButton;

   private TemplateTreeGrid<Template> templateTreeGrid;

   private TextField templateNameField;
   
   private TextAreaItem templateDescriptionField;
   
   private CreateProjectTemplatePresenter presenter;
   
   public CreateProjectTemplateForm(HandlerManager eventBus, List<Template> templateList)
   {
      super(eventBus, WIDTH, HEIGHT, ID);
      
      
      setTitle("Create project template");
      setCanDragResize(true);
      setShowMaximizeButton(true);

      windowLayout = new VLayout();
      windowLayout.setMargin(15);
      addItem(windowLayout);

      createFieldsForm();

      Layout l = new Layout();
      l.setHeight(15);
      windowLayout.addMember(l);
      
      createFileTemplateListLayout();
      
      Layout l2 = new Layout();
      l2.setHeight(10);
      windowLayout.addMember(l2);
      
      createButtonsForm();

      show();

      presenter = new CreateProjectTemplatePresenter(eventBus, templateList);
      presenter.bindDisplay(this);

      addCloseClickHandler(new CloseClickHandler()
      {
         public void onCloseClick(CloseClientEvent event)
         {
            destroy();
         }
      });
      
   }
   
   private void createFieldsForm()
   {
      VLayout fieldsLayout = new VLayout();
      fieldsLayout.setHeight(35);
      fieldsLayout.setWidth100();

      DynamicForm form = new DynamicForm();
      form.setID(ID_NAME_FIELDS_FORM);
      templateNameField = new TextField("TemplateName","Name");
      templateNameField.setName(TEMPLATE_NAME_FIELD);
      templateNameField.setWidth(TEXT_FIELD_WIDTH);
      templateNameField.setWrapTitle(false);
      
      templateDescriptionField = new TextAreaItem("Description");
      templateDescriptionField.setName(DESCRIPTION_FIELD);
      templateDescriptionField.setTitle("Description");
      templateDescriptionField.setTitleOrientation(TitleOrientation.LEFT);
      templateDescriptionField.setColSpan(2);
      templateDescriptionField.setHeight(40);
      templateDescriptionField.setWidth(TEXT_FIELD_WIDTH);

      form.setColWidths("*", "" + TEXT_FIELD_WIDTH);
      form.setItems(templateNameField, templateDescriptionField);
      fieldsLayout.addMember(form);
      
      windowLayout.addMember(fieldsLayout);
   }
   
   private void createFileTemplateListLayout()
   {
      HLayout projectLayout = new HLayout();
      
      
      templateTreeGrid = new TemplateTreeGrid<Template>();
      templateTreeGrid.setShowHeader(false);
      templateTreeGrid.setLeaveScrollbarGap(false);
      templateTreeGrid.setShowOpenIcons(true);
      templateTreeGrid.setEmptyMessage("Enter name of project template!");

      templateTreeGrid.setHeight100();
      templateTreeGrid.setWidth100();
      
      projectLayout.addMember(templateTreeGrid);
      
      Layout l = new Layout();
      l.setWidth(10);
      projectLayout.addMember(l);
      
      projectLayout.addMember(getActionsButtons());

      windowLayout.addMember(projectLayout);
   }
   
   private VLayout getActionsButtons()
   {
      VLayout buttonsLayout = new VLayout();
      buttonsLayout.setWidth(BUTTONS_WIDTH);
      buttonsLayout.setHeight100();

      buttonsLayout.setAlign(Alignment.CENTER);
      buttonsLayout.setAlign(VerticalAlignment.TOP);

      buttonsLayout.setMembersMargin(15);

      addFolderButton = new IButton(ADD_FOLDER_BUTTON);
      addFolderButton.setID(ID_ADD_FOLDER_BUTTON);
      addFolderButton.setWidth(BUTTONS_WIDTH);
      addFolderButton.setHeight(BUTTONS_HEIGHT);
      addFolderButton.setIcon(Images.Buttons.ADD);

      addFileButton = new IButton(ADD_FILE_BUTTON);
      addFileButton.setID(ID_ADD_FILE_BUTTON);
      addFileButton.setWidth(BUTTONS_WIDTH);
      addFileButton.setHeight(BUTTONS_HEIGHT);
      addFileButton.setIcon(Images.Buttons.ADD);

      deleteButton = new IButton(DELETE_BUTTON);
      deleteButton.setID(ID_DELETE_BUTTON);
      deleteButton.setWidth(BUTTONS_WIDTH);
      deleteButton.setHeight(BUTTONS_HEIGHT);
      deleteButton.setIcon(Images.Buttons.DELETE);

      buttonsLayout.addMember(addFolderButton);
      buttonsLayout.addMember(addFileButton);
      buttonsLayout.addMember(deleteButton);

      return buttonsLayout;
   }

   private void createButtonsForm()
   {
      DynamicForm buttonsForm = new DynamicForm();
      buttonsForm.setLayoutAlign(Alignment.CENTER);

      createButton = new IButton("Create");
      createButton.setID(ID_CREATE_BUTTON);
      createButton.setWidth(75);
      createButton.setHeight(22);
      createButton.setIcon(Images.Buttons.YES);

      cancelButton = new IButton("Cancel");
      cancelButton.setID(ID_CANCEL_BUTTON);
      cancelButton.setWidth(75);
      cancelButton.setHeight(22);
      cancelButton.setIcon(Images.Buttons.NO);

      ToolbarItem tbi = new ToolbarItem();

      StatefulCanvas delimiter1 = new StatefulCanvas();
      delimiter1.setWidth(2);

      tbi.setButtons(createButton, delimiter1, cancelButton);
      buttonsForm.setFields(tbi);
      buttonsForm.setAutoWidth();
      
      windowLayout.addMember(buttonsForm);
      
      Layout buttonsBottomLayout = new Layout();
      buttonsBottomLayout.setHeight(15);
      windowLayout.addMember(buttonsBottomLayout);
   }
   
   @Override
   protected void onDestroy()
   {
      presenter.destroy();
      super.onDestroy();
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
      createButton.setDisabled(false);
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateProjectTemplatePresenter.Display#disableCreateButton()
    */
   public void disableCreateButton()
   {
      createButton.setDisabled(true);
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
      addFolderButton.setDisabled(false);
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateProjectTemplatePresenter.Display#disableAddFolderButton()
    */
   public void disableAddFolderButton()
   {
      addFolderButton.setDisabled(true);
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateProjectTemplatePresenter.Display#enableAddFileButton()
    */
   public void enableAddFileButton()
   {
      addFileButton.setDisabled(false);
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateProjectTemplatePresenter.Display#disableAddFileButton()
    */
   public void disableAddFileButton()
   {
      addFileButton.setDisabled(true);
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateProjectTemplatePresenter.Display#enableDeleteButton()
    */
   public void enableDeleteButton()
   {
      deleteButton.setDisabled(false);
   }

   /**
    * @see org.exoplatform.ide.client.template.CreateProjectTemplatePresenter.Display#disableDeleteButton()
    */
   public void disableDeleteButton()
   {
      deleteButton.setDisabled(true);
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
