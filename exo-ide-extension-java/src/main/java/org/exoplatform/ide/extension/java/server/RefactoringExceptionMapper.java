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
package org.exoplatform.ide.extension.java.server;

import com.codenvy.eclipse.core.runtime.CoreException;
import com.codenvy.eclipse.core.runtime.IStatus;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: RefactoringExceptionMapper.java Jan 22, 2013 vetal $
 */
public class RefactoringExceptionMapper implements ExceptionMapper<CoreException> {

    @Override
    public Response toResponse(CoreException e) {
        IStatus status = e.getStatus();
        return Response.status(400)
                       .header("JAXRS-Body-Provided", "Error-Message")
                       .header("Java-Refactoring-Error-Code", status.getCode())
                       .header("Java-Refactoring-Error-Severity", status.getSeverity())
                       .entity(status.getMessage())
                       .type(MediaType.TEXT_PLAIN).build();
    }

}
