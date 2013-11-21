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
package com.codenvy.ide.tutorial.wizard.newresource.page;

import com.codenvy.ide.annotations.NotNull;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * The implementation of {@link MyResourcePageView}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class MyResourcePageViewImpl extends Composite implements MyResourcePageView {
    interface MyResourcePageViewImplUiBinder extends UiBinder<Widget, MyResourcePageViewImpl> {
    }

    private static MyResourcePageViewImplUiBinder ourUiBinder = GWT.create(MyResourcePageViewImplUiBinder.class);

    @UiField
    TextBox password;
    @UiField
    TextBox login;
    private ActionDelegate delegate;

    @Inject
    public MyResourcePageViewImpl() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public String getLogin() {
        return login.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setLogin(@NotNull String login) {
        this.login.setText(login);
    }

    /** {@inheritDoc} */
    @Override
    public String getPassword() {
        return password.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setPassword(@NotNull String password) {
        this.password.setText(password);
    }

    @UiHandler({"password", "login"})
    public void onValueChanged(KeyUpEvent event) {
        delegate.onValueChanged();
    }
}