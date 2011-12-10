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
package org.exoplatform.ide.codeassistant.storage;

import org.exoplatform.ide.codeassistant.asm.JarParser;
import org.exoplatform.ide.codeassistant.framework.server.extractors.TypeInfoExtractor;
import org.exoplatform.ide.codeassistant.jvm.CodeAssistantException;
import org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage;
import org.exoplatform.ide.codeassistant.jvm.RoutineInfo;
import org.exoplatform.ide.codeassistant.jvm.TypeInfo;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.List;

@Ignore
public class DeclaredTypeInfoResolverTest
{

   private static CodeAssistantStorage storage;

   private final static String PATH_TO_INDEX = "target/index3";

   private final static String PATH_TO_RT = System.getProperty("java.home") + "/lib/rt.jar";

   @BeforeClass
   public static void initializeStorage() throws IOException, SaveTypeInfoIndexException
   {
      TypeInfoIndexWriter writer = new TypeInfoIndexWriter(PATH_TO_INDEX);

      List<TypeInfo> typeInfos = JarParser.parse(new File(PATH_TO_RT));
      writer.addTypeInfo(typeInfos);
      writer.close();

      storage = new LuceneCodeAssistantStorage(PATH_TO_INDEX);
   }

   @Test
   public void testTestClassResolving() throws CodeAssistantException
   {
      DeclaredTypeInfoResolver resolver = new DeclaredTypeInfoResolver(storage);
      TypeInfo testClass = storage.getTypeByFqn("java.lang.Integer");
      testClass = resolver.resolveTypeInfo(testClass);
      System.out.println(testClass.getDeclaredMethods().length);
      for (RoutineInfo method : testClass.getDeclaredConstructors())
      {
         System.out.println(method.getGeneric());
      }

      System.out.println();
      System.out.println();
      TypeInfo reflectMap = TypeInfoExtractor.extract(Integer.class);
      System.out.println(reflectMap.getDeclaredMethods().length);
      for (RoutineInfo method : reflectMap.getDeclaredConstructors())
      {
         System.out.println(method.getGeneric());
      }

      System.out.println();
      System.out.println();
      for (Constructor method : Integer.class.getDeclaredConstructors())
      {
         System.out.println(method.getName());
      }
   }

}
