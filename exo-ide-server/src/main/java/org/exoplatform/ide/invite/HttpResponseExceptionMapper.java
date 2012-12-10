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
package org.exoplatform.ide.invite;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

/**
 * ExceptionMapper for all  HttpResponseException generated in IDE invites.
 */
public class HttpResponseExceptionMapper implements ExceptionMapper<HttpResponseException>
{
   
   private static final Log LOG = ExoLogger.getLogger(HttpResponseExceptionMapper.class);

   /**
    * @see javax.ws.rs.ext.ExceptionMapper#toResponse(java.lang.Throwable)
    */
   public Response toResponse(HttpResponseException exception)
   {
      LOG.warn(exception.getLocalizedMessage());
      String message = exception.getMessage();
      if (message != null)
      {
         return Response.status(exception.getStatus()).entity(message).type(MediaType.TEXT_PLAIN).build();
      }
      return Response.status(exception.getStatus()).build();
   }

}
