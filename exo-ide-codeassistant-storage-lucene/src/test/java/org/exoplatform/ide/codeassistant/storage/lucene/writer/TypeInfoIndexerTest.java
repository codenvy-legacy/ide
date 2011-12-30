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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import test.classes.CTestClass;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldSelector;
import org.apache.lucene.index.IndexReader;
import org.exoplatform.ide.codeassistant.asm.ClassParser;
import org.exoplatform.ide.codeassistant.jvm.bean.TypeInfoBean;
import org.exoplatform.ide.codeassistant.jvm.shared.FieldInfo;
import org.exoplatform.ide.codeassistant.jvm.shared.MethodInfo;
import org.exoplatform.ide.codeassistant.jvm.shared.ShortTypeInfo;
import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;
import org.exoplatform.ide.codeassistant.storage.lucene.LuceneCodeAssistantStorage;
import org.exoplatform.ide.codeassistant.storage.lucene.search.ShortTypeInfoExtractor;
import org.exoplatform.ide.codeassistant.storage.lucene.search.TypeInfoExtractor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

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

   @Mock
   private LuceneCodeAssistantStorage luceneCodeAssistantStorage;

   @Test
   public void shouldCallPredefinedSetOfFields() throws Exception
   {
      indexer.createDocument(typeInfo);
      // one for fields + one for externalization
      verify(typeInfo, times(2)).getName();
      verify(typeInfo, times(2)).getModifiers();
      verify(typeInfo, times(2)).getType();
      verify(typeInfo, times(2)).getInterfaces();
      verify(typeInfo, times(2)).getSuperClass();

      //only externalization
      verify(typeInfo).getFields();
      verify(typeInfo).getMethods();
      verifyNoMoreInteractions(typeInfo);
   }

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

   @Test
   public void shouldBeAbleToRestoreTypeInfo() throws Exception
   {
      TypeInfo expected = ClassParser.parse(ClassParser.getClassFile(CTestClass.class));
      Document document = indexer.createDocument(expected);
      when(reader.document(anyInt(), (FieldSelector)anyObject())).thenReturn(document);

      TypeInfo actual = new TypeInfoExtractor(luceneCodeAssistantStorage).getValue(reader, 5);

      assertTypeInfoEquals(expected, actual);
   }

   public static void assertFieldsEqual(List<FieldInfo> expected, List<FieldInfo> actual)
   {
      assertNotNull(expected);
      assertNotNull(actual);
      assertEquals(expected.size(), actual.size());
      for (int i = 0; i < expected.size(); i++)
      {
         //member
         assertEquals(expected.get(i).getName(), actual.get(i).getName());
         assertEquals(expected.get(i).getModifiers(), actual.get(i).getModifiers());
         //fieldInfo
         assertEquals(expected.get(i).getDeclaringClass(), actual.get(i).getDeclaringClass());
         assertEquals(expected.get(i).getType(), actual.get(i).getType());

      }
   }

   public static void assertMethodsEqual(List<MethodInfo> expected, List<MethodInfo> actual)
   {
      assertNotNull(expected);
      assertNotNull(actual);
      assertEquals(expected.size(), actual.size());
      for (int i = 0; i < expected.size(); i++)
      {
         //member
         assertEquals(expected.get(i).getName(), actual.get(i).getName());
         assertEquals(expected.get(i).getModifiers(), actual.get(i).getModifiers());
         //methodInfo
         assertEquals(expected.get(i).getDeclaringClass(), actual.get(i).getDeclaringClass());
         assertEquals(expected.get(i).isConstructor(), actual.get(i).isConstructor());
         assertEquals(expected.get(i).getReturnType(), actual.get(i).getReturnType());
         assertArrayEquals(expected.get(i).getExceptionTypes().toArray(), actual.get(i).getExceptionTypes().toArray());
         assertArrayEquals(expected.get(i).getParameterNames().toArray(), actual.get(i).getParameterNames().toArray());
         assertArrayEquals(expected.get(i).getParameterTypes().toArray(), actual.get(i).getParameterTypes().toArray());

      }
   }

   public static void assertTypeInfoEquals(TypeInfo expected, TypeInfo actual)
   {
      //Member
      assertEquals(expected.getModifiers(), actual.getModifiers());
      assertEquals(expected.getName(), actual.getName());
      //Short type info
      assertEquals(expected.getType(), actual.getType());
      //TypeInfo
      assertEquals(expected.getSuperClass(), actual.getSuperClass());
      assertFieldsEqual(expected.getFields(), actual.getFields());
      assertMethodsEqual(expected.getMethods(), actual.getMethods());

   }
}
