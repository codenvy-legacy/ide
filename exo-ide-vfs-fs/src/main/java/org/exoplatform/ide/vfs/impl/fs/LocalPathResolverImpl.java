/*
 * Copyright (C) 2011 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
