/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.codeassistant.storage;

import org.exoplatform.ide.codeassistant.jvm.bean.Dependency;
import org.exoplatform.ide.codeassistant.storage.api.InfoStorage;
import org.exoplatform.ide.codeassistant.storage.api.WriterTask;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class UpdateStorageService {

    public static final String                  INFO_STORAGE                    = "update.info.storage";

    /**
     * Name of configuration parameter that provides build timeout is seconds. After this time build may be terminated.
     * 
     * @see #DEFAULT_UPDATE_TIMEOUT
     */
    public static final String                  UPDATE_TIMEOUT                  = "update.timeout";

    /**
     * Name of configuration parameter that sets the number of update workers. In other words it set the number of build process that can be
     * run at the same time. If this parameter is not set then the number of available processors used, e.g.
     * <code>Runtime.getRuntime().availableProcessors();</code>
     */
    public static final String                  UPDATE_WORKERS_NUMBER           = "update.workers.number";

    /**
     * Name of parameter that set the max size of build queue. The number of build task in queue may not be greater than provided by this
     * parameter.
     * 
     * @see #DEFAULT_UPDATE_QUEUE_SIZE
     */
    public static final String                  UPDATE_QUEUE_SIZE               = "update.queue.size";

    /**
     * Name of configuration parameter that points to the directory where all jars stored. Is such parameter is not specified then
     * 'java.io.tmpdir' used.
     */
    public static final String                  UPDATE_FOLDER                   = "update.folder";

    /** Default build timeout in seconds (120). After this time build may be terminated. */
    public static final int                     DEFAULT_UPDATE_TIMEOUT          = 180;

    /** Default max size of build queue (200). */
    public static final int                     DEFAULT_UPDATE_QUEUE_SIZE       = 200;

    /** Default build timeout in minutes (10). After this time update task may be terminated. */
    public static final int                     DEFAULT_CLEAN_RESULT_DELAY_TIME = 10;

    private final ExecutorService               pool;

    private final File                          tempFolder;

    private final int                           timeoutMillis;

    private final InfoStorage                   infoStorage;

    private Thread                              writerThread;

    private BlockingQueue<WriterTask>           writerQueue;

    private final ScheduledExecutorService      cleaner;

    private ConcurrentMap<String, CacheElement> concurrentMap                   = new ConcurrentHashMap<String, CacheElement>();

    /** task ID generator. */
    private static final AtomicLong             idGenerator                     = new AtomicLong(1);

    private static String nextTaskID() {
        return Long.toString(idGenerator.getAndIncrement());
    }

    private static <O> O getOption(Map<String, Object> config, String option, Class<O> type, O defaultValue) {
        if (config != null) {
            Object value = config.get(option);
            return value != null ? type.cast(value) : defaultValue;
        }
        return defaultValue;
    }

    /**
     *
     */
    public UpdateStorageService(Map<String, Object> options) {
        this(//
             getOption(options, INFO_STORAGE, InfoStorage.class, null),//
             getOption(options, UPDATE_FOLDER, String.class, System.getProperty("java.io.tmpdir")), //
             getOption(options, UPDATE_TIMEOUT, Integer.class, DEFAULT_UPDATE_TIMEOUT),//
             getOption(options, UPDATE_WORKERS_NUMBER, Integer.class, Runtime.getRuntime().availableProcessors()),//
             getOption(options, UPDATE_QUEUE_SIZE, Integer.class, DEFAULT_UPDATE_QUEUE_SIZE)//
        );
    }

    /**
     *
     */
    public UpdateStorageService(InfoStorage infoStorage, String tempFolder, int timeout, int workerNumber,
                                int updateQueueSize) {
        this.infoStorage = infoStorage;
        this.tempFolder = new File(tempFolder);
        this.timeoutMillis = timeout * 1000; // to milliseconds
        //
        this.pool =
                    new ThreadPoolExecutor(workerNumber, workerNumber, 0L, TimeUnit.MILLISECONDS,
                                           new LinkedBlockingQueue<Runnable>(updateQueueSize), new ThreadPoolExecutor.AbortPolicy());

        this.cleaner = Executors.newSingleThreadScheduledExecutor();
        cleaner.scheduleAtFixedRate(new CleanTask(), DEFAULT_CLEAN_RESULT_DELAY_TIME, DEFAULT_CLEAN_RESULT_DELAY_TIME,
                                    TimeUnit.MINUTES);

        writerQueue = new LinkedBlockingQueue<WriterTask>();
        StorageWriter storageWriter = new StorageWriter(writerQueue, infoStorage);
        writerThread = new Thread(storageWriter, "StorageWriter");
        writerThread.setDaemon(true);
        writerThread.start();
    }

    public UpdateStorageTask updateTypeIndex(List<Dependency> dependencies, InputStream in) throws IOException {
        return addTask(new TypeUpdateInvoker(infoStorage, writerQueue, dependencies, createDependencys(in)));
    }

    public UpdateStorageTask updateDockIndex(List<Dependency> dependencies, InputStream in) throws IOException {
        return addTask(new DockUpdateInvoker(infoStorage, writerQueue, dependencies, createDependencys(in)));
    }

    public void shutdown() {
        pool.shutdown();
        cleaner.shutdown();
        try {
            if (!pool.awaitTermination(30, TimeUnit.SECONDS)) {
                pool.shutdownNow();
            }
            if (!cleaner.awaitTermination(30, TimeUnit.SECONDS)) {
                cleaner.shutdownNow();
            }
            // Task with null artifact will shutdown writer thread
            writerQueue.add(new WriterTask(null, null, null, null));
        } catch (InterruptedException e) {
            if (!pool.isShutdown())
              pool.shutdownNow();
            if (!cleaner.isShutdown())
                cleaner.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * @param createDependencys
     * @param dependencies
     */
    private UpdateStorageTask addTask(final UpdateInvoker invoker) {
        Future<UpdateStorageResult> future = pool.submit(new Callable<UpdateStorageResult>() {
            @Override
            public UpdateStorageResult call() throws Exception {
                TimeOutThread t = new TimeOutThread(timeoutMillis, Thread.currentThread());
                t.start();
                UpdateStorageResult result = invoker.execute();
                t.kill();
                return result;
            }
        });
        final String nextTaskID = nextTaskID();

        UpdateStorageTask task = new UpdateStorageTask(nextTaskID, future);
        CacheElement newElement =
                                  new CacheElement(nextTaskID, task, System.currentTimeMillis() + DEFAULT_CLEAN_RESULT_DELAY_TIME);
        concurrentMap.put(nextTaskID, newElement);
        return task;
    }

    /**
     * @param createDependencys
     * @param dependencies
     */
    public UpdateStorageTask getTask(String id) {
        CacheElement e = concurrentMap.get(id);
        return e != null ? e.task : null;
    }

    /** Timeout Thread. Kill the main task if necessary. */
    public static class TimeOutThread extends Thread {
        final long   timeout;

        final Thread controlledObj;

        TimeOutThread(long timeout, Thread controlledObj) {
            setDaemon(true);
            this.timeout = timeout;
            this.controlledObj = controlledObj;
        }

        boolean isRunning = true;

        /** If we done need the {@link TimeOutThread} thread, we may kill it. */
        public void kill() {
            isRunning = false;
            synchronized (this) {
                notify();
            }
        }

        /**
         *
         */
        @Override
        public void run() {
            long deltaT = 0l;
            try {
                long start = System.currentTimeMillis();
                while (isRunning && deltaT < timeout) {
                    synchronized (this) {
                        wait(Math.max(100, timeout - deltaT));
                    }
                    deltaT = System.currentTimeMillis() - start;
                }
            } catch (InterruptedException e) {
            } finally {
                isRunning = false;
            }
            controlledObj.interrupt();
        }
        
    }

    /**
     * @param in
     * @return
     * @throws IOException
     */
    private File createDependencys(InputStream in) throws IOException {
        File depFolder = UpdateUtil.makeProjectDirectory(tempFolder);
        UpdateUtil.unzip(in, depFolder);
        return depFolder;
    }

    /* ====================================================== */

    private class CleanTask implements Runnable {
        public void run() {
            Set<String> keySet = concurrentMap.keySet();
            for (String key : keySet) {
                CacheElement element = concurrentMap.get(key);
                if (element.isExpired()) {
                    element.task.cancel();
                    concurrentMap.remove(key);
                }
            }

        }
    }

    private static final class CacheElement {
        private final long      expirationTime;

        private final int       hash;

        final String            id;

        final UpdateStorageTask task;

        CacheElement(String id, UpdateStorageTask task, long expirationTime) {
            this.id = id;
            this.task = task;
            this.expirationTime = expirationTime;
            this.hash = 7 * 31 + id.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            CacheElement e = (CacheElement)o;
            return id.equals(e.id);
        }

        @Override
        public int hashCode() {
            return hash;
        }

        boolean isExpired() {
            return expirationTime < System.currentTimeMillis();
        }
    }

}
