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
package org.exoplatform.ide.vfs.shared;

import java.util.List;

/**
 * Set of abstract items for paging view.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface ItemList<T extends Item>
{
   /** @return set of items */
   List<T> getItems();

   void setItems(List<T> list);

   /**
    * @return total number of items. It is not need to be equals to number of items in current list {@link #getItems()}.
    *         It may be equals to number of items in current list only if this list contains all requested items and no
    *         more pages available. This method must return -1 if total number of items in unknown.
    */
   int getNumItems();

   void setNumItems(int numItems);

   /** @return <code>false</code> if this is last sub-set of items in paging */
   boolean isHasMoreItems();

   void setHasMoreItems(boolean hasMoreItems);
}
