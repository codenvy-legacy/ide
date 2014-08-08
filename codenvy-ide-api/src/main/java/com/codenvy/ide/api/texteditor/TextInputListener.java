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
package com.codenvy.ide.api.texteditor;

import com.codenvy.ide.api.text.Document;

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
