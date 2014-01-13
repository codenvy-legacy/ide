package com.codenvy.ide.factory.server;

import com.codenvy.commons.env.EnvironmentContext;

import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * Temporary service to detect if current workspace is private or not.
 * When Workspace API will be ready, this helper class should be deleted.
 * @author Vladyslav Zhukovskii
 */
@Path("{ws-name}/workspace")
public class WorkspacePrivacyService {

    @GET
    @Path("private")
    @Produces(MimeType.TEXT_PLAIN)
    public Response getWorkspacePrivacy() {
        Object access = EnvironmentContext.getCurrent().getVariable("WORKSPACE_IS_PRIVATE");

        if (access != null && access instanceof Boolean) {
            return Response.ok().entity(access.toString()).header(HTTPHeader.CONTENT_TYPE, MimeType.TEXT_PLAIN).build();
        }

        return Response.ok().entity(false).header(HTTPHeader.CONTENT_TYPE, MimeType.TEXT_PLAIN).build();
    }
}
