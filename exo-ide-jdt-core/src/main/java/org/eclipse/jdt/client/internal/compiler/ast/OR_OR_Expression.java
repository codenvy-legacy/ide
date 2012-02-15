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

//dedicated treatment for the ||
public class OR_OR_Expression extends BinaryExpression
{

   public OR_OR_Expression(Expression left, Expression right, int operator)
   {
      super(left, right, operator);
   }

   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo)
   {

      Constant cst = this.left.optimizedBooleanConstant();
      boolean isLeftOptimizedTrue = cst != Constant.NotAConstant && cst.booleanValue() == true;
      boolean isLeftOptimizedFalse = cst != Constant.NotAConstant && cst.booleanValue() == false;

      if (isLeftOptimizedFalse)
      {
         // FALSE || anything
         // need to be careful of scenario:
         // (x || y) || !z, if passing the left info to the right, it would be swapped by the !
         FlowInfo mergedInfo = this.left.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits();
         mergedInfo = this.right.analyseCode(currentScope, flowContext, mergedInfo);
         return mergedInfo;
      }

      FlowInfo leftInfo = this.left.analyseCode(currentScope, flowContext, flowInfo);

      // need to be careful of scenario:
      // (x || y) || !z, if passing the left info to the right, it would be swapped by the !
      FlowInfo rightInfo = leftInfo.initsWhenFalse().unconditionalCopy();

      int previousMode = rightInfo.reachMode();
      if (isLeftOptimizedTrue)
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
      // The definitely null variables in right info when true should not be missed out while merging
      // https://bugs.eclipse.org/bugs/show_bug.cgi?id=299900
      FlowInfo leftInfoWhenTrueForMerging =
         leftInfo.initsWhenTrue().unconditionalCopy()
            .addPotentialInitializationsFrom(rightInfo.unconditionalInitsWithoutSideEffect());
      FlowInfo mergedInfo =
         FlowInfo.conditional(
            // merging two true initInfos for such a negative case: if ((t && (b = t)) || f) r = b; // b may not have been
            // initialized
            leftInfoWhenTrueForMerging.unconditionalInits().mergedWith(
               rightInfo.safeInitsWhenTrue().setReachMode(previousMode).unconditionalInits()),
            rightInfo.initsWhenFalse());
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
