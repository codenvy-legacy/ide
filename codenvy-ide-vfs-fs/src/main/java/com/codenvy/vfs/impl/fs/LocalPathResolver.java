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

import com.codenvy.api.vfs.server.VirtualFile;
import com.codenvy.api.vfs.server.exceptions.VirtualFileSystemException;
import com.codenvy.vfs.impl.fs.exceptions.LocalPathResolveException;

import javax.inject.Singleton;

/**
 * Resolves location of virtual filesystem item on local filesystem.
 *
 * @author Vitaly Parfonov
 */
@Singleton
public class LocalPathResolver {
    public String resolve(VirtualFile virtualFile) throws VirtualFileSystemException {
        if (!(virtualFile instanceof VirtualFileImpl)) {
            throw new LocalPathResolveException(String.format("Cannot resolve path on the local filesystem for %s", virtualFile));
        }
        return ((VirtualFileImpl)virtualFile).getIoFile().getAbsolutePath();
    }
}
