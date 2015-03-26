/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.upload.folder;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.ui.window.Window;

import javax.annotation.Nonnull;

/**
 * The implementation of {@link UploadFolderFromZipView}.
 *
 * @author Roman Nikitenko.
 */
public class UploadFolderFromZipViewImpl extends Window implements UploadFolderFromZipView {

    public interface UploadFolderFromZipViewBinder extends UiBinder<Widget, UploadFolderFromZipViewImpl> {
    }

    Button                   btnCancel;
    Button                   btnUpload;
    FileUpload               file;
    ActionDelegate           delegate;
    CoreLocalizationConstant constant;

    @UiField
    FormPanel submitForm;
    @UiField
    CheckBox  overwrite;
    @UiField
    CheckBox  skipFirstLevel;
    @UiField
    FlowPanel uploadPanel;

    /** Create view. */
    @Inject
    public UploadFolderFromZipViewImpl(UploadFolderFromZipViewBinder uploadFileViewBinder,
                                       CoreLocalizationConstant locale,
                                       org.eclipse.che.ide.Resources resources) {
        this.constant = locale;
        this.setTitle(locale.uploadFileTitle());
        setWidget(uploadFileViewBinder.createAndBindUi(this));
        bind();

        btnCancel = createButton(locale.cancel(), "file-uploadFolder-cancel", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                delegate.onCancelClicked();
            }
        });
        getFooter().add(btnCancel);

        btnUpload = createButton(locale.uploadButton(), "file-uploadFolder-upload", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                delegate.onUploadClicked();
            }
        });
        btnUpload.addStyleName(resources.Css().buttonLoader());
        getFooter().add(btnUpload);
    }

    /** Bind handlers. */
    private void bind() {
        submitForm.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
            @Override
            public void onSubmitComplete(FormPanel.SubmitCompleteEvent event) {
                delegate.onSubmitComplete(event.getResults());
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void showDialog() {
        addFile();
        show();
    }

    /** {@inheritDoc} */
    @Override
    public void closeDialog() {
        hide();
        onClose();
        btnUpload.setEnabled(false);
        overwrite.setValue(false);
        skipFirstLevel.setValue(false);
    }

    @Override
    public void setLoaderVisibility(boolean isVisible) {
        if (isVisible) {
            btnUpload.setHTML("<i></i>");
            btnUpload.setEnabled(false);
        } else {
            btnUpload.setText(constant.uploadButton());
            btnUpload.setEnabled(true);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void setEnabledUploadButton(boolean enabled) {
        btnUpload.setEnabled(enabled);
    }

    /** {@inheritDoc} */
    @Override
    public void setEncoding(@Nonnull String encodingType) {
        submitForm.setEncoding(encodingType);
    }

    /** {@inheritDoc} */
    @Override
    public void setAction(@Nonnull String url) {
        submitForm.setAction(url);
        submitForm.setMethod(FormPanel.METHOD_POST);
    }

    /** {@inheritDoc} */
    @Override
    public void submit() {
        overwrite.setFormValue(overwrite.getValue().toString());
        skipFirstLevel.setFormValue(skipFirstLevel.getValue().toString());
        submitForm.submit();
        btnUpload.setEnabled(false);
    }

    /** {@inheritDoc} */
    @Override
    @Nonnull
    public String getFileName() {
        String fileName = file.getFilename();
        if (fileName.contains("/") || fileName.contains("\\")) {
            int index = fileName.contains("\\") ? fileName.lastIndexOf("\\") + 1 : fileName.lastIndexOf("/") + 1;
            fileName = fileName.substring(index);
        }
        return fileName;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isOverwriteFileSelected() {
        return overwrite.getValue();
    }

    /** {@inheritDoc} */
    @Override
    protected void onClose() {
        uploadPanel.remove(file);
    }

    private void addFile() {
        file = new FileUpload();
        file.setHeight("22px");
        file.setWidth("100%");
        file.setName("file");
        file.ensureDebugId("file-uploadFile-ChooseFile");
        file.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                delegate.onFileNameChanged();
            }
        });
        uploadPanel.insert(file, 0);
    }
}
