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
 * A region describes a certain range in an indexed text store. Text stores are for example documents or strings. A region is
 * defined by its offset into the text store and its length.
 * <p>
 * A region is considered a value object. Its offset and length do not change over time.
 * <p>
 * Clients may implement this interface or use the standard implementation {@link RegionImpl}.
 * </p>
 */
public interface Region
{

   /**
    * Returns the length of the region.
    * 
    * @return the length of the region
    */
   int getLength();

   /**
    * Returns the offset of the region.
    * 
    * @return the offset of the region
    */
   int getOffset();
}
