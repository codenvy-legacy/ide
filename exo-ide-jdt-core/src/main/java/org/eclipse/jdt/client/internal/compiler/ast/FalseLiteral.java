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
package org.eclipse.jdt.client.internal.compiler.ast;

import org.eclipse.jdt.client.internal.compiler.ASTVisitor;
import org.eclipse.jdt.client.internal.compiler.impl.BooleanConstant;
import org.eclipse.jdt.client.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.client.internal.compiler.lookup.TypeBinding;

public class FalseLiteral extends MagicLiteral
{

   static final char[] source = {'f', 'a', 'l', 's', 'e'};

   public FalseLiteral(int s, int e)
   {
      super(s, e);
   }

   public void computeConstant()
   {
      this.constant = BooleanConstant.fromValue(false);
   }

   public TypeBinding literalType(BlockScope scope)
   {
      return TypeBinding.BOOLEAN;
   }

   /**
    *
    */
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
