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
package org.exoplatform.ide.codeassistant.jvm;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.lang.reflect.Modifier;

/**
 * Check result of method toString() classes which extends Member
 */
public class TestMemberToString
{
   @Test
   public void testFieldInfoToString()
   {
      FieldInfo fieldInfo = new FieldInfo("java.lang.String", Modifier.PUBLIC, "field", "test.TestClass");

      assertEquals("public java.lang.String test.TestClass.field", fieldInfo.toString());
   }

   @Test
   public void testRoutineInfoToString()
   {
      RoutineInfo routineInfo =
         new RoutineInfo(Modifier.PUBLIC, "TestClass", new String[]{}, "java.lang.Object", "Object",
            "public test.TestClass(java.lang.Object)", "test.TestClass");

      assertEquals("public test.TestClass(java.lang.Object)", routineInfo.toString());
   }

   @Test
   public void testMethodInfoToString()
   {
      MethodInfo methodInfo =
         new MethodInfo(Modifier.PUBLIC, "method", new String[]{}, "", "", "public void test.TestClass.method()",
            "test.TestClass", "", "");

      assertEquals("public void test.TestClass.method()", methodInfo.toString());
   }

   @Test
   public void testShortTypeInfoToString()
   {
      ShortTypeInfo shortTypeInfo = new ShortTypeInfo(Modifier.PUBLIC, "TestClass", "test.TestClass", "CLASS");

      assertEquals("public CLASS test.TestClass", shortTypeInfo.toString());
   }

   @Test
   public void testTypeInfoToString()
   {
      TypeInfo typeInfo = new TypeInfo();
      typeInfo.setModifiers(Modifier.PROTECTED);
      typeInfo.setType(JavaType.INTERFACE.toString());
      typeInfo.setQualifiedName("test.TestInterface");
      typeInfo.setName("TestClass");

      assertEquals("protected INTERFACE test.TestInterface", typeInfo.toString());
   }
}
