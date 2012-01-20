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
package org.exoplatform.ide.codeassistant.storage.lucene.writer;

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;
import org.exoplatform.ide.codeassistant.storage.lucene.LuceneInfoStorage;
import org.exoplatform.ide.codeassistant.storage.lucene.SaveDataIndexException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Add javadoc or TypeInfo information to Lucene index.
 * 
 */
public class LuceneDataWriter
{

   private static final Logger LOG = LoggerFactory.getLogger(LuceneDataWriter.class);

   private final Directory indexDirectory;

   private final DataIndexer indexer;

   public LuceneDataWriter(LuceneInfoStorage luceneInfoStorage) throws IOException
   {
      this.indexDirectory = luceneInfoStorage.getTypeInfoIndexDirectory();
      this.indexer = new DataIndexer();
   }

   /**
    * Add javaDocs to lucene storage.
    * 
    * @param javaDocs
    *           - Map<fqn, doc>
    * @throws SaveDataIndexException
    */
   public void addJavaDocs(Map<String, String> javaDocs) throws SaveDataIndexException
   {

      try
      {
         IndexWriter writer =
            new IndexWriter(indexDirectory, new SimpleAnalyzer(), IndexWriter.MaxFieldLength.UNLIMITED);
         for (Entry<String, String> javaDoc : javaDocs.entrySet())
         {
            writer.addDocument(indexer.createJavaDocDocument(javaDoc.getKey(), javaDoc.getValue()));
         }
         writer.commit();
         writer.close();
      }
      catch (IOException e)
      {
         throw new SaveDataIndexException(e.getLocalizedMessage(), e);
      }
   }

   /**
    * Add List of TypeInfo to index.
    * 
    * @param typeInfos
    * @throws SaveDataIndexException
    */
   public void addTypeInfo(List<TypeInfo> typeInfos) throws SaveDataIndexException
   {

      try
      {
         IndexWriter writer =
            new IndexWriter(indexDirectory, new SimpleAnalyzer(), IndexWriter.MaxFieldLength.UNLIMITED);
         for (TypeInfo typeInfo : typeInfos)
         {
            writer.addDocument(indexer.createTypeInfoDocument(typeInfo));
         }
         writer.commit();
         writer.close();
      }
      catch (IOException e)
      {
         throw new SaveDataIndexException(e.getLocalizedMessage(), e);
      }
   }

}
