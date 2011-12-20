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

import org.exoplatform.ide.codeassistant.jvm.TypeInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
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
            if (name.endsWith(".class"))
            {
               classes.add(ClassParser.parse(zip));
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

   public static List<TypeInfo> parse(File jarFile) throws IOException
   {
      return parse(new FileInputStream(jarFile));
   }

}