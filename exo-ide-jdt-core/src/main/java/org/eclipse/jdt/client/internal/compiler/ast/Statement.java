/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Stephan Herrmann - Contribution for bug 335093 - [compiler][null] minimal hook for future null annotation support
 *******************************************************************************/
package org.eclipse.jdt.client.internal.compiler.ast;

import org.eclipse.jdt.client.internal.compiler.codegen.BranchLabel;
import org.eclipse.jdt.client.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.client.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.client.internal.compiler.impl.Constant;
import org.eclipse.jdt.client.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.client.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.client.internal.compiler.lookup.Scope;
import org.eclipse.jdt.client.internal.compiler.lookup.TypeBinding;

public abstract class Statement extends ASTNode
{

   /**
    * Answers true if the if is identified as a known coding pattern which should be tolerated by dead code analysis. e.g. if
    * (DEBUG) print(); // no complaint Only invoked when overall condition is known to be optimizeable into false/true.
    */
   protected static boolean isKnowDeadCodePattern(Expression expression)
   {
      // if (!DEBUG) print(); - tolerated
      if (expression instanceof UnaryExpression)
      {
         expression = ((UnaryExpression)expression).expression;
      }
      // if (DEBUG) print(); - tolerated
      if (expression instanceof Reference)
         return true;

      return false;
   }

   public abstract FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo);

   public static final int NOT_COMPLAINED = 0;

   public static final int COMPLAINED_FAKE_REACHABLE = 1;

   public static final int COMPLAINED_UNREACHABLE = 2;

   /**
    * Empty hook for checking null status against declaration using null annotations, once this will be supported.
    */
   protected int checkAgainstNullAnnotation(BlockScope currentScope, LocalVariableBinding local, int nullStatus)
   {
      return nullStatus;
   }

   /** INTERNAL USE ONLY. This is used to redirect inter-statements jumps. */
   public void branchChainTo(BranchLabel label)
   {
      // do nothing by default
   }

   // Report an error if necessary (if even more unreachable than previously reported
   // complaintLevel = 0 if was reachable up until now, 1 if fake reachable (deadcode), 2 if fatal unreachable (error)
   public int complainIfUnreachable(FlowInfo flowInfo, BlockScope scope, int previousComplaintLevel)
   {
      if ((flowInfo.reachMode() & FlowInfo.UNREACHABLE) != 0)
      {
         if ((flowInfo.reachMode() & FlowInfo.UNREACHABLE_OR_DEAD) != 0)
            this.bits &= ~ASTNode.IsReachable;
         if (flowInfo == FlowInfo.DEAD_END)
         {
            if (previousComplaintLevel < COMPLAINED_UNREACHABLE)
            {
               scope.problemReporter().unreachableCode(this);
            }
            return COMPLAINED_UNREACHABLE;
         }
         else
         {
            if (previousComplaintLevel < COMPLAINED_FAKE_REACHABLE)
            {
               scope.problemReporter().fakeReachable(this);
            }
            return COMPLAINED_FAKE_REACHABLE;
         }
      }
      return previousComplaintLevel;
   }

   protected boolean isBoxingCompatible(TypeBinding expressionType, TypeBinding targetType, Expression expression,
      Scope scope)
   {
      if (scope.isBoxingCompatibleWith(expressionType, targetType))
         return true;

      return expressionType.isBaseType() // narrowing then boxing ?
         && !targetType.isBaseType()
         && !targetType.isTypeVariable()
         && scope.compilerOptions().sourceLevel >= org.eclipse.jdt.client.internal.compiler.classfmt.ClassFileConstants.JDK1_5 // autoboxing
         && expression.isConstantValueOfTypeAssignableToType(expressionType,
            scope.environment().computeBoxingType(targetType));
   }

   public boolean isEmptyBlock()
   {
      return false;
   }

   public boolean isValidJavaStatement()
   {
      // the use of this method should be avoid in most cases
      // and is here mostly for documentation purpose.....
      // while the parser is responsible for creating
      // welled formed expression statement, which results
      // in the fact that java-non-semantic-expression-used-as-statement
      // should not be parsed...thus not being built.
      // It sounds like the java grammar as help the compiler job in removing
      // -by construction- some statement that would have no effect....
      // (for example all expression that may do side-effects are valid statement
      // -this is an approximative idea.....-)

      return true;
   }

   public StringBuffer print(int indent, StringBuffer output)
   {
      return printStatement(indent, output);
   }

   public abstract StringBuffer printStatement(int indent, StringBuffer output);

   public abstract void resolve(BlockScope scope);

   /** Returns case constant associated to this statement (NotAConstant if none) */
   public Constant resolveCase(BlockScope scope, TypeBinding testType, SwitchStatement switchStatement)
   {
      // statement within a switch that are not case are treated as normal statement....
      resolve(scope);
      return Constant.NotAConstant;
   }

   /**
    * Implementation of {@link org.eclipse.jdt.client.internal.compiler.lookup.InvocationSite#expectedType} suitable at this
    * level. Subclasses should override as necessary.
    * 
    * @see org.eclipse.jdt.client.internal.compiler.lookup.InvocationSite#expectedType()
    */
   public TypeBinding expectedType()
   {
      return null;
   }
}
