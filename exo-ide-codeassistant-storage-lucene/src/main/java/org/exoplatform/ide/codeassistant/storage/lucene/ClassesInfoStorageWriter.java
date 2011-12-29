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

import org.exoplatform.ide.codeassistant.asm.JarParser;
import org.exoplatform.ide.codeassistant.jvm.TypeInfo;
import org.exoplatform.ide.codeassistant.storage.lucene.writer.LuceneTypeInfoWriter;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Writes all classes from received list of jars in lucene storage
 */
public class ClassesInfoStorageWriter
{
   public static void writeJarsToIndex(String pathToIndex, List<String> jars)
   {
      LuceneInfoStorage luceneInfoStorage = null;
      try
      {
         luceneInfoStorage = new LuceneInfoStorage(pathToIndex);
         LuceneTypeInfoWriter writer = new LuceneTypeInfoWriter(luceneInfoStorage);

         for (String jar : jars)
         {
            File jarFile = new File(jar);
            List<TypeInfo> typeInfos = JarParser.parse(jarFile);
            writer.addTypeInfo(typeInfos);
         }
      }
      catch (IOException e)
      {
         throw new RuntimeException(e.getLocalizedMessage(), e);
      }
      catch (SaveTypeInfoIndexException e)
      {
         throw new RuntimeException("Can't to store data in index", e);
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
