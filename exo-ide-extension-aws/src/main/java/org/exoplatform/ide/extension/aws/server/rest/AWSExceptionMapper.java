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
package org.exoplatform.ide.extension.aws.server.rest;

import com.amazonaws.AmazonServiceException;

import org.exoplatform.ide.extension.aws.server.AWSException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: AmazonWebServiceExceptionMapper.java Aug 23, 2012
 */
@Provider
public class AWSExceptionMapper implements ExceptionMapper<AWSException> {
    /** @see javax.ws.rs.ext.ExceptionMapper#toResponse(java.lang.Throwable) */
    @Override
    public Response toResponse(AWSException e) {
        Throwable cause = e.getCause();
        if (cause instanceof AmazonServiceException) {
            AmazonServiceException awsException = (AmazonServiceException)cause;
            return Response.status(awsException.getStatusCode())
                           .header("JAXRS-Body-Provided", "Error-Message")
                           .header("AWS-Error-Code", awsException.getErrorCode())
                           .header("AWS-Error-Type", awsException.getErrorType().toString())
                           .header("AWS-Service-Name", awsException.getServiceName())
                           .entity(awsException.getMessage())
                           .type(MediaType.TEXT_PLAIN)
                           .build();
        } else if ("Authentication required.".equals(e.getMessage())) {
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
