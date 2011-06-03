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
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import org.exoplatform.gwtframework.ui.client.component.ComboBoxField;
import org.exoplatform.gwtframework.ui.client.component.SelectItem;
import org.exoplatform.gwtframework.ui.client.component.TitleOrientation;
import org.exoplatform.ide.client.framework.configuration.IDEConfiguration;
import org.exoplatform.ide.client.framework.ui.upload.FormFields;
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
      labelTitle = "File to upload:";
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
      mimeTypesField.setValue(mimeType);
   }
   
   @Override
   protected VerticalPanel createUploadFormItems()
   {
      final SelectItem dropBox = new SelectItem();
      dropBox.setVisible(false);

      mimeTypesField = new ComboBoxField();
      mimeTypesField.setName(MIME_TYPE_FIELD);
      //set width of combobox element
      mimeTypesField.setWidth(330);
      /*
       * set width of complex panel (with containt combobox and label)
       * it is need for correct dislaying of elements at the center of 
       * horizontal panel
       */
      mimeTypesField.setWidth("330px");
      mimeTypesField.setTitle("Mime Type:");
      mimeTypesField.setShowTitle(true);
      mimeTypesField.setTitleOrientation(TitleOrientation.TOP);
      
      mimeTypesField.setPickListHeight(100);
      
      HorizontalPanel hpanel = new HorizontalPanel();
      hpanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
      hpanel.setHeight("60px");
      hpanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
      hpanel.add(mimeTypesField);
      
      VerticalPanel verPanel = super.createUploadFormItems();
      verPanel.add(dropBox);
      verPanel.add(hpanel);
      return verPanel;
   }

}
