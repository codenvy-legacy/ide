/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.vfs.impl.fs;

import com.codenvy.api.core.notification.EventService;
import com.codenvy.api.vfs.server.VirtualFileSystem;
import com.codenvy.api.vfs.server.VirtualFileSystemProvider;
import com.codenvy.api.vfs.server.VirtualFileSystemRegistry;
import com.codenvy.api.vfs.server.VirtualFileSystemUserContext;
import com.codenvy.api.vfs.server.exceptions.VirtualFileSystemException;
import com.codenvy.api.vfs.server.exceptions.VirtualFileSystemRuntimeException;
import com.codenvy.api.vfs.server.search.Searcher;
import com.codenvy.api.vfs.server.search.SearcherProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Implementation of VirtualFileSystemProvider for plain file system.
 *
 * @author andrew00x
 */
public class LocalFileSystemProvider extends VirtualFileSystemProvider {
    private static final Logger LOG = LoggerFactory.getLogger(LocalFileSystemProvider.class);

    private final String                       workspaceId;
    private final LocalFSMountStrategy         mountStrategy;
    private final EventService                 eventService;
    private final SearcherProvider             searcherProvider;
    private final MountPointRef                mountRef;
    private final VirtualFileSystemUserContext userContext;
    private final VirtualFileSystemRegistry    vfsRegistry;

    /**
     * @param workspaceId
     *         virtual file system identifier
     * @param mountStrategy
     *         LocalFSMountStrategy
     * @param searcherProvider
     *         SearcherProvider or {@code null}
     * @see LocalFileSystemProvider
     */
    public LocalFileSystemProvider(String workspaceId,
                                   LocalFSMountStrategy mountStrategy,
                                   EventService eventService,
                                   SearcherProvider searcherProvider,
                                   VirtualFileSystemRegistry vfsRegistry) {
        this(workspaceId, mountStrategy, eventService, searcherProvider, VirtualFileSystemUserContext.newInstance(), vfsRegistry);
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
    protected LocalFileSystemProvider(String workspaceId,
                                      LocalFSMountStrategy mountStrategy,
                                      EventService eventService,
                                      SearcherProvider searcherProvider,
                                      VirtualFileSystemUserContext userContext,
                                      VirtualFileSystemRegistry vfsRegistry) {
        super(workspaceId);
        this.workspaceId = workspaceId;
        this.mountStrategy = mountStrategy;
        this.eventService = eventService;
        this.searcherProvider = searcherProvider;
        this.userContext = userContext;
        this.mountRef = new MountPointRef();
        this.vfsRegistry = vfsRegistry;
    }

    /** Get new instance of LocalFileSystem. If virtual file system is not mounted yet if mounted automatically when used first time. */
    @Override
    public VirtualFileSystem newInstance(URI baseUri) throws VirtualFileSystemException {
        return new LocalFileSystem(workspaceId,
                                   baseUri == null ? URI.create("") : baseUri,
                                   userContext,
                                   getMountPoint(true),
                                   searcherProvider, vfsRegistry);
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
        if (!mountRef.maybeSet(new FSMountPoint(getWorkspaceId(), ioFile, eventService, searcherProvider))) {
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
            FSMountPoint newMount = new FSMountPoint(workspaceId, workspaceMountPoint, eventService, searcherProvider);
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
