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
package com.codenvy.ide.text;

/**
 * Factory interface to receive new instance of the {@link Document}
 *
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public interface DocumentFactory {
    /**
     * Create new the empty Document.
     *
     * @return the new Document.
     */
    Document get();

    /**
     * Create new Document with initial content.
     *
     * @param initialContent
     *         the initial content of the new Document.
     * @return the new Document.
     */
    Document get(String initialContent);
}
