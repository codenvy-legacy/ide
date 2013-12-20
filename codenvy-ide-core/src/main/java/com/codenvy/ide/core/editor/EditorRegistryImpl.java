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
package com.codenvy.ide.core.editor;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.editor.EditorProvider;
import com.codenvy.ide.api.editor.EditorRegistry;
import com.codenvy.ide.api.extension.SDK;
import com.codenvy.ide.api.resources.FileType;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.IntegerMap;
import com.google.inject.Inject;
import com.google.inject.name.Named;


/**
 * Registry for holding {@link EditorProvider} for specific {@link FileType}.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
@SDK(title = "ide.api.editorRegistry")
public class EditorRegistryImpl implements EditorRegistry {

    private IntegerMap<EditorProvider> registry;

    @Inject
    public EditorRegistryImpl(@Named("defaultEditor") EditorProvider defaultProvider,
                              @Named("defaultFileType") FileType defaultFile) {
        super();
        registry = Collections.createIntegerMap();
        register(defaultFile, defaultProvider);
    }

    /** {@inheritDoc} */
    @Override
    public void register(@NotNull FileType fileType, @NotNull EditorProvider provider) {
        registry.put(fileType.getId(), provider);
    }

    /** {@inheritDoc} */
    @Override
    public EditorProvider getDefaultEditor(@NotNull FileType fileType) {
        return registry.get(fileType.getId());
    }
}
