/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide.ext.openshift.client.login;

import com.codenvy.ide.ext.openshift.client.OpenShiftLocalizationConstant;
import com.codenvy.ide.ext.openshift.client.OpenShiftResources;
import com.codenvy.ide.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class LoginViewImpl extends DialogBox implements LoginView {

    interface LoginViewImplUiBinder extends UiBinder<Widget, LoginViewImpl> {
    }

    private static LoginViewImplUiBinder uiBinder = GWT.create(LoginViewImplUiBinder.class);

    @UiField
    TextBox email;

    @UiField
    PasswordTextBox password;

    @UiField
    Button btnLogin;

    @UiField
    Button btnCancel;

    @UiField
    Label errorLabel;

    @UiField(provided = true)
    final OpenShiftResources resources;

    @UiField(provided = true)
    final OpenShiftLocalizationConstant locale;

    private ActionDelegate delegate;

    private boolean isShown;

    @Inject
    protected LoginViewImpl(OpenShiftResources resources, OpenShiftLocalizationConstant locale) {
        this.resources = resources;
        this.locale = locale;

        Widget widget = uiBinder.createAndBindUi(this);

        this.setText(locale.loginViewTitle());
        this.setWidget(widget);
    }

    @Override
    public String getEmail() {
        return email.getValue();
    }

    @Override
    public void setEmail(String email) {
        this.email.setText(email);
    }

    @Override
    public String getPassword() {
        return password.getText();
    }

    @Override
    public void setPassword(String password) {
        this.password.setText(password);
    }

    @Override
    public void setError(String message) {
        this.errorLabel.setText(message);
    }

    @Override
    public void setEnableLoginButton(boolean isEnable) {
        btnLogin.setEnabled(isEnable);
    }

    @Override
    public void focusEmailField() {
        email.setFocus(true);
    }

    @Override
    public boolean isShown() {
        return isShown;
    }

    @Override
    public void close() {
        this.isShown = false;
        this.hide();
    }

    @Override
    public void showDialog() {
        this.isShown = true;
        this.center();
        this.show();
    }

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
    public void onEmailKeyUp(KeyUpEvent event) {
        delegate.onValueChanged();
    }

    @UiHandler("password")
    public void onPasswordKeyUp(KeyUpEvent event) {
        delegate.onValueChanged();
    }
}
