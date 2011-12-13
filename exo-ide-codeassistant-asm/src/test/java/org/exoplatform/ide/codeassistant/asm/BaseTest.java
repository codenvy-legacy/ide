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

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 */
public abstract class BaseTest
{
   private static String CLASSES_DIRECTORY_PATH = System.getProperty("generated.classes.directory",
      "target/generated-classes/");

   @BeforeClass
   public static void setUp() throws Exception
   {
      File classDirectory = new File(CLASSES_DIRECTORY_PATH);
      if (classDirectory.exists())
      {
         FileUtils.deleteDirectory(classDirectory);
      }
      classDirectory.mkdirs();
   }

   protected static void generateClassFile(String pathToSource) throws IOException
   {
      Runtime exec = Runtime.getRuntime();
      String cmd = "javac -cp " + CLASSES_DIRECTORY_PATH + " " + pathToSource + " -d " + CLASSES_DIRECTORY_PATH;
      Process process = exec.exec(cmd);
      try
      {
         process.waitFor();
      }
      catch (InterruptedException e)
      {
         Thread.currentThread().interrupt();
      }
   }

   protected static InputStream getClassFileAsStream(String fqn) throws FileNotFoundException
   {
      String pathToClass = fqn.replace(".", "/");
      File classFile = new File(CLASSES_DIRECTORY_PATH + pathToClass + ".class");

      return new FileInputStream(classFile);
   }

   protected static void generateJarFile(String jarName) throws IOException
   {
      Runtime exec = Runtime.getRuntime();
      String cmd = "jar -cf " + CLASSES_DIRECTORY_PATH + "../" + jarName + " -C " + CLASSES_DIRECTORY_PATH + " .";
      Process process = exec.exec(cmd);
      try
      {
         process.waitFor();
      }
      catch (InterruptedException e)
      {
         Thread.currentThread().interrupt();
      }
      File jar = new File(CLASSES_DIRECTORY_PATH + "../" + jarName);
      jar.renameTo(new File(CLASSES_DIRECTORY_PATH, jarName));
   }

}
