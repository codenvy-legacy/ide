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
package org.exoplatform.ide.extension.googleappengine.server;

import com.google.appengine.tools.admin.AdminException;
import com.google.appengine.tools.admin.HttpIoException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Provider
public class AppAdminExceptionMapper implements ExceptionMapper<AdminException> {
    @Override
    public Response toResponse(AdminException exception) {
        String causeMessage = null;
        Throwable cause = exception.getCause();
        int status = 500;
        if (cause != null) {
            causeMessage = cause.getMessage();
            if (cause instanceof HttpIoException) {
                status = ((HttpIoException)cause).getResponseCode();
            }
        }
        if (causeMessage != null) {
            return Response
                    .status(status)
                    .entity(exception.getMessage() + ' ' + causeMessage)
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
        return Response
                .status(status)
                .entity(exception.getMessage())
                .type(MediaType.TEXT_PLAIN)
                .build();
    }
}
