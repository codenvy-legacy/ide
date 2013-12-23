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
package com.codenvy.ide.ext.java.jdt.quickassist.api;

/**
 * Text quick assist invocation context.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class TextInvocationContext implements QuickAssistInvocationContext {

    private int offset;

    private int length;

    /**
     * @param offset
     * @param length
     */
    public TextInvocationContext(int offset, int length) {
        super();
        this.offset = offset;
        this.length = length;
    }

    /** {@inheritDoc} */
    @Override
    public int getOffset() {
        return offset;
    }

    /** {@inheritDoc} */
    @Override
    public int getLength() {
        return length;
    }
}
