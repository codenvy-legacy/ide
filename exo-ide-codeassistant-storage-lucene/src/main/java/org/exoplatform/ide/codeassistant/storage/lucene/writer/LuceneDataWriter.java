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
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.store.Directory;
import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;
import org.exoplatform.ide.codeassistant.storage.lucene.DataIndexFields;
import org.exoplatform.ide.codeassistant.storage.lucene.IndexType;
import org.exoplatform.ide.codeassistant.storage.lucene.LuceneInfoStorage;
import org.exoplatform.ide.codeassistant.storage.lucene.SaveDataIndexException;
import org.exoplatform.ide.codeassistant.storage.lucene.delete.CleanUpIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Add javadoc or TypeInfo information to Lucene index.
 * 
 */
public class LuceneDataWriter
{

   private final Directory indexDirectory;

   private final DataIndexer indexer;

   private static final Logger LOG = LoggerFactory.getLogger(LuceneDataWriter.class);

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
    * @param artifact 
    * @throws SaveDataIndexException
    */
   public void addJavaDocs(Map<String, String> javaDocs, String artifact) throws SaveDataIndexException
   {

      IndexWriter writer = null;
      try
      {
         writer = new IndexWriter(indexDirectory, new SimpleAnalyzer(), IndexWriter.MaxFieldLength.UNLIMITED);
         for (Entry<String, String> javaDoc : javaDocs.entrySet())
         {
            writer.addDocument(indexer.createJavaDocDocument(javaDoc.getKey(), javaDoc.getValue(), artifact));
         }
         writer.commit();
      }
      catch (IOException e)
      {
         throw new SaveDataIndexException(e.getLocalizedMessage(), e);
      }
      finally
      {
         try
         {
            writer.close();
         }
         catch (IOException e)
         {
            throw new SaveDataIndexException(e.getLocalizedMessage(), e);
         }
      }
   }

   /**
    * remove javaDocs to lucene storage.
    * 
    * @param javaDocs
    *           - Map<fqn, doc>
    * @param artifact 
    * @throws SaveDataIndexException
    */
   public void removeJavaDocs(String artifact) throws SaveDataIndexException
   {

      IndexWriter writer = null;
      try
      {
         writer = new IndexWriter(indexDirectory, new SimpleAnalyzer(), IndexWriter.MaxFieldLength.UNLIMITED);
         removeDoc(artifact, IndexType.DOC, DataIndexFields.FQN, writer);
         writer.commit();
      }
      catch (IOException e)
      {
         throw new SaveDataIndexException(e.getLocalizedMessage(), e);
      }
      finally
      {
         try
         {
            writer.close();
         }
         catch (IOException e)
         {
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
   public void addTypeInfo(List<TypeInfo> typeInfos, String artifact) throws SaveDataIndexException
   {

      IndexWriter writer = null;
      try
      {
         writer = new IndexWriter(indexDirectory, new SimpleAnalyzer(), IndexWriter.MaxFieldLength.UNLIMITED);
         for (TypeInfo typeInfo : typeInfos)
         {
            writer.addDocument(indexer.createTypeInfoDocument(typeInfo, artifact));
         }

         writer.commit();
      }
      catch (IOException e)
      {
         throw new SaveDataIndexException(e.getLocalizedMessage(), e);
      }
      finally
      {
         try
         {
            writer.close();
         }
         catch (IOException e)
         {
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
   public void removeTypeInfo(String artifact) throws SaveDataIndexException
   {

      LOG.info("Delete: TypeInfo for : " + artifact);
      IndexWriter writer = null;
      try
      {
         writer = new IndexWriter(indexDirectory, new SimpleAnalyzer(), IndexWriter.MaxFieldLength.UNLIMITED);
         removeDoc(artifact, IndexType.JAVA, DataIndexFields.FQN, writer);
         writer.commit();
      }
      catch (IOException e)
      {
         throw new SaveDataIndexException(e.getLocalizedMessage(), e);
      }
      finally
      {
         try
         {
            writer.close();
         }
         catch (IOException e)
         {
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
   public void addPackages(Set<String> packages, String artifact) throws SaveDataIndexException
   {
      IndexWriter writer = null;
      try
      {
         writer = new IndexWriter(indexDirectory, new SimpleAnalyzer(), IndexWriter.MaxFieldLength.UNLIMITED);
         for (String pack : packages)
         {
            writer.addDocument(indexer.createPackageDocument(pack, artifact));
         }
         writer.commit();
      }
      catch (IOException e)
      {
         throw new SaveDataIndexException(e.getLocalizedMessage(), e);
      }
      finally
      {
         try
         {
            writer.close();
         }
         catch (IOException e)
         {
            throw new SaveDataIndexException(e.getLocalizedMessage(), e);
         }
      }
   }

   /**
    * Remove packages to index.
    * 
    * @param packages
    * @param artifact 
    * @throws SaveDataIndexException
    */
   public void removePackages(String artifact) throws SaveDataIndexException
   {
      LOG.info("Delete: Packages  for : " + artifact);
      IndexWriter writer = null;
      try
      {
         writer = new IndexWriter(indexDirectory, new SimpleAnalyzer(), IndexWriter.MaxFieldLength.UNLIMITED);
         removeDoc(artifact, IndexType.PACKAGE, DataIndexFields.PACKAGE, writer);
         writer.commit();
      }
      catch (IOException e)
      {
         throw new SaveDataIndexException(e.getLocalizedMessage(), e);
      }
      finally
      {
         try
         {
            writer.close();
         }
         catch (IOException e)
         {
            throw new SaveDataIndexException(e.getLocalizedMessage(), e);
         }
      }
   }

   /**
    * @param artifact
    * @param indexType
    * @param dataField
    * @param writer
    * @param pack
    * @throws CorruptIndexException
    * @throws IOException
    */
   private void removeDoc(String artifact, IndexType indexType, String dataField, IndexWriter writer, String key)
      throws CorruptIndexException, IOException
   {
      Query from = indexType.getQuery();
      TermQuery termQuery = new TermQuery(new Term(DataIndexFields.ARTIFACT, artifact));
      BooleanQuery query = new BooleanQuery();
      query.add(from, Occur.MUST);
      query.add(termQuery, Occur.MUST);
      if (key != null && !key.isEmpty())
        query.add(new PrefixQuery(new Term(dataField, key)), Occur.MUST);
      writer.deleteDocuments(query);
   }
   
   /**
    * @param artifact
    * @param indexType
    * @param dataField
    * @param writer
    * @param pack
    * @throws CorruptIndexException
    * @throws IOException
    */
   private void removeDoc(String artifact, IndexType indexType, String dataField, IndexWriter writer)
      throws CorruptIndexException, IOException
   {
      removeDoc(artifact, indexType, dataField, writer, null);
   }

}
