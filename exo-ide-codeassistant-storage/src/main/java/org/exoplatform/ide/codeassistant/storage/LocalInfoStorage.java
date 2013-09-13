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

import org.exoplatform.ide.codeassistant.jvm.CodeAssistantException;
import org.exoplatform.ide.codeassistant.storage.api.DataWriter;
import org.exoplatform.ide.codeassistant.storage.api.InfoStorage;
import org.exoplatform.ide.codeassistant.storage.lucene.IndexType;
import org.exoplatform.ide.codeassistant.storage.lucene.LuceneInfoStorage;
import org.exoplatform.ide.codeassistant.storage.lucene.search.ArtifactExtractor;
import org.exoplatform.ide.codeassistant.storage.lucene.search.LuceneQueryExecutor;
import org.exoplatform.ide.codeassistant.storage.lucene.writer.LuceneDataWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

import static org.exoplatform.ide.codeassistant.storage.lucene.search.SearchByFieldConstraint.eq;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class LocalInfoStorage implements InfoStorage {

    private static final Logger LOG = LoggerFactory.getLogger(LocalInfoStorage.class);

    private LuceneInfoStorage infoStorage;

    private final LuceneQueryExecutor queryExecutor;

    /** @param infoStorage */
    public LocalInfoStorage(LuceneInfoStorage infoStorage) {
        super();
        this.infoStorage = infoStorage;
        queryExecutor = new LuceneQueryExecutor(infoStorage);
    }

    /**
     * @throws IOException
     * @see org.exoplatform.ide.codeassistant.storage.api.InfoStorage#getWriter()
     */
    @Override
    public DataWriter getWriter() throws IOException {
        return new LocalDataWriter(new LuceneDataWriter(infoStorage));
    }

    /** @see org.exoplatform.ide.codeassistant.storage.api.InfoStorage#isArtifactExist(java.lang.String) */
    @Override
    public boolean isArtifactExist(String artifact) {
        return isExist(artifact, IndexType.PACKAGE);
    }

    /**
     * @param artifact
     * @return
     */
    private boolean isExist(String artifact, IndexType indexType) {
        try {
            List<String> artifacts =
                    queryExecutor.executeQuery(new ArtifactExtractor(), indexType, eq("artifact", artifact), 100, 0);
            return (artifacts != null && !artifacts.isEmpty());
        } catch (CodeAssistantException e) {
            if (LOG.isDebugEnabled())
                LOG.debug(e.getMessage(), e);
        }
        return false;
    }

    /** @see org.exoplatform.ide.codeassistant.storage.api.InfoStorage#isJavaDockForArtifactExist(java.lang.String) */
    @Override
    public boolean isJavaDockForArtifactExist(String artifact) {
        return isExist(artifact, IndexType.DOC);
    }

}
