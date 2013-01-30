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

import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Make accessible backing local filesystem over virtual filesystem API.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class LocalFileSystemMounter
{
   private final VirtualFileSystemRegistry virtualFileSystemRegistry;
   private final ConcurrentMap<java.io.File, String> mounts;

   public LocalFileSystemMounter(VirtualFileSystemRegistry virtualFileSystemRegistry)
   {
      this.virtualFileSystemRegistry = virtualFileSystemRegistry;
      this.mounts = new ConcurrentHashMap<java.io.File, String>();
   }

   /**
    * Mount backing local filesystem.
    *
    * @param ioFile
    *    root point on the backing local filesystem
    * @param vfsId
    *    unique id for virtual filesystem
    * @throws VirtualFileSystemException
    *    if mount is failed, e.g. if virtual filesystem with specified <code>vfsId</code> already exists
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem
    */
   public void mount(java.io.File ioFile, String vfsId) throws VirtualFileSystemException
   {
      if (mounts.putIfAbsent(ioFile, vfsId) != null)
      {
         throw new VirtualFileSystemException(
            String.format("Local filesystem '%s' already mounted to '%s'. ", ioFile, vfsId));
      }
      virtualFileSystemRegistry.registerProvider(vfsId, new LocalFileSystemProvider(vfsId, new MountPoint(ioFile)));
   }

   /**
    * Unmount backing local filesystem.
    *
    * @param ioFile
    *    root point on the backing local filesystem
    * @return <code>true</code> if specified local file system path successfully unmounted and <code>false</code> if
    *         specified path was not mounted
    * @throws VirtualFileSystemException
    *    if any error occurs when try unmount
    */
   public boolean unmount(java.io.File ioFile) throws VirtualFileSystemException
   {
      String vfsId = mounts.remove(ioFile);
      return vfsId != null && virtualFileSystemRegistry.unregisterProvider(vfsId);
   }
}
