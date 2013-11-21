/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.java.worker.internal.compiler.ast;

import com.codenvy.ide.ext.java.worker.internal.compiler.lookup.BlockScope;
import com.codenvy.ide.ext.java.worker.internal.compiler.lookup.LocalVariableBinding;

/** Extra behavior for statements which are generating subroutines */
public abstract class SubRoutineStatement extends Statement {

    /**
     * Generate an invocation of a subroutine (e.g. jsr finally) in current context.
     *
     * @param currentScope
     * @param codeStream
     * @param targetLocation
     * @param stateIndex
     * @param secretLocal
     * @return boolean, <code>true</code> if the generated code will abrupt completion
     */
    public abstract boolean generateSubRoutineInvocation(BlockScope currentScope, Object targetLocation, int stateIndex,
                                                         LocalVariableBinding secretLocal);

    public abstract boolean isSubRoutineEscaping();
}
