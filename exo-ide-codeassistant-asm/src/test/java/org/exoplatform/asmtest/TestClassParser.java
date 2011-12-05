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

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.objectweb.asm.ClassReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;

@Ignore
class ByteCodeFilenameFilterForTest implements FilenameFilter
{

   @Override
   public boolean accept(File dir, String name)
   {
      return name.endsWith("_jar") || name.endsWith("_class");
   }

}

public class TestClassParser
{

   @Test
   public void test() throws IOException
   {
      /*
      ByteArrayOutputStream bytes = new ByteArrayOutputStream();
      //FileInputStream stream = new FileInputStream("FreeSpaceRatioAutoscalingAlgorithm.class");
      FileInputStream stream = new FileInputStream("AutoscalingAlgorithm.class");
      //FileInputStream stream = new FileInputStream("AutoscalingState.class");
      try
      {
         byte[] buf = new byte[1024];
         int length = 0;
         while (length >= 0)
         {
            bytes.write(buf, 0, length);
            length = stream.read(buf);
         }
      }
      finally
      {
         stream.close();
      }

      ClassReader cr = new ClassReader(bytes.toByteArray());
      */

      ClassReader cr =
         new ClassReader(new FileInputStream(
            "target/test-classes/testclasses/classes/org/exoplatform/test/TestInterface_class"));

      // ClassReader cr = new ClassReader("org.exoplatform.asmtest.SimpleEnum");

      TypeInfoClassVisitor typeInfoClassVisitor = new TypeInfoClassVisitor();
      cr.accept(typeInfoClassVisitor, ClassReader.SKIP_CODE);

      // System.out.println(TypeInfoBuilder.toString(typeInfoClassVisitor.getBuilder().buildTypeInfo()));
   }

   @Test
   public void testClassParsing() throws IOException
   {
      ClassParser classParser = new ClassParser();
      classParser.parseDir(new File("target/test-classes/testclasses/classes"), new ByteCodeFilenameFilterForTest());

      assertLoadedClasses(classParser);
   }

   private void assertLoadedClasses(ClassParser classParser)
   {
      Assert.assertEquals(6, classParser.getClasses().size());
      for (TypeInfoBuilder builder : classParser.getClasses())
      {
         System.out.println(TypeInfoBuilder.toString(builder.buildTypeInfo()));
      }
      // TODO add more and more assertions for each class
   }
}