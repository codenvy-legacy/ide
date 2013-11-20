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

import com.codenvy.ide.ext.java.worker.internal.compiler.ASTVisitor;
import com.codenvy.ide.ext.java.worker.internal.compiler.ClassFileConstants;
import com.codenvy.ide.ext.java.worker.internal.compiler.flow.FlowContext;
import com.codenvy.ide.ext.java.worker.internal.compiler.flow.FlowInfo;
import com.codenvy.ide.ext.java.worker.internal.compiler.lookup.BlockScope;

public class EmptyStatement extends Statement {

    public EmptyStatement(int startPosition, int endPosition) {
        this.sourceStart = startPosition;
        this.sourceEnd = endPosition;
    }

    public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
        return flowInfo;
    }

    // Report an error if necessary
    public int complainIfUnreachable(FlowInfo flowInfo, BlockScope scope, int complaintLevel) {
        // before 1.4, empty statements are tolerated anywhere
        if (scope.compilerOptions().complianceLevel < ClassFileConstants.JDK1_4) {
            return complaintLevel;
        }
        return super.complainIfUnreachable(flowInfo, scope, complaintLevel);
    }

    public void generateCode(BlockScope currentScope) {
        // no bytecode, no need to check for reachability or recording source positions
    }

    public StringBuffer printStatement(int tab, StringBuffer output) {
        return printIndent(tab, output).append(';');
    }

    public void resolve(BlockScope scope) {
        if ((this.bits & IsUsefulEmptyStatement) == 0) {
            scope.problemReporter().superfluousSemicolon(this.sourceStart, this.sourceEnd);
        } else {
            scope.problemReporter().emptyControlFlowStatement(this.sourceStart, this.sourceEnd);
        }
    }

    public void traverse(ASTVisitor visitor, BlockScope scope) {
        visitor.visit(this, scope);
        visitor.endVisit(this, scope);
    }

}
