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
 * Check correctness of ShortTypeInfo deserialization
 */
public class TestShortTypeInfoExternalization extends BaseTest
{
   private ShortTypeInfo serializedShortTypeInfo;

   private ShortTypeInfo deserializedShortTypeInfo;

   @Before
   public void setUp() throws IOException, ClassNotFoundException
   {
      serializedShortTypeInfo = generateShortTypeInfo();
      byte[] serializedData = serializeObject(serializedShortTypeInfo);
      deserializedShortTypeInfo = new ShortTypeInfo();
      deserializedShortTypeInfo.readExternal(createObjectInputStream(serializedData));
   }

   @Test
   public void testSuperTypeFieldsDeserialization() throws IOException, ClassNotFoundException
   {
      assertEquals(serializedShortTypeInfo.getModifiers(), deserializedShortTypeInfo.getModifiers());
      assertEquals(serializedShortTypeInfo.getName(), deserializedShortTypeInfo.getName());
   }

   @Test
   public void testObjectFieldsDeserialization() throws IOException, ClassNotFoundException
   {
      assertEquals(serializedShortTypeInfo.getQualifiedName(), deserializedShortTypeInfo.getQualifiedName());
      assertEquals(serializedShortTypeInfo.getType(), deserializedShortTypeInfo.getType());
   }

   private ShortTypeInfo generateShortTypeInfo()
   {
      ShortTypeInfo shortTypeInfo = new ShortTypeInfo();
      shortTypeInfo.setModifiers(1);
      shortTypeInfo.setName("TestClass");
      shortTypeInfo.setQualifiedName("test.TestClass");
      shortTypeInfo.setType("CLASS");

      return shortTypeInfo;
   }
}
