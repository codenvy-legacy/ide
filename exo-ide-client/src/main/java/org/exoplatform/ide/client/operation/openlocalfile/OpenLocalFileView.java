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

package org.exoplatform.ide.client.operation.openlocalfile;

import org.exoplatform.gwtframework.ui.client.component.ComboBoxField;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextField;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.upload.FileUploadInput;
import org.exoplatform.ide.client.framework.ui.upload.FormFields;
import org.exoplatform.ide.client.framework.ui.upload.HasFileSelectedHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class OpenLocalFileView extends ViewImpl implements
   org.exoplatform.ide.client.operation.openlocalfile.OpenLocalFilePresenter.Display
{

   public static final int WIDTH = 500;

   public static final int HEIGHT = 200;

   private static final String ID = "ideOpenLocalFile";

   private static final String TITLE = IDE.UPLOAD_CONSTANT.openLocalFileTitle();

   private static OpenLocalFileViewUiBinder uiBinder = GWT.create(OpenLocalFileViewUiBinder.class);

   interface OpenLocalFileViewUiBinder extends UiBinder<Widget, OpenLocalFileView>
   {
   }

   @UiField
   ImageButton openButton, cancelButton;

   @UiField
   TextField fileNameField;

   @UiField
   HorizontalPanel postFieldsPanel;

   @UiField
   FormPanel uploadForm;

   @UiField
   FileUploadInput fileUploadInput;

   @UiField
   ComboBoxField mimeTypesField;

   public OpenLocalFileView()
   {
      super(ID, "modal", TITLE, new Image(IDEImageBundle.INSTANCE.ok()), WIDTH, HEIGHT, false);
      add(uiBinder.createAndBindUi(this));
   }

   @Override
   public HasValue<String> getMimeTypeField()
   {
      return mimeTypesField;
   }

   @Override
   public void setSelectedMimeType(String mimeType)
   {
      mimeTypesField.setValue(mimeType);
   }

   @Override
   public void setMimeTypes(String[] mimeTypes)
   {
      mimeTypesField.setValueMap(mimeTypes);
   }

   @Override
   public void setMimeTypeFieldEnabled(boolean enabled)
   {
      mimeTypesField.setEnabled(enabled);
   }

   @Override
   public void setHiddenFields(String location, String mimeType, String nodeType, String jcrContentNodeType)
   {
      Hidden mimeTypeField = new Hidden(FormFields.MIME_TYPE, mimeType);
      postFieldsPanel.add(mimeTypeField);

//      Hidden nodeTypeField = new Hidden(FormFields.NODE_TYPE, nodeType);
//      postFieldsPanel.add(nodeTypeField);
//
//      Hidden jcrContentNodeTypeField = new Hidden(FormFields.JCR_CONTENT_NODE_TYPE, jcrContentNodeType);
//      postFieldsPanel.add(jcrContentNodeTypeField);

      Hidden locationField = new Hidden(FormFields.LOCATION, location);
      postFieldsPanel.add(locationField);
   }

   @Override
   public HasClickHandlers getOpenButton()
   {
      return openButton;
   }

   @Override
   public void setOpenButtonEnabled(boolean enabled)
   {
      openButton.setEnabled(enabled);
   }

   @Override
   public HasClickHandlers getCloseButton()
   {
      return cancelButton;
   }

   @Override
   public FormPanel getUploadForm()
   {
      return uploadForm;
   }

   @Override
   public HasValue<String> getFileNameField()
   {
      return fileNameField;
   }

   @Override
   public HasFileSelectedHandler getFileUploadInput()
   {
      return fileUploadInput;
   }

}
