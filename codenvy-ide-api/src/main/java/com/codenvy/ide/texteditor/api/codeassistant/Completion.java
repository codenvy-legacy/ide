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
package com.codenvy.ide.texteditor.api.codeassistant;

import com.codenvy.ide.text.Document;
import com.codenvy.ide.text.Region;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public interface Completion {

    /**
     * Inserts the proposed completion into the given document.
     *
     * @param document
     *         the document into which to insert the proposed completion
     */
    void apply(Document document);

    /**
     * Returns the new selection after the proposal has been applied to the given document in absolute document coordinates. If it
     * returns <code>null</code>, no new selection is set.
     * <p/>
     * A document change can trigger other document changes, which have to be taken into account when calculating the new
     * selection. Typically, this would be done by installing a document listener or by using a document position during
     * {@link #apply(Document)}.
     *
     * @param document
     *         the document into which the proposed completion has been inserted
     * @return the new selection in absolute document coordinates
     */
    Region getSelection(Document document);
}
