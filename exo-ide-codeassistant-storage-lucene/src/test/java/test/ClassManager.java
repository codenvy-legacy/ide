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
package test;

import test.annotations.CTestAnnotation;
import test.annotations.DTestAnnotation;
import test.classes.ATestClass;
import test.classes.ATestClass2;
import test.classes.BTestClass;
import test.classes.CTestClass;
import test.classes.DTestClass;
import test.classes2.ITestClass;
import test.interfaces.DTestInterface;
import test.interfaces.ETestInterface;
import test.interfaces.ETestInterface2;

import org.exoplatform.ide.codeassistant.asm.ClassParser;
import org.exoplatform.ide.codeassistant.jvm.TypeInfo;
import org.exoplatform.ide.codeassistant.storage.lucene.SaveTypeInfoIndexException;
import org.exoplatform.ide.codeassistant.storage.lucene.writer.LuceneTypeInfoWriter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Test classes enumerator
 */
public class ClassManager
{
   //disable instance creation
   private ClassManager()
   {
      super();
   }

   /**
    * 
    * @return array of all test classes.
    */
   public static Class<?>[] getAllTestClasses()
   {
      return new Class[]{CTestAnnotation.class, DTestAnnotation.class, ATestClass.class, ATestClass2.class,
         BTestClass.class, ITestClass.class, DTestInterface.class, ETestInterface.class, ETestInterface2.class,
         CTestClass.class, DTestClass.class};
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

   /**
    * @param className
    *           TODO
    * @throws IOException
    * @throws SaveTypeInfoIndexException
    */
   public static void createIndexForClass(LuceneTypeInfoWriter typeWriter, Class<?>... classesToIndex)
      throws IOException, SaveTypeInfoIndexException
   {

      List<TypeInfo> typeInfos = new ArrayList<TypeInfo>();

      for (Class<?> classToIndex : classesToIndex)
      {
         typeInfos.add(ClassParser.parse(ClassManager.getClassFile(classToIndex)));
      }

      typeWriter.addTypeInfo(typeInfos);
   }
}
