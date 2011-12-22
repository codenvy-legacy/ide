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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.git.client.GitExtension;

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

   private static final String CLONE_BUTTON_ID = "ideCloneRepositoryViewCloneButton";

   private static final String CANCEL_BUTTON_ID = "ideCloneRepositoryViewCancelButton";

   private static final String WORKDIR_FIELD_ID = "ideCloneRepositoryViewWorkDirField";

   private static final String REMOTE_URI_FIELD_ID = "ideCloneRepositoryViewRemoteUriField";

   private static final String REMOTE_NAME_FIELD_ID = "ideCloneRepositoryViewRemoteNameField";

   @UiField
   ImageButton cloneButton;

   @UiField
   ImageButton cancelButton;

   @UiField
   TextInput workdirField;

   @UiField
   TextInput remoteUriField;

   @UiField
   TextInput remoteNameField;

   interface CloneRepositoryViewUiBinder extends UiBinder<Widget, CloneRepositoryView>
   {
   }

   private static CloneRepositoryViewUiBinder uiBinder = GWT.create(CloneRepositoryViewUiBinder.class);

   public CloneRepositoryView()
   {
      super(ID, ViewType.MODAL, GitExtension.MESSAGES.cloneTitle(), null, 480, 260);
      setCloseOnEscape(true);
      add(uiBinder.createAndBindUi(this));

      workdirField.setName(WORKDIR_FIELD_ID);
      remoteUriField.setName(REMOTE_URI_FIELD_ID);
      remoteNameField.setName(REMOTE_NAME_FIELD_ID);
     
      cloneButton.setButtonId(CLONE_BUTTON_ID);
      cancelButton.setButtonId(CANCEL_BUTTON_ID);
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

   /**
    * @see org.exoplatform.ide.git.client.clone.CloneRepositoryPresenter.Display#focusInRemoteUrlField()
    */
   @Override
   public void focusInRemoteUrlField()
   {
      remoteUriField.focus();
   }

}
