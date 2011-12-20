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
import org.exoplatform.container.configuration.ConfigurationException;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.ide.codeassistant.asm.JarParser;
import org.exoplatform.ide.codeassistant.jvm.TypeInfo;
import org.exoplatform.ide.codeassistant.storage.lucene.writer.LuceneTypeInfoWriter;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.picocontainer.Startable;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Container component responsible for extracting class information from jars
 * specified in configuration
 */
public class FsLuceneInfoStorage implements Startable, LuceneInfoStorage
{

   private static final Log LOG = ExoLogger.getLogger(FsLuceneInfoStorage.class);

   private static final String JARS_PARAM_NAME = "jars";

   private static final String STORAGE_PATH_NAME = "storage-path";

   private static List<String> extractJarNames(InitParams initParams) throws ConfigurationException
   {

      ValueParam jarsParamValue = initParams.getValueParam(JARS_PARAM_NAME);
      if (jarsParamValue == null)
      {
         throw new ConfigurationException();
      }
      String[] jars = jarsParamValue.getValue().split(",");
      return Arrays.asList(jars);
   }

   private static String extractStoragePath(InitParams initParams) throws ConfigurationException
   {
      ValueParam storagePathParamValue = initParams.getValueParam(STORAGE_PATH_NAME);
      if (storagePathParamValue == null)
      {
         throw new ConfigurationException();
      }
      return storagePathParamValue.getValue();
   }

   private final List<String> jars;

   private LuceneTypeInfoWriter typeInfoIndexWriter;

   private final Directory typeInfoIndexDirectory;

   private IndexReader typeInfoIndexReader;

   private IndexSearcher typeInfoIndexSearcher;

   public FsLuceneInfoStorage(InitParams initParams) throws ConfigurationException, IOException
   {
      this(extractJarNames(initParams), extractStoragePath(initParams));
   }

   /**
    * Now it will be used for test purposes
    * 
    * @throws IOException
    */
   private FsLuceneInfoStorage(List<String> jars, String storagePath) throws IOException
   {
      this.jars = jars;
      this.typeInfoIndexDirectory = NIOFSDirectory.open(new File(storagePath));
   }

   /**
    * @see org.exoplatform.ide.codeassistant.storage.lucene.LuceneInfoStorage#getTypeInfoIndexDirectory()
    */
   @Override
   public Directory getTypeInfoIndexDirectory() throws IOException
   {
      return typeInfoIndexDirectory;
   }

   /**
    * @see org.picocontainer.Startable#start()
    */
   @Override
   public void start()
   {
      try
      {
         typeInfoIndexWriter = new LuceneTypeInfoWriter(this);
         extractJars();
      }
      catch (SaveTypeInfoIndexException e)
      {
         throw new RuntimeException(e);
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   /**
    * @see org.picocontainer.Startable#stop()
    */
   @Override
   public void stop()
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

   private void extractJars() throws IOException, SaveTypeInfoIndexException
   {
      for (String jar : jars)
      {
         List<TypeInfo> typeInfos = JarParser.parse(new File(jar));
         typeInfoIndexWriter.addTypeInfo(typeInfos);
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
