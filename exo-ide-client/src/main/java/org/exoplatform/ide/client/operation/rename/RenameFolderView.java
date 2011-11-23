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
package org.exoplatform.ide.client.operation.rename;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasKeyPressHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * View for renaming folders.
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class RenameFolderView extends ViewImpl implements RenameFolderPresenter.Display
{

   /*
    * Form constants.
    */
   private static final int WIDTH = 400;

   private static final int HEIGHT = 150;

   private static final String ID = "ideRenameItemForm";

   private static final String RENAME_BUTTON_ID = "ideRenameItemFormRenameButton";

   private static final String CANCEL_BUTTON_ID = "ideRenameItemFormCancelButton";

   private static final String RENAME_FIELD = "ideRenameItemFormRenameField";

   @UiField
   TextInput nameField;

   @UiField
   ImageButton renameButton;
   
   @UiField
   ImageButton cancelButton;

   private static final String TITLE = IDE.NAVIGATION_CONSTANT.renameItemTitle();
   
   interface RenameFolderViewUiBinder extends UiBinder<Widget, RenameFolderView>
   {
   }
   
   private static RenameFolderViewUiBinder uiBinder = GWT.create(RenameFolderViewUiBinder.class);

   public RenameFolderView()
   {
      super(ID, "popup", TITLE, new Image(IDEImageBundle.INSTANCE.ok()), WIDTH, HEIGHT);
      setCloseOnEscape(true);
      add(uiBinder.createAndBindUi(this));
      
      nameField.setName(RENAME_FIELD);
      renameButton.setButtonId(RENAME_BUTTON_ID);
      cancelButton.setButtonId(CANCEL_BUTTON_ID);
   }

   public HasValue<String> getNameField()
   {
      return nameField;
   }

   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   public HasClickHandlers getRenameButton()
   {
      return renameButton;
   }

   public HasKeyPressHandlers getNameFieldKeyPressHandler()
   {
      return (HasKeyPressHandlers)nameField;
   }

   /**
    * @see org.exoplatform.ide.client.navigation.RenameFolderPresenter.Display#enableRenameButton(boolean)
    */
   public void enableRenameButton(boolean enable)
   {
      renameButton.setEnabled(enable);
   }

   /**
    * @see org.exoplatform.ide.client.navigation.RenameFolderPresenter.Display#focusInNameField()
    */
   @Override
   public void focusInNameField()
   {
      nameField.setFocus(true);
   }

}
