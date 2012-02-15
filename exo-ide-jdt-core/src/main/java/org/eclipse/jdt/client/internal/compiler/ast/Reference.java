/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Stephan Herrmann <stephan@cs.tu-berlin.de> - Contribution for bug 185682 - Increment/decrement operators mark local variables as read
 *******************************************************************************/
package org.eclipse.jdt.client.internal.compiler.ast;

import org.eclipse.jdt.client.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.client.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.client.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.client.internal.compiler.lookup.FieldBinding;

public abstract class Reference extends Expression
{
   /** BaseLevelReference constructor comment. */
   public Reference()
   {
      super();
   }

   public abstract FlowInfo analyseAssignment(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo,
      Assignment assignment, boolean isCompound);

   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo)
   {
      return flowInfo;
   }

   public FieldBinding fieldBinding()
   {
      // this method should be sent one FIELD-tagged references
      // (ref.bits & BindingIds.FIELD != 0)()
      return null;
   }

}
