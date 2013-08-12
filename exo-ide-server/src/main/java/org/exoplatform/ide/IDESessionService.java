package org.exoplatform.ide;

import com.codenvy.organization.client.UserManager;
import com.codenvy.organization.client.WorkspaceManager;
import com.codenvy.organization.exception.OrganizationServiceException;
import com.codenvy.organization.model.Workspace;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

/**
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladyslav Zhukovskii</a>
 * @version $Id: July 17, 2013 id $
 */
@Path("{ws-name}/session/{app-name}")
public class IDESessionService {
    private static final Log LOG = ExoLogger.getLogger(IDESessionService.class);

    WorkspaceManager workspaceManager;

    UserManager userManager;

    @PathParam("ws-name")
    private String wsName;

    @PathParam("app-name")
    private String appName;

    public IDESessionService() throws OrganizationServiceException {
        userManager = new UserManager();
        workspaceManager = new WorkspaceManager();
    }

    @GET
    @Path("start")
    public void startSession(@QueryParam("sessionId") String sessionId, @QueryParam("browserInfo") String browserInfo)
            throws OrganizationServiceException {
        String userId = ConversationState.getCurrent().getIdentity().getUserId();
        LOG.info(
                "EVENT#session-started# SESSION-ID#" + sessionId + "# USER#" + userId + "# WS#" + wsName + "# WINDOW#" +
                appName + "#");

        Workspace workspace = workspaceManager.getWorkspaceByName(wsName);
        if (workspace.isTemporary()) {
            LOG.info(
                    "EVENT#session-factory-started# SESSION-ID#" + sessionId + "# WS#" + wsName + "# USER#" + userId +
                    "# AUTHENTICATED#" + userManager.getUserByAlias(userId).isTemporary() + "# BROWSER-TYPE#" +
                    browserInfo.substring(0, browserInfo.indexOf("/")) +
                    "# BROWSER-VER#" + browserInfo.substring(browserInfo.indexOf("/") + 1) + "#");
        }


    }

    @GET
    @Path("stop")
    public void stopSession(@QueryParam("sessionId") String sessionId, @QueryParam("browserInfo") String browserInfo)
            throws OrganizationServiceException {
        String userId = ConversationState.getCurrent().getIdentity().getUserId();
        LOG.info("EVENT#session-finished# SESSION-ID#" + sessionId + "# USER#" + userId + "# WS#" + wsName +
                 "# WINDOW#" + appName + "#");
        Workspace workspace = workspaceManager.getWorkspaceByName(wsName);
        if (workspace.isTemporary()) {
            LOG.info(
                    "EVENT#session-factory-stopped# SESSION-ID#" + sessionId + "# WS#" + wsName + "# USER#" + userId +
                    "#");
        }
    }
}