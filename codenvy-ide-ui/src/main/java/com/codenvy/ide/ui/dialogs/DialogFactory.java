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
package com.codenvy.ide.ui.dialogs;

import com.codenvy.ide.ui.dialogs.confirm.ConfirmDialog;
import com.codenvy.ide.ui.dialogs.input.InputDialog;
import com.codenvy.ide.ui.dialogs.message.MessageDialog;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.assistedinject.Assisted;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * Factory for {@link MessageDialog}, {@link ConfirmDialog} and {@link InputDialog} components.
 *
 * @author Mickaël Leduque
 * @author Artem Zatsarynnyy
 */
public interface DialogFactory {

    /**
     * Create a message dialog with only text as content.
     *
     * @param title
     *         the window title
     * @param content
     *         the window content/text
     * @param confirmCallback
     *         the callback used on OK
     * @return a {@link com.codenvy.ide.ui.dialogs.confirm.ConfirmDialog} instance
     */
    MessageDialog createMessageDialog(@NotNull @Assisted("title") String title,
                                      @NotNull @Assisted("message") String content,
                                      @Nullable ConfirmCallback confirmCallback);

    /**
     * Create a message dialog with a widget as content.
     *
     * @param title
     *         the window title
     * @param content
     *         the window content
     * @param confirmCallback
     *         the callback used on OK
     * @return a {@link com.codenvy.ide.ui.dialogs.confirm.ConfirmDialog} instance
     */
    MessageDialog createMessageDialog(@NotNull String title,
                                      @NotNull IsWidget content,
                                      @Nullable ConfirmCallback confirmCallback);

    /**
     * Create a confirm dialog with only text as content.
     *
     * @param title
     *         the window title
     * @param content
     *         the window content/text
     * @param confirmCallback
     *         the callback used on OK
     * @param cancelCallback
     *         the callback used on cancel
     * @return a {@link com.codenvy.ide.ui.dialogs.confirm.ConfirmDialog} instance
     */
    ConfirmDialog createConfirmDialog(@NotNull @Assisted("title") String title,
                                      @NotNull @Assisted("message") String content,
                                      @Nullable ConfirmCallback confirmCallback,
                                      @Nullable CancelCallback cancelCallback);

    /**
     * Create a confirm dialog with a widget as content.
     *
     * @param title
     *         the window title
     * @param content
     *         the window content
     * @param confirmCallback
     *         the callback used on OK
     * @param cancelCallback
     *         the callback used on cancel
     * @return a {@link com.codenvy.ide.ui.dialogs.confirm.ConfirmDialog} instance
     */
    ConfirmDialog createConfirmDialog(@NotNull String title,
                                      @NotNull IsWidget content,
                                      @Nullable ConfirmCallback confirmCallback,
                                      @Nullable CancelCallback cancelCallback);

    /**
     * Create an input dialog.
     *
     * @param title
     *         the window title
     * @param label
     *         the label of the input field
     * @param initialValue
     *         the value used to initialize the input
     * @param inputCallback
     *         the callback used on OK
     * @param cancelCallback
     *         the callback used on cancel
     * @return a {@link com.codenvy.ide.ui.dialogs.input.InputDialog} instance
     */
    InputDialog createInputDialog(@NotNull @Assisted("title") String title,
                                  @NotNull @Assisted("label") String label,
                                  @NotNull @Assisted("initialValue") String initialValue,
                                  @Nullable InputCallback inputCallback,
                                  @Nullable CancelCallback cancelCallback);
}
