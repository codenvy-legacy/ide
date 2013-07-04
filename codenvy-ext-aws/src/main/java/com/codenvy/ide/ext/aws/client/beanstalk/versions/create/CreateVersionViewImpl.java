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
