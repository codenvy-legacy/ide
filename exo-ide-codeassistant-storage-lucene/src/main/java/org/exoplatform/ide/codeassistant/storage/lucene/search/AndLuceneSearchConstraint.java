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
import org.apache.lucene.search.Query;
import org.exoplatform.ide.codeassistant.jvm.CodeAssistantException;

/**
 * Combine with logical AND two constrains.
 */
public class AndLuceneSearchConstraint implements LuceneSearchConstraint
{

   private final LuceneSearchConstraint leftConstraint;

   private final LuceneSearchConstraint rightConstraint;

   public AndLuceneSearchConstraint(LuceneSearchConstraint leftConstraint, LuceneSearchConstraint rightConstraint)
   {
      this.leftConstraint = leftConstraint;
      this.rightConstraint = rightConstraint;

   }

   /**
    * @see org.exoplatform.ide.codeassistant.storage.lucene.search.LuceneSearchConstraint#getQuery()
    */
   @Override
   public Query getQuery() throws CodeAssistantException
   {
      BooleanQuery booleanQuery = new BooleanQuery();
      booleanQuery.add(leftConstraint.getQuery(), BooleanClause.Occur.MUST);
      booleanQuery.add(rightConstraint.getQuery(), BooleanClause.Occur.MUST);
      return booleanQuery;
   }

   /**
    * Combine with logical AND two constrains.
    * 
    * @param leftConstraint
    * @param rightConstraint
    * @return
    */
   public static LuceneSearchConstraint and(LuceneSearchConstraint leftConstraint,
      LuceneSearchConstraint rightConstraint)
   {
      if (leftConstraint.matchAll() && !rightConstraint.matchAll())
      {
         return rightConstraint;
      }
      else if (!leftConstraint.matchAll() && rightConstraint.matchAll())
      {
         return leftConstraint;
      }
      return new AndLuceneSearchConstraint(leftConstraint, rightConstraint);
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
