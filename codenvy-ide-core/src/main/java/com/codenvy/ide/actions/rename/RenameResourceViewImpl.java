/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.actions.rename;

import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.ui.window.Window;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * UI for the {@link RenameResourceView}.
 * 
 * @author Ann Shumilova
 */
public class RenameResourceViewImpl extends Window implements RenameResourceView {

    interface RenameResourceViewImplUiBinder extends UiBinder<Widget, RenameResourceViewImpl> {
    }

    @UiField
    TextBox                  newName;
    Button                   btnRename;
    Button                   btnCancel;
    @UiField(provided = true)
    CoreLocalizationConstant locale;
    private ActionDelegate   delegate;

    @Inject
    public RenameResourceViewImpl(CoreLocalizationConstant locale, RenameResourceViewImplUiBinder uiBinder) {
        this.locale = locale;
        this.setTitle(locale.renameResourceViewTitle());

        this.setWidget(uiBinder.createAndBindUi(this));
        
        btnCancel = createButton(locale.cancel(), "file-rename-cancel", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                delegate.onCancelClicked();
            }
        });
        getFooter().add(btnCancel);

        btnRename = createButton(locale.renameButton(), "file-rename-rename", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                delegate.onRenameClicked();
            }
        });
        getFooter().add(btnRename);
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
    public void selectText(final String value) {
        new Timer() {
            @Override
            public void run() {
                newName.setSelectionRange(0, value.length());
            }
        }.schedule(100);
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
        this.show();
        this.newName.setFocus(true);
    }

    @UiHandler("newName")
    public void onMessageChanged(KeyUpEvent event) {
        delegate.onValueChanged();
    }

    /** {@inheritDoc} */
    @Override
    protected void onClose() {
    }

}
