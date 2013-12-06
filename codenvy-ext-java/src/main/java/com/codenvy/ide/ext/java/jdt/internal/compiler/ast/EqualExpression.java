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
package com.codenvy.ide.ext.java.jdt.internal.compiler.ast;

import com.codenvy.ide.ext.java.jdt.internal.compiler.ASTVisitor;
import com.codenvy.ide.ext.java.jdt.internal.compiler.ClassFileConstants;
import com.codenvy.ide.ext.java.jdt.internal.compiler.codegen.BranchLabel;
import com.codenvy.ide.ext.java.jdt.internal.compiler.flow.FlowContext;
import com.codenvy.ide.ext.java.jdt.internal.compiler.flow.FlowInfo;
import com.codenvy.ide.ext.java.jdt.internal.compiler.flow.UnconditionalFlowInfo;
import com.codenvy.ide.ext.java.jdt.internal.compiler.impl.BooleanConstant;
import com.codenvy.ide.ext.java.jdt.internal.compiler.impl.Constant;
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.*;

public class EqualExpression extends BinaryExpression {

    public EqualExpression(Expression left, Expression right, int operator) {
        super(left, right, operator);
    }

    private void checkNullComparison(BlockScope scope, FlowContext flowContext, FlowInfo flowInfo,
                                     FlowInfo initsWhenTrue, FlowInfo initsWhenFalse) {

        LocalVariableBinding local = this.left.localVariableBinding();
        if (local != null && (local.type.tagBits & TagBits.IsBaseType) == 0) {
            checkVariableComparison(scope, flowContext, flowInfo, initsWhenTrue, initsWhenFalse, local,
                                    this.right.nullStatus(flowInfo), this.left);
        }
        local = this.right.localVariableBinding();
        if (local != null && (local.type.tagBits & TagBits.IsBaseType) == 0) {
            checkVariableComparison(scope, flowContext, flowInfo, initsWhenTrue, initsWhenFalse, local,
                                    this.left.nullStatus(flowInfo), this.right);
        }
    }

    private void checkVariableComparison(BlockScope scope, FlowContext flowContext, FlowInfo flowInfo,
                                         FlowInfo initsWhenTrue, FlowInfo initsWhenFalse, LocalVariableBinding local, int nullStatus,
                                         Expression reference) {
        switch (nullStatus) {
            case FlowInfo.NULL:
                if (((this.bits & OperatorMASK) >> OperatorSHIFT) == EQUAL_EQUAL) {
                    flowContext.recordUsingNullReference(scope, local, reference, FlowContext.CAN_ONLY_NULL_NON_NULL
                                                                                  | FlowContext.IN_COMPARISON_NULL, flowInfo);
                    initsWhenTrue.markAsComparedEqualToNull(local); // from thereon it is set
                    initsWhenFalse.markAsComparedEqualToNonNull(local); // from thereon it is set
                } else {
                    flowContext.recordUsingNullReference(scope, local, reference, FlowContext.CAN_ONLY_NULL_NON_NULL
                                                                                  | FlowContext.IN_COMPARISON_NON_NULL, flowInfo);
                    initsWhenTrue.markAsComparedEqualToNonNull(local); // from thereon it is set
                    initsWhenFalse.markAsComparedEqualToNull(local); // from thereon it is set
                }
                if ((flowContext.tagBits & FlowContext.HIDE_NULL_COMPARISON_WARNING) != 0) {
                    flowInfo.markedAsNullOrNonNullInAssertExpression(local);
                }
                break;
            case FlowInfo.NON_NULL:
                if (((this.bits & OperatorMASK) >> OperatorSHIFT) == EQUAL_EQUAL) {
                    flowContext.recordUsingNullReference(scope, local, reference, FlowContext.CAN_ONLY_NULL
                                                                                  | FlowContext.IN_COMPARISON_NON_NULL, flowInfo);
                    initsWhenTrue.markAsComparedEqualToNonNull(local); // from thereon it is set
                    if ((flowContext.tagBits & FlowContext.HIDE_NULL_COMPARISON_WARNING) != 0) {
                        initsWhenTrue.markedAsNullOrNonNullInAssertExpression(local);
                    }
                } else {
                    flowContext.recordUsingNullReference(scope, local, reference, FlowContext.CAN_ONLY_NULL
                                                                                  | FlowContext.IN_COMPARISON_NULL, flowInfo);
                }
                break;
        }
        // we do not impact enclosing try context because this kind of protection
        // does not preclude the variable from being null in an enclosing scope
    }

    @Override
    public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
        FlowInfo result;
        if (((this.bits & OperatorMASK) >> OperatorSHIFT) == EQUAL_EQUAL) {
            if ((this.left.constant != Constant.NotAConstant) && (this.left.constant.typeID() == T_boolean)) {
                if (this.left.constant.booleanValue()) { //  true == anything
                    //  this is equivalent to the right argument inits
                    result = this.right.analyseCode(currentScope, flowContext, flowInfo);
                } else { // false == anything
                    //  this is equivalent to the right argument inits negated
                    result = this.right.analyseCode(currentScope, flowContext, flowInfo).asNegatedCondition();
                }
            } else if ((this.right.constant != Constant.NotAConstant) && (this.right.constant.typeID() == T_boolean)) {
                if (this.right.constant.booleanValue()) { //  anything == true
                    //  this is equivalent to the left argument inits
                    result = this.left.analyseCode(currentScope, flowContext, flowInfo);
                } else { // anything == false
                    //  this is equivalent to the right argument inits negated
                    result = this.left.analyseCode(currentScope, flowContext, flowInfo).asNegatedCondition();
                }
            } else {
                result =
                        this.right.analyseCode(currentScope, flowContext,
                                               this.left.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits())
                                  .unconditionalInits();
            }
        } else { //NOT_EQUAL :
            if ((this.left.constant != Constant.NotAConstant) && (this.left.constant.typeID() == T_boolean)) {
                if (!this.left.constant.booleanValue()) { //  false != anything
                    //  this is equivalent to the right argument inits
                    result = this.right.analyseCode(currentScope, flowContext, flowInfo);
                } else { // true != anything
                    //  this is equivalent to the right argument inits negated
                    result = this.right.analyseCode(currentScope, flowContext, flowInfo).asNegatedCondition();
                }
            } else if ((this.right.constant != Constant.NotAConstant) && (this.right.constant.typeID() == T_boolean)) {
                if (!this.right.constant.booleanValue()) { //  anything != false
                    //  this is equivalent to the right argument inits
                    result = this.left.analyseCode(currentScope, flowContext, flowInfo);
                } else { // anything != true
                    //  this is equivalent to the right argument inits negated
                    result = this.left.analyseCode(currentScope, flowContext, flowInfo).asNegatedCondition();
                }
            } else {
                result =
                        this.right.analyseCode(currentScope, flowContext,
                                               this.left.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits()).
               /* unneeded since we flatten it: asNegatedCondition(). */
                       unconditionalInits();
            }
        }
        if (result instanceof UnconditionalFlowInfo && (result.tagBits & FlowInfo.UNREACHABLE) == 0) { // the flow info is flat
            result = FlowInfo.conditional(result.copy(), result.copy());
            // TODO (maxime) check, reintroduced copy
        }
        checkNullComparison(currentScope, flowContext, result, result.initsWhenTrue(), result.initsWhenFalse());
        return result;
    }

    public final void computeConstant(TypeBinding leftType, TypeBinding rightType) {
        if ((this.left.constant != Constant.NotAConstant) && (this.right.constant != Constant.NotAConstant)) {
            this.constant =
                    Constant.computeConstantOperationEQUAL_EQUAL(this.left.constant, leftType.id, this.right.constant,
                                                                 rightType.id);
            if (((this.bits & OperatorMASK) >> OperatorSHIFT) == NOT_EQUAL) {
                this.constant = BooleanConstant.fromValue(!this.constant.booleanValue());
            }
        } else {
            this.constant = Constant.NotAConstant;
            // no optimization for null == null
        }
    }

    /**
     * Normal == or != code generation.
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

        if (this.constant != Constant.NotAConstant) {
            return;
        }

        if ((this.left.implicitConversion & COMPILE_TYPE_MASK) /*compile-time*/ == T_boolean) {
            generateBooleanEqual(currentScope, valueRequired);
        } else {
            generateNonBooleanEqual(currentScope, valueRequired);
        }
    }

    /**
     * Boolean operator code generation
     * Optimized operations are: == and !=
     */
    @Override
    public void generateOptimizedBoolean(BlockScope currentScope, BranchLabel trueLabel, BranchLabel falseLabel,
                                         boolean valueRequired) {

        if (this.constant != Constant.NotAConstant) {
            super.generateOptimizedBoolean(currentScope, trueLabel, falseLabel, valueRequired);
            return;
        }
        if (((this.bits & OperatorMASK) >> OperatorSHIFT) == EQUAL_EQUAL) {
            if ((this.left.implicitConversion & COMPILE_TYPE_MASK) /*compile-time*/ == T_boolean) {
                generateOptimizedBooleanEqual(currentScope, trueLabel, falseLabel, valueRequired);
            } else {
                generateOptimizedNonBooleanEqual(currentScope, trueLabel, falseLabel, valueRequired);
            }
        } else {
            if ((this.left.implicitConversion & COMPILE_TYPE_MASK) /*compile-time*/ == T_boolean) {
                generateOptimizedBooleanEqual(currentScope, falseLabel, trueLabel, valueRequired);
            } else {
                generateOptimizedNonBooleanEqual(currentScope, falseLabel, trueLabel, valueRequired);
            }
        }
    }

    /**
     * Boolean generation for == with boolean operands
     * <p/>
     * Note this code does not optimize conditional constants !!!!
     */
    public void generateBooleanEqual(BlockScope currentScope, boolean valueRequired) {

        // optimized cases: <something equivalent to true> == x, <something equivalent to false> == x,
        // optimized cases: <something equivalent to false> != x, <something equivalent to true> != x,
        boolean isEqualOperator = ((this.bits & OperatorMASK) >> OperatorSHIFT) == EQUAL_EQUAL;
        Constant cst = this.left.optimizedBooleanConstant();
        if (cst != Constant.NotAConstant) {
            Constant rightCst = this.right.optimizedBooleanConstant();
            if (rightCst != Constant.NotAConstant) {
                // <something equivalent to true> == <something equivalent to true>, <something equivalent to false> != <something
                // equivalent to true>
                // <something equivalent to true> == <something equivalent to false>, <something equivalent to false> != <something
                // equivalent to false>
                this.left.generateCode(currentScope, false);
                this.right.generateCode(currentScope, false);
            } else if (cst.booleanValue() == isEqualOperator) {
                // <something equivalent to true> == x, <something equivalent to false> != x
                this.left.generateCode(currentScope, false);
                this.right.generateCode(currentScope, valueRequired);
            } else {
                // <something equivalent to false> == x, <something equivalent to true> != x
                if (valueRequired) {
                    BranchLabel falseLabel = new BranchLabel();
                    this.left.generateCode(currentScope, false);
                    this.right.generateOptimizedBoolean(currentScope, null, falseLabel, valueRequired);
                    // comparison is TRUE

                } else {
                    this.left.generateCode(currentScope, false);
                    this.right.generateCode(currentScope, false);
                }
                //				left.generateCode(currentScope,  false);
                //				right.generateCode(currentScope,  valueRequired);
                //				if (valueRequired) {
                //					codeStream.iconst_1();
                //					codeStream.ixor(); // negate
                //				}
            }
            return;
        }
        cst = this.right.optimizedBooleanConstant();
        if (cst != Constant.NotAConstant) {
            if (cst.booleanValue() == isEqualOperator) {
                // x == <something equivalent to true>, x != <something equivalent to false>
                this.left.generateCode(currentScope, valueRequired);
                this.right.generateCode(currentScope, false);
            } else {
                // x == <something equivalent to false>, x != <something equivalent to true>
                if (valueRequired) {
                    BranchLabel falseLabel = new BranchLabel();
                    this.left.generateOptimizedBoolean(currentScope, null, falseLabel, valueRequired);
                    this.right.generateCode(currentScope, false);
                    // comparison is TRUE
                } else {
                    this.left.generateCode(currentScope, false);
                    this.right.generateCode(currentScope, false);
                }
                return;
            }
            // default case
            this.left.generateCode(currentScope, valueRequired);
            this.right.generateCode(currentScope, valueRequired);
        }
    }

    /**
     * Boolean generation for == with boolean operands
     * <p/>
     * Note this code does not optimize conditional constants !!!!
     */
    public void generateOptimizedBooleanEqual(BlockScope currentScope, BranchLabel trueLabel, BranchLabel falseLabel,
                                              boolean valueRequired) {

        // optimized cases: true == x, false == x
        if (this.left.constant != Constant.NotAConstant) {
            boolean inline = this.left.constant.booleanValue();
            this.right.generateOptimizedBoolean(currentScope, (inline ? trueLabel : falseLabel), (inline ? falseLabel
                                                                                                         : trueLabel), valueRequired);
            return;
        } // optimized cases: x == true, x == false
        if (this.right.constant != Constant.NotAConstant) {
            boolean inline = this.right.constant.booleanValue();
            this.left.generateOptimizedBoolean(currentScope, (inline ? trueLabel : falseLabel), (inline ? falseLabel
                                                                                                        : trueLabel), valueRequired);
            return;
        }
        // default case
        this.left.generateCode(currentScope, valueRequired);
        this.right.generateCode(currentScope, valueRequired);
    }

    /** Boolean generation for == with non-boolean operands */
    public void generateNonBooleanEqual(BlockScope currentScope, boolean valueRequired) {

        if (((this.left.implicitConversion & IMPLICIT_CONVERSION_MASK) >> 4) == T_int) {
            Constant cst;
            if ((cst = this.left.constant) != Constant.NotAConstant && cst.intValue() == 0) {
                // optimized case: 0 == x, 0 != x
                this.right.generateCode(currentScope, valueRequired);
                return;
            }
            if ((cst = this.right.constant) != Constant.NotAConstant && cst.intValue() == 0) {
                // optimized case: x == 0, x != 0
                this.left.generateCode(currentScope, valueRequired);
                return;
            }
        }

        // null cases
        if (this.right instanceof NullLiteral) {
            if (this.left instanceof NullLiteral) {
                // null == null, null != null
            }
            return;
        } else if (this.left instanceof NullLiteral) {
            // null = x, null != x
            this.right.generateCode(currentScope, valueRequired);
            return;
        }

        // default case
        this.left.generateCode(currentScope, valueRequired);
        this.right.generateCode(currentScope, valueRequired);
    }

    /** Boolean generation for == with non-boolean operands */
    public void generateOptimizedNonBooleanEqual(BlockScope currentScope, BranchLabel trueLabel, BranchLabel falseLabel,
                                                 boolean valueRequired) {

        Constant inline;
        if ((inline = this.right.constant) != Constant.NotAConstant) {
            // optimized case: x == 0
            if ((((this.left.implicitConversion & IMPLICIT_CONVERSION_MASK) >> 4) == T_int) && (inline.intValue() == 0)) {
                this.left.generateCode(currentScope, valueRequired);
                return;
            }
        }
        if ((inline = this.left.constant) != Constant.NotAConstant) {
            // optimized case: 0 == x
            if ((((this.left.implicitConversion & IMPLICIT_CONVERSION_MASK) >> 4) == T_int) && (inline.intValue() == 0)) {
                this.right.generateCode(currentScope, valueRequired);
                return;
            }
        }
        // null cases
        // optimized case: x == null
        if (this.right instanceof NullLiteral) {
            if (this.left instanceof NullLiteral) {
                // null == null
                return;
            } else if (this.left instanceof NullLiteral) { // optimized case: null == x
                this.right.generateCode(currentScope, valueRequired);
                return;
            }

            // default case
            this.left.generateCode(currentScope, valueRequired);
            this.right.generateCode(currentScope, valueRequired);
        }
    }

    @Override
    public boolean isCompactableOperation() {
        return false;
    }

    @Override
    public TypeBinding resolveType(BlockScope scope) {

        boolean leftIsCast, rightIsCast;
        if ((leftIsCast = this.left instanceof CastExpression) == true) {
            this.left.bits |= DisableUnnecessaryCastCheck; // will check later on
        }
        TypeBinding originalLeftType = this.left.resolveType(scope);

        if ((rightIsCast = this.right instanceof CastExpression) == true) {
            this.right.bits |= DisableUnnecessaryCastCheck; // will check later on
        }
        TypeBinding originalRightType = this.right.resolveType(scope);

        // always return BooleanBinding
        if (originalLeftType == null || originalRightType == null) {
            this.constant = Constant.NotAConstant;
            return null;
        }

        // autoboxing support
        boolean use15specifics = scope.compilerOptions().sourceLevel >= ClassFileConstants.JDK1_5;
        TypeBinding leftType = originalLeftType, rightType = originalRightType;
        if (use15specifics) {
            if (leftType != TypeBinding.NULL && leftType.isBaseType()) {
                if (!rightType.isBaseType()) {
                    rightType = scope.environment().computeBoxingType(rightType);
                }
            } else {
                if (rightType != TypeBinding.NULL && rightType.isBaseType()) {
                    leftType = scope.environment().computeBoxingType(leftType);
                }
            }
        }
        // both base type
        if (leftType.isBaseType() && rightType.isBaseType()) {
            int leftTypeID = leftType.id;
            int rightTypeID = rightType.id;

            // the code is an int
            // (cast)  left   == (cast)  right --> result
            //  0000   0000       0000   0000      0000
            //  <<16   <<12       <<8    <<4       <<0
            int operatorSignature = OperatorSignatures[EQUAL_EQUAL][(leftTypeID << 4) + rightTypeID];
            this.left.computeConversion(scope, TypeBinding.wellKnownType(scope, (operatorSignature >>> 16) & 0x0000F),
                                        originalLeftType);
            this.right.computeConversion(scope, TypeBinding.wellKnownType(scope, (operatorSignature >>> 8) & 0x0000F),
                                         originalRightType);
            this.bits |= operatorSignature & 0xF;
            if ((operatorSignature & 0x0000F) == T_undefined) {
                this.constant = Constant.NotAConstant;
                scope.problemReporter().invalidOperator(this, leftType, rightType);
                return null;
            }
            // check need for operand cast
            if (leftIsCast || rightIsCast) {
                CastExpression.checkNeedForArgumentCasts(scope, EQUAL_EQUAL, operatorSignature, this.left, leftType.id,
                                                         leftIsCast, this.right, rightType.id, rightIsCast);
            }
            computeConstant(leftType, rightType);

            // check whether comparing identical expressions
            Binding leftDirect = Expression.getDirectBinding(this.left);
            if (leftDirect != null && leftDirect == Expression.getDirectBinding(this.right)) {
                if (leftTypeID != TypeIds.T_double && leftTypeID != TypeIds.T_float
                    && (!(this.right instanceof Assignment))) {
                    scope.problemReporter().comparingIdenticalExpressions(this);
                }
            } else if (this.constant != Constant.NotAConstant) {
                // https://bugs.eclipse.org/bugs/show_bug.cgi?id=276740
                int operator = (this.bits & OperatorMASK) >> OperatorSHIFT;
                if ((operator == EQUAL_EQUAL && this.constant == BooleanConstant.fromValue(true))
                    || (operator == NOT_EQUAL && this.constant == BooleanConstant.fromValue(false))) {
                    scope.problemReporter().comparingIdenticalExpressions(this);
                }
            }
            return this.resolvedType = TypeBinding.BOOLEAN;
        }

        // Object references
        // spec 15.20.3
        if ((!leftType.isBaseType() || leftType == TypeBinding.NULL) // cannot compare: Object == (int)0
            && (!rightType.isBaseType() || rightType == TypeBinding.NULL)
            && (checkCastTypesCompatibility(scope, leftType, rightType, null) || checkCastTypesCompatibility(scope,
                                                                                                             rightType, leftType, null))) {

            // (special case for String)
            if ((rightType.id == T_JavaLangString) && (leftType.id == T_JavaLangString)) {
                computeConstant(leftType, rightType);
            } else {
                this.constant = Constant.NotAConstant;
            }
            TypeBinding objectType = scope.getJavaLangObject();
            this.left.computeConversion(scope, objectType, leftType);
            this.right.computeConversion(scope, objectType, rightType);
            // check need for operand cast
            boolean unnecessaryLeftCast = (this.left.bits & UnnecessaryCast) != 0;
            boolean unnecessaryRightCast = (this.right.bits & UnnecessaryCast) != 0;
            if (unnecessaryLeftCast || unnecessaryRightCast) {
                TypeBinding alternateLeftType =
                        unnecessaryLeftCast ? ((CastExpression)this.left).expression.resolvedType : leftType;
                TypeBinding alternateRightType =
                        unnecessaryRightCast ? ((CastExpression)this.right).expression.resolvedType : rightType;
                if (checkCastTypesCompatibility(scope, alternateLeftType, alternateRightType, null)
                    || checkCastTypesCompatibility(scope, alternateRightType, alternateLeftType, null)) {
                    if (unnecessaryLeftCast) {
                        scope.problemReporter().unnecessaryCast((CastExpression)this.left);
                    }
                    if (unnecessaryRightCast) {
                        scope.problemReporter().unnecessaryCast((CastExpression)this.right);
                    }
                }
            }
            // check whether comparing identical expressions
            Binding leftDirect = Expression.getDirectBinding(this.left);
            if (leftDirect != null && leftDirect == Expression.getDirectBinding(this.right)) {
                if (!(this.right instanceof Assignment)) {
                    scope.problemReporter().comparingIdenticalExpressions(this);
                }
            }
            return this.resolvedType = TypeBinding.BOOLEAN;
        }
        this.constant = Constant.NotAConstant;
        scope.problemReporter().notCompatibleTypesError(this, leftType, rightType);
        return null;
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope scope) {
        if (visitor.visit(this, scope)) {
            this.left.traverse(visitor, scope);
            this.right.traverse(visitor, scope);
        }
        visitor.endVisit(this, scope);
    }
}
