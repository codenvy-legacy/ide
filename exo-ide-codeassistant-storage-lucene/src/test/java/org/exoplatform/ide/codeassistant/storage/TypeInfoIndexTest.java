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

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.exoplatform.ide.codeassistant.asm.JarParser;
import org.exoplatform.ide.codeassistant.jvm.TypeInfo;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 *
 */
public class TypeInfoIndexTest extends TestCase
{
   private final static String PATH_TO_JAR = "src/test/resources/test_jar";

   private final static String PATH_TO_INDEX = "target/index";

   private final static int CLASSES_IN_JAR = 6;

   private static TypeInfoIndexWriter writer;

   @Override
   public void setUp() throws Exception
   {
      super.setUp();

      writer = new TypeInfoIndexWriter(PATH_TO_INDEX);

      List<TypeInfo> typeInfos = JarParser.parse(new File(PATH_TO_JAR));
      writer.writeTypeInfo(typeInfos);
      writer.close();
   }

   @Override
   public void tearDown() throws Exception
   {
      FileUtils.deleteDirectory(new File(PATH_TO_INDEX));
      super.tearDown();
   }

   public void testCreatedDocsCount() throws Exception
   {
      IndexReader reader = IndexReader.open(getDirectory(), true);
      assertEquals(CLASSES_IN_JAR, reader.numDocs());
      reader.close();
   }

   private Directory getDirectory() throws IOException
   {
      return FSDirectory.open(new File(PATH_TO_INDEX));
   }
}
