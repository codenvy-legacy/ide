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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Segmented LRU cache. See for details <a href="http://en.wikipedia.org/wiki/Cache_algorithms#Segmented_LRU">Segmented
 * LRU cache</a>
 * <p/>
 * Implementation is threadsafe.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class SLRUCache<K, V> implements Cache<K, V>
{
   private final Map<K, V> protectedSegment;
   private final Map<K, V> probationarySegment;
   private final int protectedSize;
   private final int probationarySize;

   private int misses;
   private int protectedHits;
   private int probationaryHits;

   /**
    * @param protectedSize
    *    size of protected area.
    * @param probationarySize
    *    size of probationary area.
    */
   public SLRUCache(int protectedSize, int probationarySize)
   {
      this.protectedSize = protectedSize;
      this.probationarySize = probationarySize;
      protectedSegment = new LinkedHashMap<K, V>(SLRUCache.this.protectedSize + 1, 1.1f, true)
      {
         @Override
         protected boolean removeEldestEntry(Map.Entry<K, V> eldest)
         {
            if (size() > SLRUCache.this.protectedSize)
            {
               probationarySegment.put(eldest.getKey(), eldest.getValue());
               return true;
            }
            return false;
         }
      };
      probationarySegment = new LinkedHashMap<K, V>(SLRUCache.this.probationarySize + 1, 1.1f, false)
      {
         @Override
         protected boolean removeEldestEntry(Map.Entry<K, V> eldest)
         {
            return size() > SLRUCache.this.probationarySize;
         }
      };
   }

   @Override
   public synchronized V get(K key)
   {
      V value = protectedSegment.get(key);
      if (value != null)
      {
         protectedHits++;
         return value;
      }
      value = probationarySegment.remove(key);
      if (value == null)
      {
         misses++;
         return null;
      }
      probationaryHits++;
      protectedSegment.put(key, value);
      return value;
   }

   @Override
   public synchronized V put(K key, V value)
   {
      V oldValue1 = protectedSegment.remove(key);
      V oldValue2 = probationarySegment.put(key, value);
      return oldValue1 == null ? oldValue2 : oldValue1;
   }

   @Override
   public synchronized V remove(K key)
   {
      V oldValue = protectedSegment.remove(key);
      if (oldValue == null)
      {
         oldValue = probationarySegment.remove(key);
      }
      return oldValue;
   }

   @Override
   public synchronized boolean contains(K key)
   {
      return probationarySegment.containsKey(key) || protectedSegment.containsKey(key);
   }

   @Override
   public synchronized void clear()
   {
      protectedSegment.clear();
      probationarySegment.clear();
   }

   public synchronized void printStats()
   {
      System.out.println("-------------------------------------------");
      System.out.printf("misses:            %d\n", misses);
      System.out.printf("protected hits:    %d\n", protectedHits);
      System.out.printf("probationary hits: %d\n", probationaryHits);
      System.out.println("-------------------------------------------");
   }
}