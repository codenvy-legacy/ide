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
package org.exoplatform.ide.testframework.server.heroku;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.util.*;

/**
 * Mockup of Heroku service.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jul 6, 2011 11:45:43 AM anya $
 */
@Path("{ws-name}/heroku")
public class MockHerokuService {
    /**
     * Heroku users. <b>key</b> - user's login; <br>
     * <b>value</b> - user's password.
     */
    private static HashMap<String, String> users = new HashMap<String, String>();

    /** Current logged in user. */
    private static String currentUser;

    /**
     * User's applications. <b>key</b> - user's login; <br>
     * <b>value</b> - user's applications.
     */
    private static HashMap<String, List<HerokuApplication>> applications =
            new HashMap<String, List<HerokuApplication>>();

    public MockHerokuService() {
        users.put("test@test.com", "test");
        users.put("exoua.ide@gmail.com", "1234qwer");
    }

    @Path("login")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void login(Map<String, String> credentials) throws IOException {
        String email = credentials.get("email");
        String password = users.get(email);
        if (password == null || !password.equals(credentials.get("password"))) {
            createErrorResponse("No such user or incorrect password.", 404);
        } else {
            currentUser = email;
        }
    }

    @Path("logout")
    @POST
    public void logout() {
        currentUser = null;
        applications.clear();
    }

    @Path("keys/add")
    @POST
    public void keysAdd() throws HerokuException {
        if (currentUser == null) {
            throw new HerokuException(200, "Authentication required.\n", "text/plain");
        }
    }

    @Path("apps/create")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> appsCreate( //
                                           @QueryParam("name") String name, //
                                           @QueryParam("remote") String remote, //
                                           @QueryParam("workdir") String workDir, //
                                           @Context UriInfo uriInfo //
                                         ) throws HerokuException {
        if (currentUser == null) {
            throw new HerokuException(200, "Authentication required.\n", "text/plain");
        }

        Random random = new Random();
        name = (name != null) ? name : "application" + random.nextInt();
        remote = (remote != null) ? remote : "heroku";

        if (name != null && getApplicationByName(name) != null) {
            createErrorResponse("Name is already taken", 422);
        }

        if (isRemoteNameExist(workDir, remote)) {
            createErrorResponse("Remote heroku already exists.", 500);
        }

        HashMap<String, String> applicationProperties = new HashMap<String, String>();
        applicationProperties.put("slugSize", "4096");
        applicationProperties.put("webUrl", "http://" + name + ".heroku.com/");
        applicationProperties.put("repoSize", "135168");
        applicationProperties.put("dynos", "1");
        applicationProperties.put("name", name);
        applicationProperties.put("owner", currentUser);

        applicationProperties.put("stack", "bamboo-mri-1.9.2");
        applicationProperties.put("workers", "0");
        applicationProperties.put("gitUrl", "git@heroku.com:" + name + ".git");
        applicationProperties.put("databaseSize", "0");

        HerokuApplication application = new HerokuApplication(name, workDir, remote, applicationProperties);
        List<HerokuApplication> userApplications = applications.get(currentUser);
        if (userApplications == null) {
            userApplications = new ArrayList<HerokuApplication>();
        }
        userApplications.add(application);
        applications.put(currentUser, userApplications);
        return applicationProperties;
    }

    /**
     * Find Heroku application by its name through all users' applications.
     *
     * @param name
     *         application's name
     * @return {@link HerokuApplication} found application or null
     */
    public HerokuApplication getApplicationByName(String name) {
        for (String user : applications.keySet()) {
            if (applications.get(user) == null) {
                continue;
            }
            for (HerokuApplication application : applications.get(user)) {
                if (name.equals(application.getName())) {
                    return application;
                }
            }
        }
        return null;
    }

    /**
     * Get if application with pointed working directory and remote name exists.
     *
     * @param gitUrl
     *         Git working directory
     * @param remoteName
     *         remote repository's name
     * @return {@link Boolean}
     */
    public boolean isRemoteNameExist(String gitUrl, String remoteName) {
        if (applications.get(currentUser) == null) {
            return false;
        }
        for (HerokuApplication application : applications.get(currentUser)) {
            if (gitUrl.equals(application.getGitUrl()) && remoteName.equals(application.getRemoteName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Find Heroku application by its Git working directory location through all users' applications.
     *
     * @param gitUrl
     * @return {@link HerokuApplication} found application or null
     */
    public HerokuApplication getApplicationByGitUrl(String gitUrl) {
        if (applications.get(currentUser) == null) {
            return null;
        }
        for (HerokuApplication application : applications.get(currentUser)) {
            if (gitUrl.equals(application.getGitUrl())) {
                return application;
            }
        }
        return null;
    }

    @Path("apps/destroy")
    @POST
    public void appsDestroy( //
                             @QueryParam("name") String name, //
                             @QueryParam("workdir") String workDir, //
                             @Context UriInfo uriInfo //
                           ) throws HerokuException {
        if (currentUser == null) {
            throw new HerokuException(200, "Authentication required.\n", "text/plain");
        }
        HerokuApplication application = null;
        if (name != null) {
            application = getApplicationByName(name);
        }

        if (application == null && workDir != null) {
            application = getApplicationByGitUrl(workDir);
        }

        if (application == null) {
            createErrorResponse("App not found", 404);
        } else {
            List<HerokuApplication> userApplications = applications.get(currentUser);
            userApplications.remove(application);
        }
    }

    @Path("apps/info")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> appsInfo( //
                                         @QueryParam("name") String name, //
                                         @QueryParam("raw") boolean inRawFormat, //
                                         @QueryParam("workdir") String workDir, //
                                         @Context UriInfo uriInfo //
                                       ) throws HerokuException {
        if (currentUser == null) {
            throw new HerokuException(200, "Authentication required.\n", "text/plain");
        }
        HerokuApplication application = null;
        if (name != null) {
            application = getApplicationByName(name);
        }

        if (application == null && workDir != null) {
            application = getApplicationByGitUrl(workDir);
        }

        if (application == null) {
            createErrorResponse("App not found", 404);
            return null;
        } else {
            return application.getProperties();
        }
    }

    @Path("apps/rename")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> appsRename( //
                                           @QueryParam("name") String name, //
                                           @QueryParam("newname") String newname, //
                                           @QueryParam("workdir") String workDir, //
                                           @Context UriInfo uriInfo //
                                         ) throws HerokuException {
        if (currentUser == null) {
            throw new HerokuException(200, "Authentication required.\n", "text/plain");
        }

        // Try to find application:
        HerokuApplication application = null;
        if (name != null) {
            application = getApplicationByName(name);
        }

        if (application == null && workDir != null) {
            application = getApplicationByGitUrl(workDir);
        }

        if (application == null) {
            createErrorResponse("App not found", 404);
        }

        // Check application with new name doesn't exist:
        if (newname != null && getApplicationByName(newname) != null) {
            createErrorResponse("Name is already taken", 422);
        }

        List<HerokuApplication> userApplications = applications.get(currentUser);
        userApplications.remove(application);
        HerokuApplication renamedApplication = renameApplication(newname, application);
        userApplications.add(renamedApplication);
        return renamedApplication.getProperties();
    }

    public HerokuApplication renameApplication(String newName, HerokuApplication application) {
        application.setName(newName);
        application.getProperties().put("webUrl", "http://" + newName + ".heroku.com/");
        application.getProperties().put("name", newName);
        application.getProperties().put("gitUrl", "git@heroku.com:" + newName + ".git");
        return application;
    }

    @Path("apps/run")
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String run( //
                       @QueryParam("name") String name, //
                       @QueryParam("workdir") String workDir, //
                       @Context UriInfo uriInfo, //
                       final String command //
                     ) throws HerokuException {
        if (currentUser == null) {
            throw new HerokuException(200, "Authentication required.\n", "text/plain");
        }
        HerokuApplication application = null;
        if (name != null) {
            application = getApplicationByName(name);
        }

        if (application == null && workDir != null) {
            application = getApplicationByGitUrl(workDir);
        }

        if (application == null) {
            createErrorResponse("App not found", 404);
        }
        return command;
    }

    /**
     * Create response to send with error message.
     *
     * @param message
     *         exception's message
     * @param status
     *         http status
     * @return {@link Response} response with error
     */
    protected void createErrorResponse(String message, int status) {
        Response response = Response.status(status).entity(message).type("text/plain").build();
        throw new WebApplicationException(response);
    }
}
