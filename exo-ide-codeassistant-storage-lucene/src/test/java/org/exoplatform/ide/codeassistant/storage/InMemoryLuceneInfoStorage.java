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
package org.exoplatform.ide.codeassistant.storage;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.exoplatform.ide.codeassistant.storage.lucene.LuceneInfoStorage;

import java.io.IOException;

/**
 * In memory storage manager
 */
public class InMemoryLuceneInfoStorage implements LuceneInfoStorage
{
   private final Directory directory;

   private IndexReader typeInfoIndexReader;

   private IndexSearcher typeInfoIndexSearcher;

   public InMemoryLuceneInfoStorage()
   {
      super();
      this.directory = new RAMDirectory();
   }

   /**
    * @see org.exoplatform.ide.codeassistant.storage.lucene.LuceneInfoStorage#getTypeInfoIndexDirectory()
    */
   @Override
   public Directory getTypeInfoIndexDirectory() throws IOException
   {
      return directory;
   }

   /**
    * Reopen reader if where is some changes in index
    * 
    * @throws CorruptIndexException
    * @throws IOException
    */
   private void reopenReaderWhenNeed() throws IOException
   {
      if (typeInfoIndexReader == null)
      {
         typeInfoIndexReader = IndexReader.open(directory, true);
         typeInfoIndexSearcher = new IndexSearcher(typeInfoIndexReader);
      }
      else
      {
         IndexReader newReader = typeInfoIndexReader.reopen(true);
         if (newReader != typeInfoIndexReader)
         {
            typeInfoIndexReader.close();
            typeInfoIndexSearcher = new IndexSearcher(newReader);
         }
         typeInfoIndexReader = newReader;
      }
   }

   /**
    * @see org.exoplatform.ide.codeassistant.storage.lucene.LuceneInfoStorage#getTypeInfoIndexSearcher()
    */
   @Override
   public IndexSearcher getTypeInfoIndexSearcher() throws IOException
   {
      reopenReaderWhenNeed();
      return typeInfoIndexSearcher;
   }

}
