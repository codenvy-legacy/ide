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
package org.exoplatform.ide.codeassistant.asm.old;

import org.exoplatform.ide.codeassistant.jvm.TypeInfo;
import org.objectweb.asm.ClassReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * <p>
 * This class parses classes. It's can parse simple class-file, jar-file and
 * directory with class-files and jar-files. There are a lot of methods to parse
 * classes like {@link ClassParser#parseDir(File)}. When parse method was
 * invoked all classes which found was added to private list
 * {@link ClassParser#classes}. You may get list of all classes which parsed by
 * method {@link ClassParser#getClasses()}. If you need to clear all classes
 * from private list, you may use method {@link ClassParser#clear()};
 * </p>
 */
public class ClassParser
{

   private static final Logger LOG = LoggerFactory.getLogger(ClassParser.class);

   public static final TypeInfo OBJECT_TYPEINFO = parseQuietly(getClassFile(Object.class));

   private ClassParser()
   {
   }

   /**
    * 
    * @param class2Find
    *           - class to find
    * @return - content of the 'class2Find.class' file
    */
   public static InputStream getClassFile(Class<?> class2Find)
   {
      ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
      String classResource = class2Find.getName().replace('.', '/') + ".class";
      return contextClassLoader.getResourceAsStream(classResource);
   }

   public static TypeInfo parse(InputStream classStream) throws IOException
   {
      ClassReader cr = new ClassReader(classStream);
      TypeInfoClassVisitor typeInfoClassVisitor = new TypeInfoClassVisitor();
      cr.accept(typeInfoClassVisitor, ClassReader.SKIP_CODE);
      return null;//typeInfoClassVisitor.getBuilder().buildTypeInfo();
   }

   private static TypeInfo parseQuietly(InputStream classStream)
   {
      try
      {
         ClassReader cr = new ClassReader(classStream);
         TypeInfoClassVisitor typeInfoClassVisitor = new TypeInfoClassVisitor();
         cr.accept(typeInfoClassVisitor, ClassReader.SKIP_CODE);
         return null;//typeInfoClassVisitor.getBuilder().buildTypeInfo();
      }
      catch (IOException e)
      {
         e.printStackTrace();
         LOG.error(e.getLocalizedMessage(), e);
      }
      return null;
   }

}
