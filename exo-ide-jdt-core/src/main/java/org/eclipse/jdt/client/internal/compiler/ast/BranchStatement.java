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

import org.eclipse.jdt.client.internal.compiler.lookup.BlockScope;

public abstract class BranchStatement extends Statement
{

   public char[] label;

   public SubRoutineStatement[] subroutines;

   /** BranchStatement constructor comment. */
   public BranchStatement(char[] label, int sourceStart, int sourceEnd)
   {
      this.label = label;
      this.sourceStart = sourceStart;
      this.sourceEnd = sourceEnd;
   }

   public void resolve(BlockScope scope)
   {
      // nothing to do during name resolution
   }
}
