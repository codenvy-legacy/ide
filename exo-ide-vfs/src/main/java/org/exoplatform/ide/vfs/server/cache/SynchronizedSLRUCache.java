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
package org.exoplatform.ide.vfs.server.cache;

/**
 * Concurrent segmented LRU cache. See for details <a href="http://en.wikipedia.org/wiki/Cache_algorithms#Segmented_LRU">Segmented
 * LRU cache</a>.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 * @see SLRUCache
 */
public final class SynchronizedSLRUCache<K, V> implements Cache<K, V>
{
   private final SLRUCache<K, V> delegate;

   /**
    * @param protectedSize
    *    size of protected area.
    * @param probationarySize
    *    size of probationary area.
    */
   @SuppressWarnings("unchecked")
   public SynchronizedSLRUCache(int protectedSize, int probationarySize)
   {
      delegate = new SLRUCache<K, V>(protectedSize, probationarySize);
   }

   @Override
   public synchronized V get(K key)
   {
      return delegate.get(key);
   }

   @Override
   public synchronized V put(K key, V value)
   {
      return delegate.put(key, value);
   }

   @Override
   public synchronized V remove(K key)
   {
      return delegate.remove(key);
   }

   @Override
   public synchronized boolean contains(K key)
   {
      return delegate.contains(key);
   }

   @Override
   public synchronized void clear()
   {
      delegate.clear();
   }
}
