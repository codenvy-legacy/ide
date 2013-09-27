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
package org.exoplatform.ide.vfs.server;

import org.exoplatform.ide.vfs.server.exceptions.LocalPathResolveException;

/**
 * Need for resolving location for file/folders on real-life file system. It can be need for using Git or some other
 * services that work with file system. Implementation depend on VirtualFileSystem implementation.
 *
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public interface LocalPathResolver {
    /**
     * Return absolute path to the item (file or folder) on file system
     *
     * @param vfs
     *         the VirtualFileSystem implementation
     * @param itemId
     *         the to id of Item
     * @return absolute path to the item (file or folder) on file system
     * @throws LocalPathResolveException
     */
    String resolve(VirtualFileSystem vfs, String itemId) throws LocalPathResolveException;
}
