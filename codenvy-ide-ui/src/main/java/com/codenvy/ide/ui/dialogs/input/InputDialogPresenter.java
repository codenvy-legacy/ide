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

import com.codenvy.ide.ui.dialogs.CancelCallback;
import com.codenvy.ide.ui.dialogs.InputCallback;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * {@link InputDialog} implementation.
 *
 * @author Mickaël Leduque
 * @author Artem Zatsarynnyy
 */
public class InputDialogPresenter implements InputDialog, InputDialogView.ActionDelegate {

    /** This component view. */
    private final InputDialogView view;

    /** The callback used on OK. */
    private final InputCallback inputCallback;

    /** The callback used on cancel. */
    private final CancelCallback cancelCallback;

    @AssistedInject
    public InputDialogPresenter(final @Nonnull InputDialogView view,
                                final @Nonnull @Assisted("title") String title,
                                final @Nonnull @Assisted("label") String label,
                                final @Nullable @Assisted InputCallback inputCallback,
                                final @Nullable @Assisted CancelCallback cancelCallback) {
        this(view, title, label, "", 0, 0, inputCallback, cancelCallback);
    }

    @AssistedInject
    public InputDialogPresenter(final @Nonnull InputDialogView view,
                                final @Nonnull @Assisted("title") String title,
                                final @Nonnull @Assisted("label") String label,
                                final @Nonnull @Assisted("initialValue") String initialValue,
                                final @Nonnull @Assisted("selectionStartIndex") Integer selectionStartIndex,
                                final @Nonnull @Assisted("selectionLength") Integer selectionLength,
                                final @Nullable @Assisted InputCallback inputCallback,
                                final @Nullable @Assisted CancelCallback cancelCallback) {
        this.view = view;
        this.view.setContent(label);
        this.view.setTitle(title);
        this.view.setValue(initialValue);
        this.view.setSelectionStartIndex(selectionStartIndex);
        this.view.setSelectionLength(selectionLength);
        this.inputCallback = inputCallback;
        this.cancelCallback = cancelCallback;
        this.view.setDelegate(this);
    }

    @Override
    public void cancelled() {
        this.view.closeDialog();
        if (this.cancelCallback != null) {
            this.cancelCallback.cancelled();
        }
    }

    @Override
    public void accepted() {
        this.view.closeDialog();
        if (this.inputCallback != null) {
            this.inputCallback.accepted(view.getValue());
        }
    }

    @Override
    public void show() {
        this.view.showDialog();
    }

    @Override
    public InputDialog withValidator(InputValidator inputValidator) {
        this.view.setValidator(inputValidator);
        return this;
    }
}
