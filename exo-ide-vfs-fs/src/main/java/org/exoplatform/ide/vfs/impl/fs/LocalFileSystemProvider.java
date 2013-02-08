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

import org.exoplatform.ide.vfs.server.RequestContext;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemProvider;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.server.observation.EventListenerList;
import org.exoplatform.services.security.ConversationState;

import java.net.URI;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Implementation of VirtualFileSystemProvider for plain file system.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class LocalFileSystemProvider implements VirtualFileSystemProvider
{
   private final String id;
   private final ConcurrentMap<java.io.File, MountPoint> mounts;
   private final java.io.File mountRoot;

   /**
    * @param id
    *    virtual file system identifier
    */
   public LocalFileSystemProvider(String id)
   {
      this.id = id;
      this.mounts = new ConcurrentHashMap<java.io.File, MountPoint>();
      this.mountRoot = new java.io.File(System.getProperty("org.exoplatform.ide.server.fs-root-path"));
   }

   /**
    * Get new instance of LocalFileSystem. If virtual file system is not mounted yet if mounted automatically when used
    * first time.
    */
   @Override
   public VirtualFileSystem newInstance(RequestContext requestContext, EventListenerList listeners)
      throws VirtualFileSystemException
   {
      // TODO : this is temporary solution. Waiting when cloud infrastructure will provide something better for us.
      final String wsName = (String)ConversationState.getCurrent().getAttribute("currentTenant");
      final java.io.File wsRoot = new java.io.File(mountRoot, wsName);
      if (!(wsRoot.exists() || wsRoot.mkdirs()))
      {
         // critical error cannot continue
         throw new VirtualFileSystemException(String.format("Virtual filesystem '%s' is not available. ", id));
      }
      MountPoint mount = mounts.get(wsRoot);
      if (mount == null)
      {
         MountPoint newMount = new MountPoint(wsRoot);
         mount = mounts.putIfAbsent(wsRoot, newMount);
         if (mount == null)
         {
            mount = newMount;
         }
      }
      return new LocalFileSystem(id,
         requestContext != null ? requestContext.getUriInfo().getBaseUri() : URI.create(""),
         listeners,
         mount);
   }

   /**
    * Mount backing local filesystem.
    *
    * @param ioFile
    *    root point on the backing local filesystem
    * @throws VirtualFileSystemException
    *    if mount is failed, e.g. if virtual filesystem with specified <code>vfsId</code> already exists
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem
    */
   public void mount(java.io.File ioFile) throws VirtualFileSystemException
   {
      if (mounts.putIfAbsent(ioFile, new MountPoint(ioFile)) != null)
      {
         throw new VirtualFileSystemException(String.format("Local filesystem '%s' already mounted. ", ioFile));
      }
   }

   /**
    * Unmount backing local filesystem. This method release resources allocated by MountPoint.
    *
    * @param ioFile
    *    root point on the backing local filesystem
    * @return <code>true</code> if specified local file system path successfully unmounted and <code>false</code> if
    *         specified path was not mounted
    */
   public boolean unmount(java.io.File ioFile) throws VirtualFileSystemException
   {
      final MountPoint mount = mounts.remove(ioFile);
      if (mount != null)
      {
         mount.reset();
         return true;
      }
      return false;
   }

   public Collection<MountPoint> getMounts()
   {
      return java.util.Collections.unmodifiableCollection(mounts.values());
   }
}
