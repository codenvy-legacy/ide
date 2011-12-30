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
import org.exoplatform.ide.codeassistant.jvm.TypeInfo;
import org.exoplatform.ide.codeassistant.storage.lucene.LuceneInfoStorage;
import org.exoplatform.ide.codeassistant.storage.lucene.SaveTypeInfoIndexException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Instrument for storing TypeInfo in Lucene Index
 */
public class LuceneTypeInfoWriter
{

   private static final Logger LOG = LoggerFactory.getLogger(LuceneTypeInfoWriter.class);

   private final Directory indexDirectory;

   private final TypeInfoIndexer indexer;

   public LuceneTypeInfoWriter(LuceneInfoStorage luceneInfoStorage) throws SaveTypeInfoIndexException, IOException
   {
      this.indexDirectory = luceneInfoStorage.getTypeInfoIndexDirectory();
      this.indexer = new TypeInfoIndexer();
   }

   /**
    */
   public void addTypeInfo(List<TypeInfo> typeInfos) throws SaveTypeInfoIndexException
   {

      try
      {
         IndexWriter writer =
            new IndexWriter(indexDirectory, new SimpleAnalyzer(), IndexWriter.MaxFieldLength.UNLIMITED);
         for (TypeInfo typeInfo : typeInfos)
         {
            writer.addDocument(indexer.createDocument(typeInfo));
         }
         writer.commit();
         writer.close();
      }
      catch (IOException e)
      {
         LOG.error(e.getLocalizedMessage());
         throw new SaveTypeInfoIndexException(e.getLocalizedMessage(), e);
      }
   }
}
