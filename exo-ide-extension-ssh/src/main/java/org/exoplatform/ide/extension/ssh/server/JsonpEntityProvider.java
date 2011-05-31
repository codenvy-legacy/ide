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
package org.exoplatform.ide.extension.ssh.server;

import org.exoplatform.services.rest.ApplicationContext;
import org.exoplatform.services.rest.impl.ApplicationContextImpl;
import org.exoplatform.services.rest.impl.provider.JsonEntityProvider;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

/**
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
@Provider
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class JsonpEntityProvider extends JsonEntityProvider
{
   private static final String CALLBACK = "callback";

   @Override
   public void writeTo(Object t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
      MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException
   {
      ApplicationContext applicationContext = ApplicationContextImpl.getCurrent();
      MultivaluedMap<String, String> queryParameters = applicationContext.getQueryParameters();
      String callback = queryParameters.getFirst(CALLBACK);
      if (callback != null)
      {
         entityStream.write(callback.getBytes(Charset.forName("UTF-8")));
         entityStream.write('(');
         entityStream.flush();
         super.writeTo(t, type, genericType, annotations, mediaType, httpHeaders, entityStream);
         entityStream.write(')');
         entityStream.write(';');
         entityStream.flush();
      }
      else
      {
         super.writeTo(t, type, genericType, annotations, mediaType, httpHeaders, entityStream);
      }
   }
}
