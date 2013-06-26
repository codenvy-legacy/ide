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
package com.codenvy.ide.extension.html.server;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: ApplicationRunnerExceptionMapper.java Jun 26, 2013 2:23:34 PM azatsarynnyy $
 *
 */
@Provider
public class ApplicationRunnerExceptionMapper implements ExceptionMapper<ApplicationRunnerException> {
    @Override
    public Response toResponse(ApplicationRunnerException exception) {
        String logs = exception.getLogs();
        if (!(logs == null || logs.isEmpty())) {
            return Response.serverError().entity(logs).type(MediaType.TEXT_PLAIN).build();
        }
        return Response.serverError().entity(exception.getMessage()).type(MediaType.TEXT_PLAIN).build();
    }
}
