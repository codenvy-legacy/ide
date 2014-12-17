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

import javax.annotation.Nonnull;

import com.codenvy.ide.ui.window.Window;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * Implementation of the choice dialog view.
 * 
 * @author Mickaël Leduque
 * @author Artem Zatsarynnyy
 */
public class ChoiceDialogViewImpl extends Window implements ChoiceDialogView {

    /** The UI binder instance. */
    private static ChoiceWindowUiBinder uiBinder = GWT.create(ChoiceWindowUiBinder.class);
    /** The window footer. */
    private final ChoiceDialogFooter footer;
    /** The container for the window content. */
    @UiField
    SimplePanel content;
    private ActionDelegate delegate;

    @Inject
    public ChoiceDialogViewImpl(final @Nonnull ChoiceDialogFooter footer) {
        Widget widget = uiBinder.createAndBindUi(this);
        setWidget(widget);

        this.footer = footer;
        getFooter().add(this.footer);
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
    public void showDialog() {
        this.show();
    }

    @Override
    public void closeDialog() {
        this.hide();
    }

    @Override
    public void setContent(final IsWidget content) {
        this.content.clear();
        this.content.setWidget(content);
    }

    @Override
    public void setFirstChoiceLabel(final String firstChoiceLabel) {
        this.footer.firstChoiceButton.setText(firstChoiceLabel);
    }

    @Override
    public void setSecondChoiceLabel(final String secondChoiceLabel) {
        this.footer.secondChoiceButton.setText(secondChoiceLabel);
    }

    /** The UI binder interface for this components. */
    interface ChoiceWindowUiBinder extends UiBinder<Widget, ChoiceDialogViewImpl> {
    }

}
