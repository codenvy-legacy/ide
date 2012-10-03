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
package org.exoplatform.ide.extension.aws.client.s3;

import com.google.gwt.user.client.ui.ListBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.aws.client.AWSExtension;

public class CreateBucketView extends ViewImpl implements CreateBucketPresenter.Display
{
   private static final String ID = "ideLoginView";

   private static final int WIDTH = 410;

   private static final int HEIGHT = 213;

   private static final String CREATE_BUTTON_ID = "ideCreateBucketButton";

   private static final String CANCEL_BUTTON_ID = "ideCreateBucketCancelButton";

   private static final String NAME_FIELD_ID = "ideCreateBucketNameField";

   private static final String REGION_FIELD_ID = "ideCreateBucketRegionField";

   /**
    * UI binder for this view.
    */
   private static LoginViewUiBinder uiBinder = GWT.create(LoginViewUiBinder.class);

   interface LoginViewUiBinder extends UiBinder<Widget, CreateBucketView>
   {
   }

   /**
    * Email field.
    */
   @UiField
   TextInput bucketNameField;

   /**
    * Password field.
    */
   @UiField
   ListBox regionField;

   /**
    * Login button.
    */
   @UiField
   ImageButton createButton;

   /**
    * Cancel button.
    */
   @UiField
   ImageButton cancelButton;

   public CreateBucketView()
   {
      super(ID, ViewType.MODAL, "Create bucket", null, WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));

      bucketNameField.setName(NAME_FIELD_ID);
      regionField.setName(REGION_FIELD_ID);
      createButton.setButtonId(CREATE_BUTTON_ID);
      cancelButton.setButtonId(CANCEL_BUTTON_ID);
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.login.LoginPresenter.Display#getBucketName()
    */
   @Override
   public TextFieldItem getBucketName()
   {
      return bucketNameField;
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.login.LoginPresenter.Display#getRegion()
    */
   @Override
   public ListBox getRegion()
   {
      return regionField;
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.login.LoginPresenter.Display#getDeployButton()
    */
   @Override
   public HasClickHandlers getCreateButton()
   {
      return createButton;
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.login.LoginPresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }


   /**
    * @see org.exoplatform.ide.extension.aws.client.login.LoginPresenter.Display#enableLaunchButton(boolean)
    */
   @Override
   public void enableCreateButton(boolean enable)
   {
      createButton.setEnabled(enable);
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.login.LoginPresenter.Display#focusInName()
    */
   @Override
   public void focusInName()
   {
      bucketNameField.setFocus(true);
   }

}
