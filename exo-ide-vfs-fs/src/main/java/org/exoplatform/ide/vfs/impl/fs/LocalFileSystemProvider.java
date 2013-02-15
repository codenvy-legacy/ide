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
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

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
   private static final Log LOG = ExoLogger.getLogger(LocalFileSystemProvider.class);

   private final String id;
   private final LocalFSMountStrategy mountStrategy;
   private final ConcurrentMap<java.io.File, MountPoint> mounts;

   /**
    * @param id
    *    virtual file system identifier
    * @param mountStrategy
    *    LocalFSMountStrategy
    * @see LocalFileSystemProvider
    */
   public LocalFileSystemProvider(String id, LocalFSMountStrategy mountStrategy)
   {
      this.id = id;
      this.mountStrategy = mountStrategy;
      this.mounts = new ConcurrentHashMap<java.io.File, MountPoint>();
   }

   /**
    * Get new instance of LocalFileSystem. If virtual file system is not mounted yet if mounted automatically when used
    * first time.
    */
   @Override
   public VirtualFileSystem newInstance(RequestContext requestContext, EventListenerList listeners)
      throws VirtualFileSystemException
   {
      final java.io.File workspaceMountPoint;
      try
      {
         workspaceMountPoint = mountStrategy.getMountPath();
      }
      catch (VirtualFileSystemException e)
      {
         LOG.error(e.getMessage(), e);
         // critical error cannot continue
         throw new VirtualFileSystemException(String.format("Virtual filesystem '%s' is not available. ", id));
      }

      final java.io.File vfsIoRoot = new java.io.File(workspaceMountPoint, id);
      if (!(vfsIoRoot.exists() || vfsIoRoot.mkdirs()))
      {
         LOG.error("Unable create directory {}", vfsIoRoot);
         // critical error cannot continue
         throw new VirtualFileSystemException(String.format("Virtual filesystem '%s' is not available. ", id));
      }
      MountPoint mount = mounts.get(vfsIoRoot);
      if (mount == null)
      {
         MountPoint newMount = new MountPoint(vfsIoRoot);
         mount = mounts.putIfAbsent(vfsIoRoot, newMount);
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
    *    if mount is failed, e.g. if specified <code>ioFile</code> already mounted
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
   public boolean umount(java.io.File ioFile) throws VirtualFileSystemException
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
