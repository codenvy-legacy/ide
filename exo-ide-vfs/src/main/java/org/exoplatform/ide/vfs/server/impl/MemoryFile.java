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
package org.exoplatform.ide.vfs.server.impl;

import org.exoplatform.ide.commons.NameGenerator;
import org.exoplatform.ide.vfs.server.exceptions.LockException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.Lock;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
class MemoryFile extends MemoryItem
{
   private byte[] bytes;
   private final Lock[] lockHolder = new Lock[1];

   MemoryFile(String name, String mediaType, byte[] bytes) throws VirtualFileSystemException
   {
      super(ItemType.FILE, name);
      this.mediaType = mediaType;
      this.bytes = bytes;
   }

   String lock() throws VirtualFileSystemException
   {
      synchronized (lockHolder)
      {
         Lock lock = lockHolder[0];
         if (lock != null)
         {
            throw new LockException("File already locked. ");
         }
         lock = new Lock(null, NameGenerator.generate(null, 16), -1);
         String token = lock.getLockToken();
         lockHolder[0] = lock;
         return token;
      }
   }

   void unlock(String lockToken) throws VirtualFileSystemException
   {
      synchronized (lockHolder)
      {
         Lock lock = lockHolder[0];
         if (lock == null)
         {
            throw new LockException("File is not locked. ");
         }
         if (lockToken == null)
         {
            throw new LockException("Null lock token. ");
         }
         if (!lockToken.equals(lock.getLockToken()))
         {
            throw new LockException("Unable remove lock from file. Lock token does not match. ");
         }
         lockHolder[0] = null;
      }
   }

   boolean isLocked()
   {
      synchronized (lockHolder)
      {
         return lockHolder[0] != null;
      }
   }

   final InputStream getContent()
   {
      byte[] bytes = this.bytes;
      return bytes == null ? new ByteArrayInputStream(new byte[0]) : new ByteArrayInputStream(bytes);
   }

   final long getContentLength()
   {
      byte[] bytes = this.bytes;
      return bytes.length;
   }

   final void setContent(InputStream data) throws IOException
   {
      byte[] bytes = null;
      if (data != null)
      {
         ByteArrayOutputStream bout = new ByteArrayOutputStream();
         byte[] buf = new byte[1024];
         int r;
         while ((r = data.read(buf)) != -1)
         {
            bout.write(buf, 0, r);
         }
         bytes = bout.toByteArray();
      }
      this.bytes = bytes;
   }

   @Override
   MemoryItem copy(MemoryFolder parent) throws VirtualFileSystemException
   {
      byte[] bytes = this.bytes;
      MemoryFile copy = new MemoryFile(name, mediaType, Arrays.copyOf(bytes, bytes.length));
      copy.updateProperties(getProperties());
      copy.updateACL(getACL(), true);
      parent.addChild(copy);
      return copy;
   }

   String getLatestVersionId()
   {
      return id; // TODO
   }

   String getVersionId() throws VirtualFileSystemException
   {
      return "0"; // TODO
   }

   @Override
   public String toString()
   {
      return "MemoryFile{" +
         "id='" + id + '\'' +
         ", path=" + getPath() +
         ", name='" + name + '\'' +
         ", type=" + type +
         ", parent=" + parent.getId() +
         ", mediaType='" + mediaType + '\'' +
         ", isLocked='" + isLocked() + '\'' +
         '}';
   }
}
