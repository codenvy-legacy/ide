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
package com.codenvy.ide.ext.aws.client.beanstalk.versions.create;

import com.codenvy.ide.ext.aws.client.AWSLocalizationConstant;
import com.codenvy.ide.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The implementation of {@link CreateVersionView}.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class CreateVersionViewImpl extends DialogBox implements CreateVersionView {
    interface CreateVersionViewImplUiBinder extends UiBinder<Widget, CreateVersionViewImpl> {
    }

    private static CreateVersionViewImplUiBinder uiBinder = GWT.create(CreateVersionViewImplUiBinder.class);

    @UiField
    TextBox versionLabelField;

    @UiField
    TextBox descriptionField;

    @UiField
    TextBox s3BucketField;

    @UiField
    TextBox s3KeyField;

    @UiField
    Button createButton;

    @UiField
    Button cancelButton;

    @UiField(provided = true)
    AWSLocalizationConstant constant;

    private boolean isSHown;

    private ActionDelegate delegate;

    /**
     * Create view.
     *
     * @param constant
     */
    @Inject
    protected CreateVersionViewImpl(AWSLocalizationConstant constant) {
        this.constant = constant;

        Widget widget = uiBinder.createAndBindUi(this);

        this.setText(constant.createVersionViewTitle());
        this.setWidget(widget);
    }

    /** {@inheritDoc} */
    @Override
    public String getVersionLabel() {
        return versionLabelField.getText();
    }

    /** {@inheritDoc} */
    @Override
    public String getDescription() {
        return versionLabelField.getText();
    }

    /** {@inheritDoc} */
    @Override
    public String getS3Bucket() {
        return s3BucketField.getText();
    }

    /** {@inheritDoc} */
    @Override
    public String getS3Key() {
        return s3KeyField.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void enableCreateButton(boolean enable) {
        createButton.setEnabled(enable);
    }

    /** {@inheritDoc} */
    @Override
    public void focusInVersionLabelField() {
        versionLabelField.setFocus(true);
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isShown() {
        return isSHown;
    }

    /** {@inheritDoc} */
    @Override
    public void showDialog() {
        this.isSHown = true;
        this.center();
        this.show();
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        this.isSHown = false;
        this.hide();
    }

    @UiHandler("createButton")
    public void onCreateButtonClicked(ClickEvent event) {
        delegate.onCreateButtonClicked();
    }

    @UiHandler("cancelButton")
    public void onCancelButtonClicked(ClickEvent event) {
        delegate.onCancelButtonClicked();
    }

    @UiHandler("versionLabelField")
    public void onVersionLabelKeyUpEvent(KeyUpEvent event) {
        delegate.onVersionLabelKeyUp();
    }
}
