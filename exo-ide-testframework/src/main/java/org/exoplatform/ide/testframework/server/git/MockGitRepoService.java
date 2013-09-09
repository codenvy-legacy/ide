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
package org.exoplatform.ide.testframework.server.git;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

@Path("{ws-name}/git-repo")
public class MockGitRepoService {

    private static MockGitRepoService instance;

    public static MockGitRepoService getInstance() {
        return instance;
    }

    public MockGitRepoService() {
        instance = this;
    }

    private List<String> gitDirectories = new ArrayList<String>();

    @GET
    @Path("workdir")
    @Produces(MediaType.TEXT_PLAIN)
    public String getWorkDir(@Context UriInfo uriInfo, @HeaderParam("location") String location) throws Exception {
        return location + ".git";
    }

    /** @param dir */
    public void addGitDirectory(String dir) {
        gitDirectories.add(dir);
    }

    @POST
    @Path("reset")
    public void resetMockGitService() {
        gitDirectories = new ArrayList<String>();
    }

}
