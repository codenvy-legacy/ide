/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.extension.ssh.client.keymanager.ui;

import com.google.gwt.uibinder.client.UiFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextField;
import org.exoplatform.ide.client.framework.ui.api.ViewType;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.upload.FileSelectedHandler;
import org.exoplatform.ide.client.framework.ui.upload.FileUploadInput;
import org.exoplatform.ide.extension.ssh.client.SshKeyExtension;
import org.exoplatform.ide.extension.ssh.client.SshLocalizationConstant;
import org.exoplatform.ide.extension.ssh.client.keymanager.UploadSshKeyPresenter.Display;

/**
 * This view represent upload private ssh key.
 * It's contains two fields:
 * <ul>
 * <li> Host name
 * <li> File name(which fills by "File upload dialog")
 * </ul>
 * And two buttons:
 * <ul>
 * <li> Upload
 * <li> Cancel
 * </ul> 
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */

public class UploadSshKeyView extends ViewImpl implements Display
{

   private static UploadSshKeyUiBinder uiBinder = GWT.create(UploadSshKeyUiBinder.class);

   interface UploadSshKeyUiBinder extends UiBinder<Widget, UploadSshKeyView>
   {
   }

   @UiField
   TextField hostField;

   @UiField
   FileUploadInput fileField;

   @UiField
   ImageButton cancelButton;

   @UiField
   ImageButton uploadButton;
   
   @UiField
   TextField fileNameField;
   
   @UiField
   Label messageLabel;
   
   @UiField
   FormPanel formPanel;

   public UploadSshKeyView()
   {
      super(ID, ViewType.MODAL, "Upload private SSH key", null, 350, 130, false);
      add(uiBinder.createAndBindUi(this));
   }

   /**
    * Factory method, uses for UiBinder
    * @return instance of {@link SshLocalizationConstant}
    */
   @UiFactory
   public SshLocalizationConstant getSshLocalizationConstant()
   {
      return SshKeyExtension.CONSTANTS;
   }
   
   /**
    * @see org.exoplatform.ide.extension.ssh.client.keymanager.UploadSshKeyPresenter.Display#getHostField()
    */
   @Override
   public HasValue<String> getHostField()
   {
      return hostField;
   }

   /**
    * @see org.exoplatform.ide.extension.ssh.client.keymanager.UploadSshKeyPresenter.Display#getCancelButon()
    */
   @Override
   public HasClickHandlers getCancelButon()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.extension.ssh.client.keymanager.UploadSshKeyPresenter.Display#setFileSelectedHandler(org.exoplatform.ide.client.framework.ui.upload.FileSelectedHandler)
    */
   @Override
   public void setFileSelectedHandler(FileSelectedHandler fileSelectedHandler)
   {
      fileField.setFileSelectedHandler(fileSelectedHandler);
   }

   /**
    * @see org.exoplatform.ide.extension.ssh.client.keymanager.UploadSshKeyPresenter.Display#getFileNameField()
    */
   @Override
   public HasValue<String> getFileNameField()
   {
      return fileNameField;
   }

   /**
    * @see org.exoplatform.ide.extension.ssh.client.keymanager.UploadSshKeyPresenter.Display#setMessage(java.lang.String)
    */
   @Override
   public void setMessage(String message)
   {
      messageLabel.setText(message);
   }

   /**
    * @see org.exoplatform.ide.extension.ssh.client.keymanager.UploadSshKeyPresenter.Display#getUploadButton()
    */
   @Override
   public HasClickHandlers getUploadButton()
   {
      return uploadButton;
   }

   /**
    * @see org.exoplatform.ide.extension.ssh.client.keymanager.UploadSshKeyPresenter.Display#getFormPanel()
    */
   @Override
   public FormPanel getFormPanel()
   {
      return formPanel;
   }

   /**
    * @see org.exoplatform.ide.extension.ssh.client.keymanager.UploadSshKeyPresenter.Display#setUploadButtonEnabled()
    */
   @Override
   public void setUploadButtonEnabled()
   {
      uploadButton.setEnabled(true);
   }

}
