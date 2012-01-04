/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.codeassistant.storage.lucene.writer;

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.exoplatform.ide.codeassistant.storage.lucene.LuceneInfoStorage;
import org.exoplatform.ide.codeassistant.storage.lucene.SaveTypeInfoIndexException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Instrument for storing JavaDocs in Lucene Index
 */
public class LuceneJavaDocWriter
{

   private static final Logger LOG = LoggerFactory.getLogger(LuceneJavaDocWriter.class);

   private final Directory indexDirectory;

   private final JavaDocIndexer indexer;

   public LuceneJavaDocWriter(LuceneInfoStorage luceneInfoStorage) throws IOException
   {
      this.indexDirectory = luceneInfoStorage.getTypeInfoIndexDirectory();
      this.indexer = new JavaDocIndexer();
   }

   /**
    * Add javaDocs to lucene storage.
    * 
    * @param javaDocs
    *           - Map<fqn, doc>
    * @throws SaveTypeInfoIndexException
    */
   public void addJavaDocs(Map<String, String> javaDocs) throws SaveTypeInfoIndexException
   {

      try
      {
         IndexWriter writer =
            new IndexWriter(indexDirectory, new SimpleAnalyzer(), IndexWriter.MaxFieldLength.UNLIMITED);
         for (Entry<String, String> javaDoc : javaDocs.entrySet())
         {
            writer.addDocument(indexer.createDocument(javaDoc.getKey(), javaDoc.getValue()));
         }
         writer.commit();
         writer.close();
      }
      catch (IOException e)
      {
         LOG.error(e.getLocalizedMessage());
         /*
          * TODO rename SaveTypeInfoIndexException to something like SaveIndexException,
          * because it's throws not only when TypeInfo saved unsuccessfully, now
          * Or create separate exception class for java doc exceptions
          */
         throw new SaveTypeInfoIndexException(e.getLocalizedMessage(), e);
      }
   }

}
