/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.git.server.rest;

import com.codenvy.ide.ext.git.server.GitException;
import com.codenvy.ide.ext.git.server.NotAuthorizedException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Exception mapper for GitException.
 *
 * @author Vitaly Parfonov
 */
@Provider
public class GitExceptionMapper implements ExceptionMapper<GitException> {
    /** @see javax.ws.rs.ext.ExceptionMapper#toResponse(java.lang.Throwable) */
    @Override
    public Response toResponse(GitException e) {
        if (e instanceof NotAuthorizedException) {
            return Response.status(401).header("JAXRS-Body-Provided", "Error-Message")
                           .entity("<pre>" + e.getMessage() + "</pre>").type(MediaType.TEXT_PLAIN).build();
        }
        // Insert error message in <pre> tags even content-type is text/plain.
        // Message will be included in HTML page by client.
        return Response.status(500).header("JAXRS-Body-Provided", "Error-Message")
                       .entity("<pre>" + e.getMessage() + "</pre>").type(MediaType.TEXT_PLAIN).build();
    }
}
