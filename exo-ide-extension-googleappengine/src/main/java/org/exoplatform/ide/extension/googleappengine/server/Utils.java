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
package org.exoplatform.ide.extension.googleappengine.server;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class Utils
{
   private static final SecureRandom STAGING_DIR_NAME_GENERATOR = new SecureRandom();

   public static java.io.File createTempDir(String prefix) throws IOException
   {
      java.io.File dir = new java.io.File(
         new java.io.File(System.getProperty("java.io.tmpdir")),
         (prefix == null || prefix.isEmpty() ? "ide-appengine" : prefix) + Long.toString(
            Math.abs(STAGING_DIR_NAME_GENERATOR.nextLong()))
      );
      if (!dir.mkdirs())
      {
         throw new IOException("Unable create temp directory. ");
      }
      return dir;
   }

   public static void unzip(InputStream in, java.io.File targetDir) throws IOException
   {
      ZipInputStream zipIn = null;
      try
      {
         zipIn = new ZipInputStream(in);
         byte[] buf = new byte[8192];
         ZipEntry zipEntry;
         while ((zipEntry = zipIn.getNextEntry()) != null)
         {
            java.io.File file = new java.io.File(targetDir, zipEntry.getName());
            if (!zipEntry.isDirectory())
            {
               java.io.File parent = file.getParentFile();
               if (!parent.exists())
               {
                  parent.mkdirs();
               }
               FileOutputStream fos = new FileOutputStream(file);
               try
               {
                  int r;
                  while ((r = zipIn.read(buf)) != -1)
                  {
                     fos.write(buf, 0, r);
                  }
               }
               finally
               {
                  fos.close();
               }
            }
            else
            {
               file.mkdirs();
            }
            zipIn.closeEntry();
         }
      }
      finally
      {
         if (zipIn != null)
         {
            zipIn.close();
         }
         in.close();
      }
   }

   public static void unzip(java.io.File zip, java.io.File targetDir) throws IOException
   {
      unzip(new FileInputStream(zip), targetDir);
   }
}
