/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
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
