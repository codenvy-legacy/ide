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
package org.exoplatform.ide.testframework.server.cloudbees;

import org.exoplatform.ide.testframework.server.FSLocation;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mockup of Cloudbees service.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: MockCloudbeesService.java Aug 16, 2011 12:07:10 PM vereshchaka $
 */
@Path("{ws-name}/cloudbees")
public class MockCloudbeesService {

    public static final List<String> domains = new ArrayList<String>();

    /**
     * Cloudbees users. <b>key</b> - user's login; <br>
     * <b>value</b> - user's password.
     */
    private static final HashMap<String, String> users = new HashMap<String, String>();

    /** Current logged in user. */
    private static String currentUser;

    /**
     * User's applications. <b>key</b> - user's login; <br>
     * <b>value</b> - user's applications.
     */
    private static HashMap<String, List<CloudbeesApplication>> applications =
            new HashMap<String, List<CloudbeesApplication>>();

    /**
     * Registered applications. <li><b>key</b> - application id; <br>
     * </li> <li><b>value</b> - CloudBees application.</li>
     */
    private static final HashMap<String, CloudbeesApplication> apps = new HashMap<String, CloudbeesApplication>();

    /**
     * Applications, that created in work directories <li><b>key</b> - url of workdir; <br>
     * </li> <li><b>value</b> - CloudBees application id.</li>
     */
    private static final HashMap<String, String> workDirs = new HashMap<String, String>();

    public MockCloudbeesService() {
        users.put("exoua.ide@gmail.com", "1234qwer");

        domains.add("exoplatform");
    }

    @Path("login")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void login(Map<String, String> credentials) throws Exception {
        String email = credentials.get("email");
        String password = users.get(email);
        if (password == null || !password.equals(credentials.get("password"))) {
            throw new CloudBeesException(
                    "AuthFailure - Server returned HTTP response code: 400 for URL: https://grandcentral.cloudbees" +
                    ".com/api/user/keys_using_aut");
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

    @Path("domains")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> domains() throws Exception {
        if (currentUser == null)
            throw new CloudBeesException(
                    "AuthFailure - Server returned HTTP response code: 400 for URL: https://grandcentral.cloudbees.com/api/user/keys");
        return domains;
    }

    @Path("apps/create")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> createApplication( //
                                                  @QueryParam("appid") String appId, //
                                                  @QueryParam("message") String message, // Optional
                                                  @QueryParam("workdir") FSLocation workDir, //
                                                  @QueryParam("war") URL war, //
                                                  @Context UriInfo uriInfo //
                                                ) throws Exception {
        if (currentUser == null) {
            throw new CloudBeesException(
                    "AuthFailure - Server returned HTTP response code: 400 for URL: https://grandcentral.cloudbees.com/api/user/keys");
        }

        if (war == null) {
            Response response = Response.status(500).entity("Location to WAR file required. ").type("text/plain").build();
            throw new WebApplicationException(response);
        }

        Map<String, String> properties = new HashMap<String, String>();
        properties.put("id", appId);
        properties.put("title", appId);
        properties.put("serverPool", "stax-global");
        properties.put("status", "active");
        properties.put("idleTimeout", "21600");
        properties.put("maxMemory", "256");
        properties.put("securityMode", "PUBLIC");
        properties.put("clusterSize", "1");
        String[] parts = appId.split("/");
        properties.put("url", "http://" + parts[1] + "." + parts[0] + ".cloudbees.net");

        CloudbeesApplication cbApp =
                new CloudbeesApplication(appId, message, workDir.getURL(), war.getFile(), properties);
        workDirs.put(workDir.getURL(), appId);
        apps.put(appId, cbApp);

        return properties;
    }

    @Path("apps/info")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> applicationInfo( //
                                                @QueryParam("appid") String appId, //
                                                @QueryParam("workdir") FSLocation workDir, //
                                                @Context UriInfo uriInfo //
                                              ) throws Exception {
        if (currentUser == null) {
            throw new CloudBeesException(
                    "AuthFailure - Server returned HTTP response code: 400 for URL: https://grandcentral.cloudbees.com/api/user/keys");
        }

        if (appId == null || appId.isEmpty()) {
            appId = workDirs.get(workDir.getURL());
            if (appId == null || appId.isEmpty()) {
                throw new CloudBeesException("Not cloudbees application. ");
            }
        }

        CloudbeesApplication application = apps.get(appId);
        if (application == null) {
            throw new CloudBeesException("Not cloudbees application. ");
        }

        return application.getProperties();
    }

    @Path("apps/delete")
    @POST
    public void deleteApplication( //
                                   @QueryParam("appid") String appId, //
                                   @QueryParam("workdir") FSLocation workDir, //
                                   @Context UriInfo uriInfo //
                                 ) throws Exception {
        if (appId == null || appId.isEmpty()) {
            appId = detectApplicationId(workDir);
            if (appId == null || appId.isEmpty()) {
                throw new CloudBeesException("Not cloudbees application. ");
            }
        }

        List<CloudbeesApplication> apps = applications.get(currentUser);
        if (apps == null) {
            throw new CloudBeesException("Not cloudbees application. ");
        }

        for (CloudbeesApplication app : apps) {
            if (appId.equals(app.getId())) {
                apps.remove(app);
                return;
            }
        }

        throw new CloudBeesException("Not cloudbees application. ");
    }

    private String detectApplicationId(FSLocation workDir) {
        if (workDir == null)
            return null;

        List<CloudbeesApplication> apps = applications.get(currentUser);
        if (apps == null)
            return null;

        for (CloudbeesApplication app : apps) {
            if (app.getWorkDir().equals(workDir.getURL())) {
                return app.getId();
            }
        }
        return null;
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
