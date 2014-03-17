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

import com.codenvy.api.vfs.server.VirtualFileSystemImpl;
import com.codenvy.api.vfs.server.VirtualFileSystemUserContext;
import com.codenvy.api.vfs.server.exceptions.VirtualFileSystemException;
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
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 */
public class LocalFileSystem extends VirtualFileSystemImpl {
    final String vfsId;
    final URI    baseUri;

    public LocalFileSystem(String vfsId,
                           URI baseUri,
                           VirtualFileSystemUserContext userContext,
                           FSMountPoint mountPoint,
                           SearcherProvider searcherProvider) {
        super(vfsId, baseUri, userContext, mountPoint, searcherProvider);
        this.vfsId = vfsId;
        this.baseUri = baseUri;
    }

    @Override
    public VirtualFileSystemInfo getInfo() throws VirtualFileSystemException {
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
