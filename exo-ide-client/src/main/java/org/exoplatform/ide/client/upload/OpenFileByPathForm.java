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
import com.google.gwt.user.client.ui.VerticalPanel;

import org.exoplatform.gwtframework.ui.client.component.IButton;
import org.exoplatform.gwtframework.ui.client.component.TextField;
import org.exoplatform.gwtframework.ui.client.component.TitleOrientation;
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

   private TextField filePathField;

   private IButton openButton;

   private IButton cancelButton;

   private String title;

   private String buttonTitle;

   private String labelTitle;

   private VerticalPanel mainPanel;

   public OpenFileByPathForm(HandlerManager eventBus)
   {
      super(eventBus, WIDTH, HEIGHT, ID);
      this.eventBus = eventBus;

      title = "Open file by path";
      buttonTitle = "Open";
      labelTitle = "File URL";
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

      openButton = new IButton(this.buttonTitle);
      openButton.setID(OPEN_BUTTON_ID);
      openButton.setHeight(22);
      openButton.setWidth(90);
      openButton.setIcon(Images.MainMenu.File.OPEN_FILE_BY_PATH);

      cancelButton = new IButton("Cancel");
      cancelButton.setID(CANCEL_BUTTON_ID);
      cancelButton.setHeight(22);
      cancelButton.setWidth(90);
      cancelButton.setIcon(Images.Buttons.CANCEL);

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
      this.openButton.disable();
   }

   public void enableOpenButton()
   {
      this.openButton.enable();
   }

   public HasClickHandlers getOpenButton()
   {
      return this.openButton;
   }

   public TextField getFilePathFieldOrigin()
   {
      return this.filePathField;
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