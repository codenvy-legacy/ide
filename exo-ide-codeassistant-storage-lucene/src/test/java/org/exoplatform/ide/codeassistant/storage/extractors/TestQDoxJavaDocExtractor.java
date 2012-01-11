/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.codeassistant.storage.extractors;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

public class TestQDoxJavaDocExtractor
{

   private static Map<String, String> javaDocs;

   @BeforeClass
   public static void extractJavaDocs() throws FileNotFoundException
   {
      QDoxJavaDocExtractor extractor = new QDoxJavaDocExtractor();
      javaDocs = extractor.extractSource(new FileInputStream(new File("src/test/java/test/javadoc/JavaDocClass.java")));
   }

   @Test
   public void checkTotalCountOfMembersWithJavaDocs()
   {
      Assert.assertEquals(17, javaDocs.size());
   }

   @Test
   public void checkClassesWithJavaDocs()
   {
      Assert.assertTrue(javaDocs.containsKey("test.javadoc.JavaDocClass"));
      Assert.assertEquals("<p>\nClass java doc<br>\nwith tags and<br>\nfew lines.\n</p>\n@author Test class doclets",
         javaDocs.get("test.javadoc.JavaDocClass"));

      Assert.assertTrue(javaDocs.containsKey("test.javadoc.JavaDocClass$PrivateClass"));
      Assert.assertEquals("Private class with java doc", javaDocs.get("test.javadoc.JavaDocClass$PrivateClass"));

      Assert.assertFalse(javaDocs.containsKey("test.javadoc.JavaDocClass$ClassWithoutJavadoc"));

      Assert.assertTrue(javaDocs.containsKey("test.javadoc.PrivateClass"));
      Assert.assertEquals("Second private class", javaDocs.get("test.javadoc.PrivateClass"));

      Assert.assertTrue(javaDocs.containsKey("test.javadoc.ClassWithGenerics"));
      Assert.assertEquals("Class with generics", javaDocs.get("test.javadoc.ClassWithGenerics"));

   }

   @Test
   public void checkFieldsWithJavaDocs()
   {
      Assert.assertFalse(javaDocs.containsKey("test.javadoc.JavaDocClass#fieldWithoutJavaDoc"));

      Assert.assertTrue(javaDocs.containsKey("test.javadoc.JavaDocClass#field"));
      Assert
         .assertEquals("Field java doc\n@author Test field doclets", javaDocs.get("test.javadoc.JavaDocClass#field"));

      Assert.assertFalse(javaDocs.containsKey("test.javadoc.JavaDocClass#fieldWithoutJavaDoc"));

      Assert.assertTrue(javaDocs.containsKey("test.javadoc.ClassWithGenerics#genericField"));
      Assert.assertEquals("Field with generics", javaDocs.get("test.javadoc.ClassWithGenerics#genericField"));
   }

   @Test
   public void checkMethodsFromJavaDocClass()
   {
      Assert.assertTrue(javaDocs.containsKey("test.javadoc.JavaDocClass(int,java.lang.Integer)"));
      Assert.assertEquals("Constructor java doc with parameters",
         javaDocs.get("test.javadoc.JavaDocClass(int,java.lang.Integer)"));

      Assert.assertFalse(javaDocs.containsKey("test.javadoc.JavaDocClass()"));

      Assert.assertTrue(javaDocs.containsKey("test.javadoc.JavaDocClass#method()"));
      Assert.assertEquals("Method java doc", javaDocs.get("test.javadoc.JavaDocClass#method()"));

      Assert.assertTrue(javaDocs.containsKey("test.javadoc.JavaDocClass#method(int)"));
      Assert.assertEquals("Method with primitive param", javaDocs.get("test.javadoc.JavaDocClass#method(int)"));

      Assert.assertTrue(javaDocs.containsKey("test.javadoc.JavaDocClass#method(java.lang.Double)"));
      Assert.assertEquals("Method with object param",
         javaDocs.get("test.javadoc.JavaDocClass#method(java.lang.Double)"));

      Assert.assertTrue(javaDocs.containsKey("test.javadoc.JavaDocClass#method(int,java.lang.Double)"));
      Assert.assertEquals("Method with primitive and object params",
         javaDocs.get("test.javadoc.JavaDocClass#method(int,java.lang.Double)"));

      Assert.assertFalse(javaDocs.containsKey("test.javadoc.JavaDocClass#methodWithoutJavaDocs(java.lang.Object)"));
   }

   @Test
   public void checkMethodsFromInnerClasses()
   {
      Assert.assertTrue(javaDocs.containsKey("test.javadoc.JavaDocClass$PrivateClass"));
      Assert.assertEquals("Private class with java doc", javaDocs.get("test.javadoc.JavaDocClass$PrivateClass"));

      Assert.assertTrue(javaDocs.containsKey("test.javadoc.JavaDocClass$PrivateClass()"));
      Assert.assertEquals("Constructor of private class", javaDocs.get("test.javadoc.JavaDocClass$PrivateClass()"));

      Assert.assertTrue(javaDocs.containsKey("test.javadoc.JavaDocClass$PrivateClass#method()"));
      Assert.assertEquals("Method of private class", javaDocs.get("test.javadoc.JavaDocClass$PrivateClass#method()"));

      Assert.assertTrue(javaDocs.containsKey("test.javadoc.JavaDocClass$ClassWithoutJavadoc#method()"));
      Assert.assertEquals("Method with java docs in uncommented class",
         javaDocs.get("test.javadoc.JavaDocClass$ClassWithoutJavadoc#method()"));
   }

   @Test
   public void checkMethodsFromClassWithGenerics()
   {
      Assert.assertTrue(javaDocs.containsKey("test.javadoc.ClassWithGenerics#method(T)"));
      Assert.assertEquals("Method with generics", javaDocs.get("test.javadoc.ClassWithGenerics#method(T)"));

      Assert.assertTrue(javaDocs
         .containsKey("test.javadoc.ClassWithGenerics#method(java.util.List<? extends java.lang.Number>)"));
      Assert.assertEquals("Method with list as parameter {@link asdf}",
         javaDocs.get("test.javadoc.ClassWithGenerics#method(java.util.List<? extends java.lang.Number>)"));

      Assert.assertTrue(javaDocs.containsKey("test.javadoc.ClassWithGenerics#method()"));
      Assert.assertEquals(
         "Begin\n@see PrivateClass asdf Middle\n@see ClassWithGenerics#method(List)\n@author Author End",
         javaDocs.get("test.javadoc.ClassWithGenerics#method()"));
   }

}
