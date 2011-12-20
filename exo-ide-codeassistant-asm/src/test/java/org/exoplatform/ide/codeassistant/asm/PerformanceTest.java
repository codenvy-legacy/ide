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

import org.exoplatform.ide.codeassistant.framework.server.extractors.ClassNamesExtractor;
import org.exoplatform.ide.codeassistant.framework.server.extractors.TypeInfoExtractor;
import org.exoplatform.ide.codeassistant.jvm.TypeInfo;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

class JarFilenameFilter implements FilenameFilter
{

   @Override
   public boolean accept(File dir, String name)
   {
      return name.endsWith(".jar");
   }

}

public class PerformanceTest
{

   private static final File source = new File("target/test-classes/testclasses");

   @Ignore
   @Test
   public void shouldParseRtJar() throws IOException, ClassNotFoundException
   {
      long start = System.currentTimeMillis();
      List<TypeInfo> classes = JarParser.parse(new File(System.getProperty("java.home") + "/lib/rt.jar"));
      System.out.println("Class count: " + classes.size());
      System.out.println("Time: " + (System.currentTimeMillis() - start));
   }

   @Ignore
   @Test
   public void testReflect() throws IOException, ClassNotFoundException
   {
      long start = System.currentTimeMillis();
      List<TypeInfo> classes = parseJarFilesReflect(getJarsFromDir(source));
      System.out.println("Class count: " + classes.size());
      System.out.println("Time: " + (System.currentTimeMillis() - start));
   }

   @Ignore
   @Test
   public void testAsm() throws IOException, ClassNotFoundException
   {
      long start = System.currentTimeMillis();
      List<TypeInfo> classes = parseJarsAsm(getJarsFromDir(source));
      System.out.println("Class count: " + classes.size());
      System.out.println("Time: " + (System.currentTimeMillis() - start));
   }

   private static List<TypeInfo> parseJarsAsm(List<File> jars) throws IOException
   {
      List<TypeInfo> classes = new ArrayList<TypeInfo>();
      for (File jar : jars)
      {
         classes.addAll(JarParser.parse(jar));
      }
      return classes;
   }

   private List<File> getJarsFromDir(File dir, FilenameFilter filter) throws IOException
   {
      List<File> jars = new ArrayList<File>();
      for (File current : dir.listFiles(filter))
      {
         jars.add(current);
      }
      for (File current : dir.listFiles())
      {
         if (current.isDirectory())
         {
            jars.addAll(getJarsFromDir(current, filter));
         }
      }
      return jars;
   }

   private List<File> getJarsFromDir(File dir) throws IOException
   {
      return getJarsFromDir(dir, new JarFilenameFilter());
   }

   private static List<TypeInfo> parseJarFilesReflect(List<File> jarFiles) throws IOException, ClassNotFoundException
   {
      List<TypeInfo> classes = new ArrayList<TypeInfo>();
      List<URL> urls = new ArrayList<URL>();
      for (File file : jarFiles)
      {
         urls.add(file.toURI().toURL());
      }
      URLClassLoader classLoader = new URLClassLoader(urls.toArray(new URL[0]), ClassLoader.getSystemClassLoader());
      int i = 0;
      for (File jarFile : jarFiles)
      {
         i++;
         for (String name : ClassNamesExtractor.getCompiledClassesFromJar(jarFile.getAbsolutePath()))
         {
            try
            {
               Class<?> current = classLoader.loadClass(name);
               classes.add(TypeInfoExtractor.extract(current));
            }
            catch (Throwable e)
            {
               // skip unresolved dependencies
            }
         }
      }
      return classes;
   }
}