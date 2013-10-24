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

import com.codenvy.api.vfs.server.MountPoint;
import com.codenvy.api.vfs.server.exceptions.VirtualFileSystemException;
import com.codenvy.api.vfs.server.search.LuceneSearcherProvider;
import com.codenvy.api.vfs.server.search.Searcher;
import com.codenvy.commons.env.EnvironmentContext;
import com.codenvy.commons.lang.NamedThreadFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Implementation of LuceneSearcherProvider which run LuceneSearcher initialization update tasks in ExecutorService.
 * <p/>
 * NOTE: This implementation always create new index in new directory. Index is not reused after call {@link
 * CleanableSearcher#close()}. Index directory is cleaned after close Searcher.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 */
public class CleanableSearcherProvider extends LuceneSearcherProvider {
    private final ConcurrentMap<java.io.File, CleanableSearcher> instances;
    private final ExecutorService                                executor;

    public CleanableSearcherProvider() {
        executor = Executors.newFixedThreadPool(1 + Runtime.getRuntime().availableProcessors(),
                                                new NamedThreadFactory("LocalVirtualFileSystem-CleanableSearcher-", true));
        instances = new ConcurrentHashMap<>();
    }

    @Override
    public Searcher getSearcher(MountPoint mountPoint, boolean create) throws VirtualFileSystemException {
        final java.io.File vfsIoRoot = ((VirtualFileImpl)mountPoint.getRoot()).getIoFile();
        CleanableSearcher searcher = instances.get(vfsIoRoot);
        if (searcher == null && create) {
            final EnvironmentContext context = EnvironmentContext.getCurrent();
            final String workspaceId = (String)context.getVariable(EnvironmentContext.WORKSPACE_ID);
            if (workspaceId == null || workspaceId.isEmpty()) {
                throw new VirtualFileSystemException("Unable create searcher. Workspace id is not set.");
            }

            final java.io.File indexRootDir = (java.io.File)context.getVariable(EnvironmentContext.VFS_INDEX_DIR);
            if (indexRootDir == null) {
                throw new VirtualFileSystemException(
                        String.format("Unable create searcher for virtual file system '%s'. Index directory is not set. ", workspaceId));
            }

            final java.io.File myIndexDir;
            CleanableSearcher newSearcher;
            try {
                Files.createDirectories(indexRootDir.toPath());
                myIndexDir = Files.createTempDirectory(indexRootDir.toPath(), workspaceId).toFile();
                newSearcher = new CleanableSearcher(this, myIndexDir, getIndexedMediaTypes());
            } catch (IOException e) {
                throw new VirtualFileSystemException("Unable create searcher. " + e.getMessage(), e);
            }
            searcher = instances.putIfAbsent(vfsIoRoot, newSearcher);
            if (searcher == null) {
                searcher = newSearcher;
                searcher.init(executor, mountPoint);
            }
        }
        return searcher;
    }

    void close(CleanableSearcher searcher) {
        instances.values().remove(searcher);
        searcher.doClose();
    }
}

