/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */

package org.exoplatform.ide.client.upload;

import org.exoplatform.gwtframework.ui.client.smartgwt.component.TextField;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.framework.ui.DialogWindow;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.StatefulCanvas;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;
import com.smartgwt.client.widgets.events.HasClickHandlers;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SpacerItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.ToolbarItem;

/**
 * Created by The eXo.
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
 * @version $Id: $
 */
public class OpenFileByPathForm extends DialogWindow implements OpenFileByPathPresenter.Display
{

   public static final int WIDTH = 500;

   public static final int HEIGHT = 160;

   private static final String ID = "ideOpenFileByPathForm";

   private static final String ID_OPEN_BUTTON = "ideOpenFileByPathFormUploadButton";

   private static final String ID_CLOSE_BUTTON = "ideOpenFileByPathFormCloseButton";

   private static final String FILE_PATH_FIELD = "ideOpenFileByPathFormFilepathField";

   private TextField filePathField;

   private IButton openFileButton;

   private IButton closeButton;

   private OpenFileByPathPresenter presenter;

   private String title;

   private String buttonTitle;

   private String labelTitle;

   public OpenFileByPathForm(HandlerManager eventBus)
   {
      super(eventBus, WIDTH, HEIGHT, ID);
      this.eventBus = eventBus;

      title = "Open file by path";
      buttonTitle = "Open";
      labelTitle = "File path";

      setTitle(title);

      addCloseClickHandler(new CloseClickHandler()
      {
         public void onCloseClick(CloseClientEvent event)
         {
            destroy();
         }
      });

      createOpenFileByPathForm();
      
      createButtons();

      show();
      
      presenter = new OpenFileByPathPresenter(eventBus);
      presenter.bindDisplay(this);
   }

   private void createOpenFileByPathForm()
   {
      DynamicForm openFileByPathForm = new DynamicForm();
      openFileByPathForm.setLayoutAlign(Alignment.CENTER);
      openFileByPathForm.setMargin(15);

      StaticTextItem promptItem = new StaticTextItem();
      promptItem.setWidth(250);
      promptItem.setTitleAlign(Alignment.LEFT);
      promptItem.setValue(labelTitle);
      promptItem.setShowTitle(false);
      promptItem.setColSpan(2);

      SpacerItem spacer = new SpacerItem();
      spacer.setHeight(2);
      
      filePathField = new FilePathInput(eventBus);
      filePathField.setWidth(450);
      filePathField.setTitleAlign(Alignment.LEFT);
      filePathField.setShowTitle(false);
      filePathField.setName(FILE_PATH_FIELD);
      filePathField.setColSpan(2);
                  
      openFileByPathForm.setItems(promptItem, spacer, filePathField);

      openFileByPathForm.setAutoWidth();

      addItem(openFileByPathForm);
   }

   private void createButtons()
   {
      DynamicForm uploadWindowButtonsForm = new DynamicForm();
      uploadWindowButtonsForm.setWidth(200);
      uploadWindowButtonsForm.setMargin(10);
      uploadWindowButtonsForm.setLayoutAlign(VerticalAlignment.TOP);
      uploadWindowButtonsForm.setLayoutAlign(Alignment.CENTER);

      openFileButton = new IButton(buttonTitle);
      openFileButton.setID(ID_OPEN_BUTTON);
      openFileButton.setHeight(22);
      openFileButton.setIcon(Images.MainMenu.File.OPEN_FILE_BY_PATH);

      StatefulCanvas buttonSpacer = new StatefulCanvas();
      buttonSpacer.setWidth(5);

      closeButton = new IButton("Cancel");
      closeButton.setID(ID_CLOSE_BUTTON);
      closeButton.setHeight(22);
      closeButton.setIcon(Images.Buttons.CANCEL);

      ToolbarItem buttonToolbar = new ToolbarItem();
      buttonToolbar.setButtons(openFileButton, buttonSpacer, closeButton);

      uploadWindowButtonsForm.setFields(buttonToolbar);

      addItem(uploadWindowButtonsForm);
   }

   public HasClickHandlers getOpenFileButton()
   {
      return this.openFileButton;
   }

   public HasClickHandlers getCloseButton()
   {
      return this.closeButton;
   }

   public HasValue<String> getFilePathField()
   {
      return filePathField;
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
}