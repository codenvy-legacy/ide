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
public abstract class LazyIterator<T> implements Iterator<T>
{
   
   public static LazyIterator<Object> EMPTY_ITEMS_ITERATOR = new EmptyIterator();

   private static class EmptyIterator extends LazyIterator<Object>
   {
      /**
       * @see org.exoplatform.ide.vfs.LazyIterator#fetchNext()
       */
      @Override
      protected void fetchNext() 
      {
      }

      /**
       * @see org.exoplatform.ide.vfs.LazyIterator#size()
       */
      @Override
      public int size()
      {
         return 0;
      }
   }

   @SuppressWarnings("unchecked")
   public static <T> LazyIterator<T> emptyItemsIterator()
   {
      return (LazyIterator<T>)EMPTY_ITEMS_ITERATOR;
   }
   
   // -----------------------------------
   
   protected T next;

   /**
    * To fetch next item and set it in field <code>next</code>
    */
   protected abstract void fetchNext();

   /**
    * @see java.util.Iterator#hasNext()
    */
   public boolean hasNext()
   {
      return next != null;
   }

   /**
    * @see java.util.Iterator#next()
    */
   public T next()
   {
      if (next == null)
         throw new NoSuchElementException();
      T n = next;
      fetchNext();
      return n;
   }

   /**
    * @see java.util.Iterator#remove()
    */
   public void remove()
   {
      throw new UnsupportedOperationException("remove");
   }

   /**
    * Get total number of items in iterator. If not able determine number of
    * items then -1 will be returned.
    * 
    * @return number of items or -1
    */
   public int size()
   {
      return -1;
   }

   /**
    * Skip specified number of element in collection.
    * 
    * @param skip the number of items to skip
    * @throws NoSuchElementException if skipped past the last item in the
    *            iterator
    */
   public void skip(int skip) throws NoSuchElementException
   {
      while (skip-- > 0)
      {
         fetchNext();
         if (next == null)
            throw new NoSuchElementException();
      }
   }
}
