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
