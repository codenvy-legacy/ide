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
package org.exoplatform.ide.codeassistant.storage;

import com.google.common.io.ByteStreams;

import org.apache.commons.io.FileUtils;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.junit.BeforeClass;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 */
public abstract class BaseTest
{
   private static final Log LOG = ExoLogger.getLogger(BaseTest.class);

   private static String CLASSES_DIRECTORY_PATH = System.getProperty("generated.classes.directory",
      "target/generated-classes/");

   private static String JARS_DIRECTORY_PATH = System.getProperty("generated.jars.directory", "target/generated-jars/");

   private static final String FILE_SEPARATOR = System.getProperty("file.separator");

   @BeforeClass
   public static void setUp() throws Exception
   {
      File classDirectory = new File(CLASSES_DIRECTORY_PATH);
      if (classDirectory.exists())
      {
         FileUtils.deleteDirectory(classDirectory);
      }
      classDirectory.mkdirs();

      File jarDirectory = new File(JARS_DIRECTORY_PATH);
      if (jarDirectory.exists())
      {
         FileUtils.deleteDirectory(jarDirectory);
      }
      jarDirectory.mkdirs();
   }

   protected static int generateClassFile(String pathToSource) throws IOException
   {
      Runtime exec = Runtime.getRuntime();
      String cmd = "javac -cp " + CLASSES_DIRECTORY_PATH + " " + pathToSource + " -d " + CLASSES_DIRECTORY_PATH;

      Process process = exec.exec(cmd);
      try
      {
         process.waitFor();
         return process.exitValue();
      }
      catch (InterruptedException e)
      {
         Thread.currentThread().interrupt();
      }
      return 1;
   }

   protected static void generateClassFiles(String pathToDir) throws IOException
   {
      boolean z = true;
      List<File> files = getJavaFiles(new File(pathToDir));
      while (z)
      {
         z = false;
         Iterator<File> iter = files.iterator();
         while (iter.hasNext())
         {
            File current = iter.next();
            if (generateClassFile(current.getAbsolutePath()) == 0)
            {
               iter.remove();
               z = true;
            }
         }
      }
   }

   protected static InputStream getClassFileAsStream(String fqn) throws FileNotFoundException
   {
      String pathToClass = fqn.replace(".", "/");
      File classFile = new File(CLASSES_DIRECTORY_PATH + pathToClass + ".class");

      return new FileInputStream(classFile);
   }

   protected static File generateJarFile(String jarName) throws IOException
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
      File newJar = new File(CLASSES_DIRECTORY_PATH, jarName);
      jar.renameTo(newJar);
      return newJar;
   }

   private static List<File> getJavaFiles(File dir)
   {
      List<File> result = new ArrayList<File>();
      for (File file : dir.listFiles())
      {
         if (file.isDirectory())
         {
            result.addAll(getJavaFiles(file));
         }
         else
         {
            if (file.getName().endsWith(".java"))
            {
               result.add(file);
            }
         }
      }
      return result;
   }

   /**
    * @return path to generated jar
    */
   @Deprecated
   protected static String createJarFile(String pathToSources, String outputPath)
   {
      String pathToGeneratedJar = null;
      try
      {
         // compile classes
         Runtime exec = Runtime.getRuntime();
         String pathToGeneratedClasses = CLASSES_DIRECTORY_PATH + outputPath + FILE_SEPARATOR;
         File file = new File(pathToGeneratedClasses);
         file.mkdirs();

         String cmd1 = "javac " + pathToSources + " -d " + pathToGeneratedClasses + " ";
         LOG.info("Execute command : " + cmd1);
         Process process1 = exec.exec(cmd1);
         process1.waitFor();

         LOG.info("Command output : " + new String(ByteStreams.toByteArray(process1.getInputStream())));
         LOG.info("Error command output :" + new String(ByteStreams.toByteArray(process1.getErrorStream())));

         // create jar file
         pathToGeneratedJar = JARS_DIRECTORY_PATH + outputPath + ".jar";

         String cmd2 = "jar cvf " + pathToGeneratedJar + pathToGeneratedClasses + "*";
         LOG.info("Execute command : " + cmd2);
         Process process2 = exec.exec(cmd2);
         process2.waitFor();

         LOG.info("Command output : " + new String(ByteStreams.toByteArray(process2.getInputStream())));
         LOG.info("Error command output :" + new String(ByteStreams.toByteArray(process2.getErrorStream())));
      }
      catch (InterruptedException e)
      {
         Thread.currentThread().interrupt();
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      return pathToGeneratedJar;
   }
}
