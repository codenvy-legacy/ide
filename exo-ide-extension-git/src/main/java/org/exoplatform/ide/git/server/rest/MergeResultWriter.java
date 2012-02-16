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
package org.exoplatform.ide.git.server.rest;

import org.exoplatform.ide.git.shared.MergeResult;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Provider
@Produces(MediaType.TEXT_PLAIN)
public final class MergeResultWriter implements MessageBodyWriter<MergeResult>
{
   /**
    * @see MessageBodyWriter#isWriteable(Class, java.lang.reflect.Type, java.lang.annotation.Annotation[],
    *      javax.ws.rs.core.MediaType)
    */
   @Override
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return MergeResult.class.isAssignableFrom(type);
   }

   /**
    * @see MessageBodyWriter#getSize(Object, Class, java.lang.reflect.Type, java.lang.annotation.Annotation[],
    *      javax.ws.rs.core.MediaType)
    */
   @Override
   public long getSize(MergeResult mergeResult,
                       Class<?> type,
                       Type genericType,
                       Annotation[] annotations,
                       MediaType mediaType)
   {
      return -1;
   }

   /**
    * @see MessageBodyWriter#writeTo(Object, Class, java.lang.reflect.Type, java.lang.annotation.Annotation[],
    *      javax.ws.rs.core.MediaType, javax.ws.rs.core.MultivaluedMap, java.io.OutputStream)
    */
   @Override
   public void writeTo(MergeResult mergeResult,
                       Class<?> type,
                       Type genericType,
                       Annotation[] annotations,
                       MediaType mediaType,
                       MultivaluedMap<String, Object> httpHeaders,
                       OutputStream entityStream) throws IOException, WebApplicationException
   {
      Writer writer = new OutputStreamWriter(entityStream);
      MergeResult.MergeStatus status = mergeResult.getMergeStatus();
      switch (mergeResult.getMergeStatus())
      {
         case FAST_FORWARD:
         case ALREADY_UP_TO_DATE:
         case MERGED:
            writer.write(status.toString());
            writer.write('\n');
            break;
         case FAILED:
            writer.write("error: Failed to merge:");
            for (String failed : mergeResult.getFailed())
            {
               writer.write("        ");
               writer.write(failed);
               writer.write('\n');
            }
            break;
         case CONFLICTING:
            for (String conflict : mergeResult.getConflicts())
            {
               writer.write("CONFLICT(content): Merge conflict in: " + conflict);
               writer.write('\n');
            }
            writer.write("Automatic merge failed; fix conflicts and then commit the result");
            writer.write('\n');
            break;
         case NOT_SUPPORTED:
            writer.write("Operation not supported");
            writer.write('\n');
            break;
      }
      writer.flush();
   }
}
