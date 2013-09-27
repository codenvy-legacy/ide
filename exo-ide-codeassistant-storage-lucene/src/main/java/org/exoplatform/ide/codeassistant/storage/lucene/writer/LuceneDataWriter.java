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
package org.exoplatform.ide.codeassistant.storage.lucene.writer;

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;
import org.exoplatform.ide.codeassistant.storage.lucene.DataIndexFields;
import org.exoplatform.ide.codeassistant.storage.lucene.IndexType;
import org.exoplatform.ide.codeassistant.storage.lucene.LuceneInfoStorage;
import org.exoplatform.ide.codeassistant.storage.lucene.SaveDataIndexException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/** Add javadoc or TypeInfo information to Lucene index. */
public class LuceneDataWriter {

    private final Directory indexDirectory;

    private final DataIndexer indexer;

    private static final Logger LOG = LoggerFactory.getLogger(LuceneDataWriter.class);

    public LuceneDataWriter(LuceneInfoStorage luceneInfoStorage) throws IOException {
        this.indexDirectory = luceneInfoStorage.getTypeInfoIndexDirectory();
        this.indexer = new DataIndexer();
    }

    /**
     * Add javaDocs to lucene storage.
     *
     * @param javaDocs
     *         - Map<fqn, doc>
     * @param artifact
     * @throws SaveDataIndexException
     */
    public void addJavaDocs(Map<String, String> javaDocs, String artifact) throws SaveDataIndexException {
        LOG.info("Add JavaDoc for : " + artifact);
        IndexWriter writer = null;
        try {
            writer = new IndexWriter(indexDirectory, new SimpleAnalyzer(), IndexWriter.MaxFieldLength.UNLIMITED);
            for (Entry<String, String> javaDoc : javaDocs.entrySet()) {
                writer.addDocument(indexer.createJavaDocDocument(javaDoc.getKey(), javaDoc.getValue(), artifact));
            }
            writer.commit();
        } catch (IOException e) {
            throw new SaveDataIndexException(e.getLocalizedMessage(), e);
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                throw new SaveDataIndexException(e.getLocalizedMessage(), e);
            }
        }
    }

    /**
     * remove javaDocs from Lucene storage.
     *
     * @param javaDocs
     *         - Map<fqn, doc>
     * @param artifact
     * @throws SaveDataIndexException
     */
    public void removeJavaDocs(String artifact) throws SaveDataIndexException {
        LOG.info("Delete JavaDoc for : " + artifact);
        IndexWriter writer = null;
        try {
            writer = new IndexWriter(indexDirectory, new SimpleAnalyzer(), IndexWriter.MaxFieldLength.UNLIMITED);
            removeLuceneDoc(artifact, IndexType.DOC, DataIndexFields.FQN, writer);
            writer.commit();
        } catch (IOException e) {
            throw new SaveDataIndexException(e.getLocalizedMessage(), e);
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                throw new SaveDataIndexException(e.getLocalizedMessage(), e);
            }
        }
    }

    /**
     * Add List of TypeInfo to index.
     *
     * @param typeInfos
     * @throws SaveDataIndexException
     */
    public void addTypeInfo(List<TypeInfo> typeInfos, String artifact) throws SaveDataIndexException {
        LOG.info("Add TypeInfo for : " + artifact);
        IndexWriter writer = null;
        try {
            writer = new IndexWriter(indexDirectory, new SimpleAnalyzer(), IndexWriter.MaxFieldLength.UNLIMITED);
            for (TypeInfo typeInfo : typeInfos) {
                writer.addDocument(indexer.createTypeInfoDocument(typeInfo, artifact));
            }

            writer.commit();
        } catch (IOException e) {
            throw new SaveDataIndexException(e.getLocalizedMessage(), e);
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                throw new SaveDataIndexException(e.getLocalizedMessage(), e);
            }
        }
    }


    /**
     * Remove TypeInfo from index according to artifact.
     *
     * @param typeInfos
     * @throws SaveDataIndexException
     */
    public void removeTypeInfo(String artifact) throws SaveDataIndexException {
        LOG.info("Delete TypeInfo for : " + artifact);
        IndexWriter writer = null;
        try {
            writer = new IndexWriter(indexDirectory, new SimpleAnalyzer(), IndexWriter.MaxFieldLength.UNLIMITED);
            removeLuceneDoc(artifact, IndexType.JAVA, DataIndexFields.FQN, writer);
            writer.commit();
        } catch (IOException e) {
            throw new SaveDataIndexException(e.getLocalizedMessage(), e);
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                throw new SaveDataIndexException(e.getLocalizedMessage(), e);
            }
        }
    }

    /**
     * Add packages to index.
     * Packages format:
     * <pre>
     * java
     * java.lang
     * java.util
     * org
     * org.exoplatform
     * org.exoplatform.ide
     * </pre>
     *
     * @param packages
     * @param artifact
     * @throws SaveDataIndexException
     */
    public void addPackages(Set<String> packages, String artifact) throws SaveDataIndexException {
        LOG.info("Add Packages  for : " + artifact);
        IndexWriter writer = null;
        try {
            writer = new IndexWriter(indexDirectory, new SimpleAnalyzer(), IndexWriter.MaxFieldLength.UNLIMITED);
            for (String pack : packages) {
                writer.addDocument(indexer.createPackageDocument(pack, artifact));
            }
            writer.commit();
        } catch (IOException e) {
            throw new SaveDataIndexException(e.getLocalizedMessage(), e);
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                throw new SaveDataIndexException(e.getLocalizedMessage(), e);
            }
        }
    }

    /**
     * Remove packages from index according to artifact.
     *
     * @param packages
     * @param artifact
     * @throws SaveDataIndexException
     */
    public void removePackages(String artifact) throws SaveDataIndexException {
        LOG.info("Delete: Packages  for : " + artifact);
        IndexWriter writer = null;
        try {
            writer = new IndexWriter(indexDirectory, new SimpleAnalyzer(), IndexWriter.MaxFieldLength.UNLIMITED);
            removeLuceneDoc(artifact, IndexType.PACKAGE, DataIndexFields.PACKAGE, writer);
            writer.commit();
        } catch (IOException e) {
            throw new SaveDataIndexException(e.getLocalizedMessage(), e);
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                throw new SaveDataIndexException(e.getLocalizedMessage(), e);
            }
        }
    }


    private void removeLuceneDoc(String artifact, IndexType indexType, String dataField, IndexWriter writer, String key)
            throws CorruptIndexException, IOException {
        Query from = indexType.getQuery();
        TermQuery termQuery = new TermQuery(new Term(DataIndexFields.ARTIFACT, artifact));
        BooleanQuery query = new BooleanQuery();
        query.add(from, Occur.MUST);
        query.add(termQuery, Occur.MUST);
        if (key != null && !key.isEmpty())
            query.add(new PrefixQuery(new Term(dataField, key)), Occur.MUST);
        writer.deleteDocuments(query);
    }

    private void removeLuceneDoc(String artifact, IndexType indexType, String dataField, IndexWriter writer)
            throws CorruptIndexException, IOException {
        removeLuceneDoc(artifact, indexType, dataField, writer, null);
    }

}
