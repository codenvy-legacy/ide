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

import java.util.List;

import org.exoplatform.gwtframework.ui.client.GwtResources;
import org.exoplatform.gwtframework.ui.client.component.DynamicForm;
import org.exoplatform.gwtframework.ui.client.component.IButton;
import org.exoplatform.gwtframework.ui.client.component.TextField;
import org.exoplatform.gwtframework.ui.client.component.TitleOrientation;
import org.exoplatform.gwtframework.ui.client.util.UIHelper;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.framework.configuration.IDEConfiguration;
import org.exoplatform.ide.client.framework.ui.DialogWindow;
import org.exoplatform.ide.client.framework.ui.upload.FileUploadInput;
import org.exoplatform.ide.client.framework.ui.upload.FormFields;
import org.exoplatform.ide.client.framework.vfs.Item;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Class for uploading zip file.
 * 
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: Dec 10, 2010 $
 *
 */
public class UploadForm extends DialogWindow implements UploadPresenter.UploadDisplay
{

   public static final int WIDTH = 450;

   public static final int HEIGHT = 150;

   private static final String ID = "ideUploadForm";

   private static final String ID_UPLOAD_BUTTON = "ideUploadFormUploadButton";

   private static final String ID_CLOSE_BUTTON = "ideUploadFormCloseButton";

   private static final String FILE_NAME_FIELD = "ideUploadFormFilenameField";

   private static final String ID_BROWSE_BUTTON = "ideUploadFormBrowseButton";

   private FormPanel uploadForm;

   private TextField fileNameField;

   private IButton uploadButton;

   private IButton closeButton;

   protected UploadPresenter presenter;

   protected String title;

   protected String buttonTitle;

   protected String labelTitle;

   protected HorizontalPanel postFieldsPanel;

   protected IDEConfiguration applicationConfiguration;
   
   protected FileUploadInput fileUploadInput;
   
   private VerticalPanel mainLayout;
   
   private HandlerManager eventBus;

   public UploadForm(HandlerManager eventBus, List<Item> selectedItems, String path, 
      IDEConfiguration applicationConfiguration)
   {
      super(WIDTH, HEIGHT, ID);
      initialize(eventBus, selectedItems, path, applicationConfiguration);
   }
   
   public UploadForm(HandlerManager eventBus, List<Item> selectedItems, String path, 
      IDEConfiguration applicationConfiguration, int width, int height)
   {
      super(width, height, ID);
      initialize(eventBus, selectedItems, path, applicationConfiguration);
   }
   
   private void initialize(HandlerManager eventBus, List<Item> selectedItems, String path, 
      IDEConfiguration applicationConfiguration)
   {
      this.eventBus = eventBus;
      this.applicationConfiguration = applicationConfiguration;
      
      mainLayout = new VerticalPanel();
      mainLayout.setHeight("100%");
      mainLayout.setWidth("100%");
      mainLayout.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
      mainLayout.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
      
      initTitles();

      setTitle(title);

      createFileUploadForm();
      createButtons();
      setWidget(mainLayout);
      show();
      UIHelper.setAsReadOnly(fileNameField.getName());
      presenter = createPresenter(eventBus, selectedItems, path);
      fileUploadInput.setFileSelectedHandler(presenter);
      presenter.bindDisplay(this);
   }
   
   protected void initTitles()
   {
      title = "Upload folder";
      buttonTitle = "Upload";
      labelTitle = "Folder to upload (zip):";
   }
   
   protected UploadPresenter createPresenter(HandlerManager eventBus, List<Item> selectedItems, String path)
   {
      return new UploadPresenter(eventBus, selectedItems, path);
   }

   private void createFileUploadForm()
   {
      mainLayout.add(createUploadFormItems());
   }
   
   protected VerticalPanel createUploadFormItems()
   {
      VerticalPanel panel = new VerticalPanel();
      panel.setWidth("420px");
      panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
      panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
      panel.add(getUploadLayout());
      
      return panel;
   }

   private void createButtons()
   {
      DynamicForm uploadWindowButtonsForm = new DynamicForm();
      uploadWindowButtonsForm.setWidth(420);
      uploadWindowButtonsForm.setHeight("50px");
      uploadWindowButtonsForm.setMargin(1);
      
      //panel to set button at the center
      HorizontalPanel hPanel = new HorizontalPanel();
      hPanel.setWidth("420px");
      hPanel.setHeight("50px");
      hPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
      hPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
      
      //panel, that contains buttons to put them near each other
      HorizontalPanel bPanel = new HorizontalPanel();
      bPanel.setWidth("170px");
      bPanel.setHeight("50px");
      bPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
      bPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
      
      uploadButton = new IButton(buttonTitle);
      uploadButton.setID(ID_UPLOAD_BUTTON);
      uploadButton.setWidth(80);
      uploadButton.setHeight(22);
      uploadButton.setIcon(Images.MainMenu.File.UPLOAD);

      closeButton = new IButton("Cancel");
      closeButton.setID(ID_CLOSE_BUTTON);
      closeButton.setWidth(80);
      closeButton.setHeight(22);
      closeButton.setIcon(Images.Buttons.CANCEL);

      bPanel.add(uploadButton);
      bPanel.add(closeButton);
      hPanel.add(bPanel);
      uploadWindowButtonsForm.add(hPanel);

      mainLayout.add(uploadWindowButtonsForm);
   }
   
   private HorizontalPanel getUploadLayout()
   {
      HorizontalPanel uploadHPanel = new HorizontalPanel();
      uploadHPanel.setWidth("330px");
      uploadHPanel.setHeight("65px");
      uploadHPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_BOTTOM);
      uploadHPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

      fileNameField = new TextField(FILE_NAME_FIELD, labelTitle);
      fileNameField.setTitleOrientation(TitleOrientation.TOP);
      fileNameField.setWidth(245);
      fileNameField.setHeight(22);

      AbsolutePanel absolutePanel = new AbsolutePanel();
      absolutePanel.setSize("80px", "22px");
      IButton selectButton = new IButton();
      selectButton.setID(ID_BROWSE_BUTTON);
      selectButton.setTitle("Browse...");
      selectButton.setWidth(80);
      selectButton.setHeight(22);

      uploadForm = new FormPanel();
      uploadForm.setMethod(FormPanel.METHOD_POST);
      uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);

      postFieldsPanel = new HorizontalPanel();

      fileUploadInput = new FileUploadInput();
      fileUploadInput.setWidth("80px");
      fileUploadInput.setHeight("22px");
      fileUploadInput.setStyleName(GwtResources.INSTANCE.css().transparent(), true);
      
      postFieldsPanel.add(fileNameField);
      postFieldsPanel.add(fileUploadInput);

      //uploadForm.setEncoding(encodingType)

      uploadForm.setAction(buildUploadPath());

      uploadForm.setWidget(postFieldsPanel);

      absolutePanel.add(selectButton, 0, 0);
      absolutePanel.add(uploadForm, 0, 0);
      uploadHPanel.add(fileNameField);
      uploadHPanel.add(absolutePanel);
      return uploadHPanel;
   }
   
   protected String buildUploadPath()
   {
      return applicationConfiguration.getUploadServiceContext() + "/folder/";
   }

   public void setHiddenFields(String location, String mimeType, String nodeType, String jcrContentNodeType)
   {
      Hidden locationField = new Hidden(FormFields.LOCATION, location);

      postFieldsPanel.add(locationField);
   }

   public FormPanel getUploadForm()
   {
      return uploadForm;
   }

   public HasClickHandlers getUploadButton()
   {
      return this.uploadButton;
   }

   public HasClickHandlers getCloseButton()
   {
      return this.closeButton;
   }

   public HasValue<String> getFileNameField()
   {
      return fileNameField;
   }

   public void closeDisplay()
   {
      destroy();
   }

   @Override
   public void destroy()
   {
      presenter.destroy();
      super.destroy();
   }

   public void disableUploadButton()
   {
      uploadButton.disable();
   }

   public void enableUploadButton()
   {
      uploadButton.enable();
   }

}
