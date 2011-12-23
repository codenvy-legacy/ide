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
package org.exoplatform.ide.codeassistant.storage.lucene;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

/**
 * Types of indexes.
 */
public enum IndexType {

   JAVA("indexType", "java"), DOC("indexType", "doc");
   private final String indexFieldName;

   private final String indexFieldValue;

   private IndexType(String indexFieldName, String indexFieldValue)
   {
      this.indexFieldName = indexFieldName;
      this.indexFieldValue = indexFieldValue;
   }

   /**
    * @return the indexFieldName
    */
   public String getIndexFieldName()
   {
      return indexFieldName;
   }

   /**
    * @return the indexFieldValue
    */
   public String getIndexFieldValue()
   {
      return indexFieldValue;
   }

   public Query getQuery()
   {
      return new TermQuery(new Term(indexFieldName, indexFieldValue));
   }

}
