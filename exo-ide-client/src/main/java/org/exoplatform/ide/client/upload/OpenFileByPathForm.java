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
import com.google.gwt.event.dom.client.HasKeyPressHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextField;
import org.exoplatform.gwtframework.ui.client.component.TitleOrientation;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.framework.ui.DialogWindow;

/**
 * Created by The eXo.
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
 * @version $Id: $
 */
public class OpenFileByPathForm extends DialogWindow implements OpenFileByPathPresenter.Display
{

   public static final int WIDTH = 500;

   public static final int HEIGHT = 150;

   private static final String ID = "ideOpenFileByPathWindow";

   private static final String FORM_ID = "ideOpenFileByPathForm";

   private static final String OPEN_BUTTON_ID = "ideOpenFileByPathFormOpenButton";

   private static final String CANCEL_BUTTON_ID = "ideOpenFileByPathFormCancelButton";

   private static final String FILE_PATH_FIELD_NAME = "ideOpenFileByPathFormFilePathField";
   
   private static final String TITLE = IDE.UPLOAD_CONSTANT.openFileByPathTitle();
   
   private static final String OPEN_BUTTON = IDE.UPLOAD_CONSTANT.openButton();
   
   private static final String FILE_URL = IDE.UPLOAD_CONSTANT.openFileByPathFileUrl();
   
   private static final String CANCEL_BUTTON = IDE.IDE_LOCALIZATION_CONSTANT.cancelButton();

   private TextField filePathField;

   private ImageButton openButton;

   private ImageButton cancelButton;

   private String title;

   private String buttonTitle;

   private String labelTitle;

   private VerticalPanel mainPanel;
   

   public OpenFileByPathForm(HandlerManager eventBus)
   {
      super(WIDTH, HEIGHT, ID);

      title = TITLE;
      buttonTitle = OPEN_BUTTON;
      labelTitle = FILE_URL;
      setTitle(title);

      mainPanel = new VerticalPanel();
      mainPanel.setWidth("100%");
      mainPanel.setHeight("100%");
      mainPanel.setSpacing(10);
      mainPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);

      createOpenFileByPathForm();
      createButtons();

      setWidget(mainPanel);

      show();

      filePathField.focusInItem();
      disableOpenButton();

      OpenFileByPathPresenter presenter = new OpenFileByPathPresenter(eventBus);
      presenter.bindDisplay(this);
   }

   private void createOpenFileByPathForm()
   {
      VerticalPanel openFileByPathForm = new VerticalPanel();
      openFileByPathForm.setWidth(450+"px");
      openFileByPathForm.getElement().setId(FORM_ID);

      filePathField = new TextField(FILE_PATH_FIELD_NAME, labelTitle);
      filePathField.setWidth(450);
      filePathField.setHeight(20);
      filePathField.setTitleOrientation(TitleOrientation.TOP);
      openFileByPathForm.add(this.filePathField);
      mainPanel.add(openFileByPathForm);
   }

   private void createButtons()
   {
      HorizontalPanel buttonsLayout = new HorizontalPanel();
      buttonsLayout.setHeight(22 + 20 + "px");
      buttonsLayout.setSpacing(5);

      openButton = new ImageButton(this.buttonTitle);
      openButton.setButtonId(OPEN_BUTTON_ID);
      openButton.setHeight("22px");
      openButton.setWidth("90px");
      openButton.setImage(new Image(Images.MainMenu.File.OPEN_FILE_BY_PATH));

      cancelButton = new ImageButton(CANCEL_BUTTON);
      cancelButton.setButtonId(CANCEL_BUTTON_ID);
      cancelButton.setHeight("22px");
      cancelButton.setWidth("90px");
      cancelButton.setImage(new Image(Images.Buttons.CANCEL));

      buttonsLayout.add(openButton);
      buttonsLayout.add(cancelButton);

      mainPanel.add(buttonsLayout);
   }

   public HasClickHandlers getCancelButton()
   {
      return this.cancelButton;
   }

   public HasKeyPressHandlers getFilePathField()
   {
      return this.filePathField;
   }

   public void closeDisplay()
   {
      destroy();
   }

   @Override
   public void destroy()
   {
      super.destroy();
   }

   public void disableOpenButton()
   {
      openButton.setEnabled(false);
   }

   public void enableOpenButton()
   {
      openButton.setEnabled(true);
   }

   public HasClickHandlers getOpenButton()
   {
      return openButton;
   }

   public TextField getFilePathFieldOrigin()
   {
      return filePathField;
   }

   /**
    * @see org.exoplatform.ide.client.upload.OpenFileByPathPresenter.Display#selectPathField()
    */
   @Override
   public void selectPathField()
   {
      filePathField.selectValue();
   }
}