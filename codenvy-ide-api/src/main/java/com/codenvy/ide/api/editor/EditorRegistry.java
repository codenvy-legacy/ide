/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.api.editor;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.resources.FileType;


/**
 * Editor Registry allows to registed new Editor for given FileType. This editor will be used as default to open such kind of Files.
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