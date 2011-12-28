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
package org.exoplatform.ide.codeassistant.storage.lucene.writer;

import static org.junit.Assert.assertEquals;
import static test.ClassManager.createIndexForClass;
import static test.ClassManager.getAllTestClasses;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.RAMDirectory;
import org.exoplatform.ide.codeassistant.asm.ClassParser;
import org.exoplatform.ide.codeassistant.jvm.TypeInfo;
import org.exoplatform.ide.codeassistant.storage.lucene.LuceneInfoStorage;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 */
public class LuceneTypeInfoWriterTest
{
   private static LuceneTypeInfoWriter writer;

   private static LuceneInfoStorage luceneInfoStorage;

   @BeforeClass
   public static void createIndex() throws Exception
   {
      luceneInfoStorage = new LuceneInfoStorage(new RAMDirectory());
      writer = new LuceneTypeInfoWriter(luceneInfoStorage);
   }

   @Test
   public void shouldIndexAllClasses() throws Exception
   {
      createIndexForClass(writer, getAllTestClasses());
      IndexReader reader = luceneInfoStorage.getTypeInfoIndexSearcher().getIndexReader();
      assertEquals(getAllTestClasses().length, reader.numDocs());
      reader.close();
   }

   @Test
   public void shouldBeAbleToIndexObject() throws Exception
   {
      TypeInfo expected = ClassParser.parse(ClassParser.getClassFile(Object.class));

   }

}
