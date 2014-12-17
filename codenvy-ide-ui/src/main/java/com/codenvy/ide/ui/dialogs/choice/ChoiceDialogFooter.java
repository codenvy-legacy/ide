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
package com.codenvy.ide.ui.dialogs.choice;

import com.codenvy.ide.ui.dialogs.choice.ChoiceDialogView.ActionDelegate;
import com.codenvy.ide.ui.window.Window.Resources;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * The footer show on choice dialogs.
 * 
 * @author Mickaël Leduque
 * @author Artem Zatsarynnyy
 */
public class ChoiceDialogFooter extends Composite {

    private static final Resources                   resources = GWT.create(Resources.class);
    /** The UI binder instance. */
    private static       ChoiceDialogFooterUiBinder uiBinder  = GWT.create(ChoiceDialogFooterUiBinder.class);
    @UiField
    Button                 firstChoiceButton;
    @UiField
    Button                 secondChoiceButton;
    /** The action delegate. */
    private ActionDelegate actionDelegate;

    @Inject
    public ChoiceDialogFooter() {
        initWidget(uiBinder.createAndBindUi(this));

        firstChoiceButton.addStyleName(resources.centerPanelCss().blueButton());
        firstChoiceButton.getElement().setId("ask-dialog-first");
        secondChoiceButton.addStyleName(resources.centerPanelCss().button());
        secondChoiceButton.getElement().setId("ask-dialog-second");
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
     * Handler set on the first button.
     * 
     * @param event the event that triggers the handler call
     */
    @UiHandler("firstChoiceButton")
    public void handleFirstChoiceClick(final ClickEvent event) {
        this.actionDelegate.firstChoiceClicked();
    }

    /**
     * Handler set on the second button.
     * 
     * @param event the event that triggers the handler call
     */
    @UiHandler("secondChoiceButton")
    public void handleSecondChoiceClick(final ClickEvent event) {
        this.actionDelegate.secondChoiceClicked();
    }

    /** The UI binder interface for this component. */
    interface ChoiceDialogFooterUiBinder extends UiBinder<Widget, ChoiceDialogFooter> {
    }
}
