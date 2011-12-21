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

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.io.File;
import java.io.IOException;

/**
 * Container component responsible for extracting class information from jars
 * specified in configuration
 */
public class LuceneInfoStorage
{

   private static final Log LOG = ExoLogger.getLogger(LuceneInfoStorage.class);


   private final Directory typeInfoIndexDirectory;

   private IndexReader typeInfoIndexReader;

   private IndexSearcher typeInfoIndexSearcher;

   /**
    *  Create file based lucene storage.
    * 
    * @throws IOException
    */
   public LuceneInfoStorage(String storagePath) throws IOException
   {
      this(NIOFSDirectory.open(new File(storagePath)));
   }

   /**
    * Create lucene info storage on the given directory.
    * 
    * @throws IOException
    */
   public LuceneInfoStorage(Directory typeInfoIndexDirectory) throws IOException
   {
      this.typeInfoIndexDirectory = typeInfoIndexDirectory;
   }


   public Directory getTypeInfoIndexDirectory() throws IOException
   {
      return typeInfoIndexDirectory;
   }

   /**
    * Close all open resources.
    */
   public void closeIndexes()
   {
      try
      {
         if (typeInfoIndexReader != null)
         {
            typeInfoIndexReader.close();
         }
         typeInfoIndexDirectory.close();
      }
      catch (IOException e)
      {
         LOG.error(e.getLocalizedMessage(), e);
      }
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
         typeInfoIndexReader = IndexReader.open(typeInfoIndexDirectory, true);
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


   public IndexSearcher getTypeInfoIndexSearcher() throws IOException
   {
      reopenReaderWhenNeed();
      return typeInfoIndexSearcher;
   }
}
