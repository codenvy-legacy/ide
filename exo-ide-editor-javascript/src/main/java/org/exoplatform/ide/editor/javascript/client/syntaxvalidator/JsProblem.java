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
package org.exoplatform.ide.editor.javascript.client.syntaxvalidator;

import org.exoplatform.ide.editor.client.marking.Marker;

/**
 * Class represents JavaScript problem, as detected by the parser.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.com">Artem Zatsarynnyy</a>
 * @version $Id: JsProblem.java Sep 18, 2012 5:01:17 PM azatsarynnyy $
 *
 */
public class JsProblem implements Marker
{

   /**
    * Native JavaScript error.
    */
   private JsError jsError;

   /**
    * The start position of the problem.
    */
   private int start;

   /**
    * The end position of the problem.
    */
   private int end;

   public JsProblem(JsError jsError, int start, int end)
   {
      this.jsError = jsError;
      this.start = start;
      this.end = end;
   }

   /**
    * @see org.exoplatform.ide.editor.client.marking.Marker#getID()
    */
   @Override
   public int getID()
   {
      return 0;
   }

   /**
    * @see org.exoplatform.ide.editor.client.marking.Marker#getMessage()
    */
   @Override
   public String getMessage()
   {
      // Original message has the follow form: "Line 45: Error message".
      // Cut the line number prefix.
      String errorMessage = jsError.getMessage();
      int colonIndex = errorMessage.indexOf(":");
      return errorMessage.substring(colonIndex + 2);
   }

   /**
    * @see org.exoplatform.ide.editor.client.marking.Marker#getLineNumber()
    */
   @Override
   public int getLineNumber()
   {
      return jsError.getLineNumber();
   }

   /**
    * @see org.exoplatform.ide.editor.client.marking.Marker#getEnd()
    */
   @Override
   public int getEnd()
   {
      return end;
   }

   /**
    * @see org.exoplatform.ide.editor.client.marking.Marker#getStart()
    */
   @Override
   public int getStart()
   {
      return start;
   }

   /**
    * @see org.exoplatform.ide.editor.client.marking.Marker#isError()
    */
   @Override
   public boolean isError()
   {
      return true;
   }

   /**
    * @see org.exoplatform.ide.editor.client.marking.Marker#isWarning()
    */
   @Override
   public boolean isWarning()
   {
      return false;
   }

   /**
    * @see org.exoplatform.ide.editor.client.marking.Marker#isBreakpoint()
    */
   @Override
   public boolean isBreakpoint()
   {
      return false;
   }

   /**
    * @see org.exoplatform.ide.editor.client.marking.Marker#isCurrentBreakPoint()
    */
   @Override
   public boolean isCurrentBreakPoint()
   {
      return false;
   }

}
