/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
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
import com.codenvy.ide.ext.java.worker.internal.compiler.codegen.BranchLabel;
import com.codenvy.ide.ext.java.worker.internal.compiler.flow.FlowContext;
import com.codenvy.ide.ext.java.worker.internal.compiler.flow.FlowInfo;
import com.codenvy.ide.ext.java.worker.internal.compiler.impl.Constant;
import com.codenvy.ide.ext.java.worker.internal.compiler.lookup.ArrayBinding;
import com.codenvy.ide.ext.java.worker.internal.compiler.lookup.BlockScope;
import com.codenvy.ide.ext.java.worker.internal.compiler.lookup.TypeBinding;
import com.codenvy.ide.ext.java.worker.internal.compiler.lookup.TypeIds;

public class BinaryExpression extends OperatorExpression {

   /* Tracking helpers
    * The following are used to elaborate realistic statistics about binary
    * expressions. This must be neutralized in the released code.
    * Search the keyword BE_INSTRUMENTATION to reenable.
    * An external device must install a suitable probe so as to monitor the
    * emission of events and publish the results.
   	public interface Probe {
   		public void ping(int depth);
   	}
   	public int depthTracker;
   	public static Probe probe;
    */

    public Expression left, right;

    public Constant optimizedBooleanConstant;

    public BinaryExpression(Expression left, Expression right, int operator) {
        this.left = left;
        this.right = right;
        this.bits |= operator << ASTNode.OperatorSHIFT; // encode operator
        this.sourceStart = left.sourceStart;
        this.sourceEnd = right.sourceEnd;
        // BE_INSTRUMENTATION: neutralized in the released code
        //	if (left instanceof BinaryExpression &&
        //			((left.bits & OperatorMASK) ^ (this.bits & OperatorMASK)) == 0) {
        //		this.depthTracker = ((BinaryExpression)left).depthTracker + 1;
        //	} else {
        //		this.depthTracker = 1;
        //	}
    }

    public BinaryExpression(BinaryExpression expression) {
        this.left = expression.left;
        this.right = expression.right;
        this.bits = expression.bits;
        this.sourceStart = expression.sourceStart;
        this.sourceEnd = expression.sourceEnd;
    }

    public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
        // keep implementation in sync with CombinedBinaryExpression#analyseCode
        if (this.resolvedType.id == TypeIds.T_JavaLangString) {
            return this.right.analyseCode(currentScope, flowContext,
                                          this.left.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits())
                             .unconditionalInits();
        } else {
            this.left.checkNPE(currentScope, flowContext, flowInfo);
            flowInfo = this.left.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits();
            this.right.checkNPE(currentScope, flowContext, flowInfo);
            return this.right.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits();
        }
    }

    public void computeConstant(BlockScope scope, int leftId, int rightId) {
        //compute the constant when valid
        if ((this.left.constant != Constant.NotAConstant) && (this.right.constant != Constant.NotAConstant)) {
            try {
                this.constant =
                        Constant.computeConstantOperation(this.left.constant, leftId,
                                                          (this.bits & ASTNode.OperatorMASK) >> ASTNode.OperatorSHIFT, this.right.constant,
                                                          rightId);
            } catch (ArithmeticException e) {
                this.constant = Constant.NotAConstant;
                // 1.2 no longer throws an exception at compile-time
                //scope.problemReporter().compileTimeConstantThrowsArithmeticException(this);
            }
        } else {
            this.constant = Constant.NotAConstant;
            //add some work for the boolean operators & |
            this.optimizedBooleanConstant(leftId, (this.bits & ASTNode.OperatorMASK) >> ASTNode.OperatorSHIFT, rightId);
        }
    }

    public Constant optimizedBooleanConstant() {
        return this.optimizedBooleanConstant == null ? this.constant : this.optimizedBooleanConstant;
    }

    /** Code generation for a binary operation */
    // given the current focus of CombinedBinaryExpression on strings concatenation,
    // we do not provide a general, non-recursive implementation of generateCode,
    // but rely upon generateOptimizedStringConcatenationCreation instead
    public void generateCode(BlockScope currentScope, boolean valueRequired) {
        if (this.constant != Constant.NotAConstant) {
            return;
        }
        switch ((this.bits & ASTNode.OperatorMASK) >> ASTNode.OperatorSHIFT) {
            case PLUS:
                switch (this.bits & ASTNode.ReturnTypeIDMASK) {
                    case T_JavaLangString:
                        // BE_INSTRUMENTATION: neutralized in the released code
                        //					if (probe != null) {
                        //						probe.ping(this.depthTracker);
                        //					}
                        break;
                    case T_int:
                        this.left.generateCode(currentScope, valueRequired);
                        this.right.generateCode(currentScope, valueRequired);
                        break;
                    case T_long:
                        this.left.generateCode(currentScope, valueRequired);
                        this.right.generateCode(currentScope, valueRequired);
                        break;
                    case T_double:
                        this.left.generateCode(currentScope, valueRequired);
                        this.right.generateCode(currentScope, valueRequired);
                        break;
                    case T_float:
                        this.left.generateCode(currentScope, valueRequired);
                        this.right.generateCode(currentScope, valueRequired);
                        break;
                }
                break;
            case MINUS:
                switch (this.bits & ASTNode.ReturnTypeIDMASK) {
                    case T_int:
                        this.left.generateCode(currentScope, valueRequired);
                        this.right.generateCode(currentScope, valueRequired);
                        break;
                    case T_long:
                        this.left.generateCode(currentScope, valueRequired);
                        this.right.generateCode(currentScope, valueRequired);
                        break;
                    case T_double:
                        this.left.generateCode(currentScope, valueRequired);
                        this.right.generateCode(currentScope, valueRequired);
                        break;
                    case T_float:
                        this.left.generateCode(currentScope, valueRequired);
                        this.right.generateCode(currentScope, valueRequired);
                        break;
                }
                break;
            case MULTIPLY:
                switch (this.bits & ASTNode.ReturnTypeIDMASK) {
                    case T_int:
                        this.left.generateCode(currentScope, valueRequired);
                        this.right.generateCode(currentScope, valueRequired);
                        break;
                    case T_long:
                        this.left.generateCode(currentScope, valueRequired);
                        this.right.generateCode(currentScope, valueRequired);
                        break;
                    case T_double:
                        this.left.generateCode(currentScope, valueRequired);
                        this.right.generateCode(currentScope, valueRequired);
                        break;
                    case T_float:
                        this.left.generateCode(currentScope, valueRequired);
                        this.right.generateCode(currentScope, valueRequired);
                        break;
                }
                break;
            case DIVIDE:
                switch (this.bits & ASTNode.ReturnTypeIDMASK) {
                    case T_int:
                        this.left.generateCode(currentScope, true);
                        this.right.generateCode(currentScope, true);
                        break;
                    case T_long:
                        this.left.generateCode(currentScope, true);
                        this.right.generateCode(currentScope, true);
                        break;
                    case T_double:
                        this.left.generateCode(currentScope, valueRequired);
                        this.right.generateCode(currentScope, valueRequired);
                        break;
                    case T_float:
                        this.left.generateCode(currentScope, valueRequired);
                        this.right.generateCode(currentScope, valueRequired);
                        break;
                }
                break;
            case REMAINDER:
                switch (this.bits & ASTNode.ReturnTypeIDMASK) {
                    case T_int:
                        this.left.generateCode(currentScope, true);
                        this.right.generateCode(currentScope, true);
                        break;
                    case T_long:
                        this.left.generateCode(currentScope, true);
                        this.right.generateCode(currentScope, true);
                        break;
                    case T_double:
                        this.left.generateCode(currentScope, valueRequired);
                        this.right.generateCode(currentScope, valueRequired);
                        break;
                    case T_float:
                        this.left.generateCode(currentScope, valueRequired);
                        this.right.generateCode(currentScope, valueRequired);
                        break;
                }
                break;
            case AND:
                switch (this.bits & ASTNode.ReturnTypeIDMASK) {
                    case T_int:
                        // 0 & x
                        if ((this.left.constant != Constant.NotAConstant) && (this.left.constant.typeID() == TypeIds.T_int)
                            && (this.left.constant.intValue() == 0)) {
                            this.right.generateCode(currentScope, false);
                        } else {
                            // x & 0
                            if ((this.right.constant != Constant.NotAConstant)
                                && (this.right.constant.typeID() == TypeIds.T_int) && (this.right.constant.intValue() == 0)) {
                                this.left.generateCode(currentScope, false);
                            } else {
                                this.left.generateCode(currentScope, valueRequired);
                                this.right.generateCode(currentScope, valueRequired);
                            }
                        }
                        break;
                    case T_long:
                        // 0 & x
                        if ((this.left.constant != Constant.NotAConstant) && (this.left.constant.typeID() == TypeIds.T_long)
                            && (this.left.constant.longValue() == 0L)) {
                            this.right.generateCode(currentScope, false);
                        } else {
                            // x & 0
                            if ((this.right.constant != Constant.NotAConstant)
                                && (this.right.constant.typeID() == TypeIds.T_long) && (this.right.constant.longValue() == 0L)) {
                                this.left.generateCode(currentScope, false);
                            } else {
                                this.left.generateCode(currentScope, valueRequired);
                                this.right.generateCode(currentScope, valueRequired);
                            }
                        }
                        break;
                    case T_boolean: // logical and
                        generateLogicalAnd(currentScope, valueRequired);
                        break;
                }
                break;
            case OR:
                switch (this.bits & ASTNode.ReturnTypeIDMASK) {
                    case T_int:
                        // 0 | x
                        if ((this.left.constant != Constant.NotAConstant) && (this.left.constant.typeID() == TypeIds.T_int)
                            && (this.left.constant.intValue() == 0)) {
                            this.right.generateCode(currentScope, valueRequired);
                        } else {
                            // x | 0
                            if ((this.right.constant != Constant.NotAConstant)
                                && (this.right.constant.typeID() == TypeIds.T_int) && (this.right.constant.intValue() == 0)) {
                                this.left.generateCode(currentScope, valueRequired);
                            } else {
                                this.left.generateCode(currentScope, valueRequired);
                                this.right.generateCode(currentScope, valueRequired);
                            }
                        }
                        break;
                    case T_long:
                        // 0 | x
                        if ((this.left.constant != Constant.NotAConstant) && (this.left.constant.typeID() == TypeIds.T_long)
                            && (this.left.constant.longValue() == 0L)) {
                            this.right.generateCode(currentScope, valueRequired);
                        } else {
                            // x | 0
                            if ((this.right.constant != Constant.NotAConstant)
                                && (this.right.constant.typeID() == TypeIds.T_long) && (this.right.constant.longValue() == 0L)) {
                                this.left.generateCode(currentScope, valueRequired);
                            } else {
                                this.left.generateCode(currentScope, valueRequired);
                                this.right.generateCode(currentScope, valueRequired);
                            }
                        }
                        break;
                    case T_boolean: // logical or
                        generateLogicalOr(currentScope, valueRequired);
                        break;
                }
                break;
            case XOR:
                switch (this.bits & ASTNode.ReturnTypeIDMASK) {
                    case T_int:
                        // 0 ^ x
                        if ((this.left.constant != Constant.NotAConstant) && (this.left.constant.typeID() == TypeIds.T_int)
                            && (this.left.constant.intValue() == 0)) {
                            this.right.generateCode(currentScope, valueRequired);
                        } else {
                            // x ^ 0
                            if ((this.right.constant != Constant.NotAConstant)
                                && (this.right.constant.typeID() == TypeIds.T_int) && (this.right.constant.intValue() == 0)) {
                                this.left.generateCode(currentScope, valueRequired);
                            } else {
                                this.left.generateCode(currentScope, valueRequired);
                                this.right.generateCode(currentScope, valueRequired);
                            }
                        }
                        break;
                    case T_long:
                        // 0 ^ x
                        if ((this.left.constant != Constant.NotAConstant) && (this.left.constant.typeID() == TypeIds.T_long)
                            && (this.left.constant.longValue() == 0L)) {
                            this.right.generateCode(currentScope, valueRequired);
                        } else {
                            // x ^ 0
                            if ((this.right.constant != Constant.NotAConstant)
                                && (this.right.constant.typeID() == TypeIds.T_long) && (this.right.constant.longValue() == 0L)) {
                                this.left.generateCode(currentScope, valueRequired);
                            } else {
                                this.left.generateCode(currentScope, valueRequired);
                                this.right.generateCode(currentScope, valueRequired);
                            }
                        }
                        break;
                    case T_boolean:
                        generateLogicalXor(currentScope, valueRequired);
                        break;
                }
                break;
            case LEFT_SHIFT:
                switch (this.bits & ASTNode.ReturnTypeIDMASK) {
                    case T_int:
                        this.left.generateCode(currentScope, valueRequired);
                        this.right.generateCode(currentScope, valueRequired);
                        break;
                    case T_long:
                        this.left.generateCode(currentScope, valueRequired);
                        this.right.generateCode(currentScope, valueRequired);
                }
                break;
            case RIGHT_SHIFT:
                switch (this.bits & ASTNode.ReturnTypeIDMASK) {
                    case T_int:
                        this.left.generateCode(currentScope, valueRequired);
                        this.right.generateCode(currentScope, valueRequired);
                        break;
                    case T_long:
                        this.left.generateCode(currentScope, valueRequired);
                        this.right.generateCode(currentScope, valueRequired);
                }
                break;
            case UNSIGNED_RIGHT_SHIFT:
                switch (this.bits & ASTNode.ReturnTypeIDMASK) {
                    case T_int:
                        this.left.generateCode(currentScope, valueRequired);
                        this.right.generateCode(currentScope, valueRequired);
                        break;
                    case T_long:
                        this.left.generateCode(currentScope, valueRequired);
                        this.right.generateCode(currentScope, valueRequired);
                }
                break;
            case GREATER:
                generateOptimizedGreaterThan(currentScope, null, (new BranchLabel()), valueRequired);
                break;
            case GREATER_EQUAL:
                generateOptimizedGreaterThanOrEqual(currentScope, null, (new BranchLabel()), valueRequired);
                break;
            case LESS:
                generateOptimizedLessThan(currentScope, null, (new BranchLabel()), valueRequired);

                break;
            case LESS_EQUAL:
                generateOptimizedLessThanOrEqual(currentScope, null, (new BranchLabel()), valueRequired);
        }
    }

    /**
     * Boolean operator code generation
     * Optimized operations are: <, <=, >, >=, &, |, ^
     */
    public void generateOptimizedBoolean(BlockScope currentScope, BranchLabel trueLabel, BranchLabel falseLabel,
                                         boolean valueRequired) {
        if ((this.constant != Constant.NotAConstant) && (this.constant.typeID() == TypeIds.T_boolean)) {
            super.generateOptimizedBoolean(currentScope, trueLabel, falseLabel, valueRequired);
            return;
        }
        switch ((this.bits & ASTNode.OperatorMASK) >> ASTNode.OperatorSHIFT) {
            case LESS:
                generateOptimizedLessThan(currentScope, trueLabel, falseLabel, valueRequired);
                return;
            case LESS_EQUAL:
                generateOptimizedLessThanOrEqual(currentScope, trueLabel, falseLabel, valueRequired);
                return;
            case GREATER:
                generateOptimizedGreaterThan(currentScope, trueLabel, falseLabel, valueRequired);
                return;
            case GREATER_EQUAL:
                generateOptimizedGreaterThanOrEqual(currentScope, trueLabel, falseLabel, valueRequired);
                return;
            case AND:
                generateOptimizedLogicalAnd(currentScope, trueLabel, falseLabel, valueRequired);
                return;
            case OR:
                generateOptimizedLogicalOr(currentScope, trueLabel, falseLabel, valueRequired);
                return;
            case XOR:
                generateOptimizedLogicalXor(currentScope, trueLabel, falseLabel, valueRequired);
                return;
        }
        super.generateOptimizedBoolean(currentScope, trueLabel, falseLabel, valueRequired);
    }

    /** Boolean generation for > */
    public void generateOptimizedGreaterThan(BlockScope currentScope, BranchLabel trueLabel, BranchLabel falseLabel,
                                             boolean valueRequired) {
        int promotedTypeID = (this.left.implicitConversion & TypeIds.IMPLICIT_CONVERSION_MASK) >> 4;
        // both sides got promoted in the same way
        if (promotedTypeID == TypeIds.T_int) {
            // 0 > x
            if ((this.left.constant != Constant.NotAConstant) && (this.left.constant.intValue() == 0)) {
                this.right.generateCode(currentScope, valueRequired);
                return;
            }
            // x > 0
            if ((this.right.constant != Constant.NotAConstant) && (this.right.constant.intValue() == 0)) {
                this.left.generateCode(currentScope, valueRequired);
                return;
            }
        }
        // default comparison
        this.left.generateCode(currentScope, valueRequired);
        this.right.generateCode(currentScope, valueRequired);
    }

    /** Boolean generation for >= */
    public void generateOptimizedGreaterThanOrEqual(BlockScope currentScope, BranchLabel trueLabel,
                                                    BranchLabel falseLabel, boolean valueRequired) {
        int promotedTypeID = (this.left.implicitConversion & TypeIds.IMPLICIT_CONVERSION_MASK) >> 4;
        // both sides got promoted in the same way
        if (promotedTypeID == TypeIds.T_int) {
            // 0 >= x
            if ((this.left.constant != Constant.NotAConstant) && (this.left.constant.intValue() == 0)) {
                this.right.generateCode(currentScope, valueRequired);
                return;
            }
            // x >= 0
            if ((this.right.constant != Constant.NotAConstant) && (this.right.constant.intValue() == 0)) {
                this.left.generateCode(currentScope, valueRequired);
                return;
            }
        }
        // default comparison
        this.left.generateCode(currentScope, valueRequired);
        this.right.generateCode(currentScope, valueRequired);

    }

    /** Boolean generation for < */
    public void generateOptimizedLessThan(BlockScope currentScope, BranchLabel trueLabel, BranchLabel falseLabel,
                                          boolean valueRequired) {
        int promotedTypeID = (this.left.implicitConversion & TypeIds.IMPLICIT_CONVERSION_MASK) >> 4;
        // both sides got promoted in the same way
        if (promotedTypeID == TypeIds.T_int) {
            // 0 < x
            if ((this.left.constant != Constant.NotAConstant) && (this.left.constant.intValue() == 0)) {
                this.right.generateCode(currentScope, valueRequired);
                return;
            }
            // x < 0
            if ((this.right.constant != Constant.NotAConstant) && (this.right.constant.intValue() == 0)) {
                this.left.generateCode(currentScope, valueRequired);
                return;
            }
        }
        // default comparison
        this.left.generateCode(currentScope, valueRequired);
        this.right.generateCode(currentScope, valueRequired);
    }

    /** Boolean generation for <= */
    public void generateOptimizedLessThanOrEqual(BlockScope currentScope, BranchLabel trueLabel, BranchLabel falseLabel,
                                                 boolean valueRequired) {
        int promotedTypeID = (this.left.implicitConversion & TypeIds.IMPLICIT_CONVERSION_MASK) >> 4;
        // both sides got promoted in the same way
        if (promotedTypeID == TypeIds.T_int) {
            // 0 <= x
            if ((this.left.constant != Constant.NotAConstant) && (this.left.constant.intValue() == 0)) {
                this.right.generateCode(currentScope, valueRequired);
                return;
            }
            // x <= 0
            if ((this.right.constant != Constant.NotAConstant) && (this.right.constant.intValue() == 0)) {
                this.left.generateCode(currentScope, valueRequired);
                return;
            }
        }
        // default comparison
        this.left.generateCode(currentScope, valueRequired);
        this.right.generateCode(currentScope, valueRequired);
    }

    /** Boolean generation for & */
    public void generateLogicalAnd(BlockScope currentScope, boolean valueRequired) {
        Constant condConst;
        if ((this.left.implicitConversion & TypeIds.COMPILE_TYPE_MASK) == TypeIds.T_boolean) {
            if ((condConst = this.left.optimizedBooleanConstant()) != Constant.NotAConstant) {
                if (condConst.booleanValue() == true) {
                    // <something equivalent to true> & x
                    this.left.generateCode(currentScope, false);
                    this.right.generateCode(currentScope, valueRequired);
                } else {
                    // <something equivalent to false> & x
                    this.left.generateCode(currentScope, false);
                    this.right.generateCode(currentScope, false);
                }
                return;
            }
            if ((condConst = this.right.optimizedBooleanConstant()) != Constant.NotAConstant) {
                if (condConst.booleanValue() == true) {
                    // x & <something equivalent to true>
                    this.left.generateCode(currentScope, valueRequired);
                    this.right.generateCode(currentScope, false);
                } else {
                    // x & <something equivalent to false>
                    this.left.generateCode(currentScope, false);
                    this.right.generateCode(currentScope, false);
                }
                return;
            }
        }
        // default case
        this.left.generateCode(currentScope, valueRequired);
        this.right.generateCode(currentScope, valueRequired);
    }

    /** Boolean generation for | */
    public void generateLogicalOr(BlockScope currentScope, boolean valueRequired) {
        Constant condConst;
        if ((this.left.implicitConversion & TypeIds.COMPILE_TYPE_MASK) == TypeIds.T_boolean) {
            if ((condConst = this.left.optimizedBooleanConstant()) != Constant.NotAConstant) {
                if (condConst.booleanValue() == true) {
                    // <something equivalent to true> | x
                    this.left.generateCode(currentScope, false);
                    this.right.generateCode(currentScope, false);

                } else {
                    // <something equivalent to false> | x
                    this.left.generateCode(currentScope, false);
                    this.right.generateCode(currentScope, valueRequired);
                }
                return;
            }
            if ((condConst = this.right.optimizedBooleanConstant()) != Constant.NotAConstant) {
                if (condConst.booleanValue() == true) {
                    // x | <something equivalent to true>
                    this.left.generateCode(currentScope, false);
                    this.right.generateCode(currentScope, false);

                } else {
                    // x | <something equivalent to false>
                    this.left.generateCode(currentScope, valueRequired);
                    this.right.generateCode(currentScope, false);
                }
                return;
            }
        }
        // default case
        this.left.generateCode(currentScope, valueRequired);
        this.right.generateCode(currentScope, valueRequired);
    }

    /** Boolean generation for ^ */
    public void generateLogicalXor(BlockScope currentScope, boolean valueRequired) {
        Constant condConst;
        if ((this.left.implicitConversion & TypeIds.COMPILE_TYPE_MASK) == TypeIds.T_boolean) {
            if ((condConst = this.left.optimizedBooleanConstant()) != Constant.NotAConstant) {
                if (condConst.booleanValue() == true) {
                    // <something equivalent to true> ^ x
                    this.left.generateCode(currentScope, false);
                    this.right.generateCode(currentScope, valueRequired);
                } else {
                    // <something equivalent to false> ^ x
                    this.left.generateCode(currentScope, false);
                    this.right.generateCode(currentScope, valueRequired);
                }
                return;
            }
            if ((condConst = this.right.optimizedBooleanConstant()) != Constant.NotAConstant) {
                if (condConst.booleanValue() == true) {
                    // x ^ <something equivalent to true>
                    this.left.generateCode(currentScope, valueRequired);
                    this.right.generateCode(currentScope, false);
                } else {
                    // x ^ <something equivalent to false>
                    this.left.generateCode(currentScope, valueRequired);
                    this.right.generateCode(currentScope, false);
                }
                return;
            }
        }
        // default case
        this.left.generateCode(currentScope, valueRequired);
        this.right.generateCode(currentScope, valueRequired);
    }

    /** Boolean generation for & */
    public void generateOptimizedLogicalAnd(BlockScope currentScope, BranchLabel trueLabel, BranchLabel falseLabel,
                                            boolean valueRequired) {
        Constant condConst;
        if ((this.left.implicitConversion & TypeIds.COMPILE_TYPE_MASK) == TypeIds.T_boolean) {
            if ((condConst = this.left.optimizedBooleanConstant()) != Constant.NotAConstant) {
                if (condConst.booleanValue() == true) {
                    // <something equivalent to true> & x
                    this.left.generateOptimizedBoolean(currentScope, trueLabel, falseLabel, false);
                    this.right.generateOptimizedBoolean(currentScope, trueLabel, falseLabel, valueRequired);
                } else {
                    // <something equivalent to false> & x
                    this.left.generateOptimizedBoolean(currentScope, trueLabel, falseLabel, false);
                    this.right.generateOptimizedBoolean(currentScope, trueLabel, falseLabel, false);
                }
                return;
            }
            if ((condConst = this.right.optimizedBooleanConstant()) != Constant.NotAConstant) {
                if (condConst.booleanValue() == true) {
                    // x & <something equivalent to true>
                    this.left.generateOptimizedBoolean(currentScope, trueLabel, falseLabel, valueRequired);
                    this.right.generateOptimizedBoolean(currentScope, trueLabel, falseLabel, false);
                } else {
                    // x & <something equivalent to false>
                    BranchLabel internalTrueLabel = new BranchLabel();
                    this.left.generateOptimizedBoolean(currentScope, internalTrueLabel, falseLabel, false);
                    this.right.generateOptimizedBoolean(currentScope, trueLabel, falseLabel, false);
                }
                return;
            }
        }
        // default case
        this.left.generateCode(currentScope, valueRequired);
        this.right.generateCode(currentScope, valueRequired);
    }

    /** Boolean generation for | */
    public void generateOptimizedLogicalOr(BlockScope currentScope, BranchLabel trueLabel, BranchLabel falseLabel,
                                           boolean valueRequired) {
        Constant condConst;
        if ((this.left.implicitConversion & TypeIds.COMPILE_TYPE_MASK) == TypeIds.T_boolean) {
            if ((condConst = this.left.optimizedBooleanConstant()) != Constant.NotAConstant) {
                if (condConst.booleanValue() == true) {
                    // <something equivalent to true> | x
                    this.left.generateOptimizedBoolean(currentScope, trueLabel, falseLabel, false);
                    BranchLabel internalFalseLabel = new BranchLabel();
                    this.right.generateOptimizedBoolean(currentScope, trueLabel, internalFalseLabel, false);
                } else {
                    // <something equivalent to false> | x
                    this.left.generateOptimizedBoolean(currentScope, trueLabel, falseLabel, false);
                    this.right.generateOptimizedBoolean(currentScope, trueLabel, falseLabel, valueRequired);
                }
                return;
            }
            if ((condConst = this.right.optimizedBooleanConstant()) != Constant.NotAConstant) {
                if (condConst.booleanValue() == true) {
                    // x | <something equivalent to true>
                    BranchLabel internalFalseLabel = new BranchLabel();
                    this.left.generateOptimizedBoolean(currentScope, trueLabel, internalFalseLabel, false);
                    this.right.generateOptimizedBoolean(currentScope, trueLabel, falseLabel, false);
                } else {
                    // x | <something equivalent to false>
                    this.left.generateOptimizedBoolean(currentScope, trueLabel, falseLabel, valueRequired);
                    this.right.generateOptimizedBoolean(currentScope, trueLabel, falseLabel, false);
                }
                return;
            }
        }
        // default case
        this.left.generateCode(currentScope, valueRequired);
        this.right.generateCode(currentScope, valueRequired);
    }

    /** Boolean generation for ^ */
    public void generateOptimizedLogicalXor(BlockScope currentScope, BranchLabel trueLabel, BranchLabel falseLabel,
                                            boolean valueRequired) {
        Constant condConst;
        if ((this.left.implicitConversion & TypeIds.COMPILE_TYPE_MASK) == TypeIds.T_boolean) {
            if ((condConst = this.left.optimizedBooleanConstant()) != Constant.NotAConstant) {
                if (condConst.booleanValue() == true) {
                    // <something equivalent to true> ^ x
                    this.left.generateOptimizedBoolean(currentScope, trueLabel, falseLabel, false);
                    this.right.generateOptimizedBoolean(currentScope, falseLabel, // negating
                                                        trueLabel, valueRequired);
                } else {
                    // <something equivalent to false> ^ x
                    this.left.generateOptimizedBoolean(currentScope, trueLabel, falseLabel, false);
                    this.right.generateOptimizedBoolean(currentScope, trueLabel, falseLabel, valueRequired);
                }
                return;
            }
            if ((condConst = this.right.optimizedBooleanConstant()) != Constant.NotAConstant) {
                if (condConst.booleanValue() == true) {
                    // x ^ <something equivalent to true>
                    this.left.generateOptimizedBoolean(currentScope, falseLabel, // negating
                                                       trueLabel, valueRequired);
                    this.right.generateOptimizedBoolean(currentScope, trueLabel, falseLabel, false);
                } else {
                    // x ^ <something equivalent to false>
                    this.left.generateOptimizedBoolean(currentScope, trueLabel, falseLabel, valueRequired);
                    this.right.generateOptimizedBoolean(currentScope, trueLabel, falseLabel, false);
                }
                return;
            }
        }
        // default case
        this.left.generateCode(currentScope, valueRequired);
        this.right.generateCode(currentScope, valueRequired);
    }

    public boolean isCompactableOperation() {
        return true;
    }

    /**
     * Separates into a reusable method the subpart of {@link
     * #resolveType(BlockScope)} that needs to be executed while climbing up the
     * chain of expressions of this' leftmost branch. For use by {@link
     * CombinedBinaryExpression#resolveType(BlockScope)}.
     *
     * @param scope
     *         the scope within which the resolution occurs
     */
    void nonRecursiveResolveTypeUpwards(BlockScope scope) {
        // keep implementation in sync with BinaryExpression#resolveType
        boolean leftIsCast, rightIsCast;
        TypeBinding leftType = this.left.resolvedType;

        if ((rightIsCast = this.right instanceof CastExpression) == true) {
            this.right.bits |= ASTNode.DisableUnnecessaryCastCheck; // will check later on
        }
        TypeBinding rightType = this.right.resolveType(scope);

        // use the id of the type to navigate into the table
        if (leftType == null || rightType == null) {
            this.constant = Constant.NotAConstant;
            return;
        }

        int leftTypeID = leftType.id;
        int rightTypeID = rightType.id;

        // autoboxing support
        boolean use15specifics = scope.compilerOptions().sourceLevel >= ClassFileConstants.JDK1_5;
        if (use15specifics) {
            if (!leftType.isBaseType() && rightTypeID != TypeIds.T_JavaLangString && rightTypeID != TypeIds.T_null) {
                leftTypeID = scope.environment().computeBoxingType(leftType).id;
            }
            if (!rightType.isBaseType() && leftTypeID != TypeIds.T_JavaLangString && leftTypeID != TypeIds.T_null) {
                rightTypeID = scope.environment().computeBoxingType(rightType).id;
            }
        }
        if (leftTypeID > 15 || rightTypeID > 15) { // must convert String + Object || Object + String
            if (leftTypeID == TypeIds.T_JavaLangString) {
                rightTypeID = TypeIds.T_JavaLangObject;
            } else if (rightTypeID == TypeIds.T_JavaLangString) {
                leftTypeID = TypeIds.T_JavaLangObject;
            } else {
                this.constant = Constant.NotAConstant;
                scope.problemReporter().invalidOperator(this, leftType, rightType);
                return;
            }
        }
        if (((this.bits & ASTNode.OperatorMASK) >> ASTNode.OperatorSHIFT) == OperatorIds.PLUS) {
            if (leftTypeID == TypeIds.T_JavaLangString) {
                this.left.computeConversion(scope, leftType, leftType);
                if (rightType.isArrayType() && ((ArrayBinding)rightType).elementsType() == TypeBinding.CHAR) {
                    scope.problemReporter().signalNoImplicitStringConversionForCharArrayExpression(this.right);
                }
            }
            if (rightTypeID == TypeIds.T_JavaLangString) {
                this.right.computeConversion(scope, rightType, rightType);
                if (leftType.isArrayType() && ((ArrayBinding)leftType).elementsType() == TypeBinding.CHAR) {
                    scope.problemReporter().signalNoImplicitStringConversionForCharArrayExpression(this.left);
                }
            }
        }

        // the code is an int
        // (cast)  left   Op (cast)  right --> result
        //  0000   0000       0000   0000      0000
        //  <<16   <<12       <<8    <<4       <<0

        // Don't test for result = 0. If it is zero, some more work is done.
        // On the one hand when it is not zero (correct code) we avoid doing the test
        int operator = (this.bits & ASTNode.OperatorMASK) >> ASTNode.OperatorSHIFT;
        int operatorSignature = OperatorExpression.OperatorSignatures[operator][(leftTypeID << 4) + rightTypeID];

        this.left.computeConversion(scope, TypeBinding.wellKnownType(scope, (operatorSignature >>> 16) & 0x0000F),
                                    leftType);
        this.right.computeConversion(scope, TypeBinding.wellKnownType(scope, (operatorSignature >>> 8) & 0x0000F),
                                     rightType);
        this.bits |= operatorSignature & 0xF;
        switch (operatorSignature & 0xF) { // record the current ReturnTypeID
            // only switch on possible result type.....
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
            case T_JavaLangString:
                this.resolvedType = scope.getJavaLangString();
                break;
            default: //error........
                this.constant = Constant.NotAConstant;
                scope.problemReporter().invalidOperator(this, leftType, rightType);
                return;
        }

        // check need for operand cast
        if ((leftIsCast = (this.left instanceof CastExpression)) == true || rightIsCast) {
            CastExpression.checkNeedForArgumentCasts(scope, operator, operatorSignature, this.left, leftTypeID,
                                                     leftIsCast, this.right, rightTypeID, rightIsCast);
        }
        // compute the constant when valid
        computeConstant(scope, leftTypeID, rightTypeID);
    }

    public void optimizedBooleanConstant(int leftId, int operator, int rightId) {
        switch (operator) {
            case AND:
                if ((leftId != TypeIds.T_boolean) || (rightId != TypeIds.T_boolean))
                    return;
                //$FALL-THROUGH$
            case AND_AND:
                Constant cst;
                if ((cst = this.left.optimizedBooleanConstant()) != Constant.NotAConstant) {
                    if (cst.booleanValue() == false) { // left is equivalent to false
                        this.optimizedBooleanConstant = cst; // constant(false)
                        return;
                    } else { //left is equivalent to true
                        if ((cst = this.right.optimizedBooleanConstant()) != Constant.NotAConstant) {
                            this.optimizedBooleanConstant = cst;
                            // the conditional result is equivalent to the right conditional value
                        }
                        return;
                    }
                }
                if ((cst = this.right.optimizedBooleanConstant()) != Constant.NotAConstant) {
                    if (cst.booleanValue() == false) { // right is equivalent to false
                        this.optimizedBooleanConstant = cst; // constant(false)
                    }
                }
                return;
            case OR:
                if ((leftId != TypeIds.T_boolean) || (rightId != TypeIds.T_boolean))
                    return;
                //$FALL-THROUGH$
            case OR_OR:
                if ((cst = this.left.optimizedBooleanConstant()) != Constant.NotAConstant) {
                    if (cst.booleanValue() == true) { // left is equivalent to true
                        this.optimizedBooleanConstant = cst; // constant(true)
                        return;
                    } else { //left is equivalent to false
                        if ((cst = this.right.optimizedBooleanConstant()) != Constant.NotAConstant) {
                            this.optimizedBooleanConstant = cst;
                        }
                        return;
                    }
                }
                if ((cst = this.right.optimizedBooleanConstant()) != Constant.NotAConstant) {
                    if (cst.booleanValue() == true) { // right is equivalent to true
                        this.optimizedBooleanConstant = cst; // constant(true)
                    }
                }
        }
    }

    public StringBuffer printExpressionNoParenthesis(int indent, StringBuffer output) {
        // keep implementation in sync with
        // CombinedBinaryExpression#printExpressionNoParenthesis
        this.left.printExpression(indent, output).append(' ').append(operatorToString()).append(' ');
        return this.right.printExpression(0, output);
    }

    public TypeBinding resolveType(BlockScope scope) {
        // keep implementation in sync with CombinedBinaryExpression#resolveType
        // and nonRecursiveResolveTypeUpwards
        boolean leftIsCast, rightIsCast;
        if ((leftIsCast = this.left instanceof CastExpression) == true)
            this.left.bits |= ASTNode.DisableUnnecessaryCastCheck; // will check later on
        TypeBinding leftType = this.left.resolveType(scope);

        if ((rightIsCast = this.right instanceof CastExpression) == true)
            this.right.bits |= ASTNode.DisableUnnecessaryCastCheck; // will check later on
        TypeBinding rightType = this.right.resolveType(scope);

        // use the id of the type to navigate into the table
        if (leftType == null || rightType == null) {
            this.constant = Constant.NotAConstant;
            return null;
        }

        int leftTypeID = leftType.id;
        int rightTypeID = rightType.id;

        // autoboxing support
        boolean use15specifics = scope.compilerOptions().sourceLevel >= ClassFileConstants.JDK1_5;
        if (use15specifics) {
            if (!leftType.isBaseType() && rightTypeID != TypeIds.T_JavaLangString && rightTypeID != TypeIds.T_null) {
                leftTypeID = scope.environment().computeBoxingType(leftType).id;
            }
            if (!rightType.isBaseType() && leftTypeID != TypeIds.T_JavaLangString && leftTypeID != TypeIds.T_null) {
                rightTypeID = scope.environment().computeBoxingType(rightType).id;
            }
        }
        if (leftTypeID > 15 || rightTypeID > 15) { // must convert String + Object || Object + String
            if (leftTypeID == TypeIds.T_JavaLangString) {
                rightTypeID = TypeIds.T_JavaLangObject;
            } else if (rightTypeID == TypeIds.T_JavaLangString) {
                leftTypeID = TypeIds.T_JavaLangObject;
            } else {
                this.constant = Constant.NotAConstant;
                scope.problemReporter().invalidOperator(this, leftType, rightType);
                return null;
            }
        }
        if (((this.bits & ASTNode.OperatorMASK) >> ASTNode.OperatorSHIFT) == OperatorIds.PLUS) {
            if (leftTypeID == TypeIds.T_JavaLangString) {
                this.left.computeConversion(scope, leftType, leftType);
                if (rightType.isArrayType() && ((ArrayBinding)rightType).elementsType() == TypeBinding.CHAR) {
                    scope.problemReporter().signalNoImplicitStringConversionForCharArrayExpression(this.right);
                }
            }
            if (rightTypeID == TypeIds.T_JavaLangString) {
                this.right.computeConversion(scope, rightType, rightType);
                if (leftType.isArrayType() && ((ArrayBinding)leftType).elementsType() == TypeBinding.CHAR) {
                    scope.problemReporter().signalNoImplicitStringConversionForCharArrayExpression(this.left);
                }
            }
        }

        // the code is an int
        // (cast)  left   Op (cast)  right --> result
        //  0000   0000       0000   0000      0000
        //  <<16   <<12       <<8    <<4       <<0

        // Don't test for result = 0. If it is zero, some more work is done.
        // On the one hand when it is not zero (correct code) we avoid doing the test
        int operator = (this.bits & ASTNode.OperatorMASK) >> ASTNode.OperatorSHIFT;
        int operatorSignature = OperatorExpression.OperatorSignatures[operator][(leftTypeID << 4) + rightTypeID];

        this.left.computeConversion(scope, TypeBinding.wellKnownType(scope, (operatorSignature >>> 16) & 0x0000F),
                                    leftType);
        this.right.computeConversion(scope, TypeBinding.wellKnownType(scope, (operatorSignature >>> 8) & 0x0000F),
                                     rightType);
        this.bits |= operatorSignature & 0xF;
        switch (operatorSignature & 0xF) { // record the current ReturnTypeID
            // only switch on possible result type.....
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
            case T_JavaLangString:
                this.resolvedType = scope.getJavaLangString();
                break;
            default: //error........
                this.constant = Constant.NotAConstant;
                scope.problemReporter().invalidOperator(this, leftType, rightType);
                return null;
        }

        // check need for operand cast
        if (leftIsCast || rightIsCast) {
            CastExpression.checkNeedForArgumentCasts(scope, operator, operatorSignature, this.left, leftTypeID,
                                                     leftIsCast, this.right, rightTypeID, rightIsCast);
        }
        // compute the constant when valid
        computeConstant(scope, leftTypeID, rightTypeID);
        return this.resolvedType;
    }

    public void traverse(ASTVisitor visitor, BlockScope scope) {
        if (visitor.visit(this, scope)) {
            this.left.traverse(visitor, scope);
            this.right.traverse(visitor, scope);
        }
        visitor.endVisit(this, scope);
    }
}
