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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Date;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

/**
 * Serializer for ContentStream. Copy headers and content provided by method {@link ContentStream#getStream()} to HTTP
 * output stream.
 * 
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Provider
public final class ContentStreamWriter implements MessageBodyWriter<ContentStream>
{
   /**
    * @see javax.ws.rs.ext.MessageBodyWriter#isWriteable(java.lang.Class, java.lang.reflect.Type,
    *      java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType)
    */
   @Override
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return ContentStream.class.isAssignableFrom(type);
   }

   /**
    * @see javax.ws.rs.ext.MessageBodyWriter#getSize(java.lang.Object, java.lang.Class, java.lang.reflect.Type,
    *      java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType)
    */
   @Override
   public long getSize(ContentStream t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return t.getLength();
   }

   /**
    * @see javax.ws.rs.ext.MessageBodyWriter#writeTo(java.lang.Object, java.lang.Class, java.lang.reflect.Type,
    *      java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType, javax.ws.rs.core.MultivaluedMap,
    *      java.io.OutputStream)
    */
   @Override
   public void writeTo(ContentStream t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
      MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException,
      WebApplicationException
   {
      String mimeType = t.getMimeType();
      if (mimeType != null)
      {
         httpHeaders.putSingle(HttpHeaders.CONTENT_TYPE, mimeType);
      }
      Date lastModificationDate = t.getLastModificationDate();
      if (lastModificationDate != null)
      {
         httpHeaders.putSingle(HttpHeaders.LAST_MODIFIED, t.getLastModificationDate());
      }
      InputStream content = t.getStream();
      try
      {
         byte[] buf = new byte[8192];
         int rd = -1;
         while ((rd = content.read(buf)) != -1)
         {
            entityStream.write(buf, 0, rd);
         }
         entityStream.flush();
      }
      finally
      {
         content.close();
      }
   }
}
