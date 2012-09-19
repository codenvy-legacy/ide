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
package org.exoplatform.ide.extension.aws.server.s3;

import java.io.InputStream;
import java.util.Date;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public final class S3Content
{
   private final InputStream stream;
   private final String contentType;
   private final Date lastModificationDate;
   private final long length;

   public S3Content(InputStream stream, String contentType, Date lastModificationDate, long length)
   {
      this.stream = stream;
      this.contentType = contentType;
      this.lastModificationDate = lastModificationDate;
      this.length = length;
   }

   public InputStream getStream()
   {
      return stream;
   }

   public String getContentType()
   {
      return contentType;
   }

   public Date getLastModificationDate()
   {
      return lastModificationDate;
   }

   public long getLength()
   {
      return length;
   }
}
