/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.codeassistant.storage.lucene.delete;

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.everrest.core.impl.provider.json.JsonException;
import org.everrest.core.impl.provider.json.JsonParser;
import org.everrest.core.impl.provider.json.ObjectBuilder;
import org.exoplatform.ide.codeassistant.storage.lucene.DataIndexFields;
import org.exoplatform.ide.codeassistant.storage.lucene.IndexType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class CleanUpIndex
{

   private static final String DEFAULT_INDEX_DIRECTORY = "/home/evgen/ide-codeassistant-lucene-index";

   private static final String DEFAULT_PACKAGE_IGNORED_LIST = "codeassistant/ignored-packages.js";

   private static final Logger LOG = LoggerFactory.getLogger(CleanUpIndex.class);

   public static void main(String[] args) throws IOException
   {
      ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
      InputStream io = contextClassLoader.getResourceAsStream(DEFAULT_PACKAGE_IGNORED_LIST);
      JsonParser p = new JsonParser();
      if (io == null)
         io = new FileInputStream(new File(DEFAULT_PACKAGE_IGNORED_LIST));
      String[] ignored = null;
      try
      {
         p.parse(io);
         ignored = (String[])ObjectBuilder.createArray(String[].class, p.getJsonObject());

      }
      catch (JsonException e)
      {
         e.printStackTrace();
      }
      finally
      {
         io.close();
      }
      Directory indexDirectory = NIOFSDirectory.open(new File(DEFAULT_INDEX_DIRECTORY));
      removeDocuments(indexDirectory, ignored, IndexType.JAVA, DataIndexFields.FQN);
      removeDocuments(indexDirectory, ignored, IndexType.DOC, DataIndexFields.FQN);
      removeDocuments(indexDirectory, ignored, IndexType.PACKAGE, DataIndexFields.PACKAGE);
      LOG.info("Optimizing index");
      IndexWriter writer = new IndexWriter(indexDirectory, new SimpleAnalyzer(), IndexWriter.MaxFieldLength.UNLIMITED);
      writer.optimize();
      writer.close();
      LOG.info("All done!");
   }

   /**
    * @param directory 
    * @param ignored
    * @throws IOException
    */
   private static void removeDocuments(Directory directory, String[] ignored, IndexType indexType, String indexField)
      throws IOException
   {
      Query from = indexType.getQuery();
      TermQuery artifact = new TermQuery(new Term(DataIndexFields.ARTIFACT, "java:rt:1.6:jar"));
      for (String prefix : ignored)
      {
         LOG.info("Delete: IndexType: " + indexType + ", Prefix: " + prefix);
         BooleanQuery query = new BooleanQuery();
         query.add(from, Occur.MUST);
         query.add(artifact, Occur.MUST);
         query.add(new PrefixQuery(new Term(indexField, prefix.replaceAll("\\/", "."))), Occur.MUST);
         IndexWriter writer = new IndexWriter(directory, new SimpleAnalyzer(), IndexWriter.MaxFieldLength.UNLIMITED);
         writer.deleteDocuments(query);
         writer.close();
      }
   }
}
