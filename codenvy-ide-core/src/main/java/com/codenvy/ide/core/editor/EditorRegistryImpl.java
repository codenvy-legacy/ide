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
package com.codenvy.ide.core.editor;

import com.codenvy.ide.api.editor.EditorProvider;
import com.codenvy.ide.api.editor.EditorRegistry;
import com.codenvy.ide.api.extension.SDK;
import com.codenvy.ide.api.resources.FileType;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.IntegerMap;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import javax.validation.constraints.NotNull;


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
