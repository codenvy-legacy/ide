/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package com.codenvy.ide.ext.java.jdt.quickassist.api;

import com.codenvy.ide.ext.java.jdt.codeassistant.api.IProblemLocation;
import com.codenvy.ide.ext.java.jdt.codeassistant.api.JavaCompletionProposal;
import com.codenvy.ide.runtime.CoreException;


/**
 * Interface to be implemented by contributors to the extension point
 * <code>org.eclipse.jdt.ui.quickFixProcessors</code>.
 */
public interface QuickFixProcessor {

    /**
     * Returns <code>true</code> if the processor has proposals for the given problem. This test should be an
     * optimistic guess and be very cheap.
     *
     * @param unit
     *         the compilation unit
     * @param problemId
     *         the problem Id. The id is of a problem of the problem type(s) this processor specified in
     *         the extension point.
     * @return <code>true</code> if the processor has proposals for the given problem
     */
    boolean hasCorrections(int problemId);

    /**
     * Collects corrections or code manipulations for the given context.
     *
     * @param context
     *         Defines current compilation unit, position and a shared AST
     * @param locations
     *         Problems are the current location.
     * @return the corrections applicable at the location or <code>null</code> if no proposals
     *         can be offered
     * @throws CoreException
     *         CoreException can be thrown if the operation fails
     */
    JavaCompletionProposal[] getCorrections(InvocationContext context, IProblemLocation[] locations)
            throws CoreException;

}
