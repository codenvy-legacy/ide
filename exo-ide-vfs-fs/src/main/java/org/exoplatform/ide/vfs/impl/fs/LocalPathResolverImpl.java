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
package org.exoplatform.ide.vfs.impl.fs;

import org.exoplatform.ide.vfs.server.LocalPathResolver;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.exceptions.LocalPathResolveException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;

/**
 * @author <a href="mailto:vparfonov@codenvy.com">Vitaly Parfonov</a>
 * @version $Id: LocalPathResolverImpl.java Apr 3, 2013 vetal $
 *
 */
public class LocalPathResolverImpl implements LocalPathResolver {
    @Override
    public String resolve(VirtualFileSystem vfs, String id) throws LocalPathResolveException {
        if (vfs == null) {
            throw new LocalPathResolveException(
                    "Cannot resolve path on the Local filesystem. Virtual filesystem is not initialized. ");
        }
        if (!(vfs instanceof LocalFileSystem)) {
            throw new LocalPathResolveException(
                    String.format("Cannot resolve path on the local filesystem. Unsupported virtual filesystem type: %s. ", vfs));
        }
        if (id == null || id.length() == 0) {
            throw new LocalPathResolveException(
                    "Cannot resolve path on the local filesystem. Item id may not be null or empty. ");
        }

        try {
            return ((LocalFileSystem)vfs).idToVirtualFile(id).getIoFile().getAbsolutePath();
        } catch (VirtualFileSystemException e) {
            throw new LocalPathResolveException("Cannot resolve path on the local filesystem. ", e);
        }
    }
}
