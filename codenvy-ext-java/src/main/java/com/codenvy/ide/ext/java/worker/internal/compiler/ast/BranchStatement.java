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

import com.codenvy.ide.ext.java.worker.internal.compiler.codegen.BranchLabel;
import com.codenvy.ide.ext.java.worker.internal.compiler.lookup.BlockScope;

public abstract class BranchStatement extends Statement {

    public char[] label;

    public BranchLabel targetLabel;

    public SubRoutineStatement[] subroutines;

    public int initStateIndex = -1;

    /** BranchStatement constructor comment. */
    public BranchStatement(char[] label, int sourceStart, int sourceEnd) {
        this.label = label;
        this.sourceStart = sourceStart;
        this.sourceEnd = sourceEnd;
    }

    /**
     * Branch code generation
     * <p/>
     * generate the finallyInvocationSequence.
     */
    public void generateCode(BlockScope currentScope) {

    }

    public void resolve(BlockScope scope) {
        // nothing to do during name resolution
    }
}
