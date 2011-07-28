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
package org.exoplatform.ide.extension.openshift.server.rest;

import org.exoplatform.ide.extension.openshift.server.ExpressException;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Provider
public class ExpressExceptionMapper implements ExceptionMapper<ExpressException>
{
   /**
    * @see javax.ws.rs.ext.ExceptionMapper#toResponse(java.lang.Throwable)
    */
   @Override
   public Response toResponse(ExpressException e)
   {
      if (e.getResponseStatus() == 200 && "Authentication required.\n".equals(e.getMessage()))
         return Response.status(e.getResponseStatus()).header("JAXRS-Body-Provided", "Authentication-required")
            .entity(e.getMessage()).type(e.getContentType()).build();
      
      //replace HTTP status 401 to other it because this status means Required Authorization on IDE app    
      int responseStatus = e.getResponseStatus() == 401 ? 400 : e.getResponseStatus(); 

      ResponseBuilder rb =
         Response.status(responseStatus).header("JAXRS-Body-Provided", "Error-Message").entity(e.getMessage())
            .type(e.getContentType());
      int exitCode = e.getExitCode();
      if (exitCode != -1)
         rb.header("Express-Exit-Code", exitCode);
      return rb.build();
   }
}
