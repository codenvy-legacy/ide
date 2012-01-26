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

import org.exoplatform.ide.codeassistant.jvm.bean.TypeInfoBean;
import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

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

   public final static TypeInfo OBJECT_TYPE = parseQuietly(Object.class);

   private ClassParser()
   {
   }

   /**
    * 
    * Find content of the class file.
    * 
    * @param classObject
    *           - class to find
    * @return - content of the 'classObject.class' file
    */
   public static InputStream getClassFile(Class<?> classObject)
   {
      ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
      String classResource = classObject.getName().replace('.', '/') + ".class";
      return contextClassLoader.getResourceAsStream(classResource);
   }

   /**
    * Read class information from 'classObject.class' file
    * 
    * @param classObject
    *           - class TypeInfo we looking for.
    * @return - empty TypeInfoBean if class file not found.
    * @throws IOException
    */
   public static TypeInfo parse(Class<?> classObject) throws IOException
   {
      InputStream classStream = getClassFile(classObject);
      if (classStream == null)
      {
         return new TypeInfoBean();
      }
      try
      {
         return parse(classStream);
      }
      finally
      {
         classStream.close();
      }
   }

   /**
    * Read class information from 'classObject.class' file
    * 
    * @param classObject
    *           - class TypeInfo we looking for.
    * @return - empty TypeInfoBean if class file not found.
    * @throws IOException
    */
   public static TypeInfo parse(String classObject) throws IOException
   {
      ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
      String classResource = classObject.replace('.', '/') + ".class";
      InputStream classStream = contextClassLoader.getResourceAsStream(classResource);
      if (classStream == null)
      {
         return new TypeInfoBean();
      }
      try
      {
         return parse(classStream);
      }
      finally
      {
         classStream.close();
      }
   }

   /**
    * Read class information from 'classObject.class' file without throwing
    * exception in the case of problems
    * 
    * @param classObject
    *           - class TypeInfo we looking for.
    * @return - empty TypeInfoBean if class file not found or exception will
    *         occure.
    * @throws IOException
    */
   public static TypeInfo parseQuietly(Class<?> classObject)
   {
      InputStream classStream = getClassFile(classObject);
      try
      {
         return parse(classStream);
      }
      catch (IOException e)
      {
         return new TypeInfoBean();
      }
      finally
      {
         try
         {
            classStream.close();
         }
         catch (IOException ignored)
         {
         }
      }
   }

   /**
    * Parse TypeInfo from class stream
    * 
    * @param classStream
    *           - stream from class file.
    * @return TypeInfo of the class file information.
    * @throws IOException
    */
   public static TypeInfo parse(InputStream classStream) throws IOException
   {
      ClassReader cr = new ClassReader(classStream);
      ClassNode cn = new ClassNode();
      cr.accept(cn, 0);
      return new AsmTypeInfo(cn);
   }
}
