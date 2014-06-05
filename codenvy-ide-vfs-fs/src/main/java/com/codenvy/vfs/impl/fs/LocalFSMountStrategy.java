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

import com.codenvy.api.vfs.server.exceptions.VirtualFileSystemException;

/**
 * Get location of local file system for 'mount' virtual filesystem.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
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
