/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.client.internal.corext.fix;

import org.eclipse.jdt.client.codeassistant.api.IProblemLocation;
import org.eclipse.jdt.client.core.dom.CompilationUnit;
import org.exoplatform.ide.editor.shared.text.IDocument;

public interface IMultiFix extends ICleanUp
{

   public class MultiFixContext extends CleanUpContext
   {

      private final IProblemLocation[] fLocations;

      public MultiFixContext(CompilationUnit ast, IDocument document, IProblemLocation[] locations)
      {
         super(ast, document);
         fLocations = locations;
      }

      /**
       * @return locations of problems to fix.
       */
      public IProblemLocation[] getProblemLocations()
      {
         return fLocations;
      }
   }

   /**
    * True if <code>problem</code> in <code>ICompilationUnit</code> can be
    * fixed by this CleanUp.
    * <p>
    * <strong>This must be a fast operation, the result can be a guess.</strong>
    * </p>
    *
    * @param compilationUnit
    *            The compilation unit to fix not null
    * @param problem
    *            The location of the problem to fix
    * @return True if problem can be fixed
    */
   public boolean canFix(IProblemLocation problem);

   /**
    * Maximal number of problems this clean up will fix in compilation unit.
    * There may be less then the returned number but never more.
    *
    * @param compilationUnit
    *            The compilation unit to fix, not null
    * @return The maximal number of fixes or -1 if unknown.
    */
   public abstract int computeNumberOfFixes(CompilationUnit compilationUnit);

}
