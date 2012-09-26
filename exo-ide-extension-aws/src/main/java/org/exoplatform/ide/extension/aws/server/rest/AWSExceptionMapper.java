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
package org.exoplatform.ide.extension.aws.server.rest;

import com.amazonaws.AmazonServiceException;
import org.exoplatform.ide.extension.aws.server.AWSException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: AmazonWebServiceExceptionMapper.java Aug 23, 2012
 */
@Provider
public class AWSExceptionMapper implements ExceptionMapper<AWSException>
{
   /** @see javax.ws.rs.ext.ExceptionMapper#toResponse(java.lang.Throwable) */
   @Override
   public Response toResponse(AWSException e)
   {
      Throwable cause = e.getCause();
      if (cause instanceof AmazonServiceException)
      {
         AmazonServiceException awsException = (AmazonServiceException)cause;
         return Response.status(awsException.getStatusCode())
            .header("JAXRS-Body-Provided", "Error-Message")
            .header("AWS-Error-Code", awsException.getErrorCode())
            .header("AWS-Error-Type", awsException.getErrorType().toString())
            .header("AWS-Service-Name", awsException.getServiceName())
            .entity(awsException.getMessage())
            .type(MediaType.TEXT_PLAIN)
            .build();
      }
      else if ("Authentication required.".equals(e.getMessage()))
      {
         return Response.ok()
            .header("JAXRS-Body-Provided", "Authentication-required")
            .entity(e.getMessage())
            .type(MediaType.TEXT_PLAIN)
            .build();
      }
      return Response.status(500)
         .header("JAXRS-Body-Provided", "Error-Message")
         .entity(e.getMessage())
         .type(MediaType.TEXT_PLAIN)
         .build();
   }
}
