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
 * Default implementation of the DocumentFactory.
 * Use {@link DocumentImpl} as implementation of Document interface
 *
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class DocumentFactoryImpl implements DocumentFactory {
    /** {@inheritDoc} */
    @Override
    public Document get() {
        return new DocumentImpl();
    }

    /** {@inheritDoc} */
    @Override
    public Document get(String initialContent) {
        return new DocumentImpl(initialContent);
    }
}
