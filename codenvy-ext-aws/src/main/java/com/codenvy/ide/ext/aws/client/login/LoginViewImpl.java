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

    @Inject
    protected LoginViewImpl(AWSLocalizationConstant constant) {
        this.constant = constant;

        Widget widget = uiBinder.createAndBindUi(this);

        this.setText(constant.loginTitle());
        this.setWidget(widget);
    }

    @Override
    public String getAccessKey() {
        return accessKeyField.getText();
    }

    @Override
    public String getSecretKey() {
        return secretKeyField.getText();
    }

    @Override
    public void focusAccessKeyField() {
        accessKeyField.setFocus(true);
    }

    @Override
    public void enableLoginButton(boolean enable) {
        btnLogin.setEnabled(enable);
    }

    @Override
    public boolean isShown() {
        return isShown;
    }

    @Override
    public void showDialog() {
        this.isShown = true;
        this.center();
        this.show();
    }

    @Override
    public void close() {
        this.isShown = false;
        this.hide();
    }

    @Override
    public void setLoginResult(String result) {
        loginResult.setText(result);
    }

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
