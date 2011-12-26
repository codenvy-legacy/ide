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

import org.exoplatform.ide.codeassistant.jvm.FieldInfo;
import org.junit.Test;

import java.lang.reflect.Modifier;

public class TestFieldInfoBuilder
{

   private final int access = Modifier.PUBLIC & Modifier.FINAL;

   private final String name = "Field1";

   private final String desc = "Z";

   private final String declaredClass = "org.exoplatform.test.TestClass";

   @Test
   public void testAccess()
   {
      FieldInfoBuilder fieldInfoBuilder =
         new FieldInfoBuilder(Modifier.PUBLIC | Modifier.FINAL, name, desc, declaredClass);
      FieldInfo fieldInfo = fieldInfoBuilder.buildFieldInfo();

      assertEquals(fieldInfo.getModifiers(), Modifier.PUBLIC | Modifier.FINAL);
      assertEquals("public final", fieldInfo.modifierToString());
   }

   @Test
   public void testName()
   {
      FieldInfoBuilder fieldInfoBuilder = new FieldInfoBuilder(access, "Field1", desc, declaredClass);
      FieldInfo fieldInfo = fieldInfoBuilder.buildFieldInfo();

      assertEquals("Field1", fieldInfo.getName());
   }

   @Test
   public void testSimpleType()
   {
      FieldInfoBuilder fieldInfoBuilder = new FieldInfoBuilder(access, name, "Z", declaredClass);
      FieldInfo fieldInfo = fieldInfoBuilder.buildFieldInfo();

      assertEquals("boolean", fieldInfo.getType());
   }

   @Test
   public void testArrayType()
   {
      FieldInfoBuilder fieldInfoBuilder = new FieldInfoBuilder(access, name, "[[D", declaredClass);
      FieldInfo fieldInfo = fieldInfoBuilder.buildFieldInfo();

      assertEquals("double[][]", fieldInfo.getType());
   }

   @Test
   public void testObjectType()
   {
      FieldInfoBuilder fieldInfoBuilder = new FieldInfoBuilder(access, name, "Ljava/lang/String;", declaredClass);
      FieldInfo fieldInfo = fieldInfoBuilder.buildFieldInfo();

      assertEquals("java.lang.String", fieldInfo.getType());
   }

   @Test
   public void testDeclaringClass()
   {
      FieldInfoBuilder fieldInfoBuilder = new FieldInfoBuilder(access, name, desc, "org.exoplatform.test.TestClass");
      FieldInfo fieldInfo = fieldInfoBuilder.buildFieldInfo();

      assertEquals("org.exoplatform.test.TestClass", fieldInfo.getDeclaringClass());
   }

}
