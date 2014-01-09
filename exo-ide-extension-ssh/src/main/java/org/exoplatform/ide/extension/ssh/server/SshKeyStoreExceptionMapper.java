package org.exoplatform.ide.extension.ssh.server;

import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.gwtframework.commons.rest.MimeType;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Exception mapper for {@link org.exoplatform.ide.extension.ssh.server.SshKeyStoreException}.
 */
@Provider
public class SshKeyStoreExceptionMapper implements ExceptionMapper<SshKeyStoreException> {
    @Override
    public Response toResponse(SshKeyStoreException exception) {
        return Response.status(HTTPStatus.INTERNAL_ERROR)
                       .entity(exception.getLocalizedMessage())
                       .header(HTTPHeader.CONTENT_TYPE, MimeType.TEXT_HTML)
                       .build();
    }
}
