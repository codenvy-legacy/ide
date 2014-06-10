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
package com.codenvy.ide.api.editor;

import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.texteditor.api.TextEditorConfiguration;
import com.codenvy.ide.texteditor.api.TextEditorPartView;

import javax.validation.constraints.NotNull;

/**
 * CodenvyTextEditor is an embedded default fully featured Text Editor
 *
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 */
public interface CodenvyTextEditor extends TextEditorPartPresenter {
    /**
     * Initializes this editor with the configuration and document provider/
     *
     * @param configuration
     *         the configuration of this editor.
     * @param documentProvider
     *         the document provider which used in this editor
     * @param notificationManager
     *         the manager that provides showing notifications
     */
    void initialize(@NotNull TextEditorConfiguration configuration, @NotNull DocumentProvider documentProvider,
                    @NotNull NotificationManager notificationManager);

    /**
     * @return the text editor view implementation
     * //todo need to introduce more simple way to use TextEditorPartView interface
     */
    TextEditorPartView getView();

    /**
     * @return the text editor configuration
     */
    TextEditorConfiguration getConfiguration();
}
