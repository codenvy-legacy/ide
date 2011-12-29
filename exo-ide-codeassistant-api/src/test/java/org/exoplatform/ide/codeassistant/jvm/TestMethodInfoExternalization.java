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

import static org.exoplatform.ide.codeassistant.jvm.serialization.ExternalizationTools.createObjectInputStream;
import static org.exoplatform.ide.codeassistant.jvm.serialization.ExternalizationTools.serializeObject;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.exoplatform.ide.codeassistant.jvm.bean.MethodInfoBean;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Arrays;

/**
 * Check correctness of MethodInfo deserialization
 */
public class TestMethodInfoExternalization
{
   private MethodInfoBean serializedMethodInfo;

   private MethodInfoBean deserializedMethodInfo;

   @Before
   public void setUp() throws IOException, ClassNotFoundException
   {
      serializedMethodInfo =
         new MethodInfoBean("method", Modifier.PUBLIC, Arrays.asList(new String[]{"java.io.IOException",
            "java.lang.IlligalStateException"}), Arrays.asList(new String[]{"java.lang.Object", "Object"}),
            Arrays.asList(new String[]{"param1", "param2"}), false, "test.TestClass", "java.lang.Integer");
      byte[] serializedData = serializeObject(serializedMethodInfo);
      deserializedMethodInfo = new MethodInfoBean();
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
      assertArrayEquals(serializedMethodInfo.getParameterTypes().toArray(), deserializedMethodInfo.getParameterTypes()
         .toArray());
   }

   @Test
   public void testGenericExceptionTypesFieldDeserialization()
   {
      assertArrayEquals(serializedMethodInfo.getExceptionTypes().toArray(), deserializedMethodInfo.getExceptionTypes()
         .toArray());
   }

   @Test
   public void testGenericReturnTypeDeserialization()
   {
      assertEquals(serializedMethodInfo.getReturnType(), deserializedMethodInfo.getReturnType());
   }
}
