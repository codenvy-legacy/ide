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
