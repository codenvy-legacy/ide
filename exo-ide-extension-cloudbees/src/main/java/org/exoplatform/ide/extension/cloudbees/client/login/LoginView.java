/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package org.exoplatform.ide.extension.cloudbees.client.login;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.Label;
import org.exoplatform.gwtframework.ui.client.component.PasswordTextInput;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesExtension;

/**
 * View for log in OpenShift.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: May 26, 2011 10:54:35 AM anya $
 */
public class LoginView extends ViewImpl implements LoginPresenter.Display {
    private static final String ID = "ideLoginView";

    private static final int WIDTH = 410;

    private static final int HEIGHT = 205;

    private static final String LOGIN_BUTTON_ID = "ideLoginViewLoginButton";

    private static final String LOGIN_RESULT_ID = "ideLoginViewLoginResult";

    private static final String CANCEL_BUTTON_ID = "ideLoginViewCancelButton";

    private static final String EMAIL_FIELD_ID = "ideLoginViewEmailField";

    private static final String PASSWORD_FIELD_ID = "ideLoginViewPasswordField";

    /** UI binder for this view. */
    private static LoginViewUiBinder uiBinder = GWT.create(LoginViewUiBinder.class);

    interface LoginViewUiBinder extends UiBinder<Widget, LoginView> {
    }

    /** Email field. */
    @UiField
    TextInput emailField;

    /** Password field. */
    @UiField
    PasswordTextInput passwordField;

    /** Login button. */
    @UiField
    ImageButton loginButton;

    /** Cancel button. */
    @UiField
    ImageButton cancelButton;

    @UiField
    Label loginResult;

    public LoginView() {
        super(ID, ViewType.MODAL, CloudBeesExtension.LOCALIZATION_CONSTANT.loginViewTitle(), null, WIDTH, HEIGHT);
        add(uiBinder.createAndBindUi(this));

        emailField.setName(EMAIL_FIELD_ID);
        passwordField.setName(PASSWORD_FIELD_ID);
        loginButton.setButtonId(LOGIN_BUTTON_ID);
        cancelButton.setButtonId(CANCEL_BUTTON_ID);
        loginResult.setID(LOGIN_RESULT_ID);
    }

    /** @see org.exoplatform.ide.extension.openshift.client.login.LoginPresenter.Display#getLoginButton() */
    @Override
    public HasClickHandlers getLoginButton() {
        return loginButton;
    }

    /** @see org.exoplatform.ide.extension.openshift.client.login.LoginPresenter.Display#getCancelButton() */
    @Override
    public HasClickHandlers getCancelButton() {
        return cancelButton;
    }

    /** @see org.exoplatform.ide.extension.openshift.client.login.LoginPresenter.Display#getEmailField() */
    @Override
    public HasValue<String> getEmailField() {
        return emailField;
    }

    /** @see org.exoplatform.ide.extension.openshift.client.login.LoginPresenter.Display#getPasswordField() */
    @Override
    public HasValue<String> getPasswordField() {
        return passwordField;
    }

    /** @see org.exoplatform.ide.extension.openshift.client.login.LoginPresenter.Display#enableLoginButton(boolean) */
    @Override
    public void enableLoginButton(boolean enabled) {
        loginButton.setEnabled(enabled);
    }

    /** @see org.exoplatform.ide.extension.openshift.client.login.LoginPresenter.Display#focusInEmailField() */
    @Override
    public void focusInEmailField() {
        emailField.focus();
    }

    /** @see org.exoplatform.ide.extension.cloudbees.client.login.LoginPresenter.Display#getLoginResult() */
    @Override
    public HasValue<String> getLoginResult() {
        return loginResult;
    }
}
