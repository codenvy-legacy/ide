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
import static org.mockito.Mockito.when;

import test.classes.ATestClass2;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldSelector;
import org.apache.lucene.document.FieldSelectorResult;
import org.apache.lucene.index.IndexReader;
import org.exoplatform.ide.codeassistant.asm.ClassParser;
import org.exoplatform.ide.codeassistant.jvm.TypeInfo;
import org.exoplatform.ide.codeassistant.storage.lucene.TypeInfoIndexFields;
import org.exoplatform.ide.codeassistant.storage.lucene.writer.TypeInfoIndexer;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class TypeInfoExtractorTest
{
   @Mock
   private IndexReader reader;

   private final TypeInfoExtractor extractor = new TypeInfoExtractor();

   @Ignore
   @Test
   public void shouldReconstructTypeInfo() throws Exception
   {
      TypeInfo expected = ClassParser.parse(ClassParser.getClassFile(ATestClass2.class));
      Document luceneDocument = new TypeInfoIndexer().createDocument(expected);

      when(reader.document(anyInt(), (FieldSelector)anyObject())).thenReturn(luceneDocument);

      TypeInfo actual = extractor.getValue(reader, 5);

      assertEquals(expected, actual);
   }

   @Ignore
   @Test
   public void shouldGetDocumentFromReaderWithPredefinedSetOfFields() throws Exception
   {
      TypeInfo expected = ClassParser.parse(ClassParser.getClassFile(ATestClass2.class));
      Document luceneDocument = new TypeInfoIndexer().createDocument(expected);
      when(reader.document(anyInt(), (FieldSelector)anyObject())).thenReturn(luceneDocument);

      extractor.getValue(reader, 5);

      ArgumentCaptor<FieldSelector> model = ArgumentCaptor.forClass(FieldSelector.class);
      verify(reader).document(eq(5), model.capture());

      assertEquals(FieldSelectorResult.NO_LOAD, model.getValue().accept(TypeInfoIndexFields.MODIFIERS));
      assertEquals(FieldSelectorResult.NO_LOAD, model.getValue().accept(TypeInfoIndexFields.CLASS_NAME));
      assertEquals(FieldSelectorResult.NO_LOAD, model.getValue().accept(TypeInfoIndexFields.FQN));
      assertEquals(FieldSelectorResult.NO_LOAD, model.getValue().accept(TypeInfoIndexFields.ENTITY_TYPE));
      assertEquals(FieldSelectorResult.LOAD, model.getValue().accept(TypeInfoIndexFields.TYPE_INFO));

      assertEquals(FieldSelectorResult.NO_LOAD, model.getValue().accept(TypeInfoIndexFields.SUPERCLASS));
      assertEquals(FieldSelectorResult.NO_LOAD, model.getValue().accept(TypeInfoIndexFields.INTERFACES));
      assertEquals(FieldSelectorResult.NO_LOAD, model.getValue().accept(TypeInfoIndexFields.INTERFACES));

   }

}
