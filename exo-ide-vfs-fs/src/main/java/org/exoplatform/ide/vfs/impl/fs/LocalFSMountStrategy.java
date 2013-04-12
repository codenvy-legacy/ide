/*
 * Copyright (C) 2013 eXo Platform SAS.
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

import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;

/**
 * Get location of local file system for 'mount' virtual filesystem.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface LocalFSMountStrategy {
    /**
     * Get 'mount point' for specified <code>workspace</code>. In this case <code>workspace</code> minds abstraction to
     * isolated few environments when we use virtual filesystem in cloud infrastructure. If <code>workspace</code> is
     * <code>null</code>, it is assumed that <code>workspace</code> can be obtained from existed context. This part is
     * implementation specific.
     *
     * @param workspace
     *         name of workspace or <code>null</code>
     * @return location on local file system where virtual filesystem should be mounter
     * @throws VirtualFileSystemException
     */
    java.io.File getMountPath(String workspace) throws VirtualFileSystemException;

    /** This is shortcut for <code>getMountPath(null)</code> */
    java.io.File getMountPath() throws VirtualFileSystemException;
}
