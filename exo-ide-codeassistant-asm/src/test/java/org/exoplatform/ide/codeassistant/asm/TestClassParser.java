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
import org.exoplatform.ide.codeassistant.jvm.ShortTypeInfo;
import org.exoplatform.ide.codeassistant.jvm.TypeInfo;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TestClassParser
{

   private static final String PACKAGE = "org.exoplatform.ide.codeassistant.asm.testclasses";

   @Test
   public void testAnnotationParsing() throws IOException
   {
      TypeInfo annotationTest =
         ClassParser
            .parse(new FileInputStream(
               new File(
                  "target/test-classes/testclasses/classes/org/exoplatform/ide/codeassistant/asm/testclasses/TestAnnotation_class")));

      ShortTypeInfo shortTypeInfo = annotationTest;
      Assert.assertEquals("TestAnnotation", shortTypeInfo.getName());
      Assert.assertEquals(PACKAGE + ".TestAnnotation", shortTypeInfo.getQualifiedName());
      Assert.assertEquals("ANNOTATION", shortTypeInfo.getType());
      Assert.assertEquals(Integer.valueOf(Modifier.PUBLIC | Modifier.ABSTRACT | Modifier.INTERFACE
         | TypeInfoBuilder.MODIFIER_ANNOTATION), shortTypeInfo.getModifiers());

      TypeInfo typeInfo = annotationTest;
      Assert.assertEquals("TestAnnotation", typeInfo.getName());
      Assert.assertEquals(PACKAGE + ".TestAnnotation", typeInfo.getQualifiedName());
      Assert.assertEquals("ANNOTATION", typeInfo.getType());
      Assert.assertEquals("java.lang.Object", typeInfo.getSuperClass());
      Assert.assertEquals(1, typeInfo.getInterfaces().length);
      Assert.assertEquals("java.lang.annotation.Annotation", typeInfo.getInterfaces()[0]);
      Assert.assertEquals(Integer.valueOf(Modifier.PUBLIC | Modifier.ABSTRACT | Modifier.INTERFACE
         | TypeInfoBuilder.MODIFIER_ANNOTATION), typeInfo.getModifiers());

      Assert.assertEquals(0, typeInfo.getFields().length);
      Assert.assertEquals(0, typeInfo.getConstructors().length);
      {
         // methods
         MethodInfo[] methods = typeInfo.getMethods();
         Assert.assertEquals(3, methods.length);
         Set<String> visitedMethods = new HashSet<String>();
         for (MethodInfo method : methods)
         {
            if (method.getName().equals("a") && method.getParameterTypes().equals("()")
               && !visitedMethods.contains(method.getName() + method.getParameterTypes()))
            {
               assertMethod(method, "a", typeInfo.getQualifiedName(), Modifier.PUBLIC | Modifier.ABSTRACT, "()", "()",
                  "public abstract int " + PACKAGE + ".TestAnnotation.a()", new String[0], "int", "int");
               visitedMethods.add(method.getName() + method.getParameterTypes());
            }
            else if (method.getName().equals("b") && method.getParameterTypes().equals("()")
               && !visitedMethods.contains(method.getName() + method.getParameterTypes()))
            {
               assertMethod(method, "b", typeInfo.getQualifiedName(), Modifier.PUBLIC | Modifier.ABSTRACT, "()", "()",
                  "public abstract double " + PACKAGE + ".TestAnnotation.b()", new String[0], "double", "double");
               visitedMethods.add(method.getName() + method.getParameterTypes());
            }
            else if (method.getName().equals("c") && method.getParameterTypes().equals("()")
               && !visitedMethods.contains(method.getName() + method.getParameterTypes()))
            {
               assertMethod(method, "c", typeInfo.getQualifiedName(), Modifier.PUBLIC | Modifier.ABSTRACT, "()", "()",
                  "public abstract java.lang.String " + PACKAGE + ".TestAnnotation.c()", new String[0], "String",
                  "java.lang.String");
               visitedMethods.add(method.getName() + method.getParameterTypes());
            }
            else
            {
               Assert.fail("Method with name " + method.getName() + method.getParameterTypes()
                  + ", not found in expected classes.");
            }
         }
         Assert.assertEquals(methods.length, visitedMethods.size());

      }
   }

   @Test
   public void testTestClass() throws IOException
   {
      TypeInfo testClass =
         ClassParser
            .parse(new FileInputStream(
               new File(
                  "target/test-classes/testclasses/classes/org/exoplatform/ide/codeassistant/asm/testclasses/TestClass_class")));

      ShortTypeInfo shortTypeInfo = testClass;
      Assert.assertEquals("TestClass", shortTypeInfo.getName());
      Assert.assertEquals(PACKAGE + ".TestClass", shortTypeInfo.getQualifiedName());
      Assert.assertEquals("CLASS", shortTypeInfo.getType());
      Assert.assertEquals(Integer.valueOf(Modifier.PUBLIC | Modifier.SYNCHRONIZED), shortTypeInfo.getModifiers());

      TypeInfo typeInfo = testClass;
      Assert.assertEquals("TestClass", typeInfo.getName());
      Assert.assertEquals(PACKAGE + ".TestClass", typeInfo.getQualifiedName());
      Assert.assertEquals("CLASS", typeInfo.getType());
      Assert.assertEquals(PACKAGE + ".TestSuper", typeInfo.getSuperClass());
      Assert.assertEquals(2, typeInfo.getInterfaces().length);
      Assert.assertEquals(PACKAGE + ".TestInterface", typeInfo.getInterfaces()[0]);
      Assert.assertEquals(PACKAGE + ".TestInterface2", typeInfo.getInterfaces()[1]);
      Assert.assertEquals(Integer.valueOf(Modifier.PUBLIC | Modifier.SYNCHRONIZED), typeInfo.getModifiers());

      {
         // fields
         FieldInfo[] fields = typeInfo.getFields();
         Assert.assertEquals(4, fields.length);
         Set<String> visitedFields = new HashSet<String>();
         for (FieldInfo field : fields)
         {
            if (field.getName().equals("a") && !visitedFields.contains(field.getName()))
            {
               assertField(field, "a", "int", Modifier.PRIVATE, typeInfo.getQualifiedName());
               visitedFields.add(field.getName());
            }
            else if (field.getName().equals("b") && !visitedFields.contains(field.getName()))
            {
               assertField(field, "b", "java.lang.String", Modifier.PUBLIC | Modifier.FINAL,
                  typeInfo.getQualifiedName());
               visitedFields.add(field.getName());
            }
            else if (field.getName().equals("c") && !visitedFields.contains(field.getName()))
            {
               assertField(field, "c", "java.lang.Double", Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL,
                  typeInfo.getQualifiedName());
               visitedFields.add(field.getName());
            }
            else if (field.getName().equals("d") && !visitedFields.contains(field.getName()))
            {
               assertField(field, "d", "double", Modifier.PROTECTED, typeInfo.getQualifiedName());
               visitedFields.add(field.getName());
            }
            else
            {
               Assert.fail("Field with name " + field.getName() + ", not found in expected classes.");
            }
         }
         Assert.assertEquals(fields.length, visitedFields.size());
      }

      {
         // constructors
         RoutineInfo[] constructors = typeInfo.getConstructors();
         Assert.assertEquals(1, constructors.length);
         Set<String> visitedConstructors = new HashSet<String>();
         for (RoutineInfo constructor : constructors)
         {
            if (constructor.getName().equals("TestClass") && !visitedConstructors.contains(constructor.getName()))
            {
               assertRoutine(constructor, "TestClass", typeInfo.getQualifiedName(), Modifier.PUBLIC, "(int, String)",
                  "(int, java.lang.String)", "public " + PACKAGE + ".TestClass(int, java.lang.String)",
                  new String[]{"java.lang.ClassNotFoundException"});
               visitedConstructors.add(constructor.getName());
            }
            else
            {
               Assert.fail("Constructor with name " + constructor.getName() + ", not found in expected classes.");
            }
         }
         Assert.assertEquals(constructors.length, visitedConstructors.size());
      }

      {
         // methods
         MethodInfo[] methods = typeInfo.getMethods();
         Assert.assertEquals(5, methods.length);
         Set<String> visitedMethods = new HashSet<String>();
         for (MethodInfo method : methods)
         {
            if (method.getName().equals("method3")
               && method.getParameterTypes().equals("(double, int, char, float[][][], String[])")
               && !visitedMethods.contains(method.getName() + method.getParameterTypes()))
            {
               assertMethod(method, "method3", typeInfo.getQualifiedName(), Modifier.PUBLIC,
                  "(double, int, char, float[][][], String[])", "(double, int, char, float[][][], java.lang.String[])",
                  "public int " + PACKAGE + ".TestClass.method3(double, int, char, float[][][], java.lang.String[])",
                  new String[0], "int", "int");
               visitedMethods.add(method.getName() + method.getParameterTypes());
            }
            else if (method.getName().equals("method4")
               && method.getParameterTypes().equals("(String, Boolean, boolean, int[][][][][])")
               && !visitedMethods.contains(method.getName() + method.getParameterTypes()))
            {
               assertMethod(method, "method4", typeInfo.getQualifiedName(), Modifier.PUBLIC,
                  "(String, Boolean, boolean, int[][][][][])",
                  "(java.lang.String, java.lang.Boolean, boolean, int[][][][][])", "public void " + PACKAGE
                     + ".TestClass.method4(java.lang.String, java.lang.Boolean, boolean, int[][][][][])",
                  new String[0], "void", "void");
               visitedMethods.add(method.getName() + method.getParameterTypes());
            }
            else if (method.getName().equals("method1") && method.getParameterTypes().equals("()")
               && !visitedMethods.contains(method.getName() + method.getParameterTypes()))
            {
               assertMethod(method, "method1", typeInfo.getQualifiedName(), Modifier.PUBLIC, "()", "()", "public int "
                  + PACKAGE + ".TestClass.method1()", new String[0], "int", "int");
               visitedMethods.add(method.getName() + method.getParameterTypes());
            }
            else if (method.getName().equals("method2") && method.getParameterTypes().equals("(int)")
               && !visitedMethods.contains(method.getName() + method.getParameterTypes()))
            {
               assertMethod(method, "method2", typeInfo.getQualifiedName(), Modifier.PUBLIC, "(int)", "(int)",
                  "public int " + PACKAGE + ".TestClass.method2(int)", new String[0], "int", "int");
               visitedMethods.add(method.getName() + method.getParameterTypes());
            }
            else if (method.getName().equals("method0") && method.getParameterTypes().equals("(int)")
               && !visitedMethods.contains(method.getName() + method.getParameterTypes()))
            {
               assertMethod(method, "method0", typeInfo.getQualifiedName(), Modifier.PROTECTED, "(int)", "(int)",
                  "protected void " + PACKAGE + ".TestClass.method0(int)", new String[0], "void", "void");
               visitedMethods.add(method.getName() + method.getParameterTypes());
            }
            else
            {
               Assert.fail("Method with name " + method.getName() + method.getParameterTypes()
                  + ", not found in expected classes.");
            }
         }
         Assert.assertEquals(methods.length, visitedMethods.size());
      }
   }

   @Test
   public void testTestEnum() throws IOException
   {
      TypeInfo testEnum =
         ClassParser
            .parse(new FileInputStream(
               new File(
                  "target/test-classes/testclasses/classes/org/exoplatform/ide/codeassistant/asm/testclasses/TestEnum_class")));

      ShortTypeInfo shortTypeInfo = testEnum;
      Assert.assertEquals("TestEnum", shortTypeInfo.getName());
      Assert.assertEquals(PACKAGE + ".TestEnum", shortTypeInfo.getQualifiedName());
      Assert.assertEquals("ENUM", shortTypeInfo.getType());
      // 0x00004000 - Modifier.ENUM
      Assert.assertEquals(
         Integer.valueOf(Modifier.SYNCHRONIZED | Modifier.FINAL | TypeInfoBuilder.MODIFIER_ENUM | Modifier.PUBLIC),
         shortTypeInfo.getModifiers());

      TypeInfo typeInfo = testEnum;
      Assert.assertEquals("TestEnum", typeInfo.getName());
      Assert.assertEquals(PACKAGE + ".TestEnum", typeInfo.getQualifiedName());
      Assert.assertEquals("ENUM", typeInfo.getType());
      Assert.assertEquals("java.lang.Enum", typeInfo.getSuperClass());
      Assert.assertEquals(0, typeInfo.getInterfaces().length);
      Assert.assertEquals(
         Integer.valueOf(Modifier.SYNCHRONIZED | Modifier.FINAL | TypeInfoBuilder.MODIFIER_ENUM | Modifier.PUBLIC),
         typeInfo.getModifiers());

      {
         // fields
         FieldInfo[] fields = typeInfo.getFields();
         Assert.assertEquals(5, fields.length);
         Set<String> visitedFields = new HashSet<String>();
         for (FieldInfo field : fields)
         {
            if (field.getName().equals("ENUM1") && !visitedFields.contains(field.getName()))
            {
               assertField(field, "ENUM1", PACKAGE + ".TestEnum", Modifier.PUBLIC | Modifier.STATIC
                  | TypeInfoBuilder.MODIFIER_ENUM | Modifier.FINAL, typeInfo.getQualifiedName());
               visitedFields.add(field.getName());
            }
            else if (field.getName().equals("ENUM2") && !visitedFields.contains(field.getName()))
            {
               assertField(field, "ENUM2", PACKAGE + ".TestEnum", Modifier.PUBLIC | Modifier.STATIC
                  | TypeInfoBuilder.MODIFIER_ENUM | Modifier.FINAL, typeInfo.getQualifiedName());
               visitedFields.add(field.getName());
            }
            else if (field.getName().equals("ENUM3") && !visitedFields.contains(field.getName()))
            {
               assertField(field, "ENUM3", PACKAGE + ".TestEnum", Modifier.PUBLIC | Modifier.STATIC
                  | TypeInfoBuilder.MODIFIER_ENUM | Modifier.FINAL, typeInfo.getQualifiedName());
               visitedFields.add(field.getName());
            }
            else if (field.getName().equals("ENUM4") && !visitedFields.contains(field.getName()))
            {
               assertField(field, "ENUM4", PACKAGE + ".TestEnum", Modifier.PUBLIC | Modifier.STATIC
                  | TypeInfoBuilder.MODIFIER_ENUM | Modifier.FINAL, typeInfo.getQualifiedName());
               visitedFields.add(field.getName());
            }
            else if (field.getName().equals("ENUM$VALUES") && !visitedFields.contains(field.getName()))
            {
               assertField(field, "ENUM$VALUES", PACKAGE + ".TestEnum[]", Modifier.PRIVATE | Modifier.STATIC
                  | Modifier.FINAL | TypeInfoBuilder.MODIFIER_SYNTHETIC, typeInfo.getQualifiedName());
               visitedFields.add(field.getName());
            }
            else
            {
               Assert.fail("Field with name " + field.getName() + ", not found in expected classes.");
            }
         }
         Assert.assertEquals(fields.length, visitedFields.size());
      }

      {
         // constructors
         RoutineInfo[] constructors = typeInfo.getConstructors();
         Assert.assertEquals(1, constructors.length);
         Set<String> visitedConstructors = new HashSet<String>();
         for (RoutineInfo constructor : constructors)
         {
            if (constructor.getName().equals("TestEnum") && !visitedConstructors.contains(constructor.getName()))
            {
               assertRoutine(constructor, "TestEnum", typeInfo.getQualifiedName(), Modifier.PRIVATE, "(String, int)",
                  "(java.lang.String, int)", "private " + PACKAGE + ".TestEnum(java.lang.String, int)", new String[0]);
               visitedConstructors.add(constructor.getName());
            }
            else
            {
               Assert.fail("Constructor with name " + constructor.getName() + ", not found in expected classes.");
            }
         }
         Assert.assertEquals(constructors.length, visitedConstructors.size());
      }

      {
         // methods
         MethodInfo[] methods = typeInfo.getMethods();
         Assert.assertEquals(2, methods.length);
         Set<String> visitedMethods = new HashSet<String>();
         for (MethodInfo method : methods)
         {
            if (method.getName().equals("values") && method.getParameterTypes().equals("()")
               && !visitedMethods.contains(method.getName() + method.getParameterTypes()))
            {
               assertMethod(method, "values", typeInfo.getQualifiedName(), Modifier.PUBLIC | Modifier.STATIC, "()",
                  "()", "public static " + PACKAGE + ".TestEnum[] " + PACKAGE + ".TestEnum.values()", new String[0],
                  "TestEnum[]", PACKAGE + ".TestEnum[]");
               visitedMethods.add(method.getName() + method.getParameterTypes());
            }
            else if (method.getName().equals("valueOf") && method.getParameterTypes().equals("(String)")
               && !visitedMethods.contains(method.getName() + method.getParameterTypes()))
            {
               assertMethod(method, "valueOf", typeInfo.getQualifiedName(), Modifier.PUBLIC | Modifier.STATIC,
                  "(String)", "(java.lang.String)", "public static " + PACKAGE + ".TestEnum " + PACKAGE
                     + ".TestEnum.valueOf(java.lang.String)", new String[0], "TestEnum", PACKAGE + ".TestEnum");
               visitedMethods.add(method.getName() + method.getParameterTypes());
            }
            else
            {
               Assert.fail("Method with name " + method.getName() + method.getParameterTypes()
                  + ", not found in expected classes.");
            }
         }
         Assert.assertEquals(methods.length, visitedMethods.size());
      }
   }

   @Test
   public void testTestInterface() throws IOException
   {
      TypeInfo testInterface =
         ClassParser
            .parse(new FileInputStream(
               new File(
                  "target/test-classes/testclasses/classes/org/exoplatform/ide/codeassistant/asm/testclasses/TestInterface_class")));

      ShortTypeInfo shortTypeInfo = testInterface;
      Assert.assertEquals("TestInterface", shortTypeInfo.getName());
      Assert.assertEquals(PACKAGE + ".TestInterface", shortTypeInfo.getQualifiedName());
      Assert.assertEquals("INTERFACE", shortTypeInfo.getType());
      Assert.assertEquals(Integer.valueOf(Modifier.ABSTRACT | Modifier.INTERFACE | Modifier.PUBLIC),
         shortTypeInfo.getModifiers());

      TypeInfo typeInfo = testInterface;
      Assert.assertEquals("TestInterface", typeInfo.getName());
      Assert.assertEquals(PACKAGE + ".TestInterface", typeInfo.getQualifiedName());
      Assert.assertEquals("INTERFACE", typeInfo.getType());
      Assert.assertEquals("java.lang.Object", typeInfo.getSuperClass());
      Assert.assertEquals(0, typeInfo.getInterfaces().length);
      Assert.assertEquals(Integer.valueOf(Modifier.ABSTRACT | Modifier.INTERFACE | Modifier.PUBLIC),
         typeInfo.getModifiers());

      Assert.assertEquals(0, typeInfo.getFields().length);
      Assert.assertEquals(0, typeInfo.getConstructors().length);

      {
         // methods
         MethodInfo[] methods = typeInfo.getMethods();
         Assert.assertEquals(2, methods.length);
         Set<String> visitedMethods = new HashSet<String>();
         for (MethodInfo method : methods)
         {
            if (method.getName().equals("method1") && method.getParameterTypes().equals("()")
               && !visitedMethods.contains(method.getName() + method.getParameterTypes()))
            {
               assertMethod(method, "method1", typeInfo.getQualifiedName(), Modifier.PUBLIC | Modifier.ABSTRACT, "()",
                  "()", "public abstract int " + PACKAGE + ".TestInterface.method1()", new String[0], "int", "int");
               visitedMethods.add(method.getName() + method.getParameterTypes());
            }
            else if (method.getName().equals("method2") && method.getParameterTypes().equals("(int)")
               && !visitedMethods.contains(method.getName() + method.getParameterTypes()))
            {
               assertMethod(method, "method2", typeInfo.getQualifiedName(), Modifier.PUBLIC | Modifier.ABSTRACT,
                  "(int)", "(int)", "public abstract int " + PACKAGE + ".TestInterface.method2(int)", new String[0],
                  "int", "int");
               visitedMethods.add(method.getName() + method.getParameterTypes());
            }
            else
            {
               Assert.fail("Method with name " + method.getName() + method.getParameterTypes()
                  + ", not found in expected classes.");
            }
         }
         Assert.assertEquals(methods.length, visitedMethods.size());
      }
   }

   @Test
   public void testTestInterface2() throws IOException
   {
      TypeInfo testInterface2 =
         ClassParser
            .parse(new FileInputStream(
               new File(
                  "target/test-classes/testclasses/classes/org/exoplatform/ide/codeassistant/asm/testclasses/TestInterface2_class")));

      ShortTypeInfo shortTypeInfo = testInterface2;
      Assert.assertEquals("TestInterface2", shortTypeInfo.getName());
      Assert.assertEquals(PACKAGE + ".TestInterface2", shortTypeInfo.getQualifiedName());
      Assert.assertEquals("INTERFACE", shortTypeInfo.getType());
      Assert.assertEquals(Integer.valueOf(Modifier.ABSTRACT | Modifier.INTERFACE | Modifier.PUBLIC),
         shortTypeInfo.getModifiers());

      TypeInfo typeInfo = testInterface2;
      Assert.assertEquals("TestInterface2", typeInfo.getName());
      Assert.assertEquals(PACKAGE + ".TestInterface2", typeInfo.getQualifiedName());
      Assert.assertEquals("INTERFACE", typeInfo.getType());
      Assert.assertEquals("java.lang.Object", typeInfo.getSuperClass());
      Assert.assertEquals(0, typeInfo.getInterfaces().length);
      Assert.assertEquals(Integer.valueOf(Modifier.ABSTRACT | Modifier.INTERFACE | Modifier.PUBLIC),
         typeInfo.getModifiers());

      Assert.assertEquals(0, typeInfo.getFields().length);
      Assert.assertEquals(0, typeInfo.getConstructors().length);

      {
         // methods
         MethodInfo[] methods = typeInfo.getMethods();
         Assert.assertEquals(2, methods.length);
         Set<String> visitedMethods = new HashSet<String>();
         for (MethodInfo method : methods)
         {
            if (method.getName().equals("method3")
               && method.getParameterTypes().equals("(double, int, char, float[][][], String[])")
               && !visitedMethods.contains(method.getName() + method.getParameterTypes()))
            {
               assertMethod(method, "method3", typeInfo.getQualifiedName(), Modifier.PUBLIC | Modifier.ABSTRACT,
                  "(double, int, char, float[][][], String[])", "(double, int, char, float[][][], java.lang.String[])",
                  "public abstract int " + PACKAGE
                     + ".TestInterface2.method3(double, int, char, float[][][], java.lang.String[])", new String[0],
                  "int", "int");
               visitedMethods.add(method.getName() + method.getParameterTypes());
            }
            else if (method.getName().equals("method4")
               && method.getParameterTypes().equals("(String, Boolean, boolean, int[][][][][])")
               && !visitedMethods.contains(method.getName() + method.getParameterTypes()))
            {
               assertMethod(method, "method4", typeInfo.getQualifiedName(), Modifier.PUBLIC | Modifier.ABSTRACT,
                  "(String, Boolean, boolean, int[][][][][])",
                  "(java.lang.String, java.lang.Boolean, boolean, int[][][][][])", "public abstract void " + PACKAGE
                     + ".TestInterface2.method4(java.lang.String, java.lang.Boolean, boolean, int[][][][][])",
                  new String[0], "void", "void");
               visitedMethods.add(method.getName() + method.getParameterTypes());
            }
            else
            {
               Assert.fail("Method with name " + method.getName() + method.getParameterTypes()
                  + ", not found in expected classes.");
            }
         }
         Assert.assertEquals(methods.length, visitedMethods.size());
      }
   }

   @Test
   public void testTestSuper() throws IOException
   {
      TypeInfo testSuper =
         ClassParser
            .parse(new FileInputStream(
               new File(
                  "target/test-classes/testclasses/classes/org/exoplatform/ide/codeassistant/asm/testclasses/TestSuper_class")));

      ShortTypeInfo shortTypeInfo = testSuper;
      Assert.assertEquals("TestSuper", shortTypeInfo.getName());
      Assert.assertEquals(PACKAGE + ".TestSuper", shortTypeInfo.getQualifiedName());
      Assert.assertEquals("CLASS", shortTypeInfo.getType());
      Assert.assertEquals(Integer.valueOf(Modifier.PUBLIC | Modifier.ABSTRACT | Modifier.SYNCHRONIZED),
         shortTypeInfo.getModifiers());

      TypeInfo typeInfo = testSuper;
      Assert.assertEquals("TestSuper", typeInfo.getName());
      Assert.assertEquals(PACKAGE + ".TestSuper", typeInfo.getQualifiedName());
      Assert.assertEquals("CLASS", typeInfo.getType());
      Assert.assertEquals("java.lang.Object", typeInfo.getSuperClass());
      Assert.assertEquals(0, typeInfo.getInterfaces().length);
      Assert.assertEquals(Integer.valueOf(Modifier.PUBLIC | Modifier.ABSTRACT | Modifier.SYNCHRONIZED),
         typeInfo.getModifiers());

      {
         // fields
         FieldInfo[] fields = typeInfo.getFields();
         Assert.assertEquals(1, fields.length);
         Set<String> visitedFields = new HashSet<String>();
         for (FieldInfo field : fields)
         {
            if (field.getName().equals("a") && !visitedFields.contains(field.getName()))
            {
               assertField(field, "a", "int", Modifier.PROTECTED, typeInfo.getQualifiedName());
               visitedFields.add(field.getName());
            }
            else
            {
               Assert.fail("Field with name " + field.getName() + ", not found in expected classes.");
            }
         }
         Assert.assertEquals(fields.length, visitedFields.size());
      }

      {
         // constructors
         RoutineInfo[] constructors = typeInfo.getConstructors();
         Assert.assertEquals(1, constructors.length);
         Set<String> visitedConstructors = new HashSet<String>();
         for (RoutineInfo constructor : constructors)
         {
            if (constructor.getName().equals("TestSuper") && !visitedConstructors.contains(constructor.getName()))
            {
               assertRoutine(constructor, "TestSuper", typeInfo.getQualifiedName(), Modifier.PUBLIC, "()", "()",
                  "public " + PACKAGE + ".TestSuper()", new String[0]);
               visitedConstructors.add(constructor.getName());
            }
            else
            {
               Assert.fail("Constructor with name " + constructor.getName() + ", not found in expected classes.");
            }
         }
         Assert.assertEquals(constructors.length, visitedConstructors.size());
      }

      {
         // methods
         MethodInfo[] methods = typeInfo.getMethods();
         Assert.assertEquals(5, methods.length);
         Set<String> visitedMethods = new HashSet<String>();
         for (MethodInfo method : methods)
         {
            if (method.getName().equals("method0") && method.getParameterTypes().equals("(int)")
               && !visitedMethods.contains(method.getName() + method.getParameterTypes()))
            {
               assertMethod(method, "method0", typeInfo.getQualifiedName(), Modifier.PROTECTED | Modifier.ABSTRACT,
                  "(int)", "(int)", "protected abstract void " + PACKAGE + ".TestSuper.method0(int)", new String[0],
                  "void", "void");
               visitedMethods.add(method.getName() + method.getParameterTypes());
            }
            else if (method.getName().equals("method") && method.getParameterTypes().equals("()")
               && !visitedMethods.contains(method.getName() + method.getParameterTypes()))
            {
               assertMethod(method, "method", typeInfo.getQualifiedName(), Modifier.PUBLIC, "()", "()", "public void "
                  + PACKAGE + ".TestSuper.method()", new String[0], "void", "void");
               visitedMethods.add(method.getName() + method.getParameterTypes());
            }
            else if (method.getName().equals("method") && method.getParameterTypes().equals("(int)")
               && !visitedMethods.contains(method.getName() + method.getParameterTypes()))
            {
               assertMethod(method, "method", typeInfo.getQualifiedName(), Modifier.PUBLIC, "(int)", "(int)",
                  "public void " + PACKAGE + ".TestSuper.method(int)", new String[]{"java.lang.RuntimeException",
                     "java.io.IOException"}, "void", "void");
               visitedMethods.add(method.getName() + method.getParameterTypes());
            }
            else if (method.getName().equals("method") && method.getParameterTypes().equals("(double)")
               && !visitedMethods.contains(method.getName() + method.getParameterTypes()))
            {
               assertMethod(method, "method", typeInfo.getQualifiedName(), Modifier.PUBLIC, "(double)", "(double)",
                  "public void " + PACKAGE + ".TestSuper.method(double)", new String[]{"java.lang.Exception",}, "void",
                  "void");
               visitedMethods.add(method.getName() + method.getParameterTypes());
            }
            else if (method.getName().equals("toString") && method.getParameterTypes().equals("()")
               && !visitedMethods.contains(method.getName() + method.getParameterTypes()))
            {
               assertMethod(method, "toString", typeInfo.getQualifiedName(), Modifier.PUBLIC, "()", "()",
                  "public java.lang.String " + PACKAGE + ".TestSuper.toString()", new String[0], "String",
                  "java.lang.String");
               visitedMethods.add(method.getName() + method.getParameterTypes());
            }
            else
            {
               Assert.fail("Method with name " + method.getName() + method.getParameterTypes()
                  + ", not found in expected classes.");
            }
         }
         Assert.assertEquals(methods.length, visitedMethods.size());
      }
   }

   @Test
   public void testJarParsing() throws IOException
   {
      List<TypeInfo> classes = JarParser.parse(new File("target/test-classes/testclasses/test_jar"));
      Assert.assertEquals(6, classes.size());
   }

   private void assertRoutine(RoutineInfo routine, String name, String declaredClass, int modifiers,
      String parameterTypes, String genericParameterTypes, String generic, String[] exceptions)
   {
      Assert.assertEquals(name, routine.getName());
      Assert.assertEquals(declaredClass, routine.getDeclaringClass());
      Assert.assertEquals(Integer.valueOf(modifiers), routine.getModifiers());
      Assert.assertEquals(exceptions.length, routine.getGenericExceptionTypes().length);
      for (int i = 0; i < exceptions.length; i++)
      {
         Assert.assertEquals(exceptions[i], routine.getGenericExceptionTypes()[i]);
      }
      Assert.assertEquals(parameterTypes, routine.getParameterTypes());
      Assert.assertEquals(genericParameterTypes, routine.getGenericParameterTypes());
      Assert.assertEquals(generic, routine.getGeneric());
   }

   private void assertMethod(MethodInfo method, String name, String declaredClass, int modifiers,
      String parameterTypes, String genericParameterTypes, String generic, String[] exceptions, String returnType,
      String genericReturnType)
   {
      assertRoutine(method, name, declaredClass, modifiers, parameterTypes, genericParameterTypes, generic, exceptions);
      Assert.assertEquals(returnType, method.getReturnType());
      Assert.assertEquals(genericReturnType, method.getGenericReturnType());
   }

   private void assertField(FieldInfo field, String name, String type, int modifiers, String declaredClass)
   {
      Assert.assertEquals(name, field.getName());
      Assert.assertEquals(type, field.getType());
      Assert.assertEquals(Integer.valueOf(modifiers), field.getModifiers());
      Assert.assertEquals(declaredClass, field.getDeclaringClass());
   }

}