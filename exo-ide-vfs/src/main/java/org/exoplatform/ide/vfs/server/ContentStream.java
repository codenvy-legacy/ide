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
package org.exoplatform.ide.vfs.server;

import java.io.InputStream;
import java.util.Date;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class ContentStream
{
   private final String fileName;
   private final InputStream stream;
   private final String mimeType;
   private final long length;
   private final Date lastModificationDate;

   public ContentStream(String fileName, InputStream stream, String mimeType, long length, Date lastModificationDate)
   {
      this.fileName = fileName;
      this.stream = stream;
      this.mimeType = mimeType;
      this.length = length;
      this.lastModificationDate = lastModificationDate;
   }

   public ContentStream(String fileName, InputStream stream, String mimeType, Date lastModificationDate)
   {
      this(fileName, stream, mimeType, -1, lastModificationDate);
   }

   public ContentStream(String fileName, InputStream stream, String mimeType)
   {
      this(fileName, stream, mimeType, -1, new Date());
   }

   public String getFileName()
   {
      return fileName;
   }

   public InputStream getStream()
   {
      return stream;
   }

   public String getMimeType()
   {
      return mimeType;
   }

   public long getLength()
   {
      return length;
   }

   public Date getLastModificationDate()
   {
      return lastModificationDate;
   }
}
