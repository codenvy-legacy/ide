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
package org.eclipse.jdt.client.internal.compiler.ast;

import org.eclipse.jdt.client.internal.compiler.ASTVisitor;
import org.eclipse.jdt.client.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.client.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.client.internal.compiler.impl.Constant;
import org.eclipse.jdt.client.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.client.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.client.internal.compiler.lookup.TypeBinding;

public class ArrayReference extends Reference
{

   public Expression receiver;

   public Expression position;

   public ArrayReference(Expression rec, Expression pos)
   {
      this.receiver = rec;
      this.position = pos;
      this.sourceStart = rec.sourceStart;
   }

   public FlowInfo analyseAssignment(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo,
      Assignment assignment, boolean compoundAssignment)
   {
      if (assignment.expression == null)
      {
         return analyseCode(currentScope, flowContext, flowInfo);
      }
      return assignment.expression.analyseCode(currentScope, flowContext,
         analyseCode(currentScope, flowContext, flowInfo).unconditionalInits());
   }

   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo)
   {
      this.receiver.checkNPE(currentScope, flowContext, flowInfo);
      flowInfo = this.receiver.analyseCode(currentScope, flowContext, flowInfo);
      return this.position.analyseCode(currentScope, flowContext, flowInfo);
   }

   public int nullStatus(FlowInfo flowInfo)
   {
      return FlowInfo.UNKNOWN;
   }

   public StringBuffer printExpression(int indent, StringBuffer output)
   {
      this.receiver.printExpression(0, output).append('[');
      return this.position.printExpression(0, output).append(']');
   }

   public TypeBinding resolveType(BlockScope scope)
   {
      this.constant = Constant.NotAConstant;
      if (this.receiver instanceof CastExpression // no cast check for ((type[])null)[0]
         && ((CastExpression)this.receiver).innermostCastedExpression() instanceof NullLiteral)
      {
         this.receiver.bits |= ASTNode.DisableUnnecessaryCastCheck; // will check later on
      }
      TypeBinding arrayType = this.receiver.resolveType(scope);
      if (arrayType != null)
      {
         this.receiver.computeConversion(scope, arrayType, arrayType);
         if (arrayType.isArrayType())
         {
            TypeBinding elementType = ((ArrayBinding)arrayType).elementsType();
            this.resolvedType =
               ((this.bits & ASTNode.IsStrictlyAssigned) == 0) ? elementType.capture(scope, this.sourceEnd)
                  : elementType;
         }
         else
         {
            scope.problemReporter().referenceMustBeArrayTypeAt(arrayType, this);
         }
      }
      TypeBinding positionType = this.position.resolveTypeExpecting(scope, TypeBinding.INT);
      if (positionType != null)
      {
         this.position.computeConversion(scope, TypeBinding.INT, positionType);
      }
      return this.resolvedType;
   }

   public void traverse(ASTVisitor visitor, BlockScope scope)
   {
      if (visitor.visit(this, scope))
      {
         this.receiver.traverse(visitor, scope);
         this.position.traverse(visitor, scope);
      }
      visitor.endVisit(this, scope);
   }
}
