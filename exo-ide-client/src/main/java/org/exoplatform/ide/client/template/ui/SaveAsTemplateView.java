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
import org.exoplatform.gwtframework.ui.client.component.TextAreaInput;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.template.SaveAsTemplatePresenter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   $
 *
 */
public class SaveAsTemplateView extends ViewImpl implements SaveAsTemplatePresenter.Display
{

   private static final int WIDTH = 450;

   private static final int HEIGHT = 290;

   private static final String ID = "ideSaveAsTemplateForm";

   private static final String SAVE_BUTTON_ID = "ideSaveAsTemplateFormSaveButton";

   private static final String CANCEL_BUTTON_ID = "ideSaveAsTemplateFormCancelButton";

   private static final String NAME_FIELD = "ideSaveAsTemplateFormNameField";

   private static final String TYPE_FIELD = "ideSaveAsTemplateFormTypeField";

   private static final String DESCRIPTION_FIELD = "ideSaveAsTemplateFormDescriptionField";

   private static final String TITLE = IDE.TEMPLATE_CONSTANT.saveAsTemplateTitle();

   @UiField
   TextInput nameField;

   @UiField
   TextAreaInput descriptionField;

   @UiField
   TextInput typeField;

   @UiField
   ImageButton saveButton;

   @UiField
   ImageButton cancelButton;

   interface SaveAsTemplateViewUiBinder extends UiBinder<Widget, SaveAsTemplateView>
   {
   }

   private static SaveAsTemplateViewUiBinder uiBinder = GWT.create(SaveAsTemplateViewUiBinder.class);

   public SaveAsTemplateView()
   {
      super(ID, "popup", TITLE, new Image(IDEImageBundle.INSTANCE.ok()), WIDTH, HEIGHT);
      setCloseOnEscape(true);
      add(uiBinder.createAndBindUi(this));

      nameField.setName(NAME_FIELD);
      descriptionField.setName(DESCRIPTION_FIELD);
      typeField.setName(TYPE_FIELD);
      saveButton.setButtonId(SAVE_BUTTON_ID);
      cancelButton.setButtonId(CANCEL_BUTTON_ID);
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
      saveButton.setEnabled(false);
   }

   /**
    * @see org.exoplatform.ide.client.template.SaveAsTemplatePresenter.Display#enableSaveButton()
    */
   public void enableSaveButton()
   {
      saveButton.setEnabled(true);
   }

   /**
    * @see org.exoplatform.ide.client.template.SaveAsTemplatePresenter.Display#focusInNameField()
    */
   @Override
   public void focusInNameField()
   {
      nameField.focus();
   }

}
