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
package com.codenvy.ide.ext.ssh.client.upload;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.ext.ssh.client.SshLocalizationConstant;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The implementation of {@link com.codenvy.ide.ext.ssh.client.key.SshKeyView}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class UploadSshKeyViewImpl extends DialogBox implements UploadSshKeyView {
    interface UploadSshKeyViewImplUiBinder extends UiBinder<Widget, UploadSshKeyViewImpl> {
    }

    private static UploadSshKeyViewImplUiBinder ourUiBinder = GWT.create(UploadSshKeyViewImplUiBinder.class);

    @UiField
    Label                     message;
    @UiField
    TextBox                   host;
    @UiField
    FormPanel                 uploadForm;
    @UiField
    com.codenvy.ide.ui.Button btnCancel;
    @UiField
    com.codenvy.ide.ui.Button btnUpload;
    @UiField(provided = true)
    final SshLocalizationConstant locale;
    FileUpload file;
    private ActionDelegate delegate;

    /**
     * Create view.
     *
     * @param locale
     */
    @Inject
    protected UploadSshKeyViewImpl(SshLocalizationConstant locale) {
        this.locale = locale;

        Widget widget = ourUiBinder.createAndBindUi(this);

        this.setText("Upload private SSH key");
        this.setWidget(widget);

        bind();
    }

    /** Bind handlers. */
    private void bind() {
        uploadForm.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
            @Override
            public void onSubmitComplete(FormPanel.SubmitCompleteEvent event) {
                delegate.onSubmitComplete(event.getResults());
            }
        });
    }

    /** {@inheritDoc} */
    @NotNull
    @Override
    public String getHost() {
        return host.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setHost(@NotNull String host) {
        this.host.setText(host);
    }

    /** {@inheritDoc} */
    @NotNull
    @Override
    public String getFileName() {
        return file.getFilename();
    }

    /** {@inheritDoc} */
    @Override
    public void setEnabledUploadButton(boolean enabled) {
        btnUpload.setEnabled(enabled);
    }

    /** {@inheritDoc} */
    @Override
    public void setMessage(@NotNull String message) {
        this.message.setText(message);
    }

    /** {@inheritDoc} */
    @Override
    public void setEncoding(@NotNull String encodingType) {
        uploadForm.setEncoding(encodingType);
    }

    /** {@inheritDoc} */
    @Override
    public void setAction(@NotNull String url) {
        uploadForm.setAction(url);
    }

    /** {@inheritDoc} */
    @Override
    public void submit() {
        uploadForm.submit();
    }

    /** {@inheritDoc} */
    @Override
    public void showDialog() {
        file = new FileUpload();
        file.setHeight("22px");
        file.setWidth("100%");
        file.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                delegate.onFileNameChanged();
            }
        });

        uploadForm.add(file);

        this.center();
        this.show();
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        this.hide();

        uploadForm.remove(file);
        file = null;
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @UiHandler("btnCancel")
    public void onCancelClicked(ClickEvent event) {
        delegate.onCancelClicked();
    }

    @UiHandler("btnUpload")
    public void onUploadClicked(ClickEvent event) {
        delegate.onUploadClicked();
    }
}