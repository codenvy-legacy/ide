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
package org.exoplatform.ide.client.navigation.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasKeyPressHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextField;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.navigation.CreateFolderPresenter;

/**
 * Created by The eXo Platform SAS .
 * 
 * View for create folder form.
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public class CreateFolderView extends ViewImpl implements CreateFolderPresenter.Display
{
   
   public static final String ID = "ideCreateFolderForm";

   public static final int WIDTH = 400;

   public static final int HEIGHT = 160;

   public final String ID_CREATE_BUTTON = "ideCreateFolderFormCreateButton";

   public final String ID_CANCEL_BUTTON = "ideCreateFolderFormCancelButton";

   public final String ID_DYNAMIC_FORM = "ideCreateFolderFormDynamicForm";

   public final String NAME_FIELD = "ideCreateFolderFormNameField";
   
   public static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.createFolderFormTitle();

   @UiField
   TextField folderNameField;

   @UiField
   ImageButton createButton;

   @UiField
   ImageButton cancelButton;

   interface CreateFolderViewUiBinder extends UiBinder<Widget, CreateFolderView>
   {
   }
   
   private static CreateFolderViewUiBinder uiBinder = GWT.create(CreateFolderViewUiBinder.class);

   /**
    * @param eventBus
    * @param selectedItem
    * @param href
    */
   public CreateFolderView()
   {
      super(ID, "popup", TITLE, new Image(IDEImageBundle.INSTANCE.ok()), WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));
      
      folderNameField.setName(NAME_FIELD);
      createButton.setButtonId(ID_CREATE_BUTTON);
      cancelButton.setButtonId(ID_CANCEL_BUTTON);
   }

   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   public HasClickHandlers getCreateButton()
   {
      return createButton;
   }

   public HasValue<String> getFolderNameField()
   {
      return folderNameField;
   }

   public HasKeyPressHandlers getFolderNameFiledKeyPressed()
   {
      return (HasKeyPressHandlers)folderNameField;
   }

   /**
    * @see org.exoplatform.ide.client.navigation.CreateFolderPresenter.Display#setFocusInNameField()
    */
   @Override
   public void setFocusInNameField()
   {
      folderNameField.focusInItem();
   }

}
