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

import org.exoplatform.ide.codeassistant.jvm.MethodInfo;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Modifier;

/**
 * Check correctness of MethodInfo deserialization
 */
public class TestMethodInfoExternalization extends BaseTest
{
   private MethodInfo serializedMethodInfo;

   private MethodInfo deserializedMethodInfo;

   @Before
   public void setUp() throws IOException, ClassNotFoundException
   {
      serializedMethodInfo =
         new MethodInfo(Modifier.PUBLIC, "method", new String[]{"java.io.IOException",
            "java.lang.IlligalStateException"}, "java.lang.Object", "Object",
            "public Integer test.TestClass.method() throws java.io.IOException, java.lang.IllegalStateException",
            "test.TestClass", "java.lang.Integer", "Integer");
      byte[] serializedData = serializeObject(serializedMethodInfo);
      deserializedMethodInfo = new MethodInfo();
      deserializedMethodInfo.readExternal(createObjectInputStream(serializedData));
   }

   @Test
   public void testNameFieldDeserialization()
   {
      assertEquals(serializedMethodInfo.getName(), deserializedMethodInfo.getName());
   }

   @Test
   public void testModifiersFieldDeserialization()
   {
      assertEquals(serializedMethodInfo.getModifiers(), deserializedMethodInfo.getModifiers());
   }

   @Test
   public void testDeclaringClassFieldDeserialization()
   {
      assertEquals(serializedMethodInfo.getDeclaringClass(), deserializedMethodInfo.getDeclaringClass());
   }

   @Test
   public void testParameterTypesFieldDeserialization()
   {
      assertEquals(serializedMethodInfo.getParameterTypes(), deserializedMethodInfo.getParameterTypes());
   }

   @Test
   public void testGenericParametersTypesFieldDeserialization()
   {
      assertEquals(serializedMethodInfo.getGenericParameterTypes(), deserializedMethodInfo.getGenericParameterTypes());
   }

   @Test
   public void testGenericFieldDeserialization()
   {
      assertEquals(serializedMethodInfo.getGeneric(), deserializedMethodInfo.getGeneric());
   }

   @Test
   public void testGenericExceptionTypesFieldDeserialization()
   {
      assertArrayEquals(serializedMethodInfo.getGenericExceptionTypes(),
         deserializedMethodInfo.getGenericExceptionTypes());
   }

   @Test
   public void testReturnTypeDeserialization()
   {
      assertEquals(serializedMethodInfo.getReturnType(), deserializedMethodInfo.getReturnType());
   }

   @Test
   public void testGenericReturnTypeDeserialization()
   {
      assertEquals(serializedMethodInfo.getGenericReturnType(), deserializedMethodInfo.getGenericReturnType());
   }
}
