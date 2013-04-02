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
