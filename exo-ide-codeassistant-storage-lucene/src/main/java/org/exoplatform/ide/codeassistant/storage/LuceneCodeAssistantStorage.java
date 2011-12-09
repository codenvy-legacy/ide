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
package org.exoplatform.ide.codeassistant.storage;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.everrest.core.impl.provider.json.JsonException;
import org.everrest.core.impl.provider.json.JsonParser;
import org.everrest.core.impl.provider.json.JsonValue;
import org.everrest.core.impl.provider.json.ObjectBuilder;
import org.exoplatform.ide.codeassistant.jvm.CodeAssistantException;
import org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage;
import org.exoplatform.ide.codeassistant.jvm.JavaType;
import org.exoplatform.ide.codeassistant.jvm.ShortTypeInfo;
import org.exoplatform.ide.codeassistant.jvm.TypeInfo;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of CodeAssistantStorage based on Lucene
 */
public class LuceneCodeAssistantStorage implements CodeAssistantStorage
{
   private static final Log LOG = ExoLogger.getLogger(LuceneCodeAssistantStorage.class.getName());

   private final String indexDirPath;

   public LuceneCodeAssistantStorage(String indexDirPath)
   {
      this.indexDirPath = indexDirPath;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage#getTypeByFqn(java.lang.String)
    */
   @Override
   public TypeInfo getTypeByFqn(String fqn) throws CodeAssistantException
   {
      try
      {
         Document document = searchByTerm(TypeInfoIndexFields.FQN, fqn);
         byte[] jsonField = document.getBinaryValue(TypeInfoIndexFields.TYPE_INFO_JSON);
         return createTypeInfo(jsonField);
      }
      catch (CorruptIndexException e)
      {
         LOG.error("Error during searching Type by FQN", e);
         throw new CodeAssistantException(404, "");
      }
      catch (IOException e)
      {
         LOG.error("Error during searching Type by FQN", e);
         throw new CodeAssistantException(404, "");
      }
      catch (JsonException e)
      {
         LOG.error("Error during transforming stored in index binary field into TypeInfo object", e);
         throw new CodeAssistantException(404, "");
      }

      //         IndexSearcher searcher = new IndexSearcher(dir, true);
      //         searcher.search(new TermQuery(new Term("fld_fqn", "org.exo..")), results);
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage#getTypesByNamePrefix(java.lang.String)
    */
   @Override
   public List<ShortTypeInfo> getTypesByNamePrefix(String namePrefix) throws CodeAssistantException
   {
      return searchFieldByPrefix(TypeInfoIndexFields.CLASS_NAME, namePrefix);
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage#getTypesByFqnPrefix(java.lang.String)
    */
   @Override
   public List<ShortTypeInfo> getTypesByFqnPrefix(String fqnPrefix) throws CodeAssistantException
   {
      return searchFieldByPrefix(TypeInfoIndexFields.FQN, fqnPrefix);
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage#getAnnotations(java.lang.String)
    */
   @Override
   public List<ShortTypeInfo> getAnnotations(String prefix) throws CodeAssistantException
   {
      return findByType(JavaType.ANNOTATION, prefix);
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage#getIntefaces(java.lang.String)
    */
   @Override
   public List<ShortTypeInfo> getIntefaces(String prefix) throws CodeAssistantException
   {
      return findByType(JavaType.INTERFACE, prefix);
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage#getClasses(java.lang.String)
    */
   @Override
   public List<ShortTypeInfo> getClasses(String prefix) throws CodeAssistantException
   {
      return findByType(JavaType.CLASS, prefix);
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage#getClassJavaDoc(java.lang.String)
    */
   @Override
   public String getClassJavaDoc(String fqn) throws CodeAssistantException
   {
      return null;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage#getMemberJavaDoc(java.lang.String)
    */
   @Override
   public String getMemberJavaDoc(String fqn) throws CodeAssistantException
   {
      return null;
   }

   protected List<ShortTypeInfo> searchFieldByPrefix(String field, String prefix) throws CodeAssistantException
   {
      try
      {
         List<Document> documents = searchByPrefix(field, prefix);

         if (documents.size() == 0)
         {
            return Collections.EMPTY_LIST;
         }

         List<ShortTypeInfo> result = new ArrayList<ShortTypeInfo>();
         for (Document document : documents)
         {
            result.add(createShortTypeInfo(document));
         }
         return result;
      }
      catch (IOException e)
      {
         LOG.error("Error during searching Type by class name prefix", e);
         throw new CodeAssistantException(404, "Can't to find ");
      }
   }

   protected List<ShortTypeInfo> findByType(JavaType type, String prefix)
   {
      return null;
   }

   protected Document searchByTerm(String fieldName, String value) throws CorruptIndexException, IOException
   {
      IndexReader reader = null;
      Directory directory = null;
      try
      {
         directory = getDirectory();

         reader = IndexReader.open(directory, true);
         TermDocs termDocs = reader.termDocs(new Term(fieldName, value));

         int[] docs = new int[1];
         int[] freqs = new int[1];

         int count = termDocs.read(docs, freqs);
         if (count == 1)
         {
            return reader.document(docs[0]);
         }
         else
         {
            return null;
         }
      }
      finally
      {
         closeDirectory(directory);
         if (reader != null)
         {
            reader.close();
         }
      }
   }

   protected List<Document> searchByPrefix(String fieldName, String prefix) throws IOException
   {
      IndexSearcher searcher = null;
      Directory directory = null;
      try
      {
         directory = getDirectory();
         searcher = new IndexSearcher(directory, true);
         TopDocs topDocs = searcher.search(new PrefixQuery(new Term(fieldName, prefix)), Integer.MAX_VALUE);

         List<Document> result = new ArrayList<Document>();
         for (ScoreDoc scoreDoc : topDocs.scoreDocs)
         {
            result.add(searcher.doc(scoreDoc.doc));
         }
         searcher.close();

         return result;
      }
      finally
      {
         closeDirectory(directory);
         if (searcher != null)
         {
            searcher.close();
         }
      }
   }

   protected Directory getDirectory() throws IOException
   {
      return FSDirectory.open(new File(indexDirPath));
   }

   protected void closeDirectory(Directory directory) throws IOException
   {
      directory.close();
   }

   protected ShortTypeInfo createShortTypeInfo(Document document)
   {
      int modifier = Integer.valueOf(document.get(TypeInfoIndexFields.MODIFIERS));
      ShortTypeInfo shortTypeInfo =
         new ShortTypeInfo(modifier, document.get(TypeInfoIndexFields.CLASS_NAME),
            document.get(TypeInfoIndexFields.FQN), document.get(TypeInfoIndexFields.ENTITY_TYPE));
      return shortTypeInfo;
   }

   protected TypeInfo createTypeInfo(byte[] data) throws JsonException
   {
      InputStream io = null;
      try
      {
         io = new ByteArrayInputStream(data);

         JsonParser jsonParser = new JsonParser();
         jsonParser.parse(io);
         JsonValue jsonValue = jsonParser.getJsonObject();
         TypeInfo typeInfo = ObjectBuilder.createObject(TypeInfo.class, jsonValue);

         return typeInfo;
      }
      finally
      {
         if (io != null)
         {
            try
            {
               io.close();
            }
            catch (IOException e)
            {
               LOG.warn("Error on InputStream closing");
            }
         }
      }
   }
}
