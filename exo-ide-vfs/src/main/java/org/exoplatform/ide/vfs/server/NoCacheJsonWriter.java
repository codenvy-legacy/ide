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
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
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
   private Providers providers;
   @SuppressWarnings("rawtypes")
   private MessageBodyWriter writer;

   public NoCacheJsonWriter()
   {
      // FIXME : Find a batter way then use EverRest embedded provider directly.
      // Injection and instance of javax.ws.rs.ext.Providers by @Context annotation does not work.
      // The problem is current MessageBodyWriter registered in Providers itself so when we try find 
      // writer we met current writer and got StackOverflowError as result. 
      try
      {
         Class<?> provider = Class.forName("org.everrest.core.impl.ProviderBinder");
         Method method = provider.getMethod("getInstance");
         providers = (Providers)method.invoke(null);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e.getMessage(), e);
      }
   }

   /**
    * @see javax.ws.rs.ext.MessageBodyWriter#isWriteable(java.lang.Class, java.lang.reflect.Type,
    *      java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType)
    */
   @SuppressWarnings("unchecked")
   @Override
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      if (providers != null)
      {
         writer = providers.getMessageBodyWriter(type, genericType, annotations, mediaType);
         if (writer != null && writer.isWriteable(type, genericType, annotations, mediaType))
         {
            return true;
         }
      }
      return false;
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
    *      java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType, javax.ws.rs.core.MultivaluedMap,
    *      java.io.OutputStream)
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
         String message =
            "Not found writer for " + type + " and MIME type " + httpHeaders.getFirst(HttpHeaders.CONTENT_TYPE);
         throw new WebApplicationException(Response.status(Response.Status.NOT_ACCEPTABLE).entity(message)
            .type(MediaType.TEXT_PLAIN).build());
      }

      // Add Cache-Control before start write body.
      httpHeaders.putSingle(HttpHeaders.CACHE_CONTROL, "public, no-cache, no-store, no-transform");

      writer.writeTo(t, type, genericType, annotations, mediaType, httpHeaders, entityStream);
   }
}
