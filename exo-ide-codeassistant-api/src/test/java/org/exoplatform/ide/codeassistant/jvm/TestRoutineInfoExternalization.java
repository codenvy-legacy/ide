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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Modifier;

/**
 * Check correctness of RoutineInfo deserialization
 */
public class TestRoutineInfoExternalization extends BaseTest
{
   private RoutineInfo serializedRoutineInfo;

   private RoutineInfo deserializedRoutineInfo;

   @Before
   public void setUp() throws IOException, ClassNotFoundException
   {
      serializedRoutineInfo =
         new RoutineInfo(Modifier.PUBLIC, "TestClass", new String[]{"java.io.IOException",
            "java.lang.IllegalStateException"}, "java.lang.Object", "Object",
            "public test.TestClass(java.lang.Object) throws java.io.IOException, java.lang.IllegalStateException",
            "test.TestClass");
      byte[] serializedData = serializeObject(serializedRoutineInfo);
      deserializedRoutineInfo = new RoutineInfo();
      deserializedRoutineInfo.readExternal(createObjectInputStream(serializedData));
   }

   @Test
   public void testModifiersFieldDeserialization()
   {
      assertEquals(serializedRoutineInfo.getModifiers(), deserializedRoutineInfo.getModifiers());
   }

   @Test
   public void testNameFieldDeserialization()
   {
      assertEquals(serializedRoutineInfo.getName(), deserializedRoutineInfo.getName());
   }

   @Test
   public void testDeclaringClassFieldDeserialization()
   {
      assertEquals(serializedRoutineInfo.getDeclaringClass(), deserializedRoutineInfo.getDeclaringClass());
   }

   @Test
   public void testParameterTypesFieldDeserialization()
   {
      assertEquals(serializedRoutineInfo.getParameterTypes(), deserializedRoutineInfo.getParameterTypes());
   }

   @Test
   public void testGenericParameterTypesFieldDeserialization()
   {
      assertEquals(serializedRoutineInfo.getGenericParameterTypes(), deserializedRoutineInfo.getGenericParameterTypes());
   }

   @Test
   public void testGenericFieldDeserialization()
   {
      assertEquals(serializedRoutineInfo.getGeneric(), deserializedRoutineInfo.getGeneric());
   }

   @Test
   public void testGenericExceptionTypesFieldDeserialization()
   {
      assertArrayEquals(serializedRoutineInfo.getGenericExceptionTypes(),
         deserializedRoutineInfo.getGenericExceptionTypes());
   }
}
