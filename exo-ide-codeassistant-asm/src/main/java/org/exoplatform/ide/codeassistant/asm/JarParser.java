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

import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * This class used for parsing jar file
 */
public class JarParser
{

   private JarParser()
   {
   }

   public static List<TypeInfo> parse(InputStream jar) throws IOException
   {
      /*
       * There are no way to predict entries order in jar, so, manifest will be added
       * to classes when all classes parsed successfully.
       */
      Manifest manifest = null;

      List<TypeInfo> classes = new ArrayList<TypeInfo>();
      ZipInputStream zip = new ZipInputStream(jar);
      try
      {
         ZipEntry entry = zip.getNextEntry();
         while (entry != null)
         {
            String name = entry.getName();
            // Make check class not anonymous. I.e ExceptionThrownEventHandler$1.class
            // so we check that first character after '$' not digit.
            if (name.endsWith(".class") && !Character.isDigit(name.charAt(name.lastIndexOf("$") + 1)))
            {
               TypeInfo typeInfo = ClassParser.parse(zip);
               if (!Modifier.isPrivate(typeInfo.getModifiers()))
                  classes.add(typeInfo);
            }
            else if (name.equalsIgnoreCase("MANIFEST.MF"))
            {
               manifest = new Manifest(zip);
            }
            entry = zip.getNextEntry();
         }
      }
      finally
      {
         zip.close();
      }

      /*
       * Temporary disabled to provide List<TypeInfo> return type
      for (TypeInfoBuilder builder : classes)
      {
         builder.addManifest(manifest);
      }
      */
      return classes;
   }

   /**
    * Parse content of the jar file
    * 
    * @param jarFile
    *           - input jar file.
    * @return - list of the TypeInfo parsed from classes in jar file
    * @throws IOException
    */
   public static List<TypeInfo> parse(File jarFile) throws IOException
   {
      FileInputStream jarStream = new FileInputStream(jarFile);
      try
      {
         return parse(jarStream);
      }
      finally
      {
         jarStream.close();
      }
   }

   /**
    * @param jarFile
    * @param ignoredPackages
    * @return
    */
   public static List<TypeInfo> parse(File jarFile, Set<String> ignoredPackages) throws IOException
   {
      FileInputStream jarStream = new FileInputStream(jarFile);
      try
      {
         return parse(jarStream, ignoredPackages);
      }
      finally
      {
         jarStream.close();
      }
   }

   /**
    * 
    * @param jarStream
    * @param ignoredPackages
    * @return
    */
   //To provide same performance duplicate method code 
   private static List<TypeInfo> parse(FileInputStream jar, Set<String> ignoredPackages) throws IOException
   {
      List<TypeInfo> classes = new ArrayList<TypeInfo>();
      ZipInputStream zip = new ZipInputStream(jar);
      try
      {
         ZipEntry entry = zip.getNextEntry();
         boolean ignore = false;
         while (entry != null)
         {
            String name = entry.getName();
            ignore = false;
            for (String s : ignoredPackages)
            {
               if (name.startsWith(s))
               {
                  ignore = true;
                  break;
               }
            }
            // Make check class not anonymous. I.e ExceptionThrownEventHandler$1.class
            // so we check that first character after '$' not digit.
            if (!ignore && name.endsWith(".class") && !Character.isDigit(name.charAt(name.lastIndexOf("$") + 1)))
            {
               TypeInfo typeInfo = ClassParser.parse(zip);
               if (!Modifier.isPrivate(typeInfo.getModifiers()))
                  classes.add(typeInfo);
            }
            entry = zip.getNextEntry();
         }
      }
      finally
      {
         zip.close();
      }
      return classes;
   }

}