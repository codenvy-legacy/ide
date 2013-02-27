/*
 * Copyright (C) 2010 eXo Platform SAS.
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
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemList;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearcherTest extends LocalFileSystemTest
{
   private Pair<String[], String>[] queryToResult;

   private String searchTestPath;
   private String file1;
   private String file2;
   private String file3;

   private CleanableSearcher searcher;

   @SuppressWarnings("unchecked")
   @Override
   protected void setUp() throws Exception
   {
      super.setUp();
      System.setProperty("java.io.tmpdir", root.getParent());

      searchTestPath = createDirectory(testRootPath, "SearcherTest_Folder");

      file1 = createFile(searchTestPath, "SearcherTest_File01", "to be or not to be".getBytes());
      writeProperties(file1, Collections.singletonMap("vfs:mimeType", new String[]{"text/xml"})); // text/xml just for test, it is not xml content

      file2 = createFile(searchTestPath, "SearcherTest_File02", "to be or not to be".getBytes());
      writeProperties(file2, Collections.singletonMap("vfs:mimeType", new String[]{"text/plain"}));

      String folder1 = createDirectory(searchTestPath, "folder01");
      file3 = createFile(folder1, "SearcherTest_File03", "to be or not to be".getBytes());

      queryToResult = new Pair[10];
      // text
      queryToResult[0] = new Pair<String[], String>(new String[]{file1, file2, file3}, "text=to%20be%20or%20not%20to%20be");
      queryToResult[1] = new Pair<String[], String>(new String[]{file1, file2, file3}, "text=to%20be%20or");
      // text + media type
      queryToResult[2] = new Pair<String[], String>(new String[]{file2, file3}, "text=to%20be%20or&mediaType=text/plain");
      queryToResult[3] = new Pair<String[], String>(new String[]{file1}, "text=to%20be%20or&mediaType=text/xml");
      // text + name
      queryToResult[4] = new Pair<String[], String>(new String[]{file2}, "text=to%20be%20or&name=*File02");
      queryToResult[5] = new Pair<String[], String>(new String[]{file1, file2, file3}, "text=to%20be%20or&name=SearcherTest*");
      // text + path
      queryToResult[6] = new Pair<String[], String>(new String[]{file3}, "text=to%20be%20or&path=" + folder1);
      queryToResult[7] = new Pair<String[], String>(new String[]{file1, file2, file3}, "text=to%20be%20or&path=" + searchTestPath);
      // name + media type
      queryToResult[8] = new Pair<String[], String>(new String[]{file2, file3}, "name=SearcherTest*&mediaType=text/plain");
      queryToResult[9] = new Pair<String[], String>(new String[]{file1}, "name=SearcherTest*&mediaType=text/xml");

      CleanableSearcherProvider searcherProvider = new CleanableSearcherProvider(root.getParentFile());
      // Re-register virtual file system with searching enabled.
      // remove old one first
      assertTrue(provider.umount(testFsIoRoot));
      virtualFileSystemRegistry.unregisterProvider(VFS_ID);
      // create new one
      provider = new LocalFileSystemProvider(VFS_ID, new ConversationStateLocalFSMountStrategy(root), searcherProvider);
      provider.mount(testFsIoRoot);
      mountPoint = provider.getMounts().iterator().next();
      virtualFileSystemRegistry.registerProvider(VFS_ID, provider);

      // Touch Searcher to initialize it.
      searcher = (CleanableSearcher)searcherProvider.getSearcher(mountPoint);
      // Wait util searcher initialized.
      while (!searcher.isInitDone())
      {
         Thread.sleep(100);
      }

      assertNull(searcher.getInitError());
   }

   @SuppressWarnings({"rawtypes", "unchecked"})
   public void testSearch() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String requestPath = SERVICE_URI + "search";
      Map<String, List<String>> h = new HashMap<String, List<String>>(1);
      h.put("Content-Type", Arrays.asList("application/x-www-form-urlencoded"));
      for (Pair<String[], String> pair : queryToResult)
      {
         ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, h, pair.b.getBytes(), writer, null);
         //log.info(new String(writer.getBody()));
         assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
         List<Item> result = ((ItemList)response.getEntity()).getItems();
         assertEquals(String.format(
            "Expected %d but found %d for query %s", pair.a.length, result.size(), pair.b),
            pair.a.length,
            result.size());
         List<String> resultPaths = new ArrayList<String>(result.size());
         for (Item item : result)
         {
            resultPaths.add(item.getPath());
         }
         List<String> copy = new ArrayList<String>(resultPaths);
         copy.removeAll(Arrays.asList(pair.a));
         assertTrue(String.format("Expected result is %s but found %s", Arrays.toString(pair.a), resultPaths), copy.isEmpty());
         writer.reset();
      }
   }

   public void testDelete() throws Exception
   {
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

   public void testDelete2() throws Exception
   {
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

   public void testAdd() throws Exception
   {
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

   public void testUpdate() throws Exception
   {
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

   public void testMove() throws Exception
   {
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

   public void testCopy() throws Exception
   {
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

   public void testRename() throws Exception
   {
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
