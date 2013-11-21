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
package com.codenvy.ide.ext.java.worker.internal.compiler.ast;

import com.codenvy.ide.ext.java.worker.internal.compiler.ASTVisitor;
import com.codenvy.ide.ext.java.worker.internal.compiler.codegen.BranchLabel;
import com.codenvy.ide.ext.java.worker.internal.compiler.flow.FlowContext;
import com.codenvy.ide.ext.java.worker.internal.compiler.flow.FlowInfo;
import com.codenvy.ide.ext.java.worker.internal.compiler.impl.Constant;
import com.codenvy.ide.ext.java.worker.internal.compiler.lookup.Binding;
import com.codenvy.ide.ext.java.worker.internal.compiler.lookup.BlockScope;
import com.codenvy.ide.ext.java.worker.internal.compiler.lookup.TypeBinding;
import com.codenvy.ide.ext.java.worker.internal.compiler.lookup.TypeIds;

//dedicated treatment for the &&
public class AND_AND_Expression extends BinaryExpression {

    int rightInitStateIndex = -1;

    int mergedInitStateIndex = -1;

    public AND_AND_Expression(Expression left, Expression right, int operator) {
        super(left, right, operator);
    }

    public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {

        Constant cst = this.left.optimizedBooleanConstant();
        boolean isLeftOptimizedTrue = cst != Constant.NotAConstant && cst.booleanValue() == true;
        boolean isLeftOptimizedFalse = cst != Constant.NotAConstant && cst.booleanValue() == false;

        if (isLeftOptimizedTrue) {
            // TRUE && anything
            // need to be careful of scenario:
            //  (x && y) && !z, if passing the left info to the right, it would
            // be swapped by the !
            FlowInfo mergedInfo = this.left.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits();
            mergedInfo = this.right.analyseCode(currentScope, flowContext, mergedInfo);
            this.mergedInitStateIndex = currentScope.methodScope().recordInitializationStates(mergedInfo);
            return mergedInfo;
        }

        FlowInfo leftInfo = this.left.analyseCode(currentScope, flowContext, flowInfo);
        // need to be careful of scenario:
        //  (x && y) && !z, if passing the left info to the right, it would be
        // swapped by the !
        FlowInfo rightInfo = leftInfo.initsWhenTrue().unconditionalCopy();
        this.rightInitStateIndex = currentScope.methodScope().recordInitializationStates(rightInfo);

        int previousMode = rightInfo.reachMode();
        if (isLeftOptimizedFalse) {
            if ((rightInfo.reachMode() & FlowInfo.UNREACHABLE) == 0) {
                currentScope.problemReporter().fakeReachable(this.right);
                rightInfo.setReachMode(FlowInfo.UNREACHABLE_OR_DEAD);
            }
        }
        rightInfo = this.right.analyseCode(currentScope, flowContext, rightInfo);
        if ((this.left.implicitConversion & TypeIds.UNBOXING) != 0) {
            this.left.checkNPE(currentScope, flowContext, flowInfo);
        }
        if ((this.right.implicitConversion & TypeIds.UNBOXING) != 0) {
            this.right.checkNPE(currentScope, flowContext, flowInfo);
        }
        FlowInfo mergedInfo =
                FlowInfo.conditional(
                        rightInfo.safeInitsWhenTrue(),
                        leftInfo.initsWhenFalse().unconditionalInits()
                                .mergedWith(rightInfo.initsWhenFalse().setReachMode(previousMode).unconditionalInits()));
        // reset after trueMergedInfo got extracted
        this.mergedInitStateIndex = currentScope.methodScope().recordInitializationStates(mergedInfo);
        return mergedInfo;
    }

    /** Code generation for a binary operation */
    public void generateCode(BlockScope currentScope, boolean valueRequired) {

        if (this.constant != Constant.NotAConstant) {
            return;
        }
        Constant cst = this.right.constant;
        if (cst != Constant.NotAConstant) {
            // <expr> && true --> <expr>
            if (cst.booleanValue() == true) {
                this.left.generateCode(currentScope, valueRequired);
            } else {
                // <expr> && false --> false
                this.left.generateCode(currentScope, false);
            }
            return;
        }

        BranchLabel falseLabel = new BranchLabel();
        cst = this.left.optimizedBooleanConstant();
        boolean leftIsConst = cst != Constant.NotAConstant;
        boolean leftIsTrue = leftIsConst && cst.booleanValue() == true;

        cst = this.right.optimizedBooleanConstant();
        boolean rightIsConst = cst != Constant.NotAConstant;

        generateOperands:
        {
            if (leftIsConst) {
                this.left.generateCode(currentScope, false);
                if (!leftIsTrue) {
                    break generateOperands; // no need to generate right operand
                }
            } else {
                this.left.generateOptimizedBoolean(currentScope, null, falseLabel, true);
                // need value, e.g. if (a == 1 && ((b = 2) > 0)) {} -> shouldn't initialize 'b' if a!=1
            }
            if (rightIsConst) {
                this.right.generateCode(currentScope, false);
            } else {
                this.right.generateOptimizedBoolean(currentScope, null, falseLabel, valueRequired);
            }
        }
    }

    /** Boolean operator code generation Optimized operations are: && */
    public void generateOptimizedBoolean(BlockScope currentScope, BranchLabel trueLabel, BranchLabel falseLabel,
                                         boolean valueRequired) {

        if (this.constant != Constant.NotAConstant) {
            super.generateOptimizedBoolean(currentScope, trueLabel, falseLabel, valueRequired);
            return;
        }

        // <expr> && true --> <expr>
        Constant cst = this.right.constant;
        if (cst != Constant.NotAConstant && cst.booleanValue() == true) {
            this.left.generateOptimizedBoolean(currentScope, trueLabel, falseLabel, valueRequired);
            return;
        }
        cst = this.left.optimizedBooleanConstant();
        boolean leftIsConst = cst != Constant.NotAConstant;
        boolean leftIsTrue = leftIsConst && cst.booleanValue() == true;

        cst = this.right.optimizedBooleanConstant();
        boolean rightIsConst = cst != Constant.NotAConstant;

        // default case
        generateOperands:
        {
            if (falseLabel == null) {
                if (trueLabel != null) {
                    // implicit falling through the FALSE case
                    BranchLabel internalFalseLabel = new BranchLabel();
                    this.left.generateOptimizedBoolean(currentScope, null, internalFalseLabel, !leftIsConst);
                    // need value, e.g. if (a == 1 && ((b = 2) > 0)) {} -> shouldn't initialize 'b' if a!=1
                    if (leftIsConst && !leftIsTrue) {
                        break generateOperands; // no need to generate right operand
                    }
                    this.right.generateOptimizedBoolean(currentScope, trueLabel, null, valueRequired && !rightIsConst);
                }
            } else {
                // implicit falling through the TRUE case
                if (trueLabel == null) {
                    this.left.generateOptimizedBoolean(currentScope, null, falseLabel, !leftIsConst);
                    // need value, e.g. if (a == 1 && ((b = 2) > 0)) {} -> shouldn't initialize 'b' if a!=1
                    if (leftIsConst && !leftIsTrue) {
                        break generateOperands; // no need to generate right operand
                    }
                    if (this.rightInitStateIndex != -1) {
                    }
                    this.right.generateOptimizedBoolean(currentScope, null, falseLabel, valueRequired && !rightIsConst);
                } else {
                    // no implicit fall through TRUE/FALSE --> should never occur
                }
            }
        }
    }

    public boolean isCompactableOperation() {
        return false;
    }

    /** @see com.codenvy.ide.ext.java.worker.internal.compiler.ast.BinaryExpression#resolveType(com.codenvy.ide.ext.java.worker.internal.compiler.lookup.BlockScope) */
    public TypeBinding resolveType(BlockScope scope) {
        TypeBinding result = super.resolveType(scope);
        // check whether comparing identical expressions
        Binding leftDirect = Expression.getDirectBinding(this.left);
        if (leftDirect != null && leftDirect == Expression.getDirectBinding(this.right)) {
            if (!(this.right instanceof Assignment))
                scope.problemReporter().comparingIdenticalExpressions(this);
        }
        return result;
    }

    public void traverse(ASTVisitor visitor, BlockScope scope) {
        if (visitor.visit(this, scope)) {
            this.left.traverse(visitor, scope);
            this.right.traverse(visitor, scope);
        }
        visitor.endVisit(this, scope);
    }
}
