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
package com.codenvy.ide.ext.ssh.client.key;

import com.codenvy.ide.annotations.NotNull;
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
        this.title = "Public Ssh Key: ";

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