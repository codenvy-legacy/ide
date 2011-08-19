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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
class FilesHelper
{
   static final Pattern SPRING1 = Pattern.compile("WEB-INF/lib/spring-core.*\\.jar");
   static final Pattern SPRING2 = Pattern.compile("WEB-INF/classes/org/springframework/.+");
   static final Pattern GRAILS = Pattern.compile("WEB-INF/lib/grails-web.*\\.jar");
   static final Pattern SINATRA = Pattern.compile("^\\s*require\\s*[\"']sinatra[\"']");

   static final FilenameFilter UPLOAD_FILE_FILTER = new FilenameFilter()
   {
      @Override
      public boolean accept(File dir, String name)
      {
         return !(".cloudfoundry-application".equals(name) // eXo IDE specific.  
            || name.endsWith("~") || name.endsWith(".log")); // Do the same as cloud foundry command line tool does. 
      }
   };

   static final FilenameFilter WAR_FILE_FILTER = new TypeFileFilter(".war");
   static final FilenameFilter RUBY_FILE_FILTER = new TypeFileFilter(".rb");
   static final FilenameFilter JS_FILE_FILTER = new TypeFileFilter(".js");
   static final FilenameFilter JAVA_FILE_FILTER = new TypeFileFilter(".java");
   static final FilenameFilter GROOVY_FILE_FILTER = new TypeFileFilter(".groovy");

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

   static void copyDir(File source, File target, FilenameFilter filter) throws IOException
   {
      if (source.isDirectory())
      {
         if (!target.exists())
            target.mkdir();
         String[] files = source.list();
         for (int i = 0; i < files.length; i++)
         {
            if (filter.accept(target, files[i]))
               copyDir(new File(source, files[i]), new File(target, files[i]), filter);
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

   static void fileList(File dir, Collection<File> files, FilenameFilter filter)
   {
      File[] list = dir.listFiles();
      if (list != null)
      {
         for (int i = 0; i < list.length; i++)
         {
            if (list[i].isDirectory())
               fileList(list[i], files, filter);
            else if (filter.accept(dir, list[i].getName()))
               files.add(list[i]);
         }
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

   static void zipDir(File dir, File zip, FilenameFilter filter) throws IOException
   {
      ZipOutputStream zipOut = null;
      try
      {
         zipOut = new ZipOutputStream(new FileOutputStream(zip));
         List<File> files = new ArrayList<File>();
         fileList(dir, files, filter);

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
      }
      finally
      {
         if (zipOut != null)
            zipOut.close();
      }
   }

   static void unzip(File zip, File targetDir) throws IOException
   {
      ZipInputStream zipIn = null;
      try
      {
         zipIn = new ZipInputStream(new FileInputStream(zip));
         ZipEntry zipEntry;
         while ((zipEntry = zipIn.getNextEntry()) != null)
         {
            if (!zipEntry.isDirectory())
            {
               File file = new File(targetDir, zipEntry.getName());
               if (!file.getParentFile().exists())
                  file.getParentFile().mkdirs();

               FileOutputStream fo = null;
               try
               {
                  fo = new FileOutputStream(file);
                  byte[] b = new byte[1024];
                  int r;
                  while ((r = zipIn.read(b)) != -1)
                     fo.write(b, 0, r);
               }
               finally
               {
                  try
                  {
                     if (fo != null)
                        fo.close();
                  }
                  finally
                  {
                     zipIn.closeEntry();
                  }
               }
            }
         }
      }
      finally
      {
         if (zipIn != null)
            zipIn.close();
      }
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

   static String detectFramework(File path) throws IOException
   {
      if (path.isFile() && WAR_FILE_FILTER.accept(path.getParentFile(), path.getName()))
      {
         // Spring application ?
         ZipInputStream zip = null;
         try
         {
            zip = new ZipInputStream(new FileInputStream(path));
            Matcher m1 = null;
            Matcher m2 = null;
            Matcher m3 = null;
            for (ZipEntry e = zip.getNextEntry(); e != null; e = zip.getNextEntry())
            {
               String name = e.getName();
               m1 = m1 == null ? SPRING1.matcher(name) : m1.reset(name);
               if (m1.matches())
                  return "spring";

               m2 = m2 == null ? SPRING2.matcher(name) : m2.reset(name);
               if (m2.matches())
                  return "spring";

               m3 = m3 == null ? GRAILS.matcher(name) : m3.reset(name);
               if (m3.matches())
                  return "grails";
            }
         }

         finally
         {
            if (zip != null)
               zip.close();
         }

         // Java web application if Spring or Grails frameworks is not detected. But use Spring settings for it.
         return "spring";
      }

      if (new File(path, "config/environment.rb").exists())
         return "rails3";

      // Lookup *.rb files. 
      File[] files = path.listFiles(RUBY_FILE_FILTER);

      if (files != null && files.length > 0)
      {
         Matcher m = null;
         // Check each ruby file to include "sinatra" import. 
         for (int i = 0; i < files.length; i++)
         {
            BufferedReader freader = null;
            try
            {
               freader = new BufferedReader(new FileReader(files[i]));

               String line;
               while ((line = freader.readLine()) != null)
               {
                  m = m == null ? SINATRA.matcher(line) : m.reset(line);
                  if (m.matches())
                     return "sinatra";
               }
            }
            finally
            {
               if (freader != null)
                  freader.close();
            }
         }
      }

      // Lookup app.js, index.js or main.js files. 
      files = path.listFiles(JS_FILE_FILTER);

      if (files != null && files.length > 0)
      {
         for (int i = 0; i < files.length; i++)
         {
            if ("app.js".equals(files[i].getName()) //
               || "index.js".equals(files[i].getName()) //
               || "main.js".equals(files[i].getName()))
               return "node";
         }
      }

      List<File> tmp = new ArrayList<File>();
      fileList(path, tmp, JAVA_FILE_FILTER);
      if (tmp.size() > 0)
         return "spring";

      tmp.clear();
      fileList(path, tmp, GROOVY_FILE_FILTER);
      if (tmp.size() > 0)
         return "grails";
      
      tmp.clear();

      return null;
   }
}
