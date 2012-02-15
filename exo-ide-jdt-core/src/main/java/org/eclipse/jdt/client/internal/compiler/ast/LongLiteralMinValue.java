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

import org.eclipse.jdt.client.internal.compiler.impl.LongConstant;

public class LongLiteralMinValue extends LongLiteral
{

   public LongLiteralMinValue(char[] token, char[] reducedForm, int start, int end)
   {
      super(token, reducedForm, start, end);
      this.constant = LongConstant.fromValue(Long.MIN_VALUE);
   }

   public void computeConstant()
   {
      /* precomputed at creation time */}
}
