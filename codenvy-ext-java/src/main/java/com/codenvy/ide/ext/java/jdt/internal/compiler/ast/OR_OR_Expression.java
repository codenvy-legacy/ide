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
import com.codenvy.ide.ext.java.jdt.internal.compiler.codegen.BranchLabel;
import com.codenvy.ide.ext.java.jdt.internal.compiler.flow.FlowContext;
import com.codenvy.ide.ext.java.jdt.internal.compiler.flow.FlowInfo;
import com.codenvy.ide.ext.java.jdt.internal.compiler.impl.Constant;
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.Binding;
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.BlockScope;
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.TypeBinding;
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.TypeIds;

//dedicated treatment for the ||
public class OR_OR_Expression extends BinaryExpression {

    int rightInitStateIndex = -1;

    int mergedInitStateIndex = -1;

    public OR_OR_Expression(Expression left, Expression right, int operator) {
        super(left, right, operator);
    }

    public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {

        Constant cst = this.left.optimizedBooleanConstant();
        boolean isLeftOptimizedTrue = cst != Constant.NotAConstant && cst.booleanValue() == true;
        boolean isLeftOptimizedFalse = cst != Constant.NotAConstant && cst.booleanValue() == false;

        if (isLeftOptimizedFalse) {
            // FALSE || anything
            // need to be careful of scenario:
            //		(x || y) || !z, if passing the left info to the right, it would be swapped by the !
            FlowInfo mergedInfo = this.left.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits();
            mergedInfo = this.right.analyseCode(currentScope, flowContext, mergedInfo);
            this.mergedInitStateIndex = currentScope.methodScope().recordInitializationStates(mergedInfo);
            return mergedInfo;
        }

        FlowInfo leftInfo = this.left.analyseCode(currentScope, flowContext, flowInfo);

        // need to be careful of scenario:
        //		(x || y) || !z, if passing the left info to the right, it would be swapped by the !
        FlowInfo rightInfo = leftInfo.initsWhenFalse().unconditionalCopy();
        this.rightInitStateIndex = currentScope.methodScope().recordInitializationStates(rightInfo);

        int previousMode = rightInfo.reachMode();
        if (isLeftOptimizedTrue) {
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
        // The definitely null variables in right info when true should not be missed out while merging
        // https://bugs.eclipse.org/bugs/show_bug.cgi?id=299900
        FlowInfo leftInfoWhenTrueForMerging =
                leftInfo.initsWhenTrue().unconditionalCopy()
                        .addPotentialInitializationsFrom(rightInfo.unconditionalInitsWithoutSideEffect());
        FlowInfo mergedInfo =
                FlowInfo.conditional(
                        // merging two true initInfos for such a negative case: if ((t && (b = t)) || f) r = b; // b may not have been
                        // initialized
                        leftInfoWhenTrueForMerging.unconditionalInits().mergedWith(
                                rightInfo.safeInitsWhenTrue().setReachMode(previousMode).unconditionalInits()),
                        rightInfo.initsWhenFalse());
        this.mergedInitStateIndex = currentScope.methodScope().recordInitializationStates(mergedInfo);
        return mergedInfo;
    }

    /** Code generation for a binary operation */
    public void generateCode(BlockScope currentScope, boolean valueRequired) {
        if (this.constant != Constant.NotAConstant) {
            // inlined value
            return;
        }
        Constant cst = this.right.constant;
        if (cst != Constant.NotAConstant) {
            // <expr> || true --> true
            if (cst.booleanValue() == true) {
                this.left.generateCode(currentScope, false);
            } else {
                // <expr>|| false --> <expr>
                this.left.generateCode(currentScope, valueRequired);
            }
            return;
        }

        BranchLabel trueLabel = new BranchLabel();
        cst = this.left.optimizedBooleanConstant();
        boolean leftIsConst = cst != Constant.NotAConstant;
        boolean leftIsTrue = leftIsConst && cst.booleanValue() == true;

        cst = this.right.optimizedBooleanConstant();
        boolean rightIsConst = cst != Constant.NotAConstant;

        generateOperands:
        {
            if (leftIsConst) {
                this.left.generateCode(currentScope, false);
                if (leftIsTrue) {
                    break generateOperands; // no need to generate right operand
                }
            } else {
                this.left.generateOptimizedBoolean(currentScope, trueLabel, null, true);
                // need value, e.g. if (a == 1 || ((b = 2) > 0)) {} -> shouldn't initialize 'b' if a==1
            }

            if (rightIsConst) {
                this.right.generateCode(currentScope, false);
            } else {
                this.right.generateOptimizedBoolean(currentScope, trueLabel, null, valueRequired);
            }
        }
    }

    /** Boolean operator code generation Optimized operations are: || */
    public void generateOptimizedBoolean(BlockScope currentScope, BranchLabel trueLabel, BranchLabel falseLabel,
                                         boolean valueRequired) {
        if (this.constant != Constant.NotAConstant) {
            super.generateOptimizedBoolean(currentScope, trueLabel, falseLabel, valueRequired);
            return;
        }

        // <expr> || false --> <expr>
        Constant cst = this.right.constant;
        if (cst != Constant.NotAConstant && cst.booleanValue() == false) {
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
                    this.left.generateOptimizedBoolean(currentScope, trueLabel, null, !leftIsConst);
                    // need value, e.g. if (a == 1 || ((b = 2) > 0)) {} -> shouldn't initialize 'b' if a==1
                    if (leftIsTrue) {
                        break generateOperands; // no need to generate right operand
                    }
                    this.right.generateOptimizedBoolean(currentScope, trueLabel, null, valueRequired && !rightIsConst);
                }
            } else {
                // implicit falling through the TRUE case
                if (trueLabel == null) {
                    BranchLabel internalTrueLabel = new BranchLabel();
                    this.left.generateOptimizedBoolean(currentScope, internalTrueLabel, null, !leftIsConst);
                    // need value, e.g. if (a == 1 || ((b = 2) > 0)) {} -> shouldn't initialize 'b' if a==1
                    if (leftIsTrue) {
                        break generateOperands; // no need to generate right operand
                    }
                    this.right.generateOptimizedBoolean(currentScope, null, falseLabel, valueRequired && !rightIsConst);
                }
            }
        }
    }

    public boolean isCompactableOperation() {
        return false;
    }

    /** @see com.codenvy.ide.ext.java.jdt.internal.compiler.ast.BinaryExpression#resolveType(com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.BlockScope) */
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
