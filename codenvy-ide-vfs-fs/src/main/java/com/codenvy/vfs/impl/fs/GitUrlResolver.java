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

import com.codenvy.api.core.ForbiddenException;
import com.codenvy.api.core.NotFoundException;
import com.codenvy.api.core.ServerException;
import com.codenvy.api.vfs.server.VirtualFileSystem;

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

    public String resolve(UriInfo uriInfo, VirtualFileSystem vfs, String path)
            throws ServerException, NotFoundException, ForbiddenException {

        return resolve(uriInfo.getBaseUri(), ((FSMountPoint)vfs.getMountPoint()).getVirtualFile(path));
    }

    public String resolve(UriInfo uriInfo, VirtualFileImpl virtualFile) {
        return resolve(uriInfo.getBaseUri(), virtualFile);
    }

    public String resolve(URI baseUri, VirtualFileImpl virtualFile) {
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
