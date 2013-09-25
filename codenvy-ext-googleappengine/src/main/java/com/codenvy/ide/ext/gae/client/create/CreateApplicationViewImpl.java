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
package com.codenvy.ide.ext.gae.client.create;

import com.codenvy.ide.ext.gae.client.GAELocalization;
import com.codenvy.ide.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The implementation of {@link CreateApplicationView}.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class CreateApplicationViewImpl extends DialogBox implements CreateApplicationView {
    interface CreateApplicationViewImplUiBinder extends UiBinder<Widget, CreateApplicationViewImpl> {
    }

    private static CreateApplicationViewImplUiBinder uiBinder = GWT.create(CreateApplicationViewImplUiBinder.class);

    @UiField
    Button createButton;

    @UiField
    Button deployButton;

    @UiField
    Button cancelButton;

    @UiField
    Label instructionLabel;

    @UiField(provided = true)
    GAELocalization constant;

    private boolean isShown;

    private ActionDelegate delegate;

    /**
     * Constructor for View.
     */
    @Inject
    public CreateApplicationViewImpl(GAELocalization constant) {
        this.constant = constant;

        Widget widget = uiBinder.createAndBindUi(this);

        this.setText(constant.createApplicationViewTitle());
        this.setWidget(widget);
    }

    /** {@inheritDoc} */
    @Override
    public void enableDeployButton(boolean enable) {
        deployButton.setEnabled(enable);
    }

    /** {@inheritDoc} */
    @Override
    public void enableCreateButton(boolean enable) {
        createButton.setEnabled(enable);
    }

    /** {@inheritDoc} */
    @Override
    public void setUserInstruction(String userInstruction) {
        instructionLabel.setText(userInstruction);
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

    @UiHandler("deployButton")
    public void onDeployButtonClicked(ClickEvent event) {
        delegate.onDeployApplicationButtonClicked();
    }

    @UiHandler("createButton")
    public void onCreateButtonClicked(ClickEvent event) {
        delegate.onCreateApplicationButtonClicked();
    }

    @UiHandler("cancelButton")
    public void onCancelButtonClicked(ClickEvent event) {
        delegate.onCancelButtonClicked();
    }
}
