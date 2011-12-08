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

import org.exoplatform.container.configuration.ConfigurationException;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.ide.codeassistant.asm.JarParser;
import org.exoplatform.ide.codeassistant.jvm.TypeInfo;
import org.picocontainer.Startable;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Container component responsible for extracting class information from jars
 * specified in configuration
 */
public class LuceneInfoStorage implements Startable
{
   private static final String JARS_PARAM_NAME = "jars";

   private static final String STORAGE_PATH_NAME = "storage-path";

   private final List<String> jars;

   private final String storagePath;

   private TypeInfoIndexWriter typeInfoIndexWriter;

   public LuceneInfoStorage(InitParams initParams) throws ConfigurationException
   {
      ValueParam jarsParamValue = initParams.getValueParam(JARS_PARAM_NAME);
      if (jarsParamValue == null)
      {
         throw new ConfigurationException();
      }
      jars = extractJarNames(jarsParamValue.getValue());

      ValueParam storagePathParamValue = initParams.getValueParam(STORAGE_PATH_NAME);
      if (storagePathParamValue == null)
      {
         throw new ConfigurationException();
      }
      storagePath = storagePathParamValue.getValue();
   }

   /**
    * Now it will be used for test purposes
    */
   public LuceneInfoStorage(List<String> jars, String storagePath)
   {
      this.jars = jars;
      this.storagePath = storagePath;
   }

   /**
    * @see org.picocontainer.Startable#start()
    */
   @Override
   public void start()
   {
      try
      {
         typeInfoIndexWriter = new TypeInfoIndexWriter(storagePath);
         extractJars();
         typeInfoIndexWriter.close();
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

   }

   private void extractJars() throws IOException, SaveTypeInfoIndexException
   {
      for (String jar : jars)
      {
         List<TypeInfo> typeInfos = JarParser.parse(new File(jar));
         typeInfoIndexWriter.writeTypeInfo(typeInfos);
      }
   }

   private List<String> extractJarNames(String conf)
   {
      String[] jars = conf.split(",");
      return Arrays.asList(jars);
   }

}
