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
package org.exoplatform.ide.client.template.ui;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextAreaItem;
import org.exoplatform.gwtframework.ui.client.component.TextField;
import org.exoplatform.gwtframework.ui.client.component.TitleOrientation;
import org.exoplatform.gwtframework.ui.client.util.UIHelper;
import org.exoplatform.ide.client.framework.ui.DialogWindow;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.template.SaveAsTemplatePresenter;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   $
 *
 */
public class SaveAsTemplateForm extends DialogWindow implements SaveAsTemplatePresenter.Display
{

   private static final int WIDTH = 450;

   private static final int HEIGHT = 290;

   private static final String ID = "ideSaveAsTemplateForm";

   private static final String ID_SAVE_BUTTON = "ideSaveAsTemplateFormSaveButton";

   private static final String ID_CANCEL_BUTTON = "ideSaveAsTemplateFormCancelButton";

   private static final String NAME_FIELD = "ideSaveAsTemplateFormNameField";

   private static final String TYPE_FIELD = "ideSaveAsTemplateFormTypeField";

   private static final String DESCRIPTION_FIELD = "ideSaveAsTemplateFormDescriptionField";

   private static final String ID_DYNAMIC_FORM = "ideSaveAsTemplateFormDynamicForm";

   private static final int BUTTON_WIDTH = 90;

   private static final int BUTTON_HEIGHT = 22;

   private static final int FIELDS_WIDTH = 350;

   private static final String TITLE = "Save file as template";

   private TextField nameField;

   private TextAreaItem descriptionField;

   private TextField typeField;

//   private IButton saveButton;
//   private IButton cancelButton;

   private ImageButton saveButton;
   
   private ImageButton cancelButton;

   private SaveAsTemplatePresenter presenter;

   public SaveAsTemplateForm(HandlerManager eventBus, File file)
   {
      super(WIDTH, HEIGHT, ID);
      setTitle(TITLE);

      VerticalPanel centerLayout = new VerticalPanel();
      centerLayout.setWidth("100%");
      centerLayout.setHeight("100%");
      centerLayout.setSpacing(10);
      centerLayout.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
      
      centerLayout.add(createItemsForm());
      centerLayout.add(createButtonsLayout());

      setWidget(centerLayout);
      show();

      presenter = new SaveAsTemplatePresenter(eventBus, file);
      presenter.bindDisplay(this);

      nameField.focusInItem();

      UIHelper.setAsReadOnly(typeField.getName());
   }

   private VerticalPanel createItemsForm()
   {
      VerticalPanel form = new VerticalPanel();
      form.getElement().setId(ID_DYNAMIC_FORM);
      form.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
      form.setWidth(FIELDS_WIDTH+"px");
      form.setSpacing(3);

      typeField = new TextField(TYPE_FIELD, "Type:");
      typeField.setTitleOrientation(TitleOrientation.TOP);
      typeField.setWidth(FIELDS_WIDTH);
      typeField.setHeight(20);

      nameField = new TextField(NAME_FIELD, "Name:");
      nameField.setTitleOrientation(TitleOrientation.TOP);
      nameField.setWidth(FIELDS_WIDTH);
      nameField.setHeight(20);

      descriptionField = new TextAreaItem(DESCRIPTION_FIELD, "Description:");
      descriptionField.setTitleOrientation(TitleOrientation.TOP);
      descriptionField.setHeight(60);
      descriptionField.setWidth(FIELDS_WIDTH);
      
      form.add(typeField);
      form.add(nameField);
      form.add(descriptionField);
      
      return form;
   }

   private HorizontalPanel createButtonsLayout()
   {
      HorizontalPanel buttonsLayout = new HorizontalPanel();
      buttonsLayout.setHeight(BUTTON_HEIGHT + 20 +"px");
      buttonsLayout.setSpacing(5);
      buttonsLayout.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);

      saveButton = new ImageButton("Save", "ok");
      saveButton.setId(ID_SAVE_BUTTON);

      cancelButton = new ImageButton("Cancel", "cancel");
      cancelButton.setId(ID_CANCEL_BUTTON);

      buttonsLayout.add(saveButton);
      buttonsLayout.add(cancelButton);

      return buttonsLayout;
   }

   @Override
   public void destroy()
   {
      presenter.destroy();
      super.destroy();
   }

   public void closeForm()
   {
      destroy();
   }

   /**
    * @see org.exoplatform.ide.client.template.SaveAsTemplatePresenter.Display#getDescriptionField()
    */
   public HasValue<String> getDescriptionField()
   {
      return descriptionField;
   }

   /**
    * @see org.exoplatform.ide.client.template.SaveAsTemplatePresenter.Display#getNameField()
    */
   public HasValue<String> getNameField()
   {
      return nameField;
   }

   /**
    * @see org.exoplatform.ide.client.template.SaveAsTemplatePresenter.Display#getSaveButton()
    */
   public HasClickHandlers getSaveButton()
   {
      return saveButton;
   }

   /**
    * @see org.exoplatform.ide.client.template.SaveAsTemplatePresenter.Display#getTypeField()
    */
   public HasValue<String> getTypeField()
   {
      return typeField;
   }

   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.client.template.SaveAsTemplatePresenter.Display#disableSaveButton()
    */
   public void disableSaveButton()
   {
      //saveButton.disable();
      saveButton.setEnabled(false);
   }

   /**
    * @see org.exoplatform.ide.client.template.SaveAsTemplatePresenter.Display#enableSaveButton()
    */
   public void enableSaveButton()
   {
      //saveButton.enable();
      saveButton.setEnabled(true);
   }

}
