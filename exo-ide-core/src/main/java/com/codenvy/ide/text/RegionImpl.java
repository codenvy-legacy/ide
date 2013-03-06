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
package com.codenvy.ide.text;

/**
 * The default implementation of the {@link Region} interface.
 */
public class RegionImpl implements Region
{

   /** The region offset */
   private int fOffset;

   /** The region length */
   private int fLength;

   /**
    * Create a new region.
    * 
    * @param offset the offset of the region
    * @param length the length of the region
    */
   public RegionImpl(int offset, int length)
   {
      fOffset = offset;
      fLength = length;
   }

   /* @see org.eclipse.jface.text.IRegion#getLength() */
   public int getLength()
   {
      return fLength;
   }

   /* @see org.eclipse.jface.text.IRegion#getOffset() */
   public int getOffset()
   {
      return fOffset;
   }

   /* @see java.lang.Object#equals(java.lang.Object) */
   public boolean equals(Object o)
   {
      if (o instanceof Region)
      {
         Region r = (Region)o;
         return r.getOffset() == fOffset && r.getLength() == fLength;
      }
      return false;
   }

   /* @see java.lang.Object#hashCode() */
   public int hashCode()
   {
      return (fOffset << 24) | (fLength << 16);
   }

   /* @see java.lang.Object#toString() */
   public String toString()
   {
      return "offset: " + fOffset + ", length: " + fLength; //$NON-NLS-1$ //$NON-NLS-2$;
   }
}
