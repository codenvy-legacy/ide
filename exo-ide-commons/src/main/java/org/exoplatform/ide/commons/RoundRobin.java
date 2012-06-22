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
package org.exoplatform.ide.commons;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple implementation of round-robin algorithm.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class RoundRobin<O>
{
   private final List<O> items;
   private final AtomicInteger position = new AtomicInteger();

   public RoundRobin(Collection<O> items)
   {
      this.items = new ArrayList<O>(items);
   }

   /**
    * Get next item.
    *
    * @return nex item
    */
   public O next()
   {
      return items.get(nextIndex());
   }

   private int nextIndex()
   {
      for (; ; )
      {
         int current = position.get();
         int next = current + 1;
         if (next >= items.size())
         {
            next = 0;
         }
         if (position.compareAndSet(current, next))
         {
            return current;
         }
      }
   }
}
