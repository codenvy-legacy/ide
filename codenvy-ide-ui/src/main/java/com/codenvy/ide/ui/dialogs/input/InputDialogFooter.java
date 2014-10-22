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
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import javax.annotation.Nonnull;

import static com.codenvy.ide.ui.dialogs.input.InputDialogView.ActionDelegate;
import static com.codenvy.ide.ui.window.Window.Resources;

/**
 * The footer show on input dialogs.
 *
 * @author Mickaël Leduque
 * @author Artem Zatsarynnyy
 */
public class InputDialogFooter extends Composite {

    private static final Resources                   resources = GWT.create(Resources.class);
    /** The UI binder instance. */
    private static       ConfirmWindowFooterUiBinder uiBinder  = GWT.create(ConfirmWindowFooterUiBinder.class);
    /** The i18n messages. */
    @UiField(provided = true)
    UILocalizationConstant messages;
    @UiField
    Button                 okButton;
    @UiField
    Button                 cancelButton;
    /** The action delegate. */
    private ActionDelegate actionDelegate;

    @Inject
    public InputDialogFooter(final @Nonnull UILocalizationConstant messages) {
        this.messages = messages;
        initWidget(uiBinder.createAndBindUi(this));

        okButton.addStyleName(resources.centerPanelCss().blueButton());
        cancelButton.addStyleName(resources.centerPanelCss().button());
    }

    /**
     * Sets the action delegate.
     *
     * @param delegate
     *         the new value
     */
    public void setDelegate(final ActionDelegate delegate) {
        this.actionDelegate = delegate;
    }

    /**
     * Handler set on the OK button.
     *
     * @param event
     *         the event that triggers the handler call
     */
    @UiHandler("okButton")
    public void handleOkClick(final ClickEvent event) {
        this.actionDelegate.accepted();
    }

    /**
     * Handler set on the cancel button.
     *
     * @param event
     *         the event that triggers the handler call
     */
    @UiHandler("cancelButton")
    public void handleCancelClick(final ClickEvent event) {
        this.actionDelegate.cancelled();
    }

    /** The UI binder interface for this component. */
    interface ConfirmWindowFooterUiBinder extends UiBinder<Widget, InputDialogFooter> {
    }
}
