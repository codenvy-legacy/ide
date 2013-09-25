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
package com.codenvy.ide.texteditor.api;

import com.codenvy.ide.text.Document;

/**
 * Text input listeners registered with an
 * {@link TextEditorPartView} are informed if the document
 * serving as the text viewer's model is replaced.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public interface TextInputListener {
    /**
     * Called after the input document has been replaced.
     *
     * @param oldDocument
     *         the text display's previous input document
     * @param newDocument
     *         the text display's new input document
     */
    void inputDocumentChanged(Document oldDocument, Document newDocument);
}
