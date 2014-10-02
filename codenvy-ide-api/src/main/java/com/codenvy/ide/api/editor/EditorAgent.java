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

import com.codenvy.ide.api.projecttree.generic.FileNode;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.StringMap;
import com.google.gwt.user.client.rpc.AsyncCallback;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Editor Agent manages Editors, it allows to open a new editor with given file,
 * retrieve current active editor and find all the opened editors.
 *
 * @author Nikolay Zamosenchuk
 */
public interface EditorAgent {
    /**
     * Open editor with given file
     *
     * @param file
     */
    void openEditor(@Nonnull final FileNode file);

    /**
     * Returns array of EditorPartPresenters whose content have changed since the last save operation.
     *
     * @return Array<EditorPartPresenter>
     */
    Array<EditorPartPresenter> getDirtyEditors();

    /**
     * Get all opened editors
     *
     * @return map with all opened editors
     */
    @Nonnull
    StringMap<EditorPartPresenter> getOpenedEditors();

    /**
     * Saves all opened files whose content have changed since the last save operation
     *
     * @param callback
     */
    void saveAll(AsyncCallback callback);

    /**
     * Current active editor
     *
     * @return the current active editor
     */
    @Nullable
    EditorPartPresenter getActiveEditor();
}