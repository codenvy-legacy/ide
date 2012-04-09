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
package org.exoplatform.ide.extension.java.jdi.client;

import org.exoplatform.ide.editor.problem.Problem;
import org.exoplatform.ide.extension.java.jdi.shared.BreakPoint;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 11:39:59 AM Mar 28, 2012 evgen $
 * 
 */
public class EditorBreakPoint implements Problem
{

   private String message;

   private final BreakPoint breakPoint;

   /**
    * @param lineNumber
    * @param message
    */
   
   public EditorBreakPoint(BreakPoint breakPoint, String message)
   {
      this.breakPoint = breakPoint;
      this.message = message;
   }

   /**
    * @see org.exoplatform.ide.editor.problem.Problem#getID()
    */
   @Override
   public int getID()
   {
      return 0;
   }

   /**
    * @see org.exoplatform.ide.editor.problem.Problem#getMessage()
    */
   @Override
   public String getMessage()
   {
      return message;
   }

   /**
    * @see org.exoplatform.ide.editor.problem.Problem#getLineNumber()
    */
   @Override
   public int getLineNumber()
   {
      return breakPoint.getLocation().getLineNumber();
   }

   /**
    * @see org.exoplatform.ide.editor.problem.Problem#getEnd()
    */
   @Override
   public int getEnd()
   {
      return 0;
   }

   /**
    * @see org.exoplatform.ide.editor.problem.Problem#getStart()
    */
   @Override
   public int getStart()
   {
      return 0;
   }

   /**
    * @see org.exoplatform.ide.editor.problem.Problem#isError()
    */
   @Override
   public boolean isError()
   {
      return false;
   }

   /**
    * @see org.exoplatform.ide.editor.problem.Problem#isWarning()
    */
   @Override
   public boolean isWarning()
   {
      return false;
   }

   /**
    * @see org.exoplatform.ide.editor.problem.Problem#isBreakpoint()
    */
   @Override
   public boolean isBreakpoint()
   {
      return true;
   }

   /**
    * @return the breakPoint
    */
   public BreakPoint getBreakPoint()
   {
      return breakPoint;
   }

   @Override
   public boolean isCurrentBreakPoint()
   {
      return false;
   }

}
