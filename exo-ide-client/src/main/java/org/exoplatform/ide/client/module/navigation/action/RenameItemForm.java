/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.client.module.navigation.action;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasKeyPressHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.StatefulCanvas;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SpacerItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.ToolbarItem;
import com.smartgwt.client.widgets.layout.VLayout;

import org.exoplatform.gwtframework.ui.client.smartgwt.component.ComboBoxField;
import org.exoplatform.gwtframework.ui.client.smartgwt.component.IButton;
import org.exoplatform.gwtframework.ui.client.smartgwt.component.TextField;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.framework.ui.DialogWindow;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.Item;

import java.util.List;
import java.util.Map;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class RenameItemForm extends DialogWindow implements RenameItemPresenter.Display
{

   public static final int WIDTH = 400;

   public static final int HEIGHT = 220;
   
   public static final int HEIGHT_SMALL = 150;

   private static final String ID = "ideRenameItemForm";
   
   private static final String ID_DYNAMIC_FORM = "ideRenameItemFormDynamicForm";
   
   private static final String ID_RENAME_BUTTON = "ideRenameItemFormRenameButton";
   
   private static final String ID_CANCEL_BUTTON = "ideRenameItemFormCancelButton";
   
   private static final String RENAME_FIELD = "ideRenameItemFormRenameField";
   
   private static final String MIME_TYPE_FIELD = "ideRenameItemFormMimeTypeField";

   private VLayout vLayout;

   private TextField itemNameField;
   
   private ComboBoxField mimeTypesField;

   private IButton renameButton;

   private IButton cancelButton;

   private RenameItemPresenter presenter;
   
   private StaticTextItem caption3;

   public RenameItemForm(HandlerManager eventBus, List<Item> selectedItems, Map<String, File> openedFiles, Map<String, String> lockTokens)
   {
      super(eventBus, WIDTH, (selectedItems.get(0) instanceof File) ? HEIGHT : HEIGHT_SMALL, ID);
      
      setTitle("Rename item");

      vLayout = new VLayout();
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
      paramsForm.setLayoutAlign(Alignment.CENTER);
      paramsForm.setPadding(1);
      paramsForm.setAutoFocus(true);
      
      SpacerItem delimiter1 = new SpacerItem();
      delimiter1.setColSpan(2);
      delimiter1.setHeight(5);

      StaticTextItem caption = new StaticTextItem();
      caption.setDefaultValue("Rename item to:");
      caption.setShowTitle(false);
      caption.setColSpan(2);

      SpacerItem delimiter = new SpacerItem();
      delimiter.setColSpan(2);
      delimiter.setHeight(5);

      itemNameField = new TextField();
      itemNameField.setName(RENAME_FIELD);
      itemNameField.setShowTitle(false);
      itemNameField.setWidth(340);
      itemNameField.setColSpan(6);
      
      SpacerItem delimiter2 = new SpacerItem();
      delimiter2.setHeight(6);
      
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
         mimeTypesField.setShowTitle(false);
         mimeTypesField.setColSpan(2);
         mimeTypesField.setCompleteOnTab(true);
         mimeTypesField.setPickListHeight(100);
         
         caption3 = new StaticTextItem();
         caption3.setShowTitle(false);
         caption3.setColSpan(2);
         
         paramsForm.setFields(delimiter1, caption, delimiter, itemNameField, delimiter2, caption2, delimiter3, mimeTypesField, caption3);
      }
      else
      {
         paramsForm.setFields(delimiter1, caption, delimiter, itemNameField, delimiter2);
      }
      paramsForm.focusInItem(itemNameField);

      vLayout.addMember(paramsForm);
   }

   private void createButtons()
   {
      DynamicForm buttonsForm = new DynamicForm();
      buttonsForm.setPadding(1);
      buttonsForm.setHeight(22);
      buttonsForm.setLayoutAlign(Alignment.CENTER);

      renameButton = new IButton("Rename");
      renameButton.setID(ID_RENAME_BUTTON);
      renameButton.setWidth(90);
      renameButton.setHeight(22);
      renameButton.setIcon(Images.Buttons.OK);

      cancelButton = new IButton("Cancel");
      cancelButton.setID(ID_CANCEL_BUTTON);
      cancelButton.setWidth(90);
      cancelButton.setHeight(22);
      cancelButton.setIcon(Images.Buttons.NO);

      ToolbarItem tbi = new ToolbarItem();
      StatefulCanvas delimiter1 = new StatefulCanvas();
      delimiter1.setWidth(3);
      tbi.setButtons(renameButton, delimiter1, cancelButton);
      buttonsForm.setFields(tbi);

      buttonsForm.setAutoWidth();
      vLayout.addMember(buttonsForm);
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
      renameButton.setDisabled(false);
   }

   /**
    * @see org.exoplatform.ide.client.module.navigation.action.RenameItemPresenter.Display#disableRenameButton()
    */
   public void disableRenameButton()
   {
      renameButton.setDisabled(true);
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
