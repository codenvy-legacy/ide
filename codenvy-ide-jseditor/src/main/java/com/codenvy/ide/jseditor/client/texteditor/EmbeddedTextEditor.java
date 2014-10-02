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
package com.codenvy.ide.jseditor.client.texteditor;

import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.texteditor.outline.OutlinePresenter;
import com.codenvy.ide.jseditor.client.editorconfig.EmbeddedTextEditorConfiguration;

import javax.annotation.Nonnull;

public interface EmbeddedTextEditor extends EditorPartPresenter {

    /**
     * Initializes this editor with the configuration and document provider/
     *
     * @param configuration
     *         the configuration of this editor.
     * @param notificationManager
     *         the manager that provides showing notifications
     */
    void initialize(@Nonnull EmbeddedTextEditorConfiguration configuration,
                    @Nonnull NotificationManager notificationManager);

    /**
     * @return the text editor view implementation //todo need to introduce more simple way to use TextEditorPartView interface
     */
    EmbeddedTextEditorPartView getView();

    /**
     * @return the text editor configuration
     */
    EmbeddedTextEditorConfiguration getConfiguration();

    /**
     * Closes this text editor after optionally saving changes.
     *
     * @param save
     *         <code>true</code> if unsaved changed should be saved, and <code>false</code> if unsaved changed should be discarded
     */
    void close(boolean save);

    /**
     * Returns whether the text in this text editor can be changed by the user.
     *
     * @return <code>true</code> if it can be edited, and <code>false</code> if it is read-only
     */
    boolean isEditable();

    /**
     * Abandons all modifications applied to this text editor's input element's textual presentation since the last save operation.
     */
    void doRevertToSaved();

    /**
     * Returns the outline presenter.<br>
     * If editor doesn't support Outline, returns <code>null</code>
     *
     * @return the outline presenter.
     */
    OutlinePresenter getOutline();
}
