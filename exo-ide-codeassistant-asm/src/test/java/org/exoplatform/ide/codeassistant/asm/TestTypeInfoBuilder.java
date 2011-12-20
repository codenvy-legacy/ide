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
package org.exoplatform.ide.codeassistant.asm;

import org.exoplatform.ide.codeassistant.jvm.FieldInfo;
import org.exoplatform.ide.codeassistant.jvm.MethodInfo;
import org.exoplatform.ide.codeassistant.jvm.RoutineInfo;
import org.exoplatform.ide.codeassistant.jvm.TypeInfo;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Modifier;

public class TestTypeInfoBuilder
{

   private final int access = Modifier.PUBLIC;

   private final String name = "org/exoplatform/test/TestClass";

   private final String superName = "org/exoplatform/test/TestSuper";

   private final String[] interfaces = {};

   @Test
   public void testAccess()
   {
      TypeInfoBuilder typeInfoBuilder =
         new TypeInfoBuilder(Modifier.PUBLIC | Modifier.ABSTRACT, name, superName, interfaces);
      TypeInfo typeInfo = typeInfoBuilder.buildTypeInfo();

      Assert.assertEquals(Integer.valueOf(Modifier.PUBLIC | Modifier.ABSTRACT), typeInfo.getModifiers());
      Assert.assertEquals("public abstract", typeInfo.modifierToString());
   }

   @Test
   public void testName()
   {
      TypeInfoBuilder typeInfoBuilder =
         new TypeInfoBuilder(access, "org/exoplatform/test/Class", superName, interfaces);
      TypeInfo typeInfo = typeInfoBuilder.buildTypeInfo();

      Assert.assertEquals("Class", typeInfo.getName());
      Assert.assertEquals("org.exoplatform.test.Class", typeInfo.getQualifiedName());
   }

   @Test
   public void testClassType()
   {
      TypeInfoBuilder typeInfoBuilder = new TypeInfoBuilder(Modifier.PUBLIC, name, superName, interfaces);
      TypeInfo typeInfo = typeInfoBuilder.buildTypeInfo();

      Assert.assertEquals(Integer.valueOf(Modifier.PUBLIC), typeInfo.getModifiers());
      Assert.assertEquals("CLASS", typeInfo.getType());
   }

   @Test
   public void testInterfaceType()
   {
      TypeInfoBuilder typeInfoBuilder =
         new TypeInfoBuilder(Modifier.PUBLIC | Modifier.INTERFACE, name, superName, interfaces);
      TypeInfo typeInfo = typeInfoBuilder.buildTypeInfo();

      Assert.assertEquals(Integer.valueOf(Modifier.PUBLIC | Modifier.INTERFACE), typeInfo.getModifiers());
      Assert.assertEquals("INTERFACE", typeInfo.getType());
   }

   @Test
   public void testAnnotationType()
   {
      TypeInfoBuilder typeInfoBuilder =
         new TypeInfoBuilder(Modifier.PUBLIC | TypeInfoBuilder.MODIFIER_ANNOTATION, name, superName, interfaces);
      TypeInfo typeInfo = typeInfoBuilder.buildTypeInfo();

      Assert.assertEquals(Integer.valueOf(Modifier.PUBLIC | TypeInfoBuilder.MODIFIER_ANNOTATION),
         typeInfo.getModifiers());
      Assert.assertEquals("ANNOTATION", typeInfo.getType());
   }

   @Test
   public void testEnumType()
   {
      TypeInfoBuilder typeInfoBuilder =
         new TypeInfoBuilder(Modifier.PUBLIC | TypeInfoBuilder.MODIFIER_ENUM, name, superName, interfaces);
      TypeInfo typeInfo = typeInfoBuilder.buildTypeInfo();

      Assert.assertEquals(Integer.valueOf(Modifier.PUBLIC | TypeInfoBuilder.MODIFIER_ENUM), typeInfo.getModifiers());
      Assert.assertEquals("ENUM", typeInfo.getType());
   }

   @Test
   public void testSyntheticDeclaredField()
   {
      TypeInfoBuilder typeInfoBuilder = new TypeInfoBuilder(access, name, superName, interfaces);
      typeInfoBuilder.addField(new FieldInfo("boolean", (TypeInfoBuilder.MODIFIER_SYNTHETIC), "field1",
         "org.exoplatform.test.Class"));
      typeInfoBuilder.addField(new FieldInfo("boolean",
         (Modifier.PUBLIC | Modifier.FINAL | TypeInfoBuilder.MODIFIER_SYNTHETIC), "field2",
         "org.exoplatform.test.Class"));
      TypeInfo typeInfo = typeInfoBuilder.buildTypeInfo();

      Assert.assertEquals(0, typeInfo.getDeclaredFields().length);
   }

   @Test
   public void testSimpleDeclaredField()
   {
      TypeInfoBuilder typeInfoBuilder = new TypeInfoBuilder(access, name, superName, interfaces);
      typeInfoBuilder.addField(new FieldInfo("boolean", (Modifier.PUBLIC | Modifier.FINAL), "field1",
         "org.exoplatform.test.Class"));
      TypeInfo typeInfo = typeInfoBuilder.buildTypeInfo();

      Assert.assertEquals(1, typeInfo.getDeclaredFields().length);
      Assert.assertEquals("field1", typeInfo.getDeclaredFields()[0].getName());
   }

   @Test
   public void testTwoDeclaredFields()
   {
      TypeInfoBuilder typeInfoBuilder = new TypeInfoBuilder(access, name, superName, interfaces);
      typeInfoBuilder.addField(new FieldInfo("boolean", (Modifier.PUBLIC | Modifier.FINAL), "field1",
         "org.exoplatform.test.Class"));
      typeInfoBuilder.addField(new FieldInfo("java.lang.Object", (Modifier.PUBLIC | Modifier.FINAL), "field2",
         "org.exoplatform.test.Class"));
      TypeInfo typeInfo = typeInfoBuilder.buildTypeInfo();

      Assert.assertEquals(2, typeInfo.getDeclaredFields().length);
      Assert.assertEquals("field1", typeInfo.getDeclaredFields()[0].getName());
      Assert.assertEquals("field2", typeInfo.getDeclaredFields()[1].getName());
   }

   @Test
   public void testSyntheticDeclaredConstructor()
   {
      TypeInfoBuilder typeInfoBuilder = new TypeInfoBuilder(access, name, superName, interfaces);
      typeInfoBuilder.addConstructor(new RoutineInfo((TypeInfoBuilder.MODIFIER_SYNTHETIC), "TestClass", new String[0],
         "(boolean)", "(boolean)", "org.exoplatform.test.TestClass(boolean)", "org.exoplatform.test.TestClass"));
      TypeInfo typeInfo = typeInfoBuilder.buildTypeInfo();

      Assert.assertEquals(0, typeInfo.getDeclaredConstructors().length);
   }

   @Test
   public void testSimpleDeclaredConstructor()
   {
      TypeInfoBuilder typeInfoBuilder = new TypeInfoBuilder(access, name, superName, interfaces);
      typeInfoBuilder.addConstructor(new RoutineInfo((Modifier.PUBLIC | Modifier.FINAL), "TestClass", new String[0],
         "(boolean)", "(boolean)", "public final org.exoplatform.test.TestClass(boolean)",
         "org.exoplatform.test.TestClass"));
      TypeInfo typeInfo = typeInfoBuilder.buildTypeInfo();

      Assert.assertEquals(1, typeInfo.getDeclaredConstructors().length);
      Assert.assertEquals("TestClass", typeInfo.getDeclaredConstructors()[0].getName());
   }

   @Test
   public void testTwoDeclaredConstructors()
   {
      TypeInfoBuilder typeInfoBuilder = new TypeInfoBuilder(access, name, superName, interfaces);
      typeInfoBuilder.addConstructor(new RoutineInfo((Modifier.PUBLIC | Modifier.FINAL), "TestClass", new String[]{
         "java.lang.Exception", "java.io.IOException"}, "(boolean)", "(boolean)",
         "public final org.exoplatform.test.TestClass(boolean)", "org.exoplatform.test.TestClass"));
      typeInfoBuilder.addConstructor(new RoutineInfo((Modifier.PRIVATE), "TestClass1", new String[0],
         "(java.lang.Object)", "(Object)", "private org.exoplatform.test.TestClass(java.lang.Object)",
         "org.exoplatform.test.TestClass"));
      TypeInfo typeInfo = typeInfoBuilder.buildTypeInfo();

      Assert.assertEquals(2, typeInfo.getDeclaredConstructors().length);
      Assert.assertEquals("TestClass", typeInfo.getDeclaredConstructors()[0].getName());
      Assert.assertEquals("TestClass1", typeInfo.getDeclaredConstructors()[1].getName());
   }

   @Test
   public void testSyntheticDeclaredMethod()
   {
      TypeInfoBuilder typeInfoBuilder = new TypeInfoBuilder(access, name, superName, interfaces);
      typeInfoBuilder.addMethod(new MethodInfo((TypeInfoBuilder.MODIFIER_SYNTHETIC), "method1", new String[0],
         "(boolean)", "(boolean)", "boolean org.exoplatform.test.TestClass.method1(boolean)",
         "org.exoplatform.test.TestClass", "boolean", "boolean"));
      TypeInfo typeInfo = typeInfoBuilder.buildTypeInfo();

      Assert.assertEquals(0, typeInfo.getDeclaredMethods().length);
   }

   @Test
   public void testSimpleDeclaredMethod()
   {
      TypeInfoBuilder typeInfoBuilder = new TypeInfoBuilder(access, name, superName, interfaces);
      typeInfoBuilder.addMethod(new MethodInfo((Modifier.PUBLIC | Modifier.FINAL), "method1", new String[0],
         "(boolean)", "(boolean)", "public final boolean org.exoplatform.test.TestClass.method1(boolean)",
         "org.exoplatform.test.TestClass", "boolean", "boolean"));
      TypeInfo typeInfo = typeInfoBuilder.buildTypeInfo();

      Assert.assertEquals(1, typeInfo.getDeclaredMethods().length);
      Assert.assertEquals("method1", typeInfo.getDeclaredMethods()[0].getName());
   }

   @Test
   public void testTwoDeclaredMethod()
   {
      TypeInfoBuilder typeInfoBuilder = new TypeInfoBuilder(access, name, superName, interfaces);
      typeInfoBuilder.addMethod(new MethodInfo((Modifier.PUBLIC | Modifier.FINAL), "method1", new String[0],
         "(boolean)", "(boolean)", "public final boolean org.exoplatform.test.TestClass.method1(boolean)",
         "org.exoplatform.test.TestClass", "boolean", "boolean"));
      typeInfoBuilder
         .addMethod(new MethodInfo(
            (Modifier.PRIVATE),
            "method2",
            new String[]{"java.io.IOException"},
            "(java.lang.String)",
            "(String)",
            "private java.lang.Object org.exoplatform.test.TestClass.method2(java.lang.String) throws java.io.IOException",
            "org.exoplatform.test.TestClass", "java.lang.Object", "Object"));
      TypeInfo typeInfo = typeInfoBuilder.buildTypeInfo();

      Assert.assertEquals(2, typeInfo.getDeclaredMethods().length);
      Assert.assertEquals("method1", typeInfo.getDeclaredMethods()[0].getName());
      Assert.assertEquals("method2", typeInfo.getDeclaredMethods()[1].getName());
   }

   @Test
   public void testSyntheticField()
   {
      TypeInfoBuilder typeInfoBuilder = new TypeInfoBuilder(access, name, superName, interfaces);
      typeInfoBuilder.addField(new FieldInfo("boolean", (TypeInfoBuilder.MODIFIER_SYNTHETIC), "field1",
         "org.exoplatform.test.Class"));
      typeInfoBuilder.addField(new FieldInfo("boolean",
         (Modifier.PUBLIC | Modifier.FINAL | TypeInfoBuilder.MODIFIER_SYNTHETIC), "field2",
         "org.exoplatform.test.Class"));
      TypeInfo typeInfo = typeInfoBuilder.buildTypeInfo();

      Assert.assertEquals(0, typeInfo.getFields().length);
   }

   @Test
   public void testFieldPublic()
   {
      TypeInfoBuilder typeInfoBuilder = new TypeInfoBuilder(access, name, superName, interfaces);
      typeInfoBuilder.addField(new FieldInfo("boolean", (Modifier.PUBLIC | Modifier.FINAL), "field1",
         "org.exoplatform.test.Class"));
      TypeInfo typeInfo = typeInfoBuilder.buildTypeInfo();

      Assert.assertEquals(1, typeInfo.getFields().length);
      Assert.assertEquals("field1", typeInfo.getFields()[0].getName());
   }

   @Test
   public void testFieldDefault()
   {
      TypeInfoBuilder typeInfoBuilder = new TypeInfoBuilder(access, name, superName, interfaces);
      typeInfoBuilder.addField(new FieldInfo("boolean", (Modifier.FINAL), "field1", "org.exoplatform.test.Class"));
      TypeInfo typeInfo = typeInfoBuilder.buildTypeInfo();

      Assert.assertEquals(0, typeInfo.getFields().length);
   }

   @Test
   public void testFieldPrivate()
   {
      TypeInfoBuilder typeInfoBuilder = new TypeInfoBuilder(access, name, superName, interfaces);
      typeInfoBuilder.addField(new FieldInfo("boolean", (Modifier.PRIVATE | Modifier.FINAL), "field1",
         "org.exoplatform.test.Class"));
      TypeInfo typeInfo = typeInfoBuilder.buildTypeInfo();

      Assert.assertEquals(0, typeInfo.getFields().length);
   }

   @Test
   public void testFieldPublicAndPrivate()
   {
      TypeInfoBuilder typeInfoBuilder = new TypeInfoBuilder(access, name, superName, interfaces);
      typeInfoBuilder.addField(new FieldInfo("boolean", (Modifier.PRIVATE | Modifier.FINAL), "field1",
         "org.exoplatform.test.Class"));
      typeInfoBuilder.addField(new FieldInfo("java.lang.Object", (Modifier.PUBLIC | Modifier.FINAL), "field2",
         "org.exoplatform.test.Class"));
      TypeInfo typeInfo = typeInfoBuilder.buildTypeInfo();

      Assert.assertEquals(1, typeInfo.getFields().length);
      Assert.assertEquals("field2", typeInfo.getFields()[0].getName());
   }

   @Test
   public void testSyntheticConstructor()
   {
      TypeInfoBuilder typeInfoBuilder = new TypeInfoBuilder(access, name, superName, interfaces);
      typeInfoBuilder.addConstructor(new RoutineInfo((TypeInfoBuilder.MODIFIER_SYNTHETIC), "TestClass", new String[0],
         "(boolean)", "(boolean)", "org.exoplatform.test.TestClass(boolean)", "org.exoplatform.test.TestClass"));
      TypeInfo typeInfo = typeInfoBuilder.buildTypeInfo();

      Assert.assertEquals(0, typeInfo.getConstructors().length);
   }

   @Test
   public void testConstructorPublic()
   {
      TypeInfoBuilder typeInfoBuilder = new TypeInfoBuilder(access, name, superName, interfaces);
      typeInfoBuilder.addConstructor(new RoutineInfo((Modifier.PUBLIC | Modifier.FINAL), "TestClass", new String[]{
         "java.lang.Exception", "java.io.IOException"}, "(boolean)", "(boolean)",
         "public final org.exoplatform.test.TestClass(boolean)", "org.exoplatform.test.TestClass"));
      TypeInfo typeInfo = typeInfoBuilder.buildTypeInfo();

      Assert.assertEquals(1, typeInfo.getConstructors().length);
      Assert.assertEquals("TestClass", typeInfo.getConstructors()[0].getName());
   }

   @Test
   public void testConstructorDefault()
   {
      TypeInfoBuilder typeInfoBuilder = new TypeInfoBuilder(access, name, superName, interfaces);
      typeInfoBuilder.addConstructor(new RoutineInfo(0, "TestClass1", new String[0], "(java.lang.Object)", "(Object)",
         "org.exoplatform.test.TestClass(java.lang.Object)", "org.exoplatform.test.TestClass"));
      TypeInfo typeInfo = typeInfoBuilder.buildTypeInfo();

      Assert.assertEquals(0, typeInfo.getConstructors().length);
   }

   @Test
   public void testConstructorPrivate()
   {
      TypeInfoBuilder typeInfoBuilder = new TypeInfoBuilder(access, name, superName, interfaces);
      typeInfoBuilder.addConstructor(new RoutineInfo((Modifier.PRIVATE), "TestClass1", new String[0],
         "(java.lang.Object)", "(Object)", "private org.exoplatform.test.TestClass(java.lang.Object)",
         "org.exoplatform.test.TestClass"));
      TypeInfo typeInfo = typeInfoBuilder.buildTypeInfo();

      Assert.assertEquals(0, typeInfo.getConstructors().length);
   }

   @Test
   public void testConstructorPublicAndPrivate()
   {
      TypeInfoBuilder typeInfoBuilder = new TypeInfoBuilder(access, name, superName, interfaces);
      typeInfoBuilder.addConstructor(new RoutineInfo((Modifier.PUBLIC | Modifier.FINAL), "TestClass", new String[]{
         "java.lang.Exception", "java.io.IOException"}, "(boolean)", "(boolean)",
         "public final org.exoplatform.test.TestClass(boolean)", "org.exoplatform.test.TestClass"));
      typeInfoBuilder.addConstructor(new RoutineInfo((Modifier.PRIVATE), "TestClass1", new String[0],
         "(java.lang.Object)", "(Object)", "private org.exoplatform.test.TestClass(java.lang.Object)",
         "org.exoplatform.test.TestClass"));
      TypeInfo typeInfo = typeInfoBuilder.buildTypeInfo();

      Assert.assertEquals(1, typeInfo.getConstructors().length);
      Assert.assertEquals("TestClass", typeInfo.getConstructors()[0].getName());
   }

   @Test
   public void testSyntheticMethod()
   {
      TypeInfoBuilder typeInfoBuilder = new TypeInfoBuilder(access, name, superName, interfaces);
      typeInfoBuilder.addMethod(new MethodInfo((TypeInfoBuilder.MODIFIER_SYNTHETIC), "method1", new String[0],
         "(boolean)", "(boolean)", "boolean org.exoplatform.test.TestClass.method1(boolean)",
         "org.exoplatform.test.TestClass", "boolean", "boolean"));
      TypeInfo typeInfo = typeInfoBuilder.buildTypeInfo();

      Assert.assertEquals(0, typeInfo.getMethods().length);
   }

   @Test
   public void testMethodPublic()
   {
      TypeInfoBuilder typeInfoBuilder = new TypeInfoBuilder(access, name, superName, interfaces);
      typeInfoBuilder.addMethod(new MethodInfo((Modifier.PUBLIC | Modifier.FINAL), "method1", new String[0],
         "(boolean)", "(boolean)", "public final boolean org.exoplatform.test.TestClass.method1(boolean)",
         "org.exoplatform.test.TestClass", "boolean", "boolean"));
      TypeInfo typeInfo = typeInfoBuilder.buildTypeInfo();

      Assert.assertEquals(1, typeInfo.getMethods().length);
      Assert.assertEquals("method1", typeInfo.getMethods()[0].getName());
   }

   @Test
   public void testMethodDefault()
   {
      TypeInfoBuilder typeInfoBuilder = new TypeInfoBuilder(access, name, superName, interfaces);
      typeInfoBuilder.addMethod(new MethodInfo((Modifier.FINAL), "method1", new String[0], "(boolean)", "(boolean)",
         "final boolean org.exoplatform.test.TestClass.method1(boolean)", "org.exoplatform.test.TestClass", "boolean",
         "boolean"));
      TypeInfo typeInfo = typeInfoBuilder.buildTypeInfo();

      Assert.assertEquals(0, typeInfo.getMethods().length);
   }

   @Test
   public void testMethodPrivate()
   {
      TypeInfoBuilder typeInfoBuilder = new TypeInfoBuilder(access, name, superName, interfaces);
      typeInfoBuilder.addMethod(new MethodInfo((Modifier.PRIVATE | Modifier.FINAL), "method1", new String[0],
         "(boolean)", "(boolean)", "public final boolean org.exoplatform.test.TestClass.method1(boolean)",
         "org.exoplatform.test.TestClass", "boolean", "boolean"));
      TypeInfo typeInfo = typeInfoBuilder.buildTypeInfo();

      Assert.assertEquals(0, typeInfo.getMethods().length);
   }

   @Test
   public void testMethodPublicAndPrivate()
   {
      TypeInfoBuilder typeInfoBuilder = new TypeInfoBuilder(access, name, superName, interfaces);
      typeInfoBuilder.addMethod(new MethodInfo((Modifier.PUBLIC | Modifier.FINAL), "method1", new String[0],
         "(boolean)", "(boolean)", "public final boolean org.exoplatform.test.TestClass.method1(boolean)",
         "org.exoplatform.test.TestClass", "boolean", "boolean"));
      typeInfoBuilder
         .addMethod(new MethodInfo(
            (Modifier.PRIVATE),
            "method2",
            new String[]{"java.io.IOException"},
            "(java.lang.String)",
            "(String)",
            "private java.lang.Object org.exoplatform.test.TestClass.method2(java.lang.String) throws java.io.IOException",
            "org.exoplatform.test.TestClass", "java.lang.Object", "Object"));
      TypeInfo typeInfo = typeInfoBuilder.buildTypeInfo();

      Assert.assertEquals(1, typeInfo.getMethods().length);
      Assert.assertEquals("method1", typeInfo.getMethods()[0].getName());
   }

}
