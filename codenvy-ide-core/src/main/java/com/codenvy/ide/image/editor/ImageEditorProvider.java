/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2014] Codenvy, S.A. 
 *  All Rights Reserved.
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
package com.codenvy.ide.image.editor;

import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.editor.EditorProvider;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Provider for image editor(only displaying images).
 * 
 * @author Ann Shumilova
 */
public class ImageEditorProvider implements EditorProvider {
    private Provider<ImageEditor>     editorProvider;

    /** @param documentProvider */
    @Inject
    public ImageEditorProvider(Provider<ImageEditor> editorProvider) {
        super();
        this.editorProvider = editorProvider;
    }

    /** {@inheritDoc} */
    @Override
    public EditorPartPresenter getEditor() {
        return editorProvider.get();
    }
}
