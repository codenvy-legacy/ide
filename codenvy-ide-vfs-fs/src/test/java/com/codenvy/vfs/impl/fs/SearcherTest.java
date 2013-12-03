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

import com.codenvy.api.vfs.shared.dto.Item;
import com.codenvy.api.vfs.shared.dto.ItemList;
import com.codenvy.commons.env.EnvironmentContext;

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.Version;
import org.everrest.core.impl.ContainerResponse;
import org.everrest.core.tools.ByteArrayContainerResponseWriter;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearcherTest extends LocalFileSystemTest {
    private Pair<String[], String>[] queryToResult;

    private String searchTestPath;
    private String file1;
    private String file2;
    private String file3;

    private CleanableSearcher searcher;

    @SuppressWarnings("unchecked")
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        System.setProperty("java.io.tmpdir", root.getParent());

        searchTestPath = createDirectory(testRootPath, "SearcherTest_Folder");

        file1 = createFile(searchTestPath, "SearcherTest_File01", "to be or not to be".getBytes());
        writeProperties(file1, Collections
                .singletonMap("vfs:mimeType", new String[]{"text/xml"})); // text/xml just for test, it is not xml content

        file2 = createFile(searchTestPath, "SearcherTest_File02", "to be or not to be".getBytes());
        writeProperties(file2, Collections.singletonMap("vfs:mimeType", new String[]{"text/plain"}));

        String folder1 = createDirectory(searchTestPath, "folder01");
        file3 = createFile(folder1, "SearcherTest_File03", "to be or not to be".getBytes());
        writeProperties(file3, Collections.singletonMap("vfs:mimeType", new String[]{"text/plain"}));

        queryToResult = new Pair[10];
        // text
        queryToResult[0] = new Pair<>(new String[]{file1, file2, file3}, "text=to%20be%20or%20not%20to%20be");
        queryToResult[1] = new Pair<>(new String[]{file1, file2, file3}, "text=to%20be%20or");
        // text + media type
        queryToResult[2] = new Pair<>(new String[]{file2, file3}, "text=to%20be%20or&mediaType=text/plain");
        queryToResult[3] = new Pair<>(new String[]{file1}, "text=to%20be%20or&mediaType=text/xml");
        // text + name
        queryToResult[4] = new Pair<>(new String[]{file2}, "text=to%20be%20or&name=*File02");
        queryToResult[5] = new Pair<>(new String[]{file1, file2, file3}, "text=to%20be%20or&name=SearcherTest*");
        // text + path
        queryToResult[6] = new Pair<>(new String[]{file3}, "text=to%20be%20or&path=" + folder1);
        queryToResult[7] = new Pair<>(new String[]{file1, file2, file3}, "text=to%20be%20or&path=" + searchTestPath);
        // name + media type
        queryToResult[8] = new Pair<>(new String[]{file2, file3}, "name=SearcherTest*&mediaType=text/plain");
        queryToResult[9] = new Pair<>(new String[]{file1}, "name=SearcherTest*&mediaType=text/xml");

        CleanableSearcherProvider searcherProvider = new CleanableSearcherProvider();
        // Re-register virtual file system with searching enabled.
        // remove old one first
        provider.close();
        assertFalse(provider.isMounted());
        virtualFileSystemRegistry.unregisterProvider(MY_WORKSPACE_ID);
        // create new one
        provider = new LocalFileSystemProvider(MY_WORKSPACE_ID, new EnvironmentContextLocalFSMountStrategy(), searcherProvider);
        provider.mount(testFsIoRoot);
        mountPoint = provider.getMountPoint(true);
        virtualFileSystemRegistry.registerProvider(MY_WORKSPACE_ID, provider);
        // set up index directory
        EnvironmentContext env = EnvironmentContext.getCurrent();
        env.setVariable(EnvironmentContext.VFS_INDEX_DIR, root.getParentFile());

        // Touch Searcher to initialize it.
        searcher = (CleanableSearcher)searcherProvider.getSearcher(mountPoint, true);
        // Wait util searcher initialized.
        Throwable error;
        while ((error = searcher.getInitError()) == null && !searcher.isInitDone()) {
            Thread.sleep(100);
        }
        if (error != null) {
          fail(error.getMessage());
        }

        assertNull(searcher.getInitError());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void testSearch() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "search";
        Map<String, List<String>> h = new HashMap<>(1);
        h.put("Content-Type", Arrays.asList("application/x-www-form-urlencoded"));
        for (Pair<String[], String> pair : queryToResult) {
            ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, h, pair.b.getBytes(), writer, null);
            //log.info(new String(writer.getBody()));
            assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
            List<Item> result = ((ItemList)response.getEntity()).getItems();
            assertEquals(String.format(
                    "Expected %d but found %d for query %s", pair.a.length, result.size(), pair.b),
                         pair.a.length,
                         result.size());
            List<String> resultPaths = new ArrayList<>(result.size());
            for (Item item : result) {
                resultPaths.add(item.getPath());
            }
            List<String> copy = new ArrayList<>(resultPaths);
            copy.removeAll(Arrays.asList(pair.a));
            assertTrue(String.format("Expected result is %s but found %s", Arrays.toString(pair.a), resultPaths), copy.isEmpty());
            writer.reset();
        }
    }

    public void testDeleteFile() throws Exception {
        IndexSearcher luceneSearcher = searcher.getLuceneSearcher();
        TopDocs topDocs = luceneSearcher.search(new TermQuery(new Term("path", file1)), 10);
        assertEquals(1, topDocs.totalHits);
        searcher.releaseLuceneSearcher(luceneSearcher);

        mountPoint.getVirtualFile(file1).delete(null);
        luceneSearcher = searcher.getLuceneSearcher();
        topDocs = luceneSearcher.search(new TermQuery(new Term("path", file1)), 10);
        assertEquals(0, topDocs.totalHits);
        searcher.releaseLuceneSearcher(luceneSearcher);
    }

    public void testDeleteFolder() throws Exception {
        IndexSearcher luceneSearcher = searcher.getLuceneSearcher();
        TopDocs topDocs = luceneSearcher.search(new PrefixQuery(new Term("path", searchTestPath)), 10);
        assertEquals(3, topDocs.totalHits);
        searcher.releaseLuceneSearcher(luceneSearcher);

        mountPoint.getVirtualFile(searchTestPath).delete(null);
        luceneSearcher = searcher.getLuceneSearcher();
        topDocs = luceneSearcher.search(new PrefixQuery(new Term("path", searchTestPath)), 10);
        assertEquals(0, topDocs.totalHits);
        searcher.releaseLuceneSearcher(luceneSearcher);
    }

    public void testAdd() throws Exception {
        IndexSearcher luceneSearcher = searcher.getLuceneSearcher();
        TopDocs topDocs = luceneSearcher.search(new PrefixQuery(new Term("path", searchTestPath)), 10);
        assertEquals(3, topDocs.totalHits);
        searcher.releaseLuceneSearcher(luceneSearcher);
        mountPoint.getVirtualFile(searchTestPath).createFile("new_file", null, new ByteArrayInputStream(DEFAULT_CONTENT_BYTES));

        luceneSearcher = searcher.getLuceneSearcher();
        topDocs = luceneSearcher.search(new PrefixQuery(new Term("path", searchTestPath)), 10);
        assertEquals(4, topDocs.totalHits);
        searcher.releaseLuceneSearcher(luceneSearcher);
    }

    public void testUpdate() throws Exception {
        IndexSearcher luceneSearcher = searcher.getLuceneSearcher();
        TopDocs topDocs = luceneSearcher.search(
                new QueryParser(Version.LUCENE_29, "text", new SimpleAnalyzer()).parse("updated"), 10);
        assertEquals(0, topDocs.totalHits);
        searcher.releaseLuceneSearcher(luceneSearcher);
        mountPoint.getVirtualFile(file2).updateContent(null, new ByteArrayInputStream("updated content".getBytes()), null);

        luceneSearcher = searcher.getLuceneSearcher();
        topDocs = luceneSearcher.search(new QueryParser(Version.LUCENE_29, "text", new SimpleAnalyzer()).parse("updated"), 10);
        assertEquals(1, topDocs.totalHits);
        searcher.releaseLuceneSearcher(luceneSearcher);
    }

    public void testMove() throws Exception {
        IndexSearcher luceneSearcher = searcher.getLuceneSearcher();
        String destination = createDirectory(testRootPath, "___destination");
        String expected = destination + '/' + "SearcherTest_File03";
        TopDocs topDocs = luceneSearcher.search(new PrefixQuery(new Term("path", expected)), 10);
        assertEquals(0, topDocs.totalHits);
        searcher.releaseLuceneSearcher(luceneSearcher);
        mountPoint.getVirtualFile(file3).moveTo(mountPoint.getVirtualFile(destination), null);

        luceneSearcher = searcher.getLuceneSearcher();
        topDocs = luceneSearcher.search(new PrefixQuery(new Term("path", expected)), 10);
        assertEquals(1, topDocs.totalHits);
        topDocs = luceneSearcher.search(new PrefixQuery(new Term("path", file3)), 10);
        assertEquals(0, topDocs.totalHits);
        searcher.releaseLuceneSearcher(luceneSearcher);
    }

    public void testCopy() throws Exception {
        IndexSearcher luceneSearcher = searcher.getLuceneSearcher();
        String destination = createDirectory(testRootPath, "___destination");
        String expected = destination + '/' + "SearcherTest_File03";
        TopDocs topDocs = luceneSearcher.search(new PrefixQuery(new Term("path", expected)), 10);
        assertEquals(0, topDocs.totalHits);
        searcher.releaseLuceneSearcher(luceneSearcher);
        mountPoint.getVirtualFile(file3).copyTo(mountPoint.getVirtualFile(destination));

        luceneSearcher = searcher.getLuceneSearcher();
        topDocs = luceneSearcher.search(new PrefixQuery(new Term("path", expected)), 10);
        assertEquals(1, topDocs.totalHits);
        topDocs = luceneSearcher.search(new PrefixQuery(new Term("path", file3)), 10);
        assertEquals(1, topDocs.totalHits);
        searcher.releaseLuceneSearcher(luceneSearcher);
    }

    public void testRename() throws Exception {
        String newName = "___renamed";
        IndexSearcher luceneSearcher = searcher.getLuceneSearcher();
        TopDocs topDocs = luceneSearcher.search(new PrefixQuery(new Term("path", file3)), 10);
        assertEquals(1, topDocs.totalHits);
        searcher.releaseLuceneSearcher(luceneSearcher);
        mountPoint.getVirtualFile(file2).rename(newName, null, null);

        luceneSearcher = searcher.getLuceneSearcher();
        topDocs = luceneSearcher.search(new PrefixQuery(new Term("path", searchTestPath + '/' + newName)), 10);
        assertEquals(1, topDocs.totalHits);
        topDocs = luceneSearcher.search(new PrefixQuery(new Term("path", file2)), 10);
        assertEquals(0, topDocs.totalHits);
        searcher.releaseLuceneSearcher(luceneSearcher);
    }
}
