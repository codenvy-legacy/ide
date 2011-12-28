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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class CodeAssistantStorageGenerator
{
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
      if (args.length >= 1)
      {
         indexDirectory = args[0];
      }
      if (args.length == 2)
      {
         jarFilesList = args[1];
      }
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
         list.add(nextLine);
      }

      return list;
   }

}
