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
package com.codenvy.ide.ext.aws.client.beanstalk.update;

import com.codenvy.ide.ext.aws.client.AWSLocalizationConstant;
import com.codenvy.ide.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The implementation for {@link DescriptionUpdateView}.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class DescriptionUpdateViewImpl extends DialogBox implements DescriptionUpdateView {
    interface DescriptionUpdateViewImplUiBinder extends UiBinder<Widget, DescriptionUpdateViewImpl> {
    }

    DescriptionUpdateViewImplUiBinder uiBinder = GWT.create(DescriptionUpdateViewImplUiBinder.class);

    @UiField
    TextArea descriptionField;

    @UiField
    Button btnUpdate;

    @UiField
    Button btnCancel;

    @UiField(provided = true)
    AWSLocalizationConstant constant;

    private boolean isShown;

    private ActionDelegate delegate;

    /**
     * Create view.
     *
     * @param constant
     */
    @Inject
    public DescriptionUpdateViewImpl(AWSLocalizationConstant constant) {
        this.constant = constant;

        Widget widget = uiBinder.createAndBindUi(this);

        this.setText(constant.updateDescriptionViewTitle());
        this.setWidget(widget);
    }

    /** {@inheritDoc} */
    @Override
    public String getDescriptionValue() {
        return descriptionField.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public void enableUpdateButton(boolean enable) {
        btnUpdate.setEnabled(enable);
    }

    /** {@inheritDoc} */
    @Override
    public void focusDescriptionField() {
        descriptionField.setFocus(true);
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

    @UiHandler("btnUpdate")
    public void updateButtonClicked(ClickEvent event) {
        delegate.onUpdateClicked();
    }

    @UiHandler("btnCancel")
    public void cancelButtonClicked(ClickEvent event) {
        delegate.onCancelClicked();
    }

    @UiHandler("descriptionField")
    public void onDescriptionFieldChangedValue(KeyUpEvent event) {
        delegate.onDescriptionFieldChangedValue();
    }
}
