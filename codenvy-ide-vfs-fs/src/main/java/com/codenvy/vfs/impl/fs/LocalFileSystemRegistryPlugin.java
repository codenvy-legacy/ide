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
import com.codenvy.api.vfs.server.VirtualFileSystemRegistry;
import com.codenvy.api.vfs.server.exceptions.VirtualFileSystemException;
import com.codenvy.api.vfs.server.search.SearcherProvider;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * @author andrew00x
 */
@Singleton
public class LocalFileSystemRegistryPlugin {
    @Inject
    public LocalFileSystemRegistryPlugin(@Named("vfs.local.id") String[] ids,
                                         LocalFSMountStrategy mountStrategy,
                                         VirtualFileSystemRegistry registry,
                                         EventService eventService,
                                         @Nullable SearcherProvider searcherProvider) throws VirtualFileSystemException {
        for (String id : ids) {
            registry.registerProvider(id, new LocalFileSystemProvider(id, mountStrategy, eventService, searcherProvider));
        }
    }
}
