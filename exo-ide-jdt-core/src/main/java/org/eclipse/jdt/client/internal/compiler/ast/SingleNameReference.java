/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Stephan Herrmann <stephan@cs.tu-berlin.de> - Contribution for bug 292478 - Report potentially null across variable assignment,
 *                                          Contribution for bug 185682 - Increment/decrement operators mark local variables as read
 *******************************************************************************/
package org.eclipse.jdt.client.internal.compiler.ast;

import org.eclipse.jdt.client.core.compiler.CharOperation;
import org.eclipse.jdt.client.internal.compiler.ASTVisitor;
import org.eclipse.jdt.client.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.client.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.client.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.client.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.client.internal.compiler.impl.Constant;
import org.eclipse.jdt.client.internal.compiler.lookup.Binding;
import org.eclipse.jdt.client.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.client.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.client.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.client.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.client.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.client.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.client.internal.compiler.lookup.MissingTypeBinding;
import org.eclipse.jdt.client.internal.compiler.lookup.ProblemFieldBinding;
import org.eclipse.jdt.client.internal.compiler.lookup.ProblemReasons;
import org.eclipse.jdt.client.internal.compiler.lookup.ProblemReferenceBinding;
import org.eclipse.jdt.client.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.client.internal.compiler.lookup.Scope;
import org.eclipse.jdt.client.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.client.internal.compiler.lookup.TagBits;
import org.eclipse.jdt.client.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.client.internal.compiler.lookup.TypeIds;
import org.eclipse.jdt.client.internal.compiler.lookup.VariableBinding;
import org.eclipse.jdt.client.internal.compiler.problem.ProblemSeverities;

public class SingleNameReference extends NameReference implements OperatorIds
{

   public static final int READ = 0;

   public static final int WRITE = 1;

   public char[] token;

   public MethodBinding[] syntheticAccessors; // [0]=read accessor [1]=write accessor

   public TypeBinding genericCast;

   public SingleNameReference(char[] source, long pos)
   {
      super();
      this.token = source;
      this.sourceStart = (int)(pos >>> 32);
      this.sourceEnd = (int)pos;
   }

   public FlowInfo analyseAssignment(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo,
      Assignment assignment, boolean isCompound)
   {
      boolean isReachable = (flowInfo.tagBits & FlowInfo.UNREACHABLE) == 0;
      // compound assignment extra work
      if (isCompound)
      { // check the variable part is initialized if blank final
         switch (this.bits & ASTNode.RestrictiveFlagMASK)
         {
            case Binding.FIELD : // reading a field
               FieldBinding fieldBinding = (FieldBinding)this.binding;
               if (fieldBinding.isBlankFinal() && currentScope.needBlankFinalFieldInitializationCheck(fieldBinding))
               {
                  FlowInfo fieldInits =
                     flowContext.getInitsForFinalBlankInitializationCheck(fieldBinding.declaringClass.original(),
                        flowInfo);
                  if (!fieldInits.isDefinitelyAssigned(fieldBinding))
                  {
                     currentScope.problemReporter().uninitializedBlankFinalField(fieldBinding, this);
                  }
               }
               if (!fieldBinding.isStatic())
               {
                  // https://bugs.eclipse.org/bugs/show_bug.cgi?id=318682
                  currentScope.resetEnclosingMethodStaticFlag();
               }
               manageSyntheticAccessIfNecessary(currentScope, flowInfo, true /*
                                                                              * read - access
                                                                              */);
               break;
            case Binding.LOCAL : // reading a local variable
               // check if assigning a final blank field
               LocalVariableBinding localBinding;
               if (!flowInfo.isDefinitelyAssigned(localBinding = (LocalVariableBinding)this.binding))
               {
                  currentScope.problemReporter().uninitializedLocalVariable(localBinding, this);
                  // we could improve error msg here telling "cannot use compound assignment on final local variable"
               }
               if (localBinding.useFlag != LocalVariableBinding.USED)
               {
                  // https://bugs.eclipse.org/bugs/show_bug.cgi?id=185682
                  // access from compound assignment does not prevent "unused" warning, unless unboxing is involved:
                  if (isReachable && (this.implicitConversion & TypeIds.UNBOXING) != 0)
                  {
                     localBinding.useFlag = LocalVariableBinding.USED;
                  }
                  else
                  {
                     // use values < 0 to count the number of compound uses:
                     if (localBinding.useFlag <= LocalVariableBinding.UNUSED)
                        localBinding.useFlag--;
                  }
               }
         }
      }
      if (assignment.expression != null)
      {
         flowInfo = assignment.expression.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits();
      }
      switch (this.bits & ASTNode.RestrictiveFlagMASK)
      {
         case Binding.FIELD : // assigning to a field
            manageSyntheticAccessIfNecessary(currentScope, flowInfo, false /*
                                                                            * write- access
                                                                            */);

            // check if assigning a final field
            FieldBinding fieldBinding = (FieldBinding)this.binding;
            if (fieldBinding.isFinal())
            {
               // inside a context where allowed
               if (!isCompound && fieldBinding.isBlankFinal()
                  && currentScope.allowBlankFinalFieldAssignment(fieldBinding))
               {
                  if (flowInfo.isPotentiallyAssigned(fieldBinding))
                  {
                     currentScope.problemReporter().duplicateInitializationOfBlankFinalField(fieldBinding, this);
                  }
                  else
                  {
                     flowContext.recordSettingFinal(fieldBinding, this, flowInfo);
                  }
                  flowInfo.markAsDefinitelyAssigned(fieldBinding);
               }
               else
               {
                  currentScope.problemReporter().cannotAssignToFinalField(fieldBinding, this);
               }
            }
            if (!fieldBinding.isStatic())
            {
               // https://bugs.eclipse.org/bugs/show_bug.cgi?id=318682
               currentScope.resetEnclosingMethodStaticFlag();
            }
            break;
         case Binding.LOCAL : // assigning to a local variable
            LocalVariableBinding localBinding = (LocalVariableBinding)this.binding;
            if (!flowInfo.isDefinitelyAssigned(localBinding))
            {// for local variable debug attributes
               this.bits |= ASTNode.FirstAssignmentToLocal;
            }
            else
            {
               this.bits &= ~ASTNode.FirstAssignmentToLocal;
            }
            if (localBinding.isFinal())
            {
               if ((this.bits & ASTNode.DepthMASK) == 0)
               {
                  // tolerate assignment to final local in unreachable code (45674)
                  if ((isReachable && isCompound) || !localBinding.isBlankFinal())
                  {
                     currentScope.problemReporter().cannotAssignToFinalLocal(localBinding, this);
                  }
                  else if (flowInfo.isPotentiallyAssigned(localBinding))
                  {
                     currentScope.problemReporter().duplicateInitializationOfFinalLocal(localBinding, this);
                  }
                  else
                  {
                     flowContext.recordSettingFinal(localBinding, this, flowInfo);
                  }
               }
               else
               {
                  currentScope.problemReporter().cannotAssignToFinalOuterLocal(localBinding, this);
               }
            }
            else /* avoid double diagnostic */if ((localBinding.tagBits & TagBits.IsArgument) != 0)
            {
               currentScope.problemReporter().parameterAssignment(localBinding, this);
            }
            flowInfo.markAsDefinitelyAssigned(localBinding);
      }
      manageEnclosingInstanceAccessIfNecessary(currentScope, flowInfo);
      return flowInfo;
   }

   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo)
   {
      return analyseCode(currentScope, flowContext, flowInfo, true);
   }

   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo,
      boolean valueRequired)
   {
      switch (this.bits & ASTNode.RestrictiveFlagMASK)
      {
         case Binding.FIELD : // reading a field
            if (valueRequired || currentScope.compilerOptions().complianceLevel >= ClassFileConstants.JDK1_4)
            {
               manageSyntheticAccessIfNecessary(currentScope, flowInfo, true /*
                                                                              * read - access
                                                                              */);
            }
            // check if reading a final blank field
            FieldBinding fieldBinding = (FieldBinding)this.binding;
            if (fieldBinding.isBlankFinal() && currentScope.needBlankFinalFieldInitializationCheck(fieldBinding))
            {
               FlowInfo fieldInits =
                  flowContext
                     .getInitsForFinalBlankInitializationCheck(fieldBinding.declaringClass.original(), flowInfo);
               if (!fieldInits.isDefinitelyAssigned(fieldBinding))
               {
                  currentScope.problemReporter().uninitializedBlankFinalField(fieldBinding, this);
               }
            }
            if (!fieldBinding.isStatic())
            {
               // https://bugs.eclipse.org/bugs/show_bug.cgi?id=318682
               currentScope.resetEnclosingMethodStaticFlag();
            }
            break;
         case Binding.LOCAL : // reading a local variable
            LocalVariableBinding localBinding;
            if (!flowInfo.isDefinitelyAssigned(localBinding = (LocalVariableBinding)this.binding))
            {
               currentScope.problemReporter().uninitializedLocalVariable(localBinding, this);
            }
            if ((flowInfo.tagBits & FlowInfo.UNREACHABLE) == 0)
            {
               localBinding.useFlag = LocalVariableBinding.USED;
            }
            else if (localBinding.useFlag == LocalVariableBinding.UNUSED)
            {
               localBinding.useFlag = LocalVariableBinding.FAKE_USED;
            }
      }
      if (valueRequired)
      {
         manageEnclosingInstanceAccessIfNecessary(currentScope, flowInfo);
      }
      return flowInfo;
   }

   public TypeBinding checkFieldAccess(BlockScope scope)
   {
      FieldBinding fieldBinding = (FieldBinding)this.binding;
      this.constant = fieldBinding.constant();

      this.bits &= ~ASTNode.RestrictiveFlagMASK; // clear bits
      this.bits |= Binding.FIELD;
      MethodScope methodScope = scope.methodScope();
      if (fieldBinding.isStatic())
      {
         // check if accessing enum static field in initializer
         ReferenceBinding declaringClass = fieldBinding.declaringClass;
         if (declaringClass.isEnum())
         {
            SourceTypeBinding sourceType = scope.enclosingSourceType();
            if (this.constant == Constant.NotAConstant && !methodScope.isStatic
               && (sourceType == declaringClass || sourceType.superclass == declaringClass) // enum constant body
               && methodScope.isInsideInitializerOrConstructor())
            {
               scope.problemReporter().enumStaticFieldUsedDuringInitialization(fieldBinding, this);
            }
         }
      }
      else
      {
         if (scope.compilerOptions().getSeverity(CompilerOptions.UnqualifiedFieldAccess) != ProblemSeverities.Ignore)
         {
            scope.problemReporter().unqualifiedFieldAccess(this, fieldBinding);
         }
         // must check for the static status....
         if (methodScope.isStatic)
         {
            scope.problemReporter().staticFieldAccessToNonStaticVariable(this, fieldBinding);
            return fieldBinding.type;
         }
      }

      if (isFieldUseDeprecated(fieldBinding, scope, this.bits))
         scope.problemReporter().deprecatedField(fieldBinding, this);

      if ((this.bits & ASTNode.IsStrictlyAssigned) == 0
         && methodScope.enclosingSourceType() == fieldBinding.original().declaringClass
         && methodScope.lastVisibleFieldID >= 0 && fieldBinding.id >= methodScope.lastVisibleFieldID
         && (!fieldBinding.isStatic() || methodScope.isStatic))
      {
         scope.problemReporter().forwardReference(this, 0, fieldBinding);
         this.bits |= ASTNode.IgnoreNoEffectAssignCheck;
      }
      return fieldBinding.type;

   }

   /**
    * @see org.eclipse.jdt.client.internal.compiler.ast.Expression#computeConversion(org.eclipse.jdt.client.internal.compiler.lookup.Scope,
    *      org.eclipse.jdt.client.internal.compiler.lookup.TypeBinding,
    *      org.eclipse.jdt.client.internal.compiler.lookup.TypeBinding)
    */
   public void computeConversion(Scope scope, TypeBinding runtimeTimeType, TypeBinding compileTimeType)
   {
      if (runtimeTimeType == null || compileTimeType == null)
         return;
      if ((this.bits & Binding.FIELD) != 0 && this.binding != null && this.binding.isValidBinding())
      {
         // set the generic cast after the fact, once the type expectation is fully known (no need for strict cast)
         FieldBinding field = (FieldBinding)this.binding;
         FieldBinding originalBinding = field.original();
         TypeBinding originalType = originalBinding.type;
         // extra cast needed if field type is type variable
         if (originalType.leafComponentType().isTypeVariable())
         {
            TypeBinding targetType = (!compileTimeType.isBaseType() && runtimeTimeType.isBaseType()) ? compileTimeType // unboxing:
                                                                                                                       // checkcast
                                                                                                                       // before
                                                                                                                       // conversion
               : runtimeTimeType;
            this.genericCast = originalType.genericCast(scope.boxing(targetType));
            if (this.genericCast instanceof ReferenceBinding)
            {
               ReferenceBinding referenceCast = (ReferenceBinding)this.genericCast;
               if (!referenceCast.canBeSeenBy(scope))
               {
                  scope.problemReporter().invalidType(
                     this,
                     new ProblemReferenceBinding(CharOperation.splitOn('.', referenceCast.shortReadableName()),
                        referenceCast, ProblemReasons.NotVisible));
               }
            }
         }
      }
      super.computeConversion(scope, runtimeTimeType, compileTimeType);
   }

   /** @see org.eclipse.jdt.client.internal.compiler.lookup.InvocationSite#genericTypeArguments() */
   public TypeBinding[] genericTypeArguments()
   {
      return null;
   }

   /**
    * Returns the local variable referenced by this node. Can be a direct reference (SingleNameReference) or thru a cast
    * expression etc...
    */
   public LocalVariableBinding localVariableBinding()
   {
      switch (this.bits & ASTNode.RestrictiveFlagMASK)
      {
         case Binding.FIELD : // reading a field
            break;
         case Binding.LOCAL : // reading a local variable
            return (LocalVariableBinding)this.binding;
      }
      return null;
   }

   public void manageEnclosingInstanceAccessIfNecessary(BlockScope currentScope, FlowInfo flowInfo)
   {
      // If inlinable field, forget the access emulation, the code gen will directly target it
      if (((this.bits & ASTNode.DepthMASK) == 0) || (this.constant != Constant.NotAConstant))
      {
         return;
      }
      if ((this.bits & ASTNode.RestrictiveFlagMASK) == Binding.LOCAL)
      {
         LocalVariableBinding localVariableBinding = (LocalVariableBinding)this.binding;
         if (localVariableBinding != null)
         {
            if ((localVariableBinding.tagBits & TagBits.NotInitialized) != 0)
            {
               // local was tagged as uninitialized
               return;
            }
            switch (localVariableBinding.useFlag)
            {
               case LocalVariableBinding.FAKE_USED :
               case LocalVariableBinding.USED :
                  currentScope.emulateOuterAccess(localVariableBinding);
            }
         }
      }
   }

   public void manageSyntheticAccessIfNecessary(BlockScope currentScope, FlowInfo flowInfo, boolean isReadAccess)
   {
      if ((flowInfo.tagBits & FlowInfo.UNREACHABLE_OR_DEAD) != 0)
         return;

      // If inlinable field, forget the access emulation, the code gen will directly target it
      if (this.constant != Constant.NotAConstant)
         return;

      if ((this.bits & Binding.FIELD) != 0)
      {
         FieldBinding fieldBinding = (FieldBinding)this.binding;
         FieldBinding codegenField = fieldBinding.original();
         if (((this.bits & ASTNode.DepthMASK) != 0) && (codegenField.isPrivate() // private access
            || (codegenField.isProtected() // implicit protected access
            && codegenField.declaringClass.getPackage() != currentScope.enclosingSourceType().getPackage())))
         {
            if (this.syntheticAccessors == null)
               this.syntheticAccessors = new MethodBinding[2];
            this.syntheticAccessors[isReadAccess ? SingleNameReference.READ : SingleNameReference.WRITE] =
               ((SourceTypeBinding)currentScope.enclosingSourceType().enclosingTypeAt(
                  (this.bits & ASTNode.DepthMASK) >> ASTNode.DepthSHIFT)).addSyntheticMethod(codegenField,
                  isReadAccess, false /* not super access */);
            currentScope.problemReporter().needToEmulateFieldAccess(codegenField, this, isReadAccess);
            return;
         }
      }
   }

   public int nullStatus(FlowInfo flowInfo)
   {
      if (this.constant != null && this.constant != Constant.NotAConstant)
      {
         return FlowInfo.NON_NULL; // constant expression cannot be null
      }
      switch (this.bits & ASTNode.RestrictiveFlagMASK)
      {
         case Binding.FIELD : // reading a field
            return FlowInfo.UNKNOWN;
         case Binding.LOCAL : // reading a local variable
            LocalVariableBinding local = (LocalVariableBinding)this.binding;
            if (local != null)
               return flowInfo.nullStatus(local);
      }
      return FlowInfo.NON_NULL; // never get there
   }

   /** @see org.eclipse.jdt.client.internal.compiler.ast.Expression#postConversionType(Scope) */
   public TypeBinding postConversionType(Scope scope)
   {
      TypeBinding convertedType = this.resolvedType;
      if (this.genericCast != null)
         convertedType = this.genericCast;
      int runtimeType = (this.implicitConversion & TypeIds.IMPLICIT_CONVERSION_MASK) >> 4;
      switch (runtimeType)
      {
         case T_boolean :
            convertedType = TypeBinding.BOOLEAN;
            break;
         case T_byte :
            convertedType = TypeBinding.BYTE;
            break;
         case T_short :
            convertedType = TypeBinding.SHORT;
            break;
         case T_char :
            convertedType = TypeBinding.CHAR;
            break;
         case T_int :
            convertedType = TypeBinding.INT;
            break;
         case T_float :
            convertedType = TypeBinding.FLOAT;
            break;
         case T_long :
            convertedType = TypeBinding.LONG;
            break;
         case T_double :
            convertedType = TypeBinding.DOUBLE;
            break;
         default :
      }
      if ((this.implicitConversion & TypeIds.BOXING) != 0)
      {
         convertedType = scope.environment().computeBoxingType(convertedType);
      }
      return convertedType;
   }

   public StringBuffer printExpression(int indent, StringBuffer output)
   {
      return output.append(this.token);
   }

   public TypeBinding reportError(BlockScope scope)
   {
      // =====error cases=======
      this.constant = Constant.NotAConstant;
      if (this.binding instanceof ProblemFieldBinding)
      {
         scope.problemReporter().invalidField(this, (FieldBinding)this.binding);
      }
      else if (this.binding instanceof ProblemReferenceBinding || this.binding instanceof MissingTypeBinding)
      {
         scope.problemReporter().invalidType(this, (TypeBinding)this.binding);
      }
      else
      {
         scope.problemReporter().unresolvableReference(this, this.binding);
      }
      return null;
   }

   public TypeBinding resolveType(BlockScope scope)
   {
      // for code gen, harm the restrictiveFlag

      if (this.actualReceiverType != null)
      {
         this.binding = scope.getField(this.actualReceiverType, this.token, this);
      }
      else
      {
         this.actualReceiverType = scope.enclosingSourceType();
         this.binding =
            scope.getBinding(this.token, this.bits & ASTNode.RestrictiveFlagMASK, this, true /* resolve */);
      }
      if (this.binding.isValidBinding())
      {
         switch (this.bits & ASTNode.RestrictiveFlagMASK)
         {
            case Binding.VARIABLE : // =========only variable============
            case Binding.VARIABLE | Binding.TYPE : // ====both variable and type============
               if (this.binding instanceof VariableBinding)
               {
                  VariableBinding variable = (VariableBinding)this.binding;
                  TypeBinding variableType;
                  if (this.binding instanceof LocalVariableBinding)
                  {
                     this.bits &= ~ASTNode.RestrictiveFlagMASK; // clear bits
                     this.bits |= Binding.LOCAL;
                     if (!variable.isFinal() && (this.bits & ASTNode.DepthMASK) != 0)
                     {
                        scope.problemReporter().cannotReferToNonFinalOuterLocal((LocalVariableBinding)variable, this);
                     }
                     variableType = variable.type;
                     this.constant =
                        (this.bits & ASTNode.IsStrictlyAssigned) == 0 ? variable.constant() : Constant.NotAConstant;
                  }
                  else
                  {
                     // a field
                     variableType = checkFieldAccess(scope);
                  }
                  // perform capture conversion if read access
                  if (variableType != null)
                  {
                     this.resolvedType =
                        variableType =
                           (((this.bits & ASTNode.IsStrictlyAssigned) == 0) ? variableType.capture(scope,
                              this.sourceEnd) : variableType);
                     if ((variableType.tagBits & TagBits.HasMissingType) != 0)
                     {
                        if ((this.bits & Binding.LOCAL) == 0)
                        {
                           // only complain if field reference (for local, its type got flagged already)
                           scope.problemReporter().invalidType(this, variableType);
                        }
                        return null;
                     }
                  }
                  return variableType;
               }

               // thus it was a type
               this.bits &= ~ASTNode.RestrictiveFlagMASK; // clear bits
               this.bits |= Binding.TYPE;
               //$FALL-THROUGH$
            case Binding.TYPE : // ========only type==============
               this.constant = Constant.NotAConstant;
               // deprecated test
               TypeBinding type = (TypeBinding)this.binding;
               if (isTypeUseDeprecated(type, scope))
                  scope.problemReporter().deprecatedType(type, this);
               type = scope.environment().convertToRawType(type, false /*
                                                                        * do not force conversion of enclosing types
                                                                        */);
               return this.resolvedType = type;
         }
      }
      // error scenarii
      return this.resolvedType = reportError(scope);
   }

   public void traverse(ASTVisitor visitor, BlockScope scope)
   {
      visitor.visit(this, scope);
      visitor.endVisit(this, scope);
   }

   public void traverse(ASTVisitor visitor, ClassScope scope)
   {
      visitor.visit(this, scope);
      visitor.endVisit(this, scope);
   }

   public String unboundReferenceErrorName()
   {
      return new String(this.token);
   }
}
