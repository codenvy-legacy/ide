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
package com.codenvy.ide.ext.aws.client.login;

import com.codenvy.ide.ext.aws.client.AWSLocalizationConstant;
import com.codenvy.ide.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The implementation of {@link LoginView}.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class LoginViewImpl extends DialogBox implements LoginView {
    interface LoginViewImplUiBinder extends UiBinder<Widget, LoginViewImpl> {
    }

    private static LoginViewImplUiBinder uiBinder = GWT.create(LoginViewImplUiBinder.class);

    @UiField
    TextBox accessKeyField;

    @UiField
    PasswordTextBox secretKeyField;

    @UiField
    Button btnLogin;

    @UiField
    Button btnCancel;

    @UiField
    Label loginResult;

    @UiField(provided = true)
    AWSLocalizationConstant constant;

    private ActionDelegate delegate;

    private boolean isShown;

    /**
     * Create view.
     *
     * @param constant
     */
    @Inject
    protected LoginViewImpl(AWSLocalizationConstant constant) {
        this.constant = constant;

        Widget widget = uiBinder.createAndBindUi(this);

        this.setText(constant.loginTitle());
        this.setWidget(widget);
    }

    /** {@inheritDoc} */
    @Override
    public String getAccessKey() {
        return accessKeyField.getText();
    }

    /** {@inheritDoc} */
    @Override
    public String getSecretKey() {
        return secretKeyField.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void focusAccessKeyField() {
        accessKeyField.setFocus(true);
    }

    /** {@inheritDoc} */
    @Override
    public void enableLoginButton(boolean enable) {
        btnLogin.setEnabled(enable);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isShown() {
        return isShown;
    }

    /** {@inheritDoc} */
    @Override
    public void showDialog() {
        this.isShown = true;
        this.center();
        this.show();
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        this.isShown = false;
        this.hide();
    }

    /** {@inheritDoc} */
    @Override
    public void setLoginResult(String result) {
        loginResult.setText(result);
    }

    /** {@inheritDoc} */
    /** Sets the delegate to receive events from this view. */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @UiHandler("btnLogin")
    public void onLoginButtonClicked(ClickEvent event) {
        delegate.onLogInClicked();
    }

    @UiHandler("btnCancel")
    public void onCancelButtonClicked(ClickEvent event) {
        delegate.onCancelClicked();
    }

    @UiHandler("accessKeyField")
    public void onAccessKeyFieldKeyUp(KeyUpEvent event) {
        delegate.onValueChanged();
    }

    @UiHandler("secretKeyField")
    public void onSecretKeyFieldKeyUp(KeyUpEvent event) {
        delegate.onValueChanged();
    }
}
