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
package org.eclipse.jdt.client.internal.compiler.ast;

import org.eclipse.jdt.client.internal.compiler.ASTVisitor;
import org.eclipse.jdt.client.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.client.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.client.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.client.internal.compiler.flow.UnconditionalFlowInfo;
import org.eclipse.jdt.client.internal.compiler.impl.BooleanConstant;
import org.eclipse.jdt.client.internal.compiler.impl.Constant;
import org.eclipse.jdt.client.internal.compiler.lookup.Binding;
import org.eclipse.jdt.client.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.client.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.client.internal.compiler.lookup.TagBits;
import org.eclipse.jdt.client.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.client.internal.compiler.lookup.TypeIds;

public class EqualExpression extends BinaryExpression
{

   public EqualExpression(Expression left, Expression right, int operator)
   {
      super(left, right, operator);
   }

   private void checkNullComparison(BlockScope scope, FlowContext flowContext, FlowInfo flowInfo,
      FlowInfo initsWhenTrue, FlowInfo initsWhenFalse)
   {

      LocalVariableBinding local = this.left.localVariableBinding();
      if (local != null && (local.type.tagBits & TagBits.IsBaseType) == 0)
      {
         checkVariableComparison(scope, flowContext, flowInfo, initsWhenTrue, initsWhenFalse, local,
            this.right.nullStatus(flowInfo), this.left);
      }
      local = this.right.localVariableBinding();
      if (local != null && (local.type.tagBits & TagBits.IsBaseType) == 0)
      {
         checkVariableComparison(scope, flowContext, flowInfo, initsWhenTrue, initsWhenFalse, local,
            this.left.nullStatus(flowInfo), this.right);
      }
   }

   private void checkVariableComparison(BlockScope scope, FlowContext flowContext, FlowInfo flowInfo,
      FlowInfo initsWhenTrue, FlowInfo initsWhenFalse, LocalVariableBinding local, int nullStatus, Expression reference)
   {
      switch (nullStatus)
      {
         case FlowInfo.NULL :
            if (((this.bits & OperatorMASK) >> OperatorSHIFT) == EQUAL_EQUAL)
            {
               flowContext.recordUsingNullReference(scope, local, reference, FlowContext.CAN_ONLY_NULL_NON_NULL
                  | FlowContext.IN_COMPARISON_NULL, flowInfo);
               initsWhenTrue.markAsComparedEqualToNull(local); // from thereon it is set
               initsWhenFalse.markAsComparedEqualToNonNull(local); // from thereon it is set
            }
            else
            {
               flowContext.recordUsingNullReference(scope, local, reference, FlowContext.CAN_ONLY_NULL_NON_NULL
                  | FlowContext.IN_COMPARISON_NON_NULL, flowInfo);
               initsWhenTrue.markAsComparedEqualToNonNull(local); // from thereon it is set
               initsWhenFalse.markAsComparedEqualToNull(local); // from thereon it is set
            }
            if ((flowContext.tagBits & FlowContext.HIDE_NULL_COMPARISON_WARNING) != 0)
            {
               flowInfo.markedAsNullOrNonNullInAssertExpression(local);
            }
            break;
         case FlowInfo.NON_NULL :
            if (((this.bits & OperatorMASK) >> OperatorSHIFT) == EQUAL_EQUAL)
            {
               flowContext.recordUsingNullReference(scope, local, reference, FlowContext.CAN_ONLY_NULL
                  | FlowContext.IN_COMPARISON_NON_NULL, flowInfo);
               initsWhenTrue.markAsComparedEqualToNonNull(local); // from thereon it is set
               if ((flowContext.tagBits & FlowContext.HIDE_NULL_COMPARISON_WARNING) != 0)
               {
                  initsWhenTrue.markedAsNullOrNonNullInAssertExpression(local);
               }
            }
            else
            {
               flowContext.recordUsingNullReference(scope, local, reference, FlowContext.CAN_ONLY_NULL
                  | FlowContext.IN_COMPARISON_NULL, flowInfo);
            }
            break;
      }
      // we do not impact enclosing try context because this kind of protection
      // does not preclude the variable from being null in an enclosing scope
   }

   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo)
   {
      FlowInfo result;
      if (((this.bits & OperatorMASK) >> OperatorSHIFT) == EQUAL_EQUAL)
      {
         if ((this.left.constant != Constant.NotAConstant) && (this.left.constant.typeID() == T_boolean))
         {
            if (this.left.constant.booleanValue())
            { // true == anything
              // this is equivalent to the right argument inits
               result = this.right.analyseCode(currentScope, flowContext, flowInfo);
            }
            else
            { // false == anything
              // this is equivalent to the right argument inits negated
               result = this.right.analyseCode(currentScope, flowContext, flowInfo).asNegatedCondition();
            }
         }
         else if ((this.right.constant != Constant.NotAConstant) && (this.right.constant.typeID() == T_boolean))
         {
            if (this.right.constant.booleanValue())
            { // anything == true
              // this is equivalent to the left argument inits
               result = this.left.analyseCode(currentScope, flowContext, flowInfo);
            }
            else
            { // anything == false
              // this is equivalent to the right argument inits negated
               result = this.left.analyseCode(currentScope, flowContext, flowInfo).asNegatedCondition();
            }
         }
         else
         {
            result =
               this.right.analyseCode(currentScope, flowContext,
                  this.left.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits()).unconditionalInits();
         }
      }
      else
      { // NOT_EQUAL :
         if ((this.left.constant != Constant.NotAConstant) && (this.left.constant.typeID() == T_boolean))
         {
            if (!this.left.constant.booleanValue())
            { // false != anything
              // this is equivalent to the right argument inits
               result = this.right.analyseCode(currentScope, flowContext, flowInfo);
            }
            else
            { // true != anything
              // this is equivalent to the right argument inits negated
               result = this.right.analyseCode(currentScope, flowContext, flowInfo).asNegatedCondition();
            }
         }
         else if ((this.right.constant != Constant.NotAConstant) && (this.right.constant.typeID() == T_boolean))
         {
            if (!this.right.constant.booleanValue())
            { // anything != false
              // this is equivalent to the right argument inits
               result = this.left.analyseCode(currentScope, flowContext, flowInfo);
            }
            else
            { // anything != true
              // this is equivalent to the right argument inits negated
               result = this.left.analyseCode(currentScope, flowContext, flowInfo).asNegatedCondition();
            }
         }
         else
         {
            result =
               this.right.analyseCode(currentScope, flowContext,
                  this.left.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits()).
               /* unneeded since we flatten it: asNegatedCondition(). */
               unconditionalInits();
         }
      }
      if (result instanceof UnconditionalFlowInfo && (result.tagBits & FlowInfo.UNREACHABLE) == 0)
      { // the flow info is flat
         result = FlowInfo.conditional(result.copy(), result.copy());
         // TODO (maxime) check, reintroduced copy
      }
      checkNullComparison(currentScope, flowContext, result, result.initsWhenTrue(), result.initsWhenFalse());
      return result;
   }

   public final void computeConstant(TypeBinding leftType, TypeBinding rightType)
   {
      if ((this.left.constant != Constant.NotAConstant) && (this.right.constant != Constant.NotAConstant))
      {
         this.constant =
            Constant.computeConstantOperationEQUAL_EQUAL(this.left.constant, leftType.id, this.right.constant,
               rightType.id);
         if (((this.bits & OperatorMASK) >> OperatorSHIFT) == NOT_EQUAL)
            this.constant = BooleanConstant.fromValue(!this.constant.booleanValue());
      }
      else
      {
         this.constant = Constant.NotAConstant;
         // no optimization for null == null
      }
   }

   public boolean isCompactableOperation()
   {
      return false;
   }

   public TypeBinding resolveType(BlockScope scope)
   {

      boolean leftIsCast, rightIsCast;
      if ((leftIsCast = this.left instanceof CastExpression) == true)
         this.left.bits |= DisableUnnecessaryCastCheck; // will check later on
      TypeBinding originalLeftType = this.left.resolveType(scope);

      if ((rightIsCast = this.right instanceof CastExpression) == true)
         this.right.bits |= DisableUnnecessaryCastCheck; // will check later on
      TypeBinding originalRightType = this.right.resolveType(scope);

      // always return BooleanBinding
      if (originalLeftType == null || originalRightType == null)
      {
         this.constant = Constant.NotAConstant;
         return null;
      }

      // autoboxing support
      boolean use15specifics = scope.compilerOptions().sourceLevel >= ClassFileConstants.JDK1_5;
      TypeBinding leftType = originalLeftType, rightType = originalRightType;
      if (use15specifics)
      {
         if (leftType != TypeBinding.NULL && leftType.isBaseType())
         {
            if (!rightType.isBaseType())
            {
               rightType = scope.environment().computeBoxingType(rightType);
            }
         }
         else
         {
            if (rightType != TypeBinding.NULL && rightType.isBaseType())
            {
               leftType = scope.environment().computeBoxingType(leftType);
            }
         }
      }
      // both base type
      if (leftType.isBaseType() && rightType.isBaseType())
      {
         int leftTypeID = leftType.id;
         int rightTypeID = rightType.id;

         // the code is an int
         // (cast) left == (cast) right --> result
         // 0000 0000 0000 0000 0000
         // <<16 <<12 <<8 <<4 <<0
         int operatorSignature = OperatorSignatures[EQUAL_EQUAL][(leftTypeID << 4) + rightTypeID];
         this.left.computeConversion(scope, TypeBinding.wellKnownType(scope, (operatorSignature >>> 16) & 0x0000F),
            originalLeftType);
         this.right.computeConversion(scope, TypeBinding.wellKnownType(scope, (operatorSignature >>> 8) & 0x0000F),
            originalRightType);
         this.bits |= operatorSignature & 0xF;
         if ((operatorSignature & 0x0000F) == T_undefined)
         {
            this.constant = Constant.NotAConstant;
            scope.problemReporter().invalidOperator(this, leftType, rightType);
            return null;
         }
         // check need for operand cast
         if (leftIsCast || rightIsCast)
         {
            CastExpression.checkNeedForArgumentCasts(scope, EQUAL_EQUAL, operatorSignature, this.left, leftType.id,
               leftIsCast, this.right, rightType.id, rightIsCast);
         }
         computeConstant(leftType, rightType);

         // check whether comparing identical expressions
         Binding leftDirect = Expression.getDirectBinding(this.left);
         if (leftDirect != null && leftDirect == Expression.getDirectBinding(this.right))
         {
            if (leftTypeID != TypeIds.T_double && leftTypeID != TypeIds.T_float
               && (!(this.right instanceof Assignment))) // https://bugs.eclipse.org/bugs/show_bug.cgi?id=281776
               scope.problemReporter().comparingIdenticalExpressions(this);
         }
         else if (this.constant != Constant.NotAConstant)
         {
            // https://bugs.eclipse.org/bugs/show_bug.cgi?id=276740
            int operator = (this.bits & OperatorMASK) >> OperatorSHIFT;
            if ((operator == EQUAL_EQUAL && this.constant == BooleanConstant.fromValue(true))
               || (operator == NOT_EQUAL && this.constant == BooleanConstant.fromValue(false)))
               scope.problemReporter().comparingIdenticalExpressions(this);
         }
         return this.resolvedType = TypeBinding.BOOLEAN;
      }

      // Object references
      // spec 15.20.3
      if ((!leftType.isBaseType() || leftType == TypeBinding.NULL) // cannot compare: Object == (int)0
         && (!rightType.isBaseType() || rightType == TypeBinding.NULL)
         && (checkCastTypesCompatibility(scope, leftType, rightType, null) || checkCastTypesCompatibility(scope,
            rightType, leftType, null)))
      {

         // (special case for String)
         if ((rightType.id == T_JavaLangString) && (leftType.id == T_JavaLangString))
         {
            computeConstant(leftType, rightType);
         }
         else
         {
            this.constant = Constant.NotAConstant;
         }
         TypeBinding objectType = scope.getJavaLangObject();
         this.left.computeConversion(scope, objectType, leftType);
         this.right.computeConversion(scope, objectType, rightType);
         // check need for operand cast
         boolean unnecessaryLeftCast = (this.left.bits & UnnecessaryCast) != 0;
         boolean unnecessaryRightCast = (this.right.bits & UnnecessaryCast) != 0;
         if (unnecessaryLeftCast || unnecessaryRightCast)
         {
            TypeBinding alternateLeftType =
               unnecessaryLeftCast ? ((CastExpression)this.left).expression.resolvedType : leftType;
            TypeBinding alternateRightType =
               unnecessaryRightCast ? ((CastExpression)this.right).expression.resolvedType : rightType;
            if (checkCastTypesCompatibility(scope, alternateLeftType, alternateRightType, null)
               || checkCastTypesCompatibility(scope, alternateRightType, alternateLeftType, null))
            {
               if (unnecessaryLeftCast)
                  scope.problemReporter().unnecessaryCast((CastExpression)this.left);
               if (unnecessaryRightCast)
                  scope.problemReporter().unnecessaryCast((CastExpression)this.right);
            }
         }
         // check whether comparing identical expressions
         Binding leftDirect = Expression.getDirectBinding(this.left);
         if (leftDirect != null && leftDirect == Expression.getDirectBinding(this.right))
         {
            if (!(this.right instanceof Assignment))
            {
               scope.problemReporter().comparingIdenticalExpressions(this);
            }
         }
         return this.resolvedType = TypeBinding.BOOLEAN;
      }
      this.constant = Constant.NotAConstant;
      scope.problemReporter().notCompatibleTypesError(this, leftType, rightType);
      return null;
   }

   public void traverse(ASTVisitor visitor, BlockScope scope)
   {
      if (visitor.visit(this, scope))
      {
         this.left.traverse(visitor, scope);
         this.right.traverse(visitor, scope);
      }
      visitor.endVisit(this, scope);
   }
}
