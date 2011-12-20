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

import org.exoplatform.ide.codeassistant.jvm.RoutineInfo;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Modifier;

public class TestConstructorInfoBuilder
{

   private final int access = Modifier.PUBLIC & Modifier.ABSTRACT;

   private final String[] exceptions = {};

   private final String desc = "(Z)";

   private final String declaredClass = "org.exoplatform.test.TestClass";

   @Test
   public void testAccess()
   {
      ConstructorInfoBuilder constructorInfoBuilder =
         new ConstructorInfoBuilder(Modifier.PUBLIC | Modifier.ABSTRACT, exceptions, desc, declaredClass);
      RoutineInfo constructorInfo = constructorInfoBuilder.buildConstructorInfo();

      Assert.assertEquals(Integer.valueOf(Modifier.PUBLIC | Modifier.ABSTRACT), constructorInfo.getModifiers());
      Assert.assertEquals("public abstract", constructorInfo.modifierToString());
   }

   @Test
   public void testExceptions()
   {
      ConstructorInfoBuilder constructorInfoBuilder =
         new ConstructorInfoBuilder(access, new String[]{"java/io/IOException", "java/lang/Exception"}, desc,
            declaredClass);
      RoutineInfo constructorInfo = constructorInfoBuilder.buildConstructorInfo();

      Assert.assertEquals(2, constructorInfo.getGenericExceptionTypes().length);
      Assert.assertEquals("java.io.IOException", constructorInfo.getGenericExceptionTypes()[0]);
      Assert.assertEquals("java.lang.Exception", constructorInfo.getGenericExceptionTypes()[1]);
   }

   @Test
   public void testSimpleParam()
   {
      ConstructorInfoBuilder constructorInfoBuilder =
         new ConstructorInfoBuilder(access, exceptions, "(Z)", declaredClass);
      RoutineInfo constructorInfo = constructorInfoBuilder.buildConstructorInfo();

      Assert.assertEquals("(boolean)", constructorInfo.getParameterTypes());
      Assert.assertEquals("(boolean)", constructorInfo.getGenericParameterTypes());
   }

   @Test
   public void testArrayParam()
   {
      ConstructorInfoBuilder constructorInfoBuilder =
         new ConstructorInfoBuilder(access, exceptions, "([Z)", declaredClass);
      RoutineInfo constructorInfo = constructorInfoBuilder.buildConstructorInfo();

      Assert.assertEquals("(boolean[])", constructorInfo.getParameterTypes());
      Assert.assertEquals("(boolean[])", constructorInfo.getGenericParameterTypes());
   }

   @Test
   public void testTwoParams()
   {
      ConstructorInfoBuilder constructorInfoBuilder =
         new ConstructorInfoBuilder(access, exceptions, "(Z I)", declaredClass);
      RoutineInfo constructorInfo = constructorInfoBuilder.buildConstructorInfo();

      Assert.assertEquals("(boolean, int)", constructorInfo.getParameterTypes());
      Assert.assertEquals("(boolean, int)", constructorInfo.getGenericParameterTypes());
   }

   @Test
   public void testObjectParams()
   {
      ConstructorInfoBuilder constructorInfoBuilder =
         new ConstructorInfoBuilder(access, exceptions, "(Ljava/lang/String; Ljava/lang/Object;)", declaredClass);
      RoutineInfo constructorInfo = constructorInfoBuilder.buildConstructorInfo();

      Assert.assertEquals("(String, Object)", constructorInfo.getParameterTypes());
      Assert.assertEquals("(java.lang.String, java.lang.Object)", constructorInfo.getGenericParameterTypes());
   }

   @Test
   public void testDeclaringClass()
   {
      ConstructorInfoBuilder constructorInfoBuilder =
         new ConstructorInfoBuilder(access, exceptions, desc, "org.exoplatform.test.TestClass");
      RoutineInfo constructorInfo = constructorInfoBuilder.buildConstructorInfo();

      Assert.assertEquals("org.exoplatform.test.TestClass", constructorInfo.getDeclaringClass());
   }

   @Test
   public void testGenericPublicWithoutParams()
   {
      ConstructorInfoBuilder constructorInfoBuilder =
         new ConstructorInfoBuilder(Modifier.PUBLIC, new String[0], "()", declaredClass);
      RoutineInfo constructorInfo = constructorInfoBuilder.buildConstructorInfo();

      Assert.assertEquals("public org.exoplatform.test.TestClass()", constructorInfo.getGeneric());
   }

   @Test
   public void testGenericProtectedWithSimpleParam()
   {
      ConstructorInfoBuilder constructorInfoBuilder =
         new ConstructorInfoBuilder(Modifier.PROTECTED, new String[0], "(F)", declaredClass);
      RoutineInfo constructorInfo = constructorInfoBuilder.buildConstructorInfo();

      Assert.assertEquals("protected org.exoplatform.test.TestClass(float)", constructorInfo.getGeneric());
   }

   @Test
   public void testGenericPublicAbstractWithTwoParams()
   {
      ConstructorInfoBuilder constructorInfoBuilder =
         new ConstructorInfoBuilder(Modifier.PUBLIC | Modifier.ABSTRACT, new String[0], "(Z Ljava/lang/String;)",
            declaredClass);
      RoutineInfo constructorInfo = constructorInfoBuilder.buildConstructorInfo();

      Assert.assertEquals("public abstract org.exoplatform.test.TestClass(boolean, java.lang.String)",
         constructorInfo.getGeneric());
   }

   @Test
   public void testGenericPublicWithExceptions()
   {
      ConstructorInfoBuilder constructorInfoBuilder =
         new ConstructorInfoBuilder(Modifier.PUBLIC, new String[]{"java/lang/Exception", "java/io/IOException"}, "()V",
            declaredClass);
      RoutineInfo constructorInfo = constructorInfoBuilder.buildConstructorInfo();

      Assert.assertEquals("public org.exoplatform.test.TestClass() throws java.lang.Exception, java.io.IOException",
         constructorInfo.getGeneric());
   }

}
