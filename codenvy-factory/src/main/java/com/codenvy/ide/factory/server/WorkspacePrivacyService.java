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

    private static final Logger LOG = LoggerFactory.getLogger(WorkspacePrivacyService.class);

    @GET
    @Path("private")
    @Produces(MimeType.TEXT_PLAIN)
    public Response getWorkspacePrivacy() {
        EnvironmentContext context = EnvironmentContext.getCurrent();
        Boolean access = false;
        if (context != null) {
            access = EnvironmentContext.getCurrent().getVariable("WORKSPACE_IS_PRIVATE") != null
                     && (Boolean)EnvironmentContext.getCurrent().getVariable("WORKSPACE_IS_PRIVATE");
            LOG.info("Workspace {} private: {}", context.getVariable(EnvironmentContext.WORKSPACE_NAME), access);
        } else {
            LOG.error("Failed to obtain environment context.");
        }

        return Response.ok().entity(access.toString()).header(HTTPHeader.CONTENT_TYPE, MimeType.TEXT_PLAIN).build();
    }
}
