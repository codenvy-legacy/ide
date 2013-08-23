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

import org.exoplatform.ide.codeassistant.jvm.bean.Dependency;
import org.exoplatform.ide.codeassistant.storage.api.InfoStorage;
import org.exoplatform.ide.codeassistant.storage.api.WriterTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class DockUpdateInvoker implements UpdateInvoker {
    private static final Logger LOG = LoggerFactory.getLogger(DockUpdateInvoker.class);

    private final InfoStorage infoStorage;

    private final List<Dependency> dependencies;

    private final File dependencyFolder;

    private final BlockingQueue<WriterTask> writerQueue;

    /**
     * @param infoStorage
     * @param dependencies
     * @param createDependencys
     */
    public DockUpdateInvoker(InfoStorage infoStorage, BlockingQueue<WriterTask> writerQueue, List<Dependency> dependencies,
                             File dependencyFolder) {
        this.infoStorage = infoStorage;
        this.writerQueue = writerQueue;
        this.dependencies = dependencies;
        this.dependencyFolder = dependencyFolder;
    }

    /** @see org.exoplatform.ide.codeassistant.storage.UpdateInvoker#execute() */
    @Override
    public UpdateStorageResult execute() {
        QDoxJavaDocExtractor javaDocExtractor = new QDoxJavaDocExtractor();
        try {
            for (Dependency dep : dependencies) {

                String artifact = dep.toString();
                if (infoStorage.isJavaDockForArtifactExist(artifact) && !dep.getVersion().contains("SNAPSHOT"))
                    continue;

                String jarName = getJarName(dep);
                File jarFile = new File(dependencyFolder, jarName);

                LOG.info("Load javadoc from: " + jarName);

                if (!jarFile.exists())
                    continue;

                InputStream zipStream = new FileInputStream(jarFile);
                try {
                    Map<String, String> javaDocs = javaDocExtractor.extractZip(zipStream);
                    writerQueue.put(new WriterTask(dep, javaDocs));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    zipStream.close();
                }
            }
            return new UpdateStorageResult();
        } catch (IOException e) {
            LOG.error("Can't index javadoc", e);
            return new UpdateStorageResult(e.getMessage(), 100);
        } finally {
            UpdateUtil.delete(dependencyFolder);
        }
    }

    /**
     * @param dep
     * @return
     */
    private String getJarName(Dependency dep) {
        StringBuilder b = new StringBuilder();
        b.append(dep.getArtifactID()).append('-').append(dep.getVersion()).append('-').append("sources").append('.')
         .append(dep.getType());
        return b.toString();
    }

}
