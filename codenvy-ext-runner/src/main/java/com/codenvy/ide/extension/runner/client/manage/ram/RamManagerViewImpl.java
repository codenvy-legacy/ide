/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.extension.runner.client.manage.ram;

import com.codenvy.ide.extension.runner.client.RunnerResources;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import javax.annotation.Nonnull;

/** @author Vitaly Parfonov */
public class RamManagerViewImpl extends Composite implements RamManagerView {
    private ActionDelegate delegate;

    @UiField
    TextBox memoryField;
    @UiField
    Label   warningLabel;

    @UiField(provided = true)
    RunnerResources.Css styles;

    interface RamManagerViewImplUiBinder extends UiBinder<Widget, RamManagerViewImpl> {
    }

    private static RamManagerViewImplUiBinder ourUiBinder = GWT.create(RamManagerViewImplUiBinder.class);

    /** Create view. */
    @Inject
    protected RamManagerViewImpl(RunnerResources resources) {
        styles = resources.runner();
        styles.ensureInjected();
        initWidget(ourUiBinder.createAndBindUi(this));
        memoryField.getElement().setAttribute("type", "number");
        memoryField.getElement().setAttribute("step", "128");
        memoryField.getElement().setAttribute("min", "0");
        memoryField.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                delegate.validateRamSize(memoryField.getText());
            }
        });
        memoryField.addKeyPressHandler(new KeyPressHandler() {
            @Override
            public void onKeyPress(KeyPressEvent event) {
                Character charCode = event.getCharCode();
                if (!(Character.isDigit(charCode))) {
                    memoryField.cancelKey();
                }
            }
        });
    }

    @Override
    public String getRam() {
        return memoryField.getText();
    }

    @Override
    public void showRam(String ram) {
        memoryField.setText(ram);
    }

    @Override
    public void showWarnMessage(@Nonnull String warning) {
        memoryField.addStyleName(styles.inputError());
        warningLabel.setText(warning);
    }

    @Override
    public void hideWarnMessage() {
        memoryField.removeStyleName(styles.inputError());
        warningLabel.setText("");
    }

    @UiHandler("memoryField")
    public void onRamFieldsChanged(KeyUpEvent event) {
        delegate.validateRamSize(memoryField.getText());
    }

    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public Widget asWidget() {
        return this;
    }
}
