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

import org.everrest.core.impl.provider.json.ArrayValue;
import org.everrest.core.impl.provider.json.JsonException;
import org.everrest.core.impl.provider.json.JsonParser;
import org.everrest.core.impl.provider.json.JsonUtils;
import org.everrest.core.impl.provider.json.JsonValue;
import org.everrest.core.impl.provider.json.ObjectBuilder;
import org.exoplatform.container.xml.Deserializer;
import org.exoplatform.ide.codeassistant.jvm.bean.Dependency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>
 * Generates code assistant storage based on Lucene index. In argument line may
 * be specified such parameters:
 * </p>
 * </p> <li>path to folder where lucene index will be created (as first
 * parameter); <li>path to file which contains list of jars that have to be
 * included in storage (as second parameter). This file has the following
 * format: path to each jar places in separate line (may have system variables
 * as part of path).</p> *
 * <p>
 * If there arguments were not specified then will be used default values.
 * </p>
 */
public class CodeAssistantStorageGenerator
{
   private static final Logger LOG = LoggerFactory.getLogger(CodeAssistantStorageGenerator.class);

   private static final String DEFAULT_INDEX_DIRECTORY = "ide-codeassistant-lucene-index";

   private static final String DEFAULT_JAR_FILES_LIST = "codeassistant/jar-files.txt";

   private static final String DEFAULT_SOURCE_ARCHIVE_FILES_LIST = "codeassistant/source-jar-files.txt";

   private final DataStorageWriter storageWriter;

   private final String jarFilesList;

   private final String sourceArchiveFilesList;

   public CodeAssistantStorageGenerator(String indexDirectory, String jarFilesList, String sourceArchiveFilesList)
   {
      this.jarFilesList = jarFilesList;
      this.sourceArchiveFilesList = sourceArchiveFilesList;
      this.storageWriter = new DataStorageWriter(indexDirectory);
   }

   private void writeDataToStorage() throws IOException, SaveDataIndexException
   {
      storageWriter.writeBinaryJarsToIndex(getJarFilesList(jarFilesList));
      storageWriter.writeSourceJarsToIndex(getJarFilesList(sourceArchiveFilesList));
   }

   private Artifact[] getJarFilesList(String jarFilesList) throws IOException
   {
      ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
      InputStream io = contextClassLoader.getResourceAsStream(jarFilesList);
      JsonParser p = new JsonParser();
      if (io == null)
         io = new FileInputStream(new File(jarFilesList));

      try
      {
         p.parse(io);
         return (Artifact[])ObjectBuilder.createArray(Artifact[].class, p.getJsonObject());

      }
      catch (JsonException e)
      {
         e.printStackTrace();
      }
      finally
      {
         io.close();
      }
      return null;
   }

   public static void main(String[] args)
   {
      String indexDirectory = DEFAULT_INDEX_DIRECTORY;
      if (args.length == 0)
      {
         LOG.info("Arguments list wasn't specified, will be used default values");
      }
      else if (args.length >= 1)
      {
         indexDirectory = args[0];
      }
      String jarFilesList = DEFAULT_JAR_FILES_LIST;
      if (args.length >= 2)
      {
         jarFilesList = args[1];
      }
      String sourceArchiveFilesList = DEFAULT_SOURCE_ARCHIVE_FILES_LIST;
      if (args.length == 3)
      {
         sourceArchiveFilesList = args[2];
      }

      LOG.info("Index will be created in " + indexDirectory + " directory");
      LOG.info("Jar files list will be read from " + jarFilesList + " file");
      LOG.info("Source archives list will be read from " + sourceArchiveFilesList + " file");

      CodeAssistantStorageGenerator codeAssistantStorageGenerator =
         new CodeAssistantStorageGenerator(indexDirectory, jarFilesList, sourceArchiveFilesList);
      try
      {
         codeAssistantStorageGenerator.writeDataToStorage();
         LOG.info("Index created successfully");
      }
      catch (FileNotFoundException e)
      {
         LOG.error(e.getMessage(), e);
      }
      catch (IOException e)
      {
         LOG.error("Error while writing data to lucene storage!", e);
      }
      catch (SaveDataIndexException e)
      {
         LOG.error("Error while writing data to lucene storage!", e);
      }
   }

}
