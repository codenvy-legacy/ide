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

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.SingleInstanceLockFactory;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.apache.lucene.search.BooleanClause.Occur;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class Searcher
{
   private static final ConcurrentMap<File, SearchService> instances = new ConcurrentHashMap<File, SearchService>();

   public static SearchService getInstance(MountPoint mountPoint)
   {
      final java.io.File key = mountPoint.getRoot().getIoFile();
      SearchService searchService = instances.get(key);
      if (searchService == null)
      {
         SearchService newSearchService = new SearchService();
         searchService = instances.putIfAbsent(key, newSearchService);
         if (searchService == null)
         {
            searchService = newSearchService;
         }
      }
      return searchService;
   }


   private final AtomicBoolean reopening = new AtomicBoolean(false);

   private  Set<String> supportedMediaTypes;

   private IndexWriter indexWriter;
   private IndexSearcher searcher;

   private SearchService()
   {

   }

   private void init(){
      // TODO:
   }

   public SearchService(java.io.File indexDir, Set<String> supportedMediaTypes) throws VirtualFileSystemException
   {
      initLucene(indexDir);
      this.supportedMediaTypes = new LinkedHashSet<String>(supportedMediaTypes);
   }

   protected void initLucene(java.io.File indexDir) throws VirtualFileSystemException
   {
      try
      {
         Directory directory = FSDirectory.open(indexDir, new SingleInstanceLockFactory());
         indexWriter = new IndexWriter(directory, new SimpleAnalyzer(), IndexWriter.MaxFieldLength.UNLIMITED);
         searcher = new IndexSearcher(indexWriter.getReader());
      }
      catch (IOException e)
      {
         throw new VirtualFileSystemException(e.getMessage(), e);// TODO : message
      }
   }

   protected Collection<String> getListOfIndicesTypes() throws VirtualFileSystemException
   {
      Set<String> result = null;
      final URL url = Thread.currentThread().getContextClassLoader().getResource("META-INF/indices_types.txt");
      if (url != null)
      {
         BufferedReader reader = null;
         try
         {
            reader = new BufferedReader(new FileReader(new java.io.File(url.toURI())));
            result = new LinkedHashSet<String>();
            String line;
            while ((line = reader.readLine()) != null)
            {
               int c = line.indexOf('#');
               if (c >= 0)
               {
                  line = line.substring(0, c);
               }
               line = line.trim();
               if (line.length() > 0)
               {
                  result.add(line);
               }
            }
         }
         catch (IOException e)
         {
            throw new VirtualFileSystemException(
               String.format("Failed to get list of media types for indexing. %s", e.getMessage()));
         }
         catch (URISyntaxException e)
         {
            throw new VirtualFileSystemException(
               String.format("Failed to get list of media types for indexing. %s", e.getMessage()));
         }
         finally
         {
            if (reader != null)
            {
               try
               {
                  reader.close();
               }
               catch (IOException ignored)
               {
               }
            }
         }
      }
      if (result == null)
      {
         throw new VirtualFileSystemException("Failed to get list of media types for indexing. " +
            "File 'META-INF/indices_types.txt not found or empty. ");
      }
      return result;
   }

   public void search(QueryExpression query) throws Exception
   {
      final BooleanQuery luceneQuery = new BooleanQuery();

      final String name = query.getName();
      final String path = query.getPath();
      final String mediaType = query.getMediaType();
      final String text = query.getText();

      if (name != null)
      {
         luceneQuery.add(new WildcardQuery(new Term("name", name)), Occur.MUST);
      }
      if (path != null)
      {
         luceneQuery.add(new PrefixQuery(new Term("path", path)), Occur.MUST);
      }
      if (mediaType != null)
      {
         luceneQuery.add(new TermQuery(new Term("media_type", mediaType)), Occur.MUST);
      }
      if (text != null)
      {
         luceneQuery.add(new TermQuery(new Term("text", text)), Occur.MUST);
      }
      final IndexSearcher searcher = getSearcher();
      final TopDocs hits = searcher.search(luceneQuery, 100);
      final int totalHits = hits.totalHits;
      System.out.printf("     %s ==>> totalHits: %d%n", query, totalHits);
      for (ScoreDoc scoreDoc : hits.scoreDocs)
      {
         Document doc = searcher.doc(scoreDoc.doc);
         System.out.printf("     %s ==>> result : path=%s%n", query, doc.getField("path"));
      }
   }

   public void add(VirtualFile f) throws VirtualFileSystemException
   {
      Reader inReader = null;
      try
      {
         inReader = new BufferedReader(new InputStreamReader(f.getContent().getStream()));
         indexWriter.addDocument(createDocument(f, inReader));
      }
      catch (CorruptIndexException e)
      {
         throw new VirtualFileSystemException(e.getMessage(), e);// TODO : message
      }
      catch (IOException e)
      {
         throw new VirtualFileSystemException(e.getMessage(), e);// TODO : message
      }
      finally
      {
         if (inReader != null)
         {
            try
            {
               inReader.close();
            }
            catch (IOException ignored)
            {
            }
         }
      }
   }

   public void delete(String path) throws VirtualFileSystemException
   {
      try
      {
         indexWriter.deleteDocuments(new Term("path", path));
      }
      catch (IOException e)
      {
         throw new VirtualFileSystemException(e.getMessage(), e);// TODO : message
      }
   }

   public void update(VirtualFile f) throws VirtualFileSystemException
   {
      Reader inReader = null;
      try
      {
         inReader = new BufferedReader(new InputStreamReader(f.getContent().getStream()));
         indexWriter.updateDocument(new Term("path", f.getPath()), createDocument(f, inReader));
      }
      catch (CorruptIndexException e)
      {
         throw new VirtualFileSystemException(e.getMessage(), e);// TODO : message
      }
      catch (IOException e)
      {
         throw new VirtualFileSystemException(e.getMessage(), e);// TODO : message
      }
      finally
      {
         if (inReader != null)
         {
            try
            {
               inReader.close();
            }
            catch (IOException ignored)
            {
            }
         }
      }
   }

   private Document createDocument(VirtualFile f, Reader inReader) throws VirtualFileSystemException
   {
      final Document doc = new Document();
      doc.add(new Field("path", f.getPath(), Field.Store.YES, Field.Index.NOT_ANALYZED));
      doc.add(new Field("name", f.getName(), Field.Store.YES, Field.Index.NOT_ANALYZED));
      String mediaType = f.getMediaType();
      int paramStartIndex = mediaType.indexOf(';');
      if (paramStartIndex != -1)
      {
         mediaType = mediaType.substring(0, paramStartIndex).trim();
      }
      doc.add(new Field("media_type", mediaType, Field.Store.YES, Field.Index.NOT_ANALYZED));
      doc.add(new Field("text", inReader));
      return doc;
   }

   IndexSearcher getSearcher() throws VirtualFileSystemException
   {
      if (reopening.compareAndSet(false, true))
      {
         try
         {
            IndexReader reader = searcher.getIndexReader();
            IndexReader newReader = searcher.getIndexReader().reopen();
            if (newReader != reader)
            {
               //System.out.println("UPDATE");
               reader.close();
               searcher = new IndexSearcher(newReader);
            }
            return searcher;
         }
         catch (IOException e)
         {
            throw new VirtualFileSystemException(e.getMessage(), e);// TODO : message
         }
         finally
         {
            reopening.set(false);
         }
      }
      return searcher;
   }
}
