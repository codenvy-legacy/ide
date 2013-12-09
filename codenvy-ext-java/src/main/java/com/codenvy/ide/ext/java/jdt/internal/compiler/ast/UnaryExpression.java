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
import com.codenvy.ide.ext.java.jdt.internal.compiler.codegen.BranchLabel;
import com.codenvy.ide.ext.java.jdt.internal.compiler.flow.FlowContext;
import com.codenvy.ide.ext.java.jdt.internal.compiler.flow.FlowInfo;
import com.codenvy.ide.ext.java.jdt.internal.compiler.impl.BooleanConstant;
import com.codenvy.ide.ext.java.jdt.internal.compiler.impl.Constant;
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.BlockScope;
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.TypeBinding;

public class UnaryExpression extends OperatorExpression {

    public Expression expression;

    public Constant optimizedBooleanConstant;

    public UnaryExpression(Expression expression, int operator) {
        this.expression = expression;
        this.bits |= operator << OperatorSHIFT; // encode operator
    }

    @Override
    public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
        this.expression.checkNPE(currentScope, flowContext, flowInfo);
        if (((this.bits & OperatorMASK) >> OperatorSHIFT) == NOT) {
            return this.expression.analyseCode(currentScope, flowContext, flowInfo).asNegatedCondition();
        } else {
            return this.expression.analyseCode(currentScope, flowContext, flowInfo);
        }
    }

    @Override
    public Constant optimizedBooleanConstant() {

        return this.optimizedBooleanConstant == null ? this.constant : this.optimizedBooleanConstant;
    }

    /**
     * Code generation for an unary operation
     *
     * @param currentScope
     *         com.codenvy.ide.java.client.internal.compiler.lookup.BlockScope
     * @param valueRequired
     *         boolean
     */
    @Override
    public void generateCode(BlockScope currentScope, boolean valueRequired) {

        BranchLabel falseLabel, endifLabel;
        if (this.constant != Constant.NotAConstant) {
            return;
        }
        switch ((this.bits & OperatorMASK) >> OperatorSHIFT) {
            case NOT:
                switch ((this.expression.implicitConversion & IMPLICIT_CONVERSION_MASK) >> 4)
            /* runtime type */ {
                    case T_boolean:
                        // ! <boolean>
                        // Generate code for the condition
                        this.expression.generateOptimizedBoolean(currentScope, null, new BranchLabel(), valueRequired);
                        break;
                }
                break;
            case TWIDDLE:
                switch ((this.expression.implicitConversion & IMPLICIT_CONVERSION_MASK) >> 4 /* runtime */) {
                    case T_int:
                        // ~int
                        this.expression.generateCode(currentScope, valueRequired);
                        break;
                    case T_long:
                        this.expression.generateCode(currentScope, valueRequired);
                }
                break;
            case MINUS:
                // - <num>
                if (this.constant != Constant.NotAConstant) {
                } else {
                    this.expression.generateCode(currentScope, valueRequired);
                }
                break;
            case PLUS:
                this.expression.generateCode(currentScope, valueRequired);
        }
    }

    /**
     * Boolean operator code generation
     * Optimized operations are: &&, ||, <, <=, >, >=, &, |, ^
     */
    @Override
    public void generateOptimizedBoolean(BlockScope currentScope, BranchLabel trueLabel, BranchLabel falseLabel,
                                         boolean valueRequired) {

        if ((this.constant != Constant.NotAConstant) && (this.constant.typeID() == T_boolean)) {
            super.generateOptimizedBoolean(currentScope, trueLabel, falseLabel, valueRequired);
            return;
        }
        if (((this.bits & OperatorMASK) >> OperatorSHIFT) == NOT) {
            this.expression.generateOptimizedBoolean(currentScope, falseLabel, trueLabel, valueRequired);
        } else {
            super.generateOptimizedBoolean(currentScope, trueLabel, falseLabel, valueRequired);
        }
    }

    @Override
    public StringBuffer printExpressionNoParenthesis(int indent, StringBuffer output) {

        output.append(operatorToString()).append(' ');
        return this.expression.printExpression(0, output);
    }

    @Override
    public TypeBinding resolveType(BlockScope scope) {
        boolean expressionIsCast;
        if ((expressionIsCast = this.expression instanceof CastExpression) == true) {
            this.expression.bits |= DisableUnnecessaryCastCheck; // will check later on
        }
        TypeBinding expressionType = this.expression.resolveType(scope);
        if (expressionType == null) {
            this.constant = Constant.NotAConstant;
            return null;
        }
        int expressionTypeID = expressionType.id;
        // autoboxing support
        boolean use15specifics = scope.compilerOptions().sourceLevel >= ClassFileConstants.JDK1_5;
        if (use15specifics) {
            if (!expressionType.isBaseType()) {
                expressionTypeID = scope.environment().computeBoxingType(expressionType).id;
            }
        }
        if (expressionTypeID > 15) {
            this.constant = Constant.NotAConstant;
            scope.problemReporter().invalidOperator(this, expressionType);
            return null;
        }

        int tableId;
        switch ((this.bits & OperatorMASK) >> OperatorSHIFT) {
            case NOT:
                tableId = AND_AND;
                break;
            case TWIDDLE:
                tableId = LEFT_SHIFT;
                break;
            default:
                tableId = MINUS;
        } //+ and - cases

        // the code is an int
        // (cast)  left   Op (cast)  rigth --> result
        //  0000   0000       0000   0000      0000
        //  <<16   <<12       <<8    <<4       <<0
        int operatorSignature = OperatorSignatures[tableId][(expressionTypeID << 4) + expressionTypeID];
        this.expression.computeConversion(scope, TypeBinding.wellKnownType(scope, (operatorSignature >>> 16) & 0x0000F),
                                          expressionType);
        this.bits |= operatorSignature & 0xF;
        switch (operatorSignature & 0xF) { // only switch on possible result type.....
            case T_boolean:
                this.resolvedType = TypeBinding.BOOLEAN;
                break;
            case T_byte:
                this.resolvedType = TypeBinding.BYTE;
                break;
            case T_char:
                this.resolvedType = TypeBinding.CHAR;
                break;
            case T_double:
                this.resolvedType = TypeBinding.DOUBLE;
                break;
            case T_float:
                this.resolvedType = TypeBinding.FLOAT;
                break;
            case T_int:
                this.resolvedType = TypeBinding.INT;
                break;
            case T_long:
                this.resolvedType = TypeBinding.LONG;
                break;
            default: //error........
                this.constant = Constant.NotAConstant;
                if (expressionTypeID != T_undefined) {
                    scope.problemReporter().invalidOperator(this, expressionType);
                }
                return null;
        }
        // compute the constant when valid
        if (this.expression.constant != Constant.NotAConstant) {
            this.constant =
                    Constant.computeConstantOperation(this.expression.constant, expressionTypeID,
                                                      (this.bits & OperatorMASK) >> OperatorSHIFT);
        } else {
            this.constant = Constant.NotAConstant;
            if (((this.bits & OperatorMASK) >> OperatorSHIFT) == NOT) {
                Constant cst = this.expression.optimizedBooleanConstant();
                if (cst != Constant.NotAConstant) {
                    this.optimizedBooleanConstant = BooleanConstant.fromValue(!cst.booleanValue());
                }
            }
        }
        if (expressionIsCast) {
            // check need for operand cast
            CastExpression.checkNeedForArgumentCast(scope, tableId, operatorSignature, this.expression, expressionTypeID);
        }
        return this.resolvedType;
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope blockScope) {

        if (visitor.visit(this, blockScope)) {
            this.expression.traverse(visitor, blockScope);
        }
        visitor.endVisit(this, blockScope);
    }
}
