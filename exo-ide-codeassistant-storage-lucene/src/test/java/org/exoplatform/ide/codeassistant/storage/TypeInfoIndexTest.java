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

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.RAMDirectory;
import org.exoplatform.ide.codeassistant.asm.JarParser;
import org.exoplatform.ide.codeassistant.jvm.TypeInfo;
import org.exoplatform.ide.codeassistant.storage.lucene.LuceneInfoStorage;
import org.exoplatform.ide.codeassistant.storage.lucene.writer.LuceneTypeInfoWriter;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.List;

/**
 *
 */
public class TypeInfoIndexTest extends BaseTest
{
   private final static String PATH_TO_INDEX = "target/index";

   private final static int CLASSES_IN_JAR = 9;

   private static LuceneTypeInfoWriter writer;

   private static LuceneInfoStorage luceneInfoStorage;

   @BeforeClass
   public static void setUp() throws Exception
   {
      //String pathToJar = createJarFile("src/test/java/test/*/*", "searchTest");
      generateClassFiles("src/test/resources/test/");
      File jar = generateJarFile("test.jar");
      luceneInfoStorage =  new LuceneInfoStorage(new RAMDirectory());
      writer = new LuceneTypeInfoWriter(luceneInfoStorage);

      List<TypeInfo> typeInfos = JarParser.parse(jar);
      writer.addTypeInfo(typeInfos);
   }

   @Test
   public void testCreatedDocsCount() throws Exception
   {
      IndexReader reader = luceneInfoStorage.getTypeInfoIndexSearcher().getIndexReader();
      assertEquals(CLASSES_IN_JAR, reader.numDocs());
      reader.close();
   }

}
