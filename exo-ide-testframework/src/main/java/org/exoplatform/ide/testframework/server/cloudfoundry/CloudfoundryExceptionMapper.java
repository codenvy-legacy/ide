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
package org.exoplatform.ide.testframework.server.cloudfoundry;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Provider
public class CloudfoundryExceptionMapper implements ExceptionMapper<CloudfoundryException> {
    /** @see javax.ws.rs.ext.ExceptionMapper#toResponse(java.lang.Throwable) */
    @Override
    public Response toResponse(CloudfoundryException e) {
        if (e.getResponseStatus() == 200 && "Authentication required.\n".equals(e.getMessage()))
            return Response.status(e.getResponseStatus()).header("JAXRS-Body-Provided", "Authentication-required")
                           .entity(e.getMessage()).type(e.getContentType()).build();

        return Response.status(e.getResponseStatus()).header("JAXRS-Body-Provided", "Error-Message")
                       .entity(e.getMessage()).type(e.getContentType()).build();
    }
}
