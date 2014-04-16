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
import com.codenvy.api.vfs.server.VirtualFile;
import com.codenvy.api.vfs.server.VirtualFileFilter;
import com.codenvy.api.vfs.server.exceptions.VirtualFileSystemException;
import com.codenvy.api.vfs.server.search.QueryExpression;

import org.apache.lucene.index.Term;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import static com.codenvy.commons.lang.IoUtil.deleteRecursive;

/**
 * Filesystem based LuceneSearcher which cleans index directory after call method {@link #close()}.
 *
 * @author andrew00x
 */
public class CleanableSearcher extends FSIndexSearcher {
    private static final Logger LOG = LoggerFactory.getLogger(CleanableSearcher.class);
    private final    CleanableSearcherProvider searcherService;
    private final    CountDownLatch            postponeUpdateLatch;
    private final    Queue<IndexUpdateTask>    postponeUpdates;
    private volatile boolean                   initDone;
    private volatile Throwable                 initError;

    CleanableSearcher(CleanableSearcherProvider searcherService, java.io.File indexDir, VirtualFileFilter filter)
            throws IOException, VirtualFileSystemException {
        super(indexDir, filter);
        this.searcherService = searcherService;
        postponeUpdates = new ConcurrentLinkedQueue<>();
        postponeUpdateLatch = new CountDownLatch(1);
    }

    public void init(ExecutorService executor, MountPoint mountPoint) {
        executor.execute(new IndexInitTask(mountPoint));
    }

    public boolean isInitDone() {
        return initDone && postponeUpdateLatch.getCount() == 0;
    }

    public Throwable getInitError() {
        return initError;
    }

    @Override
    public void close() {
        searcherService.close(this);
    }

    void doClose() {
        super.close();
        deleteRecursive(getIndexDir());
    }

    @Override
    protected void doAdd(VirtualFile virtualFile) throws VirtualFileSystemException {
        if (initDone) {
            try {
                postponeUpdateLatch.await();
                super.doAdd(virtualFile);
            } catch (InterruptedException ignored) {
            }
        } else {
            postponeUpdate(new IndexUpdateTask(virtualFile, null));
        }
    }

    @Override
    protected void doDelete(Term deleteTerm) throws VirtualFileSystemException {
        if (initDone) {
            try {
                postponeUpdateLatch.await();
                super.doDelete(deleteTerm);
            } catch (InterruptedException ignored) {
            }
        } else {
            postponeUpdate(new IndexUpdateTask(null, deleteTerm));
        }
    }

    @Override
    protected void doUpdate(Term deleteTerm, VirtualFile virtualFile) throws VirtualFileSystemException {
        if (initDone) {
            try {
                postponeUpdateLatch.await();
                super.doUpdate(deleteTerm, virtualFile);
            } catch (InterruptedException ignored) {
            }
        } else {
            postponeUpdate(new IndexUpdateTask(virtualFile, deleteTerm));
        }
    }

    private void postponeUpdate(IndexUpdateTask update) {
        postponeUpdates.add(update);
    }

    private class IndexInitTask implements Runnable {
        final MountPoint mountPoint;

        IndexInitTask(MountPoint mountPoint) {
            this.mountPoint = mountPoint;
        }

        @Override
        public void run() {
            try {
                CleanableSearcher.super.init(mountPoint);
                initDone = true;
                for (IndexUpdateTask update : postponeUpdates) {
                    if (Thread.currentThread().isInterrupted()) {
                        break;
                    }
                    update.run();
                }
                postponeUpdates.clear();
            } catch (VirtualFileSystemException e) {
                LOG.error(e.getMessage(), e);
                initError = e;
                close();
            } catch (RuntimeException | Error e) {
                LOG.error(e.getMessage(), e);
                initError = e;
                close();
                throw e;
            } finally {
                postponeUpdateLatch.countDown();
            }
        }
    }

    private class IndexUpdateTask implements Runnable {
        final VirtualFile virtualFile;
        final Term        deleteTerm;

        IndexUpdateTask(VirtualFile virtualFile, Term deleteTerm) {
            this.virtualFile = virtualFile;
            this.deleteTerm = deleteTerm;
        }

        @Override
        public void run() {
            try {
                if (deleteTerm != null) {
                    if (virtualFile != null) {
                        CleanableSearcher.super.doUpdate(deleteTerm, virtualFile);
                    } else {
                        CleanableSearcher.super.doDelete(deleteTerm);
                    }
                } else {
                    CleanableSearcher.super.doAdd(virtualFile);
                }
            } catch (VirtualFileSystemException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }
}
