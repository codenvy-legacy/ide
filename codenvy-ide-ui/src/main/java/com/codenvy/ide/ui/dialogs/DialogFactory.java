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

import com.codenvy.ide.ui.dialogs.confirm.ConfirmWindow;
import com.codenvy.ide.ui.dialogs.message.MessageWindow;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.assistedinject.Assisted;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * Factory for {@link MessageWindow} and {@link ConfirmWindow} components.
 *
 * @author "Mickaël Leduque"
 */
public interface DialogFactory {

    /**
     * Create a message window with only text as content.
     *
     * @param title
     *         the window title
     * @param content
     *         the window content/text
     * @param confirmCallback
     *         the callback used on OK
     * @return a {@link ConfirmWindow} instance
     */
    MessageWindow createMessageWindow(@NotNull @Assisted("title") String title,
                                      @NotNull @Assisted("message") String content,
                                      @Nullable ConfirmCallback confirmCallback);

    /**
     * Create a message window with a widget as content.
     *
     * @param title
     *         the window title
     * @param content
     *         the window content
     * @param confirmCallback
     *         the callback used on OK
     * @return a {@link ConfirmWindow} instance
     */
    MessageWindow createMessageWindow(@NotNull String title,
                                      @NotNull IsWidget content,
                                      @Nullable ConfirmCallback confirmCallback);

    /**
     * Create a confirm window with only text as content.
     *
     * @param title
     *         the window title
     * @param content
     *         the window content/text
     * @param confirmCallback
     *         the callback used on OK
     * @param cancelCallback
     *         the callback used on cancel
     * @return a {@link ConfirmWindow} instance
     */
    ConfirmWindow createConfirmWindow(@NotNull @Assisted("title") String title,
                                      @NotNull @Assisted("message") String content,
                                      @Nullable ConfirmCallback confirmCallback,
                                      @Nullable CancelCallback cancelCallback);

    /**
     * Create a confirm window with a widget as content.
     *
     * @param title
     *         the window title
     * @param content
     *         the window content
     * @param confirmCallback
     *         the callback used on OK
     * @param cancelCallback
     *         the callback used on cancel
     * @return a {@link ConfirmWindow} instance
     */
    ConfirmWindow createConfirmWindow(@NotNull String title,
                                      @NotNull IsWidget content,
                                      @Nullable ConfirmCallback confirmCallback,
                                      @Nullable CancelCallback cancelCallback);
}
