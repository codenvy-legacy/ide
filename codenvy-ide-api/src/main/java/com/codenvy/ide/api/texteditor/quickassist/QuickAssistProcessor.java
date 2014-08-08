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
package com.codenvy.ide.api.texteditor.quickassist;

import com.codenvy.ide.api.texteditor.CodeAssistCallback;

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
