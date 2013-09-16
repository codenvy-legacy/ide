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
package org.exoplatform.ide.vfs.server.observation;

import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;

/**
 * Filter events by VirtualFileSystem ID.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#getInfo()
 */
public final class VfsIDFilter extends ChangeEventFilter {
    private final String vfsId;

    public VfsIDFilter(String vfsId) {
        this.vfsId = vfsId;
    }

    @Override
    public boolean matched(ChangeEvent event) throws VirtualFileSystemException {
        final String theVfsId = event.getVirtualFileSystem().getInfo().getId();
        return vfsId == null ? theVfsId == null : vfsId.equals(theVfsId);
    }
}
