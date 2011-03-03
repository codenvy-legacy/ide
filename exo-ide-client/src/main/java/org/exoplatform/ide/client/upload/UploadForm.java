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

import com.google.gwt.user.client.ui.Widget;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.StatefulCanvas;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;
import com.smartgwt.client.widgets.events.HasClickHandlers;
import com.smartgwt.client.widgets.form.fields.CanvasItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.SpacerItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.ToolbarItem;
import com.smartgwt.client.widgets.layout.HLayout;

import org.exoplatform.gwtframework.ui.client.component.DynamicForm;
import org.exoplatform.gwtframework.ui.client.component.TextField;
import org.exoplatform.gwtframework.ui.client.util.UIHelper;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.framework.configuration.IDEConfiguration;
import org.exoplatform.ide.client.framework.ui.DialogWindow;
import org.exoplatform.ide.client.framework.vfs.Item;

import java.util.List;

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

   public static final int HEIGHT = 170;

   private static final String ID = "ideUploadForm";

   private static final String ID_UPLOAD_BUTTON = "ideUploadFormUploadButton";

   private static final String ID_CLOSE_BUTTON = "ideUploadFormCloseButton";

   private static final String ID_DYNAMIC_FORM = "ideUploadFormDynamicForm";

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

   protected VerticalPanel postFieldsPanel;

   protected IDEConfiguration applicationConfiguration;
   
   protected FileUploadInput fileUploadInput;

   public UploadForm(HandlerManager eventBus, List<Item> selectedItems, String path, 
      IDEConfiguration applicationConfiguration)
   {
      super(eventBus, WIDTH, HEIGHT, ID);
      initialize(eventBus, selectedItems, path, applicationConfiguration);
   }
   
   public UploadForm(HandlerManager eventBus, List<Item> selectedItems, String path, 
      IDEConfiguration applicationConfiguration, int width, int height)
   {
      super(eventBus, width, height, ID);
      initialize(eventBus, selectedItems, path, applicationConfiguration);
   }
   
   private void initialize(HandlerManager eventBus, List<Item> selectedItems, String path, 
      IDEConfiguration applicationConfiguration)
   {
      this.eventBus = eventBus;
      this.applicationConfiguration = applicationConfiguration;
      
      initTitles();

      setTitle(title);

      addCloseClickHandler(new CloseClickHandler()
      {
         public void onCloseClick(CloseClientEvent event)
         {
            destroy();
         }
      });

      createFileUploadForm();
      createButtons();

      show();
//      UIHelper.setAsReadOnly(fileNameField.getName());
      presenter = createPresenter(eventBus, selectedItems, path);
//      fileUploadInput.setFileSelectedHandler(presenter);
      presenter.bindDisplay(this);
   }
   
   protected void initTitles()
   {
      title = "Upload folder";
      buttonTitle = "Upload";
      labelTitle = "Folder to upload (zip)";
   }
   
   protected UploadPresenter createPresenter(HandlerManager eventBus, List<Item> selectedItems, String path)
   {
      return new UploadPresenter(eventBus, selectedItems, path);
   }

   private void createFileUploadForm()
   {
      DynamicForm uploadForm = new DynamicForm();
  //TODO    uploadForm.setLayoutAlign(Alignment.CENTER);
      uploadForm.setMargin(15);

   //TODO   
//      uploadForm.add(createUploadFormItems());

//      uploadForm.setAutoWidth();

      addItem(uploadForm);
   }
   
   protected FormItem[] createUploadFormItems()
   {
      StaticTextItem promptItem = new StaticTextItem();
      promptItem.setWidth(250);
      promptItem.setTitleAlign(Alignment.LEFT);
      promptItem.setValue(labelTitle);
      promptItem.setShowTitle(false);
      promptItem.setColSpan(2);

      SpacerItem spacer = new SpacerItem();
      spacer.setHeight(2);

      CanvasItem canvasItem = new CanvasItem();
      canvasItem.setShowTitle(false);
      canvasItem.setColSpan(2);
      canvasItem.setCanvas(getUploadLayout());

      SpacerItem spacer2 = new SpacerItem();
      spacer2.setHeight(5);
      
      FormItem[] items = new FormItem[4];
      items[0] = promptItem;
      items[1] = spacer;
      items[2] = canvasItem;
      items[3] = spacer2;
      
      VerticalPanel panel = new VerticalPanel();
      
      return items;
//      return panel;
   }

   private void createButtons()
   {
      DynamicForm uploadWindowButtonsForm = new DynamicForm();
      uploadWindowButtonsForm.setWidth(200);
      uploadWindowButtonsForm.setMargin(10);
/*TODO      uploadWindowButtonsForm.setLayoutAlign(VerticalAlignment.TOP);
      uploadWindowButtonsForm.setLayoutAlign(Alignment.CENTER);*/

      uploadButton = new IButton(buttonTitle);
      uploadButton.setID(ID_UPLOAD_BUTTON);
      uploadButton.setHeight(22);
      uploadButton.setIcon(Images.MainMenu.File.UPLOAD);

      StatefulCanvas buttonSpacer = new StatefulCanvas();
      buttonSpacer.setWidth(5);

      closeButton = new IButton("Cancel");
      closeButton.setID(ID_CLOSE_BUTTON);
      closeButton.setHeight(22);
      closeButton.setIcon(Images.Buttons.CANCEL);

      ToolbarItem buttonToolbar = new ToolbarItem();
      buttonToolbar.setButtons(uploadButton, buttonSpacer, closeButton);

   //TODO   uploadWindowButtonsForm.setFields(buttonToolbar);

      addItem(uploadWindowButtonsForm);
   }

   private HLayout getUploadLayout()
   {
      HLayout uploadLayout = new HLayout();
      uploadLayout.setWidth(330);
      uploadLayout.setHeight(22);

      DynamicForm textFieldForm = new DynamicForm();
      textFieldForm.setID(ID_DYNAMIC_FORM);
      //TODO textFieldForm.setCellPadding(0);
      fileNameField = new TextField();
      fileNameField.setName(FILE_NAME_FIELD);
      fileNameField.setShowTitle(false);
      //TODO fileNameField.setColSpan(2);
      fileNameField.setWidth("*");
      textFieldForm.add(fileNameField);
      uploadLayout.addMember(textFieldForm);

      Canvas uploadButtonCanvas = new Canvas();
      uploadButtonCanvas.setWidth(85);
      uploadButtonCanvas.setHeight(22);
      uploadLayout.addMember(uploadButtonCanvas);
      textFieldForm.setWidth("*");

      Canvas uploadCanvas = new Canvas();
      uploadCanvas.setWidth(80);
      uploadCanvas.setHeight(22);
      uploadCanvas.setLeft(5);
      uploadCanvas.setOverflow(Overflow.HIDDEN);
      uploadButtonCanvas.addChild(uploadCanvas);

      IButton selectButton = new IButton("Browse...");
      selectButton.setID(ID_BROWSE_BUTTON);
      selectButton.setTop(0);
      selectButton.setWidth(80);
      uploadCanvas.addChild(selectButton);

      Canvas fileUploadCanvas = new Canvas();
      fileUploadCanvas.setWidth(80);
      fileUploadCanvas.setHeight(22);
      uploadCanvas.addChild(fileUploadCanvas);

      fileUploadCanvas.setOpacity(0);

      // create upload form

      uploadForm = new FormPanel();
      uploadForm.setMethod(FormPanel.METHOD_POST);
      uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
      fileUploadCanvas.addChild(uploadForm);

      // create file upload input

      postFieldsPanel = new VerticalPanel();

      fileUploadInput = new FileUploadInput();
      fileUploadInput.setWidth("80px");
      fileUploadInput.setHeight("22px");
      postFieldsPanel.add(fileUploadInput);

      //uploadForm.setEncoding(encodingType)

      uploadForm.setAction(buildUploadPath());

      uploadForm.setWidget(postFieldsPanel);

      return uploadLayout;
   }
   
   protected String buildUploadPath()
   {
      return applicationConfiguration.getUploadServiceContext() + "/folder/";
   }

   public void setHiddenFields(String location, String mimeType, String nodeType, String jcrContentNodeType)
   {
      Hidden locationField = new Hidden(FormFields.LOCATION, location);
//      Hidden mimeTypeField = new Hidden(FormFields.MIME_TYPE, mimeType);
//      Hidden nodeTypeField = new Hidden(FormFields.NODE_TYPE, nodeType);
//      Hidden jcrContentNodeTypeField = new Hidden(FormFields.JCR_CONTENT_NODE_TYPE, jcrContentNodeType);

      postFieldsPanel.add(locationField);
//      postFieldsPanel.add(mimeTypeField);
//      postFieldsPanel.add(nodeTypeField);
//      postFieldsPanel.add(jcrContentNodeTypeField);
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
