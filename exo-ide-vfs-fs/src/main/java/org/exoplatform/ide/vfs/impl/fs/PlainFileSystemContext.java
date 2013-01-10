/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package org.exoplatform.ide.vfs.impl.fs;

import org.apache.commons.codec.binary.Base64;
import org.exoplatform.ide.commons.FileUtils;
import org.exoplatform.ide.vfs.server.ContentStream;
import org.exoplatform.ide.vfs.server.DeleteOnCloseFileInputStream;
import org.exoplatform.ide.vfs.server.cache.Cache;
import org.exoplatform.ide.vfs.server.cache.SLRUCache;
import org.exoplatform.ide.vfs.server.exceptions.ItemAlreadyExistException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemRuntimeException;
import org.exoplatform.ide.vfs.shared.ItemType;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
class PlainFileSystemContext
{
   /* === Cache parameters === */
   // setup
   private static final int partitionsNum = 1 << 3;
   private static final int protectedSize = 200;
   private static final int probationarySize = 100;
   // calculated
   private static final int mask = partitionsNum - 1;
   private static final int partitionProtectedSize = protectedSize / partitionsNum;
   private static final int partitionProbationarySize = probationarySize / partitionsNum;
   /* ======================== */

   private static final long ACCESS_FILE_TIMEOUT = 5000; // 5 seconds
   private static final int MAX_BUFFER_SIZE = 100 * 1024; // 100k
   private static final int COPY_BUFFER_SIZE = 8 * 1024; // 8k;

   static final String ROOT_ID = "@root";

   private final Cache<String, VirtualFile>[] cachePartitions;
   private final Lock[] cacheLocks;
   private final VirtualFile rootHandler;
   private final Path rootPath;
   private final java.io.File ioRoot;
   private final ReadWriteFileLock fileLock;

   /**
    * @param root
    *    root directory for virtual file system. Any file in higher level than root are not accessible through
    *    virtual file system API.
    */
   @SuppressWarnings("unchecked")
   PlainFileSystemContext(java.io.File root)
   {
      ioRoot = root;
      rootPath = Path.ROOT;
      rootHandler = new VirtualFile(ROOT_ID, rootPath, ItemType.FOLDER, /*ioRoot*/-1);
      cacheLocks = new Lock[partitionsNum];
      cachePartitions = new SLRUCache[partitionsNum];
      for (int i = 0; i < partitionsNum; i++)
      {
         cachePartitions[i] = new SLRUCache<String, VirtualFile>(partitionProtectedSize, partitionProbationarySize);
         cacheLocks[i] = new ReentrantLock();
      }
      fileLock = new ReadWriteFileLock(1024);
   }

   VirtualFile getRoot()
   {
      return rootHandler;
   }

   VirtualFile getVirtualFileById(String id)
   {
      if (ROOT_ID.equals(id))
      {
         return getRoot();
      }
      final int index = id.hashCode() & mask;
      final Lock lock = cacheLocks[index];
      lock.lock();
      try
      {
         final Cache<String, VirtualFile> partition = cachePartitions[index];
         VirtualFile virtualFile = partition.get(id);
         if (virtualFile == null)
         {
            Path path = idToPath(id);
            java.io.File ioFile = getIoFile(path);
            if (ioFile.exists())
            {
               virtualFile = new VirtualFile(id,
                  idToPath(id),
                  ioFile.isDirectory() ? (new java.io.File(ioFile, ".project").exists() ? ItemType.PROJECT : ItemType.FOLDER) : ItemType.FILE,
                  ioFile.isDirectory() ? -1 : ioFile.length());
               partition.put(id, virtualFile);
            }
         }

         return virtualFile;
      }
      finally
      {
         lock.unlock();
      }
   }

   VirtualFile getVirtualFileByPath(String path)
   {
      return getVirtualFileByPath(Path.fromString(path));
   }

   VirtualFile getVirtualFileByPath(Path path)
   {
      return getVirtualFileById(pathToId(path));
   }

   boolean isLocked(VirtualFile virtualFile)
   {
      return false; // TODO
   }

   VirtualFile createFile(VirtualFile parent, String name, String mediaType, InputStream content)
      throws VirtualFileSystemException
   {
      // TODO : save media-type in metadata storage
      final Path parentPath = parent.getPath();
      final Path newPath = parentPath.newPath(name);
      java.io.File ioFile = getIoFile(newPath);
      if (null == content)
      {
         // If there is no content for file.
         try
         {
            if (!ioFile.createNewFile())
            {
               throw new ItemAlreadyExistException("Item with the name '" + name + "' already exists. ");
            }
         }
         catch (IOException e)
         {
            throw new VirtualFileSystemException(e.getMessage(), e);
         }
      }
      else
      {
         // Try to get lock first.
         try
         {
            fileLock.acquireWrite(ACCESS_FILE_TIMEOUT, newPath);
         }
         catch (InterruptedException e)
         {
            // not expected
            throw new VirtualFileSystemRuntimeException(e);
         }
         catch (TimeoutException e)
         {
            throw new VirtualFileSystemRuntimeException("Unable get file lock for '" + newPath + "'. ");
         }
         try
         {
            if (ioFile.exists())
            {
               // File may be already created by other thread.
               throw new ItemAlreadyExistException("Item with the name '" + name + "' already exists. ");
            }
            FileOutputStream fOut = null;
            try
            {
               fOut = new FileOutputStream(ioFile);
               FileChannel fOutChannel = fOut.getChannel();
               ReadableByteChannel contentChannel = Channels.newChannel(content);
               ByteBuffer buff = ByteBuffer.allocate(COPY_BUFFER_SIZE);
               while (contentChannel.read(buff) != -1)
               {
                  buff.flip();
                  do
                  {
                     fOutChannel.write(buff);
                  }
                  while (buff.hasRemaining());
                  buff.clear();
               }
            }
            catch (IOException e)
            {
               throw new VirtualFileSystemException(e.getMessage(), e);
            }
            finally
            {
               // Also close FileChannel.
               close(fOut);
            }
         }
         catch (Exception ee)
         {
            ee.printStackTrace();
         }
         finally
         {
            fileLock.releaseWrite(newPath);
         }
      }
      return getVirtualFileByPath(newPath);
   }

   VirtualFile createFolder(VirtualFile parent, String name) throws VirtualFileSystemException
   {
      final Path parentPath = parent.getPath();
      // Name may be hierarchical, e.g. folder1/folder2/folder3.
      // Some folder in hierarchy may already exists but at least one folder must be created.
      // If no one folder created then ItemAlreadyExistException is thrown.
      // Method returns first created folder.
      Path created = null;
      Path current = parentPath;
      for (String e : Path.fromString(name).elements())
      {
         current = current.newPath(e);
         java.io.File ioFile = getIoFile(current);
         if (ioFile.mkdirs() && created == null)
         {
            created = current;
         }
      }
      if (created == null)
      {
         throw new ItemAlreadyExistException("Unable create folder '" + parentPath.newPath(name) +
            "'. Probably item with the same name already exists. ");
      }
      return getVirtualFileByPath(created);
   }

   List<VirtualFile> getChildren(VirtualFile parent)
   {
      String[] names = getIoFile(parent.getPath()).list();
      if (names == null)
      {
         return null;
      }
      Arrays.sort(names); // Always sort to get the exact same order of files for each listing.
      List<VirtualFile> children = new ArrayList<VirtualFile>(names.length);
      final Path parentPath = parent.getPath();
      for (String name : names)
      {
         VirtualFile child = getVirtualFileByPath(parentPath.newPath(name));
         if (child != null)
         {
            children.add(child);
         }
      }
      return children;
   }

   VirtualFile getParent(VirtualFile child)
   {
      Path path = child.getPath();
      if (path.isRoot())
      {
         return null;
      }
      return getVirtualFileByPath(path.getParent());
   }

   ContentStream getContent(VirtualFile virtualFile) throws VirtualFileSystemException
   {
      final Path path = virtualFile.getPath();
      try
      {
         fileLock.acquireRead(ACCESS_FILE_TIMEOUT, path);
      }
      catch (InterruptedException e)
      {
         // not expected
         throw new VirtualFileSystemRuntimeException(e);
      }
      catch (TimeoutException e)
      {
         throw new VirtualFileSystemRuntimeException("Unable get content. Item '" + path +
            "' is locked by another process for write or max number of allowed concurrent readers is reached. ");
      }
      try
      {
         java.io.File ioFile = getIoFile(virtualFile.getPath());
         FileInputStream fIn = null;
         try
         {
            fIn = new FileInputStream(ioFile);
            if (ioFile.length() <= MAX_BUFFER_SIZE)
            {
               // If file small enough save its content in memory.
               byte[] buff = new byte[(int)ioFile.length()];
               int offset = 0;
               int len = buff.length;
               int r;
               while ((r = fIn.read(buff, offset, len)) > 0)
               {
                  offset += r;
                  len -= r;
               }
               return new ContentStream(virtualFile.getPath().getName(), new ByteArrayInputStream(buff),
                  virtualFile.getMediaType(), buff.length, new Date(ioFile.lastModified()));
            }

            // Otherwise copy this file to be able release the file lock before leave this method.
            java.io.File f = java.io.File.createTempFile("spool_file", null);
            FileOutputStream fOut = new FileOutputStream(f);
            try
            {
               FileChannel fInChannel = fIn.getChannel();
               fOut.getChannel().transferFrom(fInChannel, 0, fInChannel.size());
            }
            finally
            {
               // Also close output FileChannel.
               close(fOut);
            }

            return new ContentStream(virtualFile.getPath().getName(), new DeleteOnCloseFileInputStream(f),
               virtualFile.getMediaType(), ioFile.length(), new Date(ioFile.lastModified()));
         }
         catch (IOException e)
         {
            throw new VirtualFileSystemException(e.getMessage(), e);
         }
         finally
         {
            // Also close input FileChannel.
            close(fIn);
         }
      }
      finally
      {
         fileLock.releaseRead(path);
      }
   }

   void delete(VirtualFile virtualFile) throws VirtualFileSystemException
   {
      try
      {
         fileLock.acquireWrite(ACCESS_FILE_TIMEOUT, virtualFile.getPath());
      }
      catch (InterruptedException e)
      {
         // not expected
         throw new VirtualFileSystemRuntimeException(e);
      }
      catch (TimeoutException e)
      {
         throw new VirtualFileSystemRuntimeException("Unable get file lock for '" + virtualFile.getPath() + "'. ");
      }

      try
      {
         if (!FileUtils.deleteRecursive(getIoFile(virtualFile.getPath())))
         {
            throw new VirtualFileSystemRuntimeException("Unable delete file or folder '" + virtualFile.getPath() + "'. ");
         }
      }
      finally
      {
         fileLock.releaseWrite(virtualFile.getPath());
      }
   }

   private void close(Closeable closeable)
   {
      if (closeable != null)
      {
         try
         {
            closeable.close();
         }
         catch (IOException ignored)
         {
         }
      }
   }

   private java.io.File getIoFile(Path path)
   {
      return new java.io.File(ioRoot, path.toString());
   }

   private Path idToPath(String id)
   {
      if (ROOT_ID.equals(id))
      {
         return rootPath;
      }
      byte[] path = Base64.decodeBase64(id);
      try
      {
         return '/' == java.io.File.separatorChar
            ? Path.fromString(new String(path, "UTF-8"))
            : Path.fromString(new String(path, "UTF-8").replace('/', java.io.File.pathSeparatorChar));
      }
      catch (UnsupportedEncodingException e)
      {
         // Should never happen.
         throw new IllegalStateException(e.getMessage(), e);
      }
   }

   private String pathToId(Path path)
   {
      if (path.isRoot())
      {
         return ROOT_ID;
      }
      try
      {
         return '/' == java.io.File.separatorChar
            ? Base64.encodeBase64URLSafeString(path.toString().getBytes("UTF-8"))
            : Base64.encodeBase64URLSafeString(path.toString().replace(java.io.File.separatorChar, '/').getBytes("UTF-8"));
      }
      catch (UnsupportedEncodingException e)
      {
         // Should never happen.
         throw new IllegalStateException(e.getMessage(), e);
      }
   }
}
