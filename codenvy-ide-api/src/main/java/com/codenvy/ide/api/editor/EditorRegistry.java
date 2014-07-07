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

import javax.validation.constraints.NotNull;


/**
 * Editor Registry allows to register new Editor for given FileType. This editor will be used as default to open such kind of Files.
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public interface EditorRegistry {
    /**
     * Register editor provider for file type.
     *
     * @param fileType
     * @param provider
     */
    void register(@NotNull FileType fileType, @NotNull EditorProvider provider);

    /**
     * Get default editor provide assigned for file type;
     *
     * @param fileType
     *         resource file type
     * @return editor provider
     */
    @NotNull
    EditorProvider getDefaultEditor(@NotNull FileType fileType);
}