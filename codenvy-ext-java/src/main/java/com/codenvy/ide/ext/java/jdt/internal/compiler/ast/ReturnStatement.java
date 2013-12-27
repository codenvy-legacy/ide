/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Stephan Herrmann - Contribution for bug 319201 - [null] no warning when unboxing SingleNameReference causes NPE
 *******************************************************************************/
package com.codenvy.ide.ext.java.jdt.internal.compiler.ast;

import com.codenvy.ide.ext.java.jdt.internal.compiler.ASTVisitor;
import com.codenvy.ide.ext.java.jdt.internal.compiler.flow.FlowContext;
import com.codenvy.ide.ext.java.jdt.internal.compiler.flow.FlowInfo;
import com.codenvy.ide.ext.java.jdt.internal.compiler.flow.InitializationFlowContext;
import com.codenvy.ide.ext.java.jdt.internal.compiler.flow.InsideSubRoutineFlowContext;
import com.codenvy.ide.ext.java.jdt.internal.compiler.impl.Constant;
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.BlockScope;
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.LocalVariableBinding;
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.MethodBinding;
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.MethodScope;
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.TagBits;
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.TypeBinding;
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.TypeIds;

public class ReturnStatement extends Statement {

    public Expression expression;

    public SubRoutineStatement[] subroutines;

    public LocalVariableBinding saveValueVariable;

    public int initStateIndex = -1;

    public ReturnStatement(Expression expression, int sourceStart, int sourceEnd) {
        this.sourceStart = sourceStart;
        this.sourceEnd = sourceEnd;
        this.expression = expression;
    }

    @Override
    public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext,
                                FlowInfo flowInfo) { // here requires to generate a sequence of finally blocks invocations depending
                                // corresponding
        // to each of the traversed try statements, so that execution will terminate properly.

        // lookup the label, this should answer the returnContext

        if (this.expression != null) {
            flowInfo = this.expression.analyseCode(currentScope, flowContext, flowInfo);
            if ((this.expression.implicitConversion & TypeIds.UNBOXING) != 0) {
                this.expression.checkNPE(currentScope, flowContext, flowInfo);
            }
        }
        this.initStateIndex = currentScope.methodScope().recordInitializationStates(flowInfo);
        // compute the return sequence (running the finally blocks)
        FlowContext traversedContext = flowContext;
        int subCount = 0;
        boolean saveValueNeeded = false;
        boolean hasValueToSave = needValueStore();
        do {
            SubRoutineStatement sub;
            if ((sub = traversedContext.subroutine()) != null) {
                if (this.subroutines == null) {
                    this.subroutines = new SubRoutineStatement[5];
                }
                if (subCount == this.subroutines.length) {
                    System.arraycopy(this.subroutines, 0, (this.subroutines = new SubRoutineStatement[subCount * 2]), 0,
                                     subCount); // grow
                }
                this.subroutines[subCount++] = sub;
                if (sub.isSubRoutineEscaping()) {
                    saveValueNeeded = false;
                    this.bits |= ASTNode.IsAnySubRoutineEscaping;
                    break;
                }
            }
            traversedContext.recordReturnFrom(flowInfo.unconditionalInits());

            if (traversedContext instanceof InsideSubRoutineFlowContext) {
                ASTNode node = traversedContext.associatedNode;
                if (node instanceof SynchronizedStatement) {
                    this.bits |= ASTNode.IsSynchronized;
                } else if (node instanceof TryStatement) {
                    TryStatement tryStatement = (TryStatement)node;
                    flowInfo.addInitializationsFrom(tryStatement.subRoutineInits); // collect inits
                    if (hasValueToSave) {
                        if (this.saveValueVariable == null) { // closest subroutine secret variable is used
                            prepareSaveValueLocation(tryStatement);
                        }
                        saveValueNeeded = true;
                        this.initStateIndex = currentScope.methodScope().recordInitializationStates(flowInfo);
                    }
                }
            } else if (traversedContext instanceof InitializationFlowContext) {
                currentScope.problemReporter().cannotReturnInInitializer(this);
                return FlowInfo.DEAD_END;
            }
        }
        while ((traversedContext = traversedContext.parent) != null);

        // resize subroutines
        if ((this.subroutines != null) && (subCount != this.subroutines.length)) {
            System.arraycopy(this.subroutines, 0, (this.subroutines = new SubRoutineStatement[subCount]), 0, subCount);
        }

        // secret local variable for return value (note that this can only occur in a real method)
        if (saveValueNeeded) {
            if (this.saveValueVariable != null) {
                this.saveValueVariable.useFlag = LocalVariableBinding.USED;
            }
        } else {
            this.saveValueVariable = null;
            if (((this.bits & ASTNode.IsSynchronized) == 0) && this.expression != null
                && this.expression.resolvedType == TypeBinding.BOOLEAN) {
                this.expression.bits |= ASTNode.IsReturnedValue;
            }
        }
        return FlowInfo.DEAD_END;
    }

    /**
     * Retrun statement code generation
     * <p/>
     * generate the finallyInvocationSequence.
     *
     * @param currentScope
     *         com.codenvy.ide.java.client.internal.compiler.lookup.BlockScope
     */
    @Override
    public void generateCode(BlockScope currentScope) {
        if ((this.bits & ASTNode.IsReachable) == 0) {
            return;
        }
        // generate the expression
        if (needValueStore()) {
            this.expression.generateCode(currentScope, needValue()); // no value needed if non-returning subroutine
        }

    }

    private boolean needValueStore() {
        return this.expression != null
               && (this.expression.constant == Constant.NotAConstant || (this.expression.implicitConversion & TypeIds.BOXING) != 0)
               && !(this.expression instanceof NullLiteral);
    }

    public boolean needValue() {
        return this.saveValueVariable != null || (this.bits & ASTNode.IsSynchronized) != 0
               || ((this.bits & ASTNode.IsAnySubRoutineEscaping) == 0);
    }

    public void prepareSaveValueLocation(TryStatement targetTryStatement) {
        this.saveValueVariable = targetTryStatement.secretReturnValue;
    }

    @Override
    public StringBuffer printStatement(int tab, StringBuffer output) {
        printIndent(tab, output).append("return "); //$NON-NLS-1$
        if (this.expression != null) {
            this.expression.printExpression(0, output);
        }
        return output.append(';');
    }

    @Override
    public void resolve(BlockScope scope) {
        MethodScope methodScope = scope.methodScope();
        MethodBinding methodBinding;
        TypeBinding methodType =
                (methodScope.referenceContext instanceof AbstractMethodDeclaration) ? ((methodBinding =
                        ((AbstractMethodDeclaration)methodScope.referenceContext).binding) == null ? null
                                                                                                   : methodBinding.returnType)
                                                                                    : TypeBinding.VOID;
        TypeBinding expressionType;
        if (methodType == TypeBinding.VOID) {
            // the expression should be null
            if (this.expression == null) {
                return;
            }
            if ((expressionType = this.expression.resolveType(scope)) != null) {
                scope.problemReporter().attemptToReturnNonVoidExpression(this, expressionType);
            }
            return;
        }
        if (this.expression == null) {
            if (methodType != null) {
                scope.problemReporter().shouldReturn(methodType, this);
            }
            return;
        }
        this.expression.setExpectedType(methodType); // needed in case of generic method invocation
        if ((expressionType = this.expression.resolveType(scope)) == null) {
            return;
        }
        if (expressionType == TypeBinding.VOID) {
            scope.problemReporter().attemptToReturnVoidValue(this);
            return;
        }
        if (methodType == null) {
            return;
        }

        if (methodType != expressionType) {
            scope.compilationUnitScope().recordTypeConversion(methodType, expressionType);
        }
        if (this.expression.isConstantValueOfTypeAssignableToType(expressionType, methodType)
            || expressionType.isCompatibleWith(methodType)) {

            this.expression.computeConversion(scope, methodType, expressionType);
            if (expressionType.needsUncheckedConversion(methodType)) {
                scope.problemReporter().unsafeTypeConversion(this.expression, expressionType, methodType);
            }
            if (this.expression instanceof CastExpression
                && (this.expression.bits & (ASTNode.UnnecessaryCast | ASTNode.DisableUnnecessaryCastCheck)) == 0) {
                CastExpression.checkNeedForAssignedCast(scope, methodType, (CastExpression)this.expression);
            }
            return;
        } else if (isBoxingCompatible(expressionType, methodType, this.expression, scope)) {
            this.expression.computeConversion(scope, methodType, expressionType);
            if (this.expression instanceof CastExpression
                && (this.expression.bits & (ASTNode.UnnecessaryCast | ASTNode.DisableUnnecessaryCastCheck)) == 0) {
                CastExpression.checkNeedForAssignedCast(scope, methodType, (CastExpression)this.expression);
            }
            return;
        }
        if ((methodType.tagBits & TagBits.HasMissingType) == 0) {
            // no need to complain if return type was missing (avoid secondary error : 220967)
            scope.problemReporter().typeMismatchError(expressionType, methodType, this.expression, null);
        }
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope scope) {
        if (visitor.visit(this, scope)) {
            if (this.expression != null) {
                this.expression.traverse(visitor, scope);
            }
        }
        visitor.endVisit(this, scope);
    }
}
