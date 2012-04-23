/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.eclipse.jdt.client;

import org.eclipse.jdt.client.core.compiler.IProblem;
import org.exoplatform.ide.editor.problem.Problem;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 11:04:46 AM 34360 2009-07-22 23:58:59Z evgen $
 * 
 */
public class ProblemImpl implements Problem
{

   private IProblem problem;

   /**
    * @param problem
    */
   public ProblemImpl(IProblem problem)
   {
      super();
      this.problem = problem;
   }

   /**
    * @see org.exoplatform.ide.editor.problem.Problem#getID()
    */
   @Override
   public int getID()
   {
      return problem.getID();
   }

   /**
    * @see org.exoplatform.ide.editor.problem.Problem#getMessage()
    */
   @Override
   public String getMessage()
   {
      return problem.getMessage();
   }

   /**
    * @see org.exoplatform.ide.editor.problem.Problem#getLineNumber()
    */
   @Override
   public int getLineNumber()
   {
      if (problem.getSourceLineNumber() == 0)
         return 1;
      return problem.getSourceLineNumber();
   }

   /**
    * @see org.exoplatform.ide.editor.problem.Problem#getEnd()
    */
   @Override
   public int getEnd()
   {
      return problem.getSourceEnd();
   }

   /**
    * @see org.exoplatform.ide.editor.problem.Problem#getStart()
    */
   @Override
   public int getStart()
   {
      return problem.getSourceStart();
   }

   /**
    * @see org.exoplatform.ide.editor.problem.Problem#isError()
    */
   @Override
   public boolean isError()
   {
      return problem.isError();
   }

   /**
    * @see org.exoplatform.ide.editor.problem.Problem#isWarning()
    */
   @Override
   public boolean isWarning()
   {
      return problem.isWarning();
   }

   /**
    * @see org.exoplatform.ide.editor.problem.Problem#isBreakpoint()
    */
   @Override
   public boolean isBreakpoint()
   {
      return false;
   }

   @Override
   public boolean isCurrentBreakPoint()
   {
      return false;
   }

}
