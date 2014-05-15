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

import com.codenvy.api.vfs.server.VirtualFileSystem;
import com.codenvy.api.vfs.server.exceptions.VirtualFileSystemException;
import com.codenvy.vfs.impl.fs.exceptions.GitUrlResolveException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

/**
 * Resolves Git URL for public access.
 *
 * @author Vitaly Parfonov
 */
@Singleton
public class GitUrlResolver {
    private final LocalPathResolver pathResolver;
    private final String            mountPath;

    @Inject
    public GitUrlResolver(@Named("vfs.local.fs_root_dir") java.io.File mountRoot, LocalPathResolver pathResolver) {
        this.mountPath = mountRoot.getAbsolutePath();
        this.pathResolver = pathResolver;
    }

    public String resolve(UriInfo uriInfo, VirtualFileSystem vfs, String id) throws VirtualFileSystemException {
        try {
            return resolve(uriInfo.getBaseUri(), ((FSMountPoint)vfs.getMountPoint()).getVirtualFileById(id));
        } catch (VirtualFileSystemException e) {
            throw new GitUrlResolveException("Can't resolve Git URL", e);
        }
    }

    public String resolve(URI baseUri, VirtualFileImpl virtualFile) throws VirtualFileSystemException {
        final String localPath = pathResolver.resolve(virtualFile);
        StringBuilder result = new StringBuilder();
        result.append("http");
        result.append("://");
        result.append(baseUri.getHost());
        int port = baseUri.getPort();
        if (port != 80 && port != 443 && port != -1) {
            result.append(':');
            result.append(port);
        }
        result.append('/');
        result.append("git");
        result.append(localPath.substring(mountPath.length()));
        return result.toString();
    }
}
