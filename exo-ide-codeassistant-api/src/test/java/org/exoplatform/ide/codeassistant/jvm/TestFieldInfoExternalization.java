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

import org.exoplatform.ide.codeassistant.jvm.FieldInfo;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Modifier;

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
      serializedFieldInfo = new FieldInfo("field", Modifier.PUBLIC, "test.TestClass", "String");
      byte[] serializedData = serializeObject(serializedFieldInfo);
      deserializedFieldInfo = new FieldInfo();
      deserializedFieldInfo.readExternal(createObjectInputStream(serializedData));
   }

   @Test
   public void testNameFieldDeserialization()
   {
      assertEquals(serializedFieldInfo.getName(), deserializedFieldInfo.getName());
   }

   @Test
   public void testModifiersFieldDeserialization()
   {
      assertEquals(serializedFieldInfo.getModifiers(), deserializedFieldInfo.getModifiers());
   }

   @Test
   public void testDeclaringClassFieldDeserialization()
   {
      assertEquals(serializedFieldInfo.getDeclaringClass(), deserializedFieldInfo.getDeclaringClass());
   }

   @Test
   public void testTypeFieldDeserialization()
   {
      assertEquals(serializedFieldInfo.getType(), deserializedFieldInfo.getType());
   }
}
