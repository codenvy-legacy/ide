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
package com.codenvy.ide.ui.dialogs.input;

import com.codenvy.ide.ui.window.Window;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import javax.annotation.Nonnull;

/**
 * Implementation of the input dialog view.
 *
 * @author Mickaël Leduque
 * @author Artem Zatsarynnyy
 */
public class InputDialogViewImpl extends Window implements InputDialogView {

    /** The UI binder instance. */
    private static ConfirmWindowUiBinder uiBinder = GWT.create(ConfirmWindowUiBinder.class);
    /** The window footer. */
    private final InputDialogFooter footer;
    @UiField
    Label   label;
    @UiField
    TextBox value;
    private ActionDelegate delegate;

    @Inject
    public InputDialogViewImpl(final @Nonnull InputDialogFooter footer) {
        Widget widget = uiBinder.createAndBindUi(this);
        setWidget(widget);

        this.footer = footer;
        getFooter().add(this.footer);

        this.ensureDebugId("askValueDialog-window");
        this.value.ensureDebugId("askValueDialog-textBox");
    }

    @Override
    public void show() {
        new Timer() {
            @Override
            public void run() {
                value.setFocus(true);
            }
        }.schedule(300);
        super.show();
    }

    @Override
    public void setDelegate(final ActionDelegate delegate) {
        this.delegate = delegate;
        this.footer.setDelegate(this.delegate);
    }

    @Override
    protected void onClose() {
    }

    @Override
    protected void onEnterClicked() {
        delegate.accepted();
    }

    @Override
    public void showDialog() {
        this.show();
    }

    @Override
    public void closeDialog() {
        this.hide();
    }

    @Override
    public void setContent(final String label) {
        this.label.setText(label);
    }

    @Override
    public void setValue(String value) {
        this.value.setText(value);
    }

    @Override
    public String getValue() {
        return value.getValue();
    }

    /** The UI binder interface for this components. */
    interface ConfirmWindowUiBinder extends UiBinder<Widget, InputDialogViewImpl> {
    }
}
