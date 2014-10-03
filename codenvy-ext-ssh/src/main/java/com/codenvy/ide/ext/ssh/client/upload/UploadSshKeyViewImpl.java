/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.ssh.client.upload;

import com.codenvy.ide.ext.ssh.client.SshLocalizationConstant;
import com.codenvy.ide.ui.window.Window;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.annotation.Nonnull;

/**
 * The implementation of {@link com.codenvy.ide.ext.ssh.client.key.SshKeyView}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class UploadSshKeyViewImpl extends Window implements UploadSshKeyView {
    interface UploadSshKeyViewImplUiBinder extends UiBinder<Widget, UploadSshKeyViewImpl> {
    }

    private static UploadSshKeyViewImplUiBinder ourUiBinder = GWT.create(UploadSshKeyViewImplUiBinder.class);

    Button btnCancel;
    Button btnUpload;

    @UiField
    Label     message;
    @UiField
    TextBox   host;
    @UiField
    FormPanel uploadForm;
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

        this.setTitle(locale.uploadSshKeyViewTitle());
        this.setWidget(widget);
        bind();

        btnCancel = createButton(locale.cancelButton(), "window-preferences-sshKeys-cancel", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                delegate.onCancelClicked();
            }
        });
        getFooter().add(btnCancel);

        btnUpload = createButton(locale.uploadButton(), "preferences-sshKeys-upload", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                delegate.onUploadClicked();
            }
        });
        getFooter().add(btnUpload);
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
    @Nonnull
    @Override
    public String getHost() {
        return host.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setHost(@Nonnull String host) {
        this.host.setText(host);
    }

    /** {@inheritDoc} */
    @Nonnull
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
    public void setMessage(@Nonnull String message) {
        this.message.setText(message);
    }

    /** {@inheritDoc} */
    @Override
    public void setEncoding(@Nonnull String encodingType) {
        uploadForm.setEncoding(encodingType);
    }

    /** {@inheritDoc} */
    @Override
    public void setAction(@Nonnull String url) {
        uploadForm.setAction(url);
        uploadForm.setMethod(FormPanel.METHOD_POST);
    }

    /** {@inheritDoc} */
    @Override
    public void submit() {
        uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
        uploadForm.submit();
    }

    /** {@inheritDoc} */
    @Override
    public void showDialog() {
        file = new FileUpload();
        file.setHeight("22px");
        file.setWidth("100%");
        file.setName("file");
        file.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                delegate.onFileNameChanged();
            }
        });

        uploadForm.add(file);

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

    /** {@inheritDoc} */
    @Override
    protected void onClose() {
        hide();
    }
}