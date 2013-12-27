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
package com.codenvy.ide.ext.ssh.client.key;

import com.codenvy.ide.ext.ssh.client.SshLocalizationConstant;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.validation.constraints.NotNull;

/**
 * The implementation of {@link SshKeyView}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class SshKeyViewImpl extends DialogBox implements SshKeyView {
    interface SshKeyViewImplUiBinder extends UiBinder<Widget, SshKeyViewImpl> {
    }

    private static SshKeyViewImplUiBinder ourUiBinder = GWT.create(SshKeyViewImplUiBinder.class);

    @UiField
    com.codenvy.ide.ui.Button btnClose;
    @UiField
    TextArea                  key;
    @UiField(provided = true)
    final   SshLocalizationConstant locale;
    private ActionDelegate          delegate;
    private String                  title;

    /**
     * Create view.
     *
     * @param locale
     */
    @Inject
    protected SshKeyViewImpl(SshLocalizationConstant locale) {
        this.locale = locale;
        this.title = locale.publicSshKeyField();

        Widget widget = ourUiBinder.createAndBindUi(this);

        this.setText(title);
        this.setWidget(widget);
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void setKey(@NotNull String value) {
        key.setText(value);
    }

    /** {@inheritDoc} */
    @Override
    public void addHostToTitle(@NotNull String host) {
        setText(title + host);
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

    @UiHandler("btnClose")
    public void onCloseClicked(ClickEvent event) {
        delegate.onCloseClicked();
    }
}