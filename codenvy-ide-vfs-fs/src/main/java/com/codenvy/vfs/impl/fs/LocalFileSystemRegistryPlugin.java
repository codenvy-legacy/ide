/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2014] Codenvy, S.A. 
 *  All Rights Reserved.
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
