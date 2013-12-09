/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Stephan Herrmann <stephan@cs.tu-berlin.de> - Contributions for 
 *     						bug 319201 - [null] no warning when unboxing SingleNameReference causes NPE
 *     						bug 292478 - Report potentially null across variable assignment
 *     						bug 335093 - [compiler][null] minimal hook for future null annotation support
 *******************************************************************************/
package com.codenvy.ide.ext.java.jdt.internal.compiler.ast;

import com.codenvy.ide.ext.java.jdt.internal.compiler.ASTVisitor;
import com.codenvy.ide.ext.java.jdt.internal.compiler.ClassFileConstants;
import com.codenvy.ide.ext.java.jdt.internal.compiler.flow.FlowContext;
import com.codenvy.ide.ext.java.jdt.internal.compiler.flow.FlowInfo;
import com.codenvy.ide.ext.java.jdt.internal.compiler.impl.Constant;
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.*;

public class LocalDeclaration extends AbstractVariableDeclaration {

    public LocalVariableBinding binding;

    public LocalDeclaration(char[] name, int sourceStart, int sourceEnd) {

        this.name = name;
        this.sourceStart = sourceStart;
        this.sourceEnd = sourceEnd;
        this.declarationEnd = sourceEnd;
    }

    public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
        // record variable initialization if any
        if ((flowInfo.tagBits & FlowInfo.UNREACHABLE_OR_DEAD) == 0) {
            this.bits |= ASTNode.IsLocalDeclarationReachable; // only set if actually reached
        }
        if (this.binding != null && this.type.resolvedType instanceof TypeVariableBinding) {
            MethodScope methodScope = this.binding.declaringScope.methodScope();
            AbstractMethodDeclaration methodDeclaration = methodScope.referenceMethod();
            if (methodDeclaration != null && ((methodDeclaration.bits & ASTNode.CanBeStatic) != 0)
                && methodDeclaration.binding != null) {
                TypeVariableBinding[] typeVariables = methodDeclaration.binding.typeVariables();
                if (typeVariables == Binding.NO_TYPE_VARIABLES) {
                    // Method declares no type variables.
                    currentScope.resetEnclosingMethodStaticFlag();
                } else {
                    // to check whether the resolved type for this is declared by enclosing method as a type variable
                    boolean usesEnclosingTypeVar = false;
                    for (int i = 0; i < typeVariables.length; i++) {
                        if (typeVariables[i] == this.type.resolvedType) {
                            usesEnclosingTypeVar = true;
                            break;
                        }
                    }
                    if (!usesEnclosingTypeVar) {
                        // uses a type variable not declared by enclosing method
                        currentScope.resetEnclosingMethodStaticFlag();
                    }
                }
            }
        }
        if (this.initialization == null) {
            return flowInfo;
        }
        if ((this.initialization.implicitConversion & TypeIds.UNBOXING) != 0) {
            this.initialization.checkNPE(currentScope, flowContext, flowInfo);
        }

        flowInfo = this.initialization.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits();
        int nullStatus = this.initialization.nullStatus(flowInfo);
        if (!flowInfo.isDefinitelyAssigned(this.binding)) {// for local variable debug attributes
            this.bits |= FirstAssignmentToLocal;
        } else {
            this.bits &= ~FirstAssignmentToLocal; // int i = (i = 0);
        }
        flowInfo.markAsDefinitelyAssigned(this.binding);
        nullStatus = checkAgainstNullAnnotation(currentScope, this.binding, nullStatus);
        if ((this.binding.type.tagBits & TagBits.IsBaseType) == 0) {
            flowInfo.markNullStatus(this.binding, nullStatus);
            // no need to inform enclosing try block since its locals won't get
            // known by the finally block
        }
        return flowInfo;
    }

    public void checkModifiers() {

        //only potential valid modifier is <<final>>
        if (((this.modifiers & ExtraCompilerModifiers.AccJustFlag) & ~ClassFileConstants.AccFinal) != 0)
            //AccModifierProblem -> other (non-visibility problem)
            //AccAlternateModifierProblem -> duplicate modifier
            //AccModifierProblem | AccAlternateModifierProblem -> visibility problem"

            this.modifiers =
                    (this.modifiers & ~ExtraCompilerModifiers.AccAlternateModifierProblem)
                    | ExtraCompilerModifiers.AccModifierProblem;
    }

    /**
     * Code generation for a local declaration:
     * i.e.&nbsp;normal assignment to a local variable + unused variable handling
     */
    public void generateCode(BlockScope currentScope) {

        if ((this.bits & IsReachable) == 0) {
            return;
        }

        // something to initialize?
        generateInit:
        {
            if (this.initialization == null)
                break generateInit;
            // forget initializing unused or final locals set to constant value (final ones are inlined)
            if (this.binding.resolvedPosition < 0) {
                if (this.initialization.constant != Constant.NotAConstant)
                    break generateInit;
                // if binding unused generate then discard the value
                this.initialization.generateCode(currentScope, false);
                break generateInit;
            }
            this.initialization.generateCode(currentScope, true);
        }
    }

    /** @see com.codenvy.ide.ext.java.jdt.internal.compiler.ast.AbstractVariableDeclaration#getKind() */
    public int getKind() {
        return LOCAL_VARIABLE;
    }

    public void resolve(BlockScope scope) {

        // create a binding and add it to the scope
        TypeBinding variableType = this.type.resolveType(scope, true /* check bounds*/);

        checkModifiers();
        if (variableType != null) {
            if (variableType == TypeBinding.VOID) {
                scope.problemReporter().variableTypeCannotBeVoid(this);
                return;
            }
            if (variableType.isArrayType() && ((ArrayBinding)variableType).leafComponentType == TypeBinding.VOID) {
                scope.problemReporter().variableTypeCannotBeVoidArray(this);
                return;
            }
        }

        Binding existingVariable =
                scope.getBinding(this.name, Binding.VARIABLE, this, false /*do not resolve hidden field*/);
        if (existingVariable != null && existingVariable.isValidBinding()) {
            if (existingVariable instanceof LocalVariableBinding && this.hiddenVariableDepth == 0) {
                scope.problemReporter().redefineLocal(this);
            } else {
                scope.problemReporter().localVariableHiding(this, existingVariable, false);
            }
        }

        if ((this.modifiers & ClassFileConstants.AccFinal) != 0 && this.initialization == null) {
            this.modifiers |= ExtraCompilerModifiers.AccBlankFinal;
        }
        this.binding = new LocalVariableBinding(this, variableType, this.modifiers, false);
        scope.addLocalVariable(this.binding);
        this.binding.setConstant(Constant.NotAConstant);
        // allow to recursivelly target the binding....
        // the correct constant is harmed if correctly computed at the end of this method

        if (variableType == null) {
            if (this.initialization != null)
                this.initialization.resolveType(scope); // want to report all possible errors
            return;
        }

        // store the constant for final locals
        if (this.initialization != null) {
            if (this.initialization instanceof ArrayInitializer) {
                TypeBinding initializationType = this.initialization.resolveTypeExpecting(scope, variableType);
                if (initializationType != null) {
                    ((ArrayInitializer)this.initialization).binding = (ArrayBinding)initializationType;
                    this.initialization.computeConversion(scope, variableType, initializationType);
                }
            } else {
                this.initialization.setExpectedType(variableType);
                TypeBinding initializationType = this.initialization.resolveType(scope);
                if (initializationType != null) {
                    if (variableType != initializationType) // must call before computeConversion() and typeMismatchError()
                        scope.compilationUnitScope().recordTypeConversion(variableType, initializationType);
                    if (this.initialization.isConstantValueOfTypeAssignableToType(initializationType, variableType)
                        || initializationType.isCompatibleWith(variableType)) {
                        this.initialization.computeConversion(scope, variableType, initializationType);
                        if (initializationType.needsUncheckedConversion(variableType)) {
                            scope.problemReporter()
                                 .unsafeTypeConversion(this.initialization, initializationType, variableType);
                        }
                        if (this.initialization instanceof CastExpression
                            && (this.initialization.bits & ASTNode.UnnecessaryCast) == 0) {
                            CastExpression.checkNeedForAssignedCast(scope, variableType, (CastExpression)this.initialization);
                        }
                    } else if (isBoxingCompatible(initializationType, variableType, this.initialization, scope)) {
                        this.initialization.computeConversion(scope, variableType, initializationType);
                        if (this.initialization instanceof CastExpression
                            && (this.initialization.bits & ASTNode.UnnecessaryCast) == 0) {
                            CastExpression.checkNeedForAssignedCast(scope, variableType, (CastExpression)this.initialization);
                        }
                    } else {
                        if ((variableType.tagBits & TagBits.HasMissingType) == 0) {
                            // if problem already got signaled on type, do not report secondary problem
                            scope.problemReporter().typeMismatchError(initializationType, variableType, this.initialization,
                                                                      null);
                        }
                    }
                }
            }
            // check for assignment with no effect
            if (this.binding == Expression.getDirectBinding(this.initialization)) {
                scope.problemReporter().assignmentHasNoEffect(this, this.name);
            }
            // change the constant in the binding when it is final
            // (the optimization of the constant propagation will be done later on)
            // cast from constant actual type to variable type
            this.binding.setConstant(this.binding.isFinal() ? this.initialization.constant.castTo((variableType.id << 4)
                                                                                                  + this.initialization.constant.typeID())
                                                            : Constant.NotAConstant);
        }
        // only resolve annotation at the end, for constant to be positioned before (96991)
        resolveAnnotations(scope, this.annotations, this.binding);
    }

    public void traverse(ASTVisitor visitor, BlockScope scope) {

        if (visitor.visit(this, scope)) {
            if (this.annotations != null) {
                int annotationsLength = this.annotations.length;
                for (int i = 0; i < annotationsLength; i++)
                    this.annotations[i].traverse(visitor, scope);
            }
            this.type.traverse(visitor, scope);
            if (this.initialization != null)
                this.initialization.traverse(visitor, scope);
        }
        visitor.endVisit(this, scope);
    }
}
