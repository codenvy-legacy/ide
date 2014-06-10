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
