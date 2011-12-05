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
package org.exoplatform.asmtest;

import org.objectweb.asm.ClassReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

class ByteCodeFilenameFilter implements FilenameFilter
{

   @Override
   public boolean accept(File dir, String name)
   {
      return name.endsWith(".jar") || name.endsWith(".class");
   }

}

public class ClassParser
{

   private List<TypeInfoBuilder> classes;

   public ClassParser()
   {
      this.classes = new LinkedList<TypeInfoBuilder>();
   }

   private void parse(InputStream classStream) throws IOException
   {
      ClassReader cr = new ClassReader(classStream);
      TypeInfoClassVisitor typeInfoClassVisitor = new TypeInfoClassVisitor();
      cr.accept(typeInfoClassVisitor, ClassReader.SKIP_CODE);
      classes.add(typeInfoClassVisitor.getBuilder());
   }

   public void parseClassFile(File classFile) throws IOException
   {
      parse(new FileInputStream(classFile));
   }

   public void parseDir(File dir, FilenameFilter filter) throws IOException
   {
      for (File current : dir.listFiles(filter))
      {
         if (current.getName().endsWith("class"))
         {
            parseClassFile(current);
         }
         else if (current.getName().endsWith("jar"))
         {
            parseJarFile(current);
         }
      }
      for (File current : dir.listFiles())
      {
         if (current.isDirectory())
         {
            parseDir(current, filter);
         }
      }
   }

   public void parseDir(File dir) throws IOException
   {
      parseDir(dir, new ByteCodeFilenameFilter());
   }

   public void parseJarFile(File jarFile) throws IOException
   {
      ZipInputStream zip = new ZipInputStream(new FileInputStream(jarFile));
      ZipEntry entry = zip.getNextEntry();
      while (entry != null)
      {
         String name = entry.getName();
         if (name.endsWith(".class"))
         {
            parse(zip);
         }
      }
   }

   public void clear()
   {
      this.classes.clear();
   }

   public List<TypeInfoBuilder> getClasses()
   {
      return classes;
   }

}
