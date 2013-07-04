/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.ide.vfs.impl.fs;

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
            MountPoint.SERVICE_DIR + java.io.File.separatorChar + "cache" + java.io.File.separatorChar + "reset";

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

    static void add(MountPoint mountPoint) {
        final java.io.File ioPath = mountPoint.getRoot().getIoFile();
        final java.nio.file.Path resetFilePath = new java.io.File(ioPath, CACHE_RESET_PATH).toPath();
        watched.put(ioPath, new Entry(mountPoint, resetFilePath));
    }

    static void remove(MountPoint mountPoint) {
        watched.remove(mountPoint.getRoot().getIoFile());
    }

    private static class Entry {
        final MountPoint         mountPoint;
        final java.nio.file.Path resetFilePath;

        Entry(MountPoint mountPoint, java.nio.file.Path resetFilePath) {
            this.mountPoint = mountPoint;
            this.resetFilePath = resetFilePath;
        }
    }
}
