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
import com.codenvy.api.core.notification.EventService;
import com.codenvy.api.core.util.ValueHolder;
import com.codenvy.api.vfs.server.ContentStream;
import com.codenvy.api.vfs.server.LazyIterator;
import com.codenvy.api.vfs.server.MountPoint;
import com.codenvy.api.vfs.server.Path;
import com.codenvy.api.vfs.server.PathLockFactory;
import com.codenvy.api.vfs.server.VirtualFile;
import com.codenvy.api.vfs.server.VirtualFileFilter;
import com.codenvy.api.vfs.server.VirtualFileSystemUser;
import com.codenvy.api.vfs.server.VirtualFileSystemUserContext;
import com.codenvy.api.vfs.server.VirtualFileVisitor;
import com.codenvy.api.vfs.server.observation.CreateEvent;
import com.codenvy.api.vfs.server.observation.DeleteEvent;
import com.codenvy.api.vfs.server.observation.MoveEvent;
import com.codenvy.api.vfs.server.observation.RenameEvent;
import com.codenvy.api.vfs.server.observation.UpdateACLEvent;
import com.codenvy.api.vfs.server.observation.UpdateContentEvent;
import com.codenvy.api.vfs.server.observation.UpdatePropertiesEvent;
import com.codenvy.api.vfs.server.search.SearcherProvider;
import com.codenvy.api.vfs.server.util.DeleteOnCloseFileInputStream;
import com.codenvy.api.vfs.server.util.NotClosableInputStream;
import com.codenvy.api.vfs.server.util.ZipContent;
import com.codenvy.api.vfs.shared.PropertyFilter;
import com.codenvy.api.vfs.shared.dto.AccessControlEntry;
import com.codenvy.api.vfs.shared.dto.Principal;
import com.codenvy.api.vfs.shared.dto.Property;
import com.codenvy.api.vfs.shared.dto.VirtualFileSystemInfo;
import com.codenvy.api.vfs.shared.dto.VirtualFileSystemInfo.BasicPermissions;
import com.codenvy.commons.lang.NameGenerator;
import com.codenvy.commons.lang.Pair;
import com.codenvy.commons.lang.cache.Cache;
import com.codenvy.commons.lang.cache.LoadingValueSLRUCache;
import com.codenvy.commons.lang.cache.SynchronizedCache;
import com.codenvy.dto.server.DtoFactory;
import com.google.common.collect.Sets;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteStreams;
import com.google.common.io.InputSupplier;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static com.codenvy.commons.lang.IoUtil.GIT_FILTER;
import static com.codenvy.commons.lang.IoUtil.deleteRecursive;
import static com.codenvy.commons.lang.IoUtil.nioCopy;

/**
 * Local filesystem implementation of MountPoint.
 *
 * @author andrew00x
 */
public class FSMountPoint implements MountPoint {
    private static final Logger LOG = LoggerFactory.getLogger(FSMountPoint.class);

    /*
     * Configuration parameters for caches.
     * Caches are split to the few partitions to reduce lock contention.
     * Use SLRU cache algorithm here.
     * This is required some additional parameters, e.g. protected and probationary size.
     * See details about SLRU algorithm: http://en.wikipedia.org/wiki/Cache_algorithms#Segmented_LRU
     */
    private static final int CACHE_PARTITIONS_NUM        = 1 << 3;
    private static final int CACHE_PROTECTED_SIZE        = 100;
    private static final int CACHE_PROBATIONARY_SIZE     = 200;
    private static final int MASK                        = CACHE_PARTITIONS_NUM - 1;
    private static final int PARTITION_PROTECTED_SIZE    = CACHE_PROTECTED_SIZE / CACHE_PARTITIONS_NUM;
    private static final int PARTITION_PROBATIONARY_SIZE = CACHE_PROBATIONARY_SIZE / CACHE_PARTITIONS_NUM;
    // end cache parameters

    private static final int MAX_BUFFER_SIZE  = 100 * 1024; // 100k
    private static final int COPY_BUFFER_SIZE = 8 * 1024; // 8k

    private static final long LOCK_FILE_TIMEOUT     = 60000; // 60 seconds
    private static final int  FILE_LOCK_MAX_THREADS = 1024;

    static final String SERVICE_DIR = ".vfs";

    static final String ACL_DIR         = SERVICE_DIR + java.io.File.separatorChar + "acl";
    static final String ACL_FILE_SUFFIX = "_acl";

    static final String LOCKS_DIR        = SERVICE_DIR + java.io.File.separatorChar + "locks";
    static final String LOCK_FILE_SUFFIX = "_lock";

    static final String PROPS_DIR              = SERVICE_DIR + java.io.File.separatorChar + "props";
    static final String PROPERTIES_FILE_SUFFIX = "_props";


    /** Hide .vfs directory. */
    private static final java.io.FilenameFilter SERVICE_DIR_FILTER = new java.io.FilenameFilter() {
        @Override
        public boolean accept(java.io.File dir, String name) {
            return !(SERVICE_DIR.equals(name));
        }
    };

    /** Hide .vfs and .git directories. */
    private static final java.io.FilenameFilter SERVICE_GIT_DIR_FILTER = new OrFileNameFilter(SERVICE_DIR_FILTER, GIT_FILTER);

    private static class OrFileNameFilter implements java.io.FilenameFilter {
        private final java.io.FilenameFilter[] filters;

        private OrFileNameFilter(java.io.FilenameFilter... filters) {
            this.filters = filters;
        }

        @Override
        public boolean accept(java.io.File dir, String name) {
            for (java.io.FilenameFilter filter : filters) {
                if (!filter.accept(dir, name)) {
                    return false;
                }
            }

            return true;
        }
    }

    private static final FileLock NO_LOCK = new FileLock("no_lock", 0);

    private class FileLockCache extends LoadingValueSLRUCache<Path, FileLock> {
        FileLockCache() {
            super(PARTITION_PROTECTED_SIZE, PARTITION_PROBATIONARY_SIZE);
        }

        @Override
        protected FileLock loadValue(Path key) {
            DataInputStream dis = null;

            try {
                java.io.File lockFile = getLockFile(key);
                if (lockFile.exists()) {
                    dis = new DataInputStream(new BufferedInputStream(new FileInputStream(lockFile)));
                    return locksSerializer.read(dis);
                }
                return NO_LOCK;
            } catch (IOException e) {
                String msg = String.format("Unable read lock for '%s'. ", key);
                LOG.error(msg + e.getMessage(), e); // More details in log but do not show internal error to caller.
                throw new RuntimeException(msg);
            } finally {
                closeQuietly(dis);
            }
        }
    }


    private class FileMetadataCache extends LoadingValueSLRUCache<Path, Map<String, String[]>> {
        FileMetadataCache() {
            super(PARTITION_PROTECTED_SIZE, PARTITION_PROBATIONARY_SIZE);
        }

        @Override
        protected Map<String, String[]> loadValue(Path key) {
            DataInputStream dis = null;
            try {
                java.io.File metadataFile = getMetadataFile(key);
                if (metadataFile.exists()) {
                    dis = new DataInputStream(new BufferedInputStream(new FileInputStream(metadataFile)));
                    return metadataSerializer.read(dis);
                }
                return Collections.emptyMap();
            } catch (IOException e) {
                String msg = String.format("Unable read properties for '%s'. ", key);
                LOG.error(msg + e.getMessage(), e); // More details in log but do not show internal error to caller.
                throw new RuntimeException(msg);
            } finally {
                closeQuietly(dis);
            }
        }
    }


    private class AccessControlListCache extends LoadingValueSLRUCache<Path, AccessControlList> {
        private AccessControlListCache() {
            super(PARTITION_PROTECTED_SIZE, PARTITION_PROBATIONARY_SIZE);
        }

        @Override
        protected AccessControlList loadValue(Path key) {
            DataInputStream dis = null;
            try {
                java.io.File aclFile = getAclFile(key);
                if (aclFile.exists()) {
                    dis = new DataInputStream(new BufferedInputStream(new FileInputStream(aclFile)));
                    return aclSerializer.read(dis);
                }

                // TODO : REMOVE!!! Temporary default ACL until will have client side for real manage
                if (key.isRoot()) {
                    final Map<Principal, Set<String>> dummy = new HashMap<>(2);
                    final Principal developer = DtoFactory.getInstance().createDto(Principal.class)
                                                          .withName("workspace/developer").withType(Principal.Type.GROUP);
                    final Principal other = DtoFactory.getInstance().createDto(Principal.class)
                                                      .withName(VirtualFileSystemInfo.ANY_PRINCIPAL).withType(Principal.Type.USER);
                    dummy.put(developer, Sets.newHashSet(BasicPermissions.ALL.value()));
                    dummy.put(other, Sets.newHashSet(BasicPermissions.READ.value()));
                    return new AccessControlList(dummy);
                }
                return new AccessControlList();
            } catch (IOException e) {
                String msg = String.format("Unable read ACL for '%s'. ", key);
                LOG.error(msg + e.getMessage(), e); // More details in log but do not show internal error to caller.
                throw new RuntimeException(msg);
            } finally {
                closeQuietly(dis);
            }
        }
    }

    private final String           workspaceId;
    private final java.io.File     ioRoot;
    private final EventService     eventService;
    private final SearcherProvider searcherProvider;

    /* NOTE -- This does not related to virtual file system locking in any kind. -- */
    private final PathLockFactory pathLockFactory;

    private final VirtualFileImpl root;

    /* ----- Access control list feature. ----- */
    private final AccessControlListSerializer      aclSerializer;
    private final Cache<Path, AccessControlList>[] aclCache;

    /* ----- Virtual file system lock feature. ----- */
    private final FileLockSerializer      locksSerializer;
    private final Cache<Path, FileLock>[] lockTokensCache;

    /* ----- File metadata. ----- */
    private final FileMetadataSerializer               metadataSerializer;
    private final Cache<Path, Map<String, String[]>>[] metadataCache;

    private final VirtualFileSystemUserContext userContext;

    /**
     * @param workspaceId
     *         id of workspace to which this MountPoint belongs to
     * @param ioRoot
     *         root directory for virtual file system. Any file in higher level than root are not accessible through
     *         virtual file system API.
     */
    @SuppressWarnings("unchecked")
    FSMountPoint(String workspaceId, java.io.File ioRoot, EventService eventService, SearcherProvider searcherProvider) {
        this.workspaceId = workspaceId;
        this.ioRoot = ioRoot;
        this.eventService = eventService;
        this.searcherProvider = searcherProvider;

        root = new VirtualFileImpl(ioRoot, Path.ROOT, pathToId(Path.ROOT), this);
        pathLockFactory = new PathLockFactory(FILE_LOCK_MAX_THREADS);

        aclSerializer = new AccessControlListSerializer();
        aclCache = new Cache[CACHE_PARTITIONS_NUM];

        locksSerializer = new FileLockSerializer();
        lockTokensCache = new Cache[CACHE_PARTITIONS_NUM];

        metadataSerializer = new FileMetadataSerializer();
        metadataCache = new Cache[CACHE_PARTITIONS_NUM];

        for (int i = 0; i < CACHE_PARTITIONS_NUM; i++) {
            aclCache[i] = new SynchronizedCache(new AccessControlListCache());
            lockTokensCache[i] = new SynchronizedCache(new FileLockCache());
            metadataCache[i] = new SynchronizedCache(new FileMetadataCache());
        }
        userContext = VirtualFileSystemUserContext.newInstance();
    }

    @Override
    public String getWorkspaceId() {
        return workspaceId;
    }

    @Override
    public VirtualFileImpl getRoot() {
        return root;
    }

    @Override
    public VirtualFileImpl getVirtualFileById(String id) throws NotFoundException, ForbiddenException, ServerException {
        if (root.getId().equals(id)) {
            return root;
        }
        return doGetVirtualFile(idToPath(id));
    }

    @Override
    public SearcherProvider getSearcherProvider() {
        return searcherProvider;
    }

    @Override
    public EventService getEventService() {
        return eventService;
    }

    @Override
    public VirtualFileImpl getVirtualFile(String path) throws NotFoundException, ForbiddenException, ServerException {
        if (path == null || path.isEmpty() || "/".equals(path)) {
            return getRoot();
        }
        return doGetVirtualFile(Path.fromString(path));
    }

    private VirtualFileImpl doGetVirtualFile(Path vfsPath) throws NotFoundException, ForbiddenException, ServerException {
        final VirtualFileImpl virtualFile =
                new VirtualFileImpl(new java.io.File(ioRoot, toIoPath(vfsPath)), vfsPath, pathToId(vfsPath), this);
        if (!virtualFile.exists()) {
            throw new NotFoundException(String.format("Object '%s' does not exists. ", vfsPath));
        }
        final PathLockFactory.PathLock lock = pathLockFactory.getLock(virtualFile.getInternalPath(), false).acquire(LOCK_FILE_TIMEOUT);
        try {
            if (!hasPermission(virtualFile, BasicPermissions.READ.value(), true)) {
                throw new ForbiddenException(String.format("Unable get item '%s'. Operation not permitted. ", virtualFile.getPath()));
            }
        } finally {
            lock.release();
        }
        return virtualFile;
    }

    /** Call after unmount this MountPoint. Clear all caches. */
    public void reset() {
        clearMetadataCache();
        clearAclCache();
        clearLockTokensCache();
    }

    // Used in tests. Need this to check state of PathLockFactory.
    // All locks MUST be released at the end of request lifecycle.
    PathLockFactory getPathLockFactory() {
        return pathLockFactory;
    }

   /* =================================== INTERNAL =================================== */

    // All methods below designed to be used from VirtualFileImpl ONLY.

    Path idToPath(String id) throws NotFoundException {
        if (id.equals(root.getId())) {
            return Path.ROOT;
        }
        final String raw;
        try {
            raw = new String(Base64.decodeBase64(id), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // Should never happen.
            throw new IllegalStateException(e.getMessage(), e);
        }
        final int split = raw.indexOf(':') + 1;
        if (split > 0) {
            return Path.fromString(raw.substring(split));
        }
        // Invalid format of ID
        throw new NotFoundException(String.format("Object '%s' does not exists. ", id));
    }


    String pathToId(Path path) {
        try {
            return Base64.encodeBase64URLSafeString((workspaceId + ':' + (path.isRoot() ? "root" : path.toString())).getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            // Should never happen.
            throw new IllegalStateException(e.getMessage(), e);
        }
    }


    VirtualFileImpl getParent(VirtualFileImpl virtualFile) {
        if (virtualFile.isRoot()) {
            return null;
        }
        final Path parentPath = virtualFile.getInternalPath().getParent();
        return new VirtualFileImpl(new java.io.File(ioRoot, toIoPath(parentPath)), parentPath, pathToId(parentPath), this);
    }


    VirtualFileImpl getChild(VirtualFileImpl parent, String name) throws ForbiddenException {
        if (parent.isFile()) {
            return null;
        }
        final Path childPath = parent.getInternalPath().newPath(name);
        final PathLockFactory.PathLock lock = pathLockFactory.getLock(childPath, false).acquire(LOCK_FILE_TIMEOUT);
        try {
            final VirtualFileImpl child =
                    new VirtualFileImpl(new java.io.File(parent.getIoFile(), name), childPath, pathToId(childPath), this);
            if (child.exists()) {
                if (hasPermission(child, BasicPermissions.READ.value(), true)) {
                    return child;
                }
                throw new ForbiddenException(String.format("Unable get item '%s'. Operation not permitted. ", child.getPath()));
            }
        } finally {
            lock.release();
        }
        return null;
    }


    LazyIterator<VirtualFile> getChildren(VirtualFileImpl parent, VirtualFileFilter filter) throws ServerException {
        if (!parent.isFolder()) {
            //throw new InvalidArgumentException(String.format("Unable get children. Item '%s' is not a folder. ", parent.getPath()));
            return LazyIterator.emptyIterator();
        }

        final PathLockFactory.PathLock parentLock = pathLockFactory.getLock(parent.getInternalPath(), false).acquire(LOCK_FILE_TIMEOUT);
        List<VirtualFile> children;
        try {
            if (parent.isRoot()) {
                // NOTE: We do not check read permissions when access to ROOT folder.
                if (!hasPermission(parent, BasicPermissions.READ.value(), false)) {
                    // User has not access to ROOT folder.
                    return LazyIterator.emptyIterator();
                }
            }
            children = doGetChildren(parent, SERVICE_GIT_DIR_FILTER);
            for (Iterator<VirtualFile> iterator = children.iterator(); iterator.hasNext(); ) {
                VirtualFile child = iterator.next();
                // Check permission directly for current file only.
                // We know the parent is accessible for current user otherwise we should not be here.
                if (!hasPermission((VirtualFileImpl)child, BasicPermissions.READ.value(), false) || !filter.accept(child)) {
                    iterator.remove(); // Do not show item in list if current user has not permission to see it
                }
            }
        } finally {
            parentLock.release();
        }
        // Always sort to get the exact same order of files for each listing.
        Collections.sort(children);
        return LazyIterator.fromList(children);
    }


    // UNDER LOCK
    private List<VirtualFile> doGetChildren(VirtualFileImpl virtualFile, java.io.FilenameFilter filter) throws ServerException {
        final String[] names = virtualFile.getIoFile().list(filter);
        if (names == null) {
            // Something wrong. According to java docs may be null only if i/o error occurs.
            throw new ServerException(String.format("Unable get children '%s'. ", virtualFile.getPath()));
        }
        final List<VirtualFile> children = new ArrayList<>(names.length);
        for (String name : names) {
            final Path childPath = virtualFile.getInternalPath().newPath(name);
            children.add(new VirtualFileImpl(new java.io.File(ioRoot, toIoPath(childPath)), childPath, pathToId(childPath), this));
        }
        return children;
    }


    VirtualFileImpl createFile(VirtualFileImpl parent, String name, String mediaType, InputStream content)
            throws ForbiddenException, ConflictException, ServerException {
        checkName(name);

        if (!parent.isFolder()) {
            throw new ForbiddenException("Unable create new file. Item specified as parent is not a folder. ");
        }

        final PathLockFactory.PathLock parentLock = pathLockFactory.getLock(parent.getInternalPath(), true).acquire(LOCK_FILE_TIMEOUT);
        try {
            if (!hasPermission(parent, BasicPermissions.WRITE.value(), true)) {
                throw new ForbiddenException(String.format("Unable create new file in '%s'. Operation not permitted. ", parent.getPath()));
            }
            final Path newPath = parent.getInternalPath().newPath(name);
            final java.io.File newIoFile = new java.io.File(ioRoot, toIoPath(newPath));
            try {
                if (!newIoFile.createNewFile()) // atomic
                {
                    throw new ConflictException(String.format("Item '%s' already exists. ", newPath));
                }
            } catch (IOException e) {
                String msg = String.format("Unable create new file '%s'. ", newPath);
                LOG.error(msg + e.getMessage(), e); // More details in log but do not show internal error to caller.
                throw new ServerException(msg);
            }

            final VirtualFileImpl newVirtualFile = new VirtualFileImpl(newIoFile, newPath, pathToId(newPath), this);
            // Update content if any.
            if (content != null) {
                doUpdateContent(newVirtualFile, mediaType, content);
            }

            if (searcherProvider != null) {
                try {
                    searcherProvider.getSearcher(this, true).add(newVirtualFile);
                } catch (ServerException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
            eventService.publish(new CreateEvent(workspaceId, newVirtualFile.getPath(), false));
            return newVirtualFile;
        } finally {
            parentLock.release();
        }
    }


    VirtualFileImpl createFolder(VirtualFileImpl parent, String name) throws ForbiddenException, ConflictException, ServerException {
        checkName(name);

        if (!parent.isFolder()) {
            throw new ForbiddenException("Unable create folder. Item specified as parent is not a folder. ");
        }

        final PathLockFactory.PathLock parentLock = pathLockFactory.getLock(parent.getInternalPath(), true).acquire(LOCK_FILE_TIMEOUT);
        try {
            if (!hasPermission(parent, BasicPermissions.WRITE.value(), true)) {
                throw new ForbiddenException(
                        String.format("Unable create new folder in '%s'. Operation not permitted. ", parent.getPath()));
            }
            // Name may be hierarchical, e.g. folder1/folder2/folder3.
            // Some folder in hierarchy may already exists but at least one folder must be created.
            // If no one folder created then ItemAlreadyExistException is thrown.
            Path currentPath = parent.getInternalPath();
            Path newPath = null;
            java.io.File newIoFile = null;
            for (String element : Path.fromString(name).elements()) {
                currentPath = currentPath.newPath(element);
                java.io.File currentIoFile = new java.io.File(ioRoot, toIoPath(currentPath));
                if (currentIoFile.mkdir()) {
                    newPath = currentPath;
                    newIoFile = currentIoFile;
                }
            }

            if (newPath == null) {
                // Folder or folder hierarchy already exists.
                throw new ConflictException(String.format("Item '%s' already exists. ", parent.getInternalPath().newPath(name)));
            }

            // Return first created folder, e.g. assume we need create: folder1/folder2/folder3 in specified folder.
            // If folder1 already exists then return folder2 as first created in hierarchy.
            final VirtualFileImpl newVirtualFile = new VirtualFileImpl(newIoFile, newPath, pathToId(newPath), this);
            eventService.publish(new CreateEvent(workspaceId, newVirtualFile.getPath(), true));
            return newVirtualFile;
        } finally {
            parentLock.release();
        }
    }


    VirtualFileImpl copy(VirtualFileImpl source, VirtualFileImpl parent) throws ForbiddenException, ConflictException, ServerException {
        if (source.getInternalPath().equals(parent.getInternalPath())) {
            throw new ForbiddenException("Item cannot be copied to itself. ");
        }
        if (!parent.isFolder()) {
            throw new ForbiddenException("Unable copy item. Item specified as parent is not a folder. ");
        }
        PathLockFactory.PathLock sourceLock = null;
        PathLockFactory.PathLock parentLock = null;
        try {
            sourceLock = pathLockFactory.getLock(source.getInternalPath(), true).acquire(LOCK_FILE_TIMEOUT);
            parentLock = pathLockFactory.getLock(parent.getInternalPath(), true).acquire(LOCK_FILE_TIMEOUT);
            if (!hasPermission(parent, BasicPermissions.WRITE.value(), true)) {
                throw new ForbiddenException(String.format("Unable copy item '%s' to %s. Operation not permitted. ",
                                                           source.getPath(), parent.getPath()));
            }
            final Path newPath = parent.getInternalPath().newPath(source.getName());
            final VirtualFileImpl destination =
                    new VirtualFileImpl(new java.io.File(ioRoot, toIoPath(newPath)), newPath, pathToId(newPath), this);
            if (destination.exists()) {
                throw new ConflictException(String.format("Item '%s' already exists. ", newPath));
            }
            doCopy(source, destination);
            eventService.publish(new CreateEvent(workspaceId, destination.getPath(), source.isFolder()));
            return destination;
        } finally {
            if (sourceLock != null) {
                sourceLock.release();
            }
            if (parentLock != null) {
                parentLock.release();
            }
        }
    }


    // UNDER LOCK
    private void doCopy(VirtualFileImpl source, VirtualFileImpl destination) throws ServerException {
        // Source is locked, but destination is not.
        // It looks like not necessary to lock destination path since it does not exists yet.
        final java.io.File sourceMetadataFile = getMetadataFile(source.getInternalPath());
        final java.io.File destinationMetadataFile = getMetadataFile(destination.getInternalPath());
        try {
            // First copy metadata (properties) for source.
            // If we do in this way and fail cause to any i/o or
            // other error client will see error and may try to copy again.
            // But if we successfully copy tree (or single file) and then
            // fail to copy metadata client may not try to copy again
            // because copy destination already exists.

            // NOTE: Don't copy lock and permissions, just files itself and metadata files.

            // Check recursively permissions of sources in case of folder
            // and add all item current user cannot read in skip list.
            java.io.FilenameFilter filter = null;
            if (source.isFolder()) {
                final LinkedList<VirtualFileImpl> skipList = new LinkedList<>();
                final LinkedList<VirtualFile> q = new LinkedList<>();
                q.add(source);
                while (!q.isEmpty()) {
                    for (VirtualFile current : doGetChildren((VirtualFileImpl)q.pop(), SERVICE_GIT_DIR_FILTER)) {
                        // Check permission directly for current file only.
                        // We already know parent accessible for current user otherwise we should not be here.
                        // Ignore item if don't have permission to read it.
                        if (!hasPermission((VirtualFileImpl)current, BasicPermissions.READ.value(), false)) {
                            skipList.add((VirtualFileImpl)current);
                        } else {
                            if (current.isFolder()) {
                                q.add(current);
                            }
                        }
                    }
                }
                if (!skipList.isEmpty()) {
                    filter = new java.io.FilenameFilter() {
                        @Override
                        public boolean accept(java.io.File dir, String name) {
                            final String testPath = dir.getAbsolutePath() + java.io.File.separatorChar + name;
                            for (VirtualFileImpl skipFile : skipList) {
                                java.io.File metadataFile;
                                if (testPath.startsWith(skipFile.getIoFile().getAbsolutePath())
                                    || ((metadataFile = getMetadataFile(skipFile.getInternalPath())).exists() &&
                                        testPath.startsWith(metadataFile.getAbsolutePath()))) {
                                    return false;
                                }
                            }
                            return true;
                        }
                    };
                }
            }

            if (sourceMetadataFile.exists()) {
                nioCopy(sourceMetadataFile, destinationMetadataFile, filter);
            }
            nioCopy(source.getIoFile(), destination.getIoFile(), filter);

            if (searcherProvider != null) {
                try {
                    searcherProvider.getSearcher(this, true).add(destination);
                } catch (ServerException e) {
                    LOG.error(e.getMessage(), e); // just log about i/o error in index
                }
            }
        } catch (IOException e) {
            // Do nothing for file tree. Let client side decide what to do.
            // User may delete copied files (if any) and try copy again.
            String msg = String.format("Unable copy '%s' to '%s'. ", source, destination);
            LOG.error(msg + e.getMessage(), e); // More details in log but do not show internal error to caller.
            throw new ServerException(msg);
        }
    }


    VirtualFileImpl rename(VirtualFileImpl virtualFile, String newName, String newMediaType, String lockToken)
            throws ForbiddenException, ConflictException, ServerException {
        if (virtualFile.isRoot()) {
            throw new ForbiddenException("Unable rename root folder. ");
        }
        final String sourcePath = virtualFile.getPath();
        final VirtualFileImpl parent = getParent(virtualFile);
        final PathLockFactory.PathLock parentLock =
                pathLockFactory.getLock(parent.getInternalPath(), true).acquire(LOCK_FILE_TIMEOUT);
        try {
            if (!hasPermission(virtualFile, BasicPermissions.WRITE.value(), true)) {
                throw new ForbiddenException(String.format("Unable rename item '%s'. Operation not permitted. ", sourcePath));
            }
            if (virtualFile.isFile() && !validateLockTokenIfLocked(virtualFile, lockToken)) {
                throw new ForbiddenException(String.format("Unable rename file '%s'. File is locked. ", sourcePath));
            }
            final String name = virtualFile.getName();
            final VirtualFileImpl renamed;
            if (!(newName == null || name.equals(newName))) {
                final Path newPath = virtualFile.getInternalPath().getParent().newPath(newName);
                renamed = new VirtualFileImpl(new java.io.File(ioRoot, toIoPath(newPath)), newPath, pathToId(newPath), this);
                if (renamed.exists()) {
                    throw new ConflictException(String.format("Item '%s' already exists. ", renamed.getName()));
                }
                // use copy and delete
                doCopy(virtualFile, renamed);
                doDelete(virtualFile, lockToken);
            } else {
                renamed = virtualFile;
            }

            if (newMediaType != null) {
                setProperty(renamed, "vfs:mimeType", newMediaType);
                if (!virtualFile.getIoFile().setLastModified(System.currentTimeMillis())) {
                    LOG.warn("Unable to set timestamp to '{}'. ", virtualFile.getIoFile());
                }
            }
            eventService.publish(new RenameEvent(workspaceId, renamed.getPath(), sourcePath, renamed.isFolder()));
            return renamed;
        } finally {
            parentLock.release();
        }
    }


    VirtualFileImpl move(VirtualFileImpl source, VirtualFileImpl parent, String lockToken)
            throws ForbiddenException, ConflictException, ServerException {
        final String sourcePath = source.getPath();
        final String parentPath = parent.getPath();
        if (source.isRoot()) {
            throw new ForbiddenException("Unable move root folder. ");
        }
        if (source.getInternalPath().equals(parent.getInternalPath())) {
            throw new ForbiddenException("Item cannot be moved to itself. ");
        }
        if (!parent.isFolder()) {
            throw new ForbiddenException("Unable move. Item specified as parent is not a folder. ");
        }
        if (source.isFolder() && parent.getInternalPath().isChild(source.getInternalPath())) {
            throw new ForbiddenException(String.format("Unable move item '%s' to '%s'. Item may not have itself as parent. ",
                                                       sourcePath, parentPath));
        }

        PathLockFactory.PathLock sourceLock = null;
        PathLockFactory.PathLock parentLock = null;
        try {
            sourceLock = pathLockFactory.getLock(source.getInternalPath(), true).acquire(LOCK_FILE_TIMEOUT);
            parentLock = pathLockFactory.getLock(parent.getInternalPath(), true).acquire(LOCK_FILE_TIMEOUT);

            if (!(hasPermission(source, BasicPermissions.WRITE.value(), true)
                  && hasPermission(parent, BasicPermissions.WRITE.value(), true))) {
                throw new ForbiddenException(
                        String.format("Unable move item '%s' to %s. Operation not permitted. ", sourcePath, parentPath));
            }
            // Even we check lock before delete original file check it here also to have better behaviour.
            // Prevent even copy original file if we already know it is locked.
            if (source.isFile() && !validateLockTokenIfLocked(source, lockToken)) {
                throw new ForbiddenException(String.format("Unable move file '%s'. File is locked. ", sourcePath));
            }
            final Path newPath = parent.getInternalPath().newPath(source.getName());
            VirtualFileImpl destination =
                    new VirtualFileImpl(new java.io.File(ioRoot, toIoPath(newPath)), newPath, pathToId(newPath), this);
            if (destination.exists()) {
                throw new ConflictException(String.format("Item '%s' already exists. ", newPath));
            }
            // use copy and delete
            doCopy(source, destination);
            doDelete(source, lockToken);
            eventService.publish(new MoveEvent(workspaceId, destination.getPath(), sourcePath, destination.isFolder()));
            return destination;
        } finally {
            if (sourceLock != null) {
                sourceLock.release();
            }
            if (parentLock != null) {
                parentLock.release();
            }
        }
    }


    ContentStream getContent(VirtualFileImpl virtualFile) throws ForbiddenException, ServerException {
        if (!virtualFile.isFile()) {
            throw new ForbiddenException(String.format("Unable get content. Item '%s' is not a file. ", virtualFile.getPath()));
        }

        final PathLockFactory.PathLock lock = pathLockFactory.getLock(virtualFile.getInternalPath(), false).acquire(LOCK_FILE_TIMEOUT);
        try {
            final java.io.File ioFile = virtualFile.getIoFile();
            FileInputStream fIn = null;
            try {
                final long fLength = ioFile.length();
                if (fLength <= MAX_BUFFER_SIZE) {
                    // If file small enough save its content in memory.
                    fIn = new FileInputStream(ioFile);
                    final byte[] buff = new byte[(int)fLength];
                    int offset = 0;
                    int len = buff.length;
                    int r;
                    while ((r = fIn.read(buff, offset, len)) > 0) {
                        offset += r;
                        len -= r;
                    }
                    return new ContentStream(virtualFile.getName(), new ByteArrayInputStream(buff),
                                             virtualFile.getMediaType(), buff.length, new Date(ioFile.lastModified()));
                }

                // Otherwise copy this file to be able release the file lock before leave this method.
                final java.io.File f = java.io.File.createTempFile("spool_file", null);
                nioCopy(ioFile, f, null);
                return new ContentStream(virtualFile.getName(), new DeleteOnCloseFileInputStream(f),
                                         virtualFile.getMediaType(), fLength, new Date(ioFile.lastModified()));
            } catch (IOException e) {
                String msg = String.format("Unable get content of '%s'. ", virtualFile.getPath());
                LOG.error(msg + e.getMessage(), e); // More details in log but do not show internal error to caller.
                throw new ServerException(msg);
            } finally {
                closeQuietly(fIn);
            }
        } finally {
            lock.release();
        }
    }


    void updateContent(VirtualFileImpl virtualFile, String mediaType, InputStream content, String lockToken)
            throws ForbiddenException, ServerException {
        updateContent(virtualFile, mediaType, content, lockToken, true);
    }


    void updateContent(VirtualFileImpl virtualFile, InputStream content, String lockToken) throws ForbiddenException, ServerException {
        updateContent(virtualFile, null, content, lockToken, false);
    }


    private void updateContent(VirtualFileImpl virtualFile, String mediaType, InputStream content, String lockToken,
                               boolean updateMediaType) throws ForbiddenException, ServerException {
        if (!virtualFile.isFile()) {
            throw new ForbiddenException(String.format("Unable update content. Item '%s' is not file. ", virtualFile.getPath()));
        }

        final PathLockFactory.PathLock lock = pathLockFactory.getLock(virtualFile.getInternalPath(), true).acquire(LOCK_FILE_TIMEOUT);
        try {
            if (!hasPermission(virtualFile, BasicPermissions.WRITE.value(), true)) {
                throw new ForbiddenException(
                        String.format("Unable update content of file '%s'. Operation not permitted. ", virtualFile.getPath()));
            }
            if (!validateLockTokenIfLocked(virtualFile, lockToken)) {
                throw new ForbiddenException(String.format("Unable update content of file '%s'. File is locked. ", virtualFile.getPath()));
            }

            if (updateMediaType) {
                doUpdateContent(virtualFile, mediaType, content);
            } else {
                doUpdateContent(virtualFile, content);
            }

            if (searcherProvider != null) {
                try {
                    searcherProvider.getSearcher(this, true).update(virtualFile);
                } catch (ServerException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
            eventService.publish(new UpdateContentEvent(workspaceId, virtualFile.getPath()));
        } finally {
            lock.release();
        }
    }


    // UNDER LOCK
    private void doUpdateContent(VirtualFileImpl virtualFile, String mediaType, InputStream content) throws ServerException {
        doUpdateContent(virtualFile, content);
        setProperty(virtualFile, "vfs:mimeType", mediaType);
    }


    // UNDER LOCK
    private void doUpdateContent(VirtualFileImpl virtualFile, InputStream content) throws ServerException {
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(virtualFile.getIoFile());
            final byte[] buff = new byte[COPY_BUFFER_SIZE];
            int r;
            while ((r = content.read(buff)) != -1) {
                fOut.write(buff, 0, r);
            }
        } catch (IOException e) {
            String msg = String.format("Unable set content of '%s'. ", virtualFile.getPath());
            LOG.error(msg + e.getMessage(), e); // More details in log but do not show internal error to caller.
            throw new ServerException(msg);
        } finally {
            closeQuietly(fOut);
        }
    }


    void delete(VirtualFileImpl virtualFile, String lockToken) throws ForbiddenException, ServerException {
        if (virtualFile.isRoot()) {
            throw new ForbiddenException("Unable delete root folder. ");
        }
        final PathLockFactory.PathLock lock = pathLockFactory.getLock(virtualFile.getInternalPath(), true).acquire(LOCK_FILE_TIMEOUT);
        final String myPath = virtualFile.getPath();
        final boolean folder = virtualFile.isFolder();
        try {
            if (!hasPermission(virtualFile, BasicPermissions.WRITE.value(), true)) {
                throw new ForbiddenException(String.format("Unable delete item '%s'. Operation not permitted. ", myPath));
            }
            if (virtualFile.isFile() && !validateLockTokenIfLocked(virtualFile, lockToken)) {
                throw new ForbiddenException(String.format("Unable delete item '%s'. Item is locked. ", myPath));
            }

            doDelete(virtualFile, lockToken);
            eventService.publish(new DeleteEvent(workspaceId, myPath, folder));
        } finally {
            lock.release();
        }
    }

    // UNDER LOCK
    private void doDelete(VirtualFileImpl virtualFile, String lockToken) throws ForbiddenException, ServerException {
        if (virtualFile.isFolder()) {
            final LinkedList<VirtualFile> q = new LinkedList<>();
            q.add(virtualFile);
            while (!q.isEmpty()) {
                for (VirtualFile child : doGetChildren((VirtualFileImpl)q.pop(), SERVICE_GIT_DIR_FILTER)) {
                    // Check permission directly for current file only.
                    // We already know parent may be deleted by current user otherwise we should not be here.
                    if (!hasPermission((VirtualFileImpl)child, BasicPermissions.WRITE.value(), false)) {
                        throw new ForbiddenException(String.format("Unable delete item '%s'. Operation not permitted. ", child.getPath()));
                    }
                    if (child.isFolder()) {
                        q.push(child);
                    } else if (isLocked((VirtualFileImpl)child)) {
                        // Do not check lock token here. It checked only when remove file directly.
                        // If folder contains locked children it may not be deleted.
                        throw new ForbiddenException(String.format("Unable delete item '%s'. Child item '%s' is locked. ",
                                                                   virtualFile.getPath(), child.getPath()));
                    }
                }
            }
        }

        // unlock file
        if (virtualFile.isFile()) {
            final FileLock fileLock = checkIsLockValidAndGet(virtualFile);
            if (NO_LOCK != fileLock) {
                doUnlock(virtualFile, fileLock, lockToken);
            }
        }

        // clear caches
        clearAclCache();
        clearLockTokensCache();
        clearMetadataCache();

        final String path = virtualFile.getPath();
        if (!deleteRecursive(virtualFile.getIoFile())) {
            LOG.error("Unable delete file {}", virtualFile.getIoFile());
            throw new ServerException(String.format("Unable delete item '%s'. ", virtualFile.getPath()));
        }

        // delete ACL file
        final java.io.File aclFile = getAclFile(virtualFile.getInternalPath());
        if (aclFile.delete()) {
            if (aclFile.exists()) {
                LOG.error("Unable delete ACL file {}", aclFile);
                throw new ServerException(String.format("Unable delete item '%s'. ", virtualFile.getPath()));
            }
        }

        // delete metadata file
        final java.io.File metadataFile = getMetadataFile(virtualFile.getInternalPath());
        if (metadataFile.delete()) {
            if (metadataFile.exists()) {
                LOG.error("Unable delete file metadata {}", metadataFile);
                throw new ServerException(String.format("Unable delete item '%s'. ", virtualFile.getPath()));
            }
        }

        if (searcherProvider != null) {
            try {
                searcherProvider.getSearcher(this, true).delete(path);
            } catch (ServerException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }


    private void clearLockTokensCache() {
        for (Cache<Path, FileLock> cache : lockTokensCache) {
            cache.clear();
        }
    }


    private void clearAclCache() {
        for (Cache<Path, AccessControlList> cache : aclCache) {
            cache.clear();
        }
    }


    private void clearMetadataCache() {
        for (Cache<Path, Map<String, String[]>> cache : metadataCache) {
            cache.clear();
        }
    }


    ContentStream zip(VirtualFileImpl virtualFile, VirtualFileFilter filter) throws ForbiddenException, ServerException {
        if (!virtualFile.isFolder()) {
            throw new ForbiddenException(String.format("Unable export to zip. Item '%s' is not a folder. ", virtualFile.getPath()));
        }
        final PathLockFactory.PathLock lock = pathLockFactory.getLock(virtualFile.getInternalPath(), false).acquire(LOCK_FILE_TIMEOUT);
        try {
            java.io.File zipFile = null;
            FileOutputStream out = null;
            try {
                zipFile = java.io.File.createTempFile("export", ".zip");
                out = new FileOutputStream(zipFile);
                final ZipOutputStream zipOut = new ZipOutputStream(out);
                final LinkedList<VirtualFile> q = new LinkedList<>();
                q.add(virtualFile);
                final int zipEntryNameTrim = virtualFile.getInternalPath().length();
                final byte[] buff = new byte[COPY_BUFFER_SIZE];
                while (!q.isEmpty()) {
                    for (VirtualFile current : doGetChildren((VirtualFileImpl)q.pop(), SERVICE_GIT_DIR_FILTER)) {
                        // (1) Check filter.
                        // (2) Check permission directly for current file only.
                        // We already know parent accessible for current user otherwise we should not be here.
                        // Ignore item if don't have permission to read it.
                        if (filter.accept(current) && hasPermission((VirtualFileImpl)current, BasicPermissions.READ.value(), false)) {
                            final String zipEntryName =
                                    ((VirtualFileImpl)current).getInternalPath().subPath(zipEntryNameTrim).toString().substring(1);
                            if (current.isFile()) {
                                final ZipEntry zipEntry = new ZipEntry(zipEntryName);
                                zipEntry.setTime(virtualFile.getLastModificationDate());
                                zipOut.putNextEntry(zipEntry);
                                InputStream in = null;
                                try {
                                    in = new FileInputStream(((VirtualFileImpl)current).getIoFile());
                                    int r;
                                    while ((r = in.read(buff)) != -1) {
                                        zipOut.write(buff, 0, r);
                                    }
                                } finally {
                                    closeQuietly(in);
                                }
                                zipOut.closeEntry();
                            } else if (current.isFolder()) {
                                final ZipEntry zipEntry = new ZipEntry(zipEntryName + '/');
                                zipEntry.setTime(0);
                                zipOut.putNextEntry(zipEntry);
                                q.add(current);
                                zipOut.closeEntry();
                            }
                        }
                    }
                }
                closeQuietly(zipOut);
                final String name = virtualFile.getName() + ".zip";
                return new ContentStream(name, new DeleteOnCloseFileInputStream(zipFile), "application/zip", zipFile.length(), new Date());
            } catch (IOException | RuntimeException ioe) {
                if (zipFile != null) {
                    zipFile.delete();
                }
                throw new ServerException(ioe.getMessage(), ioe);
            } finally {
                closeQuietly(out);
            }
        } finally {
            lock.release();
        }
    }


    void unzip(VirtualFileImpl parent, InputStream zipped, boolean overwrite)
            throws ForbiddenException, ConflictException, ServerException {
        if (!parent.isFolder()) {
            throw new ForbiddenException(String.format("Unable import zip content. Item '%s' is not a folder. ", parent.getPath()));
        }
        final ZipContent zipContent;
        try {
            zipContent = ZipContent.newInstance(zipped);
        } catch (IOException e) {
            throw new ServerException(e.getMessage(), e);
        }
        final PathLockFactory.PathLock lock = pathLockFactory.getLock(parent.getInternalPath(), true).acquire(LOCK_FILE_TIMEOUT);
        try {
            if (!hasPermission(parent, BasicPermissions.WRITE.value(), true)) {
                throw new ForbiddenException(String.format("Unable import from zip to '%s'. Operation not permitted. ", parent.getPath()));
            }

            ZipInputStream zip = null;
            try {
                zip = new ZipInputStream(zipContent.zippedData);
                // Wrap zip stream to prevent close it. We can pass stream to other method and it can read content of current
                // ZipEntry but not able to close original stream of ZIPed data.
                InputStream noCloseZip = new NotClosableInputStream(zip);
                ZipEntry zipEntry;
                while ((zipEntry = zip.getNextEntry()) != null) {
                    VirtualFileImpl current = parent;
                    final Path relPath = Path.fromString(zipEntry.getName());
                    final String name = relPath.getName();
                    if (relPath.length() > 1) {
                        // create all required parent directories
                        final Path parentPath = parent.getInternalPath().newPath(relPath.subPath(0, relPath.length() - 1));
                        current = new VirtualFileImpl(new java.io.File(ioRoot, toIoPath(parentPath)), parentPath, pathToId(parentPath),
                                                      this);
                        if (!(current.exists() || current.getIoFile().mkdirs())) {
                            throw new ServerException(String.format("Unable create directory '%s' ", parentPath));
                        }
                    }
                    final Path newPath = current.getInternalPath().newPath(name);
                    if (zipEntry.isDirectory()) {
                        final java.io.File dir = new java.io.File(current.getIoFile(), name);
                        if (!(dir.exists() || dir.mkdir())) {
                            throw new ServerException(String.format("Unable create directory '%s' ", newPath));
                        }
                    } else {
                        final VirtualFileImpl file =
                                new VirtualFileImpl(new java.io.File(current.getIoFile(), name), newPath, pathToId(newPath), this);
                        if (file.exists()) {
                            if (isLocked(file)) {
                                throw new ForbiddenException(String.format("File '%s' already exists and locked. ", file.getPath()));
                            }
                            if (!hasPermission(file, BasicPermissions.WRITE.value(), true)) {
                                throw new ForbiddenException(
                                        String.format("Unable update file '%s'. Operation not permitted. ", file.getPath()));
                            }
                        }

                        try {
                            if (!file.getIoFile().createNewFile()) { // atomic
                                if (!overwrite) {
                                    throw new ConflictException(String.format("File '%s' already exists. ", file.getPath()));
                                }
                            }
                        } catch (IOException e) {
                            String msg = String.format("Unable create new file '%s'. ", newPath);
                            LOG.error(msg + e.getMessage(), e); // More details in log but do not show internal error to caller.
                            throw new ServerException(msg);
                        }

                        doUpdateContent(file, noCloseZip);
                    }
                    zip.closeEntry();
                }
                if (searcherProvider != null) {
                    try {
                        searcherProvider.getSearcher(this, true).add(parent);
                    } catch (ServerException e) {
                        LOG.error(e.getMessage(), e);
                    }
                }
            } catch (IOException e) {
                throw new ServerException(e.getMessage(), e);
            } finally {
                closeQuietly(zip);
            }
        } finally {
            lock.release();
        }
    }

   /* ============ LOCKING ============ */

    String lock(VirtualFileImpl virtualFile, long timeout) throws ForbiddenException, ConflictException, ServerException {
        if (!virtualFile.isFile()) {
            throw new ForbiddenException(String.format("Unable lock '%s'. Locking allowed for files only. ", virtualFile.getPath()));
        }

        final PathLockFactory.PathLock lock = pathLockFactory.getLock(virtualFile.getInternalPath(), true).acquire(LOCK_FILE_TIMEOUT);
        try {
            if (!hasPermission(virtualFile, BasicPermissions.WRITE.value(), true)) {
                throw new ForbiddenException(String.format("Unable lock '%s'. Operation not permitted. ", virtualFile.getPath()));
            }
            return doLock(virtualFile, timeout);
        } finally {
            lock.release();
        }
    }


    // UNDER LOCK
    private String doLock(VirtualFileImpl virtualFile, long timeout) throws ConflictException, ServerException {
        final int index = virtualFile.getInternalPath().hashCode() & MASK;
        if (NO_LOCK == lockTokensCache[index].get(virtualFile.getInternalPath())) // causes read from file if need.
        {
            final String lockToken = NameGenerator.generate(null, 16);
            final long expired = timeout > 0 ? (System.currentTimeMillis() + timeout) : Long.MAX_VALUE;
            final FileLock fileLock = new FileLock(lockToken, expired);
            DataOutputStream dos = null;
            try {
                java.io.File lockLockFile = getLockFile(virtualFile.getInternalPath());
                lockLockFile.getParentFile().mkdirs(); // Ignore result of 'mkdirs' here. If we are failed to create
                // directory we will get FileNotFoundException at the next line when try to create FileOutputStream.
                dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(lockLockFile)));
                locksSerializer.write(dos, fileLock);
            } catch (IOException e) {
                String msg = String.format("Unable lock file '%s'. ", virtualFile.getPath());
                LOG.error(msg + e.getMessage(), e); // More details in log but do not show internal error to caller.
                throw new ServerException(msg);
            } finally {
                closeQuietly(dos);
            }

            // Save lock token in cache if lock successful.
            lockTokensCache[index].put(virtualFile.getInternalPath(), fileLock);
            return lockToken;
        }

        throw new ConflictException(String.format("Unable lock file '%s'. File already locked. ", virtualFile.getPath()));
    }


    void unlock(VirtualFileImpl virtualFile, String lockToken) throws ForbiddenException, ConflictException, ServerException {
        if (lockToken == null) {
            throw new ForbiddenException("Null lock token. ");
        }
        if (!virtualFile.isFile()) {
            // Locks available for files only.
            throw new ConflictException(String.format("Item '%s' is not locked. ", virtualFile.getPath()));
        }
        final PathLockFactory.PathLock lock = pathLockFactory.getLock(virtualFile.getInternalPath(), true).acquire(LOCK_FILE_TIMEOUT);
        try {
            final FileLock fileLock = checkIsLockValidAndGet(virtualFile);
            if (NO_LOCK == fileLock) {
                throw new ConflictException(String.format("File '%s' is not locked. ", virtualFile.getPath()));
            }
            doUnlock(virtualFile, fileLock, lockToken);
        } finally {
            lock.release();
        }
    }

    // UNDER LOCK
    private void doUnlock(VirtualFileImpl virtualFile, FileLock lock, String lockToken) throws ForbiddenException, ServerException {
        final int index = virtualFile.getInternalPath().hashCode() & MASK;
        try {
            if (!lock.getLockToken().equals(lockToken)) {
                throw new ForbiddenException(String.format("Unable unlock file '%s'. Lock token does not match. ", virtualFile.getPath()));
            }
            final java.io.File lockIoFile = getLockFile(virtualFile.getInternalPath());
            if (!lockIoFile.delete()) {
                throw new IOException(String.format("Unable delete lock file %s. ", lockIoFile));
            }
            // Mark as unlocked in cache.
            lockTokensCache[index].put(virtualFile.getInternalPath(), NO_LOCK);
        } catch (IOException e) {
            String msg = String.format("Unable unlock file '%s'. ", virtualFile.getPath());
            LOG.error(msg + e.getMessage(), e); // More details in log but do not show internal error to caller.
            throw new ServerException(msg);
        }
    }


    boolean isLocked(VirtualFileImpl virtualFile) {
        if (!virtualFile.isFile()) {
            return false;
        }
        final PathLockFactory.PathLock pathLock = pathLockFactory.getLock(virtualFile.getInternalPath(), false).acquire(LOCK_FILE_TIMEOUT);
        try {
            return NO_LOCK != checkIsLockValidAndGet(virtualFile);
        } finally {
            pathLock.release();
        }
    }

    // UNDER LOCK
    private FileLock checkIsLockValidAndGet(VirtualFileImpl virtualFile) {
        final int index = virtualFile.getInternalPath().hashCode() & MASK;
        // causes read from file if need
        final FileLock lock = lockTokensCache[index].get(virtualFile.getInternalPath());
        if (NO_LOCK == lock) {
            return NO_LOCK;
        }
        if (lock.getExpired() < System.currentTimeMillis()) {
            final java.io.File lockIoFile = getLockFile(virtualFile.getInternalPath());
            if (!lockIoFile.delete()) {
                if (lockIoFile.exists()) {
                    // just warn here
                    LOG.warn("Unable delete lock file %s. ", lockIoFile);
                }
            }
            lockTokensCache[index].put(virtualFile.getInternalPath(), NO_LOCK);
            return NO_LOCK;
        }
        return lock;
    }


    // UNDER LOCK
    private boolean validateLockTokenIfLocked(VirtualFileImpl virtualFile, String checkLockToken) {
        final FileLock lock = checkIsLockValidAndGet(virtualFile);
        return NO_LOCK == lock || lock.getLockToken().equals(checkLockToken);
    }


    private java.io.File getLockFile(Path path) {
        java.io.File locksDir = path.isRoot()
                                ? new java.io.File(ioRoot, LOCKS_DIR)
                                : new java.io.File(ioRoot, toIoPath(path.getParent().newPath(LOCKS_DIR)));
        //boolean result = locksDir.mkdirs();
        //assert result || locksDir.exists();
        return new java.io.File(locksDir, path.getName() + LOCK_FILE_SUFFIX);
    }

   /* ============ ACCESS CONTROL  ============ */

    AccessControlList getACL(VirtualFileImpl virtualFile) {
        // Do not check permission here. We already check 'read' permission when get VirtualFile.
        final PathLockFactory.PathLock lock = pathLockFactory.getLock(virtualFile.getInternalPath(), false).acquire(LOCK_FILE_TIMEOUT);
        try {
            return new AccessControlList(aclCache[virtualFile.getInternalPath().hashCode() & MASK].get(virtualFile.getInternalPath()));
        } finally {
            lock.release();
        }
    }


    void updateACL(VirtualFileImpl virtualFile, List<AccessControlEntry> acl, boolean override, String lockToken)
            throws ForbiddenException, ServerException {
        final PathLockFactory.PathLock lock = pathLockFactory.getLock(virtualFile.getInternalPath(), true).acquire(LOCK_FILE_TIMEOUT);
        try {
            final int index = virtualFile.getInternalPath().hashCode() & MASK;
            final AccessControlList actualACL = aclCache[index].get(virtualFile.getInternalPath());

            if (!hasPermission(virtualFile, BasicPermissions.UPDATE_ACL.value(), true)) {
                throw new ForbiddenException(String.format("Unable update ACL for '%s'. Operation not permitted. ", virtualFile.getPath()));
            }

            if (virtualFile.isFile() && !validateLockTokenIfLocked(virtualFile, lockToken)) {
                throw new ForbiddenException(String.format("Unable update ACL of item '%s'. Item is locked. ", virtualFile.getPath()));
            }

            // 1. make copy of ACL
            final AccessControlList copy = new AccessControlList(actualACL);
            // 2. update ACL copy
            copy.update(acl, override);
            // 3. save updated ACL (write in file)
            DataOutputStream dos = null;
            try {
                java.io.File aclFile = getAclFile(virtualFile.getInternalPath());
                if (copy.isEmpty()) {
                    if (!aclFile.delete()) {
                        if (aclFile.exists()) {
                            throw new IOException(String.format("Unable delete file '%s'. ", aclFile));
                        }
                    }
                } else {
                    aclFile.getParentFile().mkdirs(); // Ignore result of 'mkdirs' here. If we are failed to create directory
                    // we will get FileNotFoundException at the next line when try to create FileOutputStream.
                    dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(aclFile)));
                    aclSerializer.write(dos, copy);
                }
            } catch (IOException e) {
                String msg = String.format("Unable save ACL for '%s'. ", virtualFile.getPath());
                LOG.error(msg + e.getMessage(), e); // More details in log but do not show internal error to caller.
                throw new ServerException(msg);
            } finally {
                closeQuietly(dos);
            }

            // 4. update cache
            aclCache[index].put(virtualFile.getInternalPath(), copy);
            // 5. update last modification time
            if (!virtualFile.getIoFile().setLastModified(System.currentTimeMillis())) {
                LOG.warn("Unable to set timestamp to '{}'. ", virtualFile.getIoFile());
            }

            eventService.publish(new UpdateACLEvent(workspaceId, virtualFile.getPath(), virtualFile.isFolder()));
        } finally {
            lock.release();
        }
    }


    // under lock
    private boolean hasPermission(VirtualFileImpl virtualFile, String p, boolean checkParent) {
        final VirtualFileSystemUser user = userContext.getVirtualFileSystemUser();
        Path path = virtualFile.getInternalPath();
        while (path != null) {
            final AccessControlList accessControlList = aclCache[path.hashCode() & MASK].get(path);
            if (!accessControlList.isEmpty()) {
                final Principal userPrincipal = DtoFactory.getInstance().createDto(Principal.class)
                                                          .withName(user.getUserId()).withType(Principal.Type.USER);
                Set<String> userPermissions = accessControlList.getPermissions(userPrincipal);
                if (userPermissions != null) {
                    return userPermissions.contains(p) || userPermissions.contains(BasicPermissions.ALL.value());
                }
                Collection<String> groups = user.getGroups();
                if (!groups.isEmpty()) {
                    for (String group : groups) {
                        final Principal groupPrincipal = DtoFactory.getInstance().createDto(Principal.class)
                                                                   .withName(group)
                                                                   .withType(Principal.Type.GROUP);
                        userPermissions = accessControlList.getPermissions(groupPrincipal);
                        if (userPermissions != null) {
                            return userPermissions.contains(p) || userPermissions.contains(BasicPermissions.ALL.value());
                        }
                    }
                }
                final Principal anyPrincipal = DtoFactory.getInstance().createDto(Principal.class)
                                                         .withName(VirtualFileSystemInfo.ANY_PRINCIPAL)
                                                         .withType(Principal.Type.USER);
                userPermissions = accessControlList.getPermissions(anyPrincipal);
                return userPermissions != null && (userPermissions.contains(p) || userPermissions.contains(BasicPermissions.ALL.value()));
            }
            if (checkParent) {
                path = path.getParent();
            } else {
                break;
            }
        }
        return true;
    }


    private java.io.File getAclFile(Path path) {
        java.io.File aclDir = path.isRoot()
                              ? new java.io.File(ioRoot, ACL_DIR)
                              : new java.io.File(ioRoot, toIoPath(path.getParent().newPath(ACL_DIR)));
        //boolean result = aclDir.mkdirs();
        //assert result || aclDir.exists();
        return new java.io.File(aclDir, path.getName() + ACL_FILE_SUFFIX);
    }

   /* ============ METADATA  ============ */

    List<Property> getProperties(VirtualFileImpl virtualFile, PropertyFilter filter) {
        // Do not check permission here. We already check 'read' permission when get VirtualFile.
        final Map<String, String[]> metadata = getFileMetadata(virtualFile);
        final List<Property> result = new ArrayList<>(metadata.size());
        for (Map.Entry<String, String[]> e : metadata.entrySet()) {
            final String name = e.getKey();
            if (filter.accept(name)) {
                final Property property = DtoFactory.getInstance().createDto(Property.class).withName(name);
                if (e.getValue() != null) {
                    List<String> list = new ArrayList<>(e.getValue().length);
                    Collections.addAll(list, e.getValue());
                    property.setValue(list);
                }
                result.add(property);
            }
        }
        return result;
    }


    void updateProperties(VirtualFileImpl virtualFile, List<Property> properties, String lockToken)
            throws ForbiddenException, ServerException {
        final int index = virtualFile.getInternalPath().hashCode() & MASK;
        final PathLockFactory.PathLock lock = pathLockFactory.getLock(virtualFile.getInternalPath(), true).acquire(LOCK_FILE_TIMEOUT);
        try {
            if (!hasPermission(virtualFile, BasicPermissions.WRITE.value(), true)) {
                throw new ForbiddenException(
                        String.format("Unable update properties for '%s'. Operation not permitted. ", virtualFile.getPath()));
            }

            if (virtualFile.isFile() && !validateLockTokenIfLocked(virtualFile, lockToken)) {
                throw new ForbiddenException(
                        String.format("Unable update properties of item '%s'. Item is locked. ", virtualFile.getPath()));
            }

            // 1. make copy of properties
            final Map<String, String[]> metadata = copyMetadataMap(metadataCache[index].get(virtualFile.getInternalPath()));
            // 2. update
            for (Property property : properties) {
                final String name = property.getName();
                final List<String> value = property.getValue();
                if (value != null) {
                    metadata.put(name, value.toArray(new String[value.size()]));
                } else {
                    metadata.remove(name);
                }
            }

            // 3. save in file
            saveFileMetadata(virtualFile, metadata);
            // 4. update cache
            metadataCache[index].put(virtualFile.getInternalPath(), metadata);
            // 5. update last modification time
            if (!virtualFile.getIoFile().setLastModified(System.currentTimeMillis())) {
                LOG.warn("Unable to set timestamp to '{}'. ", virtualFile.getIoFile());
            }
            eventService.publish(new UpdatePropertiesEvent(workspaceId, virtualFile.getPath(), virtualFile.isFolder()));
        } finally {
            lock.release();
        }
    }


    private Map<String, String[]> getFileMetadata(VirtualFileImpl virtualFile) {
        final int index = virtualFile.getInternalPath().hashCode() & MASK;
        final PathLockFactory.PathLock lock = pathLockFactory.getLock(virtualFile.getInternalPath(), false).acquire(LOCK_FILE_TIMEOUT);
        try {
            return copyMetadataMap(metadataCache[index].get(virtualFile.getInternalPath()));
        } finally {
            lock.release();
        }
    }


    String getPropertyValue(VirtualFileImpl virtualFile, String name) {
        // Do not check permission here. We already check 'read' permission when get VirtualFile.
        final int index = virtualFile.getInternalPath().hashCode() & MASK;
        final PathLockFactory.PathLock lock = pathLockFactory.getLock(virtualFile.getInternalPath(), false).acquire(LOCK_FILE_TIMEOUT);
        try {
            final String[] value = metadataCache[index].get(virtualFile.getInternalPath()).get(name);
            return value == null || value.length == 0 ? null : value[0];
        } finally {
            lock.release();
        }
    }


    String[] getPropertyValues(VirtualFileImpl virtualFile, String name) {
        // Do not check permission here. We already check 'read' permission when get VirtualFile.
        final int index = virtualFile.getInternalPath().hashCode() & MASK;
        final PathLockFactory.PathLock lock = pathLockFactory.getLock(virtualFile.getInternalPath(), false).acquire(LOCK_FILE_TIMEOUT);
        try {
            final String[] value = metadataCache[index].get(virtualFile.getInternalPath()).get(name);
            final String[] copyValue = new String[value.length];
            System.arraycopy(value, 0, copyValue, 0, value.length);
            return copyValue;
        } finally {
            lock.release();
        }
    }


    void setProperty(VirtualFileImpl virtualFile, String name, String value) throws ServerException {
        setProperty(virtualFile, name, value == null ? null : new String[]{value});
    }


    void setProperty(VirtualFileImpl virtualFile, String name, String... value) throws ServerException {
        final int index = virtualFile.getInternalPath().hashCode() & MASK;
        final PathLockFactory.PathLock lock = pathLockFactory.getLock(virtualFile.getInternalPath(), true).acquire(LOCK_FILE_TIMEOUT);
        try {
            // 1. make copy of properties
            final Map<String, String[]> metadata = copyMetadataMap(metadataCache[index].get(virtualFile.getInternalPath()));
            // 2. update
            if (value != null) {
                String[] copyValue = new String[value.length];
                System.arraycopy(value, 0, copyValue, 0, value.length);
                metadata.put(name, copyValue);
            } else {
                metadata.remove(name);
            }
            // 3. save in file
            saveFileMetadata(virtualFile, metadata);
            // 4. update cache
            metadataCache[index].put(virtualFile.getInternalPath(), metadata);
        } finally {
            lock.release();
        }
    }


    private void saveFileMetadata(VirtualFileImpl virtualFile, Map<String, String[]> properties) throws ServerException {
        DataOutputStream dos = null;

        try {
            final java.io.File metadataFile = getMetadataFile(virtualFile.getInternalPath());
            if (properties.isEmpty()) {
                if (!metadataFile.delete()) {
                    if (metadataFile.exists()) {
                        throw new IOException(String.format("Unable delete file '%s'. ", metadataFile));
                    }
                }
            } else {
                metadataFile.getParentFile().mkdirs(); // Ignore result of 'mkdirs' here. If we are failed to create
                // directory we will get FileNotFoundException at the next line when try to create FileOutputStream.
                dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(metadataFile)));
                metadataSerializer.write(dos, properties);
            }
        } catch (IOException e) {
            String msg = String.format("Unable save properties for '%s'. ", virtualFile.getPath());
            LOG.error(msg + e.getMessage(), e); // More details in log but do not show internal error to caller.
            throw new ServerException(msg);
        } finally {
            closeQuietly(dos);
        }
    }


    private java.io.File getMetadataFile(Path path) {
        java.io.File metadataDir = path.isRoot()
                                   ? new java.io.File(ioRoot, PROPS_DIR)
                                   : new java.io.File(ioRoot, toIoPath(path.getParent().newPath(PROPS_DIR)));
        //boolean result = metadataDir.mkdirs();
        //assert result || metadataDir.exists();
        return new java.io.File(metadataDir, path.getName() + PROPERTIES_FILE_SUFFIX);
    }

   /* ============ VERSIONING ============ */
   /* versions is not supported in fact. Here implements simple contract for single version. */

    String getVersionId(VirtualFileImpl virtualFile) {
        return virtualFile.isFile() ? "0" : null;
    }

    LazyIterator<VirtualFile> getVersions(VirtualFileImpl virtualFile, VirtualFileFilter filter) throws ForbiddenException {
        if (!virtualFile.isFile()) {
            throw new ForbiddenException("Versioning allowed for files only. ");
        }
        if (filter.accept(virtualFile)) {
            return LazyIterator.<VirtualFile>singletonIterator(virtualFile);
        }
        return LazyIterator.emptyIterator();
    }

    VirtualFileImpl getVersion(VirtualFileImpl virtualFile, String versionId) throws ForbiddenException, NotFoundException {
        if (!virtualFile.isFile()) {
            throw new ForbiddenException("Versioning allowed for files only. ");
        }
        if ("0".equals(versionId)) {
            return virtualFile;
        }
        throw new NotFoundException("Version " + versionId + " for file " + virtualFile.getPath() + " doesn't exist. ");
    }

    LazyIterator<Pair<String, String>> countMd5Sums(VirtualFileImpl virtualFile) throws ServerException {
        if (!virtualFile.isFolder()) {
            return LazyIterator.emptyIterator();
        }
        final PathLockFactory.PathLock lock = pathLockFactory.getLock(virtualFile.getInternalPath(), true).acquire(LOCK_FILE_TIMEOUT);
        try {
            final List<Pair<String, String>> hashes = new ArrayList<>();
            final int trimPathLength = virtualFile.getPath().length() + 1;
            final HashFunction hashFunction = Hashing.md5();
            final ValueHolder<ServerException> errorHolder = new ValueHolder<>();
            virtualFile.accept(new VirtualFileVisitor() {
                @Override
                public void visit(final VirtualFile virtualFile) {
                    try {
                        if (virtualFile.isFile()) {
                            hashes.add(Pair.of(countHashSum(virtualFile, hashFunction), virtualFile.getPath().substring(trimPathLength)));
                        } else {
                            final LazyIterator<VirtualFile> children = virtualFile.getChildren(VirtualFileFilter.ALL);
                            while (children.hasNext()) {
                                children.next().accept(this);
                            }
                        }
                    } catch (ServerException e) {
                        errorHolder.set(e);
                    }
                }
            });
            return LazyIterator.fromList(hashes);
        } finally {
            lock.release();
        }
    }


    private String countHashSum(VirtualFile virtualFile, HashFunction hashFunction) throws ServerException {
        try {
            final InputStream stream = virtualFile.getContent().getStream();
            return ByteStreams.hash(new InputSupplier<InputStream>() {
                @Override
                public InputStream getInput() throws IOException {
                    return stream;
                }
            }, hashFunction).toString();
        } catch (ForbiddenException e) {
            throw new ServerException(e.getServiceError());
        } catch (IOException e) {
            throw new ServerException(e);
        }
    }

   /* ============ HELPERS  ============ */

    /* Relative system path */
    private String toIoPath(Path vfsPath) {
        if (vfsPath.isRoot()) {
            return "";
        }
        if ('/' == java.io.File.separatorChar) {
            // Unix like system. Use vfs path as relative i/o path.
            return vfsPath.toString();
        }
        return vfsPath.join(java.io.File.separatorChar);
    }

    private Map<String, String[]> copyMetadataMap(Map<String, String[]> source) {
        final Map<String, String[]> copyMap = new HashMap<>(source.size());
        for (Map.Entry<String, String[]> e : source.entrySet()) {
            String[] value = e.getValue();
            String[] copyValue = new String[value.length];
            System.arraycopy(value, 0, copyValue, 0, value.length);
            copyMap.put(e.getKey(), copyValue);
        }
        return copyMap;
    }


    private void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ignored) {
            }
        }
    }


    private void checkName(String name) throws ServerException {
        if (name == null || name.trim().isEmpty()) {
            throw new ServerException("Item's name is not set. ");
        }
    }
}
