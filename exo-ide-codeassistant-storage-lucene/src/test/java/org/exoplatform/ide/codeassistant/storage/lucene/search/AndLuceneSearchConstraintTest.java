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

import static org.exoplatform.ide.codeassistant.storage.lucene.search.AndLuceneSearchConstraint.and;
import static org.exoplatform.ide.codeassistant.storage.lucene.search.FieldPrefixSearchConstraint.prefix;
import static org.exoplatform.ide.codeassistant.storage.lucene.search.SearchByFieldConstraint.eq;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.junit.Test;

/**
 *
 */
public class AndLuceneSearchConstraintTest
{

   @Test
   public void shouldCreateInstanceFromStaticMethodIfBothDoestMatchAll() throws Exception
   {
      LuceneSearchConstraint constraint = and(prefix("test", "test"), eq("test", "somePrefix"));
      assertTrue(constraint instanceof AndLuceneSearchConstraint);
   }

   @Test
   public void shouldReturnLeftIfRightMatchAll() throws Exception
   {
      SearchByFieldConstraint left = eq("test", "somePrefix");
      LuceneSearchConstraint right = prefix("test", "");
      assertTrue(right.matchAll());
      assertFalse(left.matchAll());

      LuceneSearchConstraint constraint = and(left, right);

      assertEquals(left, constraint);
   }

   @Test
   public void shouldReturnRightIfLeftMatchAll() throws Exception
   {
      SearchByFieldConstraint right = eq("test", "somePrefix");
      LuceneSearchConstraint left = prefix("test", "");

      assertFalse(right.matchAll());
      assertTrue(left.matchAll());

      LuceneSearchConstraint constraint = and(left, right);

      assertEquals(right, constraint);
   }

   @Test
   public void shouldCreateReturnBooleanQuery() throws Exception
   {
      LuceneSearchConstraint constraint = and(prefix("test", "test"), eq("test", "somePrefix"));
      assertTrue(constraint.getQuery() instanceof BooleanQuery);
   }

   @Test
   public void boolenaQueryShouldContainsTwoClauses() throws Exception
   {
      LuceneSearchConstraint constraint = and(prefix("test", "test"), eq("test", "somePrefix"));
      BooleanQuery query = (BooleanQuery)constraint.getQuery();
      assertEquals(2, query.getClauses().length);
   }

   @Test
   public void boolenaQueryShouldMatchBothConstrains() throws Exception
   {
      LuceneSearchConstraint left = prefix("test", "test");
      SearchByFieldConstraint right = eq("test", "somePrefix");
      LuceneSearchConstraint constraint = and(left, right);
      BooleanQuery query = (BooleanQuery)constraint.getQuery();
      BooleanClause[] clauses = query.getClauses();

      assertEquals(left.getQuery(), clauses[0].getQuery());
      assertEquals(right.getQuery(), clauses[1].getQuery());
   }

   @Test
   public void occurShouldBeBust() throws Exception
   {
      LuceneSearchConstraint left = prefix("test", "test");
      SearchByFieldConstraint right = eq("test", "somePrefix");
      LuceneSearchConstraint constraint = and(left, right);
      BooleanQuery query = (BooleanQuery)constraint.getQuery();
      BooleanClause[] clauses = query.getClauses();

      assertEquals(Occur.MUST, clauses[0].getOccur());
      assertEquals(Occur.MUST, clauses[1].getOccur());
   }
}
