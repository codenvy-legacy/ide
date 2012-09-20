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
package org.exoplatform.ide.codeassistant.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 2:34:18 PM Mar 5, 2012 evgen $
 * 
 */
public class PackageParser
{
   private PackageParser()
   {
   }

   public static Set<String> parse(InputStream jar) throws IOException
   {
      Set<String> packages = new HashSet<String>();
      ZipInputStream zip = new ZipInputStream(jar);
      try
      {
         ZipEntry entry = zip.getNextEntry();
         while (entry != null)
         {

            String name = entry.getName();
            if (name.endsWith(".class"))
            {

               packages.addAll(parsePath(name));
            }
            entry = zip.getNextEntry();
         }
      }
      finally
      {
         zip.close();
      }

      return packages;
   }

   /**
    * @param name
    * @return
    */
   private static Set<String> parsePath(String name)
   {

      String[] segments = name.split("/");
      Set<String> packageSegment = new HashSet<String>();
      StringBuilder first = new StringBuilder(segments[0]);
      packageSegment.add(first.toString());
      for (int i = 1; i < segments.length - 1; i++)
      {
         first.append('.').append(segments[i]);
         packageSegment.add(first.toString());
      }
      return packageSegment;
   }

   /**
    * @param jarFile
    * @return
    * @throws IOException 
    */
   public static Set<String> parse(File jarFile) throws IOException
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
   public static Set<String> parse(File jarFile, Set<String> ignoredPackages) throws IOException
   {
      FileInputStream jarStream = new FileInputStream(jarFile);
      try
      {
         Set<String> packages = new HashSet<String>();
         ZipInputStream zip = new ZipInputStream(jarStream);
         try
         {
            ZipEntry entry = zip.getNextEntry();
            boolean ignore = false;
            while (entry != null)
            {

               String name = entry.getName();
               if (name.endsWith(".class"))
               {
                  ignore = false;
                  for (String s : ignoredPackages)
                  {
                     if (entry.getName().startsWith(s))
                     {
                        ignore = true;
                        break;
                     }
                  }
                  if (!ignore)
                  {
                     packages.addAll(parsePath(name));
                  }
               }
               entry = zip.getNextEntry();
            }
         }
         finally
         {
            zip.close();
         }
         return packages;
      }
      finally
      {
         jarStream.close();
      }
   }
}
