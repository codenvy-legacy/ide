/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.text.edits;

import org.exoplatform.ide.text.BadLocationException;
import org.exoplatform.ide.text.Document;

/**
 * A range marker can be used to track positions when executing text edits.
 * 
 */
public final class RangeMarker extends TextEdit
{

   /**
    * Creates a new range marker for the given offset and length.
    * 
    * @param offset the marker's offset
    * @param length the marker's length
    */
   public RangeMarker(int offset, int length)
   {
      super(offset, length);
   }

   /* Copy constructor */
   private RangeMarker(RangeMarker other)
   {
      super(other);
   }

   /* @see TextEdit#copy */
   protected TextEdit doCopy()
   {
      return new RangeMarker(this);
   }

   /* @see TextEdit#accept0 */
   protected void accept0(TextEditVisitor visitor)
   {
      boolean visitChildren = visitor.visit(this);
      if (visitChildren)
      {
         acceptChildren(visitor);
      }
   }

   /* @see TextEdit#performDocumentUpdating */
   int performDocumentUpdating(Document document) throws BadLocationException
   {
      fDelta = 0;
      return fDelta;
   }

   /* @see TextEdit#deleteChildren */
   boolean deleteChildren()
   {
      return false;
   }
}
