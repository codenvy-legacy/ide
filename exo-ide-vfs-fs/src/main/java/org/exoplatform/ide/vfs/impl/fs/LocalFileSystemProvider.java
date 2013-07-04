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
import java.util.concurrent.atomic.AtomicReference;

/**
 * Implementation of VirtualFileSystemProvider for plain file system.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class LocalFileSystemProvider implements VirtualFileSystemProvider {
    private static final Log LOG = ExoLogger.getLogger(LocalFileSystemProvider.class);

    private final String               workspaceId;
    private final LocalFSMountStrategy mountStrategy;
    private final SearcherProvider     searcherProvider;
    private final MountPointRef        mountRef;

    /**
     * @param vfsId
     *         virtual file system identifier
     * @param mountStrategy
     *         LocalFSMountStrategy
     * @see LocalFileSystemProvider
     */
    public LocalFileSystemProvider(String vfsId, LocalFSMountStrategy mountStrategy) {
        this(vfsId, mountStrategy, null);
    }

    /**
     * @param workspaceId
     *         virtual file system identifier
     * @param mountStrategy
     *         LocalFSMountStrategy
     * @param searcherProvider
     *         SearcherProvider
     * @see LocalFileSystemProvider
     */
    public LocalFileSystemProvider(String workspaceId, LocalFSMountStrategy mountStrategy, SearcherProvider searcherProvider) {
        this.workspaceId = workspaceId;
        this.mountStrategy = mountStrategy;
        this.searcherProvider = searcherProvider;
        this.mountRef = new MountPointRef();
    }

    /**
     * Get new instance of LocalFileSystem. If virtual file system is not mounted yet if mounted automatically when used
     * first time.
     */
    @Override
    public VirtualFileSystem newInstance(RequestContext requestContext, EventListenerList listeners)
            throws VirtualFileSystemException {
        MountPoint mount = mountRef.get();

        if (mount == null) {
            final java.io.File workspaceMountPoint;
            try {
                workspaceMountPoint = mountStrategy.getMountPath(workspaceId);
            } catch (VirtualFileSystemException e) {
                LOG.error(e.getMessage(), e);
                // critical error cannot continue
                throw new VirtualFileSystemException(String.format("Virtual filesystem '%s' is not available. ", workspaceId));
            }
            MountPoint newMount = new MountPoint(workspaceMountPoint, searcherProvider);
            if (mountRef.maybeSet(newMount)) {
                if (!(workspaceMountPoint.exists() || workspaceMountPoint.mkdirs())) {
                    LOG.error("Unable create directory {}", workspaceMountPoint);
                    // critical error cannot continue
                    throw new VirtualFileSystemException(String.format("Virtual filesystem '%s' is not available. ", workspaceId));
                }
                mount = newMount;
            }
        }
        return new LocalFileSystem(workspaceId,
                                   requestContext != null ? requestContext.getUriInfo().getBaseUri() : URI.create(""),
                                   listeners,
                                   mount,
                                   searcherProvider);
    }

    @Override
    public void close() {
        final MountPoint mount = mountRef.remove();
        if (mount != null) {
            mount.reset();
            if (searcherProvider != null) {
                try {
                    final Searcher searcher = searcherProvider.getSearcher(mount, false);
                    if (searcher != null) {
                        searcher.close();
                    }
                } catch (VirtualFileSystemException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Mount backing local filesystem.
     *
     * @param ioFile
     *         root point on the backing local filesystem
     * @throws VirtualFileSystemException
     *         if mount is failed, e.g. if specified <code>ioFile</code> already mounted
     * @see org.exoplatform.ide.vfs.server.VirtualFileSystem
     */
    public void mount(java.io.File ioFile) throws VirtualFileSystemException {
        if (!mountRef.maybeSet(new MountPoint(ioFile, searcherProvider))) {
            throw new VirtualFileSystemException(String.format("Local filesystem '%s' already mounted. ", ioFile));
        }
    }

    public boolean isMounted() throws VirtualFileSystemException {
        return mountRef.get() != null;
    }

    public MountPoint getMountPoint() throws VirtualFileSystemException {
        final MountPoint mount = mountRef.get();
        if (mount == null) {
            throw new VirtualFileSystemException(String.format("Virtual filesystem '%s' is not mounted yet. ", workspaceId));
        }
        return mount;
    }

    private static class MountPointRef {
        final AtomicReference<MountPoint> ref;

        private MountPointRef() {
            ref = new AtomicReference<MountPoint>();
        }

        boolean maybeSet(MountPoint mountPoint) {
            final boolean res = ref.compareAndSet(null, mountPoint);
            if (res) {
                MountPointCacheCleaner.add(mountPoint);
            }
            return res;
        }

        MountPoint get() {
            return ref.get();
        }

        MountPoint remove() {
            final MountPoint mountPoint = ref.getAndSet(null);
            if (mountPoint != null) {
                MountPointCacheCleaner.remove(mountPoint);
            }
            return mountPoint;
        }
    }
}
