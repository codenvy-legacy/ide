/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.vfs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class ContentImpl implements Content
{
   /** Stream. */
   private final InputStream stream;

   /** Media type of stream. */
   private final MimeType mediaType;

   /** Length. */
   private final long length;

   /** Last modification date. */
   private final Calendar lastModificationDate;

   /**
    * @param bytes source bytes of content
    * @param mediaType media type of content
    * @param lastModificationDate last modification date
    */
   public ContentImpl(byte[] bytes, MimeType mediaType, Calendar lastModificationDate)
   {
      this(new ByteArrayInputStream(bytes), bytes.length, mediaType, lastModificationDate);
   }

   /**
    * @param stream source stream
    * @param length content length. Must be -1 if content length is unknown.
    * @param mediaType media type of content
    * @param lastModificationDate last modification date
    */
   public ContentImpl(InputStream stream, long length, MimeType mediaType, Calendar lastModificationDate)
   {
      this.stream = stream;
      this.length = length;
      this.mediaType = mediaType;
      this.lastModificationDate = lastModificationDate;
   }

   /**
    * @param stream source stream
    * @param mediaType media type of content
    * @param lastModificationDate last modification date
    */
   public ContentImpl(InputStream stream, MimeType mediaType, Calendar lastModificationDate)
   {
      this(stream, -1, mediaType, lastModificationDate);
   }

   /**
    * @see org.exoplatform.ide.vfs.Content#getLastModificationDate()
    */
   public Calendar getLastModificationDate()
   {
      return lastModificationDate;
   }

   /**
    * @see org.exoplatform.ide.vfs.Content#getLength()
    */
   public long getLength()
   {
      return length;
   }

   /**
    * @see org.exoplatform.ide.vfs.Content#getMediaType()
    */
   public MimeType getMediaType()
   {
      return mediaType;
   }

   /**
    * @see org.exoplatform.ide.vfs.Content#getStream()
    */
   public InputStream getStream() throws IOException
   {
      return stream;
   }
}
