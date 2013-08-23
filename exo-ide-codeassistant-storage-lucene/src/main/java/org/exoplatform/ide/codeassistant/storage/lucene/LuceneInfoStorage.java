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
package org.exoplatform.ide.codeassistant.storage.lucene;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Container component responsible for extracting class information from jars
 * specified in configuration
 */
public class LuceneInfoStorage {

    private static final Logger LOG = LoggerFactory.getLogger(LuceneInfoStorage.class);

    private final Directory typeInfoIndexDirectory;

    private IndexReader typeInfoIndexReader;

    private IndexSearcher typeInfoIndexSearcher;

    /**
     * Create file based lucene storage.
     *
     * @throws IOException
     */
    public LuceneInfoStorage(String storagePath) throws IOException {
        this(NIOFSDirectory.open(new File(storagePath)));
    }

    /**
     * Create lucene info storage on the given directory.
     *
     * @throws IOException
     */
    public LuceneInfoStorage(Directory typeInfoIndexDirectory) throws IOException {
        this.typeInfoIndexDirectory = typeInfoIndexDirectory;
    }

    public Directory getTypeInfoIndexDirectory() throws IOException {
        return typeInfoIndexDirectory;
    }

    /** Close all open resources. */
    public void closeIndexes() {
        try {
            if (typeInfoIndexReader != null) {
                typeInfoIndexReader.close();
            }
            typeInfoIndexDirectory.close();
        } catch (IOException e) {
            LOG.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * Reopen reader if where is some changes in index
     *
     * @throws CorruptIndexException
     * @throws IOException
     */
    private synchronized void reopenReaderWhenNeed() throws IOException {
        if (typeInfoIndexReader == null) {
            typeInfoIndexReader = IndexReader.open(typeInfoIndexDirectory, true);
            typeInfoIndexSearcher = new IndexSearcher(typeInfoIndexReader);
        } else {
            IndexReader newReader = typeInfoIndexReader.reopen(true);
            if (newReader != typeInfoIndexReader) {
                typeInfoIndexReader.close();
                typeInfoIndexSearcher = new IndexSearcher(newReader);
            }
            typeInfoIndexReader = newReader;
        }
    }

    public IndexSearcher getTypeInfoIndexSearcher() throws IOException {
        reopenReaderWhenNeed();
        return typeInfoIndexSearcher;
    }
}
