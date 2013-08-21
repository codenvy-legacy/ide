package org.exoplatform.ide;

import com.codenvy.organization.client.UserManager;
import com.codenvy.organization.client.WorkspaceManager;
import com.codenvy.organization.exception.OrganizationServiceException;
import com.codenvy.organization.model.Workspace;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

/**
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladyslav Zhukovskii</a>
 * @version $Id: July 17, 2013 id $
 */
@Path("{ws-name}/session/{app-name}")
public class IDESessionService {
    private static final Log LOG = ExoLogger.getLogger(IDESessionService.class);

    private static final Gson gson = new GsonBuilder().serializeNulls().create();

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

    @POST
    @Path("start")
    @Consumes(MediaType.APPLICATION_JSON)
    public void startSession(String json)
            throws OrganizationServiceException {
        JsonElement jsonElement = new JsonParser().parse(json);
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        String sessionId = "";
        if (jsonObject.has("sessionId")) {
            JsonElement value = jsonObject.get("sessionId");
            sessionId = gson.fromJson(value, String.class);
        }

        String browserInfo = "";
        if (jsonObject.has("browserInfo")) {
            JsonElement value = jsonObject.get("browserInfo");
            browserInfo = gson.fromJson(value, String.class);
        }

        String userId = ConversationState.getCurrent().getIdentity().getUserId();

        LOG.info("EVENT#session-started# SESSION-ID#" + sessionId + "# USER#" + userId + "# WS#" + wsName + "# WINDOW#" + appName + "#");

        Workspace workspace = workspaceManager.getWorkspaceByName(wsName);
        if (workspace.isTemporary()) {
            LOG.info("EVENT#session-factory-started# SESSION-ID#" + sessionId + "# WS#" + wsName + "# USER#" + userId + "# AUTHENTICATED#" +
                     !userManager.getUserByAlias(userId).isTemporary() + "# BROWSER-TYPE#" +
                     browserInfo.substring(0, browserInfo.indexOf("/")) + "# BROWSER-VER#" +
                     browserInfo.substring(browserInfo.indexOf("/") + 1) + "#");
        }
    }

    @POST
    @Path("stop")
    @Consumes(MediaType.APPLICATION_JSON)
    public void stopSession(String json)
            throws OrganizationServiceException {
        JsonElement jsonElement = new JsonParser().parse(json);
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        String sessionId = "";
        if (jsonObject.has("sessionId")) {
            JsonElement value = jsonObject.get("sessionId");
            sessionId = gson.fromJson(value, String.class);
        }

        String userId = ConversationState.getCurrent().getIdentity().getUserId();

        LOG.info("EVENT#session-finished# SESSION-ID#" + sessionId + "# USER#" + userId + "# WS#" + wsName + "# WINDOW#" + appName + "#");
        Workspace workspace = workspaceManager.getWorkspaceByName(wsName);
        if (workspace.isTemporary()) {
            LOG.info("EVENT#session-factory-stopped# SESSION-ID#" + sessionId + "# WS#" + wsName + "# USER#" + userId + "#");
        }
    }
}