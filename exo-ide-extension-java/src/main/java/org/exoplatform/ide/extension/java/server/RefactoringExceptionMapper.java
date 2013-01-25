/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package org.exoplatform.ide.extension.java.server;

import com.codenvy.eclipse.core.runtime.CoreException;
import com.codenvy.eclipse.core.runtime.IStatus;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: RefactoringExceptionMapper.java Jan 22, 2013 vetal $
 *
 */
public class RefactoringExceptionMapper implements ExceptionMapper<CoreException>
{

   @Override
   public Response toResponse(CoreException e)
   {
      IStatus status = e.getStatus();
      return Response.status(400)
                     .header("JAXRS-Body-Provided", "Error-Message")
                     .header("Java-Refactoring-Error-Code", status.getCode())
                     .header("Java-Refactoring-Error-Severity", status.getSeverity())
                     .entity(status.getMessage())
                     .type(MediaType.TEXT_PLAIN).build();
   }

}
