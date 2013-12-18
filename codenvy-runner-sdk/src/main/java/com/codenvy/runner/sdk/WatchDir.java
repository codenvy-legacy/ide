/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2013] Codenvy, S.A.
 *  All Rights Reserved.
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
package com.codenvy.runner.sdk;

import com.codenvy.commons.lang.IoUtil;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

/**
 * Watch a directory for changes and copies the content of another
 * directory to every directory that matches a path pattern.
 * <p/>
 * Used in {@link SDKRunner} to copy <code>javaParserWorker</code> directory
 * (that contains compiled GWT permutation) to every code server's
 * working directory (that contains recompiled extension).
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 */
public class WatchDir implements Runnable {
    private final WatchService        watcher;
    private final Map<WatchKey, Path> keys;
    private final Path                baseWatchableDirPath;
    private final Path                dirPathToCopy;
    private final String              globPattern;

    /**
     * Creates a {@link WatchService} and registers the given <code>baseWatchableDirPath</code> directory.
     *
     * @param baseWatchableDirPath
     *         path to the directory to watch
     * @param dirPathToCopy
     *         path to the directory that contains resources to copy
     * @param globPattern
     *         relative path pattern (in glob syntax) to the folders
     *         where <code>dirPathToCopy</code> content will be copied
     * @throws IOException
     *         error occurred while register a directory in {@link WatchService}
     */
    WatchDir(Path baseWatchableDirPath, Path dirPathToCopy, String globPattern) throws IOException {
        this.baseWatchableDirPath = baseWatchableDirPath;
        this.dirPathToCopy = dirPathToCopy;
        this.globPattern = globPattern;
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<>();
        register(baseWatchableDirPath);
    }

    @SuppressWarnings("unchecked")
    private static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>)event;
    }

    /** Register the given directory with the {@link WatchService}. */
    private void register(Path dir) throws IOException {
        WatchKey key = dir.register(watcher, ENTRY_CREATE);
        keys.put(key, dir);
    }

    /**
     * Register the given directory, and all its sub-directories which
     * path matches the special pattern, with the {@link WatchService}.
     */
    private void registerAll(final Path path) throws IOException {
        // register directory and sub-directories
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                String p = "{com.codenvy.ide.IDEPlatform," +
                           "com.codenvy.ide.IDEPlatform/compile-*," +
                           "com.codenvy.ide.IDEPlatform/compile-*/war," +
                           "com.codenvy.ide.IDEPlatform/compile-*/war/_app}";
                PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + p);
                Path relPath = baseWatchableDirPath.relativize(dir);
                if (matcher.matches(relPath)) {
                    register(dir);
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

    @Override
    public void run() {
        processEvents();
    }

    /** Process all events for keys queued to the watcher. */
    private void processEvents() {
        for (; ; ) {

            // wait for key to be signalled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }

            Path dir = keys.get(key);
            if (dir == null) {
                System.err.println("WatchKey not recognized!!");
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();

                // TBD - provide example of how OVERFLOW event is handled
                if (kind == OVERFLOW) {
                    continue;
                }

                // Context for directory entry event is the file name of entry
                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path child = dir.resolve(name);
                try {
                    registerAll(child);
                } catch (IOException ignored) {
                }

                PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + globPattern);
                Path relPath = baseWatchableDirPath.relativize(child);
                if (matcher.matches(relPath)) {
                    try {
                        IoUtil.copy(dirPathToCopy.toFile(), child.resolve(dirPathToCopy.getFileName()).toFile(), null);
                    } catch (IOException ignore) {
                    }
                }
            }

            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);

                // all directories are inaccessible
                if (keys.isEmpty()) {
                    break;
                }
            }
        }
    }
}