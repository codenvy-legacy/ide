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
package com.codenvy.ide.extension.css.editor;

import com.codenvy.ide.api.editor.CodenvyTextEditor;
import com.codenvy.ide.api.editor.DocumentProvider;
import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.editor.EditorProvider;
import com.google.inject.Inject;
import com.google.inject.Provider;


/**
 * EditorProvider for Css file type
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class CssEditorProvider implements EditorProvider {

    private final DocumentProvider documentProvider;

    private Provider<CodenvyTextEditor> editorProvider;

    private final CssResources cssRes;

    /** @param documentProvider */
    @Inject
    public CssEditorProvider(DocumentProvider documentProvider, CssResources cssRes, Provider<CodenvyTextEditor> editorProvider) {
        super();
        this.documentProvider = documentProvider;
        this.editorProvider = editorProvider;
        this.cssRes = cssRes;
    }

    /** @see com.codenvy.ide.api.editor.EditorProvider#getEditor() */
    @Override
    public EditorPartPresenter getEditor() {
        CodenvyTextEditor textEditor = editorProvider.get();
        textEditor.initialize(new CssEditorConfiguration(cssRes), documentProvider);
        return textEditor;
    }

}
