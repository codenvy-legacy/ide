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
package org.exoplatform.ide.vfs.impl.jcr;

import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
class ZipUtils
{
   static java.io.File unzip(InputStream in) throws IOException
   {
      java.io.File unzipRoot =
         new java.io.File(
            new java.io.File(Thread.currentThread().getContextClassLoader().getResource(".").getPath()).getParentFile(),
            "unzip_" + System.currentTimeMillis());

      ZipInputStream zip = new ZipInputStream(in);
      ZipEntry zipEntry;
      byte[] b = new byte[8192];
      while ((zipEntry = zip.getNextEntry()) != null)
      {
         String zipEntryName = zipEntry.getName();
         if (zipEntry.isDirectory())
         {
            new java.io.File(unzipRoot, zipEntryName).mkdirs();
         }
         else
         {
            java.io.File file = new java.io.File(unzipRoot, zipEntryName);
            java.io.File parent = file.getParentFile();
            if (!parent.exists())
            {
               parent.mkdirs();
            }
            FileOutputStream out = new FileOutputStream(file);
            int bytes;
            try
            {
               while ((bytes = zip.read(b)) != -1)
               {
                  out.write(b, 0, bytes);
               }
            }
            finally
            {
               out.close();
            }
         }
         zip.closeEntry();
      }
      zip.close();
      return unzipRoot;
   }
   
   static class TreeWalker
   {
      private final File fsTree;
      private final FolderData vfsTree;

      TreeWalker(java.io.File fsTree, FolderData vfsTree)
      {
         if (fsTree == null || vfsTree == null)
         {
            throw new NullPointerException();
         }
         if (!fsTree.isDirectory())
         {
            throw new IllegalArgumentException(fsTree + " not a directory. ");
         }
         this.fsTree = fsTree;
         this.vfsTree = vfsTree;
      }

      void walk() throws Exception
      {
         match(fsTree, vfsTree);
         LinkedList<java.io.File> q = new LinkedList<File>();
         q.add(fsTree);
         while (!q.isEmpty())
         {
            File current = q.pop();
            File[] files = current.listFiles();
            for (int i = 0; i < files.length; i++)
            {
               String name = files[i].getPath().substring(fsTree.getPath().length() + 1);
               TestCase.assertTrue("Item '" + vfsTree.getPath() + "/" + name + "' is missed. ", vfsTree.hasChild(name));
               match(files[i], vfsTree.getChild(name));
               if (files[i].isDirectory())
               {
                  q.add(files[i]);
               }
            }
         }
      }

      private void match(java.io.File fileOrDir, ItemData item) throws Exception
      {
         if (!fileOrDir.isDirectory())
         {
            if (!".project".equals(fileOrDir.getName()))
            {
               // File '.project' on file system is not file in VFS. 
               TestCase.assertTrue(item instanceof FileData);
               InputStream content = ((FileData)item).getContent();
               FileInputStream origin = new FileInputStream(fileOrDir);
               try
               {
                  match(origin, content);
               }
               finally
               {
                  origin.close();
                  content.close();
               }
            }
         }
         else if (new java.io.File(fileOrDir, ".project").exists())
         {
            TestCase.assertTrue(item instanceof ProjectData);
         }
         else
         {
            TestCase.assertTrue(item instanceof FolderData);
         }
      }

      private void match(InputStream in1, InputStream in2) throws IOException
      {
         ByteArrayOutputStream out1 = new ByteArrayOutputStream();
         ByteArrayOutputStream out2 = new ByteArrayOutputStream();
         int r;
         byte[] buf = new byte[1024];
         while ((r = in1.read(buf)) != -1)
         {
            out1.write(buf, 0, r);
         }
         r = -1;
         while ((r = in2.read(buf)) != -1)
         {
            out2.write(buf, 0, r);
         }
         byte[] bytes1 = out1.toByteArray();
         byte[] bytes2 = out2.toByteArray();
         TestCase.assertEquals(bytes1.length, bytes2.length);
         for (int i = 0; i < bytes1.length; i++)
         {
            byte b1 = bytes1[i];
            byte b2 = bytes2[i];
            TestCase.assertEquals(b1, b2);
         }
      }
   }

   private ZipUtils()
   {
   }
}
