/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package org.exoplatform.ide.client.operation.createfile;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasKeyPressHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;

/**
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: CreateFileView.java Feb 6, 2013 6:04:55 PM azatsarynnyy $
 *
 */

public class CreateFileView extends ViewImpl implements CreateFilePresenter.Display
{

   public static final String ID = "ideCreateFileForm";

   public static final int WIDTH = 410;

   public static final int HEIGHT = 175;

   public static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.createFileFormTitle();

   public final String NAME_FIELD = "ideCreateFileFormNameField";

   public final String ID_CREATE_BUTTON = "ideCreateFileFormCreateButton";

   public final String ID_CANCEL_BUTTON = "ideCreateFileFormCancelButton";

   @UiField
   TextInput fileNameField;

   @UiField
   ImageButton createButton;

   @UiField
   ImageButton cancelButton;

   interface CreateFolderViewUiBinder extends UiBinder<Widget, CreateFileView>
   {
   }

   private static CreateFolderViewUiBinder uiBinder = GWT.create(CreateFolderViewUiBinder.class);

   public CreateFileView()
   {
      super(ID, "popup", TITLE, new Image(IDEImageBundle.INSTANCE.newFile()), WIDTH, HEIGHT);
      setCloseOnEscape(true);
      add(uiBinder.createAndBindUi(this));

      fileNameField.setName(NAME_FIELD);
      createButton.setButtonId(ID_CREATE_BUTTON);
      cancelButton.setButtonId(ID_CANCEL_BUTTON);
   }

   /**
    * @see org.exoplatform.ide.client.operation.createfile.CreateFilePresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.client.operation.createfile.CreateFilePresenter.Display#getCreateButton()
    */
   @Override
   public HasClickHandlers getCreateButton()
   {
      return createButton;
   }

   /**
    * @see org.exoplatform.ide.client.operation.createfile.CreateFilePresenter.Display#getFileNameField()
    */
   @Override
   public HasValue<String> getFileNameField()
   {
      return fileNameField;
   }

   /**
    * @see org.exoplatform.ide.client.operation.createfile.CreateFilePresenter.Display#getFileNameFiledKeyPressed()
    */
   @Override
   public HasKeyPressHandlers getFileNameFiledKeyPressed()
   {
      return (HasKeyPressHandlers)fileNameField;
   }

   /**
    * @see org.exoplatform.ide.client.operation.createfile.CreateFilePresenter.Display#setFocusInNameField()
    */
   @Override
   public void setFocusInNameField()
   {
      fileNameField.focus();
   }

   /**
    * @see org.exoplatform.ide.client.operation.createfile.CreateFilePresenter.Display#selectFileName(int)
    */
   @Override
   public void selectFileName(int extensionLength)
   {
      fileNameField.setSelectionRange(0, fileNameField.getValue().length() - extensionLength - 1);
   }

}