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
package org.exoplatform.ide.codeassistant.storage.externalization;

import org.junit.Test;

/**
 * Check correctness of TypeInfo deserialization
 */
public class TestTypeInfoExternalization
{
   @Test
   public void testName() throws Exception
   {

   }
   //   private TypeInfoBean serializedTypeInfo;
   //
   //   private TypeInfoBean deserializedTypeInfo;
   //
   //   @Before
   //   public void setUp() throws IOException, ClassNotFoundException
   //   {
   //      serializedTypeInfo = generateTypeInfo();
   //      byte[] serializedData = serializeObject(serializedTypeInfo);
   //      deserializedTypeInfo = new TypeInfoBean();
   //      deserializedTypeInfo.readExternal(createObjectInputStream(serializedData));
   //   }
   //
   //   @Test
   //   public void testModifiersFieldDeserialization()
   //   {
   //      assertEquals(serializedTypeInfo.getModifiers(), deserializedTypeInfo.getModifiers());
   //   }
   //
   //   @Test
   //   public void testNameFieldDeserialization()
   //   {
   //      assertEquals(serializedTypeInfo.getName(), deserializedTypeInfo.getName());
   //   }
   //
   //   @Test
   //   public void testTypeFieldDeserialization()
   //   {
   //      assertEquals(serializedTypeInfo.getType(), deserializedTypeInfo.getType());
   //   }
   //
   //   @Test
   //   public void testSuperClassDeserialization()
   //   {
   //      assertEquals(serializedTypeInfo.getSuperClass(), deserializedTypeInfo.getSuperClass());
   //   }
   //
   //   @Test
   //   public void testInterfacesDeserialization()
   //   {
   //      assertArrayEquals(serializedTypeInfo.getInterfaces().toArray(), deserializedTypeInfo.getInterfaces().toArray());
   //   }
   //
   //   @Test
   //   public void testMethodsDeserialization()
   //   {
   //      List<MethodInfo> serializedMethods = serializedTypeInfo.getMethods();
   //      List<MethodInfo> deserializedMethods = deserializedTypeInfo.getMethods();
   //
   //      assertArrayEquals(serializedMethods.toArray(), deserializedMethods.toArray());
   //   }
   //
   //   @Test
   //   public void testFieldsDeserialization()
   //   {
   //      List<FieldInfo> serializedFields = serializedTypeInfo.getFields();
   //      List<FieldInfo> deserializedFields = deserializedTypeInfo.getFields();
   //
   //      assertArrayEquals(serializedFields.toArray(), deserializedFields.toArray());
   //   }
   //
   //   private TypeInfoBean generateTypeInfo()
   //   {
   //      TypeInfoBean typeInfo = new TypeInfoBean();
   //
   //      typeInfo.setModifiers(Modifier.PUBLIC);
   //      typeInfo.setName("test.TestClass");
   //      typeInfo.setSuperClass("java.lang.Object");
   //      typeInfo.setType("CLASS");
   //
   //      String[] interfaces = new String[]{"java.io.Serializable"};
   //      typeInfo.setInterfaces(Arrays.asList(interfaces));
   //
   //      MethodInfoBean publicConstructor =
   //         new MethodInfoBean("test.TestClass", Modifier.PUBLIC, Arrays.asList(new String[]{"java.io.IOException",
   //            "java.lang.IllegalStateException"}), Arrays.asList(new String[]{"java.lang.Object", "Object"}),
   //            Arrays.asList(new String[]{"param1", "param2"}), true, "", "test.TestClass");
   //      MethodInfoBean protectedConstructor =
   //         new MethodInfoBean("test.TestClass", Modifier.PROTECTED, Arrays.asList(new String[]{"java.io.IOException"}),
   //            Arrays.asList(new String[]{"java.lang.String", "String"}), Arrays.asList(new String[]{"param1", "param2"}),
   //            true, "", "test.TestClass");
   //
   //      MethodInfoBean publicMethod =
   //         new MethodInfoBean("method1", Modifier.PUBLIC, Arrays.asList(new String[]{"java.io.IOException"}),
   //            Arrays.asList(new String[]{"java.lang.Object", "Object"}), Arrays.asList(new String[]{"param1", "param2"}),
   //            false, "test.TestClass", "java.lang.Integer");
   //      MethodInfoBean privateMethod =
   //         new MethodInfoBean("method2", Modifier.PRIVATE, Arrays.asList(new String[]{"java.io.IOException"}),
   //            Arrays.asList(new String[]{"java.lang.String", "String"}), Arrays.asList(new String[]{"param1", "param2"}),
   //            false, "test.TestClass", "java.lang.Integer");
   //      typeInfo.setMethods(Arrays.asList(new MethodInfo[]{publicConstructor, protectedConstructor, publicMethod,
   //         privateMethod}));
   //
   //      FieldInfoBean publicField = new FieldInfoBean("field1", Modifier.PUBLIC, "test.TestClass", "String");
   //      FieldInfoBean privateField = new FieldInfoBean("field2", Modifier.PRIVATE, "test.TestClass", "Integer");
   //      typeInfo.setFields(Arrays.asList(new FieldInfo[]{publicField, privateField}));
   //      return typeInfo;
   //   }
}
