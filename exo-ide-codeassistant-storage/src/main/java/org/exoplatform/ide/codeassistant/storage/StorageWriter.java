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

import org.exoplatform.ide.codeassistant.storage.api.DataWriter;
import org.exoplatform.ide.codeassistant.storage.api.InfoStorage;
import org.exoplatform.ide.codeassistant.storage.api.WriterTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class StorageWriter implements Runnable
{

   private static final Logger LOG = LoggerFactory.getLogger(StorageWriter.class);

   private BlockingQueue<WriterTask> queue;

   private final InfoStorage infoStorage;

   /**
    * @param queue
    * @param dataWriter
    */
   public StorageWriter(BlockingQueue<WriterTask> queue, InfoStorage infoStorage)
   {
      super();
      this.queue = queue;
      this.infoStorage = infoStorage;
   }

   /**
    * @see java.lang.Runnable#run()
    */
   @Override
   public void run()
   {
      WriterTask task;
      try
      {
         while ((task = queue.take()).getArtifact() != null)
         {
            DataWriter dataWriter;
            try
            {
               dataWriter = infoStorage.getWriter();
            }
            catch (IOException e)
            {
               LOG.error("Can't get Data Writer", e);
               continue;
            }
            String artifact = task.getArtifact().toString();
            String version = task.getArtifact().getVersion();
            try
            {
               if (task.getTypesInfo() != null)
               {
                  if (!infoStorage.isArtifactExist(artifact))
                  {
                     dataWriter.addTypeInfo(task.getTypesInfo(), artifact);
                     dataWriter.addPackages(task.getPackages(), artifact);
                  }
                  else if (version.contains("SNAPSHOT")) // CHeck if it SNAPSHOT version in this case need rewrite index
                  {
                     dataWriter.removeTypeInfo(artifact);
                     dataWriter.removePackages(artifact);
                     dataWriter.addTypeInfo(task.getTypesInfo(), artifact);
                     dataWriter.addPackages(task.getPackages(), artifact);
                  }
               }
               if (task.getJavaDock() != null)
               {
                  if (!infoStorage.isJavaDockForArtifactExist(artifact))
                  {
                     dataWriter.addJavaDocs(task.getJavaDock(), artifact);
                  }
                  else if (version.contains("SNAPSHOT"))
                  {
                     dataWriter.removeJavaDocs(artifact);
                     dataWriter.addJavaDocs(task.getJavaDock(), artifact);
                  }
               }
            }
            catch (Exception e)
            {
               LOG.error("Can't write artifact: " + artifact, e);
            }

         }
      }
      catch (InterruptedException e)
      {
         LOG.error("Writer thread interripted", e);
      }
   }

}
