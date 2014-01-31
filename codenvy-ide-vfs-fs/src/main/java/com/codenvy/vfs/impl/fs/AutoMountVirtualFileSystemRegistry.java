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

import com.codenvy.api.vfs.server.VirtualFileSystemProvider;
import com.codenvy.api.vfs.server.VirtualFileSystemRegistry;
import com.codenvy.api.vfs.server.exceptions.VirtualFileSystemException;
import com.codenvy.api.vfs.server.search.SearcherProvider;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Implementation of VirtualFileSystemRegistry that is able to create VirtualFileSystemProvider automatically (even if it doesn't
 * registered) if required path on local filesystem exists.
 *
 * @author andrew00x
 */
@Singleton
public class AutoMountVirtualFileSystemRegistry extends VirtualFileSystemRegistry {
    private final LocalFSMountStrategy mountStrategy;
    private final SearcherProvider     searcherProvider;

    @Inject
    public AutoMountVirtualFileSystemRegistry(LocalFSMountStrategy mountStrategy,
                                              @Nullable SearcherProvider searcherProvider) {
        this.mountStrategy = mountStrategy;
        this.searcherProvider = searcherProvider;
    }

    @Override
    protected VirtualFileSystemProvider loadProvider(String vfsId) throws VirtualFileSystemException {
        if (mountStrategy.getMountPath(vfsId).exists()) {
            return new LocalFileSystemProvider(vfsId, mountStrategy, searcherProvider);
        }
        return null;
    }
}
