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
package org.exoplatform.ide.extension.java.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import sun.net.www.protocol.jar.JarURLConnection;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */
public class JavaProjectArchetype
{

   /**
    * Destination folder where project will be created.
    */
   private File destination;
   
   /**
    * Project's name.
    */
   private String projectName;

   /**
    * Project's group ID.
    */
   private String groupId;

   /**
    * Project's artifact ID.
    */
   private String artifactId;

   /**
    * Project's version.
    */
   private String version;

   public JavaProjectArchetype(File destination, String projectName, String groupId, String artifactId, String version)
   {
      this.destination = destination;
      this.projectName = projectName;
      this.groupId = groupId;
      this.artifactId = artifactId;
      this.version = version;
   }

   public void exportResourcesFromURL(URL url) throws IOException
   {
      if (url.getProtocol().startsWith("jar"))
      {
         JarURLConnection con = new JarURLConnection(url, null);
         copyJarResources(con);
      }
      else
      {
         File template = new File(url.getFile());
         if (template.exists())
            FileUtils.copyDirectory(template, destination);
         else
            throw new IllegalStateException("Can not create project \"" + artifactId + "\".");
      }
   }

   private void copyJarResources(final JarURLConnection jarConnection) throws IOException
   {
      final JarFile jarFile = jarConnection.getJarFile();

      for (final Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements();)
      {
         final JarEntry entry = e.nextElement();
         if (entry.getName().startsWith(jarConnection.getEntryName()))
         {
            final String filename = StringUtils.removeStart(entry.getName(), //
               jarConnection.getEntryName());

            final File f = new File(destination, filename);
            if (!entry.isDirectory())
            {
               final InputStream entryInputStream = jarFile.getInputStream(entry);
               if ("/pom.xml".equals(filename))
               {
                  copyPomXML(entryInputStream, f);
               }
               else
               {
                  copyStream(entryInputStream, f);
               }
            }
            else
            {
               if (!ensureDirectoryExists(f))
               {
                  throw new IOException("Could not create directory: " + f.getAbsolutePath());
               }
            }
         }
      }
   }

   private void copyStream(InputStream is, File destFile) throws IOException
   {
      OutputStream outputStream = new FileOutputStream(destFile);

      final byte[] buf = new byte[1024];

      int readed = 0;
      while ((readed = is.read(buf)) > 0)
      {
         outputStream.write(buf, 0, readed);
      }

      is.close();
      outputStream.close();
   }

   private void copyPomXML(InputStream is, File destFile) throws IOException
   {
      PrintWriter writer = new PrintWriter(destFile);

      InputStreamReader streamReader = new InputStreamReader(is);
      BufferedReader reader = new BufferedReader(streamReader);

      String line = reader.readLine();
      while (line != null)
      {
         // replacing ${name} ${groupId}, ${artifactId}, ${version}
         while (line.indexOf("${name}") >= 0) {
            line = line.replace("${name}", projectName);
         }
         
         while (line.indexOf("${groupId}") >= 0)
         {
            line = line.replace("${groupId}", groupId);
         }

         while (line.indexOf("${artifactId}") >= 0)
         {
            line = line.replace("${artifactId}", artifactId);
         }

         while (line.indexOf("${version}") >= 0)
         {
            line = line.replace("${version}", version);
         }

         writer.println(line);
         
         line = reader.readLine();
      }
      writer.println();

      writer.close();
      is.close();
   }

   private boolean ensureDirectoryExists(final File f)
   {
      return f.exists() || f.mkdir();
   }

}
