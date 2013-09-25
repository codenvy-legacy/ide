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
 * The implementation of {@link S3UploadObjectView}.
 *
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

    /**
     * Create view.
     *
     * @param constant
     */
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

    /** {@inheritDoc} */
    @Override
    public boolean isShown() {
        return isShown;
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
    public void close() {
        this.isShown = false;
        this.hide();
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public String getMimeType() {
        return mimeTypesField.getItemText(mimeTypesField.getSelectedIndex());
    }

    /** {@inheritDoc} */
    @Override
    public void setMimeType(String mimeType) {
        for (int i = 0; i < mimeTypesField.getItemCount(); i++) {
            if (mimeTypesField.getValue(i).equals(mimeType)) {
                mimeTypesField.setSelectedIndex(i);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setMimeTypes(JsonStringMap<String> mimeTypes) {
        mimeTypes.iterate(new JsonStringMap.IterationCallback<String>() {
            @Override
            public void onIteration(String key, String value) {
                mimeTypesField.addItem(value);
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void setMimeTypeFieldEnabled(boolean enabled) {
        mimeTypesField.setEnabled(enabled);
    }

    /** {@inheritDoc} */
    @Override
    public void setUploadButtonEnabled(boolean enabled) {
        btnUpload.setEnabled(enabled);
    }

    /** {@inheritDoc} */
    @Override
    public FormPanel getUploadForm() {
        return uploadForm;
    }

    /** {@inheritDoc} */
    @Override
    public void setMimeTypeHiddenField(String mimeType) {
        mimeTypeHiddenField.setValue(mimeType);
        if (postFieldsPanel.getWidgetIndex(mimeTypeHiddenField) == -1)
            postFieldsPanel.add(mimeTypeHiddenField);
    }

    /** {@inheritDoc} */
    @Override
    public void setNameHiddenField(String name) {
        nameHiddenField.setValue(name);
        if (postFieldsPanel.getWidgetIndex(nameHiddenField) == -1)
            postFieldsPanel.add(nameHiddenField);
    }

    /** {@inheritDoc} */
    @Override
    public void setOverwriteHiddenField(Boolean overwrite) {
        overwriteHiddenField.setValue(overwrite.toString());
        if (postFieldsPanel.getWidgetIndex(overwriteHiddenField) == -1)
            postFieldsPanel.add(overwriteHiddenField);
    }

    /** {@inheritDoc} */
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
