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
package org.exoplatform.ide.vfs.server;

import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Registry for virtual file system providers.
 * 
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 * 
 * @see VirtualFileSystemFactory
 */
public class VirtualFileSystemRegistry
{
   private final ConcurrentMap<String, VirtualFileSystemProvider> providers =
      new ConcurrentHashMap<String, VirtualFileSystemProvider>();

   public void registerProvider(String vfsId, VirtualFileSystemProvider provider) throws VirtualFileSystemException
   {
      if (providers.putIfAbsent(vfsId, provider) != null)
      {
         throw new VirtualFileSystemException("Virtual file system " + vfsId + " already registered. ");
      }
   }

   public void unregisterProvider(String vfsId) throws VirtualFileSystemException
   {
      providers.remove(vfsId);
   }

   public VirtualFileSystemProvider getProvider(String vfsId) throws VirtualFileSystemException
   {
      VirtualFileSystemProvider provider = providers.get(vfsId);
      if (provider == null)
      {
         throw new VirtualFileSystemException("Virtual file system " + vfsId + " does not exist. ");
      }
      return provider;
   }

   public Collection<VirtualFileSystemProvider> getRegisteredProviders() throws VirtualFileSystemException
   {
      return Collections.unmodifiableCollection(providers.values());
   }
}
