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
package org.exoplatform.ide.codeassistant.asm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.codeassistant.asm.objects.A;
import org.exoplatform.ide.codeassistant.jvm.MethodInfo;
import org.exoplatform.ide.codeassistant.jvm.RoutineInfo;
import org.exoplatform.ide.codeassistant.jvm.TypeInfo;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;

/**
 * Compare information received with help of java reflection and ClassParser
 */
public class TestClassParser2 extends BaseTest
{
   private static TypeInfo typeInfo;

   private static Class<A> aClass = A.class;

   @BeforeClass
   public static void createTypeInfo() throws Exception
   {
      InputStream io = getClassFileAsStream(aClass.getCanonicalName());
      typeInfo = ClassParser.parse(io);
   }

   @Test
   public void testClassNameExtraction() throws IOException
   {
      assertEquals(aClass.getCanonicalName(), typeInfo.getQualifiedName());
      assertEquals(aClass.getSimpleName(), typeInfo.getName());
   }

   @Test
   public void testPublicConstructorsExtraction() throws IOException
   {
      // check all public constructors
      assertEquals(3, aClass.getConstructors().length);
      assertEquals(aClass.getConstructors().length, typeInfo.getConstructors().length);

      for (RoutineInfo routineInfo : typeInfo.getConstructors())
      {
         boolean isConstructorPublic = ((routineInfo.getModifiers() & Modifier.PUBLIC) != 0);
         assertTrue(isConstructorPublic);
      }
   }

   @Test
   public void testDeclaredConstructorsExtraction() throws IOException
   {
      // check all declared constructors (private, public, protected)
      assertEquals(5, aClass.getDeclaredConstructors().length);
      assertEquals(aClass.getDeclaredConstructors().length, typeInfo.getDeclaredConstructors().length);
   }

   @Test
   public void testPublicMethodsExtraction() throws IOException
   {
      // with help of asm ClassParser we can't to get list of inherited methods 
      assertFalse(aClass.getMethods().length == typeInfo.getMethods().length);
      assertEquals(6, typeInfo.getMethods().length);

      for (MethodInfo methodInfo : typeInfo.getMethods())
      {
         boolean isMethodPublic = ((methodInfo.getModifiers() & Modifier.PUBLIC) != 0);
         assertTrue(isMethodPublic);
      }
   }

   @Test
   public void testDeclaredMethodsExtraction() throws IOException
   {
      // check count of all declared in the class methods
      assertEquals(8, aClass.getDeclaredMethods().length);
      assertEquals(aClass.getDeclaredMethods().length, typeInfo.getDeclaredMethods().length);
   }

   @Test
   public void testPublicFieldsExtraction() throws IOException
   {
      // with help of asm ClassParser we can't to get list of inherited fields 
      // class B contains just its own public fields
      assertEquals(aClass.getFields().length, typeInfo.getFields().length);
      assertEquals(1, typeInfo.getFields().length);

      assertTrue((typeInfo.getFields()[0].getModifiers() & Modifier.PUBLIC) != 0);
   }

   @Test
   public void testDeclaredFieldsExtraction() throws IOException
   {
      // check count of all declared in the class fields
      assertEquals(3, aClass.getDeclaredFields().length);
      assertEquals(aClass.getDeclaredFields().length, typeInfo.getDeclaredFields().length);
   }
}
