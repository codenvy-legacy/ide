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

import com.codenvy.api.core.ConflictException;
import com.codenvy.api.core.ForbiddenException;
import com.codenvy.api.core.NotFoundException;
import com.codenvy.api.core.ServerException;
import com.codenvy.api.core.util.ContentTypeGuesser;
import com.codenvy.api.vfs.server.ContentStream;
import com.codenvy.api.vfs.server.LazyIterator;
import com.codenvy.api.vfs.server.Path;
import com.codenvy.api.vfs.server.VirtualFile;
import com.codenvy.api.vfs.server.VirtualFileFilter;
import com.codenvy.api.vfs.server.VirtualFileVisitor;
import com.codenvy.api.vfs.shared.PropertyFilter;
import com.codenvy.api.vfs.shared.dto.AccessControlEntry;
import com.codenvy.api.vfs.shared.dto.Folder;
import com.codenvy.api.vfs.shared.dto.Principal;
import com.codenvy.api.vfs.shared.dto.Property;
import com.codenvy.commons.lang.Pair;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of VirtualFile which uses java.io.File.
 *
 * @author andrew00x
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
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return path.getName();
    }

    @Override
    public String getPath() {
        return path.toString();
    }

    @Override
    public Path getVirtualFilePath() {
        return path;
    }

    @Override
    public boolean exists() {
        return getIoFile().exists();
    }

    @Override
    public boolean isRoot() {
        return path.isRoot();
    }

    @Override
    public boolean isFile() {
        return getIoFile().isFile();
    }

    @Override
    public boolean isFolder() {
        return getIoFile().isDirectory();
    }

    @Override
    public VirtualFile getParent() {
        return mountPoint.getParent(this);
    }

    @Override
    public LazyIterator<VirtualFile> getChildren(VirtualFileFilter filter) throws ServerException {
        return mountPoint.getChildren(this, filter);
    }

    @Override
    public VirtualFile getChild(String name) throws ForbiddenException, ServerException {
        return mountPoint.getChild(this, name);
    }

    @Override
    public ContentStream getContent() throws ForbiddenException, ServerException {
        return mountPoint.getContent(this);
    }

    @Override
    public VirtualFile updateContent(String mediaType, InputStream content, String lockToken) throws ForbiddenException, ServerException {
        mountPoint.updateContent(this, mediaType, content, lockToken);
        return this;
    }

    @Override
    public String getMediaType() throws ServerException {
        String mediaType = mountPoint.getPropertyValue(this, "vfs:mimeType");
        if (mediaType == null) {
            // If media type is not set then item may be file or regular folder and cannot be a project.
            mediaType = isFile() ? ContentTypeGuesser.guessContentType(ioFile) : Folder.FOLDER_MIME_TYPE;
        }
        return mediaType;
    }

    //    @Override
    public VirtualFile setMediaType(String mediaType) throws ServerException {
        mountPoint.setProperty(this, "vfs:mimeType", mediaType);
        return this;
    }

    @Override
    public long getCreationDate() {
        // Creation date may not be available from underlying file system.
        return -1;
    }

    @Override
    public long getLastModificationDate() {
        return getIoFile().lastModified();
    }

    @Override
    public long getLength() throws ServerException {
        return getIoFile().length();
    }

    //

    @Override
    public List<Property> getProperties(PropertyFilter filter) throws ServerException {
        if (PropertyFilter.NONE_FILTER == filter) {
            // Do not 'disturb' backend if we already know result is always empty.
            return Collections.emptyList();
        }
        return mountPoint.getProperties(this, filter);
    }

    @Override
    public VirtualFile updateProperties(List<Property> properties, String lockToken) throws ForbiddenException, ServerException {
        mountPoint.updateProperties(this, properties, lockToken);
        return this;
    }

    @Override
    public String getPropertyValue(String name) throws ServerException {
        return mountPoint.getPropertyValue(this, name);
    }

    @Override
    public String[] getPropertyValues(String name) throws ServerException {
        return mountPoint.getPropertyValues(this, name);
    }

    //

    @Override
    public String getVersionId() {
        return mountPoint.getVersionId(this);
    }

    @Override
    public LazyIterator<VirtualFile> getVersions(VirtualFileFilter filter) throws ForbiddenException, ServerException {
        return mountPoint.getVersions(this, filter);
    }

    @Override
    public VirtualFile getVersion(String versionId) throws NotFoundException, ForbiddenException, ServerException {
        return mountPoint.getVersion(this, versionId);
    }

    //

    @Override
    public VirtualFile copyTo(VirtualFile parent) throws ForbiddenException, ConflictException, ServerException {
        return mountPoint.copy(this, (VirtualFileImpl)parent);
    }

    @Override
    public VirtualFile moveTo(VirtualFile parent, String lockToken) throws ForbiddenException, ConflictException, ServerException {
        return mountPoint.move(this, (VirtualFileImpl)parent, lockToken);
    }

    @Override
    public VirtualFile rename(String newName, String newMediaType, String lockToken)
            throws ForbiddenException, ConflictException, ServerException {
        return mountPoint.rename(this, newName, newMediaType, lockToken);
    }

    @Override
    public void delete(String lockToken) throws ForbiddenException, ServerException {
        mountPoint.delete(this, lockToken);
    }

    //

    @Override
    public ContentStream zip(VirtualFileFilter filter) throws ForbiddenException, ServerException {
        return mountPoint.zip(this, filter);
    }

    @Override
    public void unzip(InputStream zipped, boolean overwrite) throws ForbiddenException, ConflictException, ServerException {
        mountPoint.unzip(this, zipped, overwrite);
    }

    //

    @Override
    public String lock(long timeout) throws ForbiddenException, ConflictException, ServerException {
        return mountPoint.lock(this, timeout);
    }

    @Override
    public VirtualFile unlock(String lockToken) throws ForbiddenException, ConflictException, ServerException {
        mountPoint.unlock(this, lockToken);
        return this;
    }

    @Override
    public boolean isLocked() throws ServerException {
        return mountPoint.isLocked(this);
    }

    //

    @Override
    public Map<Principal, Set<String>> getPermissions() throws ServerException {
        return mountPoint.getACL(this).getPermissionMap();
    }

    @Override
    public List<AccessControlEntry> getACL() throws ServerException {
        return mountPoint.getACL(this).getEntries();
    }

    @Override
    public VirtualFile updateACL(List<AccessControlEntry> acl, boolean override, String lockToken)
            throws ForbiddenException, ServerException {
        mountPoint.updateACL(this, acl, override, lockToken);
        return this;
    }

    //

    @Override
    public VirtualFile createFile(String name, String mediaType, InputStream content)
            throws ForbiddenException, ConflictException, ServerException {
        return mountPoint.createFile(this, name, mediaType, content);
    }

    @Override
    public VirtualFile createFolder(String name) throws ForbiddenException, ConflictException, ServerException {
        return mountPoint.createFolder(this, name);
    }

    //

    @Override
    public FSMountPoint getMountPoint() {
        return mountPoint;
    }

    @Override
    public void accept(VirtualFileVisitor visitor) throws ServerException {
        visitor.visit(this);
    }

    @Override
    public LazyIterator<Pair<String, String>> countMd5Sums() throws ServerException {
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
        if (isFolder()) {
            return other.isFolder() ? getName().compareTo(other.getName()) : -1;
        } else if (other.isFolder()) {
            return 1;
        }
        return getName().compareTo(other.getName());
    }

   /* =================== */

    public final java.io.File getIoFile() {
        return ioFile;
    }

    Path getInternalPath() {
        return path;
    }
}
