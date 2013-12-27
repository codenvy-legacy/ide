/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
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
import com.codenvy.ide.ext.java.jdt.internal.compiler.flow.LoopingFlowContext;
import com.codenvy.ide.ext.java.jdt.internal.compiler.flow.UnconditionalFlowInfo;
import com.codenvy.ide.ext.java.jdt.internal.compiler.impl.Constant;
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.ArrayBinding;
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.Binding;
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.BlockScope;
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.LocalVariableBinding;
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.ReferenceBinding;
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.TypeBinding;

public class ForeachStatement extends Statement {

    public LocalDeclaration elementVariable;

    public int elementVariableImplicitWidening = -1;

    public Expression collection;

    public Statement action;

    // set the kind of foreach
    private int kind;

    // possible kinds of iterating behavior
    private static final int ARRAY = 0;

    private static final int RAW_ITERABLE = 1;

    private static final int GENERIC_ITERABLE = 2;

    private TypeBinding iteratorReceiverType;

    private TypeBinding collectionElementType;

    // loop labels
    private BranchLabel breakLabel;

    private BranchLabel continueLabel;

    public BlockScope scope;

    // secret variables for codegen
    public LocalVariableBinding indexVariable;

    public LocalVariableBinding collectionVariable; // to store the collection expression value

    public LocalVariableBinding maxVariable;

    // secret variable names
    private static final char[] SecretIteratorVariableName = " iterator".toCharArray(); //$NON-NLS-1$

    private static final char[] SecretIndexVariableName = " index".toCharArray(); //$NON-NLS-1$

    private static final char[] SecretCollectionVariableName = " collection".toCharArray(); //$NON-NLS-1$

    private static final char[] SecretMaxVariableName = " max".toCharArray(); //$NON-NLS-1$

    int postCollectionInitStateIndex = -1;

    int mergedInitStateIndex = -1;

    public ForeachStatement(LocalDeclaration elementVariable, int start) {

        this.elementVariable = elementVariable;
        this.sourceStart = start;
        this.kind = -1;
    }

    @Override
    public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
        // initialize break and continue labels
        this.breakLabel = new BranchLabel();
        this.continueLabel = new BranchLabel();
        int initialComplaintLevel =
                (flowInfo.reachMode() & FlowInfo.UNREACHABLE) != 0 ? Statement.COMPLAINED_FAKE_REACHABLE
                                                                   : Statement.NOT_COMPLAINED;

        // process the element variable and collection
        this.collection.checkNPE(currentScope, flowContext, flowInfo);
        flowInfo = this.elementVariable.analyseCode(this.scope, flowContext, flowInfo);
        FlowInfo condInfo = this.collection.analyseCode(this.scope, flowContext, flowInfo.copy());

        // element variable will be assigned when iterating
        condInfo.markAsDefinitelyAssigned(this.elementVariable.binding);

        this.postCollectionInitStateIndex = currentScope.methodScope().recordInitializationStates(condInfo);

        // process the action
        LoopingFlowContext loopingContext =
                new LoopingFlowContext(flowContext, flowInfo, this, this.breakLabel, this.continueLabel, this.scope);
        UnconditionalFlowInfo actionInfo = condInfo.nullInfoLessUnconditionalCopy();
        actionInfo.markAsDefinitelyUnknown(this.elementVariable.binding);
        FlowInfo exitBranch;
        if (!(this.action == null ||
              (this.action.isEmptyBlock() && currentScope.compilerOptions().complianceLevel <= ClassFileConstants.JDK1_3))) {

            if (this.action.complainIfUnreachable(actionInfo, this.scope, initialComplaintLevel) < Statement.COMPLAINED_UNREACHABLE) {
                actionInfo = this.action.analyseCode(this.scope, loopingContext, actionInfo).unconditionalCopy();
            }

            // code generation can be optimized when no need to continue in the loop
            exitBranch = flowInfo.unconditionalCopy().addInitializationsFrom(condInfo.initsWhenFalse());
            // TODO (maxime) no need to test when false: can optimize (same for action being unreachable above)
            if ((actionInfo.tagBits & loopingContext.initsOnContinue.tagBits & FlowInfo.UNREACHABLE_OR_DEAD) != 0) {
                this.continueLabel = null;
            } else {
                actionInfo = actionInfo.mergedWith(loopingContext.initsOnContinue);
                loopingContext.complainOnDeferredFinalChecks(this.scope, actionInfo);
                exitBranch.addPotentialInitializationsFrom(actionInfo);
            }
        } else {
            exitBranch = condInfo.initsWhenFalse();
        }

        // we need the variable to iterate the collection even if the
        // element variable is not used
        final boolean hasEmptyAction =
                this.action == null || this.action.isEmptyBlock() || ((this.action.bits & IsUsefulEmptyStatement) != 0);

        switch (this.kind) {
            case ARRAY:
                if (!hasEmptyAction || this.elementVariable.binding.resolvedPosition != -1) {
                    this.collectionVariable.useFlag = LocalVariableBinding.USED;
                    if (this.continueLabel != null) {
                        this.indexVariable.useFlag = LocalVariableBinding.USED;
                        this.maxVariable.useFlag = LocalVariableBinding.USED;
                    }
                }
                break;
            case RAW_ITERABLE:
            case GENERIC_ITERABLE:
                this.indexVariable.useFlag = LocalVariableBinding.USED;
                break;
        }
        //end of loop
        loopingContext.complainOnDeferredNullChecks(currentScope, actionInfo);

        FlowInfo mergedInfo =
                FlowInfo.mergedOptimizedBranches((loopingContext.initsOnBreak.tagBits & FlowInfo.UNREACHABLE) != 0
                                                 ? loopingContext.initsOnBreak
                                                 : flowInfo.addInitializationsFrom(loopingContext.initsOnBreak),
                                                 // recover upstream null info
                                                 false, exitBranch, false, true /*for(;;){}while(true); unreachable(); */);
        this.mergedInitStateIndex = currentScope.methodScope().recordInitializationStates(mergedInfo);
        return mergedInfo;
    }

    /**
     * For statement code generation
     *
     * @param currentScope
     *         com.codenvy.ide.java.client.internal.compiler.lookup.BlockScope
     * @param codeStream
     *         com.codenvy.ide.java.client.internal.compiler.codegen.CodeStream
     */
    @Override
    public void generateCode(BlockScope currentScope) {

        if ((this.bits & IsReachable) == 0) {
            return;
        }
        final boolean hasEmptyAction =
                this.action == null || this.action.isEmptyBlock() || ((this.action.bits & IsUsefulEmptyStatement) != 0);

        if (hasEmptyAction && this.elementVariable.binding.resolvedPosition == -1 && this.kind == ARRAY) {
            this.collection.generateCode(this.scope, false);
            return;
        }

        // generate the initializations
        switch (this.kind) {
            case ARRAY:
                this.collection.generateCode(this.scope, true);
                break;
            case RAW_ITERABLE:
            case GENERIC_ITERABLE:
                this.collection.generateCode(this.scope, true);
                break;
        }
        // label management
        BranchLabel actionLabel = new BranchLabel();
        actionLabel.tagBits |= BranchLabel.USED;
        BranchLabel conditionLabel = new BranchLabel();
        conditionLabel.tagBits |= BranchLabel.USED;


        if (!hasEmptyAction) {
            this.action.generateCode(this.scope);
        }
    }

    @Override
    public StringBuffer printStatement(int indent, StringBuffer output) {

        printIndent(indent, output).append("for ("); //$NON-NLS-1$
        this.elementVariable.printAsExpression(0, output);
        output.append(" : ");//$NON-NLS-1$
        if (this.collection != null) {
            this.collection.print(0, output).append(") "); //$NON-NLS-1$
        } else {
            output.append(')');
        }
        //block
        if (this.action == null) {
            output.append(';');
        } else {
            output.append('\n');
            this.action.printStatement(indent + 1, output);
        }
        return output;
    }

    @Override
    public void resolve(BlockScope upperScope) {
        // use the scope that will hold the init declarations
        this.scope = new BlockScope(upperScope);
        this.elementVariable.resolve(this.scope); // collection expression can see itemVariable
        TypeBinding elementType = this.elementVariable.type.resolvedType;
        TypeBinding collectionType = this.collection == null ? null : this.collection.resolveType(this.scope);

        TypeBinding expectedCollectionType = null;
        if (elementType != null && collectionType != null) {
            boolean isTargetJsr14 = this.scope.compilerOptions().targetJDK == ClassFileConstants.JDK1_4;
            if (collectionType.isArrayType()) { // for(E e : E[])
                this.kind = ARRAY;
                this.collectionElementType = ((ArrayBinding)collectionType).elementsType();
                if (!this.collectionElementType.isCompatibleWith(elementType)
                    && !this.scope.isBoxingCompatibleWith(this.collectionElementType, elementType)) {
                    this.scope.problemReporter().notCompatibleTypesErrorInForeach(this.collection,
                                                                                  this.collectionElementType, elementType);
                } else if (this.collectionElementType
                        .needsUncheckedConversion(elementType)) { // https://bugs.eclipse.org/bugs/show_bug.cgi?id=321085
                    this.scope.problemReporter().unsafeTypeConversion(this.collection, collectionType,
                                                                      upperScope.createArrayType(elementType, 1));
                }
                // in case we need to do a conversion
                int compileTimeTypeID = this.collectionElementType.id;
                if (elementType.isBaseType()) {
                    this.collection.computeConversion(this.scope, collectionType, collectionType);
                    if (!this.collectionElementType.isBaseType()) {
                        compileTimeTypeID = this.scope.environment().computeBoxingType(this.collectionElementType).id;
                        this.elementVariableImplicitWidening = UNBOXING;
                        if (elementType.isBaseType()) {
                            this.elementVariableImplicitWidening |= (elementType.id << 4) + compileTimeTypeID;
                            this.scope.problemReporter().autoboxing(this.collection, this.collectionElementType, elementType);
                        }
                    } else {
                        this.elementVariableImplicitWidening = (elementType.id << 4) + compileTimeTypeID;
                    }
                } else if (this.collectionElementType.isBaseType()) {
                    this.collection.computeConversion(this.scope, collectionType, collectionType);
                    int boxedID = this.scope.environment().computeBoxingType(this.collectionElementType).id;
                    this.elementVariableImplicitWidening =
                            BOXING | (compileTimeTypeID << 4) | compileTimeTypeID; // use primitive type in implicit conversion
                    compileTimeTypeID = boxedID;
                    this.scope.problemReporter().autoboxing(this.collection, this.collectionElementType, elementType);
                } else {
                    expectedCollectionType = upperScope.createArrayType(elementType, 1);
                    this.collection.computeConversion(this.scope, expectedCollectionType, collectionType);
                }
            } else if (collectionType instanceof ReferenceBinding) {
                ReferenceBinding iterableType =
                        ((ReferenceBinding)collectionType)
                                .findSuperTypeOriginatingFrom(T_JavaLangIterable, false /*Iterable is not a class*/);
                if (iterableType == null && isTargetJsr14) {
                    iterableType =
                            ((ReferenceBinding)collectionType)
                                    .findSuperTypeOriginatingFrom(T_JavaUtilCollection, false /*Iterable is not a class*/);
                }
                checkIterable:
                {
                    if (iterableType == null) {
                        break checkIterable;
                    }

                    this.iteratorReceiverType = collectionType.erasure();
                    if (isTargetJsr14) {
                        if (((ReferenceBinding)this.iteratorReceiverType).findSuperTypeOriginatingFrom(T_JavaUtilCollection,
                                                                                                       false) == null) {
                            this.iteratorReceiverType = iterableType; // handle indirect inheritance thru variable secondary bound
                            this.collection.computeConversion(this.scope, iterableType, collectionType);
                        } else {
                            this.collection.computeConversion(this.scope, collectionType, collectionType);
                        }
                    } else if (((ReferenceBinding)this.iteratorReceiverType).findSuperTypeOriginatingFrom(T_JavaLangIterable,
                                                                                                          false) == null) {
                        this.iteratorReceiverType = iterableType; // handle indirect inheritance thru variable secondary bound
                        this.collection.computeConversion(this.scope, iterableType, collectionType);
                    } else {
                        this.collection.computeConversion(this.scope, collectionType, collectionType);
                    }

                    TypeBinding[] arguments = null;
                    switch (iterableType.kind()) {
                        case Binding.RAW_TYPE: // for(Object o : Iterable)
                            this.kind = RAW_ITERABLE;
                            this.collectionElementType = this.scope.getJavaLangObject();
                            if (!this.collectionElementType.isCompatibleWith(elementType)
                                && !this.scope.isBoxingCompatibleWith(this.collectionElementType, elementType)) {
                                this.scope.problemReporter().notCompatibleTypesErrorInForeach(this.collection,
                                                                                              this.collectionElementType, elementType);
                            }
                            // no conversion needed as only for reference types
                            break checkIterable;

                        case Binding.GENERIC_TYPE: // for (T t : Iterable<T>) - in case used inside Iterable itself
                            arguments = iterableType.typeVariables();
                            break;

                        case Binding.PARAMETERIZED_TYPE: // for(E e : Iterable<E>)
                            arguments = ((ParameterizedTypeBinding)iterableType).arguments;
                            break;

                        default:
                            break checkIterable;
                    }
                    // generic or parameterized case
                    if (arguments.length != 1) {
                        break checkIterable; // per construction can only be one
                    }
                    this.kind = GENERIC_ITERABLE;

                    this.collectionElementType = arguments[0];
                    if (!this.collectionElementType.isCompatibleWith(elementType)
                        && !this.scope.isBoxingCompatibleWith(this.collectionElementType, elementType)) {
                        this.scope.problemReporter().notCompatibleTypesErrorInForeach(this.collection,
                                                                                      this.collectionElementType, elementType);
                    }
                    int compileTimeTypeID = this.collectionElementType.id;
                    // no conversion needed as only for reference types
                    if (elementType.isBaseType()) {
                        if (!this.collectionElementType.isBaseType()) {
                            compileTimeTypeID = this.scope.environment().computeBoxingType(this.collectionElementType).id;
                            this.elementVariableImplicitWidening = UNBOXING;
                            if (elementType.isBaseType()) {
                                this.elementVariableImplicitWidening |= (elementType.id << 4) + compileTimeTypeID;
                            }
                        } else {
                            this.elementVariableImplicitWidening = (elementType.id << 4) + compileTimeTypeID;
                        }
                    } else {
                        if (this.collectionElementType.isBaseType()) {
                            this.elementVariableImplicitWidening =
                                    BOXING | (compileTimeTypeID << 4) | compileTimeTypeID; // use primitive type in implicit conversion
                        }
                    }
                }
            }
            switch (this.kind) {
                case ARRAY:
                    // allocate #index secret variable (of type int)
                    this.indexVariable =
                            new LocalVariableBinding(SecretIndexVariableName, TypeBinding.INT, ClassFileConstants.AccDefault,
                                                     false);
                    this.scope.addLocalVariable(this.indexVariable);
                    this.indexVariable.setConstant(Constant.NotAConstant); // not inlinable
                    // allocate #max secret variable
                    this.maxVariable =
                            new LocalVariableBinding(SecretMaxVariableName, TypeBinding.INT, ClassFileConstants.AccDefault, false);
                    this.scope.addLocalVariable(this.maxVariable);
                    this.maxVariable.setConstant(Constant.NotAConstant); // not inlinable
                    // add #array secret variable (of collection type)
                    if (expectedCollectionType == null) {
                        this.collectionVariable =
                                new LocalVariableBinding(SecretCollectionVariableName, collectionType,
                                                         ClassFileConstants.AccDefault, false);
                    } else {
                        this.collectionVariable =
                                new LocalVariableBinding(SecretCollectionVariableName, expectedCollectionType,
                                                         ClassFileConstants.AccDefault, false);
                    }
                    this.scope.addLocalVariable(this.collectionVariable);
                    this.collectionVariable.setConstant(Constant.NotAConstant); // not inlinable
                    break;
                case RAW_ITERABLE:
                case GENERIC_ITERABLE:
                    // allocate #index secret variable (of type Iterator)
                    this.indexVariable =
                            new LocalVariableBinding(SecretIteratorVariableName, this.scope.getJavaUtilIterator(),
                                                     ClassFileConstants.AccDefault, false);
                    this.scope.addLocalVariable(this.indexVariable);
                    this.indexVariable.setConstant(Constant.NotAConstant); // not inlinable
                    break;
                default:
                    if (isTargetJsr14) {
                        this.scope.problemReporter().invalidTypeForCollectionTarget14(this.collection);
                    } else {
                        this.scope.problemReporter().invalidTypeForCollection(this.collection);
                    }
            }
        }
        if (this.action != null) {
            this.action.resolve(this.scope);
        }
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope blockScope) {

        if (visitor.visit(this, blockScope)) {
            this.elementVariable.traverse(visitor, this.scope);
            if (this.collection != null) {
                this.collection.traverse(visitor, this.scope);
            }
            if (this.action != null) {
                this.action.traverse(visitor, this.scope);
            }
        }
        visitor.endVisit(this, blockScope);
    }
}
