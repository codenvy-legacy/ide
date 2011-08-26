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
package org.exoplatform.ide.git.client.remote;

import com.google.gwt.user.client.ui.Image;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasValue;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextField;
import org.exoplatform.gwtframework.ui.client.component.TitleOrientation;
import org.exoplatform.gwtframework.ui.client.component.VPanel;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.git.client.GitClientBundle;
import org.exoplatform.ide.git.client.GitExtension;

/**
 * View for adding new remote repository.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Apr 18, 2011 5:48:04 PM anya $
 *
 */
public class AddRemoteRepositoryView extends ViewImpl implements AddRemoteRepositoryPresenter.Display
{
   private static final int HEIGHT = 200;

   private static final int WIDTH = 480;

   public static final String ID = "ideAddRemoteRepositoryView";

   private static final int BUTTON_HEIGHT = 22;

   private static final int BUTTON_WIDTH = 90;

   /*Elements IDs*/

   private static final String OK_BUTTON_ID = "ideAddRemoteRepositoryViewOkButton";

   private static final String CANCEL_BUTTON_ID = "ideAddRemoteRepositoryViewCancelButton";

   private static final String NAME_FIELD_ID = "ideAddRemoteRepositoryViewNameField";

   private static final String URL_FIELD_ID = "ideAddRemoteRepositoryViewUrlField";

   /**
    * Ok button.
    */
   private ImageButton okButton;

   /**
    * Cancel button.
    */
   private ImageButton cancelButton;

   /**
    * Remote repository name field.
    */
   private TextField nameField;

   /**
    * Remote repository URL field.
    */
   private TextField urlField;

   public AddRemoteRepositoryView(String title)
   {
      super(ID, ViewType.MODAL, title, null, WIDTH, HEIGHT);
      VPanel mainPanel = new VPanel();

      nameField = new TextField(NAME_FIELD_ID, GitExtension.MESSAGES.remoteNameField());
      nameField.setTitleOrientation(TitleOrientation.TOP);
      nameField.setHeight(22);
      nameField.setWidth("100%");

      urlField = new TextField(URL_FIELD_ID, GitExtension.MESSAGES.remoteLocationField());
      urlField.setTitleOrientation(TitleOrientation.TOP);
      urlField.setHeight(22);
      urlField.setWidth("100%");

      VerticalPanel fieldPanel = new VerticalPanel();
      fieldPanel.add(nameField);
      fieldPanel.add(urlField);
      fieldPanel.setSpacing(3);
      mainPanel.add(fieldPanel);

      HorizontalPanel panel = new HorizontalPanel();
      panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
      panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
      panel.setHeight("50px");

      addButtonsLayout(panel);

      mainPanel.add(panel);
      add(mainPanel);
   }

   /**
    * Add buttons to the pointed panel.
    * 
    * @param panel
    */
   private void addButtonsLayout(HorizontalPanel panel)
   {
      HorizontalPanel buttonsLayout = new HorizontalPanel();
      buttonsLayout.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
      buttonsLayout.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
      buttonsLayout.setSpacing(5);

      okButton =
         createButton(OK_BUTTON_ID, GitExtension.MESSAGES.buttonOk(), GitClientBundle.INSTANCE.ok(),
            GitClientBundle.INSTANCE.okDisabled());
      cancelButton =
         createButton(CANCEL_BUTTON_ID, GitExtension.MESSAGES.buttonCancel(), GitClientBundle.INSTANCE.cancel(),
            GitClientBundle.INSTANCE.cancelDisabled());

      buttonsLayout.add(okButton);
      buttonsLayout.add(cancelButton);
      panel.add(buttonsLayout);
   }

   /**
    * Creates button.
    * 
    * @param id button's id
    * @param title button's title
    * @param icon button's normal icon
    * @param disabledIcon button's icon in disabled state
    * @return {@link ImageButton}
    */
   private ImageButton createButton(String id, String title, ImageResource icon, ImageResource disabledIcon)
   {
      ImageButton button = new ImageButton(title);
      button.setButtonId(id);
      button.setImages(new Image(icon), new Image(disabledIcon));
      button.setHeight(BUTTON_HEIGHT + "px");
      button.setWidth(BUTTON_WIDTH + "px");
      return button;
   }

   /**
    * @see org.exoplatform.ide.git.client.remote.AddRemoteRepositoryPresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.git.client.remote.AddRemoteRepositoryPresenter.Display#getOkButton()
    */
   @Override
   public HasClickHandlers getOkButton()
   {
      return okButton;
   }

   /**
    * @see org.exoplatform.ide.git.client.remote.AddRemoteRepositoryPresenter.Display#enableOkButton(boolean)
    */
   @Override
   public void enableOkButton(boolean enable)
   {
      okButton.setEnabled(enable);
   }

   /**
    * @see org.exoplatform.ide.git.client.remote.AddRemoteRepositoryPresenter.Display#getName()
    */
   @Override
   public HasValue<String> getName()
   {
      return nameField;
   }

   /**
    * @see org.exoplatform.ide.git.client.remote.AddRemoteRepositoryPresenter.Display#getUrl()
    */
   @Override
   public HasValue<String> getUrl()
   {
      return urlField;
   }

}
