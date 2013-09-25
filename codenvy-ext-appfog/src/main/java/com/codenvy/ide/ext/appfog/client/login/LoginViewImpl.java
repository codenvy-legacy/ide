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
package com.codenvy.ide.ext.appfog.client.login;

import com.codenvy.ide.ext.appfog.client.AppfogLocalizationConstant;
import com.codenvy.ide.ext.appfog.client.AppfogResources;
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
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class LoginViewImpl extends DialogBox implements LoginView {
    interface LoginViewImplUiBinder extends UiBinder<Widget, LoginViewImpl> {
    }

    private static LoginViewImplUiBinder uiBinder = GWT.create(LoginViewImplUiBinder.class);

    @UiField
    TextBox                   email;
    @UiField
    PasswordTextBox           password;
    @UiField
    com.codenvy.ide.ui.Button btnLogin;
    @UiField
    com.codenvy.ide.ui.Button btnCancel;
    @UiField
    Label                     errorText;
    @UiField
    TextBox                   target;
    @UiField(provided = true)
    final   AppfogResources            res;
    @UiField(provided = true)
    final   AppfogLocalizationConstant locale;
    private ActionDelegate             delegate;
    private boolean                    isShown;

    /** Create view. */
    @Inject
    protected LoginViewImpl(AppfogResources resources, AppfogLocalizationConstant constant) {
        this.res = resources;
        this.locale = constant;

        Widget widget = uiBinder.createAndBindUi(this);

        this.setText("Login to AppFog");
        this.setWidget(widget);
    }

    /** {@inheritDoc} */
    @Override
    public String getEmail() {
        return email.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setEmail(String email) {
        this.email.setText(email);
    }

    /** {@inheritDoc} */
    @Override
    public String getPassword() {
        return password.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setPassword(String password) {
        this.password.setText(password);
    }

    /** {@inheritDoc} */
    @Override
    public String getTarget() {
        return target.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setTarget(String target) {
        this.target.setText(target);
    }

    /** {@inheritDoc} */
    @Override
    public void setError(String message) {
        errorText.setText(message);
    }

    /** {@inheritDoc} */
    @Override
    public void setEnableLoginButton(boolean enabled) {
        btnLogin.setEnabled(enabled);
    }

    /** {@inheritDoc} */
    @Override
    public void focusInEmailField() {
        email.setFocus(true);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isShown() {
        return isShown;
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        this.isShown = false;
        this.hide();
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
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @UiHandler("btnLogin")
    public void onBtnLoginClick(ClickEvent event) {
        delegate.onLoginClicked();
    }

    @UiHandler("btnCancel")
    public void onBtnCancelClick(ClickEvent event) {
        delegate.onCancelClicked();
    }

    @UiHandler("email")
    void onEmailKeyUp(KeyUpEvent event) {
        delegate.onValueChanged();
    }

    @UiHandler("password")
    void onPasswordKeyUp(KeyUpEvent event) {
        delegate.onValueChanged();
    }
}