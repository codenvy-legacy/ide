/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.google.collide.client.editor.folding;

import org.exoplatform.ide.editor.shared.runtime.Assert;
import org.exoplatform.ide.editor.shared.text.Position;
import org.exoplatform.ide.editor.shared.text.projection.IProjectionPosition;

/**
 * A base implementation of a {@link IProjectionPosition}.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: AbstractFoldRange.java Mar 18, 2013 12:18:43 PM azatsarynnyy $
 *
 */
public abstract class AbstractFoldRange implements IProjectionPosition
{

   /** The offset of the position */
   protected int offset;

   /** The length of the position */
   protected int length;

   /**
    * Creates a new fold range with the given offset and length.
    * 
    * @param offset the position offset, must be >= 0
    * @param length the position length, must be >= 0
    */
   public AbstractFoldRange(int offset, int length)
   {
      Assert.isTrue(offset >= 0);
      Assert.isTrue(length >= 0);
      this.offset = offset;
      this.length = length;
   }

   /**
    * Checks whether the given index is inside of this range's text range.
    * 
    * @param index the index to check
    * @return <code>true</code> if <code>index</code> is inside of this range
    */
   public boolean includes(int index)
   {
      return (this.offset <= index) && (index < this.offset + length);
   }

   /**
    * Checks whether the intersection of the given text range and the text range represented by this range is empty or not.
    * 
    * @param rangeOffset the offset of the range to check
    * @param rangeLength the length of the range to check
    * @return <code>true</code> if intersection is not empty
    */
   public boolean overlapsWith(int rangeOffset, int rangeLength)
   {
      int end = rangeOffset + rangeLength;
      int thisEnd = this.offset + this.length;

      if (rangeLength > 0)
      {
         if (this.length > 0)
            return this.offset < end && rangeOffset < thisEnd;
         return rangeOffset <= this.offset && this.offset < end;
      }

      if (this.length > 0)
         return this.offset <= rangeOffset && rangeOffset < thisEnd;
      return this.offset == rangeOffset;
   }

   /**
    * Returns the length of this range.
    * 
    * @return the length of this range
    */
   public int getLength()
   {
      return length;
   }

   /**
    * Returns the offset of this range.
    * 
    * @return the offset of this range
    */
   public int getOffset()
   {
      return offset;
   }

   /**
    * Changes the length of this range to the given length.
    * 
    * @param length the new length of this range
    */
   public void setLength(int length)
   {
      Assert.isTrue(length >= 0);
      this.length = length;
   }

   /**
    * Changes the offset of this range to the given offset.
    * 
    * @param offset the new offset of this range
    */
   public void setOffset(int offset)
   {
      Assert.isTrue(offset >= 0);
      this.offset = offset;
   }

   /**
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      return "offset: " + offset + ", length: " + length;
   }

   /**
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode()
   {
      return (offset << 24) | (length << 16);
   }

   /**
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object other)
   {
      if (other instanceof Position)
      {
         Position rp = (Position)other;
         return (rp.offset == offset) && (rp.length == length);
      }
      return super.equals(other);
   }

}
