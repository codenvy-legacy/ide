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
package org.exoplatform.ide.codeassistant.storage;

import org.exoplatform.ide.codeassistant.storage.api.DataWriter;
import org.exoplatform.ide.codeassistant.storage.api.InfoStorage;
import org.exoplatform.ide.codeassistant.storage.api.WriterTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class StorageWriter implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(StorageWriter.class);

    private BlockingQueue<WriterTask> queue;

    private final InfoStorage infoStorage;

    /**
     * @param queue
     * @param dataWriter
     */
    public StorageWriter(BlockingQueue<WriterTask> queue, InfoStorage infoStorage) {
        super();
        this.queue = queue;
        this.infoStorage = infoStorage;
    }

    /** @see java.lang.Runnable#run() */
    @Override
    public void run() {
        WriterTask task;
        try {
            while ((task = queue.take()).getArtifact() != null) {
                DataWriter dataWriter;
                try {
                    dataWriter = infoStorage.getWriter();
                } catch (IOException e) {
                    LOG.error("Can't get Data Writer", e);
                    continue;
                }
                String artifact = task.getArtifact().toString();
                String version = task.getArtifact().getVersion();
                try {
                    if (task.getTypesInfo() != null) {
                        if (!infoStorage.isArtifactExist(artifact)) {
                            dataWriter.addTypeInfo(task.getTypesInfo(), artifact);
                            dataWriter.addPackages(task.getPackages(), artifact);
                        } else if (version.contains("SNAPSHOT")) // CHeck if it SNAPSHOT version in this case need rewrite index
                        {
                            dataWriter.removeTypeInfo(artifact);
                            dataWriter.removePackages(artifact);
                            dataWriter.addTypeInfo(task.getTypesInfo(), artifact);
                            dataWriter.addPackages(task.getPackages(), artifact);
                        }
                    }
                    if (task.getJavaDock() != null) {
                        if (!infoStorage.isJavaDockForArtifactExist(artifact)) {
                            dataWriter.addJavaDocs(task.getJavaDock(), artifact);
                        } else if (version.contains("SNAPSHOT")) {
                            dataWriter.removeJavaDocs(artifact);
                            dataWriter.addJavaDocs(task.getJavaDock(), artifact);
                        }
                    }
                } catch (Exception e) {
                    LOG.error("Can't write artifact: " + artifact, e);
                }

            }
        } catch (InterruptedException e) {
            LOG.error("Writer thread interripted", e);
        }
    }

}
