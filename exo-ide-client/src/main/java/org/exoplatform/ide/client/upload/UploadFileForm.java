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
package org.exoplatform.ide.client.upload;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.VerticalPanel;

import org.exoplatform.gwtframework.ui.client.component.ComboBoxField;
import org.exoplatform.gwtframework.ui.client.component.SelectItem;
import org.exoplatform.gwtframework.ui.client.component.TitleOrientation;
import org.exoplatform.ide.client.framework.configuration.IDEConfiguration;
import org.exoplatform.ide.client.framework.vfs.Item;

import java.util.List;

/**
 * Form for uploading file (with selecting of mime type).
 * 
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: Dec 13, 2010 $
 *
 */
public class UploadFileForm extends UploadForm implements UploadFilePresenter.Display
{

   private static final String MIME_TYPE_FIELD = "ideUploadFormMimeTypeField";

   private ComboBoxField mimeTypesField;

   public UploadFileForm(HandlerManager eventBus, List<Item> selectedItems, String path, IDEConfiguration applicationConfiguration)
   {
      super(eventBus, selectedItems, path, applicationConfiguration, 450, 230);
   }
   
   @Override
   protected void initTitles()
   {
      title = "File upload";
      buttonTitle = "Upload";
      labelTitle = "File to upload";
   }
   
   @Override
   protected UploadPresenter createPresenter(HandlerManager eventBus, List<Item> selectedItems, String path)
   {
      return new UploadFilePresenter(eventBus, selectedItems, path);
   }
   

   @Override
   public void setHiddenFields(String location, String mimeType, String nodeType, String jcrContentNodeType)
   {
      super.setHiddenFields(location, mimeType, nodeType, jcrContentNodeType);
      
      Hidden mimeTypeField = new Hidden(FormFields.MIME_TYPE, mimeType);
      Hidden nodeTypeField = new Hidden(FormFields.NODE_TYPE, nodeType);
      Hidden jcrContentNodeTypeField = new Hidden(FormFields.JCR_CONTENT_NODE_TYPE, jcrContentNodeType);

      postFieldsPanel.add(mimeTypeField);
      postFieldsPanel.add(nodeTypeField);
      postFieldsPanel.add(jcrContentNodeTypeField);
   }
   
   @Override
   protected String buildUploadPath()
   {
      return applicationConfiguration.getUploadServiceContext() + "/";
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
//      mimeTypesField.setDefaultValue(mimeType);
   }
   
   @Override
   protected VerticalPanel createUploadFormItems()
   {
      VerticalPanel panel = super.createUploadFormItems();
      
      final SelectItem dropBox = new SelectItem();
      final String[] listTypes = {"value 1", "value 2", "value 3"};
      dropBox.setValueMap(listTypes);
      dropBox.setVisible(false);

      panel.add(dropBox);

      mimeTypesField = new ComboBoxField();
      mimeTypesField.setName(MIME_TYPE_FIELD);
      mimeTypesField.setWidth(330);
      mimeTypesField.setLabel("Mime Type:");
      mimeTypesField.setShowTitle(true);
      mimeTypesField.setTitleOrientation(TitleOrientation.TOP);
      
      panel.setHeight("100px");
      panel.setWidth("330px");
      
      mimeTypesField.setPickListHeight(100);
      
      panel.add(mimeTypesField);
      return panel;
   }

}
