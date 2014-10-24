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

import com.codenvy.ide.ui.UILocalizationConstant;
import com.codenvy.ide.ui.window.Window;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
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
    @UiField
    Label   errorHint;
    private ActionDelegate         delegate;
    private int                    selectionStartIndex;
    private int                    selectionLength;
    private InputValidator         validator;
    private UILocalizationConstant localizationConstant;

    @Inject
    public InputDialogViewImpl(final @Nonnull InputDialogFooter footer, UILocalizationConstant localizationConstant) {
        this.localizationConstant = localizationConstant;
        Widget widget = uiBinder.createAndBindUi(this);
        setWidget(widget);

        this.footer = footer;
        getFooter().add(this.footer);

        this.ensureDebugId("askValueDialog-window");
        this.value.ensureDebugId("askValueDialog-textBox");
    }

    @Override
    public void show() {
        super.show();
        footer.okButton.setEnabled(isInputValid());
        value.setSelectionRange(selectionStartIndex, selectionLength);
        new Timer() {
            @Override
            public void run() {
                value.setFocus(true);
            }
        }.schedule(300);
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
        if (isInputValid()) {
            delegate.accepted();
        }
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

    @Override
    public void setSelectionStartIndex(int selectionStartIndex) {
        this.selectionStartIndex = selectionStartIndex;
    }

    @Override
    public void setSelectionLength(int selectionLength) {
        this.selectionLength = selectionLength;
    }

    @Override
    public void setValidator(InputValidator inputValidator) {
        this.validator = inputValidator;
    }

    @Override
    public void showErrorHint(String text) {
        errorHint.setText(text);
    }

    @Override
    public void hideErrorHint() {
        errorHint.setText("");
    }

    @UiHandler("value")
    void onKeyUp(KeyUpEvent event) {
        final boolean inputValid = isInputValid();
        footer.okButton.setEnabled(inputValid);

        if (!inputValid) {
            if (validator == null) {
                showErrorHint(localizationConstant.validationErrorMessage());
            } else {
                final InputValidator.Violation violation = validator.validate(value.getValue());
                if (violation != null) {
                    final String message = violation.getMessage();
                    showErrorHint(message != null ? message : localizationConstant.validationErrorMessage());
                } else {
                    showErrorHint(localizationConstant.validationErrorMessage());
                }
            }
        } else {
            hideErrorHint();
        }
    }

    private boolean isInputValid() {
        if (value.getValue().trim().isEmpty()) {
            return false;
        }
        if (validator != null) {
            final InputValidator.Violation violation = validator.validate(value.getValue());
            return violation == null;
        }
        return true;
    }

    /** The UI binder interface for this components. */
    interface ConfirmWindowUiBinder extends UiBinder<Widget, InputDialogViewImpl> {
    }
}
