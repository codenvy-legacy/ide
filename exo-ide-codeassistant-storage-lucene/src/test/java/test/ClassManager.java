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
import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;
import org.exoplatform.ide.codeassistant.storage.extractors.QDoxJavaDocExtractor;
import org.exoplatform.ide.codeassistant.storage.lucene.SaveDataIndexException;
import org.exoplatform.ide.codeassistant.storage.lucene.writer.LuceneDataWriter;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test classes enumerator
 */
public class ClassManager
{
   //disable instance creation
   private ClassManager()
   {
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
    * @param className
    *           TODO
    * @throws IOException
    * @throws SaveDataIndexException
    */
   public static void createIndexForClass(LuceneDataWriter typeWriter, Class<?>... classesToIndex) throws IOException,
      SaveDataIndexException
   {

      List<TypeInfo> typeInfos = new ArrayList<TypeInfo>();

      for (Class<?> classToIndex : classesToIndex)
      {
         typeInfos.add(ClassParser.parse(ClassParser.getClassFile(classToIndex)));
      }

      typeWriter.addTypeInfo(typeInfos);
   }

   public static void createIndexForSources(LuceneDataWriter dataWriter, String... sources) throws IOException,
      SaveDataIndexException
   {
      Map<String, String> javaDocs = new HashMap<String, String>();
      for (String source : sources)
      {
         QDoxJavaDocExtractor javaDocExtractor = new QDoxJavaDocExtractor();
         javaDocs.putAll(javaDocExtractor.extractSource(new FileInputStream(source)));
      }

      dataWriter.addJavaDocs(javaDocs);
   }

}
