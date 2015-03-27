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
package org.eclipse.che.ide.ui.dialogs;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.eclipse.che.ide.ui.dialogs.choice.ChoiceDialog;
import org.eclipse.che.ide.ui.dialogs.confirm.ConfirmDialog;
import org.eclipse.che.ide.ui.dialogs.input.InputDialog;
import org.eclipse.che.ide.ui.dialogs.message.MessageDialog;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.assistedinject.Assisted;

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
     * @return a {@link ConfirmDialog} instance
     */
    MessageDialog createMessageDialog(@Nonnull @Assisted("title") String title,
                                      @Nonnull @Assisted("message") String content,
                                      @Nullable ConfirmCallback confirmCallback);

    /**
     * Create a message dialog with only text as content.
     *
     * @param title
     *         the window title
     * @param content
     *         the window content/text
     * @param confirmCallback
     *         the window confirm button tex
     * @param confirmButtonText
     *         the callback used on Confirm
     * @return a {@link ConfirmDialog} instance
     */

    MessageDialog createMessageDialog(@Nonnull @Assisted("title") String title,
                                      @Nonnull IsWidget content,
                                      @Nullable ConfirmCallback confirmCallback,
                                      @Nonnull @Assisted("confirmButtonText") String confirmButtonText);

    /**
     * Create a message dialog with a widget as content.
     *
     * @param title
     *         the window title
     * @param content
     *         the window content
     * @param confirmCallback
     *         the callback used on OK
     * @return a {@link ConfirmDialog} instance
     */
    MessageDialog createMessageDialog(@Nonnull String title,
                                      @Nonnull IsWidget content,
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
     * @return a {@link ConfirmDialog} instance
     */
    ConfirmDialog createConfirmDialog(@Nonnull @Assisted("title") String title,
                                      @Nonnull @Assisted("message") String content,
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
     * @return a {@link ConfirmDialog} instance
     */
    ConfirmDialog createConfirmDialog(@Nonnull String title,
                                      @Nonnull IsWidget content,
                                      @Nullable ConfirmCallback confirmCallback,
                                      @Nullable CancelCallback cancelCallback);

    /**
     * Create an input dialog.
     *
     * @param title
     *         the window title
     * @param label
     *         the label of the input field
     * @param inputCallback
     *         the callback used on OK
     * @param cancelCallback
     *         the callback used on cancel
     * @return an {@link InputDialog} instance
     */
    InputDialog createInputDialog(@Nonnull @Assisted("title") String title,
                                  @Nonnull @Assisted("label") String label,
                                  @Nullable InputCallback inputCallback,
                                  @Nullable CancelCallback cancelCallback);

    /**
     * Create an input dialog with the specified initial value.
     * <p/>
     * The {@code initialValue} may be pre-selected. Selection begins
     * at the specified {@code selectionStartIndex} and extends to the
     * character at index {@code selectionLength}.
     *
     * @param title
     *         the window title
     * @param label
     *         the label of the input field
     * @param initialValue
     *         the value used to initialize the input
     * @param selectionStartIndex
     *         the beginning index of the {@code initialValue} to select, inclusive
     * @param selectionLength
     *         the number of characters of the {@code initialValue} to be selected
     * @param inputCallback
     *         the callback used on OK
     * @param cancelCallback
     *         the callback used on cancel
     * @return an {@link InputDialog} instance
     */
    InputDialog createInputDialog(@Nonnull @Assisted("title") String title,
                                  @Nonnull @Assisted("label") String label,
                                  @Nonnull @Assisted("initialValue") String initialValue,
                                  @Nonnull @Assisted("selectionStartIndex") Integer selectionStartIndex,
                                  @Nonnull @Assisted("selectionLength") Integer selectionLength,
                                  @Nullable InputCallback inputCallback,
                                  @Nullable CancelCallback cancelCallback);

    /**
     * Create a choice dialog with only text as content.
     *
     * @param title
     *         the window title
     * @param content
     *         the window content/text
     * @param firstChoiceLabel
     *         the label for the first choice
     * @param secondChoiceLabel
     *         the label for the second choice
     * @param firstChoiceCallback
     *         the callback used on fist choice
     * @param secondChoiceCallback
     *         the callback used on second choice
     * @return a {@link ConfirmDialog} instance
     */
    ChoiceDialog createChoiceDialog(@Nonnull @Assisted("title") String title,
                                     @Nonnull @Assisted("message") String content,
                                     @Nonnull @Assisted("firstChoice") String firstChoiceLabel,
                                     @Nonnull @Assisted("secondChoice") String secondChoiceLabel,
                                    @Nullable @Assisted("firstCallback") ConfirmCallback firstChoiceCallback,
                                    @Nullable @Assisted("secondCallback") ConfirmCallback secondChoiceCallback);

    /**
     * Create a choice dialog with a widget as content.
     *
     * @param title
     *         the window title
     * @param content
     *         the window content
     * @param firstChoiceLabel
     *         the label for the first choice
     * @param secondChoiceLabel
     *         the label for the first choice
     * @param firstChoiceCallback
     *         the callback used on fist choice
     * @param secondChoiceCallback
     *         the callback used on second choice
     * @return a {@link ConfirmDialog} instance
     */
    ChoiceDialog createChoiceDialog(@Nonnull String title,
                                     @Nonnull IsWidget content,
                                     @Nonnull @Assisted("firstChoice") String firstChoiceLabel,
                                     @Nonnull @Assisted("secondChoice") String secondChoiceLabel,
                                    @Nullable @Assisted("firstCallback") ConfirmCallback firstChoiceCallback,
                                    @Nullable @Assisted("secondCallback") ConfirmCallback secondChoiceCallback);
}
