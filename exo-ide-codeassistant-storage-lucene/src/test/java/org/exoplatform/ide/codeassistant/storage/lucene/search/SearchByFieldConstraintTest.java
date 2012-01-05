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

import static org.exoplatform.ide.codeassistant.storage.lucene.search.SearchByFieldConstraint.eq;
import static org.exoplatform.ide.codeassistant.storage.lucene.search.SearchByFieldConstraint.eqJavaType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.exoplatform.ide.codeassistant.jvm.shared.JavaType;
import org.exoplatform.ide.codeassistant.storage.lucene.DataIndexFields;
import org.junit.Test;

/**
 *
 */
public class SearchByFieldConstraintTest
{
   @Test
   public void shouldCreateInstanceFromStaticEqMethod() throws Exception
   {
      LuceneSearchConstraint constraint = eq("test", "test");
      assertTrue(constraint instanceof SearchByFieldConstraint);
   }

   @Test
   public void shouldCreateInstanceFromStaticEqJavaTypeMethod() throws Exception
   {
      LuceneSearchConstraint constraint = eqJavaType(JavaType.ANNOTATION);
      assertTrue(constraint instanceof SearchByFieldConstraint);
   }

   @Test
   public void shouldConstructTermQuery() throws Exception
   {
      LuceneSearchConstraint constraint = eq("test", "somePrefix");
      Query query = constraint.getQuery();
      assertTrue(query instanceof TermQuery);
   }

   @Test
   public void termQueryShouldMachArguments() throws Exception
   {
      LuceneSearchConstraint constraint = eq("test", "somePrefix");
      TermQuery query = (TermQuery)constraint.getQuery();
      assertEquals("test", query.getTerm().field());
      assertEquals("somePrefix", query.getTerm().text());
   }

   @Test
   public void termQueryFromJavaTypeShouldMachArguments() throws Exception
   {
      LuceneSearchConstraint constraint = eqJavaType(JavaType.ANNOTATION);
      TermQuery query = (TermQuery)constraint.getQuery();
      assertEquals(DataIndexFields.ENTITY_TYPE, query.getTerm().field());
      assertEquals(JavaType.ANNOTATION.toString(), query.getTerm().text());
   }

   @Test
   public void shouldNotMatchAll() throws Exception
   {
      LuceneSearchConstraint constraint = eqJavaType(JavaType.ANNOTATION);
      assertFalse(constraint.matchAll());
   }
}
