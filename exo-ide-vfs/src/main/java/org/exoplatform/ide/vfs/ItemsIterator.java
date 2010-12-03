/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.vfs;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public interface ItemsIterator<E> extends Iterator<E>
{
   /**
    * Skip specified number of element in collection.
    * 
    * @param skip the number of items to skip
    * @throws NoSuchElementException if skipped past the last item in the
    *         iterator
    */
   void skip(int skip) throws NoSuchElementException;

   /**
    * Get total number of items in iterator. If not able determine number of
    * items then -1 will be returned.
    * 
    * @return number of items or -1
    */
   int size();
}
