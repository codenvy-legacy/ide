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
import java.io.IOException;
import java.io.InputStream;

/**
 * Result of maven task.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
final class Result
{
   /** Stream that contains result ot task, e.g. binary stream of maven artifact. */
   private final InputStream stream;

   /** File that contains result of task. */
   private final File file;

   /** Media type of result. This value is sent to the client. */
   private final String mediaType;

   /** Time when task was done. */
   private final long time;
   

   /**
    * This value may be sent to the client in Content-Disposition header. It may be useful to prevent some clients (e.g.
    * browsers) to open file but download it.
    */
   private final String fileName;

   Result(InputStream stream, String mediaType, String fileName, long time)
   {
      this.stream = stream;
      this.file = null;
      this.mediaType = mediaType;
      this.fileName = fileName;
      this.time = time;
   }

   Result(File file, String mediaType, String fileName, long time)
   {
      this.file = file;
      this.stream = null;
      this.mediaType = mediaType;
      this.fileName = fileName;
      this.time = time;
   }
   
  

   String getMediaType()
   {
      return mediaType;
   }

   InputStream getStream() throws IOException
   {
      return stream != null ? stream : new FileInputStream(file);
   }

   String getFileName()
   {
      return fileName;
   }

   long getTime()
   {
      return time;
   }
   
}
