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
package org.eclipse.jdt.client.internal.compiler.ast;

import org.eclipse.jdt.client.internal.compiler.ASTVisitor;
import org.eclipse.jdt.client.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.client.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.client.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.client.internal.compiler.flow.InsideSubRoutineFlowContext;
import org.eclipse.jdt.client.internal.compiler.impl.Constant;
import org.eclipse.jdt.client.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.client.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.client.internal.compiler.lookup.TypeBinding;

public class SynchronizedStatement extends SubRoutineStatement
{

   public Expression expression;

   public Block block;

   public BlockScope scope;

   public LocalVariableBinding synchroVariable;

   static final char[] SecretLocalDeclarationName = " syncValue".toCharArray(); //$NON-NLS-1$

   public SynchronizedStatement(Expression expression, Block statement, int s, int e)
   {

      this.expression = expression;
      this.block = statement;
      this.sourceEnd = e;
      this.sourceStart = s;
   }

   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo)
   {

      // TODO (philippe) shouldn't it be protected by a check whether reachable statement ?

      // mark the synthetic variable as being used
      this.synchroVariable.useFlag = LocalVariableBinding.USED;

      // simple propagation to subnodes
      flowInfo =
         this.block.analyseCode(this.scope, new InsideSubRoutineFlowContext(flowContext, this),
            this.expression.analyseCode(this.scope, flowContext, flowInfo));

      // optimizing code gen
      if ((flowInfo.tagBits & FlowInfo.UNREACHABLE_OR_DEAD) != 0)
      {
         this.bits |= ASTNode.BlockExit;
      }

      return flowInfo;
   }

   public boolean isSubRoutineEscaping()
   {
      return false;
   }

   public void resolve(BlockScope upperScope)
   {
      // special scope for secret locals optimization.
      this.scope = new BlockScope(upperScope);
      TypeBinding type = this.expression.resolveType(this.scope);
      if (type == null)
         return;
      switch (type.id)
      {
         case T_boolean :
         case T_char :
         case T_float :
         case T_double :
         case T_byte :
         case T_short :
         case T_int :
         case T_long :
            this.scope.problemReporter().invalidTypeToSynchronize(this.expression, type);
            break;
         case T_void :
            this.scope.problemReporter().illegalVoidExpression(this.expression);
            break;
         case T_null :
            this.scope.problemReporter().invalidNullToSynchronize(this.expression);
            break;
      }
      // continue even on errors in order to have the TC done into the statements
      this.synchroVariable =
         new LocalVariableBinding(SecretLocalDeclarationName, type, ClassFileConstants.AccDefault, false);
      this.scope.addLocalVariable(this.synchroVariable);
      this.synchroVariable.setConstant(Constant.NotAConstant); // not inlinable
      this.expression.computeConversion(this.scope, type, type);
      this.block.resolveUsing(this.scope);
   }

   public StringBuffer printStatement(int indent, StringBuffer output)
   {
      printIndent(indent, output);
      output.append("synchronized ("); //$NON-NLS-1$
      this.expression.printExpression(0, output).append(')');
      output.append('\n');
      return this.block.printStatement(indent + 1, output);
   }

   public void traverse(ASTVisitor visitor, BlockScope blockScope)
   {
      if (visitor.visit(this, blockScope))
      {
         this.expression.traverse(visitor, this.scope);
         this.block.traverse(visitor, this.scope);
      }
      visitor.endVisit(this, blockScope);
   }
}
