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

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * Check correctness of FieldInfo deserialization
 */
public class TestFieldInfoExternalization extends BaseTest
{
   private FieldInfo serializedFieldInfo;

   private FieldInfo deserializedFieldInfo;

   @Before
   public void setUp() throws IOException, ClassNotFoundException
   {
      serializedFieldInfo = generateFieldInfo();
      byte[] serializedData = serializeObject(serializedFieldInfo);
      deserializedFieldInfo = new FieldInfo();
      deserializedFieldInfo.readExternal(createObjectInputStream(serializedData));
   }

   @Test
   public void testSuperTypeDeserialization()
   {
      assertEquals(serializedFieldInfo.getModifiers(), deserializedFieldInfo.getModifiers());
      assertEquals(serializedFieldInfo.getName(), deserializedFieldInfo.getName());
   }

   @Test
   public void testObjectFieldsDeserialization()
   {
      assertEquals(serializedFieldInfo.getDeclaringClass(), deserializedFieldInfo.getDeclaringClass());
      assertEquals(serializedFieldInfo.getType(), deserializedFieldInfo.getType());
   }

   private FieldInfo generateFieldInfo()
   {
      FieldInfo fieldInfo = new FieldInfo();
      fieldInfo.setModifiers(1);
      fieldInfo.setName("field");
      fieldInfo.setDeclaringClass("test.TestClass");
      fieldInfo.setType("String");

      return fieldInfo;
   }
}
