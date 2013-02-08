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
package org.exoplatform.ide.vfs.impl.fs;

import org.exoplatform.ide.vfs.server.cache.SLRUCache;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemRuntimeException;

/**
 * SLRUCache that loads value for key if it is not cached yet.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 * @see SLRUCache
 */
public abstract class LoadingValueSLRUCache<K, V> extends SLRUCache<K, V>
{
   /**
    * @param protectedSize
    *    size of protected area.
    * @param probationarySize
    *    size of probationary area.
    */
   public LoadingValueSLRUCache(int protectedSize, int probationarySize)
   {
      super(protectedSize, probationarySize);
   }

   @Override
   public V get(K key)
   {
      V value = super.get(key);
      if (value != null)
      {
         return value;
      }
      try
      {
         value = loadValue(key);
      }
      catch (VirtualFileSystemException e)
      {
         throw new VirtualFileSystemRuntimeException(e.getMessage(), e); // re-throw as RuntimeException
      }
      put(key, value);
      return value;
   }

   protected abstract V loadValue(K key) throws VirtualFileSystemException;
}
