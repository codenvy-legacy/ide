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

import static org.exoplatform.ide.codeassistant.asm.ClassParser.getClassFile;
import static org.exoplatform.ide.codeassistant.asm.ClassParser.parse;
import static org.junit.Assert.assertEquals;
import static test.ClassManager.createIndexForClass;
import static test.ClassManager.getAllTestClasses;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.RAMDirectory;
import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;
import org.exoplatform.ide.codeassistant.storage.lucene.LuceneInfoStorage;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class LuceneTypeInfoWriterTest
{
   private LuceneTypeInfoWriter writer;

   private LuceneInfoStorage luceneInfoStorage;

   @Before
   public void createIndex() throws Exception
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
   public void shouldBeAbleToAddTwice() throws Exception
   {
      writer.addTypeInfo(Arrays.asList(new TypeInfo[]{parse(getClassFile(Object.class))}));
      IndexReader reader = luceneInfoStorage.getTypeInfoIndexSearcher().getIndexReader();
      assertEquals(1, reader.numDocs());
      writer.addTypeInfo(Arrays.asList(new TypeInfo[]{parse(getClassFile(List.class))}));
      reader = luceneInfoStorage.getTypeInfoIndexSearcher().getIndexReader();
      assertEquals(2, reader.numDocs());

   }
}
