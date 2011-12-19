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
package org.exoplatform.ide.codeassistant.storage.extension;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.codeassistant.asm.JarParser;
import org.exoplatform.ide.codeassistant.jvm.CodeAssistantException;
import org.exoplatform.ide.codeassistant.jvm.RoutineInfo;
import org.exoplatform.ide.codeassistant.jvm.TypeInfo;
import org.exoplatform.ide.codeassistant.storage.InMemoryLuceneInfoStorage;
import org.exoplatform.ide.codeassistant.storage.lucene.LuceneCodeAssistantStorage;
import org.exoplatform.ide.codeassistant.storage.lucene.SaveTypeInfoIndexException;
import org.exoplatform.ide.codeassistant.storage.lucene.search.LuceneTypeInfoSearcher;
import org.exoplatform.ide.codeassistant.storage.lucene.writer.LuceneCachedTypeInfoResolver;
import org.exoplatform.ide.codeassistant.storage.lucene.writer.LuceneTypeInfoWriter;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Ignore
public class TestLuceneCachedTypeInfoResolver
{

   private static LuceneTypeInfoSearcher searcher;

   private static LuceneTypeInfoWriter writer;

   private static InMemoryLuceneInfoStorage luceneInfoStorage;

   private final static String PATH_TO_INDEX = "target/index3";

   private final static String PATH_TO_RT = System.getProperty("java.home") + "/lib/rt.jar";

   @BeforeClass
   public static void initializeStorage() throws IOException, SaveTypeInfoIndexException
   {

      luceneInfoStorage = new InMemoryLuceneInfoStorage();
      writer = new LuceneTypeInfoWriter(luceneInfoStorage);

      List<TypeInfo> typeInfos = JarParser.parse(new File(PATH_TO_RT));
      writer.addTypeInfo(typeInfos);

      searcher = new LuceneTypeInfoSearcher(luceneInfoStorage);
   }

   @Ignore
   @Test
   public void testCachedTypeInfoResolver() throws CodeAssistantException
   {
      LuceneCachedTypeInfoResolver resolver = new LuceneCachedTypeInfoResolver(searcher, writer);
      TypeInfo testClass = new LuceneCodeAssistantStorage(searcher).getTypeByFqn("java.util.HashMap");
      testClass = resolver.resolveTypeInfo(testClass);
      Set<String> methods = new HashSet<String>();
      for (RoutineInfo method : testClass.getMethods())
      {
         methods.add(method.getGeneric());
      }
      assertEquals(22, methods.size());
      assertTrue(methods.contains("public int java.util.HashMap.size()"));
      assertTrue(methods.contains("public boolean java.util.HashMap.isEmpty()"));
      assertTrue(methods.contains("public java.lang.Object java.util.HashMap.get(java.lang.Object)"));
      assertTrue(methods.contains("public boolean java.util.HashMap.containsKey(java.lang.Object)"));
      assertTrue(methods.contains("public java.lang.Object java.util.HashMap.put(java.lang.Object, java.lang.Object)"));
      assertTrue(methods.contains("public void java.util.HashMap.putAll(java.util.Map)"));
      assertTrue(methods.contains("public java.lang.Object java.util.HashMap.remove(java.lang.Object)"));
      assertTrue(methods.contains("public void java.util.HashMap.clear()"));
      assertTrue(methods.contains("public boolean java.util.HashMap.containsValue(java.lang.Object)"));
      assertTrue(methods.contains("public java.lang.Object java.util.HashMap.clone()"));
      assertTrue(methods.contains("public java.util.Set java.util.HashMap.keySet()"));
      assertTrue(methods.contains("public java.util.Collection java.util.HashMap.values()"));
      assertTrue(methods.contains("public java.util.Set java.util.HashMap.entrySet()"));
      assertTrue(methods.contains("public boolean java.util.AbstractMap.equals(java.lang.Object)"));
      assertTrue(methods.contains("public int java.util.AbstractMap.hashCode()"));
      assertTrue(methods.contains("public java.lang.String java.util.AbstractMap.toString()"));
      assertTrue(methods.contains("public final native java.lang.Class java.lang.Object.getClass()"));
      assertTrue(methods.contains("public final native void java.lang.Object.notify()"));
      assertTrue(methods.contains("public final native void java.lang.Object.notifyAll()"));
      assertTrue(methods.contains("public final native void java.lang.Object.wait(long)"
         + " throws java.lang.InterruptedException"));
      assertTrue(methods.contains("public final void java.lang.Object.wait(long, int)"
         + " throws java.lang.InterruptedException"));
      assertTrue(methods.contains("public final void java.lang.Object.wait()"
         + " throws java.lang.InterruptedException"));
   }

}
