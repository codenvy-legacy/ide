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
package com.codenvy.ide.ext.java.jdt.internal.compiler.ast;

import com.codenvy.ide.ext.java.jdt.internal.compiler.ASTVisitor;
import com.codenvy.ide.ext.java.jdt.internal.compiler.ClassFileConstants;
import com.codenvy.ide.ext.java.jdt.internal.compiler.flow.FlowContext;
import com.codenvy.ide.ext.java.jdt.internal.compiler.flow.FlowInfo;
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.BlockScope;
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.TypeBinding;
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.TypeIds;

public class ThrowStatement extends Statement {

    public Expression exception;

    public TypeBinding exceptionType;

    public ThrowStatement(Expression exception, int sourceStart, int sourceEnd) {
        this.exception = exception;
        this.sourceStart = sourceStart;
        this.sourceEnd = sourceEnd;
    }

    @Override
    public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
        this.exception.analyseCode(currentScope, flowContext, flowInfo);
        this.exception.checkNPE(currentScope, flowContext, flowInfo);
        // need to check that exception thrown is actually caught somewhere
        flowContext.checkExceptionHandlers(this.exceptionType, this, flowInfo, currentScope);
        return FlowInfo.DEAD_END;
    }

    /**
     * Throw code generation
     *
     * @param currentScope
     *         com.codenvy.ide.java.client.internal.compiler.lookup.BlockScope
     * @param codeStream
     *         com.codenvy.ide.java.client.internal.compiler.codegen.CodeStream
     */
    @Override
    public void generateCode(BlockScope currentScope) {
        if ((this.bits & ASTNode.IsReachable) == 0) {
            return;
        }
        this.exception.generateCode(currentScope, true);
    }

    @Override
    public StringBuffer printStatement(int indent, StringBuffer output) {
        printIndent(indent, output).append("throw "); //$NON-NLS-1$
        this.exception.printExpression(0, output);
        return output.append(';');
    }

    @Override
    public void resolve(BlockScope scope) {
        this.exceptionType = this.exception.resolveType(scope);
        if (this.exceptionType != null && this.exceptionType.isValidBinding()) {
            if (this.exceptionType == TypeBinding.NULL) {
                if (scope.compilerOptions().complianceLevel <= ClassFileConstants.JDK1_3) {
                    // if compliant with 1.4, this problem will not be reported
                    scope.problemReporter().cannotThrowNull(this.exception);
                }
            } else if (this.exceptionType.findSuperTypeOriginatingFrom(TypeIds.T_JavaLangThrowable, true) == null) {
                scope.problemReporter().cannotThrowType(this.exception, this.exceptionType);
            }
            this.exception.computeConversion(scope, this.exceptionType, this.exceptionType);
        }
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope blockScope) {
        if (visitor.visit(this, blockScope)) {
            this.exception.traverse(visitor, blockScope);
        }
        visitor.endVisit(this, blockScope);
    }
}
