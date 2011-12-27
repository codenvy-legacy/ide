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

import test.classes.CTestClass;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FieldSelector;
import org.apache.lucene.document.FieldSelectorResult;
import org.apache.lucene.index.IndexReader;
import org.exoplatform.ide.codeassistant.asm.ClassParser;
import org.exoplatform.ide.codeassistant.jvm.ShortTypeInfo;
import org.exoplatform.ide.codeassistant.jvm.TypeInfo;
import org.exoplatform.ide.codeassistant.storage.lucene.TypeInfoIndexFields;
import org.exoplatform.ide.codeassistant.storage.lucene.writer.TypeInfoIndexer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ShortTypeInfoExtractorTest
{
   @Mock
   private IndexReader reader;

   private final ShortTypeInfoExtractor extractor = new ShortTypeInfoExtractor();

   @Test
   public void shouldCallReaderGetDocumentWithSameIdAndFieldSelector() throws Exception
   {

      Document luceneDocument = new Document();
      //add minimal set of field to be able to call method  extractor.getValue
      luceneDocument.add(new Field(TypeInfoIndexFields.MODIFIERS, "1", Store.YES, Index.NO));
      when(reader.document(anyInt(), (FieldSelector)anyObject())).thenReturn(luceneDocument);

      extractor.getValue(reader, 5);

      verify(reader).document(eq(5), (FieldSelector)anyObject());
      verifyNoMoreInteractions(reader);
   }

   @org.junit.Ignore
   @Test
   public void shouldReconstructShortTypeInfo() throws Exception
   {
      TypeInfo expected = ClassParser.parse(ClassParser.getClassFile(CTestClass.class));
      Document luceneDocument = new TypeInfoIndexer().createDocument(expected);

      when(reader.document(anyInt(), (FieldSelector)anyObject())).thenReturn(luceneDocument);

      ShortTypeInfo actual = extractor.getValue(reader, 5);

      assertEquals(expected.getType(), actual.getType());
      assertEquals(expected.getModifiers(), actual.getModifiers());
      assertEquals(expected.getName(), actual.getName());
   }

   @Test
   public void shouldGetDocumentFromReaderWithPredefinedSetOfFields() throws Exception
   {
      Document luceneDocument = new Document();
      //add minimal set of field to be able to call method  extractor.getValue
      luceneDocument.add(new Field(TypeInfoIndexFields.MODIFIERS, "1", Store.YES, Index.NO));
      when(reader.document(anyInt(), (FieldSelector)anyObject())).thenReturn(luceneDocument);

      extractor.getValue(reader, 5);

      ArgumentCaptor<FieldSelector> model = ArgumentCaptor.forClass(FieldSelector.class);
      verify(reader).document(eq(5), model.capture());

      assertEquals(FieldSelectorResult.LOAD, model.getValue().accept(TypeInfoIndexFields.MODIFIERS));
      assertEquals(FieldSelectorResult.LOAD, model.getValue().accept(TypeInfoIndexFields.CLASS_NAME));
      assertEquals(FieldSelectorResult.LOAD, model.getValue().accept(TypeInfoIndexFields.FQN));
      assertEquals(FieldSelectorResult.LOAD, model.getValue().accept(TypeInfoIndexFields.ENTITY_TYPE));
      assertEquals(FieldSelectorResult.NO_LOAD, model.getValue().accept(TypeInfoIndexFields.SUPERCLASS));
      assertEquals(FieldSelectorResult.NO_LOAD, model.getValue().accept(TypeInfoIndexFields.INTERFACES));
      assertEquals(FieldSelectorResult.NO_LOAD, model.getValue().accept(TypeInfoIndexFields.INTERFACES));

   }

}
