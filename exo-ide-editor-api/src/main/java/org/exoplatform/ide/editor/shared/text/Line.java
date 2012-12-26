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
package org.exoplatform.ide.editor.shared.text;

/**
 * Describes a line as a particular number of characters beginning at a particular offset, consisting of a particular number of
 * characters, and being closed with a particular line delimiter.
 */
final class Line implements IRegion
{

   /** The offset of the line */
   public int offset;

   /** The length of the line */
   public int length;

   /** The delimiter of this line */
   public final String delimiter;

   /**
    * Creates a new Line.
    * 
    * @param offset the offset of the line
    * @param end the last including character offset of the line
    * @param delimiter the line's delimiter
    */
   public Line(int offset, int end, String delimiter)
   {
      this.offset = offset;
      this.length = (end - offset) + 1;
      this.delimiter = delimiter;
   }

   /**
    * Creates a new Line.
    * 
    * @param offset the offset of the line
    * @param length the length of the line
    */
   public Line(int offset, int length)
   {
      this.offset = offset;
      this.length = length;
      this.delimiter = null;
   }

   /* @see org.eclipse.jface.text.IRegion#getOffset() */
   public int getOffset()
   {
      return offset;
   }

   /* @see org.eclipse.jface.text.IRegion#getLength() */
   public int getLength()
   {
      return length;
   }
}
