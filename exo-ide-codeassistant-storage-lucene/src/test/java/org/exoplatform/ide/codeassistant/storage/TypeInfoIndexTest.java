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

import static org.junit.Assert.assertEquals;

import test.ClassManager;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.RAMDirectory;
import org.exoplatform.ide.codeassistant.asm.ClassParser;
import org.exoplatform.ide.codeassistant.jvm.TypeInfo;
import org.exoplatform.ide.codeassistant.storage.lucene.LuceneCodeAssistantStorage;
import org.exoplatform.ide.codeassistant.storage.lucene.LuceneInfoStorage;
import org.exoplatform.ide.codeassistant.storage.lucene.SaveTypeInfoIndexException;
import org.exoplatform.ide.codeassistant.storage.lucene.search.LuceneTypeInfoSearcher;
import org.exoplatform.ide.codeassistant.storage.lucene.writer.LuceneTypeInfoWriter;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class TypeInfoIndexTest
{
   private static LuceneCodeAssistantStorage storage;

   private static LuceneTypeInfoWriter writer;

   private static LuceneInfoStorage luceneInfoStorage;

   @BeforeClass
   public static void createIndex() throws Exception
   {
      luceneInfoStorage = new LuceneInfoStorage(new RAMDirectory());
      writer = new LuceneTypeInfoWriter(luceneInfoStorage);
      storage = new LuceneCodeAssistantStorage(new LuceneTypeInfoSearcher(luceneInfoStorage));

      createIndexForClass(writer, ClassManager.getAllTestClasses());

   }

   @Test
   public void testCreatedDocsCount() throws Exception
   {
      IndexReader reader = luceneInfoStorage.getTypeInfoIndexSearcher().getIndexReader();
      assertEquals(ClassManager.getAllTestClasses().length, reader.numDocs());
      reader.close();
   }

   /**
    * @param className
    *           TODO
    * @throws IOException
    * @throws SaveTypeInfoIndexException
    */
   public static void createIndexForClass(LuceneTypeInfoWriter typeWriter, Class<?>... classesToIndex)
      throws IOException, SaveTypeInfoIndexException
   {
      ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

      List<TypeInfo> typeInfos = new ArrayList<TypeInfo>();

      for (Class<?> classToIndex : classesToIndex)
      {
         String classResource = classToIndex.getName().replace('.', '/') + ".class";
         typeInfos.add(ClassParser.parse(contextClassLoader.getResourceAsStream(classResource)));
      }

      typeWriter.addTypeInfo(typeInfos);
   }
}
