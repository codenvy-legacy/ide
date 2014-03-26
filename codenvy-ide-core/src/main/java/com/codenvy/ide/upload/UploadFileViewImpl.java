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
package com.codenvy.ide.upload;

import com.codenvy.ide.CoreLocalizationConstant;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;

/**
 * @author Roman Nikitenko.
 */
public class UploadFileViewImpl extends DialogBox implements UploadFileView{

    public interface UploadFileViewBinder extends UiBinder<Widget, UploadFileViewImpl> {
    }

    CoreLocalizationConstant locale;
    FileUpload               file;
    private ActionDelegate delegate;

    @UiField
    Button    btnCancel;

    @UiField
    Button    btnUpload;

    @UiField
    FormPanel uploadForm;

    @UiField
    ListBox mimeTypeListBox;

    @Inject
    public UploadFileViewImpl(UploadFileViewBinder uploadFileViewBinder, CoreLocalizationConstant localization) {
        this.locale = localization;
        this.setText(locale.uploadFileTitle());
        setWidget(uploadFileViewBinder.createAndBindUi(this));
        uploadForm.setTitle("gfd");
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

    @UiHandler("btnCancel")
    public void onCancelClicked(ClickEvent event) {
        delegate.onCancelClicked();
    }

    @UiHandler("btnUpload")
    public void onUploadClicked(ClickEvent event) {
        delegate.onUploadClicked();
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate){
        this.delegate = delegate;
    }

    @Override
    public void setEnabledUploadButton(boolean enabled) {
        btnUpload.setEnabled(enabled);
    }

    @Override
    public void setEnabledMimeType(boolean enabled) {
        mimeTypeListBox.setEnabled(enabled);
    }

    @Override
    public void setSupportedMimeTypes(List<String> items) {
        Collections.sort(items);
        for (String value : items) {
            mimeTypeListBox.addItem(value);
        }
    }

    @Override
    public void setEncoding(@NotNull String encodingType) {
        uploadForm.setEncoding(encodingType);
    }

    @Override
    public void setAction(@NotNull String url) {
        uploadForm.setAction(url);
        uploadForm.setMethod(FormPanel.METHOD_POST);
    }

    @Override
    public void submit() {
        uploadForm.submit();
    }

    @Override
    public String getFileName() {
        return file.getName();
    }

}
