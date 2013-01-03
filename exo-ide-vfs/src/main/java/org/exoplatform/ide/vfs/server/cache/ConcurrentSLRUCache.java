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
 */
public final class ConcurrentSLRUCache<K, V> implements Cache<K, V>
{
   private final int mask;
   private final SLRUCache<K, V>[] partitions;

   /**
    * @param partitionsNum
    *    number of partitions. Cache are partitioned to try to permit the specified number of concurrent access without
    *    contention.
    * @param protectedSize
    *    size of protected area.
    * @param probationarySize
    *    size of probationary area.
    */
   @SuppressWarnings("unchecked")
   public ConcurrentSLRUCache(int partitionsNum, int protectedSize, int probationarySize)
   {
      int p = 1;
      while (p < partitionsNum)
      {
         p <<= 2;
      }
      final int partitionProtectedSize = protectedSize / p;
      final int partitionProbationarySize = probationarySize / p;
      partitions = new SLRUCache[p];
      for (int i = 0; i < partitions.length; i++)
      {
         partitions[i] = new SLRUCache<K, V>(partitionProtectedSize, partitionProbationarySize);
      }
      mask = p - 1;
   }

   @Override
   public V get(K key)
   {
      if (key == null)
      {
         throw new IllegalArgumentException("Null key is not allowed. ");
      }
      return partitions[key.hashCode() & mask].get(key);
   }

   @Override
   public V put(K key, V value)
   {
      if (key == null)
      {
         throw new IllegalArgumentException("Null key is not allowed. ");
      }
      if (value == null)
      {
         throw new IllegalArgumentException("Null value is not allowed. ");
      }
      return partitions[key.hashCode() & mask].put(key, value);
   }

   @Override
   public V remove(K key)
   {
      if (key == null)
      {
         throw new IllegalArgumentException("Null key is not allowed. ");
      }
      return partitions[key.hashCode() & mask].remove(key);
   }

   @Override
   public boolean contains(K key)
   {
      return key != null && partitions[key.hashCode() & mask].contains(key);
   }

   @Override
   public void clear()
   {
      for (SLRUCache<K, V> partition : partitions)
      {
         partition.clear();
      }
   }
}
