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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.exoplatform.ide.codeassistant.jvm.FieldInfo;
import org.exoplatform.ide.codeassistant.jvm.MethodInfo;
import org.exoplatform.ide.codeassistant.jvm.RoutineInfo;
import org.exoplatform.ide.codeassistant.jvm.ShortTypeInfo;
import org.exoplatform.ide.codeassistant.jvm.TypeInfo;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

public class TestClassParser extends BaseTest
{

   private static final String PACKAGE = "org.exoplatform.ide.codeassistant.asm.testclasses";

   @Test
   public void testTestGeneric() throws IOException, ClassNotFoundException
   {
      /**
       * NOTE: now code assistant not supports java generics. So this test check
       * that all generics will removed.
       */
      TypeInfo annotationTest = ClassParser.parse(getClassFileAsStream(PACKAGE + ".NoTestGeneric"));

      ShortTypeInfo shortTypeInfo = annotationTest;
      assertEquals("NoTestGeneric", shortTypeInfo.getName());
      assertEquals(PACKAGE + ".NoTestGeneric", shortTypeInfo.getQualifiedName());
      assertEquals("CLASS", shortTypeInfo.getType());
      assertEquals(Integer.valueOf(Modifier.PUBLIC | Modifier.SYNCHRONIZED), shortTypeInfo.getModifiers());

      TypeInfo typeInfo = annotationTest;
      assertEquals("NoTestGeneric", typeInfo.getName());
      assertEquals(PACKAGE + ".NoTestGeneric", typeInfo.getQualifiedName());
      assertEquals("CLASS", typeInfo.getType());
      assertEquals("java.lang.Object", typeInfo.getSuperClass());
      assertEquals(0, typeInfo.getInterfaces().length);
      assertEquals(Integer.valueOf(Modifier.PUBLIC | Modifier.SYNCHRONIZED), typeInfo.getModifiers());

      // fields
      FieldInfo[] fields = typeInfo.getDeclaredFields();
      assertEquals(1, fields.length);
      Set<String> visitedFields = new HashSet<String>();
      for (FieldInfo field : fields)
      {
         if (field.getName().equals("value") && !visitedFields.contains(field.getName()))
         {
            assertField(field, "value", "java.lang.Object", Modifier.PUBLIC, typeInfo.getQualifiedName());
            visitedFields.add(field.getName());
         }
      }
      assertEquals(fields.length, visitedFields.size());

      // constructors
      RoutineInfo[] constructors = typeInfo.getDeclaredConstructors();
      assertEquals(1, constructors.length);
      Set<String> visitedConstructors = new HashSet<String>();
      for (RoutineInfo constructor : constructors)
      {
         if (constructor.getName().equals("NoTestGeneric") && !visitedConstructors.contains(constructor.getName()))
         {
            assertRoutine(constructor, "NoTestGeneric", typeInfo.getQualifiedName(), Modifier.PUBLIC, "(Object)",
               "(java.lang.Object)", "public " + PACKAGE + ".NoTestGeneric(java.lang.Object)", new String[0]);
            visitedConstructors.add(constructor.getName());
         }
         else
         {
            fail("Constructor with name " + constructor.getName() + ", not found in expected classes.");
         }
      }
      assertEquals(constructors.length, visitedConstructors.size());

      // methods
      MethodInfo[] methods = typeInfo.getDeclaredMethods();
      assertEquals(6, methods.length);
      Set<String> visitedMethods = new HashSet<String>();
      for (MethodInfo method : methods)
      {
         if (method.getName().equals("noGeneric") && method.getParameterTypes().equals("()")
            && !visitedMethods.contains(method.getName() + method.getParameterTypes()))
         {
            assertMethod(typeInfo.getMethods()[0], "noGeneric", PACKAGE + ".NoTestGeneric", Modifier.PUBLIC, "()",
               "()",
               "public java.lang.Object org.exoplatform.ide.codeassistant.asm.testclasses.NoTestGeneric.noGeneric()",
               new String[0], "Object", "java.lang.Object");
            visitedMethods.add(method.getName() + method.getParameterTypes());
         }
         else if (method.getName().equals("getGeneric") && method.getParameterTypes().equals("()")
            && !visitedMethods.contains(method.getName() + method.getParameterTypes()))
         {
            assertMethod(typeInfo.getMethods()[1], "getGeneric", PACKAGE + ".NoTestGeneric", Modifier.PUBLIC, "()",
               "()",
               "public java.lang.Object org.exoplatform.ide.codeassistant.asm.testclasses.NoTestGeneric.getGeneric()",
               new String[0], "Object", "java.lang.Object");
            visitedMethods.add(method.getName() + method.getParameterTypes());
         }
         else if (method.getName().equals("getGenerics") && method.getParameterTypes().equals("(Collection)")
            && !visitedMethods.contains(method.getName() + method.getParameterTypes()))
         {
            assertMethod(typeInfo.getMethods()[2], "getGenerics", PACKAGE + ".NoTestGeneric", Modifier.PUBLIC,
               "(Collection)", "(java.util.Collection)",
               "public java.util.Collection org.exoplatform.ide.codeassistant.asm.testclasses.NoTestGeneric."
                  + "getGenerics(java.util.Collection)", new String[0], "Collection", "java.util.Collection");
            visitedMethods.add(method.getName() + method.getParameterTypes());
         }
         else if (method.getName().equals("getTwoGenerics") && method.getParameterTypes().equals("()")
            && !visitedMethods.contains(method.getName() + method.getParameterTypes()))
         {
            assertMethod(typeInfo.getMethods()[3], "getTwoGenerics", PACKAGE + ".NoTestGeneric", Modifier.PUBLIC, "()",
               "()", "public java.util.HashMap org.exoplatform.ide.codeassistant.asm.testclasses.NoTestGeneric."
                  + "getTwoGenerics()", new String[0], "HashMap", "java.util.HashMap");
            visitedMethods.add(method.getName() + method.getParameterTypes());
         }
         else if (method.getName().equals("getHashMap") && method.getParameterTypes().equals("()")
            && !visitedMethods.contains(method.getName() + method.getParameterTypes()))
         {
            assertMethod(typeInfo.getMethods()[4], "getHashMap", PACKAGE + ".NoTestGeneric", Modifier.PUBLIC, "()",
               "()", "public java.util.HashMap org.exoplatform.ide.codeassistant.asm.testclasses.NoTestGeneric."
                  + "getHashMap()", new String[0], "HashMap", "java.util.HashMap");
            visitedMethods.add(method.getName() + method.getParameterTypes());
         }
         else if (method.getName().equals("getNumber") && method.getParameterTypes().equals("(Object)")
            && !visitedMethods.contains(method.getName() + method.getParameterTypes()))
         {
            assertMethod(
               typeInfo.getMethods()[5],
               "getNumber",
               PACKAGE + ".NoTestGeneric",
               Modifier.PUBLIC,
               "(Object)",
               "(java.lang.Object)",
               "public java.lang.Number org.exoplatform.ide.codeassistant.asm.testclasses.NoTestGeneric.getNumber(java.lang.Object)",
               new String[0], "Number", "java.lang.Number");
            visitedMethods.add(method.getName() + method.getParameterTypes());
         }
         else
         {
            fail("Method with name " + method.getName() + method.getParameterTypes()
               + ", not found in expected classes.");
         }
      }
   }

   @Test
   public void testAnnotationParsing() throws IOException
   {
      TypeInfo annotationTest = ClassParser.parse(getClassFileAsStream(PACKAGE + ".NoTestAnnotation"));

      ShortTypeInfo shortTypeInfo = annotationTest;
      assertEquals("NoTestAnnotation", shortTypeInfo.getName());
      assertEquals(PACKAGE + ".NoTestAnnotation", shortTypeInfo.getQualifiedName());
      assertEquals("ANNOTATION", shortTypeInfo.getType());
      assertEquals(Integer.valueOf(Modifier.PUBLIC | Modifier.ABSTRACT | Modifier.INTERFACE
         | TypeInfoBuilder.MODIFIER_ANNOTATION), shortTypeInfo.getModifiers());

      TypeInfo typeInfo = annotationTest;
      assertEquals("NoTestAnnotation", typeInfo.getName());
      assertEquals(PACKAGE + ".NoTestAnnotation", typeInfo.getQualifiedName());
      assertEquals("ANNOTATION", typeInfo.getType());
      assertEquals("java.lang.Object", typeInfo.getSuperClass());
      assertEquals(1, typeInfo.getInterfaces().length);
      assertEquals("java.lang.annotation.Annotation", typeInfo.getInterfaces()[0]);
      assertEquals(Integer.valueOf(Modifier.PUBLIC | Modifier.ABSTRACT | Modifier.INTERFACE
         | TypeInfoBuilder.MODIFIER_ANNOTATION), typeInfo.getModifiers());

      assertEquals(0, typeInfo.getFields().length);
      assertEquals(0, typeInfo.getConstructors().length);
      // methods
      MethodInfo[] methods = typeInfo.getDeclaredMethods();
      assertEquals(3, methods.length);
      Set<String> visitedMethods = new HashSet<String>();
      for (MethodInfo method : methods)
      {
         if (method.getName().equals("a") && method.getParameterTypes().equals("()")
            && !visitedMethods.contains(method.getName() + method.getParameterTypes()))
         {
            assertMethod(method, "a", typeInfo.getQualifiedName(), Modifier.PUBLIC | Modifier.ABSTRACT, "()", "()",
               "public abstract int " + PACKAGE + ".NoTestAnnotation.a()", new String[0], "int", "int");
            visitedMethods.add(method.getName() + method.getParameterTypes());
         }
         else if (method.getName().equals("b") && method.getParameterTypes().equals("()")
            && !visitedMethods.contains(method.getName() + method.getParameterTypes()))
         {
            assertMethod(method, "b", typeInfo.getQualifiedName(), Modifier.PUBLIC | Modifier.ABSTRACT, "()", "()",
               "public abstract double " + PACKAGE + ".NoTestAnnotation.b()", new String[0], "double", "double");
            visitedMethods.add(method.getName() + method.getParameterTypes());
         }
         else if (method.getName().equals("c") && method.getParameterTypes().equals("()")
            && !visitedMethods.contains(method.getName() + method.getParameterTypes()))
         {
            assertMethod(method, "c", typeInfo.getQualifiedName(), Modifier.PUBLIC | Modifier.ABSTRACT, "()", "()",
               "public abstract java.lang.String " + PACKAGE + ".NoTestAnnotation.c()", new String[0], "String",
               "java.lang.String");
            visitedMethods.add(method.getName() + method.getParameterTypes());
         }
         else
         {
            fail("Method with name " + method.getName() + method.getParameterTypes()
               + ", not found in expected classes.");
         }
      }
      assertEquals(methods.length, visitedMethods.size());

   }

   @Test
   public void testTestClass() throws IOException
   {
      TypeInfo testClass = ClassParser.parse(getClassFileAsStream(PACKAGE + ".NoTestClass"));

      ShortTypeInfo shortTypeInfo = testClass;
      assertEquals("NoTestClass", shortTypeInfo.getName());
      assertEquals(PACKAGE + ".NoTestClass", shortTypeInfo.getQualifiedName());
      assertEquals("CLASS", shortTypeInfo.getType());
      assertEquals(Integer.valueOf(Modifier.PUBLIC | Modifier.SYNCHRONIZED), shortTypeInfo.getModifiers());

      TypeInfo typeInfo = testClass;
      assertEquals("NoTestClass", typeInfo.getName());
      assertEquals(PACKAGE + ".NoTestClass", typeInfo.getQualifiedName());
      assertEquals("CLASS", typeInfo.getType());
      assertEquals(PACKAGE + ".NoTestSuper", typeInfo.getSuperClass());
      assertEquals(2, typeInfo.getInterfaces().length);
      assertEquals(PACKAGE + ".NoTestInterface", typeInfo.getInterfaces()[0]);
      assertEquals(PACKAGE + ".NoTestInterface2", typeInfo.getInterfaces()[1]);
      assertEquals(Integer.valueOf(Modifier.PUBLIC | Modifier.SYNCHRONIZED), typeInfo.getModifiers());

      // fields
      FieldInfo[] fields = typeInfo.getDeclaredFields();
      assertEquals(4, fields.length);
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
            assertField(field, "b", "java.lang.String", Modifier.PUBLIC | Modifier.FINAL, typeInfo.getQualifiedName());
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
            fail("Field with name " + field.getName() + ", not found in expected classes.");
         }
      }
      assertEquals(fields.length, visitedFields.size());

      // constructors
      RoutineInfo[] constructors = typeInfo.getDeclaredConstructors();
      assertEquals(1, constructors.length);
      Set<String> visitedConstructors = new HashSet<String>();
      for (RoutineInfo constructor : constructors)
      {
         if (constructor.getName().equals("NoTestClass") && !visitedConstructors.contains(constructor.getName()))
         {
            assertRoutine(constructor, "NoTestClass", typeInfo.getQualifiedName(), Modifier.PUBLIC, "(int, String)",
               "(int, java.lang.String)", "public " + PACKAGE
                  + ".NoTestClass(int, java.lang.String) throws java.lang.ClassNotFoundException",
               new String[]{"java.lang.ClassNotFoundException"});
            visitedConstructors.add(constructor.getName());
         }
         else
         {
            fail("Constructor with name " + constructor.getName() + ", not found in expected classes.");
         }
      }
      assertEquals(constructors.length, visitedConstructors.size());

      // methods
      MethodInfo[] methods = typeInfo.getDeclaredMethods();
      assertEquals(5, methods.length);
      Set<String> visitedMethods = new HashSet<String>();
      for (MethodInfo method : methods)
      {
         if (method.getName().equals("method3")
            && method.getParameterTypes().equals("(double, int, char, float[][][], String[])")
            && !visitedMethods.contains(method.getName() + method.getParameterTypes()))
         {
            assertMethod(method, "method3", typeInfo.getQualifiedName(), Modifier.PUBLIC,
               "(double, int, char, float[][][], String[])", "(double, int, char, float[][][], java.lang.String[])",
               "public int " + PACKAGE + ".NoTestClass.method3(double, int, char, float[][][], java.lang.String[])",
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
                  + ".NoTestClass.method4(java.lang.String, java.lang.Boolean, boolean, int[][][][][])", new String[0],
               "void", "void");
            visitedMethods.add(method.getName() + method.getParameterTypes());
         }
         else if (method.getName().equals("method1") && method.getParameterTypes().equals("()")
            && !visitedMethods.contains(method.getName() + method.getParameterTypes()))
         {
            assertMethod(method, "method1", typeInfo.getQualifiedName(), Modifier.PUBLIC, "()", "()", "public int "
               + PACKAGE + ".NoTestClass.method1()", new String[0], "int", "int");
            visitedMethods.add(method.getName() + method.getParameterTypes());
         }
         else if (method.getName().equals("method2") && method.getParameterTypes().equals("(int)")
            && !visitedMethods.contains(method.getName() + method.getParameterTypes()))
         {
            assertMethod(method, "method2", typeInfo.getQualifiedName(), Modifier.PUBLIC, "(int)", "(int)",
               "public int " + PACKAGE + ".NoTestClass.method2(int)", new String[0], "int", "int");
            visitedMethods.add(method.getName() + method.getParameterTypes());
         }
         else if (method.getName().equals("method0") && method.getParameterTypes().equals("(int)")
            && !visitedMethods.contains(method.getName() + method.getParameterTypes()))
         {
            assertMethod(method, "method0", typeInfo.getQualifiedName(), Modifier.PROTECTED, "(int)", "(int)",
               "protected void " + PACKAGE + ".NoTestClass.method0(int)", new String[0], "void", "void");
            visitedMethods.add(method.getName() + method.getParameterTypes());
         }
         else
         {
            fail("Method with name " + method.getName() + method.getParameterTypes()
               + ", not found in expected classes.");
         }
      }
      assertEquals(methods.length, visitedMethods.size());
   }

   @Test
   public void testTestEnum() throws IOException
   {
      TypeInfo testEnum = ClassParser.parse(getClassFileAsStream(PACKAGE + ".NoTestEnum"));

      ShortTypeInfo shortTypeInfo = testEnum;
      assertEquals("NoTestEnum", shortTypeInfo.getName());
      assertEquals(PACKAGE + ".NoTestEnum", shortTypeInfo.getQualifiedName());
      assertEquals("ENUM", shortTypeInfo.getType());

      assertEquals(
         Integer.valueOf(Modifier.SYNCHRONIZED | Modifier.FINAL | TypeInfoBuilder.MODIFIER_ENUM | Modifier.PUBLIC),
         shortTypeInfo.getModifiers());

      TypeInfo typeInfo = testEnum;
      assertEquals("NoTestEnum", typeInfo.getName());
      assertEquals(PACKAGE + ".NoTestEnum", typeInfo.getQualifiedName());
      assertEquals("ENUM", typeInfo.getType());
      assertEquals("java.lang.Enum", typeInfo.getSuperClass());
      assertEquals(0, typeInfo.getInterfaces().length);
      assertEquals(
         Integer.valueOf(Modifier.SYNCHRONIZED | Modifier.FINAL | TypeInfoBuilder.MODIFIER_ENUM | Modifier.PUBLIC),
         typeInfo.getModifiers());

      // fields
      FieldInfo[] fields = typeInfo.getDeclaredFields();
      assertEquals(4, fields.length);
      Set<String> visitedFields = new HashSet<String>();
      for (FieldInfo field : fields)
      {
         if (field.getName().equals("ENUM1") && !visitedFields.contains(field.getName()))
         {
            assertField(field, "ENUM1", PACKAGE + ".NoTestEnum", Modifier.PUBLIC | Modifier.STATIC
               | TypeInfoBuilder.MODIFIER_ENUM | Modifier.FINAL, typeInfo.getQualifiedName());
            visitedFields.add(field.getName());
         }
         else if (field.getName().equals("ENUM2") && !visitedFields.contains(field.getName()))
         {
            assertField(field, "ENUM2", PACKAGE + ".NoTestEnum", Modifier.PUBLIC | Modifier.STATIC
               | TypeInfoBuilder.MODIFIER_ENUM | Modifier.FINAL, typeInfo.getQualifiedName());
            visitedFields.add(field.getName());
         }
         else if (field.getName().equals("ENUM3") && !visitedFields.contains(field.getName()))
         {
            assertField(field, "ENUM3", PACKAGE + ".NoTestEnum", Modifier.PUBLIC | Modifier.STATIC
               | TypeInfoBuilder.MODIFIER_ENUM | Modifier.FINAL, typeInfo.getQualifiedName());
            visitedFields.add(field.getName());
         }
         else if (field.getName().equals("ENUM4") && !visitedFields.contains(field.getName()))
         {
            assertField(field, "ENUM4", PACKAGE + ".NoTestEnum", Modifier.PUBLIC | Modifier.STATIC
               | TypeInfoBuilder.MODIFIER_ENUM | Modifier.FINAL, typeInfo.getQualifiedName());
            visitedFields.add(field.getName());
         }
         else
         {
            fail("Field with name " + field.getName() + ", not found in expected classes.");
         }
      }
      assertEquals(fields.length, visitedFields.size());

      // constructors
      RoutineInfo[] constructors = typeInfo.getDeclaredConstructors();
      assertEquals(1, constructors.length);
      Set<String> visitedConstructors = new HashSet<String>();
      for (RoutineInfo constructor : constructors)
      {
         if (constructor.getName().equals("NoTestEnum") && !visitedConstructors.contains(constructor.getName()))
         {
            assertRoutine(constructor, "NoTestEnum", typeInfo.getQualifiedName(), Modifier.PRIVATE, "(String, int)",
               "(java.lang.String, int)", "private " + PACKAGE + ".NoTestEnum(java.lang.String, int)", new String[0]);
            visitedConstructors.add(constructor.getName());
         }
         else
         {
            fail("Constructor with name " + constructor.getName() + ", not found in expected classes.");
         }
      }
      assertEquals(constructors.length, visitedConstructors.size());

      // methods
      MethodInfo[] methods = typeInfo.getDeclaredMethods();
      assertEquals(2, methods.length);
      Set<String> visitedMethods = new HashSet<String>();
      for (MethodInfo method : methods)
      {
         if (method.getName().equals("values") && method.getParameterTypes().equals("()")
            && !visitedMethods.contains(method.getName() + method.getParameterTypes()))
         {
            assertMethod(method, "values", typeInfo.getQualifiedName(), Modifier.PUBLIC | Modifier.STATIC, "()", "()",
               "public static " + PACKAGE + ".NoTestEnum[] " + PACKAGE + ".NoTestEnum.values()", new String[0],
               "NoTestEnum[]", PACKAGE + ".NoTestEnum[]");
            visitedMethods.add(method.getName() + method.getParameterTypes());
         }
         else if (method.getName().equals("valueOf") && method.getParameterTypes().equals("(String)")
            && !visitedMethods.contains(method.getName() + method.getParameterTypes()))
         {
            assertMethod(method, "valueOf", typeInfo.getQualifiedName(), Modifier.PUBLIC | Modifier.STATIC, "(String)",
               "(java.lang.String)", "public static " + PACKAGE + ".NoTestEnum " + PACKAGE
                  + ".NoTestEnum.valueOf(java.lang.String)", new String[0], "NoTestEnum", PACKAGE + ".NoTestEnum");
            visitedMethods.add(method.getName() + method.getParameterTypes());
         }
         else
         {
            fail("Method with name " + method.getName() + method.getParameterTypes()
               + ", not found in expected classes.");
         }
      }
      assertEquals(methods.length, visitedMethods.size());
   }

   @Test
   public void testTestInterface() throws IOException
   {
      TypeInfo testInterface = ClassParser.parse(getClassFileAsStream(PACKAGE + ".NoTestInterface"));

      ShortTypeInfo shortTypeInfo = testInterface;
      assertEquals("NoTestInterface", shortTypeInfo.getName());
      assertEquals(PACKAGE + ".NoTestInterface", shortTypeInfo.getQualifiedName());
      assertEquals("INTERFACE", shortTypeInfo.getType());
      assertEquals(Integer.valueOf(Modifier.ABSTRACT | Modifier.INTERFACE | Modifier.PUBLIC),
         shortTypeInfo.getModifiers());

      TypeInfo typeInfo = testInterface;
      assertEquals("NoTestInterface", typeInfo.getName());
      assertEquals(PACKAGE + ".NoTestInterface", typeInfo.getQualifiedName());
      assertEquals("INTERFACE", typeInfo.getType());
      assertEquals("java.lang.Object", typeInfo.getSuperClass());
      assertEquals(0, typeInfo.getInterfaces().length);
      assertEquals(Integer.valueOf(Modifier.ABSTRACT | Modifier.INTERFACE | Modifier.PUBLIC), typeInfo.getModifiers());

      assertEquals(0, typeInfo.getFields().length);
      assertEquals(0, typeInfo.getConstructors().length);

      // methods
      MethodInfo[] methods = typeInfo.getDeclaredMethods();
      assertEquals(2, methods.length);
      Set<String> visitedMethods = new HashSet<String>();
      for (MethodInfo method : methods)
      {
         if (method.getName().equals("method1") && method.getParameterTypes().equals("()")
            && !visitedMethods.contains(method.getName() + method.getParameterTypes()))
         {
            assertMethod(method, "method1", typeInfo.getQualifiedName(), Modifier.PUBLIC | Modifier.ABSTRACT, "()",
               "()", "public abstract int " + PACKAGE + ".NoTestInterface.method1()", new String[0], "int", "int");
            visitedMethods.add(method.getName() + method.getParameterTypes());
         }
         else if (method.getName().equals("method2") && method.getParameterTypes().equals("(int)")
            && !visitedMethods.contains(method.getName() + method.getParameterTypes()))
         {
            assertMethod(method, "method2", typeInfo.getQualifiedName(), Modifier.PUBLIC | Modifier.ABSTRACT, "(int)",
               "(int)", "public abstract int " + PACKAGE + ".NoTestInterface.method2(int)", new String[0], "int", "int");
            visitedMethods.add(method.getName() + method.getParameterTypes());
         }
         else
         {
            fail("Method with name " + method.getName() + method.getParameterTypes()
               + ", not found in expected classes.");
         }
      }
      assertEquals(methods.length, visitedMethods.size());
   }

   @Test
   public void testTestInterface2() throws IOException
   {
      TypeInfo testInterface2 = ClassParser.parse(getClassFileAsStream(PACKAGE + ".NoTestInterface2"));

      ShortTypeInfo shortTypeInfo = testInterface2;
      assertEquals("NoTestInterface2", shortTypeInfo.getName());
      assertEquals(PACKAGE + ".NoTestInterface2", shortTypeInfo.getQualifiedName());
      assertEquals("INTERFACE", shortTypeInfo.getType());
      assertEquals(Integer.valueOf(Modifier.ABSTRACT | Modifier.INTERFACE | Modifier.PUBLIC),
         shortTypeInfo.getModifiers());

      TypeInfo typeInfo = testInterface2;
      assertEquals("NoTestInterface2", typeInfo.getName());
      assertEquals(PACKAGE + ".NoTestInterface2", typeInfo.getQualifiedName());
      assertEquals("INTERFACE", typeInfo.getType());
      assertEquals("java.lang.Object", typeInfo.getSuperClass());
      assertEquals(0, typeInfo.getInterfaces().length);
      assertEquals(Integer.valueOf(Modifier.ABSTRACT | Modifier.INTERFACE | Modifier.PUBLIC), typeInfo.getModifiers());

      assertEquals(0, typeInfo.getFields().length);
      assertEquals(0, typeInfo.getConstructors().length);

      // methods
      MethodInfo[] methods = typeInfo.getDeclaredMethods();
      assertEquals(2, methods.length);
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
                  + ".NoTestInterface2.method3(double, int, char, float[][][], java.lang.String[])", new String[0],
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
                  + ".NoTestInterface2.method4(java.lang.String, java.lang.Boolean, boolean, int[][][][][])",
               new String[0], "void", "void");
            visitedMethods.add(method.getName() + method.getParameterTypes());
         }
         else
         {
            fail("Method with name " + method.getName() + method.getParameterTypes()
               + ", not found in expected classes.");
         }
      }
      assertEquals(methods.length, visitedMethods.size());
   }

   @Test
   public void testTestSuper() throws IOException
   {
      TypeInfo testSuper = ClassParser.parse(getClassFileAsStream(PACKAGE + ".NoTestSuper"));

      ShortTypeInfo shortTypeInfo = testSuper;
      assertEquals("NoTestSuper", shortTypeInfo.getName());
      assertEquals(PACKAGE + ".NoTestSuper", shortTypeInfo.getQualifiedName());
      assertEquals("CLASS", shortTypeInfo.getType());
      assertEquals(Integer.valueOf(Modifier.PUBLIC | Modifier.ABSTRACT | Modifier.SYNCHRONIZED),
         shortTypeInfo.getModifiers());

      TypeInfo typeInfo = testSuper;
      assertEquals("NoTestSuper", typeInfo.getName());
      assertEquals(PACKAGE + ".NoTestSuper", typeInfo.getQualifiedName());
      assertEquals("CLASS", typeInfo.getType());
      assertEquals("java.lang.Object", typeInfo.getSuperClass());
      assertEquals(0, typeInfo.getInterfaces().length);
      assertEquals(Integer.valueOf(Modifier.PUBLIC | Modifier.ABSTRACT | Modifier.SYNCHRONIZED),
         typeInfo.getModifiers());

      // fields
      FieldInfo[] fields = typeInfo.getDeclaredFields();
      assertEquals(1, fields.length);
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
            fail("Field with name " + field.getName() + ", not found in expected classes.");
         }
      }
      assertEquals(fields.length, visitedFields.size());

      // constructors
      RoutineInfo[] constructors = typeInfo.getDeclaredConstructors();
      assertEquals(1, constructors.length);
      Set<String> visitedConstructors = new HashSet<String>();
      for (RoutineInfo constructor : constructors)
      {
         if (constructor.getName().equals("NoTestSuper") && !visitedConstructors.contains(constructor.getName()))
         {
            assertRoutine(constructor, "NoTestSuper", typeInfo.getQualifiedName(), Modifier.PUBLIC, "()", "()",
               "public " + PACKAGE + ".NoTestSuper()", new String[0]);
            visitedConstructors.add(constructor.getName());
         }
         else
         {
            fail("Constructor with name " + constructor.getName() + ", not found in expected classes.");
         }
      }
      assertEquals(constructors.length, visitedConstructors.size());

      // methods
      MethodInfo[] methods = typeInfo.getDeclaredMethods();
      assertEquals(5, methods.length);
      Set<String> visitedMethods = new HashSet<String>();
      for (MethodInfo method : methods)
      {
         if (method.getName().equals("method0") && method.getParameterTypes().equals("(int)")
            && !visitedMethods.contains(method.getName() + method.getParameterTypes()))
         {
            assertMethod(method, "method0", typeInfo.getQualifiedName(), Modifier.PROTECTED | Modifier.ABSTRACT,
               "(int)", "(int)", "protected abstract void " + PACKAGE + ".NoTestSuper.method0(int)", new String[0],
               "void", "void");
            visitedMethods.add(method.getName() + method.getParameterTypes());
         }
         else if (method.getName().equals("method") && method.getParameterTypes().equals("()")
            && !visitedMethods.contains(method.getName() + method.getParameterTypes()))
         {
            assertMethod(method, "method", typeInfo.getQualifiedName(), Modifier.PUBLIC, "()", "()", "public void "
               + PACKAGE + ".NoTestSuper.method()", new String[0], "void", "void");
            visitedMethods.add(method.getName() + method.getParameterTypes());
         }
         else if (method.getName().equals("method") && method.getParameterTypes().equals("(int)")
            && !visitedMethods.contains(method.getName() + method.getParameterTypes()))
         {
            assertMethod(method, "method", typeInfo.getQualifiedName(), Modifier.PUBLIC, "(int)", "(int)",
               "public void " + PACKAGE + ".NoTestSuper.method(int) throws java.lang.RuntimeException, "
                  + "java.io.IOException", new String[]{"java.lang.RuntimeException", "java.io.IOException"}, "void",
               "void");
            visitedMethods.add(method.getName() + method.getParameterTypes());
         }
         else if (method.getName().equals("method") && method.getParameterTypes().equals("(double)")
            && !visitedMethods.contains(method.getName() + method.getParameterTypes()))
         {
            assertMethod(method, "method", typeInfo.getQualifiedName(), Modifier.PUBLIC, "(double)", "(double)",
               "public void " + PACKAGE + ".NoTestSuper.method(double) throws java.lang.Exception",
               new String[]{"java.lang.Exception",}, "void", "void");
            visitedMethods.add(method.getName() + method.getParameterTypes());
         }
         else if (method.getName().equals("toString") && method.getParameterTypes().equals("()")
            && !visitedMethods.contains(method.getName() + method.getParameterTypes()))
         {
            assertMethod(method, "toString", typeInfo.getQualifiedName(), Modifier.PUBLIC, "()", "()",
               "public java.lang.String " + PACKAGE + ".NoTestSuper.toString()", new String[0], "String",
               "java.lang.String");
            visitedMethods.add(method.getName() + method.getParameterTypes());
         }
         else
         {
            fail("Method with name " + method.getName() + method.getParameterTypes()
               + ", not found in expected classes.");
         }
      }
      assertEquals(methods.length, visitedMethods.size());
   }

   private void assertRoutine(RoutineInfo routine, String name, String declaredClass, int modifiers,
      String parameterTypes, String genericParameterTypes, String generic, String[] exceptions)
   {
      assertEquals(name, routine.getName());
      assertEquals(declaredClass, routine.getDeclaringClass());
      assertEquals(Integer.valueOf(modifiers), routine.getModifiers());
      assertEquals(exceptions.length, routine.getGenericExceptionTypes().length);
      for (int i = 0; i < exceptions.length; i++)
      {
         assertEquals(exceptions[i], routine.getGenericExceptionTypes()[i]);
      }
      assertEquals(parameterTypes, routine.getParameterTypes());
      assertEquals(genericParameterTypes, routine.getGenericParameterTypes());
      assertEquals(generic, routine.getGeneric());
   }

   private void assertMethod(MethodInfo method, String name, String declaredClass, int modifiers,
      String parameterTypes, String genericParameterTypes, String generic, String[] exceptions, String returnType,
      String genericReturnType)
   {
      assertRoutine(method, name, declaredClass, modifiers, parameterTypes, genericParameterTypes, generic, exceptions);
      assertEquals(returnType, method.getReturnType());
      assertEquals(genericReturnType, method.getGenericReturnType());
   }

   private void assertField(FieldInfo field, String name, String type, int modifiers, String declaredClass)
   {
      assertEquals(name, field.getName());
      assertEquals(type, field.getType());
      assertEquals(Integer.valueOf(modifiers), field.getModifiers());
      assertEquals(declaredClass, field.getDeclaringClass());
   }

}