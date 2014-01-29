/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
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
package com.codenvy.ide.rename;

import com.codenvy.ide.CoreLocalizationConstant;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * UI for the {@link RenameResourceView}.
 * 
 * @author Ann Shumilova
 */
public class RenameResourceViewImpl extends DialogBox implements RenameResourceView {

    interface RenameResourceViewImplUiBinder extends UiBinder<Widget, RenameResourceViewImpl> {
    }

    @UiField
    TextBox                  newName;
    @UiField
    Button                   btnRename;
    @UiField
    Button                   btnCancel;
    @UiField(provided = true)
    CoreLocalizationConstant locale;
    private ActionDelegate   delegate;

    @Inject
    public RenameResourceViewImpl(CoreLocalizationConstant locale, RenameResourceViewImplUiBinder uiBinder) {
        this.locale = locale;
        this.setText(locale.renameResourceViewTitle());

        this.setWidget(uiBinder.createAndBindUi(this));
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void setName(String name) {
        newName.setValue(name, true);
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return newName.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public void selectText(String value) {
        newName.setSelectionRange(0, value.length());
    }

    /** {@inheritDoc} */
    @Override
    public void setEnableRenameButton(boolean enable) {
        btnRename.setEnabled(enable);
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        this.hide();
    }

    /** {@inheritDoc} */
    @Override
    public void showDialog() {
        this.center();
        this.show();
        this.newName.setFocus(true);
    }

    @UiHandler("btnRename")
    public void onRenameClicked(ClickEvent event) {
        delegate.onRenameClicked();
    }

    @UiHandler("btnCancel")
    public void onCancelClicked(ClickEvent event) {
        delegate.onCancelClicked();
    }

    @UiHandler("newName")
    public void onMessageChanged(KeyUpEvent event) {
        delegate.onValueChanged();
    }

}
