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
