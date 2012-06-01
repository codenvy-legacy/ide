/*
 * Copyright (C) 2012 eXo Platform SAS.
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

import static org.exoplatform.ide.codeassistant.storage.lucene.search.SearchByFieldConstraint.eq;

import org.exoplatform.ide.codeassistant.jvm.CodeAssistantException;
import org.exoplatform.ide.codeassistant.storage.api.DataWriter;
import org.exoplatform.ide.codeassistant.storage.api.InfoStorage;
import org.exoplatform.ide.codeassistant.storage.lucene.IndexType;
import org.exoplatform.ide.codeassistant.storage.lucene.LuceneInfoStorage;
import org.exoplatform.ide.codeassistant.storage.lucene.search.ArtifactExtractor;
import org.exoplatform.ide.codeassistant.storage.lucene.search.LuceneQueryExecutor;
import org.exoplatform.ide.codeassistant.storage.lucene.writer.LuceneDataWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class LocalInfoStorage implements InfoStorage
{

   private static final Logger LOG = LoggerFactory.getLogger(LocalInfoStorage.class);

   private LuceneInfoStorage infoStorage;

   private final LuceneQueryExecutor queryExecutor;

   /**
    * @param infoStorage
    */
   public LocalInfoStorage(LuceneInfoStorage infoStorage)
   {
      super();
      this.infoStorage = infoStorage;
      queryExecutor = new LuceneQueryExecutor(infoStorage);
   }

   /**
    * @throws IOException 
    * @see org.exoplatform.ide.codeassistant.storage.api.InfoStorage#getWriter()
    */
   @Override
   public DataWriter getWriter() throws IOException
   {
      return new LocalDataWriter(new LuceneDataWriter(infoStorage));
   }

   /**
    * @see org.exoplatform.ide.codeassistant.storage.api.InfoStorage#isArtifactExist(java.lang.String)
    */
   @Override
   public boolean isArtifactExist(String artifact)
   {
      try
      {
         List<String> artifacts =
            queryExecutor.executeQuery(new ArtifactExtractor(), IndexType.PACKAGE, eq("artifact", artifact), 100, 0);
         return (artifacts != null && !artifacts.isEmpty());
      }
      catch (CodeAssistantException e)
      {
         if (LOG.isDebugEnabled())
            LOG.error(e.getMessage(), e);
      }
      return false;
   }

}
