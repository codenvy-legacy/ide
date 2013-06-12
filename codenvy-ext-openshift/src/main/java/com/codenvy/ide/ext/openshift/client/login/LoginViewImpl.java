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
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * The implementation for {@link LoginView}.
 *
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

    /**
     * Create view.
     *
     * @param resources
     *         image resources
     * @param locale
     *         locale constants
     */
    @Inject
    protected LoginViewImpl(OpenShiftResources resources, OpenShiftLocalizationConstant locale) {
        this.resources = resources;
        this.locale = locale;

        Widget widget = uiBinder.createAndBindUi(this);

        this.setText(locale.loginViewTitle());
        this.setWidget(widget);
    }

    /** {@inheritDoc} */
    @Override
    public String getEmail() {
        return email.getValue();
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
    public void setError(String message) {
        this.errorLabel.setText(message);
    }

    /** {@inheritDoc} */
    @Override
    public void setEnableLoginButton(boolean isEnable) {
        btnLogin.setEnabled(isEnable);
    }

    /** {@inheritDoc} */
    @Override
    public void focusEmailField() {
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

    /**
     * handler for Login button.
     *
     * @param event
     */
    @UiHandler("btnLogin")
    public void onBtnLoginClick(ClickEvent event) {
        delegate.onLoginClicked();
    }

    /**
     * Handler for Cancel button.
     *
     * @param event
     */
    @UiHandler("btnCancel")
    public void onBtnCancelClick(ClickEvent event) {
        delegate.onCancelClicked();
    }

    /**
     * Handler for changing email field value.
     *
     * @param event
     */
    @UiHandler("email")
    public void onEmailKeyUp(KeyUpEvent event) {
        delegate.onValueChanged();
    }

    /**
     * Handler for changing password field value.
     *
     * @param event
     */
    @UiHandler("password")
    public void onPasswordKeyUp(KeyUpEvent event) {
        delegate.onValueChanged();
    }
}
