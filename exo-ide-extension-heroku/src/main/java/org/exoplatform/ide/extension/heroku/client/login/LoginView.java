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
package org.exoplatform.ide.extension.heroku.client.login;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.PasswordField;
import org.exoplatform.gwtframework.ui.client.component.TextField;
import org.exoplatform.ide.client.framework.ui.api.ViewType;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.extension.heroku.client.HerokuExtension;

/**
 * View for log in Heroku.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  May 26, 2011 10:54:35 AM anya $
 *
 */
public class LoginView extends ViewImpl implements LoginPresenter.Display
{
   private static final String ID = "ideLoginView";

   private static final int WIDTH = 400;

   private static final int HEIGHT = 190;

   private static final String LOGIN_BUTTON_ID = "ideLoginViewLoginButton";
   
   /**
    * Button for login as user "Demo".
    */
   private static final String LOGIN_DEMO_BUTTON_ID = "ideLoginDemoViewLoginButton";

   private static final String CANCEL_BUTTON_ID = "ideLoginViewCancelButton";

   private static final String EMAIL_FIELD_ID = "ideLoginViewEmailField";

   private static final String PASSWORD_FIELD_ID = "ideLoginViewPasswordField";

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
   TextField emailField;

   /**
    * Password field.
    */
   @UiField
   PasswordField passwordField;

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
   
   @UiField
   ImageButton loginDemoButton;

   public LoginView()
   {
      super(ID, ViewType.MODAL, HerokuExtension.LOCALIZATION_CONSTANT.loginViewTitle(), null, WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));

      emailField.setName(EMAIL_FIELD_ID);
      emailField.setHeight(22);
      passwordField.setName(PASSWORD_FIELD_ID);
      passwordField.setHeight(22);
      loginButton.setButtonId(LOGIN_BUTTON_ID);
      cancelButton.setButtonId(CANCEL_BUTTON_ID);
      loginDemoButton.setButtonId(LOGIN_DEMO_BUTTON_ID);
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.login.LoginPresenter.Display#getLoginButton()
    */
   @Override
   public HasClickHandlers getLoginButton()
   {
      return loginButton;
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.login.LoginPresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.login.LoginPresenter.Display#getEmailField()
    */
   @Override
   public HasValue<String> getEmailField()
   {
      return emailField;
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.login.LoginPresenter.Display#getPasswordField()
    */
   @Override
   public HasValue<String> getPasswordField()
   {
      return passwordField;
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.login.LoginPresenter.Display#enableLoginButton(boolean)
    */
   @Override
   public void enableLoginButton(boolean enabled)
   {
      loginButton.setEnabled(enabled);
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.login.LoginPresenter.Display#focusInEmailField()
    */
   @Override
   public void focusInEmailField()
   {
      emailField.focusInItem();
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.login.LoginPresenter.Display#getLoginDemoButton()
    */
   @Override
   public HasClickHandlers getLoginDemoButton()
   {
      return loginDemoButton;
   }
}
