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

import org.apache.lucene.index.Term;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.io.IOException;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class CleanableSearcher extends Searcher {
    private static final Log LOG = ExoLogger.getLogger(CleanableSearcher.class);
    private final    CleanableSearcherProvider searcherService;
    private final    CountDownLatch            postponeUpdateLatch;
    private final    Queue<IndexUpdateTask>    postponeUpdates;
    private volatile boolean                   initDone;
    private volatile Throwable                 initError;

    CleanableSearcher(CleanableSearcherProvider searcherService, java.io.File indexDir, Set<String> indexedMediaTypes)
            throws IOException, VirtualFileSystemException {
        super(indexDir, indexedMediaTypes);
        this.searcherService = searcherService;
        postponeUpdates = new ConcurrentLinkedQueue<IndexUpdateTask>();
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
    }

    @Override
    protected void doAdd(VirtualFile virtualFile) throws IOException, VirtualFileSystemException {
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
    protected void doDelete(Term deleteTerm) throws IOException, VirtualFileSystemException {
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
    protected void doUpdate(Term deleteTerm, VirtualFile virtualFile) throws IOException, VirtualFileSystemException {
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
                    update.run();
                }
                postponeUpdates.clear();
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
                initError = e;
                close();
            } catch (VirtualFileSystemException e) {
                LOG.error(e.getMessage(), e);
                initError = e;
                close();
            } catch (RuntimeException e) {
                LOG.error(e.getMessage(), e);
                initError = e;
                close();
                throw e;
            } catch (Error e) {
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
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            } catch (VirtualFileSystemException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }
}
