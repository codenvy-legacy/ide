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
package org.eclipse.che.ide.ui.dialogs.choice;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.eclipse.che.ide.ui.dialogs.ConfirmCallback;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * {@link ChoiceDialog} implementation.
 * 
 * @author Mickaël Leduque
 * @author Artem Zatsarynnyy
 */
public class ChoiceDialogPresenter implements ChoiceDialog, ChoiceDialogView.ActionDelegate {

    /** This component view. */
    private final ChoiceDialogView view;

    /** The callback used on first button. */
    private final ConfirmCallback firstChoiceCallback;

    /** The callback used on second button. */
    private final ConfirmCallback secondChoiceCallback;

    @AssistedInject
    public ChoiceDialogPresenter(final @Nonnull ChoiceDialogView view,
                                 final @Nonnull @Assisted("title") String title,
                                 final @Nonnull @Assisted("message") String message,
                                 final @Nonnull @Assisted("firstChoice") String firstChoiceLabel,
                                 final @Nonnull @Assisted("secondChoice") String secondChoiceLabel,
                                 final @Nullable @Assisted("firstCallback") ConfirmCallback firstChoiceCallback,
                                 final @Nullable @Assisted("secondCallback") ConfirmCallback secondChoiceCallback) {
        this(view, title, new InlineHTML(message), firstChoiceLabel, secondChoiceLabel, firstChoiceCallback, secondChoiceCallback);
    }

    @AssistedInject
    public ChoiceDialogPresenter(final @Nonnull ChoiceDialogView view,
                                 final @Nonnull @Assisted String title,
                                 final @Nonnull @Assisted IsWidget content,
                                 final @Nonnull @Assisted("firstChoice") String firstChoiceLabel,
                                 final @Nonnull @Assisted("secondChoice") String secondChoiceLabel,
                                 final @Nullable @Assisted("firstCallback") ConfirmCallback firstChoiceCallback,
                                 final @Nullable @Assisted("secondCallback") ConfirmCallback secondChoiceCallback) {
        this.view = view;
        this.view.setContent(content);
        this.view.setTitle(title);
        this.view.setFirstChoiceLabel(firstChoiceLabel);
        this.view.setSecondChoiceLabel(secondChoiceLabel);
        this.firstChoiceCallback = firstChoiceCallback;
        this.secondChoiceCallback = secondChoiceCallback;
        this.view.setDelegate(this);
    }

    @Override
    public void firstChoiceClicked() {
        this.view.closeDialog();
        if (this.firstChoiceCallback != null) {
            this.firstChoiceCallback.accepted();
        }
    }

    @Override
    public void secondChoiceClicked() {
        this.view.closeDialog();
        if (this.secondChoiceCallback != null) {
            this.secondChoiceCallback.accepted();
        }
    }

    @Override
    public void show() {
        this.view.showDialog();
    }
}
