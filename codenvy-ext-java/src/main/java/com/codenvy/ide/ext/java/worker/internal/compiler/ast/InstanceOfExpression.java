/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
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
import com.codenvy.ide.ext.java.worker.internal.compiler.flow.FlowContext;
import com.codenvy.ide.ext.java.worker.internal.compiler.flow.FlowInfo;
import com.codenvy.ide.ext.java.worker.internal.compiler.impl.Constant;
import com.codenvy.ide.ext.java.worker.internal.compiler.lookup.*;

public class InstanceOfExpression extends OperatorExpression {

    public Expression expression;

    public TypeReference type;

    public InstanceOfExpression(Expression expression, TypeReference type) {
        this.expression = expression;
        this.type = type;
        type.bits |= IgnoreRawTypeCheck; // https://bugs.eclipse.org/bugs/show_bug.cgi?id=282141
        this.bits |= INSTANCEOF << OperatorSHIFT;
        this.sourceStart = expression.sourceStart;
        this.sourceEnd = type.sourceEnd;
    }

    @Override
    public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
        LocalVariableBinding local = this.expression.localVariableBinding();
        if (local != null && (local.type.tagBits & TagBits.IsBaseType) == 0) {
            flowInfo = this.expression.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits();
            FlowInfo initsWhenTrue = flowInfo.copy();
            initsWhenTrue.markAsComparedEqualToNonNull(local);
            if ((flowContext.tagBits & FlowContext.HIDE_NULL_COMPARISON_WARNING) != 0) {
                initsWhenTrue.markedAsNullOrNonNullInAssertExpression(local);
            }
            flowContext.recordUsingNullReference(currentScope, local, this.expression, FlowContext.CAN_ONLY_NULL
                                                                                       | FlowContext.IN_INSTANCEOF, flowInfo);
            // no impact upon enclosing try context
            return FlowInfo.conditional(initsWhenTrue, flowInfo.copy());
        }
        return this.expression.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits();
    }

    /**
     * Code generation for instanceOfExpression
     *
     * @param currentScope
     *         com.codenvy.ide.java.client.internal.compiler.lookup.BlockScope
     * @param codeStream
     *         com.codenvy.ide.java.client.internal.compiler.codegen.CodeStream
     * @param valueRequired
     *         boolean
     */
    @Override
    public void generateCode(BlockScope currentScope, boolean valueRequired) {
        this.expression.generateCode(currentScope, true);
    }

    @Override
    public StringBuffer printExpressionNoParenthesis(int indent, StringBuffer output) {
        this.expression.printExpression(indent, output).append(" instanceof "); //$NON-NLS-1$
        return this.type.print(0, output);
    }

    @Override
    public TypeBinding resolveType(BlockScope scope) {
        this.constant = Constant.NotAConstant;
        TypeBinding expressionType = this.expression.resolveType(scope);
        TypeBinding checkedType = this.type.resolveType(scope, true /* check bounds*/);
        if (expressionType == null || checkedType == null) {
            return null;
        }

        if (!checkedType.isReifiable()) {
            scope.problemReporter().illegalInstanceOfGenericType(checkedType, this);
        } else if ((expressionType != TypeBinding.NULL && expressionType.isBaseType()) // disallow autoboxing
                   || !checkCastTypesCompatibility(scope, checkedType, expressionType, null)) {
            scope.problemReporter().notCompatibleTypesError(this, expressionType, checkedType);
        }
        return this.resolvedType = TypeBinding.BOOLEAN;
    }

    /** @see com.codenvy.ide.ext.java.worker.internal.compiler.ast.Expression#tagAsUnnecessaryCast(Scope, TypeBinding) */
    @Override
    public void tagAsUnnecessaryCast(Scope scope, TypeBinding castType) {
        // null is not instanceof Type, recognize direct scenario
        if (this.expression.resolvedType != TypeBinding.NULL) {
            scope.problemReporter().unnecessaryInstanceof(this, castType);
        }
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope scope) {
        if (visitor.visit(this, scope)) {
            this.expression.traverse(visitor, scope);
            this.type.traverse(visitor, scope);
        }
        visitor.endVisit(this, scope);
    }
}
