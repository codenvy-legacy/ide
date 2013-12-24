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
package com.codenvy.ide.texteditor.api.quickassist;

import com.codenvy.ide.texteditor.api.CodeAssistCallback;

/**
 * Quick assist processor for quick fixes and quick assists.
 * <p>
 * A processor can provide just quick fixes, just quick assists
 * or both.
 * </p>
 * <p>
 * This interface can be implemented by clients.</p>
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public interface QuickAssistProcessor {

    /**
     * Returns a list of quick assist and quick fix proposals for the
     * given invocation context.
     *
     * @param invocationContext
     *         the invocation context
     * @return an array of completion proposals or <code>null</code> if no proposals are available
     */
    void computeQuickAssistProposals(QuickAssistInvocationContext invocationContext, CodeAssistCallback callback);

    /**
     * Returns the reason why this quick assist processor
     * was unable to produce any completion proposals.
     *
     * @return an error message or <code>null</code> if no error occurred
     */
    String getErrorMessage();

//    /**
//     * Tells whether this processor has a fix for the given annotation.
//     * <p>
//     * <strong>Note:</strong> This test must be fast and optimistic i.e. it is OK to return
//     * <code>true</code> even though there might be no quick fix.
//     * </p>
//     *
//     * @param annotation
//     *         the annotation
//     * @return <code>true</code> if the assistant has a fix for the given annotation
//     */
//    boolean canFix(Annotation annotation);

//    /**
//     * Tells whether this assistant has assists for the given invocation context.
//     *
//     * @param invocationContext
//     *         the invocation context
//     * @return <code>true</code> if the assistant has a fix for the given annotation
//     */
//    boolean canAssist(QuickAssistInvocationContext invocationContext);

}
