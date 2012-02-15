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
import org.eclipse.jdt.client.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.client.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.client.internal.compiler.lookup.TypeIds;

public class IfStatement extends Statement
{

   // this class represents the case of only one statement in
   // either else and/or then branches.

   public Expression condition;

   public Statement thenStatement;

   public Statement elseStatement;

   public IfStatement(Expression condition, Statement thenStatement, int sourceStart, int sourceEnd)
   {
      this.condition = condition;
      this.thenStatement = thenStatement;
      // remember useful empty statement
      if (thenStatement instanceof EmptyStatement)
         thenStatement.bits |= IsUsefulEmptyStatement;
      this.sourceStart = sourceStart;
      this.sourceEnd = sourceEnd;
   }

   public IfStatement(Expression condition, Statement thenStatement, Statement elseStatement, int sourceStart,
      int sourceEnd)
   {
      this.condition = condition;
      this.thenStatement = thenStatement;
      // remember useful empty statement
      if (thenStatement instanceof EmptyStatement)
         thenStatement.bits |= IsUsefulEmptyStatement;
      this.elseStatement = elseStatement;
      if (elseStatement instanceof IfStatement)
         elseStatement.bits |= IsElseIfStatement;
      if (elseStatement instanceof EmptyStatement)
         elseStatement.bits |= IsUsefulEmptyStatement;
      this.sourceStart = sourceStart;
      this.sourceEnd = sourceEnd;
   }

   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo)
   {
      // process the condition
      FlowInfo conditionFlowInfo = this.condition.analyseCode(currentScope, flowContext, flowInfo);
      int initialComplaintLevel =
         (flowInfo.reachMode() & FlowInfo.UNREACHABLE) != 0 ? Statement.COMPLAINED_FAKE_REACHABLE
            : Statement.NOT_COMPLAINED;

      Constant cst = this.condition.optimizedBooleanConstant();
      if ((this.condition.implicitConversion & TypeIds.UNBOXING) != 0)
      {
         this.condition.checkNPE(currentScope, flowContext, flowInfo);
      }
      boolean isConditionOptimizedTrue = cst != Constant.NotAConstant && cst.booleanValue() == true;
      boolean isConditionOptimizedFalse = cst != Constant.NotAConstant && cst.booleanValue() == false;

      // process the THEN part
      FlowInfo thenFlowInfo = conditionFlowInfo.safeInitsWhenTrue();
      if (isConditionOptimizedFalse)
      {
         thenFlowInfo.setReachMode(FlowInfo.UNREACHABLE_OR_DEAD);
      }
      FlowInfo elseFlowInfo = conditionFlowInfo.initsWhenFalse().copy();
      if (isConditionOptimizedTrue)
      {
         elseFlowInfo.setReachMode(FlowInfo.UNREACHABLE_OR_DEAD);
      }
      if (((flowInfo.tagBits & FlowInfo.UNREACHABLE) == 0) && ((thenFlowInfo.tagBits & FlowInfo.UNREACHABLE) != 0))
      {
         // Mark then block as unreachable
         // No need if the whole if-else construct itself lies in unreachable code
         this.bits |= ASTNode.IsThenStatementUnreachable;
      }
      else if (((flowInfo.tagBits & FlowInfo.UNREACHABLE) == 0) && ((elseFlowInfo.tagBits & FlowInfo.UNREACHABLE) != 0))
      {
         // Mark else block as unreachable
         // No need if the whole if-else construct itself lies in unreachable code
         this.bits |= ASTNode.IsElseStatementUnreachable;
      }
      if (this.thenStatement != null)
      {
         if (isConditionOptimizedFalse || ((this.bits & ASTNode.IsThenStatementUnreachable) != 0))
         {
            if (!isKnowDeadCodePattern(this.condition)
               || currentScope.compilerOptions().reportDeadCodeInTrivialIfStatement)
            {
               this.thenStatement.complainIfUnreachable(thenFlowInfo, currentScope, initialComplaintLevel);
            }
            else
            {
               // its a known coding pattern which should be tolerated by dead code analysis
               // according to isKnowDeadCodePattern()
               this.bits &= ~ASTNode.IsThenStatementUnreachable;
            }
         }
         thenFlowInfo = this.thenStatement.analyseCode(currentScope, flowContext, thenFlowInfo);
      }
      // code gen: optimizing the jump around the ELSE part
      if ((thenFlowInfo.tagBits & FlowInfo.UNREACHABLE_OR_DEAD) != 0)
      {
         this.bits |= ASTNode.ThenExit;
      }

      // process the ELSE part
      if (this.elseStatement != null)
      {
         // signal else clause unnecessarily nested, tolerate else-if code pattern
         if (thenFlowInfo == FlowInfo.DEAD_END && (this.bits & IsElseIfStatement) == 0 // else of an else-if
            && !(this.elseStatement instanceof IfStatement))
         {
            currentScope.problemReporter().unnecessaryElse(this.elseStatement);
         }
         if (isConditionOptimizedTrue || ((this.bits & ASTNode.IsElseStatementUnreachable) != 0))
         {
            if (!isKnowDeadCodePattern(this.condition)
               || currentScope.compilerOptions().reportDeadCodeInTrivialIfStatement)
            {
               this.elseStatement.complainIfUnreachable(elseFlowInfo, currentScope, initialComplaintLevel);
            }
            else
            {
               // its a known coding pattern which should be tolerated by dead code analysis
               // according to isKnowDeadCodePattern()
               this.bits &= ~ASTNode.IsElseStatementUnreachable;
            }
         }
         elseFlowInfo = this.elseStatement.analyseCode(currentScope, flowContext, elseFlowInfo);
      }
      // merge THEN & ELSE initializations
      FlowInfo mergedInfo =
         FlowInfo.mergedOptimizedBranchesIfElse(thenFlowInfo, isConditionOptimizedTrue, elseFlowInfo,
            isConditionOptimizedFalse, true /*
                                             * if(true){ return; } fake-reachable();
                                             */, flowInfo, this);
      return mergedInfo;
   }

   public StringBuffer printStatement(int indent, StringBuffer output)
   {
      printIndent(indent, output).append("if ("); //$NON-NLS-1$
      this.condition.printExpression(0, output).append(")\n"); //$NON-NLS-1$
      this.thenStatement.printStatement(indent + 2, output);
      if (this.elseStatement != null)
      {
         output.append('\n');
         printIndent(indent, output);
         output.append("else\n"); //$NON-NLS-1$
         this.elseStatement.printStatement(indent + 2, output);
      }
      return output;
   }

   public void resolve(BlockScope scope)
   {
      TypeBinding type = this.condition.resolveTypeExpecting(scope, TypeBinding.BOOLEAN);
      this.condition.computeConversion(scope, type, type);
      if (this.thenStatement != null)
         this.thenStatement.resolve(scope);
      if (this.elseStatement != null)
         this.elseStatement.resolve(scope);
   }

   public void traverse(ASTVisitor visitor, BlockScope blockScope)
   {
      if (visitor.visit(this, blockScope))
      {
         this.condition.traverse(visitor, blockScope);
         if (this.thenStatement != null)
            this.thenStatement.traverse(visitor, blockScope);
         if (this.elseStatement != null)
            this.elseStatement.traverse(visitor, blockScope);
      }
      visitor.endVisit(this, blockScope);
   }
}
