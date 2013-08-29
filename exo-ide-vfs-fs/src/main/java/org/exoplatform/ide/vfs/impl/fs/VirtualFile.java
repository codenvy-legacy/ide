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

import org.exoplatform.ide.vfs.server.ContentStream;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemRuntimeException;
import org.exoplatform.ide.vfs.server.util.MediaTypes;
import org.exoplatform.ide.vfs.shared.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class VirtualFile implements Comparable<VirtualFile> {
    private final java.io.File ioFile;
    private final Path         path;
    private final MountPoint   mountPoint;

    VirtualFile(java.io.File ioFile, Path path, MountPoint mountPoint) {
        this.ioFile = ioFile;
        this.path = path;
        this.mountPoint = mountPoint;
    }

    public String getName() throws VirtualFileSystemException {
        return path.getName();
    }

    public String getPath() throws VirtualFileSystemException {
        return path.toString();
    }

    public boolean exists() throws VirtualFileSystemException {
        return getIoFile().exists();
    }

    public boolean isRoot() throws VirtualFileSystemException {
        return path.isRoot();
    }

    public boolean isFile() throws VirtualFileSystemException {
        return getIoFile().isFile();
    }

    public boolean isFolder() throws VirtualFileSystemException {
        return getIoFile().isDirectory();
    }

    public boolean isProject() throws VirtualFileSystemException {
        return isFolder() && Project.PROJECT_MIME_TYPE.equals(getMediaType());
    }

    public VirtualFile getParent() throws VirtualFileSystemException {
        return mountPoint.getParent(this);
    }

    public List<VirtualFile> getChildren() throws VirtualFileSystemException {
        return mountPoint.getChildren(this);
    }

    public ContentStream getContent() throws VirtualFileSystemException {
        return mountPoint.getContent(this);
    }

    public VirtualFile updateContent(String mediaType, InputStream content, String lockToken) throws VirtualFileSystemException {
        mountPoint.updateContent(this, mediaType, content, lockToken);
        return this;
    }

    public String getMediaType() throws VirtualFileSystemException {
        String mediaType = mountPoint.getPropertyValue(this, "vfs:mimeType");
        if (mediaType == null) {
            // If media type is not set then item may be file or regular folder and cannot be a project.
            mediaType = isFile() ? MediaTypes.INSTANCE.getMediaType(path.getName()) : Folder.FOLDER_MIME_TYPE;
        }
        return mediaType;
    }

    public long getCreationDate() throws VirtualFileSystemException {
        // Creation date is not accessible over JDK API. May be done when switch to JDK7.
        // But even after switch to JDK7 creation date may not be available from underlying file system.
        return -1;
    }

    public long getLastModificationDate() throws VirtualFileSystemException {
        return getIoFile().lastModified();
    }

    public long getLength() throws VirtualFileSystemException {
        return getIoFile().length();
    }

    //

    public List<Property> getProperties(PropertyFilter filter) throws VirtualFileSystemException {
        if (PropertyFilter.NONE_FILTER == filter) {
            // Do not 'disturb' backend if we already know result is always empty.
            return Collections.emptyList();
        }
        return mountPoint.getProperties(this, filter);
    }

    public VirtualFile updateProperties(List<Property> properties, String lockToken) throws VirtualFileSystemException {
        mountPoint.updateProperties(this, properties, lockToken);
        return this;
    }

    public String getPropertyValue(String name) throws VirtualFileSystemException {
        return mountPoint.getPropertyValue(this, name);
    }

    public String[] getPropertyValues(String name) throws VirtualFileSystemException {
        return mountPoint.getPropertyValues(this, name);
    }

    //

    public VirtualFile copyTo(VirtualFile parent) throws VirtualFileSystemException {
        return mountPoint.copy(this, parent);
    }

    public VirtualFile moveTo(VirtualFile parent, String lockToken) throws VirtualFileSystemException {
        return mountPoint.move(this, parent, lockToken);
    }

    public VirtualFile rename(String newName, String newMediaType, String lockToken) throws VirtualFileSystemException {
        return mountPoint.rename(this, newName, newMediaType, lockToken);
    }

    public void delete(String lockToken) throws VirtualFileSystemException {
        mountPoint.delete(this, lockToken);
    }

    //

    public ContentStream zip() throws IOException, VirtualFileSystemException {
        return mountPoint.zip(this);
    }

    public void unzip(InputStream zipped, boolean overwrite) throws IOException, VirtualFileSystemException {
        mountPoint.unzip(this, zipped, overwrite);
    }

    //

    public String lock() throws VirtualFileSystemException {
        return mountPoint.lock(this);
    }

    public VirtualFile unlock(String lockToken) throws VirtualFileSystemException {
        mountPoint.unlock(this, lockToken);
        return this;
    }

    public boolean isLocked() throws VirtualFileSystemException {
        return mountPoint.isLocked(this);
    }

    //

    public List<AccessControlEntry> getACL() throws VirtualFileSystemException {
        return mountPoint.getACL(this).getEntries();
    }

    public VirtualFile updateACL(List<AccessControlEntry> acl, boolean override, String lockToken) throws VirtualFileSystemException {
        mountPoint.updateACL(this, acl, override, lockToken);
        return this;
    }

    //

    public VirtualFile createFile(String name, String mediaType, InputStream content) throws VirtualFileSystemException {
        return mountPoint.createFile(this, name, mediaType, content);
    }

    public VirtualFile createFolder(String name) throws VirtualFileSystemException {
        return mountPoint.createFolder(this, name);
    }

    public VirtualFile createProject(String name, List<Property> properties) throws VirtualFileSystemException {
        return mountPoint.createProject(this, name, properties);
    }

    //

    public MountPoint getMountPoint() {
        return mountPoint;
    }

    @Override
    public int compareTo(VirtualFile other) {
        // To get nice order of items:
        // 1. Projects
        // 2. Regular folders
        // 3. Files
        if (other == null) {
            throw new NullPointerException();
        }
        try {
            if (isProject()) {
                return other.isProject() ? getName().compareTo(other.getName()) : -1;
            } else if (other.isProject()) {
                return 1;
            } else if (isFolder()) {
                return other.isFolder() ? getName().compareTo(other.getName()) : -1;
            } else if (other.isFolder()) {
                return 1;
            }
            return getName().compareTo(other.getName());
        } catch (VirtualFileSystemException e) {
            // cannot continue if failed to determine item type.
            throw new VirtualFileSystemRuntimeException(e.getMessage(), e);
        }
    }

   /* =================== */

    public final java.io.File getIoFile() {
        return ioFile;
    }

    Path getInternalPath() {
        return path;
    }
}
