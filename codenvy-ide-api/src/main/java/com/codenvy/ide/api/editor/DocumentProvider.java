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
import com.codenvy.ide.annotations.Nullable;
import com.codenvy.ide.text.Document;
import com.codenvy.ide.text.annotation.AnnotationModel;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * A document provider maps between domain elements and documents. A document provider has the
 * following responsibilities:
 * <ul>
 * <li>create an annotation model of a domain model element
 * <li>create and manage a textual representation, i.e., a document, of a domain model element
 * <li>create and save the content of domain model elements based on given documents
 * <li>update the documents this document provider manages for domain model elements to changes
 * directly applied to those domain model elements
 * <li>notify all element state listeners about changes directly applied to domain model elements
 * this document provider manages a document for, i.e. the document provider must know which changes
 * of a domain model element are to be interpreted as element moves, deletes, etc.
 * </ul>
 * Text editors use document providers to bridge the gap between their input elements and the
 * documents they work on. A single document provider may be shared between multiple editors; the
 * methods take the editors' input elements as a parameter.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public interface DocumentProvider {
    /** Callback for document */
    public interface DocumentCallback {
        void onDocument(@NotNull Document document);
    }

    /**
     * Returns the document for the given element. Usually the document contains
     * a textual presentation of the content of the element, or is the element itself.
     * Through asynchronous nature of IDE document may create after request to server, so use callback for
     * receiving document
     *
     * @param input
     *         the input, or <code>null</code>
     * @param callback
     *         the document callback
     */
    void getDocument(@Nullable EditorInput input, @NotNull DocumentCallback callback);

    /**
     * Saves the given document provided for the given input.
     *
     * @param input
     *         the input, or <code>null</code>
     * @param document
     *         the document
     * @param overwrite
     *         indicates whether overwrite should be performed
     *         while saving the given element if necessary
     * @param callback
     *         the callback for save operation
     */
    void saveDocument(@Nullable EditorInput input, @NotNull Document document, boolean overwrite,
                      @NotNull AsyncCallback<EditorInput> callback);

    /**
     * Saves the given document as new resource, provided for the given input.
     *
     * @param input
     *         the input, or <code>null</code>
     * @param document
     *         the document
     * @param overwrite
     *         indicates whether overwrite should be performed
     *         while saving the given element if necessary
     */
    void saveDocumentAs(@Nullable EditorInput input, @NotNull Document document, boolean overwrite);

    /**
     * Tels provider that Document not used.
     * Editor's must call this method when they closing.
     * @param document
     */
    void documentClosed(@NotNull Document document);

    /**
     * Returns the annotation model for the given input.
     *
     * @param input
     *         the input, or <code>null</code>
     * @return the annotation model, or <code>null</code> if none
     */
    @Nullable
    AnnotationModel getAnnotationModel(@Nullable EditorInput input);
}
