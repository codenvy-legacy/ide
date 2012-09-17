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
package org.exoplatform.ide.extension.aws.client.login;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.Label;
import org.exoplatform.gwtframework.ui.client.component.PasswordTextInput;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.aws.client.AWSExtension;

public class LoginView extends ViewImpl implements LoginPresenter.Display
{
   private static final String ID = "ideLoginView";

   private static final int WIDTH = 410;

   private static final int HEIGHT = 210;

   private static final String LOGIN_BUTTON_ID = "ideLoginViewLoginButton";

   private static final String LOGIN_RESULT_ID = "ideLoginViewLoginResult";

   private static final String CANCEL_BUTTON_ID = "ideLoginViewCancelButton";

   private static final String ACCESS_KEY_FIELD_ID = "ideLoginViewAccessKeyField";

   private static final String SECRET_KEY__FIELD_ID = "ideLoginViewSecretKeyField";

   /**
    * UI binder for this view.
    */
   private static LoginViewUiBinder uiBinder = GWT.create(LoginViewUiBinder.class);

   interface LoginViewUiBinder extends UiBinder<Widget, LoginView>
   {
   }

   /**
    * Email field.
    */
   @UiField
   TextInput accessKeyField;

   /**
    * Password field.
    */
   @UiField
   PasswordTextInput secretKeyField;

   /**
    * Login button.
    */
   @UiField
   ImageButton loginButton;

   /**
    * Cancel button.
    */
   @UiField
   ImageButton cancelButton;

   /**
    * Login result label.
    */
   @UiField
   Label loginResult;

   public LoginView()
   {
      super(ID, ViewType.MODAL, AWSExtension.LOCALIZATION_CONSTANT.loginTitle(), null, WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));

      accessKeyField.setName(ACCESS_KEY_FIELD_ID);
      secretKeyField.setName(SECRET_KEY__FIELD_ID);
      loginButton.setButtonId(LOGIN_BUTTON_ID);
      cancelButton.setButtonId(CANCEL_BUTTON_ID);
      loginResult.setID(LOGIN_RESULT_ID);
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.login.LoginPresenter.Display#getAccessKey()
    */
   @Override
   public TextFieldItem getAccessKey()
   {
      return accessKeyField;
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.login.LoginPresenter.Display#getSecretKey()
    */
   @Override
   public TextFieldItem getSecretKey()
   {
      return secretKeyField;
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.login.LoginPresenter.Display#getLoginButton()
    */
   @Override
   public HasClickHandlers getLoginButton()
   {
      return loginButton;
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
    * @see org.exoplatform.ide.extension.aws.client.login.LoginPresenter.Display#getLoginResult()
    */
   @Override
   public HasValue<String> getLoginResult()
   {
      return loginResult;
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.login.LoginPresenter.Display#enableLoginButton(boolean)
    */
   @Override
   public void enableLoginButton(boolean enable)
   {
      loginButton.setEnabled(enable);
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.login.LoginPresenter.Display#focusInAccessKey()
    */
   @Override
   public void focusInAccessKey()
   {
      accessKeyField.setFocus(true);
   }

}
