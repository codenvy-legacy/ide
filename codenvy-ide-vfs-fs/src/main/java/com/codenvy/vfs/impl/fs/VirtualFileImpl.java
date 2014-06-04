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

import com.codenvy.api.core.util.ContentTypeGuesser;
import com.codenvy.api.core.util.Pair;
import com.codenvy.api.vfs.server.ContentStream;
import com.codenvy.api.vfs.server.LazyIterator;
import com.codenvy.api.vfs.server.Path;
import com.codenvy.api.vfs.server.VirtualFile;
import com.codenvy.api.vfs.server.VirtualFileFilter;
import com.codenvy.api.vfs.server.VirtualFileVisitor;
import com.codenvy.api.vfs.server.exceptions.VirtualFileSystemException;
import com.codenvy.api.vfs.server.exceptions.VirtualFileSystemRuntimeException;
import com.codenvy.api.vfs.shared.PropertyFilter;
import com.codenvy.api.vfs.shared.dto.AccessControlEntry;
import com.codenvy.api.vfs.shared.dto.Folder;
import com.codenvy.api.vfs.shared.dto.Principal;
import com.codenvy.api.vfs.shared.dto.Property;
import com.codenvy.api.vfs.shared.dto.VirtualFileSystemInfo;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of VirtualFile which uses java.io.File.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 */
public class VirtualFileImpl implements VirtualFile {
    private final java.io.File ioFile;
    private final String       id;
    private final Path         path;
    private final FSMountPoint mountPoint;

    VirtualFileImpl(java.io.File ioFile, Path path, String id, FSMountPoint mountPoint) {
        this.ioFile = ioFile;
        this.path = path;
        this.id = id;
        this.mountPoint = mountPoint;
    }

    @Override
    public String getId() throws VirtualFileSystemException {
        return id;
    }

    @Override
    public String getName() throws VirtualFileSystemException {
        return path.getName();
    }

    @Override
    public String getPath() throws VirtualFileSystemException {
        return path.toString();
    }

    @Override
    public Path getVirtualFilePath() throws VirtualFileSystemException {
        return path;
    }

    @Override
    public boolean exists() throws VirtualFileSystemException {
        return getIoFile().exists();
    }

    @Override
    public boolean isRoot() throws VirtualFileSystemException {
        return path.isRoot();
    }

    @Override
    public boolean isFile() throws VirtualFileSystemException {
        return getIoFile().isFile();
    }

    @Override
    public boolean isFolder() throws VirtualFileSystemException {
        return getIoFile().isDirectory();
    }

    @Override
    public VirtualFile getParent() throws VirtualFileSystemException {
        return mountPoint.getParent(this);
    }

    @Override
    public LazyIterator<VirtualFile> getChildren(VirtualFileFilter filter) throws VirtualFileSystemException {
        return mountPoint.getChildren(this, filter);
    }

    @Override
    public VirtualFile getChild(String name) throws VirtualFileSystemException {
        return mountPoint.getChild(this, name);
    }

    @Override
    public ContentStream getContent() throws VirtualFileSystemException {
        return mountPoint.getContent(this);
    }

    @Override
    public VirtualFile updateContent(String mediaType, InputStream content, String lockToken) throws VirtualFileSystemException {
        mountPoint.updateContent(this, mediaType, content, lockToken);
        return this;
    }

    @Override
    public String getMediaType() throws VirtualFileSystemException {
        String mediaType = mountPoint.getPropertyValue(this, "vfs:mimeType");
        if (mediaType == null) {
            // If media type is not set then item may be file or regular folder and cannot be a project.
            mediaType = isFile() ? ContentTypeGuesser.guessContentType(ioFile) : Folder.FOLDER_MIME_TYPE;
        }
        return mediaType;
    }

//    @Override
    public VirtualFile setMediaType(String mediaType) throws VirtualFileSystemException {
        mountPoint.setProperty(this, "vfs:mimeType", mediaType);
        return this;
    }

    @Override
    public long getCreationDate() throws VirtualFileSystemException {
        // Creation date is not accessible over JDK API. May be done when switch to JDK7.
        // But even after switch to JDK7 creation date may not be available from underlying file system.
        return -1;
    }

    @Override
    public long getLastModificationDate() throws VirtualFileSystemException {
        return getIoFile().lastModified();
    }

    @Override
    public long getLength() throws VirtualFileSystemException {
        return getIoFile().length();
    }

    //

    @Override
    public List<Property> getProperties(PropertyFilter filter) throws VirtualFileSystemException {
        if (PropertyFilter.NONE_FILTER == filter) {
            // Do not 'disturb' backend if we already know result is always empty.
            return Collections.emptyList();
        }
        return mountPoint.getProperties(this, filter);
    }

    @Override
    public VirtualFile updateProperties(List<Property> properties, String lockToken) throws VirtualFileSystemException {
        mountPoint.updateProperties(this, properties, lockToken);
        return this;
    }

    @Override
    public String getPropertyValue(String name) throws VirtualFileSystemException {
        return mountPoint.getPropertyValue(this, name);
    }

    @Override
    public String[] getPropertyValues(String name) throws VirtualFileSystemException {
        return mountPoint.getPropertyValues(this, name);
    }

    //

    @Override
    public String getVersionId() throws VirtualFileSystemException {
        return mountPoint.getVersionId(this);
    }

    @Override
    public LazyIterator<VirtualFile> getVersions(VirtualFileFilter filter) throws VirtualFileSystemException {
        return mountPoint.getVersions(this, filter);
    }

    @Override
    public VirtualFile getVersion(String versionId) throws VirtualFileSystemException {
        return mountPoint.getVersion(this, versionId);
    }

    //

    @Override
    public VirtualFile copyTo(VirtualFile parent) throws VirtualFileSystemException {
        return mountPoint.copy(this, (VirtualFileImpl)parent);
    }

    @Override
    public VirtualFile moveTo(VirtualFile parent, String lockToken) throws VirtualFileSystemException {
        return mountPoint.move(this, (VirtualFileImpl)parent, lockToken);
    }

    @Override
    public VirtualFile rename(String newName, String newMediaType, String lockToken) throws VirtualFileSystemException {
        return mountPoint.rename(this, newName, newMediaType, lockToken);
    }

    @Override
    public void delete(String lockToken) throws VirtualFileSystemException {
        mountPoint.delete(this, lockToken);
    }

    //

    @Override
    public ContentStream zip(VirtualFileFilter filter) throws IOException, VirtualFileSystemException {
        return mountPoint.zip(this, filter);
    }

    @Override
    public void unzip(InputStream zipped, boolean overwrite) throws IOException, VirtualFileSystemException {
        mountPoint.unzip(this, zipped, overwrite);
    }

    //

    @Override
    public String lock(long timeout) throws VirtualFileSystemException {
        return mountPoint.lock(this, timeout);
    }

    @Override
    public VirtualFile unlock(String lockToken) throws VirtualFileSystemException {
        mountPoint.unlock(this, lockToken);
        return this;
    }

    @Override
    public boolean isLocked() throws VirtualFileSystemException {
        return mountPoint.isLocked(this);
    }

    //

    @Override
    public Map<Principal, Set<VirtualFileSystemInfo.BasicPermissions>> getPermissions() throws VirtualFileSystemException {
        return mountPoint.getACL(this).getPermissionMap();
    }

    @Override
    public List<AccessControlEntry> getACL() throws VirtualFileSystemException {
        return mountPoint.getACL(this).getEntries();
    }

    @Override
    public VirtualFile updateACL(List<AccessControlEntry> acl, boolean override, String lockToken) throws VirtualFileSystemException {
        mountPoint.updateACL(this, acl, override, lockToken);
        return this;
    }

    //

    @Override
    public VirtualFile createFile(String name, String mediaType, InputStream content) throws VirtualFileSystemException {
        return mountPoint.createFile(this, name, mediaType, content);
    }

    @Override
    public VirtualFile createFolder(String name) throws VirtualFileSystemException {
        return mountPoint.createFolder(this, name);
    }

    //

    @Override
    public FSMountPoint getMountPoint() {
        return mountPoint;
    }

    @Override
    public void accept(VirtualFileVisitor visitor) throws VirtualFileSystemException {
        visitor.visit(this);
    }

    @Override
    public LazyIterator<Pair<String, String>> countMd5Sums() throws VirtualFileSystemException {
        return mountPoint.countMd5Sums(this);
    }

    @Override
    public int compareTo(VirtualFile other) {
        // To get nice order of items:
        // 1. Regular folders
        // 2. Files
        if (other == null) {
            throw new NullPointerException();
        }
        try {
            if (isFolder()) {
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
