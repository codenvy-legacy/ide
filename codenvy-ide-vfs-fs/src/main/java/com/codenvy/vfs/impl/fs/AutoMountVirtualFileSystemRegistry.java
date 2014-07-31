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

import com.codenvy.api.core.ServerException;
import com.codenvy.api.core.notification.EventService;
import com.codenvy.api.vfs.server.VirtualFileSystemProvider;
import com.codenvy.api.vfs.server.VirtualFileSystemRegistry;
import com.codenvy.api.vfs.server.search.SearcherProvider;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;

/**
 * Implementation of VirtualFileSystemRegistry that is able to create VirtualFileSystemProvider automatically (even if it doesn't
 * registered) if required path on local filesystem exists.
 *
 * @author andrew00x
 */
@Singleton
public class AutoMountVirtualFileSystemRegistry extends VirtualFileSystemRegistry {
    private final LocalFSMountStrategy mountStrategy;
    private final EventService         eventService;
    private final SearcherProvider     searcherProvider;

    @Inject
    public AutoMountVirtualFileSystemRegistry(LocalFSMountStrategy mountStrategy,
                                              EventService eventService,
                                              @Nullable SearcherProvider searcherProvider) {
        this.mountStrategy = mountStrategy;
        this.eventService = eventService;
        this.searcherProvider = searcherProvider;
    }

    @Override
    protected VirtualFileSystemProvider loadProvider(String vfsId) throws ServerException {
        File wsPath = mountStrategy.getMountPath(vfsId);
        if (!wsPath.exists()) {
            return null;
        }
        return new LocalFileSystemProvider(vfsId, mountStrategy, eventService, searcherProvider, this);
    }
}
