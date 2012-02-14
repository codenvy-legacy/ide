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
package org.eclipse.jdt.client.internal.compiler.ast;

import org.eclipse.jdt.client.internal.compiler.ASTVisitor;
import org.eclipse.jdt.client.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.client.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.client.internal.compiler.impl.Constant;
import org.eclipse.jdt.client.internal.compiler.lookup.Binding;
import org.eclipse.jdt.client.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.client.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.client.internal.compiler.lookup.TypeIds;

//dedicated treatment for the &&
public class AND_AND_Expression extends BinaryExpression
{

   public AND_AND_Expression(Expression left, Expression right, int operator)
   {
      super(left, right, operator);
   }

   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo)
   {

      Constant cst = this.left.optimizedBooleanConstant();
      boolean isLeftOptimizedTrue = cst != Constant.NotAConstant && cst.booleanValue() == true;
      boolean isLeftOptimizedFalse = cst != Constant.NotAConstant && cst.booleanValue() == false;

      if (isLeftOptimizedTrue)
      {
         // TRUE && anything
         // need to be careful of scenario:
         // (x && y) && !z, if passing the left info to the right, it would
         // be swapped by the !
         FlowInfo mergedInfo = this.left.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits();
         mergedInfo = this.right.analyseCode(currentScope, flowContext, mergedInfo);
         return mergedInfo;
      }

      FlowInfo leftInfo = this.left.analyseCode(currentScope, flowContext, flowInfo);
      // need to be careful of scenario:
      // (x && y) && !z, if passing the left info to the right, it would be
      // swapped by the !
      FlowInfo rightInfo = leftInfo.initsWhenTrue().unconditionalCopy();

      int previousMode = rightInfo.reachMode();
      if (isLeftOptimizedFalse)
      {
         if ((rightInfo.reachMode() & FlowInfo.UNREACHABLE) == 0)
         {
            currentScope.problemReporter().fakeReachable(this.right);
            rightInfo.setReachMode(FlowInfo.UNREACHABLE_OR_DEAD);
         }
      }
      rightInfo = this.right.analyseCode(currentScope, flowContext, rightInfo);
      if ((this.left.implicitConversion & TypeIds.UNBOXING) != 0)
      {
         this.left.checkNPE(currentScope, flowContext, flowInfo);
      }
      if ((this.right.implicitConversion & TypeIds.UNBOXING) != 0)
      {
         this.right.checkNPE(currentScope, flowContext, flowInfo);
      }
      FlowInfo mergedInfo =
         FlowInfo.conditional(
            rightInfo.safeInitsWhenTrue(),
            leftInfo.initsWhenFalse().unconditionalInits()
               .mergedWith(rightInfo.initsWhenFalse().setReachMode(previousMode).unconditionalInits()));
      return mergedInfo;
   }

   public boolean isCompactableOperation()
   {
      return false;
   }

   /** @see org.eclipse.jdt.client.internal.compiler.ast.BinaryExpression#resolveType(org.eclipse.jdt.client.internal.compiler.lookup.BlockScope) */
   public TypeBinding resolveType(BlockScope scope)
   {
      TypeBinding result = super.resolveType(scope);
      // check whether comparing identical expressions
      Binding leftDirect = Expression.getDirectBinding(this.left);
      if (leftDirect != null && leftDirect == Expression.getDirectBinding(this.right))
      {
         if (!(this.right instanceof Assignment))
            scope.problemReporter().comparingIdenticalExpressions(this);
      }
      return result;
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
