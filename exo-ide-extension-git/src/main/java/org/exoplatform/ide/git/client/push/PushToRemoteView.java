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
package org.exoplatform.ide.git.client.push;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ComboBoxField;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.SelectItem;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.git.client.GitExtension;

import java.util.LinkedHashMap;

/**
 * View for pushing changes to remote repository.
 * Must be pointed in View.gwt.xml file.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Apr 4, 2011 10:48:28 AM anya $
 *
 */
public class PushToRemoteView extends ViewImpl implements PushToRemotePresenter.Display
{
   public static final String ID = "idePushToRemoteView";

   /*Elements IDs*/

   private static final String PUSH_BUTTON_ID = "idePushToRemoteViewPushButton";

   private static final String CANCEL_BUTTON_ID = "idePushToRemoteViewCancelButton";

   private static final String REMOTE_FIELD_ID = "idePushToRemoteViewRemoteField";

   private static final String LOCAL_BRANCHES_FIELD_ID = "idePushToRemoteViewLocalBranchesField";

   private static final String REMOTE_BRANCHES_FIELD_ID = "idePushToRemoteViewRemoteBranchesField";

   @UiField
   ImageButton pushButton;

   @UiField
   ImageButton cancelButton;

   @UiField
   SelectItem remoteField;

   @UiField
   SelectItem localBranchesField;

   @UiField
   ComboBoxField remoteBranchesField;

   interface PushToRemoteViewUiBinder extends UiBinder<Widget, PushToRemoteView>
   {
   }

   private static PushToRemoteViewUiBinder uiBinder = GWT.create(PushToRemoteViewUiBinder.class);

   public PushToRemoteView()
   {
      super(ID, ViewType.MODAL, GitExtension.MESSAGES.pushViewTitle(), null, 490, 205);
      add(uiBinder.createAndBindUi(this));

      remoteField.setName(REMOTE_FIELD_ID);
      localBranchesField.setName(LOCAL_BRANCHES_FIELD_ID);
      remoteBranchesField.setName(REMOTE_BRANCHES_FIELD_ID);
      pushButton.setButtonId(PUSH_BUTTON_ID);
      cancelButton.setButtonId(CANCEL_BUTTON_ID);
   }

   /**
    * @see org.exoplatform.ide.git.client.push.PushToRemotePresenter.Display#getPushButton()
    */
   @Override
   public HasClickHandlers getPushButton()
   {
      return pushButton;
   }

   /**
    * @see org.exoplatform.ide.git.client.push.PushToRemotePresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.git.client.push.PushToRemotePresenter.Display#getRemoteValue()
    */
   @Override
   public HasValue<String> getRemoteValue()
   {
      return remoteField;
   }

   /**
    * @see org.exoplatform.ide.git.client.push.PushToRemotePresenter.Display#setRemoteValues(java.lang.String[])
    */
   @Override
   public void setRemoteValues(LinkedHashMap<String, String> values)
   {
      remoteField.setValueMap(values);
   }

   /**
    * @see org.exoplatform.ide.git.client.push.PushToRemotePresenter.Display#getRemoteBranchesValue()
    */
   @Override
   public HasValue<String> getRemoteBranchesValue()
   {
      return remoteBranchesField;
   }

   /**
    * @see org.exoplatform.ide.git.client.push.PushToRemotePresenter.Display#getLocalBranchesValue()
    */
   @Override
   public HasValue<String> getLocalBranchesValue()
   {
      return localBranchesField;
   }

   /**
    * @see org.exoplatform.ide.git.client.push.PushToRemotePresenter.Display#setRemoteBranches(java.lang.String[])
    */
   @Override
   public void setRemoteBranches(String[] values)
   {
      remoteBranchesField.setValueMap(values);
      if (values != null && values.length > 0)
      {
         remoteBranchesField.setValue(values[0], true);
      }
   }

   /**
    * @see org.exoplatform.ide.git.client.push.PushToRemotePresenter.Display#setLocalBranches(java.lang.String[])
    */
   @Override
   public void setLocalBranches(String[] values)
   {
      localBranchesField.setValueMap(values);
   }

   /**
    * @see org.exoplatform.ide.git.client.push.PushToRemotePresenter.Display#enablePushButton(boolean)
    */
   @Override
   public void enablePushButton(boolean enable)
   {
      pushButton.setEnabled(enable);
   }

   /**
    * @see org.exoplatform.ide.git.client.push.PushToRemotePresenter.Display#getRemoteDisplayValue()
    */
   @Override
   public String getRemoteDisplayValue()
   {
      return remoteField.getDisplayValue();
   }
}
