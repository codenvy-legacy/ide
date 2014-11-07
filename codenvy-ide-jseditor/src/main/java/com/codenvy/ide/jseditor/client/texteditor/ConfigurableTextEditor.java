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

import javax.annotation.Nonnull;

import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.jseditor.client.editorconfig.TextEditorConfiguration;

public interface ConfigurableTextEditor extends EditorPartPresenter {

    /**
     * Initializes this editor with the configuration and document provider/
     *
     * @param configuration
     *         the configuration of this editor.
     * @param notificationManager
     *         the manager that provides showing notifications
     */

    void initialize(@Nonnull TextEditorConfiguration configuration,
                    @Nonnull NotificationManager notificationManager);


    /**
     * Returns the text editor configuration that was used for initialization.
     * @return the text editor configuration
     */
    TextEditorConfiguration getConfiguration();
}
