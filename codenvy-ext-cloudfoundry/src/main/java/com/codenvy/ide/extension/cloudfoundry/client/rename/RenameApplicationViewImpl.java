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
package com.codenvy.ide.extension.cloudfoundry.client.rename;

import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryLocalizationConstant;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryResources;
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
 * The implementation of {@link RenameApplicationView}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class RenameApplicationViewImpl extends DialogBox implements RenameApplicationView {
    interface RenameApplicationViewImplUiBinder extends UiBinder<Widget, RenameApplicationViewImpl> {
    }

    private static RenameApplicationViewImplUiBinder uiBinder = GWT.create(RenameApplicationViewImplUiBinder.class);

    @UiField
    com.codenvy.ide.ui.Button btnRename;
    @UiField
    com.codenvy.ide.ui.Button btnCancel;
    @UiField
    TextBox                   nameField;
    @UiField(provided = true)
    final   CloudFoundryResources            res;
    @UiField(provided = true)
    final   CloudFoundryLocalizationConstant locale;
    private ActionDelegate                   delegate;

    /**
     * Create view.
     *
     * @param resources
     * @param constant
     */
    @Inject
    protected RenameApplicationViewImpl(CloudFoundryResources resources, CloudFoundryLocalizationConstant constant) {
        this.res = resources;
        this.locale = constant;

        Widget widget = uiBinder.createAndBindUi(this);

        this.setText(constant.renameApplicationViewTitle());
        this.setWidget(widget);
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void selectValueInRenameField() {
        nameField.selectAll();
    }

    /** {@inheritDoc} */
    @Override
    public void setEnableRenameButton(boolean isEnabled) {
        btnRename.setEnabled(isEnabled);
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return nameField.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setName(String name) {
        nameField.setText(name);
    }

    /** {@inheritDoc} */
    @Override
    public void showDialog() {
        this.center();
        this.show();
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        this.hide();
    }

    @UiHandler("btnRename")
    void onBtnRenameClick(ClickEvent event) {
        delegate.onRenameClicked();
    }

    @UiHandler("btnCancel")
    void onBtnCancelClick(ClickEvent event) {
        delegate.onCancelClicked();
    }

    @UiHandler("nameField")
    void onNameFieldKeyUp(KeyUpEvent event) {
        delegate.onNameChanged();
    }
}