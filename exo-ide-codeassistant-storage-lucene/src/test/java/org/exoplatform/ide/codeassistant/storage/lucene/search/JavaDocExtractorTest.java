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
package org.exoplatform.ide.codeassistant.storage.lucene.search;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldSelector;
import org.apache.lucene.index.IndexReader;
import org.exoplatform.ide.codeassistant.storage.lucene.writer.DataIndexer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class JavaDocExtractorTest
{
   @Mock
   private IndexReader reader;

   private final JavaDocExtractor extractor = new JavaDocExtractor();

   @Test
   public void shouldCallReaderGetDocumentWithSameIdAndFieldSelector() throws Exception
   {

      Document luceneDocument = new Document();
      when(reader.document(anyInt(), (FieldSelector)anyObject())).thenReturn(luceneDocument);

      extractor.getValue(reader, 5);

      verify(reader).document(eq(5), (FieldSelector)anyObject());
      verifyNoMoreInteractions(reader);
   }

   @Test
   public void shouldReconstructJavaDocInfo() throws Exception
   {
      final String fqn = "org.exoplatform.ide.codeassistant.test";
      final String doc = "test javadoc";

      Document luceneDocument = new DataIndexer().createJavaDocDocument(fqn, doc);

      when(reader.document(anyInt(), (FieldSelector)anyObject())).thenReturn(luceneDocument);

      String actualJavaDoc = extractor.getValue(reader, 5);

      assertEquals(doc, actualJavaDoc);
   }

}
