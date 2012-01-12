/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.codeassistant.framework.server.extractors;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

/**
 * Extracting class names from jar's or from jdk source.
 * 
 * 
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/

public class ClassNamesExtractor
{
   
   private static final Log LOG = ExoLogger.getLogger(ClassNamesExtractor.class);

   /**
    * Extract all class names from jar
    * 
    * @param jarPath the path to jar
    * @return set of canonical names 
    * @throws IOException
    */
   public static List<String> getCompiledClassesFromJar(String jarPath) throws IOException
   {
     return getCompiledClassesFromJar(jarPath, null);
   }
   
   /**
    * Extract all class names from jar in given package
    * 
    * @param jarPath the path to jar
    * @param packageName the package name for filtering class names
    * @return set of canonical names
    * @throws IOException
    */
   public static List<String> getCompiledClassesFromJar(String jarPath,  String packageName) throws IOException
   {
      return extract(jarPath, packageName, ".class");
   }

   /**
    * Extract all information from zip archive of java source 
    * 
    * @param javaSrcPath the path to source archive
    * @return set of canonical names
    * @throws IOException
    */
   public static List<String> getSourceClassesFromJar(String javaSrcPath) throws IOException
   {
      
      return getSourceClassesFromJar(javaSrcPath, null);
   }

   /**
    *  Extract all information from zip archive of java source in given package 
    * 
    * @param javaSrcPath path to source archive
    * @param packageName the package for filtering class names
    * @return set of canonical names
    * @throws IOException
    */
   public static List<String> getSourceClassesFromJar(String javaSrcPath, String packageName) throws IOException
   {
      return extract(javaSrcPath, packageName, ".java");
   }

   private static List<String> extract(String archath, String packageName, String fileExtension) throws FileNotFoundException, IOException
   {
      ArrayList<String> classes = new ArrayList<String>();
      ZipInputStream zipFile = new ZipInputStream(new FileInputStream(archath));
      ZipEntry zipEntry;
      while (true)
      {
         zipEntry = zipFile.getNextEntry();
         if (zipEntry == null)
         {
            break;
         }
         if (zipEntry.getName().endsWith(fileExtension))
         {
            String fqn = zipEntry.getName();

            try {
               fqn = fqn.substring(0, fqn.lastIndexOf("."));
               fqn = fqn.replaceAll("/", "\\.");
               if (packageName != null)
               {
                  if (fqn.startsWith(packageName))
                  {
                     classes.add(fqn);
                  }
               }
               else
               {
                  classes.add(fqn);
               }
               
            } catch (Exception e) {
               LOG.error("Could not add class " + fqn);
            }
                        
         }
      }
      return classes;
   }
   
  
     }
