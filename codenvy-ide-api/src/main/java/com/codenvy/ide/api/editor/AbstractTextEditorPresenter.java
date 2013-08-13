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

import com.codenvy.ide.text.Document;
import com.codenvy.ide.texteditor.api.TextEditorConfiguration;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Abstract implementation of TextEditorPresenter
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 */
public abstract class AbstractTextEditorPresenter extends AbstractEditorPresenter implements CodenvyTextEditor {

    protected TextEditorConfiguration configuration;

    protected DocumentProvider documentProvider;

    protected Document document;

    /** {@inheritDoc} */
    @Override
    public void initialize(TextEditorConfiguration configuration, DocumentProvider documentProvider) {
        this.configuration = configuration;
        this.documentProvider = documentProvider;
    }

    /** @see com.codenvy.ide.api.editor.TextEditorPartPresenter#getDocumentProvider() */
    @Override
    public DocumentProvider getDocumentProvider() {
        return documentProvider;
    }

    /** @see com.codenvy.ide.api.ui.workspace.PartPresenter#getTitleImage() */
    @Override
    public ImageResource getTitleImage() {
        return input.getImageResource();
    }

    /** @see com.codenvy.ide.api.ui.workspace.PartPresenter#getTitle() */
    @Override
    public String getTitle() {
        if (isDirty()) {
            return "*" + input.getName();
        } else {
            return input.getName();
        }
    }

    /** @see com.codenvy.ide.api.editor.EditorPartPresenter#doSave() */
    @Override
    public void doSave() {
        documentProvider.saveDocument(getEditorInput(), document, false, new AsyncCallback<EditorInput>() {

            @Override
            public void onSuccess(EditorInput result) {
                updateDirtyState(false);
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error(AbstractTextEditorPresenter.class, caught);
            }
        });
    }

    /** @see com.codenvy.ide.api.editor.EditorPartPresenter#doSaveAs() */
    @Override
    public void doSaveAs() {
        // TODO Auto-generated method stub

    }
}
