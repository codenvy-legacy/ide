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

public class LoginView extends ViewImpl implements LoginPresenter.Display {
    private static final String ID = "ideLoginView";

    private static final int WIDTH = 410;

    private static final int HEIGHT = 213;

    private static final String LOGIN_BUTTON_ID = "ideLoginViewLoginButton";

    private static final String LOGIN_RESULT_ID = "ideLoginViewLoginResult";

    private static final String CANCEL_BUTTON_ID = "ideLoginViewCancelButton";

    private static final String ACCESS_KEY_FIELD_ID = "ideLoginViewAccessKeyField";

    private static final String SECRET_KEY__FIELD_ID = "ideLoginViewSecretKeyField";

    /** UI binder for this view. */
    private static LoginViewUiBinder uiBinder = GWT.create(LoginViewUiBinder.class);

    interface LoginViewUiBinder extends UiBinder<Widget, LoginView> {
    }

    /** Email field. */
    @UiField
    TextInput accessKeyField;

    /** Password field. */
    @UiField
    PasswordTextInput secretKeyField;

    /** Login button. */
    @UiField
    ImageButton loginButton;

    /** Cancel button. */
    @UiField
    ImageButton cancelButton;

    /** Login result label. */
    @UiField
    Label loginResult;

    public LoginView() {
        super(ID, ViewType.MODAL, AWSExtension.LOCALIZATION_CONSTANT.loginTitle(), null, WIDTH, HEIGHT);
        add(uiBinder.createAndBindUi(this));

        accessKeyField.setName(ACCESS_KEY_FIELD_ID);
        secretKeyField.setName(SECRET_KEY__FIELD_ID);
        loginButton.setButtonId(LOGIN_BUTTON_ID);
        cancelButton.setButtonId(CANCEL_BUTTON_ID);
        loginResult.setID(LOGIN_RESULT_ID);
    }

    /** @see org.exoplatform.ide.extension.aws.client.login.LoginPresenter.Display#getAccessKey() */
    @Override
    public TextFieldItem getAccessKey() {
        return accessKeyField;
    }

    /** @see org.exoplatform.ide.extension.aws.client.login.LoginPresenter.Display#getSecretKey() */
    @Override
    public TextFieldItem getSecretKey() {
        return secretKeyField;
    }

    /** @see org.exoplatform.ide.extension.aws.client.login.LoginPresenter.Display#getLoginButton() */
    @Override
    public HasClickHandlers getLoginButton() {
        return loginButton;
    }

    /** @see org.exoplatform.ide.extension.aws.client.login.LoginPresenter.Display#getCancelButton() */
    @Override
    public HasClickHandlers getCancelButton() {
        return cancelButton;
    }

    /** @see org.exoplatform.ide.extension.aws.client.login.LoginPresenter.Display#getLoginResult() */
    @Override
    public HasValue<String> getLoginResult() {
        return loginResult;
    }

    /** @see org.exoplatform.ide.extension.aws.client.login.LoginPresenter.Display#enableLoginButton(boolean) */
    @Override
    public void enableLoginButton(boolean enable) {
        loginButton.setEnabled(enable);
    }

    /** @see org.exoplatform.ide.extension.aws.client.login.LoginPresenter.Display#focusInAccessKey() */
    @Override
    public void focusInAccessKey() {
        accessKeyField.setFocus(true);
    }

}
