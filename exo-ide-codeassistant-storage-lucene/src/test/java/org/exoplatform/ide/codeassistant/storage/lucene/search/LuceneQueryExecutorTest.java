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

import static org.exoplatform.ide.codeassistant.storage.lucene.search.FieldPrefixSearchConstraint.prefix;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.exoplatform.ide.codeassistant.jvm.CodeAssistantException;
import org.exoplatform.ide.codeassistant.jvm.shared.ShortTypeInfo;
import org.exoplatform.ide.codeassistant.storage.lucene.IndexType;
import org.exoplatform.ide.codeassistant.storage.lucene.LuceneInfoStorage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.List;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class LuceneQueryExecutorTest
{
   @Mock
   private LuceneInfoStorage luceneInfoStorage;

   @Mock
   private ShortTypeInfoExtractor select;

   @Mock
   private IndexSearcher searcher;

   @InjectMocks
   private LuceneQueryExecutor queryExecutor;

   /**
    * @throws IOException
    * 
    */
   @Before
   public void setUp() throws IOException
   {
      when(luceneInfoStorage.getTypeInfoIndexSearcher()).thenReturn(searcher);
      when(searcher.search(any(Query.class), anyInt())).thenReturn(new TopDocs(0, new ScoreDoc[0], 1));
   }

   @Test
   public void shouldSearchByTypeIfWhereMatchAll() throws Exception
   {
      LuceneSearchConstraint where = prefix("test", "");

      queryExecutor.executeQuery(select, IndexType.JAVA, where, 10, 1);

      verify(searcher).search(eq(IndexType.JAVA.getQuery()), eq(11));
      assertTrue(where.matchAll());
   }

   @Test
   public void shouldComposeQueryIfWherDoesntMatchAll() throws Exception
   {
      LuceneSearchConstraint where = prefix("test", "pref");

      queryExecutor.executeQuery(select, IndexType.JAVA, where, 10, 1);

      ArgumentCaptor<Query> query = ArgumentCaptor.forClass(Query.class);
      verify(searcher).search(query.capture(), eq(11));
      assertTrue(query.getValue() instanceof BooleanQuery);
      BooleanQuery bQuery = (BooleanQuery)query.getValue();

      assertEquals(2, bQuery.getClauses().length);
      assertEquals(IndexType.JAVA.getQuery(), bQuery.getClauses()[0].getQuery());
      assertEquals(Occur.MUST, bQuery.getClauses()[0].getOccur());
      assertEquals(where.getQuery(), bQuery.getClauses()[1].getQuery());
      assertEquals(Occur.MUST, bQuery.getClauses()[1].getOccur());

   }

   @Test
   public void shouldSearchAtLeastLimitPlusOffsetValues() throws Exception
   {
      LuceneSearchConstraint where = prefix("test", "pref");

      queryExecutor.executeQuery(select, IndexType.JAVA, where, 25, 36);
      verify(searcher).search(any(Query.class), eq(25 + 36));
   }

   @Test(expected = CodeAssistantException.class)
   public void shouldNotAllowNegativeOffset() throws Exception
   {
      LuceneSearchConstraint where = prefix("test", "pref");

      queryExecutor.executeQuery(select, IndexType.JAVA, where, 25, -1);
   }

   @Test(expected = CodeAssistantException.class)
   public void shouldNotAllowNegativeLimit() throws Exception
   {
      LuceneSearchConstraint where = prefix("test", "pref");

      queryExecutor.executeQuery(select, IndexType.JAVA, where, -1, 55);
   }

   @Test
   public void shouldReturntResultsWithOffset() throws Exception
   {
      LuceneSearchConstraint where = prefix("test", "pref");

      when(searcher.search(any(Query.class), anyInt())).thenReturn(
         new TopDocs(3, new ScoreDoc[]{new ScoreDoc(1, (float)0.1), new ScoreDoc(2, (float)0.2),
            new ScoreDoc(3, (float)0.3)}, (float)0.3));

      List<ShortTypeInfo> actual = queryExecutor.executeQuery(select, IndexType.JAVA, where, 3, 1);

      assertEquals(2, actual.size());

      InOrder inOrder = inOrder(select);
      inOrder.verify(select).getValue(any(IndexReader.class), eq(2));
      inOrder.verify(select).getValue(any(IndexReader.class), eq(3));
      verifyNoMoreInteractions(select);

   }

   @Test
   public void shouldReThrow404CodeAssistantExceptionWhenIoExceptionOccur() throws IOException
   {
      when(luceneInfoStorage.getTypeInfoIndexSearcher()).thenThrow(new IOException());
      LuceneSearchConstraint where = prefix("test", "pref");

      try
      {
         queryExecutor.executeQuery(select, IndexType.JAVA, where, 2, 0);
      }
      catch (CodeAssistantException e)
      {
         assertEquals(404, e.getStatus());
      }

   }
}
