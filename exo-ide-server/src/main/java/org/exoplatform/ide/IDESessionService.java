package org.exoplatform.ide;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

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
    public void startSession() {
        LOG.info("EVENT#session-started# WINDOW#" + appName + "#");
    }

    @GET
    @Path("stop")
    public void stopSession() {
        LOG.info("EVENT#session-finished# WINDOW#" + appName + "#");
    }

}
