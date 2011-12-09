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
import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.codeassistant.asm.JarParser;
import org.exoplatform.ide.codeassistant.jvm.ShortTypeInfo;
import org.exoplatform.ide.codeassistant.jvm.TypeInfo;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.List;

/**
 * Test Searching in Lucene TypeInfo Storage
 */
public class SearchTest
{
   private final static String PATH_TO_JAR = "src/test/resources/test.jar";

   private final static String PATH_TO_INDEX = "target/index2";

   private static LuceneCodeAssistantStorage storage;

   @BeforeClass
   public static void createIndex() throws Exception
   {
      TypeInfoIndexWriter writer = new TypeInfoIndexWriter(PATH_TO_INDEX);

      List<TypeInfo> typeInfos = JarParser.parse(new File(PATH_TO_JAR));
      writer.addTypeInfo(typeInfos);
      writer.close();

      storage = new LuceneCodeAssistantStorage(PATH_TO_INDEX);
   }

   @Test
   public void testSearchTypeInfoByName() throws Exception
   {
      TypeInfo typeInfo = storage.getTypeByFqn("test.classes.ATestClass");

      assertEquals("ATestClass", typeInfo.getName());
      assertEquals("test.classes.ATestClass", typeInfo.getQualifiedName());
      assertEquals(2, typeInfo.getFields().length);
      assertEquals(3, typeInfo.getMethods().length);
      assertEquals("java.lang.Object", typeInfo.getSuperClass());
   }

   @Test
   public void testSearchByNamePrefix() throws Exception
   {
      List<ShortTypeInfo> typeInfos = storage.getTypesByNamePrefix("ATest");

      assertEquals(2, typeInfos.size());

      ShortTypeInfo info1 = typeInfos.get(0);
      ShortTypeInfo info2 = typeInfos.get(1);
      assertTrue(info1.getName().startsWith("ATest"));
      assertTrue(info2.getName().startsWith("ATest"));
   }

   @Test
   public void testSearchByFqnPrefix() throws Exception
   {
      List<ShortTypeInfo> typeInfos = storage.getTypesByFqnPrefix("test.classes");

      assertEquals(3, typeInfos.size());
      for (ShortTypeInfo shortTypeInfo : typeInfos)
      {
         assertTrue(shortTypeInfo.getQualifiedName().startsWith("test.classes"));
      }
   }

   @Test
   public void testSearchAllAnnotations() throws Exception
   {
      List<ShortTypeInfo> typeInfos = storage.getAnnotations("");

      assertEquals(2, typeInfos.size());
   }

   @Test
   public void testSearchAnnotationsStartsWithC() throws Exception
   {
      List<ShortTypeInfo> typeInfos = storage.getAnnotations("C");

      assertEquals(1, typeInfos.size());
   }

   @Test
   public void testSearchClassesStartsWithA() throws Exception
   {
      List<ShortTypeInfo> typeInfos = storage.getClasses("A");

      assertEquals(2, typeInfos.size());

      ShortTypeInfo info1 = typeInfos.get(0);
      assertTrue(info1.getName().startsWith("A"));
      assertEquals(info1.getType(), "CLASS");

      ShortTypeInfo info2 = typeInfos.get(1);
      assertTrue(info2.getName().startsWith("A"));
      assertEquals(info2.getType(), "CLASS");
   }

   @Test
   public void testSearchClassesStartsWithB() throws Exception
   {
      List<ShortTypeInfo> typeInfos = storage.getClasses("B");

      assertEquals(1, typeInfos.size());

      ShortTypeInfo info = typeInfos.get(0);
      assertTrue(info.getName().startsWith("B"));
   }

   @Test
   public void testSearchAllInterfaces() throws Exception
   {
      List<ShortTypeInfo> typeInfos = storage.getIntefaces("");

      assertEquals(3, typeInfos.size());
   }

   @Test
   public void testSearchUnexistanceClasses() throws Exception
   {
      List<ShortTypeInfo> typeInfos = storage.getClasses("W");

      assertEquals(0, typeInfos.size());
   }

   @Test
   public void testSearchUnexistanceClasses2() throws Exception
   {
      List<ShortTypeInfo> typeInfos = storage.getTypesByFqnPrefix("exo");

      assertEquals(0, typeInfos.size());
   }
}
