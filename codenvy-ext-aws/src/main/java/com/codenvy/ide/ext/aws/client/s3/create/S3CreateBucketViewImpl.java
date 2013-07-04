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
