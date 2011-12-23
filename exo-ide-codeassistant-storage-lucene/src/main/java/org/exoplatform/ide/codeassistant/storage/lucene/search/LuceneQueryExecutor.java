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

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.exoplatform.ide.codeassistant.jvm.CodeAssistantException;
import org.exoplatform.ide.codeassistant.storage.lucene.IndexType;
import org.exoplatform.ide.codeassistant.storage.lucene.LuceneInfoStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class LuceneQueryExecutor
{
   private static final Logger LOG = LoggerFactory.getLogger(LuceneQueryExecutor.class);

   private final LuceneInfoStorage infoStorage;

   public LuceneQueryExecutor(LuceneInfoStorage infoStorage)
   {
      this.infoStorage = infoStorage;
   }

   /**
    * 
    * @param select
    * @param from
    * @param where
    * @param limit
    * @param offset
    * @return
    * @throws CodeAssistantException
    */
   public <T> List<T> executeQuery(ContentExtractor<T> select, IndexType from, LuceneSearchConstraint where, int limit,
      int offset) throws CodeAssistantException
   {
      try
      {
         IndexSearcher searcher = infoStorage.getTypeInfoIndexSearcher();
         TopScoreDocCollector collector = TopScoreDocCollector.create(limit + offset, true);

         Query contentQuery = from.getQuery();
         if (!where.matchAll())
         {
            BooleanQuery booleanQuery = new BooleanQuery();
            booleanQuery.add(contentQuery, BooleanClause.Occur.MUST);
            booleanQuery.add(where.getQuery(), BooleanClause.Occur.MUST);
            contentQuery = booleanQuery;
         }

         searcher.search(contentQuery, collector);
         TopDocs docs = collector.topDocs();

         List<T> result = new ArrayList<T>(Math.max(0, docs.totalHits - offset));

         for (int i = offset; i < docs.scoreDocs.length; i++)
         {
            result.add(select.getValue(searcher.getIndexReader(), docs.scoreDocs[i].doc));
         }
         return result;

      }
      catch (IOException e)
      {
         LOG.error(e.getLocalizedMessage(), e);
         throw new CodeAssistantException(404, e.getLocalizedMessage());
      }

   }

}
