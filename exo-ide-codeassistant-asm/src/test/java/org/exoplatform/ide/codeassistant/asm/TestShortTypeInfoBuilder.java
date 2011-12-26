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

import org.exoplatform.ide.codeassistant.jvm.ShortTypeInfo;
import org.junit.Test;

import java.lang.reflect.Modifier;

public class TestShortTypeInfoBuilder
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
      ShortTypeInfo shortTypeInfo = typeInfoBuilder.buildShortTypeInfo();

      assertEquals(Modifier.PUBLIC | Modifier.ABSTRACT, shortTypeInfo.getModifiers());
      assertEquals("public abstract", shortTypeInfo.modifierToString());
   }

   @Test
   public void testName()
   {
      TypeInfoBuilder typeInfoBuilder =
         new TypeInfoBuilder(access, "org/exoplatform/test/Class", superName, interfaces);
      ShortTypeInfo shortTypeInfo = typeInfoBuilder.buildShortTypeInfo();

      assertEquals("Class", shortTypeInfo.getName());
      assertEquals("org.exoplatform.test.Class", shortTypeInfo.getQualifiedName());
   }

   @Test
   public void testClassType()
   {
      TypeInfoBuilder typeInfoBuilder = new TypeInfoBuilder(Modifier.PUBLIC, name, superName, interfaces);
      ShortTypeInfo shortTypeInfo = typeInfoBuilder.buildShortTypeInfo();

      assertEquals(Modifier.PUBLIC, shortTypeInfo.getModifiers());
      assertEquals("CLASS", shortTypeInfo.getType());
   }

   @Test
   public void testInterfaceType()
   {
      TypeInfoBuilder typeInfoBuilder =
         new TypeInfoBuilder(Modifier.PUBLIC | Modifier.INTERFACE, name, superName, interfaces);
      ShortTypeInfo shortTypeInfo = typeInfoBuilder.buildShortTypeInfo();

      assertEquals(Modifier.PUBLIC | Modifier.INTERFACE, shortTypeInfo.getModifiers());
      assertEquals("INTERFACE", shortTypeInfo.getType());
   }

   @Test
   public void testAnnotationType()
   {
      TypeInfoBuilder typeInfoBuilder =
         new TypeInfoBuilder(Modifier.PUBLIC | TypeInfoBuilder.MODIFIER_ANNOTATION, name, superName, interfaces);
      ShortTypeInfo shortTypeInfo = typeInfoBuilder.buildShortTypeInfo();

      assertEquals(Modifier.PUBLIC | TypeInfoBuilder.MODIFIER_ANNOTATION, shortTypeInfo.getModifiers());
      assertEquals("ANNOTATION", shortTypeInfo.getType());
   }

   @Test
   public void testEnumType()
   {
      TypeInfoBuilder typeInfoBuilder =
         new TypeInfoBuilder(Modifier.PUBLIC | TypeInfoBuilder.MODIFIER_ENUM, name, superName, interfaces);
      ShortTypeInfo shortTypeInfo = typeInfoBuilder.buildShortTypeInfo();

      assertEquals(Modifier.PUBLIC | TypeInfoBuilder.MODIFIER_ENUM, shortTypeInfo.getModifiers());
      assertEquals("ENUM", shortTypeInfo.getType());
   }

}
