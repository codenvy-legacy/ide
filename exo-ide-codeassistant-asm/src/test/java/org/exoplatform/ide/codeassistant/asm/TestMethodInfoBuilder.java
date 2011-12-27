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

import org.junit.Test;

public class TestMethodInfoBuilder
{
   //
   //   private final int access = Modifier.PUBLIC & Modifier.ABSTRACT;
   //
   //   private final String name = "Method1";
   //
   //   private final String[] exceptions = {};
   //
   //   private final String desc = "(Z)Ljava/lang/String;";
   //
   //   private final String declaredClass = "org.exoplatform.test.TestClass";
   //
   //   @Test
   //   public void testAccess()
   //   {
   //      MethodInfoBuilder methodInfoBuilder =
   //         new MethodInfoBuilder(Modifier.PUBLIC | Modifier.ABSTRACT, name, exceptions, desc, declaredClass);
   //      MethodInfo methodInfo = methodInfoBuilder.buildMethodInfo();
   //
   //      assertEquals(Modifier.PUBLIC | Modifier.ABSTRACT, methodInfo.getModifiers());
   //      assertEquals("public abstract", methodInfo.modifierToString());
   //   }
   //
   //   @Test
   //   public void testName()
   //   {
   //      MethodInfoBuilder methodInfoBuilder = new MethodInfoBuilder(access, "Method1", exceptions, desc, declaredClass);
   //      MethodInfo methodInfo = methodInfoBuilder.buildMethodInfo();
   //
   //      assertEquals("Method1", methodInfo.getName());
   //   }
   //
   //   @Test
   //   public void testExceptions()
   //   {
   //      MethodInfoBuilder methodInfoBuilder =
   //         new MethodInfoBuilder(access, name, new String[]{"java/io/IOException", "java/lang/Exception"}, desc,
   //            declaredClass);
   //      MethodInfo methodInfo = methodInfoBuilder.buildMethodInfo();
   //
   //      assertEquals(2, methodInfo.getGenericExceptionTypes().length);
   //      assertEquals("java.io.IOException", methodInfo.getGenericExceptionTypes()[0]);
   //      assertEquals("java.lang.Exception", methodInfo.getGenericExceptionTypes()[1]);
   //   }
   //
   //   @Test
   //   public void testSimpleReturnType()
   //   {
   //      MethodInfoBuilder methodInfoBuilder = new MethodInfoBuilder(access, name, exceptions, "()Z", declaredClass);
   //      MethodInfo methodInfo = methodInfoBuilder.buildMethodInfo();
   //
   //      assertEquals("boolean", methodInfo.getReturnType());
   //      assertEquals("boolean", methodInfo.getGenericReturnType());
   //   }
   //
   //   @Test
   //   public void testVoidReturnType()
   //   {
   //      MethodInfoBuilder methodInfoBuilder = new MethodInfoBuilder(access, name, exceptions, "()V", declaredClass);
   //      MethodInfo methodInfo = methodInfoBuilder.buildMethodInfo();
   //
   //      assertEquals("void", methodInfo.getReturnType());
   //      assertEquals("void", methodInfo.getGenericReturnType());
   //   }
   //
   //   @Test
   //   public void testArrayReturnType()
   //   {
   //      MethodInfoBuilder methodInfoBuilder = new MethodInfoBuilder(access, name, exceptions, "()[[Z", declaredClass);
   //      MethodInfo methodInfo = methodInfoBuilder.buildMethodInfo();
   //
   //      assertEquals("boolean[][]", methodInfo.getReturnType());
   //      assertEquals("boolean[][]", methodInfo.getGenericReturnType());
   //   }
   //
   //   @Test
   //   public void testObjectReturnType()
   //   {
   //      MethodInfoBuilder methodInfoBuilder =
   //         new MethodInfoBuilder(access, name, exceptions, "()Ljava/lang/String;", declaredClass);
   //      MethodInfo methodInfo = methodInfoBuilder.buildMethodInfo();
   //
   //      assertEquals("String", methodInfo.getReturnType());
   //      assertEquals("java.lang.String", methodInfo.getGenericReturnType());
   //   }
   //
   //   @Test
   //   public void testSimpleParam()
   //   {
   //      MethodInfoBuilder methodInfoBuilder = new MethodInfoBuilder(access, name, exceptions, "(Z)V", declaredClass);
   //      MethodInfo methodInfo = methodInfoBuilder.buildMethodInfo();
   //
   //      assertEquals("(boolean)", methodInfo.getParameterTypes());
   //      assertEquals("(boolean)", methodInfo.getGenericParameterTypes());
   //   }
   //
   //   @Test
   //   public void testArrayParam()
   //   {
   //      MethodInfoBuilder methodInfoBuilder = new MethodInfoBuilder(access, name, exceptions, "([Z)V", declaredClass);
   //      MethodInfo methodInfo = methodInfoBuilder.buildMethodInfo();
   //
   //      assertEquals("(boolean[])", methodInfo.getParameterTypes());
   //      assertEquals("(boolean[])", methodInfo.getGenericParameterTypes());
   //   }
   //
   //   @Test
   //   public void testTwoParams()
   //   {
   //      MethodInfoBuilder methodInfoBuilder = new MethodInfoBuilder(access, name, exceptions, "(Z I)V", declaredClass);
   //      MethodInfo methodInfo = methodInfoBuilder.buildMethodInfo();
   //
   //      assertEquals("(boolean, int)", methodInfo.getParameterTypes());
   //      assertEquals("(boolean, int)", methodInfo.getGenericParameterTypes());
   //   }
   //
   //   @Test
   //   public void testObjectParams()
   //   {
   //      MethodInfoBuilder methodInfoBuilder =
   //         new MethodInfoBuilder(access, name, exceptions, "(Ljava/lang/String; Ljava/lang/Object;)V", declaredClass);
   //      MethodInfo methodInfo = methodInfoBuilder.buildMethodInfo();
   //
   //      assertEquals("(String, Object)", methodInfo.getParameterTypes());
   //      assertEquals("(java.lang.String, java.lang.Object)", methodInfo.getGenericParameterTypes());
   //   }
   //
   //   @Test
   //   public void testDeclaringClass()
   //   {
   //      MethodInfoBuilder methodInfoBuilder =
   //         new MethodInfoBuilder(access, name, exceptions, desc, "org.exoplatform.test.TestClass");
   //      MethodInfo methodInfo = methodInfoBuilder.buildMethodInfo();
   //
   //      assertEquals("org.exoplatform.test.TestClass", methodInfo.getDeclaringClass());
   //   }
   //
   //   @Test
   //   public void testGenericPublicWithoutParams()
   //   {
   //      MethodInfoBuilder methodInfoBuilder =
   //         new MethodInfoBuilder(Modifier.PUBLIC, "method1", new String[0], "()V", declaredClass);
   //      MethodInfo methodInfo = methodInfoBuilder.buildMethodInfo();
   //
   //      assertEquals("public void org.exoplatform.test.TestClass.method1()", methodInfo.getGeneric());
   //   }
   //
   //   @Test
   //   public void testGenericProtectedWithSimpleParam()
   //   {
   //      MethodInfoBuilder methodInfoBuilder =
   //         new MethodInfoBuilder(Modifier.PROTECTED, "method1", new String[0], "(F)V", declaredClass);
   //      MethodInfo methodInfo = methodInfoBuilder.buildMethodInfo();
   //
   //      assertEquals("protected void org.exoplatform.test.TestClass.method1(float)", methodInfo.getGeneric());
   //   }
   //
   //   @Test
   //   public void testGenericPublicAbstractWithTwoParams()
   //   {
   //      MethodInfoBuilder methodInfoBuilder =
   //         new MethodInfoBuilder(Modifier.PUBLIC | Modifier.ABSTRACT, "method1", new String[0],
   //            "(Z Ljava/lang/String;)D", declaredClass);
   //      MethodInfo methodInfo = methodInfoBuilder.buildMethodInfo();
   //
   //      assertEquals("public abstract double org.exoplatform.test.TestClass.method1(boolean, java.lang.String)",
   //         methodInfo.getGeneric());
   //   }
   //
   //   @Test
   //   public void testGenericPublicWithExceptions()
   //   {
   //      MethodInfoBuilder methodInfoBuilder =
   //         new MethodInfoBuilder(Modifier.PUBLIC, "method1", new String[]{"java/lang/Exception", "java/io/IOException"},
   //            "()V", declaredClass);
   //      MethodInfo methodInfo = methodInfoBuilder.buildMethodInfo();
   //
   //      assertEquals(
   //         "public void org.exoplatform.test.TestClass.method1() throws java.lang.Exception, java.io.IOException",
   //         methodInfo.getGeneric());
   //   }

   @Test
   public void testName() throws Exception
   {

   }

}
