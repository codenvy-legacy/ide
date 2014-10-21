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

import com.codenvy.ide.ui.dialogs.ConfirmCallback;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.codenvy.ide.ui.dialogs.message.MessageWindowView.ActionDelegate;

/**
 * Message/information window {@link MessageWindow} implementation.
 *
 * @author "Mickaël Leduque"
 */
public class MessageWindowPresenter implements MessageWindow, ActionDelegate {

    /** This component view. */
    private final MessageWindowView view;

    /** The callback used on OK. */
    private final ConfirmCallback confirmCallback;

    @AssistedInject
    public MessageWindowPresenter(final @Nonnull MessageWindowView view,
                                  final @Nonnull @Assisted("title") String title,
                                  final @Nonnull @Assisted("message") String message,
                                  final @Nullable @Assisted ConfirmCallback confirmCallback) {
        this(view, title, new InlineHTML(message), confirmCallback);
    }

    @AssistedInject
    public MessageWindowPresenter(final @Nonnull MessageWindowView view,
                                  final @Nonnull @Assisted String title,
                                  final @Nonnull @Assisted IsWidget content,
                                  final @Nullable @Assisted ConfirmCallback confirmCallback) {
        this.view = view;
        this.view.setContent(content);
        this.view.setTitle(title);
        this.confirmCallback = confirmCallback;
        this.view.setDelegate(this);
    }

    @Override
    public void accepted() {
        this.view.closeDialog();
        if (this.confirmCallback != null) {
            this.confirmCallback.accepted();
        }
    }

    @Override
    public void inform() {
        this.view.showDialog();
    }
}
