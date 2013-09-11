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

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.SingleInstanceLockFactory;
import org.apache.lucene.util.Version;
import org.exoplatform.ide.vfs.server.exceptions.InvalidArgumentException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemRuntimeException;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.io.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * Lucene based searcher.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class Searcher {
    private static final Log            LOG               = ExoLogger.getLogger(Searcher.class);
    private static final int            RESULT_LIMIT      = 1000;
    private static final FilenameFilter HIDDEN_DIR_FILTER = new FilenameFilter() {
        @Override
        public boolean accept(java.io.File dir, String name) {
            return name.charAt(0) != '.';
        }
    };


    protected final java.io.File indexDir;
    protected final Set<String>  indexedMediaTypes; // update after creation is not expected
    protected final Directory    luceneIndexDirectory;
    protected final IndexWriter  luceneIndexWriter;

    private IndexSearcher luceneIndexSearcher;
    private boolean       reopening;

    public Searcher(java.io.File indexDir, Set<String> indexedMediaTypes) throws IOException, VirtualFileSystemException {
        this.indexDir = indexDir;
        this.indexedMediaTypes = new HashSet<String>(indexedMediaTypes);
        luceneIndexDirectory = FSDirectory.open(indexDir, new SingleInstanceLockFactory());
        luceneIndexWriter = new IndexWriter(luceneIndexDirectory, makeAnalyzer(), IndexWriter.MaxFieldLength.UNLIMITED);
        luceneIndexSearcher = new IndexSearcher(luceneIndexWriter.getReader());
    }

    public java.io.File getIndexDir() {
        return indexDir;
    }

    /**
     * Init lucene index. Need call this method is index directory is clean. Scan all files in virtual filesystem and
     * add to index.
     *
     * @param mountPoint
     *         MountPoint
     * @throws IOException
     *         if any i/o error
     * @throws VirtualFileSystemException
     *         if any virtual filesystem error
     */
    public void init(MountPoint mountPoint) throws IOException, VirtualFileSystemException {
        final long start = System.currentTimeMillis();
        addTree(mountPoint.getRoot());
        final long end = System.currentTimeMillis();
        LOG.debug("Index creation time: {} ms", (end - start));
    }

    /**
     * Get IndexSearcher. It is important to call method {@link #releaseLuceneSearcher(org.apache.lucene.search.IndexSearcher)}
     * to release obtained searcher.
     * <pre>
     *    Searcher searcher = ...
     *    IndexSearcher luceneSearcher = searcher.getLuceneSearcher();
     *    try {
     *       // use obtained lucene searcher
     *    } finally {
     *       searcher.releaseLuceneSearcher(searcher);
     *    }
     * </pre>
     *
     * @return IndexSearcher
     * @throws IOException
     *         if any i/o error
     */
    public synchronized IndexSearcher getLuceneSearcher() throws IOException {
        maybeReopenIndexReader();
        luceneIndexSearcher.getIndexReader().incRef();
        return luceneIndexSearcher;
    }

    // MUST CALL UNDER LOCK
    private void maybeReopenIndexReader() throws IOException {
        while (reopening) {
            try {
                wait();
            } catch (InterruptedException e) {
                notify();
                throw new VirtualFileSystemRuntimeException(e);
            }
        }

        reopening = true;
        try {
            IndexReader reader = luceneIndexSearcher.getIndexReader();
            IndexReader newReader = luceneIndexSearcher.getIndexReader().reopen();
            if (newReader != reader) {
                luceneIndexSearcher = new IndexSearcher(newReader);
            }
        } finally {
            reopening = false;
            notifyAll();
        }
    }

    /**
     * Release IndexSearcher.
     *
     * @param luceneSearcher
     *         IndexSearcher
     * @throws IOException
     *         if any i/o error
     * @see #getLuceneSearcher()
     */
    public synchronized void releaseLuceneSearcher(IndexSearcher luceneSearcher) throws IOException {
        luceneSearcher.getIndexReader().decRef();
    }

    /**
     * Return paths of matched items on virtual filesystem.
     *
     * @param query
     *         query expression
     * @return paths of matched items
     * @throws VirtualFileSystemException
     */
    public String[] search(QueryExpression query) throws VirtualFileSystemException {
        final BooleanQuery luceneQuery = new BooleanQuery();
        final String name = query.getName();
        final String path = query.getPath();
        final String mediaType = query.getMediaType();
        final String text = query.getText();
        if (path != null) {
            luceneQuery.add(new PrefixQuery(new Term("path", path)), BooleanClause.Occur.MUST);
        }
        if (name != null) {
            luceneQuery.add(new WildcardQuery(new Term("name", name)), BooleanClause.Occur.MUST);
        }
        if (mediaType != null) {
            luceneQuery.add(new TermQuery(new Term("mediatype", mediaType)), BooleanClause.Occur.MUST);
        }
        if (text != null) {
            QueryParser qParser = new QueryParser(Version.LUCENE_29, "text", makeAnalyzer());
            try {
                luceneQuery.add(qParser.parse(text), BooleanClause.Occur.MUST);
            } catch (ParseException e) {
                throw new InvalidArgumentException(e.getMessage());
            }
        }
        IndexSearcher luceneSearcher = null;
        try {
            luceneSearcher = getLuceneSearcher();
            final TopDocs topDocs = luceneSearcher.search(luceneQuery, RESULT_LIMIT);
            if (topDocs.totalHits > RESULT_LIMIT) {
                throw new VirtualFileSystemException(
                        String.format("Too many (%d) matched results found. ", topDocs.totalHits));
            }
            final String[] result = new String[topDocs.scoreDocs.length];
            for (int i = 0, length = result.length; i < length; i++) {
                result[i] = luceneSearcher.doc(topDocs.scoreDocs[i].doc).getField("path").stringValue();
            }
            return result;
        } catch (IOException e) {
            throw new VirtualFileSystemException(e.getMessage(), e);
        } finally {
            try {
                releaseLuceneSearcher(luceneSearcher);
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    public final void add(VirtualFile virtualFile) throws IOException, VirtualFileSystemException {
        if (indexedMediaTypes.contains(getMediaType(virtualFile))) {
            doAdd(virtualFile);
        }
    }

    protected void doAdd(VirtualFile virtualFile) throws IOException, VirtualFileSystemException {
        if (virtualFile.isFolder()) {
            addTree(virtualFile);
        } else {
            addFile(virtualFile);
        }
    }

    private void addTree(VirtualFile tree) throws IOException, VirtualFileSystemException {
        final MountPoint mountPoint = tree.getMountPoint();
        final java.io.File ioRoot = tree.getMountPoint().getRoot().getIoFile();
        final LinkedList<VirtualFile> q = new LinkedList<VirtualFile>();
        q.add(tree);
        String[] names;
        int indexedFiles = 0;
        while (!q.isEmpty()) {
            final VirtualFile folder = q.pop();
            if (folder.exists()) {
                names = folder.getIoFile().list(HIDDEN_DIR_FILTER);
                if (names == null) {
                    // Something wrong. According to java docs may be null only if i/o error occurs.
                    throw new VirtualFileSystemException(String.format("Unable get children '%s'. ", folder.getPath()));
                }

                for (String name : names) {
                    final Path childPath = folder.getInternalPath().newPath(name);
                    final VirtualFile child = new VirtualFile(new java.io.File(ioRoot, childPath.toIoPath()), childPath, mountPoint);
                    if (child.isFolder()) {
                        q.push(child);
                    } else {
                        if (indexedMediaTypes.contains(getMediaType(child))) {
                            addFile(child);
                            indexedFiles++;
                        }
                    }
                }
            }
        }
        LOG.debug("Indexed {} files from {}", indexedFiles, tree.getPath());
    }

    private void addFile(VirtualFile file) throws IOException, VirtualFileSystemException {
        if (file.exists()) {
            Reader fContentReader = null;
            try {
                fContentReader = new BufferedReader(new InputStreamReader(file.getContent().getStream()));
                luceneIndexWriter.addDocument(createDocument(file, fContentReader));
            } catch (OutOfMemoryError oome) {
                close();
                throw oome;
            } finally {
                if (fContentReader != null) {
                    try {
                        fContentReader.close();
                    } catch (IOException ignored) {
                    }
                }
            }
        }
    }

    public final void delete(String path) throws IOException, VirtualFileSystemException {
        doDelete(new Term("path", path));
    }

    protected void doDelete(Term deleteTerm) throws IOException, VirtualFileSystemException {
        try {
            luceneIndexWriter.deleteDocuments(new PrefixQuery(deleteTerm));
        } catch (OutOfMemoryError oome) {
            close();
            throw oome;
        }
    }

    public final void update(VirtualFile virtualFile) throws IOException, VirtualFileSystemException {
        if (indexedMediaTypes.contains(getMediaType(virtualFile))) {
            doUpdate(new Term("path", virtualFile.getPath()), virtualFile);
        }
    }

    protected void doUpdate(Term deleteTerm, VirtualFile virtualFile) throws IOException, VirtualFileSystemException {
        Reader fContentReader = null;
        try {
            fContentReader = new BufferedReader(new InputStreamReader(virtualFile.getContent().getStream()));
            luceneIndexWriter.updateDocument(deleteTerm, createDocument(virtualFile, fContentReader));
        } catch (OutOfMemoryError oome) {
            close();
            throw oome;
        } finally {
            if (fContentReader != null) {
                try {
                    fContentReader.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    public void close() {
        closeQuietly(luceneIndexSearcher);
        closeQuietly(luceneIndexWriter);
        closeQuietly(luceneIndexDirectory);
    }

    protected Document createDocument(VirtualFile virtualFile, Reader inReader) throws VirtualFileSystemException {
        final Document doc = new Document();
        doc.add(new Field("path", virtualFile.getPath(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field("name", virtualFile.getName(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field("mediatype", getMediaType(virtualFile), Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field("text", inReader));
        return doc;
    }

    /** Get virtual file media type. Any additional parameters (e.g. 'charset') are removed. */
    protected String getMediaType(VirtualFile virtualFile) throws VirtualFileSystemException {
        String mediaType = virtualFile.getMediaType();
        final int paramStartIndex = mediaType.indexOf(';');
        if (paramStartIndex != -1) {
            mediaType = mediaType.substring(0, paramStartIndex).trim();
        }
        return mediaType;
    }

    protected Analyzer makeAnalyzer() {
        return new SimpleAnalyzer();
    }

    private void closeQuietly(IndexSearcher indexSearcher) {
        if (indexSearcher != null) {
            try {
                indexSearcher.getIndexReader().close();
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    private void closeQuietly(IndexWriter indexWriter) {
        if (indexWriter != null) {
            try {
                indexWriter.close();
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    private void closeQuietly(Directory directory) {
        if (directory != null) {
            try {
                directory.close();
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }
}
