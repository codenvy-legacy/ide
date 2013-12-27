/*******************************************************************************
 * Copyright (c) 2006, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.java.jdt.quickassist.api;

import com.codenvy.ide.ext.java.jdt.codeassistant.api.IProblemLocation;
import com.codenvy.ide.ext.java.jdt.codeassistant.api.JavaCompletionProposal;
import com.codenvy.ide.runtime.CoreException;


/**
 * Interface to be implemented by contributors to the extension point
 * <code>org.eclipse.jdt.ui.quickAssistProcessors</code>.
 */
public interface QuickAssistProcessor {

    /**
     * Evaluates if quick assists can be created for the given context. This evaluation must be precise.
     *
     * @param context
     *         The invocation context
     * @return Returns <code>true</code> if quick assists can be created
     * @throws CoreException
     *         CoreException can be thrown if the operation fails
     */
    boolean hasAssists(InvocationContext context) throws CoreException;

    /**
     * Collects quick assists for the given context.
     *
     * @param context
     *         Defines current compilation unit, position and a shared AST
     * @param locations
     *         The locations of problems at the invocation offset. The processor can decide to only
     *         add assists when there are no errors at the selection offset.
     * @return Returns the assists applicable at the location or <code>null</code> if no proposals
     *         can be offered.
     * @throws CoreException
     *         CoreException can be thrown if the operation fails
     */
    JavaCompletionProposal[] getAssists(InvocationContext context, IProblemLocation[] locations) throws CoreException;

}
