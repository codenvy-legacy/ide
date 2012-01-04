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

import org.exoplatform.ide.codeassistant.storage.extractors.QDoxJavaDocExtractor;
import org.exoplatform.ide.codeassistant.storage.lucene.writer.LuceneJavaDocWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Writes all classes from received list of jars in lucene storage
 */
public class JavaDocStorageWriter
{

   public static void writeJarsToIndex(String pathToIndex, List<String> sourceJars) throws IOException,
      SaveTypeInfoIndexException
   {
      LuceneInfoStorage luceneInfoStorage = null;
      try
      {
         luceneInfoStorage = new LuceneInfoStorage(pathToIndex);
         LuceneJavaDocWriter writer = new LuceneJavaDocWriter(luceneInfoStorage);

         for (String jar : sourceJars)
         {
            File jarFile = new File(jar);
            Map<String, String> javaDocs = QDoxJavaDocExtractor.extractZip(new FileInputStream(jarFile));
            writer.addJavaDocs(javaDocs);
         }
      }
      finally
      {
         if (luceneInfoStorage != null)
         {
            luceneInfoStorage.closeIndexes();
         }
      }
   }
}
