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

import org.eclipse.jdt.client.internal.compiler.codegen.ExceptionLabel;

/** Extra behavior for statements which are generating subroutines */
public abstract class SubRoutineStatement extends Statement
{

   ExceptionLabel anyExceptionLabel;

   public void exitAnyExceptionHandler()
   {
      if (this.anyExceptionLabel != null)
      {
         this.anyExceptionLabel.placeEnd();
      }
   }

   public abstract boolean isSubRoutineEscaping();

   public void placeAllAnyExceptionHandler()
   {
      this.anyExceptionLabel.place();
   }
}
