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
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;

/**
 * Add Cache-Control response header. For write JSON content use JSON provider embedded in REST framework if any.
 * 
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Provider
@Produces({MediaType.APPLICATION_JSON})
public class NoCacheJsonWriter<T> implements MessageBodyWriter<T>
{
   @Context
   private Providers providers;

   @SuppressWarnings("rawtypes")
   private MessageBodyWriter writer;

   private static ThreadLocal<MessageBodyWriter> writerContext = new ThreadLocal<MessageBodyWriter>();

   /**
    * @see javax.ws.rs.ext.MessageBodyWriter#isWriteable(java.lang.Class, java.lang.reflect.Type,
    *      java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType)
    */
   @SuppressWarnings("unchecked")
   @Override
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      if (null != writerContext.get())
      {
         // Avoid recursively check the same type of writer in current thread.
         // It forces JAX-RS framework look embedded JSON writer if any.
         // If we got such writer then use it for writing body.
         return false;
      }
      else
      {
         try
         {
            writerContext.set(this);
            return null != (writer = providers.getMessageBodyWriter(type, genericType, annotations, mediaType));
         }
         finally
         {
            writerContext.remove();
         }
      }
   }

   /**
    * @see javax.ws.rs.ext.MessageBodyWriter#getSize(java.lang.Object, java.lang.Class, java.lang.reflect.Type,
    *      java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType)
    */
   @Override
   public long getSize(T t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return -1;
   }

   /**
    * @see javax.ws.rs.ext.MessageBodyWriter#writeTo(java.lang.Object, java.lang.Class, java.lang.reflect.Type,
    *      java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType, javax.ws.rs.core.MultivaluedMap, java.io.OutputStream)
    */
   @SuppressWarnings("unchecked")
   @Override
   public void writeTo(T t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
      MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException,
      WebApplicationException
   {
      if (writer == null)
      {
         // Be sure writer available.
         throw new WebApplicationException(
            Response
               .status(Response.Status.NOT_ACCEPTABLE)
               .entity(
                  "Not found writer for " + type + " and MIME type " + httpHeaders.getFirst(HttpHeaders.CONTENT_TYPE))
               .type(MediaType.TEXT_PLAIN).build());
      }

      // Add Cache-Control before start write body.
      httpHeaders.putSingle(HttpHeaders.CACHE_CONTROL, "public, no-cache, no-store, no-transform");

      writer.writeTo(t, type, genericType, annotations, mediaType, httpHeaders, entityStream);
   }
}
