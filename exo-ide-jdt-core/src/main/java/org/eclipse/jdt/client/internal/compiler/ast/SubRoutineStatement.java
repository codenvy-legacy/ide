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


/** Extra behavior for statements which are generating subroutines */
public abstract class SubRoutineStatement extends Statement
{


   public void exitAnyExceptionHandler()
   {
   }

   public abstract boolean isSubRoutineEscaping();

}
