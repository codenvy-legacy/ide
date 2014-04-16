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
import com.codenvy.api.vfs.server.VirtualFileFilter;
import com.codenvy.api.vfs.server.exceptions.VirtualFileSystemException;
import com.codenvy.api.vfs.server.search.LuceneSearcherProvider;
import com.codenvy.api.vfs.server.search.Searcher;
import com.codenvy.api.vfs.server.util.MediaTypeFilter;
import com.codenvy.api.vfs.server.util.VirtualFileFilters;
import com.codenvy.commons.lang.NamedThreadFactory;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Implementation of LuceneSearcherProvider which run LuceneSearcher initialization update tasks in ExecutorService.
 * <p/>
 * NOTE: This implementation always create new index in new directory. Index is not reused after call {@link
 * com.codenvy.vfs.impl.fs.CleanableSearcher#close()}. Index directory is cleaned after close Searcher.
 *
 * @author andrew00x
 */
@Singleton
public class CleanableSearcherProvider extends LuceneSearcherProvider {
    private final ConcurrentMap<java.io.File, CleanableSearcher> instances;
    private final ExecutorService                                executor;
    private final java.io.File                                   indexRootDir;
    private final Set<VirtualFileFilter>                         filters;

    @Inject
    CleanableSearcherProvider(@Named("vfs.local.fs_index_root_dir") java.io.File indexRootDir,
                              @Named("vfs.index_filter") Set<VirtualFileFilter> filters) {
        this.indexRootDir = indexRootDir;
        this.filters = filters;
        executor = Executors.newFixedThreadPool(1 + Runtime.getRuntime().availableProcessors(),
                                                new NamedThreadFactory("LocalVirtualFileSystem-CleanableSearcher-", true));
        instances = new ConcurrentHashMap<>();
    }

    @Override
    public Searcher getSearcher(MountPoint mountPoint, boolean create) throws VirtualFileSystemException {
        final java.io.File vfsIoRoot = ((VirtualFileImpl)mountPoint.getRoot()).getIoFile();
        CleanableSearcher searcher = instances.get(vfsIoRoot);
        if (searcher == null && create) {
            final java.io.File myIndexDir;
            CleanableSearcher newSearcher;
            try {
                Files.createDirectories(indexRootDir.toPath());
                myIndexDir = Files.createTempDirectory(indexRootDir.toPath(), null).toFile();
                final VirtualFileFilter filter;
                if (!filters.isEmpty()) {
                    final VirtualFileFilter[] myFilters = new VirtualFileFilter[filters.size() + 1];
                    final Iterator<VirtualFileFilter> iterator = filters.iterator();
                    for (int i = 1; i < myFilters.length; i++) {
                        myFilters[i] = iterator.next();
                    }
                    myFilters[0] = new MediaTypeFilter(getIndexedMediaTypes());
                    filter = VirtualFileFilters.createAndFilter(myFilters);
                } else {
                    filter = new MediaTypeFilter(getIndexedMediaTypes());
                }
                newSearcher = new CleanableSearcher(this, myIndexDir, filter);
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

    @PreDestroy
    private void stop() {
        executor.shutdownNow();
        for (CleanableSearcher searcher : instances.values()) {
            searcher.close();
        }
    }
}

