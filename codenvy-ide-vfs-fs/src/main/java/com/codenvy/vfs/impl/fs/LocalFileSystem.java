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
import com.codenvy.api.vfs.server.VirtualFileSystemImpl;
import com.codenvy.api.vfs.server.VirtualFileSystemRegistry;
import com.codenvy.api.vfs.server.VirtualFileSystemUserContext;
import com.codenvy.api.vfs.server.search.SearcherProvider;
import com.codenvy.api.vfs.server.util.LinksHelper;
import com.codenvy.api.vfs.shared.PropertyFilter;
import com.codenvy.api.vfs.shared.dto.Folder;
import com.codenvy.api.vfs.shared.dto.VirtualFileSystemInfo;
import com.codenvy.api.vfs.shared.dto.VirtualFileSystemInfo.ACLCapability;
import com.codenvy.api.vfs.shared.dto.VirtualFileSystemInfo.BasicPermissions;
import com.codenvy.api.vfs.shared.dto.VirtualFileSystemInfo.QueryCapability;
import com.codenvy.dto.server.DtoFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of VirtualFileSystem for local filesystem.
 *
 * @author andrew00x
 */
public class LocalFileSystem extends VirtualFileSystemImpl {
    final String vfsId;
    final URI    baseUri;

    public LocalFileSystem(String vfsId,
                           URI baseUri,
                           VirtualFileSystemUserContext userContext,
                           FSMountPoint mountPoint,
                           SearcherProvider searcherProvider,
                           VirtualFileSystemRegistry vfsRegistry) {
        super(vfsId, baseUri, userContext, mountPoint, searcherProvider, vfsRegistry);
        this.vfsId = vfsId;
        this.baseUri = baseUri;
    }

    @Override
    public VirtualFileSystemInfo getInfo() throws ServerException {
        final BasicPermissions[] basicPermissions = BasicPermissions.values();
        final List<String> permissions = new ArrayList<>(basicPermissions.length);
        for (BasicPermissions bp : basicPermissions) {
            permissions.add(bp.value());
        }
        return DtoFactory.getInstance().createDto(VirtualFileSystemInfo.class)
                         .withId(vfsId)
                         .withVersioningSupported(false)
                         .withLockSupported(true)
                         .withAnonymousPrincipal(VirtualFileSystemInfo.ANONYMOUS_PRINCIPAL)
                         .withAnyPrincipal(VirtualFileSystemInfo.ANY_PRINCIPAL)
                         .withPermissions(permissions)
                         .withAclCapability(ACLCapability.MANAGE)
                         .withQueryCapability(searcherProvider == null ? QueryCapability.NONE : QueryCapability.FULLTEXT)
                         .withUrlTemplates(LinksHelper.createUrlTemplates(baseUri, vfsId))
                         .withRoot((Folder)fromVirtualFile(getMountPoint().getRoot(), true, PropertyFilter.ALL_FILTER));
    }
}
