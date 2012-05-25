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

import java.io.InputStream;

/**
 * Result of maven task.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class Result
{
   /** Stream that contains result ot task, e.g. binary stream of maven artifact. */
   private final InputStream stream;

   /** Media type of result. This value is sent to the client. */
   private final String mediaType;

   /**
    * This value may be sent to the client in Content-Disposition header. It may be useful to prevent some clients (e.g.
    * browsers) to open file but download it.
    */
   private final String fileName;

   public Result(InputStream stream, String mediaType, String fileName)
   {
      this.stream = stream;
      this.mediaType = mediaType;
      this.fileName = fileName;
   }

   public String getMediaType()
   {
      return mediaType;
   }

   public InputStream getStream()
   {
      return stream;
   }

   public String getFileName()
   {
      return fileName;
   }
}
