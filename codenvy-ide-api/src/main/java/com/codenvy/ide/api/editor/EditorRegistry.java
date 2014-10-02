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

import com.codenvy.ide.api.filetypes.FileType;

import javax.annotation.Nonnull;
import java.util.List;


/**
 * Editor Registry allows to register new Editor for given FileType. This editor will be used as default to open such kind of Files.
 *
 * @author Nikolay Zamosenchuk
 * @author Evgen Vidolob
 */
public interface EditorRegistry {
    /**
     * Register editor provider for file type.
     *
     * @param fileType
     * @param provider
     */
    void register(@Nonnull FileType fileType, @Nonnull EditorProvider provider);

    /**
     * Register default editor.
     *
     * @param fileType
     *         the file type
     * @param provider
     *         the provider
     */
    void registerDefaultEditor(@Nonnull FileType fileType, @Nonnull EditorProvider provider);

    /**
     * Get editor provide assigned for file type;
     *
     * @param fileType
     *         resource file type
     * @return editor provider
     */
    @Nonnull
    EditorProvider getEditor(@Nonnull FileType fileType);


    /**
     * Gets all editors for file type.
     *
     * @param fileType
     *         the file type
     * @return the all editors for file type
     */
    @Nonnull
    List<EditorProvider> getAllEditorsForFileType(@Nonnull FileType fileType);
}