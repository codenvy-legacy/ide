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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.junit.Test;

/**
 *
 */
public class FieldPrefixSearchConstraintTest
{
   @Test
   public void shouldCreateInstanceFromStaticMethod() throws Exception
   {
      LuceneSearchConstraint constraint = prefix("test", "test");
      assertTrue(constraint instanceof FieldPrefixSearchConstraint);
   }

   @Test
   public void shouldMatchAllIfPrefixNull() throws Exception
   {
      LuceneSearchConstraint constraint = prefix("test", null);
      assertTrue(constraint.matchAll());
   }

   @Test
   public void shouldMatchAllIfPrefixEmptyString() throws Exception
   {
      LuceneSearchConstraint constraint = prefix("test", "");
      assertTrue(constraint.matchAll());
   }

   @Test
   public void shouldNotMatchAllIfPrefixNotEmptyString() throws Exception
   {
      LuceneSearchConstraint constraint = prefix("test", "somePrefix");
      assertFalse(constraint.matchAll());
   }

   @Test
   public void shouldConstructPrefixedQuery() throws Exception
   {
      LuceneSearchConstraint constraint = prefix("test", "somePrefix");
      Query query = constraint.getQuery();
      assertTrue(query instanceof PrefixQuery);
   }

   @Test
   public void prefixQueryShouldMachArguments() throws Exception
   {
      LuceneSearchConstraint constraint = prefix("test", "somePrefix");
      PrefixQuery query = (PrefixQuery)constraint.getQuery();
      assertEquals("test", query.getPrefix().field());
      assertEquals("somePrefix", query.getPrefix().text());
   }

}
