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
package com.codenvy.ide.ui.dialogs.message;

import com.codenvy.ide.ui.window.Window;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import javax.annotation.Nonnull;

/**
 * Implementation of the message dialog view.
 *
 * @author Mickaël Leduque
 * @author Artem Zatsarynnyy
 */
public class MessageDialogViewImpl extends Window implements MessageDialogView {

    /** The UI binder instance. */
    private static MessageWindowUiBinder uiBinder = GWT.create(MessageWindowUiBinder.class);
    /** The window footer. */
    private final MessageDialogFooter footer;
    /** The container for the window content. */
    @UiField
    SimplePanel content;
    private ActionDelegate delegate;

    @Inject
    public MessageDialogViewImpl(final @Nonnull MessageDialogFooter footer) {
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
    protected void onEnterClicked() {
        delegate.accepted();
    }

    @Override
    public void setContent(final IsWidget content) {
        this.content.clear();
        this.content.setWidget(content);
    }

    /** The UI binder interface for this components. */
    interface MessageWindowUiBinder extends UiBinder<Widget, MessageDialogViewImpl> {
    }
}
