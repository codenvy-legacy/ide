/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.eclipse.jdt.internal.corext;

import com.codenvy.eclipse.jdt.core.ISourceRange;
import com.codenvy.eclipse.jdt.core.SourceRange;
import com.codenvy.eclipse.jdt.core.compiler.IProblem;
import com.codenvy.eclipse.jdt.core.dom.ASTNode;


public class SourceRangeFactory
{

   public static ISourceRange create(ASTNode node)
   {
      return new SourceRange(node.getStartPosition(), node.getLength());
   }

   public static ISourceRange create(IProblem problem)
   {
      return new SourceRange(problem.getSourceStart(), problem.getSourceEnd() - problem.getSourceStart() + 1);
   }

}
