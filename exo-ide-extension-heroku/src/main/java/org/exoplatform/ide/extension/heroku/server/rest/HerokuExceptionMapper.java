/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package org.exoplatform.ide.extension.heroku.server.rest;

import org.exoplatform.ide.extension.heroku.server.HerokuException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Provider
public class HerokuExceptionMapper implements ExceptionMapper<HerokuException> {
    /** @see javax.ws.rs.ext.ExceptionMapper#toResponse(java.lang.Throwable) */
    @Override
    public Response toResponse(HerokuException he) {
        if (he.getResponseStatus() == 200 && "Authentication required.\n".equals(he.getMessage()))
            return Response.status(he.getResponseStatus()).header("JAXRS-Body-Provided", "Authentication-required")
                           .entity(he.getMessage()).type(he.getContentType()).build();

        return Response.status(he.getResponseStatus()).header("JAXRS-Body-Provided", "Error-Message")
                       .entity(he.getMessage()).type(he.getContentType()).build();
    }
}
