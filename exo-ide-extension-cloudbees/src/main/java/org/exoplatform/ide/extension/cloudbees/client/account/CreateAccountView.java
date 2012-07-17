/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.cloudbees.client.account;

import com.google.gwt.user.client.ui.Image;

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
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesClientBundle;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesExtension;

/**
 * View for creating new user's account on CloudBees.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jul 11, 2012 9:35:33 AM anya $
 * 
 */
public class CreateAccountView extends ViewImpl implements CreateAccountPresenter.Display
{
   private static final String ID = "ideCreateAccountView";

   private static final int WIDTH = 610;

   private static final int HEIGHT = 340;

   private static final String CREATE_BUTTON_ID = "ideCreateAccountViewCreateButton";

   private static final String CANCEL_BUTTON_ID = "ideCreateAccountViewCancelButton";

   private static final String EMAIL_FIELD_ID = "ideCreateAccountViewEmailField";

   private static final String COMPANY_FIELD_ID = "ideCreateAccountViewCompanyField";

   private static final String USER_NAME_FIELD_ID = "ideCreateAccountViewUserNameField";

   private static final String FIRST_NAME_FIELD_ID = "ideCreateAccountViewFirstNameField";

   private static final String LAST_NAME_FIELD_ID = "ideCreateAccountViewLastNameField";

   private static final String PASSWORD_FIELD_ID = "ideCreateAccountViewPasswordField";

   private static final String CONFIRM_PASSWORD_FIELD_ID = "ideCreateAccountViewConfirmPasswordField";

   private static final String DOMAIN_FIELD_ID = "ideCreateAccountViewDomainField";

   private static CreateAccountViewUiBinder uiBinder = GWT.create(CreateAccountViewUiBinder.class);

   interface CreateAccountViewUiBinder extends UiBinder<Widget, CreateAccountView>
   {
   }

   @UiField
   TextInput emailField;

   @UiField
   TextInput companyField;

   @UiField
   TextInput firstNameField;

   @UiField
   TextInput lastNameField;

   @UiField
   PasswordTextInput passwordField;

   @UiField
   PasswordTextInput confirmPasswordField;

   @UiField
   TextInput domainField;

   @UiField
   TextInput userNameField;

   @UiField
   ImageButton createButton;

   @UiField
   ImageButton cancelButton;

   @UiField
   Label domainErrorLabel;

   @UiField
   Label errorLabel;

   public CreateAccountView()
   {
      super(ID, ViewType.MODAL, CloudBeesExtension.LOCALIZATION_CONSTANT.createAccountViewTitle(), new Image(
         CloudBeesClientBundle.INSTANCE.createAccount()), WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));

      emailField.setName(EMAIL_FIELD_ID);
      companyField.setName(COMPANY_FIELD_ID);
      firstNameField.setName(FIRST_NAME_FIELD_ID);
      lastNameField.setName(LAST_NAME_FIELD_ID);
      passwordField.setName(PASSWORD_FIELD_ID);
      confirmPasswordField.setName(CONFIRM_PASSWORD_FIELD_ID);
      domainField.setName(DOMAIN_FIELD_ID);
      userNameField.setName(USER_NAME_FIELD_ID);

      createButton.setButtonId(CREATE_BUTTON_ID);
      cancelButton.setButtonId(CANCEL_BUTTON_ID);
   }

   /**
    * @see org.exoplatform.ide.extension.cloudbees.client.account.CreateAccountPresenter.Display#getCreateButton()
    */
   @Override
   public HasClickHandlers getCreateButton()
   {
      return createButton;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudbees.client.account.CreateAccountPresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudbees.client.account.CreateAccountPresenter.Display#enableCreateButton(boolean)
    */
   @Override
   public void enableCreateButton(boolean enabled)
   {
      createButton.setEnabled(enabled);
   }

   /**
    * @see org.exoplatform.ide.extension.cloudbees.client.account.CreateAccountPresenter.Display#getAccountNameField()
    */
   @Override
   public TextFieldItem getAccountNameField()
   {
      return domainField;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudbees.client.account.CreateAccountPresenter.Display#getCompanyField()
    */
   @Override
   public TextFieldItem getCompanyField()
   {
      return companyField;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudbees.client.account.CreateAccountPresenter.Display#getFirstNameField()
    */
   @Override
   public TextFieldItem getFirstNameField()
   {
      return firstNameField;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudbees.client.account.CreateAccountPresenter.Display#getLastNameField()
    */
   @Override
   public TextFieldItem getLastNameField()
   {
      return lastNameField;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudbees.client.account.CreateAccountPresenter.Display#getEmailField()
    */
   @Override
   public TextFieldItem getEmailField()
   {
      return emailField;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudbees.client.account.CreateAccountPresenter.Display#getPasswordField()
    */
   @Override
   public TextFieldItem getPasswordField()
   {
      return passwordField;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudbees.client.account.CreateAccountPresenter.Display#getConfirmPasswordField()
    */
   @Override
   public TextFieldItem getConfirmPasswordField()
   {
      return confirmPasswordField;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudbees.client.account.CreateAccountPresenter.Display#focusEmailField()
    */
   @Override
   public void focusEmailField()
   {
      emailField.setFocus(true);
   }

   /**
    * @see org.exoplatform.ide.extension.cloudbees.client.account.CreateAccountPresenter.Display#getDomainErrorLabel()
    */
   @Override
   public HasValue<String> getDomainErrorLabel()
   {
      return domainErrorLabel;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudbees.client.account.CreateAccountPresenter.Display#getErrorLabel()
    */
   @Override
   public HasValue<String> getErrorLabel()
   {
      return errorLabel;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudbees.client.account.CreateAccountPresenter.Display#getUserNameField()
    */
   @Override
   public TextFieldItem getUserNameField()
   {
      return userNameField;
   }
}
