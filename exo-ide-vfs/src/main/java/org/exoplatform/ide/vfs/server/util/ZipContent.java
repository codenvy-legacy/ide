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
package org.exoplatform.ide.vfs.server.util;

import org.apache.commons.io.input.CountingInputStream;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemRuntimeException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class ZipContent
{
   /** Memory threshold. If zip stream over this size it spooled in file. */
   private static final int BUFFER = 100 * 1024; // 100k
   private static final int BUFFER_SIZE = 8 * 1024; // 8k
   /** The threshold after that checking of ZIP ratio started. */
   private static final long ZIP_THRESHOLD = 1000000;
   /**
    * Max compression ratio. If the number of bytes uncompressed data is exceed the number
    * of bytes of compressed stream more than this ratio (and number of uncompressed data
    * is more than threshold) then VirtualFileSystemRuntimeException is thrown.
    */
   private static final int ZIP_RATIO = 100;

   public static ZipContent newInstance(InputStream in) throws IOException
   {
      java.io.File file = null;
      byte[] inMemory = null;

      int count = 0;
      ByteArrayOutputStream inMemorySpool = new ByteArrayOutputStream(BUFFER);

      int bytes;
      final byte[] buff = new byte[BUFFER_SIZE];
      while (count <= BUFFER && (bytes = in.read(buff)) != -1)
      {
         inMemorySpool.write(buff, 0, bytes);
         count += bytes;
      }

      InputStream spool;
      if (count > BUFFER)
      {
         file = java.io.File.createTempFile("import", ".zip");
         FileOutputStream fileSpool = new FileOutputStream(file);
         try
         {
            inMemorySpool.writeTo(fileSpool);
            while ((bytes = in.read(buff)) != -1)
            {
               fileSpool.write(buff, 0, bytes);
            }
         }
         finally
         {
            fileSpool.close();
         }
         spool = new FileInputStream(file);
      }
      else
      {
         inMemory = inMemorySpool.toByteArray();
         spool = new ByteArrayInputStream(inMemory);
      }

      ZipInputStream zip = null;
      try
      {
         // Counts numbers of compressed data.
         final CountingInputStream compressedCounter = new CountingInputStream(spool);
         zip = new ZipInputStream(compressedCounter);
         // Counts number of uncompressed data.
         CountingInputStream uncompressedCounter = new CountingInputStream(zip)
         {
            @Override
            public int read() throws IOException
            {
               int i = super.read();
               checkCompressionRatio();
               return i;
            }

            @Override
            public int read(byte[] b, int off, int len) throws IOException
            {
               int i = super.read(b, off, len);
               checkCompressionRatio();
               return i;
            }

            @Override
            public int read(byte[] b) throws IOException
            {
               int i = super.read(b);
               checkCompressionRatio();
               return i;
            }

            @Override
            public long skip(long length) throws IOException
            {
               long i = super.skip(length);
               checkCompressionRatio();
               return i;
            }

            private void checkCompressionRatio()
            {
               long uncompressedBytes = getByteCount(); // number of uncompressed bytes
               if (uncompressedBytes > ZIP_THRESHOLD)
               {
                  long compressedBytes = compressedCounter.getByteCount(); // number of compressed bytes
                  if (uncompressedBytes > (ZIP_RATIO * compressedBytes))
                  {
                     throw new VirtualFileSystemRuntimeException("Zip bomb detected. ");
                  }
               }
            }
         };

         boolean isProject = false;

         ZipEntry zipEntry;
         while ((zipEntry = zip.getNextEntry()) != null)
         {
            if (".project".equals(zipEntry.getName()))
            {
               isProject = true;
            }
            else if (!zipEntry.isDirectory())
            {
               while (uncompressedCounter.read(buff) != -1)
               {
                  // Read full data from stream to be able detect zip-bomb.
               }
            }
         }

         return new ZipContent(
            inMemory != null ? new ByteArrayInputStream(inMemory) : new DeleteOnCloseFileInputStream(file),
            isProject,
            file == null);
      }
      finally
      {
         if (zip != null)
         {
            zip.close();
         }
      }
   }

   public final InputStream zippedData;
   public final boolean isProject;
   public final boolean inMemory;

   private ZipContent(InputStream zippedData, boolean project, boolean inMemory)
   {
      this.zippedData = zippedData;
      isProject = project;
      this.inMemory = inMemory;
   }
}
