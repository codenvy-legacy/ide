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
package org.exoplatform.ide.extension.cloudfoundry.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
class FilesHelper
{
   static final FilenameFilter EXCLUDE_FILE_FILTER = new FilenameFilter()
   {
      @Override
      public boolean accept(File dir, String name)
      {
         return ".cloudfoundry-application".equals(name);
      }
   };

   static final FilenameFilter WAR_FILE_FILTER = new TypeFileFilter(".war");
   static final FilenameFilter RUBY_FILE_FILTER = new TypeFileFilter(".rb");
   static final FilenameFilter JS_FILE_FILTER = new TypeFileFilter(".js");

   private static class TypeFileFilter implements FilenameFilter
   {
      private final String ext;

      public TypeFileFilter(String ext)
      {
         this.ext = ext;
      }

      @Override
      public boolean accept(File dir, String name)
      {
         return name.endsWith(ext);
      }
   }

   static void copyDir(File source, File target, FilenameFilter exclude) throws IOException
   {
      if (source.isDirectory())
      {
         if (!target.exists())
            target.mkdir();
         String[] files = source.list();
         for (int i = 0; i < files.length; i++)
         {
            if (!exclude.accept(target, files[i]))
               copyDir(new File(source, files[i]), new File(target, files[i]), exclude);
         }
      }
      else
      {
         FileInputStream fi = null;
         FileOutputStream fo = null;
         try
         {
            fi = new FileInputStream(source);
            fo = new FileOutputStream(target);
            byte[] b = new byte[1024];
            int r;
            while ((r = fi.read(b)) != -1)
               fo.write(b, 0, r);
         }
         finally
         {
            try
            {
               if (fi != null)
                  fi.close();
            }
            finally
            {
               if (fo != null)
                  fo.close();
            }
         }
      }
   }

   static void fileList(File dir, Collection<File> files, FilenameFilter exclude)
   {
      File[] list = dir.listFiles();
      for (int i = 0; i < list.length; i++)
      {
         if (list[i].isDirectory())
            fileList(list[i], files, exclude);
         else if (!exclude.accept(dir, list[i].getName()))
            files.add(list[i]);
      }
   }

   static String countFileHash(File file, MessageDigest digest) throws IOException
   {
      DigestInputStream d = null;
      try
      {
         d = new DigestInputStream(new FileInputStream(file), digest);
         while (d.read() != -1) //
         ;
         return toHex(digest.digest());
      }
      finally
      {
         if (d != null)
            d.close();
      }
   }

   private static final char[] hex = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
      'e', 'f'};

   static String toHex(byte[] hash)
   {
      StringBuilder b = new StringBuilder();
      for (int i = 0; i < hash.length; i++)
      {
         b.append(hex[(hash[i] >> 4) & 0x0f]);
         b.append(hex[hash[i] & 0x0f]);
      }
      return b.toString();
   }

   static void zipDir(File dir, File zip, FilenameFilter exclude) throws IOException
   {
      ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zip));
      List<File> files = new ArrayList<File>();
      fileList(dir, files, exclude);

      String dirPath = dir.getAbsolutePath() + "/";
      for (File f : files)
      {
         zipOut.putNextEntry(new ZipEntry(f.getAbsolutePath().replace(dirPath, "")));
         FileInputStream in = null;
         try
         {
            in = new FileInputStream(f);
            byte[] b = new byte[1024];
            int r;
            while ((r = in.read(b)) != -1)
               zipOut.write(b, 0, r);
         }
         finally
         {
            try
            {
               if (in != null)
                  in.close();
            }
            finally
            {
               zipOut.closeEntry();
            }
         }
      }
      zipOut.close();
   }

   static boolean delete(File fileOrDir)
   {
      if (fileOrDir.isDirectory())
      {
         for (File innerFile : fileOrDir.listFiles())
         {
            if (!delete(innerFile))
               return false;
         }
      }
      return fileOrDir.delete();
   }

}
