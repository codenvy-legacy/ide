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
package com.codenvy.ide.imageviewer;

import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.editor.EditorProvider;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Provider for image editor(only displaying images).
 * 
 * @author Ann Shumilova
 */
public class ImageViewerProvider implements EditorProvider {
    private Provider<ImageViewer>     editorProvider;

    @Inject
    public ImageViewerProvider(Provider<ImageViewer> editorProvider) {
        super();
        this.editorProvider = editorProvider;
    }

    @Override
    public String getId() {
        return "codenvyImageViewer";
    }

    @Override
    public String getDescription() {
        return "Codenvy Image Viewer";
    }

    /** {@inheritDoc} */
    @Override
    public EditorPartPresenter getEditor() {
        return editorProvider.get();
    }
}
