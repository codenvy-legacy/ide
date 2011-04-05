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
package org.exoplatform.ide.git.client.clone;

import com.google.gwt.resources.client.ImageResource;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import org.exoplatform.gwtframework.ui.client.component.IButton;
import org.exoplatform.gwtframework.ui.client.component.TextField;
import org.exoplatform.gwtframework.ui.client.component.TitleOrientation;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.git.client.GitClientBundle;

/**
 * UI for cloning repository.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Mar 22, 2011 4:54:24 PM anya $
 *
 */
public class CloneRepositoryView extends ViewImpl implements
   org.exoplatform.ide.git.client.clone.CloneRepositoryPresenter.Display
{
   public static final String ID = "ideCloneRepositoryView";

   public static final String TYPE = "modal";

   public static final String TITLE = "Clone remote repository";

   private static final int BUTTON_HEIGHT = 22;

   private static final int BUTTON_WIDTH = 90;

   /*Elements IDs*/

   private static final String CLONE_BUTTON_ID = "ideCloneRepositoryViewCloneButton";

   private static final String CANCEL_BUTTON_ID = "ideCloneRepositoryViewCancelButton";

   private static final String WORKDIR_FIELD_ID = "ideCloneRepositoryViewWorkDirField";

   private static final String REMOTE_URI_FIELD_ID = "ideCloneRepositoryViewRemoteUriField";

   private static final String REMOTE_NAME_FIELD_ID = "ideCloneRepositoryViewRemoteNameField";

   /*Elements titles*/

   private static final String CLONE_BUTTON_TITLE = "Clone";

   private static final String CANCEL_BUTTON_TITLE = "Cancel";

   private static final String WORKDIR_FIELD_TITLE = "Work directory";

   private static final String REMOTE_URI_FIELD_TITLE = "Remote repository URI";

   private static final String REMOTE_NAME_FIELD_TITLE = "Remote name";

   private IButton cloneButton;

   private IButton cancelButton;

   private TextField workdirField;

   private TextField remoteUriField;

   private TextField remoteNameField;

   public CloneRepositoryView()
   {
      super(ID, TYPE, TITLE, null, 480, 240);

      VerticalPanel mainLayout = new VerticalPanel();
      mainLayout.setWidth("100%");
      mainLayout.setHeight("100%");
      mainLayout.setSpacing(2);
      mainLayout.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

      workdirField = createTextField(WORKDIR_FIELD_ID, WORKDIR_FIELD_TITLE);
      remoteUriField = createTextField(REMOTE_URI_FIELD_ID, REMOTE_URI_FIELD_TITLE);
      remoteNameField = createTextField(REMOTE_NAME_FIELD_ID, REMOTE_NAME_FIELD_TITLE);

      mainLayout.add(workdirField);
      mainLayout.add(remoteUriField);
      mainLayout.add(remoteNameField);
      addButtonsLayout(mainLayout);

      add(mainLayout);
   }

   /**
    * Add buttons to the pointed panel.
    * 
    * @param panel
    */
   private void addButtonsLayout(VerticalPanel panel)
   {
      HorizontalPanel buttonsLayout = new HorizontalPanel();
      buttonsLayout.setHeight(BUTTON_HEIGHT + 30 + "px");
      buttonsLayout.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
      buttonsLayout.setSpacing(5);

      cloneButton = createButton(CLONE_BUTTON_ID, CLONE_BUTTON_TITLE, GitClientBundle.INSTANCE.ok(), GitClientBundle.INSTANCE.okDisabled());
      cancelButton = createButton(CANCEL_BUTTON_ID, CANCEL_BUTTON_TITLE, GitClientBundle.INSTANCE.cancel(), GitClientBundle.INSTANCE.cancelDisabled());

      buttonsLayout.add(cloneButton);
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
    * @return {@link IButton}
    */
   private IButton createButton(String id, String title, ImageResource icon, ImageResource disabledIcon)
   {
      IButton button = new IButton(title);
      button.setID(id);
      button.setIcon(icon.getURL(), disabledIcon.getURL());
      button.setHeight(BUTTON_HEIGHT);
      button.setWidth(BUTTON_WIDTH);
      return button;
   }

   /**
    * Creates {@link TextField} component.
    * 
    * @param id element's id
    * @param title title near text field
    * @return {@link TextField}
    */
   private TextField createTextField(String id, String title)
   {
      TextField textField = new TextField(id, title);
      textField.setTitleOrientation(TitleOrientation.TOP);
      textField.setWidth(420);
      textField.setHeight(22);
      return textField;
   }

   /**
    * @see org.exoplatform.ide.git.client.clone.CloneRepositoryPresenter.Display#getWorkDirValue()
    */
   public HasValue<String> getWorkDirValue()
   {
      return workdirField;
   }

   /**
    * @see org.exoplatform.ide.git.client.clone.CloneRepositoryPresenter.Display#getRemoteUriValue()
    */
   public HasValue<String> getRemoteUriValue()
   {
      return remoteUriField;
   }

   /**
    * @see org.exoplatform.ide.git.client.clone.CloneRepositoryPresenter.Display#getRemoteNameValue()
    */
   public HasValue<String> getRemoteNameValue()
   {
      return remoteNameField;
   }

   /**
    * @see org.exoplatform.ide.git.client.clone.CloneRepositoryPresenter.Display#getCloneButton()
    */
   public HasClickHandlers getCloneButton()
   {
      return cloneButton;
   }

   /**
    * @see org.exoplatform.ide.git.client.clone.CloneRepositoryPresenter.Display#getCancelButton()
    */
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.git.client.clone.CloneRepositoryPresenter.Display#enableCloneButton(boolean)
    */
   public void enableCloneButton(boolean enable)
   {
      cloneButton.setEnabled(enable);
   }

}
