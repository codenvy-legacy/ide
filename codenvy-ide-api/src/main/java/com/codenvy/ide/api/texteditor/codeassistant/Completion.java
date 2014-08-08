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
package com.codenvy.ide.api.texteditor.codeassistant;

import com.codenvy.ide.api.text.Document;
import com.codenvy.ide.api.text.Region;

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
