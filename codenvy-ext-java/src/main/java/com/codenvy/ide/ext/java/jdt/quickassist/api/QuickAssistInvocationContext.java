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
 * Context information for quick fix and quick assist processors.
 * <p>
 * This interface can be implemented by clients.</p>
 */
public interface QuickAssistInvocationContext {

    /**
     * Returns the offset where quick assist was invoked.
     *
     * @return the invocation offset or <code>-1</code> if unknown
     */
    int getOffset();

    /**
     * Returns the length of the selection at the invocation offset.
     *
     * @return the length of the current selection or <code>-1</code> if none or unknown
     */
    int getLength();
}
