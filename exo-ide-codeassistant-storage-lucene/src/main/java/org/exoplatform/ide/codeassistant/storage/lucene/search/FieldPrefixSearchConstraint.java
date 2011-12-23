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
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.exoplatform.ide.codeassistant.jvm.CodeAssistantException;

/**
 * 
 */
public class FieldPrefixSearchConstraint implements LuceneSearchConstraint
{

   private final String filedName;

   private final String prefix;

   public FieldPrefixSearchConstraint(String filedName, String prefix)
   {
      super();
      this.filedName = filedName;
      this.prefix = prefix;
   }

   /**
    * @throws CodeAssistantException
    * 
    */
   @Override
   public Query getQuery() throws CodeAssistantException
   {
      return new PrefixQuery(new Term(filedName, prefix));
   }

   public static FieldPrefixSearchConstraint prefix(String fieldName, String prefix)
   {
      return new FieldPrefixSearchConstraint(fieldName, prefix);
   }

   /**
    * @see org.exoplatform.ide.codeassistant.storage.lucene.search.LuceneSearchConstraint#matchAll()
    */
   @Override
   public boolean matchAll()
   {
      return prefix == null || prefix.length() < 1;
   }
}
