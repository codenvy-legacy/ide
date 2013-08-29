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
package org.exoplatform.ide.jrebel.server;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * @author <a href="vzhukovskii@exoplatform.com">Vladyslav Zhukovskii</a>
 * @version $Id: JRebelProfilerExceptionMapper.java 34027 19.12.12 17:01Z vzhukovskii $
 */
public class JRebelConsumerExceptionMapper implements ExceptionMapper<JRebelConsumerException> {
    /** @see javax.ws.rs.ext.ExceptionMapper#toResponse(java.lang.Throwable) */
    @Override
    public Response toResponse(JRebelConsumerException e) {
        return Response.status(500).entity(e.getMessage()).type(MediaType.TEXT_PLAIN_TYPE).build();
    }
}