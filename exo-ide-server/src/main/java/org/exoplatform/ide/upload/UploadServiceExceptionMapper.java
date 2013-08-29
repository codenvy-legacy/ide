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
package org.exoplatform.ide.upload;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Jan 13, 2011 10:40:56 AM evgen $
 */
@Provider
public class UploadServiceExceptionMapper implements ExceptionMapper<UploadServiceException> {

    /** @see javax.ws.rs.ext.ExceptionMapper#toResponse(java.lang.Throwable) */
    public Response toResponse(UploadServiceException exception) {
        String message = exception.getMessage();
        if (message != null)
            return Response.status(exception.getStatus()).entity(message).type(MediaType.TEXT_HTML).build();

        return Response.status(exception.getStatus()).entity(exception.getCause()).type(MediaType.TEXT_HTML).build();
    }

}
