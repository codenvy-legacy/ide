package org.exoplatform.ide;

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

    @PathParam("ws-name")
    private String wsName;

    @PathParam("app-name")
    private String appName;

    @GET
    @Path("start")
    public void startSession(@QueryParam("sessionId") String sessionId) {
        String userId = ConversationState.getCurrent().getIdentity().getUserId();
        LOG.info("EVENT#session-started# SESSION-ID#" + sessionId + "# USER#" + userId + "# WS#" + wsName + "# WINDOW#" + appName + "#");
    }

    @GET
    @Path("stop")
    public void stopSession(@QueryParam("sessionId") String sessionId) {
        String userId = ConversationState.getCurrent().getIdentity().getUserId();
        LOG.info("EVENT#session-finished# SESSION-ID#" + sessionId + "# USER#" + userId + "# WS#" + wsName + "# WINDOW#" + appName + "#");
    }
}