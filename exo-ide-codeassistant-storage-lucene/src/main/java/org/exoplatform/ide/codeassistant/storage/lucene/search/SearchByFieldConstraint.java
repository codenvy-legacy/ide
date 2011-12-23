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

import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.exoplatform.ide.codeassistant.jvm.CodeAssistantException;
import org.exoplatform.ide.codeassistant.jvm.JavaType;
import org.exoplatform.ide.codeassistant.storage.lucene.TypeInfoIndexFields;

/**
 * 
 */
public class SearchByFieldConstraint implements LuceneSearchConstraint
{

   private final String filedName;

   private final String value;

   public SearchByFieldConstraint(String filedName, String value)
   {
      super();
      this.filedName = filedName;
      this.value = value;
   }

   /**
    * @throws CodeAssistantException
    * 
    */
   @Override
   public Query getQuery() throws CodeAssistantException
   {
      return new TermQuery(new Term(filedName, value));
   }

   public static SearchByFieldConstraint eq(String fieldName, String value)
   {
      return new SearchByFieldConstraint(fieldName, value);
   }

   public static SearchByFieldConstraint eqJavaType(JavaType type)
   {
      return new SearchByFieldConstraint(TypeInfoIndexFields.ENTITY_TYPE, type.toString());
   }

   /**
    * @see org.exoplatform.ide.codeassistant.storage.lucene.search.LuceneSearchConstraint#matchAll()
    */
   @Override
   public boolean matchAll()
   {
      return false;
   }
}
