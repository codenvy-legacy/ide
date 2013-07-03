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
package com.codenvy.ide.ext.aws.client.s3.upload;

import com.codenvy.ide.ext.aws.client.AWSLocalizationConstant;
import com.codenvy.ide.json.JsonStringMap;
import com.codenvy.ide.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class S3UploadObjectViewImpl extends DialogBox implements S3UploadObjectView {
    interface S3CreateBucketViewImplUiBinder extends UiBinder<Widget, S3UploadObjectViewImpl> {
    }

    private static S3CreateBucketViewImplUiBinder uiBinder = GWT.create(S3CreateBucketViewImplUiBinder.class);

    @UiField
    Button btnUpload;

    @UiField
    Button btnCancel;

    @UiField
    FormPanel uploadForm;

    @UiField
    FileUpload fileUploadInput;

    @UiField
    HorizontalPanel postFieldsPanel;

    @UiField
    ListBox mimeTypesField;

    @UiField(provided = true)
    AWSLocalizationConstant constant;

    private Hidden nameHiddenField;

    private Hidden mimeTypeHiddenField;

    private Hidden overwriteHiddenField;

    private static final String MIME_TYPE_HIDDEN_FIELD = "mimeType";

    private static final String NAME_HIDDEN_FIELD = "name";

    private static final String OVERWRITE_HIDDEN_FIELD = "overwrite";

    private ActionDelegate delegate;

    private boolean isShown;

    @Inject
    protected S3UploadObjectViewImpl(AWSLocalizationConstant constant) {
        this.constant = constant;

        Widget widget = uiBinder.createAndBindUi(this);

        nameHiddenField = new Hidden(NAME_HIDDEN_FIELD);
        mimeTypeHiddenField = new Hidden(MIME_TYPE_HIDDEN_FIELD);
        overwriteHiddenField = new Hidden(OVERWRITE_HIDDEN_FIELD);

        this.setText("Upload");
        this.setWidget(widget);
    }

    @Override
    public boolean isShown() {
        return isShown;
    }

    @Override
    public void showDialog() {
        this.isShown = true;
        this.center();
        this.show();
    }

    @Override
    public void close() {
        this.isShown = false;
        this.hide();
    }

    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public String getMimeType() {
        return mimeTypesField.getItemText(mimeTypesField.getSelectedIndex());
    }

    @Override
    public void setMimeType(String mimeType) {
        for (int i = 0; i < mimeTypesField.getItemCount(); i++) {
            if (mimeTypesField.getValue(i).equals(mimeType)) {
                mimeTypesField.setSelectedIndex(i);
            }
        }
    }

    @Override
    public void setMimeTypes(JsonStringMap<String> mimeTypes) {
        mimeTypes.iterate(new JsonStringMap.IterationCallback<String>() {
            @Override
            public void onIteration(String key, String value) {
                mimeTypesField.addItem(value);
            }
        });
    }

    @Override
    public void setMimeTypeFieldEnabled(boolean enabled) {
        mimeTypesField.setEnabled(enabled);
    }

    @Override
    public void setUploadButtonEnabled(boolean enabled) {
        btnUpload.setEnabled(enabled);
    }

    @Override
    public FormPanel getUploadForm() {
        return uploadForm;
    }

    @Override
    public void setMimeTypeHiddenField(String mimeType) {
        mimeTypeHiddenField.setValue(mimeType);
        if (postFieldsPanel.getWidgetIndex(mimeTypeHiddenField) == -1)
            postFieldsPanel.add(mimeTypeHiddenField);
    }

    @Override
    public void setNameHiddenField(String name) {
        nameHiddenField.setValue(name);
        if (postFieldsPanel.getWidgetIndex(nameHiddenField) == -1)
            postFieldsPanel.add(nameHiddenField);
    }

    @Override
    public void setOverwriteHiddenField(Boolean overwrite) {
        overwriteHiddenField.setValue(overwrite.toString());
        if (postFieldsPanel.getWidgetIndex(overwriteHiddenField) == -1)
            postFieldsPanel.add(overwriteHiddenField);
    }

    @Override
    public FileUpload getFileUpload() {
        return fileUploadInput;
    }

    @UiHandler("btnUpload")
    public void onOpenButtonClicked(ClickEvent event) {
        delegate.onUploadButtonClicked();
    }

    @UiHandler("btnCancel")
    public void onCancelButtonClicked(ClickEvent event) {
        delegate.onCloseButtonCLicked();
    }
}
