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

import org.exoplatform.container.configuration.ConfigurationException;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.picocontainer.Startable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * FsLuceneInfoStorage to be able to configure over eXo configuration
 */
public class ExoFsLuceneInfoStorage extends LuceneInfoStorage implements Startable
{

   private static final Logger LOG = LoggerFactory.getLogger(ExoFsLuceneInfoStorage.class);

   public static final String STORAGE_PATH_NAME = "storage-path";

   /**
    * Extract configuration parameter from InitParams
    * 
    * @param initParams
    * @return
    * @throws ConfigurationException
    */
   private static String extractStoragePath(InitParams initParams) throws ConfigurationException
   {
      ValueParam storagePathParamValue = initParams.getValueParam(STORAGE_PATH_NAME);
      if (storagePathParamValue == null)
      {
         LOG.error("Configuration parameter {} not found", STORAGE_PATH_NAME);
         throw new ConfigurationException("Configuration parameter " + STORAGE_PATH_NAME + " not found");
      }
      return storagePathParamValue.getValue();
   }

   public ExoFsLuceneInfoStorage(InitParams params) throws IOException, ConfigurationException
   {
      super(extractStoragePath(params));
   }

   @Override
   public void start()
   {
   }

   @Override
   public void stop()
   {
      this.closeIndexes();
   }

}
