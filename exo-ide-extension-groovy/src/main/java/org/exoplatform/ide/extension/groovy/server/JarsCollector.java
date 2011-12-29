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

import org.exoplatform.ide.extension.groovy.shared.Attribute;
import org.exoplatform.ide.extension.groovy.shared.Jar;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class JarsCollector
{
   /** Collected JARs. */
   private final Map<String, Jar> jars;

   private JarsCollector()
   {
      jars = new HashMap<String, Jar>();
   }

   public static Collection<Jar> collect() throws IOException
   {
      return new JarsCollector().processJars();
   }

   private Collection<Jar> processJars() throws IOException
   {
      String pathSeparator = System.getProperty("path.separator");

      String javaClassPath = System.getProperty("java.class.path");
      getJarsFromClasspath(javaClassPath, pathSeparator);

      String javaExtDirs = System.getProperty("java.ext.dirs");
      getJarsFromClasspath(javaExtDirs, pathSeparator);

      String javaLibraryPath = System.getProperty("java.library.path");
      getJarsFromClasspath(javaLibraryPath, pathSeparator);

      String sunBootLibraryPath = System.getProperty("sun.boot.library.path");
      getJarsFromClasspath(sunBootLibraryPath, pathSeparator);

      String catalinaBase = System.getProperty("catalina.base");
      String catalinaHome = System.getProperty("catalina.home");

      String commonLoaderPath = System.getProperty("common.loader");
      if (commonLoaderPath != null)
      {
         String[] commonLoaderPathEntries = commonLoaderPath.split(",");
         for (String commonLoaderPathEntry : commonLoaderPathEntries)
         {
            if (commonLoaderPathEntry.contains("${catalina.base}"))
            {
               commonLoaderPathEntry = commonLoaderPathEntry.replace("${catalina.base}", catalinaBase);
            }

            if (commonLoaderPathEntry.contains("${catalina.home}"))
            {
               commonLoaderPathEntry = commonLoaderPathEntry.replace("${catalina.home}", catalinaHome);
            }

            getJarsFromClasspath(commonLoaderPathEntry, pathSeparator);
         }
      }
      return jars.values();
   }

   private void getJarsFromClasspath(String classPath, String pathSeparator) throws IOException
   {
      String[] classPathEntries = classPath.split(pathSeparator);
      for (int i = 0; i < classPathEntries.length; i++)
      {
         File file = new File(classPathEntries[i]);
         if (!file.exists())
         {
            return;
         }

         if (file.isFile())
         {
            jars.put(file.getAbsolutePath(), makeJar(file));
         }
         else
         {
            File[] files = file.listFiles(new FileFilter()
            {
               @Override
               public boolean accept(File pathname)
               {
                  return pathname.getName().endsWith(".jar");
               }
            });
            if (files != null && files.length > 0)
            {
               for (int k = 0; k < files.length; k++)
               {
                  if (files[k].exists())
                  {
                     jars.put(file.getAbsolutePath(), makeJar(files[k]));
                  }
               }
            }
         }
      }
   }

   /** Create JAR description. */
   private Jar makeJar(File file) throws IOException
   {
      Jar jar = new Jar(file.getAbsolutePath());
      JarFile jarfile = new JarFile(file);
      ZipEntry manifestZipEntry = jarfile.getEntry("META-INF/MANIFEST.MF");
      if (manifestZipEntry != null)
      {
         InputStream in = null;
         Attributes attributes;
         try
         {
            in = jarfile.getInputStream(manifestZipEntry);
            Manifest manifest = new Manifest(in);
            attributes = manifest.getMainAttributes();
         }
         finally
         {
            if (in != null)
            {
               try
               {
                  in.close();
               }
               catch (IOException ignored)
               {
               }
            }
         }

         for (Map.Entry<Object, Object> e : attributes.entrySet())
         {
            jar.getAttributes().add(new Attribute((String)e.getKey(), (String)e.getValue()));
         }
      }
      return jar;
   }
}
