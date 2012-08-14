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

import org.exoplatform.ide.editor.java.client.Breakpoint;
import org.exoplatform.ide.extension.java.jdi.shared.BreakPoint;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 11:39:59 AM Mar 28, 2012 evgen $
 * 
 */
public class EditorBreakPoint extends Breakpoint
{

   private String message;

   private final BreakPoint breakPoint;

   /**
    * @param lineNumber
    * @param message
    */

   public EditorBreakPoint(BreakPoint breakPoint, String message)
   {
      super(Type.BREAKPOINT, breakPoint.getLocation().getLineNumber(), message);
      this.breakPoint = breakPoint;

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
    * @return the breakPoint
    */
   public BreakPoint getBreakPoint()
   {
      return breakPoint;
   }

}
