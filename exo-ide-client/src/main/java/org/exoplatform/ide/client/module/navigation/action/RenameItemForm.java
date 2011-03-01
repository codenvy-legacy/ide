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
package org.exoplatform.ide.client.module.navigation.action;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasKeyPressHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;
import com.smartgwt.client.widgets.form.fields.SpacerItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;

import org.exoplatform.gwtframework.ui.client.component.ComboBoxField;
import org.exoplatform.gwtframework.ui.client.component.DynamicForm;
import org.exoplatform.gwtframework.ui.client.component.IButton;
import org.exoplatform.gwtframework.ui.client.component.TextField;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.framework.ui.DialogWindow;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.Item;

import java.util.List;
import java.util.Map;

/**
 * Form for renaming files and folders.
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class RenameItemForm extends DialogWindow implements RenameItemPresenter.Display
{

   public static final int WIDTH = 400;

   public static final int HEIGHT = 220;
   
   public static final int BUTTON_WIDTH = 90;

   public static final int BUTTON_HEIGHT = 22;

   public static final int HEIGHT_SMALL = 150;

   private static final String ID = "ideRenameItemForm";

   private static final String ID_DYNAMIC_FORM = "ideRenameItemFormDynamicForm";

   private static final String ID_RENAME_BUTTON = "ideRenameItemFormRenameButton";

   private static final String ID_CANCEL_BUTTON = "ideRenameItemFormCancelButton";

   private static final String RENAME_FIELD = "ideRenameItemFormRenameField";

   private static final String MIME_TYPE_FIELD = "ideRenameItemFormMimeTypeField";

   private VerticalPanel vLayout;

   private TextField itemNameField;

   private ComboBoxField mimeTypesField;

   private IButton renameButton;

   private IButton cancelButton;

   private RenameItemPresenter presenter;

   private StaticTextItem caption3;

   public RenameItemForm(HandlerManager eventBus, List<Item> selectedItems, Map<String, File> openedFiles,
      Map<String, String> lockTokens)
   {
      super(eventBus, WIDTH, (selectedItems.get(0) instanceof File) ? HEIGHT : HEIGHT_SMALL, ID);

      setTitle("Rename item");

      vLayout = new VerticalPanel();
      vLayout.setWidth("100%");
      vLayout.setHeight("100%");
      addItem(vLayout);

      createFieldForm(selectedItems.get(0) instanceof File);
      createButtons();

      show();

      presenter = new RenameItemPresenter(eventBus, selectedItems, openedFiles, lockTokens);
      presenter.bindDisplay(this);

      addCloseClickHandler(new CloseClickHandler()
      {
         public void onCloseClick(CloseClientEvent event)
         {
            destroy();
         }
      });
   }

   private void createFieldForm(boolean isFile)
   {
      DynamicForm paramsForm = new DynamicForm();
      paramsForm.setID(ID_DYNAMIC_FORM);
      paramsForm.setWidth(340);
      paramsForm.setPadding(1);

      itemNameField = new TextField(RENAME_FIELD, "Rename item to:");
      itemNameField.setName(RENAME_FIELD);
      itemNameField.setWidth(340);
      itemNameField.setHeight(22);
      if (isFile)
      {
         StaticTextItem caption2 = new StaticTextItem();
         caption2.setDefaultValue("Select mime-type");
         caption2.setShowTitle(false);
         caption2.setColSpan(2);

         SpacerItem delimiter3 = new SpacerItem();
         delimiter3.setHeight(2);

         mimeTypesField = new ComboBoxField();
         mimeTypesField.setName(MIME_TYPE_FIELD);
         mimeTypesField.setWidth(340);
         mimeTypesField.setColSpan(2);
         mimeTypesField.setCompleteOnTab(true);
         mimeTypesField.setPickListHeight(100);

         caption3 = new StaticTextItem();
         caption3.setShowTitle(false);
         caption3.setColSpan(2);
         //TODO fix when combobox ready:
         //paramsForm.setFields(delimiter1, caption, delimiter, itemNameField, delimiter2, caption2, delimiter3,
          //  mimeTypesField, caption3);
      }
      else
      {
         paramsForm.add(itemNameField);
      }
      itemNameField.focusInItem();

      vLayout.add(paramsForm);
      vLayout.setCellHorizontalAlignment(paramsForm, HorizontalPanel.ALIGN_CENTER);
      vLayout.setCellVerticalAlignment(paramsForm, HorizontalPanel.ALIGN_MIDDLE);
   }

   private void createButtons()
   {
      HorizontalPanel buttonsLayout = new HorizontalPanel();
      buttonsLayout.setHeight(BUTTON_HEIGHT+"px");
      buttonsLayout.setSpacing(5);

      renameButton = new IButton("Rename");
      renameButton.setID(ID_RENAME_BUTTON);
      renameButton.setWidth(BUTTON_WIDTH);
      renameButton.setHeight(BUTTON_HEIGHT);
      renameButton.setIcon(Images.Buttons.OK);

      cancelButton = new IButton("Cancel");
      cancelButton.setID(ID_CANCEL_BUTTON);
      cancelButton.setWidth(BUTTON_WIDTH);
      cancelButton.setHeight(BUTTON_HEIGHT);
      cancelButton.setIcon(Images.Buttons.NO);

      buttonsLayout.add(renameButton);
      buttonsLayout.add(cancelButton);

      vLayout.add(buttonsLayout);
      vLayout.setCellHorizontalAlignment(buttonsLayout, HorizontalPanel.ALIGN_CENTER);
      vLayout.setCellVerticalAlignment(buttonsLayout, HorizontalPanel.ALIGN_TOP);
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

   public HasValue<String> getItemNameField()
   {
      return itemNameField;
   }

   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   public HasClickHandlers getRenameButton()
   {
      return renameButton;
   }

   public HasKeyPressHandlers getItemNameFieldKeyPressHandler()
   {
      return (HasKeyPressHandlers)itemNameField;
   }

   public void setMimeTypes(String[] mimeTypes)
   {
      mimeTypesField.setValueMap(mimeTypes);
   }

   public HasValue<String> getMimeType()
   {
      return mimeTypesField;
   }

   public void disableMimeTypeSelect()
   {
      mimeTypesField.setDisabled(true);
   }

   public void enableMimeTypeSelect()
   {
      mimeTypesField.setDisabled(false);
   }

   public void setDefaultMimeType(String mimeType)
   {
      mimeTypesField.setDefaultValue(mimeType);
   }

   /**
    * @see org.exoplatform.ide.client.module.navigation.action.RenameItemPresenter.Display#enableRenameButton()
    */
   public void enableRenameButton()
   {
      renameButton.enable();
   }

   /**
    * @see org.exoplatform.ide.client.module.navigation.action.RenameItemPresenter.Display#disableRenameButton()
    */
   public void disableRenameButton()
   {
      renameButton.disable();
   }

   public void addLabel(String style, String text)
   {
      if (text == null)
      {
         caption3.setValue("");
         caption3.setHeight(0);
      }
      else
      {
         caption3.setHeight(12);
         caption3.setValue("<font color=\"#7d7d7d\">" + text + "</font>");
      }
   }
}
