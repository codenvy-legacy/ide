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

import org.exoplatform.ide.vfs.server.ContentStream;
import org.exoplatform.ide.vfs.server.PropertyFilter;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.exceptions.ItemNotFoundException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemType;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.LinkedList;
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
   private static final Pattern SPRING1 = Pattern.compile("WEB-INF/lib/spring-core.*\\.jar");
   private static final Pattern SPRING2 = Pattern.compile("WEB-INF/classes/org/springframework/.+");
   private static final Pattern GRAILS = Pattern.compile("WEB-INF/lib/grails-web.*\\.jar");
   private static final Pattern SINATRA = Pattern.compile("^\\s*require\\s*[\"']sinatra[\"']");

   static interface NameFilter
   {
      boolean accept(String name);
   }

   static final NameFilter UPLOAD_FILTER = new NameFilter()
   {
      @Override
      public boolean accept(String name)
      {
         return !(".cloudfoundry-application".equals(name) || ".vmc_target".equals(name) || ".project".equals(name) // eXo IDE specific.  
            || name.endsWith("~") || name.endsWith(".log")); // Do the same as cloud foundry command line tool does. 
      }
   };

   static final NameFilter WAR_FILTER = new TypeFilter(".war");
   static final NameFilter RUBY_FILTER = new TypeFilter(".rb");
   static final NameFilter JS_FILTER = new TypeFilter(".js");

   private static class TypeFilter implements NameFilter
   {
      private final String ext;

      public TypeFilter(String ext)
      {
         this.ext = ext;
      }

      @Override
      public boolean accept(String name)
      {
         return name.endsWith(ext);
      }
   }

   static void copy(VirtualFileSystem vfs, String source, java.io.File target) throws VirtualFileSystemException,
      IOException
   {
      InputStream zip = vfs.exportZip(source).getStream();
      unzip(zip, target);
   }

   static List<java.io.File> list(java.io.File dir, NameFilter filter)
   {
      if (!dir.isDirectory())
      {
         throw new IllegalArgumentException("Not a directory. ");
      }
      List<java.io.File> files = new ArrayList<java.io.File>();
      LinkedList<java.io.File> q = new LinkedList<java.io.File>();
      q.add(dir);
      while (!q.isEmpty())
      {
         java.io.File current = q.pop();
         java.io.File[] list = current.listFiles();
         if (list != null)
         {
            for (int i = 0; i < list.length; i++)
            {
               final java.io.File f = list[i];
               if (f.isDirectory())
               {
                  // Always hide .git directory. Not need to send it to CloudFoundry infrastructure.
                  if (!(".git".equals(f.getName())))
                  {
                     q.push(f);
                  }
               }
               else if (filter.accept(f.getName()))
               {
                  files.add(f);
               }
            }
         }
      }
      return files;
   }

   static void zipDir(String zipRootPath, java.io.File dir, java.io.File zip, NameFilter filter) throws IOException
   {
      if (!dir.isDirectory())
      {
         throw new IllegalArgumentException("Not a directory. ");
      }
      FileOutputStream fos = null;
      ZipOutputStream zipOut = null;
      try
      {
         byte[] b = new byte[8192];
         fos = new FileOutputStream(zip);
         zipOut = new ZipOutputStream(fos);
         LinkedList<java.io.File> q = new LinkedList<java.io.File>();
         q.add(dir);
         while (!q.isEmpty())
         {
            java.io.File current = q.pop();
            java.io.File[] list = current.listFiles();
            if (list != null)
            {
               for (int i = 0; i < list.length; i++)
               {
                  final java.io.File f = list[i];
                  final String zipEntryName =
                     f.getAbsolutePath().substring(zipRootPath.length() + 1).replace('\\', '/');
                  if (f.isDirectory())
                  {
                     if (!(".git".equals(f.getName())))
                     {
                        q.push(f);
                        zipOut.putNextEntry(new ZipEntry(zipEntryName.endsWith("/") //
                           ? zipEntryName //
                           : zipEntryName + "/"));
                     }
                  }
                  else if (filter.accept(f.getName()))
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

   static void unzip(java.io.File zip, java.io.File targetDir) throws IOException
   {
      unzip(new FileInputStream(zip), targetDir);
   }

   /**
    * Read the first line from file or <code>null</code> if file not found.
    */
   static String readFile(VirtualFileSystem vfs, Item parent, String name) throws VirtualFileSystemException,
      IOException
   {
      return readFile(vfs, (parent.getPath() + "/" + name));
   }

   /**
    * Read the first line from file or <code>null</code> if file not found.
    */
   static String readFile(VirtualFileSystem vfs, String path) throws VirtualFileSystemException, IOException
   {
      InputStream in = null;
      BufferedReader r = null;
      try
      {
         ContentStream content = vfs.getContent(path, null);
         in = content.getStream();
         r = new BufferedReader(new InputStreamReader(in));
         String line = r.readLine();
         return line;
      }
      catch (ItemNotFoundException e)
      {
      }
      finally
      {
         if (r != null)
         {
            r.close();
         }
         if (in != null)
         {
            in.close();
         }
      }
      return null;
   }

   static void delete(VirtualFileSystem vfs, String parentId, String name) throws VirtualFileSystemException
   {
      Item item = vfs.getItem(parentId, PropertyFilter.NONE_FILTER);
      String parentPath = item.getPath();
      try
      {
         Item file = vfs.getItemByPath(parentPath + "/" + name, null, PropertyFilter.NONE_FILTER);
         vfs.delete(file.getId(), null);
      }
      catch (ItemNotFoundException ignored)
      {
      }
   }

   static void unzip(InputStream in, java.io.File targetDir) throws IOException
   {
      ZipInputStream zipIn = null;
      try
      {
         zipIn = new ZipInputStream(in);
         byte[] b = new byte[8192];
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

   static boolean delete(java.io.File fileOrDir)
   {
      if (fileOrDir.isDirectory())
      {
         for (java.io.File f : fileOrDir.listFiles())
         {
            if (!delete(f))
            {
               return false;
            }
         }
      }
      return fileOrDir.delete();
   }

   static String detectFramework(java.io.File war) throws IOException
   {
      if (war.isFile() && WAR_FILTER.accept(war.getName()))
      {
         FileInputStream fis = null;
         ZipInputStream zipIn = null;
         try
         {
            fis = new FileInputStream(war);
            zipIn = new ZipInputStream(fis);
            Matcher m1 = null;
            Matcher m2 = null;
            Matcher m3 = null;
            for (ZipEntry e = zipIn.getNextEntry(); e != null; e = zipIn.getNextEntry())
            {
               String name = e.getName();
               m1 = m1 == null ? SPRING1.matcher(name) : m1.reset(name);
               if (m1.matches())
               {
                  return "spring";
               }
               m2 = m2 == null ? SPRING2.matcher(name) : m2.reset(name);
               if (m2.matches())
               {
                  return "spring";
               }
               m3 = m3 == null ? GRAILS.matcher(name) : m3.reset(name);
               if (m3.matches())
               {
                  return "grails";
               }
            }
         }
         finally
         {
            if (zipIn != null)
            {
               zipIn.close();
            }
            if (fis != null)
            {
               fis.close();
            }
         }
         // Java web application if Spring or Grails frameworks is not detected. But use Spring settings for it.
         return "spring";
      }
      return null;
   }

   static String detectFramework(VirtualFileSystem vfs, String projectId) throws VirtualFileSystemException,
      IOException
   {
      Item project = vfs.getItem(projectId, PropertyFilter.NONE_FILTER);
      try
      {
         vfs.getItemByPath(project.getPath() + "/config/environment.rb", null, PropertyFilter.NONE_FILTER);
         return "rails3";
      }
      catch (ItemNotFoundException e)
      {
      }
      List<Item> children = vfs.getChildren(projectId, -1, 0, PropertyFilter.NONE_FILTER).getItems();
      Matcher m = null;
      // Check each ruby file to include "sinatra" import. 
      for (Item i : children)
      {
         if (ItemType.FILE == i.getItemType() && RUBY_FILTER.accept(i.getName()))
         {
            InputStream in = null;
            BufferedReader reader = null;
            try
            {
               in = vfs.getContent(i.getId()).getStream();
               reader = new BufferedReader(new InputStreamReader(in));
               String line;
               while ((line = reader.readLine()) != null)
               {
                  m = m == null ? SINATRA.matcher(line) : m.reset(line);
                  if (m.matches())
                  {
                     return "sinatra";
                  }
               }
            }
            finally
            {
               if (reader != null)
               {
                  reader.close();
               }
               if (in != null)
               {
                  in.close();
               }
            }
         }
      }
      // Lookup app.js, index.js or main.js files.
      for (Item i : children)
      {
         if (ItemType.FILE == i.getItemType() //
            && ("app.js".equals(i.getName()) || "index.js".equals(i.getName()) || "main.js".equals(i.getName())))
         {
            return "node";
         }
      }
      return null;
   }

   static String countFileHash(java.io.File file, MessageDigest digest) throws IOException
   {
      FileInputStream fis = null;
      DigestInputStream dis = null;
      try
      {
         fis = new FileInputStream(file);
         dis = new DigestInputStream(fis, digest);
         while (dis.read() != -1)
         {
         }
         return toHex(digest.digest());
      }
      finally
      {
         if (dis != null)
         {
            dis.close();
         }
         if (fis != null)
         {
            dis.close();
         }
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
}
