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

import com.codenvy.ide.api.editor.DocumentProvider;
import com.codenvy.ide.api.editor.EditorInput;
import com.codenvy.ide.resources.model.File;
import com.codenvy.ide.text.Document;
import com.codenvy.ide.text.DocumentFactory;
import com.codenvy.ide.text.annotation.AnnotationModel;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;


/**
 * Document provider implementation on Resource API
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class ResourceDocumentProvider implements DocumentProvider {

    private DocumentFactory documentFactory;

    @Inject
    public ResourceDocumentProvider(DocumentFactory documentFactory) {
        this.documentFactory = documentFactory;
    }

    /**
     * {@inheritDoc}
     * This implementation return null
     */
    @Override
    public AnnotationModel getAnnotationModel(EditorInput input) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void getDocument(EditorInput input, final DocumentCallback callback) {
        File file = input.getFile();

        file.getProject().getContent(file, new AsyncCallback<File>() {

            @Override
            public void onSuccess(File result) {
                contentReceived(result.getContent(), callback);
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error(ResourceDocumentProvider.class, caught);
            }
        });

    }

    /**
     * @param content
     * @param callback
     */
    private void contentReceived(String content, DocumentCallback callback) {
        callback.onDocument(documentFactory.get(content));
    }


    /** {@inheritDoc} */
    @Override
    public void saveDocument(final EditorInput input, Document document, boolean overwrite, final AsyncCallback<EditorInput> callback) {
        File file = input.getFile();
        file.setContent(document.get());
        file.getProject().updateContent(file, new AsyncCallback<File>() {

            @Override
            public void onSuccess(File result) {
                callback.onSuccess(input);
            }

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }
        });
    }

    /**
     * @see com.codenvy.ide.api.editor.DocumentProvider#saveDocumentAs(com.codenvy.ide.api.editor.EditorInput,
     *      com.codenvy.ide.text.Document,
     *      boolean)
     */
    @Override
    public void saveDocumentAs(EditorInput input, Document document, boolean overwrite) {
        File file = input.getFile();
        file.getProject().createFile(file.getParent(), file.getName(), file.getContent(), file.getMimeType(),
                                     new AsyncCallback<File>() {

                                         @Override
                                         public void onSuccess(File result) {
                                             //TODO
                                         }

                                         @Override
                                         public void onFailure(Throwable caught) {
                                             Log.error(ResourceDocumentProvider.class, caught);
                                         }
                                     });
    }
}
