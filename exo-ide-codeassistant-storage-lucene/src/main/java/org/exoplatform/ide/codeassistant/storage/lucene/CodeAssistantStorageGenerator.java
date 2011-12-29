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

import org.exoplatform.container.xml.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
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

   private static final String DEFAULT_INDEX_DIRECTORY = "code-assistant/index/";

   private static final String DEFAULT_JAR_FILES_LIST = "src/main/resources/codeassistant/jar-files.txt";

   private static String indexDirectory = DEFAULT_INDEX_DIRECTORY;

   private static String jarFilesList = DEFAULT_JAR_FILES_LIST;

   public static void main(String[] args)
   {
      resolveArgs(args);
      writeClassInfosInStorage();
   }

   private static void resolveArgs(String[] args)
   {
      if (args.length == 0)
      {
         LOG.info("Arguments list wasn't specified, will be used default values");
      }
      else if (args.length >= 1)
      {
         indexDirectory = args[0];
      }
      if (args.length == 2)
      {
         jarFilesList = args[1];
      }

      LOG.info("Index will be created in " + indexDirectory + " directory\n");
      LOG.info("Jar files list will be read from " + jarFilesList + " file\n");
   }

   private static void writeClassInfosInStorage()
   {
      try
      {
         List<String> jars = getFilesList(jarFilesList);
         ClassesInfoStorageWriter.writeJarsToIndex(indexDirectory, jars);
      }
      catch (IOException e)
      {
         throw new RuntimeException(e.getLocalizedMessage(), e);
      }
   }

   private static List<String> getFilesList(String pathToFile) throws IOException
   {
      Reader reader = new FileReader(new File(pathToFile));
      BufferedReader br = new BufferedReader(reader);

      List<String> list = new ArrayList<String>();
      String nextLine = null;
      while ((nextLine = br.readLine()) != null)
      {
         String pathToJar = Deserializer.resolveVariables(nextLine);
         list.add(pathToJar);
      }

      return list;
   }

}
