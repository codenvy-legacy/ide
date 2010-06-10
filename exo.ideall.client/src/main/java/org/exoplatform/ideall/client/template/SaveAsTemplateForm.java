/*
 * Copyright (C) 2009 eXo Platform SAS.
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
package org.exoplatform.ideall.client.template;

import org.exoplatform.gwtframework.ui.client.smartgwt.component.IButton;
import org.exoplatform.gwtframework.ui.client.smartgwt.component.TextAreaItem;
import org.exoplatform.gwtframework.ui.client.smartgwt.component.TextField;
import org.exoplatform.gwtframework.ui.client.util.UIHelper;
import org.exoplatform.ideall.client.Images;
import org.exoplatform.ideall.client.component.DialogWindow;
import org.exoplatform.ideall.vfs.api.File;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   $
 *
 */
public class SaveAsTemplateForm extends DialogWindow implements SaveAsTemplatePresenter.Display
{

   private static final int WIDTH = 450;

   private static final int HEIGHT = 270;

   private static final int BUTTON_WIDTH = 90;

   private static final int BUTTON_HEIGHT = 22;

   private static final int FIELDS_WIDTH = 350;

   private static final String TITLE = "Save file as template";

   private final String SAVE = "Save";

   private final String CANCEL = "Cancel";

   private TextField nameField;

   private TextAreaItem descriptionField;

   private TextField typeField;

   private IButton saveButton;

   private IButton cancelButton;

   private SaveAsTemplatePresenter presenter;

   public SaveAsTemplateForm(HandlerManager eventBus, File file)
   {
      super(eventBus, WIDTH, HEIGHT);      
      setTitle(TITLE);

      VLayout centerLayout = new VLayout();
      centerLayout.setWidth100();
      centerLayout.setHeight100();
      centerLayout.setPadding(10);
      centerLayout.setMembersMargin(20);

      centerLayout.addMember(createItemsForm());
      centerLayout.addMember(createButtonsLayout());

      addItem(centerLayout);
      show();

      presenter = new SaveAsTemplatePresenter(eventBus, file);
      presenter.bindDisplay(this);

      nameField.focusInItem();

      UIHelper.setAsReadOnly(typeField.getName());

      addCloseClickHandler(new CloseClickHandler()
      {
         public void onCloseClick(CloseClientEvent event)
         {
            destroy();
         }
      });
   }

   private DynamicForm createItemsForm()
   {
      DynamicForm form = new DynamicForm();
      //form.setCellBorder(1);

      form.setLayoutAlign(Alignment.CENTER);
      form.setAlign(Alignment.RIGHT);
      //form.setCellSpacing(5);
      form.setTitleWidth(40);
      form.setAutoWidth();

      StaticTextItem typeTitle = new StaticTextItem();
      typeTitle.setShowTitle(false);
      typeTitle.setDefaultValue("Type:");
      typeTitle.setColSpan(2);

      typeField = new TextField();
      typeField.setShowTitle(false);
      typeField.setColSpan(2);
      typeField.setWidth(FIELDS_WIDTH);

      //      LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();
      //
      //      valueMap.put("application/xml", "XML");
      //      valueMap.put("text/html", "HTML");
      //      valueMap.put("script/groovy", "Groovy");
      //      valueMap.put("text/css", "CSS");
      //      valueMap.put("text/javascript", "javascript");
      //
      //      typeField.setValueMap(valueMap);
      //      typeField.setDefaultToFirstOption(true);
      //      typeField.setType("select");

      StaticTextItem nameTitle = new StaticTextItem();
      nameTitle.setShowTitle(false);
      nameTitle.setDefaultValue("Name:");
      nameTitle.setColSpan(2);

      nameField = new TextField();
      nameField.setShowTitle(false);
      nameField.setColSpan(2);
      nameField.setWidth(FIELDS_WIDTH);

      StaticTextItem descriptionTitle = new StaticTextItem();
      descriptionTitle.setShowTitle(false);
      descriptionTitle.setDefaultValue("Description:");
      descriptionTitle.setColSpan(2);

      descriptionField = new TextAreaItem();
      //descriptionField.setTitle(DESCRIPTION);
      descriptionField.setShowTitle(false);
      //descriptionField.setTitleOrientation(TitleOrientation.TOP);
      descriptionField.setColSpan(2);
      descriptionField.setHeight(60);
      descriptionField.setWidth(FIELDS_WIDTH);

      form.setItems(typeTitle, typeField, nameTitle, nameField, descriptionTitle, descriptionField);
      return form;
   }

   private HLayout createButtonsLayout()
   {
      HLayout buttonsLayout = new HLayout();
      buttonsLayout.setAutoWidth();
      buttonsLayout.setAutoHeight();
      buttonsLayout.setLayoutAlign(Alignment.CENTER);
      buttonsLayout.setMembersMargin(10);

      saveButton = new IButton(SAVE);

      saveButton.setWidth(BUTTON_WIDTH);
      saveButton.setHeight(BUTTON_HEIGHT);
      saveButton.setIcon(Images.Buttons.OK);

      cancelButton = new IButton(CANCEL);
      cancelButton.setWidth(BUTTON_WIDTH);
      cancelButton.setHeight(BUTTON_HEIGHT);
      cancelButton.setIcon(Images.Buttons.NO);

      buttonsLayout.addMember(saveButton);
      buttonsLayout.addMember(cancelButton);

      return buttonsLayout;
   }

   @Override
   protected void onDestroy()
   {
      presenter.destroy();
      super.onDestroy();
   }

   public void closeForm()
   {
      destroy();
   }

   /**
    * @see org.exoplatform.ideall.client.template.SaveAsTemplatePresenter.Display#getDescriptionField()
    */
   public HasValue<String> getDescriptionField()
   {
      return descriptionField;
   }

   /**
    * @see org.exoplatform.ideall.client.template.SaveAsTemplatePresenter.Display#getNameField()
    */
   public HasValue<String> getNameField()
   {
      return nameField;
   }

   /**
    * @see org.exoplatform.ideall.client.template.SaveAsTemplatePresenter.Display#getSaveButton()
    */
   public HasClickHandlers getSaveButton()
   {
      return saveButton;
   }

   /**
    * @see org.exoplatform.ideall.client.template.SaveAsTemplatePresenter.Display#getTypeField()
    */
   public HasValue<String> getTypeField()
   {
      return typeField;
   }

   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

}
