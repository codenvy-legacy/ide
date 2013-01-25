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
package com.codenvy.eclipse.jdt.internal.corext.refactoring.util;

import com.codenvy.eclipse.core.runtime.CoreException;
import com.codenvy.eclipse.jdt.core.ICompilationUnit;
import com.codenvy.eclipse.jdt.core.dom.ASTNode;
import com.codenvy.eclipse.jdt.core.dom.ArrayInitializer;
import com.codenvy.eclipse.jdt.internal.corext.dom.Selection;
import com.codenvy.eclipse.jdt.internal.corext.refactoring.RefactoringCoreMessages;
import com.codenvy.eclipse.jdt.internal.corext.refactoring.base.JavaStatusContext;
import com.codenvy.eclipse.ltk.core.refactoring.RefactoringStatus;

public class CodeAnalyzer extends StatementAnalyzer
{

   public CodeAnalyzer(ICompilationUnit cunit, Selection selection, boolean traverseSelectedNode) throws CoreException
   {
      super(cunit, selection, traverseSelectedNode);
   }

   @Override
   protected final void checkSelectedNodes()
   {
      super.checkSelectedNodes();
      RefactoringStatus status = getStatus();
      if (status.hasFatalError())
      {
         return;
      }
      ASTNode node = getFirstSelectedNode();
      if (node instanceof ArrayInitializer)
      {
         status.addFatalError(RefactoringCoreMessages.CodeAnalyzer_array_initializer,
            JavaStatusContext.create(fCUnit, node));
      }
   }
}
