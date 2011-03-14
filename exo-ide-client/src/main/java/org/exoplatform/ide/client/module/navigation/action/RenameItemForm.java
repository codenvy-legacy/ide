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
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import org.exoplatform.gwtframework.ui.client.component.ComboBoxField;
import org.exoplatform.gwtframework.ui.client.component.DynamicForm;
import org.exoplatform.gwtframework.ui.client.component.IButton;
import org.exoplatform.gwtframework.ui.client.component.SelectItem;
import org.exoplatform.gwtframework.ui.client.component.TextField;
import org.exoplatform.gwtframework.ui.client.component.TitleOrientation;
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

   /*
    * Form constants.
    */
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

   /*
    * Css styles.
    */
   private static final String WARNING_MSG_STYLE = "exo-rename-warning-msg";

   /*
    * Variables.
    */
   private VerticalPanel mainLayout;

   private TextField itemNameField;

   private ComboBoxField mimeTypesField;

   private IButton renameButton;

   private IButton cancelButton;

   private RenameItemPresenter presenter;

   private Label warningMimeTypeLabel;

   public RenameItemForm(HandlerManager eventBus, List<Item> selectedItems, Map<String, File> openedFiles,
      Map<String, String> lockTokens)
   {
      super(eventBus, WIDTH, (selectedItems.get(0) instanceof File) ? HEIGHT : HEIGHT_SMALL, ID);

      setTitle("Rename item");

      mainLayout = new VerticalPanel();
      mainLayout.setWidth("100%");
      mainLayout.setHeight("100%");
      mainLayout.setSpacing(10);
      mainLayout.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
      setWidget(mainLayout);

      createFieldForm(selectedItems.get(0) instanceof File);
      createButtons();

      show();

      presenter = new RenameItemPresenter(eventBus, selectedItems, openedFiles, lockTokens);
      presenter.bindDisplay(this);

      itemNameField.focusInItem();
   }

   private void createFieldForm(boolean isFile)
   {
      VerticalPanel vPanel = new VerticalPanel();
      vPanel.setSpacing(3);

      itemNameField = new TextField(RENAME_FIELD, "Rename item to:");
      itemNameField.setName(RENAME_FIELD);
      itemNameField.setWidth(340);
      itemNameField.setTitleOrientation(TitleOrientation.TOP);
      itemNameField.setHeight(22);
      vPanel.add(itemNameField);
      if (isFile)
      {
         final SelectItem dropBox = new SelectItem();
         dropBox.setVisible(false);

         mimeTypesField = new ComboBoxField();
         mimeTypesField.setName(MIME_TYPE_FIELD);
         mimeTypesField.setWidth(340);
         mimeTypesField.setPickListHeight(100);
         mimeTypesField.setEnabled();
         mimeTypesField.setShowTitle(true);
         mimeTypesField.setTitle("Select mime-type: ");
         mimeTypesField.setTitleOrientation(TitleOrientation.TOP);

         vPanel.add(dropBox);
         vPanel.add(mimeTypesField);

         warningMimeTypeLabel = new Label();
         warningMimeTypeLabel.setHeight("0");
         warningMimeTypeLabel.addStyleName(WARNING_MSG_STYLE);
         vPanel.add(warningMimeTypeLabel);
      }
      itemNameField.focusInItem();

      mainLayout.add(vPanel);
   }

   private void createButtons()
   {
      HorizontalPanel buttonsLayout = new HorizontalPanel();
      buttonsLayout.setHeight(BUTTON_HEIGHT + "px");
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

      mainLayout.add(buttonsLayout);
      mainLayout.setCellVerticalAlignment(buttonsLayout, HorizontalPanel.ALIGN_TOP);
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
      mimeTypesField.setDisabled();
   }

   public void enableMimeTypeSelect()
   {
      mimeTypesField.setEnabled();
   }

   public void setDefaultMimeType(String mimeType)
   {
      mimeTypesField.setValue(mimeType);
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
         warningMimeTypeLabel.setText("");
         warningMimeTypeLabel.setHeight("0px");
      }
      else
      {
         warningMimeTypeLabel.setHeight("12px");
         warningMimeTypeLabel.setText(text);
      }
   }
}
