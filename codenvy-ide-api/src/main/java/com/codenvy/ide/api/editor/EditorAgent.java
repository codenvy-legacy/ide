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

import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.api.resources.model.File;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * Editor Agent manages Editors, it allows to open a new editor with given file, retrieve current active editor and find all the opened
 * editors.
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public interface EditorAgent {
    /**
     * Open editor with given file
     *
     * @param file
     */
    void openEditor(@NotNull final File file);

    /**
     * Get all opened editors
     *
     * @return map with all opened editors
     */
    @NotNull
    StringMap<EditorPartPresenter> getOpenedEditors();

    /**
     * Current active editor
     *
     * @return the current active editor
     */
    @Nullable
    EditorPartPresenter getActiveEditor();
}