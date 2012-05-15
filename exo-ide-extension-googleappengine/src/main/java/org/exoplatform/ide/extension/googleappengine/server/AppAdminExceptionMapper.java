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
package org.exoplatform.ide.extension.googleappengine.server;

import com.google.appengine.tools.admin.AdminException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Provider
public class AppAdminExceptionMapper implements ExceptionMapper<AdminException>
{
   @Override
   public Response toResponse(AdminException exception)
   {
      String causeMessage = null;
      Throwable cause = exception.getCause();
      if (cause != null)
      {
         causeMessage = cause.getMessage();
      }
      if (causeMessage != null)
      {
         return Response
            .serverError()
            .entity(exception.getMessage() + ' ' + causeMessage)
            .type(MediaType.TEXT_PLAIN)
            .build();
      }
      return Response
         .serverError()
         .entity(exception.getMessage())
         .type(MediaType.TEXT_PLAIN)
         .build();
   }
}
