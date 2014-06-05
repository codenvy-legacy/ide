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
package com.codenvy.ide.ext.github.server.rest;

import com.codenvy.ide.ext.github.server.GitHubException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: SamplesServiceExceptionMapper.java Sep 2, 2011 12:22:10 PM vereshchaka $
 */
@Provider
public class GitHubExceptionMapper implements ExceptionMapper<GitHubException> {

    /** @see javax.ws.rs.ext.ExceptionMapper#toResponse(java.lang.Throwable) */
    @Override
    public Response toResponse(GitHubException e) {
        return Response.status(e.getResponseStatus()).header("JAXRS-Body-Provided", "Error-Message")
                       .entity(e.getMessage()).type(e.getContentType()).build();
    }

}
