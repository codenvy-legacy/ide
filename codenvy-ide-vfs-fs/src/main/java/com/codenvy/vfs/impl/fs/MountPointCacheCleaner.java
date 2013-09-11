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

import com.codenvy.commons.lang.NamedThreadFactory;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Helps to reset data cached in {@code MountPoint}.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 */
class MountPointCacheCleaner {
    private static final Log LOG = ExoLogger.getLogger(MountPointCacheCleaner.class);

    private static final String CACHE_RESET_PATH =
            FSMountPoint.SERVICE_DIR + java.io.File.separatorChar + "cache" + java.io.File.separatorChar + "reset";

    private static ScheduledExecutorService exec =
            Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("MountPointCacheCleaner", true));

    private static Map<java.io.File, Entry> watched = new ConcurrentHashMap<>();

    static {
        exec.scheduleAtFixedRate(
                new Runnable() {
                    @Override
                    public void run() {
                        for (Entry entry : watched.values()) {
                            if (Files.exists(entry.resetFilePath)) {
                                entry.mountPoint.reset();
                                LOG.info("reset cache for VFS mounted at {}", entry.mountPoint.getRoot().getIoFile());
                                try {
                                    Files.delete(entry.resetFilePath);
                                } catch (IOException e) {
                                    LOG.error(e.getMessage(), e);
                                }
                            }
                        }
                    }
                },
                30,
                30,
                TimeUnit.SECONDS);
    }

    static void add(FSMountPoint mountPoint) {
        final java.io.File ioPath = mountPoint.getRoot().getIoFile();
        final java.nio.file.Path resetFilePath = new java.io.File(ioPath, CACHE_RESET_PATH).toPath();
        watched.put(ioPath, new Entry(mountPoint, resetFilePath));
    }

    static void remove(FSMountPoint mountPoint) {
        watched.remove(mountPoint.getRoot().getIoFile());
    }

    private static class Entry {
        final FSMountPoint         mountPoint;
        final java.nio.file.Path resetFilePath;

        Entry(FSMountPoint mountPoint, java.nio.file.Path resetFilePath) {
            this.mountPoint = mountPoint;
            this.resetFilePath = resetFilePath;
        }
    }
}
