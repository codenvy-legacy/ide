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
   public static final String INDEX_DIRECTORY = "target/code-assistant/index/";

   private static final String JAR_FILES = "src/main/resources/codeassistant/jar-files.txt";

   public static void main(String[] args)
   {
      try
      {
         List<String> jars = getFilesList(JAR_FILES);
         ClassesInfoStorageWriter.writeJarsToIndex(INDEX_DIRECTORY, jars);
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
