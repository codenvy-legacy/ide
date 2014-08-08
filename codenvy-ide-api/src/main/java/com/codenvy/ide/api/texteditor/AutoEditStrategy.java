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
import com.codenvy.ide.api.text.DocumentCommand;

/**
 * An auto edit strategy can adapt changes that will be applied to
 * a text display document. The strategy is informed by the text display
 * about each upcoming change in form of a document command. By manipulating
 * this document command, the strategy can influence in which way the text
 * viewer's document is changed. Clients may implement this interface.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public interface AutoEditStrategy {
    /**
     * Allows the strategy to manipulate the document command.
     *
     * @param document
     *         the document that will be changed
     * @param command
     *         the document command describing the change
     */
    void customizeDocumentCommand(Document document, DocumentCommand command);
}
