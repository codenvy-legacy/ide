/*
 * Copyright (C) 2012 eXo Platform SAS.
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

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.RAMDirectory;
import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;
import org.exoplatform.ide.codeassistant.storage.lucene.LuceneInfoStorage;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

/**
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: LuceneTypeInfoRemoveTest.java Oct 24, 2012 vetal $
 *
 */
public class LuceneRemoveInfoTest
{

   private LuceneDataWriter writer;

   private LuceneInfoStorage luceneInfoStorage;

   private IndexWriter indexWriter;

   RAMDirectory indexDirectory;

   @Before
   public void createIndex() throws Exception
   {
      indexDirectory = new RAMDirectory();
      luceneInfoStorage = new LuceneInfoStorage(indexDirectory);
      writer = new LuceneDataWriter(luceneInfoStorage);
      indexWriter = new IndexWriter(indexDirectory, new SimpleAnalyzer(), IndexWriter.MaxFieldLength.UNLIMITED);
   }

   @Test
   public void removeTypeInfoTest() throws Exception
   {
      //full index
      List<TypeInfo> typeInfos = Arrays.asList(new TypeInfo[]{parse(getClassFile(Object.class))});
      DataIndexer indexer = new DataIndexer();
      for (TypeInfo typeInfo : typeInfos)
      {
         indexWriter.addDocument(indexer.createTypeInfoDocument(typeInfo, "rt"));
      }
      indexWriter.commit();
      indexWriter.close();

      //insure that document add
      IndexReader reader = IndexReader.open(indexDirectory, true);
      assertEquals(1, reader.numDocs());

      writer.removeTypeInfo("rt");

      reader = IndexReader.open(indexDirectory, true);
      assertEquals(0, reader.numDocs());

   }
   
   
   @Test
   public void removePackageTest() throws Exception
   {
      //full index
      DataIndexer indexer = new DataIndexer();
      TreeSet<String> packages = new TreeSet<String>(Arrays.asList("java", "java.lang","org","org.exoplatform","org.exoplatform.ide"));
      for (String pack : packages)
      {
         indexWriter.addDocument(indexer.createPackageDocument(pack, "rt"));
      }
      indexWriter.commit();
      indexWriter.close();

      //insure that document add
      IndexReader reader = IndexReader.open(indexDirectory, true);
      assertEquals(5, reader.numDocs());

      writer.removePackages("rt");

      reader = IndexReader.open(indexDirectory, true);
      assertEquals(0, reader.numDocs());

   }

}
