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
package org.exoplatform.ide.text;

/**
 * Default implementation of {@link org.eclipse.TypedRegion.text.ITypedRegion}. A <code>TypedRegion</code> is a value object.
 */
public class TypedRegionImpl extends RegionImpl implements TypedRegion
{

   /** The region's type */
   private String fType;

   /**
    * Creates a typed region based on the given specification.
    * 
    * @param offset the region's offset
    * @param length the region's length
    * @param type the region's type
    */
   public TypedRegionImpl(int offset, int length, String type)
   {
      super(offset, length);
      fType = type;
   }

   /* @see org.eclipse.jface.text.ITypedRegion#getType() */
   public String getType()
   {
      return fType;
   }

   /* @see java.lang.Object#equals(java.lang.Object) */
   public boolean equals(Object o)
   {
      if (o instanceof TypedRegionImpl)
      {
         TypedRegionImpl r = (TypedRegionImpl)o;
         return super.equals(r) && ((fType == null && r.getType() == null) || fType.equals(r.getType()));
      }
      return false;
   }

   /* @see java.lang.Object#hashCode() */
   public int hashCode()
   {
      int type = fType == null ? 0 : fType.hashCode();
      return super.hashCode() | type;
   }

   /*
    * @see org.eclipse.jface.text.Region#toString()
    * @since 3.5
    */
   public String toString()
   {
      return fType + " - " + super.toString(); //$NON-NLS-1$
   }

}
