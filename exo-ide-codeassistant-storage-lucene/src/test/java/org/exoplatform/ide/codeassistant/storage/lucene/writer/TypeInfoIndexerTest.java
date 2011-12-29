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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import test.classes.CTestClass;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldSelector;
import org.apache.lucene.index.IndexReader;
import org.exoplatform.ide.codeassistant.asm.ClassParser;
import org.exoplatform.ide.codeassistant.jvm.ShortTypeInfo;
import org.exoplatform.ide.codeassistant.jvm.TypeInfo;
import org.exoplatform.ide.codeassistant.jvm.bean.TypeInfoBean;
import org.exoplatform.ide.codeassistant.storage.lucene.search.ShortTypeInfoExtractor;
import org.exoplatform.ide.codeassistant.storage.lucene.search.TypeInfoExtractor;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.ObjectOutput;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class TypeInfoIndexerTest
{
   private final TypeInfoIndexer indexer = new TypeInfoIndexer();

   @Mock(answer = Answers.RETURNS_SMART_NULLS)
   private TypeInfoBean typeInfo;

   @Mock
   private IndexReader reader;

   @Test
   public void shouldCallPredefinedSetOfFields() throws Exception
   {
      indexer.createDocument(typeInfo);
      verify(typeInfo).getName();
      verify(typeInfo).getModifiers();
      verify(typeInfo).getType();
      verify(typeInfo).getInterfaces();
      verify(typeInfo).getSuperClass();
      verify(typeInfo).writeExternal(any(ObjectOutput.class));
      verifyNoMoreInteractions(typeInfo);
   }

   @Ignore
   @Test
   public void shouldBeAbleToRestoreShortTypeInfo() throws Exception
   {
      TypeInfo expected = ClassParser.parse(ClassParser.getClassFile(CTestClass.class));
      Document document = indexer.createDocument(expected);
      when(reader.document(anyInt(), (FieldSelector)anyObject())).thenReturn(document);

      ShortTypeInfo actual = new ShortTypeInfoExtractor().getValue(reader, 5);

      assertEquals(expected.getType(), actual.getType());
      assertEquals(expected.getModifiers(), actual.getModifiers());
      assertEquals(expected.getName(), actual.getName());
   }

   @Ignore
   @Test
   public void shouldBeAbleToRestoreTypeInfo() throws Exception
   {
      TypeInfo expected = ClassParser.parse(ClassParser.getClassFile(CTestClass.class));
      Document document = indexer.createDocument(expected);
      when(reader.document(anyInt(), (FieldSelector)anyObject())).thenReturn(document);

      TypeInfo actual = new TypeInfoExtractor().getValue(reader, 5);

      assertEquals(expected, actual);
   }

}
