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

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

import org.exoplatform.gwtframework.ui.client.GwtResources;
import org.exoplatform.gwtframework.ui.client.component.DynamicForm;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextField;
import org.exoplatform.gwtframework.ui.client.component.TitleOrientation;
import org.exoplatform.gwtframework.ui.client.util.UIHelper;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.framework.configuration.IDEConfiguration;
import org.exoplatform.ide.client.framework.ui.IDEDialogWindow;
import org.exoplatform.ide.client.framework.ui.upload.FileUploadInput;
import org.exoplatform.ide.client.framework.ui.upload.FormFields;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.List;

/**
 * Class for uploading zip file.
 * 
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: Dec 10, 2010 $
 *
 */
public class UploadForm extends IDEDialogWindow implements UploadPresenter.UploadDisplay
{

   public static final int WIDTH = 450;

   public static final int HEIGHT = 150;

   private static final String ID = "ideUploadForm";

   private static final String ID_UPLOAD_BUTTON = "ideUploadFormUploadButton";

   private static final String ID_CLOSE_BUTTON = "ideUploadFormCloseButton";

   private static final String FILE_NAME_FIELD = "ideUploadFormFilenameField";

   private static final String ID_BROWSE_BUTTON = "ideUploadFormBrowseButton";
   
   private static final String BROWSE_BTN = IDE.UPLOAD_CONSTANT.uploadBrowseBtn();
   
   private static final String UPLOAD_FOLDER_TITLE = IDE.UPLOAD_CONSTANT.uploadFolderTitle();
   
   private static final String UPLOAD_BUTTON = IDE.UPLOAD_CONSTANT.uploadButton();
   
   private static final String FOLDER_TO_UPLOAD = IDE.UPLOAD_CONSTANT.folderToUpload();
   
   private final String BUTTON_W = "80px";
      
   private final String BUTTON_H = "22px";

   private FormPanel uploadForm;

   private TextField fileNameField;

   private ImageButton uploadButton;

   private ImageButton closeButton;

   protected UploadPresenter presenter;

   protected String title;

   protected String buttonTitle;

   protected String labelTitle;

   protected HorizontalPanel postFieldsPanel;

   protected IDEConfiguration applicationConfiguration;
   
   protected FileUploadInput fileUploadInput;
   
   private VerticalPanel mainLayout;
   
   private HandlerManager eventBus;

   public UploadForm(HandlerManager eventBus, List<Item> selectedItems, FolderModel folder, 
      IDEConfiguration applicationConfiguration)
   {
      super(WIDTH, HEIGHT, ID);
      initialize(eventBus, selectedItems, folder, applicationConfiguration);
   }
   
   public UploadForm(HandlerManager eventBus, List<Item> selectedItems, FolderModel folder, 
      IDEConfiguration applicationConfiguration, int width, int height)
   {
      super(width, height, ID);
      initialize(eventBus, selectedItems, folder, applicationConfiguration);
   }
   
   private void initialize(HandlerManager eventBus, List<Item> selectedItems, FolderModel folder, 
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
      presenter = createPresenter(eventBus, selectedItems, folder);
      fileUploadInput.addFileSelectedHandler(presenter);
      presenter.bindDisplay(this);
   }
   
   protected void initTitles()
   {
      title = UPLOAD_FOLDER_TITLE;
      buttonTitle = UPLOAD_BUTTON;
      labelTitle = FOLDER_TO_UPLOAD;
   }
   
   protected UploadPresenter createPresenter(HandlerManager eventBus, List<Item> selectedItems, FolderModel folder)
   {
      return new UploadPresenter(eventBus, selectedItems, folder);
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
      
      uploadButton = new ImageButton(buttonTitle);
      uploadButton.setButtonId(ID_UPLOAD_BUTTON);
      uploadButton.setWidth(BUTTON_W);
      uploadButton.setHeight(BUTTON_H);
      uploadButton.setImage(new Image(Images.MainMenu.File.UPLOAD));

      closeButton = new ImageButton("Cancel");
      closeButton.setButtonId(ID_CLOSE_BUTTON);
      closeButton.setWidth(BUTTON_W);
      closeButton.setHeight(BUTTON_H);
      closeButton.setImage(new Image(Images.Buttons.CANCEL));

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
      ImageButton selectButton = new ImageButton();
      selectButton.setButtonId(ID_BROWSE_BUTTON);
      selectButton.setText(BROWSE_BTN);
      selectButton.setWidth(BUTTON_W);
      selectButton.setHeight(BUTTON_H);

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
      uploadButton.setEnabled(false);
   }

   public void enableUploadButton()
   {
      uploadButton.setEnabled(true);
   }

}
