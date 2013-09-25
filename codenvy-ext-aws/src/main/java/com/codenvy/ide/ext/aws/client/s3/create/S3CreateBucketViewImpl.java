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
package com.codenvy.ide.ext.aws.client.s3.create;

import com.codenvy.ide.ext.aws.client.AWSLocalizationConstant;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The implementation of {@link S3CreateBucketPresenter}.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class S3CreateBucketViewImpl extends DialogBox implements S3CreateBucketView {
    interface S3CreateBucketViewImplUiBinder extends UiBinder<Widget, S3CreateBucketViewImpl> {
    }

    private static S3CreateBucketViewImplUiBinder uiBinder = GWT.create(S3CreateBucketViewImplUiBinder.class);

    @UiField
    TextBox bucketNameField;

    @UiField
    ListBox regionField;

    @UiField
    Button btnCreate;

    @UiField
    Button btnCancel;

    @UiField(provided = true)
    AWSLocalizationConstant constant;

    private ActionDelegate delegate;

    private boolean isShown;

    /**
     * Create view.
     *
     * @param constant
     */
    @Inject
    protected S3CreateBucketViewImpl(AWSLocalizationConstant constant) {
        this.constant = constant;

        Widget widget = uiBinder.createAndBindUi(this);

        this.setText("Create bucket");
        this.setWidget(widget);
    }

    /** {@inheritDoc} */
    @Override
    public String getBucketName() {
        return bucketNameField.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setBucketName(String name) {
        bucketNameField.setText(name);
    }

    /** {@inheritDoc} */
    @Override
    public void setCreateButtonEnable(boolean enable) {
        btnCreate.setEnabled(enable);
    }

    /** {@inheritDoc} */
    @Override
    public void setFocusNameField() {
        bucketNameField.setFocus(true);
    }

    /** {@inheritDoc} */
    @Override
    public void setRegions(JsonArray<String> regions) {
        regionField.clear();
        for (int i = 0; i < regions.size(); i++) {
            regionField.addItem(regions.get(i));
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getRegion() {
        return regionField.getValue(regionField.getSelectedIndex());
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

    @UiHandler("btnCreate")
    public void onCreateButtonClicked(ClickEvent event) {
        delegate.onCreateButtonClicked();
    }

    @UiHandler("btnCancel")
    public void onCancelButtonClicked(ClickEvent event) {
        delegate.onCancelButtonCLicked();
    }

    @UiHandler("bucketNameField")
    public void onBucketNameFieldChanged(KeyUpEvent event) {
        delegate.onNameFieldChanged();
    }
}
