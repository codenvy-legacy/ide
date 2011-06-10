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
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      assert classLoader != null;

      String classPath = System.getProperty("java.class.path");
      String[] classPathEntries = classPath.split(":");
      for (String classPathEntry : classPathEntries)
      {
         if (!jars.containsKey(classPathEntry))
         {
            readJARManifest(classPathEntry);
         }
      }
   }

   /**
    * Reads attributes from MANIFEST.MF and adds new JAR description to the map of JAR's.
    * 
    * @param jarPath
    */
   private void readJARManifest(String jarPath)
   {
      try
      {
         String jarFilePath = URLDecoder.decode(jarPath, "UTF-8");
         File file = new File(jarFilePath);
         if (!file.exists() || !file.isFile())
         {
            return;
         }

         JarFile jarFile = new JarFile(jarFilePath);
         ZipEntry manifestZipEntry = jarFile.getEntry(MANIFEST);
         if (manifestZipEntry == null)
         {
            return;
         }

         InputStream in = jarFile.getInputStream(manifestZipEntry);
         Manifest manifest = new Manifest(in);
         Attributes attributes = manifest.getMainAttributes();

         Jar jar = new Jar(jarPath);
         jars.put(jarPath, jar);

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
         System.out.println("Error reading JAR " + jarPath);
         System.out.println("Message:  " + e.getMessage());
         e.printStackTrace();
      }
   }

   public Map<String, Jar> getJars()
   {
      return jars;
   }

}
