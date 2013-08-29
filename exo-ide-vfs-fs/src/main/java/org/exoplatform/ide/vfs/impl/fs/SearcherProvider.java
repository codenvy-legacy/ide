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

import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;

/**
 * Manages instances of Searcher.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface SearcherProvider {
    /**
     * Get Searcher for specified MountPoint.
     *
     * @param mountPoint
     *         MountPoint
     * @param create
     *         <code>true</code> to create new Searcher if there is no Searcher for specified <code>mountPoint</code> and <code>false</code>
     *         to return <code>null</code> if there is no Searcher
     * @return instance of Searcher
     * @throws VirtualFileSystemException
     * @see MountPoint
     */
    Searcher getSearcher(MountPoint mountPoint, boolean create) throws VirtualFileSystemException;
}
