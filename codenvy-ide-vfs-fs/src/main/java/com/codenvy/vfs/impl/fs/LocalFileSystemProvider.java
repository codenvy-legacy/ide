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
package com.codenvy.vfs.impl.fs;

import com.codenvy.api.vfs.server.RequestContext;
import com.codenvy.api.vfs.server.VirtualFileSystem;
import com.codenvy.api.vfs.server.VirtualFileSystemProvider;
import com.codenvy.api.vfs.server.VirtualFileSystemUserContext;
import com.codenvy.api.vfs.server.exceptions.VirtualFileSystemException;
import com.codenvy.api.vfs.server.exceptions.VirtualFileSystemRuntimeException;
import com.codenvy.api.vfs.server.observation.EventListenerList;
import com.codenvy.api.vfs.server.search.Searcher;
import com.codenvy.api.vfs.server.search.SearcherProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Implementation of VirtualFileSystemProvider for plain file system.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class LocalFileSystemProvider extends VirtualFileSystemProvider {
    private static final Logger LOG = LoggerFactory.getLogger(LocalFileSystemProvider.class);

    private final String                       workspaceId;
    private final LocalFSMountStrategy         mountStrategy;
    private final SearcherProvider             searcherProvider;
    private final MountPointRef                mountRef;
    private final VirtualFileSystemUserContext userContext;

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
        this(workspaceId, mountStrategy, searcherProvider, VirtualFileSystemUserContext.newInstance());
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
    public LocalFileSystemProvider(String workspaceId,
                                   LocalFSMountStrategy mountStrategy,
                                   SearcherProvider searcherProvider,
                                   VirtualFileSystemUserContext userContext) {
        super(workspaceId);
        this.workspaceId = workspaceId;
        this.mountStrategy = mountStrategy;
        this.searcherProvider = searcherProvider;
        this.userContext = userContext;
        this.mountRef = new MountPointRef();
    }

    /**
     * Get new instance of LocalFileSystem. If virtual file system is not mounted yet if mounted automatically when used
     * first time.
     */
    @Override
    public VirtualFileSystem newInstance(RequestContext requestContext, EventListenerList listeners) throws VirtualFileSystemException {
        return new LocalFileSystem(workspaceId,
                                   requestContext != null ? requestContext.getUriInfo().getBaseUri() : URI.create(""),
                                   listeners,
                                   userContext,
                                   getMountPoint(true),
                                   searcherProvider);
    }

    @Override
    public void close() {
        final FSMountPoint mount = mountRef.remove();
        if (mount != null) {
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
        super.close();
    }

    /**
     * Mount backing local filesystem.
     *
     * @param ioFile
     *         root point on the backing local filesystem
     * @throws VirtualFileSystemException
     *         if mount is failed, e.g. if specified <code>ioFile</code> already mounted
     * @see VirtualFileSystem
     */
    public void mount(java.io.File ioFile) throws VirtualFileSystemException {
        if (!mountRef.maybeSet(new FSMountPoint(getWorkspaceId(), ioFile, searcherProvider))) {
            throw new VirtualFileSystemException(String.format("Local filesystem '%s' already mounted. ", ioFile));
        }
    }

    public boolean isMounted() throws VirtualFileSystemException {
        return mountRef.get() != null;
    }

    @Override
    public FSMountPoint getMountPoint(boolean create) {
        FSMountPoint mount = mountRef.get();
        if (mount == null && create) {
            final java.io.File workspaceMountPoint;
            try {
                workspaceMountPoint = mountStrategy.getMountPath(workspaceId);
            } catch (VirtualFileSystemException e) {
                LOG.error(e.getMessage(), e);
                // critical error cannot continue
                throw new VirtualFileSystemRuntimeException(String.format("Virtual filesystem '%s' is not available. ", workspaceId));
            }
            FSMountPoint newMount = new FSMountPoint(workspaceId, workspaceMountPoint, searcherProvider);
            if (mountRef.maybeSet(newMount)) {
                if (!(workspaceMountPoint.exists() || workspaceMountPoint.mkdirs())) {
                    LOG.error("Unable create directory {}", workspaceMountPoint);
                    // critical error cannot continue
                    throw new VirtualFileSystemRuntimeException(String.format("Virtual filesystem '%s' is not available. ", workspaceId));
                }
                mount = newMount;
            }
        }
        return mount;
    }

    private static class MountPointRef {
        final AtomicReference<FSMountPoint> ref;

        private MountPointRef() {
            ref = new AtomicReference<>();
        }

        boolean maybeSet(FSMountPoint mountPoint) {
            final boolean res = ref.compareAndSet(null, mountPoint);
            if (res) {
                MountPointCacheCleaner.add(mountPoint);
            }
            return res;
        }

        FSMountPoint get() {
            return ref.get();
        }

        FSMountPoint remove() {
            final FSMountPoint mountPoint = ref.getAndSet(null);
            if (mountPoint != null) {
                MountPointCacheCleaner.remove(mountPoint);
            }
            return mountPoint;
        }
    }
}
