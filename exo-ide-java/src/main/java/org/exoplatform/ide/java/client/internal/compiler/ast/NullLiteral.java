/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.exoplatform.ide.java.client.internal.compiler.ast;

import org.exoplatform.ide.java.client.internal.compiler.ASTVisitor;
import org.exoplatform.ide.java.client.internal.compiler.flow.FlowInfo;
import org.exoplatform.ide.java.client.internal.compiler.impl.Constant;
import org.exoplatform.ide.java.client.internal.compiler.lookup.BlockScope;
import org.exoplatform.ide.java.client.internal.compiler.lookup.TypeBinding;

public class NullLiteral extends MagicLiteral
{

   static final char[] source = {'n', 'u', 'l', 'l'};

   public NullLiteral(int s, int e)
   {

      super(s, e);
   }

   public void computeConstant()
   {

      this.constant = Constant.NotAConstant;
   }

   /**
    * Code generation for the null literal
    *
    * @param currentScope org.exoplatform.ide.java.client.internal.compiler.lookup.BlockScope
    * @param valueRequired boolean
    */
   public void generateCode(BlockScope currentScope, boolean valueRequired)
   {

   }

   public TypeBinding literalType(BlockScope scope)
   {
      return TypeBinding.NULL;
   }

   public int nullStatus(FlowInfo flowInfo)
   {
      return FlowInfo.NULL;
   }

   public Object reusableJSRTarget()
   {
      return TypeBinding.NULL;
   }

   public char[] source()
   {
      return source;
   }

   public void traverse(ASTVisitor visitor, BlockScope scope)
   {
      visitor.visit(this, scope);
      visitor.endVisit(this, scope);
   }
}
