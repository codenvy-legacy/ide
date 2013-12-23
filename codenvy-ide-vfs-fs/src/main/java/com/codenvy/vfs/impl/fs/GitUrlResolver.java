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
import com.codenvy.commons.env.EnvironmentContext;
import com.codenvy.vfs.impl.fs.exceptions.GitUrlResolveException;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

/**
 * Resolves Git URL for public access.
 *
 * @author Vitaly Parfonov
 */
public class GitUrlResolver {
    private final LocalPathResolver pathResolver;

    public GitUrlResolver(LocalPathResolver pathResolver) {
        this.pathResolver = pathResolver;
    }

    public String resolve(UriInfo uriInfo, VirtualFileSystem vfs, String id) throws VirtualFileSystemException {
        try {
            if (vfs instanceof LocalFileSystem) {
                throw new GitUrlResolveException(String.format("Can't resolve Git URL for item %s on file system %s", id, vfs));
            }
            return resolve(uriInfo.getBaseUri(), ((FSMountPoint)vfs.getMountPoint()).getVirtualFileById(id));
        } catch (VirtualFileSystemException e) {
            throw new GitUrlResolveException("Can't resolve Git URL", e);
        }
    }

    public String resolve(URI baseUri, VirtualFileImpl virtualFile) throws VirtualFileSystemException {
        final String localPath = pathResolver.resolve(virtualFile);
        final EnvironmentContext context = EnvironmentContext.getCurrent();
        final String rootPath = ((java.io.File)context.getVariable(EnvironmentContext.VFS_ROOT_DIR)).getAbsolutePath();
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
        result.append(localPath.substring(rootPath.length()));
        return result.toString();
    }
}
