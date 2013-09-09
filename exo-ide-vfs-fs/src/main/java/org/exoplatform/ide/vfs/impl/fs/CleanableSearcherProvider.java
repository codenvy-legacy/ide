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

import com.codenvy.commons.env.EnvironmentContext;
import com.codenvy.commons.lang.NamedThreadFactory;

import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.codenvy.commons.lang.IoUtil.deleteRecursive;

/**
 * Implementation of SearcherProvider which run Searcher initialization update tasks in ExecutorService.
 * <p/>
 * NOTE: This implementation always create new index in new directory. Index is not reused after call {@link
 * org.exoplatform.ide.vfs.impl.fs.CleanableSearcher#close()}. Index directory is cleaned after close Searcher.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class CleanableSearcherProvider implements SearcherProvider {
    private final ConcurrentMap<java.io.File, CleanableSearcher> instances;
    private final ExecutorService                        executor;

    public CleanableSearcherProvider() {
        executor = Executors.newFixedThreadPool(1 + Runtime.getRuntime().availableProcessors(),
                                                new NamedThreadFactory("LocalVirtualFileSystem-CleanableSearcher-", true));
        instances = new ConcurrentHashMap<java.io.File, CleanableSearcher>();
    }

    @Override
    public Searcher getSearcher(MountPoint mountPoint, boolean create) throws VirtualFileSystemException {
        final java.io.File vfsIoRoot = mountPoint.getRoot().getIoFile();
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
        deleteRecursive(searcher.getIndexDir());
    }

    private Set<String> getIndexedMediaTypes() throws VirtualFileSystemException {
        Set<String> forIndex = null;
        final URL url = Thread.currentThread().getContextClassLoader().getResource("META-INF/indices_types.txt");
        if (url != null) {
            InputStream in = null;
            BufferedReader reader = null;
            try {
                in = url.openStream();
                reader = new BufferedReader(new InputStreamReader(in));
                forIndex = new LinkedHashSet<String>();
                String line;
                while ((line = reader.readLine()) != null) {
                    int c = line.indexOf('#');
                    if (c >= 0) {
                        line = line.substring(0, c);
                    }
                    line = line.trim();
                    if (line.length() > 0) {
                        forIndex.add(line);
                    }
                }
            } catch (IOException e) {
                throw new VirtualFileSystemException(
                        String.format("Failed to get list of media types for indexing. %s", e.getMessage()));
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException ignored) {
                    }
                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException ignored) {
                    }
                }
            }
        }
        if (forIndex == null || forIndex.isEmpty()) {
            throw new VirtualFileSystemException("Failed to get list of media types for indexing. " +
                                                 "File 'META-INF/indices_types.txt not found or empty. ");
        }
        return forIndex;
    }
}

