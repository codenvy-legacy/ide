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
package org.exoplatform.ide.maven;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class BuildHelper
{
   private static final String BUILDER_FILE_PREFIX = "build-";
   private static final SecureRandom gen = new SecureRandom();

   public static FilenameFilter makeBuilderFilesFilter()
   {
      return new FilenameFilter()
      {
         @Override
         public boolean accept(File dir, String name)
         {
            return name.startsWith(BUILDER_FILE_PREFIX);
         }
      };
   }

   /**
    * Create new directory with random name.
    *
    * @param parent
    *    parent for creation directory
    * @return newly created directory
    */
   public static File makeProjectDirectory(File parent)
   {
      File dir = new File(parent, BUILDER_FILE_PREFIX + Long.toString(Math.abs(gen.nextLong())));
      if (!dir.mkdirs())
      {
         throw new RuntimeException("Unable create project directory. ");
      }
      return dir;
   }

   /**
    * Remove specified file or directory.
    *
    * @param fileOrDirectory
    *    the file or directory to cancel
    * @return <code>true</code> if specified File was deleted and <code>false</code> otherwise
    */
   public static boolean delete(File fileOrDirectory)
   {
      if (fileOrDirectory.isDirectory())
      {
         for (File f : fileOrDirectory.listFiles())
         {
            if (!delete(f))
            {
               return false;
            }
         }
      }
      return !fileOrDirectory.exists() || fileOrDirectory.delete();
   }

   /**
    * Unzip content of input stream in directory.
    *
    * @param in
    *    zipped content
    * @param targetDir
    *    target directory
    * @throws IOException
    *    if any i/o error occurs
    */
   public static void unzip(InputStream in, File targetDir) throws IOException
   {
      ZipInputStream zipIn = null;
      try
      {
         zipIn = new ZipInputStream(in);
         byte[] b = new byte[8192];
         ZipEntry zipEntry;
         while ((zipEntry = zipIn.getNextEntry()) != null)
         {
            File file = new File(targetDir, zipEntry.getName());
            if (!zipEntry.isDirectory())
            {
               File parent = file.getParentFile();
               if (!parent.exists())
               {
                  parent.mkdirs();
               }
               FileOutputStream fos = new FileOutputStream(file);
               try
               {
                  int r;
                  while ((r = zipIn.read(b)) != -1)
                  {
                     fos.write(b, 0, r);
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

   public static void zip(File dir, File zip) throws IOException
   {
      if (!dir.isDirectory())
      {
         throw new IllegalArgumentException("Not a directory. ");
      }
      final String zipRootPath = dir.getAbsolutePath();
      FileOutputStream fos = null;
      ZipOutputStream zipOut = null;
      try
      {
         byte[] b = new byte[8192];
         fos = new FileOutputStream(zip);
         zipOut = new ZipOutputStream(fos);
         LinkedList<File> q = new LinkedList<File>();
         q.add(dir);
         while (!q.isEmpty())
         {
            File current = q.pop();
            File[] list = current.listFiles();
            if (list != null)
            {
               for (File f : list)
               {
                  final String zipEntryName =
                     f.getAbsolutePath().substring(zipRootPath.length() + 1).replace('\\', '/');
                  if (f.isDirectory())
                  {
                     q.push(f);
                     zipOut.putNextEntry(new ZipEntry(zipEntryName.endsWith("/")
                        ? zipEntryName
                        : zipEntryName + '/'));
                  }
                  else
                  {
                     zipOut.putNextEntry(new ZipEntry(zipEntryName));
                     FileInputStream in = null;
                     try
                     {
                        in = new FileInputStream(f);
                        int r;
                        while ((r = in.read(b)) != -1)
                        {
                           zipOut.write(b, 0, r);
                        }
                     }
                     finally
                     {
                        if (in != null)
                        {
                           in.close();
                        }
                        zipOut.closeEntry();
                     }
                  }
               }
            }
         }
      }
      finally
      {
         if (zipOut != null)
         {
            zipOut.close();
         }
         if (fos != null)
         {
            fos.close();
         }
      }
   }

   private BuildHelper()
   {
   }
}
