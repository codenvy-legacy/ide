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
package org.exoplatform.ide.codeassistant.storage.lucene.search;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.everrest.core.impl.provider.json.JsonException;
import org.everrest.core.impl.provider.json.JsonParser;
import org.everrest.core.impl.provider.json.JsonValue;
import org.everrest.core.impl.provider.json.ObjectBuilder;
import org.exoplatform.ide.codeassistant.jvm.CodeAssistantException;
import org.exoplatform.ide.codeassistant.jvm.JavaType;
import org.exoplatform.ide.codeassistant.jvm.ShortTypeInfo;
import org.exoplatform.ide.codeassistant.jvm.TypeInfo;
import org.exoplatform.ide.codeassistant.storage.lucene.TypeInfoIndexFields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class LuceneTypeInfoSearcher
{

   private static final Logger LOG = LoggerFactory.getLogger(LuceneTypeInfoSearcher.class);

   private IndexReader indexReader;

   public LuceneTypeInfoSearcher(IndexReader indexReader)
   {
      super();
      this.indexReader = indexReader;
   }

   public TypeInfo searchDocumentByTerm(String fieldName, String value) throws CodeAssistantException
   {
      try
      {
         reopenReaderWhenNeed();
         TermDocs termDocs = indexReader.termDocs(new Term(fieldName, value));

         int[] docs = new int[1];
         int[] freqs = new int[1];

         int count = termDocs.read(docs, freqs);
         if (count == 1)
         {

            byte[] jsonField = indexReader.document(docs[0]).getBinaryValue(TypeInfoIndexFields.TYPE_INFO_JSON);
            return createTypeInfoObject(jsonField);

         }
         else
         {
            return null;
         }
      }
      catch (IOException e)
      {
         LOG.error("Error during searching Type by FQN", e);
         throw new CodeAssistantException(404, e.getLocalizedMessage());
      }
      catch (JsonException e)
      {
         throw new CodeAssistantException(404, e.getLocalizedMessage());
      }
   }

   private List<Document> searchDocumentsByFieldPrefix(String fieldName, String prefix) throws CodeAssistantException
   {
      try
      {
         reopenReaderWhenNeed();
         IndexSearcher searcher = new IndexSearcher(indexReader);
         TopDocs topDocs = searcher.search(new PrefixQuery(new Term(fieldName, prefix)), Integer.MAX_VALUE);

         List<Document> result = new ArrayList<Document>();
         for (ScoreDoc scoreDoc : topDocs.scoreDocs)
         {
            result.add(searcher.doc(scoreDoc.doc));
         }
         searcher.close();

         return result;
      }
      catch (IOException e)
      {
         LOG.error(e.getLocalizedMessage(), e);
         throw new CodeAssistantException(404, e.getLocalizedMessage());
      }
   }

   private List<Document> searchDocumentsByType(JavaType type, String prefix) throws CodeAssistantException
   {
      try
      {
         reopenReaderWhenNeed();
         IndexSearcher searcher = new IndexSearcher(indexReader);
         TopDocs topDocs = null;
         TermQuery termQuery = new TermQuery(new Term(TypeInfoIndexFields.ENTITY_TYPE, type.toString()));
         if (prefix != null && !prefix.isEmpty())
         {
            PrefixQuery prefixQuery = new PrefixQuery(new Term(TypeInfoIndexFields.CLASS_NAME, prefix));

            BooleanQuery booleanQuery = new BooleanQuery();
            booleanQuery.add(prefixQuery, BooleanClause.Occur.MUST);
            booleanQuery.add(termQuery, BooleanClause.Occur.MUST);

            topDocs = searcher.search(booleanQuery, Integer.MAX_VALUE);
         }
         else
         {
            topDocs = searcher.search(termQuery, Integer.MAX_VALUE);
         }

         if (topDocs.totalHits == 0)
         {
            return Collections.emptyList();
         }

         List<Document> result = new ArrayList<Document>();
         for (ScoreDoc scoreDoc : topDocs.scoreDocs)
         {
            result.add(searcher.doc(scoreDoc.doc));
         }

         return result;
      }
      catch (IOException e)
      {
         LOG.error(e.getLocalizedMessage(), e);
         throw new CodeAssistantException(404, e.getLocalizedMessage());
      }
   }

   public List<ShortTypeInfo> searchFieldByPrefix(String field, String prefix) throws CodeAssistantException
   {

      List<Document> documents = searchDocumentsByFieldPrefix(field, prefix);
      if (documents.size() == 0)
      {
         return Collections.emptyList();
      }

      List<ShortTypeInfo> result = new ArrayList<ShortTypeInfo>(documents.size());
      for (Document document : documents)
      {
         result.add(createShortTypeInfoObject(document));
      }
      return result;
   }

   public List<ShortTypeInfo> searchJavaTypesByPrefix(JavaType type, String prefix) throws CodeAssistantException
   {
      List<Document> documents = searchDocumentsByType(type, prefix);
      if (documents.size() == 0)
      {
         return Collections.emptyList();
      }

      List<ShortTypeInfo> shortTypeInfos = new ArrayList<ShortTypeInfo>(documents.size());
      for (Document document : documents)
      {
         shortTypeInfos.add(createShortTypeInfoObject(document));
      }
      return shortTypeInfos;
   }

   private ShortTypeInfo createShortTypeInfoObject(Document document)
   {
      int modifier = Integer.valueOf(document.get(TypeInfoIndexFields.MODIFIERS));
      ShortTypeInfo shortTypeInfo =
         new ShortTypeInfo(modifier, document.get(TypeInfoIndexFields.CLASS_NAME),
            document.get(TypeInfoIndexFields.FQN), document.get(TypeInfoIndexFields.ENTITY_TYPE));
      return shortTypeInfo;
   }

   private TypeInfo createTypeInfoObject(byte[] data) throws JsonException
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

   /**
    * Reopen reader if where is some changes in index
    * 
    * @throws CorruptIndexException
    * @throws IOException
    */
   private void reopenReaderWhenNeed() throws CorruptIndexException, IOException
   {

      IndexReader newReader = indexReader.reopen();
      if (newReader != indexReader)
      {
         indexReader.close();
      }
      indexReader = newReader;
   }
}
