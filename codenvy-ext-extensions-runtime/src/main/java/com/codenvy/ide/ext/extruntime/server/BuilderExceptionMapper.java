/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
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
package com.codenvy.ide.ext.extruntime.server;

import com.codenvy.ide.ext.extruntime.server.builder.BuilderException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Maps {@link com.codenvy.ide.ext.extruntime.server.builder.BuilderException} to {@link javax.ws.rs.core.Response}.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: BuilderExceptionMapper.java Jul 19, 2013 4:11:26 PM azatsarynnyy $
 */
@Provider
public class BuilderExceptionMapper implements ExceptionMapper<BuilderException> {
    @Override
    public Response toResponse(BuilderException exception) {
        return Response.status(exception.getResponseStatus()).entity(exception.getMessage()).type(MediaType.TEXT_PLAIN)
                       .build();
    }
}
