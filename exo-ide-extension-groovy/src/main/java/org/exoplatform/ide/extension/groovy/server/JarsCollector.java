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
package org.exoplatform.ide.extension.groovy.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import org.exoplatform.ide.extension.groovy.shared.Attribute;
import org.exoplatform.ide.extension.groovy.shared.Jar;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class JarsCollector
{

   /**
    * The path to MANIFEST.MF file inside JAR.
    */
   public static final String MANIFEST = "META-INF/MANIFEST.MF";

   /**
    * List of collected JAR packages.
    */
   private Map<String, Jar> jars = new HashMap<String, Jar>();

   /**
    * Creates new instance of JarsCollector and search for JAR's.
    * 
    * @throws IOException
    */
   public JarsCollector() throws Exception
   {
      System.out.println("JarsCollector.JarsCollector()");
      
      String javaClassPath = System.getProperty("java.class.path");
      getJarsFromClasspath(javaClassPath);

      String javaExtDirs = System.getProperty("java.ext.dirs");
      getJarsFromClasspath(javaExtDirs);

      String javaLibraryPath = System.getProperty("java.library.path");
      getJarsFromClasspath(javaLibraryPath);

      String sunBootLibraryPath = System.getProperty("sun.boot.library.path");
      getJarsFromClasspath(sunBootLibraryPath);

      String catalinaBase = System.getProperty("catalina.base");
      String catalinaHome = System.getProperty("catalina.home");

      String commonLoaderPath = System.getProperty("common.loader");
      if (commonLoaderPath != null) {
         String[] commonLoaderPathEntries = commonLoaderPath.split(",");
         for (String commonLoaderPathEntry : commonLoaderPathEntries)
         {
            if (commonLoaderPathEntry.indexOf("${catalina.base}") >= 0)
            {
               commonLoaderPathEntry = commonLoaderPathEntry.replace("${catalina.base}", catalinaBase);
            }

            if (commonLoaderPathEntry.indexOf("${catalina.home}") >= 0)
            {
               commonLoaderPathEntry = commonLoaderPathEntry.replace("${catalina.home}", catalinaHome);
            }

            getJarsFromClasspath(commonLoaderPathEntry);
         }         
      }
   }

   /**
    * Get list of JAR files from classpath entry.
    * 
    * @param entry
    * @throws Exception
    */
   private void getJarsFromEntry(String entry) throws Exception
   {
      String path = URLDecoder.decode(entry, "UTF-8");
      File file = new File(path);
      if (file.exists() && file.isFile())
      {
         readJARManifest(file.getAbsolutePath());
         return;
      }

      if (file.exists() && file.isDirectory())
      {
         File[] files = file.listFiles();

         for (File f : files)
         {
            if (f.getName().endsWith(".jar"))
            {
               readJARManifest(f.getAbsolutePath());
            }
         }
      }
   }

   /**
    * Get list of JAR files from classpath.
    * 
    * @param classPath
    * @throws Exception
    */
   private void getJarsFromClasspath(String classPath) throws Exception
   {
      String pathSeparator = System.getProperty("path.separator");

      String[] classPathEntries = classPath.split(pathSeparator);
      for (String classPathEntry : classPathEntries)
      {
         getJarsFromEntry(classPathEntry);
      }
   }

   /**
    * Reads attributes from MANIFEST.MF and adds new JAR description to the map of JAR's.
    * 
    * @param jarPath
    */
   private void readJARManifest(String path)
   {
      try
      {
         JarFile jarFile = new JarFile(path);
         ZipEntry manifestZipEntry = jarFile.getEntry(MANIFEST);
         if (manifestZipEntry == null)
         {
            return;
         }

         InputStream in = jarFile.getInputStream(manifestZipEntry);
         Manifest manifest = new Manifest(in);
         Attributes attributes = manifest.getMainAttributes();

         Jar jar = new Jar(path);
         jars.put(path, jar);

         Iterator<Object> iter = attributes.keySet().iterator();
         while (iter.hasNext())
         {
            Name name = (Name)iter.next();
            String value = attributes.getValue(name);
            jar.getAttributes().add(new Attribute(name.toString(), value));
         }
      }
      catch (Exception e)
      {
         System.out.println("Error reading JAR " + path);
         e.printStackTrace();
      }
   }

   /**
    * Get list of JAR files with attributes from MANIFEST.MF.
    * 
    * @return
    */
   public Map<String, Jar> getJars()
   {
      return jars;
   }

}
