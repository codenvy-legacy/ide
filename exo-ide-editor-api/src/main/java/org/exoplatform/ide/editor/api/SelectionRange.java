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
package org.exoplatform.ide.editor.api;

import org.exoplatform.ide.editor.text.IRegion;

/**
 * Range of the selection in editor.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Apr 6, 2012 3:30:42 PM anya $
 * @deprecated Use {@link IRegion} instead
 */
public class SelectionRange
{
   /**
    * Selection's start line.
    */
   private int startLine;

   /**
    * Selection's start symbol.
    */
   private int startSymbol;

   /**
    * Selection's end line.
    */
   private int endLine;

   /**
    * Selection's end symbol.
    */
   private int endSymbol;

   /**
    * @param startLine
    * @param startSymbol
    * @param endLine
    * @param endSymbol
    */
   public SelectionRange(int startLine, int startSymbol, int endLine, int endSymbol)
   {
      this.startLine = startLine;
      this.startSymbol = startSymbol;
      this.endLine = endLine;
      this.endSymbol = endSymbol;
   }

   /**
    * @return the startLine
    */
   public int getStartLine()
   {
      return startLine;
   }

   /**
    * @return the startSymbol
    */
   public int getStartSymbol()
   {
      return startSymbol;
   }

   /**
    * @return the endLine
    */
   public int getEndLine()
   {
      return endLine;
   }

   /**
    * @return the endSymbol
    */
   public int getEndSymbol()
   {
      return endSymbol;
   }
   
}
