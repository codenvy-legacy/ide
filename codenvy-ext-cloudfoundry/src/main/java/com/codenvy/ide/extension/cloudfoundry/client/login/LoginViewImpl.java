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
package com.codenvy.ide.extension.cloudfoundry.client.login;

import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryLocalizationConstant;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryResources;
import com.codenvy.ide.json.JsonArray;
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
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
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
    ListBox                   target;
    @UiField(provided = true)
    final   CloudFoundryResources            res;
    @UiField(provided = true)
    final   CloudFoundryLocalizationConstant locale;
    private ActionDelegate                   delegate;

    /**
     * Create view.
     *
     * @param resources
     */
    @Inject
    protected LoginViewImpl(CloudFoundryResources resources, CloudFoundryLocalizationConstant constant) {
        this.res = resources;
        this.locale = constant;

        Widget widget = uiBinder.createAndBindUi(this);

        this.setText("Login to CloudFoundry");
        this.setWidget(widget);
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
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
    public String getServer() {
        int serverIndex = target.getSelectedIndex();
        return serverIndex != -1 ? target.getItemText(serverIndex) : "";
    }

    /** {@inheritDoc} */
    @Override
    public void setServer(String server) {
        int count = this.target.getItemCount();
        boolean isItemFound = false;

        // Looks up entered server into available list of servers
        int i = 0;
        while (i < count && !isItemFound) {
            String item = this.target.getItemText(i);
            isItemFound = item.equals(server);

            i++;
        }

        // If item was found then it will be shown otherwise do nothing
        if (isItemFound) {
            this.target.setSelectedIndex(i - 1);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setError(String message) {
        errorText.setText(message);
    }

    /** {@inheritDoc} */
    @Override
    public void enableLoginButton(boolean enabled) {
        btnLogin.setEnabled(enabled);
    }

    /** {@inheritDoc} */
    @Override
    public void focusInEmailField() {
        email.setFocus(true);
    }

    /** {@inheritDoc} */
    @Override
    public void setServerValues(JsonArray<String> servers) {
        target.clear();
        for (int i = 0; i < servers.size(); i++) {
            target.addItem(servers.get(i));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        this.hide();
    }

    /** {@inheritDoc} */
    @Override
    public void showDialog() {
        this.center();
        this.show();
    }

    @UiHandler("btnLogin")
    void onBtnLogInClick(ClickEvent event) {
        delegate.onLogInClicked();
    }

    @UiHandler("btnCancel")
    void onBtnCancelClick(ClickEvent event) {
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