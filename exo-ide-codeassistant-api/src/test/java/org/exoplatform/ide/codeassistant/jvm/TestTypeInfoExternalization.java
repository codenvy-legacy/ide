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
 * Check correctness of TypeInfo deserialization
 */
public class TestTypeInfoExternalization extends BaseTest
{
   private TypeInfo serializedTypeInfo;

   private TypeInfo deserializedTypeInfo;

   @Before
   public void setUp() throws IOException, ClassNotFoundException
   {
      serializedTypeInfo = generateTypeInfo();
      byte[] serializedData = serializeObject(serializedTypeInfo);
      deserializedTypeInfo = new TypeInfo();
      deserializedTypeInfo.readExternal(createObjectInputStream(serializedData));
   }

   @Test
   public void testModifiersFieldDeserialization()
   {
      assertEquals(serializedTypeInfo.getModifiers(), deserializedTypeInfo.getModifiers());
   }

   @Test
   public void testNameFieldDeserialization()
   {
      assertEquals(serializedTypeInfo.getName(), deserializedTypeInfo.getName());
   }

   @Test
   public void testTypeFieldDeserialization()
   {
      assertEquals(serializedTypeInfo.getType(), deserializedTypeInfo.getType());
   }

   @Test
   public void testSuperClassDeserialization()
   {
      assertEquals(serializedTypeInfo.getSuperClass(), deserializedTypeInfo.getSuperClass());
   }

   @Test
   public void testInterfacesDeserialization()
   {
      assertArrayEquals(serializedTypeInfo.getInterfaces(), deserializedTypeInfo.getInterfaces());
   }

   @Test
   public void testMethodsDeserialization()
   {
         MethodInfo[] serializedMethods = serializedTypeInfo.getMethods();
      MethodInfo[] deserializedMethods = deserializedTypeInfo.getMethods();

      assertArrayEquals(serializedMethods, deserializedMethods);
   }

   @Test
   public void testFieldsDeserialization()
   {
     FieldInfo[] serializedFields = serializedTypeInfo.getFields();
      FieldInfo[] deserializedFields = deserializedTypeInfo.getFields();

      assertArrayEquals(serializedFields, deserializedFields);
   }

   private TypeInfo generateTypeInfo()
   {
      TypeInfo typeInfo = new TypeInfo();

      typeInfo.setModifiers(Modifier.PUBLIC);
      typeInfo.setName("test.TestClass");
      typeInfo.setSuperClass("java.lang.Object");
      typeInfo.setType("CLASS");

      String[] interfaces = new String[]{"java.io.Serializable"};
      typeInfo.setInterfaces(interfaces);

      MethodInfo publicConstructor =
         new MethodInfo("test.TestClass", Modifier.PUBLIC, new String[]{"java.io.IOException",
            "java.lang.IllegalStateException"}, new String[]{"java.lang.Object", "Object"}, true, "", "test.TestClass");
      MethodInfo protectedConstructor =
         new MethodInfo("test.TestClass", Modifier.PROTECTED, new String[]{"java.io.IOException"}, new String[]{
            "java.lang.String", "String"}, true, "", "test.TestClass");

      MethodInfo publicMethod =
         new MethodInfo("method1", Modifier.PUBLIC, new String[]{"java.io.IOException",}, new String[]{
            "java.lang.Object", "Object"}, false, "test.TestClass", "java.lang.Integer");
      MethodInfo privateMethod =
         new MethodInfo("method2", Modifier.PRIVATE, new String[]{"java.io.IOException"}, new String[]{
            "java.lang.String", "String"}, false, "test.TestClass", "java.lang.Integer");
      typeInfo.setMethods(new MethodInfo[]{publicConstructor, protectedConstructor, publicMethod, privateMethod});

      FieldInfo publicField = new FieldInfo("field1", Modifier.PUBLIC, "test.TestClass", "String");
      FieldInfo privateField = new FieldInfo("field2", Modifier.PRIVATE, "test.TestClass", "Integer");
      typeInfo.setFields(new FieldInfo[]{publicField, privateField});
      return typeInfo;
   }
}
