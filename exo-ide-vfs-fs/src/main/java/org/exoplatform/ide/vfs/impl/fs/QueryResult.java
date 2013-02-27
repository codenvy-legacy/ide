/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package org.exoplatform.ide.vfs.impl.fs;

import org.apache.lucene.search.TopDocs;
import org.exoplatform.ide.vfs.server.LazyIterator;

/**
* @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
* @version $Id: $
*/
class QueryResult extends LazyIterator<String>
{
   final TopDocs hits;
   int currentIndex = 0;

   QueryResult(TopDocs hits)
   {
      this.hits = hits;
      fetchNext();
   }

   @Override
   protected void fetchNext()
   {
      if (currentIndex<hits.scoreDocs.length)
                      next=hits.scoreDocs[currentIndex++];
   }

   @Override
   public int size()
   {
      return hits.totalHits;
   }
}
