/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Stephan Herrmann - Contribution for bug 332637 - Dead Code detection removing code that isn't dead
 *******************************************************************************/
package com.codenvy.ide.ext.java.worker.internal.compiler.ast;

import com.codenvy.ide.ext.java.worker.core.compiler.CharOperation;
import com.codenvy.ide.ext.java.worker.internal.compiler.ASTVisitor;
import com.codenvy.ide.ext.java.worker.internal.compiler.ClassFileConstants;
import com.codenvy.ide.ext.java.worker.internal.compiler.codegen.BranchLabel;
import com.codenvy.ide.ext.java.worker.internal.compiler.codegen.ConstantPool;
import com.codenvy.ide.ext.java.worker.internal.compiler.flow.*;
import com.codenvy.ide.ext.java.worker.internal.compiler.impl.Constant;
import com.codenvy.ide.ext.java.worker.internal.compiler.lookup.*;

public class TryStatement extends SubRoutineStatement {

    static final char[] SECRET_RETURN_ADDRESS_NAME = " returnAddress".toCharArray(); //$NON-NLS-1$

    static final char[] SECRET_ANY_HANDLER_NAME = " anyExceptionHandler".toCharArray(); //$NON-NLS-1$

    static final char[] SECRET_PRIMARY_EXCEPTION_VARIABLE_NAME = " primaryException".toCharArray(); //$NON-NLS-1$

    static final char[] SECRET_CAUGHT_THROWABLE_VARIABLE_NAME = " caughtThrowable".toCharArray(); //$NON-NLS-1$;

    static final char[] SECRET_RETURN_VALUE_NAME = " returnValue".toCharArray(); //$NON-NLS-1$

    private static LocalDeclaration[] NO_RESOURCES = new LocalDeclaration[0];

    public LocalDeclaration[] resources = NO_RESOURCES;

    public Block tryBlock;

    public Block[] catchBlocks;

    public Argument[] catchArguments;

    // should rename into subRoutineComplete to be set to false by default

    public Block finallyBlock;

    BlockScope scope;

    public UnconditionalFlowInfo subRoutineInits;

    ReferenceBinding[] caughtExceptionTypes;

    boolean[] catchExits;

    BranchLabel subRoutineStartLabel;

    public LocalVariableBinding anyExceptionVariable, returnAddressVariable, secretReturnValue;

    private static final int NO_FINALLY = 0; // no finally block

    private static final int FINALLY_SUBROUTINE = 1; // finally is generated as a subroutine (using jsr/ret bytecodes)

    private static final int FINALLY_DOES_NOT_COMPLETE = 2; // non returning finally is optimized with only one instance of finally block

    private static final int FINALLY_INLINE = 3; // finally block must be inlined since cannot use jsr/ret bytecodes >1.5

    // for local variables table attributes
    int mergedInitStateIndex = -1;

    int preTryInitStateIndex = -1;

    int[] postResourcesInitStateIndexes;

    int naturalExitMergeInitStateIndex = -1;

    int[] catchExitInitStateIndexes;

    private LocalVariableBinding primaryExceptionVariable;

    private LocalVariableBinding caughtThrowableVariable;

    private int[] caughtExceptionsCatchBlocks;

    public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {

        // Consider the try block and catch block so as to compute the intersection of initializations and
        // the minimum exit relative depth amongst all of them. Then consider the subroutine, and append its
        // initialization to the try/catch ones, if the subroutine completes normally. If the subroutine does not
        // complete, then only keep this result for the rest of the analysis

        // process the finally block (subroutine) - create a context for the subroutine

        this.preTryInitStateIndex = currentScope.methodScope().recordInitializationStates(flowInfo);

        if (this.anyExceptionVariable != null) {
            this.anyExceptionVariable.useFlag = LocalVariableBinding.USED;
        }
        if (this.primaryExceptionVariable != null) {
            this.primaryExceptionVariable.useFlag = LocalVariableBinding.USED;
        }
        if (this.caughtThrowableVariable != null) {
            this.caughtThrowableVariable.useFlag = LocalVariableBinding.USED;
        }
        if (this.returnAddressVariable != null) { // TODO (philippe) if subroutine is escaping, unused
            this.returnAddressVariable.useFlag = LocalVariableBinding.USED;
        }
        int resourcesLength = this.resources.length;
        if (resourcesLength > 0) {
            this.postResourcesInitStateIndexes = new int[resourcesLength];
        }

        if (this.subRoutineStartLabel == null) {
            // no finally block -- this is a simplified copy of the else part
            // process the try block in a context handling the local exceptions.
            ExceptionHandlingFlowContext handlingContext =
                    new ExceptionHandlingFlowContext(flowContext, this, this.caughtExceptionTypes,
                                                     this.caughtExceptionsCatchBlocks, this.catchArguments, null, this.scope,
                                                     flowInfo.unconditionalInits());
            handlingContext.initsOnFinally = new NullInfoRegistry(flowInfo.unconditionalInits());
            // only try blocks initialize that member - may consider creating a
            // separate class if needed

            for (int i = 0; i < resourcesLength; i++) {
                flowInfo = this.resources[i].analyseCode(currentScope, handlingContext, flowInfo.copy());
                this.postResourcesInitStateIndexes[i] = currentScope.methodScope().recordInitializationStates(flowInfo);
                this.resources[i].binding.useFlag = LocalVariableBinding.USED; // Is implicitly used anyways.
                TypeBinding type = this.resources[i].binding.type;
                if (type != null && type.isValidBinding()) {
                    ReferenceBinding binding = (ReferenceBinding)type;
                    MethodBinding closeMethod =
                            binding.getExactMethod(ConstantPool.Close, new TypeBinding[0],
                                                   this.scope.compilationUnitScope()); // scope needs to be tighter
                    if (closeMethod != null && closeMethod.returnType.id == TypeIds.T_void) {
                        ReferenceBinding[] thrownExceptions = closeMethod.thrownExceptions;
                        for (int j = 0, length = thrownExceptions.length; j < length; j++) {
                            handlingContext.checkExceptionHandlers(thrownExceptions[j], this.resources[i], flowInfo,
                                                                   currentScope, true);
                        }
                    }
                }
            }
            FlowInfo tryInfo;
            if (this.tryBlock.isEmptyBlock()) {
                tryInfo = flowInfo;
            } else {
                tryInfo = this.tryBlock.analyseCode(currentScope, handlingContext, flowInfo.copy());
                if ((tryInfo.tagBits & FlowInfo.UNREACHABLE_OR_DEAD) != 0)
                    this.bits |= ASTNode.IsTryBlockExiting;
            }

            // check unreachable catch blocks
            handlingContext.complainIfUnusedExceptionHandlers(this.scope, this);

            // process the catch blocks - computing the minimal exit depth amongst try/catch
            if (this.catchArguments != null) {
                int catchCount;
                this.catchExits = new boolean[catchCount = this.catchBlocks.length];
                this.catchExitInitStateIndexes = new int[catchCount];
                for (int i = 0; i < catchCount; i++) {
                    // keep track of the inits that could potentially have led to this exception handler (for final assignments diagnosis)
                    FlowInfo catchInfo;
                    if (isUncheckedCatchBlock(i)) {
                        catchInfo =
                                handlingContext.initsOnFinally.mitigateNullInfoOf(flowInfo.unconditionalCopy()
                                                                                          .addPotentialInitializationsFrom(
                                                                                                  handlingContext.initsOnException(i))
                                                                                          .addPotentialInitializationsFrom(tryInfo)
                                                                                          .addPotentialInitializationsFrom(
                                                                                                  handlingContext.initsOnReturn));
                    } else {
                        FlowInfo initsOnException = handlingContext.initsOnException(i);
                        catchInfo =
                                flowInfo.nullInfoLessUnconditionalCopy().addPotentialInitializationsFrom(initsOnException)
                                        .addNullInfoFrom(initsOnException)
                                                // null info only from here, this is the only way to enter the catch block
                                        .addPotentialInitializationsFrom(tryInfo.nullInfoLessUnconditionalCopy())
                                        .addPotentialInitializationsFrom(handlingContext.initsOnReturn.nullInfoLessUnconditionalCopy());
                    }

                    // catch var is always set
                    LocalVariableBinding catchArg = this.catchArguments[i].binding;
                    catchInfo.markAsDefinitelyAssigned(catchArg);
                    catchInfo.markAsDefinitelyNonNull(catchArg);
               /*
               "If we are about to consider an unchecked exception handler, potential inits may have occured inside
               the try block that need to be detected , e.g.
               try { x = 1; throwSomething();} catch(Exception e){ x = 2} "
               "(uncheckedExceptionTypes notNil and: [uncheckedExceptionTypes at: index])
               ifTrue: [catchInits addPotentialInitializationsFrom: tryInits]."
               */
                    if (this.tryBlock.statements == null &&
                        this.resources == NO_RESOURCES) { // https://bugs.eclipse.org/bugs/show_bug.cgi?id=350579
                        catchInfo.setReachMode(FlowInfo.UNREACHABLE_OR_DEAD);
                    }
                    catchInfo = this.catchBlocks[i].analyseCode(currentScope, flowContext, catchInfo);
                    this.catchExitInitStateIndexes[i] = currentScope.methodScope().recordInitializationStates(catchInfo);
                    this.catchExits[i] = (catchInfo.tagBits & FlowInfo.UNREACHABLE_OR_DEAD) != 0;
                    tryInfo = tryInfo.mergedWith(catchInfo.unconditionalInits());
                }
            }
            this.mergedInitStateIndex = currentScope.methodScope().recordInitializationStates(tryInfo);

            // chain up null info registry
            if (flowContext.initsOnFinally != null) {
                flowContext.initsOnFinally.add(handlingContext.initsOnFinally);
            }

            return tryInfo;
        } else {
            InsideSubRoutineFlowContext insideSubContext;
            FinallyFlowContext finallyContext;
            UnconditionalFlowInfo subInfo;
            // analyse finally block first
            insideSubContext = new InsideSubRoutineFlowContext(flowContext, this);

            subInfo =
                    this.finallyBlock.analyseCode(currentScope,
                                                  finallyContext = new FinallyFlowContext(flowContext, this.finallyBlock),
                                                  flowInfo.nullInfoLessUnconditionalCopy()).unconditionalInits();
            if (subInfo == FlowInfo.DEAD_END) {
                this.bits |= ASTNode.IsSubRoutineEscaping;
                this.scope.problemReporter().finallyMustCompleteNormally(this.finallyBlock);
            }
            this.subRoutineInits = subInfo;
            // process the try block in a context handling the local exceptions.
            ExceptionHandlingFlowContext handlingContext =
                    new ExceptionHandlingFlowContext(insideSubContext, this, this.caughtExceptionTypes,
                                                     this.caughtExceptionsCatchBlocks, this.catchArguments, null, this.scope,
                                                     flowInfo.unconditionalInits());
            handlingContext.initsOnFinally = new NullInfoRegistry(flowInfo.unconditionalInits());
            // only try blocks initialize that member - may consider creating a
            // separate class if needed

            for (int i = 0; i < resourcesLength; i++) {
                flowInfo = this.resources[i].analyseCode(currentScope, handlingContext, flowInfo.copy());
                this.postResourcesInitStateIndexes[i] = currentScope.methodScope().recordInitializationStates(flowInfo);
                this.resources[i].binding.useFlag = LocalVariableBinding.USED; // Is implicitly used anyways.
                TypeBinding type = this.resources[i].binding.type;
                if (type != null && type.isValidBinding()) {
                    ReferenceBinding binding = (ReferenceBinding)type;
                    MethodBinding closeMethod =
                            binding.getExactMethod(ConstantPool.Close, new TypeBinding[0],
                                                   this.scope.compilationUnitScope()); // scope needs to be tighter
                    if (closeMethod != null && closeMethod.returnType.id == TypeIds.T_void) {
                        ReferenceBinding[] thrownExceptions = closeMethod.thrownExceptions;
                        for (int j = 0, length = thrownExceptions.length; j < length; j++) {
                            handlingContext.checkExceptionHandlers(thrownExceptions[j], this.resources[j], flowInfo,
                                                                   currentScope);
                        }
                    }
                }
            }
            FlowInfo tryInfo;
            if (this.tryBlock.isEmptyBlock()) {
                tryInfo = flowInfo;
            } else {
                tryInfo = this.tryBlock.analyseCode(currentScope, handlingContext, flowInfo.copy());
                if ((tryInfo.tagBits & FlowInfo.UNREACHABLE_OR_DEAD) != 0)
                    this.bits |= ASTNode.IsTryBlockExiting;
            }

            // check unreachable catch blocks
            handlingContext.complainIfUnusedExceptionHandlers(this.scope, this);

            // process the catch blocks - computing the minimal exit depth amongst try/catch
            if (this.catchArguments != null) {
                int catchCount;
                this.catchExits = new boolean[catchCount = this.catchBlocks.length];
                this.catchExitInitStateIndexes = new int[catchCount];
                for (int i = 0; i < catchCount; i++) {
                    // keep track of the inits that could potentially have led to this exception handler (for final assignments diagnosis)
                    FlowInfo catchInfo;
                    if (isUncheckedCatchBlock(i)) {
                        catchInfo =
                                handlingContext.initsOnFinally.mitigateNullInfoOf(flowInfo.unconditionalCopy()
                                                                                          .addPotentialInitializationsFrom(
                                                                                                  handlingContext.initsOnException(i))
                                                                                          .addPotentialInitializationsFrom(tryInfo)
                                                                                          .addPotentialInitializationsFrom(
                                                                                                  handlingContext.initsOnReturn));
                    } else {
                        FlowInfo initsOnException = handlingContext.initsOnException(i);
                        catchInfo =
                                flowInfo.nullInfoLessUnconditionalCopy().addPotentialInitializationsFrom(initsOnException)
                                        .addNullInfoFrom(initsOnException)
                                                // null info only from here, this is the only way to enter the catch block
                                        .addPotentialInitializationsFrom(tryInfo.nullInfoLessUnconditionalCopy())
                                        .addPotentialInitializationsFrom(handlingContext.initsOnReturn.nullInfoLessUnconditionalCopy());
                    }

                    // catch var is always set
                    LocalVariableBinding catchArg = this.catchArguments[i].binding;
                    catchInfo.markAsDefinitelyAssigned(catchArg);
                    catchInfo.markAsDefinitelyNonNull(catchArg);
               /*
               "If we are about to consider an unchecked exception handler, potential inits may have occured inside
               the try block that need to be detected , e.g.
               try { x = 1; throwSomething();} catch(Exception e){ x = 2} "
               "(uncheckedExceptionTypes notNil and: [uncheckedExceptionTypes at: index])
               ifTrue: [catchInits addPotentialInitializationsFrom: tryInits]."
               */
                    if (this.tryBlock.statements == null &&
                        this.resources == NO_RESOURCES) { // https://bugs.eclipse.org/bugs/show_bug.cgi?id=350579
                        catchInfo.setReachMode(FlowInfo.UNREACHABLE_OR_DEAD);
                    }
                    catchInfo = this.catchBlocks[i].analyseCode(currentScope, insideSubContext, catchInfo);
                    this.catchExitInitStateIndexes[i] = currentScope.methodScope().recordInitializationStates(catchInfo);
                    this.catchExits[i] = (catchInfo.tagBits & FlowInfo.UNREACHABLE_OR_DEAD) != 0;
                    tryInfo = tryInfo.mergedWith(catchInfo.unconditionalInits());
                }
            }
            // we also need to check potential multiple assignments of final variables inside the finally block
            // need to include potential inits from returns inside the try/catch parts - 1GK2AOF
            finallyContext.complainOnDeferredChecks(
                    handlingContext.initsOnFinally.mitigateNullInfoOf((tryInfo.tagBits & FlowInfo.UNREACHABLE) == 0 ? flowInfo
                            .unconditionalCopy().addPotentialInitializationsFrom(tryInfo).
                                    // lighten the influence of the try block, which may have
                                            // exited at any point
                                            addPotentialInitializationsFrom(insideSubContext.initsOnReturn)
                                                                                                                    : insideSubContext
                                                                              .initsOnReturn),
                    currentScope);

            // chain up null info registry
            if (flowContext.initsOnFinally != null) {
                flowContext.initsOnFinally.add(handlingContext.initsOnFinally);
            }

            this.naturalExitMergeInitStateIndex = currentScope.methodScope().recordInitializationStates(tryInfo);
            if (subInfo == FlowInfo.DEAD_END) {
                this.mergedInitStateIndex = currentScope.methodScope().recordInitializationStates(subInfo);
                return subInfo;
            } else {
                FlowInfo mergedInfo = tryInfo.addInitializationsFrom(subInfo);
                this.mergedInitStateIndex = currentScope.methodScope().recordInitializationStates(mergedInfo);
                return mergedInfo;
            }
        }
    }

    // Return true if the catch block corresponds to an unchecked exception making allowance for multi-catch blocks.
    private boolean isUncheckedCatchBlock(int catchBlock) {
        if (this.caughtExceptionsCatchBlocks == null) {
            return this.caughtExceptionTypes[catchBlock].isUncheckedException(true);
        }
        for (int i = 0, length = this.caughtExceptionsCatchBlocks.length; i < length; i++) {
            if (this.caughtExceptionsCatchBlocks[i] == catchBlock) {
                if (this.caughtExceptionTypes[i].isUncheckedException(true)) {
                    return true;
                }
            }
        }
        return false;
    }

    private int finallyMode() {
        if (this.subRoutineStartLabel == null) {
            return NO_FINALLY;
        } else if (isSubRoutineEscaping()) {
            return FINALLY_DOES_NOT_COMPLETE;
        } else if (this.scope.compilerOptions().inlineJsrBytecode) {
            return FINALLY_INLINE;
        } else {
            return FINALLY_SUBROUTINE;
        }
    }

    /**
     * Try statement code generation with or without jsr bytecode use
     * post 1.5 target level, cannot use jsr bytecode, must instead inline finally block
     * returnAddress is only allocated if jsr is allowed
     */
    public void generateCode(BlockScope currentScope) {
        if ((this.bits & ASTNode.IsReachable) == 0) {
            return;
        }
        finallyMode();

        // generate the try block
        int resourceCount = this.resources.length;
        if (resourceCount > 0) {
            // Please see https://bugs.eclipse.org/bugs/show_bug.cgi?id=338402#c16
            for (int i = 0; i <= resourceCount; i++) {
                // put null for the exception type to treat them as any exception handlers (equivalent to a try/finally)
                if (i < resourceCount) {
                    this.resources[i].generateCode(this.scope); // Initialize resources ...
                }
            }
        }
        this.tryBlock.generateCode(this.scope);
        // flag telling if some bytecodes were issued inside the try block
    }

    /** @see SubRoutineStatement#generateSubRoutineInvocation(BlockScope, CodeStream, Object, int, LocalVariableBinding) */
    public boolean generateSubRoutineInvocation(BlockScope currentScope, Object targetLocation, int stateIndex,
                                                LocalVariableBinding secretLocal) {

        return false;
    }

    public boolean isSubRoutineEscaping() {
        return (this.bits & ASTNode.IsSubRoutineEscaping) != 0;
    }

    public StringBuffer printStatement(int indent, StringBuffer output) {
        int length = this.resources.length;
        printIndent(indent, output).append("try" + (length == 0 ? "\n" : " (")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        for (int i = 0; i < length; i++) {
            this.resources[i].printAsExpression(0, output);
            if (i != length - 1) {
                output.append(";\n"); //$NON-NLS-1$
                printIndent(indent + 2, output);
            }
        }
        if (length > 0) {
            output.append(")\n"); //$NON-NLS-1$
        }
        this.tryBlock.printStatement(indent + 1, output);

        //catches
        if (this.catchBlocks != null)
            for (int i = 0; i < this.catchBlocks.length; i++) {
                output.append('\n');
                printIndent(indent, output).append("catch ("); //$NON-NLS-1$
                this.catchArguments[i].print(0, output).append(")\n"); //$NON-NLS-1$
                this.catchBlocks[i].printStatement(indent + 1, output);
            }
        //finally
        if (this.finallyBlock != null) {
            output.append('\n');
            printIndent(indent, output).append("finally\n"); //$NON-NLS-1$
            this.finallyBlock.printStatement(indent + 1, output);
        }
        return output;
    }

    public void resolve(BlockScope upperScope) {
        // special scope for secret locals optimization.
        this.scope = new BlockScope(upperScope);

        BlockScope finallyScope = null;
        BlockScope resourceManagementScope = null; // Single scope to hold all resources and additional secret variables.
        int resourceCount = this.resources.length;
        if (resourceCount > 0) {
            resourceManagementScope = new BlockScope(this.scope);
            this.primaryExceptionVariable =
                    new LocalVariableBinding(TryStatement.SECRET_PRIMARY_EXCEPTION_VARIABLE_NAME,
                                             this.scope.getJavaLangThrowable(), ClassFileConstants.AccDefault, false);
            resourceManagementScope.addLocalVariable(this.primaryExceptionVariable);
            this.primaryExceptionVariable.setConstant(Constant.NotAConstant); // not inlinable
            this.caughtThrowableVariable =
                    new LocalVariableBinding(TryStatement.SECRET_CAUGHT_THROWABLE_VARIABLE_NAME,
                                             this.scope.getJavaLangThrowable(), ClassFileConstants.AccDefault, false);
            resourceManagementScope.addLocalVariable(this.caughtThrowableVariable);
            this.caughtThrowableVariable.setConstant(Constant.NotAConstant); // not inlinable
        }
        for (int i = 0; i < resourceCount; i++) {
            this.resources[i].resolve(resourceManagementScope);
            LocalVariableBinding localVariableBinding = this.resources[i].binding;
            if (localVariableBinding != null && localVariableBinding.isValidBinding()) {
                localVariableBinding.modifiers |= ClassFileConstants.AccFinal;
                localVariableBinding.tagBits |= TagBits.IsResource;
                TypeBinding resourceType = localVariableBinding.type;
                if (resourceType instanceof ReferenceBinding) {
                    if (resourceType
                                .findSuperTypeOriginatingFrom(TypeIds.T_JavaLangAutoCloseable, false /*AutoCloseable is not a class*/) ==
                        null
                        && resourceType.isValidBinding()) {
                        upperScope.problemReporter()
                                  .resourceHasToImplementAutoCloseable(resourceType, this.resources[i].type);
                        localVariableBinding.type =
                                new ProblemReferenceBinding(CharOperation.splitOn('.', resourceType.shortReadableName()), null,
                                                            ProblemReasons.InvalidTypeForAutoManagedResource);
                    }
                } else if (resourceType !=
                           null) { // https://bugs.eclipse.org/bugs/show_bug.cgi?id=349862, avoid secondary error in problematic null case
                    upperScope.problemReporter().resourceHasToImplementAutoCloseable(resourceType, this.resources[i].type);
                    localVariableBinding.type =
                            new ProblemReferenceBinding(CharOperation.splitOn('.', resourceType.shortReadableName()), null,
                                                        ProblemReasons.InvalidTypeForAutoManagedResource);
                }
            }
        }
        BlockScope tryScope = new BlockScope(resourceManagementScope != null ? resourceManagementScope : this.scope);

        if (this.finallyBlock != null) {
            if (this.finallyBlock.isEmptyBlock()) {
                if ((this.finallyBlock.bits & ASTNode.UndocumentedEmptyBlock) != 0) {
                    this.scope.problemReporter().undocumentedEmptyBlock(this.finallyBlock.sourceStart,
                                                                        this.finallyBlock.sourceEnd);
                }
            } else {
                finallyScope = new BlockScope(this.scope, false); // don't add it yet to parent scope

                // provision for returning and forcing the finally block to run
                MethodScope methodScope = this.scope.methodScope();

                // the type does not matter as long as it is not a base type
                if (!upperScope.compilerOptions().inlineJsrBytecode) {
                    this.returnAddressVariable =
                            new LocalVariableBinding(TryStatement.SECRET_RETURN_ADDRESS_NAME, upperScope.getJavaLangObject(),
                                                     ClassFileConstants.AccDefault, false);
                    finallyScope.addLocalVariable(this.returnAddressVariable);
                    this.returnAddressVariable.setConstant(Constant.NotAConstant); // not inlinable
                }
                this.subRoutineStartLabel = new BranchLabel();

                this.anyExceptionVariable =
                        new LocalVariableBinding(TryStatement.SECRET_ANY_HANDLER_NAME, this.scope.getJavaLangThrowable(),
                                                 ClassFileConstants.AccDefault, false);
                finallyScope.addLocalVariable(this.anyExceptionVariable);
                this.anyExceptionVariable.setConstant(Constant.NotAConstant); // not inlinable

                if (!methodScope.isInsideInitializer()) {
                    MethodBinding methodBinding = ((AbstractMethodDeclaration)methodScope.referenceContext).binding;
                    if (methodBinding != null) {
                        TypeBinding methodReturnType = methodBinding.returnType;
                        if (methodReturnType.id != TypeIds.T_void) {
                            this.secretReturnValue =
                                    new LocalVariableBinding(TryStatement.SECRET_RETURN_VALUE_NAME, methodReturnType,
                                                             ClassFileConstants.AccDefault, false);
                            finallyScope.addLocalVariable(this.secretReturnValue);
                            this.secretReturnValue.setConstant(Constant.NotAConstant); // not inlinable
                        }
                    }
                }
                this.finallyBlock.resolveUsing(finallyScope);
                // force the finally scope to have variable positions shifted after its try scope and catch ones
                int shiftScopesLength = this.catchArguments == null ? 1 : this.catchArguments.length + 1;
                finallyScope.shiftScopes = new BlockScope[shiftScopesLength];
                finallyScope.shiftScopes[0] = tryScope;
            }
        }
        this.tryBlock.resolveUsing(tryScope);

        // arguments type are checked against JavaLangThrowable in resolveForCatch(..)
        if (this.catchBlocks != null) {
            int length = this.catchArguments.length;
            TypeBinding[] argumentTypes = new TypeBinding[length];
            boolean containsUnionTypes = false;
            boolean catchHasError = false;
            for (int i = 0; i < length; i++) {
                BlockScope catchScope = new BlockScope(this.scope);
                if (finallyScope != null) {
                    finallyScope.shiftScopes[i + 1] = catchScope;
                }
                // side effect on catchScope in resolveForCatch(..)
                Argument catchArgument = this.catchArguments[i];
                containsUnionTypes |= (catchArgument.type.bits & ASTNode.IsUnionType) != 0;
                if ((argumentTypes[i] = catchArgument.resolveForCatch(catchScope)) == null) {
                    catchHasError = true;
                }
                this.catchBlocks[i].resolveUsing(catchScope);
            }
            if (catchHasError) {
                return;
            }
            // Verify that the catch clause are ordered in the right way:
            // more specialized first.
            verifyDuplicationAndOrder(length, argumentTypes, containsUnionTypes);
        } else {
            this.caughtExceptionTypes = new ReferenceBinding[0];
        }

        if (finallyScope != null) {
            // add finallyScope as last subscope, so it can be shifted behind try/catch subscopes.
            // the shifting is necessary to achieve no overlay in between the finally scope and its
            // sibling in term of local variable positions.
            this.scope.addSubscope(finallyScope);
        }
    }

    public void traverse(ASTVisitor visitor, BlockScope blockScope) {
        if (visitor.visit(this, blockScope)) {
            LocalDeclaration[] localDeclarations = this.resources;
            for (int i = 0, max = localDeclarations.length; i < max; i++) {
                localDeclarations[i].traverse(visitor, this.scope);
            }
            this.tryBlock.traverse(visitor, this.scope);
            if (this.catchArguments != null) {
                for (int i = 0, max = this.catchBlocks.length; i < max; i++) {
                    this.catchArguments[i].traverse(visitor, this.scope);
                    this.catchBlocks[i].traverse(visitor, this.scope);
                }
            }
            if (this.finallyBlock != null)
                this.finallyBlock.traverse(visitor, this.scope);
        }
        visitor.endVisit(this, blockScope);
    }

    protected void verifyDuplicationAndOrder(int length, TypeBinding[] argumentTypes, boolean containsUnionTypes) {
        // Verify that the catch clause are ordered in the right way:
        // more specialized first.
        if (containsUnionTypes) {
            int totalCount = 0;
            ReferenceBinding[][] allExceptionTypes = new ReferenceBinding[length][];
            for (int i = 0; i < length; i++) {
                ReferenceBinding currentExceptionType = (ReferenceBinding)argumentTypes[i];
                TypeReference catchArgumentType = this.catchArguments[i].type;
                if ((catchArgumentType.bits & ASTNode.IsUnionType) != 0) {
                    TypeReference[] typeReferences = ((UnionTypeReference)catchArgumentType).typeReferences;
                    int typeReferencesLength = typeReferences.length;
                    ReferenceBinding[] unionExceptionTypes = new ReferenceBinding[typeReferencesLength];
                    for (int j = 0; j < typeReferencesLength; j++) {
                        unionExceptionTypes[j] = (ReferenceBinding)typeReferences[j].resolvedType;
                    }
                    totalCount += typeReferencesLength;
                    allExceptionTypes[i] = unionExceptionTypes;
                } else {
                    allExceptionTypes[i] = new ReferenceBinding[]{currentExceptionType};
                    totalCount++;
                }
            }
            this.caughtExceptionTypes = new ReferenceBinding[totalCount];
            this.caughtExceptionsCatchBlocks = new int[totalCount];
            for (int i = 0, l = 0; i < length; i++) {
                ReferenceBinding[] currentExceptions = allExceptionTypes[i];
                loop:
                for (int j = 0, max = currentExceptions.length; j < max; j++) {
                    ReferenceBinding exception = currentExceptions[j];
                    this.caughtExceptionTypes[l] = exception;
                    this.caughtExceptionsCatchBlocks[l++] = i;
                    // now iterate over all previous exceptions
                    for (int k = 0; k < i; k++) {
                        ReferenceBinding[] exceptions = allExceptionTypes[k];
                        for (int n = 0, max2 = exceptions.length; n < max2; n++) {
                            ReferenceBinding currentException = exceptions[n];
                            if (exception.isCompatibleWith(currentException)) {
                                TypeReference catchArgumentType = this.catchArguments[i].type;
                                if ((catchArgumentType.bits & ASTNode.IsUnionType) != 0) {
                                    catchArgumentType = ((UnionTypeReference)catchArgumentType).typeReferences[j];
                                }
                                this.scope.problemReporter().wrongSequenceOfExceptionTypesError(catchArgumentType, exception,
                                                                                                currentException);
                                break loop;
                            }
                        }
                    }
                }
            }
        } else {
            this.caughtExceptionTypes = new ReferenceBinding[length];
            for (int i = 0; i < length; i++) {
                this.caughtExceptionTypes[i] = (ReferenceBinding)argumentTypes[i];
                for (int j = 0; j < i; j++) {
                    if (this.caughtExceptionTypes[i].isCompatibleWith(argumentTypes[j])) {
                        this.scope.problemReporter().wrongSequenceOfExceptionTypesError(this.catchArguments[i].type,
                                                                                        this.caughtExceptionTypes[i], argumentTypes[j]);
                    }
                }
            }
        }
    }
}
