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
   public void testQualifiedNameFieldDeserialization()
   {
      assertEquals(serializedTypeInfo.getQualifiedName(), deserializedTypeInfo.getQualifiedName());
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
   public void testConstructorsDeserialization()
   {
      RoutineInfo[] serializedConstructors = serializedTypeInfo.getConstructors();
      RoutineInfo[] deserializedConstructors = deserializedTypeInfo.getConstructors();

      assertArrayEquals(serializedConstructors, deserializedConstructors);
   }

   @Test
   public void testDeclaredConstructorsDeserialization()
   {
      RoutineInfo[] serializedConstructors = serializedTypeInfo.getDeclaredConstructors();
      RoutineInfo[] deserializedConstructors = deserializedTypeInfo.getDeclaredConstructors();

      assertArrayEquals(serializedConstructors, deserializedConstructors);
   }

   @Test
   public void testMethodsDeserialization()
   {
      MethodInfo[] serializedMethods = serializedTypeInfo.getMethods();
      MethodInfo[] deserializedMethods = deserializedTypeInfo.getMethods();

      assertArrayEquals(serializedMethods, deserializedMethods);
   }

   @Test
   public void testDeclaredMethodsDeserialization()
   {
      MethodInfo[] serializedMethods = serializedTypeInfo.getDeclaredMethods();
      MethodInfo[] deserializedMethods = deserializedTypeInfo.getDeclaredMethods();

      assertArrayEquals(serializedMethods, deserializedMethods);
   }

   @Test
   public void testFieldsDeserialization()
   {
      FieldInfo[] serializedFields = serializedTypeInfo.getFields();
      FieldInfo[] deserializedFields = deserializedTypeInfo.getFields();

      assertArrayEquals(serializedFields, deserializedFields);
   }

   @Test
   public void testDeclaredFieldsDeserialization()
   {
      FieldInfo[] serializedFields = serializedTypeInfo.getDeclaredFields();
      FieldInfo[] deserializedFields = deserializedTypeInfo.getDeclaredFields();

      assertArrayEquals(serializedFields, deserializedFields);
   }

   private TypeInfo generateTypeInfo()
   {
      TypeInfo typeInfo = new TypeInfo();

      typeInfo.setModifiers(Modifier.PUBLIC);
      typeInfo.setName("TestClass");
      typeInfo.setQualifiedName("test.TestClass");
      typeInfo.setSuperClass("java.lang.Object");
      typeInfo.setType("CLASS");

      String[] interfaces = new String[]{"java.io.Serializable"};
      typeInfo.setInterfaces(interfaces);

      RoutineInfo publicConstructor =
         new RoutineInfo(Modifier.PUBLIC, "TestClass", new String[]{"java.io.IOException",
            "java.lang.IllegalStateException"}, "java.lang.Object", "Object",
            "public test.TestClass(java.lang.Object) throws java.io.IOException, java.lang.IllegalStateException",
            "test.TestClass");
      RoutineInfo protectedConstructor =
         new RoutineInfo(Modifier.PROTECTED, "TestClass", new String[]{"java.io.IOException"}, "java.lang.String",
            "String", "protected test.TestClass(java.lang.String) throws java.io.IOException", "test.TestClass");
      typeInfo.setConstructors(new RoutineInfo[]{publicConstructor});
      typeInfo.setDeclaredConstructors(new RoutineInfo[]{publicConstructor, protectedConstructor});

      MethodInfo publicMethod =
         new MethodInfo(Modifier.PUBLIC, "method1", new String[]{"java.io.IOException",}, "java.lang.Object", "Object",
            "public Integer test.TestClass.method1(java.lang.Object) throws java.io.IOException", "test.TestClass",
            "java.lang.Integer", "Integer");
      MethodInfo privateMethod =
         new MethodInfo(Modifier.PRIVATE, "method2", new String[]{"java.io.IOException"}, "java.lang.String", "String",
            "public Integer test.TestClass.method2(java.lang.String) throws java.io.IOException", "test.TestClass",
            "java.lang.Integer", "Integer");
      typeInfo.setMethods(new MethodInfo[]{publicMethod});
      typeInfo.setDeclaredMethods(new MethodInfo[]{publicMethod, privateMethod});

      FieldInfo publicField = new FieldInfo("field1", Modifier.PUBLIC, "test.TestClass", "String");
      FieldInfo privateField = new FieldInfo("field2", Modifier.PRIVATE, "test.TestClass", "Integer");
      typeInfo.setFields(new FieldInfo[]{publicField});
      typeInfo.setDeclaredFields(new FieldInfo[]{publicField, privateField});

      return typeInfo;
   }
}
