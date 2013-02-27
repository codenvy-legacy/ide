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

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;
import org.exoplatform.ide.vfs.server.LazyIterator;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.io.IOException;

/**
 * Query result. Method {@link #next()} return path of matched item on virtual filesystem.
 * <p/>
 * NOTE: important to call {@link #close()} method when get results.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class QueryResult extends LazyIterator<String>
{
   private final static Log LOG = ExoLogger.getLogger(QueryResult.class);
   private final TopDocs hits;
   private final Searcher searcher;
   private final IndexSearcher luceneSearcher;

   private int index = 0;
   private boolean closed;

   QueryResult(TopDocs hits, Searcher searcher, IndexSearcher luceneSearcher)
   {
      this.hits = hits;
      this.searcher = searcher;
      this.luceneSearcher = luceneSearcher;
      fetchNext();
   }

   @Override
   protected void fetchNext()
   {
      next = null;
      if (!closed)
      {
         if (index < hits.scoreDocs.length)
         {
            try
            {
               next = luceneSearcher.doc(hits.scoreDocs[index++].doc).getField("path").stringValue();
            }
            catch (IOException e)
            {
               LOG.error(e.getMessage(), e);
            }
         }
      }
   }

   public void close()
   {
      if (!closed)
      {
         try
         {
            searcher.releaseLuceneSearcher(luceneSearcher);
         }
         catch (IOException e)
         {
            LOG.error(e.getMessage(), e);
         }
         closed = true;
      }
   }

   @Override
   public int size()
   {
      return hits.totalHits;
   }
}
