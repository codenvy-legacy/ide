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

import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.server.observation.*;

import static org.exoplatform.ide.vfs.server.observation.ChangeEvent.ChangeType;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
class ProjectUpdateEventFilter extends ChangeEventFilter {
    private final String            vfsId;
    private final String            projectId;
    // Use root i/o file as additional identifier.
    // VirtualFileSystem IDs is the same (or potentially may be the same) for different workspaces (tenants),
    // but each workspace has own unique location on local filesystem. We use this location to avoid
    // sending events between workspaces (tenants).
    private final java.io.File      ioRoot;
    private final ChangeEventFilter delegate;

    static ProjectUpdateEventFilter newFilter(LocalFileSystem vfs, VirtualFile project) throws VirtualFileSystemException {
        final ChangeEventFilter filter = ChangeEventFilter.createAndFilter(
                new VfsIDFilter(vfs.vfsId),
                new PathFilter(project.getPath() + "/.*"), // events for all project items
                new TypeFilter(
                        ChangeType.CREATED,
                        ChangeType.CONTENT_UPDATED,
                        ChangeType.DELETED,
                        ChangeType.RENAMED,
                        ChangeType.MOVED
                )
                                                                          );
        return new ProjectUpdateEventFilter(
                filter,
                vfs.vfsId,
                vfs.virtualFileToId(project),
                vfs.mountPoint.getRoot().getIoFile()
        );
    }

    @Override
    public boolean matched(ChangeEvent event) throws VirtualFileSystemException {
        final VirtualFileSystem vfs = event.getVirtualFileSystem();
        return (vfs instanceof LocalFileSystem)
               && ioRoot.equals(((LocalFileSystem)vfs).mountPoint.getRoot().getIoFile())
               && delegate.matched(event);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProjectUpdateEventFilter)) {
            return false;
        }

        ProjectUpdateEventFilter other = (ProjectUpdateEventFilter)o;

        if (!ioRoot.equals(other.ioRoot)) {
            return false;
        }

        if (vfsId == null) {
            if (other.vfsId != null) {
                return false;
            }
        } else {
            if (!vfsId.equals(other.vfsId)) {
                return false;
            }
        }

        return projectId.equals(other.projectId);
    }

    @Override
    public final int hashCode() {
        int hash = 7;
        hash = 31 * hash + ioRoot.hashCode();
        hash = 31 * hash + (vfsId != null ? vfsId.hashCode() : 0);
        hash = 31 * hash + projectId.hashCode();
        return hash;
    }

    private ProjectUpdateEventFilter(ChangeEventFilter delegate, String vfsId, String projectId, java.io.File ioRoot) {
        this.delegate = delegate;
        this.vfsId = vfsId;
        this.projectId = projectId;
        this.ioRoot = ioRoot;
    }
}
